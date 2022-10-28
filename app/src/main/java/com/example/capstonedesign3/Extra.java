package com.example.capstonedesign3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

public class Extra extends AppCompatActivity {
    LinearLayout listView;
    View view;
    private FinallincityDao finallincityDao;
    private FinalloutcityDao finalloutcityDao;
    private SelectedRouteDao selectedRouteDao;
    private SelectedRoute route = new SelectedRoute();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);

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
        List<Finallincity> finallincities = finallincityDao.getData(nowDay);
        List<Finalloutcity> finalloutcities = finalloutcityDao.getData(nowDay);
        listView = findViewById(R.id.listView);
        if (finallincities.size() == 0 && finalloutcities.size() == 0) {
            TextView textViewNm = new TextView(getApplicationContext());
            textViewNm.setText("오늘 일정은 없습니다.");
            textViewNm.setTextSize(20);
            textViewNm.setTypeface(null, Typeface.BOLD);
            textViewNm.setId(0);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            param.leftMargin = 30;
            textViewNm.setLayoutParams(param);
            listView.addView(textViewNm);
        } else if (finallincities.size() > 0 && finalloutcities.size() > 0) {
            for (int i = 0; i < finallincities.size(); i++) {
                TextView textViewin = new TextView(getApplicationContext());
                TextView textViewin1 = new TextView(getApplicationContext());
                DecimalFormat df = new DecimalFormat("###,###");
                String money = df.format(finallincities.get(i).getFare());
                textViewin.setText(" 출발시간 : " + finallincities.get(i).getSchedule() + " 소요시간 : " + finallincities.get(i).getTotalTime() + "분");
                textViewin1.setText(" 출발정류소 : " + finallincities.get(i).getStart() + " (" + finallincities.get(i).getName() + "버스)    " + money + "원");
                textViewin.setTextSize(20);
                textViewin1.setTextSize(10);
                textViewin.setTypeface(null, Typeface.BOLD);
                textViewin1.setTypeface(null, Typeface.BOLD);
                textViewin.setId(i);
                textViewin1.setId(i);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                param.leftMargin = 30;
                textViewin.setLayoutParams(param);
                textViewin1.setLayoutParams(param);
                listView.addView(textViewin);
                listView.addView(textViewin1);
                int finalI = i;
                textViewin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      int a = finalI;
                      inset(a);
                    }
                });
            }
            for (int j = 0; j < finalloutcities.size(); j++) {
                TextView textViewout = new TextView(getApplicationContext());
                TextView textViewout1 = new TextView(getApplicationContext());
                DecimalFormat df = new DecimalFormat("###,###");
                String money = df.format(finallincities.get(j).getFare());
                textViewout.setText(" 출발시간 : " +finalloutcities.get(j).getSchedule() + " 소요시간 : " + finalloutcities.get(j).getTotalTime() + "분");
                textViewout1.setText(" 출발정류소 : " + finalloutcities.get(j).getStart() + " (" + finalloutcities.get(j).getStart() + "버스)    " + money + "원");
                textViewout.setTextSize(20);
                textViewout1.setTextSize(10);
                textViewout.setTypeface(null, Typeface.BOLD);
                textViewout1.setTypeface(null, Typeface.BOLD);
                textViewout.setId(j);
                textViewout1.setId(j);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                param.leftMargin = 30;
                textViewout.setLayoutParams(param);
                textViewout1.setLayoutParams(param);
                listView.addView(textViewout);
                listView.addView(textViewout1);
                int finalI = j;
                textViewout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int a = finalI;
                        outset(a);
                    }
                });
            }
        } else {
            if(finallincities.size()>0){
                for (int i = 0; i < finallincities.size(); i++) {
                    TextView textViewin = new TextView(getApplicationContext());
                    TextView textViewin1 = new TextView(getApplicationContext());
                    DecimalFormat df = new DecimalFormat("###,###");
                    String money = df.format(finallincities.get(i).getFare());
                    textViewin.setText(" 출발시간 : " + finallincities.get(i).getSchedule() + " 소요시간 : " + finallincities.get(i).getTotalTime() + "분");
                    textViewin1.setText(" 출발정류소 : " + finallincities.get(i).getStart() + " (" + finallincities.get(i).getName() + "버스)    " + money + "원");
                    textViewin.setTextSize(20);
                    textViewin1.setTextSize(10);
                    textViewin.setTypeface(null, Typeface.BOLD);
                    textViewin1.setTypeface(null, Typeface.BOLD);
                    textViewin.setId(i);
                    textViewin1.setId(i);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    param.leftMargin = 30;
                    textViewin.setLayoutParams(param);
                    textViewin1.setLayoutParams(param);
                    listView.addView(textViewin);
                    listView.addView(textViewin1);
                    int finalI = i;
                    textViewin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int a = finalI;
                            inset(a);
                        }
                    });
                }
            }
            if(finalloutcities.size()>0){
                for (int j = 0; j < finalloutcities.size(); j++) {
                    TextView textViewout = new TextView(getApplicationContext());
                    TextView textViewout1 = new TextView(getApplicationContext());
                    DecimalFormat df = new DecimalFormat("###,###");
                    String money = df.format(finallincities.get(j).getFare());
                    textViewout.setText(" 출발시간 : " +finalloutcities.get(j).getSchedule() + " 소요시간 : " + finalloutcities.get(j).getTotalTime() + "분");
                    textViewout1.setText(" 출발정류소 : " + finalloutcities.get(j).getStart() + " (" + finalloutcities.get(j).getStart() + "버스)    " + money + "원");
                    textViewout.setTextSize(20);
                    textViewout1.setTextSize(10);
                    textViewout.setTypeface(null, Typeface.BOLD);
                    textViewout1.setTypeface(null, Typeface.BOLD);
                    textViewout.setId(j);
                    textViewout1.setId(j);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    param.leftMargin = 30;
                    textViewout.setLayoutParams(param);
                    textViewout1.setLayoutParams(param);
                    listView.addView(textViewout);
                    listView.addView(textViewout1);
                    int finalI = j;
                    textViewout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int a = finalI;
                            outset(a);
                        }
                    });
                }
            }
        }
    }

    void inset(int a) {
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
        SelectedRouteDatabase selectedRouteDatabase = Room.databaseBuilder(getApplicationContext(), SelectedRouteDatabase.class, "selectedroute.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        selectedRouteDao = selectedRouteDatabase.selectedRouteDao();
        FinallincityDatabase finallincityDatabase = Room.databaseBuilder(getApplicationContext(), FinallincityDatabase.class, "finallincity.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        finallincityDao = finallincityDatabase.finallincityDao();
        List<Finallincity> finallincities = finallincityDao.getData(nowDay);
        List<SelectedRoute> selectedRoutes = selectedRouteDao.getData(nowDay);
        if (selectedRoutes.size() > 0) {
            selectedRouteDao.del(nowDay);
        }
        route.setFare(finallincities.get(a).getFare());
        route.setTotalTitme(finallincities.get(a).getTotalTime());
        route.setName(finallincities.get(a).getName());
        route.setPathtype(finallincities.get(a).getPathtype());
        route.setSubpath(finallincities.get(a).getSubpath());
        route.setSchedule(finallincities.get(a).getSchedule());
        route.setDay(finallincities.get(a).getDay());
        route.setStart(finallincities.get(a).getStart());
        route.setArrtime(finallincities.get(a).getArrtime());
        route.setFirstpath("null");
        selectedRouteDao.insert(route);
    }

    void outset(int n) {
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
        FinalloutcityDatabase finalloutcityDatabase = Room.databaseBuilder(getApplicationContext(), FinalloutcityDatabase.class, "finalloutcity.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        finalloutcityDao = finalloutcityDatabase.finalloutcityDao();
        SelectedRouteDatabase selectedRouteDatabase = Room.databaseBuilder(getApplicationContext(), SelectedRouteDatabase.class, "selectedroute.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        selectedRouteDao = selectedRouteDatabase.selectedRouteDao();
        List<Finalloutcity> finalloutcities = finalloutcityDao.getData(nowDay);
        List<SelectedRoute> selectedRoutes = selectedRouteDao.getData(nowDay);
        if (selectedRoutes.size() > 0) {
            selectedRouteDao.del(nowDay);
        }
        route.setFare(finalloutcities.get(n).getFare());
        route.setTotalTitme(finalloutcities.get(n).getTotalTime());
        route.setName(finalloutcities.get(n).getName());
        route.setPathtype(finalloutcities.get(n).getPathtype());
        route.setThirdpath(finalloutcities.get(n).getThirdpath());
        route.setSecondpath(finalloutcities.get(n).getSecondpath());
        route.setFirstpath(finalloutcities.get(n).getFirstpath());
        route.setSchedule(finalloutcities.get(n).getSchedule());
        route.setDay(finalloutcities.get(n).getDay());
        route.setArrtime(finalloutcities.get(n).getArrtime());
    }
}