import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Collections;

/*------------------------------------------------------------------------------
Auxiliar Class DataDivision
------------------------------------------------------------------------------*/
class DataDivision{
    /*
     * Will store the dada after a slit, that is, the data below the split (data
     *      equal or less than the split value) and the data above the split (
     *      not equal or bigger) 
     */
    ArrayList<DataEntry> data_above_split;
    ArrayList<DataEntry> data_below_split;

    DataDivision(ArrayList<DataEntry>data_above_split, ArrayList<DataEntry> data_below_split){
        this.data_above_split=data_above_split;
        this.data_below_split=data_below_split;
    }

    public ArrayList<DataEntry> getAbove(){return data_above_split;}
    public ArrayList<DataEntry> getBelow(){return data_below_split;}
}
/*------------------------------------------------------------------------------
Auxiliar Class BestSplit
------------------------------------------------------------------------------*/
class BestSplit{
    /*
     * Will store the atribute to split the data and, if the atribute is
     *      continuous, the value where to split 
     */
    String split_value;
    Atribute split;

    BestSplit(Atribute split, String split_value){
        this.split=split;
        this.split_value=split_value;
    }

    public Atribute getBestSplit(){return split;}
    public String getBestValue(){return split_value;}
}

/*------------------------------------------------------------------------------
DecisionTree Class
------------------------------------------------------------------------------*/
public class DecisionTree{
    /*
     * This class represents a node in the decision tree and has capabilities of
     *      creating child nodes, using the ID3 algorithm.
     * The class stores the parent_data_set that is the data_set used to build 
     *      the parent node if it exists, the data_set used in the current node
     *      a set of atributes yet to split and a Hashmap for all its children
     *      (tree nodes)
     */
    ArrayList<DataEntry> parent_data_set;
    ArrayList<DataEntry> data_set;
    LinkedHashSet<Atribute> atributes;    
    HashMap<String,DecisionTree> descendents = new HashMap<>();
    BestSplit split=null; //stores the split for this node
    String classification=""; //stores the classification of this node, if leaf

    public DecisionTree(ArrayList<DataEntry> data,ArrayList<DataEntry> parent_data,LinkedHashSet<Atribute>atributes){
        this.data_set=data;
        this.parent_data_set=parent_data;
        this.atributes=atributes;
    }

    public BestSplit getSplit(){return split;}
    public boolean isPure(ArrayList<DataEntry> data_set){
        /*
         * Checks if data is pure that is, if all the values in the given data_ste
         *      have the same classification
         */
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
        /*
         * Returns the classification that occurs most often in the given data 
         */
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
        /*
         * Calculates the entropy of a given data_set 
         */
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
        /*
         * Given a splited data, will calculate its overall entropy, note that
         *  all atributes can split the data in each of its values
         */
        int n=below.size() + above.size();

        double p_below = (double)((double)below.size()/n);
        double p_above = (double)((double)above.size()/n);

        double gain = (p_below*dataEntropy(below)) + (p_above*dataEntropy(above));
        return gain;
    }
    public DataDivision splitData(Atribute atribute, String value){
        /*
         * Given an atribute and one of its values to split on, this method splits
         *      the data in two major grups,the data above/below the value 
         */
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
        /*
         * Calculates the information gain of a categorical atribute 
         */
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
    public double round(String val, int places){
        /*
         * rounds the value of atribute to ease presentation 
         */
        if(places<0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(val);
        bd = bd.setScale(places,RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public BestSplit bestAtributeToSplit(){
        /*
         * Finds the best atribute to split on, before searching considers the 
         *      best atribute to be a random atribute in the data set 
         */
        Random rand = new Random();
        double max_info_gain=Double.MIN_VALUE;
        double set_entropy=dataEntropy(data_set);
        ArrayList<Atribute> at = new ArrayList<>();
        for(Atribute a : atributes) at.add(a);
        Atribute split_on=at.get(rand.nextInt(at.size()));
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
                        split_value=""+round(value,4);
                    }
                }
            }
            else{
                double gain=atributeInfoGainCategorical(atribute,data_set);
                double info_gain=set_entropy-gain;
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
         */
        if(classification==""){
            String s=data.getAtributeVal(split.getBestSplit().getAtribute());
            if(split.getBestSplit().isContinuous()){
                for(String key : descendents.keySet()){
                        String[] node = key.split(" ");
                        if(node[0].equals("<=") && Double.parseDouble(s) <= Double.parseDouble(node[1]))
                            return descendents.get(key).classify(data);
                        else if(node[0].equals(">") && Double.parseDouble(s) > Double.parseDouble(node[1]))
                            return descendents.get(key).classify(data);
                }
            }
            else{
                for(String key : descendents.keySet()){
                    String[] node = key.split(" ");
                    if(s.equals(node[1]))
                        return descendents.get(key).classify(data);
                }
            }
        }
        return classification;
    }

    public void buildTree(){
        /*
         * ID3 algorithm 
         */
        if(data_set.isEmpty())
            this.classification=mostCommonTarget(parent_data_set);
        else if(isPure(data_set))
            for(DataEntry data : data_set){classification=data.getClassification(); break;}
        else if(atributes.isEmpty())
            this.classification=mostCommonTarget(data_set);
        else{
            split = bestAtributeToSplit();
            if(split.getBestSplit() !=null && split.getBestSplit().isContinuous()){
                String value = split.getBestValue();
                DataDivision data_divided = splitData(split.getBestSplit(), value);

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
                left="<= "+ value;
                right="> "+ value;

                String c_r = (data_divided.getAbove().size() !=0 ? " (counter:"+data_divided.getAbove().size()+")" : "");
                String c_l = (data_divided.getBelow().size() !=0 ? " (counter:"+data_divided.getBelow().size()+")" : "");
                
                descendents.put(left+c_l,left_tree);
                descendents.put(right+c_r,right_tree);
            }
            else{
                for(String possible_value : split.getBestSplit().getAtributeVals()){
                    ArrayList<DataEntry> sub_data_set=new ArrayList<>();
                    for(DataEntry data : data_set)
                        if(data.getAtributeVal(split.getBestSplit().getAtribute()).equals(possible_value))
                                sub_data_set.add(data);

                    LinkedHashSet<Atribute> sub_atributes=new LinkedHashSet<Atribute>(atributes);
                    sub_atributes.remove(split.getBestSplit());
    
                    DecisionTree sub_tree=new DecisionTree(sub_data_set, data_set, sub_atributes);
                
                    int count=0;
                    for(DataEntry data : data_set){
                        if(data.getAtributeVal(split.getBestSplit().getAtribute()).equals(possible_value))
                            count++;
                    }
                    String s = (count!=0 ? " (counter:"+count+")" : "");

                    sub_tree.buildTree();
                    
                    descendents.put("= "+possible_value+s,sub_tree);
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
                    System.out.printf("%s    |  %s:\n", prefix, key);
                    descendents.get(key).printTree(prefix + "    | ");
				}
				else {  // last one
                    System.out.printf("%s     \\\n", prefix);
                    System.out.printf("%s       %s:\n", prefix, key);
					descendents.get(key).printTree(prefix + "      ");
				}
			}
        } else if(classification !=""){  // leaf node
            System.out.printf("%s  [%s]\n", prefix, classification);
		}
    }
}
