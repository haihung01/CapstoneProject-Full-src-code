package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Route.*;
import com.example.triptix.DTO.Station.StationDTOview;
import com.example.triptix.Enum.Status;
import com.example.triptix.Model.*;

import com.example.triptix.Model.key.StationTimeComeKey;
import com.example.triptix.Repository.*;
import com.example.triptix.Service.ProvinceCityService;
import com.example.triptix.Service.RouteService;

import com.example.triptix.Service.StationTimeComeService;
import com.example.triptix.Util.UTCTimeZoneUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Time;
import java.util.*;

@Service
public class RouteServiceImpl implements RouteService {
    @Autowired
    private RouteRepo routeRepo;

    @Autowired
    private StationRepo stationRepo;

    @Autowired
    private StationInRouteRepo stationInRouteRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserSystemRepo userSystemRepo;

    @Autowired
    private ProvinceCityService provinceCityService;

    @Autowired
    private ProvinceCityRepo provinceCityRepo;

    @Autowired
    private TicketTypeRepo ticketTypeRepo;

    @Autowired
    private StationTimeComeService stationTimeComeService;

    @Override
    public ResponseObject<?> getAll(String codeDepartureDate, String codeEndDate, String name) {
        try{
            List<Route> listRoute;
            if(codeDepartureDate != null && codeEndDate != null && name != null){
                listRoute = routeRepo.findByCodeDepartureAndCodeEndAndName(codeDepartureDate,codeEndDate,name);
            }else if (codeDepartureDate != null && codeEndDate != null) {
                listRoute = routeRepo.findByCodeDepartureAndEndDate(codeDepartureDate, codeEndDate);
            }
            else if (codeDepartureDate != null && name != null) {
                listRoute = routeRepo.findByCodeDepartureAndName(codeDepartureDate, name);
            } else if (codeEndDate != null && name != null) {
                listRoute = routeRepo.findByCodeEndDateAndName(codeEndDate, name);
            } else if (codeDepartureDate != null) {
                listRoute = routeRepo.findByCodeDeparture(codeDepartureDate);
            } else if (codeEndDate != null) {
                listRoute = routeRepo.findByCodeEndDate(codeEndDate);
            } else if (name != null) {
                listRoute = routeRepo.findByName(name);
            } else {
                listRoute = routeRepo.findAllOrderByName();
            }
            if(listRoute == null){
                return ResponseObject.builder().status(true).message("Chưa có một tuyến đường nào hết.").build();
            }
            List<RouteDTOview> listRouteDTOview = new ArrayList<>();
            for(Route route : listRoute){
                RouteDTOview dto = modelMapper.map(route, RouteDTOview.class);
                dto.setIdAdmin(route.getAdmin().getIdUserSystem());
                dto.setIdRoute(route.getIdRoute());
                dto.setDeparturePoint(route.getStartProvinceCity().getName());
                dto.setDestination(route.getEndProvinceCity().getName());

                List<StationInRoute> stationInRoutes = stationInRouteRepo.findByRoute(route.getIdRoute());
                List<StationInRouteDTOview> listStationInRouteDTOview = new ArrayList<>();
                for (StationInRoute stationInRoute : stationInRoutes) {
                    StationInRouteDTOview stationDTOview = modelMapper.map(stationInRoute, StationInRouteDTOview.class);
                    stationDTOview.setIdStation(stationInRoute.getStation().getIdStation());
                    stationDTOview.setIdStationInRoute(stationInRoute.getIdStationInRoute());
                    Station station = stationRepo.findByIdStation(stationDTOview.getIdStation());
                    StationDTOview dtoStation = modelMapper.map(station, StationDTOview.class);
                    stationDTOview.setStation(dtoStation);
                    listStationInRouteDTOview.add(stationDTOview);

                }
                // Đặt danh sách trạm vào dto
                dto.setListStationInRoute(listStationInRouteDTOview);

                //Lấy loại vé ra.
                List<TicketType> ticketTypes = ticketTypeRepo.findByRoute(route.getIdRoute());
                List<TicketTypeDTOview> ticketTypeDTOviewList = new ArrayList<>();

                for (TicketType item : ticketTypes){
                    TicketTypeDTOview ticketTypeDTOview = new TicketTypeDTOview();
                    try {
                        modelMapper.map(item, ticketTypeDTOview);
                        ticketTypeDTOview.setName(item.getName());
                        ticketTypeDTOview.setIdTicketType(item.getIdTicketType());
                        ticketTypeDTOview.setIdRoute(route.getIdRoute());
                        ticketTypeDTOview.setIdEarlyOnStation(item.getEarlyOnStation().getIdStation());
                        ticketTypeDTOview.setIdLateOffStation(item.getLateOffStation().getIdStation());
                        ticketTypeDTOview.setDefaultPrice(item.getDefaultPrice());
                        ticketTypeDTOviewList.add(ticketTypeDTOview);
                    } catch (Exception ex) {
                        // In thông báo lỗi hoặc ghi log để xem vấn đề
                        System.err.println("Error mapping TicketType to TicketTypeDTOview: " + ex.getMessage());
                    }
                }
                dto.setListTicketType(ticketTypeDTOviewList);
                listRouteDTOview.add(dto);
            }
            return ResponseObject.builder().status(true).message("Lấy thông tin các tuyến đường thành công.").data(listRouteDTOview).build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra.").build();
        }

    }

