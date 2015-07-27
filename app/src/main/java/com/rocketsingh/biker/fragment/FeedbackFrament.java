package com.rocketsingh.biker.fragment;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.rocketsingh.biker.R;
import com.rocketsingh.biker.base.BaseMapFragment;
import com.rocketsingh.biker.model.Bill;
import com.rocketsingh.biker.model.RequestDetail;
import com.rocketsingh.biker.parse.AsyncTaskCompleteListener;
import com.rocketsingh.biker.parse.HttpRequester;
import com.rocketsingh.biker.utills.AndyConstants;
import com.rocketsingh.biker.utills.AndyUtils;
import com.rocketsingh.biker.utills.AppLog;
import com.rocketsingh.biker.widget.MyFontEdittextView;
import com.rocketsingh.biker.widget.MyFontTextView;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * @author Kishan H Dhamat
 * 
 */
public class FeedbackFrament extends BaseMapFragment implements
		AsyncTaskCompleteListener {

	private MyFontEdittextView etFeedbackComment;
	private ImageView ivDriverImage;
	private RatingBar ratingFeedback;
	private MyFontTextView tvTime, tvDistance, tvClientName;
	private final String TAG = "FeedbackFrament";
	private AQuery aQuery;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View feedbackFragmentView = inflater.inflate(
				R.layout.fragment_feedback, container, false);

		etFeedbackComment = (MyFontEdittextView) feedbackFragmentView
				.findViewById(R.id.etFeedbackComment);
		tvTime = (MyFontTextView) feedbackFragmentView
				.findViewById(R.id.tvFeedBackTime);
		tvDistance = (MyFontTextView) feedbackFragmentView
				.findViewById(R.id.tvFeedbackDistance);
		ratingFeedback = (RatingBar) feedbackFragmentView
				.findViewById(R.id.ratingFeedback);
		ivDriverImage = (ImageView) feedbackFragmentView
				.findViewById(R.id.ivFeedbackDriverImage);
		tvClientName = (MyFontTextView) feedbackFragmentView
				.findViewById(R.id.tvClientName);

		mapActivity.setActionBarTitle(getResources().getString(
				R.string.text_feedback));

		feedbackFragmentView.findViewById(R.id.tvFeedbackSubmit)
				.setOnClickListener(this);
		feedbackFragmentView.findViewById(R.id.tvFeedbackskip)
				.setOnClickListener(this);

		return feedbackFragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		aQuery = new AQuery(mapActivity);
		RequestDetail requestDetail = (RequestDetail) getArguments()
				.getSerializable(AndyConstants.REQUEST_DETAIL);

		//removed to hide bill

		Bill bill = (Bill) getArguments().getSerializable("bill");

		final Dialog mDialog = new Dialog(getActivity(),
				android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.bill_layout);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		DecimalFormat perHourFormat = new DecimalFormat("0.0");
		String currency = bill.getCurrency();
		String basePrice = String.valueOf(decimalFormat.format(Double
				.parseDouble(bill.getBasePrice())));
		String basePricetmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(basePrice)));
		String totalTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(bill.getTotal())));
		String distCostTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(bill.getDistanceCost())));
		String timeCost = String.valueOf(decimalFormat.format(Double
				.parseDouble(bill.getTimeCost())));
		String primary_amount = String.valueOf(decimalFormat.format(Double
				.parseDouble(bill.getPrimary_amount())));
		String secoundry_amount = String.valueOf(decimalFormat.format(Double
				.parseDouble(bill.getSecoundry_amount())));
		String discounts = String.valueOf(decimalFormat.format(Math.abs((Double
				.parseDouble(primary_amount) + Double
				.parseDouble(secoundry_amount))
				- (Double.parseDouble(totalTmp)))));

		((TextView) mDialog.findViewById(R.id.tvBasePrice)).setText(currency
				+ " " + basePrice);
		if (distCostTmp.equals("0.00")) {
			((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile))
					.setText(currency
							+ "0 "
							+ getResources().getString(
									R.string.text_cost_per_mile));
		} else
			((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile))
					.setText(currency

							+ String.valueOf(perHourFormat.format((Double
									.parseDouble(bill.getDistanceCost()) / Double
									.parseDouble(bill.getDistance()))))
							+ " "
							+ getResources().getString(
									R.string.text_cost_per_mile));
		if (timeCost.equals("0.00")) {
			((TextView) mDialog.findViewById(R.id.tvBillTimePerHour))
					.setText(currency
							+ "0 "
							+ getResources().getString(
									R.string.text_cost_per_min));
		} else
			((TextView) mDialog.findViewById(R.id.tvBillTimePerHour))
					.setText(currency
							+ String.valueOf(perHourFormat.format((Double
									.parseDouble(timeCost) / Double
									.parseDouble(bill.getTime()))))
							+ " "
							+ getResources().getString(
									R.string.text_cost_per_min));

		((TextView) mDialog.findViewById(R.id.adminCost))
				.setText(getResources().getString(R.string.text_cost_for_admin)
						+ " :    " + currency + " " + secoundry_amount);

		((TextView) mDialog.findViewById(R.id.providercost))
				.setText(getResources().getString(
						R.string.text_cost_for_provider)
						+ " : " + currency + " " + primary_amount);

		((TextView) mDialog.findViewById(R.id.discounts))
				.setText(getResources().getString(R.string.text_discount)
						+ " :     " + currency + " " + discounts);

		((TextView) mDialog.findViewById(R.id.tvDis1)).setText(currency + " "
				+ distCostTmp);

		((TextView) mDialog.findViewById(R.id.tvTime1)).setText(currency + " "
				+ timeCost);

		((TextView) mDialog.findViewById(R.id.tvTotal1)).setText(currency + " "
				+ totalTmp);

		Button btnConfirm = (Button) mDialog
				.findViewById(R.id.btnBillDialogClose);

		btnConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
			}
		});

		mDialog.setCancelable(true);
		//mDialog.show();

		if (requestDetail.getClientProfile() != null)
			aQuery.id(ivDriverImage).image(requestDetail.getClientProfile());
		// tvTime.setText(getArguments().getString(AndyConstants.Params.TIME));
		// tvDistance.setText(getArguments().getString(
		// AndyConstants.Params.DISTANCE));

		tvTime.setText((int) (Double.parseDouble(requestDetail.getTime()))
				+ " " + getString(R.string.text_mins));
		tvDistance.setText(new DecimalFormat("0.00").format(Double
				.parseDouble(requestDetail.getDistance()))
				+ " "
				+ requestDetail.getUnit());
		tvClientName.setText(requestDetail.getClientName());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tvFeedbackSubmit:

		/*	if (TextUtils.isEmpty(etFeedbackComment.getText().toString())) {
				AndyUtils.showToast(
						mapActivity.getResources().getString(
								R.string.text_empty_feedback), mapActivity);
				return;
			} else {*/
				giveRating();
		//	}
			break;

		case R.id.tvFeedbackskip:
			preferenceHelper.clearRequestData();
			mapActivity.addFragment(new ClientRequestFragment(), false,
					AndyConstants.CLIENT_REQUEST_TAG, true);

		default:
			break;
		}
	}

	// giving feedback for perticular job
	private void giveRating() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}

		AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
				.getString(R.string.progress_rating), false);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, AndyConstants.ServiceType.RATING);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		map.put(AndyConstants.Params.REQUEST_ID,
				String.valueOf(preferenceHelper.getRequestId()));
		map.put(AndyConstants.Params.RATING,
				String.valueOf(ratingFeedback.getNumStars()));
		map.put(AndyConstants.Params.COMMENT, etFeedbackComment.getText()
				.toString().trim());

		new HttpRequester(mapActivity, map, AndyConstants.ServiceCode.RATING,
				this);
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		AndyUtils.removeCustomProgressDialog();
		switch (serviceCode) {
		case AndyConstants.ServiceCode.RATING:
			AppLog.Log(TAG, "rating response" + response);
			if (parseContent.isSuccess(response)) {
				preferenceHelper.clearRequestData();
				AndyUtils.showToast(
						mapActivity.getResources().getString(
								R.string.toast_feedback_success), mapActivity);
				mapActivity.addFragment(new ClientRequestFragment(), false,
						AndyConstants.CLIENT_REQUEST_TAG, true);
			}

			break;

		default:
			break;
		}
	}
}
