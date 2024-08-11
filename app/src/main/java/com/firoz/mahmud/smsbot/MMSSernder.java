package com.firoz.mahmud.smsbot;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class MMSSernder extends AccessibilityService {

    public static boolean isitrunning=false;
    Handler hand;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        isitrunning=true;
        hand=new Handler();






    }

    public static boolean ismms=false;
public static boolean startmms=false;
    public void sendSms(Map<String,Integer> users, Map<String,Long>timet) throws Exception {
        SharedPreferences sp=getSharedPreferences("data",MODE_PRIVATE);
        Set<String> keys=users.keySet();
        Iterator<String> ite=keys.iterator();
        while(ite.hasNext()){
            String address=ite.next();
            int old=sp.getInt(address+"count",0);
            int newc=users.get(address);
            if( old<newc){
                JSONArray arr=new JSONArray(sp.getString("sms","[]"));
                int index=sp.getInt(address+"index",0);
                if(index>=arr.length()) {
                    continue;
                }
                JSONObject jo=arr.getJSONObject(index);
                long lastsms=sp.getLong(address+"last",-2);
                if(lastsms==-2){
                    SharedPreferences.Editor she=sp.edit();
                    she.putLong(address+"last",System.currentTimeMillis());
                    she.apply();
                    lastsms=System.currentTimeMillis();
                }
                long dis= TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())-TimeUnit.MILLISECONDS.toSeconds(lastsms);
                if (dis<jo.getInt("time"))continue;


                if(index==arr.length()-1) {
                    SharedPreferences.Editor she = sp.edit();
                    she.putInt(address + "followindex", -1);
                    she.apply();
                }



                if(jo.has("img")) {
                    try {
                        SharedPreferences.Editor spe=sp.edit();
                        spe.putString("image",jo.getString("img"));
                        spe.putString("message",jo.getString("msg"));
                        spe.putString("number",address);
                        spe.apply();
                        startmms=true;

                        while(startmms){
                            try {
                                Thread.sleep(100);
                            }catch (Exception e){}
                        }

                        AccessibilityNodeInfo ani=MMSSernder.this.getRootInActiveWindow();

                        AccessibilityNodeInfo out=nodetotext(ani,"MMS");
                        while(out==null) {
                        Thread.sleep(100);
                        ani=MMSSernder.this.getRootInActiveWindow();
                        out=nodetotext(ani,"MMS");
                        }
//    out.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            out.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            ismms=false;

                    }catch (Exception e){
                        int a=10;
                    }
                }else {

                    SmsManager sms = SmsManager.getDefault();
                    sms.sendMultipartTextMessage(address, null, sms.divideMessage(jo.getString("msg")), null, null);
                }
                SharedPreferences.Editor she=sp.edit();
                she.putInt(address+"count",newc);
                she.putInt(address+"index",index+1);
                she.putLong(address+"last",System.currentTimeMillis());
                she.apply();



            }else{
                JSONArray arr=new JSONArray(sp.getString("followup","[]"));
                int index=sp.getInt(address+"followindex",0);
                if(index==-1)continue;
                if(arr.length()<=index)continue;

                JSONObject jo=arr.getJSONObject(index);

                long dis= TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis())-TimeUnit.MILLISECONDS.toMinutes(sp.getLong(address+"last",System.currentTimeMillis()));


                if (dis>=jo.getInt("time")){



                    if(jo.has("img")) {
                        try {
                            SharedPreferences.Editor spe=sp.edit();
                            spe.putString("image",jo.getString("img"));
                            spe.putString("message",jo.getString("msg"));
                            spe.putString("number",address);
                            spe.apply();
                            startmms=true;

                            while(startmms){
                                try {
                                    Thread.sleep(100);
                                }catch (Exception e){}
                            }

                            AccessibilityNodeInfo ani=MMSSernder.this.getRootInActiveWindow();

                            AccessibilityNodeInfo out=nodetotext(ani,"MMS");
                            while(out==null) {
                                Thread.sleep(100);
                                ani=MMSSernder.this.getRootInActiveWindow();
                                out=nodetotext(ani,"MMS");
                            }
//    out.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            out.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            ismms=false;

                        }catch (Exception e){
                            int a=10;
                        }
                    }else{
                        SmsManager sms=SmsManager.getDefault();
                        sms.sendMultipartTextMessage(address,null,sms.divideMessage(jo.getString("msg")),null,null);
                    }


                    SharedPreferences.Editor she=sp.edit();
                    she.putInt(address+"followindex",index+1);
                    she.putLong(address+"last",System.currentTimeMillis());
                    she.apply();
                }

            }
        }





    }



    public void readSms() throws Exception {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                null,
                null,
                null,
                null);



        Map<String,Integer> users=new HashMap<String,Integer>();
        Map<String,Long>utime=new HashMap<String,Long>();


        if (cursor != null && cursor.moveToFirst()) {

            do {
                String type = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE));
                if(type.equals("2"))continue;
                String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String datet = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));
                if(!isAddressValid(address))continue;
