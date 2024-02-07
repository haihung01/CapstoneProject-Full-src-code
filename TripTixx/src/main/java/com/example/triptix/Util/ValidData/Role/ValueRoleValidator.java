package com.example.triptix.Util.ValidData.Role;

import com.example.triptix.Enum.Role;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ValueRoleValidator implements ConstraintValidator<ValueRole, String> {

    @Override
    public void initialize(ValueRole constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // Cho phép trường birthday có giá trị null
        }

        List<String> values = new ArrayList<>();
        values.add(Role.CUSTOMER.name());
        values.add(Role.STAFF.name());
        values.add(Role.DRIVER.name());
//        values.add(Role.ADMIN.name());
        return values.contains(value);
    }
}
