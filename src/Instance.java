
public class Instance {
    static int classIndex = 0;

	String[] attValues;
	private int classId;

    public Instance(String[] values){
        attValues = values;

        classId = Builder.classLabelList.indexOf(attValues[Instance.classIndex]);
        if(classId == -1)
            throw new RuntimeException("Unknown classLabel:"+attValues[Instance.classIndex]);
    }

    public int getClassId(){
        return classId;
    }
    public String getClassLabel(){
        return attValues[Instance.classIndex];
    }
    public String toString(){
        String str = "";

        for(int i=0; i < attValues.length;i++){
            if(i == classIndex)
                continue;

            str = str.concat(attValues[i]+" ");
        }
        str = str.concat(String.format("classLabel:%s classId:%d\n",
                attValues[Instance.classIndex], classId));

        return str;

    }
}
