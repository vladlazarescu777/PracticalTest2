package ro.pub.cs.systems.eim.practicaltest2;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread{
    private final ServerThread serverThread;
    private final Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        // It first checks whether the socket is null, and if so, it logs an error and returns.
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }

            // Create BufferedReader and PrintWriter instances for reading from and writing to the socket
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = Utilities.getReader(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PrintWriter printWriter = null;
        try {
            printWriter = Utilities.getWriter(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

            // Read the city and informationType values sent by the client
        String city = null;
        try {
            city = bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (city == null || city.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }

            // It checks whether the serverThread has already received the weather forecast information for the given city.
            HashMap<String, Information> data = serverThread.getData();
            Information weatherForecastInformation;
            if (data.containsKey(city)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                weatherForecastInformation = data.get(city);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";

                // make the HTTP request to the web service
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + city);
                HttpResponse httpGetResponse = null;
                try {
                    httpGetResponse = httpClient.execute(httpGet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    try {
                        pageSourceCode = EntityUtils.toString(httpGetEntity);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else Log.i(Constants.TAG, pageSourceCode);



                // Parse the page source code into a JSONObject and extract the needed information
//                JSONObject content = new JSONObject(pageSourceCode);
//                JSONArray weatherArray = content.getJSONArray(Constants.WEATHER);
//                JSONObject weather;
//                StringBuilder condition = new StringBuilder();
//                for (int i = 0; i < weatherArray.length(); i++) {
//                    weather = weatherArray.getJSONObject(i);
//                    condition.append(weather.getString(Constants.MAIN)).append(" : ").append(weather.getString(Constants.DESCRIPTION));
//
//                    if (i < weatherArray.length() - 1) {
//                        condition.append(";");
//                    }
//                }
//                JSONObject main = content.getJSONObject(Constants.MAIN);
//                String definition = main.getString(Constants.DESCRIPTION);
//
                // Create a WeatherForecastInformation object with the information extracted from the JSONObject
                weatherForecastInformation = new Information(pageSourceCode);
//
//                // Cache the information for the given city
//                serverThread.setData(city, weatherForecastInformation);
            }

            if (weatherForecastInformation == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }

            // Send the information back to the client
            String result  = weatherForecastInformation.toString();

            // Send the result back to the client
            printWriter.println(result);
            printWriter.flush();
            Log.d("myTaggg", "This is my message22");

    }
}
