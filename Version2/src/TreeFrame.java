import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeFrame extends JFrame{
    /*public String class_name;
    public void processData(DecisionTree tree, DefaultMutableTreeNode node, String atribute_val_name){
        if(tree.getSplit()!=null){
            Iterator <String> it = tree.descendents.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (it.hasNext()) {  // more to come
					System.out.printf("%s    |  =%s:\n", prefix, key);
					descendents.get(key).printTree(prefix + "    | ");
				}
				else {  // last one
					System.out.printf("%s     \\\n", prefix);
					System.out.printf("%s       =%s:\n", prefix, key);
					descendents.get(key).printTree(prefix + "      ");
				}
			}
        }else{
            node.add(new DefaultMutableTreeNode(atribute_val_name+" : ["+class_name+" = "+tree.classification+"]"));
        }
    }
    TreeFrame(DecisionTree tree, String class_name){
        this.class_name=class_name;
        JTree t=null;
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(tree.split.getAtribute());
        processData(tree,node,"");
    }*/
}