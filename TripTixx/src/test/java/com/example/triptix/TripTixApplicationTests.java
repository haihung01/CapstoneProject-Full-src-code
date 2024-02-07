package com.example.triptix;

import com.example.triptix.DTO.ProvinceCity.ProvinceCityDTOcreate;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Model.ProvinceCity;
import com.example.triptix.Service.ProvinceCityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TripTixApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        ProvinceCityService provinceCityService = context.getBean(ProvinceCityService.class);
        ResponseObject rs = provinceCityService.add(new ProvinceCityDTOcreate("Ha Noi no 1", "Province", "Bắc"));
        Assertions.assertEquals(true, rs.isStatus());
    }
}
