package com.example.dev.techbuck;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    EditText profile_name, profile_email, profile_password;
    ImageButton btn_name, btn_email, btn_password, importprofileimage;
    String oldpw, M = "add";
    CircleImageView profile_image;
    Session session;
    ArrayList<String> key, value;
    CallServices cs;
    String URL_setimage = URL.url + "/uploads/";
    String URL_username = com.example.dev.techbuck.URL.url + "/getusername.php";
    String URL_userimage = com.example.dev.techbuck.URL.url + "/getuserimage.php";
    String URL_updateuserwithimage = com.example.dev.techbuck.URL.url + "/updateuserwithimage.php";
    String URL_updateuser = com.example.dev.techbuck.URL.url + "/userupdate.php";
    String oldusername, olduserimage;
    private Uri filePath;
    Button btn_update;
    Boolean image_status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        init();
        key.add("email");
        value.add(session.preferences.getString("unm", ""));

        oldusername = cs.CallServices(UserProfile.this, URL_username, M, key, value);
        olduserimage = cs.CallServices(UserProfile.this, URL_userimage, M, key, value);

        setProfile();
        Log.e("pw", session.getPassword());

        btn_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();

            }
        });
        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_email.setEnabled(true);
            }
        });
        btn_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_name.setEnabled(true);
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!image_status) {
                    key.clear();
                    value.clear();
                    key.add("email");
                    value.add(session.checkLogin());
                    key.add("newemail");
                    value.add(profile_email.getText().toString());
                    key.add("unm");
                    value.add(profile_name.getText().toString());
                    key.add("pw");
                    value.add(profile_password.getText().toString());
                    Log.e("pww",profile_password.getText().toString());
                    String r = cs.CallServices(UserProfile.this, URL_updateuser, M, key, value);
                    session.setLogin(profile_email.getText().toString());
                    session.setLogin(profile_password.getText().toString());
                    Log.e("rrrr", r);
                    if (r.trim().equals("0"))
                        Toast.makeText(UserProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(UserProfile.this, "Can't Update Profile", Toast.LENGTH_SHORT).show();
                } else {
                    uploadMultipart();
                }
            }
        });
        importprofileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionManager.checkStoragePermission(UserProfile.this)) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(UserProfile.this);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            filePath = result.getUri();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_image.setImageBitmap(bitmap);
                image_status = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void uploadMultipart() {
        //getting name for the image

        Log.d("Filepath===>", String.valueOf(filePath));
        //getting the actual path of the image
        String path = getPath(filePath);
        Log.d("IMage Path===>", path);
        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request

            String res = new MultipartUploadRequest(this, uploadId, URL_updateuserwithimage)
                    .addParameter("oldemail", session.preferences.getString("unm", ""))
                    .addParameter("pw", profile_password.getText().toString())
                    .addParameter("newemail", profile_email.getText().toString())
                    .addParameter("username", profile_name.getText().toString())
                    .addFileToUpload(path, "image") //Adding file
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
            Log.e("resssssss", res);
            if (res.trim().equals("0")){
                session.setLogin(profile_email.getText().toString());
                session.setLogin(profile_password.getText().toString());
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }


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

    private void setProfile() {

        profile_name.setText(oldusername);
        profile_email.setText(session.preferences.getString("unm", ""));
        profile_password.setText(session.getPassword());
        if (olduserimage.trim().length() > 0)
            Glide.with(getApplicationContext()).load(URL_setimage + olduserimage).into(profile_image);
    }

    private void init() {
        profile_email = findViewById(R.id.profile_email);
        profile_name = findViewById(R.id.profile_name);
        profile_password = findViewById(R.id.profile_password);
        btn_email = findViewById(R.id.btn_email);
        btn_name = findViewById(R.id.btn_name);
        btn_password = findViewById(R.id.btn_password);
        profile_image = findViewById(R.id.profile_image);
        importprofileimage = findViewById(R.id.importprofileimage);
        btn_update = findViewById(R.id.btn_update);
        session = new Session(UserProfile.this);
        key = new ArrayList<>();
        value = new ArrayList<>();
        cs = new CallServices();
    }

    private void openDialog() {
        final Dialog d = new Dialog(UserProfile.this);
        d.setContentView(R.layout.userpassword_dialog);
        final EditText ed = d.findViewById(R.id.dialog_pw);
        Button dialog_ok = d.findViewById(R.id.dialog_ok);
        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldpw = ed.getText().toString();
                if (oldpw.equals(session.getPassword())) {
                    profile_password.setEnabled(true);
                    d.cancel();
                } else
                    Toast.makeText(UserProfile.this, "Opps Wrong Password", Toast.LENGTH_SHORT).show();

            }
        });
        d.show();
    }
}
