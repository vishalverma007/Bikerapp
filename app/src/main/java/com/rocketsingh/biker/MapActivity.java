package com.rocketsingh.biker;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.rocketsingh.biker.adapter.DrawerAdapter;
import com.rocketsingh.biker.base.ActionBarBaseActivitiy;
import com.rocketsingh.biker.db.DBHelper;
import com.rocketsingh.biker.fragment.ClientRequestFragment;
import com.rocketsingh.biker.fragment.FeedbackFrament;
import com.rocketsingh.biker.fragment.JobFragment;
import com.rocketsingh.biker.model.ApplicationPages;
import com.rocketsingh.biker.model.Bill;
import com.rocketsingh.biker.model.RequestDetail;
import com.rocketsingh.biker.model.User;
import com.rocketsingh.biker.parse.AsyncTaskCompleteListener;
import com.rocketsingh.biker.parse.HttpRequester;
import com.rocketsingh.biker.parse.ParseContent;
import com.rocketsingh.biker.utills.AndyConstants;
import com.rocketsingh.biker.utills.AndyUtils;
import com.rocketsingh.biker.utills.AppLog;
import com.rocketsingh.biker.utills.PreferenceHelper;
import com.rocketsingh.biker.widget.MyFontTextViewDrawer;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Kishan H Dhamat
 * 
 */
