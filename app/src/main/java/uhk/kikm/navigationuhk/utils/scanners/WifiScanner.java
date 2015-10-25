package uhk.kikm.navigationuhk.utils.scanners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uhk.kikm.navigationuhk.dataLayer.WifiScan;
import uhk.kikm.navigationuhk.utils.C;

/**
 * Trida reprezentujici skenovani Wifi
 * Dominik Matoulek
 */
public class WifiScanner {
    int calls = 0, returns = 0;
    Context context;
    long startTime;
    List<ScanResult> scans = new ArrayList<>();
    /**
     * Jestli se ma po obdrzeni vysledku hned zacit skenovat znovu.
     */
    boolean scanAgain = false;
    ScanResultListener scanResultListener;
    BroadcastReceiver broadcastReceiver;

    /**
     * Inicializuje WifiScanner
     *
     * @param context context
     */
    public WifiScanner(Context context) {
        this.context = context;
    }

    public WifiScanner(Context context, ScanResultListener scanResultListener) {
        this.context = context;
        this.scanResultListener = scanResultListener;
    }

    /**
     * Nalezne vsechny wifi site v okoli telefonu, ktere jsou po (asynchronnim) obdrzeni vysledku dostupne pomoci metody getScanresults()
     *
     * @param continuous Jestli ma byt skenovani hned po obdrzeni vysledku spusteno znovu (dokud se nezavola stopScan()) a vsechny vysledky kumulativne pridavany do kolekce
     */
    public void startScan(boolean continuous) {
        startTime = SystemClock.uptimeMillis();
        scanAgain = continuous;
        scans.clear();

        final WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                returns++;
                Log.d(C.LOG_WIFISCAN, "Number of wifi networks scanned: " + wm.getScanResults().size() + ", " + (scanAgain ? "Scanning again" : "Finished scanning"));
                scans.addAll(wm.getScanResults());
                if (scanResultListener != null) {
                    scanResultListener.onScanResult(scans.size());
                }
                if (scanAgain) {
                    wm.startScan();
                    calls++;
                }
            }
        };

        if (!wm.isWifiEnabled()) {
            wm.setWifiEnabled(true);
        }

        context.registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wm.startScan();
        calls++;

    }

    public List<ScanResult> stopScan() {
        Log.d(C.LOG_WIFISCAN, "calls: " + calls + ", returns: " + returns);
        scanAgain = false;
        context.unregisterReceiver(broadcastReceiver);
        return scans;
    }

    public List<ScanResult> getScans() {
        return scans;
    }
    public ArrayList<WifiScan> getScanResultsInCustomClass() {
        ArrayList<WifiScan> customScans = new ArrayList<>();
        for (ScanResult scan : scans){
            WifiScan bleScan = new WifiScan(scan.SSID, scan.BSSID, scan.level);
            bleScan.setTime((scan.timestamp/1000)-(startTime));
            customScans.add(bleScan);
        }
        return customScans;
    }
}
