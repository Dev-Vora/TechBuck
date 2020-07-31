package com.example.dev.techbuck.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.dev.techbuck.Answer;
import com.example.dev.techbuck.Bean.QuestionBean;
import com.example.dev.techbuck.CallServices;
import com.example.dev.techbuck.R;
import com.example.dev.techbuck.Session;
import com.example.dev.techbuck.URL;

import java.util.ArrayList;
import java.util.List;


public class RvQuestionDesignAdapter extends RecyclerView.Adapter<RvQuestionDesignAdapter.Holder> {

    private Context context;
    private LayoutInflater inflater;
    private List<QuestionBean> beans;
    private String URL_userimage = URL.url + "/uploads/";
    private String URL_queimage = URL.url + "/questionimage/";
    private String URL_delete = URL.url + "/deletequestion.php";
    private Session session;
    private OnOpenDialog onOpenDialog;
    private OnOpenReportDialog onOpenReportDialog;
    private ArrayList<String> key, value;
    private String M = "m";
    private CallServices cs;

    public RvQuestionDesignAdapter() {
    }

    public RvQuestionDesignAdapter(List<QuestionBean> beans, Context context) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.beans = beans;
        session = new Session(context);
        key = new ArrayList<>();
        value = new ArrayList<>();
        cs = new CallServices();

    }

    public void setOnOpenDialog(OnOpenDialog onOpenDialog) {
        this.onOpenDialog = onOpenDialog;
    }

    public void setOnOpenReportDialog(OnOpenReportDialog onOpenReportDialog) {
        this.onOpenReportDialog = onOpenReportDialog;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new RvQuestionDesignAdapter.Holder(inflater.inflate(R.layout.rv_question_design, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        Log.d("Sixee===>", beans.size() + "");
        holder.userquestion.setText(beans.get(holder.getAdapterPosition()).question);
        holder.username.setText(beans.get(holder.getAdapterPosition()).unm);

        holder.Date.setText(beans.get(holder.getAdapterPosition()).date);
        if (beans.get(holder.getAdapterPosition()).userimage.trim().length() > 0)
            Glide.with(context).load(URL_userimage + beans.get(holder.getAdapterPosition()).userimage).into(holder.cirimage);
        else
            holder.cirimage.setImageResource(R.drawable.default_image);
        Log.e("userimage", beans.get(holder.getAdapterPosition()).userimage);
        Log.e("queimage", beans.get(holder.getAdapterPosition()).image);
        if (beans.get(holder.getAdapterPosition()).image.trim().length() > 0) {
            Glide.with(context).load(URL_queimage + beans.get(holder.getAdapterPosition()).image).into(holder.queimage);
            holder.queimage.setVisibility(View.VISIBLE);
        } else
            holder.queimage.setVisibility(View.GONE);

        if (session.checkLogin().trim().equalsIgnoreCase(beans.get(holder.getAdapterPosition()).email)) {

            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }


        holder.queimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onOpenDialog != null) {
                    onOpenDialog.onClick(holder.getAdapterPosition());
                }

            }
        });
        holder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onOpenReportDialog != null) {
                    onOpenReportDialog.onClick(holder.getAdapterPosition());
                }


            }
        });
        holder.answers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, Answer.class);
                i.putExtra("qid", beans.get(holder.getAdapterPosition()).id);

                context.startActivity(i);
            }
        });

    }


    @Override
    public int getItemCount() {
        return beans.size();
    }


    class Holder extends RecyclerView.ViewHolder {
        TextView username, Date, userquestion;
        Button answers, report;
        ImageView queimage, cirimage;
        ImageButton delete;


        public Holder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            userquestion = itemView.findViewById(R.id.userquestion);
            Date = itemView.findViewById(R.id.Date);
            report = itemView.findViewById(R.id.report);
            answers = itemView.findViewById(R.id.answers);
            cirimage = itemView.findViewById(R.id.cirimage);
            queimage = itemView.findViewById(R.id.queimage);
            delete = itemView.findViewById(R.id.delete);


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Delete");
                    alertDialog.setMessage("Are you sure you want to Delete?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    key.add("qid");
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
