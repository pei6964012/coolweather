package com.wangpw.coolweather.app.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.wangpw.coolweather.app.db.CoolWeatherDB;
import com.wangpw.coolweather.app.model.City;
import com.wangpw.coolweather.app.model.County;
import com.wangpw.coolweather.app.model.Province;

public class Utility {

	/*
	 * �����ʹ�����������ص�ʡ������
	 */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {			
			try {
				JSONObject jsonObject = new JSONObject(response);
				if(jsonObject.getInt("status")!=0){
					return false;
				}
				JSONArray jsonArrayResult = jsonObject.getJSONArray("result");
				JSONArray jsonArrayProvinces = jsonArrayResult.getJSONArray(0);
				if(jsonArrayProvinces!=null&&jsonArrayProvinces.length()>0){
					for(int i=0;i<jsonArrayProvinces.length();i++){
						JSONObject jsonObjectProvince = jsonArrayProvinces.getJSONObject(i);
						Province province = new Province();
						province.setProvinceCode(jsonObjectProvince.getString("id"));
						province.setProvinceName(jsonObjectProvince.getString("fullname"));
						//������������Provinceʵ���洢�����ݿ���
						coolWeatherDB.saveProvince(province);
					}
				return true;
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return false;
	}
	/*
	 * �����ʹ�����������ص��м�����
	 */
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response,int provinceId) {
		if (!TextUtils.isEmpty(response)) {			
			try {
				JSONObject jsonObject = new JSONObject(response);
				if(jsonObject.getInt("status")!=0){
					return false;
				}
				JSONArray jsonArrayResult = jsonObject.getJSONArray("result");
				JSONArray jsonArrayCities = jsonArrayResult.getJSONArray(0);
				if(jsonArrayCities!=null&&jsonArrayCities.length()>0){
					for(int i=0;i<jsonArrayCities.length();i++){
						JSONObject jsonObjectCity = jsonArrayCities.getJSONObject(i);
						City city = new City();
						city.setCityCode(jsonObjectCity.getString("id"));
						city.setCityName(jsonObjectCity.getString("fullname"));
						city.setProvinceId(provinceId);
						//������������Cityʵ���洢�����ݿ���
						coolWeatherDB.saveCity(city);
						
					}
				return true;
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return false;
	}
	/*
	 * �����ʹ�����������ص��ؼ�����
	 */
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
			String response,int cityId) {
		if (!TextUtils.isEmpty(response)) {			
			try {
				JSONObject jsonObject = new JSONObject(response);
				if(jsonObject.getInt("status")!=0){
					return false;
				}
				JSONArray jsonArrayResult = jsonObject.getJSONArray("result");
				JSONArray jsonArrayCounties = jsonArrayResult.getJSONArray(0);
				if(jsonArrayCounties!=null&&jsonArrayCounties.length()>0){
					for(int i=0;i<jsonArrayCounties.length();i++){
						JSONObject jsonObjectCounty = jsonArrayCounties.getJSONObject(i);
						County county = new County();
						county.setCountyCode(jsonObjectCounty.getString("id"));
						county.setCountyName(jsonObjectCounty.getString("fullname"));
						county.setCityId(cityId);
						//������������Cityʵ���洢�����ݿ���
						coolWeatherDB.saveCounty(county);						
					}
				return true;
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return false;
	}
}
