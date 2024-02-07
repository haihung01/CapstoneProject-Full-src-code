package com.example.triptix.Util.ValidData;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValueTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueType {
    String message() default "Invalid Status, must be LIMOUSINE / GHE / GIUONG";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
