package com.firoz.mahmud.smsbot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;

public class Edit extends AppCompatActivity {
    EditText timee,msge;
    ImageView imagee;
    String image;
    int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        timee=findViewById(R.id.edit_timeedittext);
        msge=findViewById(R.id.edit_msg_edittext);
        imagee=findViewById(R.id.edit_imageview);

        Intent in=getIntent();
        index=in.getIntExtra("index",-1);
        if(index==-1)finish();
        int time=in.getIntExtra("time",0);
        String msg=in.getStringExtra("msg");
        image=in.getStringExtra("img");

        timee.setText(""+time);
        msge.setText(msg);
        if(image!=null){
            Glide.with(Edit.this).load(Uri.parse(image)).into(imagee);
        }

        findViewById(R.id.edit_choose_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,1000);
            }
        });


        findViewById(R.id.edit_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timee.getText().toString().isEmpty()||msge.getText().toString().isEmpty()){
                    return;
                }
                Intent in=new Intent();
                in.putExtra("time",Integer.parseInt(timee.getText().toString()));
                in.putExtra("msg",msge.getText().toString());
                in.putExtra("index",index);
                if(image!=null) {
                    in.putExtra("img", image);
                }
                setResult(100,in);
                finish();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000)
        {
            //todo do everything you need
            image=data.getData().toString();
            try {
                Bitmap bit=MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.parse(image));


                image=saveImage(bit).toString();

                imagee.setImageBitmap(bit);
            } catch (Exception e) {
                int a=10;
            }
        }
    }


    private Uri saveImage(Bitmap image) {
        File imagePath = new File(getFilesDir(), "external_files");
        imagePath.mkdir();
        File imageFile = new File(imagePath.getPath(), "img"+System.currentTimeMillis()+".jpg");
        Uri uri = null;
        try {

            FileOutputStream stream = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(
                    Edit.this,
                    "com.firoz.mahmud.smsbot.fileprovider", //(use your app signature + ".provider" )
                    imageFile);

        } catch (Exception e) {
            int a=10;
        }
        return uri;
    }
}