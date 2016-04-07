package core;

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
		Instances RawData = pp.getDataInstances(pp.getBowPath());
		Instances Str = pp.getStr(pp.getBowPath());
		String path = pp.getBowPath();
		pp.arffWriter(RawData, path);
		RawData = pp.stringToWordVectorFilter(RawData);
		RawData = pp.quitSparseValues(RawData);
		//RawData = pp.filterAtributes(RawData);
		//System.out.println(RawData.numAttributes());
		pp.arffWriter(RawData, path);
		// pp.separator(kop,args);*/
		// pp.quitSparseValues(toTry);

	}
}
