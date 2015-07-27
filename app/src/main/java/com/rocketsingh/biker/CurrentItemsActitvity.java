package com.rocketsingh.biker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rocketsingh.biker.base.ActionBarBaseActivitiy;
import com.rocketsingh.biker.parse.AsyncTaskCompleteListener;
import com.rocketsingh.biker.utills.PreferenceHelper;

/**
 * Created by (-_-) on 11-07-2015.
 */
public class CurrentItemsActitvity extends ActionBarBaseActivitiy implements AdapterView.OnItemClickListener,AsyncTaskCompleteListener{

    public Button call_customer_btn;
    public  ImageView tv_empty_itemDetails;
    public  CardView tv_item_itemDetails;
    public TextView tv_item_1,tv_item_2,tv_item_3,tv_item_4,tv_item_5;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentitems);

        PreferenceHelper preferenceHelper=new PreferenceHelper(getApplicationContext());

        tv_item_1 = (TextView)findViewById(R.id.card_tv_Customername);
        tv_item_2 = (TextView)findViewById(R.id.card_tv_Amount);
        tv_item_3 = (TextView)findViewById(R.id.card_tv_phonenno);
        tv_item_4 = (TextView)findViewById(R.id.card_tv_address);
        tv_item_5 = (TextView)findViewById(R.id.card_tv_locality);

        tv_empty_itemDetails = (ImageView)findViewById(R.id.tvItemEmpty);
        tv_item_itemDetails = (CardView)findViewById(R.id.card_itemDetails);
        call_customer_btn = (Button)findViewById(R.id.call_customer_btn);
        tv_item_itemDetails.setVisibility(View.GONE);

        String item_desc = preferenceHelper.getCustomerName();
        String amount = preferenceHelper.getCustomerAmount();
        final String phone = preferenceHelper.getCustomerPhoneNo();
        String address = preferenceHelper.getCustomerAddress();
        String locality = preferenceHelper.getCustomerLocality();

        call_customer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"
                        + phone));
                startActivity(callIntent);
            }
        });


        if(item_desc != null && !item_desc.isEmpty() ){
            tv_empty_itemDetails.setVisibility(View.GONE);
            tv_item_itemDetails.setVisibility(View.VISIBLE);
            tv_item_1.setText(item_desc);
            tv_item_2.setText(amount);
            tv_item_3.setText(phone);
            tv_item_4.setText(address);
            tv_item_5.setText(locality);
        }
        else
        {
            tv_empty_itemDetails.setVisibility(View.VISIBLE);
            tv_item_itemDetails.setVisibility(View.GONE);
        }
        setActionBarTitle(getString(R.string.text_currentdetails));
        setActionBarIcon(R.drawable.nav_about);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnActionNotification:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
                break;

            default:
                break;
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CurrentItemsActitvity.this, MapActivity.class));
    }
}
