package com.nilhcem.usbfun.mobile;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class OkHttpHandler extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] objects) {
        Request.Builder builder = new Request.Builder();
        builder.url("http://192.168.43.4:38176/");
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}