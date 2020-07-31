package com.example.dev.techbuck.Adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dev.techbuck.Bean.AnswerBean;
import com.example.dev.techbuck.CallServices;
import com.example.dev.techbuck.R;
import com.example.dev.techbuck.Session;
import com.example.dev.techbuck.URL;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RvAnswerAdapter extends RecyclerView.Adapter<RvAnswerAdapter.Holder> {

    private Context context;
    private LayoutInflater inflater;
    OnOpenDialog onOpenDialog;
    OnOpenReportDialog onOpenReportDialog;
    Session session;
    private List<AnswerBean> beans;
    String URL_userimage = URL.url + "/uploads/";
    String URL_ansimage = URL.url + "/answerimage/";
    ArrayList<String> key, value;
    String M = "m";
    CallServices cs;
    String URL_delete = URL.url + "/deleteanswer.php";


    public void setOnOpenDialog(OnOpenDialog onOpenDialog) {
        this.onOpenDialog = onOpenDialog;
    }

    public void setOnOpenReportDialog(OnOpenReportDialog onOpenReportDialog) {
        this.onOpenReportDialog = onOpenReportDialog;
    }

    public RvAnswerAdapter(Context context, List<AnswerBean> beans) {
        this.context = context;
        this.beans = beans;
        inflater = LayoutInflater.from(context);
        session = new Session(context);
        key = new ArrayList<>();
        value = new ArrayList<>();
        cs = new CallServices();

    }

    public RvAnswerAdapter() {
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RvAnswerAdapter.Holder(inflater.inflate(R.layout.rv_ans, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.ansDate.setText(beans.get(holder.getAdapterPosition()).date);
        holder.ansusername.setText(beans.get(holder.getAdapterPosition()).unm);
        holder.useranswer.setText(beans.get(holder.getAdapterPosition()).answer);
        Log.e("userimage", beans.get(holder.getAdapterPosition()).userimage);
        if (beans.get(holder.getAdapterPosition()).userimage.trim().length() > 0)
            Glide.with(context).load(URL_userimage + beans.get(holder.getAdapterPosition()).userimage).into(holder.anscirimage);
        else
            holder.anscirimage.setImageResource(R.drawable.default_image);
        if (beans.get(holder.getAdapterPosition()).image.trim().length() > 0) {
            Glide.with(context).load(URL_ansimage + beans.get(holder.getAdapterPosition()).image).into(holder.ansimage);
            holder.ansimage.setVisibility(View.VISIBLE);
        } else
            holder.ansimage.setVisibility(View.GONE);

        if (session.checkLogin().trim().equals(beans.get(holder.getAdapterPosition()).email))
            holder.ansdelete.setVisibility(View.VISIBLE);
        else
            holder.ansdelete.setVisibility(View.GONE);


    }


    @Override
    public int getItemCount() {
        return beans.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        CircleImageView anscirimage;
        TextView ansusername, ansDate, useranswer;
        Button ansreport;
        ImageView ansimage;
        ImageButton ansdelete;

        public Holder(View itemView) {
            super(itemView);
            anscirimage = itemView.findViewById(R.id.anscirimage);
            ansusername = itemView.findViewById(R.id.ansusername);
            ansDate = itemView.findViewById(R.id.ansDate);
            ansreport = itemView.findViewById(R.id.ansreport);
            useranswer = itemView.findViewById(R.id.useranswer);
            ansimage = itemView.findViewById(R.id.ansimage);
            ansdelete = itemView.findViewById(R.id.ansdelete);
            ansimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (onOpenDialog != null) {
                        onOpenDialog.onClick(getAdapterPosition());
                    }
                }
            });
            ansreport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (onOpenReportDialog != null) {
                        onOpenReportDialog.onClick(getAdapterPosition());
                    }

                }
            });
            ansdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Delete");
                    alertDialog.setMessage("Are you sure you want to Delete?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    key.add("id");
                                    value.add(beans.get(getAdapterPosition()).id);
                                    cs.CallServices(context, URL_delete, M, key, value);
                                    beans.remove(getAdapterPosition());
                                    notifyDataSetChanged();
                                    dialog.dismiss();

                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            });
        }
    }

    public interface OnOpenDialog {
        public void onClick(int position);

    }

    public interface OnOpenReportDialog {
        public void onClick(int position);
    }
}
