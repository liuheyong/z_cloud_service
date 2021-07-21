//package com.cloud.service.impl;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.yiche.mapi.platform.common.result.MapiAppResult;
//import com.yiche.mapi.platform.logs.LoggerUtil;
//import com.yiche.mapi.statistic.business.ipip.BackendIpipService;
//import com.yiche.mapi.statistic.common.constants.ESConst;
//import com.yiche.mapi.statistic.common.utils.DateUtil;
//import com.yiche.mapi.statistic.common.utils.ESUtil;
//import com.yiche.mapi.statistic.entity.*;
//import com.yiche.mapi.statistic.mapper.FlowApiUrlPvMapper;
//import com.yiche.mapi.statistic.mapper.FlowAppRespMapper;
//import com.yiche.mapi.statistic.mapper.FlowRegionCollectMapper;
//import com.yiche.mapi.statistic.object.param.RespApplicationParam;
//import com.yiche.mapi.statistic.service.*;
//import com.yiche.mapi.statistic.utils.CommonUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//
///**
// * 后台部门查询es
// * @author lixianbin
// *
// */
//@Service
//public class BackendStatisticServiceImpl implements BackendStatisticService{
//
//	@Autowired
//	public RestTemplate restTemplate;
//	@Autowired
//	public ESUtil esUtil;
//
//	@Value("${domain.es.monitor-es-url}")
//	private String monitorESUrl;
//	@Value("${domain.es.monitor-es-account}")
//	private String monitorESAccount;
//	@Value("${domain.es.monitor-es-pwd}")
//	private String monitorESPwd;
//
//	public int querySize = ESConst.querySize;// es每页查询条数
//	@Autowired
//	private FlowRegionCollectService flowRegionCollectService;
//	@Autowired
//	private FlowAppRespService flowAppRespService;
//	@Autowired
//	private FlowAppRespMapper flowAppRespMapper;
//	@Autowired
//	private FlowApiUrlPvMapper flowApiUrlPvMapper;
//	@Autowired
//	private FlowRegionCollectMapper flowRegionCollectMapper;
//	@Autowired
//	private BackendIpipService backendIpipService;
//
//	/**
//	 * 应用服务
//	 */
//	@Autowired
//	private FlowAppService flowAppService;
//	@Autowired
//	private FlowAppPvService flowAppPvService;
//	@Autowired
//	private FlowApiUrlPvService flowApiUrlPvService;
//
//	/**
//	 * pv访问量统计
//	 */
//	@Override
//	public void getPvStatistics(long beginTime, long endTime) {
//		LoggerUtil.logger().info("后台pv访问量统计");
//		// 获取es查询语句
//		String queryJson = esUtil.getQueryJsonStr("esSearchJson/backend_pvStatistics.json");
//		if (queryJson == null || queryJson.length() == 0) {
//			LoggerUtil.logger().error("pv访问量统计-获取json查询语句出错");
//			return;
//		}
//		// 初始化从0条开始查询
//		queryJson = queryJson.replace("beginTime", beginTime + "").replace("endTime", endTime + "");
//
//		HttpHeaders headers = new HttpHeaders();
//		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
//		headers.setContentType(type);
//		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//		CommonUtil.putESUsernameAndPassword(headers, monitorESAccount, monitorESPwd);
//		HttpEntity<String> formEntity = new HttpEntity<>(queryJson, headers);
//		String url = monitorESUrl + ESConst.BACKEND_ES_INDEX;
//		JSONObject result = restTemplate.postForEntity(url, formEntity, JSONObject.class).getBody();
//
//		// 解析查询结果
//		if (result != null && !result.containsKey("error")) {
//			try {
//				JSONArray bucketsArray = result.getJSONObject("aggregations").getJSONObject("data").getJSONArray("buckets");
//				for (int i = 0; bucketsArray != null && i < bucketsArray.size(); i++) {
//					// 获取应用名称
//					String application = bucketsArray.getJSONObject(i).getString("key");
//					if(application == null || application.equals("")) {
//						continue;
//					}
//					long pvValue = bucketsArray.getJSONObject(i).getLongValue("doc_count");
//					FlowApp flowApp = flowAppService.sgetFlowAppByAppContextPath(application);
//					if (flowApp != null) {
//						FlowAppPv flowAppPv = new FlowAppPv();
//						flowAppPv.setApplication(application);
//						flowAppPv.setBizlineId(flowApp.getBizlineId());
//						flowAppPv.setCreateTime(new Date());
//						flowAppPv.setDeptId(flowApp.getDeptId());
//						flowAppPv.setStartTime(DateUtil.timeStampToTime(beginTime));
//						flowAppPv.setEndTime(DateUtil.timeStampToTime(endTime));
//						flowAppPv.setPvValue(pvValue);
//						flowAppPv.setUpdateTime(new Date());
//						flowAppPv.setAppId(flowApp.getId());
//						flowAppPvService.insert(flowAppPv);
//					}
//				}
//			} catch (Exception e) {
//				LoggerUtil.logger().error("pv访问量统计-ES查询结果错误", e);
//			}
//		}
//	}
//
//	/**
//	 * 访问时长统计
//	 * @author lixianbin
//	 */
//	@Override
//	public void getRespTimeStatistics(long beginTime, long endTime) {
//		// 类型（1：<100ms,2:100-300ms,3:300-500ms,4:>500ms）
//		this.getMapiHourStatistics(beginTime, endTime, 1);
//		this.getMapiHourStatistics(beginTime, endTime, 2);
//		this.getMapiHourStatistics(beginTime, endTime, 3);
//		this.getMapiHourStatistics(beginTime, endTime, 4);
//	}
//
//	/**
//	 * 针对不同类型的访问时长做统计
//	 * @author lixianbin
//	 * @param beginTime 开始时间
//	 * @param endTime 截止时间
//	 * @param hourType 类型（1：<100ms,2:100-300ms,3:300-500ms,4:>500ms）
//	 */
//	public void getMapiHourStatistics(long beginTime, long endTime, int hourType) {
//		// 获取查询语句
//		String queryJson = esUtil.getQueryJsonStr("esSearchJson/backend_hoursStatistics.json");
//
//		queryJson = queryJson.replace("beginTime", beginTime + "").replace("endTime", endTime + "").replace("pageIndex","0");
//		// 时长类型（1：<100ms,2:100-300ms,3:300-500ms,4:>500ms）
//		int hoursStart = 0;
//		int hoursEnd = 0;
//		if (hourType == 1) {
//			hoursStart = 0;
//			hoursEnd = 100;
//			queryJson = queryJson.replace("hoursStart", "0").replace("hoursEnd", "100");
//		} else if (hourType == 2) {
//			hoursStart = 100;
//			hoursEnd = 300;
//			queryJson = queryJson.replace("hoursStart", "100").replace("hoursEnd", "300");
//		} else if (hourType == 3) {
//			hoursStart = 300;
//			hoursEnd = 500;
//			queryJson = queryJson.replace("hoursStart", "300").replace("hoursEnd", "500");
//		} else if (hourType == 4) {
//			hoursStart = 500;
//			hoursEnd = 10000;
//			queryJson = queryJson.replace("hoursStart", "500").replace("hoursEnd", "10000");
//		}
//
//		// 查询
//		HttpHeaders headers = new HttpHeaders();
//		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
//		headers.setContentType(type);
//		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//		CommonUtil.putESUsernameAndPassword(headers, monitorESAccount, monitorESPwd);
//		HttpEntity<String> formEntity = new HttpEntity<>(queryJson, headers);
//		String url = monitorESUrl + ESConst.BACKEND_ES_INDEX;
//		JSONObject result = restTemplate.postForEntity(url, formEntity, JSONObject.class).getBody();
//
//		// 解析查询结果
//		if (result != null && !result.containsKey("error")) {
//			try {
//				JSONArray bucketsArray = result.getJSONObject("aggregations").getJSONObject("data").getJSONArray("buckets");
//				for (int i = 0; bucketsArray != null && i < bucketsArray.size(); i++) {
//					String application = bucketsArray.getJSONObject(i).getString("key");
//					long pvValue = bucketsArray.getJSONObject(i).getLongValue("doc_count");
//					if(application != null && !application.equals("")) {
//						FlowApp flowApp = flowAppService.sgetFlowAppByAppContextPath(application);
//						if (flowApp != null) {
//							FlowAppResp flowAppResp = new FlowAppResp();
//							flowAppResp.setAppId(flowApp.getId());
//							flowAppResp.setApplication(application);
//							flowAppResp.setBizlineId(flowApp.getBizlineId());
//							flowAppResp.setCreateTime(new Date());
//							flowAppResp.setDeptId(flowApp.getDeptId());
//							flowAppResp.setStartTime(DateUtil.timeStampToTime(beginTime));
//							flowAppResp.setEndTime(DateUtil.timeStampToTime(endTime));
//							flowAppResp.setType(hourType);
//							flowAppResp.setUpdateTime(new Date());
//							flowAppResp.setValue(pvValue);
//							// 应用在数据库中存在并且创建时间是当天时更新，否则插入
//							RespApplicationParam param = new RespApplicationParam();
//							param.setApplication(application);
//							param.setType(hourType);
//							FlowAppResp resultEntity = flowAppRespService.queryOneByApplication(param);
//							if(resultEntity != null) {
//								resultEntity.setValue(resultEntity.getValue()+pvValue);
//								resultEntity.setUpdateTime(new Date());
//								flowAppRespMapper.updateById(resultEntity);
//							}else {
//								flowAppRespService.insert(flowAppResp);
//							}
//						}
//					}
//				}
//				// 重复分页查询
//				//getDataForHourPage(beginTime, endTime, hoursStart, hoursEnd, 1, querySize,hourType);
//			} catch (Exception e) {
//				LoggerUtil.logger().error("访问时长做统计ES查询结果错误", e);
//			}
//		}
//	}
//
//	/**
//	 * @author lixianbin
//	 * 访问url接口统计
//	 */
//	@Override
//	public void getURLStatistics(long beginTime, long endTime) {
//		// 获取es查询语句
//		String queryJson = esUtil.getQueryJsonStr("esSearchJson/backend_urlStatistics.json");
//		if (queryJson == null || queryJson.length() == 0) {
//			LoggerUtil.logger().error("访问url接口统计-获取json查询语句出错");
//			return;
//		}
//		// 初始化从0条开始查询
//		queryJson = queryJson.replace("beginTime", beginTime + "").replace("endTime", endTime + "");
//		long time1=System.currentTimeMillis();
//		HttpHeaders headers = new HttpHeaders();
//		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
//		headers.setContentType(type);
//		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//		CommonUtil.putESUsernameAndPassword(headers, monitorESAccount, monitorESPwd);
//		HttpEntity<String> formEntity = new HttpEntity<>(queryJson, headers);
//		String url = monitorESUrl + ESConst.BACKEND_ES_INDEX;
//		JSONObject result = restTemplate.postForEntity(url, formEntity, JSONObject.class).getBody();
//
//		// 解析查询结果
//		Set<String> set = new HashSet<String>();
//		Map<String, Long> resultMap = new HashMap<String, Long>();
//		if (result != null && !result.containsKey("error")) {
//			try {
//				JSONArray bucketsArray = result.getJSONObject("aggregations").getJSONObject("data").getJSONArray("buckets");
//				for (int i = 0; bucketsArray != null && i < bucketsArray.size(); i++) {
//					String appURI = bucketsArray.getJSONObject(i).getString("key");
//					long pvValue = bucketsArray.getJSONObject(i).getLongValue("doc_count");
//					if(appURI != null && !"".equals(appURI)) {
//						String appURIArray[] = appURI.split("-split-");
//						if(appURIArray.length==2) {
//							String application = appURIArray[0];
//							String getUrl = appURIArray[1];
//							FlowApp flowApp = flowAppService.getFlowAppByApp(application);
//							if (flowApp != null) {
//								FlowApiUrlPv flowApiUrlPv = new FlowApiUrlPv();
//								flowApiUrlPv.setApiPv(pvValue);
//								flowApiUrlPv.setApiUrl(getUrl);
//								flowApiUrlPv.setAppId(flowApp.getId());
//								flowApiUrlPv.setApplication(application);
//								flowApiUrlPv.setBizlineId(flowApp.getBizlineId());
//								flowApiUrlPv.setCreateTime(new Date());
//								flowApiUrlPv.setDeptId(flowApp.getDeptId());
//								flowApiUrlPv.setEndTime(DateUtil.timeStampToTime(endTime));
//								flowApiUrlPv.setStartTime(DateUtil.timeStampToTime(beginTime));
//								flowApiUrlPv.setUpdateTime(new Date());
//								flowApiUrlPvService.insert(flowApiUrlPv);
//							}
//						}
//					}
//				}
//				this.getUrlDataForPage(beginTime, endTime, 1, querySize, set,resultMap);
//			} catch (Exception e) {
//				LoggerUtil.logger().error("访问url接口统计-ES查询结果错误", e);
//			}
//		}
//		long time2=System.currentTimeMillis();
//		LoggerUtil.logger().info("后台get URL 当前程序耗时："+(time2-time1)+"ms");
//	}
//
//	/**
//	 * 访问URL分页查询数据
//	 * @author lixianbin
//	 * @param beginTime 开始时间
//	 * @param endTime 截止时间
//	 * @param hoursStart 时长开始范围
//	 * @param hoursEnd 时长截止范围
//	 * @param pageIndex 第几页
//	 * @param pageSize 每页显示条数
//	 * @param set
//	 * @param resultMap
//	 */
//	public void getUrlDataForPage(long beginTime, long endTime, int pageIndex, int pageSize,Set<String> set,
//			Map<String, Long> resultMap) {
//		// 获取查询语句
//		String queryJson = esUtil.getQueryJsonStr("esSearchJson/mapi_pvStatistics.json");
//		queryJson = queryJson.replace("beginTime", beginTime + "").replace("endTime", endTime + "").replace("pageIndex",
//				pageIndex * pageSize + "").replace("querySize", querySize+"");
//		// 查询
//		HttpHeaders headers = new HttpHeaders();
//		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
//		headers.setContentType(type);
//		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//		CommonUtil.putESUsernameAndPassword(headers, monitorESAccount, monitorESPwd);
//		HttpEntity<String> formEntity = new HttpEntity<>(queryJson, headers);
//		String url = monitorESUrl + ESConst.BACKEND_ES_INDEX;
//		JSONObject result = restTemplate.postForEntity(url, formEntity, JSONObject.class).getBody();
//		if (result != null && !result.containsKey("error")) {
//			try {
//				JSONArray hitsArray = result.getJSONObject("hits").getJSONArray("hits");
//				if (hitsArray != null && hitsArray.size() > 0) {
//					for (int i = 0; hitsArray != null && i < hitsArray.size(); i++) {
//						// 获取访问的URL
//						String requestedURI = hitsArray.getJSONObject(i).getJSONObject("_source").getString("request");
//						// 获取访问应用名称
//						String application = hitsArray.getJSONObject(i).getJSONObject("_source").getString("context_path");
//						if(requestedURI != null && !"".equals(requestedURI)) {
//							if(requestedURI.contains("?")) {
//								requestedURI = requestedURI.split("\\?")[0];
//							}
//							String index = requestedURI+"lixb"+application;
//							set.add(index);
//							if (resultMap.get(index) != null) {
//								resultMap.put(index, resultMap.get(index) + 1);
//							} else {
//								resultMap.put(index, 1L);
//							}
//						}
//					}
//					// 重复分页查询
//					getUrlDataForPage(beginTime, endTime, pageIndex+1, querySize, set, resultMap);
//				} else {
//					return;
//				}
//			} catch (Exception e) {
//				LoggerUtil.logger().error("流量统计解析ES查询结果错误", e);
//			}
//		}
//	}
//
//	/**
//	 * 根据IP获取省份接口统计
//	 */
//	@Override
//	public void getRegionStatistics(long beginTime, long endTime) {
//		// 获取es查询语句
//		String queryJson = esUtil.getQueryJsonStr("esSearchJson/backend_regionStatistics.json");
//		if (queryJson == null || queryJson.length() == 0) {
//			LoggerUtil.logger().error("根据IP获取省份接口统计-获取json查询语句出错");
//			return;
//		}
//		// 开始查询
//		queryJson = queryJson.replace("beginTime", beginTime + "").replace("endTime", endTime + "");
//
//		HttpHeaders headers = new HttpHeaders();
//		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
//		headers.setContentType(type);
//		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//		CommonUtil.putESUsernameAndPassword(headers, monitorESAccount, monitorESPwd);
//		HttpEntity<String> formEntity = new HttpEntity<>(queryJson, headers);
//		String url = monitorESUrl + ESConst.BACKEND_ES_INDEX;
//		JSONObject result = restTemplate.postForEntity(url, formEntity, JSONObject.class).getBody();
//
//		// 解析查询结果
//		if (result != null && !result.containsKey("error")) {
//			try {
//				JSONArray bucketsArray = result.getJSONObject("aggregations").getJSONObject("data").getJSONArray("buckets");
//				for (int i = 0; bucketsArray != null && i < bucketsArray.size(); i++) {
//					// 获取访问的realIP
//					String realIP = bucketsArray.getJSONObject(i).getString("key");
//					long pvValue = bucketsArray.getJSONObject(i).getLongValue("doc_count");
////					String provice = IpipUtil.getProviceByClientIp(realIP);
//					MapiAppResult<String> proviceResult =  backendIpipService.getProviceByClientIp(realIP);
//					String provice = proviceResult.getData();
//	//				LoggerUtil.logger().info("---realIP address:"+realIP+"---province:"+provice);
//					if(provice == null || provice.equals("")) {
//						continue;
//					}
//					FlowRegionCollect regionCollect = flowRegionCollectService.queryOneByProvice(provice);
//					// 省份在数据库中存在并且创建时间是当天时更新，否则插入
//					if(regionCollect != null) {
//						regionCollect.setUpdateTime(new Date());
//						regionCollect.setPv(regionCollect.getPv()+pvValue);
//						flowRegionCollectMapper.updateById(regionCollect);
//					}else {
//						FlowRegionCollect entity = new FlowRegionCollect();
//						entity.setCreateTime(new Date());
//						entity.setProvice(provice);
//						entity.setPv(pvValue);
//						entity.setUpdateTime(new Date());
//						flowRegionCollectMapper.insert(entity);
//					}
//				}
//			} catch (Exception e) {
//				LoggerUtil.logger().error("访问url接口统计-ES查询结果错误", e);
//			}
//		}
//	}
//}
