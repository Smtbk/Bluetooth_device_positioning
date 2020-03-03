package com.example.test10;

        import androidx.appcompat.app.AppCompatActivity;
        import android.bluetooth.*;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.pm.PackageManager;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.ProgressBar;
        import android.widget.Switch;
        import android.widget.TextView;
        import android.widget.Toast;
        import java.util.ArrayList;
        import java.util.List;



public class MainActivity extends AppCompatActivity {


    private Button but1;
    TextView message1;
    private BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
    ListView list;
    private ImageView on;
    private ImageView off;
    Switch BTswitch;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CODE_LOC = 1;
    ArrayList<String> mList;
    ArrayAdapter<String> mAdapter;
    String white;
    String green;
    String net_mess = "devices not found";
    Short RSSI = Short.MIN_VALUE;
    ProgressBar power;
    private String hostIp = "192.168.43.14";
    private Integer hostPort = 4321;
    private SettingActivity SetAc = new SettingActivity();
    int fd = 0;
    int clicking = 0;
    Button settingButton;



    BroadcastReceiver receiver1 = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())){
                message1.setText("");
                message1.setVisibility(View.VISIBLE);
                but1.setText("Stop");
                power.setVisibility(View.VISIBLE);
                mList.clear();
                mAdapter.notifyDataSetChanged();
                RSSI = Short.MIN_VALUE;
                net_mess = "devices not found";
                clicking = 0;
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                but1.setText("Start");
                power.setVisibility(View.INVISIBLE);

                new Thread(new CClient(net_mess, hostIp, hostPort)).start();

                if (clicking == 0)BTAdapter.startDiscovery();
            }

            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                if (device.getAddress().equals(white)){
                    mList.add("______WHITE______" + "\nMAC: " + device.getAddress() + "\nRSSI: "
                            + intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
                } else if (device.getAddress().equals(green)) {
                    mList.add("______GREEN______" + "\nMAC: " + device.getAddress() + "\nRSSI: "
                            + intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
                } else {
                    mList.add(device.getName() + "\nMAC: " + device.getAddress() + "\nRSSI: "
                            + intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
                }

                mAdapter.notifyDataSetChanged();

                if (device.getAddress().equals(white)){
                    fd++;
                    if (RSSI < intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)) {
                        message1.setText("Вы рядом с белым маячком!");
                        RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    }
                    if (fd == 1) net_mess = device.getAddress()+ "   " + RSSI + "; ";
                    else net_mess = net_mess + device.getAddress()+ "   " + RSSI + "; ";
                }

                if (device.getAddress().equals(green)){
                    fd++;
                    if (RSSI < intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)) {
                        message1.setText("Вы рядом с зеленым маячком!");
                        RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    }
                    if (fd == 1) net_mess = device.getAddress()+ "   " + RSSI + "; ";
                    else net_mess = net_mess + device.getAddress()+ "   " + RSSI + "; ";
                }

                if (fd >= 2) {
                    BTAdapter.cancelDiscovery();
                    fd = 0;
                }
            }

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){
                message1.setText("");
                message1.setVisibility(View.INVISIBLE);
                RSSI = Short.MIN_VALUE;
                mList.clear();
                mAdapter.notifyDataSetChanged();
                if (!BTAdapter.isEnabled()){
                    off.setVisibility(View.VISIBLE);
                    on.setVisibility(View.INVISIBLE);
                    BTswitch.setChecked(false);
                    BTAdapter.cancelDiscovery();
                } else {
                    on.setVisibility(View.VISIBLE);
                    off.setVisibility(View.INVISIBLE);
                    BTswitch.setChecked(true);
                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        on = findViewById(R.id.BluetoothSymbolOn);
        off = findViewById(R.id.BluetoothSymbolOff);
        but1 = findViewById(R.id.button1);
        list = findViewById(R.id.List1);
        BTswitch = findViewById(R.id.switch1);
        message1 = findViewById(R.id.textView2);
        mList = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list.setAdapter(mAdapter);
        white = "FC:58:FA:56:70:D2";
        green = "FC:58:FA:B4:4E:31";
        power = findViewById(R.id.progressBar);
        settingButton = findViewById(R.id.settingButton);
    }



    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter Found = new IntentFilter();
        Found.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        Found.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        Found.addAction(BluetoothDevice.ACTION_FOUND);
        Found.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver1, Found);

        power.setVisibility(View.INVISIBLE);

        if (BTAdapter != null){
            if(BTAdapter.isEnabled()){
                on.setVisibility(View.VISIBLE);
                off.setVisibility(View.INVISIBLE);
                BTswitch.setChecked(true);
                BTAdapter.cancelDiscovery();
            } else {
                off.setVisibility(View.VISIBLE);
                on.setVisibility(View.INVISIBLE);
                BTswitch.setChecked(false);
            }
        } else {
            off.setVisibility(View.VISIBLE);
            on.setVisibility(View.INVISIBLE);
            BTswitch.setChecked(false);
            BTswitch.setClickable(false);
        }

        if( SetAc.hostIP != "KEK" )
        {
            hostIp = SetAc.hostIP;
            hostPort = Integer.parseInt(SetAc.hostPORT);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();

    }



    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver1);
        message1.setText("");
        message1.setVisibility(View.INVISIBLE);
        mList.clear();
        mAdapter.notifyDataSetChanged();
        if (BTAdapter.isDiscovering()){
            but1.setText("Stop");
            BTAdapter.cancelDiscovery();
            power.setVisibility(View.INVISIBLE);
        }
    }



    public void But_click(View view) {
        clicking++;
        if (BTAdapter!=null){
            if (BTAdapter.isEnabled()){
                if (!BTAdapter.isDiscovering()){
                    accessLocationPermission();
                    BTAdapter.startDiscovery();
                } else {
                    BTAdapter.cancelDiscovery();
                }
            }
        }
    }



    public void Switch_click(View view) {
        if (BTAdapter!=null){
            if (BTAdapter.isEnabled()){
                if (!BTswitch.isChecked()){
                    BTAdapter.cancelDiscovery();
                    BTAdapter.disable();
                }
            } else {
                if (BTswitch.isChecked()){
                    BTswitch.setChecked(false);
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        } else {
            BTswitch.setChecked(false);
            BTswitch.setClickable(false);
        }
    }



    public void Set_click(View view) {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }



    /**
     * Запрос на разрешение данных о местоположении (для Marshmallow 6.0)
     */
    private void accessLocationPermission() {
        int accessCoarseLocation = this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineLocation   = this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listRequestPermission = new ArrayList<String>();

        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listRequestPermission.isEmpty()) {
            String[] strRequestPermission = listRequestPermission.toArray(new String[listRequestPermission.size()]);
            this.requestPermissions(strRequestPermission, REQUEST_CODE_LOC);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOC:

                if (grantResults.length > 0) {
                    for (int gr : grantResults) {
                        // Check if request is granted or not
                        if (gr != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    //TODO - Add your code here to start Discovery
                }
                break;
            default:
                return;
        }
    }
}
