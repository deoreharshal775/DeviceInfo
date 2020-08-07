package com.mentebit.deviceinfo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

class AdapterCallRecording extends RecyclerView.Adapter<AdapterCallRecording.Holder> {
    ArrayList<DataAutoCallRecord> arrayList;
    Context context;
    File directory;
    String audiofile, calllogdate;
    MediaPlayer mPlayer;
    AlertDialog alert;
    AlertDialog.Builder builder;
    ImageView muClose;
    TextView txtstarttm, txtclosetm, txtmuname;
    Button btnrevind, btnpause, btnplay, btnforward;
    private SeekBar seekBar;
    private Handler myHandler;
    private double startTime = 0;
    private double finalTime = 0;
    private int forwardTime = 5000;
    private int backwardTime = 5000;

    public AdapterCallRecording(ArrayList<DataAutoCallRecord> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_callrecording, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final DataAutoCallRecord details = arrayList.get(position);

        holder.userName.setText(details.getLeadName());
        holder.userContact.setText(details.getLeadNumber());
        holder.userDate.setText(details.getLeadDateCompare());

        //directory = new File(Environment.getExternalStorageDirectory() + "/Bizcall/LeadRecordings");
        directory = new File(MainActivity.sharedPath);
        directory.mkdirs();

        if (!details.getLeadRecName().equals("null")) {
            holder.playaudio.setVisibility(View.VISIBLE);
            holder.noaudio.setVisibility(View.GONE);
        } else {
            holder.playaudio.setVisibility(View.GONE);
            holder.noaudio.setVisibility(View.VISIBLE);
        }

        /*for (File f : directory.listFiles()) {
            audiofile = f.getName();
            if (details.getLeadRecName().equals(audiofile)) {
                holder.playaudio.setVisibility(View.VISIBLE);
                holder.noaudio.setVisibility(View.GONE);
            } else {
                holder.playaudio.setVisibility(View.GONE);
                holder.noaudio.setVisibility(View.VISIBLE);
            }
        }*/

        try {
            holder.playaudio.setOnClickListener(new View.OnClickListener() {
                @SuppressLint({"NewApi", "SetTextI18n", "DefaultLocale"})
                @Override
                public void onClick(View v) {
                    mPlayer = new MediaPlayer();
                    builder = new AlertDialog.Builder(context);
                    LayoutInflater li = LayoutInflater.from(context);
                    View musicUI = li.inflate(R.layout.lay_music_ui, null);

                    muClose = musicUI.findViewById(R.id.img_closeplayer);
                    txtstarttm = musicUI.findViewById(R.id.textView2);
                    txtclosetm = musicUI.findViewById(R.id.textView3);
                    txtmuname = musicUI.findViewById(R.id.textView4);
                    seekBar = musicUI.findViewById(R.id.seekBar);
                    btnrevind = musicUI.findViewById(R.id.button);
                    btnpause = musicUI.findViewById(R.id.button2);
                    btnplay = musicUI.findViewById(R.id.button3);
                    btnforward = musicUI.findViewById(R.id.button4);

                    builder.setView(musicUI);
                    builder.setCancelable(false);
                    alert = builder.create();
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alert.show();

                    txtmuname.setText(details.getLeadRecName());
                    seekBar.setClickable(false);

                    for (File f : directory.listFiles()) {
                        if (f.isFile()) {
                            audiofile = f.getName();
                        }
                        if (details.getLeadRecName().equals(audiofile)) {
                            try {
                                mPlayer.setDataSource(directory + "/" + details.getLeadRecName());
                                Log.d("RecPath", details.getLeadRecName());
                                mPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Can't Play", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    txtstarttm.setText(Math.toIntExact(TimeUnit.MILLISECONDS.toSeconds((long) mPlayer.getCurrentPosition())) + " Sec");
                    txtclosetm.setText(String.format("%d:%d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) mPlayer.getDuration()),
                            TimeUnit.MILLISECONDS.toSeconds((long) mPlayer.getDuration()) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mPlayer.getDuration()))));

                    muClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPlayer.stop();
                            myHandler = new Handler();
                            myHandler.removeCallbacksAndMessages(null);
                            alert.dismiss();
                        }
                    });

                    btnplay.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("DefaultLocale")
                        @Override
                        public void onClick(View v) {
                            startTime = 0;
                            finalTime = 0;
                            mPlayer.start();
                            Toast.makeText(context, "Playing sound", Toast.LENGTH_SHORT).show();

                            finalTime = mPlayer.getDuration();
                            startTime = mPlayer.getCurrentPosition();
                            Log.d("audiotime", startTime + " " + TimeUnit.MILLISECONDS.toSeconds((long) finalTime));

                            seekBar.setMax((int) finalTime);

                            txtclosetm.setText(String.format("%d:%d sec",
                                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));

                            txtstarttm.setText(String.format("%d:%d sec",
                                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));

                            //seekBar.setProgress((int) startTime);
                            myHandler = new Handler();
                            myHandler.postDelayed(UpdateSongTime, 1000);
                            btnpause.setVisibility(View.VISIBLE);
                            btnplay.setVisibility(View.GONE);
                        }
                    });

                    btnpause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "Pausing sound", Toast.LENGTH_SHORT).show();
                            mPlayer.pause();
                            btnpause.setVisibility(View.GONE);
                            btnplay.setVisibility(View.VISIBLE);
                        }
                    });

                    btnforward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int temp = (int) startTime;

                            if ((temp + forwardTime) <= finalTime) {
                                startTime = startTime + forwardTime;
                                mPlayer.seekTo((int) startTime);
                                Toast.makeText(context, "You have Jumped forward 5 seconds", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    btnrevind.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int temp = (int) startTime;

                            if ((temp - backwardTime) > 0) {
                                startTime = startTime - backwardTime;
                                mPlayer.seekTo((int) startTime);
                                Toast.makeText(context, "You have Jumped backward 5 seconds", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "AdapterFragmentLeads : Plugins-001", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        //Collections.reverse(arrayList);
        return arrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView userName, userContact, userDate;
        ImageView playaudio, noaudio;

        public Holder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.txt_username);
            userContact = itemView.findViewById(R.id.txt_usernumber);
            userDate = itemView.findViewById(R.id.txt_userdate);
            playaudio = itemView.findViewById(R.id.txt_leadplayaudio);
            noaudio = itemView.findViewById(R.id.txt_leadnoaudio);
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        @SuppressLint("DefaultLocale")
        public void run() {
            startTime = mPlayer.getCurrentPosition();

            txtstarttm.setText(String.format("%d:%d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
            );
            seekBar.setProgress((int) startTime);
            myHandler = new Handler();
            myHandler.postDelayed(UpdateSongTime, 1000);

            if (txtstarttm.getText().toString().equals(txtclosetm.getText().toString())) {
                mPlayer.pause();
                btnpause.setVisibility(View.GONE);
                btnplay.setVisibility(View.VISIBLE);
            }
        }
    };
}
