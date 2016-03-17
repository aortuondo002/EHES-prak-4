package preprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

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

	public Instances stringToWordVector(Instances rawData) throws Exception {
		StringToWordVector stringToWordVectorFilter = new StringToWordVector();
		stringToWordVectorFilter.setInputFormat(rawData);
		stringToWordVectorFilter.setWordsToKeep(4000);
		stringToWordVectorFilter.setOutputWordCounts(true);
		stringToWordVectorFilter.setLowerCaseTokens(true);
		Instances dataToWordVector = Filter.useFilter(rawData, stringToWordVectorFilter);
		return dataToWordVector;
	}

	public int[] AttributeEvaluator(Instances data) throws Exception {
		InfoGainAttributeEval igae = new InfoGainAttributeEval();
		Ranker rank = new Ranker();
		String[] options = new String[2];
		options[0] = "-N -1";
		options[1] = "-T " + Long.toString(Long.MIN_VALUE) + "";
		rank.setOptions(options);
		return rank.search(igae, data);
	}

	public String converter(String arg) throws IOException {
		String outputFile= arg + ".arff";
		FileWriter fw = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(fw);

		FileReader fr = new FileReader(arg);
		BufferedReader br = new BufferedReader(fr);

		bw.write("@RELATION tweetSentiment\n\n");
		// bw.write("@ATTRIBUTE Topic string \n");
		// bw.write("@ATTRIBUTE ID NUMERIC \n");
		// bw.write("@ATTRIBUTE timestamp DATE \"HH:mm:ss\" \n");
		bw.write("@ATTRIBUTE Text string \n\n");
		bw.write("@ATTRIBUTE CLASS {neutral,positive,negative}\n");

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
