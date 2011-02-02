package com.ladaube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class HttpClient {

    private static final String TAG = "HttpClient";

    DefaultHttpClient httpclient = new DefaultHttpClient();

    private final String ladaubeUrl;

    public HttpClient(String ladaubeUrl) {
        this.ladaubeUrl = ladaubeUrl;
    }

    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    private String sendHttpGetAndGetString(String url) throws IOException {

        HttpGet httpRequest = new HttpGet(ladaubeUrl+url);

        // Set HTTP parameters
//            httpRequest.setHeader("Accept", "application/json");
        httpRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

        long t = System.currentTimeMillis();
        HttpResponse response = httpclient.execute(httpRequest);
        Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis() - t) + "ms]");

        // grab last session id
        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
        for (Cookie c : cookies) {
            if (c.getName().toLowerCase().equals("jsessionid")) {
                sessionId = c.getValue();
            }
        }

        // Get hold of the response entity (-> the data):
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            // Read the content stream
            InputStream instream = entity.getContent();
            Header contentEncoding = response.getFirstHeader("Content-Encoding");
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                instream = new GZIPInputStream(instream);
            }

            // convert content stream to a String
            String resultString = convertStreamToString(instream);
            instream.close();
            return resultString;
        }

        return null;
    }

    public JSONArray jsonGetArray(String url) throws JSONException, IOException {
        String resultString = sendHttpGetAndGetString(url);
        if (resultString==null) {
            return null;
        }

        // Transform the String into a JSONObject
        JSONArray jsonObjRecv = new JSONArray(resultString);
        // Raw DEBUG output of our received JSON object:
        Log.i(TAG, "<jsonobject>\n" + jsonObjRecv.toString() + "\n</jsonobject>");

        return jsonObjRecv;
    }

    public JSONObject jsonGet(String url) throws JSONException, IOException {
        String resultString = sendHttpGetAndGetString(url);
        if (resultString==null) {
            return null;
        }

        // Transform the String into a JSONObject
        JSONObject jsonObjRecv = new JSONObject(resultString);
        // Raw DEBUG output of our received JSON object:
        Log.i(TAG, "<jsonobject>\n" + jsonObjRecv.toString() + "\n</jsonobject>");

        return jsonObjRecv;
    }


    private static String convertStreamToString(InputStream is) {
        /*
        * To convert the InputStream to String we use the BufferedReader.readLine()
        * method. We iterate until the BufferedReader return null which means
        * there's no more data to read. Each line will appended to a StringBuilder
        * and returned as String.
        *
        * (c) public domain: http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/11/a-simple-restful-client-at-android/
        */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
