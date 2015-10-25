package uhk.kikm.navigationuhk.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import uhk.kikm.navigationuhk.R;
import uhk.kikm.navigationuhk.dataLayer.CouchDBManager;
import uhk.kikm.navigationuhk.dataLayer.Fingerprint;
import uhk.kikm.navigationuhk.utils.C;
import uhk.kikm.navigationuhk.utils.DeviceInformation;
import uhk.kikm.navigationuhk.utils.LocalizationService.LocalizationService;
import uhk.kikm.navigationuhk.utils.scanners.SensorScanner;
import uhk.kikm.navigationuhk.utils.WebViewInterface;
import uhk.kikm.navigationuhk.utils.scanners.WifiScanner;
import uhk.kikm.navigationuhk.utils.scanners.BLEScanner;
import uhk.kikm.navigationuhk.utils.scanners.ScanResultListener;

/**
 * Activita určená na tvorbu fingerprintů a příležitostné hlednání
 * <p/>
 * Dominik Matoulek 2015
 */

public class CollectorActivity extends ActionBarActivity {

    WebViewInterface webInterface;
    CouchDBManager dbManager;
    WebView view;
    SensorScanner sensorScanner;
    DeviceInformation deviceInformation;
    String selectedLevel = "J3NP";
    boolean supportsBLE;

    BLEScanner bleScannerMy;
    int bleCount = 0;

    WifiScanner wifiScanner;
    int wifiCount = 0;
    ProgressDialog progressDialog;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportsBLE = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        setContentView(R.layout.activity_collector);

        webInterface = new WebViewInterface(this);

