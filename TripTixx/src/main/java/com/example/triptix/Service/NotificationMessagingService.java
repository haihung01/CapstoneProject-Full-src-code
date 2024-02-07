package com.example.triptix.Service;

import com.example.triptix.DTO.Notification.NotificationMessage;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Model.SpecialDay;

public interface NotificationMessagingService {
    ResponseObject<?> sendNotification(NotificationMessage notificationMessage);
    ResponseObject<?> notiBirthday(String fcmToken);
    ResponseObject<?> notiSpecialDay(String fcmToken, SpecialDay SpecialDay);
}