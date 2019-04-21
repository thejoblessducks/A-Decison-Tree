import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Collections;

public class DecisionTree{
    ArrayList<DataEntry> parent_data_set;
    ArrayList<DataEntry> data_set;
    LinkedHashSet<Atribute> atributes;    
    HashMap<String,DecisionTree> descendents = new HashMap<>();
    Atribute split=null;
    String classification="";

    public DecisionTree(ArrayList<DataEntry> data,ArrayList<DataEntry> parent_data,LinkedHashSet<Atribute>atributes){
        this.data_set=data;
        this.parent_data_set=parent_data;
        this.atributes=atributes;
    }

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
    public double dataEntropy(ArrayList<DataEntry> data_set){
        double sum=0;
        HashMap<String,Integer> count = new HashMap<>();
        for(DataEntry data : data_set){
            String data_class;
            if(!count.containsKey(data.getClassification()))
                count.put(data.getClassification(),0);
            count.put(data.getClassification(),count.get(data.getClassification())+1);
        }
        int size=data_set.size();
        for(String option : count.keySet()){
            double p=(double)count.get(option)/(double)size;
            sum+=(-1)*p*(Math.log(p)/Math.log(2));
        }
        return sum;
    }
    public Atribute getSplit(){return split;}

    public double atributeInfoGainCategorical(Atribute atribute,ArrayList<DataEntry> data_set,int n){
        double gain=0;
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
    public double atributeInfoGainContinuous(Atribute atribute,ArrayList<DataEntry> data_set,int n){
        double gain=0;
        String best_value="";
        for(String atribute_value : atribute.getAtributeVals()){
            HashMap<String,Integer> times_for_class=new HashMap<>();
            HashMap<String,Integer> up_times=new HashMap<>();
            int count=0;
            int count_up=0;
            for(DataEntry data : data_set){
                if(Double.parseDouble(data.getAtributeVal(atribute.getAtribute())) <= Double.parseDouble(atribute_value)){
                    count++;
                    String s=data.getClassification();
                    if(!times_for_class.containsKey(s))
                        times_for_class.put(s,0);
                    times_for_class.put(s,times_for_class.get(s)+1);
                }
                else{
                    count_up++;
                    String s=data.getClassification();
                    if(!up_times.containsKey(s))
                        up_times.put(s,0);
                        up_times.put(s,up_times.get(s)+1);
                }
            }
            if(count!=0){
                double sum=0;
                for(String c : times_for_class.keySet()){
                    double p=((double)times_for_class.get(c))/(double)count;
                    sum+=(-1)*p*(Math.log(p)/Math.log(2));
                }
                double val=(((double)count)/(double)n)*sum;
                if(val>gain){
                    gain=val;
                    best_value=atribute_value;
                }
            }
        }
        return gain;
    }
    public Atribute splitOnAtribute(LinkedHashSet<Atribute> atributes,ArrayList<DataEntry> data_set){
        double max_info_gain=Double.MIN_VALUE;
        double set_entropy=dataEntropy(data_set);
        int n=data_set.size();
        Atribute split_on=null;
        double info_gain=0;
        for(Atribute atribute : atributes){
            if(atribute.isContinuous())
                info_gain=atributeInfoGainContinuous(atribute,data_set,n);
            else
                info_gain=atributeInfoGainCategorical(atribute,data_set,n);
            info_gain = set_entropy - info_gain;
            if(info_gain>max_info_gain){
                max_info_gain=info_gain;
                split_on=atribute;
            }
        }
        return split_on;
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
    public String classify(DataEntry data){
        /*
         * Given a data entry, that is, a line of data we will travel through the
         *  tree in order to classify it 
        **/
        if(classification=="")
            return descendents.get(data.getAtributeVal(split.getAtribute())).classify(data);
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
            split=splitOnAtribute(atributes, data_set);
            if(split!=null){
                for(String possible_value : split.getAtributeVals()){
                    ArrayList<DataEntry> sub_data_set=new ArrayList<>();
                    for(DataEntry data : data_set){
                        if(split.isContinuous()){
                            if(Double.parseDouble(data.getAtributeVal(split.getAtribute())) <= Double.parseDouble(possible_value)){
                                sub_data_set.add(data);
                            }
                        }
                        else{
                            if(data.getAtributeVal(split.getAtribute()).equals(possible_value))
                                sub_data_set.add(data);
                        }
                    }

                    LinkedHashSet<Atribute> sub_atributes=new LinkedHashSet<Atribute>(atributes);
                    sub_atributes.remove(split);
    
                    DecisionTree sub_tree=new DecisionTree(sub_data_set, data_set, sub_atributes);
                
                    sub_tree.buildTree();
                    descendents.put(possible_value,sub_tree);
                }
            }
        }
    }
    public void prunTree(){prunTree(this);}
    private void prunTree(DecisionTree tree){
        if(tree.getSplit()!=null){
                ArrayList<String> to_remove=new ArrayList<>();                
                for(String val : tree.descendents.keySet())
                    if(tree.descendents.get(val).classification=="" && tree.descendents.get(val).getSplit()==null) to_remove.add(val);
                for(String val : to_remove)
                    if(tree.descendents.get(val).classification=="") tree.descendents.remove(val);

                /*HashMap<String,ArrayList<String>> repeted=new HashMap<>();
                for(String val : tree.descendents.keySet()){
                    if(tree.descendents.get(val).classification!=""){
                        if(!repeted.containsKey(tree.descendents.get(val).classification)){
                            ArrayList<String> tmp=new ArrayList<>();
                            tmp.add(val);
                            repeted.put(tree.descendents.get(val).classification,tmp);
                        }
                        else{
                            repeted.get(tree.descendents.get(val).classification).add(val);
                        }
                    }
                }
                for(String clss : repeted.keySet()){
                    ArrayList<String> cont =new ArrayList<>(repeted.get(clss));
                    for(int i=0;i<cont.size()-1;i++)
                        tree.descendents.remove(cont.get(i));
                }*/
                    
                for(DecisionTree node : tree.descendents.values()) prunTree(node);
        }
        else return;
    }
    public void printTree(){
        printTree("");
    }
    private void printTree(String prefix){
        if (split != null) {  // Still traveling down tree
			System.out.printf ("%s <%s>\n", prefix, split.getAtribute());
			System.out.printf ("%s    | \\\n", prefix);
			
			// Iterate over values
			Iterator <String> it = descendents.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
                if (it.hasNext()) {  // more to come
                    if(split.isContinuous() && descendents.get(key).classification!=""){
                        System.out.printf("%s    |  <=%s:\n", prefix, key);
                    }
                    else if(descendents.get(key).classification!=""){
                        System.out.printf("%s    |  = %s:\n", prefix, key);
                }
                    if(descendents.get(key).classification!="")
					    descendents.get(key).printTree(prefix + "    | ");
				}
				else {  // last one
					System.out.printf("%s     \\\n", prefix);
					System.out.printf("%s       = %s:\n", prefix, key);
					descendents.get(key).printTree(prefix + "      ");
				}
			}
        } else if(classification !=""){  // leaf node
            System.out.printf("%s  [%s]\n", prefix, classification);
		}
    }
}