package com.example.triptix.Service.Impl;


import com.example.triptix.Enum.ProvinceCityType;
import com.example.triptix.DTO.ProvinceCity.DetailProvinceCity;
import com.example.triptix.DTO.ProvinceCity.ListDetailProvinceCity;
import com.example.triptix.DTO.ProvinceCity.ProvinceCityDTOcreate;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Model.ProvinceCity;
import com.example.triptix.Repository.ProvinceCityRepo;
import com.example.triptix.Service.ProvinceCityService;
import com.example.triptix.Util.Province.Province;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ProvinceCityServiceImpl implements ProvinceCityService {
    public static final String KEY_SEARCH_PROVINCE = "PROVINCE";
    public static final String KEY_SEARCH_CITY = "CITY";
    public static final String KEY_SEARCH_VN = "VN";
    @Autowired
    private ProvinceCityRepo repo;

//    public boolean importRegion() {
//        List<ProvinceCity> provinceCityList = repo.find63ProvinceInVN(null);
//        ProvinceUtil provinceUtil = new ProvinceUtil();
//        for (ProvinceCity provinceCity : provinceCityList) {
//            if(provinceUtil.getProvinceVn(provinceCity.getName()).getRegion() != null){
//                provinceCity.setRegion(provinceUtil.getProvinceVn(provinceCity.getName()).getRegion());
//                repo.save(provinceCity);
//            }
//        }
//        return true;
//    }

    @Override
    public ResponseObject<?> getAll(String type, int pageSize, int pageIndex) {
        List<ProvinceCity> provinceCityList = null;
        try {
            //paging
            Pageable pageable = null;
            if (pageSize != 0 && pageIndex != 0) {
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
//                provinceCityList = repo.findAll(pageable).getContent();
            } else { //get all
//                provinceCityList = repo.findAll();
                pageIndex = 1;
            }
            if (type != null) {
                List<String> typeSearch = Arrays.asList(KEY_SEARCH_PROVINCE, KEY_SEARCH_CITY, KEY_SEARCH_VN);
                if (!typeSearch.contains(type)) {
                    return ResponseObject.builder().status(false).message("type must be one of these " + typeSearch.toString()).build();
                }
                if (type.equals(KEY_SEARCH_VN)) {
                    provinceCityList = repo.find63ProvinceInVN(pageable);
                } else {
                    provinceCityList = repo.findByType(type, pageable);
                }
            } else {
                if (pageable != null) {
                    provinceCityList = repo.findAll(pageable).getContent();
                } else {
                    provinceCityList = repo.findAll();
                }
            }

        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
        return ResponseObject.builder().status(true)
                .pageSize(provinceCityList.size()).pageIndex(pageIndex)
                .message("get all success").data(provinceCityList).build();
    }

    @Override
    public ResponseObject<?> getDetail(String id) {
        try {
            ProvinceCity provinceCity = repo.findById(id).orElse(null);
            if (provinceCity == null) {
                return ResponseObject.builder().status(false).message("not found").build();
            }
            return ResponseObject.builder().status(true).message("get detail success").data(provinceCity).build();
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> add(ProvinceCityDTOcreate b) {
        try {
            ProvinceCity provinceCity = new ProvinceCity();
            provinceCity.setName(b.getName());
            provinceCity.setType(b.getType());
            int idrandom = (int) (Math.random() * 100000);
            while (true) {
                if (!repo.existsById(idrandom + "")) {
                    break;
                }
                idrandom = (int) (Math.random() * 100000);
            }
            provinceCity.setIdProvince(idrandom + "");
            repo.save(provinceCity);
            return ResponseObject.builder().status(true).message("success").build();
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> delete(String id) {
        try {
            ProvinceCity provinceCity = repo.findById(id).orElse(null);
            if (provinceCity == null) {
                return ResponseObject.builder().status(false).message("not found").build();
            }
            repo.delete(provinceCity);
            return ResponseObject.builder().status(true).message("success").build();
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> importData() {
        //import hết các tỉnh - tp vào
        List<ProvinceCity> provinceCityList = repo.findAll();
        if (provinceCityList.size() == 0) { //chưa gì gì, cần import vào
            //import tỉnh
            //lấy list province từ OPEN API và import vào
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://provinces.open-api.vn/api/p/";
            ProvinceCity provinceCity = null;
            //gọi API
            ResponseEntity<Province[]> response = restTemplate.getForEntity(apiUrl, Province[].class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Province[] provinces = response.getBody();
                for (Province item : Arrays.asList(provinces)) {
                    provinceCity = new ProvinceCity();
                    provinceCity.setIdProvince(item.getCode() + "");

                    if (item.getName().contains("Tỉnh")) {
                        provinceCity.setType(ProvinceCityType.PROVINCE.name());
                        provinceCity.setName(item.getName().substring(5));
                    } else {
                        provinceCity.setType(ProvinceCityType.CITY.name());
                        provinceCity.setName(item.getName().substring(10));
                    }
//                        System.out.println(provinceCity.toString());
                    repo.save(provinceCity);

                    //import thành phố
                    String apiUrlDetailProvince = "https://vapi.vnappmob.com/api/province/district/" + item.getCode();
                    ResponseEntity<ListDetailProvinceCity> responseDetail = restTemplate.getForEntity(apiUrlDetailProvince, ListDetailProvinceCity.class);
                    if (responseDetail.getStatusCode().is2xxSuccessful()) {
                        provinceCity = null;
                        List<DetailProvinceCity> detailProvinces = responseDetail.getBody().getResults();
                        if (detailProvinces.size() != 0) {
                            for (DetailProvinceCity itemDetail : detailProvinces) {
                                if (itemDetail.getDistrict_type().equals("Thành phố")) {
                                    provinceCity = new ProvinceCity();
                                    provinceCity.setIdProvince(item.getCode() + "-" + itemDetail.getDistrict_id());
                                    provinceCity.setName("Thành Phố " + itemDetail.getDistrict_name().substring(10));
                                    provinceCity.setType(ProvinceCityType.CITY.name());
                                    repo.save(provinceCity);
                                }
                            }
                        }
                    }
                }
                ;
                System.out.println("==> 63 province ang city each province created !");
            } else {
                throw new RuntimeException("Lỗi khi gọi API: " + response.getStatusCode());
            }
            return ResponseObject.builder().status(true).message("import success").build();
        }
        ;
        return ResponseObject.builder().status(true).message("nothing import").build();
    }

    @Override
    public ResponseObject<?> deleteAll() {
        try {
            repo.deleteAll();
        } catch (Exception e) {
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
        return ResponseObject.builder().status(true).message("delete all success").build();
    }

    @Override
    public boolean checkProvinceVn(String province) {
        try{
            ProvinceCity provinceCity = repo.findByName(province);
            if(provinceCity == null){
                return false;
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public ProvinceCity getProvinceVn(String provinceName) {
        try{
            ProvinceCity provinceCity = repo.findByName(provinceName);
            if(provinceCity == null){
                return null;
            }
            return provinceCity;
        }catch (Exception e){
            log.error("error at ServiceImpl/getProvinceVn: " + e.getMessage());
            return null;
        }
    }
}
