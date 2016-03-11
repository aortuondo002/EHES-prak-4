package arffconverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.processing.Filer;

public class main {
	
	public static void main(String[] args)throws Exception{
		
		FileWriter fw = new FileWriter(args[0]+".arff");
		BufferedWriter bw = new BufferedWriter(fw);
		FileReader fr=new FileReader(args[0]);
		BufferedReader br = new BufferedReader(fr);
		
		String topic=null;
		String Sentiment=null;
		String tweetID=null;
		String tweetDate=null;
		String text=null;
		bw.write("@RELATION tweetSentiment.dev.csv\n\n");
		//bw.write("@ATTRIBUTE Topic  string \n");
		bw.write("@ATTRIBUTE CLASS {neutral,positive,negative}\n");
		//bw.write("@ATTRIBUTE ID NUMERIC \n");
		//bw.write("@ATTRIBUTE timestamp DATE  \"HH:mm:ss\" \n");
		bw.write("@ATTRIBUTE Text string \n\n");
		bw.write("@DATA\n");
		String lerroa=br.readLine();
		while ((lerroa=br.readLine())!=null){
			if(lerroa.startsWith("\"")&&!lerroa.startsWith("\"\"")){
			lerroa=lerroa.substring(1, lerroa.length());
			String[] atazak= lerroa.split("\",\"");
					if(!atazak[1].equalsIgnoreCase("irrelevant")){
					String data;
					data=/*atazak[3].substring(26, atazak[3].length())+" "+*/atazak[3].substring(11,19);
					bw.write(atazak[1]+",'"+atazak[4].replace("'", "´")+"'\n");
					bw.flush();}
			}
			}//while
			bw.close();
			br.close();
		 }
	

	private static ArrayList<String[]> datuakIrakurri(String fitxategia) throws IOException {
		FileReader fr = new FileReader(fitxategia);
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String[]> array = new ArrayList<>();
		String s;
		boolean klaseaDu = true;
		while ((s = br.readLine()) != null) {
			String[] sa = s.split("\n");
			if (sa[0].equalsIgnoreCase("ham")) {
				array.add(new String[] { sa[0], s.substring(4, s.length()) });
			} else if (sa[0].equalsIgnoreCase("spam")) {
				array.add(new String[] { sa[0], s.substring(5, s.length()) });
			} else {
				array.add(new String[] { s });
				klaseaDu = false;
			}
		}
		br.close();
		return array;
	}
}
	
			
		
		
		
		
	
