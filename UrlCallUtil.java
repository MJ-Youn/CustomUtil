package com.smartmirror.advertisement.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class UrlCallUtil {

	private static final String CONTENT_TYPE = "application/json";
	
	public static JSONObject callURL(String urlPath, Object body, String method) {
		URL url = null;
		HttpURLConnection connection = null;
		JSONObject responseData = null;
		
		try {
			url = new URL(urlPath);
			connection = (HttpURLConnection) url.openConnection();
			
			connection.setRequestMethod(method);
			connection.setRequestProperty("Content-Type", CONTENT_TYPE);
			connection.setDoOutput(true);
			connection.connect();
			
			OutputStreamWriter output = new OutputStreamWriter(connection.getOutputStream());

			output.write(body.toString());
			output.flush();
			
			BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line = input.readLine();
			
			if (line != null) {
				responseData = new JSONObject(line);
			}
			
		} catch(MalformedURLException murle) {
			murle.printStackTrace();
		} catch (ProtocolException pe) {
			pe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (JSONException jsone) {
			jsone.printStackTrace();
		}
		
		return responseData;
	}
	
}
