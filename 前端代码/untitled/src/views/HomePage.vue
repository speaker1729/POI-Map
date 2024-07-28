<template>
  <div>
    <div id="popup" class="ol-popup">
      <div id="popup-content"></div>
    </div>
    <div class="search-container">
      <input type="text" placeholder="按城市查询" v-model="searchCity"/>
      <input type="text" placeholder="按区县查询" v-model="searchDistrict"/>
      <input type="text" placeholder="按名称查询" v-model="searchName"/>
      <input type="text" placeholder="按类型查询" v-model="searchType"/>
      <button @click="filterPOIData">确认</button>
      <button @click="clearSearch">取消</button>
    </div>
    <div id="map" class="map-container"></div>
    <div id="scale-line" class="scale-line"></div>
    <el-button @click="refreshMap" icon="el-icon-refresh" circle class="map-button refresh-button"></el-button>
    <el-button @click="addData" icon="el-icon-plus" circle class="map-button add-button"></el-button>
    <el-button @click="removeData" icon="el-icon-minus" circle class="map-button remove-button"></el-button>
    <el-button @click="editData" icon="el-icon-edit" circle class="map-button edit-button"></el-button>
    <el-button @click="handleClick" icon="el-icon-pie-chart" circle class="map-button stats-button"></el-button>
    <el-dialog title="地点数据统计" :visible="showStatisticsDialog" @close="closeStatisticsPopup">
      <div id="statistics-chart" style="width: 100%; height: 400px;"></div>
      <div class="chart-switch">
        <el-button type="text" @click="switchChart('province')">按省份</el-button>
        <el-button type="text" @click="switchChart('city')">按城市</el-button>
        <el-button type="text" @click="switchChart('district')">按区县</el-button>
        <el-button type="text" @click="switchChart('poi_type')">按类型</el-button>
      </div>
    </el-dialog>
    <el-button @click="switchLayer" class="map-button layer-button">切换地图图层</el-button>
    <el-button class="map-button jump-button" @click="jumpToWUHAN" type="primary">快捷跳转武汉</el-button>
    <el-button class="map-button heatmap-button" @click="createHeatmapLayer">热力图</el-button>
    <el-dialog title="添加地点数据" :visible="showAddPopup" @close="cancelAddData">
      <el-form :model="newPOI" ref="addPOIForm" label-width="120px">
        <el-form-item label="省份" prop="province">
          <el-input v-model="newPOI.province"></el-input>
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="newPOI.city"></el-input>
        </el-form-item>
        <el-form-item label="区县" prop="district" required>
          <el-input v-model="newPOI.district"></el-input>
        </el-form-item>
        <el-form-item label="地点名称" prop="poi_name" required>
          <el-input v-model="newPOI.poi_name"></el-input>
        </el-form-item>
        <el-form-item label="地点地址" prop="poi_address" required>
          <el-input v-model="newPOI.poi_address"></el-input>
        </el-form-item>
        <el-form-item label="地点类型" prop="poi_type" required>
          <el-input v-model="newPOI.poi_type"></el-input>
        </el-form-item>
        <el-form-item label="纬度">
          <el-input v-model="newPOI.latitude" disabled></el-input>
        </el-form-item>
        <el-form-item label="经度">
          <el-input v-model="newPOI.longitude" disabled></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="clearForm">清空</el-button>
        <el-button @click="cancelAddData">取消</el-button>
        <el-button type="primary" @click="confirmAddData">确认添加</el-button>
      </div>
    </el-dialog>
    <el-dialog title="编辑地点数据" :visible="showEditPopup" @close="cancelEditData">
      <el-form ref="editPOIForm" :model="editingPOI" label-width="120px">
        <el-form-item label="省份" prop="province">
          <el-input v-model="editingPOI.province"></el-input>
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="editingPOI.city"></el-input>
        </el-form-item>
        <el-form-item label="区县" prop="district" required>
          <el-input v-model="editingPOI.district" required></el-input>
        </el-form-item>
        <el-form-item label="地点名称" prop="poi_name" required>
          <el-input v-model="editingPOI.poi_name" required></el-input>
        </el-form-item>
        <el-form-item label="地点地址" prop="poi_address" required>
          <el-input v-model="editingPOI.poi_address" required></el-input>
        </el-form-item>
        <el-form-item label="地点类型" prop="poi_type" required>
          <el-input v-model="editingPOI.poi_type" required></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelEditData">取消</el-button>
        <el-button type="primary" @click="confirmEditData">确认修改</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios';
