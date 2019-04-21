import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Collections;


class DataDivision{
    ArrayList<DataEntry> data_above_split;
    ArrayList<DataEntry> data_below_split;

    DataDivision(ArrayList<DataEntry>data_above_split, ArrayList<DataEntry> data_below_split){
        this.data_above_split=data_above_split;
        this.data_below_split=data_below_split;
    }

    public ArrayList<DataEntry> getAbove(){return data_above_split;}
    public ArrayList<DataEntry> getBelow(){return data_below_split;}
}
class BestSplit{
    String split_value;
    Atribute split;

    BestSplit(Atribute split, String split_value){
        this.split=split;
        this.split_value=split_value;
    }

    public Atribute getBestSplit(){return split;}
    public String getBestValue(){return split_value;}
}


public class DecisionTree{
    ArrayList<DataEntry> parent_data_set;
    ArrayList<DataEntry> data_set;
    LinkedHashSet<Atribute> atributes;    
    HashMap<String,DecisionTree> descendents = new HashMap<>();
    BestSplit split=null;
    String classification="";

    public DecisionTree(ArrayList<DataEntry> data,ArrayList<DataEntry> parent_data,LinkedHashSet<Atribute>atributes){
        this.data_set=data;
        this.parent_data_set=parent_data;
        this.atributes=atributes;
    }
    public BestSplit getSplit(){return split;}
    public boolean isPure(ArrayList<DataEntry> data_set){
        String s=null;
        for(DataEntry data: data_set){
            if(s==null)
                s=data.getClassification();
            else{
                if(!data.getClassification().equals(s))
                    return false;
            }
        }
        return true;
    }
    public String mostCommonTarget(ArrayList<DataEntry> data_set){
        HashMap<String,Integer> corresondance=new HashMap<>();
        for(DataEntry data : data_set){
            if(!corresondance.containsKey(data.getClassification())){
                corresondance.put(data.getClassification(),0);
            }
            corresondance.put(data.getClassification(),corresondance.get(data.getClassification())+1);
        }

        return Collections.max(corresondance.entrySet(),Map.Entry.comparingByValue()).getKey();
    }
    public double dataEntropy(ArrayList<DataEntry> data_set){
        double sum=0;
        HashMap<String,Integer> count = new HashMap<>();
        for(DataEntry data : data_set){
            String data_class=data.getClassification();
            if(!count.containsKey(data_class))
                count.put(data_class,0);
            count.put(data_class,count.get(data_class)+1);
        }
        int size=data_set.size();
        for(String option : count.keySet()){
            double p=(double)count.get(option)/(double)size;
            sum+=(-1)*p*(Math.log(p)/Math.log(2));
        }
        return sum;
    }
    public double overallEntropy(ArrayList<DataEntry> below,ArrayList<DataEntry> above){
        int n=below.size() + above.size();

        double p_below = (double)((double)below.size()/n);
        double p_above = (double)((double)above.size()/n);

        double gain = (p_below*dataEntropy(below)) + (p_above*dataEntropy(above));
        return gain;
    }
    public DataDivision splitData(Atribute atribute, String value){
        ArrayList<DataEntry> above = new ArrayList<>();
        ArrayList<DataEntry> below = new ArrayList<>();
        if(atribute.isContinuous()){
            for(DataEntry data : data_set){
                if(Double.parseDouble(data.getAtributeVal(atribute.getAtribute())) <= Double.parseDouble(value))
                    below.add(data);
                else above.add(data);
            }
        }
        else{
            for(DataEntry data : data_set){
                if(data.getAtributeVal(atribute.getAtribute()).equals(value))
                    below.add(data);
                else above.add(data);
            }
        }
        return new DataDivision(above, below);
    }
    public double atributeInfoGainCategorical(Atribute atribute,ArrayList<DataEntry> data_set){
        double gain=0;
        int n=data_set.size();
        for(String atribute_value : atribute.getAtributeVals()){
            HashMap<String,Integer> times_for_class=new HashMap<>();
            int count=0;
            for(DataEntry data : data_set){
                if(data.getAtributeVal(atribute.getAtribute()).equals(atribute_value)){
                    count++;
                    String s=data.getClassification();
                    if(!times_for_class.containsKey(s))
                        times_for_class.put(s,0);
                    times_for_class.put(s,times_for_class.get(s)+1);
                }
            }
            if(count!=0){
                double sum=0;
                for(String c : times_for_class.keySet()){
                    double p=((double)times_for_class.get(c))/(double)count;
                    sum+=(-1)*p*(Math.log(p)/Math.log(2));
                }
                gain+=(((double)count)/(double)n)*sum;
            }
        }
        return gain;
    }
    public BestSplit bestAtributeToSplit(){
        double max_info_gain=Double.MIN_VALUE;
        double set_entropy=dataEntropy(data_set);
        Atribute split_on=null;
        String split_value="";

        for(Atribute atribute : atributes){
            if(atribute.isContinuous()){
                for(String value : atribute.getAtributeVals()){
                    DataDivision data_divided = splitData(atribute,value);
                    double gain = overallEntropy(data_divided.getBelow(),data_divided.getAbove());
                    double info_gain= set_entropy - gain;
                    if(info_gain > max_info_gain){
                        max_info_gain=info_gain;
                        split_on=atribute;
                        split_value=value;
                    }
                }
            }
            else{
                /*double gain=atributeInfoGainCategorical(atribute,data_set);
                double info_gain=set_entropy-gain;
                if(info_gain > max_info_gain){
                    max_info_gain=info_gain;
                    split_on=atribute;
                    split_value="";
                }*/
                double gain=0;
                double info_gain=0;
                for(String value : atribute.getAtributeVals()){
                    DataDivision data_divided= splitData(atribute,value);
                    gain+=overallEntropy(data_divided.getBelow(),data_divided.getAbove());
                }
                info_gain=set_entropy -  gain;
                if(info_gain > max_info_gain){
                    max_info_gain=info_gain;
                    split_on=atribute;
                    split_value="";
                }

            }
        }
        return new BestSplit(split_on,split_value);
    }
    public String classify(DataEntry data){
        /*
         * Given a data entry, that is, a line of data we will travel through the
         *  tree in order to classify it 
        **/
        if(classification=="")
            return descendents.get(data.getAtributeVal(split.getBestSplit().getAtribute())).classify(data);
        return classification;
    }

