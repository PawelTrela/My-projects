package my.project.musicbrainz;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class XMLProvider {
	private String xmlFilesDirectoryLocation;

	
	public XMLProvider(String xmlFilesDirectoryLocation) {
		this.xmlFilesDirectoryLocation = xmlFilesDirectoryLocation;
	}
	
	public Path getRelease(String releaseId) throws IOException {
		return getXMLFile("release", releaseId);
	}
	
	public Path getRecording(String recordingId) throws IOException {
		return getXMLFile("recording", recordingId);
	}
	
	public Path getArtist(String artistId) throws IOException {
		return getXMLFile("artist", artistId);
	}
	
	public Path getWork(String workId) throws IOException {
		return getXMLFile("work", workId);
	}
	
	private Path getXMLFile(String type, String id) throws IOException {
		File xmlFile = new File(xmlFilesDirectoryLocation + File.separator + type + "-" + id + ".xml");
		Path xmlPath;
		if (xmlFile.isFile()) {
			xmlPath = xmlFile.toPath();
		}
		else {
			xmlPath = MusicBrainzConnector.downloadXML(type, id, xmlFilesDirectoryLocation);
		}
		return xmlPath;
	}
}
