package com.tomek.luckynumber.model.utils;

import android.content.Context;
import android.media.MediaPlayer;


/**
 * Created by tomek on 04.02.16.
 */
public class SoundHelper {


    public static void play(Context context, int audio) {
        final MediaPlayer mplayer = MediaPlayer.create(context, audio);
        mplayer.start();
        mplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
                mediaPlayer.release();

            }
        });
    }

    private static void release(MediaPlayer mplayer) {
        mplayer.release();
    }
}
