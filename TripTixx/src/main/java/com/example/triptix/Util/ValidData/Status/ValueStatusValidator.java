package com.example.triptix.Util.ValidData.Status;


import com.example.triptix.Enum.ObjectStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ValueStatusValidator implements ConstraintValidator<ValueStatus, String> {

    @Override
    public void initialize(ValueStatus constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        List<String> values = new ArrayList<>();
        values.add(ObjectStatus.ACTIVE.name());
        values.add(ObjectStatus.DEACTIVE.name());
        return values.contains(value);
    }
}
