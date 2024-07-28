package com.example.poi_map.Controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.example.poi_map.Service.PoiService;
import com.example.poi_map.Entity.PoiEntity;

import java.util.List;

@RestController
@RequestMapping("/api/poi")
public class PoiController {

    @Resource
    private PoiService poiService;

    @GetMapping("/all")
    public List<PoiEntity> showAllPoi() {
        return poiService.showAllPoi();
    }

    @GetMapping("/{id}")
    public List<PoiEntity> showPoiByPoi_Id(@PathVariable Integer id) {
        return poiService.showPoiByPoi_Id(id);
    }

    @GetMapping("/nearby")
    public List<PoiEntity> findNearbyPoi(@RequestParam double latitude, @RequestParam double longitude, @RequestParam double radius) {
        return poiService.findNearbyPoi(latitude, longitude, radius);
    }

    @GetMapping("/search")
    public List<PoiEntity> showPoiByUserChoice(@RequestParam(required = false) String province,
                                               @RequestParam(required = false) String city,
                                               @RequestParam(required = false) String district,
                                               @RequestParam(required = false) String poi_name,
                                               @RequestParam(required = false) String poi_address,
                                               @RequestParam(required = false) String poi_type) {
        return poiService.showPoiByUserChoice(province, city, district, poi_name, poi_address, poi_type);
    }

    @PostMapping("/add")
    public PoiEntity addNewPoiInMap(@RequestBody PoiEntity poiEntity) {
        return poiService.addNewPoiInMap(poiEntity);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deletePoiByPoi_Id(@PathVariable Integer id) {
        return poiService.deletePoiByPoi_Id(id);
    }

    @PutMapping("/update/{id}")
    public boolean updatePoiByPoi_Id(@PathVariable Integer id, @RequestBody PoiEntity poiEntity) {
        return poiService.updatePoiByPoi_Id(id, poiEntity.getProvince(), poiEntity.getCity(), poiEntity.getDistrict(), poiEntity.getPoi_name(), poiEntity.getPoi_address(), poiEntity.getPoi_type(), poiEntity.getLatitude(), poiEntity.getLongitude());
    }
}

