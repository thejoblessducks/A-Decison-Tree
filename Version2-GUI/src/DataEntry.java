import java.util.HashMap;
/*------------------------------------------------------------------------------
Class DataEntry
------------------------------------------------------------------------------*/
public class DataEntry{
    /*
     * Will represent a data entry, that is, one line in the given CSV file
     * The class will store the entry's classification
     * In order to help us ma nipulate the data we will store every value of the
     *      entry for every atribute in a HashMap 
     */
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
        /*
         * Given an entry classification, an array of all the atributes being 
         *      studied and the array of values for each of those atributes
         *      we create a new data entry
         *  
         * This constructor is the general constructor
         */
        this.final_classification=classification;
        int i=0;
        for(String atribute : atributes)
            entry_values.put(atribute,vals[i++]);
    }
    public DataEntry(String[]atributes,String[] vals){
        /*
         * Constructor similar to the previous, however, now we do not know the
         *      entry's classification, therefore this will be used for the 
         *      testing scenerio 
         */
        int i=0;
        for(String atribute : atributes)
            entry_values.put(atribute,vals[i++]);
    }

    public String getAtributeVal(String atribute){
        //return the value of the entry for a certain atribute O(1)
        return entry_values.get(atribute);
    }
    public String getClassification(){
        //return the entry's classification
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