public class MapActivity extends ActionBarBaseActivitiy implements
		OnItemClickListener, AsyncTaskCompleteListener {
	// private DrawerLayout drawerLayout;
	public long back_pressed;
	private DrawerAdapter adapter;
	private String[] items_offline;
	private ListView drawerList;
	// private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private PreferenceHelper preferenceHelper;
	private ParseContent parseContent;
	private static final String TAG = "MapActivity";
	private ArrayList<ApplicationPages> arrayListApplicationPages;
	private boolean isDataRecieved = false, isRecieverRegistered = false,
			isNetDialogShowing = false, isGpsDialogShowing = false;
	private AlertDialog internetDialog, gpsAlertDialog;
	private LocationManager manager;
	private MenuDrawer mMenuDrawer;
	private DBHelper dbHelper;
	private AQuery aQuery;
	private ImageOptions imageOptions;
	private ImageView ivMenuProfile;
	private MyFontTextViewDrawer tvMenuName;
	private int is_approved = -1;
	private SharedPreferences pref;// =getSharedPreferences("approved",
									// MODE_PRIVATE);
	SharedPreferences.Editor editor;// =pref.edit();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = getSharedPreferences("approved", MODE_PRIVATE);
		editor = pref.edit();

		// Mint.initAndStartSession(MapActivity.this, "fdd1b971");
		// setContentView(R.layout.activity_map);
		preferenceHelper = new PreferenceHelper(this);
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW);
		mMenuDrawer.setContentView(R.layout.activity_map);
		mMenuDrawer.setMenuView(R.layout.menu_drawer);
		mMenuDrawer.setDropShadowEnabled(false);
		arrayListApplicationPages = new ArrayList<ApplicationPages>();
		parseContent = new ParseContent(this);
		mTitle = mDrawerTitle = getTitle();
		// drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);

		ivMenuProfile = (ImageView) findViewById(R.id.ivMenuProfile);
		tvMenuName = (MyFontTextViewDrawer) findViewById(R.id.tvMenuName);


		drawerList.setOnItemClickListener(this);
		adapter = new DrawerAdapter(this, arrayListApplicationPages);
		drawerList.setAdapter(adapter);

		// drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
		// GravityCompat.START);
		btnActionMenu.setVisibility(View.VISIBLE);
		btnActionMenu.setOnClickListener(this);
		tvTitle.setOnClickListener(this);
		btnNotification.setVisibility(View.GONE);
		setActionBarIcon(R.drawable.menu);
		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// getSupportActionBar().setHomeButtonEnabled(true);

		// mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
		// R.drawable.slide_btn, R.string.drawer_open,
		// R.string.drawer_close) {
		//
		// public void onDrawerClosed(View view) {
		// getSupportActionBar().setTitle(mTitle);
		// // supportInvalidateOptionsMenu(); // creates call to
		// // onPrepareOptionsMenu()
		// }
		//
		// public void onDrawerOpened(View drawerView) {
		// getSupportActionBar().setTitle(mDrawerTitle);
		// supportInvalidateOptionsMenu();
		// }
		// };
		// drawerLayout.setDrawerListener(mDrawerToggle);
		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		aQuery = new AQuery(this);
		imageOptions = new ImageOptions();
		imageOptions.memCache = true;
		imageOptions.fileCache = true;
		imageOptions.targetWidth = 200;
		imageOptions.fallback = R.drawable.user;

		dbHelper = new DBHelper(getApplicationContext());
		User user = dbHelper.getUser();

		//DbFetchAsync dbFetchAsync = new DbFetchAsync();
		//dbFetchAsync.execute();
		// if (savedInstanceState == null) {
		// selectItem(-1);
		// }
		aQuery.id(ivMenuProfile).progress(R.id.pBar)
				.image(user.getPicture(),imageOptions);
		tvMenuName.setText(user.getFname() + " " + user.getLname());
		try
		{
			if (getIntent().getExtras() != null) {
				is_approved = getIntent().getExtras().getInt("approved", -1);
				Log.i(TAG, "Getting approved flag from Intent :" + is_approved);
				editor.putInt("approved value", is_approved);
				editor.commit();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		/*if (getIntent().getExtras() != null) {
			is_approved = getIntent().getExtras().getInt("approved");
		} else {
			is_approved = pref.getInt("approved value", 0);
		}*/
		if (pref.getInt("approved value", 0) == 0) {
			new AlertDialog.Builder(this)
					.setTitle("Driver not approved")
					.setMessage("Please contact your admin to approve you and login again")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// continue with delete
//									Intent intent;
//									intent = new Intent(getApplicationContext(), MainActivity.class);
//									startActivity(intent);
									checkState();
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		}
		//TO BE REMOVED-just for testing
		Intent intent =getIntent();
		if(intent.getStringExtra("fromNotification") != null)
		{
			Log.i("cancallclientreqfrag","we will call here");
		}

		//Mint.initAndStartSession(MapActivity.this, "95115ca7");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// if (mDrawerToggle.onOptionsItemSelected(item)) {
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}

	public void checkStatus() {
		if (preferenceHelper.getRequestId() == AndyConstants.NO_REQUEST) {
			AppLog.Log(TAG, "onResume getRequestInProgress() called since there is no request in sharedpref");
			getRequestsInProgress();
		} else {
			AppLog.Log(TAG, "onResume checkRequestStatus() called since there is a request in sharedpref");
			checkRequestStatus();
		}
	}

	private void getMenuItems() {
		getMenuItemsOffline();
		/*HashMap<String, String> ;map = new HashMap<String, String>();
		map.put(AndyConstants.URL, AndyConstants.ServiceType.APPLICATION_PAGES);
		new HttpRequester(this, map,
				AndyConstants.ServiceCode.APPLICATION_PAGES, true, this);
				*/
	}
    //new method
	private void getMenuItemsOffline()
	{
		arrayListApplicationPages = parseContent.parsePages(
				arrayListApplicationPages,"");
		ApplicationPages applicationPages = new ApplicationPages();
		applicationPages.setData("");
		applicationPages.setId(-4);
		applicationPages.setIcon("");
		applicationPages.setTitle(getString(R.string.text_logout));
		arrayListApplicationPages.add(applicationPages);
		adapter.notifyDataSetChanged();
		isDataRecieved = true;
	}

	public BroadcastReceiver GpsChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			AppLog.Log(TAG, "On recieve GPS provider broadcast");
			final LocationManager manager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// do something
				removeGpsDialog();
			} else {
				// do something else
				if (isGpsDialogShowing) {
					return;
				}
				ShowGpsDialog();
			}

		}
	};
	public BroadcastReceiver internetConnectionReciever = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo activeWIFIInfo = connectivityManager
					.getNetworkInfo(connectivityManager.TYPE_WIFI);

			if (activeWIFIInfo.isConnected() || activeNetInfo.isConnected()) {
				removeInternetDialog();
			} else {
				if (isNetDialogShowing) {
					return;
				}
				showInternetDialog();
			}
		}
	};

	private void ShowGpsDialog() {
		AndyUtils.removeCustomProgressDialog();
		isGpsDialogShowing = true;
		AlertDialog.Builder gpsBuilder = new AlertDialog.Builder(
				MapActivity.this);
		gpsBuilder.setCancelable(false);
		gpsBuilder
				.setTitle(getString(R.string.dialog_no_gps))
				.setMessage(getString(R.string.dialog_no_gps_messgae))
				.setPositiveButton(getString(R.string.dialog_enable_gps),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
								Intent intent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(intent);
								removeGpsDialog();
							}
						})

				.setNegativeButton(getString(R.string.dialog_exit),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								removeGpsDialog();
								finish();
							}
						});
		gpsAlertDialog = gpsBuilder.create();
		gpsAlertDialog.show();
	}

	private void removeGpsDialog() {
		if (gpsAlertDialog != null && gpsAlertDialog.isShowing()) {
			gpsAlertDialog.dismiss();
			isGpsDialogShowing = false;
			gpsAlertDialog = null;

		}
	}

	private void removeInternetDialog() {
		if (internetDialog != null && internetDialog.isShowing()) {
			internetDialog.dismiss();
			isNetDialogShowing = false;
			internetDialog = null;
		}
	}

	private void showInternetDialog() {
		AndyUtils.removeCustomProgressDialog();
		isNetDialogShowing = true;
		AlertDialog.Builder internetBuilder = new AlertDialog.Builder(
				MapActivity.this);
		internetBuilder.setCancelable(false);
		internetBuilder
				.setTitle(getString(R.string.dialog_no_internet))
				.setMessage(getString(R.string.dialog_no_inter_message))
				.setPositiveButton(getString(R.string.dialog_enable_3g),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
								Intent intent = new Intent(
										android.provider.Settings.ACTION_SETTINGS);
								startActivity(intent);
								removeInternetDialog();
							}
						})
				.setNeutralButton(getString(R.string.dialog_enable_wifi),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// User pressed Cancel button. Write
								// Logic Here
								startActivity(new Intent(
										Settings.ACTION_WIFI_SETTINGS));
								removeInternetDialog();
							}
						})
				.setNegativeButton(getString(R.string.dialog_exit),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								removeInternetDialog();
								finish();
							}
						});
		internetDialog = internetBuilder.create();
		internetDialog.show();
	}

	@Override
	protected void onResume() {
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			ShowGpsDialog();
		} else {
			removeGpsDialog();
		}
		registerReceiver(internetConnectionReciever, new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE"));
		registerReceiver(GpsChangeReceiver, new IntentFilter(
				LocationManager.PROVIDERS_CHANGED_ACTION));
		isRecieverRegistered = true;

		if (AndyUtils.isNetworkAvailable(this)
				&& manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if (!isDataRecieved) {

				checkStatus();
				startLocationUpdateService();

			}
		}
		super.onResume();
		//Mint.startSession(MapActivity.this);
	};

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//Mint.closeSession(MapActivity.this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AndyUtils.removeCustomProgressDialog();
		// Mint.closeSession(this);
		if (isRecieverRegistered) {
			unregisterReceiver(internetConnectionReciever);
			unregisterReceiver(GpsChangeReceiver);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// AndyUtils.showToast("Postion :" + arg2, this);
		// drawerLayout.closeDrawer(drawerList);
		mMenuDrawer.closeMenu();
		if (position == 0) {
			startActivity(new Intent(this, ProfileActivity.class));
		} else if (position == 1) {
			startActivity(new Intent(this, HistoryActivity.class));
		} else if (position == 2) {
			startActivity(new Intent(this, SettingActivity.class));
		} else if (position == 3) {
			startActivity(new Intent(this, CurrentItemsActitvity.class));
		} else if (position == (arrayListApplicationPages.size() - 1)) {
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.dialog_logout))
					.setMessage(getString(R.string.dialog_logout_text))
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// continue with delete
									checkState();

								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
									dialog.cancel();
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		} else {
			Intent intent = new Intent(this, MenuDescActivity.class);
			intent.putExtra(AndyConstants.Params.TITLE,
					arrayListApplicationPages.get(position).getTitle());
			intent.putExtra(AndyConstants.Params.CONTENT,
					arrayListApplicationPages.get(position).getData());
			startActivity(intent);
		}
	}

	// @Override
	// public void setTitle(CharSequence title) {
	// mTitle = title;
	// getSupportActionBar().setTitle(mTitle);
	// }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnActionMenu:
			mMenuDrawer.toggleMenu();
			break;

		case R.id.tvTitle:
			mMenuDrawer.toggleMenu();

		default:
			break;
		}
	}

	long requestInProgressStarted, checkRequestStatusStarted;
	public void getRequestsInProgress() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this, "",
				getResources().getString(R.string.progress_dialog_request),
				false);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL,
				AndyConstants.ServiceType.REQUEST_IN_PROGRESS
						+ AndyConstants.Params.ID + "="
						+ preferenceHelper.getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ preferenceHelper.getSessionToken());
		requestInProgressStarted = System.currentTimeMillis();
		Log.i(TAG, "sending HTTP-REQUESTINPROGRESS");
		new HttpRequester(this, map,
				AndyConstants.ServiceCode.REQUEST_IN_PROGRESS, true, this);
	}

	public void checkRequestStatus() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}
		Log.i(TAG, "request start here");
		AndyUtils.showCustomProgressDialog(this, "",
				getResources().getString(R.string.progress_dialog_request),
				false);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL,
				AndyConstants.ServiceType.CHECK_REQUEST_STATUS
						+ AndyConstants.Params.ID + "="
						+ preferenceHelper.getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ preferenceHelper.getSessionToken() + "&"
						+ AndyConstants.Params.REQUEST_ID + "="
						+ preferenceHelper.getRequestId());
		checkRequestStatusStarted = System.currentTimeMillis();
		Log.i(TAG, "sending HTTP-CHECKREQUESTSTATUS");
		new HttpRequester(this, map,
				AndyConstants.ServiceCode.CHECK_REQUEST_STATUS, true, this);
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		super.onTaskCompleted(response, serviceCode);

		switch (serviceCode) {
		case AndyConstants.ServiceCode.REQUEST_IN_PROGRESS:
			AndyUtils.removeCustomProgressDialog();
			Log.i(TAG, "HTTP-REQUESTINPROGRESS serviced. Time taken :" + (System.currentTimeMillis() - requestInProgressStarted) + "ms");
			AppLog.Log(TAG, "requestInProgress Response :" + response);
			if (!parseContent.isSuccess(response)) {
				if (parseContent.getErrorCode(response) == AndyConstants.REQUEST_ID_NOT_FOUND) {
					AndyUtils.removeCustomProgressDialog();
					preferenceHelper.clearRequestData();
					//getMenuItemsOffline();
					getMenuItems();
					Log.i(TAG, "ClientRequestFragment Added - REQUEST_ID_NOT_FOUND");
					addFragment(new ClientRequestFragment(), false,
							AndyConstants.CLIENT_REQUEST_TAG, true);
				} else if (parseContent.getErrorCode(response) == AndyConstants.INVALID_TOKEN) {
					if (preferenceHelper.getLoginBy().equalsIgnoreCase(
							AndyConstants.MANUAL))
						login();
					else
						loginSocial(preferenceHelper.getUserId(),
								preferenceHelper.getLoginBy());
				}
				return;
			}

			int requestId = parseContent.parseRequestInProgress(response);
			if (requestId == AndyConstants.NO_REQUEST) {
				//getMenuItemsOffline();
				getMenuItems();
				Log.i(TAG, "ClientRequestFragment Added - NO_REQUEST");
				addFragment(new ClientRequestFragment(), false,
						AndyConstants.CLIENT_REQUEST_TAG, true);
			} else {
				checkRequestStatus();
			}
			break;
		case AndyConstants.ServiceCode.CHECK_REQUEST_STATUS:
			Log.i(TAG, "HTTP-CHECKREQUESTSTATUS serviced. Time taken :" + (System.currentTimeMillis() - checkRequestStatusStarted) + "ms");
			AppLog.Log(TAG, "checkrequeststatus Response :" + response);
			if (!parseContent.isSuccess(response)) {
				if (parseContent.getErrorCode(response) == AndyConstants.REQUEST_ID_NOT_FOUND) {
					preferenceHelper.clearRequestData();
					AndyUtils.removeCustomProgressDialog();
					Log.i(TAG, "ClientRequestFragment Added - CHECK_REQUEST_STATUS");
					addFragment(new ClientRequestFragment(), false,
							AndyConstants.CLIENT_REQUEST_TAG, true);
				} else if (parseContent.getErrorCode(response) == AndyConstants.INVALID_TOKEN) {
					if (preferenceHelper.getLoginBy().equalsIgnoreCase(
							AndyConstants.MANUAL))
						login();
					else
						loginSocial(preferenceHelper.getUserId(),
								preferenceHelper.getLoginBy());
				}
				return;
			}
			AndyUtils.removeCustomProgressDialog();
			Bundle bundle = new Bundle();
			JobFragment jobFragment = new JobFragment();
			RequestDetail requestDetail = parseContent
					.parseRequestStatus(response);
			if (requestDetail == null) {
				return;
			}
			//getMenuItemsOffline();
			getMenuItems();
			switch (requestDetail.getJobStatus()) {
			case AndyConstants.IS_WALKER_STARTED:
				bundle.putInt(AndyConstants.JOB_STATUS,
						AndyConstants.IS_WALKER_STARTED);
				bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
						requestDetail);
				jobFragment.setArguments(bundle);
				addFragment(jobFragment, false, AndyConstants.JOB_FRGAMENT_TAG,
						true);
				break;
			case AndyConstants.IS_WALKER_ARRIVED:
				bundle.putInt(AndyConstants.JOB_STATUS,
						AndyConstants.IS_WALKER_ARRIVED);
				bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
						requestDetail);
				jobFragment.setArguments(bundle);
				addFragment(jobFragment, false, AndyConstants.JOB_FRGAMENT_TAG,
						true);
				break;
			case AndyConstants.IS_WALK_STARTED:
				bundle.putInt(AndyConstants.JOB_STATUS,
						AndyConstants.IS_WALK_STARTED);
				bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
						requestDetail);
				jobFragment.setArguments(bundle);
				addFragment(jobFragment, false, AndyConstants.JOB_FRGAMENT_TAG,
						true);
				break;
			case AndyConstants.IS_WALK_COMPLETED:
				bundle.putInt(AndyConstants.JOB_STATUS,
						AndyConstants.IS_WALK_COMPLETED);
				bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
						requestDetail);
				jobFragment.setArguments(bundle);
				addFragment(jobFragment, false, AndyConstants.JOB_FRGAMENT_TAG,
						true);

				break;
			case AndyConstants.IS_DOG_RATED:

				FeedbackFrament feedbackFrament = new FeedbackFrament();
				bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
						requestDetail);
				bundle.putString(AndyConstants.Params.TIME, 0 + " "
						+ getResources().getString(R.string.text_mins));
				bundle.putString(AndyConstants.Params.DISTANCE, 0 + " "
						+ getResources().getString(R.string.text_miles));
				Bill bill = parseContent.parsebillwhenwalkcomplete(response);
				bundle.putSerializable("bill", bill);

				feedbackFrament.setArguments(bundle);
				addFragment(feedbackFrament, false,
						AndyConstants.FEEDBACK_FRAGMENT_TAG, true);
				break;
			// case AndyConstants.IS_ASSIGNED:
			// bundle.putInt(AndyConstants.JOB_STATUS,
			// AndyConstants.IS_ASSIGNED);
			// jobFragment.setArguments(bundle);
			// addFragment(jobFragment, false, AndyConstants.JOB_FRGAMENT_TAG);
			// break;
			default:
				break;
			}

			break;
		case AndyConstants.ServiceCode.LOGIN:
			AndyUtils.removeCustomProgressDialog();
			if (parseContent.isSuccessWithId(response)) {
				checkStatus();
			}
			break;
		case AndyConstants.ServiceCode.APPLICATION_PAGES:

			AppLog.Log(TAG, "Menuitems Response::" + response);
			arrayListApplicationPages = parseContent.parsePages(
					arrayListApplicationPages,"");
			ApplicationPages applicationPages = new ApplicationPages();
			applicationPages.setData("");
			applicationPages.setId(-4);
			applicationPages.setIcon("");
			applicationPages.setTitle(getString(R.string.text_logout));
			arrayListApplicationPages.add(applicationPages);
			adapter.notifyDataSetChanged();
			isDataRecieved = true;
			break;
		case AndyConstants.ServiceCode.TOGGLE_STATE:
			AndyUtils.removeCustomProgressDialog();
			if (!parseContent.isSuccess(response)) {
				Toast.makeText(this, "Could not logout.Please try again",
						Toast.LENGTH_LONG).show();
				return;
			} else {
				preferenceHelper.Logout();
				goToMainActivity();
			}
			break;
		case AndyConstants.ServiceCode.CHECK_STATE:
			if (parseContent.parseAvaibilty(response)) {
				changeState();
			} else {
				preferenceHelper.Logout();
				goToMainActivity();
			}
			break;
		default:
			break;
		}
	}

	private void login() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, AndyConstants.ServiceType.LOGIN);
		map.put(AndyConstants.Params.EMAIL, preferenceHelper.getEmail());
		map.put(AndyConstants.Params.PASSWORD, preferenceHelper.getPassword());
		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.DEVICE_TOKEN,
				preferenceHelper.getDeviceToken());
		map.put(AndyConstants.Params.LOGIN_BY, AndyConstants.MANUAL);
		new HttpRequester(this, map, AndyConstants.ServiceCode.LOGIN, this);

	}

	private void loginSocial(String id, String loginType) {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, AndyConstants.ServiceType.LOGIN);
		map.put(AndyConstants.Params.SOCIAL_UNIQUE_ID, id);
		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.DEVICE_TOKEN,
				preferenceHelper.getDeviceToken());
		map.put(AndyConstants.Params.LOGIN_BY, loginType);
		new HttpRequester(this, map, AndyConstants.ServiceCode.LOGIN, this);

	}

	private void changeState() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}

		AndyUtils.showCustomProgressDialog(this, "",
				getResources().getString(R.string.progress_changing_avaibilty),
				false);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, AndyConstants.ServiceType.TOGGLE_STATE);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());

		new HttpRequester(this, map, AndyConstants.ServiceCode.TOGGLE_STATE,
				this);

	}

	private void checkState() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this, "",
				getResources().getString(R.string.progress_getting_avaibility),
				false);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL,
				AndyConstants.ServiceType.CHECK_STATE + AndyConstants.Params.ID
						+ "=" + preferenceHelper.getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ preferenceHelper.getSessionToken());
		new HttpRequester(this, map, AndyConstants.ServiceCode.CHECK_STATE,
				true, this);
	}

	public class DbFetchAsync extends AsyncTask<Void, Void, Void>{
		User user;

		@Override
		protected Void doInBackground(Void... voids) {
			dbHelper = new DBHelper(getApplicationContext());
			user = dbHelper.getUser();
			return null;
		}
		@Override
		protected void onPostExecute(Void aVoid) {
			Log.d("xxx", "from map activity" + user.getPicture());
			aQuery.id(ivMenuProfile).progress(R.id.pBar)
					.image(user.getPicture(), imageOptions);
			tvMenuName.setText(user.getFname() + " " + user.getLname());
		}
	}
	@Override
	public void onBackPressed(){
		if (back_pressed + 2000 > System.currentTimeMillis()){
			super.onBackPressed();
			finish();
		}
		else{
			Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
			back_pressed = System.currentTimeMillis();
		}
	}

}