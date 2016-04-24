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