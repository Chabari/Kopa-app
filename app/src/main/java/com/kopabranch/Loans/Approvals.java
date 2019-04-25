package com.kopabranch.Loans;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.kopabranch.Home;
import com.kopabranch.R;

public class Approvals extends AppCompatActivity {
    private TextView response;
    private String responSe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approvals);

        responSe =getIntent().getExtras().getString("fullname");
        response = (TextView)findViewById(R.id.tv_response);

        response.setText("Hi? "+responSe+" your application details have been successfully approved you can now proceed with loan application.");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(Approvals.this, Home.class));
    }
}
