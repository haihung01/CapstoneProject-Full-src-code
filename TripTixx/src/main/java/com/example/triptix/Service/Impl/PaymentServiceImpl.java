package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.Booking.BookingDTOcreateGuest;
import com.example.triptix.Enum.Role;
import com.example.triptix.DTO.Payment.GuestBookingDTO;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Model.Transaction;
import com.example.triptix.Model.UserSystem;
import com.example.triptix.Repository.TransactionRepo;
import com.example.triptix.Repository.UserSystemRepo;
import com.example.triptix.Service.BookingService;
import com.example.triptix.Service.ConfigSystemService;
import com.example.triptix.Service.PaymentService;
import com.example.triptix.Util.VnPayHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private UserSystemRepo userSystemRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private BookingService bookingService;

    @Override
    public ResponseObject<?> createPayment(HttpServletRequest req, int amount_param, int idCustomer) {
        try {
            //check id customer
            UserSystem userSystem = userSystemRepo.findById(idCustomer).orElse(null);
            if (userSystem == null) {
                return ResponseObject.builder().status(false).message("User not found").build();
            }
            if (!userSystem.getRole().equals("ROLE_" + Role.CUSTOMER.name())) {
                return ResponseObject.builder().status(false).message("User not Customer").build();
            }

            //create param for vnpay-url required
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String orderType = "other";
            long amount = amount_param * 100;       //số tiền phải nhận với 100, quy định của vn pay
//        String bankCode = req.getParameter("bankCode");
            //nếu ko truyền giá trị này thì nó sẽ cho ta chọn ngân hàng để thanh toán
            String vnp_TxnRef = VnPayHelper.getRandomNumber(8);
            String vnp_IpAddr = VnPayHelper.getIpAddress(req);    //kham khảo vnp_IpAddr tại https://sandbox.vnpayment.vn/apis/files/VNPAY%20Payment%20Gateway_Techspec%202.1.0-VN.pdf

            String vnp_TmnCode = VnPayHelper.vnp_TmnCode;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");      //hiện gi chỉ hỗ trợ VND

//        if (bankCode != null && !bankCode.isEmpty()) {    //vì ta ko cần bankcode vì ta sẽ tự chọn ngân hàng test, nhìn nó vip bro hơn :))
//            vnp_Params.put("vnp_BankCode", bankCode);     //còn nếu mú chỉ định thì cứ ghi ngân hàng đó ra, chữ in hoa, vd: NCB, VIETCOMBANK, ..
//        }
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + vnp_TxnRef + " cua UserID: " + idCustomer);   //nội dung thanh toán, nhưng ở đây ta fix cứng như bên trái cho lẹ, hợp vs mọi tình huống
            vnp_Params.put("vnp_OrderType", orderType);

//        String locate = req.getParameter("language");
            vnp_Params.put("vnp_Locale", "vn"); //fix cứng luôn vì vnpay hiện tại nó chỉ hỗ trợ tại vietnam
            vnp_Params.put("vnp_ReturnUrl", VnPayHelper.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance();   //() -> TimeZone.getTimeZone("UTC") //Etc/GMT+7
            cld.add(Calendar.HOUR, 7); //vì trên server nó ko nhận giờ GMT+7 nên phải add tay khúc này
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            System.out.println("==> vnp_CreateDate: " + vnp_CreateDate);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = VnPayHelper.hmacSHA512(VnPayHelper.secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = VnPayHelper.vnp_PayUrl + "?" + queryUrl;

            return ResponseObject.builder().status(true).message("create url payment success").data(paymentUrl).build();

            //trả về kiểu này để nó tự chuyển hướng qua url payment của VNpay, kết thức api hiện tại
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.LOCATION, paymentUrl);
//            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> createPayment2(HttpServletRequest req, int amount_param, int idCustomer) {
        try {
            //check id customer
            UserSystem userSystem = userSystemRepo.findById(idCustomer).orElse(null);
            if (userSystem == null) {
                return ResponseObject.builder().status(false).message("User not found").build();
            }
            if (!userSystem.getRole().equals("ROLE_" + Role.CUSTOMER.name())) {
                return ResponseObject.builder().status(false).message("User not Customer").build();
            }

            //create param for vnpay-url required
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String orderType = "other";
            long amount = amount_param * 100;       //số tiền phải nhận với 100, quy định của vn pay
//        String bankCode = req.getParameter("bankCode");
            //nếu ko truyền giá trị này thì nó sẽ cho ta chọn ngân hàng để thanh toán
            String vnp_TxnRef = VnPayHelper.getRandomNumber(8);
            String vnp_IpAddr = VnPayHelper.getIpAddress(req);    //kham khảo vnp_IpAddr tại https://sandbox.vnpayment.vn/apis/files/VNPAY%20Payment%20Gateway_Techspec%202.1.0-VN.pdf

            String vnp_TmnCode = VnPayHelper.vnp_TmnCode;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");      //hiện gi chỉ hỗ trợ VND

//        if (bankCode != null && !bankCode.isEmpty()) {    //vì ta ko cần bankcode vì ta sẽ tự chọn ngân hàng test, nhìn nó vip bro hơn :))
//            vnp_Params.put("vnp_BankCode", bankCode);     //còn nếu mú chỉ định thì cứ ghi ngân hàng đó ra, chữ in hoa, vd: NCB, VIETCOMBANK, ..
//        }
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + vnp_TxnRef + " cua UserID: " + idCustomer);   //nội dung thanh toán, nhưng ở đây ta fix cứng như bên trái cho lẹ, hợp vs mọi tình huống
            vnp_Params.put("vnp_OrderType", orderType);

//        String locate = req.getParameter("language");
            vnp_Params.put("vnp_Locale", "vn"); //fix cứng luôn vì vnpay hiện tại nó chỉ hỗ trợ tại vietnam
            vnp_Params.put("vnp_ReturnUrl", VnPayHelper.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

//            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            // Tạo đối tượng Calendar và đặt múi giờ UTC
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            //Chuyển đổi múi giờ sang GMT+7
//            cld.setTimeZone(TimeZone.getTimeZone("GMT+7"));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            System.out.println("==> vnp_CreateDate: " + vnp_CreateDate);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = VnPayHelper.hmacSHA512(VnPayHelper.secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = VnPayHelper.vnp_PayUrl + "?" + queryUrl;

            return ResponseObject.builder().status(true).message("create url payment success").data(paymentUrl).build();

            //trả về kiểu này để nó tự chuyển hướng qua url payment của VNpay, kết thức api hiện tại
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.LOCATION, paymentUrl);
//            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> bookingGuest(HttpServletRequest req, GuestBookingDTO guestBookingDTO) {
        try {
            //create param for vnpay-url required
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String orderType = "other";
            long amount = guestBookingDTO.getAmountMoneyToRecharge() * 100;       //số tiền phải nhận với 100, quy định của vn pay
//        String bankCode = req.getParameter("bankCode");
            //nếu ko truyền giá trị này thì nó sẽ cho ta chọn ngân hàng để thanh toán
            String vnp_TxnRef = VnPayHelper.getRandomNumber(8);
            String vnp_IpAddr = VnPayHelper.getIpAddress(req);    //kham khảo vnp_IpAddr tại https://sandbox.vnpayment.vn/apis/files/VNPAY%20Payment%20Gateway_Techspec%202.1.0-VN.pdf

            String vnp_TmnCode = VnPayHelper.vnp_TmnCode;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");      //hiện gi chỉ hỗ trợ VND

//        if (bankCode != null && !bankCode.isEmpty()) {    //vì ta ko cần bankcode vì ta sẽ tự chọn ngân hàng test, nhìn nó vip bro hơn :))
//            vnp_Params.put("vnp_BankCode", bankCode);     //còn nếu mú chỉ định thì cứ ghi ngân hàng đó ra, chữ in hoa, vd: NCB, VIETCOMBANK, ..
//        }
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            String info = guestBookingDTO.getNameGuest()        //format:  nameGuest_phoneGuest_emailGuest_idTrip_idOnStation_idoffStation_seatNames
                    + "_" + guestBookingDTO.getPhoneGuest()
                    + "_" + guestBookingDTO.getEmailGuest()
                    + "_" + guestBookingDTO.getIdTrip()
                    + "_" + guestBookingDTO.getIdOnStation()
                    + "_" + guestBookingDTO.getIdoffStation()
                    + "_" ;
            for (int i = 0; i < guestBookingDTO.getSeatName().size(); i++) {
                if(i == 0){
                    info += guestBookingDTO.getSeatName().get(i);
                }else{
                    info += ","+guestBookingDTO.getSeatName().get(i);
                }
            }

            vnp_Params.put("vnp_OrderInfo", "guestbooking: " +info);   //nội dung thanh toán, nhưng ở đây ta fix cứng như bên trái cho lẹ, hợp vs mọi tình huống
            vnp_Params.put("vnp_OrderType", orderType);

//        String locate = req.getParameter("language");
            vnp_Params.put("vnp_Locale", "vn"); //fix cứng luôn vì vnpay hiện tại nó chỉ hỗ trợ tại vietnam
            vnp_Params.put("vnp_ReturnUrl", VnPayHelper.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            //Chuyển đổi múi giờ sang GMT+7
            cld.add(Calendar.HOUR, 7);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            System.out.println("==> vnp_CreateDate: " + vnp_CreateDate);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = VnPayHelper.hmacSHA512(VnPayHelper.secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = VnPayHelper.vnp_PayUrl + "?" + queryUrl;

            System.out.println("payment guest url: "+paymentUrl);

            return ResponseObject.builder().status(true).message("create url payment success").data(paymentUrl).build();

            //trả về kiểu này để nó tự chuyển hướng qua url payment của VNpay, kết thức api hiện tại
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.LOCATION, paymentUrl);
//            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> resultTransaction(String vnp_Amount, String vnp_BankCode, String vnp_OrderInfo, String vnp_PayDate, String vnp_ResponseCode) {
        try {
            ResponseObject responseObject = new ResponseObject();
            if (vnp_ResponseCode.equals("00")) {
                System.out.println("payment from VNpay success");
                //payment success
                //check order info xem là customer hay guest booking
                if(vnp_OrderInfo.contains("guestbooking:")){
                    System.out.println("guest booking : " + vnp_OrderInfo);
                    String[] arr = vnp_OrderInfo.substring(14).split("_");
                        //format:  nameGuest_phoneGuest_emailGuest_idTrip_idOnStation_idoffStation_seatNames
                        // -> pham huynh phuong kha_0971724108_khaphpdzhihi@gmail.com_19873_1_2_A1,A2
                    BookingDTOcreateGuest bookingDTOcreateGuest = new BookingDTOcreateGuest();
                    bookingDTOcreateGuest.setNameGuest(arr[0]);
                    bookingDTOcreateGuest.setPhoneGuest(arr[1]);
                    bookingDTOcreateGuest.setEmailGuest(arr[2]);
                    bookingDTOcreateGuest.setIdTrip(Integer.parseInt(arr[3]));
                    bookingDTOcreateGuest.setCodePickUpPoint(Integer.parseInt(arr[4]));
                    bookingDTOcreateGuest.setCodeDropOffPoint(Integer.parseInt(arr[5]));
                    bookingDTOcreateGuest.setSeatName(Arrays.asList(arr[6].split(",")));
                    ResponseObject<?> result =  bookingService.createGuest(bookingDTOcreateGuest);
                    if(!result.isStatus()){
                        System.out.println("booking guest error: " + result.getMessage());
                        responseObject.setStatus(false);
                        responseObject.setMessage("guest " + result.getMessage());
                        responseObject.setData(result.getData());
                        return responseObject;
                    }
                }else{ //customer
                    System.out.println("customer payment : " + vnp_OrderInfo);
                    //khúc này có thể lưu DB or tăng số tiền đã nạp vào ví
                    int tmp = vnp_OrderInfo.lastIndexOf(":");
                    int idCus = Integer.parseInt(vnp_OrderInfo.substring(tmp + 1).trim());
                    UserSystem userSystem = userSystemRepo.findById(idCus).orElse(null);
                    if (userSystem != null) {
                        //cập nhật ví
                        userSystem.getWallet().setBalance(userSystem.getWallet().getBalance() + (Integer.parseInt(vnp_Amount) / 100));    //tại lúc đầu tạo * 100, nên giờ chia 100 cho đúng
                        userSystemRepo.save(userSystem);
                        //cập nhật transaction này
                        Transaction paymentTransaction = new Transaction();
                        paymentTransaction.setWallet(userSystem.getWallet());
                        String formatvnp_PayDate = vnp_PayDate.substring(0, 4) + "-" + vnp_PayDate.substring(4, 6) + "-" + vnp_PayDate.substring(6, 8)
                                + " " + vnp_PayDate.substring(8, 10) + ":" + vnp_PayDate.substring(10, 12) + ":" + vnp_PayDate.substring(12, 14);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        paymentTransaction.setDate(format.parse(formatvnp_PayDate));    // vnp_PayDate format: yyyyMMddHHmmss -> formatvnp_PayDate: yyyy-MM-dd HH:mm:ss
                        paymentTransaction.setAmount(Integer.parseInt(vnp_Amount) / 100);
                        paymentTransaction.setBankCode(vnp_BankCode);
                        paymentTransaction.setDescription(vnp_OrderInfo);
                        transactionRepo.save(paymentTransaction);
                    }
                }

                if(vnp_OrderInfo.contains("guestbooking:")){
                    responseObject.setMessage("Successfully-guest");
                }else{
                    responseObject.setMessage("Successfully");
                }
                responseObject.setStatus(true);
                responseObject.setData(vnp_OrderInfo);
            } else {
                System.out.println("payment from VNpay fail");
                //payment fail
                responseObject.setStatus(false);
                responseObject.setMessage("Failed");
                responseObject.setData(vnp_OrderInfo);
            }
            return responseObject;
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> resultPayment(String status) {
        try {
            switch (status) {
                case "success":
                    return ResponseObject.builder().status(true).message("Recharge Successfully").build();
                case "fail":
                    return ResponseObject.builder().status(false).message("Recharge Failed").build();
                default:
                    return ResponseObject.builder().status(false).message("Pending").build();
            }
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    public static void main(String[] args) {
//        String info = guestBookingDTO.getNameGuest()
//                + "_" + guestBookingDTO.getPhoneGuest()
//                + "_" + guestBookingDTO.getEmailGuest()
//                + "_" + guestBookingDTO.getIdTrip()
//                + "_" + guestBookingDTO.getIdOnStation()
//                + "_" + guestBookingDTO.getIdoffStation()
//                + "_" + guestBookingDTO.getSeatNames();
        String info = "guestbooking: pham huynh phuong kha_0971724708_khaphpdz@gmail.com_19873_1_2_A1,A2";
        System.out.println(info.substring(14));
        String[] tmp = info.substring(14).split("_");
        System.out.println("name guest: " + tmp[0]);
        System.out.println("phone guest: " + tmp[1]);
        System.out.println("email guest: " + tmp[2]);
        System.out.println("id trip: " + tmp[3]);
        System.out.println("id on station: " + tmp[4]);
        System.out.println("id off station: " + tmp[5]);
        System.out.println("seat names: " + tmp[6]);
        List<String> seatList = Arrays.asList(tmp[6].split(","));
        System.out.println("seat names: " +seatList.toString());
    }
}
