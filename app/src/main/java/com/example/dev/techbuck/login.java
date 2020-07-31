package com.example.dev.techbuck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class login extends AppCompatActivity {
    EditText ed_email, password;
    Button notsignup, login;
    String Method = "add";
    String URL = "http://192.168.43.37/techbuckfiles/user_login.php";
    CallServices cs;
    ArrayList<String> key;
    ArrayList<String> value;
    String email, pw;
    Session session;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ed_email = findViewById(R.id.ed_email);
        password = findViewById(R.id.password);
        notsignup = findViewById(R.id.notsignup);
        login = findViewById(R.id.btnlogin);
        session = new Session(getApplicationContext());
        cs = new CallServices();
        key = new ArrayList<>();
        value = new ArrayList<>();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validemail()&&validpassword())
                {
                    email = ed_email.getText().toString();
                    pw = password.getText().toString();

                    key.add("email");
                    value.add(email);
                    key.add("password");
                    value.add(pw);

                    String res = cs.CallServices(login.this, URL, Method, key, value);

                    Log.e("error===>", res);
                    if (res.equalsIgnoreCase("0")) {
                        Toast.makeText(login.this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
                    } else if (res.equalsIgnoreCase("1")) {
                        session.setLogin(email);
                        session.setPassword(pw);
                        Intent intent = new Intent(login.this, Home.class);
                        startActivity(intent);

                    }
                }
            }
        });
        notsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Registration.class));
            }
        });

    }

    private boolean validemail() {

        if (ed_email.getText().toString().isEmpty()) {
            ed_email.setError("Feild Can't Be Empty");
            return false;
        } else {
            ed_email.setError(null);
            return true;
        }
    }

    private boolean validpassword() {

        if (password.getText().toString().isEmpty()) {
            password.setError("Feild Can't Be Empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }


}
