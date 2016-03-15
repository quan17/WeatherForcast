package com.cyz.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ycz.weatherforcast.R;

public class MainActivity extends Activity {
	TextView tempTv, weatherTv, descTv, windTv;
	RequestQueue mQueue;
	LinearLayout chart;

	ArrayList<PointItem> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainactivity);
		chart = (LinearLayout) findViewById(R.id.chart);
		tempTv = (TextView) findViewById(R.id.temp);
		weatherTv = (TextView) findViewById(R.id.weather);
		descTv = (TextView) findViewById(R.id.desc);
		windTv = (TextView) findViewById(R.id.wind);
		mQueue = Volley.newRequestQueue(this);
		list = new ArrayList<PointItem>();
		// list.add(new PointItem("TUS", 233.2f));
		// list.add(new PointItem("WEN", 222.4f));
		// list.add(new PointItem("THI", 241.7f));
		// list.add(new PointItem("FRI", 223.5f));
		// list.add(new PointItem("SAT", 255.5f));

		getCurrent("Harrison");
		getForcastSix("Harrison");
	}

	private void getCurrent(String cityName) {
		// ��ǰ����
		String urlCurrent = "http://api.openweathermap.org/data/2.5/weather?q="
				+ cityName + "&appid=" + "b1b15e88fa797225412429c1c50c122a";
		JsonObjectRequest request = new JsonObjectRequest(Method.POST,
				urlCurrent, null, new Response.Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						// ��ǰ���������ݸ�ʽ����
						// ����Ҫ��ȡ������ʾ
						// {"coord":{"lon":-0.13,"lat":51.51},"weather":[{"id":721,"main":"Haze","description":"haze","icon":"50d"}],"base":"stations","main":{"temp":278.65,"pressure":1034,"humidity":70,"temp_min":278.15,"temp_max":280.15},"visibility":9000,"wind":{"speed":7.7,"deg":90},"clouds":{"all":40},"dt":1457949978,"sys":{"type":1,"id":5091,"message":0.0189,"country":"GB","sunrise":1457936107,"sunset":1457978685},"id":2643743,"name":"London","cod":200}
						JSONObject object = response.optJSONObject("main");
						double temp = object.optDouble("temp");
						tempTv.setText(k2F(temp) + "°");
						JSONArray a = response.optJSONArray("weather");
						JSONObject b = a.optJSONObject(0);
						String weather = b.optString("main");
						String desc = b.optString("description");
						weatherTv.setText(weather);
						descTv.setText(desc);
						JSONObject object1 = response.optJSONObject("wind");
						setWind(object1.optDouble("speed"));
					};
				}, new Response.ErrorListener() {
					public void onErrorResponse(VolleyError error) {
					};
				});
		request.setSequence(1);
		request.setShouldCache(true);
		request.setRetryPolicy(new DefaultRetryPolicy(4000, 3,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(request);
	}

	private void setWind(double wind) {
		int windInt = (int) wind;
		if (windInt == 0) {
			windTv.setText("NO WIND");
		} else {
			windTv.setText(windInt + " Degree Wind");
		}

	}

	private void getForcastSix(String cityName) {
		// δ��6���������������죩
		String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q="
				+ cityName + "&cnt=6&appid=b1b15e88fa797225412429c1c50c122a";
		JsonObjectRequest request = new JsonObjectRequest(url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						list.clear();
						// TODO Auto-generated method stub
						JSONArray object = response.optJSONArray("list");
						for (int i = 0; i < object.length(); i++) {
							JSONObject item = object.optJSONObject(i);
							JSONObject temp = item.optJSONObject("temp");
							String week = dateFormat(i);
							PointItem aPoint;
							if (i == 0) {
								aPoint = new PointItem("Today",
										k2F(temp.optDouble("max")),
										k2F(temp.optDouble("min")));
							} else {
								aPoint = new PointItem(week,
										k2F(temp.optDouble("max")),
										k2F(temp.optDouble("min")));
							}

							list.add(aPoint);
						}
						setDataToView();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
					}
				});
		request.setSequence(2);
		request.setShouldCache(true);
		request.setRetryPolicy(new DefaultRetryPolicy(4000, 3,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(request);
	}

	private void setDataToView() {
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = width * 2 / 3;
		CusChartView tu = new CusChartView(getApplicationContext(), width,
				height);
		tu.setData(list, "Unit: °F");
		chart.addView(tu);
	}

	Calendar calendar = Calendar.getInstance(Locale.US);

	private String dateFormat(int i) {
		calendar.add(Calendar.DAY_OF_YEAR, i);
		return new SimpleDateFormat("E").format(calendar.getTime());

	}

	// ��ʽ��ת���϶�
	private int k2F(double k) {
		return (int) ((k - 273.16) * 1.8 + 32);
	}
}
