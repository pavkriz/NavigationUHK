package uhk.kikm.navigationuhk.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Bc. Radek Brůha <bruhara1@uhk.cz>
 */
public class HttpRequestGetSender extends AsyncTask<String, Void, String> {
	@Override
	protected String doInBackground(String... parameters) {
		try {
			String url = parameters[0] + "?" + parameters[1];

	//		url = "http://10.0.0.247:85/?fingerprint={wifiScans=[{SSID=%27eduroam%27,%20MAC=%2700:1a:e3:d2:e7:20%27,%20technology=%27802.11a/n%27,%20frequency=5180,%20channel=36,%20strength=-53,%20time=2825},%20{SSID=%27Delta-AP-Orinoco802.11g%27,%20MAC=%2700:20:a6:53:91:1c%27,%20technology=%27802.11g/n%27,%20frequency=2412,%20channel=1,%20strength=-58,%20time=2825},%20{SSID=%27MeteorCS_213_04%27,%20MAC=%2700:27:22:74:d0:d4%27,%20technology=%27802.11g/n%27,%20frequency=2427,%20channel=4,%20strength=-75,%20time=2826},%20{SSID=%27Delta-AP-Orinoco802.11a%27,%20MAC=%2700:20:a6:53:91:1b%27,%20technology=%27802.11a/n%27,%20frequency=5180,%20channel=36,%20strength=-51,%20time=4947},%20{SSID=%27Delta-AP-Orinoco802.11g%27,%20MAC=%2700:20:a6:53:91:1c%27,%20technology=%27802.11g/n%27,%20frequency=2412,%20channel=1,%20strength=-68,%20time=4948},%20{SSID=%27MeteorCS_213_04%27,%20MAC=%2700:27:22:74:d0:d4%27,%20technology=%27802.11g/n%27,%20frequency=2427,%20channel=4,%20strength=-75,%20time=4948}],%20bleScans=[{rssi=-91,%20uuid=%270x%27,%20major=0,%20minor=0,%20address=%2766:C9:92:C6:A7:EA%27,%20time=563},%20{rssi=-90,%20uuid=%270x%27,%20major=0,%20minor=0,%20address=%2748:D2:37:E8:BC:E7%27,%20time=783},%20{rssi=-88,%20uuid=%270x%27,%20major=0,%20minor=0,%20address=%2748:D2:37:E8:BC:E7%27,%20time=1333},%20{rssi=-91,%20uuid=%270x%27,%20major=0,%20minor=0,%20address=%2748:D2:37:E8:BC:E7%27,%20time=2423},%20{rssi=-93,%20uuid=%270x%27,%20major=0,%20minor=0,%20address=%2766:C9:92:C6:A7:EA%27,%20time=2566},%20{rssi=-93,%20uuid=%270x%27,%20major=0,%20minor=0,%20address=%2748:D2:37:E8:BC:E7%27,%20time=4253},%20{rssi=-92,%20uuid=%270x%27,%20major=0,%20minor=0,%20address=%2766:C9:92:C6:A7:EA%27,%20time=4397},%20{rssi=-92,%20uuid=%270x%27,%20major=0,%20minor=0,%20address=%2748:D2:37:E8:BC:E7%27,%20time=4619}]}";
			System.out.println(url);

			HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
			httpURLConnection.setRequestMethod("GET");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			StringBuilder stringBuilder = new StringBuilder();
			String responseLine;
			while ((responseLine = bufferedReader.readLine()) != null) stringBuilder.append(responseLine);
			return stringBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "An IOException occurred!";
		}
	}
}