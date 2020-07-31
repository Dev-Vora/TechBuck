package com.example.dev.techbuck.Bean;

import java.io.Serializable;


public class QuestionBean implements Serializable {

    public String id;
    public String unm;
    public String email;
    public String date;
    public String question;
    public String image;
    public String cat;
    public String userimage;

    public QuestionBean(String id, String unm, String email, String date, String question, String image, String cat, String userimage) {
        this.id = id;
        this.unm = unm;
        this.email = email;
        this.date = date;
        this.question = question;
        this.image = image;
        this.cat = cat;
        this.userimage = userimage;
    }

    public QuestionBean() {
    }
}
