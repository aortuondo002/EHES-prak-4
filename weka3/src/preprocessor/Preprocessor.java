package preprocessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class Preprocessor {

	public static Preprocessor PP;
	
	public Preprocessor(){
	}
	public static synchronized Preprocessor getPreprocessor(){
		if(PP==null){
			PP=new Preprocessor();
		}
		return PP;
	}
	
	public Instances getDataInstances(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		Instances rawData = null;
		rawData = new Instances(br);
		br.close();
		rawData.setClassIndex(rawData.numAttributes() - 1);
		return rawData;
	}
	
	public Instances stringToWordVector (Instances rawData) throws Exception{
		StringToWordVector stringToWordVectorFilter = new StringToWordVector();
		stringToWordVectorFilter.setInputFormat(rawData);
		stringToWordVectorFilter.setWordsToKeep(4000);
		stringToWordVectorFilter.setOutputWordCounts(true);
		stringToWordVectorFilter.setLowerCaseTokens(true);
		Instances dataToWordVector=Filter.useFilter(rawData, stringToWordVectorFilter);
		return dataToWordVector;
	}
	
	public int[] AttributeEvaluator(Instances data) throws Exception{
		InfoGainAttributeEval igae=new InfoGainAttributeEval();
		Ranker rank= new Ranker();
		String[] options= new String[2];
		options[0]="-N -1";
		options[1]="-T "+Long.toString(Long.MIN_VALUE)+"";
		rank.setOptions(options);
		return rank.search(igae, data);
		
		
		
		
		
	}
	
	

}
