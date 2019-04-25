package com.kopabranch.Account;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kopabranch.Home;
import com.kopabranch.Loans.Loaning;
import com.kopabranch.R;

public class AccountCreation extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText mPhone;
    private Button mContinue;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);
        MobileAds.initialize(this, "ca-app-pub-7987686181413584~3431200430");


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7987686181413584/4407286477");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mAdView =(AdView) findViewById(R.id.adView_account);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        auth = FirebaseAuth.getInstance();

        mPhone=(EditText)findViewById(R.id.phone_number);
        mContinue=(Button)findViewById(R.id.btn_continue);


        //setting country code
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }



            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                updateUI();
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                updateUI();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser!=null){
            startActivity(new Intent(AccountCreation.this,Home.class));
            finish();
        }
    }

    private void updateUI(){
        final String mobile = mPhone.getText().toString().trim();


        if(mobile.isEmpty() || mobile.length() < 10 ||  mobile.length()>10){
            mPhone.setError("Enter a valid mobile number");
            mPhone.requestFocus();
        }
        else {

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AccountCreation.this);
            builder.setTitle("Account Creation...");
            builder.setMessage("Is this the number you want to create account with\n+254" + mobile + "?");
// Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    Intent intent = new Intent(AccountCreation.this, Verification.class);
                    intent.putExtra("mobile", mobile);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            android.app.AlertDialog dialog = builder.create();
            dialog.show();
        }
    }



}
