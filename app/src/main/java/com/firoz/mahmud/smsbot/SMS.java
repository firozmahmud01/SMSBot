package com.firoz.mahmud.smsbot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class SMS extends Fragment {
    SharedPreferences sp;
    JSONArray sms;
    ListView lv;
    BaseAdapter ba;
    EditText dtime;
    Uri imageuri=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mv= inflater.inflate(R.layout.fragment_s_m_s, container, false);
        sp= getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        String olddata=sp.getString("sms", null);
        dtime=mv.findViewById(R.id.smstimedelayedittext);

        mv.findViewById(R.id.smsimageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,1000);

            }
        });


        try {
            sms=new JSONArray(olddata==null?"[]":olddata);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        lv=mv.findViewById(R.id.smslistview);
        ba=new BaseAdapter() {
            @Override
            public int getCount() {
                if (sms==null){
                    return 0;
                }else {
                    return sms.length();
                }
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if(view==null){
                    view=inflater.inflate(R.layout.listitem,null);
                }
                TextView tv=view.findViewById(R.id.listitemtextview);
                ImageView iv=view.findViewById(R.id.listitemimageview);
                TextView time=view.findViewById(R.id.listitemtimetextview);
                try {
                    JSONObject jo=sms.getJSONObject(i);
                    tv.setText(jo.getString("msg"));
                    time.setText("Delay:"+jo.get("time"));
                    if(jo.has("img")){
                        iv.setVisibility(View.VISIBLE);
                        Glide.with(getContext()).load(Uri.parse(jo.getString("img"))).into(iv);
                    }else{
                        iv.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                return view;
            }
        };

        lv.setAdapter(ba);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject jo = sms.getJSONObject(i);
                    Intent in = new Intent(getContext(), Edit.class);
                    in.putExtra("time", jo.getInt("time"));
                    in.putExtra("msg",jo.getString("msg"));
                    if (jo.has("img")) {
                        in.putExtra("img", jo.getString("img"));
                    }
                    in.putExtra("index", i);
                    startActivityForResult(in, 200);
                }catch (Exception e){}
            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                sms.remove(i);
                SharedPreferences.Editor she=sp.edit();
                she.putString("sms",sms.toString());
                she.apply();

                ba.notifyDataSetChanged();
                return true;
            }
        });
        mv.findViewById(R.id.smsaddbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser()==null)return;
                try {
                EditText ed=mv.findViewById(R.id.smsedittext);
                EditText time=mv.findViewById(R.id.smstimedelayedittext);
                if(ed.getText().toString().isEmpty()||time.getText().toString().isEmpty())return;
                JSONObject jo=new JSONObject();
                jo.put("msg",ed.getText().toString());
                if(imageuri!=null) {
                    jo.put("img",imageuri.toString());
                }
                jo.put("time",Integer.parseInt(time.getText().toString()));
                sms.put(jo);
                SharedPreferences.Editor she=sp.edit();
                she.putString("sms",sms.toString());
                she.apply();
                ba.notifyDataSetChanged();
                ed.setText("");
                imageuri=null;
                time.setText("");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return mv;
    }

    public Set<String> removefrom(Set<String>dd,int index){
        Set<String> mmm=new HashSet<>();
        Iterator<String>so=dd.iterator();
        int i=0;
        while(so.hasNext()){
            if(i==index) {
                i++;
                so.next();
                continue;
            }

            i++;
            mmm.add(so.next());
        }

        return mmm;
    }
    public Set<String> addto(Set<String>dd,String data){
        Set<String> mmm=new HashSet<>();
        Iterator<String>so=dd.iterator();
        mmm.add(data);
        while(so.hasNext()){
            mmm.add(so.next());
        }
        return mmm;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200){
            try {
                int index = data.getIntExtra("index", -1);
                if (index == -1) return;
                JSONArray ja = new JSONArray();
                for (int i = 0; i < sms.length(); i++) {
                    if (index == i) {
                        JSONObject jo=new JSONObject();
                        jo.put("time",data.getIntExtra("time",0));
                        jo.put("msg",data.getStringExtra("msg"));
                        String images=data.getStringExtra("img");
                        if(images!=null) {
                            jo.put("img",images);
                        }
                        ja.put(jo);
                    } else {
                        ja.put(sms.getJSONObject(i));
                    }
                }
                sms=ja;
                SharedPreferences.Editor she=sp.edit();
                she.putString("sms",sms.toString());
                she.apply();
                ba.notifyDataSetChanged();

            }catch (Exception e){};

            return;
        }
        if(requestCode==1000){
            imageuri=data.getData();
            try {
                Bitmap bit=MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),imageuri);


            imageuri=saveImage(bit);
            } catch (Exception e) {
int a=10;
            }
        }
    }

    private Uri saveImage(Bitmap image) {
        File imagePath = new File(getContext().getFilesDir(), "external_files");
        imagePath.mkdir();
        File imageFile = new File(imagePath.getPath(), "img"+System.currentTimeMillis()+".jpg");
        Uri uri = null;
        try {

            FileOutputStream stream = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(
                    SMS.this.getContext(),
                    "com.firoz.mahmud.smsbot.fileprovider", //(use your app signature + ".provider" )
                    imageFile);

        } catch (Exception e) {
            int a=10;
        }
        return uri;
    }

}