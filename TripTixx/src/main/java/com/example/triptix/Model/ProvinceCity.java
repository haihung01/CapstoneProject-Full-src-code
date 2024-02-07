package com.example.triptix.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class ProvinceCity {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String idProvince;
    private String name;
    private String type;  //province/city

    @OneToMany(mappedBy = "startProvinceCity")
    @JsonIgnore
    private List<Route> routesStart;

    @OneToMany(mappedBy = "endProvinceCity")
    @JsonIgnore
    private List<Route> routesEnd;

    @Override
    public String toString() {
        return "ProvinceCity{" +
                "idProvince=" + idProvince +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
