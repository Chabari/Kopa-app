package bororren.loan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import bororren.loan.Account.AccountSetting;
import bororren.loan.Loans.LoanStatus;
import bororren.loan.Loans.Loaning;

public class Home extends AppCompatActivity {
    private Button mOkoa,mStatus,mPay;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);
        toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);



        MobileAds.initialize(this, "ca-app-pub-7987686181413584~8365184101");


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7987686181413584/2233167827");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());



        mAdView =(AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mOkoa = (Button)findViewById(R.id.btn_okoa);
        mStatus = (Button)findViewById(R.id.btn_status);
        mPay = (Button)findViewById(R.id.btn_payment);


        mOkoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

                startActivity(new Intent(Home.this, Loaning.class));


            }
        });



        checkingLoanPayment(mPay);

        mStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, LoanStatus.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (!task.getResult().exists()){
                                startActivity(new Intent(Home.this, AccountSetting.class));
                            }
                        }
                    }
                });

    }
    private void checkingDetails(final Button button){

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Loading...");
                progressDialog.show();


                firebaseFirestore.collection("Users").document(user_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                if (!task.getResult().exists()){

                                    new AlertDialog.Builder(Home.this)
                                            .setMessage("Your have not registered,please restart your application")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).show();

                                }else {
                                    firebaseFirestore.collection("Users").document(user_id).collection("MyLoan").document("Loan")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        if (!task.getResult().exists()){


                                                            startActivity(new Intent(Home.this, Loaning.class));

                                                        }else {
                                                            new AlertDialog.Builder(Home.this)
                                                                    .setTitle("Loan Processing")
                                                                    .setMessage("Your loan request is being processed")
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
                                progressDialog.dismiss();
                            }
                        }
                    });


            }
        });

    }

    private void checkingLoanPayment(final Button button){

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Loading...");
                progressDialog.show();
                firebaseFirestore.collection("Users").document(user_id).collection("MyLoan").document("Loan")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                if (!task.getResult().exists()){
                                    new AlertDialog.Builder(Home.this)
                                            .setMessage("Your do not have an outstanding loan")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).show();
                                }else {
                                    new AlertDialog.Builder(Home.this)
                                            .setTitle("Loan Processing")
                                            .setMessage("Your loan request is being processed")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).show();
                                }
                                progressDialog.dismiss();
                            }
                        }
                    });

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.logout){
            new AlertDialog.Builder(Home.this)
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            auth.signOut();
                            finish();
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();

        }
        return super.onOptionsItemSelected(item);
    }
}
