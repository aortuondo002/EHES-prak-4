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
import weka.filters.Filter;

public class mainBOW {

	public static void main(String[] args) throws IOException, Exception {
		Preprocessor PP = Preprocessor.getPreprocessor();
		String[] arffs=new String[3];
		for (int i = 0; i < args.length; i++) {
			String path = PP.converter(args[i]);
			Instances berria = PP.stringToWordVectorFilter(PP.getDataInstances(path));
			String endfile=path.replace(".csv.arff",".bow.arff");
			ArffSaver arff = new ArffSaver();
			arff.setFile(new File(endfile));
			arff.setInstances(berria);
			arff.writeBatch();
			arffs[i]=endfile;
		}
		//PP.Batidora(arffs);
	}
}
