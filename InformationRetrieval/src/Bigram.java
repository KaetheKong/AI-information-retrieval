import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.StringUtils;


public class Bigram {
	private String[] query;
	private HashMap<Pair<String, String>,Pair<Integer, Integer>> possibleSets;
	private HashMap<Pair<String, String>, Double> probabilitySets;
	public Bigram(String[] query){
		this.query = query;
		this.possibleSets = new HashMap<>();
		this.probabilitySets = new HashMap<>();
	}
	
	public void createAllBisets(){
		for (int i = 0; i < this.query.length-1; i++){
			for (int j = i + 1; j < this.query.length; j++){
				Pair<String, String> p = new Pair<>();
				p.setFirst(this.query[i]);
				p.setSecond(this.query[j]);
				Pair<Integer, Integer> ind = new Pair<>();
				ind.setFirst(i);
				ind.setSecond(j);
				this.possibleSets.put(p, ind);
			}
		}
	}
	
	public HashMap<Pair<String, String>, Pair<Integer, Integer>> getSets(){
		return this.possibleSets;
	}
	
	public void countOcc(String docCon){
		
		for (Pair<String, String> p : this.possibleSets.keySet()){
			String first = p.first;
			String second = p.second;
			String composed = first + " " + second;
			
			List<String> words = Arrays.asList(docCon.split("\\s"));
			
			int overallnumMatch = (int) words.stream()
					// Filter by words that equal the query
					.filter(word -> word.equalsIgnoreCase(composed))
					// count the number of words that match the query
					.count();
			
			int firstnumMatch = (int) words.stream()
					.filter(word -> word.equalsIgnoreCase(first))
					.count();
			
			int secondnumMatch = (int) words.stream()
					.filter(word -> word.equalsIgnoreCase(second))
					.count();
			
			double secondProb = (double) (overallnumMatch + 1) / (secondnumMatch + composed.length());
			double firstProb = (double) (overallnumMatch + 1) / (firstnumMatch + composed.length());
			double prb = secondProb*firstProb;
			
			System.out.println(prb);
//			if (secondnumMatch == 0){
//				prb = 0;
//			}
//			else{
//				prb = (double) secondnumMatch/(docCon.split("\\s").length);
//				
//				if (overallnumMatch == 0){
//					prb = Double.MIN_VALUE * prb;
//				}
//				else{
//					double overallp = (double) overallnumMatch /(docCon.split("\\s").length);
//					prb = overallp/prb;
//				}
//			}

			this.probabilitySets.put(p, prb);
		}
	}
	
	public double overallprob(){
		double overallprobability = 1;
		for (Pair<String, String> p : this.probabilitySets.keySet()){
			double prob = this.probabilitySets.get(p);
			overallprobability = overallprobability*prob;
		}
		return overallprobability;
	}
}
