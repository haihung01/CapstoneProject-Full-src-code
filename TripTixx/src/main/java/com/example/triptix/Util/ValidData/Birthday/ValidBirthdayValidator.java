package com.example.triptix.Util.ValidData.Birthday;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Date;
import java.time.LocalDate;

public class ValidBirthdayValidator implements ConstraintValidator<ValidBirthday, Date> {

    @Override
    public void initialize(ValidBirthday constraintAnnotation) {
    }

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // ko Cho phép trường birthday có giá trị null
        }

        Date currentDate = Date.valueOf(LocalDate.now());
        return value.compareTo(currentDate) < 0;    //birthday < date now -> rs < 0 hay kết quả value.compareTo(currentDate) < 0 = true
    }
}
