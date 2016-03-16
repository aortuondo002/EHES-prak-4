package core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.jar.Attributes;

import preprocessor.Preprocessor;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

public class mainBOW {

		public static void main(String[] args) throws IOException, Exception{
			Preprocessor PP=Preprocessor.getPreprocessor();
			Instances berria=PP.stringToWordVector(PP.getDataInstances(args[0]));
			FileWriter fw = new FileWriter(args[0] + "-2.arff");
			BufferedWriter bw = new BufferedWriter(fw);
			ArffSaver arff=new ArffSaver();
			for(int i= 0;i<berria.numAttributes();i++){
				System.out.println(berria.attribute(i).name());
				
				//bw.write(berria.attribute(i).name());
			}
			
			
		}
}
