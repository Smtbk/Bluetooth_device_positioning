package com.example.test10;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Fone extends Service {

    int num = 200;
    int[] RSSI_mass = new int[num];
    Short RSSI = Short.MIN_VALUE;
    String PC = "74:40:BB:D1:72:20";
    int st = 0;
    final String FILENAME_SD = "fileSD.txt";
    final Handler h = new Handler();

    BroadcastReceiver receiver1 = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())){
                RSSI = Short.MIN_VALUE;
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                if (st < num) {
                    MainActivity.BTAdapter.startDiscovery();
                } else {
                    writeFileSD();
                }
            }

            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                if (device.getAddress().equals(PC)){
                    RSSI_mass[st] = RSSI;

                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), st + ". RSSI = " + RSSI, Toast.LENGTH_SHORT).show();
                        }
                    });

                    st++;
                    MainActivity.BTAdapter.cancelDiscovery();
                }
            }

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){
                RSSI = Short.MIN_VALUE;
                if (!MainActivity.BTAdapter.isEnabled()){
                    MainActivity.BTAdapter.cancelDiscovery();
                }
            }
        }
    };

    public Fone() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"Created",Toast.LENGTH_SHORT).show();

        IntentFilter Found = new IntentFilter();
        Found.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        Found.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        Found.addAction(BluetoothDevice.ACTION_FOUND);
        Found.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver1, Found);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Work...", Toast.LENGTH_SHORT).show();
                    }
                });
                Thread thr = Thread.currentThread();
                if (!MainActivity.BTAdapter.isDiscovering()) {
                    MainActivity.BTAdapter.startDiscovery();
                }
                /*try {
                    thr.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Potok stopped", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (MainActivity.BTAdapter.isDiscovering()) {
            MainActivity.BTAdapter.cancelDiscovery();
        }
        unregisterReceiver(receiver1);
        super.onDestroy();
        Toast.makeText(this,"Destoyed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void writeFileSD() {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(getExternalFilesDir(null), FILENAME_SD);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            st = 0;
            while(st < num) {
                bw.write(st + ". RSSI = " + RSSI_mass[st] + "\n");
                st++;
            }
            st++;
            // закрываем поток
            bw.close();
            if (st == num + 1) {
                stopSelf();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
