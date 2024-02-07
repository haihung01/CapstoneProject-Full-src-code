package com.example.triptix.Util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.triptix.DTO.ResponseObject;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class FileStoreS3 {

    private final AmazonS3 amazonS3;

    @Autowired
    Environment env;        //org.springframework.core.env.Environment;

    public void upload(String path,
                       Optional<Map<String, String>> optionalMetaData,
                       InputStream inputStream) {       //inputstream này là img của ta đã đc chuyển thành stream
        ObjectMetadata objectMetadata = new ObjectMetadata();
        optionalMetaData.ifPresent(map -> {     //này là viê rút gọn map từ optionalMetaData vào ObjectMetadata của aws s3
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });
        String contentLengthInputStream = optionalMetaData.map(map -> map.get("Content-Length")).orElse("0");   //để tránh warn: No content length specified for stream data. Stream contents will be buffered in memory and could result in out of memory errors.
        System.out.println("contentLengthInputStream: " + contentLengthInputStream);
        objectMetadata.setContentLength(Long.parseLong(contentLengthInputStream));
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(env.getProperty("aws.s3.bucketname"), path, inputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead);  //tạo PutObjectRequest đề setting cho img trc khi lên
                                                                                                                    //khúc cannedAcll giúp ta up img lên mà và url từ img đó sẽ đc pulbish cho mn cùng xem
                                                                                                            //lưu path thì khi xóa cũng lấy path này ra (cùng vs xác định tên Bucket) để xác định IMG để xóa nha ra khỏi bucket nha
            amazonS3.putObject(putObjectRequest);   //đưa ảnh lên s3
            System.out.println("-> url: " + amazonS3.getUrl(env.getProperty("aws.s3.bucketname"), path));
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file", e);
        }
    }

    public String deleteImage(String bucketName, String imagePath) {
        try {
            amazonS3.deleteObject(bucketName, imagePath);       //delete img khá đơn giản, xác định bucket + path tính từ bucket vào tới img cần xóa
                                                                //vd: bucket: kphpbucket, path: 808930ba-f2e7-46b2-9110-451ce7b0a28a_face1.jpg (nhớ là tính từ bucket nha)
                                                                // ko phải path như này: https://kphpbucket.s3.ap-southeast-1.amazonaws.com/808930ba-f2e7-46b2-9110-451ce7b0a28a_face1.jpg (sai vì nó ko cần phần URL https://kphpbucket.s3.ap-southeast-1.amazonaws.com/, vì ta đã xác định bucket rồi nên phần URl đầu thừa)
            return "Image deleted successfully.";
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to delete the image", e);
        }
    }

    public ResponseObject<?> saveQRCodeToAWSS3(String nameQRCode, BitMatrix matrix) throws IOException {
        //
        String contentlength = getContentLengthFromBitMatrix(matrix) + "";
        System.out.println("content-length: "+contentlength);
        //get file metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", "image/png");
        metadata.put("Content-Length", contentlength);

        //Save Image in S3 and then save in the database
        try {
            this.upload(nameQRCode, Optional.of(metadata), convertBitMatrixToInputStream(matrix));
        } catch (IOException e) {
            return new ResponseObject<>().builder().status(false).message("Failed to upload IMG QR code ["+nameQRCode+"] to S3").data(e.getMessage()).build();
        }
        return new ResponseObject().builder().status(true).message("success upload img QRCode").data(nameQRCode).build();   //link full: env.getProperty("aws.s3.link_bucket") + nameQRCode
    }

    private long getContentLengthFromBitMatrix(BitMatrix bitMatrix) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);
        byte[] bytes = outputStream.toByteArray();

        return bytes.length;
    }

    private InputStream convertBitMatrixToInputStream(BitMatrix bitMatrix) throws IOException {
        MatrixToImageConfig config = new MatrixToImageConfig(0xFF000000, 0xFFFFFFFF);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        byte[] bytes = outputStream.toByteArray();

        return new ByteArrayInputStream(bytes);
    }
}