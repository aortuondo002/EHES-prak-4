package core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import preprocessor.Preprocessor;
import weka.core.Instances;

public class mainBOW {

		public static void main(String[] args) throws IOException, Exception{
			Preprocessor PP=Preprocessor.getPreprocessor();
			Instances berria=PP.stringToWordVector(PP.getDataInstances(args[0]));
			FileWriter fw = new FileWriter(args[0] + "-2.arff");
			BufferedWriter bw = new BufferedWriter(fw);
			
			
		}
}
