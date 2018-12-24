package com.example.pc_asus.nguoimu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    EditText edt_email,edt_password;
    Button btn_signIn, btn_signUp;
    private FirebaseAuth mAuth;
// ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
       // Intent intent= new Intent(MainActivity.this,VideoChatViewActivity.class);
       // startActivity(intent);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        btn_signIn = findViewById(R.id.btn_dangNhap);
        btn_signUp = findViewById(R.id.btn_dangKi);
        mAuth = FirebaseAuth.getInstance();

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangNhap();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.e("abc","trang thai dang nhap "+currentUser);
        if(currentUser!=null) {
              Intent intent= new Intent(SignInActivity.this,VideoChatActivity.class);
              startActivity(intent);
              finish();
        }
    }

    private void  dangNhap() {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("loading...");

        String email = edt_email.getText().toString().trim();
        final String password = edt_password.getText().toString().trim();
        Log.e("abc",email);
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignInActivity.this, "Đăng Nhập Thất Bại", Toast.LENGTH_SHORT).show();
        } else {
            dialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                //     Toast.makeText(MainActivity.this, "Đăng Nhập Thành Công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignInActivity.this, VideoChatActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(SignInActivity.this, "Đăng Nhập Thất Bại", Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    });

        }
    }
}
