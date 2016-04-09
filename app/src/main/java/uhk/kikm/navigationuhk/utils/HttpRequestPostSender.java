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
public class HttpRequestPostSender extends AsyncTask<String, Void, String> {
	@Override
	protected String doInBackground(String... parameters) {
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(parameters[0]).openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
			bufferedWriter.write(parameters[1]);
			bufferedWriter.close();
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