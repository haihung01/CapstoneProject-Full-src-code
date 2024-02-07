package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.StationTimeCome.StationTimeComeDTOcreate;
import com.example.triptix.Model.StationInRoute;
import com.example.triptix.Model.StationTimeCome;
import com.example.triptix.Model.Trip;
import com.example.triptix.Model.key.StationTimeComeKey;
import com.example.triptix.Repository.StationInRouteRepo;
import com.example.triptix.Repository.StationTimeComeRepo;
import com.example.triptix.Repository.TripRepo;
import com.example.triptix.Service.StationTimeComeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationTimeComeServiceImpl implements StationTimeComeService {
    @Autowired
    private StationTimeComeRepo stationTimeComeRepo;

    @Autowired
    private StationInRouteRepo stationInRouteRepo;

    @Autowired
    private TripRepo tripRepo;

    @Override
    public ResponseObject<?> getAll(Integer idTrip, int pageSize, int pageIndex) {
        try{
            int totalPage = 0;
            //paging
            Pageable pageable = null;
            if(pageSize != 0 && pageIndex != 0){
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
            }else{ //get all
                pageIndex = 1;
            }

            Page<StationTimeCome> listPage = null;
            List<StationTimeCome> list = null;
            if(idTrip != null){
                list = stationTimeComeRepo.findByTripIdTrip(idTrip);
                return ResponseObject.builder().status(true)
                        .totalPage(1).pageIndex(1).pageSize(list.size())
                        .data(list).build();
            }
            list = stationTimeComeRepo.findAll(pageable).getContent();
            return ResponseObject.builder().status(true)
                    .totalPage(1).pageIndex(1).pageSize(list.size())
                    .data(list).build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> getDetail(StationTimeComeKey idStationTimeCome) {
        try {
            StationTimeCome stationTimeCome = stationTimeComeRepo.findById(idStationTimeCome).get();
            if(stationTimeCome == null){
                return ResponseObject.builder().status(false).message("Not found").build();
            }
            return ResponseObject.builder().status(true).message("success").data(stationTimeCome).build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> create(StationTimeComeDTOcreate b) {
        try {
            StationInRoute stationInRoute = stationInRouteRepo.findById(b.getIdStationInRoute()).orElse(null);
            if(stationInRoute == null){
                return ResponseObject.builder().status(false).message("không tìm thấy trạm trong tuyến (station in route)").build();
            }
            Trip trip = tripRepo.findById(b.getIdTrip()).get();
            if(trip == null){
                return ResponseObject.builder().status(false).message("Not found").build();
            }
            StationTimeCome stationTimeCome = new StationTimeCome();
            stationTimeCome.setStationTimeComeKey(new StationTimeComeKey(b.getIdStationInRoute(), b.getIdTrip()));
            stationTimeCome.setStationInRoute(stationInRoute);
            stationTimeCome.setTrip(trip);
            stationTimeCome.setTimeCome(b.getTimeCome());
            stationTimeComeRepo.save(stationTimeCome);
            return ResponseObject.builder().status(true).message("success").build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> delete(StationTimeComeKey idStationTimeCome) {
        try {
            if(stationTimeComeRepo.existsById(idStationTimeCome) == false){
                return ResponseObject.builder().status(false).message("Not found").build();
            }
            stationTimeComeRepo.deleteById(idStationTimeCome);

            return ResponseObject.builder().status(true).message("success").build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message(e.getMessage()).build();
        }
    }
}
