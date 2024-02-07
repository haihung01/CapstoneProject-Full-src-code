package com.example.triptix.Util.ValidData.SexGender;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValueSexGenderValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueSexGender {
    String message() default "Invalid Sex-gender, must be MALE/ FEMALE";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
