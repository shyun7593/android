package com.example.capstonedesign3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TimeService extends JobService {
    private static final String TAG = "ExamJobService";
    private boolean jobCancelled = false;
    private FinallincityDao finallincityDao;
    private FinalloutcityDao finalloutcityDao;
    private SelectedRouteDao routeDao;

    //서비스 시작
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob");
        //현재 시간 분으로 구하기
        TimeZone tz;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.KOREAN);
        tz = TimeZone.getTimeZone("Asia/Seoul");
        format.setTimeZone(tz);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String[] getTime = format.format(date).split(":");
        int nowTime = Integer.parseInt(getTime[0])*60 + Integer.parseInt(getTime[1])+1;

        Log.d("testest",nowTime+"");
        //
        FinallincityDatabase finallincityDatabase = Room.databaseBuilder(getApplicationContext(), FinallincityDatabase.class, "finallincity.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        finallincityDao = finallincityDatabase.finallincityDao();
        FinalloutcityDatabase database1 = Room.databaseBuilder(getApplicationContext(), FinalloutcityDatabase.class, "finalloutcity.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        finalloutcityDao = database1.finalloutcityDao();
        SelectedRouteDatabase selectedRouteDatabase = Room.databaseBuilder(getApplicationContext(), SelectedRouteDatabase.class, "selectedroute.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        routeDao = selectedRouteDatabase.selectedRouteDao();
        String nowDay = "";
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case 1:
                nowDay = "일";
                break;
            case 2:
                nowDay = "월";
                break;
            case 3:
                nowDay = "화";
                break;
            case 4:
                nowDay = "수";
                break;
            case 5:
                nowDay = "목";
                break;
            case 6:
                nowDay = "금";
                break;
            case 7:
                nowDay = "토";
                break;
        }
        String altime = "gu";
        List<SelectedRoute> routes = routeDao.getData(nowDay);
        List<Finallincity> finallincities = finallincityDao.getData(nowDay);
        List<Finalloutcity> finalloutcities = finalloutcityDao.getData(nowDay);
        if (routes.size() > 0) {
            altime = routes.get(0).getSchedule();
        } else if (finallincities.size() > 0 && finalloutcities.size() > 0) {
            if (finallincities.get(0).getTotalTime() > finalloutcities.get(0).getTotalTime()) {
                altime = finalloutcities.get(0).getSchedule();
            } else {
                altime = finallincities.get(0).getSchedule();
            }
        } else if (finallincities.size() == 0 && finalloutcities.size() == 0) {
            Log.d(TAG, "onStopJob");
            jobCancelled = true;
            return true;
        } else {
            if (finallincities.size() > 0) {
                altime = finallincities.get(0).getSchedule();
            } else {
                altime = finalloutcities.get(0).getSchedule();
            }
        }
        String[] starTime = altime.split(":");
        int startTime = Integer.parseInt(starTime[0])*60 + Integer.parseInt(starTime[1])+1;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Notification 객체를 생성해주는 건축가객체 생성(AlertDialog 와 비슷)
        NotificationCompat.Builder builder = null;
        NotificationCompat.Builder builder2 = null;
        //Oreo 버전(API26 버전)이상에서는 알림시에 NotificationChannel 이라는 개념이 필수 구성요소가 됨.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelID = "channel_01"; //알림채널 식별자
            String channelName = "MyChannel01"; //알림채널의 이름(별명)
            //알림채널 객체 만들기
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            //알림매니저에게 채널 객체의 생성을 요청
            notificationManager.createNotificationChannel(channel);
            //알림건축가 객체 생성
            builder = new NotificationCompat.Builder(this, channelID);
            builder2 = new NotificationCompat.Builder(this,channelID);

        } else {
            //알림 건축가 객체 생성
            builder = new NotificationCompat.Builder(this, (Notification) null);
            builder2 = new NotificationCompat.Builder(this,(Notification) null);
        }
        // 푸시 알람 클릭 시 RouteScreen으로 이동
        Intent intent = new Intent(this, Routescreen.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,NotificationManager.IMPORTANCE_DEFAULT,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(notificationPendingIntent);
        builder2.setContentIntent(notificationPendingIntent);

        //건축가에게 원하는 알림의 설정작업
        builder.setSmallIcon(android.R.drawable.ic_menu_view);
        builder2.setSmallIcon(android.R.drawable.ic_menu_view);

        //상태바를 드래그하여 아래로 내리면 보이는
        //알림창(확장 상태바)의 설정
        builder.setContentTitle("출발시간 알림이");//알림창 제목
        builder.setContentText("출발해야될 시간은 " + altime + " 입니다.");//알림창 내용


        //알림창의 큰 이미지
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        builder.setLargeIcon(bm);//매개변수가 Bitmap을 줘야한다.
        //건축가에게 알림 객체 생성하도록
        Notification notification = builder.build();


        //알림매니저에게 알림(Notify) 요청
        notificationManager.notify(1, notification);
        int restTime;
        if(startTime - nowTime>0){
            if (startTime - nowTime<65){
                restTime = startTime - nowTime;
                builder2.setContentTitle("출발시간 알림이");
                builder2.setContentTitle("출발까지 "+ restTime +"남았습니다.");
                builder2.setLargeIcon(bm);
                Notification notification1 = builder2.build();
                notificationManager.notify(2, notification1);
            }
        }

        //알림 요청시에 사용한 번호를 알림제거 할 수 있음.
//        notificationManager.cancel(1);

        Log.d(TAG, "onStopJob");
        doBackgroundWork(params);
        return true;
    }

    //서비스 중지
    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 2; i++) {
                    Log.d(TAG, "run: " + i);
                    if (jobCancelled) {
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }
}