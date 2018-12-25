package com.example.pc_asus.nguoimu;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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
import java.util.Locale;
import java.util.Random;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,TextToSpeech.OnInitListener {
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    String uid;
    TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;

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




        tts= new TextToSpeech(this, this);

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
                promptSpeechInput();
            }
        });


    }





// Của Layout








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
        getMenuInflater().inflate(R.menu.video_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_accountSetting) {
            startActivity(new Intent(MainActivity.this,AccountSettingsActivity.class));

        } else if (id == R.id.nav_friends) {
            startActivity(new Intent(MainActivity.this,FriendsActivity.class));

        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,SignInActivity.class));
            finish();

        }else if (id == R.id.nav_search) {
            startActivity(new Intent(MainActivity.this,SearchTnvActivity.class));

        }else if (id == R.id.nav_place) {
            startActivity(new Intent(MainActivity.this,PlacesOftenComeActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }







    ////của VideoCall





    @Override
    protected void onDestroy() {
        super.onDestroy();


        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
    }



    @Override
    public void onInit(int i) {
        if(i !=TextToSpeech.ERROR) {

            Locale l = new Locale("vi");
            tts.setLanguage(l);

        }
    }


    //dialog voice của google

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // xac nhan ung dung muon gui yeu cau
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        // goi y nhung dieu nguoi dung muon noi

        // goi y nhan dang nhung gi nguoi dung se noi
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something…");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Trả lại dữ liệu sau khi nhập giọng nói vào
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        switch (requestCode) {
//            case REQ_CODE_SPEECH_INPUT:
        if( requestCode==REQ_CODE_SPEECH_INPUT){
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                // nói lại những gì vừa nghe dc

                for(int i=0;i<result.size();i++) {
                    Log.e("abc", result.get(i));
                    if(result.get(i).equalsIgnoreCase("kết nối")){
                        tts.speak("đang kết nối, vui lòng chờ", TextToSpeech.QUEUE_FLUSH,null);
                        startActivity(new Intent(MainActivity.this, VideoCallViewActivity.class));
                    }else if(result.get(i).equalsIgnoreCase("alo")){
                       // tts.speak("đang kết nối, vui lòng chờ", TextToSpeech.QUEUE_FLUSH,null);
                        startActivity(new Intent(MainActivity.this, TextRecognitionActivity.class));
                    }


                }
            }



        }


    }
}
