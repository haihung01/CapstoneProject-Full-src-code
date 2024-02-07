package com.example.triptix.Service.Impl;


import com.example.triptix.DTO.Notification.NotificationMessage;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Model.SpecialDay;
import com.example.triptix.Service.NotificationMessagingService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FireBaseNotificationMessagingServiceImpl implements NotificationMessagingService {
    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @Autowired
    private Environment env;

    @Override
    public ResponseObject<?> sendNotification(NotificationMessage notificationMessage) {
        Notification notification = Notification
                .builder()
                .setTitle(notificationMessage.getTitle())
                .setBody(notificationMessage.getBody())
                .setImage(notificationMessage.getImage())
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken(notificationMessage.getRecipientToken())
                .putAllData(notificationMessage.getData())
                .build();

        try {
            String rs = firebaseMessaging.send(message);
            return ResponseObject.builder().status(true).message("success").data("Success Sending Notification !! - " + rs).build();
        } catch (FirebaseMessagingException e) {
            return ResponseObject.builder().status(true).message("failed").data("Failed Sending Notification !! - " + e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> notiBirthday(String fcmToken) {
        try {
            if (fcmToken == null || fcmToken.isEmpty()) {
                return ResponseObject.builder().status(false).message("Fcm Token is null").build();
            }
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setRecipientToken(fcmToken);      //tạo thêm FCM_token _Device cho từng customer
            notificationMessage.setTitle("Happy Birthday <3");
            notificationMessage.setBody("Tadaaaaaa, Happy Birthday to You, Best wish for U <3 <3 <3");
            notificationMessage.setImage(env.getProperty("birthday_img"));
            Map<String, String> data = new HashMap<>();
            data.put("HappyBirthday", "To You");
            notificationMessage.setData(data);

            ResponseObject<?> rs = sendNotification(notificationMessage);

            return ResponseObject.builder().status(true).message("Success Sending Notification !!").build();
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> notiSpecialDay(String fcmToken, SpecialDay SpecialDay) {
        try {
            if (fcmToken == null || fcmToken.isEmpty()) {
                return ResponseObject.builder().status(false).message("Fcm Token is null").build();
            }

            if (SpecialDay == null) {
                return ResponseObject.builder().status(false).message("Special Day is null").build();
            }
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setRecipientToken(fcmToken);      //tạo thêm FCM_token _Device cho từng customer
            notificationMessage.setTitle(SpecialDay.getName());
            notificationMessage.setBody("Chúc bạn 1 " + SpecialDay.getName() + " thật nhiều niềm vui <3 <3 <3");
            notificationMessage.setImage(env.getProperty("logobtb_img"));
            Map<String, String> data = new HashMap<>();
            data.put("Happy day", "To You");
            notificationMessage.setData(data);

            ResponseObject<?> rs = sendNotification(notificationMessage);

            return ResponseObject.builder().status(true).message("Success Sending Notification !!").build();
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }
}