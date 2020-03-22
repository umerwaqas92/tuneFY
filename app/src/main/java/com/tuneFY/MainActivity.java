package com.tuneFY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.BassBoost;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;

import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chibde.visualizer.CircleBarVisualizer;
import com.chibde.visualizer.LineBarVisualizer;
import com.gauravk.audiovisualizer.model.AnimSpeed;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    //Audio
    private Button mOn;
    private boolean isOn;
    private boolean isRecording;
    private AudioRecord record;
    private AudioTrack player;
    private AudioTrack player2;
    private AudioManager manager;
    private int recordState, playerState;
    private int minBuffer;

    //Audio Settings
    private final int source = MediaRecorder.AudioSource.DEFAULT;
    private final int channel_in = AudioFormat.CHANNEL_IN_MONO;
    private final int channel_out = AudioFormat.CHANNEL_OUT_MONO;
    private final int format = AudioFormat.ENCODING_PCM_16BIT;

    private final static int REQUEST_ENABLE_BT = 1;
    private boolean IS_HEADPHONE_AVAILBLE=false;


    SeekBar sekbar_mic_volume ;
    SeekBar sekbar_mic_volume1 ;

    BarChart chart;
    BarVisualizer barVisualizer1,barVisualizer ;
    boolean isauto_tune_on;

    boolean isRecording_audio=false;

    Context c;


     MediaRecorder recorder;


     ProgressBar progressBar;
     TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c = this;

        setVolumeControlStream(AudioManager.MODE_IN_COMMUNICATION);






        mOn = (Button) findViewById(R.id.button);
        isOn = false;
        isRecording = false;

        sekbar_mic_volume = (SeekBar)findViewById(R.id.seekBar2);
        sekbar_mic_volume1 = (SeekBar)findViewById(R.id.seekBar);

        manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        manager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        chart = (BarChart) findViewById(R.id.chart);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView11);


        barVisualizer1 = findViewById(R.id.barvisulizer2);
         barVisualizer = findViewById(R.id.barvisulizer);




        BarData data = new BarData(getDataSet());
        chart.setData(data);

        chart.animateXY(2000, 2000);
        chart.invalidate();


        //Check for headset availability
//        AudioDeviceInfo[] audioDevices = manager.getDevices(AudioManager.GET_DEVICES_ALL);
//        for(AudioDeviceInfo deviceInfo : audioDevices) {
//            if (deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET || deviceInfo.getType() == AudioDeviceInfo.TYPE_USB_HEADSET) {
//                IS_HEADPHONE_AVAILBLE = true;
//            }
//        }
        IS_HEADPHONE_AVAILBLE = true;

//        if (!IS_HEADPHONE_AVAILBLE){
            // get delete_audio_dialog.xml view

            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
//            View promptView = layoutInflater.inflate(R.layout.insert_headphone_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
//            alertDialogBuilder.setView(promptView);

            // setup a dialog window
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(getIntent()));
                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                                    dialog.cancel();
                                }
                            });

            // create an alert dialog
            AlertDialog alert = alertDialogBuilder.create();
//            alert.show();
//        }

        initAudio();
        setgone_progress();

//        barVisualizer.setAudioSessionId(player2.getAudioSessionId());
        barVisualizer1.setAnimationSpeed(AnimSpeed.FAST);
        barVisualizer.setAnimationSpeed(AnimSpeed.FAST);
//        barVisualizer1.setAudioSessionId(player.getAudioSessionId());

        mOn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            123);
                    return;
                }
                mOn.setBackgroundResource(R.drawable.onn_mic);
                isOn = !isOn;
                if(isOn) {
                    (new Thread() {
                        @Override
                        public void run()
                        {


                            startAudio();
//                            MediaPlayer mp = MediaPlayer.create(MainActivity.this,R.raw.testaudio);
//                            mp.start();


//                            mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//
//                            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//                            mAudioManager.setSpeakerphoneOn(false);

                        }
                    }).start();
                } else {
                    endAudio();
                    mOn.setBackgroundResource(R.drawable.off_mic);

                }
            }
        });


       final AudioManager  audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        sekbar_mic_volume1.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        float vol = Prerances.volume_music(c);//((float)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)/(float)15.0)*100;

        Log.e("vol",AudioManager.STREAM_MUSIC +"");
        sekbar_mic_volume1.setProgress((int) vol);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);

        sekbar_mic_volume1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                final float vol=(float)progress/ (float) 100;


                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        int vol1 = (int) (vol *15);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol1, 0);
                        Prerances.volume_music(MainActivity.this,progress);

                        Log.e("vol",vol1 +"");
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


//        sekbar_mic_volume1.setMax(player.getM(AudioManager.STREAM_MUSIC));
        sekbar_mic_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        float vol=((float)progress/ (float) 100)*2;
                        player.setVolume((int)vol);
                        player2.setVolume((int)vol);
