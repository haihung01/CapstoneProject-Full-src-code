package com.example.triptix.Util.ValidData.DateTime;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeFormatValidator.class)
public @interface DateTimeFormat {
    String message() default "Invalid date-time format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class DateTimeFormatValidator implements ConstraintValidator<DateTimeFormat, Date> {
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Allow null values
        }

        try {
            // Parse the date to check if it's in the correct format
            DATE_TIME_FORMAT.format(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
