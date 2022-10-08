package com.example.capstonedesign3;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    private FincityDao fincityDao;
    private FoutcityDao foutcityDao;
    private DayTimeDao dayTimeDao;
    private FinallincityDao finallincityDao;
    private FinalloutcityDao finalloutcityDao;
    private DayTime dayTime = new DayTime();
    private Fincity fincity = new Fincity();
    private Foutcity foutcity = new Foutcity();
    private Finallincity finallincity = new Finallincity();
    private Finalloutcity finalloutcity = new Finalloutcity();

    public class onHTTPConnection {
        public String connectAndGet(Double sx, Double sy) {
            String apiKey = "QEX7ua4WgGErXl+jz2xtjmfPZ4rRn5TwjDIrOhpo5Ho";
            try {
                FincityDatabase indatabase = Room.databaseBuilder(getApplicationContext(), FincityDatabase.class, "fincity.db")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build();
                fincityDao = indatabase.fincityDao();
                String result;
                StringBuffer sb = new StringBuffer();
                URL url = new URL("https://api.odsay.com/v1/api/searchPubTransPathT?SX=" + sx + "&SY=" + sy + "&EX=127.2635&EY=37.0094&SearchType=0&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                conn.setDoOutput(true);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((result = br.readLine()) != null) {
                    sb.append(result);
                }
                result = sb.toString();
                JSONObject jio = new JSONObject(result);
                if (jio.has("error")) {
                    Log.i("jio", jio.toString());
                    return "검색결과 없음";
                }
                String results;
                try {
                    JSONObject jsonObj = new JSONObject(result).getJSONObject("result");
                    String resultst = jsonObj.toString();
                    int t = (int) jsonObj.get("searchType");
                    if (t == 1) {
                        Log.i("jio", "ㅎㅎ");
                        return "null";
                    }
                    JSONObject jObj = new JSONObject(resultst);
                    JSONArray array = (JSONArray) jObj.get("path");
                    JSONArray incity = new JSONArray();
                    if (array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject ac = new JSONObject();
                            String ara = array.getJSONObject(i).toString();
                            JSONObject jsonobj = new JSONObject(ara).getJSONObject("info");
                            JSONObject subpath = new JSONObject(ara);
                            JSONArray subarray = (JSONArray) subpath.get("subPath");
                            JSONArray inarray = new JSONArray();
                            if (subarray.length() > 0) {
                                for (int k = 0; k < subarray.length(); k++) {
                                    String tra = subarray.getJSONObject(k).toString();
                                    JSONObject trafiic = (JSONObject) subarray.get(k);
                                    switch (trafiic.get("trafficType").toString()) {
                                        case "3":
                                            JSONObject walkdata = new JSONObject();
                                            walkdata.put("type", "걷기");
                                            walkdata.put("name", "");
                                            walkdata.put("distance", trafiic.get("distance"));
                                            walkdata.put("sectionTime", trafiic.get("sectionTime"));
                                            inarray.put(walkdata);
                                            break;
                                        case "2":
                                            JSONObject busdata = new JSONObject();
                                            JSONObject lane = new JSONObject(tra);
                                            JSONArray lane1 = (JSONArray) lane.get("lane");
                                            JSONObject lne = (JSONObject) lane1.get(0);
                                            busdata.put("type", "버스");
                                            busdata.put("name", lne.get("busNo"));
                                            busdata.put("cityCode", lne.get("busCityCode"));
                                            busdata.put("busID", lne.get("busID"));
                                            busdata.put("start", trafiic.get("startName").toString());
                                            busdata.put("startID", trafiic.get("startID"));
                                            if (trafiic.has("startLocalStationID")) {
                                                busdata.put("localID", trafiic.get("startLocalStationID").toString());
                                            }
                                            inarray.put(busdata);
                                            break;
                                        case "1":
                                            JSONObject subway = new JSONObject();
                                            JSONObject lane2 = new JSONObject(tra);
                                            JSONArray lane3 = (JSONArray) lane2.get("lane");
                                            JSONObject ln4 = (JSONObject) lane3.get(0);
                                            String result2;
                                            StringBuffer sb2 = new StringBuffer();
                                            URL ul = new URL("https://api.odsay.com/v1/api/subwayPath?lang=0&CID=" + ln4.get("subwayCityCode").toString() + "&SID=" + trafiic.get("startID").toString() + "&EID=" + trafiic.get("endID").toString() + "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                                            HttpURLConnection con = (HttpURLConnection) ul.openConnection();
                                            con.setRequestMethod("GET");
                                            con.setRequestProperty("Content-type", "application/json");
                                            con.setDoOutput(true);
                                            BufferedReader br2 = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                            while ((result2 = br2.readLine()) != null) {
                                                sb2.append(result2);
                                            }
                                            result2 = sb2.toString();
                                            JSONObject jsonj = new JSONObject(result2).getJSONObject("result");
                                            JSONObject js = (JSONObject) jsonj.get("driveInfoSet");
                                            JSONArray jj = (JSONArray) js.get("driveInfo");
                                            JSONObject jjj = (JSONObject) jj.get(0);
                                            subway.put("type", "지하철");
                                            subway.put("name", ln4.get("name"));
                                            subway.put("wayCode", trafiic.get("wayCode"));
                                            subway.put("startID", trafiic.get("startID"));
                                            subway.put("wayName", jjj.get("wayName"));
                                            inarray.put(subway);
                                            break;
                                    }
                                }

                            }
                            ac.put("pay", jsonobj.get("payment"));
                            ac.put("start", jsonobj.get("firstStartStation"));
                            ac.put("end", jsonobj.get("lastEndStation"));
                            ac.put("time", jsonobj.get("totalTime"));
                            ac.put("subpath", inarray);
                            fincity.setFare(jsonobj.getInt("payment"));
                            fincity.setTotaltime(jsonobj.getInt("totalTime"));
                            fincity.setPathtype(subpath.getInt("pathType"));
                            fincity.setSubpath(inarray.toString());
                            fincity.setStart(jsonobj.getString("firstStartStation"));
                            fincityDao.insert(fincity);
                            incity.put(ac);
                        }
                    }

                    Log.i("arraytest", incity.toString());
                    results = incity.toString();
                    InTime();
                    return results;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "hello";
            } catch (Exception exception) {
                return null;
            }
        }

        public String InTime() {
            String apiKey = "QEX7ua4WgGErXl+jz2xtjmfPZ4rRn5TwjDIrOhpo5Ho";
            FincityDatabase indatabase = Room.databaseBuilder(getApplicationContext(), FincityDatabase.class, "fincity.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            DayTimeDatabase database = Room.databaseBuilder(getApplicationContext(), DayTimeDatabase.class, "daytime.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            FinallincityDatabase finallincityDatabase = Room.databaseBuilder(getApplicationContext(), FinallincityDatabase.class, "finallincity.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
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
            List<DayTime> dayTimes = dayTimeDao.getDay(nowDay);
            if (dayTimes.size() == 0) {
                return null;
            }
            finallincityDao = finallincityDatabase.finallincityDao();
            List<Finallincity> finallincities = finallincityDao.getFinday(nowDay);
            if (finallincities.size() > 0) {
                finallincityDao.delday(nowDay);
            }
            fincityDao = indatabase.fincityDao();
            List<Fincity> fincities = fincityDao.getFincityAll();
//            Log.i("dgat",incity.toString());
            if (fincities.size() == 0) {
                return null;
            }
            try {
                for (int i = 0; i < fincities.size(); i++) {
                    Thread.sleep(200);
                    JSONArray jsonObject = new JSONArray(fincities.get(i).getSubpath());
                    JSONObject jsonObject1 = (JSONObject) jsonObject.get(1);
                    Log.i("tttest", jsonObject1.toString());
                    Log.i("tttest", jsonObject1.getString("type"));
                    switch (jsonObject1.getString("type")) {
                        case "버스":
                            String result;
                            StringBuffer st = new StringBuffer();
                            URL url = new URL("https://api.odsay.com/v1/api/busLaneDetail?lang=0&busID=" + jsonObject1.getString("busID") + "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setRequestProperty("Content-type", "application/json");
                            conn.setDoOutput(true);
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            while ((result = br.readLine()) != null) {
                                st.append(result);
                            }
                            result = st.toString();
                            JSONObject jjo = new JSONObject(result).getJSONObject("result");
                            Log.i("jjoio", jjo.toString());
                            Log.i("jjoio", jjo.getString("busFirstTime"));
                            String firsttime = jjo.getString("busFirstTime");
                            Log.i("firsttime", firsttime);
                            String[] q = new String[2];
                            q = firsttime.split(":");
                            int interval = 0;
                            switch (nowDay) {
                                case "토":
                                    interval = jjo.getInt("bus_Interval_Sat");
                                    break;
                                case "일":
                                    interval = jjo.getInt("bus_Interval_Sun");
                                    break;
                                default:
                                    interval = jjo.getInt("bus_Interval_Week");
                                    break;
                            }
                            JSONArray jar = (JSONArray) jjo.get("station");
                            for (int k = 0; k < jar.length(); k++) {
                                JSONObject job = (JSONObject) jar.getJSONObject(k);

                                if (jsonObject1.getInt("startID") == job.getInt("stationID")) {
                                    Log.i("heil", "hello");
                                    int gettime = (int) Math.ceil((double) 1.5 * job.getInt("idx"));
                                    int r = (int) (Integer.parseInt(q[0]) * 60) + (int) (Integer.parseInt(q[1])) + gettime;
                                    Log.i("rrrr", r + "");
                                    Log.i("tttt", fincities.get(i).getTotaltime() + "");
                                    int t = (int) dayTimes.get(0).getTime() - (int) fincities.get(i).getTotaltime() - 5;
                                    int hour = 0;
                                    int minute = 0;
                                    if (interval == 0 || r > t) {
                                        finallincity.setDay(nowDay);
                                        finallincity.setFare(fincities.get(i).getFare());
                                        finallincity.setStart(fincities.get(i).getStart());
                                        finallincity.setPathtype(fincities.get(i).getPathtype());
                                        finallincity.setTotalTime(fincities.get(i).getTotaltime());
                                        finallincity.setSubpath(fincities.get(i).getSubpath());
                                        finallincity.setSchedule("운행정보가 없습니다.");
                                        finallincity.setName(jsonObject1.getString("name"));
                                        finallincityDao.insert(finallincity);
                                    } else {
                                        while (r < t) {
                                            r = r + interval;
                                            if (r > t) {
                                                r = r - interval;
                                                break;
                                            }
                                        }
                                        hour = (int) Math.floor((double) r / 60);
                                        minute = r % 60;
                                        String hur1 = String.format("%02d", hour);
                                        String min1 = String.format("%02d", minute);
                                        finallincity.setDay(nowDay);
                                        finallincity.setFare(fincities.get(i).getFare());
                                        finallincity.setStart(fincities.get(i).getStart());
                                        finallincity.setPathtype(fincities.get(i).getPathtype());
                                        finallincity.setTotalTime(fincities.get(i).getTotaltime());
                                        finallincity.setSubpath(fincities.get(i).getSubpath());
                                        finallincity.setSchedule(hur1 + ":" + min1);
                                        finallincity.setName(jsonObject1.getString("name"));
                                        finallincityDao.insert(finallincity);
                                    }
                                }
                            }
                            Log.i("jarlenght", jar.length() + "");
                            Log.i("interval", interval + "");
                            break;
                        case "지하철":
                            String results;
                            StringBuffer sts = new StringBuffer();
                            URL urls = new URL("https://api.odsay.com/v1/api/subwayTimeTable?lang=0&stationID=" + jsonObject1.getString("startID") + "&wayCode=" + jsonObject1.getString("wayCode") + "&showExpressTime=1&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                            HttpURLConnection conns = (HttpURLConnection) urls.openConnection();
                            conns.setRequestMethod("GET");
                            conns.setRequestProperty("Content-type", "application/json");
                            conns.setDoOutput(true);
                            BufferedReader brs = new BufferedReader(new InputStreamReader(conns.getInputStream()));
                            while ((results = brs.readLine()) != null) {
                                sts.append(results);
                            }
                            results = sts.toString();
                            String list = "";
                            switch (nowDay) {
                                case "토":
                                    list = "SatList";
                                    break;
                                case " 일":
                                    list = "SunList";
                                    break;
                                default:
                                    list = "OrdList";
                                    break;
                            }
                            String way = "";
                            switch (jsonObject1.getInt("wayCode")) {
                                case 1:
                                    way = "up";
                                    break;
                                case 2:
                                    way = "down";
                                    break;
                            }
                            JSONObject jjjo = new JSONObject(results).getJSONObject("result");
                            JSONObject joo = (JSONObject) jjjo.getJSONObject(list);
                            JSONObject jooo = (JSONObject) joo.getJSONObject(way);
                            JSONArray array = (JSONArray) jooo.get("time");
                            Log.i("arraylen", array.length() + "");
                            JSONArray subtime2 = new JSONArray();
                            subtime2.put(0);
                            for (int m = 0; m < array.length(); m++) {
                                String[] abc = new String[100];
                                String[] subtime = new String[10];
                                JSONObject jn = (JSONObject) array.get(m);
                                Log.i("jnn", jn.toString());
                                String n = jn.getString("list");
                                int hou = jn.getInt("Idx");
                                Log.i("nnn", n);
                                subtime = n.split("\\s");
                                Log.i("subtimelen", subtime.length + "");
                                for (int s = 0; s < subtime.length; s++) {

                                    abc = subtime[s].split("\\(");
                                }
                                for (int y = 0; y < abc.length; y++) {
                                    if (y % 2 == 0) {
                                        int nk = Integer.parseInt(abc[y]) + hou * 60;
                                        subtime2.put(nk);
                                    }
                                }
                            }
                            JSONObject je = (JSONObject) jsonObject.get(0);
                            Log.i("jeje", je.toString());
                            int ti = dayTimes.get(0).getTime() - fincities.get(i).getTotaltime() - je.getInt("sectionTime");
                            int a = 1;
                            int b = 0;
                            int hour2 = 0;
                            int minute2 = 0;

                            while (true) {
                                b = subtime2.getInt(a);
                                if (b > ti) {
                                    b = subtime2.getInt(a - 1);
                                    break;
                                }
                                a++;
                            }
                            if (b == 0) {
                                finallincity.setName(jsonObject1.getString("name"));
                                finallincity.setStart(fincities.get(i).getStart());
                                finallincity.setPathtype(fincities.get(i).getPathtype());
                                finallincity.setTotalTime(fincities.get(i).getTotaltime());
                                finallincity.setFare(fincities.get(i).getFare());
                                finallincity.setSubpath(fincities.get(i).getSubpath());
                                finallincity.setSchedule("운행정보가 없습니다.");
                                finallincity.setDay(nowDay);
                                finallincityDao.insert(finallincity);
                            } else {
                                hour2 = (int) Math.floor((double) b / 60);
                                minute2 = b % 60;
                                String hur2 = String.format("%02d", hour2);
                                String min2 = String.format("%02d", minute2);
                                finallincity.setName(jsonObject1.getString("name"));
                                finallincity.setStart(fincities.get(i).getStart());
                                finallincity.setPathtype(fincities.get(i).getPathtype());
                                finallincity.setTotalTime(fincities.get(i).getTotaltime());
                                finallincity.setFare(fincities.get(i).getFare());
                                finallincity.setSubpath(fincities.get(i).getSubpath());
                                finallincity.setSchedule(hur2 + ":" + min2);
                                finallincity.setDay(nowDay);
                                finallincityDao.insert(finallincity);
                            }
                            Log.i("timearray", array.get(0).toString());
                    }
                    jsonObject1.getString("type");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        public String outGet(Double sx, Double sy) {
            String apiKey = "QEX7ua4WgGErXl+jz2xtjmfPZ4rRn5TwjDIrOhpo5Ho";
            try {
                String result;
                StringBuffer st = new StringBuffer();
                URL url = new URL("https://api.odsay.com/v1/api/searchPubTransPathT?lang=0&SX=" + sx + "&SY=" + sy + "&EX=127.2635&EY=37.0094&SearchType=1&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                conn.setDoOutput(true);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((result = br.readLine()) != null) {
                    st.append(result);
                }
                result = st.toString();
                JSONObject jio = new JSONObject(result);
                if (jio.has("error")) {
                    Log.i("outGetjio", jio.toString());
                    return "검색결과 없음";
                }
                String results;
                try {
                    JSONObject jsonObj = new JSONObject(result).getJSONObject("result");
                    String resultst = jsonObj.toString();
                    JSONObject jObj = new JSONObject(resultst);
                    JSONArray array = (JSONArray) jObj.get("path");
                    JSONArray outcity = new JSONArray();
                    String[] start = new String[20];
                    String[] last = new String[20];
                    int t = 0;
                    if (array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            Thread.sleep(200);
                            String ara = array.getJSONObject(i).toString();
                            JSONObject jsonobj = new JSONObject(ara).getJSONObject("info");
                            JSONObject subpath = new JSONObject(ara);
                            JSONArray subarray = (JSONArray) subpath.get("subPath");
                            if (subarray.length() > 0) {
                                for (int k = 0; k < subarray.length(); k++) {
                                    String startcheck;
                                    String lastcheck;
                                    JSONObject ac = new JSONObject();
                                    JSONObject bc = new JSONObject();
                                    JSONObject second = new JSONObject();
                                    JSONArray inarray = new JSONArray();
                                    String tra = subarray.getJSONObject(k).toString();
                                    JSONObject trafiic = (JSONObject) subarray.get(k);
                                    if (trafiic.get("endName").toString().equals("평택지제") ||
                                            trafiic.get("endName").toString().equals("안성종합버스터미널") ||
                                            trafiic.get("endName").toString().equals("평택시외버스터미널") ||
                                            trafiic.get("endName").toString().equals("평택") ||
                                            trafiic.get("endName").toString().equals("두원대정류소") ||
                                            trafiic.get("endName").toString().equals("죽산시외버스터미널") ||
                                            trafiic.get("endName").toString().equals("일죽시외버스터미널") ||
                                            trafiic.get("endName").toString().equals("공도정류소") ||
                                            trafiic.get("endName").toString().equals("대림동산정류소") ||
                                            trafiic.get("endName").toString().equals("중앙대안성캠퍼스정류소")) {
                                        if (Arrays.asList(start).contains(trafiic.get("startName").toString()) &&
                                                Arrays.asList(last).contains(trafiic.get("endName").toString())) {
                                        } else {
                                            start[t] = trafiic.get("startName").toString();
                                            last[t] = trafiic.get("endName").toString();
                                            ac.put("totalTime", jsonobj.get("totalTime"));
                                            ac.put("sx", trafiic.get("startX"));
                                            ac.put("sy", trafiic.get("startY"));
                                            ac.put("ex", trafiic.get("endX"));
                                            ac.put("ey", trafiic.get("endY"));
                                            ac.put("pay", trafiic.get("payment"));
                                            bc.put("startID", trafiic.get("startID"));
                                            bc.put("endID", trafiic.get("endID"));
                                            bc.put("trafficType", trafiic.get("trafficType"));
                                            second.put("start", trafiic.get("startName"));
                                            second.put("end", trafiic.get("endName"));
                                            second.put("sectiontime", trafiic.get("sectionTime"));
                                            second.put("subpath", bc);
                                            ac.put("pathType", subpath.get("pathType"));
                                            ac.put("second", second);
                                            outcity.put(ac);
                                            t++;
                                        }
                                    }


                                }
                            }
                        }
                    }
                    Log.i("outcity", outcity.toString());
                    String out = firstGet(outcity, sx, sy);

                    return out;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "hello";
            } catch (Exception exception) {
                return null;
            }
        }

        public String firstGet(JSONArray t, Double sx, Double sy) throws JSONException, InterruptedException {
            String apiKey = "QEX7ua4WgGErXl+jz2xtjmfPZ4rRn5TwjDIrOhpo5Ho";
            JSONArray jsonArray = (JSONArray) t;
            String jsona = t.toString();
            JSONArray jsonArray1 = new JSONArray(jsona);
            JSONArray outcity = new JSONArray();
            if (jsonArray.length() > 0) {
                for (int l = 0; l < jsonArray.length(); l++) {
                    Thread.sleep(200);
                    JSONObject jsonObj = (JSONObject) jsonArray.get(l);
                    JSONObject jsonObj2 = (JSONObject) jsonArray1.get(l);
                    try {
                        String[] type = new String[100];
                        int p = 0;
                        int op = 0;
                        String result;
                        StringBuffer st = new StringBuffer();
                        URL url = new URL("https://api.odsay.com/v1/api/searchPubTransPathT?lang=0&SX=" + sx + "&SY=" + sy + "&EX=" + jsonObj.get("sx") + "&EY=" + jsonObj.get("sy") + "&SearchType=0&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Content-type", "application/json");
                        conn.setDoOutput(true);
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((result = br.readLine()) != null) {
                            st.append(result);
                        }
                        result = st.toString();
                        JSONObject jio = new JSONObject(result);
                        Log.i("jio", jio.toString());
                        if (jio.has("error")) {
                            return "null";
                        }
                        JSONObject jj = new JSONObject(result).getJSONObject("result");
                        String resultst = jj.toString();
                        JSONObject jObj = new JSONObject(resultst);
                        JSONArray array = (JSONArray) jObj.get("path");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject ac = new JSONObject();
                                String ara = array.getJSONObject(i).toString();
                                JSONObject arar = (JSONObject) array.getJSONObject(i);
                                if (Arrays.asList(type).contains(arar.get("pathType").toString())) {
                                } else {
                                    Thread.sleep(400);
                                    type[p] = arar.get("pathType").toString();
                                    p++;
                                    JSONObject jsonobj = new JSONObject(ara).getJSONObject("info");
                                    JSONObject subpath = new JSONObject(ara);
                                    JSONArray subarray = (JSONArray) subpath.get("subPath");
                                    JSONArray inarray = new JSONArray();
                                    if (subarray.length() > 0) {
                                        for (int k = 0; k < subarray.length(); k++) {
                                            String tra = subarray.getJSONObject(k).toString();
                                            Log.i("bce", k + "" + tra);
                                            JSONObject trafiic = (JSONObject) subarray.get(k);
                                            switch (trafiic.get("trafficType").toString()) {
                                                case "3":
                                                    JSONObject walkdata = new JSONObject();
                                                    walkdata.put("type", "걷기");
                                                    walkdata.put("name", "");
                                                    walkdata.put("distance", trafiic.get("distance"));
                                                    walkdata.put("sectionTime", trafiic.get("sectionTime"));
                                                    inarray.put(walkdata);
                                                    break;
                                                case "2":
                                                    JSONObject busdata = new JSONObject();
                                                    JSONObject lane = new JSONObject(tra);
                                                    JSONArray lane1 = (JSONArray) lane.get("lane");
                                                    JSONObject lne = (JSONObject) lane1.get(0);
                                                    busdata.put("type", "버스");
                                                    busdata.put("name", lne.get("busNo"));
                                                    busdata.put("cityCode", lne.get("busCityCode"));
                                                    busdata.put("busID", lne.get("busID"));
                                                    busdata.put("start", trafiic.get("startName").toString());
                                                    busdata.put("startID", trafiic.get("startID"));
                                                    if (trafiic.has("startLocalStationID")) {
                                                        busdata.put("localID", trafiic.get("startLocalStationID").toString());
                                                    }
                                                    inarray.put(busdata);
                                                    break;
                                                case "1":
                                                    JSONObject subway = new JSONObject();
                                                    JSONObject lane2 = new JSONObject(tra);
                                                    JSONArray lane3 = (JSONArray) lane2.get("lane");
                                                    JSONObject ln4 = (JSONObject) lane3.get(0);
                                                    StringBuffer sb2 = new StringBuffer();
                                                    String result2;
                                                    URL ul = new URL("https://api.odsay.com/v1/api/subwayPath?lang=0&CID=" + ln4.get("subwayCityCode").toString() + "&SID=" + trafiic.get("startID").toString() + "&EID=" + trafiic.get("endID").toString() + "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                                                    HttpURLConnection con = (HttpURLConnection) ul.openConnection();
                                                    con.setRequestMethod("GET");
                                                    con.setRequestProperty("Content-type", "application/json");
                                                    con.setDoOutput(true);
                                                    BufferedReader br2 = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                                    while ((result2 = br2.readLine()) != null) {
                                                        sb2.append(result2);
                                                    }
                                                    result2 = sb2.toString();
                                                    JSONObject jsonj = new JSONObject(result2).getJSONObject("result");
                                                    JSONObject js = (JSONObject) jsonj.get("driveInfoSet");
                                                    JSONArray joj = (JSONArray) js.get("driveInfo");
                                                    JSONObject jjj = (JSONObject) joj.get(0);
                                                    subway.put("type", "지하철");
                                                    subway.put("name", ln4.get("name"));
                                                    subway.put("wayCode", trafiic.get("wayCode"));
                                                    subway.put("startID", trafiic.get("startID"));
                                                    subway.put("wayName", jjj.get("wayName"));
                                                    inarray.put(subway);
                                                    break;
                                            }
                                        }

                                    }
                                    if (op > 0) {
                                        int payment = (int) jsonObj2.get("pay") + (int) jsonobj.get("payment");
                                        int totalTime = (int) jsonObj2.get("totalTime") + (int) jsonobj.get("totalTime");
                                        jsonObj2.put("pay", payment);
                                        jsonObj2.put("totalTime", totalTime);
                                        JSONObject abd = (JSONObject) inarray.get(0);
                                        jsonObj2.put("walktime", abd.get("sectionTime"));
                                        ac.put("totalTime", jsonobj.get("totalTime"));
                                        ac.put("start", jsonobj.get("firstStartStation"));
                                        ac.put("end", jsonobj.get("lastEndStation"));
                                        ac.put("subpath", inarray);
                                        jsonObj2.put("first", ac);
                                        outcity.put(jsonObj2);
                                    } else {
                                        int payment = (int) jsonObj.get("pay") + (int) jsonobj.get("payment");
                                        int totalTime = (int) jsonObj.get("totalTime") + (int) jsonobj.get("totalTime");
                                        jsonObj.put("pay", payment);
                                        jsonObj.put("totalTime", totalTime);
                                        JSONObject abd = (JSONObject) inarray.get(0);
                                        jsonObj.put("walktime", abd.get("sectionTime"));
                                        ac.put("totalTime", jsonobj.get("totalTime"));
                                        ac.put("start", jsonobj.get("firstStartStation"));
                                        ac.put("end", jsonobj.get("lastEndStation"));
                                        ac.put("subpath", inarray);
                                        jsonObj.put("first", ac);
                                        outcity.put(jsonObj);
                                        op++;
                                    }

                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                final int chunkSize = 2048;
                for (int i = 0; i < outcity.toString().length(); i += chunkSize) {
                    Log.d("outcity8", outcity.toString().substring(i, Math.min(outcity.toString().length(), i + chunkSize)));
                }
                String out = ThirdandGet(outcity);
                return out;
            }

            return "noway";
        }

        public String ThirdandGet(JSONArray t) throws JSONException, InterruptedException {
            String apiKey = "QEX7ua4WgGErXl+jz2xtjmfPZ4rRn5TwjDIrOhpo5Ho";
            JSONArray jsonArray = (JSONArray) t;
            JSONArray outcity = new JSONArray();
            if (jsonArray.length() > 0) {
                FoutcityDatabase outdatabase = Room.databaseBuilder(getApplicationContext(), FoutcityDatabase.class, "foutcity2.db")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build();
                foutcityDao = outdatabase.foutcityDao();
                for (int l = 0; l < jsonArray.length(); l++) {
                    String t2 = t.toString();
                    JSONArray jsonArray1 = new JSONArray(t2);
                    Thread.sleep(400);
                    JSONObject jsonObj = (JSONObject) jsonArray.get(l);
                    JSONObject jje = jsonObj.getJSONObject("first");
                    Log.i("jje", jje.getString("start"));
                    JSONObject jsonObj2 = (JSONObject) jsonArray1.get(l);
                    try {
                        String result;
                        StringBuffer st = new StringBuffer();
                        String[] type = new String[100];
                        int p = 0;
                        URL url = new URL("https://api.odsay.com/v1/api/searchPubTransPathT?lang=0&SX=" + jsonObj.get("ex") + "&SY=" + jsonObj.get("ey") + "&EX=127.2635&EY=37.0094&SearchType=0&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Content-type", "application/json");
                        conn.setDoOutput(true);
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((result = br.readLine()) != null) {
                            st.append(result);
                        }
                        result = st.toString();
                        JSONObject jj = new JSONObject(result).getJSONObject("result");
                        String resultst = jj.toString();
                        JSONObject jObj = new JSONObject(resultst);
                        JSONArray array = (JSONArray) jObj.get("path");
                        int op = 0;
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject ac = new JSONObject();
                                String ara = array.getJSONObject(i).toString();
                                JSONObject arar = (JSONObject) array.getJSONObject(i);
                                if (Arrays.asList(type).contains(arar.get("pathType").toString())) {

                                } else {
                                    Thread.sleep(200);
                                    type[p] = arar.get("pathType").toString();
                                    p++;
                                    JSONObject jsonobj = new JSONObject(ara).getJSONObject("info");
                                    JSONObject subpath = new JSONObject(ara);
                                    JSONArray subarray = (JSONArray) subpath.get("subPath");
                                    JSONArray inarray = new JSONArray();
                                    if (subarray.length() > 0) {
                                        for (int k = 0; k < subarray.length(); k++) {
                                            String tra = subarray.getJSONObject(k).toString();
                                            JSONObject trafiic = (JSONObject) subarray.get(k);
                                            switch (trafiic.get("trafficType").toString()) {
                                                case "3":
                                                    JSONObject walkdata = new JSONObject();
                                                    walkdata.put("type", "걷기");
                                                    walkdata.put("name", "");
                                                    walkdata.put("distance", trafiic.get("distance"));
                                                    walkdata.put("sectionTime", trafiic.get("sectionTime"));
                                                    inarray.put(walkdata);
                                                    break;
                                                case "2":
                                                    JSONObject busdata = new JSONObject();
                                                    JSONObject lane = new JSONObject(tra);
                                                    JSONArray lane1 = (JSONArray) lane.get("lane");
                                                    JSONObject lne = (JSONObject) lane1.get(0);
                                                    busdata.put("type", "버스");
                                                    busdata.put("name", lne.get("busNo"));
                                                    busdata.put("cityCode", lne.get("busCityCode"));
                                                    busdata.put("busID", lne.get("busID"));
                                                    busdata.put("start", trafiic.get("startName").toString());
                                                    busdata.put("startID", trafiic.get("startID"));
                                                    if (trafiic.has("startLocalStationID")) {
                                                        busdata.put("localID", trafiic.get("startLocalStationID").toString());
                                                    }
                                                    inarray.put(busdata);
                                                    break;
                                                case "1":
                                                    JSONObject subway = new JSONObject();
                                                    JSONObject lane2 = new JSONObject(tra);
                                                    JSONArray lane3 = (JSONArray) lane2.get("lane");
                                                    JSONObject ln4 = (JSONObject) lane3.get(0);
                                                    StringBuffer sb2 = new StringBuffer();
                                                    String result2;
                                                    URL ul = new URL("https://api.odsay.com/v1/api/subwayPath?lang=0&CID=" + ln4.get("subwayCityCode").toString() + "&SID=" + trafiic.get("startID").toString() + "&EID=" + trafiic.get("endID").toString() + "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                                                    HttpURLConnection con = (HttpURLConnection) ul.openConnection();
                                                    con.setRequestMethod("GET");
                                                    con.setRequestProperty("Content-type", "application/json");
                                                    con.setDoOutput(true);
                                                    BufferedReader br2 = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                                    while ((result2 = br2.readLine()) != null) {
                                                        sb2.append(result2);
                                                    }
                                                    result2 = sb2.toString();
                                                    JSONObject jsonj = new JSONObject(result2).getJSONObject("result");
                                                    JSONObject js = (JSONObject) jsonj.get("driveInfoSet");
                                                    JSONArray joj = (JSONArray) js.get("driveInfo");
                                                    JSONObject jjj = (JSONObject) joj.get(0);
                                                    subway.put("type", "지하철");
                                                    subway.put("name", ln4.get("name"));
                                                    subway.put("wayCode", trafiic.get("wayCode"));
                                                    subway.put("startID", trafiic.get("startID"));
                                                    subway.put("wayName", jjj.get("wayName"));
                                                    inarray.put(subway);
                                                    break;
                                            }
                                        }
                                    }
                                    if (op > 0) {
                                        int payment = (int) jsonObj2.getInt("pay") + (int) jsonobj.getInt("payment");
                                        int totalTime = (int) jsonObj2.getInt("totalTime") + (int) jsonobj.getInt("totalTime");
                                        jsonObj2.put("pay", payment);
                                        JSONObject abd = (JSONObject) inarray.get(0);
                                        jsonObj2.put("totalTime", totalTime);
                                        ac.put("totalTime", jsonobj.get("totalTime"));
                                        ac.put("start", jsonobj.get("firstStartStation"));
                                        ac.put("end", jsonobj.get("lastEndStation"));
                                        ac.put("subpath", inarray);
                                        jsonObj2.put("third", ac);
                                        outcity.put(jsonObj2);
                                        foutcity.setFare(payment);
                                        foutcity.setPathtype(jsonObj2.getInt("pathType"));
                                        foutcity.setFirstpath(jsonObj2.getString("first"));
                                        foutcity.setSecondpath(jsonObj2.getString("second"));
                                        foutcity.setThirdpath(ac.toString());
                                        foutcity.setTotaltime(totalTime);
                                        foutcity.setStart(jje.getString("start"));
                                        foutcityDao.insert(foutcity);
                                    } else {
                                        int payment = (int) jsonObj.getInt("pay") + (int) jsonobj.getInt("payment");
                                        int totalTime = (int) jsonObj.getInt("totalTime") + (int) jsonobj.getInt("totalTime");
                                        jsonObj.put("pay", payment);
                                        JSONObject abd = (JSONObject) inarray.get(0);
                                        jsonObj.put("totalTime", totalTime);
                                        ac.put("totalTime", jsonobj.get("totalTime"));
                                        ac.put("start", jsonobj.get("firstStartStation"));
                                        ac.put("end", jsonobj.get("lastEndStation"));
                                        ac.put("subpath", inarray);
                                        jsonObj.put("third", ac);
                                        outcity.put(jsonObj);
                                        foutcity.setFare(payment);
                                        foutcity.setPathtype(jsonObj.getInt("pathType"));
                                        foutcity.setFirstpath(jsonObj.getString("first"));
                                        foutcity.setSecondpath(jsonObj.getString("second"));
                                        foutcity.setThirdpath(ac.toString());
                                        foutcity.setTotaltime(totalTime);
                                        foutcity.setStart(jje.getString("start"));
                                        foutcityDao.insert(foutcity);
                                        op = op + 1;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            String q = Outtime();
//            Log.i("Outtime",q);
            final int chunkSize = 2048;
            for (int i = 0; i < outcity.toString().length(); i += chunkSize) {
                Log.d("outcity4", outcity.toString().substring(i, Math.min(outcity.toString().length(), i + chunkSize)));
            }
            return outcity.toString();
        }

        public String Outtime() {
            String apiKey = "QEX7ua4WgGErXl+jz2xtjmfPZ4rRn5TwjDIrOhpo5Ho";
            FoutcityDatabase foutcityDatabase = Room.databaseBuilder(getApplicationContext(), FoutcityDatabase.class, "foutcity2.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            FinalloutcityDatabase database1 = Room.databaseBuilder(getApplicationContext(), FinalloutcityDatabase.class, "finalloutcity.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            DayTimeDatabase database = Room.databaseBuilder(getApplicationContext(), DayTimeDatabase.class, "daytime.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
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
            List<DayTime> dayTimes = dayTimeDao.getDay(nowDay);
            if (dayTimes.size() == 0) {
                return null;
            }
            finalloutcityDao = database1.finalloutcityDao();
            List<Finalloutcity> finalloutcities = finalloutcityDao.getFday(nowDay);
            if (finalloutcities.size() > 0) {
                finalloutcityDao.delday(nowDay);
            }
            foutcityDao = foutcityDatabase.foutcityDao();
            List<Foutcity> foutcities = foutcityDao.getFoutctiyAll();
            if (foutcities.size() == 0) {
                return null;
            }
            try {
                for (int i = 0; i < foutcities.size(); i++) {
                    JSONObject timecheck = new JSONObject();
                    Thread.sleep(200);
                    JSONObject array1 = new JSONObject(foutcities.get(i).getThirdpath());
                    JSONObject array2 = new JSONObject(foutcities.get(i).getSecondpath());
                    JSONObject array3 = new JSONObject(foutcities.get(i).getFirstpath());
                    String Name = "";
                    JSONArray array = (JSONArray) array1.get("subpath");
                    JSONArray arrayy = (JSONArray) array3.get("subpath");
                    JSONObject object1 = (JSONObject) array.getJSONObject(1);
                    JSONObject object3 = (JSONObject) arrayy.getJSONObject(1);
                    JSONObject object2 = (JSONObject) array2.getJSONObject("subpath");
                    Log.i("ddddd", object2.get("trafficType") + "");
                    switch (object2.getInt("trafficType")) {
                        case 4:
                            Name = "열차";
                            break;
                        case 5:
                            Name = "고속버스";
                            break;
                        case 6:
                            Name = "시외버스";
                            break;
                    }
                    Log.i("dsdfs", object1.toString());
                    Log.i("dsdfs", object1.getString("type"));
                    switch (object2.getInt("trafficType")){
                        case 4:
                            String result1;
                            StringBuffer st1 = new StringBuffer();
//                            URL url1 = new URL("https://api.odsay.com/v1/api/trainServiceTime?lang=0&startStationID=3300140&endStationID=3300335&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
//                            URL url1 = new URL("https://api.odsay.com/v1/api/expressServiceTime?lang=0&startStationID=4000135&endStationID=4000172&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));

                            URL url1 = new URL("https://api.odsay.com/v1/api/trainServiceTime?lang=0&startStationID=" + object2.getString("startID") + "&endStationID=" + object2.getString("endID") + "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                            HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
                            con1.setRequestMethod("GET");
                            con1.setRequestProperty("Content-type", "application/json");
                            con1.setDoOutput(true);
                            BufferedReader br1 = new BufferedReader(new InputStreamReader(con1.getInputStream()));
                            while ((result1 = br1.readLine()) != null) {
                                st1.append(result1);
                            }
                            result1 = st1.toString();
                            JSONObject jjo1 = new JSONObject(result1).getJSONObject("result");
                            JSONArray ar = (JSONArray) jjo1.get("station");
                            JSONArray ac = new JSONArray();
                            for(int aa = 0; aa<ar.length();aa++){
                                JSONObject ad = new JSONObject();
                                JSONObject jii = (JSONObject) ar.get(aa);
                                String qqe = jii.getString("arrivalTime");
                                String qqq = jii.getString("departureTime");
                                String[] qqq1 = qqq.split(":");
                                String[] qqe1 = qqe.split(":");
                                int arrival = Integer.parseInt(qqe1[0])*60 + Integer.parseInt(qqe1[1]);
                                int departure = Integer.parseInt(qqq1[0])*60 + Integer.parseInt(qqq1[1]);
                                ad.put("arrival",arrival);
                                ad.put("departure",departure);
                                ac.put(ad);
                            }
                            JSONArray sortac = new JSONArray();
                            List<JSONObject> jsonvalue = new ArrayList<JSONObject>();
                            for(int qq=0; qq<ac.length();qq++){
                                jsonvalue.add(ac.getJSONObject(qq));
                            }
                            Collections.sort(jsonvalue, new Comparator<JSONObject>() {
                                @Override
                                public int compare(JSONObject o1, JSONObject o2) {
                                    int valA=0;
                                    int valB=0;
                                    try {
                                        valA = o1.getInt("arrival");
                                        valB = o2.getInt("arrival");

                                    } catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                    return Integer.compare(valA,valB);
                                }
                            });
                            for(int qr = 0; qr<ac.length();qr++){
                                sortac.put(jsonvalue.get(qr));
                            }
                            for(int nn = 0; nn<sortac.length();nn++){
                                JSONObject de = (JSONObject) sortac.get(nn);
                                if(de.getInt("arrival")<dayTimes.get(0).getTime() - array1.getInt("totalTime")){
                                    timecheck.put("arrival",de.getInt("arrival"));
                                    timecheck.put("departure",de.getInt("departure"));
                                    int wastetime = de.getInt("arrival") - de.getInt("departure");
                                    timecheck.put("totaltime",wastetime);
                                }
                            }
                            break;
                        case 5:
                            String result2;
                            StringBuffer st2 = new StringBuffer();
//                            URL url1 = new URL("https://api.odsay.com/v1/api/trainServiceTime?lang=0&startStationID=3300140&endStationID=3300335&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
//                            URL url2 = new URL("https://api.odsay.com/v1/api/expressServiceTime?lang=0&startStationID=4000135&endStationID=4000172&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                            URL url2 = new URL("https://api.odsay.com/v1/api/expressServiceTime?lang=&startStationID=" + object2.getString("startID") + "&endStationID=" + object2.getString("endID") + "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                            HttpURLConnection con2 = (HttpURLConnection) url2.openConnection();
                            con2.setRequestMethod("GET");
                            con2.setRequestProperty("Content-type", "application/json");
                            con2.setDoOutput(true);
                            BufferedReader br2 = new BufferedReader(new InputStreamReader(con2.getInputStream()));
                            while ((result2 = br2.readLine()) != null) {
                                st2.append(result2);
                            }
                            result2 = st2.toString();
                            JSONObject jjo2 = new JSONObject(result2).getJSONObject("result");
                            JSONArray jo2 = (JSONArray) jjo2.get("station");
                            JSONObject jji2 = (JSONObject) jo2.get(0);
                            String[] nii = jji2.getString("schedule").replaceAll(System.getProperty("line.separator"),"/").replaceAll("\\(우등\\)","").split("/");
                            int nii2;
                            for(int qw = 0;qw<nii.length;qw++){
                                String[] jk = nii[qw].split(":");
                               nii2 = Integer.parseInt(jk[0])*60+Integer.parseInt(jk[1]);
                               if(nii2 < dayTimes.get(0).getTime()-array1.getInt("totalTime")){
                                   timecheck.put("departure",nii2);
                                   int arrival = nii2+array2.getInt("sectiontime");
                                   timecheck.put("arrival",arrival);
                                   timecheck.put("totaltime",array2.getInt("sectiontime"));
                               }
                            }
                            break;
                        case 6:
                            String result3;
                            StringBuffer st3 = new StringBuffer();
                            URL url3 = new URL("https://api.odsay.com/v1/api/intercityServiceTime?lang=&startStationID=" + object2.getString("startID") + "&endStationID=" + object2.getString("endID") + "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                            HttpURLConnection con3 = (HttpURLConnection) url3.openConnection();
                            con3.setRequestMethod("GET");
                            con3.setRequestProperty("Content-type", "application/json");
                            con3.setDoOutput(true);
                            BufferedReader br3 = new BufferedReader(new InputStreamReader(con3.getInputStream()));
                            while ((result3 = br3.readLine()) != null) {
                                st3.append(result3);
                            }
                            result3 = st3.toString();
                            JSONObject jjo3 = new JSONObject(result3).getJSONObject("result");
                            JSONArray jo3 = (JSONArray) jjo3.get("station");
                            JSONObject jji3 = (JSONObject) jo3.get(0);
                            String[] sde = jji3.getString("schedule").replaceAll(System.getProperty("line.separator"),"/").split("/");
                            int nii3;
                            for(int qw2 = 0; qw2< sde.length;qw2++){
                                String[] jk2 = sde[qw2].split(":");
                                nii3 = Integer.parseInt(jk2[0])*60 + Integer.parseInt(jk2[1]);
                                if(nii3 < dayTimes.get(0).getTime()-array1.getInt("totalTime")){
                                    timecheck.put("departure",nii3);
                                    int arrival = nii3 + array2.getInt("sectiontime");
                                    timecheck.put("arrival",arrival);
                                    timecheck.put("totaltime",array2.getInt("sectiontime"));
                                }
                            }
                            break;
                    }
                    int hour = 0;
                    int mintue = 0;
                    switch (object3.getString("type")){
                        case "버스":
                            String result1;
                            StringBuffer st1 = new StringBuffer();
                            URL url1 = new URL("https://api.odsay.com/v1/api/busLaneDetail?lang=0&busID=" + object3.getString("busID") + "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                            HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
                            con1.setRequestMethod("GET");
                            con1.setRequestProperty("Content-type", "application/json");
                            con1.setDoOutput(true);
                            BufferedReader br1 = new BufferedReader(new InputStreamReader(con1.getInputStream()));
                            while ((result1 = br1.readLine()) != null) {
                                st1.append(result1);
                            }
                            result1 = st1.toString();
                            JSONObject jo = new JSONObject(result1).getJSONObject("result");
                            String firsttime = jo.getString("busFirstTime");
                            String[] q;
                            q = firsttime.split(":");
                            int interval1=0;
                            switch (nowDay){
                                case "토":
                                    interval1 = jo.getInt("bus_Interval_Sat");
                                    break;
                                case "일":
                                    interval1 = jo.getInt("bus_Interval_Sat");
                                    break;
                                default:
                                    interval1 = jo.getInt("bus_Interval_Week");
                                    break;
                            }
                            JSONArray jar1 = (JSONArray) jo.get("station");
                            for(int k =0; k<jar1.length();k++){
                                JSONObject job1 = jar1.getJSONObject(k);
                                if(object3.getInt("startID")== job1.getInt("stationID")){
                                    int gettime1 = (int) Math.ceil((double) 1.5 * job1.getInt("idx"));
                                    int r1 = (int) (Integer.parseInt(q[0]) * 60) + (int) (Integer.parseInt(q[1])) + gettime1;
                                    int t1 = timecheck.getInt("departure") - array3.getInt("totalTime");
                                    if(interval1==0||r1>t1){
                                        finalloutcity.setDay(nowDay);
                                        finalloutcity.setFare(foutcities.get(i).getFare());
                                        finalloutcity.setStart(foutcities.get(i).getStart());
                                        finalloutcity.setPathtype(foutcities.get(i).getPathtype());
                                        finalloutcity.setTotalTime(foutcities.get(i).getTotaltime());
                                        finalloutcity.setFirstpath(foutcities.get(i).getFirstpath());
                                        finalloutcity.setSecondpath(foutcities.get(i).getSecondpath());
                                        finalloutcity.setThirdpath(foutcities.get(i).getThirdpath());
                                        finalloutcity.setSchedule("운행정보가 없습니다.");
                                        finalloutcity.setName(Name);
                                        finalloutcityDao.insert(finalloutcity);
                                    } else {
                                        while(r1<t1){
                                            r1 = r1 + interval1;
                                            if(r1>t1){
                                                r1=r1-interval1;
                                                break;
                                            }
                                        }
                                        hour = (int) Math.floor((double) r1 / 60);
                                        mintue = r1 % 60;
                                        timecheck.put("schedule",hour+":"+mintue);
                                    }
                                }
                            }
                            break;
                        case "지하철":
                            String result2;
                            StringBuffer st2 = new StringBuffer();
                            URL url2 = new URL("https://api.odsay.com/v1/api/subwayTimeTable?lang=0&stationID=" + object3.getString("startID") + "&wayCode=" + object3.getString("wayCode") + "&showExpressTime=1&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                            HttpURLConnection con2 = (HttpURLConnection) url2.openConnection();
                            con2.setRequestMethod("GET");
                            con2.setRequestProperty("Content-type", "application/json");
                            con2.setDoOutput(true);
                            BufferedReader br2 = new BufferedReader(new InputStreamReader(con2.getInputStream()));
                            while ((result2 = br2.readLine()) != null) {
                                st2.append(result2);
                            }
                            result2 = st2.toString();
                            String list = "";
                            switch (nowDay) {
                                case "토":
                                    list = "SatList";
                                    break;
                                case "일":
                                    list = "SunList";
                                    break;
                                default:
                                    list = "OrdList";
                                    break;
                            }
                            String way = "";
                            switch (object3.getInt("wayCode")) {
                                case 1:
                                    way = "up";
                                    break;
                                case 2:
                                    way = "down";
                                    break;
                            }
                            JSONObject jjo = new JSONObject(result2).getJSONObject("result");
                            JSONObject joo = (JSONObject) jjo.getJSONObject(list);
                            JSONObject jooo = (JSONObject) joo.getJSONObject(way);
                            JSONArray arr = (JSONArray) jooo.get("time");
                            JSONArray subtime2 = new JSONArray();
                            subtime2.put(0);
                            for(int m=0; m<arr.length();m++){
                                String[] abc = new String[100];
                                String[] subtime ;
                                JSONObject jn = (JSONObject) arr.get(m);
                                String nn = jn.getString("list");
                                int hou = jn.getInt("Idx");
                                subtime = nn.split("\\s");
                                for (int ss =0; ss< subtime.length;ss++){
                                    abc = subtime[ss].split("\\(");
                                }
                                for (int yy =0; yy<abc.length;yy++){
                                    if(yy%2==0){
                                        int nk = Integer.parseInt(abc[yy])+hou*60;
                                        subtime2.put(nk);
                                    }
                                }
                            }
                            int t1 = timecheck.getInt("departure") - array3.getInt("totalTime");
                            int a = 1;
                            int b =0;
                            while(true){
                                b = subtime2.getInt(a);
                                if(b>t1){
                                    b = subtime2.getInt(a-1);
                                    break;
                                }
                                a++;
                            }
                            if(b==0) {
                                finalloutcity.setDay(nowDay);
                                finalloutcity.setFare(foutcities.get(i).getFare());
                                finalloutcity.setStart(foutcities.get(i).getStart());
                                finalloutcity.setPathtype(foutcities.get(i).getPathtype());
                                finalloutcity.setTotalTime(foutcities.get(i).getTotaltime());
                                finalloutcity.setFirstpath(foutcities.get(i).getFirstpath());
                                finalloutcity.setSecondpath(foutcities.get(i).getSecondpath());
                                finalloutcity.setThirdpath(foutcities.get(i).getThirdpath());
                                finalloutcity.setSchedule("운행정보가 없습니다.");
                                finalloutcity.setName(Name);
                                finalloutcityDao.insert(finalloutcity);

                            }else {
                                hour = (int) Math.floor((double) b / 60);
                                mintue = b % 60;
                                timecheck.put("schedule", hour + ":" + mintue);
                            }
                            break;
                    }
                    switch (object1.getString("type")) {
                        case "버스":
                            String result1;
                            StringBuffer st1 = new StringBuffer();
                            URL url1 = new URL("https://api.odsay.com/v1/api/busLaneDetail?lang=0&busID=" + object1.getString("busID") + "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                            HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
                            con1.setRequestMethod("GET");
                            con1.setRequestProperty("Content-type", "application/json");
                            con1.setDoOutput(true);
                            BufferedReader br1 = new BufferedReader(new InputStreamReader(con1.getInputStream()));
                            while ((result1 = br1.readLine()) != null) {
                                st1.append(result1);
                            }
                            result1 = st1.toString();
                            JSONObject jo = new JSONObject(result1).getJSONObject("result");
                            String thridtime = jo.getString("busFirstTime");
                            String[] q = new String[2];
                            q = thridtime.split(":");
                            int interval1 = 0;
                            switch (nowDay) {
                                case "토":
                                    if (jo.has("bus_Interval_Sat"))
                                        interval1 = jo.getInt("bus_Interval_Sat");
                                    break;
                                case "일":
                                    interval1 = jo.getInt("bus_Interval_Sun");
                                    break;
                                default:
                                    interval1 = jo.getInt("bus_Interval_Week");
                                    break;
                            }
                            JSONArray jar1 = (JSONArray) jo.get("station");
                            for (int k = 0; k < jar1.length(); k++) {
                                JSONObject job1 = (JSONObject) jar1.getJSONObject(k);
                                if (object1.getInt("startID") == job1.getInt("stationID")) {
                                    int gettime1 = (int) Math.ceil((double) 1.5 * job1.getInt("idx"));
                                    int r1 = (int) (Integer.parseInt(q[0]) * 60) + (int) (Integer.parseInt(q[1])) + gettime1;
                                    int t1 = timecheck.getInt("arrival") - (int) array1.getInt("totalTime");
                                    if (interval1 == 0||r1>t1) {
                                        finalloutcity.setDay(nowDay);
                                        finalloutcity.setFare(foutcities.get(i).getFare());
                                        finalloutcity.setStart(foutcities.get(i).getStart());
                                        finalloutcity.setPathtype(foutcities.get(i).getPathtype());
                                        finalloutcity.setTotalTime(foutcities.get(i).getTotaltime());
                                        finalloutcity.setFirstpath(foutcities.get(i).getFirstpath());
                                        finalloutcity.setSecondpath(foutcities.get(i).getSecondpath());
                                        finalloutcity.setThirdpath(foutcities.get(i).getThirdpath());
                                        finalloutcity.setSchedule("운행정보가 없습니다.");
                                        finalloutcity.setName(Name);
                                        finalloutcityDao.insert(finalloutcity);
                                    } else {
                                        while (r1 < t1) {
                                            r1 = r1 + interval1;
                                            if (r1 > t1) {
                                                r1 = r1 - interval1;
                                                break;
                                            }
                                        }
                                        int total = timecheck.getInt("totaltime")+array1.getInt("totalTime")+array3.getInt("totalTime");
                                        finalloutcity.setDay(nowDay);
                                        finalloutcity.setFare(foutcities.get(i).getFare());
                                        finalloutcity.setStart(foutcities.get(i).getStart());
                                        finalloutcity.setPathtype(foutcities.get(i).getPathtype());
                                        finalloutcity.setTotalTime(total);
                                        finalloutcity.setFirstpath(foutcities.get(i).getFirstpath());
                                        finalloutcity.setSecondpath(foutcities.get(i).getSecondpath());
                                        finalloutcity.setThirdpath(foutcities.get(i).getThirdpath());
                                        finalloutcity.setSchedule(timecheck.getString("schedule"));
                                        finalloutcity.setName(Name);
                                        finalloutcityDao.insert(finalloutcity);
                                    }
                                }
                            }
                            break;
                        case "지하철":
                            String result2;
                            StringBuffer st2 = new StringBuffer();
                            URL url2 = new URL("https://api.odsay.com/v1/api/subwayTimeTable?lang=0&stationID=" + object1.getString("startID") + "&wayCode=" + object1.getString("wayCode") + "&showExpressTime=1&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
                            HttpURLConnection con2 = (HttpURLConnection) url2.openConnection();
                            con2.setRequestMethod("GET");
                            con2.setRequestProperty("Content-type", "application/json");
                            con2.setDoOutput(true);
                            BufferedReader br2 = new BufferedReader(new InputStreamReader(con2.getInputStream()));
                            while ((result2 = br2.readLine()) != null) {
                                st2.append(result2);
                            }
                            result2 = st2.toString();
                            String list = "";
                            switch (nowDay) {
                                case "토":
                                    list = "SatList";
                                    break;
                                case "일":
                                    list = "SunList";
                                    break;
                                default:
                                    list = "OrdList";
                                    break;
                            }
                            String way = "";
                            switch (object1.getInt("wayCode")) {
                                case 1:
                                    way = "up";
                                    break;
                                case 2:
                                    way = "down";
                                    break;
                            }
                            JSONObject jjo = new JSONObject(result2).getJSONObject("result");
                            JSONObject joo = (JSONObject) jjo.getJSONObject(list);
                            JSONObject jooo = (JSONObject) joo.getJSONObject(way);
                            JSONArray arr = (JSONArray) jooo.get("time");
                            JSONArray subtime2 = new JSONArray();
                            subtime2.put(0);
                            for (int m = 0; m < arr.length(); m++) {
                                String[] abc = new String[100];
                                String[] subtime = new String[10];
                                JSONObject jn = (JSONObject) arr.get(m);
                                String nn = jn.getString("list");
                                int hou = jn.getInt("Idx");
                                subtime = nn.split("\\s");
                                for (int ss = 0; ss < subtime.length; ss++) {
                                    abc = subtime[ss].split("\\(");
                                }
                                for (int yy = 0; yy < abc.length; yy++) {
                                    if (yy % 2 == 0) {
                                        int nk = Integer.parseInt(abc[yy]) + hou * 60;
                                        subtime2.put(nk);
                                    }
                                }
                            }
                            int t1 = timecheck.getInt("arrival") - (int) array1.getInt("totalTime") - 5;
                            int a = 1;
                            int b = 0;
                            while (true) {
                                b = subtime2.getInt(a);
                                if (b > t1) {
                                    b = subtime2.getInt(a - 1);
                                    break;
                                }
                                a++;
                            }
                            if (b == 0) {
                                finalloutcity.setDay(nowDay);
                                finalloutcity.setFare(foutcities.get(i).getFare());
                                finalloutcity.setStart(foutcities.get(i).getStart());
                                finalloutcity.setPathtype(foutcities.get(i).getPathtype());
                                finalloutcity.setTotalTime(foutcities.get(i).getTotaltime());
                                finalloutcity.setFirstpath(foutcities.get(i).getFirstpath());
                                finalloutcity.setSecondpath(foutcities.get(i).getSecondpath());
                                finalloutcity.setThirdpath(foutcities.get(i).getThirdpath());
                                finalloutcity.setSchedule("운행정보가 없습니다.");
                                finalloutcity.setName(Name);
                                finalloutcityDao.insert(finalloutcity);
                            } else {
                                int total = timecheck.getInt("totaltime")+array1.getInt("totalTime")+array3.getInt("totalTime");
                                finalloutcity.setDay(nowDay);
                                finalloutcity.setFare(foutcities.get(i).getFare());
                                finalloutcity.setStart(foutcities.get(i).getStart());
                                finalloutcity.setPathtype(foutcities.get(i).getPathtype());
                                finalloutcity.setTotalTime(total);
                                finalloutcity.setFirstpath(foutcities.get(i).getFirstpath());
                                finalloutcity.setSecondpath(foutcities.get(i).getSecondpath());
                                finalloutcity.setThirdpath(foutcities.get(i).getThirdpath());
                                finalloutcity.setSchedule("운행정보가 없습니다.");
                                finalloutcity.setName(Name);
                                finalloutcityDao.insert(finalloutcity);
                            }

                            break;
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

    }

    public class HttpFunc extends AsyncTask<Object, Object, String> {
        Double sx, sy;

        public HttpFunc(Double sx, Double sy) {
            this.sx = sx;
            this.sy = sy;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(Object... objects) {
            onHTTPConnection urlconn = new onHTTPConnection();
//            String GET_result = urlconn.connectAndGet(sx, sy);
            String GET_result2 = urlconn.outGet(sx, sy);
            return GET_result2;
        }

        protected void onProgressUpdate(Object... objects) {
            textView.setText("ㅎㅇ");
        }

        protected void onPostExecute(String a) {
            textView.setText(a.toString());
        }

        protected void onCancelled() {
            super.onCancelled();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FoutcityDatabase outdatabase = Room.databaseBuilder(getApplicationContext(), FoutcityDatabase.class, "foutcity2.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        FincityDatabase indatabase = Room.databaseBuilder(getApplicationContext(), FincityDatabase.class, "fincity.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        DayTimeDatabase database = Room.databaseBuilder(getApplicationContext(), DayTimeDatabase.class, "daytime.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        dayTimeDao = database.dayTimeDao();
        fincityDao = indatabase.fincityDao();
        fincityDao.clearAll();
        foutcityDao = outdatabase.foutcityDao();
        foutcityDao.clearAll();

        Double sx = 127.0729;
        Double sy = 37.5668;
        HttpFunc httpFunc = new HttpFunc(sx, sy);
        httpFunc.execute();
//

//        Log.d("MyTag",httpFunc.toString());
//        Log.i("week", Calendar.DAY_OF_WEEK+"");

        textView = (TextView) findViewById(R.id.textView);
    }
}