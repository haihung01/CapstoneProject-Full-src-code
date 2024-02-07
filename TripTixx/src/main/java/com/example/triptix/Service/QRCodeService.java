package com.example.triptix.Service;


import com.example.triptix.DTO.ResponseObject;

public interface QRCodeService {
    ResponseObject<?> saveQRCodeToAWSS3(String contentQRCode);
}
