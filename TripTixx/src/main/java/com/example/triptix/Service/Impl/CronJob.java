package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Enum.AdminCheck;
import com.example.triptix.Enum.StatusTrip;
import com.example.triptix.Enum.TicketStatus;
import com.example.triptix.Model.Booking;
import com.example.triptix.Model.Ticket;
import com.example.triptix.Model.Trip;
import com.example.triptix.Repository.TicketRepo;
import com.example.triptix.Repository.TripRepo;
import com.example.triptix.Service.SpecialDayService;
import com.example.triptix.Service.UserSystemService;
import com.example.triptix.Util.UTCTimeZoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Service
@EnableScheduling
public class CronJob {
    private static final short TIME_CHECK_FINISH_AUTO = 2;
    @Autowired
    private UserSystemService userSystemService;

    @Autowired
    private SpecialDayService specialDayService;

    @Autowired
    private TripRepo tripRepo;

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private TripServiceImpl tripServiceImpl;

    @Scheduled(cron = "0 * * * * *")  //chạy vào 0s mỗi phút - dùng để quét xem xe tới giờ chạy chưa
//    @Scheduled(cron = "*/5 * * * * *")  //chạy vào 0s mỗi phút - dùng để quét xem xe tới giờ chạy chưa
    public void cronjob1() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 7);
            //lấ format ngày giờ hện tại
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String now = format.format(calendar.getTime());
            if (now.substring(14).trim().equals("00")) {
                System.out.println(now);
            }

            //check xem có chuyên nào finish mà quên bấm finish ko, nê sau 2 tiếng chưa có thì tự chuyển status FINISH
            ResponseObject<?> rsCheckFinsh = changeStatusTripAndBookingWhenFinish(now.substring(0, 10));
            if (!rsCheckFinsh.isStatus()) {
                System.out.println(now);
                System.out.println("Something wrong, change status FINISH failed to Trip and Booking " + rsCheckFinsh.getData());
            }

            //check finsh cho case ở ngày quá khứ, hay nó end lúc 23h tối, vì method call check ngày hiện tai5, nên case hôm qua 23h end cha bắt
            calendar.add(Calendar.DATE, -1);
            now = format.format(calendar.getTime());
//            System.out.println("yesterday : " + now);
            ResponseObject<?> rsCheckFinshBefore = changeStatusTripAndBookingWhenFinish(now.substring(0, 10));
            if (!rsCheckFinshBefore.isStatus()) {
                System.out.println("yesterday : " + now);
                System.out.println("Something wrong, change status FINISH failed to Trip and Booking " + rsCheckFinshBefore.getData());
            }

            //chck time đên giờ chạy
            calendar.add(Calendar.DATE, 1);
            now = format.format(calendar.getTime());
            ResponseObject<?> rs = changeStatusTripAndBookingWhenStartRun(now);
            if (!rs.isStatus()) {
                System.out.println("Something wrong, change status RUN failed to Trip and Booking " + rs.getData());
            }

            //check noti user xem có chuyến nào 1tieng nữa chạy ko
            calendar.add(Calendar.HOUR, 1);
            rs = notiTripRunAfter1Hour(format.format(calendar.getTime()));
            if (!rs.isStatus()) {
                System.out.println("Something wrong, noti Trip Run After 1Hour failed. " + rs.getData());
            }
            calendar.add(Calendar.HOUR, -1);   //reset lại giờ

            //check noti tài xế khi chuyến sắp finsih trc 30ph -> chắc ko cần vì 1 xe sẽ có ít nhất 2 tài xế, 1 ng main và 1 ng phụ xe, ng phụ xe sẽ dùng để kiểm tra khách hàng (checkin, check out) cũng như là finish trip (nếu ko finish thì sau 2 tiếng nó cũng tự finish auto)
