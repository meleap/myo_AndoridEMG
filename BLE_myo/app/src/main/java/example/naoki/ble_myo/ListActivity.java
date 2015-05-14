package example.naoki.ble_myo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListActivity extends ActionBarActivity {
    public static final int MENU_SCAN = 0;
    public static final int LIST_DEVICE_MAX = 5;

    public static String TAG = "BluetoothList";

    /** Device Scanning Time (ms) */
    private static final long SCAN_PERIOD = 5000;

    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt    mBluetoothGatt;
    private BluetoothLeScanner mBluetoothScanner;
    private ScanMyoListCallback mCallback;
    private ArrayList<String> deviceNames = new ArrayList<>();
    private String myoName = null;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mCallback = new ScanMyoListCallback();

        mHandler = new Handler();

        ListView lv = (ListView) findViewById(R.id.listView1);



        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, deviceNames);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), item + " connect", Toast.LENGTH_SHORT).show();
                myoName = item;

                Intent intent;
                intent = new Intent(getApplicationContext(), MainActivity.class);

                intent.putExtra(TAG, myoName);

                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_list, menu);
        menu.add(0, MENU_SCAN, 0, "Scan");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == MENU_SCAN) {
            scanDevice();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickScan(View v) {
        scanDevice();
    }

    public void scanDevice() {
        deviceNames.clear();
        // Scanning Time out by Handler.
        // The device scanning needs high energy.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothScanner.stopScan(mCallback);

                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Stop Device Scan", Toast.LENGTH_SHORT).show();

            }
        }, SCAN_PERIOD);
        mBluetoothScanner.startScan(mCallback);
    }

    protected class ScanMyoListCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            ParcelUuid[] uuids = device.getUuids();
            String uuid = "";
            if (uuids != null) {
                for (ParcelUuid puuid : uuids) {
                    uuid += puuid.toString() + " ";
                }
            }

            String msg = "name=" + device.getName() + ", bondStatus="
                    + device.getBondState() + ", address="
                    + device.getAddress() + ", type" + device.getType()
                    + ", uuids=" + uuid;
            Log.d("BLEActivity", msg);

            if (device.getName() != null && !deviceNames.contains(device.getName())) {
                deviceNames.add(device.getName());
            }
        }
    }
}
