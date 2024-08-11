package com.firoz.mahmud.smsbot;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.mms.util_alt.SqliteWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;
import com.klinker.android.send_message.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Log.e("FirozMahmud","This is only for bot");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        } else {
            dosomething();
        }

//        startActivity(new Intent(MainActivity.this,Home.class));




    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,
                                            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dosomething();

            }
        }
    }






    public void dosomething(){


        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this, Login.class));
        }else{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, Login.class));
        }
        finish();
//sendmms();

    }


public void sendmms(){

//        if (!android.provider.Settings.canDrawOverlays(this)) {
//            // send user to the device settings
//            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//            startActivity(myIntent);
//        }else {

            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 1000);
//        }
}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
           try{

//               Intent smsIntent = new Intent(Intent.ACTION_VIEW);
////               smsIntent.setType("vnd.android-dir/mms-sms");
//               smsIntent.putExtra("sms_body", "what");
//               smsIntent.putExtra("address","01713716821");
//               startActivity(smsIntent);

               Intent i = new Intent(Intent.ACTION_SEND);
               i.putExtra("address", "01713716821");
               i.putExtra("sms_body", "testing");

               i.setType("vnd.wap.mms-message");
               i.putExtra(Intent.EXTRA_STREAM, data.getData());
               String type = data.getData().toString();
               type = type.substring(type.lastIndexOf(".") + 1);
               i.setType("image/" + type);

               startActivity(i);

//com.android.internal.app








//                Settings settings = new Settings();
//                settings.setProxy("10.16.18.77");
//                settings.setPort("9028");
//                settings.setMmsc("http://10.16.18.4:38090/was");
//
//                Transaction transaction = new Transaction(MainActivity.this, settings);
//                Message message = new Message("hello", "+8801868719391");
//                message.setImage(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
//                transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);


//            SmsManager smss=SmsManager.getDefault();
//
//            smss.sendMultimediaMessage(getApplicationContext(), data.getData(), "01722029853", null, null);
//
                int a = 10;











            }catch (Exception e){
               Toast.makeText(this, "failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }





}