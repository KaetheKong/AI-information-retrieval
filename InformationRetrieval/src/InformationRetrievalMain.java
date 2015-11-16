import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class InformationRetrievalMain {

	public static void main(String[] args) throws IOException {
		// Get files
		File folder = new File("res/curated-presidents");
		File[] fileList = folder.listFiles();

		// Store content and length of each file
		int numFiles = fileList.length;
		double averageDocLength = 0;
		Map<String, List<String>> filesMap = new HashMap<>();
		Map<String, Integer> fileLengthsMap = new HashMap<>();
		for (File file : fileList) {
			String content = new String(Files.readAllBytes(file.toPath())).toLowerCase();

			String[] words = content.split("\\s");
			filesMap.put(file.getName(), Arrays.asList(words));
			averageDocLength += words.length;
			fileLengthsMap.put(file.getName(), words.length);
		}
		averageDocLength /= numFiles;
		String query;
		while (true) {
			// Get and parse query
			query = JOptionPane.showInputDialog(null, "What do you want to search for?\n(type \"quit\" to quit)");
			if (query.equals("quit")) break;
			query = parseQuery(query);
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
					List<String> words = filesMap.get(file.getName());
					if (words.contains(q)) {
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
					List<String> words = filesMap.get(file.getName());
					int numMatches = (int) words.stream()
							// Filter by words that equal the query
							.filter(word -> word.equals(q))
							// count the number of words that match the query
							.count();
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
			scores.entrySet().stream()
					// Sort descending
					.sorted(Comparator.comparing(e -> e.getValue(), Comparator.reverseOrder()))
					// get the first 10
					.limit(10)
					// add them to the list
					.forEach(e -> sortedScores.add(e.getKey()));

			if (sortedScores.size() > 0) {
				System.out.println("Top result: " + sortedScores.get(0));
				System.out.println("Top 10 results: ");
				for (String name : sortedScores) {
					System.out.println("\t" + name + String.format(", score = %.4f", scores.get(name)));
				}
			} else {
				System.out.println("No results");
			}
		}
	}

	private static String parseQuery(String query) {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		Annotation document = new Annotation(query);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<String> importantPOS = Arrays.asList("CD", "EX", "FW", "JJ", "JJR", "JJS", "LS", "MD", "NN", "NNS", "NNP",
				"NNPS", "POS", "PRP", "PRP$", "RB", "RBR", "RBS", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ");

		StringBuilder sb = new StringBuilder();
		for (CoreMap sentence : sentences) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String lemma = token.get(LemmaAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				if (importantPOS.contains(pos)) {
					sb.append(lemma);
					sb.append(" ");
				}
			}
		}
		return sb.toString();
	}

}
