package my.project.musicbrainz;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

public class MusicBrainzConnector {
	private static String musicBrainzUrl = "https://musicbrainz.org/ws/2/";
	
	
	public static Path downloadXML(String type, String id, String targetDirectory) throws IOException {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String resourceAddress = prepareUrl(type, id);
		System.out.println(resourceAddress);
	    URL url = new URL(prepareUrl(type, id));
	    String fileName = type + "-" + id + ".xml";
	    Path targetPath = new File(targetDirectory + File.separator + fileName).toPath();
	    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
	    httpConnection.setRequestMethod("GET");
	    httpConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
	    httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36");
	    httpConnection.setDoOutput(true);
	    Files.copy(httpConnection.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
	    return targetPath;
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
