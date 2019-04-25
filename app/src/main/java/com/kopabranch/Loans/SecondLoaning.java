package com.kopabranch.Loans;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kopabranch.Home;
import com.kopabranch.R;

import org.apache.http.util.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class SecondLoaning extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private EditText mAmount,mDays;
    private Button mSubmit;
    private RadioButton no,yes;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private String user_id;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_loaning);




        MobileAds.initialize(this,"ca-app-pub-7987686181413584~3431200430");

        mAdView =(AdView) findViewById(R.id.adView_secondloaning);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);

        mAmount = (EditText)findViewById(R.id.edt_amount);
        mDays = (EditText)findViewById(R.id.edt_period);
        mSubmit = (Button)findViewById(R.id.btn_submit_second);
        yes = (RadioButton)findViewById(R.id.radio_yes_second);
        no =(RadioButton)findViewById(R.id.radio_no_second);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String amount = mAmount.getText().toString().trim();
                final String period = mDays.getText().toString().trim();
                int Amount=0;
                int Period=0;

                //integer coversion
                if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(period))
                {


                    if (TextUtils.isEmpty(amount)) {
                        Toast.makeText(SecondLoaning.this, "Enter amount to loan", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(period)) {
                        Toast.makeText(SecondLoaning.this, "Enter payment period", Toast.LENGTH_SHORT).show();


                    }



                }else {
                    Amount = Integer.parseInt(amount);
                    Period = Integer.parseInt(period);

                    if (!yes.isChecked() && !no.isChecked()) {
                        Toast.makeText(SecondLoaning.this, "Specify if you have any outstanding loan", Toast.LENGTH_SHORT).show();
                    } else if (Amount > 3500 || Amount < 500) {
                        Toast.makeText(SecondLoaning.this, "Enter amount within the specified loan limit ", Toast.LENGTH_SHORT).show();
                    } else if (Period > 30 || Period < 2) {
                        Toast.makeText(SecondLoaning.this, "Enter the specified payment period range", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        final int finalAmount = Amount;
                        firebaseFirestore.collection("Users").document(user_id).collection("MyLoan").document("Loan")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            if (!task.getResult().exists()){


                                                progressDialog.setMessage("Submitting your loan request");
                                                progressDialog.show();
                                                float TotalAmountToPay = (float) ((finalAmount * 0.1) + finalAmount);
                                                Map<String, Object> objectMap = new HashMap<>();
                                                objectMap.put("amount", amount);
                                                objectMap.put("period", period);
                                                objectMap.put("amounttopay", ""+TotalAmountToPay);

                                                firebaseFirestore.collection("Users").document(user_id).collection("MyLoan").document("Loan").set(objectMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    new AlertDialog.Builder(SecondLoaning.this)
                                                                            .setTitle("Success")
                                                                            .setMessage("Your loan request has been submitted successfully. Please wait for your loan disbursement within 8hrs")
                                                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                                    dialogInterface.dismiss();
                                                                                    Dialog dialog = new Dialog(SecondLoaning.this);
                                                                                    dialog.setTitle("Rate Us");
                                                                                    dialog.setContentView(R.layout.dialog_rateus);
                                                                                    dialog.setCancelable(true);

                                                                                    Button button = (Button) dialog.findViewById(R.id.btn_ratus);
                                                                                    button.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View view) {
                                                                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=KopaBranch")));
                                                                                            finish();
                                                                                        }
                                                                                    });
                                                                                    dialog.show();
                                                                                }
                                                                            })
                                                                            .show();
                                                                }
                                                                progressDialog.dismiss();
                                                            }
                                                        });

                                            }else {
                                                new AlertDialog.Builder(SecondLoaning.this)
                                                        .setTitle("Loan Processing")
                                                        .setMessage("You have an outstanding loan")
                                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                dialogInterface.dismiss();
                                                            }
                                                        }).show();
                                            }
                                        }
                                    }
                                });




                    }
                }
            }
        });

    }

}
