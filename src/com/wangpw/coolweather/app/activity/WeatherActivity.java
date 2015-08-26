package com.wangpw.coolweather.app.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.wangpw.coolweather.app.R;
import com.wangpw.coolweather.app.util.HttpCallbackListener;
import com.wangpw.coolweather.app.util.HttpUtil;
import com.wangpw.coolweather.app.util.StringUtil;
import com.wangpw.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {

	private LinearLayout weatherInfoLayout;
	private TextView cityNameTextView;
	private TextView publishTimeTextView;
	private TextView weatherDescTextView;
	private TextView tempTextView;
	private TextView currentDateTextView;
	private Button switchCityButton;
	private Button refreshWeatherButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// 初始化各控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.layoutWetherInfo);
		cityNameTextView = (TextView) findViewById(R.id.tvCityName);
		publishTimeTextView = (TextView) findViewById(R.id.tvPublishInfo);
		weatherDescTextView = (TextView) findViewById(R.id.tvWeatherDesc);
		tempTextView = (TextView) findViewById(R.id.tvTempLowToHigh);
		currentDateTextView = (TextView) findViewById(R.id.tvCurrentDate);
		String countyName = getIntent().getStringExtra("county_name");
		if (!TextUtils.isEmpty(countyName)) {
			// 有城市名称传过来，说明是从城市列表中选中过来开启的Activity，查询服务器并储存
			publishTimeTextView.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameTextView.setVisibility(View.INVISIBLE);
			initialAddressThenSend(countyName);			
		} else {
			// 没有城市名称传过来，说明本地已经存储了数据，直接显示天气即可
			showWeather();
		}
		switchCityButton = (Button)findViewById(R.id.btnSwitchCity);
		refreshWeatherButton = (Button)findViewById(R.id.btnRefreshWeather);
		switchCityButton.setOnClickListener(this);
		refreshWeatherButton.setOnClickListener(this);
	}
	
	private void initialAddressThenSend(String countyName){
		//将地区中带“区”字的部分去掉，以符合api接口的标准
		String requestCountyName = StringUtil.cutStringToInt(countyName, "区");
		//如果地区中带“县”字，且名字长度超过3，如“密云县”，也去掉“县”字
		if(requestCountyName.length()>=3){
			requestCountyName = StringUtil.cutStringToInt(countyName, "县");
		}
		//最后，将名字Utf-8编码
		try {
			requestCountyName = URLEncoder.encode(requestCountyName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String address = "http://v.juhe.cn/weather/index?cityname="
				+ requestCountyName + "&key=c9e11bd06c0d96e8cf8eede1507cfadf";
		queryWeatherFromServer(address);//地址初始化完成后发送地址
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
						publishTimeTextView.setText("同步失败");
					}
				});
			}
		});

	}
/*
 * 从SharedPreferences中读取天气数据，并显示出来
 */
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameTextView.setText(prefs.getString("city_name", "未知"));
		tempTextView.setText(prefs.getString("weather_temp", "未知"));
		weatherDescTextView.setText(prefs.getString("weather_desc", "未知"));
		publishTimeTextView.setText(prefs.getString("publish_time", "未知"));
		currentDateTextView.setText(prefs.getString("current_date", "未知"));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameTextView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSwitchCity:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);//标记一下是从该Acitivty返回的，以便不再跳回来
			startActivity(intent);
			finish();
			break;
		case R.id.btnRefreshWeather:
			publishTimeTextView.setText("同步中...");
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String tempCountyName = prefs.getString("city_name", "");
			if(!TextUtils.isEmpty(tempCountyName)){
				initialAddressThenSend(tempCountyName);
			}
			break;
		default:
			break;
		}
	}
}
