package uhk.kikm.navigationuhk.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import uhk.kikm.navigationuhk.R;
import uhk.kikm.navigationuhk.dataLayer.BleScan;
import uhk.kikm.navigationuhk.dataLayer.CellScan;
import uhk.kikm.navigationuhk.dataLayer.CouchDBManager;
import uhk.kikm.navigationuhk.dataLayer.Fingerprint;
import uhk.kikm.navigationuhk.dataLayer.WifiScan;
import uhk.kikm.navigationuhk.utils.C;
import uhk.kikm.navigationuhk.utils.finders.WifiFinder;
import uhk.kikm.navigationuhk.utils.scanners.ScanResultListener;
import uhk.kikm.navigationuhk.utils.scanners.Scanner;

/**
 * Odlehcena verze CollectorActivity urcena pouze ke hledani
 */

public class MainActivity extends ActionBarActivity {

    WebView view;
    CouchDBManager dbManager;
    Scanner scanner;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary);

        dbManager = new CouchDBManager(this);

        view = (WebView) findViewById(R.id.webViewPrimary);

        view.getSettings().setBuiltInZoomControls(true); // Zapnuti zoom controls
        view.getSettings().setSupportZoom(true);
        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new WebViewClient());

        view.loadData(readTextFromResource(R.drawable.uhk_j_2_level), null, "UTF-8"); // nacteni souboru do prohlizece
        Toast.makeText(this, getString(R.string.title_level2), Toast.LENGTH_SHORT).show();

        scanner = new Scanner(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_primary, menu);
        return true;
    }

    @Override
    protected void onRestart() {
        dbManager = new CouchDBManager(this);
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanner.stopScan();
    }

    @Override
    protected void onStop() {
        dbManager.closeConnection();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_find) {
            Toast.makeText(this, R.string.searching, Toast.LENGTH_SHORT).show();
            findPosition();
        } else if (id == R.id.action_level_1) {
            Toast.makeText(this, getString(R.string.title_level1), Toast.LENGTH_SHORT).show();
            changeLevel(1);
        } else if (id == R.id.action_level_2) {
            Toast.makeText(this, getString(R.string.title_level2), Toast.LENGTH_SHORT).show();
            changeLevel(2);
        } else if (id == R.id.action_level_3) {
            Toast.makeText(this, getString(R.string.title_level3), Toast.LENGTH_SHORT).show();
            changeLevel(3);
        } else if (id == R.id.action_level_4) {
            Toast.makeText(this, getString(R.string.title_level4), Toast.LENGTH_SHORT).show();
            changeLevel(4);
        } else if (id == R.id.action_download) {
            Toast.makeText(this, R.string.downloading, Toast.LENGTH_SHORT).show();
            downloadDB();
        } else if (id == R.id.action_change_mode) {
            runCollectorActivity();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Zobrazi bod na mape o urcite barve, ktera je vyhodnocena z patra porizeni
     *
     * @param x     x bodu
     * @param y     y bodu
     * @param level cislo patra
     */
    private void showPoint(int x, int y, String level) {
        switch (level) {
            case "J1NP":
                view.loadUrl("javascript:setPoint(" + String.valueOf(x) + ", " + String.valueOf(y) + ", \"red\"" + ")");
                break;
            case "J2NP":
                view.loadUrl("javascript:setPoint(" + String.valueOf(x) + ", " + String.valueOf(y) + ", \"blue\"" + ")");
                break;
            case "J3NP":
                view.loadUrl("javascript:setPoint(" + String.valueOf(x) + ", " + String.valueOf(y) + ", \"green\"" + ")");
                break;
            case "J4NP":
                view.loadUrl("javascript:setPoint(" + String.valueOf(x) + ", " + String.valueOf(y) + ", \"yellow\"" + ")");
                break;
        }
    }

    /**
     * Spusti CollectorActivity pres LoginActivity kvuli prihlaseni
     */
    private void runCollectorActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Vyhledava pozici
     */
    private void findPosition() {
        if (!dbManager.existsDB()) // pokud DB neexstuje, je nutne stahnout data
            Toast.makeText(this, R.string.db_needed, Toast.LENGTH_SHORT).show();
        else {
            scanner.startScan(C.SCAN_FINDER_TIME, true, false, false, new ScanResultListener() {
                @Override
                public void onScanFinished(final List<WifiScan> wifiScans, List<BleScan> bleScans, List<CellScan> cellScans) {
                    final List<Fingerprint> fingerprints = new ArrayList<>();
                    for (WifiScan s : wifiScans) {
                        String[] mac = new String[]{s.getSSID()};
                        fingerprints.addAll(dbManager.getFingerprintsByMacs(mac));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (fingerprints.size() > 0) {
                                Fingerprint possibleFingerprint = new WifiFinder(fingerprints).computePossibleFingerprint(wifiScans);
                                showPoint(possibleFingerprint.getX(), possibleFingerprint.getY(), possibleFingerprint.getLevel());
                            } else {
                                Toast.makeText(MainActivity.this, R.string.insufficient_wifi_data, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }

    private void downloadDB() {
        dbManager.downloadDBFromServer(this);
    }

    /**
     * Reloaduje obrazek patra
     *
     * @param level cislo patra
     */
    private void changeLevel(int level) {
        switch (level) {
            case 1:
                view.loadData(readTextFromResource(R.drawable.uhk_j_1_level), null, "UTF-8"); // nacteni souboru do prohlizece
                view.reload();
                break;
            case 2:
                view.loadData(readTextFromResource(R.drawable.uhk_j_2_level), null, "UTF-8"); // nacteni souboru do prohlizece
                view.reload();
                break;
            case 3:
                view.loadData(readTextFromResource(R.drawable.uhk_j_3_level), null, "UTF-8"); // nacteni souboru do prohlizece
                view.reload();
                break;
            case 4:
                view.loadData(readTextFromResource(R.drawable.uhk_j_4_level), null, "UTF-8"); // nacteni souboru do prohlizece
                view.reload();
                break;

        }
    }

    /**
     * Metoda na nacteni textu z nejakeho souboru.
     *
     * @param resourceID ID zdroje
     * @return String text
     */
    private String readTextFromResource(int resourceID) {
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
}
