
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Builder {
    public static final String missingValueSign = "?";
    public static int classLabelCount = 0;
    static ArrayList<String> classLabelList = new ArrayList<String>();
    LinkedList<Attribute> attributes;
    BufferedReader reader;
    ArrayList<Instance> trainingInstances;
    ArrayList<Instance> testInstances;
    //String flag = null;
    boolean isTestData = false;
    Builder(String arffFile) {
        //flag = f;
        attributes = new LinkedList<Attribute>();
        trainingInstances = new ArrayList<Instance>();
        testInstances = new ArrayList<Instance>();
        try {
            reader = new BufferedReader(new FileReader(arffFile));

        } catch (FileNotFoundException ex) {
            System.out.println("Cannot open file:"+arffFile);
            System.exit(2);
        }
    }

    void processArffHeader() {
        String nextLine = null;
        StringTokenizer tokenizer = null;
        String declarationToken = null;
        int attId = 0, classId = 0;
        try {
            while ((nextLine = reader.readLine()) != null) {
                tokenizer = new StringTokenizer(nextLine);
                if(!tokenizer.hasMoreTokens())
                    continue;
                declarationToken = tokenizer.nextToken();
                declarationToken = declarationToken.toLowerCase();

                if (declarationToken.equals("@attribute")) {
                    String attNameToken = tokenizer.nextToken();
                    String valuesToken = "";
                    while (tokenizer.hasMoreTokens()) {
                        valuesToken = valuesToken.concat(tokenizer.nextToken());
                    }

                    String[] values = null;
                    if (valuesToken.charAt(0) == '{' &&
                            valuesToken.charAt(valuesToken.length() - 1) == '}') {
                        valuesToken = valuesToken.substring(1, valuesToken.length() - 1);
                        values = valuesToken.split(",");
//                        for(int i = 0; i < values.length;i++){
//                            values[i] = values[i].trim();
//                        }
                    } else {
                        //throw Exception
                        throw new RuntimeException("{<nominal-name1>, <nominal-name2>, <nominal-name3>, ...} expected.");
                    }
                    if (attNameToken.equals("class")) {
                        for (int i = 0; i < values.length; i++) {
                            classLabelList.add(values[i]);
                        }
                        Instance.classIndex = attId;
                        Builder.classLabelCount = values.length;
                        attId++;
                    } else {
                        attributes.add(new DiscreteValuedAttribute(attId,
                                attNameToken,
                                values));
                        attId++;
                    }

                } else if (declarationToken.equals("@relation")) {
                } else if (declarationToken.equals("@data")) {
                    //processArffData();
                    break;
                } else if (declarationToken.charAt(0) == '%') {
                } else {
                    System.out.println("@attribute || @relation || @data expected" +
                            "but " + declarationToken + " is found.\n");
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Builder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void processArffData() {
        //create instance array
        //set ContinousValuedAttributes' v value;

        String nextLine = null;
        String[] values = null;
        StringTokenizer tokenizer = null;
        String tmp = "";        
        try {
            while ((nextLine = reader.readLine()) != null) {
                tokenizer = new StringTokenizer(nextLine);
                tmp = "";
                while (tokenizer.hasMoreTokens()) {
                    tmp = tmp.concat(tokenizer.nextToken());
                }
                if(tmp.equals("@test")){
                    isTestData = true;
                    continue;
                }
               values = tmp.split(",");
                if(!isTestData){                     
                    trainingInstances.add(new Instance(values));
                }
                else{                                          
                    testInstances.add(new Instance(values));
                }
            }

            //reader.close();
        } catch (IOException ex) {
            Logger.getLogger(Builder.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getCause());
        }
    }

    public Node build() {
        LinkedList<Node> nodeQueue = new LinkedList<Node>();
        Node r = new Node(trainingInstances, this.attributes);
        nodeQueue.add(r);

        while (!nodeQueue.isEmpty()) {
            Node currentNode = nodeQueue.removeFirst();

            if (currentNode.attributeList.isEmpty() || currentNode.entropy == 0) {
                int max = 0;
                for (int i = 1; i < currentNode.classDist.length; i++) {
                    if (currentNode.classDist[i] > currentNode.classDist[max]) {
                        max = i;
                    }
                }
                currentNode.finalClassLabel = classLabelList.get(max); // majority voting
                //3 case is handled elsewhere
            } else {
                Attribute selected = null;
//                if(flag.equals("-r")){
//                    selected = selectAttribute_GainRatio(currentNode);
//                }else
                    selected = selectAttribute_Gain(currentNode);
                currentNode.attributeList.remove(selected);
                nodeQueue.addAll(currentNode.setAttribute(selected));
            }
        }
        return r;
    }
    Attribute selectAttribute_Gain(Node currentNode) {
        Attribute bestAttribute = null;
        Attribute tmp = null;
        double minEntropy =
                Double.MAX_VALUE;

        ListIterator<Attribute> ite = currentNode.attributeList.listIterator();
        while (ite.hasNext()) {
            tmp = ite.next();
            double e = tmp.entropyAccordingToThisAttribute(currentNode.instances);

            if (e < minEntropy) {
                bestAttribute = tmp;
                minEntropy = e;
            }
        }
        return tmp;
    }

    Attribute selectAttribute_GainRatio(Node currentNode) {
        Attribute bestAttribute = null;
        Attribute tmp = null;
        double maxGainRatio =
                Double.MIN_VALUE;

        ListIterator<Attribute> ite = currentNode.attributeList.listIterator();
        while (ite.hasNext()) {
            tmp = ite.next();
            double e = (currentNode.entropy
                    -
                    tmp.entropyAccordingToThisAttribute(currentNode.instances))
                    / tmp.splitInformation(currentNode.instances);

            if (e > maxGainRatio) {
                bestAttribute = tmp;
                maxGainRatio = e;
            }
        }
        return tmp;
    }

    static double entropy(int[] S) {
        double entropy = 0, p;
        int total = 0;
        for (int i = 0; i < S.length; i++) {
            total += S[i];
        }
        for (int i = 0; i < S.length; i++) {
            p = (double) S[i] / total;
            if (p == 0) {
                continue;
            }
            entropy = entropy + (p * (log(1 / p) / log(2)));
        }
        return entropy;
    }

    public static void main(String args[]) {
        if(args.length != 2){
            System.out.println("usage: java -jar Id3.jar  <input_file> <output_file>");
            return;
        }
        Builder builder = new Builder(args[0]);
        builder.processArffHeader();
        builder.processArffData();
        System.out.printf("# of training instances:%d\n", builder.trainingInstances.size());
        System.out.printf("# of test instances:%d\n", builder.testInstances.size());
        
        Node root = builder.build();

        
        Tester tester = new Tester(root, builder.testInstances);

        tester.printResultsToFile(args[1]);
    }
}
