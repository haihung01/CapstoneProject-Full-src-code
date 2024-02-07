package com.example.triptix.Util.ValidData.StatusTrip;


import com.example.triptix.Enum.StatusTrip;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ValueStatusTripValidator implements ConstraintValidator<ValueStatusTrip, String> {

    @Override
    public void initialize(ValueStatusTrip constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        List<String> values = new ArrayList<>();
        values.add(StatusTrip.READY.name());
        values.add(StatusTrip.RUNNING.name());
        values.add(StatusTrip.FINISHED.name());
        values.add(StatusTrip.CANCELED.name());
        return values.contains(value);
    }
}
