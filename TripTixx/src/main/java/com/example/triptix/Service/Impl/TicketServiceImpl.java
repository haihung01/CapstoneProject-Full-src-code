package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Station.StationDTOview;
import com.example.triptix.DTO.Station.StationDTOviewInTrip;
import com.example.triptix.DTO.Ticket.*;
import com.example.triptix.DTO.Vehicle.VehicleDTOview;
import com.example.triptix.Enum.StatusTrip;
import com.example.triptix.Enum.TicketStatus;
import com.example.triptix.Model.*;
import com.example.triptix.Repository.*;
import com.example.triptix.Service.TicketService;
import com.example.triptix.Util.UTCTimeZoneUtil;
import org.checkerframework.checker.index.qual.SearchIndexBottom;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private TripRepo tripRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private UserSystemRepo userSystemRepo;

    @Autowired
    private ConfigSystemRepo configSystemRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private PaymentTransactionRepo paymentTransactionRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private StationRepo stationRepo;

    @Autowired
    private VehicleRepo vehicleRepo;



    @Autowired
    private WalletRepo walletRepo;
    @Override
    public ResponseObject<?> getAll(Integer idTrip, Integer idBooking, String status, Integer idCustomer,  int pageSize, int pageIndex) {
        try{
            int totalPage = 0;
            //paging
            Pageable pageable = null;
            if (pageSize != 0 && pageIndex != 0) {
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
            } else {
                pageIndex = 1;
            }
            List<Ticket> listTicket = new ArrayList<>();
            Page<Ticket> listTicketPage = null;
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            // Lọc dựa trên idTrip, idBooking, status và idCustomer
            if (idTrip != null && idBooking != null && status != null && idCustomer != null && !status.isEmpty()) {
                listTicketPage = ticketRepo.findByTrip_IdTripAndBooking_IdBookingAndStatusAndBooking_Customer_IdCustomer(idTrip, idBooking, status, idCustomer, pageable);
            } else if (idTrip != null && idBooking != null && idCustomer != null) {
                listTicketPage = ticketRepo.findByTrip_IdTripAndBooking_IdBookingAndBooking_Customer_IdCustomer(idTrip, idBooking, idCustomer, pageable);
            } else if (idTrip != null && idBooking != null) {
                listTicketPage = ticketRepo.findByTrip_IdTripAndBooking_IdBooking(idTrip, idBooking, pageable);
            } else if (idTrip != null && status != null && idCustomer != null && !status.isEmpty()) {
                listTicketPage = ticketRepo.findByTrip_IdTripAndStatusAndBooking_Customer_IdCustomer(idTrip, status, idCustomer, pageable);
            } else if (idBooking != null && status != null && idCustomer != null && !status.isEmpty()) {
                listTicketPage = ticketRepo.findByBooking_IdBookingAndStatusAndBooking_Customer_IdCustomer(idBooking, status, idCustomer, pageable);
            } else if (idTrip != null && idCustomer != null) {
                listTicketPage = ticketRepo.findByTrip_IdTripAndBooking_Customer_IdCustomer(idTrip, idCustomer, pageable);
            } else if (idBooking != null && idCustomer != null) {
                listTicketPage = ticketRepo.findByBooking_IdBookingAndBooking_Customer_IdCustomer(idBooking, idCustomer, pageable);
            } else if (status != null && idCustomer != null && !status.isEmpty()) {
                listTicketPage = ticketRepo.findByStatusAndBooking_Customer_IdCustomer(status, idCustomer, pageable);
            } else if (idTrip != null) {
                listTicketPage = ticketRepo.findByTrip_IdTrip(idTrip, pageable);
            } else if (idBooking != null) {
                listTicketPage = ticketRepo.findByBooking_IdBooking(idBooking, pageable);
            } else if (idCustomer != null) {
                listTicketPage = ticketRepo.findByBooking_Customer_IdCustomer(idCustomer, pageable);
            } else if (status != null && !status.isEmpty()) {
                listTicketPage = ticketRepo.findByStatus(status, pageable);
            } else {
                // Nếu không có bất kỳ điều kiện lọc nào, lấy tất cả
                listTicketPage = ticketRepo.findAllOrderByDepartureDate(pageable);
            }
            if(listTicketPage != null){
                totalPage = listTicketPage.getTotalPages();
                listTicket = listTicketPage.getContent();
            }
            List<TicketDTOview> listDTO = new ArrayList<>();
            for(Ticket item : listTicket){
                TicketDTOview dto = modelMapper.map(item, TicketDTOview.class);
                dto.setIdTicket(item.getIdTicket());
                dto.setIdTicketType(item.getTicketType().getIdTicketType());
                dto.setIdBooking(item.getBooking().getIdBooking());
                dto.setIdOnStation(item.getOnStation().getIdStation());
                Station onStaion = stationRepo.findByIdStation(item.getOnStation().getIdStation());
                StationDTOview onStationView = modelMapper.map(onStaion, StationDTOview.class);
                dto.setInOffStation(item.getOffStation().getIdStation());
                Station offStation = stationRepo.findByIdStation(item.getOffStation().getIdStation());
                StationDTOview offStationView = modelMapper.map(offStation, StationDTOview.class);
                dto.setOnStation(onStationView);
                dto.setOffStation(offStationView);
                dto.setIdTrip(item.getTrip().getIdTrip());
                //set Trip
                Trip trip = tripRepo.findById(item.getTrip().getIdTrip()).orElse(null);
                TripDTOviewInTicket dtoTrip = modelMapper.map(trip, TripDTOviewInTicket.class);
                dtoTrip.setIdTrip(trip.getIdTrip());
                dtoTrip.setIdRoute(trip.getRoute().getIdRoute());
                dtoTrip.setNameRoute(trip.getRoute().getName());
                dtoTrip.setIdDriver(trip.getDriver().getIdUserSystem());
                dtoTrip.setIdStaff(trip.getDriver().getIdUserSystem());
                dtoTrip.setIdVehicle(trip.getVehicle().getIdBus());
                //parse
                String departureDate = sdf1.format(trip.getDepartureDate());
                String endDate = sdf1.format(trip.getEndDate());
                String createdDate = sdf1.format(item.getCreateDate());
                Date createDateTMP = sdf1.parse(createdDate);
                Date startDate = sdf1.parse(departureDate);
                Date offDate = sdf1.parse(endDate);
                dto.setCreatedDate(createDateTMP.getTime() / 1000);
                //end parse
                dtoTrip.setDepartureDate(startDate.getTime() / 1000 );
                dtoTrip.setEndDate(offDate.getTime() / 1000);
                dtoTrip.setAvarageStar(trip.getAvarageStar());
                dtoTrip.setAdminCheck(trip.getAdminCheck());
                dtoTrip.setStatus(trip.getStatus());
                dto.setTrip(dtoTrip);
                Vehicle vehicle = vehicleRepo.findById(trip.getVehicle().getIdBus()).orElse(null);
                UserSystem userSystem = userSystemRepo.findById(trip.getDriver().getIdUserSystem()).orElse(null);
                VehicleDTOview vehicleDTOview = modelMapper.map(vehicle, VehicleDTOview.class);
                dtoTrip.setVehicleDTO(vehicleDTOview);
                UserSystemDTOInTicket driverDTO = modelMapper.map(userSystem, UserSystemDTOInTicket.class);
                dtoTrip.setDriverDTO(driverDTO);
                listDTO.add(dto);
            }
            return ResponseObject.builder().status(true)
                    .message("Lấy thông tin các tấm vé thành công.").data(listDTO)
                    .totalPage(totalPage).pageSize(pageSize).pageIndex(pageIndex)
                    .build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra. " + ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> getDetail(int id) {
        try{
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Ticket ticket  = ticketRepo.findById(id).orElse(null);
            TicketDTOview dto  = modelMapper.map(ticket,TicketDTOview.class);
            dto.setIdTicket(ticket.getIdTicket());
            dto.setIdTicketType(ticket.getTicketType().getIdTicketType());
            dto.setIdBooking(ticket.getBooking().getIdBooking());
            dto.setIdOnStation(ticket.getOnStation().getIdStation());
            dto.setInOffStation(ticket.getOffStation().getIdStation());
            Station onStation = stationRepo.findByIdStation(ticket.getOnStation().getIdStation());
            Station offStation  = stationRepo.findByIdStation(ticket.getOffStation().getIdStation());
            StationDTOview onStationView = modelMapper.map(onStation, StationDTOview.class);
            StationDTOview offStationView = modelMapper.map(offStation, StationDTOview.class);
            dto.setOnStation(onStationView);
            dto.setOffStation(offStationView);
            Trip trip = tripRepo.findById(ticket.getTrip().getIdTrip()).orElse(null);
            TripDTOviewInTicket dtoTrip = modelMapper.map(trip, TripDTOviewInTicket.class);
            dtoTrip.setIdTrip(trip.getIdTrip());
            dtoTrip.setIdRoute(trip.getRoute().getIdRoute());
            dtoTrip.setNameRoute(trip.getRoute().getName());
            dtoTrip.setIdDriver(trip.getDriver().getIdUserSystem());
            dtoTrip.setIdStaff(trip.getDriver().getIdUserSystem());
            dtoTrip.setIdVehicle(trip.getVehicle().getIdBus());
            String createdDate = sdf1.format(ticket.getCreateDate());
            String departureDate = sdf1.format(trip.getDepartureDate());
            String endDate = sdf1.format(trip.getEndDate());
            Date createdDateTMP = sdf1.parse(createdDate);
            Date startDate = sdf1.parse(departureDate);
            Date offDate = sdf1.parse(endDate);
            dto.setCreatedDate(createdDateTMP.getTime() / 1000);
            dtoTrip.setDepartureDate(startDate.getTime() / 1000);
            dtoTrip.setEndDate(offDate.getTime() / 1000);
            dtoTrip.setAvarageStar(trip.getAvarageStar());
            dtoTrip.setAdminCheck(trip.getAdminCheck());
            dtoTrip.setStatus(trip.getStatus());
            dto.setTrip(dtoTrip);
            dto.setIdTrip(ticket.getTrip().getIdTrip());
            Vehicle vehicle = vehicleRepo.findById(trip.getVehicle().getIdBus()).orElse(null);
            UserSystem userSystem = userSystemRepo.findById(trip.getDriver().getIdUserSystem()).orElse(null);
            VehicleDTOview vehicleDTOview = modelMapper.map(vehicle, VehicleDTOview.class);
            dtoTrip.setVehicleDTO(vehicleDTOview);
            UserSystemDTOInTicket driverDTO = modelMapper.map(userSystem, UserSystemDTOInTicket.class);
            dtoTrip.setDriverDTO(driverDTO);
            return ResponseObject.builder().status(true).message("Lấy thông tin các tấm vé thành công.").data(dto).build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra.").build();
        }
    }

    @Override
    public ResponseObject<?> updateVoteStar(TicketDTOVoteStartupdate b) {
        try {
            Ticket ticket = ticketRepo.findById(b.getIdTicket()).orElse(null);
            if (ticket == null) {
                return ResponseObject.builder().status(false).message("Không tìm thấy id ticket.").build();
            } else if (ticket.getStar() > 0) {
                float startOld = ticket.getStar(); //4
                ticket.setStar(b.getStar());
                int idTrip = ticket.getTrip().getIdTrip();
                Trip  trip = tripRepo.findById(idTrip).orElse(null);
                float start = b.getStar(); //5
                float averageStar = trip.getAvarageStar(); //4.5
                int countTicket = ticketRepo.findByIdTripAndCountTicket(idTrip);
                if(countTicket == 1){
                    averageStar = start;
                }else {
                    averageStar = (averageStar * 2 ) - startOld;
                    averageStar = (averageStar + start ) / 2;

                }
                trip.setAvarageStar(averageStar);
                ticket.setCreateDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
                ticketRepo.save(ticket);
                return ResponseObject.builder().status(true).message("Cảm ơn bạn đã đánh giá lại.").build();
            } else if (!ticket.getStatus().equals("FINISHED")) {
                return ResponseObject.builder().status(false).message("Bạn không thể đánh giá vì chuyến đi chưa hoàn thành").build();
            } else if(ticket.getStatus().equals("NO_SHOW")){
                return ResponseObject.builder().status(false).message("Bạn không thể đánh giá vì đã lỡ chuyến").build();
            }
            ticket.setStar(b.getStar());
            //Set start cho trip.
            int idTrip = ticket.getTrip().getIdTrip();
            Trip trip = tripRepo.findById(idTrip).orElse(null);
            float starTmp = b.getStar();
            float averageStar = trip.getAvarageStar();
            if (averageStar == 0) {
                averageStar = starTmp;
            } else {
                averageStar = (averageStar + starTmp) / 2;
            }
            trip.setAvarageStar(averageStar);
            ticket.setCreateDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            ticketRepo.save(ticket);
            return ResponseObject.builder().status(true).message("Cảm ơn bạn đã đánh giá.").build();

        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra.").build();
        }
    }

    @Override
    public ResponseObject<?> cancelTicket(TicketDTOCancelupdate b) {
        try{
            Ticket check = ticketRepo.findById(b.getIdTicket()).orElse(null);
            if(check == null){
                return ResponseObject.builder().status(false).message("Không có ticket này.").build();
            }else if(check.getStatus().equals(TicketStatus.CANCELED.name())){
                return ResponseObject.builder().status(false).message("Không thể hủy vé vì chuyến đi hủy.").build();
            } else if (!check.getStatus().equals(TicketStatus.PAID.name())) {
                return ResponseObject.builder().status(false).message("Không thể hủy vé.").build();
            }
            UserSystem customer = userSystemRepo.findById(check.getBooking().getCustomer().getIdUserSystem()).orElse(null);
            Wallet wallet = walletRepo.findByIdCustomer(customer.getIdUserSystem());
            //set tiền lại
            ConfigSystem configSystem13 = configSystemRepo.findByTimeRefund();
            ConfigSystem configSystem14 = configSystemRepo.findByTimeCanNotRefund();
            ConfigSystem configSystem17 = configSystemRepo.findByPerCancelTicketBeforeTimeCannotRefund();
            ConfigSystem configSystem18 = configSystemRepo.findByPerCancelTicket();
            int idTripTmp = check.getTrip().getIdTrip();
            Trip trip = tripRepo.findById(idTripTmp).orElse(null);
            short timeCanNotRefund = (short) configSystem14.getValue(); //1 -> Trước 1 tiếng xe chạy thì không hủy vé được
            short timeRefund = (short) configSystem13.getValue();  //24 -> (24h thì dc hoàn 80-85%)
            short perCancelTicketBeforeTimeCannotRefund = (short) configSystem17.getValue();//85% -> Hủy vé trước 1 ngày (túc: vé chạy T6, T5 hủy thì hoa 85%) thì hoàn 80-85%
            short perCancelTicket = (short) configSystem18.getValue(); //95%

            //Ngày giờ hiện tại
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            System.out.println("1");
            //Ngày giờ xe chạy
//            String startTimeTmp = String.valueOf(trip.getDepartureDate());
//            Date startTime = dateFormat.parse(startTimeTmp);
            //Trừ 1 tiếng trước khi xe chạy
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(trip.getDepartureDate());
            calendar.add(Calendar.HOUR, -(timeCanNotRefund));
            Date start1 = calendar.getTime();
            if (currentDate.after(start1)) {
                return ResponseObject.builder().status(false).message("Vé không thể bị hủy trước " + timeCanNotRefund + " tiếng trước giờ khởi hành").build();
            }
            //lấy thời gian hiện tại so sánh với thời gian chạy của xe.
            //hoàn 95%.
            //trước 1 ngày thì hoàn 85%.
            //trước 30 phút thời gian xe chạy thì không hoàn vé được.
            //Xóa ticket đi
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(trip.getDepartureDate());
            calendar2.add(Calendar.HOUR, -(timeRefund));
            Date start2 = calendar2.getTime();
            double price = check.getPrice();
            int balance = wallet.getBalance();
            double totalPrice  = 0;
            PaymentTransaction paymentTransaction = new PaymentTransaction();
            if(currentDate.after(start2)){
                totalPrice = price * perCancelTicketBeforeTimeCannotRefund / 100;
                balance += totalPrice;
                wallet.setBalance(balance);
                Transaction transaction = new Transaction();
                transaction.setAmount((int)totalPrice);
                transaction.setBankCode(null);
                transaction.setDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
                transaction.setDescription("Hoàn tiền vì hủy chuyến: " + trip.getRoute().getStartProvinceCity().getName() + "->" +
                        trip.getRoute().getEndProvinceCity().getName());
                transaction.setWallet(wallet);
                transaction.setPaymentTransaction(null);
                transactionRepo.save(transaction);
            } else if (currentDate.before(start2) || currentDate.equals(start2)) {
                totalPrice = price * perCancelTicket / 100;
                balance += totalPrice;
                wallet.setBalance(balance);
                Transaction transaction = new Transaction();
                transaction.setAmount((int)totalPrice);
                transaction.setBankCode(null);
                transaction.setDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
                transaction.setDescription("Hoàn tiền vì hủy chuyến: " + trip.getRoute().getStartProvinceCity().getName() + "->" +
                        trip.getRoute().getEndProvinceCity().getName());
                transaction.setWallet(wallet);
                transaction.setPaymentTransaction(null);
                transactionRepo.save(transaction);
            }
            //Set trạng thái lại
            check.setStatus(TicketStatus.CANCELED.name());
            check.setCreateDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            ticketRepo.save(check);
            return ResponseObject.builder().status(true).data("Hoàn tiền thành công.").message("Số tiền hoàn trả vào ví là " + totalPrice + "VND.").build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra.").build();
        }
    }

    @Override
    public ResponseObject<?> getRevenueNgay() {
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        int yearValue = today.get(Calendar.YEAR);
        int monthValue = today.get(Calendar.MONTH) + 1; // Note: Calendar.MONTH is zero-based
        int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

            // Calculate revenue for today
            Date dateToQueryToday = today.getTime();
            TicketDTOCompareRevenue todayRevenue = calculateRevenueForDate(dateToQueryToday, sdf);

            // Calculate revenue for yesterday
            Date dateToQueryYesterday = yesterday.getTime();
            TicketDTOCompareRevenue yesterdayRevenue = calculateRevenueForDate(dateToQueryYesterday, sdf);
            // Compare the two values
            String comparisonResult;
            if (todayRevenue.getTotalAmountPriceToday() > yesterdayRevenue.getTotalAmountPriceToday()) {
                comparisonResult = "increased";
            } else if (todayRevenue.getTotalAmountPriceToday() < yesterdayRevenue.getNumberOfTicketToday()) {
                comparisonResult = "decreased";
            } else {
                comparisonResult = "remained the same";
            }

            // Create a combined DTO
            TicketDTOCompareRevenue combinedDTO = new TicketDTOCompareRevenue();
            combinedDTO.setNumberOfTicketToday(todayRevenue.getNumberOfTicketToday());
            combinedDTO.setTotalAmountPriceToday(todayRevenue.getTotalAmountPriceToday());
            combinedDTO.setNumberOfTicketYesterday(yesterdayRevenue.getNumberOfTicketToday());
            combinedDTO.setTotalAmountPriceYesterday(yesterdayRevenue.getTotalAmountPriceToday());
            combinedDTO.setComparisonResult(comparisonResult);

            return ResponseObject.builder().status(true)
                    .message("Doanh thu ngày hôm nay và ngày hôm qua")
                    .data(combinedDTO)
                    .build();

        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra").data(e.getMessage()).build();
        }
    }

    private TicketDTOCompareRevenue calculateRevenueForDate(Date dateToQuery, SimpleDateFormat sdf) throws ParseException {
        List<Ticket> listTicket = ticketRepo.findAllByBookingStatusFinish();

        double totalAmountPrice = 0;
        int numberOfTicket = 0;

        for (Ticket  b : listTicket) {
            TicketDTORevenue dto = modelMapper.map(b, TicketDTORevenue.class);
            int idTripTmp = b.getTrip().getIdTrip();
            Trip trip = tripRepo.findById(idTripTmp).orElse(null)  ;
            Date startTime = sdf.parse(String.valueOf(trip.getDepartureDate()));

            // Check if the booking is for the specified date
            if (startTime.getYear() == dateToQuery.getYear() &&
                    startTime.getMonth() == dateToQuery.getMonth() &&
                    startTime.getDate() == dateToQuery.getDate()) {
                dto.setIdTrip(idTripTmp);
                double price = b.getPrice();
                totalAmountPrice += price;
                numberOfTicket++;
                dto.setDate(String.valueOf(trip.getDepartureDate()));
            }
        }

        TicketDTOCompareRevenue comparRevenue = new TicketDTOCompareRevenue();
        comparRevenue.setNumberOfTicketToday(numberOfTicket);
        comparRevenue.setTotalAmountPriceToday(totalAmountPrice);
        comparRevenue.setComparisonResult("N/A"); // Default value

        return comparRevenue;
    }
    private int getQuarterOrder(TicketDTOTotalRevenue quarter) {
        // Hàm này trả về số thứ tự của quý
        // Có thể thay đổi logic tùy thuộc vào cách bạn đặt tên quý và thứ tự của chúng
        switch (quarter.getName()) {
            case "Quý 1":
                return 1;
            case "Quý 2":
                return 2;
            case "Quý 3":
                return 3;
            case "Quý 4":
                return 4;
            default:
                return 0; // Trả về 0 cho trường hợp không xác định
        }
    }

    @Override
    public ResponseObject<?> getRevenueChartQuy(int year) {
        List<TicketDTOTotalRevenue> revenueList = (List<TicketDTOTotalRevenue>) getRevenueChart(year).getData();
        Map<String, TicketDTOTotalRevenue> quyRevenueMap = new HashMap<>();

        for (TicketDTOTotalRevenue b : revenueList) {
            String quarterName = b.getName();
            if (quyRevenueMap.containsKey(quarterName)) {
                // Nếu quý đã tồn tại trong Map, cộng thêm doanh thu và số lượng vé
                TicketDTOTotalRevenue existingQuarter = quyRevenueMap.get(quarterName);
                existingQuarter.setTotalPrice(existingQuarter.getTotalPrice() + b.getTotalPrice());
                existingQuarter.setTotalTicket(existingQuarter.getTotalTicket() + b.getTotalTicket());
            } else {
                // Nếu quý chưa tồn tại trong Map, thêm mới
                TicketDTOTotalRevenue newQuarter = new TicketDTOTotalRevenue();
                newQuarter.setName(quarterName);
                newQuarter.setTotalPrice(b.getTotalPrice());
                newQuarter.setTotalTicket(b.getTotalTicket());
                quyRevenueMap.put(quarterName, newQuarter);
            }
        }

        // Sắp xếp danh sách theo thứ tự của quý
        List<TicketDTOTotalRevenue> revenueListQuy = new ArrayList<>(quyRevenueMap.values());
        Collections.sort(revenueListQuy, Comparator.comparing(this::getQuarterOrder));

        return ResponseObject.builder().status(true).message("Doanh thu của năm " + year)
                .data(revenueListQuy).build();
    }

    @Override
    public ResponseObject<?> getRevenueChart(int year) {
        List<TicketDTOTotalRevenue> listRevenue = new ArrayList<>();
        try {
            for (int monthValue = 1; monthValue <= 12; monthValue++) {
                String thang = null;
                int iTMP = 0;
                if(monthValue == 1){
                    thang = "01";
                }else if(monthValue == 2){
                    thang = "02";
                }else if(monthValue == 3){
                    thang = "03";
                }else if(monthValue == 4){
                    thang = "04";
                }else if(monthValue == 5){
                    thang = "05";
                }else if(monthValue == 6){
                    thang = "06";
                }else if(monthValue == 7){
                    thang = "07";
                }else if(monthValue == 8){
                    thang = "08";
                }else if(monthValue == 9){
                    thang = "10";
                }else if(monthValue == 11){
                    thang = "11";
                }else if(monthValue == 12){
                    thang = "12";
                }
                String queryTime = year + "-" + thang + "-%";
                List<Ticket> tickets = ticketRepo.findAllByTicketStatusFinishAndTimeRange(queryTime);
                double totalAmountPrice = 0;
                int numberOfTicket = 0;
                String quarterOfYear;
                if (monthValue >= 1 && monthValue <= 3) {
                    quarterOfYear = "Quý 1";
                } else if (monthValue >= 4 && monthValue <= 6) {
                    quarterOfYear = "Quý 2";
                } else if (monthValue >= 7 && monthValue <= 9) {
                    quarterOfYear = "Quý 3";
                } else if (monthValue >= 10 && monthValue <= 12) {
                    quarterOfYear = "Quý 4";
                } else {
                    return ResponseObject.builder().status(false).message("Có lỗi xảy ra").data(null).build();
                }
                for (Ticket b : tickets) {
                    double price = b.getPrice();
                    totalAmountPrice += price;
                     iTMP++;
                }
                numberOfTicket += iTMP;
                TicketDTOTotalRevenue revenue = new TicketDTOTotalRevenue();
                revenue.setDate("Tháng " + monthValue);
                revenue.setName(quarterOfYear);
                revenue.setTotalTicket(numberOfTicket);
                revenue.setTotalPrice(totalAmountPrice);
                listRevenue.add(revenue);
            }

            return ResponseObject.builder().status(true).message("Doanh thu của năm " + year)
                    .data(listRevenue).build();
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra").data(e.getMessage()).build();
        }

    }

    @Override
    public ResponseObject<?> getListOfPotentialCustomers() {
        try {
            List<TicketDTOListPotentialCustomer> listCustomers = new ArrayList<>();
            List<Object[]> listBooking = ticketRepo.getTop10CustomersByTotalSpent();
            int i = 1;
            for (Object[] b : listBooking) {
                Integer idUserSystem = (Integer) b[0];
                UserSystem userSystem = userSystemRepo.findByIdCustomerNOTROLE(idUserSystem);
                TicketDTOListPotentialCustomer dto = new TicketDTOListPotentialCustomer();
                dto.setTop(i++);
                dto.setNameCustomer(userSystem.getFullName());
                dto.setEmail(userSystem.getEmail());
                String totalPriceAsString = (String) b[1];
                dto.setTotalPriceUsed(totalPriceAsString);
                listCustomers.add(dto);

            }
            return ResponseObject.builder().status(true).message("Top 10 - Khách hàng tiềm năng:").data(listCustomers).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> checkIn(Integer idTrip, String ticketCode, Integer idStationNow) {
        try{
            Ticket ticket = ticketRepo.findByIdTripAndTicketCode(idTrip,ticketCode);
            if(ticket == null){
                return ResponseObject.builder().status(false).message("Không tìm thấy.").build();
            }else if(ticket.getStatus().equals(TicketStatus.FINISHED.name())){
                return ResponseObject.builder().status(false).message("Chuyến đi này hoàn thành.").build();
            } else if (ticket.getStatus().equals(TicketStatus.CANCELED.name())) {
                return ResponseObject.builder().status(false).message("Chuyến đi này đã bị hủy bỏ hoặc tấm vé đã hủy.").build();
            }else if (ticket.getStatus().equals(TicketStatus.CHECK_IN.name())) {
                return ResponseObject.builder().status(false).message("Tấm vé này đã được checkin.").build();
            }
            int idStationOn = ticket.getOnStation().getIdStation();
            if(idStationOn != idStationNow){
                return ResponseObject.builder().status(false).message("Điểm lên trạm không đúng.").build();
            }
            Trip trip = tripRepo.findById(ticket.getTrip().getIdTrip()).orElse(null);
            ticket.setStatus(TicketStatus.CHECK_IN.name());
            TicketDTOviewCheckIn dtoTicket = modelMapper.map(ticket, TicketDTOviewCheckIn.class);
            dtoTicket.setIdTicket(ticket.getIdTicket());
            dtoTicket.setIdBooking(ticket.getBooking().getIdBooking());
            dtoTicket.setStatus(TicketStatus.CHECK_IN.name());
            dtoTicket.setCreateDate(ticket.getCreateDate());
            Station stationOn = stationRepo.findByIdStation(ticket.getOnStation().getIdStation());
            Station stationOff = stationRepo.findByIdStation(ticket.getOffStation().getIdStation());
            StationDTOviewInTrip dtoStationOn = modelMapper.map(stationOn, StationDTOviewInTrip.class);
            StationDTOviewInTrip dtoStationOff = modelMapper.map(stationOff, StationDTOviewInTrip.class);
            dtoTicket.setOnStation(dtoStationOn);
            dtoTicket.setOffStation(dtoStationOff);
            UserSystem user = userSystemRepo.findById(ticket.getBooking().getCustomer().getIdUserSystem()).orElse(null);
            UserSystemDTOInTicket userSystemDTOInTicket = modelMapper.map(user , UserSystemDTOInTicket.class);
            dtoTicket.setCustomer(userSystemDTOInTicket);
            ticketRepo.save(ticket);
            return ResponseObject.builder().status(true).message("Tấm vé đã được checkin.").data(dtoTicket).build();

        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> checkOut(Integer idTrip, String ticketCode) {
        try{
            Ticket ticket = ticketRepo.findByIdTripAndTicketCode(idTrip,ticketCode);
            if(ticket == null){
                return ResponseObject.builder().status(false).message("Không tìm thấy.").build();
            }else if(ticket.getStatus().equals(TicketStatus.NOT_CHECKIN.name())){
                return ResponseObject.builder().status(false).message("Tấm vé này chưa được checkin.").build();
            } else if (ticket.getStatus().equals(TicketStatus.CANCELED.name())) {
                return ResponseObject.builder().status(false).message("Chuyến đi này đã bị hủy bỏ hoặc tấm vé đã hủy.").build();
            } else if(ticket.getStatus().equals(TicketStatus.PAID.name())){
                return ResponseObject.builder().status(false).message("Tấm vé này chưa được checkin.").build();
            }else if(ticket.getStatus().equals(TicketStatus.FINISHED.name())){
                return ResponseObject.builder().status(false).message("Tấm vé này đã được hoàn thành.").build();
            }else if(ticket.getStatus().equals(TicketStatus.NO_SHOW.name())){
                return ResponseObject.builder().status(false).message("Tấm vé này đã được hoàn thành.").build();
            }

            Trip trip = tripRepo.findById(ticket.getTrip().getIdTrip()).orElse(null);
            ticket.setStatus(TicketStatus.FINISHED.name());
            TicketDTOviewCheckIn dtoTicket = modelMapper.map(ticket, TicketDTOviewCheckIn.class);
            dtoTicket.setIdTicket(ticket.getIdTicket());
            dtoTicket.setIdBooking(ticket.getBooking().getIdBooking());
            dtoTicket.setStatus(TicketStatus.FINISHED.name());
            dtoTicket.setCreateDate(ticket.getCreateDate());
            Station stationOn = stationRepo.findByIdStation(ticket.getOnStation().getIdStation());
            Station stationOff = stationRepo.findByIdStation(ticket.getOffStation().getIdStation());
            StationDTOviewInTrip dtoStationOn = modelMapper.map(stationOn, StationDTOviewInTrip.class);
            StationDTOviewInTrip dtoStationOff = modelMapper.map(stationOff, StationDTOviewInTrip.class);
            dtoTicket.setOnStation(dtoStationOn);
            dtoTicket.setOffStation(dtoStationOff);
            UserSystem user = userSystemRepo.findById(ticket.getBooking().getCustomer().getIdUserSystem()).orElse(null);
            UserSystemDTOInTicket userSystemDTOInTicket = modelMapper.map(user , UserSystemDTOInTicket.class);
            dtoTicket.setCustomer(userSystemDTOInTicket);
            ticketRepo.save(ticket);
            return ResponseObject.builder().status(true).message("Tấm vé được hoàn thành.").data(dtoTicket).build();

        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> changeSeatOfTicket(TicketDTOChangeSeat b) {
        try{
            Ticket check =ticketRepo.findById(b.getIdTicket()).orElse(null);
            if(check == null){
                return ResponseObject.builder().status(false).message("Không tìm thấy ticket.").build();
            }else if(check.getStatus().equals("CANCELED")){
                return ResponseObject.builder().status(false).message("Không thể đổi chỗ ngồi.").build();
            }else if(!check.getStatus().equals("PAID")){
                return ResponseObject.builder().status(false).message("Bạn chỉ có thể đổi chỗ nếu chuyến đi này chưa xuất phát.").build();
            }else if(check.getSeatName().equals(b.getSeatName())){
                return ResponseObject.builder().status(true).message("Không có gì thay đổi.").build();
            }
            check.setSeatName(b.getSeatName());
            ticketRepo.save(check);
            return ResponseObject.builder().status(true).message("Đổi vé thành công.").build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra.").build();
        }
    }


}
