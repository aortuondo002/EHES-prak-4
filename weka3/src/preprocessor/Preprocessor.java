package preprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.pmml.jaxbbindings.COMPAREFUNCTION;
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

	public Instances bow;
	public Instances dev;
	public Instances train;
	public Instances test;
	public int[] atrLista;

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
		saver.setFile(new File(args[0].replace("twitterSentiment\\S+", "BOW")));
		Instances bow = null;
		Instances[] toSave = new Instances[3];
		for (int i = 0; i < kop.length; i++) {
			if (args[i].contains("train")) {
				toSave[i] = train;
			} else if (args[i].contains("test")) {
				toSave[i] = test;
			} else {
				toSave[i] = dev;
			}
			kop[i] = toSave[i].numInstances();
		}
		this.bow = new Instances(toSave[0]);
		for (int i = 1; i < toSave.length; i++) {
			for (int j = 0; j < toSave[i].numInstances(); j++) {
				this.bow.add(toSave[i].instance(j));
			}
		}
		saver.setInstances(this.bow);
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
				bi = bi.replace("\t", "/").replace(",", "/").replace("'", "´");
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
		if (path.contains("train")) {
			train = data;
		}
		if (path.contains("test")) {
			test = data;
		}
		if (path.contains("dev")) {
			dev = data;
		}

	}

	public Instances filterAtributes(Instances data) throws Exception {
		AttributeSelection filter = new AttributeSelection();
		InfoGainAttributeEval eval = new InfoGainAttributeEval();
		Ranker search = new Ranker();
		data.setClassIndex(0);
		search.setThreshold(0.00001);
		filter.setEvaluator(eval);
		filter.setSearch(search);
		filter.SelectAttributes(data);
		this.atrLista = filter.selectedAttributes();
		return this.atributuakKendu(data);

	}

	public Instances[] getInstances() {
		return new Instances[] { this.dev, this.train, this.test };
	}

	public void setter(String path, Instances data) {
		if (path.contains("test")) {
			this.test=data;}
			else if(path.contains("train")){
				this.train=data;	
			}else this.dev=data;
			
		
	}

	public Instances getDataInstance(String name) {
		if (name.contains("train")) {
			return train;
		} else if (name.contains("test")) {
			return test;
		} else {
			return dev;
		}
	}

	public Instances quitSparseValues(Instances Data) throws Exception {
		SparseToNonSparse filter = new SparseToNonSparse();
		filter.setInputFormat(Data);
		Instances newData = Filter.useFilter(Data, filter);
		return newData;
	}

	public void separator(int[] kop, String[] args, Instances dataset) throws IOException {
		Instances separate=new Instances(dataset);
		
		separate.clear();
		Instances[] asd= new Instances[3];
		for (int i = 0; i < asd.length; i++) {
			asd[i]=separate;
		}
		int start = 0,end=0;
		for (int i = 0; i < kop.length; i++) {
			separate.clear();
			end=end+kop[i];
			while(start!=end){
				separate.add(dataset.instance(start));
				start++;
				
			}
			
			asd[i]=separate;
			arffWriter(separate,args[i].replaceAll("_2.+","bow.arff"));
		}
		for (int i = 0; i < args.length; i++) {
			setter(args[i], asd[i]);
		}
	}

	public Instances stringToWordVectorFilter(Instances rawData, boolean tfidf) throws Exception {
		StringToWordVector stringToWordVectorFilter = new StringToWordVector();
		stringToWordVectorFilter.setInputFormat(rawData);
		stringToWordVectorFilter.setIDFTransform(tfidf);
		stringToWordVectorFilter.setTFTransform(tfidf);
		stringToWordVectorFilter.setWordsToKeep(4000);
		stringToWordVectorFilter.setOutputWordCounts(true);
		stringToWordVectorFilter.setLowerCaseTokens(true);
		Instances newData = Filter.useFilter(rawData, stringToWordVectorFilter);
		return newData;
	}

	public Instances atributuakKendu(Instances test) throws Exception {
		Remove r = new Remove();
		r.setAttributeIndicesArray(atrLista);
		r.setInvertSelection(true);
		r.setInputFormat(test);
		return Filter.useFilter(test, r);
	}

}
