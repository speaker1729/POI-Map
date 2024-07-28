package com.example.poi_map.Mapper;

import com.example.poi_map.Entity.PoiEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PoiMapper {
    /*
    * 单查询
    * */

    //查询所有POI
    @Select("SELECT * FROM poi")
    List<PoiEntity> showAllPoi();

    //根据poi_id查询
    @Select("SELECT * FROM poi WHERE poi_id = #{poi_id}")
    List<PoiEntity> showPoiByPoi_Id(@Param("poi_id") Integer poi_id);


    //根据距离查询
    @Select("SELECT * FROM poi WHERE (6371 * acos(cos(radians(#{latitude})) * cos(radians(latitude)) * cos(radians(longitude) - radians(#{longitude})) + sin(radians(#{latitude})) * sin(radians(latitude)))) < #{radius}")
    List<PoiEntity> findNearbyPoi(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("radius") double radius);


    /*
    //根据省份查询
    @Select("SELECT * FROM poi WHERE province = #{province}")
    List<PoiEntity> showPoiByProvince(@Param("province") String province);

    //根据城市查询
    @Select("SELECT * FROM poi WHERE city = #{city}")
    List<PoiEntity> showPoiByCity(@Param("city") String city);

    //根据地区查询
    @Select("SELECT * FROM poi WHERE district = #{district}")
    List<PoiEntity> showPoiByDistrict(@Param("district") String district);

    //根据POI名查询
    @Select("SELECT * FROM poi WHERE poi_name = #{poi_name}")
    List<PoiEntity> showPoiByPoi_Name(@Param("poi_name") String poi_name);

    //根据POI地址询
    @Select("SELECT * FROM poi WHERE poi_address = #{poi_address}")
    List<PoiEntity> showPoiByPoi_Address(@Param("poi_address") String poi_address);

    //根据POI类型询
    @Select("SELECT * FROM poi WHERE poi_type = #{poi_type}")
    List<PoiEntity> showPoiByPoi_Type(@Param("poi_type") String poi_type);

*/


    /*
     * 组合查询
     * */

    //用一个函数全封装，即可实现所有的组合查询，传参数进来，如果这个限制参数用户未选择，就会传进来是NULL，此时在这个Select语句中就不会有这个WHERE限制

    @Select("<script>"
            + "SELECT * FROM poi"
            + "<where>"
            + "<if test='province != null'>AND province = #{province}</if>"
            + "<if test='city != null'>AND city = #{city}</if>"
            + "<if test='district != null'>AND district = #{district}</if>"
            + "<if test='poi_name != null'>AND poi_name = #{poi_name}</if>"
            + "<if test='poi_address != null'>AND poi_address = #{poi_address}</if>"
            + "<if test='poi_type != null'>AND poi_type = #{poi_type}</if>"
            + "</where>"
            + "</script>")
    List<PoiEntity> showPoiByUserChoice(@Param("province") String province,
                                            @Param("city") String city,
                                            @Param("district") String district,
                                            @Param("poi_name") String poi_name,
                                            @Param("poi_address") String poi_address,
                                            @Param("poi_type") String poi_type);




    /*
     * 添加数据
     * */

    /*
    @Insert("INSERT INTO "
            + "poi(province,city,district,poi_name,poi_address,poi_type,latitude,longitude) "
            + "VALUES(#{province},#{city},#{district},#{poi_name},#{poi_address},#{poi_type},#{latitude},#{longitude})")
    int addNewPoiInMap(@Param("province") String province,
                        @Param("city") String city,
                        @Param("district") String district,
                        @Param("poi_name") String poi_name,
                        @Param("poi_address") String poi_address,
                        @Param("poi_type") String poi_type,
                        @Param("latitude") Double latitude,
                        @Param("longitude") Double longitude);

*/

    @Insert("INSERT INTO poi(province, city, district, poi_name, poi_address, poi_type, latitude, longitude) " +
            "VALUES(#{province}, #{city}, #{district}, #{poi_name}, #{poi_address}, #{poi_type}, #{latitude}, #{longitude})")
    @Options(useGeneratedKeys = true, keyProperty = "poi_id")
    int addNewPoiInMap(PoiEntity poi);

    /*
     * 删除数据
     * */

    // 根据指定id删除数据，返回删除个数，因为是根据唯一值来搜索删除的，所以最多也只删除一个，所一返回值要么是1，要么是0，可以表示是否删除成功
    @Delete("DELETE FROM poi WHERE poi_id = #{poi_id}")
    int deletePoiByPoi_Id(@Param("poi_id")  Integer poi_id);


    /*
     * 更新数据
     * */

    // 更改指定的poi_id的指定数据，注意对于直辖市province和city可能为空，所以对这两个要加以限制
    @Update("<script>"
            + "UPDATE poi"
            + "<set>"
            + "<if test='province != null'>province = <choose><when test='province==\"NULL_VALUE\"'>null</when><otherwise>#{province}</otherwise></choose>,</if>"
            + "<if test='city != null'>city = <choose><when test='city==\"NULL_VALUE\"'>null</when><otherwise>#{city}</otherwise></choose>,</if>"
            + "<if test='district != null'>district = #{district},</if>"
            + "<if test='poi_name != null'>poi_name = #{poi_name},</if>"
            + "<if test='poi_address != null'>poi_address = #{poi_address},</if>"
            + "<if test='poi_type != null'>poi_type = #{poi_type},</if>"
            + "<if test='latitude != null'>latitude = #{latitude},</if>"
            + "<if test='longitude != null'>longitude = #{longitude},</if>"
            + "</set>"
            + "WHERE poi_id = #{poi_id}"
            + "</script>")
    int updatePoiByPoi_Id(@Param("poi_id") Integer poi_id,
                          @Param("province") String province,
                          @Param("city") String city,
                          @Param("district") String district,
                          @Param("poi_name") String poi_name,
                          @Param("poi_address") String poi_address,
                          @Param("poi_type") String poi_type,
                          @Param("latitude") Double latitude,
                          @Param("longitude") Double longitude);
}
