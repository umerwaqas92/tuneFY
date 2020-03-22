package com.tuneFY;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;

public class Audio_Source extends AppCompatActivity {


    RadioGroup rg;
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio__source);
        rg = findViewById(R.id.rg);
        c=this;

        rg.check(getCheckedId());

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
               if(R.id.MIC == checkedId){
                   Prerances.save_audio_source(c,1);
               }else if(R.id.DEFAULT == checkedId){
                   Prerances.save_audio_source(c,0);
          }else if(R.id.VOICE_CALL == checkedId){
                   Prerances.save_audio_source(c,3);
               }else if(R.id.CAMCORDER == checkedId){
                   Prerances.save_audio_source(c,2);

               }
            }
        });
    }

    public int getCheckedId(){
        if(Prerances.save_audio_source(c) == 0){
            return R.id.DEFAULT;
        }else  if(Prerances.save_audio_source(c) == 1){
            return R.id.MIC;
        }else  if(Prerances.save_audio_source(c) == 2){
            return R.id.CAMCORDER;
        }else  if(Prerances.save_audio_source(c) == 3){
            return R.id.VOICE_CALL;
        }
        return  0;
    }
}
