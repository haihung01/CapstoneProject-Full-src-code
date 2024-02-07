package com.example.triptix.Util.ValidData.Status;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValueStatusValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueStatus {
    String message() default "Invalid Status, must be ACTIVE/ DEACTIVE";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
