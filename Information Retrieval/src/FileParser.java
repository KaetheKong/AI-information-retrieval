import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Parses the HTML document for important information
 */
public class FileParser {
	public static void main(String[] args) {
		
	}
	
	public static void parse(File file) throws IOException {
		Document doc = Jsoup.parse(file, "UTF-8");
		
		// TODO: parse info box
		// TODO: parse anchor tags 
		// TODO: parse table of contents?
	}
}
