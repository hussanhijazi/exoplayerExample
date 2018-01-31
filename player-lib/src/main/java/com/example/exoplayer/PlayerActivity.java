/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
* limitations under the License.
 */
package com.example.exoplayer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity {

  private SimpleExoPlayerView playerView;
  private SimpleExoPlayer player;
  private DonutProgress progress;
  private View exoShutter;
  private boolean playWhenReady;
  private int currentWindow = 0;
  private long playbackPosition = 0;
  private boolean durationSet = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);
    playerView = findViewById(R.id.video_view);
    progress = findViewById(R.id.donut_progress);
    progress.setTextColor(Color.TRANSPARENT);
    exoShutter = findViewById(R.id.exo_shutter);

  }

  private void initializePlayer() {
    player = ExoPlayerFactory.newSimpleInstance(
            new DefaultRenderersFactory(this),
            new DefaultTrackSelector(), new DefaultLoadControl());

    playerView.setPlayer(player);
    player.setPlayWhenReady(playWhenReady);
    player.seekTo(currentWindow, playbackPosition);
    playerView.setControllerShowTimeoutMs(0);
//    playerView.setShutterBackgroundColor(Color.RED);
    Uri uri = Uri.parse(getString(R.string.media_url_mp3));
    MediaSource mediaSource = buildMediaSource(uri);
    player.prepare(mediaSource, true, false);

    player.addListener(new Player.EventListener() {
      @Override
      public void onTimelineChanged(Timeline timeline, Object manifest) {

      }

      @Override
      public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

      }

      @Override
      public void onLoadingChanged(boolean isLoading) {

      }

      @Override
      public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY && !durationSet) {
          long realDurationMillis = player.getDuration();
          durationSet = true;
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
            Log.d("h2", player.getCurrentPosition()+"");
            progress.setProgress(player.getCurrentPosition());
            handler.postDelayed(this, 500);
          }
        }, 500);
        Log.d("h2 - Duration", player.getDuration()+"");
        progress.setMax((int)player.getDuration());
      }

      @Override
      public void onRepeatModeChanged(int repeatMode) {

      }

      @Override
      public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

      }

      @Override
      public void onPlayerError(ExoPlaybackException error) {

      }

      @Override
      public void onPositionDiscontinuity(int reason) {

      }

      @Override
      public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

      }

      @Override
      public void onSeekProcessed() {

      }
    });
    Log.d("h22", player.getDuration()+"");

  }
  private MediaSource buildMediaSource(Uri uri) {
    return new ExtractorMediaSource.Factory(
            new DefaultHttpDataSourceFactory("exoplayer-codelab")).
            createMediaSource(uri);
  }

  @Override
  public void onStart() {
    super.onStart();
    if (Util.SDK_INT > 23) {
      initializePlayer();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    hideSystemUi();
    if ((Util.SDK_INT <= 23 || player == null)) {
      initializePlayer();
    }
  }
  @SuppressLint("InlinedApi")
  private void hideSystemUi() {
    playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
  }
  @Override
  public void onPause() {
    super.onPause();
    if (Util.SDK_INT <= 23) {
      releasePlayer();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (Util.SDK_INT > 23) {
      releasePlayer();
    }
  }
  private void releasePlayer() {
    if (player != null) {
      playbackPosition = player.getCurrentPosition();
      currentWindow = player.getCurrentWindowIndex();
      playWhenReady = player.getPlayWhenReady();
      player.release();
      player = null;
    }
  }

}
