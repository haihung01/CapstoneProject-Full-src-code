package com.example.triptix.Service.Impl;


import com.example.triptix.DTO.ConfigSystem.ConfigSystemDTOcreate;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Model.ConfigSystem;
import com.example.triptix.Repository.ConfigSystemRepo;
import com.example.triptix.Service.ConfigSystemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigSystemServiceImpl implements ConfigSystemService {
    @Autowired
    private ConfigSystemRepo repo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseObject<?> getAll() {
        try{
            return ResponseObject.builder().status(true).message("found").data(repo.findAll()).build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> getDetail(int id) {
        try{
            ConfigSystem obj = repo.findById(id).orElse(null);
            if(obj == null){
                return ResponseObject.builder().status(false).message("not found").build();
            }
            return ResponseObject.builder().status(true).message("found").data(obj).build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> create(ConfigSystemDTOcreate b) {
        try{
            ConfigSystem obj = modelMapper.map(b, ConfigSystem.class);
            repo.save(obj);
            return ResponseObject.builder().status(true).message("success").build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> update(ConfigSystem b) {
        try{
            if(!repo.existsById(b.getIdConfigSystem())){
                return ResponseObject.builder().status(false).message("not found").build();
            }
            repo.save(b);
            return ResponseObject.builder().status(true).message("success").build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> delete(int id) {
        try{
            repo.deleteById(id);
            return ResponseObject.builder().status(true).message("success").build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message(e.getMessage()).build();
        }
    }
}