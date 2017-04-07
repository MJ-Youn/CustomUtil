package com.smartmirror.advertisement.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author MJYoun
 * @since 2017. 03. 06.
 *
 */
public class ResponseHeaderUtil {

	private static final int SUCCESS_CODE = 200;
	private static final String SUCCESS_MESSAGE = "success";
	private static final boolean SUCCESS_BOOLEAN = true;

	private static final int LOGIN_FAILURE_CODE = 403;
	private static final String LOGIN_FAILURE_MESSAGE = "login failure";

	public static final Map<String, Object> RESPONSE_SUCCESS_HEADER = createHeader(SUCCESS_CODE, SUCCESS_MESSAGE, SUCCESS_BOOLEAN);
	public static final Map<String, Object> RESPONSE_LOGIN_FAILURE_HEADER = createHeader(LOGIN_FAILURE_CODE, LOGIN_FAILURE_MESSAGE, SUCCESS_BOOLEAN);

	public static Map<String, Object> createHeader(int resultCode, String resultMessage, boolean isSuccessful) {
		Map<String, Object> header = new HashMap<String, Object>();
		header.put("resultCode", resultCode);
		header.put("resultMessage", resultMessage);
		header.put("isSuccessful", isSuccessful);
		return header;
	}

	public static boolean isSuccess(JSONObject response) {
		try {
			JSONObject header = (JSONObject) response.get("header");

			if (header.getInt("resultCode") == SUCCESS_CODE) {
				return true;
			}

		} catch (JSONException jsone) {
			jsone.printStackTrace();
		}

		return false;
	}

	public static JSONObject existBody(JSONObject response) {
		try {
			JSONObject body = (JSONObject) response.get("body");

			return body;
		} catch (JSONException jsone) {
			jsone.printStackTrace();
		}

		return null;
	}
}
