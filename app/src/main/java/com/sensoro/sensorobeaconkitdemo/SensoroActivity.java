package com.sensoro.sensorobeaconkitdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sensoro.beacon.kit.BatterySaveInBackground;
import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.SensoroBeaconConnection;
import com.sensoro.beacon.kit.SensoroBeaconManager;

import java.util.ArrayList;


public class SensoroActivity extends Activity implements SensoroBeaconManager.BeaconManagerListener,SensoroBeaconConnection.BeaconConnectionCallback{
    private Context context;
    private ListView listView;
    private SensoroAdapter sensoroAdapter;
    private ArrayList<Beacon> beaconArrayList;
    private BatterySaveInBackground batterySaveInBackground;
    private SensoroBeaconManager sensoroBeaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensoro);

        init();
    }

    @Override
    protected void onDestroy() {
        if (sensoroBeaconManager != null) {
            /**
             * 停止 SBK 服务
             * stop SBK service
             */
            sensoroBeaconManager.stopService();
        }
        super.onDestroy();
    }

    private void init() {
        initView();
        initSensoroSDK();
    }

    private void initView() {
        context = this;
        beaconArrayList = new ArrayList<Beacon>();
        sensoroAdapter = new SensoroAdapter(this);
        listView = (ListView) findViewById(R.id.lv_beacons);
        listView.setAdapter(sensoroAdapter);
    }

    private void initSensoroSDK() {
        /**
         * 实例化 BatterySaveInBackground 对象，即可开启后台省电模式。
         * 当 app 切换到后台，使用后台扫描频率和扫描间隔；当 app 切换到前台，使用前台扫描频率和扫描间隔。
         * init BatterySaveInBackground object, than opened background saved battery mode.
         * if app switch to background, use background scan period and scan between period; if app switch to foreground, use foreground scan period and scan between period.
         */
        batterySaveInBackground = new BatterySaveInBackground(getApplication());
        /**
         * 获取单例模式 SBK 管理对象。
         * get single instance of SBK manager object
         */
        sensoroBeaconManager = SensoroBeaconManager.getInstance(context);
        /**
         * 设置SBK管理对象监听。
         * set listener of SBK manager object
         */
        sensoroBeaconManager.setBeaconManagerListener(this);
        /**
         * 设置前台扫描时间。默认 1100 毫秒。
         * set foreground scan period. default is 1100 ms.
         */
        sensoroBeaconManager.setForegroundScanPeriod(1000);
        /**
         * 设置前台扫描间隔。默认 0 毫秒。
         * set foreground between scan period. default is 0 ms.
         */
        sensoroBeaconManager.setForegroundBetweenScanPeriod(10);
        /**
         * 设置后台扫描时间。默认 10000 毫秒。
         * set background scan period. default is 10000 ms.
         */
        sensoroBeaconManager.setBackgroundScanPeriod(3000);
        /**
         * 设置后台扫描间隔。默认 300000 毫秒。
         * set foreground between scan period. default is 300000 ms.
         */
        sensoroBeaconManager.setBackgroundBetweenScanPeriod(5000);
        /**
         * 设置 beacon 有效期。默认 8000 毫秒。
         * set exist time of beacon. default is 8000 ms.
         */
        sensoroBeaconManager.setBeaconExistTime(5000);
        /**
         * 设置 beacon 定时更新间隔。默认 1000 毫秒。
         * set beacon update period. default is 1000 ms.
         */
        sensoroBeaconManager.setUpdateBeaconPeriod(1000);
        /**
         * 启动 SBK 服务
         * start SBK service
         */
        sensoroBeaconManager.startService();
        /**
         * 停止 SBK 服务
         * stop SBK service.
         */
//        sensoroBeaconManager.stopService();
    }

    /**
     * 发现一个新 beacon 回调
     * callback when a beacon appeared
     *
     * @param beacon
     */
    @Override
    public void onNewBeacon(final Beacon beacon) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!beaconArrayList.contains(beacon)) {
                    beaconArrayList.add(beacon);
                    sensoroAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 一个 beacon 消失时回调
     * callback when a beacon disappeared
     *
     * @param beacon
     */
    @Override
    public void onGoneBeacon(final Beacon beacon) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Beacon deleteBeacon = null;
                for (Beacon inBeacon : beaconArrayList) {
                    if (inBeacon.equals(beacon)) {
                        deleteBeacon = beacon;
                    }
                }
                if (deleteBeacon != null) {
                    beaconArrayList.remove(deleteBeacon);
                    sensoroAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 定时更新当前所在 beacon 列表
     * callback regularly update
     *
     * @param beacons
     */
    @Override
    public void onUpdateBeacon(final ArrayList<Beacon> beacons) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (beaconArrayList != null && beaconArrayList.size() != 0) {
                    for (Beacon beacon : beacons) {
                        if (beaconArrayList.contains(beacon)) {
                            beaconArrayList.set(beaconArrayList.indexOf(beacon), beacon);
                        }
                    }
                    sensoroAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onConnectedState(Beacon beacon, int i, int i2) {

    }

    @Override
    public void onSetNewPassword(Beacon beacon, int i) {

    }

    @Override
    public void onDisabledPassword(Beacon beacon, int i) {

    }

    @Override
    public void onCheckPassword(Beacon beacon, int i) {

    }

    @Override
    public void onSetBaseSetting(Beacon beacon, int i) {

    }

    @Override
    public void onSetSensorSetting(Beacon beacon, int i) {

    }

    @Override
    public void onSetMajoMinor(Beacon beacon, int i) {

    }

    @Override
    public void onSetProximityUUID(Beacon beacon, int i) {

    }

    @Override
    public void onResetToFactory(Beacon beacon, int i) {

    }

    @Override
    public void onResetAcceleratorCount(Beacon beacon, int i) {

    }

    @Override
    public void onUpdateSensorData(Beacon beacon, int i) {

    }

    @Override
    public void onTemperatureDataUpdate(Beacon beacon, int i) {

    }

    @Override
    public void onBrightnessLuxDataUpdate(Beacon beacon, double v) {

    }

    @Override
    public void onAcceleratorMovingUpdate(Beacon beacon, int i) {

    }

    @Override
    public void onAcceleratorCountUpdate(Beacon beacon, int i) {

    }

    class SensoroAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater = null;
        private ViewHolder holder = null;

        SensoroAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return beaconArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return beaconArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_beacons, parent, false);
                holder = new ViewHolder();
                holder.uuidTextView = (TextView) convertView.findViewById(R.id.item_tv_uuid);
                holder.majorTextView = (TextView) convertView.findViewById(R.id.item_tv_major);
                holder.minorTextView = (TextView) convertView.findViewById(R.id.item_tv_minor);
                holder.rssiTextView = (TextView) convertView.findViewById(R.id.item_tv_rssi);
                holder.modleTextView = (TextView) convertView.findViewById(R.id.item_tv_modle);
                holder.stateImageView = (ImageView) convertView.findViewById(R.id.item_iv_state);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.uuidTextView.setText(beaconArrayList.get(position).getProximityUuid());
            holder.majorTextView.setText(String.format("%04X",beaconArrayList.get(position).getMajor()));
            holder.minorTextView.setText(String.format("%04X", beaconArrayList.get(position).getMinor()));
            holder.modleTextView.setText(beaconArrayList.get(position).getHardwareModelName());
            int rssi = beaconArrayList.get(position).getRssi();
            holder.rssiTextView.setText(String.valueOf(rssi));
            if (rssi < -80){
                holder.stateImageView.setBackgroundResource(R.drawable.yellow);
            } else {
                holder.stateImageView.setBackgroundResource(R.drawable.green);
            }
            return convertView;
        }

        class ViewHolder {
            private TextView uuidTextView = null;
            private TextView majorTextView = null;
            private TextView minorTextView = null;
            private TextView rssiTextView = null;
            private TextView modleTextView = null;
            private ImageView stateImageView = null;
        }
    }
}