//                        MediaPlayer.
                        Log.e("vol",vol +"");
                    }
                });

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initAudio();
    }

    int sec_recored =0;

    private void setvisible_progress(){
        textView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

       Timer t= new Timer();
       t.scheduleAtFixedRate(new TimerTask() {
           @Override
           public void run() {
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      textView.setText(sec_recored+"");
                  }
              });
               sec_recored ++;


           }
       },1000,1000);
    }

    private void setgone_progress(){
        textView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }


    public int getAudiSource(){
        if (Prerances.save_audio_source(this) == 0){
            return MediaRecorder.AudioSource.DEFAULT;
        }else if (Prerances.save_audio_source(this) == 1){
            return MediaRecorder.AudioSource.MIC;
        }if (Prerances.save_audio_source(this) == 2){
            return MediaRecorder.AudioSource.CAMCORDER;
        }if (Prerances.save_audio_source(this) == 3){
            return MediaRecorder.AudioSource.VOICE_CALL;
        }
        return -1;
    }



    public void initAudio() {
        //Tests all sample rates before selecting one that works
        int sample_rate = getSampleRate();
        minBuffer = AudioRecord.getMinBufferSize(sample_rate, channel_in, format);

        record = new AudioRecord(getAudiSource(), sample_rate, channel_in, format, minBuffer);
        recordState = record.getState();
        int id = record.getAudioSessionId();

        Log.d("Record", "ID: " + id);
        playerState = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            player = new AudioTrack(
                    new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build(),
                    new AudioFormat.Builder().setEncoding(format).setSampleRate(sample_rate).setChannelMask(channel_out).build(),
                    minBuffer,
                    AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE);

            player2 = new AudioTrack(
                    new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build(),
                    new AudioFormat.Builder().setEncoding(format).setSampleRate(sample_rate).setChannelMask(channel_out).build(),
                    minBuffer,
                    AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE);
//            player2.setVolume(0);

            playerState = player.getState();

            // Formatting Audio
//            if(AcousticEchoCanceler.isAvailable()) {
//                AcousticEchoCanceler echo = AcousticEchoCanceler.create(id);
//                echo.setEnabled(true);
//                Log.d("Echo", "Off");
//            }
//            if(NoiseSuppressor.isAvailable()) {
//                NoiseSuppressor noise = NoiseSuppressor.create(id);
//                noise.setEnabled(true);
//                Log.d("Noise", "Off");
//            }
//            if(AutomaticGainControl.isAvailable()) {
//                AutomaticGainControl gain = AutomaticGainControl.create(id);
//                gain.setEnabled(true);
//                Log.d("Gain", "Off");
//            }
//
//            BassBoost base = new BassBoost(10, player.getAudioSessionId());
//
//            base.setStrength((short) 1000);




//            mediaPlayer.start();

        }
    }
    int read = 0, write = 0;

    public void startAudio() {
       read = 0;
       write = 0;







        LineBarVisualizer lineBarVisualizer = findViewById(R.id.visualizer);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.testaudio);

// set custom color to the line.
//            lineBarVisualizer.setBackgroundColor(Color.BLACK);
        lineBarVisualizer.setColor(ContextCompat.getColor(this, R.color.colorPrimary));

// define custom number of bars you want in the visualizer between (10 - 256).
        lineBarVisualizer.setDensity(100);

//            lineBarVisualizer.set

