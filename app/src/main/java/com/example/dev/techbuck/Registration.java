package com.example.dev.techbuck;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registration extends AppCompatActivity {

    EditText name, email, pw;
    Button submit;
    ImageButton importimage;
    ArrayList<String> key;
    ArrayList<String> value;
    CircleImageView image;
    //Image request code
    private int PICK_IMAGE_REQUEST = 1;
    Boolean status = false;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    public String UPLOAD_URL = com.example.dev.techbuck.URL.url + "/registration2.php";
    public static String URL = com.example.dev.techbuck.URL.url + "/user_register.php";

    public static String METHOD = "add";
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        name = findViewById(R.id.unm);
        email = findViewById(R.id.ed_email);
        importimage = findViewById(R.id.importimage);
        image = findViewById(R.id.image);
        pw = findViewById(R.id.pw);
        submit = findViewById(R.id.submit);
        key = new ArrayList<>();
        value = new ArrayList<>();


        //import profile image
        importimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionManager.checkStoragePermission(Registration.this)) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(Registration.this);
                }
            }
        });

        //Registeration
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name1 = name.getText().toString();
                String email1 = email.getText().toString();
                String pw1 = pw.getText().toString();
                if (!email1.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                    email.setError("Invalid Email Address");
                } else {


                    key.add("name");
                    value.add(name1);
                    key.add("email");
                    value.add(email1);
                    key.add("pw");
                    value.add(pw1);
                    CallServices cs = new CallServices();
                    String res = cs.CallServices(Registration.this, URL, METHOD, key, value);
                    Log.e("error===>", res);

                    if (res.trim().equals("0")) {
                        Toast.makeText(Registration.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        Session session = new Session(getApplicationContext());
                        session.setLogin(email1);
                        session.setPassword(pw1);
                        if (status)
                            uploadMultipart();
                        startActivity(new Intent(getApplicationContext(), Home.class));


                    } else if (res.trim().equals("1"))
                        Toast.makeText(Registration.this, "Email Allready Exist", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void uploadMultipart() {
        //getting name for the image

        Log.d("Filepath===>", String.valueOf(filePath));
        //getting the actual path of the image
        path = getPath(filePath);
        //Log.d("IMage Path===>", path);
        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request

            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addParameter("email", email.getText().toString())
                    .addFileToUpload(path, "image") //Adding file
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

        } catch (Exception exc) {
            Toast.makeText(this, "Error==" + exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getPath(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            filePath = result.getUri();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
                status = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

