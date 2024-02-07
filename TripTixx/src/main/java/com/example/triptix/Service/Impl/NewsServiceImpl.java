package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.News.NewsDTOcreate;
import com.example.triptix.DTO.News.NewsDTOupdate;
import com.example.triptix.DTO.News.NewsDTOview;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Model.News;
import com.example.triptix.Model.UserSystem;
import com.example.triptix.Repository.NewsRepo;
import com.example.triptix.Repository.UserSystemRepo;
import com.example.triptix.Service.NewsService;
import com.example.triptix.Util.FileStoreS3;
import com.example.triptix.Util.UTCTimeZoneUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;
import static org.apache.http.entity.ContentType.IMAGE_JPEG;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepo repo;


    @Autowired
    private UserSystemRepo userSystemRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Environment env;

    @Autowired
    private FileStoreS3 fileStoreS3;

    @Override
    public ResponseObject<?> getAll(int pageSize, int pageIndex) {
        try{
            List<News> objList = new ArrayList<>();
            //paging
            Pageable pageable = null;
            if(pageSize != 0 && pageIndex != 0){
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
                objList = repo.findAll(pageable).getContent();
            }else{ //get all
                objList = repo.findAll();
                pageIndex = 1;
            }
            List<NewsDTOview> newsDTOviews = new ArrayList<>();
            try{
                for( News n : objList){
                    NewsDTOview dto = modelMapper.map(n, NewsDTOview.class);
                    dto.setIdNews(n.getIdNews());
                    dto.setIdStaff(n.getStaff().getIdUserSystem());
                    dto.setImgLink(env.getProperty("aws.s3.link_bucket") + n.getImageLink());
                    newsDTOviews.add(dto);
                }
            }catch (Exception e){
                return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
            }
            return ResponseObject.builder().status(true)
                    .pageSize(objList.size()).pageIndex(pageIndex)
                    .message("Lấy tất cả bài viết thành công.").data(newsDTOviews).build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra ...").build();
        }

    }

    @Override
    public ResponseObject<?> getDetail(int id) {
        try{
            News obj = repo.findById(id).orElse(null);
            if (obj == null) {
                return ResponseObject.builder().status(false).message("Không tìm thấy bài viết.").build();
            }
            NewsDTOview objDTO = modelMapper.map(obj, NewsDTOview.class);
            objDTO.setIdStaff(obj.getStaff().getIdUserSystem());
            objDTO.setImgLink(env.getProperty("aws.s3.link_bucket") + obj.getImageLink());
            return ResponseObject.builder().status(true).message("Tìm thấy bài viết có id = "+ id).data(objDTO).build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra ...").build();
        }
    }

    @Override
    public ResponseObject<?> create(NewsDTOcreate t) {
        String pathImg = "";
        try {
            //check id staff
            UserSystem staff = userSystemRepo.findById(t.getIdStaff()).orElse(null);
            if(staff == null || !staff.getRole().equals("ROLE_STAFF")){
                return ResponseObject.builder().status(false).message("Không tìm thấy đúng id nhân viên.").build();
            }
            //random id
            int id = (int) (Math.random() * 100000);
            while (true){
                if (!repo.existsById(id)){  //nếu có đối tượng thì trả về true, ko thì false
                    break;
                }
                id = (int) (Math.random() * 100000);
            }
            News obj = modelMapper.map(t, News.class);
            obj.setIdNews(id);
            obj.setStaff(staff);
            obj.setCreatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            obj.setUpdatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            pathImg = saveIMG(t.getImage());
            obj.setImageLink(pathImg);
            repo.save(obj);
            return ResponseObject.builder().status(true).message("Tạo thành công.").build();
        }catch (Exception ex) {
            if(!pathImg.equals("")){
                fileStoreS3.deleteImage(env.getProperty("aws.s3.bucketname"), pathImg);
            }
            return ResponseObject.builder().status(false).message(ex.getCause().getCause().getMessage()).build();
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
    public ResponseObject<?> update(NewsDTOupdate t) {
        String pathImg = "";
        try {
            //check xem id có obj ko
            News check = repo.findById(t.getIdNews()).orElse(null);
            if(check == null){
                return ResponseObject.builder().status(false).message("Không tìm thấy bài viết.").build();
            }
            //check xem có thay đổi gì ko
            NewsDTOupdate checkOld = modelMapper.map(check, NewsDTOupdate.class);
            checkOld.setIdStaff(check.getStaff().getIdUserSystem());
            if(checkOld.equalsValue(t)){
                return ResponseObject.builder().status(true).message("Không có gì thay đổi.").build();
            }
            //check id staff
            UserSystem staff = userSystemRepo.findById(t.getIdStaff()).orElse(null);
            if(staff == null){
                return ResponseObject.builder().status(false).message("Không tìm thấy nhân viên.").build();
            }else if(!staff.getRole().equals("ROLE_STAFF")){
                return ResponseObject.builder().status(false).message("Không đúng chức vụ nhân viên.").build();
            }
            News obj = modelMapper.map(t, News.class);
            obj.setCreatedDate(check.getCreatedDate());
            obj.setUpdatedDate(UTCTimeZoneUtil.getTimeGTMplus7VN_utildate());
            obj.setStaff(staff);
            obj.setImageLink(check.getImageLink());
            repo.save(obj);
            return ResponseObject.builder().status(true).message("Cập nhật thành công").build();

        } catch (Exception ex) {
            if(!pathImg.equals("")){
                fileStoreS3.deleteImage(env.getProperty("aws.s3.bucketname"), pathImg);
            }
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra..").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> updateImgNew(int idNew, MultipartFile t) {
        String pathImg = "";
        try {
            //check xem id có obj ko
            News check = repo.findById(idNew).orElse(null);
            if(check == null){
                return ResponseObject.builder().status(false).message("Không tìm thấy bài viết.").build();
            }
            //delete img cũ đi
            if(check.getImageLink() != null){
                fileStoreS3.deleteImage(env.getProperty("aws.s3.bucketname"), check.getImageLink());
            }

            //save img mới
            pathImg = saveIMG(t);
            check.setImageLink(pathImg);
            repo.save(check);
            return ResponseObject.builder().status(true).message("Cập nhật thành công").build();

        } catch (Exception ex) {
            if(!pathImg.equals("")){
                fileStoreS3.deleteImage(env.getProperty("aws.s3.bucketname"), pathImg);
            }
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra..").data(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> delete(int id) {
        try {
            //check xem id có obj ko
            News check = repo.findById(id).orElse(null);
            if(check == null){
                return ResponseObject.builder().status(false).message("Không tìm thấy bài viết.").build();
            }
            //delete img cũ đi
            if(check.getImageLink() != null){
                fileStoreS3.deleteImage(env.getProperty("aws.s3.bucketname"), check.getImageLink());
            }
            repo.deleteById(id);
            return ResponseObject.builder().status(true).message("Xóa thành công.").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xãy ra ...").data(ex.getMessage()).build();
        }
    }
}
