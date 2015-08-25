package com.wangpw.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.wangpw.coolweather.app.R;
import com.wangpw.coolweather.app.db.CoolWeatherDB;
import com.wangpw.coolweather.app.model.City;
import com.wangpw.coolweather.app.model.County;
import com.wangpw.coolweather.app.model.Province;
import com.wangpw.coolweather.app.util.HttpCallbackListener;
import com.wangpw.coolweather.app.util.HttpUtil;
import com.wangpw.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;

	public static final int LEVEL_CITY = 1;

	public static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;

	private TextView titleTextView;

	private ListView listView;

	private ArrayAdapter<String> adapter;

	private CoolWeatherDB coolWeatherDB;

	private List<String> dataList = new ArrayList<String>();
	/*
	 * ʡ�б�
	 */
	private List<Province> provinceList;
	/*
	 * ���б�
	 */
	private List<City> cityList;
	/*
	 * ���б�
	 */
	private List<County> countyList;
	/*
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	/*
	 * ѡ�е���
	 */
	private City selectedCity;
	/*
	 * ѡ�е���
	 */
	private County selectedCounty;
	/*
	 * ��ǰѡ�м���
	 */
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false)){
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.lvArea);
		titleTextView = (TextView) findViewById(R.id.tvTitle);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				}else if(currentLevel == LEVEL_COUNTY){
					String countyName = countyList.get(index).getCountyName();
					Intent intent = new Intent(ChooseAreaActivity.this
							,WeatherActivity.class);
					intent.putExtra("county_name", countyName);
					startActivity(intent);
					finish();
				}
			}

		});
		queryProvince();// ����ʡ������
	}

	/*
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryProvince() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleTextView.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/*
	 * ��ѯѡ��ʡ�������е��У����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleTextView.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/*
	 * ��ѯѡ���������е��أ����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleTextView.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	/*
	 * ���ݴ�����ź����Ͳ�ѯʡ�������ݲ����������ݿ���
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {// �д��ţ�˵���ǲ�ѯCity��County����
			address = "http://apis.map.qq.com/ws/district/v1/getchildren?&id="
					+ code + "&key=3DJBZ-LP633-V5K3Z-3F6CE-NT3YO-MZBQC";
		} else {// �޴��ţ�˵�����ǲ�ѯʡ������
			address = "http://apis.map.qq.com/ws/district/v1/getchildren?key=3DJBZ-LP633-V5K3Z-3F6CE-NT3YO-MZBQC";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {
					// ͨ��runOnUiThread()�����ص����̴߳����߼�
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvince();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				} else {// �����������򣬿��Կ�ʼ��������
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							closeProgressDialog();
							//��ǰ���������Ѿ�Ϊ��С������ֱ�ӻ�ȡʡ���м���Ϣ
							if (currentLevel == LEVEL_PROVINCE) {
								String countyName = selectedProvince.getProvinceName();
								Intent intent = new Intent(ChooseAreaActivity.this
										,WeatherActivity.class);
								intent.putExtra("county_name", countyName);
								startActivity(intent);
								finish();
							} else if (currentLevel == LEVEL_CITY) {
								String countyName = selectedCity.getCityName();
								Intent intent = new Intent(ChooseAreaActivity.this
										,WeatherActivity.class);
								intent.putExtra("county_name", countyName);
								startActivity(intent);
								finish();
							}
						}
					});
				}

			}

			@Override
			public void onError(Exception e) {
				// ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��",
								Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

	/*
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/*
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/*
	 * ����Back���������ݵ�ǰ�б��𣬴�ʱӦ�÷����ļ��б��ֱ���˳�
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvince();
		} else {
			finish();
		}
	}

}
