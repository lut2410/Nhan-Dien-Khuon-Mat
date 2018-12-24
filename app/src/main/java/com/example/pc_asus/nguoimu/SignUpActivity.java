package com.example.pc_asus.nguoimu;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    EditText edt_email,edt_password, edt_name, edt_phoneNumber;
    Button btn_signUp;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edt_email = findViewById(R.id.edt_email2);
        edt_password = findViewById(R.id.edt_pass2);
        edt_name = findViewById(R.id.edt_hoTen);
        edt_phoneNumber = findViewById(R.id.edt_sdt);

        btn_signUp = findViewById(R.id.btn_dangKy2);
        mAuth = FirebaseAuth.getInstance();
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangKy();
            }
        });
    }

    private void dangKy() {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("loading...");

        String email = edt_email.getText().toString().trim();
        final String password = edt_password.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Đăng Ký Thất Bại", Toast.LENGTH_SHORT).show();
        } else {
            dialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Đăng Ký Thành Công", Toast.LENGTH_SHORT).show();

                                User user= new User(edt_name.getText().toString(),edt_email.getText().toString(), edt_phoneNumber.getText().toString(),"https://firebasestorage.googleapis.com/v0/b/map-82eb0.appspot.com/o/generic-user-purple.png?alt=media&token=21815e8a-2bcd-477a-bf37-f6b382f0c409");
                                FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
                                String uid=currentUser.getUid();
                                DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("NguoiMu").child("Users").child(uid);
                                mDatabase.setValue(user);
                                finish();

                            } else {
                                dialog.dismiss();
                                if (password.length() < 6) {
                                    Toast.makeText(SignUpActivity.this, "    Đăng Ký Thất Bại\nMật Khẩu Không Được Dưới 6 Ký Tự", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(SignUpActivity.this, "Đăng Ký Thất Bại", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }
}
