package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.Booking.*;
import com.example.triptix.DTO.EMail.TripSendMail;
import com.example.triptix.DTO.Notification.NotificationDTOcreate;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Vehicle.VehicleDTOview;
import com.example.triptix.Model.*;
import com.example.triptix.Repository.*;
import com.example.triptix.Service.BookingService;
import com.example.triptix.Service.MailService;
import com.example.triptix.Service.NotificationService;
import com.example.triptix.Util.UTCTimeZoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class BookingServiceImpl  implements BookingService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private TicketTypeRepo ticketTypeRepo;

    @Autowired
    private TicketTypeInTripRepo ticketTypeInTripRepo;

    @Autowired
    private UserSystemRepo userSystemRepo;

    @Autowired
    private ConfigSystemRepo configSystemRepo;

    @Autowired
    private TripRepo tripRepo;

    @Autowired
    private StationInRouteRepo stationInRouteRepo;

    @Autowired
    private StationRepo stationRepo;

    @Autowired
    private WalletRepo walletRepo;

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private RouteRepo routeRepo;

    @Autowired
    private VehicleRepo vehicleRepo;

    @Autowired
    private PaymentTransactionRepo paymentTransactionRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private MailService mailService;

    @Autowired
    private StationTimeComeRepo stationTimeComeRepo;

    @Autowired
    private NotificationService notificationService;


    public String generateRandomBookingCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
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
    public ResponseObject<?> getAll(Integer idTrip, Integer idCustomer) {
        try {
            List<Booking> listBooking;
            // Lọc dựa trên idTrip và idCustomer
            if (idTrip != null && idCustomer != null) {
                listBooking = bookingRepo.findByIdTripAndIdCustomer(idTrip, idCustomer);
            } else if (idTrip != null) {
                listBooking = bookingRepo.findByIdTrip(idTrip);
            } else if (idCustomer != null) {
                listBooking = bookingRepo.findByIdCustomer(idCustomer);
            } else {
                // Nếu không có bất kỳ điều kiện lọc nào, lấy tất cả
                listBooking = bookingRepo.findAll();
            }
            List<BookingDTOview> listDTO = new ArrayList<>();
            for (Booking item : listBooking) {
                BookingDTOview dto = modelMapper.map(item, BookingDTOview.class);
                dto.setIdBooking(item.getIdBooking());
                dto.setIdCustomer(item.getCustomer().getIdUserSystem());
                dto.setIdTrip(item.getTrip().getIdTrip());


                //Lấy customer ra
                UserSystem user = userSystemRepo.findByIdCustomer(item.getCustomer().getIdUserSystem());
                if(user != null){
                    UserSystemDTOviewInBooking userDTO = modelMapper.map(user, UserSystemDTOviewInBooking.class);
                    userDTO.setIdUserSystem(user.getIdUserSystem());

                    dto.setCustomer(userDTO);
                }

                //Lay trip ra
                Trip trip = tripRepo.findById(item.getTrip().getIdTrip()).orElse(null);
                TripDTOviewInBooking tripDTO = modelMapper.map(trip, TripDTOviewInBooking.class);
                tripDTO.setIdTrip(trip.getIdTrip());
                tripDTO.setIdRoute(trip.getRoute().getIdRoute());
                tripDTO.setIdStaff(trip.getStaff().getIdUserSystem());
                tripDTO.setIdVehicle(trip.getVehicle().getIdBus());
                tripDTO.setIdDriver(trip.getDriver().getIdUserSystem());
                //Lấy trip ra thì lấy luôn route
                Route route = routeRepo.findById(trip.getRoute().getIdRoute()).orElse(null);
                RouteDTOviewInBooking routeDTO = modelMapper.map(route, RouteDTOviewInBooking.class);
                routeDTO.setIdRoute(route.getIdRoute());
                routeDTO.setDeparturePoint(route.getStartProvinceCity().getName());
                routeDTO.setDestination(route.getEndProvinceCity().getName());
                tripDTO.setRoute(routeDTO);

                //lấy trip ra lấy luôn xe
                Vehicle vehicle = vehicleRepo.findById(trip.getVehicle().getIdBus()).orElse(null);
                VehicleDTOview vehicleDTO = modelMapper.map(vehicle, VehicleDTOview.class);
                vehicleDTO.setIdBus(vehicle.getIdBus());
                vehicleDTO.setImgLink(vehicle.getImageLink());
                tripDTO.setVehicle(vehicleDTO);

                dto.setTrip(tripDTO);


                //Lấy ticket ra
                List<Ticket> listTicket = ticketRepo.findByIdBooking(item.getIdBooking());
                List<TicketDTOview> listTicketDTO = new ArrayList<>();
                for (Ticket ticketItem : listTicket) {
                    TicketDTOview ticketDTO = modelMapper.map(ticketItem, TicketDTOview.class);
                    ticketDTO.setIdTicket(ticketItem.getIdTicket());
                    ticketDTO.setIdTicketType(ticketItem.getTicketType().getIdTicketType());
                    ticketDTO.setCreatedDate(ticketItem.getCreateDate());
                    ticketDTO.setIdBooking(ticketItem.getBooking().getIdBooking());
                    ticketDTO.setIdOnStation(ticketItem.getOnStation().getIdStation());
                    ticketDTO.setInOffStation(ticketItem.getOffStation().getIdStation());
                    ticketDTO.setIdTrip(ticketItem.getTrip().getIdTrip());
                    listTicketDTO.add(ticketDTO);
                }
                dto.setListTicket(listTicketDTO);
                listDTO.add(dto);
            }
            return ResponseObject.builder().status(true).message("Lấy thông tin các tấm vé thành công.").data(listDTO).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra.").build();
        }
    }

    @Override
    public ResponseObject<?> getDetail(int id) {
        try {
            Booking booking = bookingRepo.findById(id).orElse(null);
            if (booking == null) {
                return ResponseObject.builder().status(false).message("Khong có id booking này.").build();
            }
            BookingDTOview dto = modelMapper.map(booking, BookingDTOview.class);
            dto.setIdBooking(booking.getIdBooking());
            dto.setIdCustomer(booking.getCustomer().getIdUserSystem());
            dto.setIdTrip(booking.getTrip().getIdTrip());


            //Lấy customer ra
            UserSystem user = userSystemRepo.findById(booking.getCustomer().getIdUserSystem()).orElse(null);
            if(user != null){
                UserSystemDTOviewInBooking userDTO = modelMapper.map(user, UserSystemDTOviewInBooking.class);
                userDTO.setIdUserSystem(user.getIdUserSystem());

                dto.setCustomer(userDTO);
            }

            //Lay trip ra
            Trip trip = tripRepo.findById(booking.getTrip().getIdTrip()).orElse(null);
            TripDTOviewInBooking tripDTO = modelMapper.map(trip, TripDTOviewInBooking.class);
            tripDTO.setIdTrip(trip.getIdTrip());
            tripDTO.setIdRoute(trip.getRoute().getIdRoute());
            tripDTO.setIdStaff(trip.getStaff().getIdUserSystem());
            tripDTO.setIdVehicle(trip.getVehicle().getIdBus());
            tripDTO.setIdDriver(trip.getDriver().getIdUserSystem());
            //Lấy trip ra thì lấy luôn route
            Route route = routeRepo.findById(trip.getRoute().getIdRoute()).orElse(null);
            RouteDTOviewInBooking routeDTO = modelMapper.map(route, RouteDTOviewInBooking.class);
            routeDTO.setIdRoute(route.getIdRoute());
            routeDTO.setDeparturePoint(route.getStartProvinceCity().getName());
            routeDTO.setDestination(route.getEndProvinceCity().getName());
            tripDTO.setRoute(routeDTO);

            //lấy trip ra lấy luôn xe
            Vehicle vehicle = vehicleRepo.findById(trip.getVehicle().getIdBus()).orElse(null);
            VehicleDTOview vehicleDTO = modelMapper.map(vehicle, VehicleDTOview.class);
            vehicleDTO.setIdBus(vehicle.getIdBus());
            vehicleDTO.setImgLink(vehicle.getImageLink());
            tripDTO.setVehicle(vehicleDTO);

            dto.setTrip(tripDTO);


            //Lấy ticket ra
            List<Ticket> listTicket = ticketRepo.findByIdBooking(booking.getIdBooking());
            List<TicketDTOview> listTicketDTO = new ArrayList<>();
            for (Ticket ticketItem : listTicket) {
                TicketDTOview ticketDTO = modelMapper.map(ticketItem, TicketDTOview.class);
                ticketDTO.setIdTicket(ticketItem.getIdTicket());
                ticketDTO.setIdTicketType(ticketItem.getTicketType().getIdTicketType());
                ticketDTO.setCreatedDate(ticketItem.getCreateDate());
                ticketDTO.setIdBooking(ticketItem.getBooking().getIdBooking());
                ticketDTO.setIdOnStation(ticketItem.getOnStation().getIdStation());
                ticketDTO.setInOffStation(ticketItem.getOffStation().getIdStation());
                ticketDTO.setIdTrip(ticketItem.getTrip().getIdTrip());
                listTicketDTO.add(ticketDTO);
            }
            dto.setListTicket(listTicketDTO);
            return ResponseObject.builder().status(true).message("Lấy thông tin các tấm vé thành công.").data(dto).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra.").build();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject<?> create(BookingDTOcreateMore b) {
        String messageError = null;
        try {
            Booking obj = modelMapper.map(b, Booking.class);
            ConfigSystem configSystem15 = configSystemRepo.findByConfigSystemTimeCanNotBook();
            short timeCanNotBook = (short) configSystem15.getValue();//15->  15ph trc khi xe chạy thì không đc book vé của chuyến đó
            Trip trip = tripRepo.findById(b.getIdTrip()).orElse(null);
            //Chck trip nếu ko full slot ghé k dc book , check trip phãi có trạng thái ready và adminCheck là ACCEPT
            if (trip == null) {
                return ResponseObject.builder().status(false).message("Không có trip đó").build();
            } else if (!trip.getAdminCheck().equals("ACCEPTED")) {   // && !trip.getStatus().equals("READY")
                return ResponseObject.builder().status(false).message("Chuyến đi chưa được admin chấp thuận").build();
            } else if (trip.getStatus().equals("FINISHED")) {
                return ResponseObject.builder().status(false).message("Chuyến đã hoàn thành không thể đặt").build();
            } else if (trip.getStatus().equals("RUNNING")) {
                return ResponseObject.builder().status(false).message("Chuyến đang trên đường không thể đặt").build();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            //Validate 15 phut truoc khi xe chay ko dc book
            //Ngày giờ hiện tại
            Calendar timeNow = Calendar.getInstance();
            timeNow.add(Calendar.HOUR, 7);
            Date currentDate = timeNow.getTime();
            //Ngày giờ xe chạy
//            String startTimeTmp = String.valueOf(trip.getDepartureDate());
//            Date startTime = dateFormat.parse(startTimeTmp);
            //Trừ 1 tiếng trước khi xe chạy
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(trip.getDepartureDate());
            calendar.add(Calendar.MINUTE, -(timeCanNotBook));
            Date start1 = calendar.getTime();
            if (currentDate.after(start1)) {
                return ResponseObject.builder().status(false).message(timeCanNotBook + " phút trước khi xe khởi hành, bạn không thể đặt vé cho chuyến đi đó").build();
            }

            UserSystem customer = userSystemRepo.findByIdCustomerAndStaff(b.getIdCustomer());
            if (customer == null) {
                return ResponseObject.builder().status(false).message("Không có id khách hàng.").build();
            }
            //Booking set còn mỗi total Price;
            obj.setCreatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            obj.setCustomer(customer);
            obj.setTrip(trip);
            Wallet wallet = walletRepo.findByIdCustomer(customer.getIdUserSystem());
            List<TicketCreate> listTicket =  b.getListTicket();
            double totalAllPrice = 0;
            double totalPrice = 0;
            int totalSeat = 0;
            ConfigSystem configSystem16 = configSystemRepo.findBySeatPerBooking();
            int seatPerBooking = configSystem16.getValue();
            //start for list ticket
            for (int y = 0; y < listTicket.size(); y++){

                Station stationOnTicket;
                Station stationOffTicket;
                //Set name của station dựa vào code của station
                StationInRoute tripPickUp = stationInRouteRepo.findByRouteAndIdStation(trip.getRoute().getIdRoute(), b.getListTicket().get(y).getCodePickUpPoint());
                if (tripPickUp != null) {
                    stationOnTicket = stationRepo.findByIdStation(tripPickUp.getStation().getIdStation());
                } else {
                    return ResponseObject.builder().status(false).message("Điểm lên không nằm trong tuyến đường này.").build();
                }
              
                StationInRoute tripDropOff = stationInRouteRepo.findByRouteAndIdStation(trip.getRoute().getIdRoute(), b.getListTicket().get(y).getCodeDropOffPoint());
                if (tripDropOff != null) {
                    stationOffTicket = stationRepo.findByIdStation(tripDropOff.getStation().getIdStation());
                  
//             }
//             double totalPrice = 0;
//             double price = 0;
//             List<BookingSuccessTripSendMail> bookingSuccessTripSendMails = new ArrayList<>();
//             //Từ đó lấy ra tấm vé để check giá và tính tiền
//             if (!selectedTicketTypes.isEmpty()) {
//                 TicketTypeInTrip minPriceTicketType = selectedTicketTypes.get(0);
//                 if (selectedTicketTypes.size() == 1) {
//                     price = minPriceTicketType.getPrice();
  
                } else {
                    return ResponseObject.builder().status(false).message("Điểm xuống không nằm trong tuyến đường này.").build();
                }

                //Lấy Số thứ tự ra so sánh
                int orderInStationOn = tripPickUp.getOrderInRoute();
                int orderInStationOff = tripDropOff.getOrderInRoute();
                if (orderInStationOff <= orderInStationOn) {
                    return ResponseObject.builder().status(false).message("Không hợp lệ điểm đón và trả").build();
                }
                // Số vé cần tạo
                List<String> seatNumbers = b.getListTicket().get(y).getSeatName();
                int sizeSeat = seatNumbers.size();
                totalSeat += sizeSeat;
                if (totalSeat > seatPerBooking) {
                    messageError = "Khách hàng chỉ có thể đặt hàng tối đa " + seatPerBooking + " chỗ ngồi";
                    throw new Exception();
                }
                obj.setTotalTicket((short) totalSeat);
                //Tính tiền , số vé / giá từ trip / khứ hồi /
                List<TicketTypeInTrip> listItem = ticketTypeInTripRepo.findByIdTrip(trip.getIdTrip());
                int soThuTuLen = tripPickUp.getOrderInRoute();
                int soThuTuXuong = tripDropOff.getOrderInRoute();
                List<TicketTypeInTrip> selectedTicketTypes = new ArrayList<>();
                for (int i = 0; i < listItem.size(); i++) {
                    TicketType ticketType = ticketTypeRepo.findById(listItem.get(i).getTicketType().getIdTicketType()).orElse(null);
                    int idStationOn = ticketType.getEarlyOnStation().getIdStation();
                    int idStationOff = ticketType.getLateOffStation().getIdStation();
                    StationInRoute stationInRouteOn = stationInRouteRepo.findByIdStationAndIdRoute(idStationOn, trip.getRoute().getIdRoute());
                    StationInRoute stationInRouteOff = stationInRouteRepo.findByIdStationAndIdRoute(idStationOff, trip.getRoute().getIdRoute());
                    if (stationInRouteOff == null || stationInRouteOn == null) {
                        return ResponseObject.builder().status(false).message("Có vấn đề ở orderInRoute.").build();
                    }
                    int orderInRouteOn = stationInRouteOn.getOrderInRoute();
                    int orderInRouteOff = stationInRouteOff.getOrderInRoute();
                    //Lấy ra được số thứ tự của stationInRoute.
                    // Kiểm tra xem số thứ tự lên và xuống có nằm trong khoảng từ soThuTuLen đến soThuTuXuong hay không
                    if (soThuTuLen >= orderInRouteOn && soThuTuXuong <= orderInRouteOff) {
                        selectedTicketTypes.add(listItem.get(i));
                    }
                }

                double price = 0;

                //Từ đó lấy ra tấm vé để check giá và tính tiền
                if (!selectedTicketTypes.isEmpty()) {
                    TicketTypeInTrip minPriceTicketType = selectedTicketTypes.get(0);
                    if (selectedTicketTypes.size() == 1) {
                        price = minPriceTicketType.getPrice();
                    } else {
                        for (int i = 1; i < selectedTicketTypes.size(); i++) {
                            TicketTypeInTrip currentTicketTypeInTrip = selectedTicketTypes.get(i);

                            if (currentTicketTypeInTrip.getPrice() < minPriceTicketType.getPrice()) {
                                minPriceTicketType = currentTicketTypeInTrip;
                            }
                            price = minPriceTicketType.getPrice();
                        }
                    }
                    totalPrice = price * sizeSeat;
                    totalAllPrice += totalPrice;
                    //Check wallet của customer còn đủ tiền không.
                    if (wallet != null) {
                        int totalPriceOfCustomer = wallet.getBalance();
                        if (totalPriceOfCustomer >= totalPrice) {
                            totalPriceOfCustomer -= totalPrice;
                            wallet.setBalance((int) totalPriceOfCustomer);
                        } else {
                            return ResponseObject.builder().status(false).message("Số dư trong ví không đủ để thanh toán").build();
                        }
                    }
                    obj.setTotalPrice(totalAllPrice);
                    bookingRepo.save(obj);
                    //Tạo ticket
                    System.out.println(seatNumbers.size());


                    for (int z = 0; z < seatNumbers.size(); z++) {
                        String seatName = seatNumbers.get(z);
                        Ticket ticket = new Ticket();
                        ticket.setBooking(obj);
                        ticket.setTrip(trip);
                        ticket.setStatus("PAID");
                        ticket.setCreateDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
                        ticket.setTicketType(minPriceTicketType.getTicketType());
                        ticket.setOnStation(stationOnTicket);
                        ticket.setOffStation(stationOffTicket);
                        ticket.setPrice(price);
                        ticket.setSeatName(seatName);
                        ticket.setStar(0);
                        String code;
                        List<Ticket> codeTmp;
                        do {
                            code = generateRandomBookingCode();
                            codeTmp = ticketRepo.findByTicketCode(code);
                        } while (!codeTmp.isEmpty());
                        ticket.setTicketCode(code);
                        ticket.setTicketCodeImg(code);
                        ticketRepo.save(ticket);
                    }

                } else {
                    messageError = "Có vấn đề ở loại vé.";
                    throw new Exception();
                }
            }
            //end list Ticket
//            ResponseObject<?> rsSendMail = null;
//            String name = trip.getRoute().getStartProvinceCity().getName() + "-" + trip.getRoute().getEndProvinceCity().getName();
//
//            rsSendMail = mailService.sendMailBookingSuccessTwo(customer.getEmail(), name, String.valueOf(totalAllPrice));
//            if(!rsSendMail.isStatus()){
////                        System.out.println("gửi mail cho guest khi booking ko thah công");
//                log.error("Gửi mail cho guest khi booking ko thành công");
//            }
            PaymentTransaction paymentTransaction = new PaymentTransaction();
            paymentTransaction.setCreateDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            paymentTransaction.setEmailGuest(null);
            paymentTransaction.setNameGuest(null);
            paymentTransaction.setPhoneGuest(null);
            paymentTransaction.setBooking(obj);
            paymentTransactionRepo.save(paymentTransaction);
            Transaction transaction = new Transaction();
            transaction.setAmount((int) -totalPrice);
            transaction.setBankCode(null);
            transaction.setDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            transaction.setDescription("Đặt vé chuyến: " + trip.getRoute().getStartProvinceCity().getName() + "->" +
                    trip.getRoute().getEndProvinceCity().getName());
            transaction.setWallet(wallet);
            transaction.setPaymentTransaction(paymentTransaction);
            transactionRepo.save(transaction);

            BookingSuccessTripSendMail bookingSuccessTripSendMail = new BookingSuccessTripSendMail();
            bookingSuccessTripSendMail.setToEmail(customer.getEmail());
            bookingSuccessTripSendMail.setNameRoute(trip.getRoute().getStartProvinceCity().getName() + " - " + trip.getRoute().getEndProvinceCity().getName());
            bookingSuccessTripSendMail.setStartTime(dateFormat.format(trip.getDepartureDate()));
            bookingSuccessTripSendMail.setEndTime(dateFormat.format(trip.getEndDate()));
            bookingSuccessTripSendMail.setTotalPrice(String.valueOf(totalAllPrice));

            return ResponseObject.builder().status(true).message("Đặt vé thành công"
            ).data(BookingResult.builder().data(bookingSuccessTripSendMail).msg("Tổng tiền là  " + totalAllPrice + "VND").build()).build();
        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseObject.builder().status(false).message(messageError).build();
        }
    }

    private boolean checkTotalTicketOfCustomer(int idCustomer) throws Exception {
        try{
            int totalTicket = ticketRepo.findAllTicketCustomerBuy(idCustomer);
            UserSystem user = userSystemRepo.findByIdCustomer(idCustomer);
            if(totalTicket > 0){
                user.setMileStone(totalTicket);
                ResponseObject rs = null;
                if(totalTicket == configSystemRepo.findMileStone1()){
                    user.setVoucherCoins(user.getVoucherCoins() + configSystemRepo.findCoinsMileStone1());
                    rs = notificationService.create(new NotificationDTOcreate(idCustomer, "Chúc mừng bạn đã đạt mốc " + configSystemRepo.findMileStone1() + " vé ! (+"+configSystemRepo.findCoinsMileStone1()+" xu khuyến mãi)"));
                }else if(totalTicket == configSystemRepo.findMileStone2()){
                    user.setVoucherCoins(user.getVoucherCoins() + configSystemRepo.findCoinsMileStone2());
                    rs = notificationService.create(new NotificationDTOcreate(idCustomer, "Chúc mừng bạn đã đạt mốc " + configSystemRepo.findMileStone2() + " vé ! (+"+configSystemRepo.findCoinsMileStone2()+" xu khuyến mãi)"));
                }else if(totalTicket == configSystemRepo.findMileStone3()){
                    user.setVoucherCoins(user.getVoucherCoins() + configSystemRepo.findCoinsMileStone3());
                    rs = notificationService.create(new NotificationDTOcreate(idCustomer, "Chúc mừng bạn đã đạt mốc " + configSystemRepo.findMileStone3() + " vé ! (+"+configSystemRepo.findCoinsMileStone3()+" xu khuyến mãi)"));
                }else if(totalTicket > configSystemRepo.findMileStone3()){
                    if(totalTicket % configSystemRepo.findCrossMileStone() == 0){ //dủ mốc vượt mức
                        user.setVoucherCoins(user.getVoucherCoins() + configSystemRepo.findCoinsCrossMileStone());
                        rs = notificationService.create(new NotificationDTOcreate(idCustomer, "Chúc mừng bạn đã đạt mốc " + totalTicket + " vé ! (+"+configSystemRepo.findCoinsCrossMileStone()+" xu khuyến mãi)"));
                    }
                }
            }
            return true;
        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public ResponseObject<?> createBookingRound(BookingDTOcreateRound b) throws Exception {
//        try {
//            ResponseObject booking1 = create(new BookingDTOcreate(b.getIdTrip(), b.getIdCustomer(), b.getCodePickUpPoint(), b.getCodeDropOffPoint(),b.getSeatName()));
//            if(!booking1.isStatus()){
//                throw new Exception(booking1.getMessage());
//            }
//            ResponseObject booking2 = create(new BookingDTOcreate(b.getIdTrip2(), b.getIdCustomer(), b.getCodePickUpPoint2(), b.getCodeDropOffPoint2(),b.getSeatName2()));
//            if(!booking2.isStatus()){
//                throw new Exception(booking2.getMessage());
//            }
//            ResponseObject rsSendMail = null;
//            BookingResult bookingResult = null;
//            //sned mail cho booking go
//            bookingResult = (BookingResult) booking1.getData();
//            for (BookingSuccessTripSendMail item: bookingResult.getData()) {
//                //sned mail to user
//                rsSendMail = mailService.sendMailBookingSuccess(item.getToEmail(), item.getListSeat(), item.getIdTrip(), item.getTripSendMail());
//                if(!rsSendMail.isStatus()){
//                    log.error("gửi mail cho customer ("+item.getToEmail()+") khi booking thất bai");
//                }
//            }
//            //sne dmail cho booking back
//            bookingResult = (BookingResult) booking2.getData();
//            for (BookingSuccessTripSendMail item: bookingResult.getData()) {
//                //sned mail to user
//                rsSendMail = mailService.sendMailBookingSuccess(item.getToEmail(), item.getListSeat(), item.getIdTrip(), item.getTripSendMail());
//                if(!rsSendMail.isStatus()){
//                    log.error("gửi mail cho customer ("+item.getToEmail()+") khi booking thất bai");
//                }
//            }
//            return ResponseObject.builder().status(true).code(200).message("Đặt vé thành công").build();
//        } catch (Exception e) {
//            throw new Exception(e.getMessage());
//        }
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject<?> createGuest(BookingDTOcreateGuest b) {
        String messageError = null;
        try {
            Booking obj = modelMapper.map(b, Booking.class);
            ConfigSystem configSystem15 = configSystemRepo.findByConfigSystemTimeCanNotBook();
            short timeCanNotBook = (short) configSystem15.getValue();//15->  15ph trc khi xe chạy thì không đc book vé của chuyến đó
            Trip trip = tripRepo.findById(b.getIdTrip()).orElse(null);
            //TEST
            System.out.println("trip check: " + trip.getIdTrip() + " - start: " + trip.getDepartureDate() + " - end: " + trip.getDepartureDate());
            //TEST
            //Chck trip nếu ko full slot ghé k dc book , check trip phãi có trạng thái ready và adminCheck là ACCEPT
            if (trip == null) {
                return ResponseObject.builder().status(false).message("Không có trip đó").build();
            } else if (!trip.getAdminCheck().equals("ACCEPTED")) {   // && !trip.getStatus().equals("READY")
                return ResponseObject.builder().status(false).message("Chuyến đi chưa được admin chấp thuận").build();
            } else if (trip.getStatus().equals("FINISHED")) {
                return ResponseObject.builder().status(false).message("Chuyến đã hoàn thành không thể đặt").build();
            } else if (trip.getStatus().equals("RUNNING")) {
                return ResponseObject.builder().status(false).message("Chuyến đang trên đường không thể đặt").build();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            //Validate 15 phut truoc khi xe chay ko dc book
            //Ngày giờ hiện tại
            Calendar timeNow = Calendar.getInstance();
            timeNow.add(Calendar.HOUR, 7);
            Date currentDate = timeNow.getTime();
            //Ngày giờ xe chạy
//            String startTimeTmp = String.valueOf(trip.getDepartureDate());
//            Date startTime = dateFormat.parse(startTimeTmp);
            //Trừ 1 tiếng trước khi xe chạy
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(trip.getDepartureDate());
            calendar.add(Calendar.MINUTE, -(timeCanNotBook));
            Date start1 = calendar.getTime();
            //TEST
            System.out.println("start 1: " + start1);
            System.out.println("current: " + currentDate);
            //TEST
            if (currentDate.after(start1)) {
                return ResponseObject.builder().status(false).message(timeCanNotBook + " phút trước khi xe khởi hành, bạn không thể đặt vé cho chuyến đi đó").build();
            }

            UserSystem customer = userSystemRepo.findByUserName("guest");

            //Booking set còn mỗi total Price;
            obj.setCreatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            obj.setCustomer(customer);
            obj.setTrip(trip);
            Station stationOnTicket;
            Station stationOffTicket;
            //Set name của station dựa vào code của station
            StationInRoute tripPickUp = stationInRouteRepo.findByRouteAndIdStation(trip.getRoute().getIdRoute(), b.getCodePickUpPoint());
            if (tripPickUp != null) {
                stationOnTicket = stationRepo.findByIdStation(tripPickUp.getStation().getIdStation());
            } else {
                return ResponseObject.builder().status(false).message("Điểm lên không nằm trong tuyến đường này.").build();
            }
            StationInRoute tripDropOff = stationInRouteRepo.findByRouteAndIdStation(trip.getRoute().getIdRoute(), b.getCodeDropOffPoint());
            if (tripDropOff != null) {
                stationOffTicket = stationRepo.findByIdStation(tripDropOff.getStation().getIdStation());
            } else {
                return ResponseObject.builder().status(false).message("Điểm xuống không nằm trong tuyến đường này.").build();
            }

            //Lấy Số thứ tự ra so sánh
            int orderInStationOn = tripPickUp.getOrderInRoute();
            int orderInStationOff = tripDropOff.getOrderInRoute();
            if (orderInStationOff <= orderInStationOn) {
                return ResponseObject.builder().status(false).message("Không hợp lệ điểm đón và trả").build();
            }
            // Số vé cần tạo
            List<String> seatNumbers = b.getSeatName();
            int sizeSeat = seatNumbers.size();
            ConfigSystem configSystem16 = configSystemRepo.findBySeatPerBooking();
            int seatPerBooking = configSystem16.getValue();

            if (sizeSeat > seatPerBooking) {
                return ResponseObject.builder().status(false).message("Khách hàng chỉ có thể đặt hàng tối đa " + seatPerBooking + " chỗ ngồi").build();
            }
            obj.setTotalTicket((short) sizeSeat);
            //Tính tiền , số vé / giá từ trip / khứ hồi /
            List<TicketTypeInTrip> listItem = ticketTypeInTripRepo.findByIdTrip(trip.getIdTrip());
            int soThuTuLen = tripPickUp.getOrderInRoute();
            int soThuTuXuong = tripDropOff.getOrderInRoute();
            List<TicketTypeInTrip> selectedTicketTypes = new ArrayList<>();
            for (int i = 0; i < listItem.size(); i++) {
                TicketType ticketType = ticketTypeRepo.findById(listItem.get(i).getTicketType().getIdTicketType()).orElse(null);
                int idStationOn = ticketType.getEarlyOnStation().getIdStation();
                int idStationOff = ticketType.getLateOffStation().getIdStation();
                StationInRoute stationInRouteOn = stationInRouteRepo.findByIdStationAndIdRoute(idStationOn, trip.getRoute().getIdRoute());
                StationInRoute stationInRouteOff = stationInRouteRepo.findByIdStationAndIdRoute(idStationOff, trip.getRoute().getIdRoute());
                if (stationInRouteOff == null || stationInRouteOn == null) {
                    return ResponseObject.builder().status(false).message("Có vấn đề ở orderInRoute.").build();
                }
                int orderInRouteOn = stationInRouteOn.getOrderInRoute();
                int orderInRouteOff = stationInRouteOff.getOrderInRoute();
                //Lấy ra được số thứ tự của stationInRoute.
                // Kiểm tra xem số thứ tự lên và xuống có nằm trong khoảng từ soThuTuLen đến soThuTuXuong hay không
                if (soThuTuLen >= orderInRouteOn && soThuTuXuong <= orderInRouteOff) {
                    selectedTicketTypes.add(listItem.get(i));
                }
            }
            double totalPrice = 0;
            double price = 0;
            //Từ đó lấy ra tấm vé để check giá và tính tiền
            if (!selectedTicketTypes.isEmpty()) {
                TicketTypeInTrip minPriceTicketType = selectedTicketTypes.get(0);
                if (selectedTicketTypes.size() == 1) {
                    price = minPriceTicketType.getPrice();
                } else {
                    for (int i = 1; i < selectedTicketTypes.size(); i++) {
                        TicketTypeInTrip currentTicketTypeInTrip = selectedTicketTypes.get(i);

                        if (currentTicketTypeInTrip.getPrice() < minPriceTicketType.getPrice()) {
                            minPriceTicketType = currentTicketTypeInTrip;
                        }
                        price = minPriceTicketType.getPrice();
                    }
                }
                totalPrice = price * sizeSeat;

                obj.setTotalPrice(totalPrice);
                bookingRepo.save(obj);
                //Tạo ticket
                System.out.println(seatNumbers.size());
                TripSendMail tripSendMail = null;
                ResponseObject<?> rsSendMail = null;
                for (int z = 0; z < seatNumbers.size(); z++) {
                    String seatName = seatNumbers.get(z);
                    Ticket ticket = new Ticket();
                    ticket.setBooking(obj);
                    ticket.setTrip(trip);
                    ticket.setStatus("PAID");
                    ticket.setCreateDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
                    ticket.setTicketType(minPriceTicketType.getTicketType());
                    ticket.setOnStation(stationOnTicket);
                    ticket.setOffStation(stationOffTicket);
                    ticket.setPrice(price);
                    ticket.setSeatName(seatName);
                    ticket.setStar(0);
                    String code;
                    List<Ticket> codeTmp;
                    do {
                        code = generateRandomBookingCode();
                        codeTmp = ticketRepo.findByTicketCode(code);
                    } while (!codeTmp.isEmpty());
                    ticket.setTicketCode(code);
                    ticket.setTicketCodeImg(code);
                    ticketRepo.save(ticket);

                    //gửi mail cho guest
                    tripSendMail = new TripSendMail();
                    tripSendMail.setTicketCode(code);
                    tripSendMail.setDestination(trip.getRoute().getEndProvinceCity().getName());
                    tripSendMail.setDeparturePoint(trip.getRoute().getStartProvinceCity().getName());
                    tripSendMail.setStationStartAddress(tripPickUp.getStation().getName() + " (" + tripPickUp.getStation().getAddress() + ")");
                    tripSendMail.setStationEndAddress(tripDropOff.getStation().getName() + " (" + tripDropOff.getStation().getAddress() + ")");
                    tripSendMail.setStartTime(trip.getDepartureDate().toString());
                    tripSendMail.setEndTime(trip.getEndDate().toString());
                    tripSendMail.setBusLicensePlates(trip.getVehicle().getLicensePlates());
                    tripSendMail.setStationTimeComeStart(stationTimeComeRepo.findTimeComeByIdStationAndIdTrip(trip.getRoute().getIdRoute(), b.getCodePickUpPoint(), trip.getIdTrip()).toString());
                    tripSendMail.setStationTimeComeEnd(stationTimeComeRepo.findTimeComeByIdStationAndIdTrip(trip.getRoute().getIdRoute(), b.getCodeDropOffPoint(), trip.getIdTrip()).toString());
                    rsSendMail = mailService.sendMailBookingSuccess(b.getEmailGuest(), seatName, b.getIdTrip(), tripSendMail);
                    if(!rsSendMail.isStatus()){
//                        System.out.println("gửi mail cho guest khi booking ko thah công");
                        log.error("gửi mail cho guest khi booking ko thah công");
                    }
                }
                PaymentTransaction paymentTransaction = new PaymentTransaction();
                paymentTransaction.setCreateDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
                paymentTransaction.setEmailGuest(b.getEmailGuest());
                paymentTransaction.setNameGuest(b.getNameGuest());
                paymentTransaction.setPhoneGuest(b.getPhoneGuest());
                paymentTransaction.setBooking(obj);
                paymentTransactionRepo.save(paymentTransaction);


            } else {
                messageError = "Có vấn đề ở loại vé.";
                throw new Exception();
            }
            return ResponseObject.builder().status(true).message("Đặt vé thành công"
            ).data("Tổng tiền là  " + totalPrice + "VND - Tổng ghế đặt là " + seatNumbers.size()).build();
        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseObject.builder().status(false).message(messageError + ". " + ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> getTicketTypeForCreate(Integer idTrip, Integer codePickUpPoint, Integer codeDropOffPoint) {
        try {
            Trip trip = tripRepo.findById(idTrip).orElse(null);
            if (trip == null) {
                return ResponseObject.builder().status(false).message("Không tìm thấy chuyến.").build();
            }
            Station stationOnTicket;
            Station stationOffTicket;
            //Set name của station dựa vào code của station
            StationInRoute tripPickUp = stationInRouteRepo.findByRouteAndIdStation(trip.getRoute().getIdRoute(), codePickUpPoint);
            if (tripPickUp != null) {
                stationOnTicket = stationRepo.findByIdStation(tripPickUp.getStation().getIdStation());
            } else {
                return ResponseObject.builder().status(false).message("Điểm lên không nằm trong tuyến đường này.").build();
            }
            StationInRoute tripDropOff = stationInRouteRepo.findByRouteAndIdStation(trip.getRoute().getIdRoute(),codeDropOffPoint);
            if (tripDropOff != null) {
                stationOffTicket = stationRepo.findByIdStation(tripDropOff.getStation().getIdStation());
            } else {
                return ResponseObject.builder().status(false).message("Điểm xuống không nằm trong tuyến đường này.").build();
            }
            //Lấy Số thứ tự ra so sánh
            int orderInStationOn = tripPickUp.getOrderInRoute();
            int orderInStationOff = tripDropOff.getOrderInRoute();
            if (orderInStationOff <= orderInStationOn) {
                return ResponseObject.builder().status(false).message("Không hợp lệ điểm đón và trả").build();
            }

            List<TicketTypeInTrip> listItem = ticketTypeInTripRepo.findByIdTrip(trip.getIdTrip());
            int soThuTuLen = tripPickUp.getOrderInRoute();
            int soThuTuXuong = tripDropOff.getOrderInRoute();
            List<TicketTypeInTrip> selectedTicketTypes = new ArrayList<>();
            for (int i = 0; i < listItem.size(); i++) {
                TicketType ticketType = ticketTypeRepo.findById(listItem.get(i).getTicketType().getIdTicketType()).orElse(null);
                int idStationOn = ticketType.getEarlyOnStation().getIdStation();
                int idStationOff = ticketType.getLateOffStation().getIdStation();
                StationInRoute stationInRouteOn = stationInRouteRepo.findByIdStationAndIdRoute(idStationOn, trip.getRoute().getIdRoute());
                StationInRoute stationInRouteOff = stationInRouteRepo.findByIdStationAndIdRoute(idStationOff, trip.getRoute().getIdRoute());
                if (stationInRouteOff == null || stationInRouteOn == null) {
                    return ResponseObject.builder().status(false).message("Có vấn đề ở orderInRoute.").build();
                }
                int orderInRouteOn = stationInRouteOn.getOrderInRoute();
                int orderInRouteOff = stationInRouteOff.getOrderInRoute();
                //Lấy ra được số thứ tự của stationInRoute.
                // Kiểm tra xem số thứ tự lên và xuống có nằm trong khoảng từ soThuTuLen đến soThuTuXuong hay không
                if (soThuTuLen >= orderInRouteOn && soThuTuXuong <= orderInRouteOff) {
                    selectedTicketTypes.add(listItem.get(i));
                }
                System.out.println("123");

            }
            double price = 0;
            TicketTypeInTrip minPriceTicketType = selectedTicketTypes.get(0);
            if (selectedTicketTypes.size() == 1) {
                price = minPriceTicketType.getPrice();
                System.out.println("1");
            } else {
                System.out.println("12");
                for (int z = 1; z < selectedTicketTypes.size(); z++) {
                    TicketTypeInTrip currentTicketTypeInTrip = selectedTicketTypes.get(z    );

                    if (currentTicketTypeInTrip.getPrice() < minPriceTicketType.getPrice()) {
                        minPriceTicketType = currentTicketTypeInTrip;
                    }
                    price = minPriceTicketType.getPrice();
                }
            }
            BookingDTOviewTicketTypeOfTrip booking = new BookingDTOviewTicketTypeOfTrip();
            booking.setIdTrip(trip.getIdTrip());
            booking.setCodePickUpPoint(codePickUpPoint);
            booking.setCodeDropOffPoint(codeDropOffPoint);
            booking.setPricePerSeat(price);
            TicketTypeDTOInBooking ticketTypeTMP = new TicketTypeDTOInBooking();
            ticketTypeTMP.setIdTicketType(minPriceTicketType.getTicketType().getIdTicketType());
            ticketTypeTMP.setIdEarlyOnStation(minPriceTicketType.getTicketType().getEarlyOnStation().getIdStation());
            ticketTypeTMP.setIdLateOffStation(minPriceTicketType.getTicketType().getLateOffStation().getIdStation());
            ticketTypeTMP.setIdRoute(minPriceTicketType.getTicketType().getRoute().getIdRoute());
            booking.setTicketType(ticketTypeTMP);
            return ResponseObject.builder().status(true).message("Vé phù hợp cho khách hàng trong chuyến đi là " + price + ".").data(booking).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra.").build();
        }
    }

}
