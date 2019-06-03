import syntaxtree.*;
import visitor.*;
import java.io.*;

public class Main {
    public static void main (String [] args){
    if(args.length < 1){
        System.err.println("Usage: java Main <inputFile> <inputFile2> ...");
        System.exit(1);
    }
    FileInputStream fis = null;
    try{
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        for (int i = 0; i < args.length; i++) {
          System.out.println("~~~~~ Filename : "+args[i]+" ~~~~~\n");
          fis = new FileInputStream(args[i]);
          MiniJavaParser parser = new MiniJavaParser(fis);
          Goal root = parser.Goal();
          System.err.println("Program parsed successfully.");
          ClassesInfoVisitor CIV = new ClassesInfoVisitor();

          root.accept(CIV, null);

          TypeCheckerVisitor TCV = new TypeCheckerVisitor(CIV.CDs);
          root.accept(TCV, null);

          System.out.println("--------------------------------------------------------------");
          if (TCV.FoundError)
            System.out.println("Program has ERRORS so we dont compile to llvm!");
          else {

            // parakatw sxoliasa to print twn offsets!
            // CIV.printProjectClassInfo();

            String fileName = new File(args[i]).getName();

            //Ta apotelesmata ta vazw ston fakelo results/  !!!
            File file = new File("./results/"+fileName.substring(0, fileName.length() - 5)+".ll");

            try {
              if (!file.createNewFile()) {
                file.delete();
                file.createNewFile();
              }
            } catch(Exception e) {
              e.printStackTrace();
            }

            try {
              FileWriter fw = new FileWriter(file);

              LLVMGenerationVisitor llvmGV = new LLVMGenerationVisitor(CIV.CDs,fw);
              root.accept(llvmGV, null);
              fw.close();
              System.out.println("Program has been successfully compiled to llvm.");
            } catch(Exception e) {
              e.printStackTrace();
            }
          }

          System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
    }
    catch(ParseException ex){
        System.out.println(ex.getMessage());
    }
    catch(FileNotFoundException ex){
        System.err.println(ex.getMessage());
    }
    finally{
        try{
        if(fis != null) fis.close();
        }
        catch(IOException ex){
        System.err.println(ex.getMessage());
        }
    }
    }
}
