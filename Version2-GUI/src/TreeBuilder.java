import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Collections;

/*------------------------------------------------------------------------------
TreeBuider class
------------------------------------------------------------------------------*/
public class TreeBuilder{
    /*
     * This class contains the basic methods to enable tree creation, from 
     *      reading the CSV file, to constructing all the structures required 
     *      to buid the tree 
     */
    public static Scanner in = new Scanner(System.in);

    public static String[][] DATA;
    public static int rows,cols;
    public static String atribute_type;


    TreeBuilder(boolean test_file,String test_file_name){
        showOptions(test_file,test_file_name);
    }

    public static void showOptions(boolean test,String filename) {
        //Main Menu
        System.out.println("What do you whant to do?");
        System.out.println("1)Build Tree;\n2)exit;");
        switch(in.nextInt()){
            case 1:
                buildTree(test,filename);
                System.exit(0);
            break;
            case 2: System.exit(0);break;
            default:
                System.out.println("Invalid option");
                System.exit(0);
            break;
        }
    }
    public static void buildTree(boolean test,String filename){
        //Buider class
        File[] listOfFiles = displayFiles("CSV");
        int i=in.nextInt();
        String str=listOfFiles[i-1].getName();
        String s=str.substring(0, str.lastIndexOf('.'));

        System.out.println("Building tree for "+s);
        readFile("../CSV/"+str);
        
        //Create atributes array
            String[] atributes=makeAtributeArray();

        //Create set of atributes
            LinkedHashSet<Atribute> atributes_set=makeAtributeSet(atributes);

        //entry exploration
            ArrayList<DataEntry> data_set=makeDataSet(atributes);

        //tree creation
            DecisionTree tree = new DecisionTree(data_set, data_set, atributes_set);
            tree.buildTree();
            tree.printTree();
            new TreeFrame(tree,DATA[0][DATA[0].length-1],s);

        //testing part
            if(test){ //file was given => classify a set of data
                System.out.println();
                //create all etries for the test file 
                    ArrayList<DataEntry> data_to_test = prepareTestFile(filename, atributes);
                for(DataEntry entry : data_to_test) //classify every entry
                    System.out.println("Classification: "+tree.classify(entry));
            }else{//no file was given => one line classification (optional)
                try {
                    while(!(s=in.nextLine()).equals("no")){
                        if(s.equals("yes"))
                            System.out.println("\nClassification: "+tree.classify(makeTest(atributes)));
                        System.out.println("\nWant to test (yes/no)");
                    }
                } catch (Exception e) {
                    System.out.println("Error, terminating program");
                }
            }
    }
    public static void readFile(String filename){
        delimitDataSet(filename);
        //reades the file
        File file = new File(filename);
        try {
            Scanner filein = new Scanner(file);
            
            String[] data=filein.next().split(",");
            int r=0;
            addLine(data,r);
            r++;
            while(filein.hasNext()){
               data=filein.next().split(",");
               //for(String d : data) System.out.print(d+" ");
               addLine(data, r);
               r++;
            }
            filein.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void delimitDataSet(String filename){
        //Creates the DATA array, ignoring the header lines and the first column, 
        //Since the first column is only the elements numbers they aren't required 
        int r=0,c=0;
        try {
            Scanner f = new Scanner(new BufferedReader( new FileReader(filename)));
            String s;
            while(f.hasNextLine()){
                s=f.nextLine();
                if(c==0) c=s.split(",").length;
                r++;
            }
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        rows=r;
        cols=c-1;
        DATA=new String[rows][cols];
    }
    public static void addLine(String[] data,int r){
        //adds all the values of an elemnt to its line in DATA (all atributes+class)
        for(int c=1;c<data.length;c++)
            DATA[r][c-1]=data[c];
    }
    public static File[] displayFiles(String dir){
        //Shows all the files that can be used as datasets, all files are in CSV/
        File folder = new File("../"+dir);
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles.length==0){
            System.out.println("No files");
            System.exit(0);
        }
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println((i+1)+")" + listOfFiles[i].getName());
            }
        }
        return listOfFiles;
    }
    public static void printDataSetArray(String[][] data){
        System.out.print("{");
        for(String line[] : data){
            System.out.print("[");
            for(String ele : line)
                System.out.print(ele+",");
            System.out.println("]");
        }
        System.out.println("}");
    }

