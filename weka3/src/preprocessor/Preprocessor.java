package preprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
	public void bow(String[] args) throws IOException{
		
		String[] arffs=new String[3];
		for (int a = 0; a < args.length; a++) {
			arffs[a]=args[a]+".arff";
		}
		FileWriter fw = new FileWriter("bow.arff");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("@RELATION BOW \n\n");
		bw.write("@ATTRIBUTE Text string \n\n");
		bw.write("@ATTRIBUTE Klase {neutral,positive,negative}\n");
		bw.write("@DATA\n");
		int[]kop = {0,0,0};
		for (int i = 0; i < arffs.length; i++) {

			FileReader fr = new FileReader(arffs[i]);
			BufferedReader br = new BufferedReader(fr);
				String lerroa = br.readLine();

				while ((lerroa = br.readLine()) != null) {
					if (!lerroa.startsWith("@")) {
						bw.write(lerroa+"\n");
						kop[i]++;
					}
				}
			System.out.println();
			fr.close();

		}
		bw.close();
	}
	
	/**
	 * Metodo honek csv fitxategia arff fitxategira bihurtzen du
	 * 
	 * @param arg
	 * @return code(@String) path-a
	 * @throws IOException
	 */

	public void converter(String arg) throws IOException {
		String outputFile = arg + ".arff";
		FileWriter fw = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(fw);
		FileReader fr = new FileReader(arg);
		BufferedReader br = new BufferedReader(fr);
		bw.write("@RELATION " + arg + "\n\n");
		bw.write("@ATTRIBUTE Text string \n\n");
		bw.write("@ATTRIBUTE Klase {neutral,positive,negative}\n");
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

	public void BOWmerger(String[] args) throws Exception {
		String[] arffs=new String[3];
		for (int a = 0; a < args.length; a++) {
			arffs[a]=args[a]+".arff";
		}
		int i = 0;
		Instances[] datasets = new Instances[3];
		Instances[] structures = new Instances[3];
		int[] kop= new int[3];
		while (i < arffs.length) {
			BufferedReader pathReader = new BufferedReader(new FileReader(arffs[i]));
			ArffReader arff = new ArffReader(pathReader);
			
			arff.getData().deleteAttributeAt(0);
			arff.getData().deleteAttributeAt(0);
			datasets[i] = arff.getData();
			structures[i] = arff.getStructure();
			kop[i]=arff.getData().numInstances();
			i++;
		}
		File f = new File("C:\\Users\\Ray\\Downloads\\tweet_sentiment\\tweet_sentiment\\BOW.arff");
		ArffSaver saver= new ArffSaver();
		BufferedWriter bw= new BufferedWriter(new FileWriter(f));
		bw.append("#"+kop[0]+"#"+kop[1]+"#"+kop[2]);
		
	}
}
