package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.EMail.TripSendMail;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Enum.Role;
import com.example.triptix.Service.MailService;
import com.example.triptix.Service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class MailServiceImpl implements MailService {
    private static final String linkLOGO = "https://i.ibb.co/ncG1m3H/btb-logo.png";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisService redisService;

    @Autowired
    private Environment env;

    @Override
    public ResponseObject<?> sendMailOTP(String toEmail) {
        try {
            //create otp
            Random random = new Random();
            int otp = random.nextInt(900000) + 100000;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setSubject("OTP for Bus Ticket Booking System");
            helper.setFrom("BTBs");
            helper.setTo(toEmail);

            boolean html = true;
            helper.setText("<b>Dear Customer</b>," +
                    "<br><br>Your register OTP is: <b>" + otp + "</b>" +
                    "<br><br>Thank you for using Bus Ticket Booking System." +
                    "<br>Best regards," +
                    "<br><b>Bus Ticket Booking System - TripTix</b>" +
                    "<br><img src='" + env.getProperty("logobtb_img") + "'/>", html);
            mailSender.send(message);

            redisService.saveOTPToCacheRedis(toEmail, otp);
            String msg = "Send otp of " + toEmail + " : " + otp;
            System.out.println(msg);

            return ResponseObject.builder().status(true).message("send OTP email success").build();

        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> sendMailBookingSuccess(String toEmail, String ListSeat, int idTrip, TripSendMail tripSendMail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setSubject("Thông tin về đặt vé");
            helper.setFrom("BTBs");
            helper.setTo(toEmail);

            boolean html = true;
            helper.setText("<b>Khách hàng thân mến</b>," +
                    "<br>" +
                    "<br>Chuyến đi của bạn từ <b>" + tripSendMail.getDeparturePoint() + " - " + tripSendMail.getDestination() + "</b>" +
                    "<br>Thời gian chạy dự kiến là từ <b>" + tripSendMail.getStartTime() + " đến " + tripSendMail.getEndTime() + "</b></b>" +

                    "<br>Địa chỉ trạm đón là: <b>" + tripSendMail.getStationStartAddress() + "</b>" +
                    "<br>Thời gian trạm đón dự kiến là: <b>" + tripSendMail.getStationTimeComeStart() + "</b></b>" +

                    "<br>Địa chỉ trạm trả là: <b>" + tripSendMail.getStationEndAddress() + "</b>" +
                    "<br>Thời gian trạm trả dự kiến là: <b>" + tripSendMail.getStationTimeComeEnd() + "</b></b>" +

                    "<br>Chỗ ngồi của bạn: <b>" + ListSeat + "</b>" +
                    "<br>Biển số xe là: <b>" + tripSendMail.getBusLicensePlates() + "</b></b>" +

                    "<br>Mã vé: <b>" + tripSendMail.getTicketCode() + "</b> (đây là mã dùng để kiểm tra vé khách hàng khi lên xe bởi tài xế) </b>" +

                    "<br><br>Cảm ơn bạn đã sử dụng TripTix" +
                    "<br>Chúc bạn có một chuyến đi an toàn." +
                    "<br>Trân trọng," +
                    "<br><b>TripTix</b>" +
                    "<br><img src='" + env.getProperty("logobtb_img") + "' width='200' height='200'/>", html);

            mailSender.send(message);

            return ResponseObject.builder().status(true).message("send booking email success").build();

        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> sendMailBookingSuccessTwo(String toEmail, String name, String startTime, String endTime, String price) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setSubject("Thông tin về đặt vé");
            helper.setFrom("BTBs");
            helper.setTo(toEmail);

            boolean html = true;
            helper.setText("<b>Khách hàng thân mến</b>," +
                    "<br>" +
                    "<br>Chuyến đi của bạn từ <b>" + name +"</b></b>" +
                    "<br>Thời gian khởi hành từ <b>" + startTime +"</b></b>" +
                    "<br>Thời gian kết thúc đến <b>" + endTime +"</b></b>" +
                    "<br>Tiền đã thanh toán: <b>" + price +"</b> vnđ</b>" +
                    "<br>(vào lịch sử các tầm vé trên app để có thể xem chi tiết được các tấm vé bạn đã đặt)" +
                    "<br><br>Cảm ơn bạn đã sử dụng TripTix" +
                    "<br>Chúc bạn có một chuyến đi an toàn." +
                    "<br>Trân trọng," +
                    "<br><b>TripTix</b>" +
                    "<br><img src='" + env.getProperty("logobtb_img") + "' width='200' height='200'/>", html);

            mailSender.send(message);

            return ResponseObject.builder().status(true).message("send booking email success").build();

        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> sendMaiRegisterSuccess(String toEmail, String username, String password, String role) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setSubject("Thông tin tài khoản");
            helper.setFrom("BTBs");
            helper.setTo(toEmail);

            boolean html = true;
            String cauchao = "";
            if (role.equals(Role.STAFF.name())) {
                cauchao = "Nhân viên thân mến";
            } else if (role.equals(Role.DRIVER.name())) {
                cauchao = "Tài xế thân mến";
            }
            helper.setText("<b>" + cauchao + "</b>," +
                    "<br>" +
                    "<br>Chúc mừng bạn đã gia nhập gia đình tripTix" +
                    "<br>Tên đăng nhập (username): <b>" + username + "</b>" +
                    "<br>Mật khẩu (password): <b>" + password + "</b>" +
                    "<br><p><i style='color:red'>Vui lòng thay đổi mật khẩu thành mật khẩu của bạn</i></p>" +
                    "<br><br>Cảm ơn bạn đã sử dụng TripTix" +
                    "<br>Trân trọng," +
                    "<br><b>TripTix</b>" +
                    "<br><img src='" + env.getProperty("logobtb_img") + "' width='200' height='200'/>", html);
            mailSender.send(message);

            return ResponseObject.builder().status(true).message("send register email success").build();

        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> sendMailCancelTrip(String toEmail, String departurePoint, String destination, String date) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setSubject("Chuyến đi bị hủy");
            helper.setFrom("BTBs");
            helper.setTo(toEmail);

            boolean html = true;
            String cauchao = "Khách hàng thân mến";
            helper.setText("<b>" + cauchao + "</b>," +
                    "<br>" +
                    "<br>Chuyến đi từ <b>" + departurePoint + " - " + destination + "</b>" +
                    "<br>Ngày: <b>" + date + "</b>" +
                    "<br><br>Rất xin lỗi bạn vì sự cố này !" +
                    "<br><p><i style='color:red'>Nhân viên sẽ gọi đến để giúp xử lý tình huống này (hotline: 0989876789)</i></p>" +
                    "<br><br>Cảm ơn bạn đã sử dụng TripTix" +
                    "<br>Trân trọng," +
                    "<br><b>TripTix</b>" +
                    "<br><img src='" + env.getProperty("logobtb_img") + "' width='200' height='200'/>", html);
            mailSender.send(message);

            return ResponseObject.builder().status(true).message("send register email success").build();

        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }
}