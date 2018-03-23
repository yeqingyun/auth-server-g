package org.jasig.cas.jwt.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gionee.jwt.blacklist.redis.RedisJwtBlacklist;
import io.jsonwebtoken.Claims;
import org.jasig.cas.CasViewConstants;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.AuthenticationContextBuilder;
import org.jasig.cas.authentication.AuthenticationException;
import org.jasig.cas.authentication.AuthenticationSystemSupport;
import org.jasig.cas.authentication.AuthenticationTransaction;
import org.jasig.cas.authentication.Credential;
import org.jasig.cas.authentication.DefaultAuthenticationContextBuilder;
import org.jasig.cas.authentication.DefaultAuthenticationSystemSupport;
import org.jasig.cas.authentication.RememberMeUsernamePasswordCredential;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.ServiceFactory;
import org.jasig.cas.authentication.principal.WebApplicationService;
import org.jasig.cas.jwt.CasJsonWebToken;
import org.jasig.cas.jwt.CasRefreshToken;
import org.jasig.cas.jwt.JwtConfiguration;
import org.jasig.cas.jwt.JwtUtils;
import org.jasig.cas.jwt.exception.JwtBuildException;
import org.jasig.cas.jwt.exception.WrongAuthorizationTokenException;
import org.jasig.cas.jwt.response.ExceptionResponse;
import org.jasig.cas.jwt.response.JwtResponse;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Leon.Yu on 2017/9/7.
 */
@RestController("jwtsResource")
public final class JwtsResource {

