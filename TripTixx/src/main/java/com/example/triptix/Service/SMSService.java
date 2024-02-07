package com.example.triptix.Service;

import com.example.triptix.DTO.ResponseObject;

public interface SMSService {
    ResponseObject<?> sendOTP(String phone);
}
