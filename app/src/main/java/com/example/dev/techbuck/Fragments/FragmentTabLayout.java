package com.example.dev.techbuck.Fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.dev.techbuck.Adapters.RvQuestionDesignAdapter;
import com.example.dev.techbuck.AddQuestion;
import com.example.dev.techbuck.Bean.CategoryBean;
import com.example.dev.techbuck.Bean.QuestionBean;
import com.example.dev.techbuck.CallServices;
import com.example.dev.techbuck.R;
import com.example.dev.techbuck.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FragmentTabLayout extends Fragment {
    FloatingActionButton flotingbtn;
    RecyclerView rv;
    View view;
    ArrayList<String> key, value;
    CategoryBean categoryBean;
    String METHOD = "add";
    ArrayList<QuestionBean> bean = new ArrayList<>();
    String URL = com.example.dev.techbuck.URL.url + "/question.php";
    String URL_report = com.example.dev.techbuck.URL.url + "/report.php";
    RvQuestionDesignAdapter ac;
    String cat;
    CallServices cs;
    Session session;
    String URL_userimage = com.example.dev.techbuck.URL.url + "/uploads/";
    String URL_queimage = com.example.dev.techbuck.URL.url + "/questionimage/";


    public FragmentTabLayout() {
        // Required empty public constructor
    }

    public static FragmentTabLayout getInstance(CategoryBean categoryBean, int position) {
        FragmentTabLayout fragmentTabLayout = new FragmentTabLayout();
        Bundle bundle = new Bundle();
        bundle.putSerializable("categoryBean", categoryBean);
        bundle.putInt("position", position);
        fragmentTabLayout.setArguments(bundle);

        return fragmentTabLayout;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fragment_tab_layout, container, false);
        init();

        flotingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddQuestion.class);
                startActivity(intent);

            }
        });

        key.add("cat");
        value.add(cat);
        String res = cs.CallServices(getContext(), URL, METHOD, key, value);
        Log.e("error-------", res);
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
        ac = new RvQuestionDesignAdapter(bean, getContext());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(ac);

        ac.setOnOpenDialog(new RvQuestionDesignAdapter.OnOpenDialog() {
            @Override
            public void onClick(int position) {

                ImageView img;
                Dialog d = new Dialog(getContext());
                d.setContentView(R.layout.open_image_dialog);
                img = d.findViewById(R.id.bigimg);
                Glide.with(getContext()).load(URL_queimage + bean.get(position).image).into(img);
                PhotoViewAttacher pAttacher;
                pAttacher = new PhotoViewAttacher(img);
                pAttacher.update();
                d.show();
            }


        });
        ac.setOnOpenReportDialog(new RvQuestionDesignAdapter.OnOpenReportDialog() {
            @Override
            public void onClick(final int position) {
                final CheckBox cb1, cb2;
                final EditText desc;
                final String[] c = new String[1];
                Button report;
                final Dialog d = new Dialog(getContext());
                d.setContentView(R.layout.report_dialog);
                report = d.findViewById(R.id.btn_report);
                cb1 = d.findViewById(R.id.cb1);
                desc = d.findViewById(R.id.desc);
                cb2 = d.findViewById(R.id.cb2);
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
                        value.add(bean.get(position).email);
                        key.add("qid");
                        value.add(bean.get(position).id);
                        key.add("aid");
                        value.add("null");
                        key.add("email");
                        value.add(session.checkLogin());
                        String r=cs.CallServices(getContext(),URL_report,METHOD,key,value);

                        d.cancel();
                    }
                });
                d.show();
            }
        });
        return view;
    }


    private void init() {

        categoryBean = new CategoryBean();
        categoryBean = (CategoryBean) getArguments().getSerializable("categoryBean");
        final int position = getArguments().getInt("position", 0);
        cat = categoryBean.c_name;
        cs = new CallServices();
        key = new ArrayList<>();
        value = new ArrayList<>();
        session = new Session(getContext());
        flotingbtn = view.findViewById(R.id.flotingbtn);
        rv = view.findViewById(R.id.rv);
    }


}
