package com.example.dev.techbuck;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.dev.techbuck.Adapters.RvAnswerAdapter;
import com.example.dev.techbuck.Bean.AnswerBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class Answer extends AppCompatActivity {
    RecyclerView ansrv;
    FloatingActionButton ansflotingbtn;
    ArrayList<String> key, value;
    String METHOD = "add";
    CallServices cs;
    String qid;
    TextView noanswer;
    ArrayList<AnswerBean> beanArrayList = new ArrayList<>();
    String URL1 = com.example.dev.techbuck.URL.url + "/answer.php";
    String URL_userimage = URL.url + "/uploads/";
    String URL_ansimage = URL.url + "/answerimage/";
    Session session;
    String URL_report = com.example.dev.techbuck.URL.url + "/report.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        Intent i = getIntent();
        qid = i.getStringExtra("qid");
        init();
        //Log.e("qid", String.valueOf(qid));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 20008) {
            qid = data.getExtras().getString("qid");
            init();
        }
    }

    private void init() {
        ansrv = findViewById(R.id.ansrv);
        ansflotingbtn = findViewById(R.id.ansflotingbtn);
        noanswer = findViewById(R.id.noanswer);
        key = new ArrayList<>();
        value = new ArrayList<>();
        cs = new CallServices();
        session = new Session(Answer.this);

        processToLoadAnswerData();
        ansflotingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startActivity(new Intent(Answer.this,AddAnswer.class));
                Intent intent = new Intent(getApplicationContext(), AddAnswer.class);
                intent.putExtra("qid", qid);
                startActivityForResult(intent, 20008);
                // finish();


            }
        });
    }

    private void processToLoadAnswerData() {

        key.add("qid");
        value.add(qid);

        beanArrayList.clear();
        String res = cs.CallServices(Answer.this, URL1, METHOD, key, value);
        Log.e("answer", res);
        try {
            JSONObject jsonObject = new JSONObject(res);
            JSONArray jsonArray = jsonObject.optJSONArray("data");

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                String id = jsonObject1.getString("id");
                String qid = jsonObject1.getString("qid");
                String email = jsonObject1.getString("email");
                String date = jsonObject1.getString("date");
                String answer = jsonObject1.getString("answer");
                String image = jsonObject1.getString("image");
                String unm = jsonObject1.getString("unm");
                String userimage = jsonObject1.getString("userimage");
                AnswerBean Ans = new AnswerBean(id, qid, email, date, answer, image, unm, userimage);
                beanArrayList.add(Ans);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Errrorrr=>", e.getLocalizedMessage());
        }

        if (beanArrayList.size() <= 0) {
            noanswer.setVisibility(View.VISIBLE);

        } else {
            RvAnswerAdapter adapter = new RvAnswerAdapter(Answer.this, beanArrayList);
            ansrv.setLayoutManager(new LinearLayoutManager(Answer.this));
            ansrv.setAdapter(adapter);

            adapter.setOnOpenDialog(new RvAnswerAdapter.OnOpenDialog() {
                @Override
                public void onClick(int position) {
                    ImageView img;
                    Dialog d = new Dialog(Answer.this);
                    d.setContentView(R.layout.open_image_dialog);
                    img = d.findViewById(R.id.bigimg);
                    Glide.with(Answer.this).load(URL_ansimage + beanArrayList.get(position).image).into(img);
                    PhotoViewAttacher pAttacher;
                    pAttacher = new PhotoViewAttacher(img);
                    pAttacher.update();
                    d.show();

                }
            });
            adapter.setOnOpenReportDialog(new RvAnswerAdapter.OnOpenReportDialog() {

                @Override
                public void onClick(final int position) {
                    final CheckBox cb1, cb2;
                    final EditText desc;
                    final String[] c = new String[1];
                    Button report;
                    final Dialog d = new Dialog(Answer.this);
                    d.setContentView(R.layout.report_dialog);
                    cb1 = d.findViewById(R.id.cb1);
                    desc = d.findViewById(R.id.desc);
                    cb2 = d.findViewById(R.id.cb2);

                    report = d.findViewById(R.id.btn_report);
                    report.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (cb1.isChecked())
                                c[0] = String.valueOf(cb1.getText());
                            if (cb2.isChecked())
                                c[0].concat(String.valueOf(cb2.getText()));
                            key.clear();
                            value.clear();
                            key.add("data");
                            value.add(c[0]);
                            key.add("dec");
                            value.add(desc.getText().toString());
                            key.add("againset");
                            value.add(beanArrayList.get(position).email);
                            key.add("qid");
                            value.add("null");
                            key.add("aid");
                            value.add(beanArrayList.get(position).id);
                            key.add("email");
                            value.add(session.checkLogin());
                            String r = cs.CallServices(Answer.this, URL_report, METHOD, key, value);


                            d.cancel();
                        }
                    });
                    d.show();
                }
            });

        }

    }
}
