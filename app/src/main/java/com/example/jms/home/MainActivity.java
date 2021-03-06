package com.example.jms.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.jms.R;
import com.example.jms.connection.model.RestfulAPI;
import com.example.jms.connection.model.dto.SleepDTO;
import com.example.jms.connection.viewmodel.APIViewModel;
import com.example.jms.home.statistic.StatSleep;
import com.example.jms.map.FragMap;
import com.example.jms.settings.FragSettings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private FragHome fragHome = new FragHome();
    private FragMap fragMap = new FragMap();
    private FragSettings fragSettings = new FragSettings();
    final static int BT_REQUEST_ENABLE = 1;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //////////////////////수면분석 db에 저장///////////////////////
        sharedPreferences = getSharedPreferences("sleep", 0);
        String start = sharedPreferences.getString("sleepTime", "");
        String end = sharedPreferences.getString("wakeTime", "");
        Log.d("MainActivity - sleepData", "시작: " + start + ", 끝: " + end);

        Long calcTime = (long) 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        try {
            //초단위로 계산되는 시간 차이
            calcTime = (simpleDateFormat.parse(end).getTime()-simpleDateFormat.parse(start).getTime())/1000;
            Log.e("SleepActivity",calcTime+"");
        } catch (ParseException e) { e.printStackTrace(); }

        if(calcTime > 1800) {///30분 안되면 저장 못함
            APIViewModel apiViewModel = new APIViewModel();
            apiViewModel.getRawdataById(RestfulAPI.principalUser.getId(), "0", start, end)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(data2 -> {
                        if (data2.getContent() != null) {
                            try {
                                Log.d("MainActivity", "데이터 첫번째result" + data2.getContent().get(0).getStartTick());
                            } catch (Exception e) {
                                Log.d("MainActivity", "데이터 첫번째result size" + data2.getContent().size());
                            }
                            if (data2.getContent().size() != 0) {
                                UserDataModel.userDataModels[0].setSleepDataList(data2.getContent());
                                Log.d("MainActivity - sleepData", "데이터 첫번째" + UserDataModel.userDataModels[0].getSleepDataList().size());
                                SleepDTO sleepDTO1 = StatSleep.analyze(data2.getContent());
                                sleepDTO1.setSleepTime(start);
                                sleepDTO1.setWakeTime(end);
                                sleepDTO1.setUser(RestfulAPI.principalUser);
                                /////////////////분석결과 db에 저장//////////////////
                                apiViewModel.postSleep(sleepDTO1)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(a -> {
                                                    Log.d("MainActicity - sleepData", "분석결과 저장");
                                                    //editor.putString("sleepTime","0");
                                                    //editor.putString("wakeTime","0");
                                                    //editor.apply();
                                                },
                                                Throwable -> Log.d("MainActivity-sleepData", "분석결과 db저장 실패 " + Throwable.getMessage()));
                            }
                        }
                        Log.d("MainActicity - sleepData", "데이터 " + data2.getContent());
                    }, Throwable -> Log.d("MainActivity-sleepData", "rawData불러오기 실패 " + Throwable.getMessage()));
        }
        //하단바
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, fragHome).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        setFrag(0);
                        break;
                    case R.id.map:
                        setFrag(1);
                        break;
                    case R.id.settings:
                        setFrag(2);
                        break;
                }
                return false;
            }
        });
        fragHome = new FragHome();
        fragMap = new FragMap();
        fragSettings = new FragSettings();

        BluetoothOn();
    }

    //하단바를 통해 프레그먼트 교체가 일어나는 실행문
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction(); //실제적인 프레그먼트 교체에서 사용
        switch (n) {
            case 0:
                ft.replace(R.id.frameLayout, fragHome);
                ft.commit(); //저장의미
                break;
            case 1:
                ft.replace(R.id.frameLayout, fragMap);
                ft.commit(); //저장의미
                break;
            case 2:
                ft.replace(R.id.frameLayout, fragSettings);
                ft.commit(); //저장의미
                break;
        }
    }

    private void BluetoothOn(){
        BluetoothAdapter mBluetoothAdapter;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), R.string.mainActivity1, Toast.LENGTH_LONG).show();
        }
        else {
            if (mBluetoothAdapter.isEnabled()) {
                //Toast.makeText(getApplicationContext(), "블루투스가 이미 활성화 되어 있습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.mainActivity2, Toast.LENGTH_LONG).show();
                Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE);
            }
        }
    }

    //mainactivity에서 back button을 press하면 띄우는 팝업
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert_ex = new AlertDialog.Builder(this);
        alert_ex.setMessage("AlchiDoc을 종료하시겠습니까?");

        alert_ex.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert_ex.setNegativeButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        alert_ex.setTitle("AlchiDoc");
        AlertDialog alert = alert_ex.create();
        alert.show();

    }


    ///권한 설정 하는 코드를 가져와봤는데... 실행이 안되네요
    ///https://stackoverflow.com/questions/34342816/android-6-0-multiple-permissions - 여기를 참조했습니다
}