package core;

import java.io.IOException;

import preprocessor.Preprocessor;

public class mainBOW {

	public static void main(String[] args) throws IOException, Exception {
		Preprocessor pp = Preprocessor.getPreprocessor();
		for (int i = 0; i < args.length; i++) {
			pp.csv2arff(args[i]);
		}
		pp.bowMixer(args);

	}
}
