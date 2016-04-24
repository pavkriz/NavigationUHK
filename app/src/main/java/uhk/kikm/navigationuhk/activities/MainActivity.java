package uhk.kikm.navigationuhk.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import uhk.kikm.navigationuhk.R;
import uhk.kikm.navigationuhk.dataLayer.BleScan;
import uhk.kikm.navigationuhk.dataLayer.CellScan;
import uhk.kikm.navigationuhk.dataLayer.CouchDBManager;
import uhk.kikm.navigationuhk.dataLayer.Fingerprint;
import uhk.kikm.navigationuhk.dataLayer.WifiScan;
import uhk.kikm.navigationuhk.utils.C;
import uhk.kikm.navigationuhk.utils.HttpRequestPostSender;
import uhk.kikm.navigationuhk.utils.HttpResponseParser;
import uhk.kikm.navigationuhk.utils.finders.WifiFinder;
import uhk.kikm.navigationuhk.utils.localization.LocalizationService;
import uhk.kikm.navigationuhk.utils.scanners.DeviceInformation;
import uhk.kikm.navigationuhk.utils.scanners.ScanResultListener;
import uhk.kikm.navigationuhk.utils.scanners.Scanner;
import uhk.kikm.navigationuhk.utils.scanners.SensorScanner;

/**
 * Odlehcena verze CollectorActivity urcena pouze ke hledani
 */

