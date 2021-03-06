package practice.hanchen.autoscrolllyrics;

import android.media.MediaPlayer;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by HanChen on 2016/1/28.
 */
public class LyricsManager {
	static final String LOG_TAG = "LyricsManager";
	private ArrayList<Integer> startTimes;
	private ArrayList<Integer> endTimes;
	private ArrayList<String> lyrics;
	private int currentPlayPosition;
	private int centerPosition;

	public LyricsManager() {
		lyrics = new ArrayList<String>();
		startTimes = new ArrayList<Integer>();
		endTimes = new ArrayList<Integer>();
		currentPlayPosition = 1;
	}

	public ArrayList<String> getLyrics() {
		return lyrics;
	}

	public int getCurrentPlayPosition() {
		return currentPlayPosition;
	}

	public void setCurrentPlayPosition(int currentPlayPosition) {
		this.currentPlayPosition = currentPlayPosition;
	}

	public int getCenterPosition() {
		return centerPosition;
	}

	public void setCenterPosition(int centerPosition) {
		this.centerPosition = centerPosition;
	}

	public long getStartTimeFromLyrics(int position) {
		return TimeUnit.SECONDS.toMillis(startTimes.get(position));
	}

	public void parseLyricsAndTime(InputStream inputStream) {
		String stringFromXML;
		int timeInSecond;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			Element root = document.getDocumentElement();
			NodeList nodes = root.getElementsByTagName("item");
			for (int i = 0; i < nodes.getLength(); i++) {
				Node currentNode = nodes.item(i);
				Element parentNode = (Element) currentNode.getParentNode();
				if (currentNode.hasChildNodes()) {
					stringFromXML = currentNode.getFirstChild().getNodeValue();
				} else {
					stringFromXML = "";
				}
				switch (parentNode.getAttribute("name")) {
					case "array_lyrics":
						lyrics.add(stringFromXML);
						break;
					case "array_start_time":
						timeInSecond = Integer.parseInt(stringFromXML.split(":")[0]) * 60 + Integer.parseInt(stringFromXML.split(":")[1]);
						startTimes.add(timeInSecond);
						break;
					case "array_end_time":
						timeInSecond = Integer.parseInt(stringFromXML.split(":")[0]) * 60 + Integer.parseInt(stringFromXML.split(":")[1]);
						endTimes.add(timeInSecond);
						break;
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean shouldChangeLine(MediaPlayer mediaPlayer) {
		if (currentPlayPosition >= lyrics.size()) {
			return false;
		}

		long musicTimestamp = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition());
		if (musicTimestamp > endTimes.get(currentPlayPosition)) {
			if (endTimes.get(currentPlayPosition) == 0) {
				if (musicTimestamp >= startTimes.get(currentPlayPosition + 1)) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
}
