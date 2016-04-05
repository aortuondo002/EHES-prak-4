package preprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.unsupervised.instance.SparseToNonSparse;

public class Preprocessor {

	public static Preprocessor PP;

	public Preprocessor() {
	}

	public static synchronized Preprocessor getPreprocessor() {
		if (PP == null) {
			PP = new Preprocessor();
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

	public Instances stringToWordVectorFilter(Instances rawData) throws Exception {
		StringToWordVector stringToWordVectorFilter = new StringToWordVector();
		stringToWordVectorFilter.setInputFormat(rawData);
		stringToWordVectorFilter.setWordsToKeep(4000);
		stringToWordVectorFilter.setOutputWordCounts(true);
		stringToWordVectorFilter.setLowerCaseTokens(true);
		Instances dataToWordVector = Filter.useFilter(rawData, stringToWordVectorFilter);
		return dataToWordVector;
	}

	public Instances quitSparseValues(Instances Data) throws Exception {
		SparseToNonSparse filter = new SparseToNonSparse();
		filter.setInputFormat(Data);
		Filter.useFilter(Data, filter);
		return Data;
	}

	public Instances filterAtributes(Instances data) throws Exception {
		weka.filters.supervised.attribute.AttributeSelection filter = new weka.filters.supervised.attribute.AttributeSelection();
		InfoGainAttributeEval eval = new InfoGainAttributeEval();
		Ranker search = new Ranker();
		filter.setEvaluator(eval);
		filter.setSearch(search);
		filter.setInputFormat(data);
		Instances filtered = Filter.useFilter(data, filter);
		System.out.println(filtered);
		System.out.println();
		return filtered;
	}

	/**
	 * Metodo honek csv fitxategia arff fitxategira bihurtzen du
	 * 
	 * @param arg
	 * @return code(@String) path-a
	 * @throws IOException
	 */
	public ArrayList<String> getAttributes(String path) throws IOException {
		ArrayList<String> atributes = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String lerroa = null;
		while ((lerroa = reader.readLine()) != null) {
			if (lerroa.startsWith("@attribute")) {
				lerroa = lerroa.substring(0, 5);
				atributes.add(lerroa);
			}

		}
		reader.close();
		return atributes;

	}

	public void bowGenerator(String[] args) throws IOException {
		String[] arffs = new String[3];
		for (int i = 0; i < args.length; i++) {
			arffs[i] = args[i] + ".arff";
		}

		String path = arffs[0].replace("train.csv", "BOW");
		FileWriter fw = new FileWriter(path);
		BufferedWriter bw = new BufferedWriter(fw);
		
		ArffSaver saver = new ArffSaver();
		ArffReader reader = new ArffReader(new FileReader(arffs[0]));
		
		Instances structure = reader.getStructure();
		saver.setDestination(new File(path));
		saver.setStructure(structure);
		saver.writeBatch();
		int kop = 0;
		Instances toCopy = reader.getData();
		
		for (int i = 0; i < arffs.length; i++) {
			reader = new ArffReader(new FileReader(arffs[i]));
			Instances toSave = reader.getData();
			toCopy = Instances.mergeInstances(toCopy, toSave);
			kop = reader.getData().numInstances();
			bw.append("#" + kop);
		}
		
		saver.setInstances(toCopy);
		saver.writeBatch();
		bw.close();
	}
	
	
	public void converter(String arg) throws IOException {
		String outputFile = arg + ".arff";
		FileWriter fw = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(fw);
		FileReader fr = new FileReader(arg);
		BufferedReader br = new BufferedReader(fr);
		bw.write("@RELATION " + arg + "\n\n");
		bw.write("@ATTRIBUTE Textua string \n\n");
		bw.write("@ATTRIBUTE Klasea {neutral,positive,negative}\n");
		bw.write("@DATA\n");
		String lerroa = br.readLine();

		while ((lerroa = br.readLine()) != null) {
			if (lerroa.startsWith("\"") && !lerroa.startsWith("\"\"")) {

				lerroa = lerroa.substring(1, lerroa.length());
				String[] atazak = lerroa.split("\",\"");

				if (!atazak[1].equalsIgnoreCase("irrelevant")) {
					if (atazak[1].equalsIgnoreCase("UNKNOWN")) {
						atazak[1] = "?";
					}
					bw.write("'" + atazak[4].replace("'", "´") + "'," + atazak[1] + "\n");
				}
				bw.flush();
			}
		} // while
		bw.close();
		br.close();

	}

}
