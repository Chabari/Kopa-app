package com.kopabranch.Loans;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kopabranch.R;

public class LoanStatus extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private TextView mStatus;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private String user_id;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_status);

        MobileAds.initialize(this, "ca-app-pub-7987686181413584~3431200430");

        mAdView =(AdView) findViewById(R.id.adView_loanstatus);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore =FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading loan status...");
        progressDialog.show();

        mStatus = (TextView)findViewById(R.id.tv_loanstatus);

        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                final String fullname = task.getResult().getString("fullname");firebaseFirestore.collection("Users").document(user_id).collection("MyLoan").document("Loan")
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if (task.isSuccessful()){
                                                    if (task.getResult().exists()){
                                                        String amount = task.getResult().getString("amount");
                                                        String amounttopay = task.getResult().getString("amounttopay");
                                                        mStatus.setText(fullname+" your loan KSH "+amount+" is being processed.\n\nLoan Details:\nAmount Requested: KSH "+amount+"\nAmount To Pay: KSH "+amounttopay);

                                                        mStatus.setVisibility(View.VISIBLE);
                                                    }
                                                }else {
                                                    mStatus.setText("No outstanding loan");
                                                    mStatus.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });

        //lying here
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                mStatus.setVisibility(View.VISIBLE);
            }
        },2000);



    }
}
