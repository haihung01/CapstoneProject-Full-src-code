package com.example.triptix.Util.ValidData;


import com.example.triptix.Enum.Type;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ValueTypeValidator implements ConstraintValidator<ValueType, String> {

    @Override
    public void initialize(ValueType constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        List<String> values = new ArrayList<>();
        values.add(Type.LIMOUSINE.name());
        values.add(Type.GHE.name());
        values.add(Type.GIUONG.name());
        return values.contains(value);
    }
}
