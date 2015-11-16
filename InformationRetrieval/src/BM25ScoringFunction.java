
public class BM25ScoringFunction {
	private int docCardinality;
	private double k1;
	private double b;
	private double averageDocLength;
	private int docLength;

	public BM25ScoringFunction(
			int docCardinality, 
			int docLength, 
			double k1, 
			double b, 
			double averageDocLength) {
		
		this.docCardinality = docCardinality;
		this.k1 = k1;
		this.b = b;
		this.averageDocLength = averageDocLength;
		this.docLength = docLength;
	}

	private double idf(int numDocsContainingQ) {
		double numerator = (double) this.docCardinality - numDocsContainingQ + 0.5;
		double denominator = numDocsContainingQ + 0.5;
		return Math.log(numerator / denominator + 1);
	}

	public double score(double frequency, int numDocsContainingQ) {
		double numerator = frequency * (this.k1 + 1);
		double denominator = frequency + this.k1 * (1 - this.b + this.b * (this.docLength) / this.averageDocLength);
		return idf(numDocsContainingQ) * numerator / denominator;
	}
}
