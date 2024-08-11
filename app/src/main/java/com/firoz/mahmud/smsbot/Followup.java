package com.firoz.mahmud.smsbot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

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


public class Followup extends Fragment {
    SharedPreferences sp;
    JSONArray sms;
    ListView lv;
    BaseAdapter ba;
    EditText timeedit;
    Uri imageuri=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mv= inflater.inflate(R.layout.fragment_followup, container, false);

        sp= getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        String olddata=sp.getString("followup", null);
        try {
            sms=new JSONArray(olddata==null?"[]":olddata);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        timeedit=mv.findViewById(R.id.followuptimeedittext);
        mv.findViewById(R.id.followupimageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,1010);

            }
        });
//        int time=sp.getInt("delay",60);
//        timeedit.setText(String.valueOf(time));

        lv=mv.findViewById(R.id.followuplistview);
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
                    startActivityForResult(in, 300);
                }catch (Exception e){}
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                sms.remove(i);
                SharedPreferences.Editor she=sp.edit();
                she.putString("followup",sms.toString());
                she.apply();

                ba.notifyDataSetChanged();
                return true;
            }
        });
        mv.findViewById(R.id.followupaddbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser()==null)return;
                try {
                    EditText ed=mv.findViewById(R.id.followupsmsedittext);
                    EditText time=mv.findViewById(R.id.followuptimeedittext);
                    if(ed.getText().toString().isEmpty()||time.getText().toString().isEmpty())return;
                    JSONObject jo=new JSONObject();
                    jo.put("msg",ed.getText().toString());
                    if(imageuri!=null) {
                        jo.put("img",imageuri.toString());
                    }
                    jo.put("time",Integer.parseInt(time.getText().toString()));
                    sms.put(jo);
                    SharedPreferences.Editor she=sp.edit();
                    she.putString("followup",sms.toString());
                    she.apply();
                    ba.notifyDataSetChanged();
                    ed.setText("");
                    imageuri=null;
                    time.setText("");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return mv;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==300){
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
                she.putString("followup",sms.toString());
                she.apply();
                ba.notifyDataSetChanged();

            }catch (Exception e){};

            return;
        }
        if(requestCode==1010){
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
                    Followup.this.getContext(),
                    "com.firoz.mahmud.smsbot.fileprovider", //(use your app signature + ".provider" )
                    imageFile);

        } catch (Exception e) {
            int a=10;
        }
        return uri;
    }
}