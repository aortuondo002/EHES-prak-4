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
		Instances dataGarbia = pp.garbitzaile(RawData);
		dataGarbia.deleteAttributeAt(0);
		dataGarbia.deleteAttributeAt(1);
		dataGarbia.deleteAttributeAt(1);
String path=pp.getBowPath()+".arff";
		pp.arffWriter(dataGarbia,path);
		dataGarbia = pp.stringToWordVectorFilter(dataGarbia);
		dataGarbia = pp.quitSparseValues(dataGarbia);
		dataGarbia = pp.filterAtributes(dataGarbia);
		System.out.println(dataGarbia.numAttributes());
		pp.arffWriter(dataGarbia, path);
		//pp.separate(kop,args);
		// pp.quitSparseValues(toTry);

	}
}
