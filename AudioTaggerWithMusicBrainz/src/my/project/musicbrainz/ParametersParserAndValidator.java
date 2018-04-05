package my.project.musicbrainz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParametersParserAndValidator  {
	private String[] commandLineArguments;
	private String programName;
	private String releaseId;
	private Properties properties;
	private Options allowedOptions;
	private String errorPreamble;
	private boolean parametersAreValid;
	private File cacheDirectory;
	private File outputDirectory;
	private String outputFileName;
	private boolean parametersWereValidated;
	
	private static final String OPTION_SHORT_HELP = "h";
	private static final String OPTION_LONG_HELP = "help";
	private static final String OPTION_SHORT_PROPERTIES = "p";
	private static final String OPTION_LONG_PROPERTIES = "properties";
	private static final String OPTION_SHORT_XML_CACHE = "x";
	private static final String OPTION_LONG_XML_CACHE = "xml-cache-directory";
	private static final String OPTION_SHORT_OUTPUT_DIRECTORY = "d";
	private static final String OPTION_LONG_OUTPUT_DIRECTORY = "output-directory";
	private static final String OPTION_SHORT_OUTPUT_FILE = "o";
	private static final String OPTION_LONG_OUTPUT_FILE = "output-file";
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
	private static final String DEFAULT_TAGS = "%discnumber%|%disctotal%|%album%|%track%|%tracknumber%|%tracktotal%|%title%|%composer%|%artist%|%year%|%organization%|%comment%|%url%";
	private static final String DEFAULT_CACHE_DIRECTORY = "MusicBrainzCache";
	
	private static final Logger logger = LogManager.getLogger();
	
	public ParametersParserAndValidator(String programName, String[] commandLineArguments) {
		this.programName = programName;
		this.commandLineArguments = commandLineArguments;
		parametersAreValid = false;
		errorPreamble = "";
		releaseId = "";
		parametersWereValidated = false;
		outputFileName = "";
	}

	public boolean areParametersValid() {
		if (parametersWereValidated) {
			return parametersAreValid;
		}
		parametersWereValidated = true;
		CommandLineParser cliParser = new DefaultParser();
		allowedOptions = createListOfAllowedOptions();
		
		try {
			logger.debug("Command line arguments: " + Arrays.toString(commandLineArguments));
			CommandLine commandLine = cliParser.parse(allowedOptions, commandLineArguments);
			if (commandLine.hasOption(OPTION_SHORT_HELP)) {
				printHelp();
				return false;
			}
			else if (commandLine.getArgList().isEmpty()) {
				errorPreamble = "RELEASE_ID argument is missing.";
				printHelp();
				return false;
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
					return false;
				}
				
			}
			if (!loadPropertiesFromFile(commandLine)) {
				return false;
			}

			putOptionsToProperties(commandLine);
			
			logger.debug("Effective properties: " + properties.toString());
			
			if (!checkCacheDirectory(commandLine)) {
				return false;
			}
			
			if (!checkOutputDirectoryAndFile(commandLine)) {
				return false;
			}
			
			parametersAreValid = true;
		} catch (ParseException | IOException e) {
			errorPreamble = e.getMessage();
			logger.error(e.getStackTrace());
			printHelp();
		}
		
		return parametersAreValid;
	}
	
	public boolean isXmlCacheActive() {
		if (areParametersValid() && !properties.getProperty(OPTION_LONG_SKIP_CACHE).equals("1")) {
			return true;
		}
		return false;
	}
	
	public boolean isXmlReCacheActive() {
		if (areParametersValid() && properties.getProperty(OPTION_LONG_RE_CACHE).equals("1")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public File getXmlCacheDirectory() {
		return cacheDirectory;
	}
	
	public File getOutputFile(String releaseName) {
		File outputFile = null;
		if (areParametersValid() && !isConsoleOutput()) {
			if (outputFileName.isEmpty()) {
				outputFileName = releaseName + ".txt";
			}
			outputFile = new File(outputDirectory.getAbsolutePath() + File.separator + outputFileName);
			if (outputFile.exists()) {
				if (outputFile.isFile()) {
					if (!canOutputFileBeOverwritten()) {
						errorPreamble = "File " + outputFile.getPath() + " is already present.";
						printHelp();
						return null;
					}
				}
				else {
					errorPreamble = "There is directory with the same name (" + outputFile.getPath() + ") as desired name for output file.";
					printHelp();
					return null;
				}
			}
		}
		return outputFile;
	}
	
	public String getMp3Tags() {
		if (areParametersValid()) {
			return properties.getProperty(OPTION_LONG_TAGS);
		}
		else {
			return "";
		}
	}
	
	public String getReleaseId() {
		return releaseId;
	}
	
	public boolean isConsoleOutput() {
		if (areParametersValid() && properties.getProperty(OPTION_LONG_CONSOLE_OUTPUT).equals("1")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean canOutputFileBeOverwritten() {
		if (areParametersValid() && properties.getProperty(OPTION_LONG_OVERWRITE_OUTPUT).equals("1")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean checkCacheDirectory(CommandLine commandLine) {
		if (!properties.getProperty(OPTION_LONG_SKIP_CACHE).equals("1") ) {
			boolean isDirectoryPresentInProperties = false;
			if (commandLine.hasOption(OPTION_SHORT_XML_CACHE)) {
				String propertyValue = commandLine.getOptionValue(OPTION_SHORT_XML_CACHE);
				Path path = Paths.get(propertyValue).normalize();
				cacheDirectory = path.toFile();
				if (!cacheDirectory.exists()) {
					errorPreamble = "Directory specified with option -" + OPTION_SHORT_XML_CACHE + " (" + propertyValue + ") does not exits.";
					printHelp();
					return false;
				}
				else if (!cacheDirectory.isDirectory()) {
					errorPreamble = "Path specified with option -" + OPTION_SHORT_XML_CACHE + " (" + propertyValue + ") isn't a directory.";
					printHelp();
					return false;
				}
				isDirectoryPresentInProperties = true;
			}
			else {
				Path path = Paths.get(properties.getProperty(OPTION_LONG_XML_CACHE)).normalize();
				cacheDirectory = path.toFile();
				if (cacheDirectory.exists()) {
					if (cacheDirectory.isFile()) {
						errorPreamble = "There is a file with name \"" + properties.getProperty(OPTION_LONG_XML_CACHE) + "\" whilst the program expects directory with that name.";
						printHelp();
						return false;
					}
					else if (!cacheDirectory.canWrite()) {
						errorPreamble = "Program doesn't have write access to cache directory (" + properties.getProperty(OPTION_LONG_XML_CACHE) + ").";
						printHelp();
						return false;
					}
					isDirectoryPresentInProperties = true;
				}
			}
			if (!isDirectoryPresentInProperties) {
				cacheDirectory = new File(DEFAULT_CACHE_DIRECTORY);
				boolean success = cacheDirectory.mkdirs();
				if (!success) {
					errorPreamble = "There was a problem with creating the directory \"" + DEFAULT_CACHE_DIRECTORY + "\".";
					printHelp();
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean checkOutputDirectoryAndFile(CommandLine commandLine) {
		if (!properties.getProperty(OPTION_LONG_CONSOLE_OUTPUT).equals("1") ) {
			if (commandLine.hasOption(OPTION_SHORT_OUTPUT_DIRECTORY)) {
				String propertyValue = commandLine.getOptionValue(OPTION_SHORT_OUTPUT_DIRECTORY);
				Path path = Paths.get(propertyValue).normalize();
				outputDirectory = path.toFile();
				if (!outputDirectory.exists()) {
					errorPreamble = "Directory specified with option -" + OPTION_SHORT_OUTPUT_DIRECTORY + " (" + propertyValue + ") does not exits.";
					printHelp();
					return false;
				}
				else if (!outputDirectory.isDirectory()) {
					errorPreamble = "Path specified with option -" + OPTION_SHORT_OUTPUT_DIRECTORY + " (" + propertyValue + ") isn't a directory.";
					printHelp();
					return false;
				}
			}
			else {
				Path path = Paths.get(properties.getProperty(OPTION_LONG_OUTPUT_DIRECTORY)).normalize();
				outputDirectory = path.toFile();
				if (outputDirectory.exists()) {
					if (outputDirectory.isFile()) {
						errorPreamble = "There is a file with name \"" + properties.getProperty(OPTION_LONG_OUTPUT_DIRECTORY) + "\" whilst the program expects directory with that name.";
						printHelp();
						return false;
					}
					else if (!outputDirectory.canWrite()) {
						errorPreamble = "Program doesn't have write access to output directory (" + properties.getProperty(OPTION_LONG_OUTPUT_DIRECTORY) + ").";
						printHelp();
						return false;
					}
				}
			}
			Path path = null;
			if (commandLine.hasOption(OPTION_SHORT_OUTPUT_FILE)) {
				String propertyValue = commandLine.getOptionValue(OPTION_SHORT_OUTPUT_FILE);
				path = Paths.get(propertyValue).normalize();
				outputFileName = path.toString();
			}
			else {
				String propertyValue = properties.getProperty(OPTION_LONG_OUTPUT_FILE);
				if (!propertyValue.isEmpty()) {
					path = Paths.get(propertyValue).normalize();
					outputFileName = path.toString();
				}
			}
			if (path != null && path.getNameCount() > 1) {
				errorPreamble = "File name (" + path.toString() + ") is invalid (it contains directory name).";
				printHelp();
				return false;
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

	private boolean loadPropertiesFromFile(CommandLine commandLine)
			throws FileNotFoundException, IOException {
		Properties defaultProperties = getDefaultProperties();
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
			System.out.println("\n\n" + errorPreamble + "\n");
			logger.error(errorPreamble);
		}
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(140);
		formatter.printHelp(programName + " RELEASE_ID",
				"\nAll options are optional. Only RELEASE_ID is required. You should provide one (and only one) release id. "
						+ "It could be only release id (e.g. b0837172-673c-4416-80d6-8a5801e6f102) or text containing release id "
						+ "(e.g. url copied from browser: https://musicbrainz.org/release/07da4b32-1a0d-4a9f-ae62-b997321fb946)."
						+ "\n\nOptions:",
						allowedOptions,
						"\nIf you encounter a problem or if you have any questions, feel free and let me know (pwtrela@gmail.com).",
						true);
	}

	private Options createListOfAllowedOptions() {
		Options options = new Options();
		options.addOption(OPTION_SHORT_HELP, OPTION_LONG_HELP, false, "Prints this message");
		options.addOption(OPTION_SHORT_PROPERTIES, OPTION_LONG_PROPERTIES, true,
				"Path to properties file. If path is not given, then it looks for file in current working directory."
				+ "\nDefault: " + programName + ".properties");
		options.addOption(OPTION_SHORT_XML_CACHE, OPTION_LONG_XML_CACHE, true,
				"Path to directory, where xml files downloaded from musicbrainz.org site will be stored."
				+ "\nDefault: " + DEFAULT_CACHE_DIRECTORY
				+ "\nThis option is ignored when -" + OPTION_SHORT_SKIP_CACHE + " option is in use");
		options.addOption(OPTION_SHORT_OUTPUT_DIRECTORY, OPTION_LONG_OUTPUT_DIRECTORY, true,
				"Path to directory, in which file with output data will be saved. If path is not given, then output file is created in current "
						+ "working directory. This option is ignored when -" + OPTION_SHORT_CONSOLE_OUTPUT + " option is in use");
		options.addOption(OPTION_SHORT_OUTPUT_FILE, OPTION_LONG_OUTPUT_FILE, true,
				"Name of file, in which output data will be saved. If name is not given, then it creates file "
						+ "with name <release name>.txt. This option is ignored when -" + OPTION_SHORT_CONSOLE_OUTPUT + " option is in use");
		options.addOption(OPTION_SHORT_OVERWRITE_OUTPUT, OPTION_LONG_OVERWRITE_OUTPUT, false, "If output file is available on disk, then it will be overwritten."
				+ "\nThis option is ignored when -" + OPTION_SHORT_CONSOLE_OUTPUT + " option is in use");
		options.addOption(OPTION_SHORT_TAGS, OPTION_LONG_TAGS, true, "Template tags for output. Default: " + DEFAULT_TAGS);
		options.addOption(OPTION_SHORT_CONSOLE_OUTPUT, OPTION_LONG_CONSOLE_OUTPUT, false, "Prints output to console (does't create output file)");
		options.addOption(OPTION_SHORT_SKIP_CACHE, OPTION_LONG_SKIP_CACHE, false,
				"Download xml files directly from musicbrainz.org site and doesn't save it in local cache directory."
				+ "\nThis option is not recommended because it significantly increases the download time and the tag calculation");
		options.addOption(OPTION_SHORT_RE_CACHE, OPTION_LONG_RE_CACHE, false,
				"Download xml files directly from musicbrainz.org site, even if they're present in local cache directory "
						+ "and saves them in local cache directory.\nUse this option only if you've noticed that release's "
						+ "data has been changed since last download of xml-s");
		return options;
	}
	
	private Properties getDefaultProperties() {
		Properties defaultProperties = new Properties();
		defaultProperties.setProperty(OPTION_LONG_XML_CACHE, DEFAULT_CACHE_DIRECTORY);
		defaultProperties.setProperty(OPTION_LONG_TAGS, DEFAULT_TAGS);
		defaultProperties.setProperty(OPTION_LONG_OUTPUT_DIRECTORY, ".");
		defaultProperties.setProperty(OPTION_LONG_OUTPUT_FILE, "");
		defaultProperties.setProperty(OPTION_LONG_CONSOLE_OUTPUT, "0");
		defaultProperties.setProperty(OPTION_LONG_SKIP_CACHE, "0");
		defaultProperties.setProperty(OPTION_LONG_RE_CACHE, "0");
		defaultProperties.setProperty(OPTION_LONG_OVERWRITE_OUTPUT, "0");
		return defaultProperties;
	}
}
