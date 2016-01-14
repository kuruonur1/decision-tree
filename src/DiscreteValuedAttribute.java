
import java.util.ArrayList;

public class DiscreteValuedAttribute extends Attribute{
	
	public DiscreteValuedAttribute(int i,
            String name,
            String[] arr){
        super(i, name);
		allEdgeValues = arr;
	}

	@Override
	String whichEdgeValue(Instance instance) {
        if(instance.attValues[attId].equals("?"))
            return allEdgeValues[0];
        else
            return instance.attValues[attId];
	}

    @Override
    void handleMissingAttributeValues(ArrayList<Instance> instances) {
        int attValueDist[] = null;

        for(Instance instance : instances){
            if(instance.attValues[attId].equals(Builder.missingValueSign)){
                attValueDist = new int[allEdgeValues.length];

                for(Instance jIns : instances){
                    if(jIns.equals(instance) ||
                            jIns.attValues[attId].equals(Builder.missingValueSign))
                        continue;
                    if(jIns.getClassLabel().equals(instance.getClassLabel())){
                        for(int i=0; i < allEdgeValues.length;i++){
                            if(jIns.attValues[attId].equals(allEdgeValues[i])){
                                attValueDist[i]++;
                                break;
                            }
                        }
                    }
                }
                int max = 0;
                for(int i=1; i < attValueDist.length; i++){
                    if(attValueDist[i] > attValueDist[max])
                        max = i;
                }
                instance.attValues[attId] = allEdgeValues[max];
            }
        }
    }

}
