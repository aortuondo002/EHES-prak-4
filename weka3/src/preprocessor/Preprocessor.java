package preprocessor;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.tokenizers.WordTokenizer;
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
		// Load the instances
		Instances rawData = null;
		rawData = new Instances(br);
		// Close the file
		br.close();
		// Specify which attribute will be used as the class: the last one, in
		// this case
		rawData.setClassIndex(rawData.numAttributes() - 1);
		// data.setClass(data.attribute("class"));

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

}
