package com.example.pc_asus.nguoimu;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
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

public class FriendsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    ArrayList<TNV> arrTNV= new ArrayList<TNV>();
    ArrayList<TNV> arrTNV2= new ArrayList<TNV>();

    ListView lv_listTnv;
    ListTnvAdapter adapter;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid= mCurrentUser.getUid();
        try {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("NguoiMu").child("Friends").child(uid);        // chưa kết bạn -> ko có nhánh friends -> lỗi
        }catch (Exception e){
            Toast.makeText(this, "Không có bạn bè để hiển thị", Toast.LENGTH_SHORT).show();
            finish();
        }
        lv_listTnv= findViewById(R.id.lv_listTNV);
        adapter= new ListTnvAdapter(getApplicationContext(),R.layout.custom_list_tnv,arrTNV2);
        lv_listTnv.setAdapter(adapter);

        searchView=(SearchView) findViewById(R.id.sv_search);
        searchView.setOnQueryTextListener(this);


        mDatabase.addChildEventListener(new ChildEventListener() {
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(FriendsActivity.this, ""+arrTNV2.get(position).user.name, Toast.LENGTH_SHORT).show();
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
