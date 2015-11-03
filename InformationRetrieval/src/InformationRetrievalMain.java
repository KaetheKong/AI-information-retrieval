import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

public class InformationRetrievalMain {

	public static void main(String[] args) throws IOException {
		// parse and curate all files in the "res" folder (to the "out" folder)
		Curator.parseAll();

		// Get files
		File folder = new File("out");
		File[] fileList = folder.listFiles();

		// Store content and length of each file
		int numFiles = fileList.length;
		double averageDocLength = 0;
		Map<String, String> filesMap = new HashMap<>();
		Map<String, Integer> fileLengthsMap = new HashMap<>();
		for (File file : fileList) {
			String content = new String(Files.readAllBytes(file.toPath()));
			filesMap.put(file.getName(), content);

			String[] words = content.split("\\s");
			averageDocLength += words.length;
			fileLengthsMap.put(file.getName(), words.length);
		}
		averageDocLength /= numFiles;
		String query;
		while (true) {
			// Get and parse query
			query = JOptionPane.showInputDialog(null, "What do you want to search for?\n(type \"quit\" to quit)");
			if (query.equals("quit")) break;
			query = query.toLowerCase();
			System.out.println("Query: " + query);
			String[] splitQuery = query.split("\\s");

			// Find docs containing the queries
			Map<String, List<File>> filesContainingQueryMap = new HashMap<>();
			for (String q : splitQuery) {
				if (filesContainingQueryMap.get(q) == null) {
					List<File> list = new ArrayList<>();
					filesContainingQueryMap.put(q, list);
				}
				for (File file : fileList) {
					String content = filesMap.get(file.getName());
					if (content.toLowerCase().contains(q)) {
						filesContainingQueryMap.get(q).add(file);
					}
				}
			}

			// Score the docs
			double k1 = 1.2;
			double b = 0.75;
			Map<String, Double> scores = new HashMap<>();
			for (File file : fileList) {
				double score = 0;
				for (String q : splitQuery) {
					String content = filesMap.get(file.getName());
					int numMatches = StringUtils.countMatches(content.toLowerCase(), q);
					int docLength = fileLengthsMap.get(file.getName());
					double frequency = numMatches / ((double) docLength);

					BM25ScoringFunction func = new BM25ScoringFunction(numFiles, docLength, k1, b, averageDocLength);
					int numFilesContainingQ = filesContainingQueryMap.get(q).size();
					score += func.score(frequency, numFilesContainingQ);
				}
				if (score != 0) {
					scores.put(file.getName(), Math.abs(score));
				}
			}

			List<String> sortedScores = new ArrayList<>();
			scores.entrySet().stream() // streaming API on the map entries
					.sorted(Comparator.comparing(e -> e.getValue(), Comparator.reverseOrder())) // sort
																								// descending
					.limit(10) // get the first 10
					.forEach(e -> sortedScores.add(e.getKey())); // add them to
																	// the
																	// list

			if (sortedScores.size() > 0) {
				System.out.println("Top result: " + sortedScores.get(0));
				System.out.println("Top 10 results: ");
				for (String score : sortedScores) {
					System.out.println("\t" + score);
				}
			} else {
				System.out.println("No results");
			}
		} 
	}

}
