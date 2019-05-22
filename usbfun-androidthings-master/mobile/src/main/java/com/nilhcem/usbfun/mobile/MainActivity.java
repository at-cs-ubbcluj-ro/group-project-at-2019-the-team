package com.nilhcem.usbfun.mobile;

import android.app.DownloadManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int USB_VENDOR_ID = 0x2341; // 9025
    private static final int USB_PRODUCT_ID = 0x0001;

    private String buffer = "";

    private OutputStream outputStream;
    private InputStream inStream;

    OkHttpHandler okHttpHandler;

    private int sensorValue=0;
    TextView mTxtView;
    private Button mBtn;

    private void init() throws IOException {
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
                int index=0;
                int myDevice = 0;
                for (BluetoothDevice bd: bondedDevices)
                {
                    Log.i("SHIT", "Paired device: " + bd.getName());
                    if (bd.getName().charAt(0) == 'I' && bd.getName().charAt(1) == 'm') myDevice = index;
                    index++;

                }

                if(bondedDevices.size() > 0) {
                    Log.i("SHIT","Trying to connect to target device.");
                    Object[] devices = (Object []) bondedDevices.toArray();
                    BluetoothDevice device = (BluetoothDevice) devices[myDevice];
                    ParcelUuid[] uuids = device.getUuids();
                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                    Class<?> clazz = socket.getRemoteDevice().getClass();
                    Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};

                    Method m = null;
                    try {
                        m = clazz.getMethod("createRfcommSocket", paramTypes);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    Object[] params = new Object[] {Integer.valueOf(1)};

                    BluetoothSocket fallbackSocket = null;
                    try {
                        fallbackSocket = (BluetoothSocket) m.invoke(socket.getRemoteDevice(), params);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    fallbackSocket.connect();
//                    socket.connect();
                    outputStream = socket.getOutputStream();
                    inStream = socket.getInputStream();
                    Log.i("SHIT", "Input stream:" + outputStream.toString());

                }

                Log.e("error", "No appropriate paired devices.");
            } else {
                Log.e("error", "Bluetooth is disabled.");
            }
        }
    }


    public void write(String s) throws IOException {
        outputStream.write(s.getBytes());
    }

    public void run() {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;
        int b = BUFFER_SIZE;

        while (true) {
            try {
                bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        okHttpHandler= new OkHttpHandler();
//        okHttpHandler.execute("http://192.168.56.1:2320/");

        mBtn = findViewById(R.id.button);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("executing call...");
                get3();
            }
        });

        mTxtView = (TextView)findViewById(R.id.textView);

//        try {
//            init();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void get3(){
        String url = "http://192.168.43.4:38176/";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                System.err.println("Failure from call!");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String myResponse = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTxtView.setText(myResponse);
                    }
                });
            }
        });
    }

    public void get2(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://192.168.56.1:2320/")
                .get()
                .build();

        try{
            Call call = client.newCall(request);

            Response response = call.execute();

//            System.out.println(response.body().toString());

        }catch(Exception e){
//            Log.e("MAIN", e.getMessage());
            e.printStackTrace();
        }
    }

    public void get(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("https://jsonplaceholder.typicode.com/todos/1");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
            }
            String result = buffer.toString();
            Log.e("MAIN", result);
            Log.e("MAIN", "Just printed the request result.");
        } catch (IOException e) {
            Log.e("Request", "Error ", e);
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Request", "Error closing stream", e);
                }
            }
        }
    }



}
