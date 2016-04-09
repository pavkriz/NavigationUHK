package uhk.kikm.navigationuhk.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Bc. Radek Brůha <bruhara1@uhk.cz>
 */
public class HttpResponseParser {
	public static HashMap<String, String> parseJson(String response) {
		HashMap<String, String> hashMap = new HashMap<>();
		try {
			JSONObject jsonObject = new JSONObject(response);
			Iterator<String> keys = jsonObject.keys();
			String key;
			while (keys.hasNext()) hashMap.put((key = keys.next()), jsonObject.getString(key));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return hashMap;
	}

	public static String parseJsonToQuery(HashMap<String, String> hashMap) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String key : hashMap.keySet()) stringBuilder.append("&" + key + "=" + hashMap.get(key));
		return stringBuilder.toString().length() != 0 ? stringBuilder.toString().substring(1) : stringBuilder.toString();
	}
}