package uhk.kikm.navigationuhk.utils.scanners;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uhk.kikm.navigationuhk.dataLayer.BleScan;
import uhk.kikm.navigationuhk.utils.C;

/**
 * Trida pro BluetoothLE skenovani. Poskytuje metody startScan (zacne skenovani), stopScan(zastavi skenovani a vrati vysledky), getResults(vrati vysledky)
 * Created by Matej on 23.10.2015.
 */
public class BLEScanner {
    ArrayList<BleScan> scans = new ArrayList<>();
    ScanResultListener scanResultListener;
    Context context;
    BluetoothLeScanner btScanner;
    long starTime;
    ScanCallback btCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            handleResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {

            }
        }

        @Override
        public void onScanFailed(int errorCode) {
        }
    };


    public BLEScanner(Context context) {
        this.context = context;
    }

    /**
     * @param context            Context
     * @param scanResultListener nullable listener ktery bude informovan pri naskenovani nove polozky
     */
    public BLEScanner(Context context, ScanResultListener scanResultListener) {
        this.scanResultListener = scanResultListener;
        this.context = context;
    }

    /**
     * Pripravi BluetoothLeScanner pomoci ktereho budou hledany beacony.
     *
     * @return pripraveny Scanner nebo null pokud doslo k problemu (bt vypnute nebo nepritomne)
     */
    private BluetoothLeScanner prepareBT() {
        BluetoothAdapter btAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (btAdapter != null && !btAdapter.isEnabled()) {
            btAdapter.enable(); //TODO zapinat bluetooth s uzivatelovou interakci

//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            context.startActivityForResult(enableBtIntent, 666); //TODO ocekovat spravne zapnuti a pritomnost BT
//            return null;
        }
        return btAdapter.getBluetoothLeScanner();
    }

    private void handleResult(ScanResult scan) {
        BleScan bleScan = new BleScan();
        bleScan.setAddress(scan.getDevice().getAddress());
        bleScan.setRssi(scan.getRssi());
        bleScan.setScanRecord(scan.getScanRecord().getBytes());
        bleScan.setTime((scan.getTimestampNanos() / 1000000) - starTime);
        scans.add(bleScan);
        Log.d(C.LOG_BLESCAN, scan.toString());
        if (scanResultListener != null) {
            scanResultListener.onScanResult(scans.size());
        }
    }

    public void startScan() {
        starTime = SystemClock.uptimeMillis();
        scans.clear();
        if (btScanner == null) {
            btScanner = prepareBT();
        }
        btScanner.stopScan(btCallBack);
        btScanner.startScan(btCallBack);
    }

    public List<BleScan> stopScan() {
        if (btScanner != null) {
            btScanner.stopScan(btCallBack); //TODO sometimes throws illegalStateException: Bt Adapter is not turned ON
        }
        return scans;
    }


    public ArrayList<BleScan> getResults() {
        return scans;
    }
}
