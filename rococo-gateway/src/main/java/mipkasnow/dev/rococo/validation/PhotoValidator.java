package mipkasnow.dev.rococo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mipkasnow.dev.rococo.exception.InvalidImageException;
import mipkasnow.dev.rococo.util.PhotoCodecUtil;

public class PhotoValidator implements ConstraintValidator<ValidPhoto, String> {

    private boolean required;

    @Override
    public void initialize(ValidPhoto constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return !required;
        }
        try {
            PhotoCodecUtil.toBytes(value);
            return true;
        } catch (InvalidImageException exception) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(exception.getMessage()).addConstraintViolation();
            return false;
        }
    }
}
