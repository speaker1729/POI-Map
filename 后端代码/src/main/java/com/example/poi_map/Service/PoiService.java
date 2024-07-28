package com.example.poi_map.Service;

import com.example.poi_map.Entity.PoiEntity;
import com.example.poi_map.Mapper.PoiMapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoiService {
    @Resource
    PoiMapper poiMapper;

    //查询所有POI
    public List<PoiEntity> showAllPoi(){
        return poiMapper.showAllPoi();
    }

    //根据poi_id查询
    public List<PoiEntity> showPoiByPoi_Id(Integer poi_id){
        return poiMapper.showPoiByPoi_Id(poi_id);
    }

    public List<PoiEntity> findNearbyPoi(double latitude,double longitude,double radius){
        return poiMapper.findNearbyPoi(latitude,longitude,radius);
    }


    /*
     * 用户组合查询
     */

    // 组合查询，有限制条件索引的就在指定参数位置传参，没有限制条件索引的就在指定参数位置传NULL，全NULL其实就是showAllPoi(),最后返回所有搜到的PoiEntity
    public List<PoiEntity>showPoiByUserChoice(String province,
                                              String city,
                                              String district,
                                              String poi_name,
                                              String poi_address,
                                              String poi_type){
        return poiMapper.showPoiByUserChoice(province, city,district,poi_name,poi_address,poi_type);
    }


    /*
     * 添加数据
     * */


    // 添加相关数据，最后返回是否添加成功
    public PoiEntity addNewPoiInMap(PoiEntity new_poiEntity){
        //PoiEntity poi = new PoiEntity(province,city,district,poi_name,poi_address,poi_type,latitude,longitude);
        if(poiMapper.addNewPoiInMap(new_poiEntity)==0){
            return null;
        }
        return new_poiEntity;
    }

    /*
     * 删除数据
     * */

    // 根据指定id删除数据，最后返回是否删除成功
    public boolean deletePoiByPoi_Id(Integer poi_id){
        return (poiMapper.deletePoiByPoi_Id(poi_id))>0;
    }



    /*
     * 更新数据
     * */

    // 更改指定的poi_id的指定数据，注意对于直辖市province和city可能为空，所以对这两个要加以限制，最后返回是否更新成功
    public boolean updatePoiByPoi_Id(Integer poi_id,
                          String province,
                          String city,
                          String district,
                          String poi_name,
                          String poi_address,
                          String poi_type,
                          Double latitude,
                          Double longitude){
        return (poiMapper.updatePoiByPoi_Id(poi_id,province,city,district,poi_name,poi_address,poi_type,latitude,longitude))>0;
    }




}
