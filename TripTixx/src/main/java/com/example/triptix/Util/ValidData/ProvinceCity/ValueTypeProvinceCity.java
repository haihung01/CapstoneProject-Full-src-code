package com.example.triptix.Util.ValidData.ProvinceCity;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValueTypeProvinceCityValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueTypeProvinceCity {
    String message() default "Invalid Status, must be PROVINCE / CITY";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
