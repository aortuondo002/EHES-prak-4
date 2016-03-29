package preprocessor;

import java.beans.FeatureDescriptor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.ArffLoader.ArffReader;
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

	public void BOWmerger(String[] args) throws Exception {
		int i = 0;
		Instances[] datasets = new Instances[3];
		Instances[] structures = new Instances[3];
		ArrayList<String> atributes = new ArrayList<String>();
		while (i < args.length) {
			BufferedReader pathReader = new BufferedReader(new FileReader(args[i]));
			ArffReader arff = new ArffReader(pathReader);
			datasets[i] = arff.getData();
			structures[i] = arff.getStructure(); 
			i++;
		}
		File f = new File("C:\\Users\\Ray\\Downloads\\tweet_sentiment\\tweet_sentiment\\BOW.arff");
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		Iterator<String> itr = atributes.iterator();
		
	}

	/*public void Batidora(String[] paths) throws Exception{
		Instances[] instancias= new Instances[3];
		Instances mix=null;
		Instances atributos=null;
		ArffSaver saver= new ArffSaver();
		File file= new File("C:\\Users\\Ray\\Downloads\\tweet_sentiment\\tweet_sentiment\\Mix2016.arff");
		saver.setDestination(file);
		for(int i=0;i<paths.length;i++){
			BurfferedReader reader= new ArffReader(new BufferedReader(new FileReader(paths[i])));
			instancias[i]=reader.getData();
			atributos=reader.getStructure();
			mix=
			atributos=Instances.mergeInstances(atributos, reader.getStructure());	
			mix= Instances.mergeInstances(mix, reader.getData());
		}
		
		saver.setStructure(atributos);
		saver.setInstances(mix);
		Instances filtered= stringToWordVectorFilter(saver.getInstances());
		saver.setInstances(filtered);
		saver.writeBatch();
		
		
	}*/
	/**Metodo honek csv fitxategia arff fitxategira bihurtzen du
	 * 
	 * @param arg
	 * @return code(@String) path-a
	 * @throws IOException
	 */
	public ArrayList<String> getAttributes(String path) throws IOException{
		ArrayList<String> atributes= new ArrayList<String>();
		BufferedReader reader= new BufferedReader(new FileReader(path));
		String lerroa=null;
		while ((lerroa = reader.readLine()) != null) {
			if(lerroa.startsWith("@attribute")){
				lerroa=lerroa.substring(0, 5);
				atributes.add(lerroa);
			}
			
		}
		return atributes;
		
	}
	/*public ArrayList<String> getInstantziak(String path) throws IOException{
		
		
	}*/	
	public String converter(String arg) throws IOException {
		String outputFile= arg + ".arff";
		FileWriter fw = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(fw);
		FileReader fr = new FileReader(arg);
		BufferedReader br = new BufferedReader(fr);
		bw.write("@RELATION "+arg+"\n\n");
		bw.write("@ATTRIBUTE Text string \n\n");
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
		return outputFile;
	}
	

}
