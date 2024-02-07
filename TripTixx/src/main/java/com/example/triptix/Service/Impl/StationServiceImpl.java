package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Station.StationDTOchangeStatus;
import com.example.triptix.DTO.Station.StationDTOcreate;
import com.example.triptix.DTO.Station.StationDTOupdate;
import com.example.triptix.DTO.Station.StationDTOview;
import com.example.triptix.Enum.Status;
import com.example.triptix.Model.Station;
import com.example.triptix.Repository.StationRepo;
import com.example.triptix.Repository.UserSystemRepo;
import com.example.triptix.Service.StationService;
import org.checkerframework.checker.units.qual.A;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StationServiceImpl implements StationService {

    public static final String LOCATION_DEFAULT = "10.756714, 106.673265";

    @Autowired
    private StationRepo stationRepo;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private UserSystemRepo userSystemRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseObject<?> getAll(String name, String province ,int pageSize, int pageIndex) {
        List<Station> stations = null;
        Page<Station> stationsPage = null;
        int totalPage = 0;
        //paging
        Pageable pageable = null;
        if(pageSize != 0 && pageIndex != 0){
            pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
        }else{ //get all
            pageIndex = 1;
        }
        // Tạo một đối tượng Example để sử dụng cho tìm kiếm theo tên và tỉnh.
        if (name != null && !name.isEmpty() && province != null && !province.isEmpty()) {
            // Nếu cả "name" và "province" được cung cấp, tìm kiếm theo cả hai.
            stationsPage = stationRepo.findByNameContainingAndProvinceContaining(name, province, pageable);
        } else if (name != null && !name.isEmpty()) {
            // Nếu chỉ "name" được cung cấp, tìm kiếm theo "name".
            stationsPage = stationRepo.findByNameContaining(name, pageable);
        } else if (province != null && !province.isEmpty()) {
            // Nếu chỉ "province" được cung cấp, tìm kiếm theo "province".
            stationsPage = stationRepo.findByProvinceContaining2("%"+province + "%", pageable);
        } else {
            // Nếu không có tham số nào được cung cấp, lấy tất cả các trạm.
            stationsPage = stationRepo.findAllOrderyByProvince(pageable);
        }

        if(stationsPage != null){
            stations = stationsPage.getContent();
            totalPage = stationsPage.getTotalPages();
        }

        List<StationDTOview> stationDTOS = new ArrayList<>();
        try{
            for( Station b : stations){
                StationDTOview dto = modelMapper.map(b, StationDTOview.class);
                stationDTOS.add(dto);
            }
        }catch (Exception e){
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra...").data(e.getMessage()).build();
        }
        return ResponseObject.builder().status(true).message("Tìm tất cả các trạm thành công.")
                .pageSize(stationDTOS.size()).pageIndex(pageIndex).totalPage(totalPage)
                .data(stationDTOS).build();
    }

    @Override
    public ResponseObject<?> getDetail(int id) {
        Station station = stationRepo.findById(id).orElse(null);
        if(station == null){
            return ResponseObject.builder().status(false).message("Không tìm thấy trạm.").build();
        }
        StationDTOview stationDTO = modelMapper.map(station, StationDTOview.class);
        return ResponseObject.builder().status(true).message("Tìm thấy trạm.").data(stationDTO).build();
    }

    @Override
    public ResponseObject<?> create(StationDTOcreate b) {
        try{
            Station station = modelMapper.map(b, Station.class);
            station.setStatus(Status.ACTIVE.name());
            station.setLocation(LOCATION_DEFAULT);
            stationRepo.save(station);
            return ResponseObject.builder().status(true).message("Tạo thành công").build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra.").data(e.getCause().getCause().getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> update(StationDTOupdate b) {
        try {
            Station check = stationRepo.findById(b.getIdStation()).orElse(null);
            if(check == null){
                return ResponseObject.<String>builder().status(false).message("Không tìm thấy trạm.").build();
            }
            Station station = modelMapper.map(b, Station.class);
            station.setLocation(LOCATION_DEFAULT);
            stationRepo.save(station);
            return ResponseObject.builder().status(true).message("Cập nhật thành công.").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra...").data(ex.getCause().getCause().getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> updateStatus(StationDTOchangeStatus b) {
        try {
            Station check = stationRepo.findById(b.getIdStation()).orElse(null);
            if(check == null){
                return ResponseObject.<String>builder().status(false).message("Không tìm thấy trạm.").build();
            }
            check.setStatus(b.getStatus());
            stationRepo.save(check);
            return ResponseObject.builder().status(true).message("Cập nhật thành công.").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra...").data(ex.getCause().getCause().getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> delete(int id) {
        try {
            stationRepo.deleteById(id);
            return ResponseObject.<String>builder().status(true).message("Xóa thành công").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra...").data(null).build();
        }
    }
}
