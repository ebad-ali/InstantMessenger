package innovate.ebad.com.instantmessenger.Adapter;


import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.apg.mobile.roundtextview.RoundTextView;

import java.io.IOException;
import java.util.ArrayList;

import innovate.ebad.com.instantmessenger.Model.MessageModel;
import innovate.ebad.com.instantmessenger.R;

public class MainMessageAdapter extends ArrayAdapter<MessageModel> {

    ArrayList<MessageModel> messageList;
    Context context;

    private boolean intialStage = true;
    private boolean playPause;


    private MediaPlayer mediaPlayer;

    public MainMessageAdapter(Context context, ArrayList<MessageModel> messageList) {
        super(context, R.layout.custom_message_reciever_layout, messageList);
        this.context = context;
        this.messageList = messageList;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = null;


        if (messageList.get(position).getType().equals("message")) {

            if (!messageList.get(position).isCheck()) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                rowView = inflater.inflate(R.layout.custom_message_reciever_layout, parent, false);

                RoundTextView mytext = (RoundTextView) rowView.findViewById(R.id.recieverTextView);
                TextView timeView = (TextView) rowView.findViewById(R.id.recieverTimeTextView);


                mytext.setText(messageList.get(position).getMessage());
                timeView.setText(messageList.get(position).getMessageTime());

            } else {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                rowView = inflater.inflate(R.layout.custom_message_sender_layout, parent, false);

                RoundTextView mytext = (RoundTextView) rowView.findViewById(R.id.senderTextView);
                TextView timeView = (TextView) rowView.findViewById(R.id.senderTimeTextView);

                mytext.setText(messageList.get(position).getMessage());
                timeView.setText(messageList.get(position).getMessageTime());
            }

        } else {

            if (!messageList.get(position).isCheck()) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                rowView = inflater.inflate(R.layout.custom_message_reciever_music_layout, parent, false);

                TextView mytext = (TextView) rowView.findViewById(R.id.songNameTextView);
                final ImageButton playButton = (ImageButton) rowView.findViewById(R.id.songPlayButton);

                mytext.setText(messageList.get(position).getMessage());


                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.e("playbuttonworkingrecieverside", "yes");

                        if (!playPause) {
                            if (intialStage) {
                                new Player()
                                        .execute(messageList.get(position).getUri());
                                playButton.setImageResource(R.drawable.ic_pause_white_icon);
                            } else {
                                if (!mediaPlayer.isPlaying())
                                    mediaPlayer.start();
                                playButton.setImageResource(R.drawable.ic_pause_white_icon);
                            }
                            playPause = true;
                        } else {
                            playButton.setImageResource(R.drawable.ic_play_white_icon);
                            if (mediaPlayer.isPlaying())
                                mediaPlayer.pause();
                            playPause = false;
                        }
                    }
                });
            } else {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                rowView = inflater.inflate(R.layout.custom_message_sender_music_layout, parent, false);

                TextView mytext = (TextView) rowView.findViewById(R.id.songNameTextView);
                final ImageButton playButton = (ImageButton) rowView.findViewById(R.id.playButton);

                mytext.setText(messageList.get(position).getMessage());

                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("playbuttonworkding", "yes");
                        if (!playPause) {

                            if (intialStage) {
                                new Player()
                                        .execute(messageList.get(position).getUri());
                                playButton.setImageResource(R.drawable.ic_pause_icon);
                            } else {
                                if (!mediaPlayer.isPlaying())
                                    mediaPlayer.start();
                                playButton.setImageResource(R.drawable.ic_pause_icon);
                            }
                            playPause = true;
                        } else {
                            playButton.setImageResource(R.drawable.ic_play_icon);
                            if (mediaPlayer.isPlaying())
                                mediaPlayer.pause();
                            playPause = false;
                        }
                    }
                });
            }
        }
        return rowView;
    }

    private class Player extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progress;


        public Player() {
            progress = new ProgressDialog(context);
            progress.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {

                mediaPlayer.setDataSource(params[0]);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        intialStage = true;
                        playPause = false;
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                //  Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.cancel();
            }
            mediaPlayer.start();
            intialStage = false;
        }


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            this.progress.setMessage("Buffering...");
            this.progress.show();
        }
    }


}