// Set you media player to the visualizer.
        lineBarVisualizer.setPlayer(player.getAudioSessionId());


        if(recordState == AudioRecord.STATE_INITIALIZED && playerState == AudioTrack.STATE_INITIALIZED) {
            record.startRecording();
            player.play();
//            player2.play();

            isRecording = true;
            int audioSessionId = player.getAudioSessionId();
            if (audioSessionId != -1){

//                BarVisualizer barVisualizer = findViewById(R.id.barvisulizer);

            }

            Log.d("Record", "Recording...");
        }
        while(isRecording) {
            byte[] audioData = new byte[minBuffer];
            if(record != null)
                read = record.read(audioData, 0, minBuffer);
            else
                break;
            Log.d("Record", "Read: " + read);
            if(player != null) {


                final byte finalAudioData[]=audioData;
                final int finalRead=read;



                player.write(finalAudioData, 0, finalRead);

//                player2.write(finalAudioData, 0, finalRead);

//                  player2.write(audioData,0, read);



//                barVisualizer1.setRawAudioBytes(audioData);


//                barVisualizer.setRawAudioBytes(audioData);



            } else
                break;
            Log.d("Record", "Write: " + write);
        }
    }

    public void endAudio() {
//        barVisualizer.clearAnimation();
//        barVisualizer1.clearAnimation();

        if(record != null) {
            if(record.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
                record.stop();
            isRecording = false;
            Log.d("Record", "Stopping...");
        }
        if(player != null) {
            if(player.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
                player.stop();
//            player2.stop();
            isRecording = false;
            Log.d("Player", "Stopping...");
        }
    }

    public int getSampleRate() {
        //Find a sample rate that works with the device
        for (int rate : new int[] {8000, 11025, 16000,  22050, 44100, 48000}) {
            int buffer = AudioRecord.getMinBufferSize(rate, channel_in, format);
            if (buffer > 0)
                return rate;
        }
        return -1;
    }



    private ArrayList getDataSet() {
        ArrayList dataSets = null;

        ArrayList valueSet1 = new ArrayList();
        BarEntry v1e1 = new BarEntry(110.000f, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(40.000f, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(60.000f, 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(30.000f, 3); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(90.000f, 4); // May
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(100.000f, 5); // Jun
        valueSet1.add(v1e6);

        ArrayList valueSet2 = new ArrayList();
        BarEntry v2e1 = new BarEntry(150.000f, 0); // Jan
        valueSet2.add(v2e1);
        BarEntry v2e2 = new BarEntry(90.000f, 1); // Feb
        valueSet2.add(v2e2);
        BarEntry v2e3 = new BarEntry(120.000f, 2); // Mar
        valueSet2.add(v2e3);
        BarEntry v2e4 = new BarEntry(60.000f, 3); // Apr
        valueSet2.add(v2e4);
        BarEntry v2e5 = new BarEntry(20.000f, 4); // May
        valueSet2.add(v2e5);
        BarEntry v2e6 = new BarEntry(80.000f, 5); // Jun
        valueSet2.add(v2e6);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Brand 1");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Brand 2");
        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }


    public void auto_tune_on(View v){
        int id  = record.getAudioSessionId();
        Button button =(Button) v;

        if(isauto_tune_on){
            isauto_tune_on = false;
            button.setBackgroundResource(R.drawable.auto_tune_off);

            if(AcousticEchoCanceler.isAvailable()) {
                AcousticEchoCanceler echo = AcousticEchoCanceler.create(id);
                echo.setEnabled(false);
                Log.d("Echo", "Off");
            }
            if(NoiseSuppressor.isAvailable()) {
                NoiseSuppressor noise = NoiseSuppressor.create(id);
                noise.setEnabled(false);
                Log.d("Noise", "Off");
            }
            if(AutomaticGainControl.isAvailable()) {
                AutomaticGainControl gain = AutomaticGainControl.create(id);
                gain.setEnabled(false);
                Log.d("Gain", "Off");
            }

            BassBoost base = new BassBoost(10, player.getAudioSessionId());

            base.setStrength((short) 0);

        }else {
            isauto_tune_on = true;
            button.setBackgroundResource(R.drawable.auto_tune);

            if(AcousticEchoCanceler.isAvailable()) {
                AcousticEchoCanceler echo = AcousticEchoCanceler.create(id);
                echo.setEnabled(true);
                Log.d("Echo", "Off");
            }
            if(NoiseSuppressor.isAvailable()) {
                NoiseSuppressor noise = NoiseSuppressor.create(id);
                noise.setEnabled(true);
                Log.d("Noise", "Off");
            }
            if(AutomaticGainControl.isAvailable()) {
                AutomaticGainControl gain = AutomaticGainControl.create(id);
                gain.setEnabled(true);
                Log.d("Gain", "Off");
            }

            BassBoost base = new BassBoost(10, player.getAudioSessionId());

            base.setStrength((short) 1000);

        }
    }

    private ArrayList getXAxisValues() {
        ArrayList xAxis = new ArrayList();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        return xAxis;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initAudio();
    }

    public void audio_source_clicked(View v){
      mOn.callOnClick();

        startActivityForResult(new Intent(this,Audio_Source.class),123);
    }


    public void help(View v){
        startActivity(new Intent(this,Help.class));
    }

    public void output_mode_clicked(View v){
        startActivityForResult(new Intent(this,output_source.class),123);
    }


    public void record(final View v){


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());


        String fileName="Tunefy "+timeStamp+".mp3";

        File dir= new File(Environment.getExternalStorageDirectory() + File.separator
                + "Tunefy" + File.separator);

        ImageView imgbtn= (ImageView) v;
        if(isRecording_audio){
            recorder.stop();
            recorder.release();
            Snackbar.make(v,"File is saved at "+dir.getPath(),Snackbar.LENGTH_SHORT).show();
            isRecording_audio=false;
            imgbtn.setImageResource(R.drawable.recotnrd_btn);
            setgone_progress();
            sec_recored = 0;
            return;
        }


        imgbtn.setImageResource(R.drawable.recotnrd_stop_btn);


        if (!dir.exists()){
            dir.mkdir();
        }
         recorder = new MediaRecorder();
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.MediaColumns.TITLE, fileName);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(dir.getPath() +"/"+fileName);



        final ProgressDialog mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setTitle(R.string.lbl_recording);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setButton("Stop recording", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mProgressDialog.dismiss();

            }
        });

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
            public void onCancel(DialogInterface p1) {
                recorder.stop();
                recorder.release();
                Snackbar.make(v,"File is saved",Snackbar.LENGTH_SHORT).show();

            }
        });
        try {
            recorder.prepare();
            recorder.start();
            isRecording_audio=true;
            setvisible_progress();
        } catch (Exception e){
            e.printStackTrace();
        }


//        mProgressDialog.show();
    }
}
