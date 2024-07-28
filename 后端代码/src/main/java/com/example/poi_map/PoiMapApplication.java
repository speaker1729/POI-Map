package com.example.poi_map;

import com.example.poi_map.Entity.PoiEntity;
import com.example.poi_map.Mapper.PoiMapper;
import com.example.poi_map.Service.PoiService;
import org.apache.ibatis.jdbc.Null;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class PoiMapApplication {

	public static void main(String[] args) {
		//SpringApplication.run(PoiMapApplication.class, args);

		// 启动Spring应用
		ConfigurableApplicationContext context = SpringApplication.run(PoiMapApplication.class, args);

//		// 从Spring上下文中获取PoiService实例
//		PoiService poiService = context.getBean(PoiService.class);
//
//		// 调用showAllPoi方法并打印结果
//		//List<PoiEntity> poiEntities = poiService.showAllPoi();
//
//		int a = -1;
//		boolean isSuccessAdded = false;
//		boolean isSuccessDeleted = false;
//		boolean isSuccessUpdated = false;
//
//
//		List<PoiEntity> poiEntities = poiService.showPoiByUserChoice(null,null,null,null,null,null);
//
//		for(int i=0;i<poiEntities.size();i++)
//		{
//			System.out.println(poiEntities.get(i).getPoi_id()+"\t"
//					+poiEntities.get(i).getProvince()+"\t"
//					+poiEntities.get(i).getCity()+"\t"
//					+poiEntities.get(i).getDistrict()+"\t"
//					+poiEntities.get(i).getPoi_name()+"\t"
//					+poiEntities.get(i).getPoi_address()+"\t"
//					+poiEntities.get(i).getPoi_type()+"\t"
//					+poiEntities.get(i).getLatitude()+"\t"
//					+poiEntities.get(i).getLongitude());
//		}
//
//		//List<PoiEntity> poiEntities = poiService.findNearbyPoi(114.624913,30.461612,10);
//
//
//
//		List<PoiEntity> test = poiService.showPoiByUserChoice(null,null,null,null,null,null);
//
//
//		if(a!=-1)
//		{
//			switch (a)
//			{
//
//				case 0:
//					// 搜索测试 a0
//
//					for(int i=0;i<poiEntities.size();i++)
//					{
//						System.out.println(poiEntities.get(i).getPoi_id()+"\t"
//								+poiEntities.get(i).getProvince()+"\t"
//								+poiEntities.get(i).getCity()+"\t"
//								+poiEntities.get(i).getDistrict()+"\t"
//								+poiEntities.get(i).getPoi_name()+"\t"
//								+poiEntities.get(i).getPoi_address()+"\t"
//								+poiEntities.get(i).getPoi_type()+"\t"
//								+poiEntities.get(i).getLatitude()+"\t"
//								+poiEntities.get(i).getLongitude());
//					}
//					break;
//
//				case 1:
//					// 添加测试 a1
//					PoiEntity poi = new PoiEntity("湖北省","武汉市","洪山区","蜜雪冰城","中国地质大学（武汉）未来城新校区","餐饮服务",114.624913,30.461612);
//					PoiEntity RePoi = poiService.addNewPoiInMap(poi);
//
//					if(RePoi!=null) {
//						System.out.println("成功添加");
//						System.out.println(RePoi.getPoi_id()+"\t"
//								+RePoi.getProvince()+"\t"
//								+RePoi.getCity()+"\t"
//								+RePoi.getDistrict()+"\t"
//								+RePoi.getPoi_name()+"\t"
//								+RePoi.getPoi_address()+"\t"
//								+RePoi.getPoi_type()+"\t"
//								+RePoi.getLatitude()+"\t"
//								+RePoi.getLongitude());
//					}
//					break;
//
//
//				case 2:
//					// 删除测试 a2
//					isSuccessDeleted = poiService.deletePoiByPoi_Id(test.get(0).getPoi_id());
//
//					if (isSuccessDeleted){
//						System.out.println("成功删除");
//					}
//					break;
//
//				case 3:
//					// 添加测试 a3
//					isSuccessUpdated = poiService.updatePoiByPoi_Id(test.get(0).getPoi_id(),null,null,null,"桥头排骨",null,null,null,null);
//
//					if (isSuccessUpdated){
//						System.out.println("成功更新");
//					}
//					break;
//
//				default:
//					break;
//			}
//		}
//
//
//
//	}
	}
}
