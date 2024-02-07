package com.example.triptix.Util.ValidData.AdminCheck;

import com.example.triptix.Enum.AdminCheck;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ValidAdminCheckValidator implements ConstraintValidator<ValueAdminCheck, String> {

    @Override
    public void initialize(ValueAdminCheck constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        List<String> values = new ArrayList<>();
        values.add(AdminCheck.PENDING.name());
        values.add(AdminCheck.ACCEPTED.name());
        values.add(AdminCheck.CANCELED.name());
        return values.contains(value);
    }
}
