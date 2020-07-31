package com.example.dev.techbuck;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dev.techbuck.Adapters.RvAnswerAdapter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class AddAnswer extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 123;
    //Bitmap to get image from gallery
    private Bitmap bitmap;
    String path;
    boolean image_status = false;
    //Uri to store the image uri
    private Uri filePath;
    EditText edans;
    Button ansimportimage, anspost;
    ImageView addansimageview;
    String URL_username = com.example.dev.techbuck.URL.url + "/getusername.php";
    String URL_AnswithImage = com.example.dev.techbuck.URL.url + "/addanswerwithimage.php";
    String URL_Ans = com.example.dev.techbuck.URL.url + "/addanswer.php";
    String URL_usernameimage = com.example.dev.techbuck.URL.url + "/getuserimage.php";
    String METHOD = "add";
    String username, usernameimage;
    Session session;
    ArrayList<String> key, value;
    CallServices cs;
    ImageButton ansmic;
    AlertDialog al;
    String qid;
    private static final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_answer);
        init();
        Intent i = getIntent();
        qid = i.getStringExtra("qid");


        //get username
        ansimportimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionManager.checkStoragePermission(AddAnswer.this)) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(AddAnswer.this);
                }
            }
        });
        ansmic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceInput(savedInstanceState);
            }
        });
    }

    public void processTOloaddata() {
        key.add("email");
        value.add(session.preferences.getString("unm", ""));
        username = cs.CallServices(AddAnswer.this, URL_username, METHOD, key, value);
        usernameimage = cs.CallServices(AddAnswer.this, URL_usernameimage, METHOD, key, value);
        anspost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edans.getText().toString().trim().length() <= 0) {
                    al = new AlertDialog.Builder(AddAnswer.this)
                            .setTitle("Empty Answer")
                            .setMessage("Can't Enter Empty Answer")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    al.cancel();
                                }
                            }).show();
                } else {
                    if (!image_status) {
                        key.clear();
                        value.clear();
                        key.add("answer");
                        value.add(edans.getText().toString());
                        key.add("email");
                        value.add(session.preferences.getString("unm", ""));
                        key.add("unm");
                        value.add(username);
                        key.add("qid");
                        value.add(qid);
                        key.add("usernameimage");
                        value.add(usernameimage);
                        String res = cs.CallServices(AddAnswer.this, URL_Ans, METHOD, key, value);
                        Toast.makeText(AddAnswer.this, "Answer Added", Toast.LENGTH_SHORT).show();
                        RvAnswerAdapter adapter = new RvAnswerAdapter();
                        adapter.notifyDataSetChanged();


                        Intent intent = new Intent(AddAnswer.this, Answer.class);
                        intent.putExtra("qid", qid);
                        setResult(20008, intent);
                        finish();
                    } else
                        uploadMultipart();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddAnswer.this, Answer.class);
        intent.putExtra("qid", qid);
        setResult(20008, intent);
        finish();

    }

    private void init() {
        edans = findViewById(R.id.edans);
        ansimportimage = findViewById(R.id.ansimportimage);
        addansimageview = findViewById(R.id.addansimageview);
        anspost = findViewById(R.id.anspost);
        ansmic = findViewById(R.id.ansmic);
        session = new Session(getApplicationContext());
        cs = new CallServices();
        key = new ArrayList<>();
        value = new ArrayList<>();

        processTOloaddata();

    }

    private void startVoiceInput(Bundle saveInstanceState) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

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
                addansimageview.setImageBitmap(bitmap);
                addansimageview.setVisibility(View.VISIBLE);
                image_status = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (result.get(0).equalsIgnoreCase("clear answer"))
                        edans.setText(null);
                    else
                        edans.append(result.get(0));
                }
                break;
            }

        }
        if (requestCode == 1) {
            init();
        }

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

            String res = new MultipartUploadRequest(this, uploadId, URL_AnswithImage)
                    .addParameter("email", session.preferences.getString("unm", ""))
                    .addParameter("unm", username)
                    .addParameter("qid", qid)
                    .addParameter("answer", edans.getText().toString())
                    .addParameter("userimage", usernameimage)
                    .addFileToUpload(path, "image") //Adding file
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
            Toast.makeText(AddAnswer.this, "Answer Added", Toast.LENGTH_SHORT).show();
            RvAnswerAdapter adapter = new RvAnswerAdapter();
            adapter.notifyDataSetChanged();

            finish();

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


}
