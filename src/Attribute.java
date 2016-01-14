import java.util.ArrayList;

public abstract class Attribute {
	int attId;
	String attName;
	String[] allEdgeValues;

    public Attribute(int i, String s){
        attId = i;
        attName = s;
    }
	abstract String whichEdgeValue(Instance instance);
	//abstract void setAllEdgeValues(Instance[] instances);
	
	double entropyAccordingToThisAttribute(ArrayList<Instance> instances){
		double entropy = 0;
		int[] classDist = null;

        handleMissingAttributeValues(instances);
        
		for(int i=0; i < allEdgeValues.length;i++){
			int instanceCountForTheEdge = 0;
			classDist = new int[Builder.classLabelCount];
			for(Instance instance : instances){
				if(whichEdgeValue(instance).equals(allEdgeValues[i])){
					instanceCountForTheEdge++;
					classDist[instance.getClassId()]++;//modify
				}
			}
			entropy = entropy + ((double)instanceCountForTheEdge/instances.size() * Builder.entropy(classDist));
		}
		
		return entropy;
	}

    double splitInformation(ArrayList<Instance> instances){
        int[] edgeDistribution = new int[allEdgeValues.length];
        for(int i = 0; i < allEdgeValues.length; i++){
            for(Instance instance : instances){
                if(allEdgeValues[i].equals(whichEdgeValue(instance))){
                    edgeDistribution[i]++;
                }
            }
        }

        return Builder.entropy(edgeDistribution);
    }

    abstract void handleMissingAttributeValues(ArrayList<Instance> instances);

    public String toString(){
        String str = String.format("attId:%d attName:%s\n", attId, attName);
        str += "Edge Values:\n";
        str += "{";
        for(String s : allEdgeValues){
            str = str + s + ",";
        }
        str += "}";
        return str;
    }
}
