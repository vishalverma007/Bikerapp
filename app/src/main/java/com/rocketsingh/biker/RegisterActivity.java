package com.rocketsingh.biker;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import com.rocketsingh.biker.base.ActionBarBaseActivitiy;
import com.rocketsingh.biker.fragment.LoginFragment;
import com.rocketsingh.biker.fragment.RegisterFragment;
import com.rocketsingh.biker.fragment.UberMainFragment;
import com.rocketsingh.biker.gcm.GCMRegisterHendler;
import com.rocketsingh.biker.utills.AndyConstants;
import com.rocketsingh.biker.utills.AndyUtils;

/**
 * @author Kishan H Dhamat
 * 
 */
public class RegisterActivity extends ActionBarBaseActivitiy {
	public ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		actionBar = getSupportActionBar();
		
		// addFragment(new UberMainFragment(), false,
		// AndyConstants.MAIN_FRAGMENT_TAG);
		if (getIntent().getBooleanExtra("isSignin", false)) {

			addFragment(new LoginFragment(), true,
					AndyConstants.LOGIN_FRAGMENT_TAG, false);
		} else {
			addFragment(new RegisterFragment(), true,
					AndyConstants.REGISTER_FRAGMENT_TAG, false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btnActionNotification:
			onBackPressed();
			break;

		default:
			break;
		}

	}

	public void registerGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
		if (mHandleMessageReceiver != null) {
			AndyUtils.showCustomProgressDialog(this, "", getResources()
					.getString(R.string.progress_loading), false);
			new GCMRegisterHendler(RegisterActivity.this,
					mHandleMessageReceiver);

		}
	}

	public void unregisterGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
		if (mHandleMessageReceiver != null) {

			if (mHandleMessageReceiver != null) {
				unregisterReceiver(mHandleMessageReceiver);
			}

		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		Fragment signinFragment = getSupportFragmentManager()
				.findFragmentByTag(AndyConstants.LOGIN_FRAGMENT_TAG);
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(
				AndyConstants.REGISTER_FRAGMENT_TAG);
		if (fragment != null && fragment.isVisible()) {

			goToMainActivity();
		} else if (signinFragment != null && signinFragment.isVisible()) {
			goToMainActivity();
		} else {
			super.onBackPressed();
		}

	}

}