    @Override
    public ResponseObject<?> getDetail(int idRoute) {
        try{
            Route route = routeRepo.findById(idRoute).orElse(null);
            if(route == null){
                return ResponseObject.builder().status(true).message("Không tìm thấy tuyến đường.").build();
            }
            RouteDTOview dto = modelMapper.map(route, RouteDTOview.class);
            dto.setIdAdmin(route.getAdmin().getIdUserSystem());
            dto.setIdRoute(route.getIdRoute());
            dto.setDeparturePoint(route.getStartProvinceCity().getName());
            dto.setDestination(route.getEndProvinceCity().getName());
            List<StationInRoute> stationInRoutes = stationInRouteRepo.findByRoute(route.getIdRoute());
            List<StationInRouteDTOview> listStationInRouteDTOview = new ArrayList<>();
            StationInRouteDTOview stationDTOview = null;
            Station station = null;
            StationDTOview dtoStation = null;
            for (StationInRoute stationInRoute : stationInRoutes) {
                stationDTOview = modelMapper.map(stationInRoute, StationInRouteDTOview.class);
                stationDTOview.setIdStationInRoute(stationInRoute.getIdStationInRoute());
                stationDTOview.setIdStation(stationInRoute.getStation().getIdStation());
                station = stationRepo.findByIdStation(stationDTOview.getIdStation());
                dtoStation = modelMapper.map(station, StationDTOview.class);
                stationDTOview.setStation(dtoStation);
                listStationInRouteDTOview.add(stationDTOview);
                }
            // Đặt danh sách trạm vào dto
            dto.setListStationInRoute(listStationInRouteDTOview);
            //Lấy loại vé ra.
            List<TicketType> ticketTypes = ticketTypeRepo.findByRoute(route.getIdRoute());
            List<TicketTypeDTOview> ticketTypeDTOviewList = new ArrayList<>();

            for (TicketType item : ticketTypes){
                TicketTypeDTOview ticketTypeDTOview = new TicketTypeDTOview();
                try {
                    modelMapper.map(item, ticketTypeDTOview);
                    ticketTypeDTOview.setIdTicketType(item.getIdTicketType());
                    ticketTypeDTOview.setIdRoute(route.getIdRoute());
                    ticketTypeDTOview.setIdEarlyOnStation(item.getEarlyOnStation().getIdStation());
                    ticketTypeDTOview.setIdLateOffStation(item.getLateOffStation().getIdStation());
                    ticketTypeDTOview.setDefaultPrice(item.getDefaultPrice());
                    ticketTypeDTOviewList.add(ticketTypeDTOview);
                } catch (Exception ex) {
                    // In thông báo lỗi hoặc ghi log để xem vấn đề
                    System.err.println("Error mapping TicketType to TicketTypeDTOview: " + ex.getMessage());
                }
            }
            dto.setListTicketType(ticketTypeDTOviewList);



            return ResponseObject.builder().status(true).message("Thông tin chi tiết tuyến có id: "+ idRoute+".").data(dto).build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra...").build();
        }
    }