//                String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                if(users.containsKey(address)){
                    users.put(address,users.get(address)+1);
                    if((utime.get(address))<Long.valueOf(datet)){
                        utime.put(address,Long.valueOf(datet));
                    }
                }else{
                    users.put(address,1);
                    utime.put(address,Long.valueOf(datet));
                }


//                Log.e("Something","We did it");


//                smsList.add("Sender: " + address + "\nMessage: " + body);

            } while (cursor.moveToNext());
        }


        sendSms(users,utime);

        if (cursor != null) {
            cursor.close();
        }
    }

    public boolean isAddressValid(String address){
        char charonly[]=address.toCharArray();
        for(char a:charonly){
            if ((a<='9'&&a>='0')||a=='+'||a=='-'||a=='('||a==')'){
                continue;
            }else{
                return false;
            }

        }
        return true;
    }

public static boolean shouldstart=false;

    public String something(AccessibilityNodeInfo root){
        String data="";
        try {
            String text= root.getText().toString();
            data+=text;
        }catch (Exception e){}
        String ani=null;
        if(root==null)return null;
        for(int i=0;i<root.getChildCount();i++){
            ani=something(root.getChild(i));
            if(ani!=null){
                data+=ani;
            }
        }
        return data;
    }
    boolean wehaveclick=false;
Thread thread=null;
    @Override
    public void onAccessibilityEvent(AccessibilityEvent ae) {
        try {
            String pack = ae.getPackageName().toString();
            if (pack.equals("android")) {
                AccessibilityNodeInfo ni = nodetotext(getRootInActiveWindow(), "Messages");
                if (ni != null) {
                    ni.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    ni.getParent().recycle();

                }
            }
//            else if (pack.equals("com.google.android.apps.messaging")){
//                AccessibilityNodeInfo ni = nodetotext(getRootInActiveWindow(), "MMS");
//                if(ni!=null){
//                    ni.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    ni.getParent().recycle();
//                    wehaveclick=true;
//                }
//            }
        }catch (Exception e){}





        try{
            if (thread==null){
                thread=new Thread(){
                    @Override
                    public void run() {
                            SharedPreferences sp=MMSSernder.this.getSharedPreferences("data",MODE_PRIVATE);
                            while(true) {
                        try {
                            if(shouldstart) {

                                            readSms();

                            }
                                sleep(1000);
                        }catch (Exception e){}
                            }
                    }
                };
                thread.start();
            }
        }catch (Exception e){}

    }



    @Override
    public void onInterrupt() {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }








    public AccessibilityNodeInfo nodetotext(AccessibilityNodeInfo root,String data){
        try {
            String text= root.getText().toString();
            if (text.contentEquals(data)){
                return root;
            }
        }catch (Exception e){}
        AccessibilityNodeInfo ani=null;
        if(root==null)return null;
        for(int i=0;i<root.getChildCount();i++){
            ani=nodetotext(root.getChild(i),data);
            if(ani!=null){
                return ani;
            }
        }
        return ani;
    }






}