    private static final Logger logger = LoggerFactory.getLogger(JwtsResource.class);
    private final ObjectMapper jacksonObjectMapper = new ObjectMapper();
    @NotNull
    @Resource(name = "servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    @Qualifier("webApplicationServiceFactory")
    private ServiceFactory<WebApplicationService> webApplicationServiceFactory;

    @Autowired
    private RedisJwtBlacklist jwtBlacklist;

    @Value("${jwt.expirationInMinutes:60}")
    private int expirationInMinutes;

    @Value("${jwt.refreshTokenExpirationInMinutes:20160}")
    private int refreshTokenExpirationInMinutes;

    @Value("${jwt.aheadRefreshTimeInMinutes:60}")
    private int aheadRefreshTokenTimeInMinutes;

    @Autowired
    @Qualifier("centralAuthenticationService")
    private CentralAuthenticationService centralAuthenticationService;

    @NotNull
    @Autowired(required = false)
    @Qualifier("defaultAuthenticationSystemSupport")
    private AuthenticationSystemSupport authenticationSystemSupport = new DefaultAuthenticationSystemSupport();

    @Value("${jwt.issuer}")
    private String issuer;

    public ServicesManager getServicesManager() {
        return servicesManager;
    }

    public void setServicesManager(ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    public ServiceFactory<WebApplicationService> getWebApplicationServiceFactory() {
        return webApplicationServiceFactory;
    }

    public void setWebApplicationServiceFactory(ServiceFactory<WebApplicationService> webApplicationServiceFactory) {
        this.webApplicationServiceFactory = webApplicationServiceFactory;
    }

    public int getExpirationInMinutes() {
        return expirationInMinutes;
    }

    public void setExpirationInMinutes(int expirationInMinutes) {
        this.expirationInMinutes = expirationInMinutes;
    }

    public int getRefreshTokenExpirationInMinutes() {
        return refreshTokenExpirationInMinutes;
    }

    public void setRefreshTokenExpirationInMinutes(int refreshTokenExpirationInMinutes) {
        this.refreshTokenExpirationInMinutes = refreshTokenExpirationInMinutes;
    }

    @RequestMapping(value = "/v1/jwts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createJsonWebTokenByRefreshToken(final HttpServletRequest request) throws JsonProcessingException {
        try {
            JwtResponse jwtResponse = buildJwtResponseInternal(request);
            return new ResponseEntity(jwtResponse, HttpStatus.OK);
        } catch (final AuthenticationException e) {
            final List<String> authnExceptions = new LinkedList<>();
            for (final Map.Entry<String, Class<? extends Exception>> handlerErrorEntry : e.getHandlerErrors().entrySet()) {
                authnExceptions.add(handlerErrorEntry.getValue().getSimpleName());
            }
            logger.error(String.format("Caused by: %s", authnExceptions));

            ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(), authnExceptions.toString());
            return new ResponseEntity(exceptionResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/v1/jwts", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteJsonWebToken(final HttpServletRequest request) throws JwtBuildException {
        String serviceStringFormRequest = request.getParameter(CasViewConstants.MODEL_ATTRIBUTE_NAME_SERVICE);
        if (StringUtils.isEmpty(serviceStringFormRequest)) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setMessage("Parameter service can't be null.");
            return ResponseEntity.badRequest().body(apiResponse);
        }

        String authorizationHeader = request.getHeader("Authorization");
        if (!JwtUtils.isAuthorizationHeaderValid(authorizationHeader)) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setMessage("There is no authorization token or wrong token in headers.");
            return ResponseEntity.badRequest().body(apiResponse);
        }

        final Service service = webApplicationServiceFactory.createService(serviceStringFormRequest);
        final RegisteredService registeredService = this.servicesManager.findServiceBy(service);

        String rawJwt = JwtUtils.extractJwt(authorizationHeader);
        String secretKey = JwtUtils.secretKey(registeredService);
        CasJsonWebToken jsonWebToken = new CasJsonWebToken(rawJwt, secretKey);
        jwtBlacklist.add(jsonWebToken.getJti(), jsonWebToken.getClaims().getExpiration());
        return ResponseEntity.ok().build();
    }

    private JwtResponse buildJwtResponseInternal(HttpServletRequest request) throws Exception {
        JwtResponse jwtResponse = null;
        String serviceStringFormRequest = request.getParameter(CasViewConstants.MODEL_ATTRIBUTE_NAME_SERVICE);
        if (StringUtils.isEmpty(serviceStringFormRequest)) {
            throw new Exception("Parameter service can't be null.");
        }

        final Service service = webApplicationServiceFactory.createService(serviceStringFormRequest);
        final RegisteredService registeredService = this.servicesManager.findServiceBy(service);

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String authorizationHeader = request.getHeader("Authorization");

        boolean isRefreshJwt = JwtUtils.isAuthorizationHeaderValid(authorizationHeader);
        boolean isFetchJsonWebToken = StringUtils.hasText(username) && StringUtils.hasText(password);
        if (isRefreshJwt) {
            jwtResponse = buildJwtResponseByRefreshToken(registeredService, authorizationHeader);
        } else if (isFetchJsonWebToken) {
            jwtResponse = buildJwtResponseByUsernamePassword(username, password, registeredService);
        } else {
            throw new WrongAuthorizationTokenException("There is no authorization token or wrong token in headers.");
        }

        return jwtResponse;
    }

    private JwtResponse buildJwtResponseByUsernamePassword(String username, String password, RegisteredService registeredService) throws AuthenticationException {
        JwtResponse jwtResponse = new JwtResponse();
        Credential credential = buildCredential(username, password);
        final AuthenticationContextBuilder builder = new DefaultAuthenticationContextBuilder(
                this.authenticationSystemSupport.getPrincipalElectionStrategy());
        final AuthenticationTransaction transaction =
                AuthenticationTransaction.wrap(credential);
        this.authenticationSystemSupport.getAuthenticationTransactionManager().handle(transaction, builder);

        // 验证成功，生成JWT
        String secretKey = JwtUtils.secretKey(registeredService);
        JwtConfiguration jwtConfiguration = JwtUtils.config()
                .issuer(issuer)
                .expirationInMinutes(expirationInMinutes)
                .refreshTokenExpirationInMinutes(refreshTokenExpirationInMinutes)
                .secretKey(secretKey)
                .audience(registeredService.getServiceId())
                .principal(username).build();
        jwtResponse.setJwt(jwtConfiguration.jwt());
        jwtResponse.setRefreshToken(jwtConfiguration.refreshToken());
        return jwtResponse;
    }

    private Credential buildCredential(String username, String password) {
        final RememberMeUsernamePasswordCredential credential = new RememberMeUsernamePasswordCredential();
        credential.setUsername(username);
        credential.setPassword(password);
        credential.setRememberMe(true);
        return credential;
    }

    private JwtResponse buildJwtResponseByRefreshToken(RegisteredService registeredService, String authorizationHeader) throws JwtBuildException, WrongAuthorizationTokenException {
        JwtResponse jwtResponse;
        String rawJwt = JwtUtils.extractJwt(authorizationHeader);
        String secretKey = JwtUtils.secretKey(registeredService);
        CasRefreshToken casRefreshToken = new CasRefreshToken(rawJwt, secretKey);
        if (!casRefreshToken.hasAuthority()) {
            throw new WrongAuthorizationTokenException("No permission to refresh token.");
        }

        if (jwtBlacklist.isBlocked(casRefreshToken.getJti())) {
            throw new WrongAuthorizationTokenException("Token has already been added to blacklist.");
        }

        jwtBlacklist.add(casRefreshToken.getJti(), casRefreshToken.getClaims().getExpiration());
        Claims claims = casRefreshToken.getClaims();
        JwtConfiguration jwtConfiguration = new JwtConfiguration(claims.getId(), claims.getIssuer(), secretKey,
                claims.getAudience(), expirationInMinutes, refreshTokenExpirationInMinutes);
        jwtResponse = buildJwtResponse(jwtConfiguration.jwt(), jwtConfiguration.refreshToken());
        return jwtResponse;
    }

    private JwtResponse buildJwtResponse(String jwt, String refreshToken) {
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setJwt(jwt);
        jwtResponse.setRefreshToken(refreshToken);

        if (logger.isDebugEnabled()) {
            logger.debug("Generating Json Web Token : {}", jwtResponse.getJwt());
            logger.debug("Generating Refresh Token : {}", jwtResponse.getRefreshToken());
        }

        return jwtResponse;
    }

    private class ApiResponse {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
