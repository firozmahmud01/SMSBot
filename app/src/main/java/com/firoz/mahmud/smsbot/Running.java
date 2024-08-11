package com.firoz.mahmud.smsbot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Running extends AppCompatActivity {





    private Context context;
    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private LayoutInflater layoutInflater;
    Thread th;
    Handler hand;
    Button but;
//    public void startTop() {
//        try {
//            context = getBaseContext();
//            mParams = new WindowManager.LayoutParams(
//                    // Shrink the window to wrap the content rather
//                    // than filling the screen
//                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
//                    // Display it on top of other application windows
//                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                    // Don't let it grab the input focus
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                    // Make the underlying application window visible
//                    // through any transparent parts
//                    PixelFormat.TRANSLUCENT);
//
//
//            // getting a LayoutInflater
//            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            // inflating the view with the custom layout we created
//            mView = layoutInflater.inflate(R.layout.popupwindow, null);
//            // set onClickListener on the remove button, which removes
//            // the view from the window
//            but=mView.findViewById(R.id.firozpopupwindowbutton);
//            but.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//try {
////    SharedPreferences sp = mView.getContext().getSharedPreferences("data", MODE_PRIVATE);
////    String image = sp.getString("image", null);
////    String msg = sp.getString("message", null);
////    String num = sp.getString("number", null);
////    Intent i = new Intent(Intent.ACTION_SEND);
////    i.putExtra("address", num);
////    i.putExtra("sms_body", msg);
////    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////    i.putExtra(Intent.EXTRA_STREAM, Uri.parse(image));
////    String type = image;
////    type = type.substring(type.lastIndexOf(".") + 1);
////    i.setType("image/" + type);
////    mView.getContext().startActivity(i);
//}catch (Exception e){
//    int a=10;
//}
//                }
//            });
//            // Define the position of the
//            // window within the screen
//            mParams.gravity = Gravity.TOP;
//            mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
//
//            hand=new Handler();
//            if (mView.getWindowToken() == null) {
//                if (mView.getParent() == null) {
//                    mWindowManager.addView(mView, mParams);
//                }
//            }
//            th=new Thread(){
//                @Override
//                public void run() {
////                    while(true ){
////                       if (!MMSSernder.startmms) {
//                           try {
//                               sleep(5000);
//                           }catch (Exception e){}
////                           continue;
////                       }
//hand.post(new Runnable() {
//    @Override
//    public void run() {
//                    but.performClick();
//
//    }
//});
//
//                            MMSSernder.ismms=true;
//                            MMSSernder.startmms=false;
//
////                    }
//                }
//            };
//            th.start();
//        }catch (Exception e){}
//    }
//    public void closepopup() {
//
//        try {
//            // remove the view from the window
//            ((WindowManager)context.getSystemService(WINDOW_SERVICE)).removeView(mView);
//            // invalidate the view
//            mView.invalidate();
//            // remove all views
//            ((ViewGroup)mView.getParent()).removeAllViews();
//
//            // the above steps are necessary when you are adding and removing
//            // the view simultaneously, it might give some exceptions
//        } catch (Exception e) {
//            Log.d("Error2",e.toString());
//        }
//    }
    Intent service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_running);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(FirebaseAuth.getInstance().getCurrentUser()==null)return;
        MMSSernder.shouldstart=true;
        service=new Intent(Running.this,Background.class);
        startService(service);
        findViewById(R.id.runningstopbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MMSSernder.shouldstart=false;
                stopService(service);
                finish();
            }
        });

    }
}