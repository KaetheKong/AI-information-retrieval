import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringEscapeUtils;

public class FileStripper {

	public static void main(String[] args) throws IOException {
		File res = new File("res");
		for (File file : res.listFiles()) {
			String trimmedText;
			try {
				trimmedText = strip(file);
			} catch (StringIndexOutOfBoundsException e) {
				System.err.println("Error parsing \"" + file.getName() + "\"");
				e.printStackTrace();
				continue;
			}
			File outputFile = new File("out/" + file.getName());
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			PrintWriter out = new PrintWriter(outputFile);
			out.print(trimmedText);
			out.flush();
			out.close();
		}
	}

	public static String strip(File file) throws IOException, StringIndexOutOfBoundsException {
		FileReader input = new FileReader(file);
		BufferedReader bf = new BufferedReader(input);

		StringBuilder lines = new StringBuilder();
		String line;
		while ((line = bf.readLine()) != null) {
			lines.append(line.trim());
			lines.append("\n");
		}
		
		// Remove entire header
		int beginningOfContent = lines.indexOf("<table class=\"infobox vcard\"");
		lines = lines.delete(0, beginningOfContent);

		// Remove everything from the hidden links down
		int hiddenLinksTag = lines.indexOf("<div id=\"mw-hidden-catlinks\"");
		String trimmedFile = lines.toString().substring(0, hiddenLinksTag);
		
		// remove everything at or below the references section
		int beginningOfReferences = trimmedFile.indexOf("<span class=\"mw-headline\" id=\"References\">");
		if (beginningOfReferences == -1) {
			beginningOfReferences = trimmedFile.indexOf("<span class=\"mw-headline\" id=\"Notes\">");

		}
		trimmedFile = trimmedFile.substring(0, beginningOfReferences);

		// kill HTML tags
		trimmedFile = trimmedFile.replaceAll("<[^>]*>", " ");
		
		trimmedFile = StringEscapeUtils.unescapeHtml4(trimmedFile);
		
		bf.close();
		
		System.out.println("Finished stripping \"" + file.getName() + "\"");
		
		return trimmedFile;
	}

}
