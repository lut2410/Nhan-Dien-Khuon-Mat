package com.example.pc_asus.nguoimu;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TextRecognitionActivity extends AppCompatActivity {
    private TessBaseAPI m_tess;
    ImageView imgView;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);
        initImageView();
        try {
            prepareLanguageDir();
            m_tess = new TessBaseAPI();
            m_tess.init(String.valueOf(getFilesDir()), "eng");
        } catch (Exception e) {
            // Logging here
        }

        Button btn= findViewById(R.id.btn_recognition);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_tess == null) {
                    return;
                }

                try {


                   // m_tess.setImage(BitmapFactory.decodeResource(getResources(), R.mipmap.texa));

                    m_tess.setImage(bitmap);
                    String result = m_tess.getUTF8Text();
                    Log.e("abc","kết quả:"+result);
                    TextView resultView = (TextView) findViewById(R.id.tv_text_result);
                    resultView.setText(result);
                } catch (Exception e) {
                    Toast.makeText(TextRecognitionActivity.this, "convert error", Toast.LENGTH_SHORT).show();
                    Log.e("abc","lỗi convert");
                    // Do what you like here...
                }

            }
        });


        Button btn_camera= findViewById(R.id.btn_text_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,2);
            }
        });

        Button btn_album= findViewById(R.id.btn_chonAnh);
        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }


    private void initImageView() {
         imgView = (ImageView) findViewById(R.id.img_text);
        Bitmap input = BitmapFactory.decodeResource(getResources(), R.mipmap.texa);
        imgView.setImageBitmap(input);
    }

    // copy file from assets to another folder due to accessible
    private void copyFile() throws IOException {
        // work with assets folder
        AssetManager assMng = getAssets();
       // OutputStream os = null;
        InputStream is = assMng.open("tessdata/eng.traineddata");
        OutputStream os = new FileOutputStream(getFilesDir() + "/tessdata/eng.traineddata");
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
        }

        is.close();
        os.flush();
        os.close();
    }

    private void prepareLanguageDir() throws IOException {
        File dir = new File(getFilesDir() + "/tessdata");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File trainedData = new File(getFilesDir() + "/tessdata/eng.traineddata");
        if (!trainedData.exists()) {
            copyFile();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {              //chọn ảnh
            if (data == null) {
                return;
            }
            try {
                InputStream inputStream = TextRecognitionActivity.this.getContentResolver().openInputStream(data.getData());
                 bitmap= BitmapFactory.decodeStream(inputStream);
                imgView.setImageBitmap(bitmap);



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (requestCode == 2 && resultCode == Activity.RESULT_OK && data!=null) {       //camera
            bitmap= (Bitmap) data.getExtras().get("data");
           imgView.setImageBitmap(bitmap);
        }

    }
}