        view = (WebView) findViewById(R.id.WebView);
        if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        view.getSettings().setJavaScriptEnabled(true); // povoleni JS
        view.addJavascriptInterface(webInterface, "android"); // nastaveni JS interface
        view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        findViewById(R.id.write_point).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScanning();
            }
        });


        deviceInformation = new DeviceInformation(this); // inicializace DeviceInfromation - ziskava informace o telefonu

        dbManager = new CouchDBManager(this); // inicializace DB managera
        Log.d(getClass().getName(), "Open db connection");

        changeLevel(selectedLevel);
    }

    private void startScanning() {
        wifiCount = 0;
        bleCount = 0;
        findViewById(R.id.write_point).setClickable(false);
        bleScannerMy = new BLEScanner(this, new ScanResultListener() {
            @Override
            public void onScanResult(int cumulativeCount) {
                updateProgressDialog(bleCount = cumulativeCount, wifiCount);
            }
        });
        if (supportsBLE) {
            bleScannerMy.startScan();
        }
        wifiScanner = new WifiScanner(this, new ScanResultListener() {
            @Override
            public void onScanResult(int cumulativeCount) {
                updateProgressDialog(bleCount, wifiCount = cumulativeCount);
            }
        });
        wifiScanner.startScan(true);
        sensorScanner = new SensorScanner(this); //inicializace SensorScanneru - Snima polohu zarizeni v prostoru
        progressDialog = showProgressDialog();


        Thread waiter = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(C.SCAN_TIME);
                } catch (InterruptedException ignored) {
                } finally {
                    CollectorActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopScanning();
                        }
                    });
                }
            }
        });
        waiter.start();

    }

    private void stopScanning() {
        bleScannerMy.stopScan();
        wifiScanner.stopScan();
        progressDialog.dismiss();
        findViewById(R.id.write_point).setClickable(true);
        writePoint();

    }

    private ProgressDialog showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.collector_dialog_title);
        progressDialog.show();
        progressDialog.setCancelable(false);
        updateProgressDialog(0, 0);
        return progressDialog;
    }

    private void updateProgressDialog(int bleCount, int wifiCount) {
        if (progressDialog != null) {
            progressDialog.setMessage("BluetoothLE: " + bleCount + "\nWifi: " + wifiCount);
        }
    }

    public void writePoint() {
//        if (webInterface.isChanged()) // pokud se ziskane souradnice u webinterface zmenily, muzeme to zaznamenat
        {
            Toast.makeText(this, webInterface.getX() + " " + webInterface.getY(), Toast.LENGTH_LONG).show();
            webInterface.setChanged(false);

            Fingerprint p = new Fingerprint(webInterface.getX(), webInterface.getY());
            p.setWifiScans(wifiScanner.getScanResultsInCustomClass());
            p.setLevel(selectedLevel); // nastavime patro
            sensorScanner.fillPosition(p);  // naplnime daty ze senzoru
            deviceInformation.fillPosition(p); // naplnime infomacemi o zarizeni
            new LocalizationService(C.pointA, C.pointB, C.pointC).getPoint(p); // nastavujeme souradnicovy system pro vypocet GPS souradnic a naplnime vypocitanymi souradnicemi
//            localizationService.getPoint(p); // naplnime vypocitanymi GPS souradnicemi
            p.setBleScans(bleScannerMy.getResults()); // naplnime daty z Bluetooth TODO naplneni fingerprintu
            dbManager.savePosition(p); // Ulozime pozici v DB
//        } else {
//            Toast.makeText(this, R.string.collector_coordinates_change, Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) { // nastaveni
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_about) // Seznam fingerprintů
        {
            Intent intent = new Intent(this, ListPositionsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_find) // Wifi hledani
        {
            findPosition();
        } else if (id == R.id.action_level_1) { // 1. patro
            changeLevel("J1NP");
        } else if (id == R.id.action_level_2) { // 2. patro
            changeLevel("J2NP");
        } else if (id == R.id.action_level_3) { // 3. patro
            changeLevel("J3NP");
        } else if (id == R.id.action_level_4) { // 4. patro
            changeLevel("J4NP");
        } else if (id == R.id.action_find_ble) { // BLE hledani
            findPositionByBle();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Reloaduje obrazek patra
     *
     * @param level cislo patra
     */
    private void changeLevel(String level) {
        try {
            view.loadUrl("http://beacon.uhk.cz/fimnav-webview/?map=" + URLEncoder.encode(level, "UTF-8"));  // nacteni souboru do prohlizece
            Toast.makeText(this, level, Toast.LENGTH_SHORT).show();
            selectedLevel = level;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda na nacteni textu z nejakeho souboru.
     *
     * @param resourceID ID zdroje
     * @return String text
     */
    private String readTextFromResource(int resourceID) { //TODO presunout metodu do externi tridy
        InputStream raw = getResources().openRawResource(resourceID);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int i;
        try {
            i = raw.read();
            while (i != -1) {
                stream.write(i);
                i = raw.read();
            }
            raw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toString();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        dbManager = new CouchDBManager(this);
        Log.d(getClass().getName(), "Open db connection");
    }

    @Override
    protected void onStop() {
        super.onStop();

        dbManager.closeConnection();
        Log.d(getClass().getName(), "Close db connection");
//        stopScanning();
        //TODO zastavit vsechna skenovani pri odchodu z aktivity
    }

    /**
     * Hledani pozice z ulozenych fingerprintu na zaklade wifi dat
     */
    private void findPosition() {
//        wScanner.startScan(false);
//        List<ScanResult> scanResults = wScanner.getScans(); // Ziskej nalezene Wifi site TODO tohle nemůže fungovat neb výsledky jsou vraceny asynchronně
//        ArrayList<Fingerprint> fingerprints = new ArrayList<>();
//
//        for (ScanResult s : scanResults) // ziskani dat z db
//        {
//            String[] mac = new String[]{s.BSSID};
//            List<Fingerprint> pos = dbManager.getFingerprintsByMacs(mac);
//            fingerprints.addAll(pos);
//        }
//
//        if (fingerprints.size() > 0) { // pokud je jich vic jak 0, muzeme je predat na prohledani
//            WifiFinder finder = new WifiFinder(fingerprints);
//            Fingerprint possibleFingerprint = finder.computePossibleFingerprint(scanResults);
//
//            // zobrazime na mape
//            showPoint(possibleFingerprint.getX(), possibleFingerprint.getY(), possibleFingerprint.getLevel());
//        } else {
//            Toast.makeText(this, "Nedostatek Wifi dat", Toast.LENGTH_SHORT).show();
//        }


    }

    /**
     * Zobrazi bod na mape o urcite barve, ktera je vyhodnocena z patra porizeni
     *
     * @param x     x bodu
     * @param y     y bodu
     * @param level cislo patra
     */
    private void showPoint(int x, int y, String level) {
        view.loadUrl("javascript:setPoint(" + String.valueOf(x) + ", " + String.valueOf(y) + ", \"red\"" + ")");
    }

    /**
     * Hledani pozice z ulozenych fingerprintu na zaklade BLE dat
     */
    private void findPositionByBle() {
//        List<BleScan> bleScans = bleScanner.getBleDeviceList(); // ziskani nalezenych ble vysilacu
//
//        ArrayList<Fingerprint> bleFingerprints = new ArrayList<>();
//
//        for (BleScan s : bleScans)// ziskani dat z db
//        {
//            String[] address = new String[]{s.getAddress()};
//            List<Fingerprint> pos = dbManager.getPositionsByBleAddresses(address);
//
//            bleFingerprints.addAll(pos);
//        }
//
//        if (bleFingerprints.size() > 0) // Pokud jsou nejake fingerprinty nalezene, muzeme prohledavat
//        {
//            BluetoothFinder bleFinder = new BluetoothFinder(bleFingerprints);
//            Fingerprint possibleBleFingerprint = bleFinder.computePossiblePosition(bleScans);
//
//            // zobrazime na mape
//            view.loadUrl("javascript:setBlePoint(" + String.valueOf(possibleBleFingerprint.getX()) + ", " + String.valueOf(possibleBleFingerprint.getY()) + ", \"purple\"" + ")");
//        } else {
//            Toast.makeText(this, "Nedostatek Bluetooth dat", Toast.LENGTH_SHORT).show();
//        }


    }
}
