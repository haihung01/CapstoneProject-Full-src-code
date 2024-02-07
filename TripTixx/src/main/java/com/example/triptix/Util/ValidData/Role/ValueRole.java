package com.example.triptix.Util.ValidData.Role;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValueRoleValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueRole {
    String message() default "Invalid Role, must be  CUSTOMER, STAFF, DRIVER";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