    public void buildTree(){
        if(data_set.isEmpty())
            this.classification=mostCommonTarget(parent_data_set);
        else if(isPure(data_set))
            for(DataEntry data : data_set){classification=data.getClassification(); break;}
        else if(atributes.isEmpty())
            this.classification=mostCommonTarget(data_set);
        else{
            split = bestAtributeToSplit();
            if(split.getBestSplit().isContinuous()){
                DataDivision data_divided = splitData(split.getBestSplit(), split.getBestValue());

                LinkedHashSet<Atribute> left_atributes=new LinkedHashSet<Atribute>(atributes);
                LinkedHashSet<Atribute> right_atributes=new LinkedHashSet<Atribute>(atributes);

                left_atributes.remove(split.getBestSplit());
                right_atributes.remove(split.getBestSplit());
                
                
                ArrayList<DataEntry> above = new ArrayList<>(data_divided.getAbove());
                ArrayList<DataEntry> below = new ArrayList<>(data_divided.getBelow());

                DecisionTree left_tree=new DecisionTree(below, data_set, left_atributes);
                DecisionTree right_tree=new DecisionTree(above, data_set, right_atributes);
                
                left_tree.buildTree();
                right_tree.buildTree();

                String right="", left="";
                left="<= "+ split.getBestValue();
                right="> "+split.getBestValue();
                descendents.put(left,left_tree);
                descendents.put(right,right_tree);
            }
            else{
                for(String possible_value : split.getBestSplit().getAtributeVals()){
                    ArrayList<DataEntry> sub_data_set=new ArrayList<>();
                    for(DataEntry data : data_set){
                        if(data.getAtributeVal(split.getBestSplit().getAtribute()).equals(possible_value))
                                sub_data_set.add(data);
                    }

                    LinkedHashSet<Atribute> sub_atributes=new LinkedHashSet<Atribute>(atributes);
                    sub_atributes.remove(split.getBestSplit());
    
                    DecisionTree sub_tree=new DecisionTree(sub_data_set, data_set, sub_atributes);
                
                    sub_tree.buildTree();
                    descendents.put(possible_value,sub_tree);
                }
            }
        }
    }
    
    public void printTree(){
        printTree("");
    }
    private void printTree(String prefix){
        if (split != null) {  // Still traveling down tree
			System.out.printf ("%s <%s>\n", prefix, split.getBestSplit().getAtribute());
			System.out.printf ("%s    | \\\n", prefix);
			
			// Iterate over values
			Iterator <String> it = descendents.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
                if (it.hasNext()) {  // more to come
                    if(split.getBestSplit().isContinuous()/* && descendents.get(key).classification!=""*/){
                        System.out.printf("%s    |  %s:\n", prefix, key);
                    }
                    else if(descendents.get(key).classification!=""){
                        System.out.printf("%s    |  = %s:\n", prefix, key);
                }
                    if(descendents.get(key).classification!="")
					    descendents.get(key).printTree(prefix + "    | ");
				}
				else {  // last one
                    System.out.printf("%s     \\\n", prefix);
                    if(split.getBestSplit().isContinuous())
                        System.out.printf("%s       %s:\n", prefix, key);
                    else 
                        System.out.printf("%s       = %s:\n", prefix, key);

					descendents.get(key).printTree(prefix + "      ");
				}
			}
        } else if(classification !=""){  // leaf node
            System.out.printf("%s  [%s]\n", prefix, classification);
		}
    }
}