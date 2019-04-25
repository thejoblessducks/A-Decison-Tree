import java.util.LinkedHashSet;

public class Atribute{
    String atribute; //Atribute name
    LinkedHashSet<String> values=new LinkedHashSet<>(); //all possible values for atribute
    public Atribute(String atribute,LinkedHashSet<String>values){
        this.atribute=atribute;
        this.values=values;
    }
    
    public boolean isContinuos(){
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
    public LinkedHashSet<String> getAtributeVals(){return values;}
    public boolean validValue(String value){return values.contains(value);}
    public String toString(){
        String s="<"+atribute+"> = [";
        for(String value : values)
            s+=value+",";
        s+="\b]\n";
        return s;
    }
}