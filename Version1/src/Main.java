public class Main{
    public static void main(String[] args){
        if(args.length != 1)//was given a file => CSV file testing
            new TreeBuilder(false,null);
        else //file not given => one line testing
            new TreeBuilder(true,args[0]);
    }
}
