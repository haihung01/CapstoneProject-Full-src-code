package com.example.triptix.Util.ValidData.StatusTrip;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValueStatusTripValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueStatusTrip {
    String message() default "Invalid Status, must be READY/RUNNING/FINISHED/CANCELED";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
