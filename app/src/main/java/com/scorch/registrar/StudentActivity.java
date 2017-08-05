package com.scorch.registrar;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentActivity extends AppCompatActivity implements View.OnClickListener{
Student student;
    private Context context;
CollapsingToolbarLayout collapsingToolbarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
if(getIntent().hasExtra("student")){
    student=getIntent().getParcelableExtra("student");
}
context=this;
        findViewById(R.id.call_student).setOnClickListener(this);
        findViewById(R.id.msg_student).setOnClickListener(this);

        de.hdodenhof.circleimageview.CircleImageView studentImage= (CircleImageView) findViewById(R.id.student_image);
        if(!student.imgUrl.equalsIgnoreCase(""))
        Picasso.with(context).load(student.imgUrl).into(studentImage);
        studentImage.setOnClickListener(this);
        TextView stuName= (TextView) findViewById(R.id.student_name);
        stuName.setText(student.getName());
        collapsingToolbarLayout= (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        TextView idText= (TextView) findViewById(R.id.id_text);
        TextView nameText= (TextView) findViewById(R.id.name_text);
        TextView addressText= (TextView) findViewById(R.id.address_text);
        TextView phoneText= (TextView) findViewById(R.id.phone_text);
        TextView subjectText= (TextView) findViewById(R.id.subject_text);
        TextView emailText= (TextView) findViewById(R.id.email_text);
emailText.setText(String.format("Student Email: %s",student.getEmail()));
        nameText.setText(String.format("Student Name: %s",student.getName()));
        idText.setText(String.format("Student ID: %s",student.getStuID()));
        phoneText.setText(String.format("Student Phone #: %s",student.getPhone()));
        addressText.setText(String.format("Student Address: %s",student.getAddress()));
        String substext = student.subjects.toString().replace("[", "").replace("]", "");

        subjectText.setText(String.format("Subjects: %s",substext));
        ImageView qrCode= (ImageView) findViewById(R.id.qr_code);
        qrCode.setImageBitmap(RegisterActivity.qr);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    setupToolBar();
    }
    private void getPic(){
        View popup = LayoutInflater.from(context).inflate(R.layout.picture_layout, null, false);
ImageView imageView= (ImageView) popup.findViewById(R.id.big_pic);
        Picasso.with(context).load(student.imgUrl).fit().into(imageView);
        ContextThemeWrapper mTheme = new ContextThemeWrapper(this,
                R.style.YOUR_STYE);
        final Dialog mDialog = new Dialog(mTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(popup);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;


        mDialog.getWindow().setAttributes(lp);
        mDialog.show();
    }
    private void setupToolBar() {

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            //actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(student.getName());
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.student_image:
                getPic();
                break;
            case R.id.call_student:
                String url = "tel:"+student.getPhone();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.msg_student:

                makeMessageDialog();
                break;

        }
    }
    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

 void makeMessageDialog(){
    View popup = LayoutInflater.from(context).inflate(R.layout.make_post_layout, null, false);
    final Dialog mDialog;
    LayoutInflater mInflater = (LayoutInflater) getBaseContext().getSystemService(
            LAYOUT_INFLATER_SERVICE);
    ContextThemeWrapper mTheme = new ContextThemeWrapper(this,
            R.style.YOUR_STYE);

    // mDialog = new Dialog(this,0); // context, theme

    mDialog = new Dialog(mTheme);
    mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    mDialog.setContentView(popup);
    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    mDialog.show();

    final MaterialEditText editText = (MaterialEditText) popup.findViewById(R.id.post_text);
    TextView ok = (TextView) popup.findViewById(R.id.post_button);
    ok.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String post = String.valueOf(editText.getText());
sendSMS(student.getPhone(),post);
            mDialog.dismiss();

            }

    });

}
}
