package com.example.triptix.Service;

import com.example.triptix.DTO.ResponseObject;

import java.util.List;

public interface RedisService {
    ResponseObject<?> saveOTPToCacheRedis(String key, int otp);
    ResponseObject<?> saveValueToCacheRedis(String key, String value);
    ResponseObject<?> checkOTP(String phone, int otp);
    ResponseObject<?> deleteValueOfKey(String key);

    String getValueOfKeyAndDeleteItFromCache(String codeBooking);
    String getValueOfKey(String key);

    boolean antiBookedSameTime(List<String> seatName, int idCustomer);
    boolean deleteAntiBookedSameTime(List<String> seatName, int idCustomer);
}