//            calendar.add(Calendar.MINUTE, 30);
//            rs = notiTripFinishBefore30Minutes(format.format(calendar.getTime()));
//            if (!rs.isStatus()) {
//                System.out.println("Something wrong, noti Trip Run After 1Hour failed. " + rs.getData());
//            }
//            calendar.add(Calendar.MINUTE, -30);  //reset lại giờ

            //cát now lấy time hiện tại ra, nếu 00:01 thì check noti borthday và specification day
            if (now.substring(11, now.length()).equals("00:01")) {
                System.out.println("--> new day become");
                notiBirthdayAndSpecialDay();
                System.out.println("end noti");
            }
        } catch (Exception ex) {
            System.out.println("error at scheduleJob - changeStatusTripAndBookingWhenStartRun() - " + ex.getMessage());
        }
    }

//    public ResponseObject<?> notiTripFinishBefore30Minutes(String format) {
//        try {
//            List<Integer> listIdDriver = bookingRepo.findListIdDriverHasTripRunWithTime(format + "%");
//            ResponseObject<?> rs1 = notiUserSystemWithContent(listIdDriver, "Xe sắp đến", "Bác tài ơi, tầm 30 phút nũa xe sẽ hoàn thành theo dự kiến !");
//            if (!rs1.isStatus()) {
//                System.out.println("listIdCustomer findListIdDriverHasTripRunWithTime: " + listIdDriver);
//                System.out.println("Something wrong, noti Trip Finish Before 30Minutes failed to User " + rs1.getMessage());
//            }
//            return ResponseObject.builder().status(true).message("success").build();
//        } catch (Exception ex) {
//            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
//        }
//    }

    public ResponseObject<?> notiTripRunAfter1Hour(String format) { //format dd-MM-yyyy HH:mm
        try {
            List<Integer> listIdCustomer = tripRepo.getListIdCustomerTripByDepartureDate(format);
            ResponseObject<?> rs1 = tripServiceImpl.notiUserSystemWithContent(listIdCustomer, "Xe sắp khởi hành", "Bạn ơi,chuyến xe của bạn sau 1 tiếng nữa xuất phát !");
            if (!rs1.isStatus()) {
                System.out.println("listIdCustomer notiTripRunAfter1Hour: " + listIdCustomer);
                System.out.println("Something wrong, noti Trip Run After 1Hour failed to User " + rs1.getMessage());
            }
            return ResponseObject.builder().status(true).message("success").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    public ResponseObject<?> changeStatusTripAndBookingWhenStartRun(String startTime) { //dd-MM-yyyy HH:mm
        try {
            //get all trip  with status ready + admin_check = accpet + giờ hiện tại (tính đến phút)
            List<Trip> listIdTrip = tripRepo.getListIdTripRunningNow(startTime, StatusTrip.READY.toString());
            for (Trip trip : listIdTrip) {
                System.out.println("==> id trip start: " + trip.getIdTrip());
                if (trip.getBookings().size() > 0) {
                    System.out.println("==> id trip start: " + trip.getIdTrip() + " - have booking");
                } else {
                    System.out.println("==> id trip start: " + trip.getIdTrip() + " - no booking");
                    //ko có booking thì change status --> CANCEL
                    trip.setStatus(StatusTrip.CANCELED.name());
                    if (trip.getAdminCheck().equals(AdminCheck.PENDING.name())) {
                        trip.setAdminCheck(AdminCheck.CANCELED.name());
                    }
                }
                tripRepo.save(trip);
            }
            return ResponseObject.builder().status(true).message("get success").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }

    }

    public ResponseObject<?> changeStatusTripAndBookingWhenFinish(String startTime) {
        try {
            //get all trip  đang run, check xem có trip nào quá giờ finish mà vẫn cha finish ko
            List<Trip> listIdTrip = tripRepo.getListIdTripRunning(startTime, StatusTrip.RUNNING.name());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 7);
            for (Trip trip : listIdTrip) {
                System.out.println("==> id trip run: " + trip.getIdTrip());
                System.out.println("==> estimated end time: " + trip.getEndDate());
                if ((calendar.getTimeInMillis() - trip.getEndDate().getTime()) >= (TIME_CHECK_FINISH_AUTO * 60 * 60 * 1000)) {   //2 gio72, nếu tích time now - end time >= 2hour thì nó cần phải finsih ngay
                    trip.setStatus(StatusTrip.FINISHED.toString());
                    trip.setUpdatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
                    //change status bủa all ticket
                    for (Ticket itemTicket : trip.getTickets()) {
                        if (itemTicket.getStatus().equals(TicketStatus.CHECK_IN.name())) {
                            itemTicket.setStatus(TicketStatus.FINISHED.name());
                        }else if (itemTicket.getStatus().equals(TicketStatus.NOT_CHECKIN.name())) {
                            itemTicket.setStatus(TicketStatus.NO_SHOW.name());
                        }
                        ticketRepo.save(itemTicket);
                    }
                    tripRepo.save(trip);
                }
            }
            return ResponseObject.builder().status(true).message("success").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    public void notiBirthdayAndSpecialDay() {
        try {
            //lấ format ngày giờ hện tại
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 7); //giờ server
            String now = format.format(calendar.getTime());
            System.out.println("--> Day now : " + now);

            //noti birthday cho user
            ResponseObject<?> rs = userSystemService.notificationCustomerBirthday("%" + now.substring(5) + "%");
            if (!rs.isStatus()) {
                System.out.println("Something wrong, noti Birthday failed to User " + rs.getMessage());
            }

            //noti special day cho user (những ngày lễ)
            rs = null;
            rs = specialDayService.notiSpecialDay(now.substring(5));
            if (!rs.isStatus()) {
                System.out.println("Something wrong, noti special day failed to User" + rs.getData());
            }

            //noti cho khách, tài xế, trip ngay hôm sau chạy
            rs = null;
            rs = notiTripReadyForCustomer();
            if (!rs.isStatus()) {
                System.out.println("Something wrong, notiTripReadyForCustomer failed " + rs.getData());
            }

        } catch (Exception ex) {
            System.out.println("error at notiBirthdayAndSpecialDay - " + ex.getMessage());
        }
    }

    public ResponseObject<?> notiTripReadyForCustomer() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 7); //add 7 hour = GMT+7 VietNam
            calendar.add(Calendar.DATE, 1); //add 1 day to noti
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

            //lấy list id customer để noti
            List<Trip> listTrip= tripRepo.findListTripRunByDay(format.format(calendar.getTime()));
            System.out.println("listTripRunTomorrow: " + listTrip.size());
            ResponseObject<?> rs1 = null;
            for (Trip trip: listTrip) {
                if(trip.getBookings().size() > 0){
                    for (Booking booking: trip.getBookings()) {
                        if(booking.getCustomer() == null){ //guest
                            continue;
                        }else{ //customer
                            rs1 = null;
                            rs1 = tripServiceImpl.notiUserSystemWithContent(Collections.singletonList(booking.getCustomer().getIdUserSystem()), "Xe sẵn sàng", "Bạn ơi, ngày mai bạn sẽ có 1 chuyến xe đấy nhé !");
                            if (!rs1.isStatus()) {
                                System.out.println("Something wrong, noti trip run before 1 day failed to User " + rs1.getMessage());
                            }
                        }
                    }
                }
                //lấy list tài xế để noti
                rs1 = null;
                rs1 = tripServiceImpl.notiUserSystemWithContent(Collections.singletonList(trip.getDriver().getIdUserSystem()), "Xe sẵn sàng", "Bác tài ơi, ngày mai bạn sẽ có 1 chuyến xe đấy nhé !");
                if (!rs1.isStatus()) {
                    System.out.println("Something wrong, noti trip run before 1 day failed to User " + rs1.getMessage());
                }
            }
            return ResponseObject.builder().status(true).message("success").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    public static void main(String[] args) {
        String date = "2022-11-01";
        System.out.println(date.substring(4));
    }
}
