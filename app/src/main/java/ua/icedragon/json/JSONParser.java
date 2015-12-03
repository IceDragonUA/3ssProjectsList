package ua.icedragon.json;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONParser {

    String resultJson;
    JSONObject objJson;
    BufferedReader reader;
    HttpURLConnection urlConnection;

    public JSONObject getJSONFromUrl(String _url) {
        try {
            URL url = new URL(_url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            resultJson = buffer.toString();
            objJson = new JSONObject(resultJson);
        } catch (Exception e)        {
            e.printStackTrace();
        }
        return objJson;
    }
}