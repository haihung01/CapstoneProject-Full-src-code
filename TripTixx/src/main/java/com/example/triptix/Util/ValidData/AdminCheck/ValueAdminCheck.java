package com.example.triptix.Util.ValidData.AdminCheck;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidAdminCheckValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueAdminCheck {
    String message() default "Invalid text admin check, must be PENDING/ACCEPTED/CANCELED";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
