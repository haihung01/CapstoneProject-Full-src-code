package com.example.triptix.Service;

import com.example.triptix.DTO.News.NewsDTOcreate;
import com.example.triptix.DTO.News.NewsDTOupdate;
import com.example.triptix.DTO.ResponseObject;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NewsService {
    ResponseObject<?> getAll(int pageSize, int pageIndex);
    ResponseObject<?> getDetail(int id);
    ResponseObject<?> create(NewsDTOcreate t);
    ResponseObject<?> update(NewsDTOupdate t);
    ResponseObject<?> updateImgNew(int idNew, MultipartFile t);
/*    ResponseObject<?> updatePatch(NewsDTOupdatePatch t, List<MultipartFile> fileList);*/
    ResponseObject<?> delete(int id);
}
