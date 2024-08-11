package com.firoz.mahmud.smsbot;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;

public class Background extends Service {




    private Context context;
    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private LayoutInflater layoutInflater;


    Button but;
    public void startTop() {
        try {
            context = getBaseContext();
            mParams = new WindowManager.LayoutParams(
                    // Shrink the window to wrap the content rather
                    // than filling the screen
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                    // Display it on top of other application windows
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    // Don't let it grab the input focus
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    // Make the underlying application window visible
                    // through any transparent parts
                    PixelFormat.TRANSLUCENT);


            // getting a LayoutInflater
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // inflating the view with the custom layout we created
            mView = layoutInflater.inflate(R.layout.popupwindow, null);

            // set onClickListener on the remove button, which removes
            // the view from the window
            but=mView.findViewById(R.id.firozpopupwindowbutton);

            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            });
            // Define the position of the
            // window within the screen
            mParams.gravity = Gravity.TOP;

            mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);


            if (mView.getWindowToken() == null) {
                if (mView.getParent() == null) {
                    mWindowManager.addView(mView, mParams);
                }
            }

        }catch (Exception e){
            int a=10;
        }
    }
    public void closepopup() {

        try {
            // remove the view from the window
            ((WindowManager)context.getSystemService(WINDOW_SERVICE)).removeView(mView);
            // invalidate the view
            mView.invalidate();
            // remove all views
            ((ViewGroup)mView.getParent()).removeAllViews();

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (Exception e) {
            Log.d("Error2",e.toString());
        }
    }

boolean runit=true;

    Thread th=null;
    Handler hand;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mView==null) {
            startTop();
        }



        hand=new Handler();
        if(th==null){
            th=new Thread() {
                @Override
                public void run() {
                    while (runit) {
                        try {
                            sleep(100);
                            if (!MMSSernder.startmms) continue;

                            hand.post(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                                        String image = sp.getString("image", null);
                                        String msg = sp.getString("message", null);
                                        String num = sp.getString("number", null);
                                        Intent i = new Intent(Intent.ACTION_SEND);
                                        i.putExtra("address", num);
                                        i.putExtra("sms_body", msg);


                                        i.putExtra(Intent.EXTRA_STREAM, Uri.parse(image));
                                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        String type = image;
                                        type = type.substring(type.lastIndexOf(".") + 1);
                                        i.setType("image/" + type);
                                        MMSSernder.ismms = true;
                                        MMSSernder.startmms = false;
                                        startActivity(i);
                                    }catch (Exception e){}
                                }

                            });
                        } catch (Exception e) {
                        }
                    }
                }


            };
            th.start();
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        runit=false;
        MMSSernder.shouldstart=false;
        try {
            th.destroy();
        }catch (Exception e){}
        th=null;
        closepopup();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}