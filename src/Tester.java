
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author onur
 */
public class Tester {

    Node root;
    ArrayList<Instance> testInstances;
    private int correct = 0;

    public Tester(Node r, ArrayList<Instance> t) {
        root = r;
        testInstances = t;
    }

    double accuracy() {
        for (Instance instance : testInstances) {
            String str = root.classify(instance);
            if (str.equals(instance.getClassLabel())) {
                correct++;
            }
        }

        return (double) correct / testInstances.size();
    }

    void printResults() {
        for (Instance instance : testInstances) {
            String str = root.classify(instance);
            System.out.println(str);
        }
    }

    void printResultsToFile(String fileName) {
        try {
            Formatter formatter = new Formatter(fileName);
            StringBuilder strBuilder = new StringBuilder();

            for (Instance instance : testInstances) {
                String str = root.classify(instance);
                strBuilder.append(str + "\n");
            }
            formatter.format("%s", strBuilder.toString());
            formatter.close();

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
