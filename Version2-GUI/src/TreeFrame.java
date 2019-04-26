import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeFrame extends JFrame{
    String class_name;
    public void processData(DecisionTree tree, DefaultMutableTreeNode node, String atribute_val_name){
        if(tree.getSplit()!=null){
            DefaultMutableTreeNode atribute = new DefaultMutableTreeNode("<"+tree.getSplit().getBestSplit().getAtribute()+">");
            Iterator <String> it = tree.descendents.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
                DefaultMutableTreeNode new_node = new DefaultMutableTreeNode(key);
                processData(tree.descendents.get(key), new_node, atribute_val_name);
				atribute.add(new_node);
            }
            node.add(atribute);
        }else{
            node.add(new DefaultMutableTreeNode(atribute_val_name+" : ["+class_name+" = "+tree.classification+"]"));
        }
    }
    TreeFrame(DecisionTree tree,String class_name,String tree_name){
        this.class_name=class_name;
        JTree t=null;
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(tree_name);
        processData(tree,node,"");
        if(t!=null) this.remove(t);
        t = new JTree(node);
        this.add(t);
        this.setSize(350,350);
        this.setTitle("Decision tree for "+tree_name);
        this.setVisible(true);
    }
}