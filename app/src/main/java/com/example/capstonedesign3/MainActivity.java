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
                    Log.i("jio",jio.toString());
                    return "검색결과 없음";
                }
                String results;
                try {
                    JSONObject jsonObj = new JSONObject(result).getJSONObject("result");
                    String resultst = jsonObj.toString();
                    int t = (int) jsonObj.get("searchType");
                    if (t == 1) {
                        Log.i("jio","ㅎㅎ");
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
                                            busdata.put("startID",trafiic.get("startID"));
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
        public String InTime(){
            String apiKey = "QEX7ua4WgGErXl+jz2xtjmfPZ4rRn5TwjDIrOhpo5Ho";
            FincityDatabase indatabase = Room.databaseBuilder(getApplicationContext(), FincityDatabase.class, "fincity.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            DayTimeDatabase database = Room.databaseBuilder(getApplicationContext(),DayTimeDatabase.class,"daytime.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            FinallincityDatabase finallincityDatabase = Room.databaseBuilder(getApplicationContext(),FinallincityDatabase.class,"finallincity.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            String nowDay="";
            switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
                case 1 :
                    nowDay="일";
                    break;
                case 2:
                    nowDay="월";
                    break;
                case 3:
                    nowDay="화";
                    break;
                case 4:
                    nowDay="수";
                    break;
                case 5:
                    nowDay="목";
                    break;
                case 6:
                    nowDay="금";
                    break;
                case 7:
                    nowDay="토";
                    break;
            }
            List<DayTime> dayTimes = dayTimeDao.getDay(nowDay);
            for (int i = 0; i < dayTimes.size(); i++) {
                Log.i("Daytiem",dayTimes.get(i).getId()+"\n"+
                        dayTimes.get(i).getDay()+"\n"+
                        dayTimes.get(i).getTime());
            }
            finallincityDao = finallincityDatabase.finallincityDao();
            finallincityDao.delday(nowDay);
            fincityDao = indatabase.fincityDao();
            List<Fincity> fincities = fincityDao.getFincityAll();
            for(int i=0;i<fincities.size();i++){
                try{
                    JSONArray jsonObject = new JSONArray(fincities.get(i).getSubpath());
                    JSONObject object = (JSONObject) jsonObject.get(0);
                    Log.i("jsonObjj",jsonObject.toString());
                    Log.i("jsonObjj",object.getString("type"));
//                    subpath.put(fincities.get(i).getSubpath());
//                    data.put("id",fincities.get(i).getId());
//                    data.put("fare",fincities.get(i).getFare());
//                    data.put("pathType",fincities.get(i).getPathtype());
//                    data.put("start",fincities.get(i).getStart());
//                    data.put("subpath",subpath);
//                    incity.put(data);
                } catch (Exception e){
                    Log.i("Intime","error");
                    return"error";
                }
            }
//            Log.i("dgat",incity.toString());
            if(fincities.size()==0){
                return null;
            }
            try{
                for (int i = 0; i < fincities.size(); i++) {
                    Thread.sleep(200);
                    JSONArray jsonObject = new JSONArray(fincities.get(i).getSubpath());
                    JSONObject jsonObject1 = (JSONObject) jsonObject.get(1);
                    Log.i("tttest",jsonObject1.toString());
                    Log.i("tttest",jsonObject1.getString("type"));
                    switch (jsonObject1.getString("type")){
                        case "버스":
                            String result;
                            StringBuffer st = new StringBuffer();
                            URL url = new URL ("https://api.odsay.com/v1/api/busLaneDetail?lang=0&busID="+jsonObject1.getString("busID")+"&apiKey="+URLEncoder.encode(apiKey,"UTF-8"));
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
                            Log.i("jjoio",jjo.toString());
                            Log.i("jjoio",jjo.getString("busFirstTime"));
                            String firsttime = jjo.getString("busFirstTime");
                            Log.i("firsttime",firsttime);
                            String[] q = new String[2];
                            q = firsttime.split(":");
                            int interval=0;
                            switch (nowDay){
                                case "토":
                                    interval = jjo.getInt("bus_Interval_Sat");
                                    break;
                                case "일":
                                    interval =jjo.getInt("bus_Interval_Sun");
                                    break;
                                default:
                                    interval = jjo.getInt("bus_Interval_Week");
                                    break;
                            }
                            JSONArray jar = (JSONArray) jjo.get("station");
                            for(int k =0; k<jar.length();k++){
                                JSONObject job =(JSONObject)jar.getJSONObject(k);

                                if(jsonObject1.getInt("startID")==job.getInt("stationID")){
                                    Log.i("heil","hello");
                                    int gettime = (int) Math.ceil((double) 1.5*job.getInt("idx"));
                                    int r = (int) (Integer.parseInt(q[0])*60) + (int) (Integer.parseInt(q[1])) + gettime;
                                    Log.i("rrrr",r+"");
                                    Log.i("tttt",fincities.get(i).getTotaltime()+"");
                                    int t = (int) dayTimes.get(0).getTime() - (int)fincities.get(i).getTotaltime() - 5;

                                    int hour = 0;
                                    int minute = 0;
                                    if(interval == 0 || r>t){
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
                                        while(r<t){
                                            r = r+interval;
                                            if(r>t){
                                                r = r - interval;
                                                break;
                                            }
                                        }
                                        hour = (int) Math.floor((double) r/60);
                                        minute = r%60;
                                        String hur = String.format("%2d",hour);
                                        String min = String.format("%2d",minute);

                                        finallincity.setDay(nowDay);
                                        finallincity.setFare(fincities.get(i).getFare());
                                        finallincity.setStart(fincities.get(i).getStart());
                                        finallincity.setPathtype(fincities.get(i).getPathtype());
                                        finallincity.setTotalTime(fincities.get(i).getTotaltime());
                                        finallincity.setSubpath(fincities.get(i).getSubpath());
                                        finallincity.setSchedule(hur+":"+min);
                                        finallincity.setName(jsonObject1.getString("name"));
                                        finallincityDao.insert(finallincity);
                                    }
                                }
                            }
                            Log.i("jarlenght",jar.length()+"");
                            Log.i("interval",interval+"");
                            break;
                        case "지하철" :
                            String results;
                            StringBuffer sts = new StringBuffer();
                            URL urls = new URL ("https://api.odsay.com/v1/api/subwayTimeTable?lang=0&stationID="+jsonObject1.getString("startID")+"&wayCode="+jsonObject1.getString("wayCode")+"&showExpressTime=1&apiKey="+URLEncoder.encode(apiKey,"UTF-8"));
                            HttpURLConnection conns = (HttpURLConnection) urls.openConnection();
                            conns.setRequestMethod("GET");
                            conns.setRequestProperty("Content-type", "application/json");
                            conns.setDoOutput(true);
                            BufferedReader brs = new BufferedReader(new InputStreamReader(conns.getInputStream()));
                            while ((results = brs.readLine()) != null) {
                                sts.append(results);
                            }
                            results = sts.toString();
                            String list="";
                            switch (nowDay){
                                case "토":
                                    list = "SatList";
                                    break;
                                case" 일":
                                    list ="SunList";
                                    break;
                                default:
                                    list="OrdList";
                                    break;
                            }
                            String way="";
                            switch (jsonObject1.getInt("wayCode")){
                                case 1:
                                    way="up";
                                    break;
                                case 2:
                                    way="down";
                                    break;
                            }
                            JSONObject jjjo = new JSONObject(results).getJSONObject("result");
                            JSONObject joo =(JSONObject) jjjo.getJSONObject(list);
                            JSONObject jooo = (JSONObject) joo.getJSONObject(way);
                            JSONArray array = (JSONArray) jooo.get("time");
                            Log.i("arraylen",array.length()+"");
                            JSONArray subtime2 = new JSONArray();
                            for(int m = 0;m<array.length();m++){
                                String[] abc = new String[100];
                                String[] subtime = new String[10];
                                JSONObject jn = (JSONObject) array.get(m);
                                Log.i("jnn",jn.toString());
                                String n = jn.getString("list");
                                int hou = jn.getInt("Idx");
                                Log.i("nnn",n);
                                subtime = n.split("\\s");
                                Log.i("subtimelen",subtime.length+"");
                                for(int s = 0; s<subtime.length;s++){

                                    abc = subtime[s].split("\\(");
                                }
                                for (int y = 0; y < abc.length;y++){
                                    if(y%2==0){
                                        int nk = Integer.parseInt(abc[y])+hou*60;
                                        subtime2.put(nk);
                                    }
                                }
                            }
                            JSONObject je = (JSONObject) jsonObject.get(0);
                            Log.i("jeje",je.toString());
                            int ti = dayTimes.get(0).getTime() - fincities.get(i).getTotaltime() - je.getInt("sectionTime");
                            int a = 1;
                            int b=0;
                            int hour2=0;
                            int minute2=0;

                            while (true){
                                b = subtime2.getInt(a);
                                if(b>ti){
                                    b = subtime2.getInt(a-1);
                                    break;
                                }
                                a++;
                            }
                            hour2 = (int) Math.floor((double) b/60);
                            minute2 = b%60;
                            String hur2 = String.format("%2d",hour2);
                            String min2 = String.format("%2d",minute2);
                            finallincity.setName(jsonObject1.getString("name"));
                            finallincity.setStart(fincities.get(i).getStart());
                            finallincity.setPathtype(fincities.get(i).getPathtype());
                            finallincity.setTotalTime(fincities.get(i).getTotaltime());
                            finallincity.setFare(fincities.get(i).getFare());
                            finallincity.setSubpath(fincities.get(i).getSubpath());
                            finallincity.setSchedule(hur2+":"+min2);
                            finallincity.setDay(nowDay);
                            finallincityDao.insert(finallincity);
                            Log.i("timearray",array.get(0).toString());
                    }
                    jsonObject1.getString("type");
                }
            } catch (Exception e){
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
                if(jio.has("error")){
                    Log.i("outGetjio",jio.toString());
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
                                            trafiic.get("endName").toString().equals("안성종합터미널") ||
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
                        int op =0;
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
                                    if(op>0){
                                        int payment = (int) jsonObj2.get("pay") + (int) jsonobj.get("payment");
                                        int totalTime = (int) jsonObj2.get("totalTime") + (int) jsonobj.get("totalTime");
                                        jsonObj2.put("pay", payment);
                                        jsonObj2.put("totalTime", totalTime);
                                        JSONObject abd = (JSONObject) inarray.get(0);
                                        jsonObj2.put("walktime", abd.get("sectionTime"));
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
                FoutcityDatabase outdatabase = Room.databaseBuilder(getApplicationContext(),FoutcityDatabase.class,"foutcity.db")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build();
                foutcityDao = outdatabase.foutcityDao();

                for (int l = 0; l < jsonArray.length(); l++) {
                    String t2 = t.toString();
                    JSONArray jsonArray1 = new JSONArray(t2);
                    Thread.sleep(400);
                    JSONObject jsonObj = (JSONObject) jsonArray.get(l);
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
                                        foutcityDao.insert(foutcity);
                                    } else {
                                        int payment = (int) jsonObj.getInt("pay") + (int) jsonobj.getInt("payment");
                                        int totalTime = (int) jsonObj.getInt("totalTime") + (int) jsonobj.getInt("totalTime");
                                        jsonObj.put("pay", payment);
                                        JSONObject abd = (JSONObject) inarray.get(0);
                                        jsonObj.put("totalTime", totalTime);
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
            final int chunkSize = 2048;
            for (int i = 0; i < outcity.toString().length(); i += chunkSize) {
                Log.d("outcity4", outcity.toString().substring(i, Math.min(outcity.toString().length(), i + chunkSize)));
            }
            return outcity.toString();
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
            String GET_result = urlconn.connectAndGet(sx, sy);
//            String GET_result2 = urlconn.outGet(sx, sy);
            return GET_result;
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
        FoutcityDatabase outdatabase = Room.databaseBuilder(getApplicationContext(), FoutcityDatabase.class, "foutcity.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        FincityDatabase indatabase = Room.databaseBuilder(getApplicationContext(), FincityDatabase.class, "fincity.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        DayTimeDatabase database = Room.databaseBuilder(getApplicationContext(),DayTimeDatabase.class,"daytime.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        dayTimeDao = database.dayTimeDao();
        fincityDao = indatabase.fincityDao();
        fincityDao.clearAll();
        foutcityDao = outdatabase.foutcityDao();
        foutcityDao.clearAll();
        List<Foutcity> foutcities = foutcityDao.getFoutctiyAll();
        for (int i = 0; i < foutcities.size(); i++) {
            Log.d("foutcity",foutcities.get(i).getId()+"\n"+
                    foutcities.get(i).getPathtype()+"\n"+
                    foutcities.get(i).getFare());
        }
        List<DayTime> dayTimes = dayTimeDao.getDayTimeAll();
        for (int i = 0; i < dayTimes.size(); i++) {
//                if(dayTimes.get(i).getId()==nowDay){
//                    Log.i("Daytiem",dayTimes.get(i).getId()+"\n"+
//                            dayTimes.get(i).getDay()+"\n"+
//                            dayTimes.get(i).getTime());
//                }
            Log.i("Daytie",dayTimes.get(i).getId()+"\n"+
                    dayTimes.get(i).getDay()+"\n"+
                    dayTimes.get(i).getTime());
        }
        Double sx = 127.0929;
        Double sy = 36.9927;
        HttpFunc httpFunc = new HttpFunc(sx, sy);
        httpFunc.execute();
//        InTime inTime = new InTime();
//        inTime.getTime();

//        Log.d("MyTag",httpFunc.toString());
//        Log.i("week", Calendar.DAY_OF_WEEK+"");

        textView = (TextView) findViewById(R.id.textView);
    }
}