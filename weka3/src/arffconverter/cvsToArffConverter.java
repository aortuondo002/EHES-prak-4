package arffconverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class cvsToArffConverter {

	public static final String Vers = "0.4";
		
	
	public void converter(String arg) throws IOException{
		FileWriter fw = new FileWriter(arg + ".arff");
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
					bw.write("'" + atazak[4].replace("'", "�") + "'," + atazak[1] + "\n");
				}
				bw.flush();
			}
		} // while
		bw.close();
		br.close();
	}
}

