package my.project.musicbrainz;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ParametersParser  {
	private Properties properties;
	private Options options;
	private String errorPreamble;
	private boolean parametersAreInvalid;

	public ParametersParser(String[] commandLineArguments) {
		parametersAreInvalid = false;
		errorPreamble = "";
		CommandLineParser cliParser = new DefaultParser();
		options = new Options();
		fillInOptions(options);

		commandLineArguments = new String[] { "-h", "--properties", "MB2mp3tag.properties", "-s",
		"b0837172-673c-4416-80d6-8a5801e6f102" };
		try {
			CommandLine commandLine = cliParser.parse(options, commandLineArguments);
			if (commandLine.hasOption('h')) {
				printHelp();
				parametersAreInvalid = true;
				return;
			}
			System.out.println(commandLine.getArgList().toString());
			// return;
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			errorPreamble = e1.getMessage();
			printHelp();
			// e1.printStackTrace();
		}
	}

	public void printHelp() {
		if (!errorPreamble.isEmpty()) {
			System.out.println(errorPreamble + "\n\n");
		}
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(140);
		formatter.printHelp("MB2mp3tag <release_id>",
				"\nAll options are optional. Only release_id is required. You shoud provide one (and only one) release_id. "
						+ "It could be only release_id (e.g. b0837172-673c-4416-80d6-8a5801e6f102) or text containing release_id "
						+ "(e.g. url copied from browser: https://musicbrainz.org/release/07da4b32-1a0d-4a9f-ae62-b997321fb946)."
						+ "\n\nOptions:",
						options,
						"\nIf you encounter a problem or if you have any questions, feel free and let me know (pwtrela@gmail.com)",
						true);
	}

	public boolean areParametersValid() {
		return !parametersAreInvalid;
	}

	private void fillInOptions(Options options) {
		options.addOption("h", "help", false, "Prints this message.");
		options.addOption("p", "properties", true,
				"Properties file (if path is not given, then it looks for file in current working directory)");
		options.addOption("x", "xml-cache", true,
				"Path to folder, where xml files downloaded from musicbrainz.org site will be stored");
		options.addOption("o", "output-file", true,
				"Path to file, in which output tags will be saved (if path is not given, then it creates file in current "
						+ "working directory)");
		options.addOption("t", "tags", true, "Template tags for output");
		options.addOption("c", "console-output", false, "Prints output to console (does't create output file)");
		options.addOption("s", "skip-cache", false,
				"Download xml files directly from musicbrainz.org site and doesn't save it in local cache folder");
		options.addOption("r", "re-cache", false,
				"Download xml files directly from musicbrainz.org site, even if they're present in local cache folder "
						+ "and saves them in local cache folder");
	}
}
