
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Node {

    ArrayList<Instance> instances;
    double entropy = 0;
    private Attribute attribute = null;
    String finalClassLabel = null;
    HashMap<String, Node> hash = null;
    boolean isPruned = false; // reserved for future implementing
    List<Attribute> attributeList = null;
    int[] classDist = null;

    Node(ArrayList<Instance> ins, List<Attribute> attList) {
        attributeList = attList;
        instances = ins;
        classDist = new int[Builder.classLabelCount];
        for (Instance i : instances) {
            classDist[i.getClassId()]++;
        }
        entropy = Builder.entropy(classDist);
    }

    LinkedList<Node> setAttribute(Attribute att){
        LinkedList<Node> childNodes = new LinkedList<Node>(); // list for childs that need further processing
        attribute = att;
        hash = new HashMap<String, Node>();
        ArrayList<Instance> edgeInstances;
        Node child;

        for(String edge : attribute.allEdgeValues){
            edgeInstances = new ArrayList<Instance>();

            for(Instance i : instances){
                if(attribute.whichEdgeValue(i).equals(edge)){
                    edgeInstances.add(i);
                }
            }
            if(edgeInstances.size() == 0){ // no instance goes to this edge
                int m = 0;
                for(int i=1; i < classDist.length;i++){
                    if(classDist[i] > classDist[m])
                        m = i;
                }
                child = new Node(Builder.classLabelList.get(m)); // majority voting
            }else{
                List<Attribute> childAttList = new LinkedList<Attribute>();
                for(Attribute a : attributeList){
                    if(!a.equals(attribute))
                        childAttList.add(a);
                }
                child = new Node(edgeInstances, childAttList);
                childNodes.add(child);
            }
            hash.put(edge, child); // add child to parent
        }
        return childNodes; // return childs that need further processing
    }

    String classify(Instance instance) {
        if (finalClassLabel != null) {
            return finalClassLabel;
        } else {
            if (attribute != null) {
                return hash.get(attribute.whichEdgeValue(instance)).classify(instance);
            } else {
                throw new RuntimeException("This node has problem.\n"+ this.toString());
            }
        }
    }

    Node(String classLabel) {
        finalClassLabel = classLabel;
    }
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        buffer.append('[');
        for (int i = 0; i < classDist.length; i++) {
            buffer.append(classDist[i] + " ");
        }
        buffer.append(']');

        return String.format("%s e:%f c:%s\n", buffer.toString(),
                entropy, finalClassLabel) + attribute;
    }
}
