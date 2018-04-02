package my.project.musicbrainz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ParametersParserAndValidator  {
	private String programName;
	private String releaseId;
	private Properties properties;
	private Options options;
	private String errorPreamble;
	private boolean parametersAreValid;
	private File cacheFolder;
	
	private static final String OPTION_SHORT_HELP = "h";
	private static final String OPTION_LONG_HELP = "help";
	private static final String OPTION_SHORT_PROPERTIES = "p";
	private static final String OPTION_LONG_PROPERTIES = "properties";
	private static final String OPTION_SHORT_XML_CACHE = "x";
	private static final String OPTION_LONG_XML_CACHE = "xml-cache";
	private static final String OPTION_SHORT_OUTPUT = "o";
	private static final String OPTION_LONG_OUTPUT = "output";
	private static final String OPTION_SHORT_OVERWRITE_OUTPUT = "O";
	private static final String OPTION_LONG_OVERWRITE_OUTPUT = "overwrite-output-file";
	private static final String OPTION_SHORT_TAGS = "t";
	private static final String OPTION_LONG_TAGS = "tags";
	private static final String OPTION_SHORT_CONSOLE_OUTPUT = "c";
	private static final String OPTION_LONG_CONSOLE_OUTPUT = "console-output";
	private static final String OPTION_SHORT_SKIP_CACHE = "s";
	private static final String OPTION_LONG_SKIP_CACHE = "skip-cache";
	private static final String OPTION_SHORT_RE_CACHE = "r";
	private static final String OPTION_LONG_RE_CACHE = "re-cache";
	private static final String DEFAULT_TAGS = "discnumber|disctotal|album|track|tracknumber|tracktotal|title|composer|artist|year|organization|comment|url";
	private static final String DEFAULT_FOLDER_CACHE = "MusicBrainzCache";
	
	public ParametersParserAndValidator(String programName, String[] commandLineArguments) {
		this.programName = programName;
		parametersAreValid = false;
		errorPreamble = "";
		releaseId = "";
		CommandLineParser cliParser = new DefaultParser();
		options = new Options();
		fillInOptions(options);

		// e5db824a-6b2c-4200-9f17-ca4c6adf6ace
		// 9c5c043e-bc69-4edb-81a4-1aaf9c81e6dc - Glenn Gould Remastered
		// 07da4b32-1a0d-4a9f-ae62-b997321fb946
		// b0837172-673c-4416-80d6-8a5801e6f102 - Andras Schiff - Mozart Piano Concertos
		
		commandLineArguments = new String[] { "b0837172-673c-4416-80d6-8a5801e6f102" };
		
		try {
			CommandLine commandLine = cliParser.parse(options, commandLineArguments);
			if (commandLine.hasOption(OPTION_SHORT_HELP)) {
				printHelp();
				return;
			}
			else if (commandLine.getArgList().isEmpty()) {
				errorPreamble = "RELEASE_ID argument is missing.";
				printHelp();
				return;
			}
			else {
				Pattern patternForReleaseId = Pattern.compile("[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}");
				Matcher matcherForReleaseId = patternForReleaseId.matcher(commandLine.getArgList().get(0));
				if (matcherForReleaseId.find()) {
					releaseId = matcherForReleaseId.group(0);
				}
				else {
					errorPreamble = "Given argument (" + commandLine.getArgList().get(0) + ") doesn't contains substring matching release id.";
					printHelp();
					return;
				}
				
			}
			Properties defaultProperties = new Properties();
			defaultProperties.setProperty(OPTION_LONG_XML_CACHE, DEFAULT_FOLDER_CACHE);
			defaultProperties.setProperty(OPTION_LONG_TAGS, DEFAULT_TAGS);
			defaultProperties.setProperty(OPTION_LONG_CONSOLE_OUTPUT, "0");
			defaultProperties.setProperty(OPTION_LONG_SKIP_CACHE, "0");
			defaultProperties.setProperty(OPTION_LONG_RE_CACHE, "0");
			defaultProperties.setProperty(OPTION_LONG_OVERWRITE_OUTPUT, "0");
			
			if (!loadPropertiesFromFile(programName, commandLine, defaultProperties)) {
				return;
			}

			putOptionsToProperties(commandLine);
			
			if (!checkXmlFolder(commandLine)) {
				return;
			}
			
			parametersAreValid = true;
		} catch (ParseException e1) {
			errorPreamble = e1.getMessage();
			printHelp();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean areParametersValid() {
		return parametersAreValid;
	}
	
	public boolean isXmlCacheActive() {
		if (properties.get(OPTION_LONG_SKIP_CACHE).equals("1")) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean isXmlReCacheActive() {
		if (properties.get(OPTION_LONG_RE_CACHE).equals("1")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public File getXmlCacheFolder() {
		return cacheFolder;
	}
	
	public boolean isConsoleOutput() {
		if (properties.get(OPTION_LONG_CONSOLE_OUTPUT).equals("1")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public File getOutputFile(String releaseName) {
		/*
		 * TODO Jeżeli wyjście jest na konsolę, to zwróć null.
		 * Jeżeli w opcjach podano ścieżkę do pliku wyjściowego, to sprawdz, czy jest prawidłowa. Jeżeli plik już istnieje,
		 * to sprawdz, czy może być nadpisywany. Jeżeli nie, to wyświetl pomoc, zwróć null i ustaw parametersAreValid na false.
		 * Jeżeli w opcjach nie podano ścieżki do pliku wyjściowego, to zbuduj ścieżkę z podanego argumentu i rozszerzenia txt.
		 * Sprawdz, czy taki plik istnieje i jeżeli tak, to sprawdz, czy można go nadpisać. Jeżeli nie można, to wyświetl pomoc,
		 * zwróć null i ustaw parametersAreValid na false.
		 */
	}
	
	public boolean canOutputFileBeOverwritten() {
		if (properties.get(OPTION_LONG_OVERWRITE_OUTPUT).equals("1")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public String getMp3Tags() {
		return properties.getProperty(OPTION_LONG_TAGS);
	}
	
	public String getReleaseId() {
		return releaseId;
	}
	
	private boolean checkXmlFolder(CommandLine commandLine) {
		if (!properties.getProperty(OPTION_LONG_SKIP_CACHE).equals("1") ) {
			boolean isPropertiesFolderPresent = false;
			if (commandLine.hasOption(OPTION_SHORT_XML_CACHE)) {
				String propertyValue = commandLine.getOptionValue(OPTION_SHORT_XML_CACHE);
				cacheFolder = new File(propertyValue);
				if (!cacheFolder.exists()) {
					errorPreamble = "Folder specified with option -" + OPTION_SHORT_XML_CACHE + " (" + propertyValue + ") does not exits.";
					printHelp();
					return false;
				}
				else if (!cacheFolder.isDirectory()) {
					errorPreamble = "Path specified with option -" + OPTION_SHORT_XML_CACHE + " (" + propertyValue + ") isn't a directory.";
					printHelp();
					return false;
				}
				isPropertiesFolderPresent = true;
			}
			else {
				cacheFolder = new File(DEFAULT_FOLDER_CACHE);
				if (cacheFolder.exists()) {
					if (cacheFolder.isFile()) {
						errorPreamble = "There is a file with name \"" + DEFAULT_FOLDER_CACHE + "\" whilst the program expects folder with that name.";
						printHelp();
						return false;
					}
					else if (!cacheFolder.canWrite()) {
						errorPreamble = "Program doesn't have write access to cache folder (" + DEFAULT_FOLDER_CACHE + ").";
						printHelp();
						return false;
					}
					isPropertiesFolderPresent = true;
				}
			}
			if (!isPropertiesFolderPresent) {
				cacheFolder = new File(DEFAULT_FOLDER_CACHE);
				boolean success = cacheFolder.mkdirs();
				if (!success) {
					errorPreamble = "There was a problem with creating the folder \"" + DEFAULT_FOLDER_CACHE + "\".";
					printHelp();
					return false;
				}
			}
		}
		return true;
	}

	private void putOptionsToProperties(CommandLine commandLine) {
		if (commandLine.hasOption(OPTION_SHORT_SKIP_CACHE)) {
			properties.setProperty(OPTION_LONG_SKIP_CACHE, "1");
		}
		if (commandLine.hasOption(OPTION_SHORT_CONSOLE_OUTPUT)) {
			properties.setProperty(OPTION_LONG_CONSOLE_OUTPUT, "1");
		}
		if (commandLine.hasOption(OPTION_SHORT_SKIP_CACHE)) {
			properties.setProperty(OPTION_LONG_SKIP_CACHE, "1");
		}
		if (commandLine.hasOption(OPTION_SHORT_RE_CACHE)) {
		properties.setProperty(OPTION_LONG_RE_CACHE, "1");
		}
		if (commandLine.hasOption(OPTION_SHORT_OVERWRITE_OUTPUT)) {
		properties.setProperty(OPTION_LONG_OVERWRITE_OUTPUT, "1");
		}
	}

	private boolean loadPropertiesFromFile(String programName, CommandLine commandLine, Properties defaultProperties)
			throws FileNotFoundException, IOException {
		properties = new Properties(defaultProperties);
		
		File propertiesFilePath;
		boolean isPropertiesFilePresent = false;
		if (commandLine.hasOption(OPTION_SHORT_PROPERTIES)) {
			String propertyValue = commandLine.getOptionValue(OPTION_SHORT_PROPERTIES);
			propertiesFilePath = new File(propertyValue);
			if (!propertiesFilePath.exists()) {
				errorPreamble = "File specified with option -" + OPTION_SHORT_PROPERTIES + " (" + propertyValue + ") does not exits.";
				printHelp();
				return false;
			}
			else if (propertiesFilePath.isDirectory()) {
				errorPreamble = "Path specified with option -" + OPTION_SHORT_PROPERTIES + " (" + propertyValue + ") points to directory.";
				printHelp();
				return false;
			}
			isPropertiesFilePresent = true;
		}
		else {
			propertiesFilePath = new File(programName + ".properties");
			if (propertiesFilePath.exists() && propertiesFilePath.isFile()) {
				isPropertiesFilePresent = true;
			}
		}
		if (isPropertiesFilePresent) {
			InputStream propertiesInputStream = new FileInputStream(propertiesFilePath);
			properties.load(propertiesInputStream);
			propertiesInputStream.close();
		}
		return true;
	}

	private void printHelp() {
		if (!errorPreamble.isEmpty()) {
			System.out.println(errorPreamble + "\n");
		}
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(140);
		formatter.printHelp(programName + " RELEASE_ID",
				"\nAll options are optional. Only RELEASE_ID is required. You should provide one (and only one) release id. "
						+ "It could be only release id (e.g. b0837172-673c-4416-80d6-8a5801e6f102) or text containing release id "
						+ "(e.g. url copied from browser: https://musicbrainz.org/release/07da4b32-1a0d-4a9f-ae62-b997321fb946)."
						+ "\n\nOptions:",
						options,
						"\nIf you encounter a problem or if you have any questions, feel free and let me know (pwtrela@gmail.com)",
						true);
	}



	private void fillInOptions(Options options) {
		options.addOption(OPTION_SHORT_HELP, OPTION_LONG_HELP, false, "Prints this message");
		options.addOption(OPTION_SHORT_PROPERTIES, OPTION_LONG_PROPERTIES, true,
				"Path to properties file. If path is not given, then it looks for file in current working directory."
				+ "\nDefault: " + programName + ".properties");
		options.addOption(OPTION_SHORT_XML_CACHE, OPTION_LONG_XML_CACHE, true,
				"Path to folder, where xml files downloaded from musicbrainz.org site will be stored."
				+ "\nDefault: " + DEFAULT_FOLDER_CACHE
				+ "\nThis option is ignored when -s option is in use");
		options.addOption(OPTION_SHORT_OUTPUT, OPTION_LONG_OUTPUT, true,
				"Path to file, in which output tags will be saved. If path is not given, then it creates file in current "
						+ "working directory with name <release name>.txt. This option is ignored when -c option is in use");
		options.addOption(OPTION_SHORT_OVERWRITE_OUTPUT, OPTION_LONG_OVERWRITE_OUTPUT, false, "If output file is available on disk, then it will be overwritten."
				+ "\nThis option is ignored when -c option is in use");
		options.addOption(OPTION_SHORT_TAGS, OPTION_LONG_TAGS, true, "Template tags for output. Default: " + DEFAULT_TAGS);
		options.addOption(OPTION_SHORT_CONSOLE_OUTPUT, OPTION_LONG_CONSOLE_OUTPUT, false, "Prints output to console (does't create output file)");
		options.addOption(OPTION_SHORT_SKIP_CACHE, OPTION_LONG_SKIP_CACHE, false,
				"Download xml files directly from musicbrainz.org site and doesn't save it in local cache folder."
				+ "\nThis option is not recommended because it significantly increases the download time and the tag calculation");
		options.addOption(OPTION_SHORT_RE_CACHE, OPTION_LONG_RE_CACHE, false,
				"Download xml files directly from musicbrainz.org site, even if they're present in local cache folder "
						+ "and saves them in local cache folder.\nUse this option only if you've noticed that release's "
						+ "data has been changed since last download of xml-s");
	}
}
