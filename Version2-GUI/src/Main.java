public class Main{
    public static void main(String[] args){
        if(args.length != 1)
            new TreeBuilder(false,null);
        else
            new TreeBuilder(true,args[0]);
    }
}
