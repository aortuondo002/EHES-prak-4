package baseline;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.net.search.fixed.NaiveBayes;
import weka.core.Instances;

public class Baseline {

	public static Baseline gureBaseline;

	public static synchronized Baseline getBaseline() {
		if (gureBaseline == null) {
			gureBaseline = new Baseline();
		}
		return gureBaseline;
	}

	public Baseline() {

	}

	public void NaiveBayer(Instances data) throws Exception {
		NaiveBayes nabe = new NaiveBayes();
		Evaluation eval = new Evaluation(data);
	}
}