    public static void printAtributeArray(String[] atributes){
        System.out.print("\nAtributes\n\t");
        for(String at:atributes)
            System.out.print("<"+at+">\n\t");
        System.out.println();
    }
    public static String[] makeAtributeArray(){
        String[] atributes=new String[DATA[0].length-1];
        int i=0;
        for(String atribute : DATA[0]){
            if(i<DATA[0].length-1)
                atributes[i++]=atribute;
        }
        return atributes;
    }

    public static void printAtributeSet(HashSet<Atribute> atribute_set){
        System.out.println("\nAttribute set:");
        for(Atribute a : atribute_set)
            System.out.print(a);
        System.out.println();
    }
    public static LinkedHashSet<Atribute> makeAtributeSet(String[] atributes){
        LinkedHashSet<Atribute> atributes_set=new LinkedHashSet<>();
        int i=0;
        for(String atribute_name : atributes){
            ArrayList<String> values=new ArrayList<>();
            for(int r=1;r<DATA.length;r++)
                if(!values.contains(DATA[r][i]))
                    values.add(DATA[r][i]);
            try {
                //data of atribute is continuous
                ArrayList<Double> continuous = new ArrayList<>();
                for(String val : values)
                    continuous.add(Double.parseDouble(val));

                Collections.sort(continuous);
                //Calculates the mid points in data
                ArrayList<Double> average = new ArrayList<>();
                double avg=0;
                for(int j=1; j<continuous.size();j++){
                    avg=(double)((continuous.get(j-1)+continuous.get(j))/2);
                    average.add(avg);
                }
                Collections.sort(average);
                continuous.clear();
                continuous=average;
                values.clear();
                for(Double val : continuous)
                    values.add(""+val);
            } catch (NumberFormatException e){}
            Atribute atribute = new Atribute(atribute_name, values);
            atributes_set.add(atribute);
            i++;
        }
        return atributes_set;
    }

    public static ArrayList<DataEntry> makeDataSet(String[] atributes){
        int index=DATA[0].length-1;
        ArrayList<DataEntry> data_set=new ArrayList<>();
        for(int r=1;r<DATA.length;r++){
            ArrayList<String> values=new ArrayList<>();
            for(int c=0;c<index;c++) values.add(DATA[r][c]);

            String[] data_values = new String[values.size()];
            int i=0;
            for(String val : values) data_values[i++]=val;

            DataEntry data= new DataEntry(DATA[r][index], atributes, data_values);
            data_set.add(data);
        }
        return data_set;
    }

    public static DataEntry makeTest(String[] atributes){
        //Method for one line testing
        ArrayList<String> values = new ArrayList<>();
        System.out.println("Give test line:");
        String s = in.nextLine();
        String[] data = s.split(",");
        if(data.length < atributes.length || data.length > atributes.length){
            String ex="";
            for(int i=0; i<atributes.length-1;i++) ex+=atributes[i]+",";
            ex+=atributes[atributes.length-1];
            System.out.println("Invalid format, you gave:"+s+"\nShould have given [\""+ex+"\"]");
            System.out.println("Try again");
            return makeTest(atributes);
        }

        for(int c=0;c<atributes.length;c++)
            values.add(data[c]);
        data=new String[values.size()];
        int i=0;
        for(String val : values) data[i++]=val;
        DataEntry entry = new DataEntry(atributes,data);
        return entry;
    }

    public static ArrayList<DataEntry> prepareTestFile(String filename,String[] atributes){
        //Method for CSV file testing 
        File file = new File(filename);
        ArrayList<DataEntry> test_data = new ArrayList<>();
        try {
            Scanner filein = new Scanner(file);
            String []data;
            filein.next(); //remove first line (header)
            while(filein.hasNext()){
               data=filein.next().split(",");
               String[] test_line = new String[data.length-1];

               for(int i=0,j=1;j<data.length;i++,j++)
                   test_line[i]=data[j];
               DataEntry entry = new DataEntry(atributes,test_line);
               test_data.add(entry);
            }
            filein.close();
        } catch (Exception e) {e.printStackTrace();}
        return test_data;
    }
}
