
public class BM25ScoringFunc {
	private int cardinalityOfDocs;
	private double k1;
	private double b;
	private double avgdl;
	private int docLen;
	
	public BM25ScoringFunc(int card, int docLen, double k1, double b, double avgdl){
		this.cardinalityOfDocs = card;
		this.k1 = k1;
		this.b = b;
		this.avgdl = avgdl;
		this.docLen = docLen;
	}
	
	public double idf(int numOfDocsContain){
		double numerator = (double) this.cardinalityOfDocs - numOfDocsContain + 0.5;
		double denomin = numOfDocsContain + 0.5;
		return Math.log(numerator/denomin);
	}
	
	public double score(double freq, int numOfDocsContain){
		double numerator = freq*(this.k1 + 1);
		double denomin = freq + this.k1 *(1 - this.b + this.b * (Math.abs(this.docLen))/this.avgdl);
		double returnScore = idf(numOfDocsContain) * numerator/denomin;
		return returnScore;
	}
}