public class MainActivity extends ActionBarActivity {
	WebView view;
	CouchDBManager dbManager;
	Scanner scanner;
	SensorScanner sensorScanner;
	String selectedLevel = "Piskoviste";
	boolean search;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_primary);

		dbManager = new CouchDBManager(this);

		sensorScanner = new SensorScanner(this);

		view = (WebView) findViewById(R.id.webViewPrimary);

		view.getSettings().setBuiltInZoomControls(true); // Zapnuti zoom controls
		view.getSettings().setSupportZoom(true);
		view.getSettings().setJavaScriptEnabled(true);
		view.setWebViewClient(new WebViewClient());
		view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		try {
			view.loadUrl("http://beacon.uhk.cz/fimnav-webview/?map=" + URLEncoder.encode("Piskoviste", "UTF-8"));  // nacteni souboru do prohlizece
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// view.loadData(readTextFromResource(R.drawable.uhk_j_2_level), null, "UTF-8"); // nacteni souboru do prohlizece
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
			item.setTitle(item.getTitle().equals("Hledej") ? "Stop" : "Hledej");
			search = !search;
			findPosition();
		} else if (id == R.id.action_level_1) {
			Toast.makeText(this, getString(R.string.title_level1), Toast.LENGTH_SHORT).show();
			changeLevel("J1NP");
		} else if (id == R.id.action_level_2) {
			Toast.makeText(this, getString(R.string.title_level2), Toast.LENGTH_SHORT).show();
			changeLevel("J2NP");
		} else if (id == R.id.action_level_3) {
			Toast.makeText(this, getString(R.string.title_level3), Toast.LENGTH_SHORT).show();
			changeLevel("J3NP");
		} else if (id == R.id.action_level_4) {
			Toast.makeText(this, getString(R.string.title_level4), Toast.LENGTH_SHORT).show();
			changeLevel("J4NP");
		} else if (id == R.id.action_level_0) { // Sandbox
			changeLevel("Piskoviste"); // // Testing purposes only
		} else if (id == R.id.action_level_krizovi) {
			changeLevel("Krizovi");
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

	private void findPosition() {
		Toast.makeText(this, R.string.searching, Toast.LENGTH_SHORT).show();
		sensorScanner.startScan();
		final Handler handler = new Handler();
		final ScanResultListener scanResultListener = new ScanResultListener() {
			@Override
			public void onScanFinished(final List<WifiScan> wifiScans, final List<BleScan> bleScans, final List<CellScan> cellScans) {
				sensorScanner.stopScan();
				final Fingerprint p = new Fingerprint(-1, -1);
				p.setWifiScans(wifiScans);
				p.setBleScans(bleScans); // naplnime daty z Bluetooth
				p.setCellScans(cellScans);
				sensorScanner.fillPosition(p);  // naplnime daty ze senzoru
				new DeviceInformation(MainActivity.this).fillPosition(p); // naplnime infomacemi o zarizeni
				new LocalizationService(C.pointA, C.pointB, C.pointC).getPoint(p); // nastavujeme souradnicovy system pro vypocet GPS souradnic a naplnime vypocitanymi souradnicemi

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						int max = p.getWifiScans().size();

						// TODO: Refactor JSON generating...
						int i = 1;
						String json = "{\"wifiScans\":[";
						for (WifiScan w : p.getWifiScans()) {
							json += w.toJson() + (i != max ? ", " : "");
							i++;
						}

						json += "], \"bleScans\":[";
						max = p.getBleScans().size();
						i = 1;
						for (BleScan b : p.getBleScans()) {
							json += b.toJson() + (i != max ? ", " : "");
							i++;
						}
						json += "]}";

						HashMap<String, String> hashMap = null;
						try {
							Log.i(getClass().toString(), "HTTP request sent: http://beacon.uhk.cz/localization-service/api/location?fingerprint=" + json);
							hashMap = HttpResponseParser.parseJson(new HttpRequestPostSender().execute("http://beacon.uhk.cz/localization-service/api/location?", "fingerprint=" + json).get());
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						if (hashMap.get("fingerprint") != null) hashMap.remove("fingerprint");

						try {
							Log.i(getClass().toString(), "HTTP request sent: http://beacon.uhk.cz/fimnav-webview/?" + HttpResponseParser.parseJsonToQuery(hashMap));
							if (hashMap.get("floor").equals(selectedLevel)) {
								view.loadUrl("javascript:scrollToLogicXY(" + hashMap.get("x") + ", " + hashMap.get("y") + ");");
							} else {
								view.loadUrl("http://beacon.uhk.cz/fimnav-webview/?map=" + URLEncoder.encode(hashMap.get("floor"), "UTF-8")
									+ "&x=" + URLEncoder.encode(hashMap.get("x"), "UTF-8") + "&y=" + URLEncoder.encode(hashMap.get("y"), "UTF-8"));  // nacteni souboru do prohlizece
							}
							selectedLevel = hashMap.get("floor");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				});

				if (search) {
					if (handler != null) handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							findPosition();
							Log.i(getClass().toString(), "Starting new scan...");
						}
					}, 1000);
				}
			}
		};
		scanner.setWithProgressDialog(false);
		scanner.startScan(C.SCAN_FINDER_TIME, true, true, true, scanResultListener);
	}

	/**
	 * Vyhledava pozici
	 */
	private void findPositionOld() {
		if (!dbManager.existsDB()) // pokud DB neexstuje, je nutne stahnout data
			Toast.makeText(this, R.string.db_needed, Toast.LENGTH_SHORT).show();
		else {
			scanner.startScan(C.SCAN_FINDER_TIME, true, false, false, new ScanResultListener() {
				@Override
				public void onScanFinished(final List<WifiScan> wifiScans, List<BleScan> bleScans, List<CellScan> cellScans) {
					final List<Fingerprint> fingerprints = new ArrayList<>();
					for (WifiScan s : wifiScans) {
						String[] mac = new String[] { s.getSSID() };
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
	private void changeLevel(String level) {
		try {
			view.loadUrl("http://beacon.uhk.cz/fimnav-webview/?map=" + URLEncoder.encode(level, "UTF-8"));  // nacteni souboru do prohlizece
			Toast.makeText(this, level, Toast.LENGTH_SHORT).show();
			//   selectedLevel = level;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reloaduje obrazek patra
	 *
	 * @param level cislo patra
	 */
	private void changeLevelOld(int level) {
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
