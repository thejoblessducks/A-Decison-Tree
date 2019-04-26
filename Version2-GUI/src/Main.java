public class Main{
    public static void main(String[] args){
        System.out.println(args[0]);
        System.out.println(args.length);
        if(args.length != 1)
            new TreeBuilder(false,null);
        else
            new TreeBuilder(true,args[0]);
    }
}
