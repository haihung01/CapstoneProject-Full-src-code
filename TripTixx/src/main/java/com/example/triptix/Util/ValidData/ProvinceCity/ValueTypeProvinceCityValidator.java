package com.example.triptix.Util.ValidData.ProvinceCity;



import com.example.triptix.Enum.ProvinceCityType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ValueTypeProvinceCityValidator implements ConstraintValidator<ValueTypeProvinceCity, String> {

    @Override
    public void initialize(ValueTypeProvinceCity constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        List<String> values = new ArrayList<>();
        values.add(ProvinceCityType.PROVINCE.name());
        values.add(ProvinceCityType.CITY.name());
        return values.contains(value);
    }
}
