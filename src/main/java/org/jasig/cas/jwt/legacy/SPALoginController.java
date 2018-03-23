package org.jasig.cas.jwt.legacy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasig.cas.CasProtocolConstants;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.AuthenticationContext;
import org.jasig.cas.authentication.AuthenticationContextBuilder;
import org.jasig.cas.authentication.AuthenticationException;
import org.jasig.cas.authentication.AuthenticationSystemSupport;
import org.jasig.cas.authentication.AuthenticationTransaction;
import org.jasig.cas.authentication.Credential;
import org.jasig.cas.authentication.DefaultAuthenticationContextBuilder;
import org.jasig.cas.authentication.DefaultAuthenticationSystemSupport;
import org.jasig.cas.authentication.RememberMeCredential;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.ServiceFactory;
import org.jasig.cas.support.rest.BadRequestException;
import org.jasig.cas.support.rest.CredentialFactory;
import org.jasig.cas.support.rest.RememberMeCredentialFactory;
import org.jasig.cas.ticket.InvalidTicketException;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.DefaultTicketRegistrySupport;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.ticket.registry.TicketRegistrySupport;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Leon.Yu on 2017/11/2.
 *
 * @Author Leon.Yu
 */
@Controller
public class SPALoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SPALoginController.class);
    @Autowired(required = false)
    private final CredentialFactory credentialFactory = new RememberMeCredentialFactory();
    private final ObjectMapper jacksonObjectMapper = new ObjectMapper();
    @Autowired
    @Qualifier("centralAuthenticationService")
    private CentralAuthenticationService centralAuthenticationService;
    @NotNull
    @Autowired(required = false)
    @Qualifier("defaultAuthenticationSystemSupport")
    private AuthenticationSystemSupport authenticationSystemSupport = new DefaultAuthenticationSystemSupport();
    @Autowired
    @Qualifier("webApplicationServiceFactory")
    private ServiceFactory webApplicationServiceFactory;
    @Autowired
    @Qualifier("defaultTicketRegistrySupport")
    private TicketRegistrySupport ticketRegistrySupport = new DefaultTicketRegistrySupport();

    @Autowired
    @Qualifier("redisTicketRegistry")
    private TicketRegistry ticketRegistry;

    @Autowired
    @Qualifier("ticketGrantingTicketCookieGenerator")
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;


    @RequestMapping(value = "/spaLogin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity serviceTicketUsingUsernameAndPassword(@RequestBody final MultiValueMap<String, String> requestBody,
                                                                final HttpServletRequest request, final HttpServletResponse response) throws JsonProcessingException {
        try (Formatter fmt = new Formatter()) {
            final Credential credential = this.credentialFactory.fromRequestBody(requestBody);
            final AuthenticationContextBuilder builder = new DefaultAuthenticationContextBuilder(
                    this.authenticationSystemSupport.getPrincipalElectionStrategy());
            final AuthenticationTransaction transaction =
                    AuthenticationTransaction.wrap(credential);
            this.authenticationSystemSupport.getAuthenticationTransactionManager().handle(transaction, builder);
            final AuthenticationContext authenticationContext = builder.build();

            final TicketGrantingTicket tgtId = this.centralAuthenticationService.createTicketGrantingTicket(authenticationContext);
            ticketRegistry.addTicket(tgtId);

            if (credential instanceof RememberMeCredential && ((RememberMeCredential) credential).isRememberMe()) {
                this.ticketGrantingTicketCookieGenerator.addCookie(request, response, tgtId.getId());
            }

            final String serviceId = requestBody.getFirst(CasProtocolConstants.PARAMETER_SERVICE);
            final Service service = this.webApplicationServiceFactory.createService(serviceId);
            final AuthenticationContext serviceTicketAuthenticationContext =
                    builder.collect(this.ticketRegistrySupport.getAuthenticationFrom(tgtId.getId())).build(service);

            final ServiceTicket serviceTicketId = this.centralAuthenticationService.grantServiceTicket(tgtId.getId(),
                    service, serviceTicketAuthenticationContext);
            return new ResponseEntity<>(serviceTicketId.getId(), HttpStatus.OK);
        } catch (final AuthenticationException e) {
            final List<String> authnExceptions = new LinkedList<>();
            for (final Map.Entry<String, Class<? extends Exception>> handlerErrorEntry : e.getHandlerErrors().entrySet()) {
                authnExceptions.add(handlerErrorEntry.getValue().getSimpleName());
            }
            final Map<String, List<String>> errorsMap = new HashMap<>();
            errorsMap.put("authentication_exceptions", authnExceptions);
            LOGGER.error(String.format("Caused by: %s", authnExceptions));
            return new ResponseEntity<>(this.jacksonObjectMapper
                    .writer()
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(errorsMap), HttpStatus.UNAUTHORIZED);
        } catch (final BadRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (final InvalidTicketException e) {
            return new ResponseEntity<>("TicketGrantingTicket could not be found", HttpStatus.NOT_FOUND);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
