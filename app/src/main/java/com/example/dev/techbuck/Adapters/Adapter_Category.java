package com.example.dev.techbuck.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dev.techbuck.Bean.CategoryBean;
import com.example.dev.techbuck.CategoryPager;
import com.example.dev.techbuck.R;

import java.util.List;

/**
 * Created by DEV on 05/02/2019.
 */

public class Adapter_Category extends RecyclerView.Adapter<Adapter_Category.Holder> {

    Context context;
    LayoutInflater inflater;
    List<CategoryBean> beanList;


    public Adapter_Category(List<CategoryBean> cat, Context context) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        beanList = cat;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {


        return new Holder(inflater.inflate(R.layout.json_category, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        holder.category.setText(beanList.get(holder.getAdapterPosition()).c_name);

        Glide.with(context).load(com.example.dev.techbuck.URL.url + "/categoryimage/" + beanList.get(holder.getAdapterPosition()).c_image).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return beanList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView category;
        ImageView image;

        public Holder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            category = itemView.findViewById(R.id.category);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CategoryPager.class);
                    intent.putExtra("cat", category.getText().toString());
                    context.startActivity(intent);
                }
            });
        }
    }
}
