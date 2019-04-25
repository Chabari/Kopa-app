package bororren.loan.Loans;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import bororren.loan.R;

public class Loaning extends AppCompatActivity {
    private FloatingActionButton next;
    private EditText reason_how;
    private TextInputEditText reason_why;
    private Spinner types;
    private RadioButton yes,no;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loaning);
        next = (FloatingActionButton)findViewById(R.id.flaot_fab_next);
        reason_how = (EditText)findViewById(R.id.edt_reason_how);
        reason_why = (TextInputEditText)findViewById(R.id.edt_reason_loan);
        types = (Spinner)findViewById(R.id.sp_loantype);
        yes = (RadioButton)findViewById(R.id.radio_yes);
        no = (RadioButton)findViewById(R.id.radio_no);

        MobileAds.initialize(this,"ca-app-pub-7987686181413584~8365184101");


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7987686181413584/2233167827");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mAdView =(AdView) findViewById(R.id.adView_loaning);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        types.setSelection(0);

        next.setOnClickListener(new View.OnClickListener() {
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

    private void updateUI(){

        if (types.getSelectedItemPosition() == 0){
            Toast.makeText(Loaning.this, "Select loan type", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(reason_why.getText().toString().trim())){
            Toast.makeText(Loaning.this, "Enter reason for the above loan type", Toast.LENGTH_SHORT).show();
        }else if (!yes.isChecked() && !no.isChecked()){
            Toast.makeText(Loaning.this, "Specify if you ever borrowed money from any creditor ", Toast.LENGTH_SHORT).show();
        }else  if (TextUtils.isEmpty(reason_how.getText().toString().trim())&&yes.isChecked()){
            Toast.makeText(Loaning.this, "Enter reason why you took the loan", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(Loaning.this);
            progressDialog.setMessage("Processing...");
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    startActivity(new Intent(Loaning.this,SecondLoaning.class));
                }
            },2000);
        }
    }
}
