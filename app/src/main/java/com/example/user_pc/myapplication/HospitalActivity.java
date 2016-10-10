package com.example.user_pc.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HospitalActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    ListView mListView;
    HospitalAdapter mAdapter;
    SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
        mListView = (ListView) findViewById(R.id.main_listview);
        mAdapter = new HospitalAdapter(this);
        mListView.setAdapter(mAdapter);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeColors(Color.BLUE); //重整的圖示用藍色

        Button btnHospital = (Button) findViewById(R.id.HospitalTextTitle);
        btnHospital.setOnClickListener(this);
        btnHospital.setBackgroundColor(Color.LTGRAY);
        btnHospital.setTextColor(Color.BLACK);
        Button btnSanchong = (Button) findViewById(R.id.SanchongTextTitle);
        btnSanchong.setOnClickListener(this);
        Button btnBanqiao = (Button) findViewById(R.id.BanqiaoTextTitle);
        btnBanqiao.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadData();

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.HospitalTextTitle:
                loadData();
                break;
            case R.id.SanchongTextTitle:
                Intent SanchongIntent = new Intent(this, SanchongActivity.class);
                SanchongIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(SanchongIntent);
                overridePendingTransition(0, 0);
                break;
            case R.id.BanqiaoTextTitle:
                Intent BanqiaoIntent = new Intent(this, BunqiaoActivity.class);
                BanqiaoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(BanqiaoIntent);
                overridePendingTransition(0, 0);
                break;

        }
    }

    private void loadData(){
        String urlString = "http://data.ntpc.gov.tw/api/v1/rest/datastore/382000000A-000043-002";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlString, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getApplicationContext(),
                        "Success!", Toast.LENGTH_LONG).show();
                Log.d("response Text:", response.toString());

                if( response.has("err") && response.optInt("err")!=0 ){
                    Toast.makeText(getApplicationContext(),"Data error", Toast.LENGTH_LONG).show();
                }

                JSONObject result = response.optJSONObject("result");
                Log.d("result Text:", result.toString());
                JSONArray records = result.optJSONArray("records");
                Log.d("records Text:", records.toString());

                mAdapter.updateData(records);
                mSwipeLayout.setRefreshing(false); //結束更新動畫
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject error) {
                Toast.makeText(getApplicationContext(),
                        "Error: " + statusCode + " " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                // Log error message
                Log.e("Hot Text:", statusCode + " " + e.getMessage());
                mSwipeLayout.setRefreshing(false); //結束更新動畫
            }
        });
    }

    @Override
    public void onRefresh() {
        loadData(); //下載並更新列表的資料

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbarmenu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_refresh: //點了重新整理
                loadData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
