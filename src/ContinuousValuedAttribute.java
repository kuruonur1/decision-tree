
import java.util.ArrayList;

public class ContinuousValuedAttribute extends Attribute{ // TODO
	double v = 0;
	
	public ContinuousValuedAttribute(int id, String name){
        super(id, name);
        allEdgeValues = new String[2];
		allEdgeValues[0] = "t";
		allEdgeValues[1] = "f";
		//get a arraylist of all possible values for this attribute
		
		//process arraylist and find the V and set V
		//setAllEdgeValues(trainningSet);
	}
	@Override
	String whichEdgeValue(Instance instance) {
		double instanceValue = Double.parseDouble(instance.attValues[attId]);
		if(instanceValue > v)
			return "t";
		else
			return "f";		
	}

    @Override
    void handleMissingAttributeValues(ArrayList<Instance> instances) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
	

}
