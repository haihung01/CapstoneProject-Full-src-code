package com.example.triptix.Util.Province;

import com.example.triptix.Enum.RegionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ProvinceUtil {
    private List<ProvinceVN> provinceList;
    private static List<ProvinceVN> provinceListTest;

    public ProvinceUtil() {
        provinceList = new ArrayList<>();
        //BẮC
        //Danh sách các tỉnh Đông Bắc Bộ
        provinceList.add(new ProvinceVN("Điện Biên", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Hoà Bình", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Lai Châu", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Lào Cai", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Sơn La", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Yên Bái", RegionType.BAC.getValue()));
        //Danh sách các tỉnh Đông Bắc Bộ
        provinceList.add(new ProvinceVN("Bắc Giang", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Bắc Kạn", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Cao Bằng", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Hà Giang", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Lạng Sơn", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Phú Thọ", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Quảng Ninh", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Thái Nguyên", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Tuyên Quang", RegionType.BAC.getValue()));
        //Danh sách các tỉnh Đồng Bằng Sông Hồng
        provinceList.add(new ProvinceVN("Bắc Ninh", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Hà Nam", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Hà Nội", RegionType.BAC.getValue()));  //Thành phố
        provinceList.add(new ProvinceVN("Hải Dương", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Hải Phòng", RegionType.BAC.getValue()));//Thành phố
        provinceList.add(new ProvinceVN("Hưng Yên", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Nam Định", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Ninh Bình", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Thái Bình", RegionType.BAC.getValue()));
        provinceList.add(new ProvinceVN("Vĩnh Phúc", RegionType.BAC.getValue()));

        //TRUNG
        //Danh sách các tỉnh Bắc Trung Bộ
        provinceList.add(new ProvinceVN("Hà Tĩnh", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Nghệ An", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Quảng Bình", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Quảng Trị", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Thanh Hóa", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Thừa Thiên Huế", RegionType.TRUNG.getValue()));
        //Danh sách các tỉnh Nam Trung Bộ
        provinceList.add(new ProvinceVN("Bình Định", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Bình Thuận", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Đà Nẵng", RegionType.TRUNG.getValue()));   //tp
        provinceList.add(new ProvinceVN("Khánh Hòa", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Ninh Thuận", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Phú Yên", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Quảng Nam", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Quảng Ngãi", RegionType.TRUNG.getValue()));
        //Danh sách các tỉnh Tây Nguyên
        provinceList.add(new ProvinceVN("Đắk Lắk", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Đắk Nông", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Gia Lai", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Kon Tum", RegionType.TRUNG.getValue()));
        provinceList.add(new ProvinceVN("Lâm Đồng", RegionType.TRUNG.getValue()));

        //NAM
        //Danh sách các tỉnh Đông Nam Bộ
        provinceList.add(new ProvinceVN("Bà Rịa - Vũng Tàu", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Bình Dương", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Bình Phước", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Đồng Nai", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Hồ Chí Minh", RegionType.NAM.getValue())); //tp
        provinceList.add(new ProvinceVN("Tây Ninh", RegionType.NAM.getValue()));
        //Danh sách các tỉnh Đồng Bằng Sông Cửu Long
        provinceList.add(new ProvinceVN("An Giang", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Bạc Liêu", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Bến Tre", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Cà Mau", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Cần Thơ", RegionType.NAM.getValue()));//tp
        provinceList.add(new ProvinceVN("Đồng Tháp", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Hậu Giang", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Kiên Giang", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Long An", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Sóc Trăng", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Tiền Giang", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Trà Vinh", RegionType.NAM.getValue()));
        provinceList.add(new ProvinceVN("Vĩnh Long", RegionType.NAM.getValue()));

        provinceListTest = new ArrayList<>();
        for (int i= 0; i< provinceList.size(); i++) {
            provinceListTest.add(provinceList.get(i));
        }
    }

    public static void main(String[] args) {
        ProvinceUtil provinceUtil = new ProvinceUtil();
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://provinces.open-api.vn/api/p/";

        ResponseEntity<Province[]> response = restTemplate.getForEntity(apiUrl, Province[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Province[] provinces = response.getBody();
            String nameTmp = "";
            boolean check = true;
            for (Province item : Arrays.asList(provinces)) {
                if (item.getName().contains("Tỉnh")){
                    nameTmp = item.getName().replace("Tỉnh ","");
                }else{
                    nameTmp = item.getName().replace("Thành phố ","");
                }
                if(!checkProvinceVnTest(nameTmp)) {
                    check = false;
                    System.out.println(nameTmp + " - NOT MATCH");
                }
            } ;
            if(check) {
                System.out.println("==> MATCH ALL");
            }else{
                System.out.println("==> NOT MATCH ALL");
            }
        } else {
            throw new RuntimeException("Lỗi khi gọi API: " + response.getStatusCode());
        }
    }

    public static boolean checkProvinceVnTest(String province) {
        for(ProvinceVN provinceVN : provinceListTest) {
            if(provinceVN.getName().equals(province)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkProvinceVn(String province) {
        for(ProvinceVN provinceVN : provinceList) {
            if(provinceVN.getName().equals(province)) {
                return true;
            }
        }
        return false;
    }

    public ProvinceVN getProvinceVn(String provinceName) {
        for(ProvinceVN provinceVN : provinceList) {
            if(provinceVN.getName().equals(provinceName)) {
                return provinceVN;
            }
        }
        return null;
    }

    public boolean checkRegionVn(String name1, String name2, String region) {
        ProvinceVN destination1 = getProvinceVn(name1);
        ProvinceVN destination2 = getProvinceVn(name2);

        if(destination2.getRegion().equals(destination1.getRegion())) { //2 tỉnh same regions -> check region cũa 2 tỉnh vs region của input
            if(region.equals(destination1.getRegion())) {
                return true;
            }
        }else {
            if(region.equals(RegionType.ORTHER.getValue())) {
                return true;
            }
        }
        return false;
    }
}
