package com.scorch.registrar;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.jksiezni.permissive.PermissionsGrantedListener;
import com.github.jksiezni.permissive.Permissive;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.scheme.VCard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import gun0912.tedbottompicker.TedBottomPicker;
import me.riddhimanadib.formmaster.helper.FormBuildHelper;
import me.riddhimanadib.formmaster.model.FormElement;
import me.riddhimanadib.formmaster.model.FormHeader;
import me.riddhimanadib.formmaster.model.FormObject;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView mRecyclerView;
    private FormBuildHelper mFormBuilder;
    private Context context;
    private StorageReference mStorageRef;
String imgurl="";
    public static Bitmap qr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // initialize variables
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mFormBuilder = new FormBuildHelper(this, mRecyclerView);
context=this;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        findViewById(R.id.submit_button).setOnClickListener(this);
// declare form elements
        FormHeader header = FormHeader.createInstance().setTitle("Student Info");
        FormElement element = FormElement.createInstance().setTag(4).setType(FormElement.TYPE_EDITTEXT_EMAIL).setTitle("Email");
// phone number input
        FormElement phone = FormElement.createInstance().setTag(3).setType(FormElement.TYPE_EDITTEXT_PHONE).setTitle("Phone");
        FormElement address = FormElement.createInstance().setTag(2).setType(FormElement.TYPE_EDITTEXT_TEXT_MULTILINE).setTitle("Address");
        FormElement name = FormElement.createInstance()
                .setType(FormElement.TYPE_EDITTEXT_TEXT_SINGLELINE).setTag(1).setTitle("Name");
// multiple items picker input
        List<String> fruits = new ArrayList<>();
        fruits.add("English");
        fruits.add("Math");
        fruits.add("Information Technology");
        fruits.add("Social Studies");
        FormElement subjects = FormElement.createInstance().setTag(555).setType(FormElement.TYPE_PICKER_MULTI_CHECKBOX).setTitle("Subjects").setOptions(fruits);
// add them in a list
        List<FormObject> formItems = new ArrayList<>();
        formItems.add(header);
        formItems.add(name);
        formItems.add(element);
        formItems.add(phone);
        formItems.add(address);
        formItems.add(subjects);

// build and display the form
        mFormBuilder.addFormElements(formItems);
        mFormBuilder.refreshView();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_add_photo:
                new Permissive.Request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .whenPermissionsGranted(new PermissionsGrantedListener() {
                            @Override
                            public void onPermissionsGranted(String[] permissions) throws SecurityException {
                                addPhoto();
                            }
                        }).execute((Activity) context);                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            //imgurl=resultUri.getPath();

            //final StorageReference storageRef = storage.getReferenceFromUrl(getString(R.string.photo_bucket)).child("Place Images/" + pID);


            final StorageReference photoRef = mStorageRef.child("Student Images/" + resultUri.getLastPathSegment());
            UploadTask uploadTask = photoRef.putFile(resultUri);
uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imgurl=uri.toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
});
// Register observers to listen for when the download is done or if it fails

        } else if (resultCode == UCrop.RESULT_ERROR) {

            final Throwable cropError = UCrop.getError(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void addPhoto() {
        final UCrop.Options options = new UCrop.Options();
        options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setMaxBitmapSize(1024);
        options.setCompressionQuality(80);
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(RegisterActivity.this)
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {

                        try {
                            //Uri tmp = Uri.fromFile(File.createTempFile("placeimg", ".jpg", getExternalCacheDir()));
                            File newFile = File.createTempFile("stuimg", ".jpg", getExternalCacheDir());

                            Uri tmp = Uri.fromFile(newFile);
                            UCrop.of(uri, tmp)
                                    .withOptions(options)

                                    .withMaxResultSize(1024, 1024)
                                    .start((Activity) context);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // here is selected uri
                    }
                })
                .showCameraTile(true)

                .setOnErrorListener(new TedBottomPicker.OnErrorListener() {
                    @Override
                    public void onError(String message) {
                        Log.e("Photo ", "Photo error: " + message);

                    }
                })
                .create();

        tedBottomPicker.show(getSupportFragmentManager());

    }
private void createStudent(){
// Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("Students");


/*
    FormElement nameElement = mFormBuilder.getFormElement(444);
    FormElement phoneElement = mFormBuilder.getFormElement(444);
    FormElement addElement = mFormBuilder.getFormElement(444);
*/

final List<String>info=new ArrayList<>();
    new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... voids) {
            for(int i=1;i<5;i++){
                FormElement tElement = mFormBuilder.getFormElement(i);
                info.add(tElement.getValue());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String stuID= UUID.randomUUID().toString();
            FormElement targetElement = mFormBuilder.getFormElement(555);
            List<String> subjects=new ArrayList<String>();
            String s=targetElement.getValue();
            subjects = new ArrayList<String>(Arrays.asList(s.split(",")));

            //subjects.addAll(targetElement.getOptionsSelected());
            Student student=new Student(stuID,info.get(0),info.get(1),info.get(2),info.get(3),subjects,imgurl);
            Log.d("Student","The new Student has been created"+targetElement.getValue());
            myRef.child(stuID).setValue(student);

            qr = QRCode.from(stuID).bitmap();
startActivity(new Intent(context,StudentActivity.class).putExtra("student",student));
            //ImageView myImage = (ImageView) findViewById(R.id.imageView);
            //myImage.setImageBitmap(myBitmap);
            super.onPostExecute(aVoid);
        }
    }.execute();

}
private void makeQR(Student student){
    VCard studentCard = new VCard(student.name)
            .setEmail(student.email)
            .setAddress(student.address)

            .setCompany("John Doe Inc.")
            .setPhoneNumber("1234")
            .setWebsite("www.example.org");
    QRCode.from(studentCard).file();
}
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.submit_button:
                createStudent();
                break;
        }
    }
}
