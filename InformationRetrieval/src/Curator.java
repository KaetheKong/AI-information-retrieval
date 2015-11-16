import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Parses the HTML document for important information
 */
public class Curator {
	public static void main(String[] args) throws IOException {
		parseAll();
	}

	public static void parseAll() throws IOException {
		File resFolder = new File("res");
		for (File file : resFolder.listFiles()) {
			String parsedText;
			try {
				parsedText = parse(file);
			} catch (StringIndexOutOfBoundsException e) {
				System.err.println("Error stripping \"" + file.getName() + "\"");
				e.printStackTrace();
				continue;
			}
			File outputFile = new File("out/" + file.getName());
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			PrintWriter out = new PrintWriter(outputFile);
			out.print(parsedText);
			out.flush();
			out.close();
		}
	}

	private static String parse(File file) throws IOException {
		Document doc = Jsoup.parse(file, "UTF-8");

		doc.select("head").remove();
		doc.select("a[href~=cite_note.*]").remove();
		doc.select("img").remove();
		doc.select("script").remove();
		doc.select("li[id~=cite_note.*]").remove();
		doc.select("div[id=mw-navigation]").remove();
		doc.select(".mw-hidden-catlinks").remove();
		doc.select(".catlinks").remove();
		doc.select(".navbox").remove();

		StringBuilder content = new StringBuilder();
		content.append(doc.select(".infobox").text());
		content.append("\n");

		for (Element e : doc.select(".mw-headline")) {
			content.append(e.text());
			content.append("\n");
		}
		for (Element e : doc.select("p")) {
			content.append(e.text());
			content.append("\n");
		}

		String curated = content.toString();
		curated = curated.replaceAll(" a ", " ");
		curated = curated.replaceAll(" the ", " ");
		
		
		System.out.println("Finished parsing \"" + file.getName() + "\"");

		return content.toString().toLowerCase();
	}
}
