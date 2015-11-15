import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLReader {
	private String flname;
	private String originalFile;
	private HashMap<String, String> readXML;
	private String newFile;
	private String content;
	private List<String> importantPOS;

	public XMLReader(String filename) {
		this.flname = "res/parsed-presidents/" + filename;
		this.originalFile = filename;
		this.setNewFile(filename.substring(0, filename.indexOf(".")) + ".txt");
		this.readXML = new HashMap<>();
		this.content = "";
		this.importantPOS = new ArrayList<String>();
		addPos();
	}

	public void addPos() {
		this.importantPOS = Arrays.asList("CD", "EX", "FW", "JJ", "JJR", "JJS",
				"LS", "MD", "NN", "NNS", "NNP", "NNPS", "POS", "PRP", "PRP$",
				"RB", "RBR", "RBS", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ");
	}

	public void readxml() {
		try {

			File fXmlFile = new File(this.flname);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			System.out.println("Root element :"
					+ doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("token");

			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					String lemma = eElement.getElementsByTagName("lemma")
							.item(0).getTextContent();
					String pos = eElement.getElementsByTagName("POS").item(0)
							.getTextContent();
					if (this.importantPOS.contains(pos)){
						this.readXML.put(lemma, pos);
						this.content = this.content + lemma + " ";
//						System.out.println(this.content);
					}
				}
			}
//			System.out.println(this.content);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, String> getXMLRead() {
		return this.readXML;
	}

	public String getContent() {
		return this.content;
	}

	public String getNewFile() {
		return newFile;
	}

	public void setNewFile(String newFile) {
		this.newFile = newFile;
	}
}
