package com.example.dev.techbuck;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.dev.techbuck.Bean.CategoryBean;
import com.example.dev.techbuck.Fragments.FragmentTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoryPager extends AppCompatActivity {

    ViewPager pager;
    TabLayout tabLayout;
    List<CategoryBean> list;
    ArrayList<String> key, value;
    ArrayList<CategoryBean> bean = new ArrayList<>();
    String URL = com.example.dev.techbuck.URL.url + "/category.php";
    String METHOD = "add";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_pager);

        initComponents();

    }
    private void initComponents() {

        list = new ArrayList<>();
        pager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tablayout);
        processToLoadCategoryData();
        tabLayout.setupWithViewPager(pager);

    }

    private void processToLoadCategoryData() {

        key = new ArrayList<>();
        value = new ArrayList<>();


        CallServices cs = new CallServices();
        String res = cs.CallServices(CategoryPager.this, URL, METHOD, key, value);
        Log.e("error-", res);
        try {
            JSONObject jsonObject = new JSONObject(res);
            JSONArray jsonArray = jsonObject.optJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String c_id = jsonObject1.getString("c_id");
                String c_name = jsonObject1.getString("c_name");
                String c_img = jsonObject1.getString("c_image");
                CategoryBean categoryBean = new CategoryBean(c_id, c_name, c_img);
                list.add(categoryBean);
                Log.e("c_img=", c_img);

            }
            addTabs();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Errrorrr=>", e.getLocalizedMessage());
        }

    }

    CategoryPager.PagerAdapter adapter;
    int j;

    private void addTabs() {
        adapter = new CategoryPager.PagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < list.size(); i++) {
            adapter.addFrag(FragmentTabLayout.getInstance(list.get(i), i), list.get(i).c_name);
            Intent intent = getIntent();
            if (list.get(i).c_name.equalsIgnoreCase(intent.getStringExtra("cat"))) {
                j = i;
            }
        }
        pager.setAdapter(adapter);

        pager.setOffscreenPageLimit(adapter.getCount());
        pager.setCurrentItem(j);

    }

    public class PagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> fragmentTitle = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        public void addFrag(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitle.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (CharSequence) fragmentTitle.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

}
