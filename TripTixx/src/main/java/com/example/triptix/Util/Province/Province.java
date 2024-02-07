package com.example.triptix.Util.Province;

import com.example.triptix.DTO.ProvinceCity.DetailProvinceCity;
import com.example.triptix.DTO.ProvinceCity.ListDetailProvinceCity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Province {

    private String name;

    private int code;

    private String division_type;   //"tỉnh" "thành phố trung ương" "huyện" "quận" "thành phố" "thị xã" "xã" "thị trấn" "phường"

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
//        String apiUrl = "https://provinces.open-api.vn/api/p/";
        String apiUrl = "https://vapi.vnappmob.com/api/province/district/1";

        ResponseEntity<ListDetailProvinceCity> response = restTemplate.getForEntity(apiUrl, ListDetailProvinceCity.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            List<DetailProvinceCity> provinces = response.getBody().getResults();
            if(provinces.size() == 0){
                System.out.println("==> NOT FOUND");
            }
            int tmp = 0;
            for (DetailProvinceCity item : provinces) {
                String name = null;
                if(item.getDistrict_type().equals("Thành phố")){
                    name = item.getDistrict_name().substring(10);
                    tmp++;
                    System.out.println(tmp + ": " + name);
                }
            } ;
        } else {
            throw new RuntimeException("Lỗi khi gọi API: " + response.getStatusCode());
        }
    }
}
