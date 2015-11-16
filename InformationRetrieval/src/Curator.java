import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Parses the HTML document for important information
 */
public class Curator {
	public static void main(String[] args) throws IOException {
		parseAll();
	}

	public static void parseAll() throws IOException {
		File folder = new File("res/parsed-presidents");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			String filename = file.getName();
			XMLReader r = new XMLReader(filename);
			r.readxml();
			String c = r.getContent();

			try {
				File out = new File("res/curated-presidents/" + r.getNewFile());
				FileWriter fileW;
				fileW = new FileWriter(out);
				fileW.write(c);
				fileW.flush();
				fileW.close();
			} catch (IOException exception) {
				exception.printStackTrace();
			}

		}
	}
}
