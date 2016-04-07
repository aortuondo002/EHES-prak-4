package preprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

	public static synchronized Preprocessor getPreprocessor() {
		if (PP == null) {
			PP = new Preprocessor();
		}
		return PP;
	}

	String bowPath = "C:\\Users\\Ray\\Downloads\\tweet_sentiment\\tweet_sentiment\\tweetSentiment.BagOfWords.arff";

	public Preprocessor() {
	}

	public void arffWriter(Instances data, String path) throws IOException {
		Instances dataSet = data;
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		writer.write(dataSet.toString());
		writer.flush();
		writer.close();
	}

	public int[] bowMixer(String[] args) throws Exception {
		int[] kop = { 0, 0, 0 };
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].replace(".csv", "_2.csv.arff");
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
		saver.setInstances(bow);
		saver.writeBatch();
		return kop;
	}

	public void csv2arff(String path) throws Exception {
		String outPath = path.replace(".csv", "_2.csv");
		FileWriter fw = new FileWriter(outPath);
		BufferedWriter bw = new BufferedWriter(fw);
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String lerroa = "";
		while ((lerroa = br.readLine()) != null) {
			if (lerroa.startsWith("\"") && !lerroa.startsWith("\"\"") && !lerroa.contains("irrelevant")) {
				String aux = lerroa;
				String[] newLerroa = aux.split("\",\"");
				String bat = newLerroa[1];
				String bi = newLerroa[4];

				bat = bat.substring(0, bat.length());
				bat = bat.replace("UNKNOWN", "?");
				bi = bi.substring(0, bi.length() - 1);
				bi = bi.replace("\t", "/").replace(",", "/").replace("'", "�");
				bi = bi.replace("\"\"", "/");

				bw.write(bat + "\t" + bi + "\n");
			}
		}
		bw.flush();
		bw.close();
		br.close();
		// load CSV

		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(outPath));
		loader.setFieldSeparator("\t");
		loader.setNominalAttributes("first");
		// loader.setNominalLabelSpecs(arg0);
		loader.setStringAttributes("last");
		Instances data = loader.getDataSet();
		/*
		 * if(path.contains("blind")){ data.deleteAttributeAt(0); Add filter=
		 * new Add(); filter.setAttributeName("Sentiment"); filter.
		 * data.attribute("Sentiment").addStringValue(
		 * "positive,neutral,negative"); }
		 */data.setClassIndex(0);

		// save ARFF
		arffWriter(data, outPath + ".arff");

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

	public String getBowPath() {
		return bowPath;
	}

	public Instances getDataInstances(String path) throws IOException {
		ArffReader reader = new ArffReader(new FileReader(new File(path)));
		Instances rawData = reader.getData();

		return rawData;
	}

	public Instances getStr(String path) throws IOException {
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

	// TODO
	public void separator(int[] kop, String[] args, Instances dataset) throws IOException {
		Instances newData = new Instances(dataset);
		newData.clear();
		int where = 0;
		int last = 0;
		for (int i = 0; i < args.length; i++) {
			last = where + kop[i];
			for (int j = where; j < last; j++) {
				newData.add(dataset.get(where));
			}
			where = where + kop[i];
			System.out.println(kop[i] + "   " + newData.numInstances());
			arffWriter(newData, args[i].replace("tweetSentiment.", "New_").replace(".csv", "").replace("_2", ""));
			newData.clear();
		}
	}

	public Instances stringToWordVectorFilter(Instances rawData, boolean tfidf) throws Exception {
		StringToWordVector stringToWordVectorFilter = new StringToWordVector();
		stringToWordVectorFilter.setInputFormat(rawData);
		stringToWordVectorFilter.setIDFTransform(tfidf);
		stringToWordVectorFilter.setTFTransform(tfidf);
		;
		stringToWordVectorFilter.setWordsToKeep(4000);
		stringToWordVectorFilter.setOutputWordCounts(true);
		stringToWordVectorFilter.setLowerCaseTokens(true);
		Instances newData = Filter.useFilter(rawData, stringToWordVectorFilter);
		return newData;
	}

}
