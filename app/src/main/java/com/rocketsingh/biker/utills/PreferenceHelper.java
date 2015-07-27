package com.rocketsingh.biker.utills;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.rocketsingh.biker.db.DBHelper;

public class PreferenceHelper {

	private SharedPreferences app_prefs;
	private final String USER_ID = "user_id";
	private final String DEVICE_TOKEN = "device_token";
	private final String SESSION_TOKEN = "session_token";
	private final String REQUEST_ID = "request_id";
	private final String WALKER_LATITUDE = "latitude";
	private final String WALKER_LONGITUDE = "longitude";
	private final String PASSWORD = "password";
	private final String EMAIL = "email";
	private final String LOGIN_BY = "login_by";
	private final String SOCIAL_ID = "social_id";
	private final String REQUEST_TIME = "request_time";
	private final String TRIP_START = "trip_start";
	private final String DISTANCE = "distance";
	private final String UNIT = "unit";
	private Context context;

	public PreferenceHelper(Context context) {
		app_prefs = context.getSharedPreferences(AndyConstants.PREF_NAME,
				Context.MODE_PRIVATE);
		this.context = context;
	}

	public void putUserId(String userId) {
		Editor edit = app_prefs.edit();
		edit.putString(USER_ID, userId);
		edit.commit();
	}

	public String getUserId() {
		return app_prefs.getString(USER_ID, null);

	}

	public void putDeviceToken(String deviceToken) {
		Editor edit = app_prefs.edit();
		edit.putString(DEVICE_TOKEN, deviceToken);
		edit.commit();
	}

	public String getDeviceToken() {
		return app_prefs.getString(DEVICE_TOKEN, null);
	}

	public void putSessionToken(String sessionToken) {
		Editor edit = app_prefs.edit();
		edit.putString(SESSION_TOKEN, sessionToken);
		edit.commit();
	}

	public String getSessionToken() {
		return app_prefs.getString(SESSION_TOKEN, null);
	}

	public void putRequestId(int reqId) {
		Editor edit = app_prefs.edit();
		edit.putInt(REQUEST_ID, reqId);
		edit.commit();
	}

	public int getRequestId() {
		return app_prefs.getInt(REQUEST_ID, AndyConstants.NO_REQUEST);
	}

	public void putDistance(Float distance) {
		Editor edit = app_prefs.edit();
		edit.putFloat(DISTANCE, distance);
		edit.commit();
	}

	public float getDistance() {
		return app_prefs.getFloat(DISTANCE, 0.0f);
	}

	public void putUnit(String unit) {
		Editor edit = app_prefs.edit();
		edit.putString(UNIT, unit);
		edit.commit();
	}

	public String getUnit() {
		return app_prefs.getString(UNIT, " ");
	}

	public void putIsTripStart(boolean status) {
		Editor edit = app_prefs.edit();
		edit.putBoolean(TRIP_START, status);
		edit.commit();
	}

	public boolean getIsTripStart() {
		return app_prefs.getBoolean(TRIP_START, false);
	}

	public void putWalkerLatitude(String latitude) {
		Editor edit = app_prefs.edit();
		edit.putString(WALKER_LATITUDE, latitude);
		edit.commit();
	}

	public String getWalkerLatitude() {
		return app_prefs.getString(WALKER_LATITUDE, null);
	}

	public void putWalkerLongitude(String longitude) {
		Editor edit = app_prefs.edit();
		edit.putString(WALKER_LONGITUDE, longitude);
		edit.commit();
	}

	public String getWalkerLongitude() {
		return app_prefs.getString(WALKER_LONGITUDE, null);
	}

	public void putEmail(String email) {
		Editor edit = app_prefs.edit();
		edit.putString(EMAIL, email);
		edit.commit();
	}

	public String getEmail() {
		return app_prefs.getString(EMAIL, null);
	}

	public void putPassword(String password) {
		Editor edit = app_prefs.edit();
		edit.putString(PASSWORD, password);
		edit.commit();
	}

	public String getPassword() {
		return app_prefs.getString(PASSWORD, null);
	}

	public void putLoginBy(String loginBy) {
		Editor edit = app_prefs.edit();
		edit.putString(LOGIN_BY, loginBy);
		edit.commit();
	}

	public String getLoginBy() {
		return app_prefs.getString(LOGIN_BY, AndyConstants.MANUAL);
	}

	public void putSocialId(String socialId) {
		Editor edit = app_prefs.edit();
		edit.putString(SOCIAL_ID, socialId);
		edit.commit();
	}

	public String getSocialId() {
		return app_prefs.getString(SOCIAL_ID, null);
	}

	public void putRequestTime(long time) {
		Editor edit = app_prefs.edit();
		edit.putLong(REQUEST_TIME, time);
		edit.commit();
	}

	public long getRequestTime() {
		return app_prefs.getLong(REQUEST_TIME, AndyConstants.NO_TIME);
	}

	public void clearRequestData() {
		Log.i("clearReqdata", "preferhelp");
		putRequestId(AndyConstants.NO_REQUEST);
		putRequestTime(AndyConstants.NO_TIME);
		putIsTripStart(false);
		// new DBHelper(context).deleteAllLocations();
	}

	public void Logout() {
		clearRequestData();
		putUserId(null);
		putSessionToken(null);
		putLoginBy(AndyConstants.MANUAL);
		putSocialId(null);
		new DBHelper(context).deleteUser();
	}

	public void putCustomerDetails(String custName, String amount, String phoneNo, String address, String locality) {
		Editor editor = app_prefs.edit();
		editor.putString("Customername", custName);
		editor.putString("amount", amount);
		editor.putString("phoneno.", phoneNo);
		editor.putString("address", address);
		editor.putString("locality", locality);
		editor.commit();
	}

	public String getCustomerName() {
		return app_prefs.getString("Customername", "");
	}

	public String getCustomerAmount() {
		return app_prefs.getString("amount", "");
	}

	public String getCustomerPhoneNo() {
		return app_prefs.getString("phoneno.", "");
	}

	public String getCustomerAddress() {
		return app_prefs.getString("address", "");
	}

	public String getCustomerLocality() { return app_prefs.getString("locality", "");
	}

}
