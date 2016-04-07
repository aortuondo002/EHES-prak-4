package baseline;

import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.net.search.fixed.NaiveBayes;
import weka.classifiers.meta.CVParameterSelection;
import weka.core.Instances;

public class Baseline {

	public static Baseline gureBaseline;

	public Baseline() {

	}

	public static synchronized Baseline getBaseline() {
		if (gureBaseline == null) {
			gureBaseline = new Baseline();
		}
		return gureBaseline;
	}

	public void NaiveBayer(Instances data) throws Exception {
		NaiveBayes nabe = new NaiveBayes();
		Evaluation eval = new Evaluation(data);
	}
}
