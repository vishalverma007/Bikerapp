package com.rocketsingh.biker.parse;

import org.json.JSONException;

public interface AsyncTaskCompleteListener {
	void onTaskCompleted(String response, int serviceCode) throws JSONException;

}
