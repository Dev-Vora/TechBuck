package com.example.dev.techbuck.Bean;

import java.io.Serializable;

/**
 * Created by DEV on 10/02/2019.
 */

public class CategoryBean implements Serializable {

    public String c_id;
    public String c_name;
    public String c_image;

    public CategoryBean(String c_id, String c_name, String c_image) {
        this.c_id = c_id;
        this.c_name = c_name;
        this.c_image = c_image;
    }

    public CategoryBean() {
    }

    public CategoryBean(String c_name, String c_image) {
        this.c_name = c_name;
        this.c_image = c_image;
    }
}
