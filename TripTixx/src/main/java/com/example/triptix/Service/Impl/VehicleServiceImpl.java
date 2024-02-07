package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Station.StationDTOview;
import com.example.triptix.DTO.Vehicle.VehicleDTOchangeStatus;
import com.example.triptix.DTO.Vehicle.VehicleDTOcreate;
import com.example.triptix.DTO.Vehicle.VehicleDTOupdate;
import com.example.triptix.DTO.Vehicle.VehicleDTOview;
import com.example.triptix.Enum.Status;
import com.example.triptix.Enum.Type;
import com.example.triptix.Model.News;
import com.example.triptix.Model.Station;
import com.example.triptix.Model.Vehicle;
import com.example.triptix.Repository.StationRepo;
import com.example.triptix.Repository.VehicleRepo;
import com.example.triptix.Service.VehicleService;
import com.example.triptix.Util.FileStoreS3;
import com.example.triptix.Util.UTCTimeZoneUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;
import static org.apache.http.entity.ContentType.IMAGE_JPEG;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepo vehicleRepo;

    @Autowired
    private StationRepo stationRepo;

    @Autowired
    private Environment env;

    @Autowired
    private FileStoreS3 fileStoreS3;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseObject<?> getAll(String type, int pageSize, int pageIndex) {
        try {
            List<Vehicle> vehicles = null;
            Page<Vehicle> vehiclePage = null;
            int totalPage = 0;
            //paging
            Pageable pageable = null;
            if(pageSize != 0 && pageIndex != 0){
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
            }else{ //get all
                pageIndex = 1;
            }
            if(type != null){
                List<String> typeSearch = Arrays.asList(Type.GHE.name(), Type.LIMOUSINE.name(), Type.GIUONG.name());
                if(!typeSearch.contains(type)){
                    return ResponseObject.builder().status(false).message("type must be one of these " + typeSearch.toString()).build();
                }
                vehiclePage = vehicleRepo.findByType(type, pageable);
            }else{
                if(pageable != null){
                    vehiclePage = vehicleRepo.findAll(pageable);
                }else{
                    vehicles = vehicleRepo.findAll();
                }
            }
            if(vehiclePage != null){
                vehicles = vehiclePage.getContent();
                totalPage = vehiclePage.getTotalPages();
            }

            List<VehicleDTOview> list = new ArrayList<>();
            for(Vehicle vehicle : vehicles){
                VehicleDTOview dto = modelMapper.map(vehicle, VehicleDTOview.class);
                Station station = stationRepo.findByIdStation(dto.getStation().getIdStation());
                StationDTOview dtoStation = modelMapper.map(station, StationDTOview.class);
                dto.setStation(dtoStation);
                dto.setImgLink(env.getProperty("aws.s3.link_bucket") + vehicle.getImageLink());
                list.add(dto);
            }

            return ResponseObject.builder().status(true)
                    .pageSize(list.size()).pageIndex(pageIndex).totalPage(totalPage)
                    .message("Lấy tất cả xe thành công").data(list).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra.").build();
        }

    }

    @Override
    public ResponseObject<?> getDetail(int id) {
        Vehicle bus = vehicleRepo.findById(id).orElse(null);
        if(bus == null){
            return ResponseObject.builder().status(false).message("Không tìm thấy.").build();
        }
        VehicleDTOview dto = modelMapper.map(bus, VehicleDTOview.class);
        Station station = stationRepo.findByIdStation(dto.getStation().getIdStation());
        StationDTOview dtoStation = modelMapper.map(station, StationDTOview.class);
        dto.setStation(dtoStation);
        dto.setImgLink(env.getProperty("aws.s3.link_bucket") + bus.getImageLink());
        return ResponseObject.builder().status(true).message("Đã tìm thấy xe có id: " + id).data(dto).build();
    }

    @Override
    public ResponseObject<?> create(VehicleDTOcreate b) {
        String pathImg = "";
        try{
            Date currentDate = new Date();
            // Kiểm tra xem biển số xe đã tồn tại trong cơ sở dữ liệu chưa
            if (vehicleRepo.existsByLicensePlates(b.getLicensePlates())) {
                // Nếu đã tồn tại, không thêm bản ghi mới và trả về false
                return ResponseObject.builder().status(false).message("Biển số xe đã tồn tại.").build();
            }

            //check station
            try{
                Station station = stationRepo.findById(b.getIdStation()).orElse(null);
                if(station == null){
                    return ResponseObject.builder().status(false).message("Không tồn tại trạm này.").build();
                }else if(station.getStatus().equals(Status.DEACTIVE.name())){
                    return ResponseObject.builder().status(false).message("Trạm có id: "+ station.getIdStation() + " này đã ngưng hoạt động.").build();
                }
            //random id
            int id = (int) (Math.random() * 100000);
            while (true){
                if (!vehicleRepo.existsById(id)){  //nếu có đối tượng thì trả về true, ko thì false
                    break;
                }
                id = (int) (Math.random() * 100000);
            }
            Vehicle bus = modelMapper.map(b, Vehicle.class);
            bus.setIdBus(id);
            bus.setStation(station);
            bus.setCreatedDate(UTCTimeZoneUtil.getTimeNow());
            bus.setUpdatedDate(UTCTimeZoneUtil.getTimeNow());
            bus.setStatus(Status.ACTIVE.name());

            pathImg = saveIMG(b.getImage());
            bus.setImageLink(pathImg);

            vehicleRepo.save(bus);
            }catch (NumberFormatException ex){
                return ResponseObject.builder().status(false).message("Vui lòng chọn trạm công tác.").build();
            }
            return ResponseObject.builder().status(true).message("Tạo thành công.").build();
        }catch (Exception ex){
            if(!pathImg.equals("")){
                fileStoreS3.deleteImage(env.getProperty("aws.s3.bucketname"), pathImg);
            }
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra.").data(ex.getCause().getCause().getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> update(VehicleDTOupdate b) {
        try {
            Vehicle check = vehicleRepo.findById(b.getIdBus()).orElse(null);
            if(check == null){
                return ResponseObject.<String>builder().status(false).message("Không tìm thấy.").build();
            }

            //check station
            try{
                Station station = stationRepo.findById(b.getIdStation()).orElse(null);
                if(station == null){
                    return ResponseObject.builder().status(false).message("Không tìm thấy trạm công tác.").build();
                }else if(station.getStatus().equals(Status.DEACTIVE.name())){
                    return ResponseObject.builder().status(false).message("Trạm có id: " + station.getIdStation() + " này đã ngưng hoạt động.").build();
                }
                Vehicle vehicle = modelMapper.map(b,Vehicle.class);
                vehicle.setLicensePlates(check.getLicensePlates());
                vehicle.setStation(station);
                vehicle.setUpdatedDate(UTCTimeZoneUtil.getTimeNow());
                vehicle.setCreatedDate(check.getCreatedDate());
                vehicleRepo.save(vehicle);
                return ResponseObject.builder().status(true).message("Cập nhật thành công.").build();
            }catch (NumberFormatException ex){
                return ResponseObject.builder().status(false).message("Vui lòng chọn trạm công tác.").build();
            }
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra.").data(ex.getCause().getCause().getMessage()).build();
        }
    }

    private String saveIMG(MultipartFile file) throws Exception {
        try {
            int fileSize = env.getProperty("limit_max_file_size", Integer.class);
            //check size <10Mb, 1MB = 1 048 576 bytes
            if (file.getSize() >= fileSize * 1048576) {
                throw new Exception("error upload img, < "+fileSize+"MB");
            }

            //Check if the file is an image
            if (!Arrays.asList(IMAGE_PNG.getMimeType(),
                    IMAGE_BMP.getMimeType(),
                    IMAGE_GIF.getMimeType(),
                    IMAGE_JPEG.getMimeType()).contains(file.getContentType())) {
                throw new Exception("lỗi tải lên img, File tải lên không phải là hình ảnh");
            }
            //get file metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("Content-Type", file.getContentType());
            metadata.put("Content-Length", String.valueOf(file.getSize()));

            //Save Image in S3 and then save in the database
            String pathName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            try {
                fileStoreS3.upload(pathName, Optional.of(metadata), file.getInputStream());
            } catch (IOException e) {
                throw new Exception("failed upload img, " + e.getMessage());
            }

            return pathName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ResponseObject<?> updateImgNew(int idVehicle, MultipartFile t) {
        String pathImg = "";
        try {
            //check xem id có obj ko
            Vehicle check = vehicleRepo.findById(idVehicle).orElse(null);
            if(check == null){
                return ResponseObject.builder().status(false).message("Không tìm thấy bài xe.").build();
            }
            //delete img cũ đi
            if(check.getImageLink() != null){
                fileStoreS3.deleteImage(env.getProperty("aws.s3.bucketname"), check.getImageLink());
            }

            //save img mới
            pathImg = saveIMG(t);
            check.setImageLink(pathImg);
            vehicleRepo.save(check);
            return ResponseObject.builder().status(true).message("Cập nhật thành công").build();

        } catch (Exception ex) {
            if(!pathImg.equals("")){
                fileStoreS3.deleteImage(env.getProperty("aws.s3.bucketname"), pathImg);
            }
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra..").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> updateStatus(VehicleDTOchangeStatus b) {
        try{
            Vehicle check = vehicleRepo.findById(b.getIdBus()).orElse(null);
            if(check == null){
                return ResponseObject.<String>builder().status(false).message("Không tìm thấy xe.").build();
            }
            check.setStatus(b.getStatus());
            vehicleRepo.save(check);
            return ResponseObject.builder().status(true).message("Cập nhật thành công.").build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> delete(int id) {
        try {
            //check xem id có obj ko
            Vehicle check = vehicleRepo.findById(id).orElse(null);
            if(check == null){
                return ResponseObject.builder().status(false).message("Không tìm thấy xe.").build();
            }
            //delete img cũ đi
            if(check.getImageLink() != null){
                fileStoreS3.deleteImage(env.getProperty("aws.s3.bucketname"), check.getImageLink());
            }
            vehicleRepo.deleteById(id);
            return ResponseObject.<String>builder().status(true).message("Xóa thành công xe.").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra.").build();
        }
    }
}
