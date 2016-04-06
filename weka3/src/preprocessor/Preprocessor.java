package preprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
	String bowPath = "";


	public static synchronized Preprocessor getPreprocessor() {
		if (PP == null) {
			PP = new Preprocessor();
		}
		return PP;
	}


	public Preprocessor() {
	}

	public int[] bowMixer(String[] args) throws FileNotFoundException, IOException {
		int[] kop = { 0, 0, 0 };
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].replace(".csv", ".csv.arff");
		}
		ArffSaver saver = new ArffSaver();
		saver.setFile(new File(args[0].replace("train.csv", "BagOfWords")));

		Instances bow = null;
		Instances[] toSave = new Instances[3];
		for (int i = 0; i < kop.length; i++) {
			ArffReader reader = new ArffReader(new FileReader(args[i]));
			toSave[i] = reader.getData();
			kop[i] = toSave[i].numInstances();
			if(i==0){
				saver.setStructure(reader.getStructure());
			}
		}
		bow=new Instances(toSave[0]);
		for (int i = 1; i < toSave.length; i++) {
			for (int j = 0; j < toSave[i].numInstances(); j++) {
				bow.add(toSave[i].instance(j));
			}	
		}	
		saver.setInstances(bow);
		saver.writeBatch();

		return kop;
	}

	public void csv2arff(String path) throws Exception {
		String outPath = path.replace(".csv", "2.arff");
		FileWriter fw = new FileWriter(outPath);
		BufferedWriter bw = new BufferedWriter(fw);
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String lerroa = "";
		String newLerroa = "";
		while ((lerroa = br.readLine()) != null) {
			if (lerroa.startsWith("\"") && !lerroa.startsWith("\"\"") && !lerroa.contains("irrelevant")) {
				newLerroa = lerroa;
				newLerroa = newLerroa.substring(1, lerroa.length() - 1);
				newLerroa = newLerroa.replace("\t", ",");
				newLerroa = newLerroa.replace("\",\"", "\t");
				newLerroa = newLerroa.replace("'", "�");
				newLerroa = newLerroa.replace(",", "/");
				bw.write(newLerroa + "\n");
			}
		}
		bw.flush();
		bw.close();
		br.close();
		// load CSV

		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(outPath));

		loader.setEnclosureCharacters("\t");
		loader.setNominalAttributes("2");
		loader.setStringAttributes("5");
		Instances data = loader.getDataSet();

		// save ARFF
		ArffSaver saver = new ArffSaver();
		saver.setStructure(loader.getStructure());
		saver.setInstances(data);
		saver.setFile(new File(path + ".arff"));
		// saver.setDestination(new File(path+".arff"));
		saver.writeBatch();

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

	public String getBowPath() {
		return bowPath;
	}

	public Instances getDataInstances(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		Instances rawData = null;
		rawData = new Instances(br);
		br.close();
		rawData.setClassIndex(rawData.numAttributes() - 1);
		return rawData;
	}

	public Instances quitSparseValues(Instances Data) throws Exception {
		SparseToNonSparse filter = new SparseToNonSparse();
		filter.setInputFormat(Data);
		Filter.useFilter(Data, filter);
		return Data;
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

}
