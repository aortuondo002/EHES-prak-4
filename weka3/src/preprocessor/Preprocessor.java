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
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.unsupervised.instance.SparseToNonSparse;

public class Preprocessor {

	public static Preprocessor PP;

	public static synchronized Preprocessor getPreprocessor() {
		if (PP == null) {
			PP = new Preprocessor();
		}
		return PP;
	}

	String bowPath = "C:\\Users\\Ray\\Downloads\\tweet_sentiment\\tweet_sentiment\\tweetSentiment.BagOfWords.arff";

	public Preprocessor() {
	}

	public void arffWriter(Instances data,String path) throws IOException {
		ArffSaver saver = new ArffSaver();
		saver.setFile(new File(path));
		saver.setInstances(data);
		;
		saver.writeBatch();
	}

	public int[] bowMixer(String[] args) throws Exception {
		int[] kop = { 0, 0, 0 };
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].replace(".csv", ".csv.arff");
		}
		ArffSaver saver = new ArffSaver();
		// bowPath=args[0].replace("train.csv", "BagOfWords");
		saver.setFile(new File(bowPath));

		Instances bow = null;
		Instances[] toSave = new Instances[3];
		for (int i = 0; i < kop.length; i++) {
			ArffReader reader = new ArffReader(new FileReader(args[i]));
			toSave[i] = reader.getData();
			kop[i] = toSave[i].numInstances();
			if (i == 0) {
				saver.setStructure(reader.getStructure());
			}
		}
		bow = new Instances(toSave[0]);
		for (int i = 1; i < toSave.length; i++) {
			for (int j = 0; j < toSave[i].numInstances(); j++) {
				bow.add(toSave[i].instance(j));
			}
		}
		Instances newBow = garbitzaile(bow);
		saver.setInstances(newBow);
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
				newLerroa = newLerroa.replace("'", "´");
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
		data.setClassIndex(0);
		search.setThreshold(0.001);
		filter.setEvaluator(eval);
		filter.setSearch(search);
		filter.setInputFormat(data);
		Instances filtered = Filter.useFilter(data, filter);		
		return filtered;

	}

	public Instances garbitzaile(Instances data) throws Exception {
		Remove remove = new Remove();
		remove.setInputFormat(data);
		remove.setAttributeIndices("1,3,4");
		Instances newData = Filter.useFilter(data, remove);
		return newData;
	}

	public String getBowPath() {
		return bowPath;
	}

	public Instances getDataInstances(String path) throws IOException {
		ArffReader reader = new ArffReader(new FileReader(new File(path)));
		Instances rawData = reader.getData();

		return rawData;
	}

	public Instances getStr(String path) throws FileNotFoundException, IOException {
		ArffReader reader = new ArffReader(new FileReader(new File(path)));
		Instances rawData = reader.getStructure();
		return rawData;
	}

	public Instances quitSparseValues(Instances Data) throws Exception {
		SparseToNonSparse filter = new SparseToNonSparse();
		filter.setInputFormat(Data);
		Instances newData = Filter.useFilter(Data, filter);
		return newData;
	}
	//TODO
	public void separator(int[]kop,String[]args) throws IOException{
		Instances dataset=getDataInstances(bowPath+".arff");
		for (int i = 0; i < args.length; i++) {
					Instances newData=
		}
	}
	public Instances stringToWordVectorFilter(Instances rawData) throws Exception {
		StringToWordVector stringToWordVectorFilter = new StringToWordVector();
		stringToWordVectorFilter.setInputFormat(rawData);
		stringToWordVectorFilter.setWordsToKeep(4000);
		stringToWordVectorFilter.setOutputWordCounts(true);
		stringToWordVectorFilter.setLowerCaseTokens(true);
		Instances newData = Filter.useFilter(rawData, stringToWordVectorFilter);
		return newData;
	}

}
