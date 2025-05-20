/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.data.validation;

import kunlun.data.property.PropertySource;
import kunlun.data.property.PropertyUtil;
import kunlun.data.validation.AutoValidator;
import kunlun.data.validation.ValidatorUtil;
import kunlun.data.validation.support.*;
import kunlun.util.MapUtil;
import kunlun.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * The validate auto-configuration.
 * @author Kahle
 */
@Configuration
@EnableConfigurationProperties({ValidateProperties.class})
public class ValidateAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ValidateAutoConfiguration.class);

    @Autowired
    public ValidateAutoConfiguration(ValidateProperties validateProperties,
                                     ApplicationContext applicationContext) {
        registerDefaultValidator();
        registerConfiguredRegexValidator(validateProperties);
        registerSpringValidator(applicationContext);
        registerPropertySourceRegexValidator();
    }

    private void registerDefaultValidator() {
        String idNumberRegex = "(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
        String urlRegex = "^(?:([A-Za-z]+):)?(\\/{0,3})([0-9.\\-A-Za-z]+)(?::(\\d+))?(?:\\/([^?#]*))?(?:\\?([^#]*))?(?:#(.*))?$";
        String emailRegex = "^[0-9A-Za-z][\\.-_0-9A-Za-z]*@[0-9A-Za-z]+(?:\\.[0-9A-Za-z]+)+$";
        String bankCardNumberRegex = "^([1-9]{1})(\\d{14}|\\d{18})$";
        String phoneNumberRegex = "^1\\d{10}$";
        String numericRegex = "^(-|\\+)?\\d+\\.?\\d*$";
        ValidatorUtil.registerValidator("not_blank", new NotBlankValidator());
        ValidatorUtil.registerValidator("not_empty", new NotEmptyValidator());
        ValidatorUtil.registerValidator("not_null", new NotNullValidator());
        ValidatorUtil.registerValidator("is_blank", new IsBlankValidator());
        ValidatorUtil.registerValidator("is_empty", new IsEmptyValidator());
        ValidatorUtil.registerValidator("is_null", new IsNullValidator());
        ValidatorUtil.registerValidator("is_false", new IsFalseValidator());
        ValidatorUtil.registerValidator("is_true", new IsTrueValidator());
        ValidatorUtil.registerValidator("regex:bank_card_number", new RegexValidator(bankCardNumberRegex));
        ValidatorUtil.registerValidator("regex:phone_number", new RegexValidator(phoneNumberRegex));
        ValidatorUtil.registerValidator("regex:id_number", new RegexValidator(idNumberRegex));
        ValidatorUtil.registerValidator("regex:numeric", new RegexValidator(numericRegex));
        ValidatorUtil.registerValidator("regex:email", new RegexValidator(emailRegex));
        ValidatorUtil.registerValidator("regex:url", new RegexValidator(urlRegex));
        ValidatorUtil.registerValidator("bean:bank_card_number:luhn", new BankCardNumberLuhnValidator());
    }

    private void registerSpringValidator(ApplicationContext applicationContext) {
        Map<String, AutoValidator> beansOfType = applicationContext.getBeansOfType(AutoValidator.class);
        if (MapUtil.isEmpty(beansOfType)) { return; }
        for (Map.Entry<String, AutoValidator> entry : beansOfType.entrySet()) {
            AutoValidator validator = entry.getValue();
            if (validator == null) { continue; }
            ValidatorUtil.registerValidator(validator.getName(), validator);
        }
    }

    private void registerConfiguredRegexValidator(ValidateProperties validateProperties) {
        if (validateProperties == null) { return; }
        Map<String, String> regexValidators =
                validateProperties.getRegexValidators();
        if (MapUtil.isEmpty(regexValidators)) { return; }
        for (Map.Entry<String, String> entry : regexValidators.entrySet()) {
            String regex = entry.getValue();
            if (StrUtil.isBlank(regex)) { continue; }
            String name = entry.getKey();
            if (StrUtil.isBlank(name)) { continue; }
            ValidatorUtil.registerValidator(name, new RegexValidator(regex));
        }
    }

    private void registerPropertySourceRegexValidator() {
        String sourceName = "_validator";
        PropertySource source = PropertyUtil.getPropertySource(sourceName);
        if (source == null) { return; }
        Map<String, Object> properties = PropertyUtil.getProperties(sourceName);
        if (MapUtil.isEmpty(properties)) { return; }
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String regex = entry.getValue() != null ? String.valueOf(entry.getValue()) : null;
            if (StrUtil.isBlank(regex)) { continue; }
            String name = entry.getKey();
            if (StrUtil.isBlank(name)) { continue; }
            ValidatorUtil.registerValidator(name, new RegexValidator(regex));
        }
    }

}
