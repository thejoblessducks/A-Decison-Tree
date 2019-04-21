import java.util.HashMap;

public class DataEntry{
    private String final_classification; //The target class value for this entry
    private HashMap<String,String> entry_values=new HashMap<>();
    /*  |*****************************|
     *  |Atribute 2|Val for Atribute 1|
     *  |-----------------------------|
     *  |Atribute 2|Val for Atribute 2|
     *  |.............................|
     *  |Atribute n|Val for Atribute n|
     *  |*****************************| 
    **/
    public DataEntry(String classification,String[]atributes,String[] vals){
        this.final_classification=classification;
        int i=0;
        for(String atribute : atributes)
            entry_values.put(atribute,vals[i++]);
    }

    public String getAtributeVal(String atribute){
        return entry_values.get(atribute);
    }
    public String getClassification(){
        return final_classification;
    }
    public String toString(){
        String s="DataEntry:\n\t";
        for(String atribute : entry_values.keySet())
            s+="<"+atribute+"> = "+entry_values.get(atribute)+"\n\t";
        s+="Classification: "+final_classification+"\n";
        return s;
    }
}