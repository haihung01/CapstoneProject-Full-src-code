package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.Notification.NotificationDTOcreate;
import com.example.triptix.DTO.Notification.NotificationDTOview;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Model.Notification;
import com.example.triptix.Model.UserSystem;
import com.example.triptix.Repository.NotificationRepo;
import com.example.triptix.Repository.UserSystemRepo;
import com.example.triptix.Service.NotificationService;
import com.example.triptix.Util.UTCTimeZoneUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepo repo;

    @Autowired
    private UserSystemRepo userSystemRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseObject<?> getAll(Integer user, int pageSize, int pageIndex) {
        try {
            List<Notification> objList = null;
            //paging
            Pageable pageable = null;
            if(pageSize != 0 && pageIndex != 0){
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
            }else{ //get all
                pageIndex = 1;
            }
            if(user != null){
                objList = repo.findByIdUserSystem(user, pageable);
            }else {
                if(pageable != null){
                    objList = repo.findAll(pageable).getContent();
                }else{
                    objList = repo.findAll();
                }
            }
            List<NotificationDTOview> notificationDTOviewList = new ArrayList<>();
            NotificationDTOview notificationDTOview = null;
            for (Notification item: objList) {
                notificationDTOview = modelMapper.map(item, NotificationDTOview.class);
                notificationDTOview.setIdUsersystem(item.getUserSystem().getIdUserSystem());
                notificationDTOview.setCreatedDateL(UTCTimeZoneUtil.convertFormatddMMyyyyHHmmssToLong(item.getCreatedDate()));
                notificationDTOviewList.add(notificationDTOview);
            }
            return ResponseObject.builder().status(true).message("get all success")
                    .data(notificationDTOviewList)
                    .pageIndex(pageIndex).pageSize(notificationDTOviewList.size())
                    .build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> getDetail(int id) {
        try {
            Notification obj = repo.findById(id).get();
            if(obj == null){
                return ResponseObject.builder().status(false).message("not found").build();
            }
            NotificationDTOview notificationDTOview = modelMapper.map(obj, NotificationDTOview.class);
            notificationDTOview.setIdUsersystem(obj.getUserSystem().getIdUserSystem());
            notificationDTOview.setCreatedDateL(UTCTimeZoneUtil.convertFormatddMMyyyyHHmmssToLong(obj.getCreatedDate()));
            return ResponseObject.builder().status(true).message("found").data(obj).build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> create(NotificationDTOcreate b) {
        try {
            Notification obj = new Notification();
            UserSystem user = userSystemRepo.findById(b.getIdUserSystem()).get();
            if(user == null) {
                return ResponseObject.builder().status(false).message("not found user").build();
            }
            obj.setDescription(b.getDescription());
            obj.setSeen(false);
            obj.setUserSystem(user);
            obj.setCreatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN());
            repo.save(obj);
            return ResponseObject.builder().status(true).message("success").build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> updateSeen(int idUser) {
        try {
            UserSystem obj = userSystemRepo.findById(idUser).get();
            if(obj == null){
                return ResponseObject.builder().status(false).message("not found").build();
            }
            int rs = repo.updateSeen(idUser);
            if(rs > 0) {
                return ResponseObject.builder().status(true).message("update success").build();
            }
            return ResponseObject.builder().status(false).message("update fail").build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> delete(int id) {
        try {
            Notification obj = repo.findById(id).get();
            if(obj == null){
                return ResponseObject.builder().status(false).message("not found").build();
            }
            repo.delete(obj);
            return ResponseObject.builder().status(true).message("success").build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }
}