import * as echarts from 'echarts';
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import XYZ from 'ol/source/XYZ';
import {fromLonLat, toLonLat, transform} from 'ol/proj';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import {Icon, Style} from 'ol/style';
import Overlay from 'ol/Overlay';
import ScaleLine from 'ol/control/ScaleLine';
import {easeOut} from 'ol/easing';
import {boundingExtent} from 'ol/extent';
import locationIcon from '@/assets/location.png';
import HeatmapLayer from 'ol/layer/Heatmap';

export default {
  data() {
    return {
      map: null,
      currentLayerIndex: 0,
      mapLayers: [
        {
          id: 1, name: 'Gaode Map New', source: new XYZ({
            attributions: 'Map data © <a href="https://amap.com">Gaode Map</a>',
            url: 'https://wprd0{1-4}.is.autonavi.com/appmaptile?lang=zh_cn&size=1&style=7&x={x}&y={y}&z={z}',
            wrapX: false
          })
        },
        {
          id: 2, name: 'Gaode Satellite', source: new XYZ({
            attributions: 'Satellite imagery © <a href="https://amap.com">Gaode Map - Satellite</a>',
            url: 'https://webst02.is.autonavi.com/appmaptile?style=6&x={x}&y={y}&z={z}'
          })
        }
      ],
      poiData: [],
      vectorLayer: null,
      showChartDialog: false,
      showStatisticsDialog: false,
      statisticsData: null,
      searchCity: '',
      searchDistrict: '',
      searchName: '',
      searchType: '',
      isDeleteMode: false,
      isAddMode: false,
      showAddPopup: false,
      heatmapVisible: false,
      newPOI: {
        poi_id: null,
        province: "",
        city: "",
        district: "",
        poi_name: "",
        poi_address: "",
        poi_type: "",
        latitude: null,
        longitude: null
      },
      isEditMode: false,
      showEditPopup: false,
      editingPOI: {
        poi_id: null,
        province: "",
        city: "",
        district: "",
        poi_name: "",
        poi_address: "",
        poi_type: "",
        latitude: null,
        longitude: null
      }
    };
  },
  mounted() {
    this.getAllPoi();
    this.initMap();
    this.addLayer();
    this.createVectorLayer();
    this.addPOILayer();
    this.map.addControl(new ScaleLine());
    this.initOverlay();
  },
  methods: {
    getAllPoi() {
      axios.get('http://localhost:8082/api/poi/all') // 发送 GET 请求到后端的获取全部数据的端点
          .then(response => {
            // 在请求成功时，将返回的地点数据存储在组件的数据中
            this.poiData = response.data;
            console.log(this.poiData);
          })
          .catch(error => {
            // 处理请求失败的情况
            console.error('Error fetching all POI data:', error);
          });
    },
    initMap() {
      this.map = new Map({
        target: 'map',
        layers: [],
        view: new View({
          center: fromLonLat([116.4, 39.9]),
          zoom: 5,
        }),
      });
      // 地图左键点击事件处理逻辑
      this.map.on('click', (event) => {
        if (this.isEditMode) {
          const feature = this.map.forEachFeatureAtPixel(event.pixel, (feature) => feature);
          if (feature) {
            this.editingPOI = this.poiData.find(poi => poi.poi_id.toString() === feature.getId());
            if (this.editingPOI) {
              this.showEditPopup = true; // 显示编辑弹窗
            }
          }
          event.stopPropagation(); // 阻止其他地图点击事件
        } else if (this.isDeleteMode) { // 如果处于删除模式
          const feature = this.map.forEachFeatureAtPixel(event.pixel, (feature) => feature);
          if (feature) {
            const poiIdToRemove = feature.getId();
            this.deletePOI(poiIdToRemove); // 调用删除函数
            event.stopPropagation();
          }
          this.isDeleteMode = false;
          // 阻止默认点击事件的冒泡，即阻止地图的正常点击事件
          event.stopPropagation();
        } else if (this.isAddMode) { // 如果处于增添模式
          // 获取点击位置的经纬度
          const clickedCoordinate = this.map.getEventCoordinate(event.originalEvent);
          const [longitude, latitude] = transform(clickedCoordinate, 'EPSG:3857', 'EPSG:4326'); // Convert from Web Mercator to WGS84
          console.log(`Clicked Latitude: ${latitude}, Longitude: ${longitude}`);
          // 将经纬度填充到新增数据的经纬度字段中
          this.newPOI.longitude = longitude;
          this.newPOI.latitude = latitude;
          // 显示增添地点数据弹窗
          this.showAddPopup = true;
        }
      });
      this.map.getViewport().addEventListener('contextmenu', (event) => {
        event.preventDefault();
        // 检查是否处于增添模式，如果是则退出增添模式
        if (this.isAddMode) {
          this.isAddMode = false;
          this.showAddPopup = false;
          console.log("Add mode deactivated by right click");
        }
        // 检查是否处于删除模式，如果是则退出删除模式
        if (this.isDeleteMode) {
          this.isDeleteMode = false;
          console.log("Delete mode deactivated by right click");
        }
        // 检查是否处于编辑模式，如果是则退出编辑模式
        if (this.isEditMode) {
          this.isEditMode = false;
          this.showEditPopup = false;
          console.log("Edit mode deactivated by right click");
        }
      });
    },
    initOverlay() {
      this.overlay = new Overlay({
        element: document.getElementById('popup'),
        offset: [10, 15],
        autoPan: true,
        autoPanAnimation: {
          duration: 250,
        },
      });
      this.map.addOverlay(this.overlay);
      this.map.on('pointermove', (event) => {
        const feature = this.map.forEachFeatureAtPixel(event.pixel, (feature) => feature);
        if (feature) {
          const properties = feature.getProperties();
          let content = '';
          for (const key in properties) {
            if (key !== 'geometry' && key !== 'layer') {
              content += `${key}: ${properties[key]}<br>`;
            }
          }
          this.overlay.setPosition(event.coordinate);
          this.overlay.getElement().innerHTML = content;
        } else {
          this.overlay.setPosition(undefined);
        }
      });
    },
    addLayer() {
      this.map.addLayer(new TileLayer({
        source: this.mapLayers[this.currentLayerIndex].source
      }));
    },
    switchLayer() {
      this.map.getLayers().clear();
      this.currentLayerIndex = (this.currentLayerIndex + 1) % this.mapLayers.length;
      this.map.addLayer(new TileLayer({
        source: this.mapLayers[this.currentLayerIndex].source
      }));
      if (this.vectorLayer) {
        this.map.addLayer(this.vectorLayer);
      }
    },
    jumpToWUHAN() {
      const universityCoordinates = fromLonLat([114.316667, 30.583333]); // 武汉坐标
      this.map.getView().animate({
        center: universityCoordinates,
        zoom: 11,
        duration: 2000,
        easing: easeOut
      });
    },
    refreshMap() {
      this.poiData = [];
      const initialCenter = [116.4, 39.9];
      const initialZoom = 5;
      this.map.getView().animate({
        center: fromLonLat(initialCenter),
        zoom: initialZoom,
        duration: 2000,
        easing: easeOut
      });
    },
    addData() {
      // 切换至增添模式
      this.isAddMode = true;
      console.log("Add mode activated");
    },
    handleAddDataClick(event) {
      // 获取点击位置的经纬度
      const clickedCoordinate = event.coordinate; // 获取点击位置的投影坐标
      const [longitude, latitude] = toLonLat(clickedCoordinate); // 将投影坐标转换为经纬度
      // 将经纬度填充到新增数据的经纬度字段中
      this.newPOI.longitude = longitude;
      this.newPOI.latitude = latitude;
      // 打开增添地点数据弹窗
      this.showAddPopup = true;
    },
    confirmAddData() {
      // 验证新增POI数据的完整性
      if (!this.validateNewPOI(this.newPOI)) {
        console.error("New POI data is incomplete.");
        this.$message.error('请输入完整的地点信息');
        return;
      }
      // 发送POST请求到后端，添加新的POI
      axios.post('http://localhost:8082/api/poi/add', this.newPOI)
          .then(response => {
            // 确保响应数据有效并包含POI ID
            if (response.data && response.data.poi_id) {
              console.log("Add POI success:", response.data);
              // 将服务器返回的完整数据（包括从数据库返回的ID）添加到本地数据数组
              this.poiData.push(response.data);
              // 更新地图上的POI图层，以显示新添加的数据
              this.updatePOILayer(this.poiData);
              console.log("Added new POI and updated map.");
              this.$message.success('添加成功！');
            } else {
              throw new Error("Invalid response data received from the server.");
            }
          })
          .catch(error => {
            console.error("Failed to add POI:", error);
            this.$message.error(`添加失败：${error}`);
          })
          .finally(() => {
            // 无论请求成功还是失败，都将关闭增添地点的弹窗，并退出增添模式
            this.showAddPopup = false;
            this.isAddMode = false;
            console.log("Add mode deactivated");
          });
    },
    // 辅助函数：验证新POI数据的完整性
    validateNewPOI(poi) {
      // 验证必需的字段是否都有值
      return poi.district && poi.poi_name && poi.poi_address && poi.poi_type && !isNaN(poi.latitude) && !isNaN(poi.longitude);
    },
    cancelAddData() {
      this.clearForm();
      this.showAddPopup = false;
      this.isAddMode = false;
      console.log("Add mode deactivated");
      // 移除地图单击事件监听器
      this.map.un('click', this.handleAddDataClick);
    },
    removeData() {
      this.isDeleteMode = !this.isDeleteMode; // 切换删除模式的激活状态
      if (this.isDeleteMode) {
        console.log("Delete mode activated");
      } else {
        console.log("Delete mode deactivated");
      }
    },
    deletePOI(poiId) {
      axios.delete(`http://localhost:8082/api/poi/delete/${poiId}`)
          .then(response => {
            if (response.status === 200) {
              console.log(`POI ID为${poiId}的地点已成功删除。`);
              this.poiData = this.poiData.filter(poi => poi.poi_id.toString() !== poiId.toString());
              this.updatePOILayer(this.poiData);
              this.$message.success('删除成功！');
              const view = this.map.getView();
              const currentCenter = view.getCenter();
              const currentZoom = view.getZoom();
              view.setCenter(currentCenter);
              view.setZoom(currentZoom);
            } else {
              throw new Error('删除操作未成功。');
            }
          })
          .catch(error => {
            console.error('错误:', error);
            this.$message.error(`删除失败：${error}`);
          });
    },
    editData() {
      this.isEditMode = true;
      console.log("Edit mode activated");
    },
    confirmEditData() {
      if (!this.validateEditPOI(this.editingPOI)) {
        this.$message.error('请输入完整的地点信息');
        return;
      }
      this.updatePOIData(this.editingPOI); // 发送更新请求
      this.showEditPopup = false; // 关闭编辑弹窗
      this.isEditMode = false; // 退出编辑模式
      console.log("Edit confirmed and mode deactivated");
      this.$message.success('修改成功！');
    },
    validateEditPOI(poi) {
      // 验证必需的字段是否都有值
      return poi.district &&  poi.poi_name && poi.poi_address && poi.poi_type;
    },
    updatePOIData(poi) {
      axios.put(`http://localhost:8082/api/poi/update/${poi.poi_id}`, poi)
          .then(response => {
            if (response.data) {
              console.log(`POI ID为${poi.poi_id}的地点已成功更新。`);
              // 更新本地poiData数组
              this.poiData = this.poiData.map(p => p.poi_id === poi.poi_id ? poi : p);
              this.updatePOILayer(this.poiData); // 重新渲染地图层以反映更新
              const view = this.map.getView();
              const currentCenter = view.getCenter();
              const currentZoom = view.getZoom();
              view.setCenter(currentCenter);
              view.setZoom(currentZoom);

            } else {
              throw new Error('更新操作未成功。');
            }
          })
          .catch(error => {
            console.error('更新POI时出错:', error);
          });
    },
    cancelEditData() {
      this.showEditPopup = false;
      this.isEditMode = false;
      console.log("Edit cancelled and mode deactivated");
    },
    filterPOIData() {
      const params = {
        province: null,
        city: this.searchCity || null,
        district: this.searchDistrict || null,
        poi_name: this.searchName || null,
        poi_address: null,
        poi_type: this.searchType || null
      };
      axios.get('http://localhost:8082/api/poi/search', {params})
          .then(response => {
            this.poiData = response.data;
            this.updatePOILayer(this.poiData);  // 使用搜索结果更新地图图层
          })
          .catch(error => {
            console.error('Error searching POI data:', error);
          });
    },
    clearSearch() {
      // 清空所有搜索字段
      this.searchCity = '';
      this.searchDistrict = '';
      this.searchName = '';
      this.searchType = '';
    },
    clearForm() {
      // 清空表单数据
      this.newPOI = {
        poi_id: null,
        province: "",
        city: "",
        district: "",
        poi_name: "",
        poi_address: "",
        poi_type: "",
        latitude: null,
        longitude: null
      };
    },
    createVectorLayer() {
      this.vectorLayer = new VectorLayer({
        source: new VectorSource(),
      });
      this.map.addLayer(this.vectorLayer);
      // 添加地图单击事件监听器
      this.map.on('click', (event) => {
        this.map.forEachFeatureAtPixel(event.pixel, (feature) => {
          const coordinates = feature.getGeometry().getCoordinates();
          this.map.getView().animate({
            center: coordinates,
            zoom: 18,
            duration: 2000,
            easing: easeOut
          });
        });
      });
    },
    createFeature(poi) {
      const feature = new Feature({
        geometry: new Point(fromLonLat([poi.longitude, poi.latitude])),
        序号: poi.poi_id,
        省份: poi.province,
        城市: poi.city,
        区县: poi.district,
        名称: poi.poi_name,
        地址: poi.poi_address,
        类型: poi.poi_type,
        纬度: poi.latitude,
        经度: poi.longitude
      });
      feature.setStyle(this.createIconStyle());
      feature.setId(poi.poi_id.toString());
      return feature;
    },
    createIconStyle() {
      return new Style({
        image: new Icon({
          anchor: [0.5, 1],
          src: locationIcon,
          scale: 0.15
        })
      });
    },
    addPOILayer() {
      const vectorSource = this.vectorLayer.getSource();
      const features = this.poiData.map(poi => this.createFeature(poi));
      vectorSource.addFeatures(features);
    },
    updatePOILayer(poiData) {
      const vectorSource = this.vectorLayer.getSource();
      vectorSource.clear();
      const features = poiData.map(poi => this.createFeature(poi));
      vectorSource.addFeatures(features);
      if (!this.isDeleteMode && !this.isAddMode && !this.isEditMode) {
        this.jumpToFeatures(features);
      }
    },
    jumpToFeatures(features) {
      const view = this.map.getView();
      if (features.length > 0) {
        if (features.length === 1) {
          // 如果结果中只有一个特征，直接定位到这个特征的位置，并设置一个适当的缩放级别
          const singleFeature = features[0];
          const coordinates = singleFeature.getGeometry().getCoordinates();
          view.animate({
            center: coordinates,
            zoom: 16, // 为单个特征选择一个适合的缩放级别
            duration: 2000,
            easing: easeOut
          });
        } else {
          // 当存在多个特征时，计算所有特征的边界范围，并调整地图视图以适应这个范围
          let extent = boundingExtent(features.map(feature => feature.getGeometry().getExtent()));
          view.fit(extent, {
            padding: [50, 50, 50, 50],
            maxZoom: 10, // 确保不会缩放得太近
            duration: 2000,
            easing: easeOut
          });
        }
      } else {
        // 如果没有找到符合条件的特征，恢复到默认视图
        view.animate({
          center: fromLonLat([116.4, 39.9]), // 中心点回到默认位置
          zoom: 5, // 默认的缩放级别
          duration: 2000,
          easing: easeOut
        });
      }
    },
    createHeatmapLayer() {
      if (!this.heatmapVisible) {
        // 如果热力图未显示，则创建热力图并添加到地图中
        const heatmapLayer = new HeatmapLayer({
          source: new VectorSource({
            features: this.vectorLayer.getSource().getFeatures()
          }), // 使用矢量图层的数据源
          blur: 15,
          radius: 8,
          weight: function(feature) {
            // 这里可以根据需要计算每个特征的权重值
            return feature.get('weight');
          }
        });
        this.map.addLayer(heatmapLayer);
        // 将地图标点设为不可见
        this.vectorLayer.setVisible(false);
        // 更新热力图状态为已显示
        this.heatmapVisible = true;
      } else {
        // 如果热力图已显示，则移除热力图
        const heatmapLayer = this.map.getLayers().getArray().find(layer => layer instanceof HeatmapLayer);
        if (heatmapLayer) {
          this.map.removeLayer(heatmapLayer);
          // 将地图标点重新设为可见
          this.vectorLayer.setVisible(true);
          // 更新热力图状态为未显示
          this.heatmapVisible = false;
        }
      }
    },
    handleClick() {
      this.$nextTick(() => {
        this.showStatisticsPopup();
      });
    },
    showStatisticsPopup() {
      this.showStatisticsDialog = true;
      this.$nextTick(() => {
        this.calculateStatistics();
        this.renderChart();
      });
    },
    closeStatisticsPopup() {
      this.showStatisticsDialog = false;
    },
    calculateStatistics() {
      const statistics = {
        province: {},
        city: {},
        district: {},
        poi_type: {}
      };

      this.poiData.forEach(poi => {
        Object.keys(statistics).forEach(key => {
          const value = poi[key];
          if (value !== undefined && value !== null) {
            if (!(value in statistics[key])) {
              statistics[key][value] = 0;
            }
            statistics[key][value]++;
          }
        });
      });
      // 将统计数据转换为 echarts 所需的格式
      this.statisticsData = Object.keys(statistics).map(category => ({
        name: category,
        data: Object.keys(statistics[category]).map(key => ({
          name: key,
          value: statistics[category][key]
        }))
      }));
      const defaultData = this.statisticsData.find(item => item.name === 'poi_type');
      // 调用渲染图表方法，传递默认数据
      this.renderChart(defaultData);
    },
    switchChart(category) {
      // 根据用户选择的类别获取对应的数据
      const selectedData = this.statisticsData.find(item => item.name === category);
      // 重新设置统计图数据
      this.renderChart(selectedData);
    },
    renderChart(selectedData) {
      const chartContainer = document.getElementById('statistics-chart');
      const chart = echarts.init(chartContainer);
      const option = {
        title: {
          text: '地点数据统计',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        series: []
      };
      // 添加条件检查，确保 selectedData 不为 undefined
      if (selectedData) {
        option.series.push({
          name: selectedData.name,
          type: 'pie',
          radius: '55%',
          data: selectedData.data.map(item => ({
            name: item.name,
            value: item.value
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        });
      }
      // 使用 setOption 方法重新设置配置项
      chart.setOption(option);
    },
  },
};
</script>

<style>
* {
  margin: 0;
  padding: 0;
  overflow: hidden;
}

html, body {
  height: 100%;
  width: 100%;
}

.map-container {
  width: 100vw;
  height: 100vh;
}

.map-button {
  position: fixed;
  z-index: 1000;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 15px;
  background-color: #409eff;
  border: none;
  font-size: 14px;
  white-space: nowrap;
}

.refresh-button {
  top: 10px;
  left: 40px;
  height: 40px;
  width: 40px;
  border-radius: 50%;
}

.add-button {
  top: 10px;
  left: 80px;
  height: 40px;
  width: 40px;
  border-radius: 50%;
}

.remove-button {
  top: 10px;
  left: 130px;
  height: 40px;
  width: 40px;
  border-radius: 50%;
}

.edit-button {
  top: 10px;
  left: 180px;
  height: 40px;
  width: 40px;
  border-radius: 50%;
}

.jump-button .layer-button {
  width: 100px;
}

.jump-button {
  top: 60px;
  right: 10px;
}

.layer-button {
  top: 10px;
  right: 10px;
}

.heatmap-button {
  top: 110px;
  right: 10px;
}

.stats-button{
  top: 10px;
  left: 230px;
  height: 40px;
  width: 40px;
  border-radius: 50%;
}

.search-container {
  position: fixed;
  bottom: 10px;
  left: 53%;
  transform: translateX(-50%);
  display: flex;
  justify-content: space-between;
  width: 75%;
  padding: 8px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.search-container input {
  flex: 1;
  margin: 0 5px;
  padding: 8px 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  outline: none;
}

.search-container button {
  padding: 8px 16px;
  margin-left: 5px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  outline: none;
}

.search-container button:hover {
  background-color: #3071e0;
}

.ol-popup {
  background-color: rgba(255, 255, 255, 0.8);
  padding: 5px 10px;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  font-family: Arial, sans-serif;
  font-size: 14px;
}

.ol-popup:after {
  content: "";
  position: absolute;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 8px;
  border-color: transparent rgba(255, 255, 255, 0.8) transparent transparent;
  top: 100%;
  left: 50%;
  margin-left: -8px;
}

.ol-popup-content p {
  margin-bottom: 5px;
}

.ol-popup-content strong {
  color: #333;
}

</style>
