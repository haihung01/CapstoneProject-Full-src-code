package com.example.triptix.Service.Impl;

import com.example.triptix.Enum.ObjectStatus;
import com.example.triptix.Enum.Role;
import com.example.triptix.DTO.Notification.NotificationDTOcreate;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.UserSystem.*;
import com.example.triptix.Model.ConfigSystem;
import com.example.triptix.Model.Station;
import com.example.triptix.Model.UserSystem;
import com.example.triptix.Model.Wallet;
import com.example.triptix.Repository.StationRepo;
import com.example.triptix.Repository.UserSystemRepo;
import com.example.triptix.Repository.WalletRepo;
import com.example.triptix.Service.*;
import com.example.triptix.Util.AesEncryptionUtil;
import com.example.triptix.Util.UTCTimeZoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
@Slf4j
public class UserSystemServiceImpl implements UserSystemService, UserDetailsService {
    public static final String BODY_HAPPYBIRTHDAY_SAVE_DB = "Chúc bạn sinh nhật vui vẻ, Happy Birthday to you !";
    @Autowired
    private UserSystemRepo userSystemRepo;

    @Autowired
    private WalletRepo walletRepo;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ConfigSystemService configSystemService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StationRepo stationRepo;

    @Autowired
    private MailService mailService;

    @Autowired
    private AesEncryptionUtil aesEncryptionUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FireBaseNotificationMessagingServiceImpl fireBaseNotificationMessagingServiceimpl;
    @Override
    public ResponseObject<?> getAll(String role, int pageSize, int pageIndex) {
        Pageable pageable = null;
        if(pageSize != 0 && pageIndex != 0){
            pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
        }else{
            pageIndex = 1;
        }
        List<UserSystem> objList = null;
        Page<UserSystem> objListPage = null;
        int totalPage = 0;
        if (role == null) {
            objListPage = userSystemRepo.findAllNotIncludeAdmin(pageable);
        } else {
            switch (role) {
                case "STAFF":
                    objListPage = userSystemRepo.findByRole("ROLE_STAFF", pageable);
                    break;
                case "CUSTOMER":
                    objListPage = userSystemRepo.findByRole("ROLE_CUSTOMER", pageable);
                    break;
                case "DRIVER":
                    objListPage = userSystemRepo.findByRole("ROLE_DRIVER", pageable);
                    break;
                default:
                    objListPage = userSystemRepo.findAllNotIncludeAdmin(pageable);
            }
        }

        if(objListPage != null){
            totalPage = objListPage.getTotalPages();
            objList = objListPage.getContent();
        }

        List<UserSystemDTOview> objDTOList = new ArrayList<>();
        try {
            for (UserSystem a : objList) {
                UserSystemDTOview DTO = modelMapper.map(a, UserSystemDTOview.class);
                //check nếu là customer thì lấy thêm conin trong ví về (vì chỉ customer mới có wallet)
                if (a.getWallet() != null) {
                    DTO.setCoins(a.getWallet().getBalance());
                }
                DTO.setBirthdayLong(a.getBirthday().getTime() / 1000);
                if(a.getStation() != null){
                    DTO.setNameStationBelong(a.getStation().getName());
                    DTO.setBelongTo(a.getStation().getIdStation());
                }
                objDTOList.add(DTO);
            }
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
        return ResponseObject.builder()
                .status(true).message("get all success")
                .pageSize(objDTOList.size()).pageIndex(pageIndex).totalPage(totalPage)
                .data(objDTOList).build();
    }

    @Override
    public ResponseObject<?> create(UserSystemDTOcreateTimeStamp b1) {
        try {
            UserSystem obj = null;
            //check unique username
            obj = userSystemRepo.findByUserName(b1.getUserName());
            if (obj != null) {
                return ResponseObject.builder().status(false).message("Username đã tồn tại").build();
            }
            //map data từ timestamp sang datetime
            UserSystemDTOcreate b = modelMapper.map(b1, UserSystemDTOcreate.class);
            b.setBirthday(new java.sql.Date(b1.getBirthdayTimeStamp() * 1000));
            //check borthday ko lơớn hơn ngày hiện tại
            if (b.getBirthday().after(new java.sql.Date(System.currentTimeMillis()))) {
                return ResponseObject.builder().status(false).message("Ngày sinh không thể ở trong tương lai").build();
            }

            obj = modelMapper.map(b, UserSystem.class);
            obj.setPassword(AesEncryptionUtil.encrypt(obj.getPassword()));
            obj.setStatus(ObjectStatus.ACTIVE.name());
            obj.setCreatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            obj.setVoucherCoins(0);
            obj.setRole("ROLE_" + b.getRole());

            ResponseObject<?> checkUniquePhoneEmail = null;
            ResponseObject<?> rsSendMailRegister = null;
            String pwdGenerate = generatePWD();
            switch (b.getRole()) {
                case "CUSTOMER":    //CUSTOMER thì bỏ  CitizenIdentityCard, station(belongTo)
                    //check unique phone/email in CUSTOMER
                    checkUniquePhoneEmail = checkUniquePhoneEmailInRole(obj.getPhone(), obj.getEmail(), "ROLE_"+ Role.CUSTOMER.name());
                    if(!checkUniquePhoneEmail.isStatus()){
                        return ResponseObject.builder().status(false).message(checkUniquePhoneEmail.getMessage()).build();
                    }
                    obj.setCitizenIdentityCard(null);
                    obj.setStation(null);
                    userSystemRepo.save(obj);

                    //create wallet of customer
                    UserSystem customer = userSystemRepo.findByUserName(obj.getUserName());
                    Wallet wallet = new Wallet();
                    wallet.setCustomer(customer);
                    wallet.setBalance(0);
                    walletRepo.save(wallet);
                    break;

                case "STAFF":        // bỏ station(belongTo)
                    obj.setPassword(AesEncryptionUtil.encrypt(pwdGenerate));
                    obj.setStation(null);
                    //check unique phone/email in CUSTOMER
                    checkUniquePhoneEmail = checkUniquePhoneEmailInRole(obj.getPhone(), obj.getEmail(), "ROLE_"+Role.STAFF.name());
                    if(!checkUniquePhoneEmail.isStatus()){
                        return ResponseObject.builder().status(false).message(checkUniquePhoneEmail.getMessage()).build();
                    }

                    // check unique CMND/ CCCD
                    if (b.getCitizenIdentityCard() == null) {
                        return ResponseObject.builder().status(false).message("CMND/CCCD (Thẻ nhận dạng công dân) là bắt buộc").build();
                    }

                    UserSystem check = userSystemRepo.findByCitizenIdentityCardAndRole(b.getCitizenIdentityCard(), "ROLE_"+Role.STAFF.name());
                    if (check != null) {
                        return ResponseObject.builder().status(false).message("CMND/CCCD (Thẻ nhận dạng công dân) phải là duy nhất").build();
                    }
                    userSystemRepo.save(obj);

                    //send mail về cho địa chỉ email đó
                    rsSendMailRegister = mailService.sendMaiRegisterSuccess(b1.getEmail(), b1.getUserName(), pwdGenerate, Role.STAFF.name());
                    if(!rsSendMailRegister.isStatus()){
                        System.out.println("Gửi mail cho tài khoản mới không thành công: " + rsSendMailRegister.getMessage());
                    }

                    break;

                case "DRIVER":      //truyền hết
                    obj.setPassword(AesEncryptionUtil.encrypt(pwdGenerate));
                    //check id station trong belongTo]
                    Station checkStation = null;
                    try{
                        checkStation = stationRepo.findById(b.getBelongTo()).orElse(null);
                        if(checkStation == null){
                            return ResponseObject.builder().status(false).message("Vui lòng chọn trạm công tác").build();
                        }
                    }catch (NumberFormatException e){
                        return ResponseObject.builder().status(false).message("Vui lòng chọn trạm công tác").build();
                    }
                    obj.setStation(checkStation);

                    //check unique phone/email in driver
                    checkUniquePhoneEmail = checkUniquePhoneEmailInRole(obj.getPhone(), obj.getEmail(), "ROLE_"+Role.DRIVER.name());
                    if(!checkUniquePhoneEmail.isStatus()){
                        return ResponseObject.builder().status(false).message(checkUniquePhoneEmail.getMessage()).build();
                    }

                    // check unique CMND/ CCCD
                    if (b.getCitizenIdentityCard() == null) {
                        return ResponseObject.builder().status(false).message("CMND/CCCD (Thẻ nhận dạng công dân) là bắt buộc").build();
                    }
                    UserSystem check3 = userSystemRepo.findByCitizenIdentityCardAndRole(b.getCitizenIdentityCard(), "ROLE_"+Role.DRIVER.name());
                    if (check3 != null) {
                        return ResponseObject.builder().status(false).message("CMND/CCCD (Thẻ nhận dạng công dân) phải là duy nhất").build();
                    }
                    userSystemRepo.save(obj);

                    //send mail về cho địa chỉ email đó
                    rsSendMailRegister = mailService.sendMaiRegisterSuccess(b1.getEmail(), b1.getUserName(), pwdGenerate, Role.DRIVER.name());
                    if(!rsSendMailRegister.isStatus()){
                        System.out.println("Gửi mail cho tài khoản mới không thành công: " + rsSendMailRegister.getMessage());
                    }
                    break;
            }
            return ResponseObject.builder().status(true).message("Tạo thành công").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    private ResponseObject<?> checkUniquePhoneEmailInRole(String phone, String email, String role) {
        try{
            UserSystem check = userSystemRepo.findByPhoneAndRole(phone, role);
            if(check != null){
                return ResponseObject.builder().status(false).message("Một Khách hàng khác đã sử dụng số điện thoại này").build();
            }

            check = null; //reset to check email
            check = userSystemRepo.findByEmailAndRole(email, role);
            if(check != null){
                return ResponseObject.builder().status(false).message("Một Khách hàng khác đã sử dụng mail này").build();
            }
            return ResponseObject.builder().status(true).message("valid email, phone success").build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    private ResponseObject<?> checkUniqueEmailInRole(String email, String role) {
        try{
            UserSystem check = userSystemRepo.findByEmailAndRole(email, role);
            if(check != null){
                return ResponseObject.builder().status(false).message("Một Khách hàng khác đã sử dụng mail này").build();
            }
            return ResponseObject.builder().status(true).message("valid success").build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    private ResponseObject<?> checkUniquePhoneInRole(String phone, String role) {
        try{
            UserSystem check = userSystemRepo.findByPhoneAndRole(phone, role);
            if(check != null){
                return ResponseObject.builder().status(false).message("Một Khách hàng khác đã sử dụng số điện thoại này").build();
            }
            return ResponseObject.builder().status(true).message("valid success").build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    public String generatePWD() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%";
        int length = 6;

        Random random = new SecureRandom();
        StringBuilder bookingCodeBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            bookingCodeBuilder.append(randomChar);
        }

        return bookingCodeBuilder.toString();
    }

    @Override
    public ResponseObject<?> bonusCoinsVoucher(int idUserSystem, int festivalCoins) {
        try{
            UserSystem obj = userSystemRepo.findById(idUserSystem).orElse(null);
            if (obj == null) {
                return ResponseObject.builder().status(false).message("Not found user with id "+ idUserSystem).build();
            }
            obj.setVoucherCoins(obj.getVoucherCoins() + festivalCoins);
            userSystemRepo.save(obj);
            return ResponseObject.builder().status(true).message("update success").build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> getDetail(int id) {
        try {
            UserSystem obj = userSystemRepo.findById(id).orElse(null);
            if (obj == null) {
                return ResponseObject.builder().status(false).message("Không tìm thấy").build();
            }
            UserSystemDTOview objDTO = modelMapper.map(obj, UserSystemDTOview.class);
            //check nếu là customer thì lấy thêm conin trong ví về (vì chỉ customer mới có wallet)
            if (obj.getWallet() != null) {
                objDTO.setCoins(obj.getWallet().getBalance());
            }
            objDTO.setBirthdayLong(obj.getBirthday().getTime() / 1000);

            return ResponseObject.builder().status(true).message("found").data(objDTO).build();
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> update(UserSystemDTOupdateTimeStamp b1) {
        try {
            //map data từ timestamp sang datetime
            UserSystemDTOupdate b = modelMapper.map(b1, UserSystemDTOupdate.class);
            b.setBirthday(new java.sql.Date(b1.getBirthdayTimeStamp() * 1000));
            b.setStatus(ObjectStatus.ACTIVE.name());
            //check borthday ko lơớn hơn ngày hiện tại
            if (b.getBirthday().after(new java.sql.Date(System.currentTimeMillis()))) {
                return ResponseObject.builder().status(false).message("error").data("Birthday cannot be in the future").build();
            }

            //check xem id có obj ko
            UserSystem check = userSystemRepo.findById(b.getIdUserSystem()).orElse(null);
            if (check == null) {
                return ResponseObject.builder().status(false).message("not found").build();
            }
            String role = "";
            ResponseObject<?> checkUnique = null;
            //check email
            if(!b1.getEmail().equals(check.getEmail())){
                checkUnique = checkUniqueEmailInRole(b.getEmail(), check.getRole());
                if(!checkUnique.isStatus()){
                    return checkUnique;
                }
            }
            //checkphone
            checkUnique = null;
            if(!b1.getPhone().equals(check.getPhone())){
                checkUnique = checkUniquePhoneInRole(b.getPhone(), check.getRole());
                if(!checkUnique.isStatus()){
                    return checkUnique;
                }
            }
            Station checkStation = null;
            switch (check.getRole()) {
                case "ROLE_CUSTOMER":    //CUSTOMER thì bỏ belongTo, CitizenIdentityCard
                    System.out.println("update CUSTOMER - " + b.getIdUserSystem());
                    b.setCitizenIdentityCard(null);
                    break;
                case "ROLE_STAFF":        //truyền hết bỏ belongTo
                    role = "ROLE_STAFF";
                    System.out.println("update STAFF - " + b.getIdUserSystem());
                    break;
                case "ROLE_DRIVER":    //DRIVER truyền hết
                    role = "ROLE_DRIVER";
                    System.out.println("update DRIVER - " + b.getIdUserSystem());
                    //check id station trong belongTo]
                    try{
                        if(b.getBelongTo() != 0){
                            checkStation = stationRepo.findById(b.getBelongTo()).orElse(null);
                            if(checkStation == null){
                                return ResponseObject.builder().status(false).message("Vui lòng chọn trạm công tác").build();
                            }
                        }
                    }catch (NumberFormatException e){
                        return ResponseObject.builder().status(false).message("Vui lòng chọn trạm công tác").build();
                    }
                    break;
            }
            //check unique CMND/ CCCD
            if (b.getCitizenIdentityCard() != null) {
                if (!check.getCitizenIdentityCard().equals(b.getCitizenIdentityCard())) {
                    UserSystem check3 = userSystemRepo.findByCitizenIdentityCardAndRole(b.getCitizenIdentityCard(), role);
                    if (check3 != null) {
                        return ResponseObject.builder().status(false).message("error").data("CMND/CCCD (CitizenIdentityCard) must be unique").build();
                    }
                }
            }

            UserSystem obj = modelMapper.map(b, UserSystem.class);
            obj.setUserName(check.getUserName());
            obj.setPassword(check.getPassword());
            obj.setCreatedDate(check.getCreatedDate());
            obj.setRole(check.getRole());
            obj.setVoucherCoins(check.getVoucherCoins());
            obj.setMileStone(check.getMileStone());
            obj.setFcmTokenDevide(check.getFcmTokenDevide());
            if(role.equals("ROLE_DRIVER")){
                if(b.getBelongTo() != 0){
                    obj.setStation(checkStation);
                }else{
                    obj.setStation(check.getStation());
                }
            }
            userSystemRepo.save(obj);
            return ResponseObject.builder().status(true).message("cập nhật thành công").build();

        } catch (Exception ex) {
            if(ex.getMessage().contains("user_system")) {
                return ResponseObject.builder().status(false).message("error").data("Phone and Email must be unique").build();
            }
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> delete(int id) {
        try {
            userSystemRepo.deleteById(id);
            return ResponseObject.builder().status(true).message("delete success").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> changePassWord(int idCus, String oldPwd, String newPwd) {
        try{
            UserSystem obj = userSystemRepo.findById(idCus).orElse(null);
            if (obj == null) {
                return ResponseObject.builder().status(false).message("Not found user with id "+ idCus).build();
            }
            // encrypt pwd dưới DB để check vs oldPwd user input
            String oldPwdDB = AesEncryptionUtil.decrypt(obj.getPassword());
            if(!oldPwdDB.equals(oldPwd)){
                return ResponseObject.builder().status(false).message("Wrong password").build();
            }
            obj.setPassword(AesEncryptionUtil.encrypt(newPwd));
            userSystemRepo.save(obj);
            return ResponseObject.builder().status(true).message("update success").build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> deActiveAccountUser(int idUser) {
        try{
            UserSystem obj = userSystemRepo.findById(idUser).orElse(null);
            if (obj == null) {
                return ResponseObject.builder().status(false).message("Not found user with id "+ idUser).build();
            }
            obj.setStatus(ObjectStatus.DEACTIVE.name());
            userSystemRepo.save(obj);
            return ResponseObject.builder().status(true).message("update success").build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> exchangeVoucherCoin(int voucherCoins, int idCustomer) {
        try{
            UserSystem obj = userSystemRepo.findById(idCustomer).orElse(null);
            if (obj == null) {
                return ResponseObject.builder().status(false).message("Not found user with id "+ idCustomer).build();
            }
            if(obj.getVoucherCoins() < voucherCoins){
                return ResponseObject.builder().status(false).message("Not enough voucher coins").build();
            }
            int coninWalletExchange = voucherCoins;  //cộng thức quy đổi: 1 xu . rate = 1 vnđ

            obj.setVoucherCoins(obj.getVoucherCoins()- voucherCoins);
            obj.getWallet().setBalance(obj.getWallet().getBalance()+coninWalletExchange);
            userSystemRepo.save(obj);

            //tạo transaction
            transactionService.create("Đổi Xu Voucher", coninWalletExchange, obj.getWallet().getIdWallet(), null);

            return ResponseObject.builder().status(true).message("update success").build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> notificationCustomerBirthday(String birthday) {
        try{
            //lấy config system ra để lấy birthday coins
            ResponseObject<?> birthdayConinsRs = configSystemService.getDetail(2); //default birthday coins id là 2
            ConfigSystem birthdayConinsObject = (ConfigSystem) birthdayConinsRs.getData();
            int coinWalletBirthday = birthdayConinsObject.getValue();

            //get list customer birthday == birthday
            List<UserSystem> list = userSystemRepo.findByBirthdayAndStatusAndRole(birthday, ObjectStatus.ACTIVE.name(), "ROLE_" + Role.CUSTOMER.name());
            if(list.size() != 0){
                for (UserSystem obj: list) {
                    System.out.println("==> happy birthday: " + obj.getFullName());
                    //notification firebase
                    ResponseObject<?> rsnoti = fireBaseNotificationMessagingServiceimpl.notiBirthday(obj.getFcmTokenDevide());
                    if(!rsnoti.isStatus()){
                        System.out.println(rsnoti.getMessage());
                    }

                    //lưu noti vào DB.
                    rsnoti = notificationService.create(new NotificationDTOcreate(obj.getIdUserSystem(), BODY_HAPPYBIRTHDAY_SAVE_DB));
                    if(!rsnoti.isStatus()){
                        System.out.println("Lỗi khi Lưu noti Birthday vào DB cho khách có id = " + obj.getIdUserSystem());
                    }

                    //cộng tiền sinh nhật vào ví voucher của họ
                    obj.setVoucherCoins(obj.getVoucherCoins()+coinWalletBirthday);

                    userSystemRepo.save(obj);
                }
            }
            return ResponseObject.builder().status(true).message("success").build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> updateFcmToken(String fcmTokenDevide, int idCustomer) {
        try{
            UserSystem obj = userSystemRepo.findById(idCustomer).orElse(null);
            if (obj == null) {
                return ResponseObject.builder().status(false).message("Not found user with id "+ idCustomer).build();
            }
            obj.setFcmTokenDevide(fcmTokenDevide);
            userSystemRepo.save(obj);
            return ResponseObject.builder().status(true).message("update success").build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> activeAccountUser(int idUser) {
        try{
            UserSystem obj = userSystemRepo.findById(idUser).orElse(null);
            if (obj == null) {
                return ResponseObject.builder().status(false).message("Not found user with id "+ idUser).build();
            }
            obj.setStatus(ObjectStatus.ACTIVE.name());
            userSystemRepo.save(obj);
            return ResponseObject.builder().status(true).message("update success").build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //kế thừa UserDetailsService để override lại
        UserSystem user = userSystemRepo.findByUserName(username);
        try {
            user.setPassword(passwordEncoder.encode(aesEncryptionUtil.decrypt(user.getPassword())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //v thì hàm SaveUser() ở dưới sẽ ko cần encode trc khi lưu Db, giữ ng mã pwd ta có thể đọc
        if (user == null) {
            log.error("user not found in the database.");
            throw new UsernameNotFoundException("user not found in the database.");
        } else {
            log.info("user found in the database: {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>(); //Collection<? extends GrantedAuthority>; SimpleGrantedAuthority có kế thừa GrantedAuthority
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
//        //mảng nà cũng đc, ở đây dùng ArrayList()
//        user.getRole().forEach(role -> {               //dòng này và lấy các role của user ra bỏ vào SimpleGrantedAuthority để add vào collection authorities nha, viết tắt á
//            authorities.add(new SimpleGrantedAuthority(role.getName()));
//        });

        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);

        //trả về theo User() của security này nha, bấm vào User() thì ta thấy nó có username, pwd, authorities là Collection<? extends GrantedAuthority> nên ta phải tạo ở trên
    }
}
