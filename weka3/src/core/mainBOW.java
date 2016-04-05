package core;

import java.io.IOException;

import preprocessor.Preprocessor;

public class mainBOW {

	public static void main(String[] args) throws IOException, Exception {
		Preprocessor PP = Preprocessor.getPreprocessor();
		for (int i = 0; i < args.length; i++) {
			PP.converter(args[i]);
		}
		PP.bowGenerator(args);
	}
}
