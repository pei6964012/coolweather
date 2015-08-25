package com.wangpw.coolweather.app.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.wangpw.coolweather.app.R;
import com.wangpw.coolweather.app.util.HttpCallbackListener;
import com.wangpw.coolweather.app.util.HttpUtil;
import com.wangpw.coolweather.app.util.StringUtil;
import com.wangpw.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {

	private LinearLayout weatherInfoLayout;
	private TextView cityNameTextView;
	private TextView publishTimeTextView;
	private TextView weatherDescTextView;
	private TextView tempTextView;
	private TextView currentDateTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// ��ʼ�����ؼ�
		weatherInfoLayout = (LinearLayout) findViewById(R.id.layoutWetherInfo);
		cityNameTextView = (TextView) findViewById(R.id.tvCityName);
		publishTimeTextView = (TextView) findViewById(R.id.tvPublishInfo);
		weatherDescTextView = (TextView) findViewById(R.id.tvWeatherDesc);
		tempTextView = (TextView) findViewById(R.id.tvTempLowToHigh);
		currentDateTextView = (TextView) findViewById(R.id.tvCurrentDate);
		String countyName = getIntent().getStringExtra("county_name");
		if (!TextUtils.isEmpty(countyName)) {
			// �г������ƴ�������˵���Ǵӳ����б���ѡ�й���������Activity����ѯ������������
			publishTimeTextView.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameTextView.setVisibility(View.INVISIBLE);
			//�������д��������ֵĲ���ȥ�����Է���api�ӿڵı�׼
			String requestCountyName = StringUtil.cutStringToInt(countyName, "��");
			//��������д����ء��֣������ֳ��ȳ���3���硰�����ء���Ҳȥ�����ء���
			if(requestCountyName.length()>=3){
				requestCountyName = StringUtil.cutStringToInt(countyName, "��");
			}
			//��󣬽�����Utf-8����
			try {
				requestCountyName = URLEncoder.encode(requestCountyName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String address = "http://v.juhe.cn/weather/index?cityname="
					+ requestCountyName + "&key=c9e11bd06c0d96e8cf8eede1507cfadf";
			queryWeatherFromServer(address);
		} else {
			// û�г������ƴ�������˵�������Ѿ��洢�����ݣ�ֱ����ʾ��������
			showWeather();
		}
	}

	private void queryWeatherFromServer(String address) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				Utility.handleWeatherResponse(WeatherActivity.this, response);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showWeather();
					}
				});
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						publishTimeTextView.setText("ͬ��ʧ��");
					}
				});
			}
		});

	}
/*
 * ��SharedPreferences�ж�ȡ�������ݣ�����ʾ����
 */
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameTextView.setText(prefs.getString("city_name", "δ֪"));
		tempTextView.setText(prefs.getString("weather_temp", "δ֪"));
		weatherDescTextView.setText(prefs.getString("weather_desc", "δ֪"));
		publishTimeTextView.setText(prefs.getString("publish_time", "δ֪"));
		currentDateTextView.setText(prefs.getString("current_date", "δ֪"));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameTextView.setVisibility(View.VISIBLE);
	}
}