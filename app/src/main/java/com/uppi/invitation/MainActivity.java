package com.uppi.invitation;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SignaturePad mSignaturePad;
    private Button mSaveButton;
    private Button mClearButton;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mClearButton = (Button) findViewById(R.id.clearSignBtn);
        mSaveButton = (Button) findViewById(R.id.saveSignBtn);
        imageView = (ImageView) findViewById(R.id.imageView) ;

        mSaveButton.setEnabled(false);

        Uri uri = Uri.parse( "/data/user/0/com.uppi.invitation/app_Images/principal_sign" );
        imageView.setImageURI(uri);

        checkIsSigned();

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
                mSaveButton.setEnabled(true);
                mSaveButton.setBackground(getDrawable(R.drawable.btn_bg));
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
            }
        });



        mClearButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }

        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                Uri uri =  SaveImage(getApplicationContext(),signatureBitmap);
                storeinexternalstorage(signatureBitmap);

                SharedPreferences sp = getSharedPreferences("FILE", MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("isSigned",true);
                edit.apply();

            }

        });


    }

    private void checkIsSigned() {

        SharedPreferences sp = getSharedPreferences("FILE", MODE_PRIVATE);
        Boolean result = sp.getBoolean("isSigned", false);

        if (result){
            mSignaturePad.setVisibility(View.INVISIBLE);
        }

    }

    public Uri SaveImage(Context mContext, Bitmap bitmap) {

        ContextWrapper wrapper = new ContextWrapper(mContext);

        File file = wrapper.getDir("Images",MODE_PRIVATE);

        file = new File(file, "principal_sign");

        if(file.exists()) file. delete();

        try{

            OutputStream stream = null;

            stream = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

            stream.flush();

            stream.close();

        }

        catch (IOException e)
          {
            e.printStackTrace();
          }


        Uri mImageUri = Uri.parse(file.getAbsolutePath());
        //imageView.setImageURI(mImageUri);
        return mImageUri;

    }


    void storeinexternalstorage(Bitmap bitmap){

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root,"/Invitation");
        myDir.mkdirs();

        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String fname = "Shutta_"+ timeStamp +".jpg";
        String fname = "principal_sign" + ".jpg";

        File file = new File(myDir, fname);

        if (file.exists()) file.delete ();

        try {

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        Uri uri = Uri.parse(file.getAbsolutePath());
        imageView.setImageURI(uri);
    }
}