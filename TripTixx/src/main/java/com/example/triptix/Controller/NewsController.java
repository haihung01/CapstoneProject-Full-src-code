package com.example.triptix.Controller;

import com.example.triptix.DTO.News.NewsDTOcreate;
import com.example.triptix.DTO.News.NewsDTOupdate;
import com.example.triptix.DTO.News.UpdateObjectImg;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Service.NewsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.RollbackException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    @Autowired
    private Environment env;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer pageSize,
                                    @RequestParam(required = false) Integer pageIndex) {
        if(pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = newsService.getAll(pageSize, pageIndex);
        if(!rs.isStatus()){
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getDetail(@RequestParam int id) {
        ResponseObject<?> rs = newsService.getDetail(id);
        if(!rs.isStatus()){
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @PostMapping(path = "",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,     //này là dể nó cho phép swagger upload file
            produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> create(@ModelAttribute @Valid NewsDTOcreate obj, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            // Bắt lỗi từng lỗi và trả về Map với key là tên thuộc tính sai và value là nội dung lỗi
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }

            // Trả về Map lỗi
            ResponseObject<?> rs = ResponseObject.builder().status(false).message("lỗi dữ liệu hợp lệ").data(errorMap).build();
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }

        ResponseObject<?> rs = newsService.create(obj);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PutMapping(path = "/img",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,     //này là dể nó cho phép swagger upload file
            produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> updateImgNews(@ModelAttribute @Valid UpdateObjectImg obj, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Bắt lỗi từng lỗi và trả về Map với key là tên thuộc tính sai và value là nội dung lỗi
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }

            // Trả về Map lỗi
            ResponseObject<?> rs = ResponseObject.builder().status(false).message("lỗi dữ liệu hợp lệ").data(errorMap).build();
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
        ResponseObject<?> rs = newsService.updateImgNew(obj.getIdObj(), obj.getImg());
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PutMapping()
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> update(@RequestBody @Valid NewsDTOupdate objDTO) {
        ResponseObject<?> rs = newsService.update(objDTO);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @DeleteMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> delete(@RequestParam int id){
        ResponseObject<?> rs = newsService.delete(id);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseObject<?> handleValidationExceptions(MethodArgumentNotValidException ex) { //hàm bắt lỗi valid data
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseObject.builder().status(false).code(400).message("lỗi dữ liệu hợp lệ").data(errors).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RollbackException.class)
    public ResponseObject<?> handleRollbackExceptions(RollbackException ex) { //hàm bắt lỗi rollback
        return ResponseObject.builder().status(false).code(400).message("lỗi thực thi").data(ex.getMessage()).build();
    }

}
