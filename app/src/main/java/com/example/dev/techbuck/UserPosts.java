package com.example.dev.techbuck;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dev.techbuck.Adapters.RvQuestionDesignAdapter;
import com.example.dev.techbuck.Bean.QuestionBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class UserPosts extends AppCompatActivity {

    CallServices cs;
    ArrayList<String> key, value;
    String M = "m";
    RecyclerView userpost_rv;
    Session session;
    ArrayList<QuestionBean> bean;
    RvQuestionDesignAdapter ac;
    String URL = com.example.dev.techbuck.URL.url + "/getuserpost.php";
    FloatingActionButton postflotingbtn;
    TextView nopost;
    String URL_userimage = com.example.dev.techbuck.URL.url + "/uploads/";
    String URL_queimage = com.example.dev.techbuck.URL.url + "/questionimage/";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        key = new ArrayList<>();
        value = new ArrayList<>();
        session = new Session(getApplicationContext());
        bean = new ArrayList<>();
        userpost_rv = findViewById(R.id.userpost_rv);
        postflotingbtn = findViewById(R.id.postflotingbtn);
        nopost = findViewById(R.id.nopost);
        cs = new CallServices();

        postflotingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserPosts.this,AddQuestion.class));
            }
        });

        key.add("email");
        value.add(session.preferences.getString("unm", ""));
        String res = cs.CallServices(getApplicationContext(), URL, M, key, value);
        Log.e("responce", res);
        try {
            JSONObject jsonObject = new JSONObject(res);
            JSONArray jsonArray = jsonObject.optJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String id = jsonObject1.getString("id");
                String unm = jsonObject1.getString("unm");
                String email = jsonObject1.getString("email");
                String date = jsonObject1.getString("date");
                String question = jsonObject1.getString("question");
                String image = jsonObject1.getString("image");
                String cat = jsonObject1.getString("cat");
                String userimage = jsonObject1.getString("userimage");
                QuestionBean questionBean = new QuestionBean(id, unm, email, date, question, image, cat, userimage);
                bean.add(questionBean);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Errrorrr=>", e.getLocalizedMessage());
        }
        Log.d("Sizee===>", bean.size() + "");
        if (bean.size() <= 0) {
            nopost.setVisibility(View.VISIBLE);
            postflotingbtn.setVisibility(View.VISIBLE);

        } else {
            ac = new RvQuestionDesignAdapter(bean, UserPosts.this);
            userpost_rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            userpost_rv.setAdapter(ac);

            ac.setOnOpenDialog(new RvQuestionDesignAdapter.OnOpenDialog() {
                @Override
                public void onClick(int position) {

                    ImageView img;
                    Dialog d = new Dialog(getApplicationContext());
                    d.setContentView(R.layout.open_image_dialog);
                    img = d.findViewById(R.id.bigimg);
                    Glide.with(getApplicationContext()).load(URL_queimage + bean.get(position).image).into(img);
                    PhotoViewAttacher pAttacher;
                    pAttacher = new PhotoViewAttacher(img);
                    pAttacher.update();
                    d.show();
                }


            });
            ac.setOnOpenReportDialog(new RvQuestionDesignAdapter.OnOpenReportDialog() {
                @Override
                public void onClick(int position) {
                    CheckBox cb1, cb2;
                    EditText desc;
                    Button report;
                    final Dialog d = new Dialog(UserPosts.this);
                    d.setContentView(R.layout.report_dialog);
                    report = d.findViewById(R.id.btn_report);
                    report.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            d.cancel();
                        }
                    });
                    d.show();
                }
            });


        }
    }


}
