package com.example.triptix.Util.ValidData.Birthday;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidBirthdayValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBirthday {
    String message() default "Invalid birthday, must be in the past";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
