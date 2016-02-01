package practice.hanchen.autoscrolllyrics;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by HanChen on 2016/1/29.
 */
public class LyricsAdapter extends BaseAdapter {
	static final String LOG_TAG = "LyricsAdapter";
	private LayoutInflater mInflater;
	private LyricsManager lyricsManager = null;

	public static class ViewHolder {
		public TextView textView;
	}

	public LyricsAdapter(Context context, LyricsManager lyricsManager) {
		mInflater = LayoutInflater.from(context);
		this.lyricsManager = lyricsManager;
	}

	@Override
	public int getCount() {
		return lyricsManager.getLyrics().size();
	}

	@Override
	public Object getItem(int position) {
		return lyricsManager.getLyrics().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.layout_lyrics_line, null);
			holder.textView = (TextView) convertView.findViewById(R.id.label_lyrics);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textView.setText(lyricsManager.getLyrics().get(position));
		if (position == lyricsManager.getCurrentPlayPosition()) {
			holder.textView.setTextColor(Color.BLUE);
		} else if (position == 0) {
			holder.textView.setTextColor(Color.RED);
		} else {
			holder.textView.setTextColor(Color.BLACK);
		}

		return convertView;
	}
}
