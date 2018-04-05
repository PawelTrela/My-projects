package my.project.musicbrainz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XMLProvider {
	private static final Logger logger = LogManager.getLogger();
	private ParametersParserAndValidator parametersParser;
	
	public XMLProvider(ParametersParserAndValidator parametersParser) {
		this.parametersParser = parametersParser;
	}
	
	public InputStream getRelease(String releaseId) throws IOException, InterruptedException {
		return getXMLFile("release", releaseId);
	}
	
	public InputStream getRecording(String recordingId) throws IOException, InterruptedException {
		return getXMLFile("recording", recordingId);
	}
	
	public InputStream getArtist(String artistId) throws IOException, InterruptedException {
		return getXMLFile("artist", artistId);
	}
	
	public InputStream getWork(String workId) throws IOException, InterruptedException {
		return getXMLFile("work", workId);
	}
	
	private InputStream getXMLFile(String type, String id) throws IOException, InterruptedException {
		InputStream stream = null;
		if (parametersParser.isXmlCacheActive() && !parametersParser.isXmlReCacheActive()) {
			File xmlFile = new File(parametersParser.getXmlCacheDirectory().toString() + File.separator + type + "-" + id + ".xml");
			if (xmlFile.isFile()) {
				logger.debug("Got file " + xmlFile);
				stream = new FileInputStream(xmlFile);
			}
		}
		if (stream == null) {
			InputStream httpStream = MusicBrainzConnector.downloadXML(type, id);
			if (parametersParser.isXmlCacheActive()) {
				String fileName = type + "-" + id + ".xml";
				Path targetPath = new File(parametersParser.getXmlCacheDirectory() + File.separator + fileName).toPath();
				logger.debug("Saving downloaded xml to file " + targetPath);
				Files.copy(httpStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
				httpStream.close();
				stream = new FileInputStream(targetPath.toFile());
			}
		}
		return stream;
	}
}
