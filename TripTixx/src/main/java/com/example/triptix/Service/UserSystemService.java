package com.example.triptix.Service;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.UserSystem.UserSystemDTOcreateTimeStamp;
import com.example.triptix.DTO.UserSystem.UserSystemDTOupdateTimeStamp;

public interface UserSystemService {
    ResponseObject<?> getAll(String role, int pageSize, int pageIndex);
    ResponseObject<?>  create(UserSystemDTOcreateTimeStamp obj);
    ResponseObject<?> bonusCoinsVoucher(int idUserSystem, int value);
    ResponseObject<?>  getDetail(int id);

    ResponseObject<?>  update(UserSystemDTOupdateTimeStamp b);
    ResponseObject<?>  delete(int id);

//    ResponseObject<?> updateAssignStaff(AssignStaffDTO b);
    ResponseObject<?> changePassWord(int idCus, String oldPwd, String newPwd);
    ResponseObject<?> deActiveAccountUser(int idUser);
    ResponseObject<?> exchangeVoucherCoin(int voucherCoins, int idCustomer);
    ResponseObject<?> notificationCustomerBirthday(String birthday);
    ResponseObject<?> updateFcmToken(String fcmTokenDevide, int idCustomer);
//    ResponseObject<?> recommendRoute(int idUserSystem);

    ResponseObject<?> activeAccountUser(int idUser);
}
