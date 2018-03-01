package my.project.musicbrainz;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class MusicBrainzConnector {
	private static String musicBrainzUrl = "https://musicbrainz.org/ws/2/";
	
	
	public static Path downloadXML(String type, String id, String targetDirectory) throws IOException {
		String resourceAddress = prepareUrl(type, id);
		System.out.println(resourceAddress);
	    URL url = new URL(prepareUrl(type, id));
	    String fileName = type + "-" + id + ".xml";
	    Path targetPath = new File(targetDirectory + File.separator + fileName).toPath();
	    Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
	    return targetPath;
	}
	
	private static String prepareUrl(String type, String id) {
		String urlSufix="";

		if (type.equalsIgnoreCase("release")) {
			urlSufix = "release/" + id + "?inc=aliases%2Bartist-credits%2Bdiscids%2Blabels%2Brecordings";
		}
		else if (type.equalsIgnoreCase("recording")) {
			urlSufix = "artist/" + id + "?inc=aliases%2Bartist-credits%2Bwork-rels%2Bartist-rels%2Breleases";
		}
		else if (type.equalsIgnoreCase("work")) {
			urlSufix = "artist/" + id + "?inc=aliases%2Bartist-rels";
		}
		else if (type.equalsIgnoreCase("artist")) {
			urlSufix = "artist/" + id + "?inc=aliases%2Bartist-rels";
		}
		return musicBrainzUrl + urlSufix;
	}
}
