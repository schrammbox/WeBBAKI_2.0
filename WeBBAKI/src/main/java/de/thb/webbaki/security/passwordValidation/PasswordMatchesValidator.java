package de.thb.webbaki.security.passwordValidation;

import de.thb.webbaki.controller.form.ChangeCredentialsForm;
import de.thb.webbaki.controller.form.UserRegisterFormModel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        boolean isValid = false;

        if (obj instanceof UserRegisterFormModel c) {
            isValid = c.getPassword().equals(c.getConfirmPassword());
        }

        /* Weitere Entitaeten
        if (obj instanceof ProviderRegisterFormModel p) {
            isValid = p.getPassword().equals(p.getConfirmPassword());
        }
        */

        if(!isValid){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode( "confirmPassword" ).addConstraintViolation();
        }
        return isValid;
    }
}
