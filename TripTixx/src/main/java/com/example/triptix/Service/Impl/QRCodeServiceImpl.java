package com.example.triptix.Service.Impl;


import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Service.QRCodeService;
import com.example.triptix.Util.FileStoreS3;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Autowired
    private FileStoreS3 fileStore;

    @Autowired
    private static Environment env;

    @Override
    public ResponseObject<?> saveQRCodeToAWSS3(String contentQRCode) {
        try{
            String qrcodeName = UUID.randomUUID().toString() + "-" + contentQRCode + "-QRCODE.png";
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(contentQRCode, BarcodeFormat.QR_CODE, 350, 350);

            ResponseObject<?> rs = fileStore.saveQRCodeToAWSS3(qrcodeName, matrix);
            return rs;
        }catch (Exception e){
            return ResponseObject.builder().status(false).message("error upload img QRCode").data(e.getMessage()).build();
        }
    }

    public static void main(String[] args) {
        String codeBooking = "ABC123";
        ResponseObject<?> rs = new QRCodeServiceImpl().saveQRCodeToAWSS3(codeBooking);
        System.out.println(rs.toString());
//        System.out.println(env.getProperty("aws.s3.link_bucket") + rs.getData());
    }
}
