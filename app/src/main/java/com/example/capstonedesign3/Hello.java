package com.example.capstonedesign3;

import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Hello {
    public URL createURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            return null;
        }
        return url;
    }
    public String connectAndGet() {
        String apiKey = "QEX7ua4WgGErXl+jz2xtjmfPZ4rRn5TwjDIrOhpo5Ho";
        try {
            URL url = createURL("https://api.odsay.com/v1/api/searchBusLane?busNo=10&CID=1000&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result;
            while ((result = br.readLine())!=null){
                sb.append(result + '\n');
            }
            result = sb.toString();
            return result;
        } catch (Exception exception) {
            return null;
        }
    }
}
