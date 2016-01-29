package practice.hanchen.autoscrolllyrics;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
	static final String LOG_TAG = "MainActivity";
	private LyricsManager lyricsManager;
	private MediaPlayer mediaPlayer;
	private ListView listViewLyrics;
	private Timer timer;
	private LyricsAdapter lyricsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		InputStream inputStream;
		AssetManager am = getAssets();
		try {
			inputStream = am.open("lyrics.xml");
			lyricsManager = new LyricsManager();
			lyricsManager.parseLyricsAndTime(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		lyricsAdapter = new LyricsAdapter(getApplicationContext(), lyricsManager);
		listViewLyrics = (ListView) findViewById(R.id.listview_lyrics);
		listViewLyrics.setAdapter(lyricsAdapter);

		playSongAndSyncLyrics();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void playSongAndSyncLyrics() {
		Log.d(LOG_TAG, "start");
		lyricsManager.setAbsFirstLyricsChildPosition(0);
		lyricsManager.setCurrentLyricsPosition(1);
		if (mediaPlayer == null) {
			mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notyourkindpeople);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.start();
			timer = new Timer();
			timer.scheduleAtFixedRate(new MusicTimerTask(), 0, 300);
		}

		if (!mediaPlayer.isPlaying()) {
			//listViewLyrics.setSelection(0);
			mediaPlayer.start();
			timer = new Timer();
			timer.scheduleAtFixedRate(new MusicTimerTask(), 0, 300);
		}
	}

	private class MusicTimerTask extends TimerTask {
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mediaPlayer != null && mediaPlayer.isPlaying()) {
						if (lyricsManager.shouldChangeLine(mediaPlayer)) {
							if (listViewLyrics.getFirstVisiblePosition() != lyricsManager.getAbsFirstLyricsChildPosition()) {
								listViewLyrics.setSelection(lyricsManager.getAbsFirstLyricsChildPosition());
							} else {
								if (listViewLyrics.getChildCount() > 0) {
									if (lyricsManager.getCenterPosition() == 0) {
										lyricsManager.setCenterPosition(listViewLyrics.getChildCount() / 2);
									}

									if (lyricsManager.getCurrentLyricsPosition() + 1 >= lyricsManager.getCenterPosition()) {
										if (listViewLyrics.getLastVisiblePosition() == listViewLyrics.getCount() - 1) {
											lyricsManager.addCurrentLyricsPosition();
											changeLine(lyricsManager.getCurrentLyricsPosition());
										} else {
											lyricsManager.addAbsFirstLyricsChildPosition();
											changeLine(lyricsManager.getCurrentLyricsPosition() + 1);
											listViewLyrics.setSelection(lyricsManager.getAbsFirstLyricsChildPosition());
										}
									} else {
										lyricsManager.addCurrentLyricsPosition();
										changeLine(lyricsManager.getCurrentLyricsPosition());
									}
								}
							}
						}

					} else {
						playSongAndSyncLyrics();
					}
				}
			});
		}
	}

	private void changeLine(int position) {
		View viewLyrics;
		TextView labelLyrics;

		if (position > 0) {
			viewLyrics = listViewLyrics.getChildAt(position - 1);
			labelLyrics = (TextView) viewLyrics.findViewById(R.id.label_lyrics);
			labelLyrics.setTextColor(Color.BLACK);
		}

		if (position < listViewLyrics.getChildCount() - 1) {
			viewLyrics = listViewLyrics.getChildAt(position);
			labelLyrics = (TextView) viewLyrics.findViewById(R.id.label_lyrics);
			labelLyrics.setTextColor(Color.BLUE);
		}
	}
}
