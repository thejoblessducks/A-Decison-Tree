import java.util.HashSet;

public class Atribute{
    String atribute; //Atribute name
    HashSet<String> values=new HashSet<>(); //all possible values for atribute

    public Atribute(String atribute,HashSet<String>values){
        this.atribute=atribute;
        this.values=values;
    }
    
    public String getAtribute(){return atribute;};
    public HashSet<String> getAtributeVals(){return values;}
    public boolean validValue(String value){return values.contains(value);}
    public String toString(){
        String s="<"+atribute+"> = [";
        for(String value : values)
            s+=value+",";
        s+="\b]\n";
        return s;
    }
}