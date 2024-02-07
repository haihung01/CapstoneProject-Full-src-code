package com.example.triptix.Util.ValidData.RegionProvinceCity;



import com.example.triptix.Enum.RegionType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ValueRegionProvinceCityValidator implements ConstraintValidator<ValueRegionProvinceCity, String> {

    @Override
    public void initialize(ValueRegionProvinceCity constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        List<String> values = new ArrayList<>();
        values.add(RegionType.BAC.name());
        values.add(RegionType.TRUNG.name());
        values.add(RegionType.NAM.name());
        return values.contains(value);
    }
}
