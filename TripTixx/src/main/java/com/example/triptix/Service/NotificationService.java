package com.example.triptix.Service;

import com.example.triptix.DTO.Notification.NotificationDTOcreate;
import com.example.triptix.DTO.ResponseObject;

public interface NotificationService {
    ResponseObject<?> getAll(Integer user, int pageSize, int pageIndex);
    ResponseObject<?> getDetail(int id);
    ResponseObject<?> create(NotificationDTOcreate b);
    ResponseObject<?> updateSeen(int idNotification);
    ResponseObject<?> delete(int id);
}