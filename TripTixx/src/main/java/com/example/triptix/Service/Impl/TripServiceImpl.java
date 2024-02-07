package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.Notification.NotificationDTOcreate;
import com.example.triptix.DTO.Notification.NotificationMessage;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Route.RouteDTOview;
import com.example.triptix.DTO.Route.RouteDTOviewInTrip;
import com.example.triptix.DTO.Route.StationInRouteDTOview;
import com.example.triptix.DTO.Route.TicketTypeDTOview;
import com.example.triptix.DTO.Station.StationDTOviewInTrip;
import com.example.triptix.DTO.StationTimeCome.StationTimeComeDTOcreate;
import com.example.triptix.DTO.Ticket.TicketDTOviewInTrip;
import com.example.triptix.DTO.Trip.*;
import com.example.triptix.DTO.UserSystem.UserSystemDTOview;
import com.example.triptix.DTO.UserSystem.UserSystemDTOviewInTrip;
import com.example.triptix.DTO.Vehicle.VehicleDTOview;
import com.example.triptix.DTO.Vehicle.VehicleDTOviewInTrip;
import com.example.triptix.Enum.AdminCheck;
import com.example.triptix.Enum.Status;
import com.example.triptix.Enum.StatusTrip;
import com.example.triptix.Enum.TicketStatus;
import com.example.triptix.Model.*;
import com.example.triptix.Model.key.TicketTypeInTripKey;
import com.example.triptix.Repository.*;
import com.example.triptix.Repository.TicketTypeInTripRepo;
import com.example.triptix.Service.*;
import com.example.triptix.Util.UTCTimeZoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class TripServiceImpl implements TripService {
    public static final String USERNAME_GUEST = "guest";
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TripRepo tripRepo;

    @Autowired
    private RouteRepo routeRepo;

    @Autowired
    private RouteService routeService;

    @Autowired
    private UserSystemRepo userSystemRepo;

    @Autowired
    private VehicleRepo vehicleRepo;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private TicketTypeRepo ticketTypeRepo;

    @Autowired
    private WalletRepo walletRepo;

    @Autowired
    private TicketTypeInTripRepo ticketTypeInTripRepo;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserSystemService userSystemService;

    @Autowired
    private Environment env;

    @Autowired
    private FireBaseNotificationMessagingServiceImpl fireBaseNotificationMessagingServiceImpl;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private MailService mailService;

    @Autowired
    private StationInRouteRepo stationInRouteRepo;

    @Autowired
    private StationRepo stationRepo;

    @Autowired
    private ProvinceCityRepo provinceCityRepo;

    @Autowired
    private StationTimeComeService stationTimeComeService;

    @Autowired
    private StationTimeComeRepo stationTimeComeRepo;

    private int generateUniqueId() {
        return (int) (Math.random() * 100000);
    }

    // Hàm kiểm tra xem hai khoảng thời gian có trùng nhau không
    public boolean isTimeOverlap(Date start1, Date end1, Date start2, Date end2) {
        Calendar calendar = Calendar.getInstance();
        //Trừ 59 phút vào star1
        calendar.setTime(start1);
        calendar.add(Calendar.MINUTE, -59);
        start1 = calendar.getTime();
        // Cộng thêm 59 phút vào end1
        calendar.setTime(end1);
        calendar.add(Calendar.MINUTE, 59);
        end1 = calendar.getTime();
        return !start1.after(end2) && !start2.after(end1);

    }

    @Override
    public ResponseObject<?> getAll(Integer routeId, String startTime, String status, String adminCheck, int pageSize, int pageIndex) {
        try {
            String formattedDate = null;
            SimpleDateFormat dateFormat = null;
            if (startTime != null) {
                Long timeInMillis = Long.valueOf(startTime);
                java.sql.Date sqlDate = new java.sql.Date(timeInMillis * 1000);
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                formattedDate = dateFormat.format(sqlDate);
            }
            List<Trip> trip = null;
            Page<Trip> tripPage = null;
            Pageable pageable = null;
            int totalPage = 0;
            if (pageSize != 0 && pageIndex != 0) {
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
            } else {
                pageIndex = 1;
            }
            // Nếu không có tham số nào được cung cấp, lấy tất cả các trạm.
            if (routeId != null && startTime != null && status != null && adminCheck != null) {
                tripPage = tripRepo.findByRouteAndStartTimeContainingAndStatusAndAdminCheck(routeId, formattedDate, status, adminCheck, pageable);
            } else if (routeId != null && startTime != null && status != null) {
                tripPage = tripRepo.findByRouteAndStartTimeContainingAndStatus(routeId, formattedDate, status, pageable);
            } else if (routeId != null && startTime != null && adminCheck != null) {
                tripPage = tripRepo.findByRouteAndStartTimeContainingAndAdminCheck(routeId, formattedDate, adminCheck, pageable);
            } else if (routeId != null && status != null && adminCheck != null) {
                tripPage = tripRepo.findByRouteAndStatusAndAdminCheck(routeId, status, adminCheck, pageable);
            } else if (routeId != null && startTime != null) {
                tripPage = tripRepo.findByRouteAndStartTimeContaining(routeId, formattedDate, pageable);
            } else if (routeId != null && status != null) {
                tripPage = tripRepo.findByRouteAndStatus(routeId, status, pageable);
            } else if (routeId != null && adminCheck != null) {
                tripPage = tripRepo.findByRouteAndAdminCheck(routeId, adminCheck, pageable);
            } else if (startTime != null && status != null) {
                tripPage = tripRepo.findByStartTimeContainingAndStatus(formattedDate, status, pageable);
            } else if (startTime != null && adminCheck != null) {
                tripPage = tripRepo.findByStartTimeContainingAndAdminCheck(formattedDate, adminCheck, pageable);
            } else if (status != null && adminCheck != null) {
                tripPage = tripRepo.findByStatusAndAdminCheck(status, adminCheck, pageable);
            } else if (startTime != null) {
                // Nếu chỉ "name" được cung cấp, tìm kiếm theo "name".
                tripPage = tripRepo.findByStartTimeContaining(formattedDate, pageable);
            } else if (routeId != null) {
                // Nếu chỉ "province" được cung cấp, tìm kiếm theo "province".
                tripPage = tripRepo.findByRoute(routeId, pageable);
            } else if (status != null) {
                tripPage = tripRepo.findByStatus(status, pageable);
            } else if (adminCheck != null) {
                tripPage = tripRepo.findByAdminCheck(adminCheck, pageable);
            } else {
                if (pageable != null) {
                    tripPage = tripRepo.findAllByOrderStatus(pageable);
                } else {
                    trip = tripRepo.findAll();
                }
            }

            if (tripPage != null) {
                totalPage = tripPage.getTotalPages();
                trip = tripPage.getContent();
            }
            List<TripDTO> tripDTOS = new ArrayList<>();
            try {
                for (Trip b : trip) {
                    TripDTO dto = modelMapper.map(b, TripDTO.class);
                    //Get Route ra
                    RouteDTOviewInTrip routeDTOview = modelMapper.map((RouteDTOview) routeService.getDetail(b.getRoute().getIdRoute()).getData(), RouteDTOviewInTrip.class);
                    for (StationInRouteDTOview item : routeDTOview.getListStationInRoute()) {
                        item.setTimeCome(String.valueOf(stationTimeComeRepo.findByIdStationTimeCome(item.getIdStationInRoute(), b.getIdTrip())));
                    }
                    for (TicketTypeDTOview item : routeDTOview.getListTicketType()) {
                        try{
                            item.setPriceInTrip(ticketTypeInTripRepo.findById(new TicketTypeInTripKey(item.getIdTicketType(), b.getIdTrip())).get().getPrice());
                        }catch (Exception e){ //for case trip don't use all ticket type, -1 is present for this case
                            item.setPriceInTrip(-1);
                        }
                    }
                    dto.setRoute(routeDTOview);
                    //Get Driver ra
                    UserSystemDTOviewInTrip driverDTOview = modelMapper.map(b.getDriver(), UserSystemDTOviewInTrip.class);
                    driverDTOview.setBirthdayLong(b.getDriver().getBirthday().getTime() / 1000);
                    dto.setDriver(driverDTOview);
                    //Get Bus ra
                    VehicleDTOviewInTrip vehicleDTOview = modelMapper.map((VehicleDTOview) vehicleService.getDetail(b.getVehicle().getIdBus()).getData(), VehicleDTOviewInTrip.class);
                    dto.setVehicle(vehicleDTOview);
                    //Get Staff ra
                    UserSystemDTOviewInTrip staffDTOview = modelMapper.map(b.getStaff(), UserSystemDTOviewInTrip.class);
                    staffDTOview.setBirthdayLong(b.getStaff().getBirthday().getTime() / 1000);
                    dto.setStaff(staffDTOview);

//                    dto.setDepartureDateStr(sdf1.format(b.getDepartureDate()));
//                    dto.setEndDateStr(sdf1.format(b.getEndDate()));
                    dto.setDepartureDateLT(b.getDepartureDate().getTime() / 1000);
                    dto.setEndDateLT(b.getEndDate().getTime() / 1000);
                    dto.setTotalCustomer(tripRepo.getTotalCustomer(b.getIdTrip()));
                    dto.setTickets(new ArrayList<>());
                    TicketDTOviewInTrip ticketDTOview = null;
                    for (Ticket ticketTrip: b.getTickets()) {
                        ticketDTOview = modelMapper.map(ticketTrip, TicketDTOviewInTrip.class);
                        if(ticketTrip.getBooking().getCustomer().getUserName().equals(USERNAME_GUEST)){
                            ticketDTOview.setFullName(ticketTrip.getBooking().getPaymentTransaction().getNameGuest());
                            ticketDTOview.setEmail(ticketTrip.getBooking().getPaymentTransaction().getEmailGuest());
                            ticketDTOview.setPhone(ticketTrip.getBooking().getPaymentTransaction().getPhoneGuest());
                        }else{
                            ticketDTOview.setFullName(ticketTrip.getBooking().getCustomer().getFullName());
                            ticketDTOview.setEmail(ticketTrip.getBooking().getCustomer().getEmail());
                            ticketDTOview.setPhone(ticketTrip.getBooking().getCustomer().getPhone());
                        }
                        dto.getTickets().add(ticketDTOview);
                    }
                    tripDTOS.add(dto);
                }
            } catch (Exception e) {
                return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
            }
            return ResponseObject.builder().status(true).message("get all success")
                    .pageSize(tripDTOS.size()).pageIndex(pageIndex).totalPage(totalPage)
                    .data(tripDTOS).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> getTripAdminCheck(int pageSize, int pageIndex) {
        try {
            List<Integer> listIdTripPendingForAdminAccept = null;
            List<Trip> trip = null;
            Page<Trip> tripPage = null;
            Pageable pageable = null;
            int totalPage = 0;
            if (pageSize != 0 && pageIndex != 0) {
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
            } else {
                pageIndex = 1;
            }
            //dang tìm chuyến để admin duyệt
            listIdTripPendingForAdminAccept = tripRepo.findIdTripPendingForAdminAccept();
            tripPage = tripRepo.findByListIDTrip(listIdTripPendingForAdminAccept, pageable);

            if (tripPage != null) {
                totalPage = tripPage.getTotalPages();
                trip = tripPage.getContent();
            }
            List<TripDTO> tripDTOS = new ArrayList<>();
            List<IdTripAndDepartureDate> idTripAndDepartureDates = null;
            IdTripAndDepartureDate idTripAndDepartureDate = null;
            Date DepartureDate = null;
            SimpleDateFormat dateFormat = null;
            try {
                for (Trip b : trip) {
                    TripDTO dto = modelMapper.map(b, TripDTO.class);
                    //Get Route ra
                    RouteDTOviewInTrip routeDTOview = modelMapper.map((RouteDTOview) routeService.getDetail(b.getRoute().getIdRoute()).getData(), RouteDTOviewInTrip.class);
                    for (StationInRouteDTOview item : routeDTOview.getListStationInRoute()) {
                        item.setTimeCome(String.valueOf(stationTimeComeRepo.findByIdStationTimeCome(item.getIdStationInRoute(), b.getIdTrip())));
                    }
                    for (TicketTypeDTOview item : routeDTOview.getListTicketType()) {
                        try{
                            item.setPriceInTrip(ticketTypeInTripRepo.findById(new TicketTypeInTripKey(item.getIdTicketType(), b.getIdTrip())).get().getPrice());
                        }catch (Exception e){ //for case trip don't use all ticket type, -1 is present for this case
                            item.setPriceInTrip(-1);
                        }
                    }
                    dto.setRoute(routeDTOview);
                    //Get Driver ra
                    UserSystemDTOviewInTrip driverDTOview = modelMapper.map(b.getDriver(), UserSystemDTOviewInTrip.class);
                    driverDTOview.setBirthdayLong(b.getDriver().getBirthday().getTime() / 1000);
                    dto.setDriver(driverDTOview);
                    //Get Bus ra
                    VehicleDTOviewInTrip vehicleDTOview = modelMapper.map((VehicleDTOview) vehicleService.getDetail(b.getVehicle().getIdBus()).getData(), VehicleDTOviewInTrip.class);
                    dto.setVehicle(vehicleDTOview);
                    //Get Staff ra
                    UserSystemDTOviewInTrip staffDTOview = modelMapper.map(b.getStaff(), UserSystemDTOviewInTrip.class);
                    staffDTOview.setBirthdayLong(b.getStaff().getBirthday().getTime() / 1000);
                    dto.setStaff(staffDTOview);

//                    dto.setDepartureDateStr(sdf1.format(b.getDepartureDate()));
//                    dto.setEndDateStr(sdf1.format(b.getEndDate()));
                    dto.setDepartureDateLT(b.getDepartureDate().getTime() / 1000);
                    dto.setEndDateLT(b.getEndDate().getTime() / 1000);
                    dto.setTotalCustomer(tripRepo.getTotalCustomer(b.getIdTrip()));
                    dto.setTickets(new ArrayList<>());
                    TicketDTOviewInTrip ticketDTOview = null;
                    for (Ticket ticketTrip: b.getTickets()) {
                        ticketDTOview = modelMapper.map(ticketTrip, TicketDTOviewInTrip.class);
                        if(ticketTrip.getBooking().getCustomer().getUserName().equals(USERNAME_GUEST)){
                            ticketDTOview.setFullName(ticketTrip.getBooking().getPaymentTransaction().getNameGuest());
                            ticketDTOview.setEmail(ticketTrip.getBooking().getPaymentTransaction().getEmailGuest());
                            ticketDTOview.setPhone(ticketTrip.getBooking().getPaymentTransaction().getPhoneGuest());
                        }else{
                            ticketDTOview.setFullName(ticketTrip.getBooking().getCustomer().getFullName());
                            ticketDTOview.setEmail(ticketTrip.getBooking().getCustomer().getEmail());
                            ticketDTOview.setPhone(ticketTrip.getBooking().getCustomer().getPhone());
                        }
                        dto.getTickets().add(ticketDTOview);
                    }
                    //if get trip pending for admin check accept so
                    if(listIdTripPendingForAdminAccept != null && listIdTripPendingForAdminAccept.size() > 0){
                        if(b.getRepeatCycle() != null ){
                            List<List<Object>> listIdTripAndDepartureDate = tripRepo.findIdTripAndDepartureDateByRepeatCycle(b.getRepeatCycle());
                            if(listIdTripAndDepartureDate != null && listIdTripAndDepartureDate.size() > 0){
                                idTripAndDepartureDates = new ArrayList<>();
                                for (List<Object> item : listIdTripAndDepartureDate) {
                                    if(Integer.parseInt(item.get(0).toString()) == b.getIdTrip()){
                                        continue;
                                    }
                                    idTripAndDepartureDate = new IdTripAndDepartureDate();
                                    idTripAndDepartureDate.setIdTrip(Integer.parseInt(item.get(0).toString()));

                                    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    DepartureDate = dateFormat.parse(item.get(1).toString());
                                    idTripAndDepartureDate.setDepartureDateLT(DepartureDate.getTime() / 1000);

                                    idTripAndDepartureDates.add(idTripAndDepartureDate);
                                }
                                dto.setListSchedules(idTripAndDepartureDates);
                            }
                        }
                    }
                    tripDTOS.add(dto);
                }
            } catch (Exception e) {
                return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
            }
            return ResponseObject.builder().status(true).message("get all success")
                    .pageSize(tripDTOS.size()).pageIndex(pageIndex).totalPage(totalPage)
                    .data(tripDTOS).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> getDetail(int id) {
        try {
            Trip trip = tripRepo.findById(id).get();
            if (trip == null) {
                return ResponseObject.builder().status(false).message("Khong tim thay").build();
            }

//            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            TripDTO dto = modelMapper.map(trip, TripDTO.class);
            //Get Route ra
            RouteDTOviewInTrip routeDTOview = modelMapper.map((RouteDTOview) routeService.getDetail(trip.getRoute().getIdRoute()).getData(), RouteDTOviewInTrip.class);
            for (StationInRouteDTOview item : routeDTOview.getListStationInRoute()) {
//                    stationTimeCome = (StationTimeCome) stationTimeComeService.getDetail(new StationTimeComeKey(item.getIdStationInRoute(), b.getIdTrip())).getData();
                item.setTimeCome(String.valueOf(stationTimeComeRepo.findByIdStationTimeCome(item.getIdStationInRoute(), id)).substring(0,5));
            }
            for (TicketTypeDTOview item : routeDTOview.getListTicketType()) {
                try {
                    item.setPriceInTrip(ticketTypeInTripRepo.findById(new TicketTypeInTripKey(item.getIdTicketType(), id)).get().getPrice());
                }catch (Exception e){ //for case trip don't use all ticket type, -1 is present for this case
                    item.setPriceInTrip(-1);
                }
            }
            dto.setRoute(routeDTOview);
            //Get Driver ra
            UserSystemDTOviewInTrip driverDTOview = modelMapper.map(trip.getDriver(), UserSystemDTOviewInTrip.class);
            driverDTOview.setBirthdayLong(trip.getDriver().getBirthday().getTime() / 1000);
            dto.setDriver(driverDTOview);
            //Get Bus ra
            VehicleDTOviewInTrip vehicleDTOview = modelMapper.map((VehicleDTOview) vehicleService.getDetail(trip.getVehicle().getIdBus()).getData(), VehicleDTOviewInTrip.class);
            dto.setVehicle(vehicleDTOview);
            //Get Staff ra
            UserSystemDTOviewInTrip staffDTOview = modelMapper.map(trip.getStaff(), UserSystemDTOviewInTrip.class);
            staffDTOview.setBirthdayLong(trip.getStaff().getBirthday().getTime() / 1000);
            dto.setStaff(staffDTOview);

//            dto.setDepartureDateStr(sdf1.format(trip.getDepartureDate()));
//            dto.setEndDateStr(sdf1.format(trip.getEndDate()));
            dto.setDepartureDateLT(trip.getDepartureDate().getTime() / 1000);
            dto.setEndDateLT(trip.getEndDate().getTime() / 1000);
            dto.setTotalCustomer(tripRepo.getTotalCustomer(id));
            dto.setTickets(new ArrayList<>());
            TicketDTOviewInTrip ticketDTOview = null;
            for (Ticket ticketTrip: trip.getTickets()) {
                ticketDTOview = modelMapper.map(ticketTrip, TicketDTOviewInTrip.class);
                if(ticketTrip.getBooking().getCustomer().getUserName().equals(USERNAME_GUEST)){
                    ticketDTOview.setFullName(ticketTrip.getBooking().getPaymentTransaction().getNameGuest());
                    ticketDTOview.setEmail(ticketTrip.getBooking().getPaymentTransaction().getEmailGuest());
                    ticketDTOview.setPhone(ticketTrip.getBooking().getPaymentTransaction().getPhoneGuest());
                }else{
                    ticketDTOview.setFullName(ticketTrip.getBooking().getCustomer().getFullName());
                    ticketDTOview.setEmail(ticketTrip.getBooking().getCustomer().getEmail());
                    ticketDTOview.setPhone(ticketTrip.getBooking().getCustomer().getPhone());
                }
                dto.getTickets().add(ticketDTOview);
            }

            return ResponseObject.builder().status(true).message("get all success")
                    .pageSize(1).pageIndex(1).totalPage(1)
                    .data(dto).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> search(String codeDeparturePoint, String codeDestination, String startTime, int pageSize, int pageIndex) {
        try {
            //format date lại từ long(kểu stampstime) truyền về
            String formattedDate = null;
            SimpleDateFormat dateFormat = null;
            if (startTime != null) {
                Long timeInMillis = Long.valueOf(startTime);
                java.sql.Date sqlDate = new java.sql.Date(timeInMillis * 1000);
                dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                formattedDate = dateFormat.format(sqlDate);
            }
            int totalPage = 0;
            List<Integer> ListMainTripId = new ArrayList<>();    //list route trip
            List<Integer> ListSubTripId = new ArrayList<>();     //list city trip
            //paging
            Pageable pageable = null;
            if (pageSize != 0 && pageIndex != 0) {
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
            } else {
                pageIndex = 1;
            }
            //từ code destination và code departurePoint lấy list trip có bao gồm chúng ra (gồm trip main và sub trip-có station mà khách mún)
            //check nếu khác null 2 cái thì lấy province-city từ 2 cái code đó ra và tìm route từ 2 province đó
            //check code truền về là 63 tỉnh thành hay là code của các thành phố
            String SearchType = "";
            ProvinceCity DeparturePoint = provinceCityRepo.findById(codeDeparturePoint).get();
            ProvinceCity Destination = provinceCityRepo.findById(codeDestination).get();
            if (codeDeparturePoint != null && codeDestination != null) {
                if (codeDeparturePoint.contains("-") && codeDestination.contains("-")) {
                    SearchType = "2city";    //2 code điều là thành phố
                } else if (codeDeparturePoint.contains("-") && !codeDestination.contains("-")) {
                    SearchType = "city-63province"; //code đầ là tp, sau là tình
                } else if (!codeDeparturePoint.contains("-") && codeDestination.contains("-")) {
                    SearchType = "63province-city"; //code đầ là tp, sau là tình
                } else if (!codeDeparturePoint.contains("-") && !codeDestination.contains("-")) {
                    SearchType = "263province"; //2 code dieu793 là 63 tỉnh
                }

                List<Integer> listIdStationDestination = null;
                List<Integer> listIdStationDeparture = null;
                List<Integer> listIdRouteIncludeBothStation = null;
                Integer orderIndexDeparture = null;
                Integer orderIndexDestination = null;
                //mục tiêu: mỗi switch-case phải lấy đc listSubTripID và ListMainTripID
                switch (SearchType) {
                    case "2city":
                        log.info("==> SearchType: " + SearchType +" ("+codeDeparturePoint+" -> "+codeDestination+")");
                        //main trip
                        ListMainTripId = tripRepo.searchReturnIdTrip(codeDeparturePoint.substring(0, codeDeparturePoint.indexOf("-")), codeDestination.substring(0, codeDestination.indexOf("-")), formattedDate, pageable).getContent();
                        //test
                        System.out.println("==> List Main TripId: " + ListMainTripId);
                        //test

                        //sub trip
                        //lâý hết id station có province thuộc 2 tình trên
                        listIdStationDeparture = stationRepo.findIdStationByAddressContaining(DeparturePoint.getName().substring(10));
                        //test
                        System.out.println("list IdStation Departure: " + listIdStationDeparture);
                        //test
                        listIdStationDestination = stationRepo.findIdStationByAddressContaining(Destination.getName().substring(10));
                        //test
                        System.out.println("list IdStation Destination: " + listIdStationDestination);
                        //test
                        break;
                    case "city-63province":
                        log.info("==> SearchType: " + SearchType +" ("+codeDeparturePoint+" -> "+codeDestination+")");
                        //main trip
                        ListMainTripId = tripRepo.searchReturnIdTrip(codeDeparturePoint.substring(0, codeDeparturePoint.indexOf("-")), codeDestination, formattedDate, pageable).getContent();
                        //test
                        System.out.println("==> List Main TripId: " + ListMainTripId);
                        //test

                        //sub trip
                        //lâý hết id station có province thuộc 2 tình trên
                        listIdStationDeparture = stationRepo.findIdStationByAddressContaining(DeparturePoint.getName().substring(10));
                        //test
                        System.out.println("list IdStation Departure: " + listIdStationDeparture);
                        //test
                        listIdStationDestination = stationRepo.findIdStationByProvince(Destination.getName());
                        //test
                        System.out.println("list IdStation Destination: " + listIdStationDestination);
                        //test
                        break;
                    case "63province-city":
                        log.info("==> SearchType: " + SearchType +" ("+codeDeparturePoint+" -> "+codeDestination+")");
                        //main trip
                        ListMainTripId = tripRepo.searchReturnIdTrip(codeDeparturePoint, codeDestination.substring(0, codeDestination.indexOf("-")), formattedDate, pageable).getContent();
                        //test
                        System.out.println("==> List Main TripId: " + ListMainTripId);
                        //test

                        //sub trip
                        //lâý hết id station có province thuộc 2 tình trên
                        listIdStationDeparture = stationRepo.findIdStationByProvince(DeparturePoint.getName());
                        //test
                        System.out.println("list IdStation Departure: " + listIdStationDeparture);
                        //test
                        listIdStationDestination = stationRepo.findIdStationByAddressContaining(Destination.getName().substring(10));
                        //test
                        System.out.println("list IdStation Destination: " + listIdStationDestination);
                        //test
                        break;
                    case "263province":
                        log.info("==> SearchType: " + SearchType +" ("+codeDeparturePoint+" -> "+codeDestination+")");
                        //search 2 tình đầu-cuối trong route
                        //main trip
                        ListMainTripId = tripRepo.searchReturnIdTrip(codeDeparturePoint, codeDestination, formattedDate, pageable).getContent();
                        //test
                        System.out.println("==> List Main TripId: " + ListMainTripId);
                        //test

                        //sub trip
                        //lâý hết id station có province thuộc 2 tình trên
                        listIdStationDeparture = stationRepo.findIdStationByProvince(DeparturePoint.getName());
                        //test
                        System.out.println("list IdStation Departure: " + listIdStationDeparture);
                        //test
                        listIdStationDestination = stationRepo.findIdStationByProvince(Destination.getName());
                        //test
                        System.out.println("list IdStation Destination: " + listIdStationDestination);
                        //test
                        break;
                }

                if((listIdStationDestination != null) && (listIdStationDeparture != null)){
                    //lấy list route thỏa đk có chứa station thuộc 2 tỉnh
                    listIdRouteIncludeBothStation = routeRepo.findIdRouteIncludeBothStation(listIdStationDeparture, listIdStationDestination);
                    //test
                    System.out.println("list IdRoute Include Both list Station: " + listIdRouteIncludeBothStation);
                    //test
                    for (Integer idRouteIncludeBothStation : listIdRouteIncludeBothStation) {
                        for(int i = 0; i < listIdStationDeparture.size(); i++){
                            orderIndexDeparture = stationInRouteRepo.findOrderByIdRouteAndIdStation(idRouteIncludeBothStation, listIdStationDeparture.get(i));   //vì nếu listidstationdeparture là 1,2,3 và listidstationdropoff là ,5,6 => route nó có station trong cả 2, cụ thể là 3,5 => chạy dòng for để tìm station pickup nằm trong nó để lấy index
                            if(orderIndexDeparture != null){ //lấy cái đầu tiên ra đc index là oke r, vì default là departure là nó nằm trc, hay index order station in route nó luôn phải nhỏ hơn dropoff
                                break;
                            }
                        }
                        for(int i = 0; i < listIdStationDestination.size(); i++){
                            orderIndexDestination = stationInRouteRepo.findOrderByIdRouteAndIdStation(idRouteIncludeBothStation, listIdStationDestination.get(i));
                            if(orderIndexDestination != null){ //tương tự cái trên
                                break;
                            }
                        }
                        if(orderIndexDeparture < orderIndexDestination){    //theo thứ tự thì departure < destionation trong order station in route
                            ListSubTripId.addAll(tripRepo.findIdTripByRouteAndStartTimeContainingAndStatusAndAdminCheck(idRouteIncludeBothStation, formattedDate, StatusTrip.READY.name(), AdminCheck.ACCEPTED.name(), null));
                        }
//                        if(orderIndexDeparture == null || orderIndexDestination == null){   //nếu ko lấy đc index thì nó ko có hay route ó ko có station thỏa đk
//                            continue;
//                        }else{
//
//                        }
                    }
                    //test
                    System.out.println("List Sub TripId: " + ListSubTripId);
                    //test
                }

                //lọc trip trùng, cùng có ở 2 bên sub, main, bỏ bên sub, giữ bên main
                List<Integer> ListSubTripIdFilterNoSameDataMainTrip = new ArrayList<>(ListSubTripId); //list na là list sub trip sau khi lọc hay nó ko còn chứa id trip trùng vs main trip hay lọc rồi
                for (Integer idTripMain: ListMainTripId){
                    if(ListSubTripId.size() > 0){
                        if(ListSubTripId.contains(idTripMain)){
                            ListSubTripIdFilterNoSameDataMainTrip.remove(idTripMain);
                        }
                    }
                }
                //test
                System.out.println("==> List Sub TripId Filter No Same Data in MainTrip: " + ListSubTripIdFilterNoSameDataMainTrip);
                //test

                //lấy dto của 2 list sub, mainđể trả về
                List<TripDTO> tripDTOS = new ArrayList<>();
                dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                tripDTOS.addAll(createListTripDTO(ListMainTripId, false, dateFormat));
                tripDTOS.addAll(createListTripDTO(ListSubTripIdFilterNoSameDataMainTrip, true, dateFormat));

                return ResponseObject.builder().status(true).message("get all success")
                        .pageSize(tripDTOS.size()).pageIndex(pageIndex).totalPage(totalPage)
                        .data(tripDTOS).build();
            }

            return ResponseObject.builder().status(false).message("check your input")
                    .pageSize(0).pageIndex(pageIndex).totalPage(totalPage)
                    .data(null).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    private List<TripDTO> createListTripDTO(List<Integer> listMainTripId, boolean SubOrNot, SimpleDateFormat dateFormat) throws Exception {
        List<TripDTO> tripDTOS = new ArrayList<>();
        List<Trip> trip = new ArrayList<>();
        for (Integer idTripMain: listMainTripId){
            trip.add(tripRepo.findById(idTripMain).get());
        }
        TripDTO dto = null;
        List<Integer> listIdStationAsc = null;
//        StationTimeCome stationTimeCome = null;
        try {
            for (Trip b : trip) {
                dto = modelMapper.map(b, TripDTO.class);
                //Get Route ra
                RouteDTOviewInTrip routeDTOview = modelMapper.map((RouteDTOview) routeService.getDetail(b.getRoute().getIdRoute()).getData(), RouteDTOviewInTrip.class);
                for (StationInRouteDTOview item : routeDTOview.getListStationInRoute()) {
//                    stationTimeCome = (StationTimeCome) stationTimeComeService.getDetail(new StationTimeComeKey(item.getIdStationInRoute(), b.getIdTrip())).getData();
                    item.setTimeCome(String.valueOf(stationTimeComeRepo.findByIdStationTimeCome(item.getIdStationInRoute(), b.getIdTrip())).substring(0, 5));
                }
                for (TicketTypeDTOview item : routeDTOview.getListTicketType()) {
                    try {
                        item.setPriceInTrip(ticketTypeInTripRepo.findById(new TicketTypeInTripKey(item.getIdTicketType(), b.getIdTrip())).get().getPrice());
                    }catch (Exception e){ //for case trip don't use all ticket type, -1 is present for this case
                        item.setPriceInTrip(-1);
                    }
                }
                dto.setRoute(routeDTOview);
                //check xem phải list sub trip ko
                if(SubOrNot){
                    //đang là list sub trip
                    dto.setSubTrip("Vé chặng của chuyến " + routeDTOview.getDeparturePoint() + " - " + routeDTOview.getDestination());
                }
                //Get Driver ra
                UserSystemDTOviewInTrip driverDTOview = modelMapper.map(b.getDriver(), UserSystemDTOviewInTrip.class);
                driverDTOview.setBirthdayLong(b.getDriver().getBirthday().getTime() / 1000);
                dto.setDriver(driverDTOview);
                //Get Bus ra
                VehicleDTOviewInTrip vehicleDTOview = modelMapper.map((VehicleDTOview) vehicleService.getDetail(b.getVehicle().getIdBus()).getData(), VehicleDTOviewInTrip.class);
                dto.setVehicle(vehicleDTOview);
                //Get Staff ra
                UserSystemDTOviewInTrip staffDTOview = modelMapper.map(b.getStaff(), UserSystemDTOviewInTrip.class);
                staffDTOview.setBirthdayLong(b.getStaff().getBirthday().getTime() / 1000);
                dto.setStaff(staffDTOview);

//                dto.setDepartureDateStr(dateFormat.format(b.getDepartureDate()));
//                dto.setEndDateStr(dateFormat.format(b.getEndDate()));
                dto.setDepartureDateLT(b.getDepartureDate().getTime() / 1000);
                dto.setEndDateLT(b.getEndDate().getTime() / 1000);
                dto.setTotalCustomer(tripRepo.getTotalCustomer(b.getIdTrip()));
                dto.setTickets(null);

                //lấy list id station order asc của trip
                listIdStationAsc = stationInRouteRepo.findListIdStationAscByIdTrip(b.getIdTrip());
                dto.setSeatNameBusy((List<String>) searchSeatInTrip(listIdStationAsc.get(0), listIdStationAsc.get(listIdStationAsc.size() - 1), b.getIdTrip(), null).getData());
                dto.setBookedSeat((short) dto.getSeatNameBusy().size());
                dto.setAvailableSeat((short) (vehicleDTOview.getCapacity() - dto.getBookedSeat()));
                tripDTOS.add(dto);
            }
            return tripDTOS;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ResponseObject<?> searchSeatInTrip(int idStationPickUp, int idStationDropOff, int idTrip, List<ComboSeatStation> comboSeatStations) {
        try{
            Map<String, List<String>> listSeatBusyInStation = null;
            //check list comboSeatStations để tạo hash map lâ data
            if(comboSeatStations != null && comboSeatStations.size() > 0){
                listSeatBusyInStation = new HashMap<>();
                for (ComboSeatStation item: comboSeatStations) {
                    if(!listSeatBusyInStation.containsKey(item.getCodePickUpPoint()+"-"+ item.getCodeDropOffPoint())){ //ko có key thì add vào
                        listSeatBusyInStation.put(item.getCodePickUpPoint()+"-"+ item.getCodeDropOffPoint(), item.getSeatName());
                    }else{ //đã có key rồi (hay đã tồn tại cặp trạm rồi)
                        listSeatBusyInStation.get(item.getCodePickUpPoint()+"-"+ item.getCodeDropOffPoint()).addAll(item.getSeatName());
                    }
                }
            }
            Trip trip = tripRepo.findById(idTrip).get();
            //test
            System.out.println("id Station pickup: " + idStationPickUp + " - id station drop off: " + idStationDropOff);
            //test
            if (trip == null) {
                return ResponseObject.builder().status(false).message("Khong tim thay").build();
            }
            List<String> listSeatNameBusy = new ArrayList<>();
            List<String> listSeatNameInCoupleStation = null;
            //lấy list id station order asc của trip
            List<Integer> listIdStationAsc = stationInRouteRepo.findListIdStationAscByIdTrip(idTrip);
            //test
            System.out.println("list station asc: " + listIdStationAsc.toString() + " of trip " + idTrip); //1 2 3-4-5 6 7
            //test
            int indexStationPickup = listIdStationAsc.indexOf(idStationPickUp);
            int indexStationDropOff = listIdStationAsc.indexOf(idStationDropOff);

            //main-right: . . 3-4-5 6 7
            for (int i = 0; i < listIdStationAsc.size(); i++) {
                if(i >= indexStationPickup && i < indexStationDropOff){
                    for (int j = i + 1; j < listIdStationAsc.size(); j++) {
                        listSeatNameInCoupleStation = ticketRepo.findSeatNameBusyByIdTripAndIdCoupleStation(idTrip, listIdStationAsc.get(i), listIdStationAsc.get(j));
                        //test
                        System.out.println("MR: list seat name of station "+listIdStationAsc.get(i)+" from "+listIdStationAsc.get(j)+": " + listSeatNameInCoupleStation.toString());
                        //test
                        listSeatNameBusy.addAll(listSeatNameInCoupleStation);
                        //check bên comboSeatStations
                        if(comboSeatStations != null && comboSeatStations.size() > 0){
                            if(listSeatBusyInStation.containsKey(listIdStationAsc.get(i)+"-"+ listIdStationAsc.get(j))){
                                //test
                                System.out.println("MR (comboSeatStations): list seat name of station "+listIdStationAsc.get(i)+" from "+listIdStationAsc.get(j)+": " + listSeatBusyInStation.get(listIdStationAsc.get(i)+"-"+ listIdStationAsc.get(j)).toString());
                                //test
                                listSeatNameBusy.addAll(listSeatBusyInStation.get(listIdStationAsc.get(i)+"-"+ listIdStationAsc.get(j)));
                            }
                        }
                    }
                }
            }

            //left-main: 1 2 3-4-5 . .
            for (int i = listIdStationAsc.size() - 1; i >= 0; i--) {
                if(i <= indexStationDropOff && i > indexStationPickup){
                    for (int j = i - 1; j >= 0; j--) {
                        listSeatNameInCoupleStation = ticketRepo.findSeatNameBusyByIdTripAndIdCoupleStation(idTrip, listIdStationAsc.get(j), listIdStationAsc.get(i));
                        //test
                        System.out.println("LM: list seat name of station "+listIdStationAsc.get(j)+" from "+listIdStationAsc.get(i)+": " + listSeatNameInCoupleStation.toString());
                        //test
                        listSeatNameBusy.addAll(listSeatNameInCoupleStation);
                        //check bên comboSeatStations
                        if(comboSeatStations != null && comboSeatStations.size() > 0){
                            if(listSeatBusyInStation.containsKey(listIdStationAsc.get(j)+"-"+ listIdStationAsc.get(i))){
                                //test
                                System.out.println("LM (comboSeatStations): list seat name of station "+listIdStationAsc.get(j)+" from "+listIdStationAsc.get(i)+": " + listSeatBusyInStation.get(listIdStationAsc.get(j)+"-"+ listIdStationAsc.get(i)).toString());
                                //test
                                listSeatNameBusy.addAll(listSeatBusyInStation.get(listIdStationAsc.get(j)+"-"+ listIdStationAsc.get(i)));
                            }
                        }
                    }
                }
            }

            //main: 1 2 3-4-5 6 7
            if(indexStationDropOff - indexStationPickup == 1){ //main chi có 2 trạm (ở trên có check qua case này rồi)
//                listSeatNameInCoupleStation = ticketRepo.findSeatNameBusyByIdTripAndIdCoupleStation(idTrip, listIdStationAsc.get(indexStationPickup), listIdStationAsc.get(indexStationDropOff));
//                //test
//                System.out.println("M: list seat name of station "+listIdStationAsc.get(indexStationPickup)+" from "+listIdStationAsc.get(indexStationDropOff)+": " + listSeatNameInCoupleStation.toString());
//                //test
//                listSeatNameBusy.addAll(listSeatNameInCoupleStation);
            }else if(indexStationDropOff - indexStationPickup > 1){ //main có nhiều hơn 2 trạm
                for (int i = 0; i < listIdStationAsc.size(); i++) {
                    if(i == indexStationDropOff){
                        break;
                    }
                    if(i > indexStationPickup && i < indexStationDropOff){
                        listSeatNameBusy.addAll(getListListFromIdStationOrderAsc(listIdStationAsc, listIdStationAsc.get(i), idTrip, listSeatBusyInStation));
                    }
                }
            }

            //left-right: 1 2 . . . 6 7
            List<Integer> right = listIdStationAsc.subList(indexStationDropOff + 1, listIdStationAsc.size());
            for (int i = 0; i < listIdStationAsc.size(); i++) {
                if(i == indexStationPickup){
                    break;
                }
                for (int j = 0; j < right.size(); j++) { //right
                    listSeatNameInCoupleStation = ticketRepo.findSeatNameBusyByIdTripAndIdCoupleStation(idTrip, listIdStationAsc.get(i), right.get(j));
                    //test
                    System.out.println("LR: list seat name of station "+listIdStationAsc.get(i)+" from "+right.get(j)+": " + listSeatNameInCoupleStation.toString());
                    //test
                    listSeatNameBusy.addAll(listSeatNameInCoupleStation);
                    //check bên comboSeatStations
                    if(comboSeatStations != null && comboSeatStations.size() > 0){
                        if(listSeatBusyInStation.containsKey(listIdStationAsc.get(i)+"-"+ right.get(j))){
                            //test
                            System.out.println("LR (comboSeatStations): list seat name of station "+listIdStationAsc.get(i)+" from "+right.get(j)+": " + listSeatBusyInStation.get(listIdStationAsc.get(i)+"-"+ right.get(j)).toString());
                            //test
                            listSeatNameBusy.addAll(listSeatBusyInStation.get(listIdStationAsc.get(i)+"-"+ right.get(j)));
                        }
                    }
                }
            }

            //filter listSeatNameBusy not contain duplicate value
            Set<String> uniqueValues = new HashSet<>(listSeatNameBusy);
            listSeatNameBusy = new ArrayList<>(uniqueValues);

            return ResponseObject.builder().status(true).message("success").data(listSeatNameBusy).build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    /*
    * method to get two list left and right in mother list with milestone is an integer you want.
    * left and right list to caculate, get the seat name in couple station in case Main
    * */
    private List<String> getListListFromIdStationOrderAsc(List<Integer> listIdStationAsc, Integer idStation, int idTrip, Map<String, List<String>> listSeatBusyInStation) throws Exception {
        try{
            //main: 1 2 3-4-5 6 7
            List<String> listSeatNameBusy = new ArrayList<>();
            List<String> listSeatNameInCoupleStation = null;
            List<Integer> left = new ArrayList<>();
            List<Integer> right = new ArrayList<>();
            for (int i = 0; i < listIdStationAsc.size(); i++) {
                if(i < listIdStationAsc.indexOf(idStation)){
                    left.add(listIdStationAsc.get(i));
                }else if(i > listIdStationAsc.indexOf(idStation)){
                    right.add(listIdStationAsc.get(i));
                } else { //i == listIdStationAsc.indexOf(idStation)
                    left.add(listIdStationAsc.get(i));
                    right.add(listIdStationAsc.get(i));
                }
            }
            Collections.reverse(left);
            for (int i = 0; i < left.size(); i++) {
                if(i == 0) continue;
                listSeatNameInCoupleStation = ticketRepo.findSeatNameBusyByIdTripAndIdCoupleStation(idTrip, left.get(i), left.get(0)); //vì list reverse nên phải đão theo thứ tự i -> 0
                //test
                System.out.println("M:(L) list seat name of station "+left.get(i)+" from "+left.get(0)+": " + listSeatNameInCoupleStation.toString());
                //test
                listSeatNameBusy.addAll(listSeatNameInCoupleStation);
                //check bên comboSeatStations
                if(listSeatBusyInStation != null && listSeatBusyInStation.size() > 0){
                    if(listSeatBusyInStation.containsKey(left.get(i)+"-"+left.get(0))){
                        //test
                        System.out.println("M:(L) (comboSeatStations) list seat name of station "+left.get(i)+" from "+left.get(0)+": " + listSeatBusyInStation.get(left.get(i)+"-"+left.get(0)).toString());
                        //test
                        listSeatNameBusy.addAll(listSeatBusyInStation.get(left.get(i)+"-"+left.get(0)));
                    }
                }
            }

            for (int i = 0; i < right.size(); i++) {
                if(i == 0) continue;
                listSeatNameInCoupleStation = ticketRepo.findSeatNameBusyByIdTripAndIdCoupleStation(idTrip, right.get(0), right.get(i));
                //test
                System.out.println("M:(R) list seat name of station "+right.get(0)+" from "+right.get(i)+": " + listSeatNameInCoupleStation.toString());
                //test
                listSeatNameBusy.addAll(listSeatNameInCoupleStation);
                //check bên comboSeatStations
                if(listSeatBusyInStation != null && listSeatBusyInStation.size() > 0){
                    if(listSeatBusyInStation.containsKey(right.get(0)+"-"+right.get(i))){
                        //test
                        System.out.println("M:(R) (comboSeatStations) list seat name of station "+right.get(0)+" from "+right.get(i)+": " + listSeatBusyInStation.get(right.get(0)+"-"+right.get(i)).toString());
                        //test
                        listSeatNameBusy.addAll(listSeatBusyInStation.get(right.get(0)+"-"+right.get(i)));
                    }
                }
            }
            return listSeatNameInCoupleStation;
        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        String date = "22-06-2001 17:00:00";
        String time = "23:00:00";
        String date2 = "22-06-2001 23:00:00";
        System.out.println("date change: " + date.replace(date.substring(11), time));
        if(!date.replace(date.substring(11), time).equals(date2)){
            System.out.println("khác time nhau");
        }else{
            System.out.println("giống time nhau");
        }
    }

    @Override
    public ResponseObject<?> getDetailAndBookingStatusCHECKIN(int idTrip) {
        try {
            Trip trip = tripRepo.findById(idTrip).get();
            if (trip == null) {
                return ResponseObject.builder().status(false).message("Khong tim thay").build();
            }

//            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            TripDTO dto = modelMapper.map(trip, TripDTO.class);
            //Get Route ra
            RouteDTOviewInTrip routeDTOview = modelMapper.map((RouteDTOview) routeService.getDetail(trip.getRoute().getIdRoute()).getData(), RouteDTOviewInTrip.class);
            for (StationInRouteDTOview item : routeDTOview.getListStationInRoute()) {
//                    stationTimeCome = (StationTimeCome) stationTimeComeService.getDetail(new StationTimeComeKey(item.getIdStationInRoute(), b.getIdTrip())).getData();
                item.setTimeCome(String.valueOf(stationTimeComeRepo.findByIdStationTimeCome(item.getIdStationInRoute(), idTrip)).substring(0,5));
            }
            for (TicketTypeDTOview item : routeDTOview.getListTicketType()) {
                try {
                    item.setPriceInTrip(ticketTypeInTripRepo.findById(new TicketTypeInTripKey(item.getIdTicketType(), idTrip)).get().getPrice());
                }catch (Exception e){ //for case trip don't use all ticket type, -1 is present for this case
                    item.setPriceInTrip(-1);
                }}
            dto.setRoute(routeDTOview);
            //Get Driver ra
            UserSystemDTOviewInTrip driverDTOview = modelMapper.map(trip.getDriver(), UserSystemDTOviewInTrip.class);
            driverDTOview.setBirthdayLong(trip.getDriver().getBirthday().getTime() / 1000);
            dto.setDriver(driverDTOview);
            //Get Bus ra
            VehicleDTOviewInTrip vehicleDTOview = modelMapper.map((VehicleDTOview) vehicleService.getDetail(trip.getVehicle().getIdBus()).getData(), VehicleDTOviewInTrip.class);
            dto.setVehicle(vehicleDTOview);
            //Get Staff ra
            UserSystemDTOviewInTrip staffDTOview = modelMapper.map(trip.getStaff(), UserSystemDTOviewInTrip.class);
            staffDTOview.setBirthdayLong(trip.getStaff().getBirthday().getTime() / 1000);
            dto.setStaff(staffDTOview);

//            dto.setDepartureDateStr(sdf1.format(trip.getDepartureDate()));
//            dto.setEndDateStr(sdf1.format(trip.getEndDate()));
            dto.setDepartureDateLT(trip.getDepartureDate().getTime() / 1000);
            dto.setEndDateLT(trip.getEndDate().getTime() / 1000);

            dto.setTickets(new ArrayList<>());
            TicketDTOviewInTrip ticketDTOview = null;
            for (Ticket ticketTrip: trip.getTickets()) {
                ticketDTOview = modelMapper.map(ticketTrip, TicketDTOviewInTrip.class);
                if(ticketTrip.getBooking().getCustomer().getUserName().equals(USERNAME_GUEST)){
                    ticketDTOview.setFullName(ticketTrip.getBooking().getPaymentTransaction().getNameGuest());
                    ticketDTOview.setEmail(ticketTrip.getBooking().getPaymentTransaction().getEmailGuest());
                    ticketDTOview.setPhone(ticketTrip.getBooking().getPaymentTransaction().getPhoneGuest());
                }else{
                    ticketDTOview.setFullName(ticketTrip.getBooking().getCustomer().getFullName());
                    ticketDTOview.setEmail(ticketTrip.getBooking().getCustomer().getEmail());
                    ticketDTOview.setPhone(ticketTrip.getBooking().getCustomer().getPhone());
                }
                dto.getTickets().add(ticketDTOview);
            }

            return ResponseObject.builder().status(true).message("get all success")
                    .pageSize(1).pageIndex(1).totalPage(1)
                    .data(dto).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> getHistoryOfDriver(Integer driverId, String status, String startTime, int pageSize, int pageIndex) {
        try {
            String formattedDate = null;
            if (startTime != null) {
                Long timeInMillis = Long.valueOf(startTime);
                java.sql.Date sqlDate = new java.sql.Date(timeInMillis * 1000);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                formattedDate = dateFormat.format(sqlDate);
            }

            List<Trip> trip = null;
            Page<Trip> tripPage = null;
            Pageable pageable = null;
            int totalPage = 0;
            if (pageSize != 0 && pageIndex != 0) {
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
            } else {
                pageIndex = 1;
            }
            if (driverId != null && status != null && startTime != null) {
                tripPage = tripRepo.findByIdDriverAndStatusAndStartTimeAndAdminCheckACCEPT(driverId, List.of(status.split(",")), formattedDate, pageable);
            } else if (driverId != null && startTime != null) {
                tripPage = tripRepo.findByIdDriverAndStartTimeAndAdminCheckACCEPT(driverId, formattedDate, pageable);
            } else if (driverId != null && status != null) {
                tripPage = tripRepo.findByIdDriverAndStatusAndAdminCheckACCEPT(driverId, List.of(status.split(",")), pageable);
            } else if (driverId != null) {
                tripPage = tripRepo.findByIdDriverAndAdminCheckACCEPT(driverId, pageable);
            }

            if (tripPage != null) {
                totalPage = tripPage.getTotalPages();
                trip = tripPage.getContent();
            }
            List<TripDTO> tripDTOS = new ArrayList<>();
//            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            try {
                for (Trip b : trip) {
                    TripDTO dto = modelMapper.map(b, TripDTO.class);
                    //Get Route ra
                    RouteDTOviewInTrip routeDTOview = modelMapper.map((RouteDTOview) routeService.getDetail(b.getRoute().getIdRoute()).getData(), RouteDTOviewInTrip.class);
                    for (StationInRouteDTOview item : routeDTOview.getListStationInRoute()) {
//                    stationTimeCome = (StationTimeCome) stationTimeComeService.getDetail(new StationTimeComeKey(item.getIdStationInRoute(), b.getIdTrip())).getData();
                        item.setTimeCome(String.valueOf(stationTimeComeRepo.findByIdStationTimeCome(item.getIdStationInRoute(), b.getIdTrip())).substring(0,5));
                    }
                    for (TicketTypeDTOview item : routeDTOview.getListTicketType()) {
                        try {
                            item.setPriceInTrip(ticketTypeInTripRepo.findById(new TicketTypeInTripKey(item.getIdTicketType(), b.getIdTrip())).get().getPrice());
                        }catch (Exception e){ //for case trip don't use all ticket type, -1 is present for this case
                            item.setPriceInTrip(-1);
                        }   }
                    dto.setRoute(routeDTOview);
                    //Get Driver ra
                    UserSystemDTOviewInTrip driverDTOview = modelMapper.map(b.getDriver(), UserSystemDTOviewInTrip.class);
                    driverDTOview.setBirthdayLong(b.getDriver().getBirthday().getTime() / 1000);
                    dto.setDriver(driverDTOview);
                    //Get Bus ra
                    VehicleDTOviewInTrip vehicleDTOview = modelMapper.map((VehicleDTOview) vehicleService.getDetail(b.getVehicle().getIdBus()).getData(), VehicleDTOviewInTrip.class);
                    dto.setVehicle(vehicleDTOview);
                    //Get Staff ra
                    UserSystemDTOviewInTrip staffDTOview = modelMapper.map(b.getStaff(), UserSystemDTOviewInTrip.class);
                    staffDTOview.setBirthdayLong(b.getStaff().getBirthday().getTime() / 1000);
                    dto.setStaff(staffDTOview);

//                    dto.setDepartureDateStr(sdf1.format(b.getDepartureDate()));
//                    dto.setEndDateStr(sdf1.format(b.getEndDate()));
                    dto.setDepartureDateLT(b.getDepartureDate().getTime() / 1000);
                    dto.setEndDateLT(b.getEndDate().getTime() / 1000);
                    dto.setTotalCustomer(tripRepo.getTotalCustomer(b.getIdTrip()));
                    dto.setTickets(new ArrayList<>());
                    TicketDTOviewInTrip ticketDTOview = null;
                    for (Ticket ticketTrip: b.getTickets()) {
                        ticketDTOview = modelMapper.map(ticketTrip, TicketDTOviewInTrip.class);
                        if(ticketTrip.getBooking().getCustomer().getUserName().equals(USERNAME_GUEST)){
                            ticketDTOview.setFullName(ticketTrip.getBooking().getPaymentTransaction().getNameGuest());
                            ticketDTOview.setEmail(ticketTrip.getBooking().getPaymentTransaction().getEmailGuest());
                            ticketDTOview.setPhone(ticketTrip.getBooking().getPaymentTransaction().getPhoneGuest());
                        }else{
                            ticketDTOview.setFullName(ticketTrip.getBooking().getCustomer().getFullName());
                            ticketDTOview.setEmail(ticketTrip.getBooking().getCustomer().getEmail());
                            ticketDTOview.setPhone(ticketTrip.getBooking().getCustomer().getPhone());
                        }
                        dto.getTickets().add(ticketDTOview);
                    }

                    tripDTOS.add(dto);
                }
            } catch (Exception e) {
                return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
            }
            return ResponseObject.builder().status(true).message("get all success")
                    .pageSize(tripDTOS.size()).pageIndex(pageIndex).totalPage(totalPage)
                    .data(tripDTOS).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> getTripFinishCancelOfDriver(Integer driverId, int pageSize, int pageIndex) {
        try {
            List<Trip> trip = null;
            Page<Trip> tripPage = null;
            Pageable pageable = null;
            int totalPage = 0;
            if (pageSize != 0 && pageIndex != 0) {
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
            } else {
                pageIndex = 1;
            }
            Page<Trip> list = tripRepo.findTripFinishAndCancelOfDriverById(driverId, pageable);
            totalPage = list.getTotalPages();
            trip = list.getContent();

            List<TripDTO> tripDTOS = new ArrayList<>();
//            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            try {
                for (Trip b : trip) {
                    TripDTO dto = modelMapper.map(b, TripDTO.class);
                    //Get Route ra
                    RouteDTOviewInTrip routeDTOview = modelMapper.map((RouteDTOview) routeService.getDetail(b.getRoute().getIdRoute()).getData(), RouteDTOviewInTrip.class);
                    for (StationInRouteDTOview item : routeDTOview.getListStationInRoute()) {
//                    stationTimeCome = (StationTimeCome) stationTimeComeService.getDetail(new StationTimeComeKey(item.getIdStationInRoute(), b.getIdTrip())).getData();
                        item.setTimeCome(String.valueOf(stationTimeComeRepo.findByIdStationTimeCome(item.getIdStationInRoute(), b.getIdTrip())).substring(0,5));
                    }
                    for (TicketTypeDTOview item : routeDTOview.getListTicketType()) {
                        try {
                            item.setPriceInTrip(ticketTypeInTripRepo.findById(new TicketTypeInTripKey(item.getIdTicketType(), b.getIdTrip())).get().getPrice());
                        }catch (Exception e){ //for case trip don't use all ticket type, -1 is present for this case
                            item.setPriceInTrip(-1);
                        }
                    }
                    dto.setRoute(routeDTOview);
                    //Get Driver ra
                    UserSystemDTOviewInTrip driverDTOview = modelMapper.map(b.getDriver(), UserSystemDTOviewInTrip.class);
                    driverDTOview.setBirthdayLong(b.getDriver().getBirthday().getTime() / 1000);
                    dto.setDriver(driverDTOview);
                    //Get Bus ra
                    VehicleDTOviewInTrip vehicleDTOview = modelMapper.map((VehicleDTOview) vehicleService.getDetail(b.getVehicle().getIdBus()).getData(), VehicleDTOviewInTrip.class);
                    dto.setVehicle(vehicleDTOview);
                    //Get Staff ra
                    UserSystemDTOviewInTrip staffDTOview = modelMapper.map(b.getStaff(), UserSystemDTOviewInTrip.class);
                    staffDTOview.setBirthdayLong(b.getStaff().getBirthday().getTime() / 1000);
                    dto.setStaff(staffDTOview);

//                    dto.setDepartureDateStr(sdf1.format(b.getDepartureDate()));
//                    dto.setEndDateStr(sdf1.format(b.getEndDate()));
                    dto.setDepartureDateLT(b.getDepartureDate().getTime() / 1000);
                    dto.setEndDateLT(b.getEndDate().getTime() / 1000);
                    dto.setTotalCustomer(tripRepo.getTotalCustomer(b.getIdTrip()));
                    dto.setTickets(new ArrayList<>());
                    TicketDTOviewInTrip ticketDTOview = null;
                    for (Ticket ticketTrip: b.getTickets()) {
                        ticketDTOview = modelMapper.map(ticketTrip, TicketDTOviewInTrip.class);
                        if(ticketTrip.getBooking().getCustomer().getUserName().equals(USERNAME_GUEST)){
                            ticketDTOview.setFullName(ticketTrip.getBooking().getPaymentTransaction().getNameGuest());
                            ticketDTOview.setEmail(ticketTrip.getBooking().getPaymentTransaction().getEmailGuest());
                            ticketDTOview.setPhone(ticketTrip.getBooking().getPaymentTransaction().getPhoneGuest());
                        }else{
                            ticketDTOview.setFullName(ticketTrip.getBooking().getCustomer().getFullName());
                            ticketDTOview.setEmail(ticketTrip.getBooking().getCustomer().getEmail());
                            ticketDTOview.setPhone(ticketTrip.getBooking().getCustomer().getPhone());
                        }
                        dto.getTickets().add(ticketDTOview);
                    }

                    tripDTOS.add(dto);
                }
            } catch (Exception e) {
                return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
            }
            return ResponseObject.builder().status(true).message("get all success")
                    .pageSize(tripDTOS.size()).pageIndex(pageIndex).totalPage(totalPage)
                    .data(tripDTOS).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> getTripReadyOfDriver(Integer driverId, int pageSize, int pageIndex) {
        try {
            List<Trip> trip = null;
            Page<Trip> tripPage = null;
            Pageable pageable = null;
            int totalPage = 0;
            if (pageSize != 0 && pageIndex != 0) {
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
            } else {
                pageIndex = 1;
            }
            Page<Trip> list = tripRepo.findTripReadyOfDriverById(driverId, pageable);
            totalPage = list.getTotalPages();
            trip = list.getContent();

            List<TripDTO> tripDTOS = new ArrayList<>();
//            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            try {
                for (Trip b : trip) {
                    TripDTO dto = modelMapper.map(b, TripDTO.class);
                    //Get Route ra
                    RouteDTOviewInTrip routeDTOview = modelMapper.map((RouteDTOview) routeService.getDetail(b.getRoute().getIdRoute()).getData(), RouteDTOviewInTrip.class);
                    for (StationInRouteDTOview item : routeDTOview.getListStationInRoute()) {
//                    stationTimeCome = (StationTimeCome) stationTimeComeService.getDetail(new StationTimeComeKey(item.getIdStationInRoute(), b.getIdTrip())).getData();
                        item.setTimeCome(String.valueOf(stationTimeComeRepo.findByIdStationTimeCome(item.getIdStationInRoute(), b.getIdTrip())).substring(0,5));
                    }
                    for (TicketTypeDTOview item : routeDTOview.getListTicketType()) {
                        try {
                            item.setPriceInTrip(ticketTypeInTripRepo.findById(new TicketTypeInTripKey(item.getIdTicketType(), b.getIdTrip())).get().getPrice());
                        }catch (Exception e){ //for case trip don't use all ticket type, -1 is present for this case
                            item.setPriceInTrip(-1);
                        }
                    }
                    dto.setRoute(routeDTOview);
                    //Get Driver ra
                    UserSystemDTOviewInTrip driverDTOview = modelMapper.map(b.getDriver(), UserSystemDTOviewInTrip.class);
                    driverDTOview.setBirthdayLong(b.getDriver().getBirthday().getTime() / 1000);
                    dto.setDriver(driverDTOview);
                    //Get Bus ra
                    VehicleDTOviewInTrip vehicleDTOview = modelMapper.map((VehicleDTOview) vehicleService.getDetail(b.getVehicle().getIdBus()).getData(), VehicleDTOviewInTrip.class);
                    dto.setVehicle(vehicleDTOview);
                    //Get Staff ra
                    UserSystemDTOviewInTrip staffDTOview = modelMapper.map(b.getStaff(), UserSystemDTOviewInTrip.class);
                    staffDTOview.setBirthdayLong(b.getStaff().getBirthday().getTime() / 1000);
                    dto.setStaff(staffDTOview);

//                    dto.setDepartureDateStr(sdf1.format(b.getDepartureDate()));
//                    dto.setEndDateStr(sdf1.format(b.getEndDate()));
                    dto.setDepartureDateLT(b.getDepartureDate().getTime() / 1000);
                    dto.setEndDateLT(b.getEndDate().getTime() / 1000);
                    dto.setTotalCustomer(tripRepo.getTotalCustomer(b.getIdTrip()));
                    dto.setTickets(new ArrayList<>());
                    TicketDTOviewInTrip ticketDTOview = null;
                    for (Ticket ticketTrip: b.getTickets()) {
                        ticketDTOview = modelMapper.map(ticketTrip, TicketDTOviewInTrip.class);
                        if(ticketTrip.getBooking().getCustomer().getUserName().equals(USERNAME_GUEST)){
                            ticketDTOview.setFullName(ticketTrip.getBooking().getPaymentTransaction().getNameGuest());
                            ticketDTOview.setEmail(ticketTrip.getBooking().getPaymentTransaction().getEmailGuest());
                            ticketDTOview.setPhone(ticketTrip.getBooking().getPaymentTransaction().getPhoneGuest());
                        }else{
                            ticketDTOview.setFullName(ticketTrip.getBooking().getCustomer().getFullName());
                            ticketDTOview.setEmail(ticketTrip.getBooking().getCustomer().getEmail());
                            ticketDTOview.setPhone(ticketTrip.getBooking().getCustomer().getPhone());
                        }
                        dto.getTickets().add(ticketDTOview);
                    }

                    tripDTOS.add(dto);
                }
            } catch (Exception e) {
                return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
            }
            return ResponseObject.builder().status(true).message("get all success")
                    .pageSize(tripDTOS.size()).pageIndex(pageIndex).totalPage(totalPage)
                    .data(tripDTOS).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> getHistoryOfStaff(Integer idStaff, String adminCheck, int pageSize, int pageIndex) {
        try {
            List<Trip> trip = null;
            Page<Trip> tripPage = null;
            Pageable pageable = null;
            int totalPage = 0;
            if (pageSize != 0 && pageIndex != 0) {
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
            } else {
                pageIndex = 1;
            }
            if (adminCheck == null) {
                tripPage = tripRepo.findByIdStaff(idStaff, pageable);
            } else {
                //check format adminCheck
                List<String> list = new ArrayList<>(Arrays.asList(AdminCheck.ACCEPTED.name(), AdminCheck.PENDING.name(), AdminCheck.CANCELED.name()));
                if (!list.contains(adminCheck)) {
                    return ResponseObject.builder().status(false).message("invalid adminCheck, must be one of ACCEPT, PENDING, CANCEL").build();
                }
                tripPage = tripRepo.findByIdStaffAndAdminCheck(idStaff, adminCheck, pageable);
            }
            if (tripPage != null) {
                trip = tripPage.getContent();
                totalPage = tripPage.getTotalPages();
            }
            if (trip == null) {
                return ResponseObject.builder().status(false).message("Not found").build();
            }

            List<TripDTO> tripDTOS = new ArrayList<>();
//            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            try {
                for (Trip b : trip) {
                    TripDTO dto = modelMapper.map(b, TripDTO.class);
                    //Get Route ra
                    RouteDTOviewInTrip routeDTOview = modelMapper.map((RouteDTOview) routeService.getDetail(b.getRoute().getIdRoute()).getData(), RouteDTOviewInTrip.class);
                    for (StationInRouteDTOview item : routeDTOview.getListStationInRoute()) {
//                    stationTimeCome = (StationTimeCome) stationTimeComeService.getDetail(new StationTimeComeKey(item.getIdStationInRoute(), b.getIdTrip())).getData();
                        item.setTimeCome(String.valueOf(stationTimeComeRepo.findByIdStationTimeCome(item.getIdStationInRoute(), b.getIdTrip())));
                    }
                    for (TicketTypeDTOview item : routeDTOview.getListTicketType()) {
                        try {
                            item.setPriceInTrip(ticketTypeInTripRepo.findById(new TicketTypeInTripKey(item.getIdTicketType(), b.getIdTrip())).get().getPrice());
                        }catch (Exception e){ //for case trip don't use all ticket type, -1 is present for this case
                            item.setPriceInTrip(-1);
                        }
                    }
                    dto.setRoute(routeDTOview);
                    //Get Driver ra
                    UserSystemDTOviewInTrip driverDTOview = modelMapper.map(b.getDriver(), UserSystemDTOviewInTrip.class);
                    driverDTOview.setBirthdayLong(b.getDriver().getBirthday().getTime() / 1000);
                    dto.setDriver(driverDTOview);
                    //Get Bus ra
                    VehicleDTOviewInTrip vehicleDTOview = modelMapper.map((VehicleDTOview) vehicleService.getDetail(b.getVehicle().getIdBus()).getData(), VehicleDTOviewInTrip.class);
                    dto.setVehicle(vehicleDTOview);
                    //Get Staff ra
                    UserSystemDTOviewInTrip staffDTOview = modelMapper.map(b.getStaff(), UserSystemDTOviewInTrip.class);
                    staffDTOview.setBirthdayLong(b.getStaff().getBirthday().getTime() / 1000);
                    dto.setStaff(staffDTOview);

//                    dto.setDepartureDateStr(sdf1.format(b.getDepartureDate()));
//                    dto.setEndDateStr(sdf1.format(b.getEndDate()));
                    dto.setDepartureDateLT(b.getDepartureDate().getTime() / 1000);
                    dto.setEndDateLT(b.getEndDate().getTime() / 1000);
                    dto.setTotalCustomer(tripRepo.getTotalCustomer(b.getIdTrip()));
                    dto.setTickets(new ArrayList<>());
                    TicketDTOviewInTrip ticketDTOview = null;
                    for (Ticket ticketTrip: b.getTickets()) {
                        ticketDTOview = modelMapper.map(ticketTrip, TicketDTOviewInTrip.class);
                        if(ticketTrip.getBooking().getCustomer().getUserName().equals(USERNAME_GUEST)){
                            ticketDTOview.setFullName(ticketTrip.getBooking().getPaymentTransaction().getNameGuest());
                            ticketDTOview.setEmail(ticketTrip.getBooking().getPaymentTransaction().getEmailGuest());
                            ticketDTOview.setPhone(ticketTrip.getBooking().getPaymentTransaction().getPhoneGuest());
                        }else{
                            ticketDTOview.setFullName(ticketTrip.getBooking().getCustomer().getFullName());
                            ticketDTOview.setEmail(ticketTrip.getBooking().getCustomer().getEmail());
                            ticketDTOview.setPhone(ticketTrip.getBooking().getCustomer().getPhone());
                        }
                        dto.getTickets().add(ticketDTOview);
                    }

                    tripDTOS.add(dto);
                }
            } catch (Exception e) {
                return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
            }
            return ResponseObject.builder().status(true).message("get all success")
                    .pageSize(tripDTOS.size()).pageIndex(pageIndex).totalPage(totalPage)
                    .data(tripDTOS).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseObject<?> create(TripDTOcreate b) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            //tạo obj trip
            ResponseObject<?> rs = createTripObject(b, dateFormat);
            if (!rs.isStatus()) {
                throw new Exception(rs.getMessage());
            }
            Trip trip = (Trip) rs.getData();
            //mã repeat cycle
            String repeatCycle = trip.getIdTrip() + "";
            //check nếu có schedule thì gán mã repeatCycle vào trip
            if (b.getListSchedule() != null) {
                if (b.getListSchedule().size() > 0) {
                    trip.setRepeatCycle(repeatCycle);
                }
            }
            //save trip
            tripRepo.save(trip);

            //tạo ticket type in trip
            rs = null;
            rs = createTicketTypeInTrip(b.getListTicketTypeInTrip(), trip);
            if (!rs.isStatus()) {
                throw new Exception(rs.getMessage());
            }

            //check station timecome 2 trạm đầu cuối
            if(!b.getDepartureDate().replace(b.getDepartureDate().substring(11), b.getListStationTimeCome().get(0).getTimeComes()).equals(b.getDepartureDate())){
                throw new Exception("thời gian trạm bắt đầu phải giống với thời gian xuất phát");
            }
            if(!b.getEndDate().replace(b.getEndDate().substring(11), b.getListStationTimeCome().get(b.getListStationTimeCome().size() - 1).getTimeComes()).equals(b.getEndDate())){
                throw new Exception("thời gian trạm kết thúc phải giống với thời gian kết thúc");
            }

            //save station timecome
            dateFormat = new SimpleDateFormat("HH:mm:ss");
            for (StationTimeComeDTOcreateInTrip item: b.getListStationTimeCome()) {
                rs = stationTimeComeService.create(new StationTimeComeDTOcreate(item.getIdStationInRoute(), trip.getIdTrip(), new Time(dateFormat.parse(item.getTimeComes()).getTime())));
                if (!rs.isStatus()) {
                    throw new Exception(rs.getMessage());
                }
            }
            dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); //reset dateformat về như cũ

            //test
            System.out.println("==> end create trip with date run " + trip.getDepartureDate() + " - " + trip.getEndDate());
            //test

            if (b.getListSchedule() != null) {
                if (b.getListSchedule().size() > 0) {
                    //test
                    System.out.println("có lặp lịch");
                    //test
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
                    Date startTime = dateFormat2.parse(b.getDepartureDate().substring(0, 10));  //của trip f0
                    Date endTime = dateFormat2.parse(b.getEndDate().substring(0, 10));          //của trip f0
                    int daysBetween = (int) ((endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24));
                    //test
                    System.out.println("start time: " + startTime + " - end time: " + endTime + " - days between: " + daysBetween);
                    //test

                    String StartTimeF1 = "";
                    String EndTimeF1 = "";
                    Calendar cal = Calendar.getInstance();
                    for (String dateSchedule : b.getListSchedule()) { //dd-MM-yyyy
                        //sửa date của trip theo shedule
                        StartTimeF1 = b.getDepartureDate().replaceFirst(b.getDepartureDate().substring(0, 10), dateSchedule);
                        cal.setTime(dateFormat.parse(StartTimeF1));
                        cal.add(Calendar.DATE, daysBetween);
                        EndTimeF1 = b.getEndDate().replaceFirst(b.getEndDate().substring(0, 10), dateFormat.format(cal.getTime()).substring(0, 10));
                        //test
                        System.out.println("start time f1: " + StartTimeF1 + " - end time f1: " + EndTimeF1);
                        //test
                        b.setDepartureDate(StartTimeF1);
                        b.setEndDate(EndTimeF1);

                        //tạo obj trip
                        rs = createTripObject(b, dateFormat);
                        if (!rs.isStatus()) {
                            throw new Exception(rs.getMessage());
                        }
                        trip = (Trip) rs.getData();
                        trip.setRepeatCycle(repeatCycle);
                        //save trip
                        tripRepo.save(trip);

                        //tạo ticket type in trip
                        rs = null;
                        rs = createTicketTypeInTrip(b.getListTicketTypeInTrip(), trip);
                        if (!rs.isStatus()) {
                            throw new Exception(rs.getMessage());
                        }

                        //save station timecome
                        dateFormat = new SimpleDateFormat("HH:mm:ss");
                        for (StationTimeComeDTOcreateInTrip item: b.getListStationTimeCome()) {
                            rs = stationTimeComeService.create(new StationTimeComeDTOcreate(item.getIdStationInRoute(), trip.getIdTrip(), new Time(dateFormat.parse(item.getTimeComes()).getTime())));
                            if (!rs.isStatus()) {
                                throw new Exception(rs.getMessage());
                            }
                        }
                        dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); //reset dateformat về như cũ

                        //test
                        System.out.println("==> end create trip with date run " + trip.getDepartureDate() + " - " + trip.getEndDate());
                        //test
                    }
                }
            }

            return ResponseObject.builder().status(true).message("Tạo thành công").build();
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return ResponseObject.builder().status(false).message("Có lỗi xảy ra...").data(messageError).build();
        }
    }

    @Override
    public ResponseObject<?> update(TripDTOupdateDriverAndBus b) {
        try {
            Trip trip = tripRepo.findById(b.getIdTrip()).get();
            if (trip == null) {
                return ResponseObject.builder().status(false).message("Khong tim thay").build();
            }
            Vehicle vehicle = vehicleRepo.findById(b.getIdBus()).orElse(null);
            if (vehicle == null) {
                return ResponseObject.builder().status(false).message("Chưa có xe nào").build();
            } else if (vehicle.getStatus().equals(Status.DEACTIVE.name())) {
                return ResponseObject.builder().status(false).message("Xe hiện tại ngưng hoạt động").build();
            }
            UserSystem driver = userSystemRepo.findByIdDriver(b.getIdDriver());
            if (driver == null) {
                return ResponseObject.builder().status(false).message("Chưa có tài xế nào.").build();
            } else if (driver.getStatus().equals(Status.DEACTIVE.name())) {
                return ResponseObject.builder().status(false).message("Tài xế hiện tại ngưng làm việc.").build();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dayRun = dateFormat.format(trip.getDepartureDate());

            List<Trip> overlappingTrips = tripRepo.findByBusAndDriver(b.getIdBus(), b.getIdDriver(), dayRun.substring(0, 7) + "%");
            if (!overlappingTrips.isEmpty()) {
                for (Trip item : overlappingTrips) {
                    Date departureDateOverlap = item.getDepartureDate();
                    Date endDateOverlap = item.getEndDate();
                    if (isTimeOverlap(trip.getDepartureDate(), trip.getEndDate(), departureDateOverlap, endDateOverlap)) {
                        // Nếu có chuyến đi nào nằm trong khung giờ và ngày
                        // thì trip không khả dụng cho chuyến đi mới
                        return ResponseObject.builder()
                                .status(false)
                                .message("Các chuyến đi đã bị chồng chéo. Nên tạo cách 1 tiếng. " +
                                        "(" + dateFormat.format(trip.getDepartureDate()) + " - " + dateFormat.format(trip.getEndDate())
                                        + " với "
                                        + dateFormat.format(departureDateOverlap) + " - " + dateFormat.format(endDateOverlap) + ")") //Overlapping trips found for Bus or Driver. Trips with this bus are created 60 minutes after the end of that bus trip
                                .build();
                    }
                }
            }
            trip.setVehicle(vehicle);
            trip.setDriver(driver);
            tripRepo.save(trip);
            return ResponseObject.builder().status(true).message("Cap nhap thanh cong").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> startTrip(int idTrip) {
        try {
            Trip trip = tripRepo.findById(idTrip).orElse(null);
            if (trip == null) {
                return ResponseObject.builder().status(false).message("Not found").build();
            }
            if (trip.getAdminCheck().equals(AdminCheck.PENDING.name())) {
                return ResponseObject.builder().status(false).message("Không thể bắt đầu vì chuyến này chưa được DUYỆT").build();
            }
            if (!trip.getStatus().equals(StatusTrip.READY.name())) {
                return ResponseObject.builder().status(false).message("Không thể bắt đầu vì chuyến này chưa SẴN SÀNG").build();
            }
            trip.setStatus(StatusTrip.RUNNING.name());

            for (Ticket ticketItem : trip.getTickets()) {
                if(ticketItem.getStatus().equals(TicketStatus.PAID.name())){
                    ticketItem.setStatus(TicketStatus.NOT_CHECKIN.name());
                }
            }
            tripRepo.save(trip);
            return ResponseObject.builder().status(true).message("update success").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> updateConfirmByDriver(TripConfirm b) {
        try {
            //check xem id có obj ko
            Trip check = tripRepo.findById(b.getIdTrip()).orElse(null);
            if (check == null) {
                return ResponseObject.builder().status(false).message("Not found").build();
            } else if (check.getStatus().equals(StatusTrip.FINISHED.name())) {
                return ResponseObject.builder().status(false).message("không thể cập nhật vì trip đã HOÀN THÀNH.").build();
            } else if (check.getStatus().equals(StatusTrip.CANCELED.name())) {
                return ResponseObject.builder().status(false).message("không thể cập nhật vì trip đã CANCELED.").build();
            } else if (!check.getAdminCheck().equals(AdminCheck.ACCEPTED.name())) {
                return ResponseObject.builder().status(false).message("không thể cập nhật vì trip chữa được DUYỆT.").build();
            }
            check.setStatus(StatusTrip.FINISHED.name());

            for (Ticket ticketItem : check.getTickets()) {
                if (ticketItem.getStatus().equals(TicketStatus.CHECK_IN.name())) {
                    ticketItem.setStatus(TicketStatus.FINISHED.name());
                }
                if (ticketItem.getStatus().equals(TicketStatus.NOT_CHECKIN.name())) {
                    ticketItem.setStatus(TicketStatus.NO_SHOW.name());
                }
            }
            tripRepo.save(check);

            return ResponseObject.builder().status(true).message("finish trip success").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> updateAcceptTrip(TripAccept b) {
        try {
            //check xem id có obj ko
            Trip check = tripRepo.findById(b.getIdTrip()).orElse(null);
            if (check == null) {
                return ResponseObject.builder().status(false).message("Not found").build();
            }
            check.setAdminCheck(b.getAdminCheck());

            tripRepo.save(check);

            //neu61 có recycle peat thì duyệt hết đống đó nốt
            if(b.getAdminCheck().equals(AdminCheck.ACCEPTED.name())) {
                if(b.getListIdTripSchedule() != null && b.getListIdTripSchedule().size() > 0) {
                    if (check.getRepeatCycle() != null) {
                        List<Trip> listSchedule = tripRepo.findByRepeatCycle(check.getRepeatCycle());
                        for (Trip trip : listSchedule) {
                            if(b.getListIdTripSchedule().contains(trip.getIdTrip())) {
                                if (trip.getStatus().equals(StatusTrip.READY.name()) && trip.getAdminCheck().equals(AdminCheck.PENDING.name())) {
                                    trip.setAdminCheck(AdminCheck.ACCEPTED.name());
                                }
                            }else{
                                if(trip.getIdTrip() != b.getIdTrip()){
                                    trip.setAdminCheck(AdminCheck.CANCELED.name());
                                    trip.setStatus(StatusTrip.CANCELED.name());
                                }
                            }
                            tripRepo.save(trip);
                        }
                    }
                }
            }
            return ResponseObject.builder().status(true).message("update success").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("error " + ex.getMessage()).build();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseObject<?> cancelTrip(int idTrip) throws Exception {
        try {
            Trip trip = tripRepo.findById(idTrip).orElse(null);
            if (trip == null) {
                return ResponseObject.builder().status(false).message("Not found").build();
            }
            //check nêu status = RUN, FINISH thì ko đc CANCEL
            if (trip.getStatus().equals(StatusTrip.RUNNING.toString()) || trip.getStatus().equals(StatusTrip.FINISHED.toString())) {
                return ResponseObject.builder().status(false).message("No cancel because trip status is " + trip.getStatus()).build();
            }
            //update status
            trip.setStatus(StatusTrip.CANCELED.toString());
            if (trip.getAdminCheck().equals(AdminCheck.PENDING.toString())) {
                trip.setAdminCheck(AdminCheck.CANCELED.toString());
            }
            trip.setUpdatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            tripRepo.save(trip);
            //test
            System.out.println("update status trip success");
            //test

            //lấy list booking của trip ra để hoàn tiền + noti + lưu noti vào db
            Wallet wallet = null;
            ResponseObject<?> rs1 = null;
            SimpleDateFormat formatter = null;
            RouteDTOview routeDTOview = (RouteDTOview) routeService.getDetail(trip.getRoute().getIdRoute()).getData();
            for (Booking booking : trip.getBookings()) {
                if (booking.getCustomer() == null) {  //khách mua là khách vãn lai thì send email thông báo
                    formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    if (booking.getPaymentTransaction().getEmailGuest() != null) {
                        mailService.sendMailCancelTrip(booking.getPaymentTransaction().getEmailGuest(), routeDTOview.getDeparturePoint(), routeDTOview.getDestination(), formatter.format(trip.getUpdatedDate()));
                    }
                } else { //khách mua là khách hàng trong APP
                    //hoàn tiền cho customer
                    wallet = walletRepo.findByIdCustomer(booking.getCustomer().getIdUserSystem());
                    wallet.setBalance((int) (wallet.getBalance() + booking.getTotalPrice()));
                    walletRepo.save(wallet);
                    //tạo transaction
                    transactionService.create("Hoàn tiền vì hủy chuyến: " + routeDTOview.getDeparturePoint() + " -> " + routeDTOview.getDestination(), (int) booking.getTotalPrice(), wallet.getIdWallet(), null);

                    //noti máy khách
                    //noti cho user đó bik (firebase)
                    rs1 = notiUserSystemWithContent(Collections.singletonList(booking.getCustomer().getIdUserSystem()), "Hủy chuyến xe", "Bạn ơi,chuyến xe của bạn đã bị hủy. Rất xin lỗi bạn vì sự cố này");
                    if (!rs1.isStatus()) {
                        System.out.println("Something wrong, noti Trip cancel failed to User " + rs1.getMessage());
                    }
                    //lưu noti
                    notificationService.create(new NotificationDTOcreate(booking.getCustomer().getIdUserSystem(), "Hủy chuyến: " + routeDTOview.getDeparturePoint() + " -> " + routeDTOview.getDestination() + " (hoàn tiền), Xin lỗi bạn vì sự bất tiện này"));
                }
            }

            //chang all tickets status of trip to CANCEL
            for (Ticket item : trip.getTickets()) {
                item.setStatus(TicketStatus.CANCELED.name());
                ticketRepo.save(item);
            }
            return ResponseObject.builder().status(true).message("update success").build();
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
//            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    public ResponseObject<?> notiUserSystemWithContent(List<Integer> listIdCustomer, String title, String body) {
        try {
            if (listIdCustomer.size() > 0) {
                UserSystemDTOview userSystem = null;
                NotificationMessage notificationMessage = null;
                ResponseObject<?> rs = null;

                for (Integer idCustomer : listIdCustomer) {
                    userSystem = (UserSystemDTOview) userSystemService.getDetail(idCustomer).getData();
                    Map<String, String> data = new HashMap<>();
                    data.put("Happy day", "To You");

                    if (userSystem != null) {
                        if (userSystem.getFcmTokenDevide() != null) {
                            notificationMessage = new NotificationMessage();
                            notificationMessage.setRecipientToken(userSystem.getFcmTokenDevide());
                            notificationMessage.setTitle(title);
                            notificationMessage.setBody(body);
                            notificationMessage.setImage(env.getProperty("logobtb_img"));
                            notificationMessage.setData(data);
                            rs = fireBaseNotificationMessagingServiceImpl.sendNotification(notificationMessage);
                            if (!rs.isStatus()) {
                                System.out.println("Lỗi khi noti thông báo cho khách có id = " + idCustomer);
                            }
                        }
                        //lưu noti vào DB
                        rs = notificationService.create(new NotificationDTOcreate(idCustomer, body));
                        if (!rs.isStatus()) {
                            System.out.println("Lỗi khi Lưu noti vào DB cho khách có id = " + idCustomer);
                        }
                    }
                }
            }
            return ResponseObject.builder().status(true).message("success").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject<?> delete(int id) throws Exception {
        try {
            Trip trip = tripRepo.findById(id).orElse(null);
            if (trip == null) {
                return ResponseObject.builder().status(false).message("Khong tim thay").build();
            }
            //test
            System.out.println("tìm thấy trip");
            //test
            tripRepo.delete(trip);
            //test
            System.out.println("xóa trip");
            //test
            return ResponseObject.builder().status(true).message("Xóa thanh cong").build();
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public ResponseObject<?> notiDriverAfterTripAcceptedByAdmin(int idTrip) {
        try {
            Trip trip = tripRepo.findById(idTrip).orElse(null);
            if (trip == null) {
                return ResponseObject.builder().status(false).message("Khong tim thay").build();
            }
            ResponseObject rs = notiUserSystemWithContent(Collections.singletonList(trip.getDriver().getIdUserSystem()), "1 chuyến xe mới", "Bác tài ơi, bạn được phân công chuyến xe mới !");
            return ResponseObject.builder().status(rs.isStatus()).message(rs.getMessage()).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    private ResponseObject<?> createTripObject(TripDTOcreate b, SimpleDateFormat dateFormat) throws Exception { //dd-MM-yyyy HH:mm:ss
        try {
            //Validate idRoute - idDriver - idStaff - idVehicle
            Route route = routeRepo.findById(b.getIdRoute()).orElse(null);
            if (route == null) {
                return ResponseObject.builder().status(false).message("Chưa có tuyến đường nào.").build();
            } else if (route.getStatus().equals(Status.DEACTIVE.name())) {
                return ResponseObject.builder().status(false).message("Tuyến đường này hiện tại ngưng hoạt động.").build();
            }
            Vehicle vehicle = vehicleRepo.findById(b.getIdVehicle()).orElse(null);
            if (vehicle == null) {
                return ResponseObject.builder().status(false).message("Chưa có xe nào").build();
            } else if (vehicle.getStatus().equals(Status.DEACTIVE.name())) {
                return ResponseObject.builder().status(false).message("Xe hiện tại ngưng hoạt động").build();
            }
            UserSystem staff = userSystemRepo.findByIdStaff(b.getIdStaff());
            if (staff == null) {
                return ResponseObject.builder().status(false).message("Chưa có nhân viên.").build();
            } else if (staff.getStatus().equals(Status.DEACTIVE.name())) {
                return ResponseObject.builder().status(false).message("Nhân viên hiện tại ngưng làm việc.").build();
            }
            UserSystem driver = userSystemRepo.findByIdDriver(b.getIdDriver());
            if (driver == null) {
                return ResponseObject.builder().status(false).message("Chưa có tài xế nào.").build();
            } else if (driver.getStatus().equals(Status.DEACTIVE.name())) {
                return ResponseObject.builder().status(false).message("Tài xế hiện tại ngưng làm việc.").build();
            }
            //Generate id random
            int id = generateUniqueId();
            while (true) {
                if (!tripRepo.existsById(id)) {  //nếu có đối tượng thì trả về true, ko thì false
                    break;
                }
                id = generateUniqueId();
            }
            Trip trip = modelMapper.map(b, Trip.class);
            trip.setIdTrip(id);
            trip.setRoute(route);
            trip.setVehicle(vehicle);
            trip.setStaff(staff);
            trip.setDriver(driver);
            trip.setCreatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            trip.setUpdatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            trip.setAdminCheck(AdminCheck.PENDING.name());
            trip.setStatus(StatusTrip.READY.name());
            trip.setAvarageStar(0);
            trip.setDepartureDate(dateFormat.parse(b.getDepartureDate()));
            trip.setEndDate(dateFormat.parse(b.getEndDate()));
            trip.setRepeatCycle(null);

            //CHECK ngày và giờ
            if (trip.getDepartureDate() == null || trip.getEndDate() == null) {
                return ResponseObject.builder().status(false).message("Các trường ngày và giờ không được để trống.").build();
            }
            if (trip.getDepartureDate().after(trip.getEndDate())) {
                return ResponseObject.builder().status(false).message("Ngày khởi hành phãi trước ngày kêt thúc.").build();

            }
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //đổi format để query dưới Db
            String dayRun = dateFormat.format(trip.getDepartureDate());
            dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); //trả về format cũ để làm tiếp

            List<Trip> overlappingTrips = tripRepo.findByBusAndDriver(vehicle.getIdBus(), driver.getIdUserSystem(), dayRun.substring(0, 7) + "%");
            if (!overlappingTrips.isEmpty()) {
                for (Trip item : overlappingTrips) {
                    Date departureDateOverlap = item.getDepartureDate();
                    Date endDateOverlap = item.getEndDate();
                    if (isTimeOverlap(trip.getDepartureDate(), trip.getEndDate(), departureDateOverlap, endDateOverlap)) {
                        // Nếu có chuyến đi nào nằm trong khung giờ và ngày
                        // thì trip không khả dụng cho chuyến đi mới
                        return ResponseObject.builder()
                                .status(false)
                                .message("Các chuyến đi đã bị chồng chéo. Nên tạo cách 1 tiếng. " +
                                        "(" + dateFormat.format(trip.getDepartureDate()) + " - " + dateFormat.format(trip.getEndDate())
                                        + " với "
                                        + dateFormat.format(departureDateOverlap) + " - " + dateFormat.format(endDateOverlap) + ")") //Overlapping trips found for Bus or Driver. Trips with this bus are created 60 minutes after the end of that bus trip
                                .build();
                    }
                }
            }
            return ResponseObject.builder().status(true).message("tạo đối tượng trip thành công").data(trip).build();
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private ResponseObject<?> createTicketTypeInTrip(List<TicketTypeInTripDTOcreate> listTicketTypeInTrip, Trip trip) throws Exception {
        try {
            TicketType tmp = null;
            TicketTypeInTrip ticketTypeInTrip = null;
            List<Integer> listIdStationOrder = stationInRouteRepo.findListIdStationAscByIdTrip(trip.getIdTrip());
            boolean checkTicketTypeFullRoute = false;
            for (TicketTypeInTripDTOcreate item : listTicketTypeInTrip) {
                tmp = ticketTypeRepo.findById(item.getIdTicketType()).orElse(null);
                if (tmp == null) {
                    throw new Exception("Không có loại này ticket type này.");
                }
                if(tmp.getEarlyOnStation().getIdStation() == listIdStationOrder.get(0) && tmp.getLateOffStation().getIdStation() == listIdStationOrder.get(listIdStationOrder.size() - 1)) { //check để xem nó có ticket type full hành trình ko,để dành cho vc query tìm loại vé phù hợp cho trip đó
                    checkTicketTypeFullRoute = true;
                }
                ticketTypeInTrip = new TicketTypeInTrip();
                ticketTypeInTrip.setPrice(item.getPrice());
                ticketTypeInTrip.setTicketTypeInTripKey(new TicketTypeInTripKey(item.getIdTicketType(), trip.getIdTrip()));
                ticketTypeInTrip.setTrip(trip);
                ticketTypeInTrip.setTicketType(tmp);
                ticketTypeInTripRepo.save(ticketTypeInTrip);
            }
            if(!checkTicketTypeFullRoute){
                return ResponseObject.builder().status(false).message("Phải có loại vé cả hành trình").build();
            }
            return ResponseObject.builder().status(true).message("Tạo ticket type trong chuyến thành công").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Tạo ticket type trong chuyến thất bại. " + ex.getMessage()).build();
        }
    }


}
