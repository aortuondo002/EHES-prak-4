package core;

import java.io.BufferedWriter;
import java.io.File;
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
			String of=PP.converter(args[0]);
			Instances berria=PP.stringToWordVector(PP.getDataInstances(of));
			ArffSaver arff= new ArffSaver();
			arff.setFile(new File(args[1]));
			arff.setInstances(berria);
			arff.writeBatch();
			
			
			
		}
}
