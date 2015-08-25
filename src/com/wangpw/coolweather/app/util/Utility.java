package com.wangpw.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

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
	/*
	 * �������������ص�json���ݣ����������������ݴ��浽����
	 */
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject jsonResult = jsonObject.getJSONObject("result");
			JSONObject jsonSK = jsonResult.getJSONObject("sk");
			JSONObject jsonToday = jsonResult.getJSONObject("today");
			String cityName = jsonToday.getString("city");
			String temp = jsonToday.getString("temperature");
			String weatherDesc = jsonToday.getString("weather");
			String publishTime = jsonSK.getString("time");
			saveWeatherInfo(context,cityName,temp,weatherDesc,publishTime);
		} catch (Exception e) {
			Log.i("HTTP", "Error in handleWeatherResponse");
		}
	}
	/*
	 * �����������ص�����������Ϣ���浽SharedPreferences�ļ���
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String temp, String weatherDesc, String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_temp", temp);
		editor.putString("weather_desc", weatherDesc);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
		
	}
}
