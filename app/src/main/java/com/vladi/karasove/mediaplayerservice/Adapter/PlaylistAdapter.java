package com.vladi.karasove.mediaplayerservice.Adapter;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.vladi.karasove.mediaplayerservice.CallBack.CallBack_AdapterToMain;
import com.vladi.karasove.mediaplayerservice.Object.Audio;
import com.vladi.karasove.mediaplayerservice.R;

import java.util.ArrayList;

public class PlaylistAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<Audio> songs;
    private CallBack_AdapterToMain callBack_adapterToMain;

    public PlaylistAdapter() {}

    public void setCallBack_adapterToPlaylist(CallBack_AdapterToMain callback){
        this.callBack_adapterToMain=callback;
    }
    public PlaylistAdapter(Context context, ArrayList<Audio> songs) {
        this.context = context;
        this.songs = songs;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Audio tempSong = songs.get(position);
        MyViewHolder mvh = (MyViewHolder) holder;
        mvh.setData(tempSong);

    }



    @Override
    public int getItemCount() {
        return songs.size();
    }

    private String getReadableTime(int duration){
        String time;
        int hrs = duration/(1000*60*60);
        int min = (duration%(1000*60*60))/(1000*60);
        int secs =(((duration%(1000*60*60))%(1000*60*60))%(1000*60))/1000;

        if (hrs < 1) {
            time = min +":"+secs;
        }else{
            time = hrs+":"+min+":"+secs;
        }
        return time;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView title;
        private MaterialCardView cardView;
        private MaterialTextView time;
        public MyViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.songList_LBL_title);
            cardView= view.findViewById(R.id.songList_LNL_song);
            time= view.findViewById(R.id.songList_LBL_time);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   // Log.d("pttt", "position:"+getAbsoluteAdapterPosition());
                    callBack_adapterToMain.clickedOnAudio(getAbsoluteAdapterPosition());
                }
            });
        }

        public void setData(Audio song) {
            title.setText(song.getTitle());
            time.setText(getReadableTime(song.getDuration()));
        }
    }
}
