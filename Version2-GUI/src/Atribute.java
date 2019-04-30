import java.util.LinkedHashSet;
import java.util.ArrayList;
/*------------------------------------------------------------------------------
Class Atribute
------------------------------------------------------------------------------*/
public class Atribute{
    /*
     * Represents an atribute
     * Contains the name of the atribute and all its possible values 
     */
    String atribute; //Atribute name
    ArrayList<String> values; //all possible values for atribute
    public Atribute(String atribute,ArrayList<String>values){
        this.atribute=atribute;
        this.values=values;
    }
    
    public boolean isContinuous(){
        //Verify is the values in the atribute are continuous or not
        for(String str : values){
            try {
                Double.parseDouble(str);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;            
    }
    public String getAtribute(){return atribute;};
    public ArrayList<String> getAtributeVals(){return values;}
    public boolean validValue(String value){return values.contains(value);}
    public String toString(){
        String s="<"+atribute+"> = [";
        for(String value : values)
            s+=value+",";
        s+="\b]\n";
        return s;
    }
}