    private int generateUniqueId() {
        return (int) (Math.random() * 100000);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseObject<?> create(RouteDTOcreate b) {
        String messageError = null;
        try {
            int id = generateUniqueId();
            while (true) {
                if (!routeRepo.existsById(id)) {  //nếu có đối tượng thì trả về true, ko thì false
                    break;
                }
                id = generateUniqueId();
            }
            UserSystem userSystem = userSystemRepo.findByRoleAdmin();
            Route route = modelMapper.map(b, Route.class);
            Station station = stationRepo.findByIdStation(b.getListStationInRoute().get(0).getIdStation());
            ProvinceCity startProvince = provinceCityRepo.findByName(station.getProvince());
            //test
            System.out.println("start province: " + startProvince.getIdProvince() + " - " + startProvince.getName());
            //test
            station = null;
            station = stationRepo.findByIdStation(b.getListStationInRoute().get(b.getListStationInRoute().size() - 1).getIdStation());
            ProvinceCity endProvince = provinceCityRepo.findByName(station.getProvince());
            //test
            System.out.println("end province: " + endProvince.getIdProvince() + " - " + endProvince.getName());
            //test
            if (startProvince == null || endProvince == null) {
                return ResponseObject.builder().status(false).message("Code điểm đi và điểm đến không đúng.").build();
            }
            route.setIdRoute(id);
            route.setStartProvinceCity(startProvince);
            route.setEndProvinceCity(endProvince);
            route.setAdmin(userSystem);
            route.setCreatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            route.setUpdatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            route.setStatus(Status.ACTIVE.name());

            routeRepo.save(route);

            List<StationInRouteDTOcreate> listStation = b.getListStationInRoute();
            if (listStation != null && !listStation.isEmpty()) {
                int count = 1;
                Set<Integer> visitedStationIds = new HashSet<>(); // Sử dụng Set để lưu trữ các id đã kiểm tra
                for (StationInRouteDTOcreate dto : listStation) {
                    Integer stationId = dto.getIdStation();
                    // Kiểm tra xem id trạm đã được kiểm tra chưa
                    if (visitedStationIds.contains(stationId)) {
                        messageError = "Trùng lặp trạm " + stationId;
                        throw new Exception();
                    }
                    visitedStationIds.add(stationId);/*
                    StationInRouteKey stationInRouteKey = new StationInRouteKey();
                    stationInRouteKey.setIdRoute(route.getIdRoute());
                    stationInRouteKey.setIdStation(stationId);*/
                    StationInRoute stationInRoute = modelMapper.map(dto, StationInRoute.class);
                    station = stationRepo.findByIdStation(stationId);
                    if (station == null) {
                        messageError = "Không tìm thấy trạm.";
                        throw new Exception();
                    }else if(station.getStatus().equals(Status.DEACTIVE.name())){
                        messageError = "Station "+ station.getName() + "đã ngưng hoạt động";
                        throw new Exception();
                    }
//                    if(count == 1){
//                        if(!station.getProvince().equals(startProvince.getName())){
//                            messageError = "Station đầu tiên "+ station.getName() + " phải nằm trong điểm bắt đầu " + startProvince.getName() + " của tuyến";
//                            throw new Exception();
//                        }
//                    }else  if(count == listStation.size()){
//                        if(!station.getProvince().equals(endProvince.getName())){
//                            messageError = "Station cuối cùng "+ station.getName() + " phải nằm trong điểm kết thúc "+ endProvince.getName() + " của tuyến";
//                            throw new Exception();
//                        }
//                    }
                    /*stationInRoute.set(stationInRouteKey);*/
                    stationInRoute.setOrderInRoute(count);
                    stationInRoute.setDistance(dto.getDistance());
                    stationInRoute.setRoute(route);
                    stationInRoute.setStation(station);

                    stationInRouteRepo.save(stationInRoute);
                    count++;
                }
            }
            List<TicketTypeDTOcreate> listTicketType = b.getListTicketType();
            if (listTicketType != null && !listTicketType.isEmpty()) {
                int diemDau = listStation.get(0).getIdStation();
                int diemCuoi = listStation.get(listStation.size() -1).getIdStation();
                boolean tamThoi = false;
                for(TicketTypeDTOcreate item : listTicketType){
                    int earlyOn = item.getIdEarlyOnStation();
                    int lateOff = item.getIdLateOffStation();
                    // Kiểm tra xem có ít nhất một cặp giống với điểm đầu và cuối không
                    if (diemDau == earlyOn && diemCuoi == lateOff){
                        tamThoi = true;
                        break;
                    }
                }
                if(!tamThoi){
                    messageError = "Không tìm thấy ít nhất loại vé cả hành trình.";
                    throw new Exception();
                }
                for (TicketTypeDTOcreate item : listTicketType) {
                    TicketType ticketType = modelMapper.map(item, TicketType.class);
                    ticketType.setName(item.getName());
                    ticketType.setRoute(route);
                    ticketType.setDefaultPrice(item.getDefaultPrice());
                    //Check điểm lên điểm xuống
                    int earlyOn = item.getIdEarlyOnStation();
                    int lateOff = item.getIdLateOffStation();
                    boolean earlyOnFound = false;
                    boolean lateOffFound = false;
                    int orderInRouteEarlyOn = -1;
                    int orderInRouteLateOff = -1;
                    //nếu điểm lên không có trong listStation in lỗi
                    for (int i = 0; i < listStation.size(); i++) {
                        if (earlyOn == listStation.get(i).getIdStation()) {
                            earlyOnFound = true;
                        }
                        if (lateOff == listStation.get(i).getIdStation()) {
                            lateOffFound = true;
                        }
                    }

                    if (earlyOnFound && lateOffFound) {
                        Station stationStart = stationRepo.findByIdStation(earlyOn);
                        Station stationEnd = stationRepo.findByIdStation(lateOff);
                        StationInRoute stationInRouteStart = stationInRouteRepo.findByIdStationAndRoute(earlyOn,route.getIdRoute());
                        StationInRoute stationInRouteEnd = stationInRouteRepo.findByIdStationAndRoute(lateOff,route.getIdRoute());
                        orderInRouteEarlyOn = stationInRouteStart.getOrderInRoute();
                        orderInRouteLateOff = stationInRouteEnd.getOrderInRoute();
                        if (orderInRouteEarlyOn >= orderInRouteLateOff) {
                            messageError = "Điểm lên và điểm xuống sai thứ tự.";
                            throw new Exception();
                        } else {
                            ticketType.setEarlyOnStation(stationStart);
                            ticketType.setLateOffStation(stationEnd);
                        }
                    } else {
                        messageError = "Không tìm thấy điểm lên hoặc điểm xuống của ticket: " + earlyOn + " - " + lateOff;
                        throw new Exception();
                    }
                    ticketTypeRepo.save(ticketType);
                }

            }

            return ResponseObject.builder().status(true).message("Tạo thành công").build();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // Đánh dấu giao dịch để rollback
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra. " + messageError).build();

        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseObject<?> update(RouteDTOupdate b) {
        //CREATE KHAC UPDATE O CHO UPDATE CAI ticketTypeId
        //Khong gan tickyTypeId thi` mac dinh no tao cai loai ve moi.
        String messageError = null;
        try{
            Route check = routeRepo.findById(b.getIdRoute()).orElse(null);
            if(check == null){
                return ResponseObject.builder().status(false).message("Không tìm thấy tuyến.").build();
            }
            UserSystem userSystem = userSystemRepo.findByRoleAdmin();
            Route route = modelMapper.map(b, Route.class);
            ProvinceCity startProvince = provinceCityRepo.findById(b.getCodeDeparturePoint()).orElse(null);
            ProvinceCity endProvince = provinceCityRepo.findById(b.getCodeDestination()).orElse(null);
            if (startProvince == null || endProvince == null) {
                return ResponseObject.builder().status(false).message("Code điểm đi và điểm đến không đúng.").build();
            }/*else if(startProvince.getType().equals("CITY") || endProvince.getType().equals("CITY")){
                return ResponseObject.builder().status(false).message("Điểm đi và điểm đến phãi bắt đầu từ TỈNH (PROVINCE).").build();
            }*/


            route.setIdRoute(b.getIdRoute());
            route.setAdmin(userSystem);
            route.setStartProvinceCity(startProvince);
            route.setEndProvinceCity(endProvince);
            route.setStatus(b.getStatus());
            route.setCreatedDate(check.getCreatedDate());
            route.setUpdatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            routeRepo.save(route);
            List<StationInRouteDTOupdate> listStation = b.getListStationInRoute();
            System.out.println("1");
            if (listStation != null && !listStation.isEmpty()) {
                Set<Integer> visitedStationIds = new HashSet<>();
                int count = 1;
                for (StationInRouteDTOupdate dto : listStation) {
                    Integer stationId = dto.getIdStation();
                    // Kiểm tra xem id trạm đã được kiểm tra chưa
                    if (visitedStationIds.contains(stationId)) {
                        messageError = "Trùng lặp trạm " + stationId;
                        throw new Exception();
                    }
                    visitedStationIds.add(stationId);
                    StationInRoute stationInRoute = modelMapper.map(dto, StationInRoute.class);
                    Station station = stationRepo.findByIdStation(stationId);
                    if(station == null){
                        return ResponseObject.builder().status(false).message("Không tìm thấy trạm.").build();
                    }else if(station.getStatus().equals(Status.DEACTIVE.name())){
                        messageError = "Station "+ station.getName() + "đã ngưng hoạt động";
                        throw new Exception();
                    }
                    /*stationInRoute.setStationInRouteKey(stationInRouteKey);*/
                    stationInRoute.setOrderInRoute(count);
                    stationInRoute.setDistance(dto.getDistance());
                    stationInRoute.setRoute(route);
                    stationInRoute.setStation(station);
                    stationInRouteRepo.save(stationInRoute);
                    count++;
                }
            }
            List<TicketTypeDTOupdate> listTicketType = b.getListTicketType();
            System.out.println("2");
            if (listTicketType != null && !listTicketType.isEmpty()) {
                for (TicketTypeDTOupdate item : listTicketType) {
                    TicketType ticketType = modelMapper.map(item, TicketType.class);
                    ticketType.setIdTicketType(item.getIdTicketType());
                    ticketType.setName(item.getName());
                    ticketType.setRoute(route);
                    ticketType.setDefaultPrice(item.getDefaultPrice());
                    //Check điểm lên điểm xuống
                    int earlyOn = item.getIdEarlyOnStation();
                    int lateOff = item.getIdLateOffStation();
                    boolean earlyOnFound = false;
                    boolean lateOffFound = false;
                    int orderInRouteEarlyOn = -1;
                    int orderInRouteLateOff = -1;
                    //nếu điểm lên không có trong listStation in lỗi
                    for (int i = 0; i < listStation.size(); i++) {
                        if (earlyOn == listStation.get(i).getIdStation()) {
                            earlyOnFound = true;
                        }
                        if (lateOff == listStation.get(i).getIdStation()) {
                            lateOffFound = true;
                        }
                    }
                    System.out.println("3.1");
                    if (earlyOnFound && lateOffFound) {
                        Station stationStart = stationRepo.findByIdStation(earlyOn);
                        Station stationEnd = stationRepo.findByIdStation(lateOff);
                        System.out.println(earlyOn);
                        System.out.println(route.getIdRoute());
                        StationInRoute stationInRouteStart = stationInRouteRepo.findByIdStationAndIdRoute(earlyOn,route.getIdRoute());
                        StationInRoute stationInRouteEnd = stationInRouteRepo.findByIdStationAndIdRoute(lateOff,route.getIdRoute());
                        orderInRouteEarlyOn = stationInRouteStart.getOrderInRoute();
                        orderInRouteLateOff = stationInRouteEnd.getOrderInRoute();
                        if (orderInRouteEarlyOn >= orderInRouteLateOff) {
                            messageError = "Điểm lên và điểm xuống sai thứ tự.";
                            throw new Exception();
                        } else {
                            ticketType.setEarlyOnStation(stationStart);
                            ticketType.setLateOffStation(stationEnd);
                        }
                    } else {
                        messageError = "Không tìm thấy điểm lên hoặc điểm xuống của ticket: " + earlyOn + " - " + lateOff;
                        throw new Exception();
                    }
                    ticketTypeRepo.save(ticketType);

                }
            }
            return ResponseObject.builder().status(true).message("Cập nhật thành công").build();
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // Đánh dấu giao dịch để rollback
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra...").data(messageError).build();

        }
    }

    @Override
    public ResponseObject<?> delete(int idRoute) {
        try {
            //check xem id có obj ko
            Route check = routeRepo.findById(idRoute).orElse(null);
            if (check == null) {
                return ResponseObject.builder().status(false).message("Không tìm thấy tuyến.").build();
            }
            List<StationInRoute> listStationInRoute = check.getStationInRoutes();
            for(StationInRoute item : listStationInRoute){
                stationInRouteRepo.delete(item);
            }
            List<TicketType> listTicketType = check.getTicketTypes();
            for(TicketType item : listTicketType){
                ticketTypeRepo.delete(item);
            }
            routeRepo.deleteById(idRoute);
            return ResponseObject.builder().status(true).message("Xóa thành công tuyến cũng như xóa các lịch trình trạm.").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra...").build();
        }
    }

    @Override
    public ResponseObject<?> updateStatus(RouteDTOchangeStatus b) {
        try {
            Route check = routeRepo.findById(b.getIdRoute()).orElse(null);
            if (check == null) {
                return ResponseObject.builder().status(false).message("Không tìm thấy tuyến.").build();
            }
            check.setStatus(b.getStatus());
            routeRepo.save(check);
            return ResponseObject.builder().status(true).message("Cập nhật thành công trạng thái.").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra...").data(ex.getMessage()).build();
        }
    }
}
