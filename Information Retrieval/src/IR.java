import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

public class IR {

	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame("Search");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(2400, 1024));

		String query = JOptionPane.showInputDialog(frame,
				"What query do you want to search for?");
		query = query.toLowerCase();
		System.out.println(query);
		
		HashMap<String, Integer> fileLength = new HashMap<>();
		HashMap<String, ArrayList<File>> fileContainsq = new HashMap<>();

		double k1 = 1.2;
		double b = 0.75;

		File folder = new File("out");
		File[] listOfFiles = folder.listFiles();

		int cardNumOfFiles = listOfFiles.length;
		String[] qarr = query.split("\\s");
		double avgdl = 0;

		for (File file : listOfFiles) {
			String content = new String(Files.readAllBytes(file.toPath()));
			String[] words = content.split("\\s");
			avgdl += words.length;
			fileLength.put(file.getName(), words.length);
		}
		avgdl = avgdl / (listOfFiles.length);
		System.out.println(avgdl);

		for (String q : qarr) {
			if (fileContainsq.get(q) == null) {
				ArrayList<File> list = new ArrayList<>();
				fileContainsq.put(q, list);
			}
			for (File file : listOfFiles) {
				String content = new String(Files.readAllBytes(file.toPath()));
				if (content.toLowerCase().contains(q)) {
					fileContainsq.get(q).add(file);
				}
			}
		}

		double maxScore = Double.MIN_VALUE;
		String maxFile = "";
		for (File file : listOfFiles) {
			double score = 0;
			for (String q : qarr) {
				String content = new String(Files.readAllBytes(file.toPath()));
				int numMatch = StringUtils.countMatches(content, q);
				int docLen = fileLength.get(file.getName());
				double freq = numMatch / ((double) docLen);
				BM25ScoringFunc fuc = new BM25ScoringFunc(cardNumOfFiles,
						docLen, k1, b, avgdl);
				score += fuc.score(freq, fileContainsq.get(q).size());
			}
			if (score > maxScore) {
				maxScore = score;
				maxFile = file.getName();
			}
		}

		System.out.println(maxFile);

	}

}
