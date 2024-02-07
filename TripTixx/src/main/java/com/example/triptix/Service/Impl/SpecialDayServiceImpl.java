package com.example.triptix.Service.Impl;


import com.example.triptix.Enum.ObjectStatus;
import com.example.triptix.Enum.Role;
import com.example.triptix.DTO.Notification.NotificationDTOcreate;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.SpecialDay.SpecialDayDTOcreate;
import com.example.triptix.DTO.SpecialDay.SpecialDayDTOupdate;
import com.example.triptix.DTO.UserSystem.UserSystemDTOview;
import com.example.triptix.Model.ConfigSystem;
import com.example.triptix.Model.SpecialDay;
import com.example.triptix.Model.UserSystem;
import com.example.triptix.Repository.SpecialDayRepo;
import com.example.triptix.Service.ConfigSystemService;
import com.example.triptix.Service.NotificationService;
import com.example.triptix.Service.SpecialDayService;
import com.example.triptix.Service.UserSystemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SpecialDayServiceImpl implements SpecialDayService {
    @Autowired
    private SpecialDayRepo repo;

    @Autowired
    private UserSystemService userService;

    @Autowired
    private ConfigSystemService configSystemService;

    @Autowired
    private FireBaseNotificationMessagingServiceImpl fireBaseNotificationMessagingServiceImpl;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public ResponseObject<?> getAll(int pageSize, int pageIndex) {
        List<SpecialDay> objDTOList = null;
        try {
            //paging
            Pageable pageable = null;
            if (pageSize != 0 && pageIndex != 0) {
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
                objDTOList = repo.findAll(pageable).getContent();
            } else { //get all
                objDTOList = repo.findAll();
                pageIndex = 1;
            }
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
        return ResponseObject.builder().status(true).message("get all success")
                .pageSize(objDTOList.size()).pageIndex(pageIndex)
                .data(objDTOList).build();
    }

    @Override
    public ResponseObject<?> getDetail(int id) {
        SpecialDay obj = repo.findById(id).orElse(null);
        if (obj == null) {
            return ResponseObject.builder().status(false).message("not found with id " + id).build();
        }
        return ResponseObject.builder().status(true).message("found").data(obj).build();
    }

    @Override
    public ResponseObject<?> create(SpecialDayDTOcreate b) {
        try {
            //parse từ long seconds tnanh2 uitl.date
            Date date = new Date(b.getDateLong() * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            SpecialDay obj = modelMapper.map(b, SpecialDay.class);

            String dateStr = sdf.format(date);
            System.out.println("dateStr: " + dateStr);
            String dateStrDB = dateStr.substring(5);
            System.out.println("dateStrDB: " + dateStrDB);

            obj.setDate(dateStrDB);
            repo.save(obj);
            return ResponseObject.builder().status(true).message("tạo thành công").build();
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    public static void main(String[] args) {
        String str = "2022-02-02";
        System.out.println(str.substring(5));
    }

    @Override
    public ResponseObject<?> update(SpecialDayDTOupdate b) {
        try {
            //check id special day
            SpecialDay obj = repo.findById(b.getIdSpecialDay()).orElse(null);
            if (obj == null) {
                return ResponseObject.builder().status(false).message("not found").build();
            }
            //parse từ long seconds tnanh2 uitl.date
            Date date = new Date(b.getDateLong() * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            obj.setDate(sdf.format(date).substring(0, 5));
            obj.setName(b.getName());
            repo.save(obj);
            return ResponseObject.builder().status(true).message("cập nhật thành công").build();
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> delete(int id) {
        try {
            //check id special day
            SpecialDay obj = repo.findById(id).orElse(null);
            if (obj == null) {
                return ResponseObject.builder().status(false).message("not found").build();
            }
            repo.deleteById(id);
            return ResponseObject.builder().status(true).message("xóa thành công").build();
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> notiSpecialDay(String date) {
        try {
            SpecialDay specialDay = repo.findByDate(date);
            if (specialDay != null) {
                //lấy config system ra để lấy festival coins
                ResponseObject<?> festivalConinsRs = configSystemService.getDetail(1); //default festival coins id là 2
                ConfigSystem festivalConinsObject = (ConfigSystem) festivalConinsRs.getData();
                //hôm nay là ngày gì đó special
                ResponseObject<?> rs = userService.getAll(Role.CUSTOMER.name(), 0, 0);  //lấ sách customer ra
                List<UserSystemDTOview> userSystemDTOviewList = (List<UserSystemDTOview>) rs.getData();
                if (userSystemDTOviewList.size() != 0) {
                    UserSystem userSystem = null;
                    for (UserSystemDTOview userSystemDTOview : userSystemDTOviewList) {     //duyệt qua từng customer
                        System.out.println("noti speciday userSystemDTOview: " + userSystemDTOview.getIdUserSystem());
                        if (userSystemDTOview.getStatus().equals(ObjectStatus.ACTIVE.name())) {   //lọc noti chỉ nhưng customer nào còn ACTIVE
                            //customer nào còn active mới đc nhân
                            userService.bonusCoinsVoucher(userSystemDTOview.getIdUserSystem(), festivalConinsObject.getValue());

                            //noti cho user đó bik (firebase)
                            ResponseObject<?> rsnoti = fireBaseNotificationMessagingServiceImpl.notiSpecialDay(userSystemDTOview.getFcmTokenDevide(), specialDay);
                            if(!rsnoti.isStatus()){
                                System.out.println(rsnoti.getMessage());
                            }

                            //lưu noti vào DB
                            rsnoti = null;
                            rsnoti = notificationService.create(new NotificationDTOcreate(userSystemDTOview.getIdUserSystem(), "Chúc bạn 1 ngày " + specialDay.getName() + " vui vẻ !"));
                            if(!rsnoti.isStatus()){
                                System.out.println("Lỗi khi Lưu noti SpecialDay vào DB cho khách có id = " + userSystemDTOview.getIdUserSystem());
                            }
                        }
                    }
                }
            }
            return ResponseObject.builder().status(true).message("notification success").build();
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }
}
