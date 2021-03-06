package com.example.jms.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jms.R;
import com.example.jms.connection.model.RestfulAPI;
import com.example.jms.connection.model.dto.UserDTO;
import com.example.jms.connection.viewmodel.APIViewModel;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SleepTime extends AppCompatActivity implements TimePicker.OnTimeChangedListener {

    TimePicker timePicker;
    Calendar calendar;
    int setHour;
    int setMin;
    Button button1;
    String AM_PM ;

    APIViewModel apiViewModel = new APIViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep_timepicker);
        calendar = Calendar.getInstance();

        //타임피커를 현재 시간으로 세팅해주기 위한 코드
        int hourOfday = calendar.get(calendar.HOUR_OF_DAY);
        int minute = calendar.get(calendar.MINUTE);
        int nHour = calendar.get(calendar.HOUR_OF_DAY);
        int nMin = calendar.get(calendar.MINUTE);

        timePicker = (TimePicker) findViewById(R.id.tp);
        button1 = (Button) findViewById(R.id.setButton); //아래 설정 버튼


        timePicker.setOnTimeChangedListener(this);
        //버전따라 사용되는 함수가 다름.

        button1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                Log.d("TimePickerDemo",setHour+","+setMin);
                if (AM_PM == null && (setHour ==0 && setMin==0))
                {
                    //timePicker.getHour();
                    //timePicker.getMinute();

                    setHour = hourOfday;
                    setMin = minute;
                }

                if (0<(nHour-setHour)){

                    if(setHour<4){//세팅할 시간이 0시에서 4시인 경우
                    Toast.makeText(getApplicationContext(), "새벽은 취침시간으로 설정되지 않습니다.", Toast.LENGTH_LONG).show();}

                    else {Toast.makeText(getApplicationContext(), "현재보다 이른 시간은 설정되지 않습니다.", Toast.LENGTH_LONG).show();}
                }
                else if ((setHour-nHour==0)&& (setMin-nMin<=0)){
                    Toast.makeText(getApplicationContext(), "현재보다 이른 시간은 설정되지 않습니다.", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d("TimePickerDemo1",""+String.format("%02d%02d", setHour, setMin));
                    String settingTime = String.format("%02d%02d", setHour, setMin);

                    UserDTO user = new UserDTO();
                    user.setSleep(settingTime);

                    apiViewModel.patchUser(RestfulAPI.principalUser.getId(),user)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(result->{RestfulAPI.principalUser = result;
                                // 알람설정하는 줄
                                new SleepAlarm(getApplicationContext()).Alarm();},Throwable::printStackTrace);

                    Toast.makeText(getApplicationContext(), "취침시간이 설정되었습니다. "+" "+setHour+"시"+setMin+"분", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SleepTime.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();}





                System.out.println(AM_PM+" "+setHour+":"+setMin);
            }
        });
    }



    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute){


        if(hourOfDay < 12) {
            AM_PM = "오전";
        } else {
            AM_PM = "오후";
        }

        if (Build.VERSION.SDK_INT>= android.os.Build.VERSION_CODES.M) {
            setHour = timePicker.getHour();
            setMin = timePicker.getMinute();
        }
        else {
            setHour = timePicker.getCurrentHour();
            setMin = timePicker.getCurrentMinute();
        }

        //Toast.makeText(this.getApplicationContext(), hourOfDay+","+minute, Toast.LENGTH_SHORT).show();

    }




}