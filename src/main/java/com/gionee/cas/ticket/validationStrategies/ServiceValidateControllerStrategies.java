package com.gionee.cas.ticket.validationStrategies;

import org.jasig.cas.web.ServiceValidateController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用于配置serviceValidateController验证，默认不验证proxy
 *
 * @author Administrator
 */
@Component
public class ServiceValidateControllerStrategies {

    @Resource
    private ServiceValidateController serviceValidateController;

    @Autowired
    public void setValidationSpecificationClass(@Value("org.jasig.cas.validation.Cas20ProtocolValidationSpecification") final Class<?> validationSpecificationClass) {

        serviceValidateController.setValidationSpecificationClass(validationSpecificationClass);

    }

}
