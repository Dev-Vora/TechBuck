package com.example.dev.techbuck.Bean;

/**
 * Created by DEV on 01/04/2019.
 */


public class AnswerBean {
    public String id;
    public String qid;
    public String email;
    public String date;
    public String answer;
    public String image;
    public String unm;
    public String userimage;

    public AnswerBean(String id, String qid, String email, String date, String answer, String image, String unm, String userimage) {
        this.id = id;
        this.qid = qid;
        this.email = email;
        this.date = date;
        this.answer = answer;
        this.image = image;
        this.unm = unm;
        this.userimage = userimage;
    }
}
