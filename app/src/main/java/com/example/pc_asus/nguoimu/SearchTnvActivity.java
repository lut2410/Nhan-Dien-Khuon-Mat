package com.example.pc_asus.nguoimu;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchTnvActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    ArrayList<TNV> arrTNV= new ArrayList<TNV>();
    ArrayList<TNV> arrTNV2= new ArrayList<TNV>();

    ListView lv_listTnv;
    ListTnvAdapter adapter;
    SearchView searchView;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tnv);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        uid= mCurrentUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        lv_listTnv= findViewById(R.id.lv_listTNV2);
        adapter= new ListTnvAdapter(getApplicationContext(),R.layout.custom_list_tnv,arrTNV2);
        lv_listTnv.setAdapter(adapter);

        searchView=(SearchView) findViewById(R.id.sv_search2);
        searchView.setOnQueryTextListener(this);


        mDatabase.child("TinhNguyenVien").child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user= dataSnapshot.getValue(User.class);
                arrTNV.add(new TNV(user,dataSnapshot.getKey()));
                arrTNV2.add(new TNV(user,dataSnapshot.getKey()));

                Log.e("abc",user.email+" "+dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
                //cho cai biến đếm

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        lv_listTnv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    final Dialog dialog = new Dialog(SearchTnvActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_add_friend);

    //                Window window = dialog.getWindow();
    //                WindowManager.LayoutParams wlp = window.getAttributes();
    //
    //                wlp.gravity = Gravity.RIGHT;
    //
    //                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
    //                window.setAttributes(wlp);
                    TextView tv_addFriend= (TextView) dialog.findViewById(R.id.tv_addFriend);
                TextView tv_close= (TextView) dialog.findViewById(R.id.tv_close);
                tv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                tv_addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mDatabase.child("NguoiMu").child("Friends").child(uid).child(arrTNV2.get(position).id).setValue(arrTNV2.get(position).user);

                        Toast.makeText(SearchTnvActivity.this, "Đã gửi yêu cầu", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String mail;
        int s = newText.length();
        arrTNV2.clear();
        adapter.notifyDataSetChanged();
        if(newText.length()>=3) {
            for (int i = 0; i < arrTNV.size(); i++) {
                mail = arrTNV.get(i).user.email.substring(0, s);
                if (newText.equalsIgnoreCase(mail) == true) {
                    arrTNV2.add(arrTNV.get(i));
                    adapter.notifyDataSetChanged();

                }
            }
        }
        return false;
    }
}
