package my.project.musicbrainz;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MusicBrainzConnector {
	private static String musicBrainzUrl = "https://musicbrainz.org/ws/2/";
	private static final Logger logger = LogManager.getLogger();
	
	public static InputStream downloadXML(String type, String id) throws IOException, InterruptedException {
		TimeUnit.SECONDS.sleep(1);
	    URL url = new URL(prepareUrl(type, id));
	    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
	    httpConnection.setRequestMethod("GET");
	    httpConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
	    httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36");
	    httpConnection.setDoOutput(true);
	    logger.debug("Downloading " + url);
	    return httpConnection.getInputStream();
	}
	
	private static String prepareUrl(String type, String id) {
		String urlSufix="";

		if (type.equalsIgnoreCase("release")) {
			urlSufix = type + "/" + id + "?inc=aliases%2Bartist-credits%2Bdiscids%2Blabels%2Brecordings";
		}
		else if (type.equalsIgnoreCase("recording")) {
			urlSufix = type + "/" + id + "?inc=aliases%2Bartist-credits%2Bwork-rels%2Bartist-rels%2Breleases";
		}
		else if (type.equalsIgnoreCase("work")) {
			urlSufix = type + "/" + id + "?inc=aliases%2Bartist-rels";
		}
		else if (type.equalsIgnoreCase("artist")) {
			urlSufix = type + "/" + id + "?inc=aliases%2Bartist-rels";
		}
		return musicBrainzUrl + urlSufix;
	}
}
