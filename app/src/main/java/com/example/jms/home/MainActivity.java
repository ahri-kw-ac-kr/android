package com.example.jms.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.jms.R;
import com.example.jms.map.FragMap;
import com.example.jms.settings.FragSettings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import cn.nightcode.sliderIndicator.SliderIndicator;

public class MainActivity extends AppCompatActivity {

    private ViewPager mainViewPager;
    private SliderIndicator indicator;
    private SamplePagerAdapter pagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private FragHome fragHome;
    private FragMap fragMap;
    private FragSettings fragSettings;

    Button button1, button2, button3;
    ImageButton sleepStart, report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //메인화면 상단에 사용자 옆으로 넘겨서 볼 수 있게 하는 역할
        mainViewPager = findViewById(R.id.main_view_pager);
        pagerAdapter = new SamplePagerAdapter(this);
        mainViewPager.setAdapter(pagerAdapter);


        indicator = findViewById(R.id.main_slide_indicator);
        pagerAdapter.setCount(5); //나중에 이 부분을 보호자 숫자대로 바꿔야함..
        indicator.setupWithViewPager(mainViewPager);



        //각 버튼 누르면 다른 페이지로 이동하는 역할!!
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        report = findViewById(R.id.report);
        sleepStart = findViewById(R.id.sleepStart);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MonthLight.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeekAct.class);
                startActivity(intent);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DaySleep.class);
                startActivity(intent);
            }
        });

        sleepStart.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setMessage("취침을 시작하시겠습니까?");

                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Report.class);
                startActivity(intent);
            }
        });

        //하단바 메뉴 누르면 다른 페이지로 이동 ㄱ
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
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
                return true;
            }
        });
        fragHome = new FragHome();
        fragMap = new FragMap();
        fragSettings = new FragSettings();
    }

    //프레그먼트 교체가 일어나는 실행문
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction(); //실제적인 프레그먼트 교체에서 사용
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, fragHome);
                ft.commit(); //저장의미
                break;
            case 1:
                ft.replace(R.id.main_frame, fragMap);
                ft.commit(); //저장의미
                break;
            case 2:
                ft.replace(R.id.main_frame, fragSettings);
                ft.commit(); //저장의미
                break;
        }
    }
}