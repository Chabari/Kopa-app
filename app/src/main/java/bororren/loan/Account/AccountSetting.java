package bororren.loan.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import bororren.loan.Loans.Approvals;
import bororren.loan.R;

import java.util.HashMap;
import java.util.Map;

public class AccountSetting extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private TextInputEditText mFullname,mID,mPhone;
    private Button mSubmit;
    private String User_Id;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        MobileAds.initialize(this, "ca-app-pub-7987686181413584~8365184101");

        mAdView =(AdView) findViewById(R.id.adView_accountsetting);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        User_Id = auth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);

        mSubmit = (Button)findViewById(R.id.btn_submit);
        mFullname = (TextInputEditText)findViewById(R.id.edt_fullname);
        mID = (TextInputEditText)findViewById(R.id.edt_idnumber);
        mPhone = (TextInputEditText)findViewById(R.id.edt_phonenumber);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checking the fields
                final String fullname = mFullname.getText().toString().trim();
                final String phone = mPhone.getText().toString().trim();
                final String id = mID.getText().toString().trim();
                if (TextUtils.isEmpty(fullname))
                {
                    mFullname.setError("Enter name");
                }else if (TextUtils.isEmpty(phone) || phone.length()<10 || phone.length()>10)
                {
                    mPhone.setError("Enter a valid phone number");
                }if (TextUtils.isEmpty(id) || id.length()>8)
                {
                    mID.setError("Enter valid id number");
                }else
                {
                    progressDialog.setMessage("Processing your details  please wait, this won't take long....");
                    submitingDetails(User_Id,fullname,phone,id);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            submitingDetails(User_Id,fullname,phone,id);
                        }
                    },20000);
                }
            }
        });




    }

    private void submitingDetails(String userid, final String fullname, String phome, String id){
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("fullname",fullname);
        stringStringMap.put("phone",phome);
        stringStringMap.put("id",id);

        firebaseFirestore.collection("Users").document(userid).set(stringStringMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AccountSetting.this, "Details submitted successfully ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AccountSetting.this, Approvals.class);
                    intent.putExtra("fullname",fullname);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(AccountSetting.this, "Something went wrong check your internet connection", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}
