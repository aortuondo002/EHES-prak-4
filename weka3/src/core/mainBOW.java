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
		String path = args[0].replaceFirst("tweetSentiment.+", "BOW.arff");
		System.out.println(path);
		pp.arffWriter(RawData, path);
		
		Instances noIdfData = pp.stringToWordVectorFilter(RawData, false);
		Instances IdfData = pp.stringToWordVectorFilter(RawData, true);

		IdfData = pp.quitSparseValues(IdfData);
		pp.arffWriter(IdfData, path.replace("BagOfWords", "IDF-TF"));

		noIdfData = pp.quitSparseValues(noIdfData);
		pp.separator(kop, args, noIdfData);
		
		System.out.println(pp.train.numAttributes()+","+pp.train.numInstances());
		System.out.println("\nDev multzoaren atributu kopurua atributu hautaketa aplikatu arinago: " + pp.dev.numAttributes());
		System.out.println("\nTrain multzoaren atributu  kopurua atributu hautaketa aplikatu arinago: " + pp.train.numAttributes());
		
		pp.train = pp.filterAtributes(pp.train);
		pp.dev = pp.filterAtributes(pp.dev);
		
		Instances[] lista=pp.getInstances();
		for (int i = 0; i < lista.length; i++) {
			lista[i] = pp.stringToWordVectorFilter(lista[i], true);
			lista[i] = pp.quitSparseValues(lista[i]);
		}
		pp.test = pp.atributuakKendu(pp.test);

		System.out.println("\nDev multzoaren atributu kopurua atributu hautaketa aplikatu eta gero: " + pp.dev.numAttributes());
		System.out.println("Train multzoaren atributu  kopurua atributu hautaketa aplikatu eta gero: " + pp.train.numAttributes());
		System.out.println("Test_blind multzoaren atributu kopurua Remove aplikatu eta gero: " + pp.test.numAttributes());

		pp.arffWriter(pp.train, path.replace("BOW", "train"));
		pp.arffWriter(pp.dev, path.replace("BOW", "dev"));
		pp.arffWriter(pp.test, path.replace("BOW", "test"));
		System.out.println("Azkeneko fitxategiak test.arff dev.arff eta test.arff dira");
		pp.arffWriter(noIdfData, path);
		
	}
}
