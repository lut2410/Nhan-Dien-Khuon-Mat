package com.example.pc_asus.nguoimu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class VideoChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private SharedPreferences sharedPreferences;
    String uid;
    boolean readData=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headView= navigationView.getHeaderView(0);
        final ImageView img= (ImageView) headView.findViewById(R.id.img_bar_avatar);
        final TextView tv_name=(TextView) headView.findViewById(R.id.tv_bar_name);
        final String[] photoURL = new String[1];


        final View tvTap =  findViewById(R.id.tv_tap);




        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
         uid= mCurrentUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("NguoiMu").child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_name.setText(dataSnapshot.child("name").getValue().toString());
                photoURL[0] =dataSnapshot.child("photoURL").getValue().toString();
               // Picasso.with(VideoChatActivity.this).load(dataSnapshot.child("photoURL").getValue().toString()).into(img);
                RequestOptions  requestOptions = new RequestOptions();
                requestOptions.fitCenter();
                requestOptions.placeholder(R.mipmap.user);
                Glide.with(getApplicationContext())
                        .load(photoURL[0])
                        .apply(requestOptions)
                        //   .override(200,150)
                        .into(img);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        tvTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readData=true;
                getListFriend(uid);
            }
        });

    }











    private void getListFriend(String uid){

        final ArrayList<String> arr = new ArrayList<String>();

            mDatabase.child("NguoiMu").child("Friends").child(uid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(readData==true) {
                        arr.add(dataSnapshot.getKey());
                    }
                    //   User user= dataSnapshot.getValue(User.class);
                    //  Log.e("arr",user.name);


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


            mDatabase.child("NguoiMu").child("Friends").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(readData==true) {
                        getStatusOfFriends(arr);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }




    private void getStatusOfFriends(final ArrayList<String> arr2){

            final ArrayList<String> arrTNVFreeTime = new ArrayList<String>();

            for (int i = 0; i < arr2.size(); i++) {
                Log.e("arr", "friends=" + arr2.get(i));
                final int finished = i;
                mDatabase.child("TinhNguyenVien").child("Status").child(arr2.get(i)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(readData==true) {
                            String s0 = dataSnapshot.child("statusWithFriends").getValue().toString();
                            int status = Integer.parseInt(s0);
                            if (status == 1) {
                                arrTNVFreeTime.add(dataSnapshot.getKey());
                                Log.e("arr", dataSnapshot.getKey());

                            }

                            if (finished == arr2.size() - 1) {
                                Random rd = new Random();
                                int number = rd.nextInt(arrTNVFreeTime.size());
                                String idSelected = arrTNVFreeTime.get(number);
                                Toast.makeText(VideoChatActivity.this, idSelected, Toast.LENGTH_SHORT).show();//////////////ĐÃ chọn dc bbe đang rãnh
                                mDatabase.child("TinhNguyenVien").child("Status").child(idSelected).child("connectionRequest").setValue(uid);                           // nhớ sữa code xet lun coi có đang kết nối vs ai ko mới chọn

                                Log.e("arr", idSelected);
                                readData = false;

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

       }
      //  return arrTNVFreeTime;
 //  }







    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.video_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_accountSetting) {
            startActivity(new Intent(VideoChatActivity.this,AccountSettingsActivity.class));
           // Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_friends) {
            startActivity(new Intent(VideoChatActivity.this,FriendsActivity.class));

            //Toast.makeText(this, "friends", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(VideoChatActivity.this,SignInActivity.class));
            finish();
          //  Toast.makeText(this, "sign out", Toast.LENGTH_SHORT).show();


        }else if (id == R.id.nav_search) {
            startActivity(new Intent(VideoChatActivity.this,SearchTnvActivity.class));
            //  Toast.makeText(this, "sign out", Toast.LENGTH_SHORT).show();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
