package core;

import java.awt.Desktop;
import java.io.IOException;

import preprocessor.Preprocessor;
import weka.core.Instances;

public class mainBOW {

	public static void main(String[] args) throws IOException, Exception {
		Preprocessor pp = Preprocessor.getPreprocessor();
		for (int i = 0; i < args.length; i++) {
			pp.csv2arff(args[i]);
		}

		int[] kop = pp.bowMixer(args);
		
		Instances RawData = pp.bow;
		String path= args[0].replaceFirst("tweetSentiment.+", "BOW.arff");
		System.out.println(path);
		pp.arffWriter(RawData,path);
		
		Instances noIdfData = pp.stringToWordVectorFilter(RawData, false);
		
		Instances IdfData = pp.stringToWordVectorFilter(RawData, true);
		
		IdfData = pp.quitSparseValues(IdfData);
		
		pp.arffWriter(IdfData, path.replace("BagOfWords", "IDF-TF"));
		
		noIdfData = pp.quitSparseValues(noIdfData);
		
		pp.arffWriter(noIdfData, path);
		
		pp.separator(kop, args,noIdfData);

	}
}
