package com.example.triptix.Util.ValidData.RegionProvinceCity;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValueRegionProvinceCityValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueRegionProvinceCity {
    String message() default "Invalid Status, must be BAC/ TRUNG/ NAM";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
