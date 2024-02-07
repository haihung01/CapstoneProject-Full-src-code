package com.example.triptix.Util.ValidData.SexGender;

import com.example.triptix.Enum.Gender;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ValueSexGenderValidator implements ConstraintValidator<ValueSexGender, String> {

    @Override
    public void initialize(ValueSexGender constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // ko Cho phép trường birthday có giá trị null
        }

        List<String> values = new ArrayList<>();
        values.add(Gender.MALE.name());
        values.add(Gender.FEMALE.name());
        return values.contains(value);
    }
}
