import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class jsouptest {

	public static void main(String[] args) throws IOException {
		//Document doc = new Document("tester");
		FileReader input = new FileReader("Adams.txt");
		@SuppressWarnings("resource")
		BufferedReader bf = new BufferedReader(input);
		
		ArrayList<String> lines = new ArrayList<>();
		String line;
		while ((line = bf.readLine()) != null){
			String newline = line.replaceAll("<[^>]*>", "");
			lines.add(newline);
		}
		
		for (int i = 0; i < lines.size(); i ++){
			System.out.println(lines.get(i));
		}

	}

}
