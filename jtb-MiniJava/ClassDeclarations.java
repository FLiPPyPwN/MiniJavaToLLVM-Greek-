import java.util.ArrayList;//Gia dynamically alocated Arrays
import java.util.Arrays;

//Auth h Class swzei tis classeis,plhrofories kai diadikasies panw se autes
public class ClassDeclarations {
    String ClassName;
    ClassDeclarations ExtClass;
    int VarDecSize, MethDecSize;
    ArrayList<String[]> VarDecArray,MethDecArray;

    boolean FoundError;  //Arxizei ws false kai an vroume error ginetai true!

    public ClassDeclarations(String ClassName) {
            this.ClassName = ClassName;
            this.ExtClass = null;
            VarDecSize = 0; MethDecSize = 0;

            this.VarDecArray = new ArrayList<String[]>();
            this.MethDecArray = new ArrayList<String[]>();

            FoundError = false;
    }
    public ClassDeclarations(String ClassName,ClassDeclarations ExtClass) {
            this.ClassName = ClassName;
            this.ExtClass = ExtClass;
            this.VarDecSize = ExtClass.VarDecSize;
            this.MethDecSize = ExtClass.MethDecSize;

            this.VarDecArray = new ArrayList<String[]>();
            this.MethDecArray = new ArrayList<String[]>();
            FoundError = false;
    }

    public void addVarDec(String type,String id) {
            for (int i = 0; i < this.VarDecArray.size(); i++) {
                    if (id.equals(this.VarDecArray.get(i)[1])) {
                            System.out.println("Class : "+this.ClassName+" - Error : "+id+" variable has already been defined!");
                            FoundError = true;
                            return;
                    }
            }


            if (type.equals("int")) VarDecSize += 4;
            else if (type.equals("boolean")) VarDecSize += 1;
            else if (type.equals("int[]")) VarDecSize += 8;
            else VarDecSize += 8;


            String tempStr[] = new String[2];
            tempStr[0] = type;
            tempStr[1] = id;
            this.VarDecArray.add(tempStr);
    }

    public void addMethDec(String type,String id,String[] args) {
            int num_of_args;
            if (args == null) num_of_args = 0;
            else num_of_args = args.length;

            boolean flag = CheckMethodDecl(type,id,args,num_of_args);

            if (flag) {
                    MethDecSize += 8;

                    String tempStr[] = new String[2 + num_of_args];
                    tempStr[0] = type;
                    tempStr[1] = id;
                    for (int i = 0; i < num_of_args; i++)
                            tempStr[i + 2] = args[i];

                    this.MethDecArray.add(tempStr);
            }
            else {
              String tempStr[] = new String[2 + num_of_args];
              tempStr[0] = type;
              tempStr[1] = id;
              for (int i = 0; i < num_of_args; i++)
                      tempStr[i + 2] = args[i];

              this.MethDecArray.add(tempStr);
            }
    }

    public boolean CheckMethodDecl(String type, String id, String[] args, int num_of_args) {
            boolean flag = true,final_flag = true;
            ClassDeclarations CDtemp = this;

            while(CDtemp != null) {

                    //Elegxoume me ola ta methods ths ExtClassclass
                    for (int i = 0; i < CDtemp.MethDecArray.size(); i++) {

                            flag = true;
                            if (CDtemp.MethDecArray.get(i)[1].equals(id)) {
                                    if (CDtemp == this) {
                                      System.out.println("Class : "+this.ClassName+" - Error : "+id+"(..) has already been defined in Class "+CDtemp.ClassName+"!");
                                      FoundError = true;
                                      flag = false;
                                      return false;
                                    }

                                    if (CDtemp.MethDecArray.get(i).length - 2 != num_of_args) {//stamatame an exoun diaforetiko arithmo apo args
                                            System.out.println("Class : "+this.ClassName+" - Error : "+id+"(..) has already been defined with different number of arguments in Class "+CDtemp.ClassName+"!");
                                            FoundError = true;
                                    }

                                    //Elegxos gia to type kai id (typos kai identifier ths sunarthshs)
                                    if (!CDtemp.MethDecArray.get(i)[0].equals(type)) {
                                            System.out.println("Class : "+this.ClassName+" - Error : "+id+"(..) has already been defined with different return type in Class "+CDtemp.ClassName+"!");
                                            FoundError = true;
                                    }
                                    else {
                                            if (num_of_args == 0) {
                                                    flag = false;
                                            }
                                            else flag = false;
                                    }

                                    //Elegxos gia ta args twn sunarthsewn
                                    for (int j = 2; j < CDtemp.MethDecArray.get(i).length; j++) {
                                            if (args.length < j - 2) break;
                                            if (!CDtemp.MethDecArray.get(i)[j].equals(args[j - 2])) {
                                                    System.out.println("Class : "+this.ClassName+" - Error : "+id+"("+Arrays.toString(args)+") has already been defined with different argument types in Class "+CDtemp.ClassName+"!");
                                                    flag = true;
                                                    FoundError = true;
                                            }
                                    }

                                    //An einai false tote vrhkame idia sunarthsh se declaration
                                    if (flag == false) {
                                            break;
                                    }
                                    else {
                                            final_flag = false;
                                    }
                            }
                    }

                    if (flag == false) break; //An einai false tote vrhkame idia sunarthsh se declaration
                    CDtemp = CDtemp.ExtClass;//elegxoume me thn poio panw class ektos an null p ftasame sto telos
            }

            if (flag == false) return flag;
            return final_flag;
    }

    //Parakatw methods gia euresh enos var h enos method gia sugkekrimenh class
    String VarExistsInClass(String var) {
            ClassDeclarations CDtemp = this;
            while (CDtemp != null) {
                    for (int i = 0; i < CDtemp.VarDecArray.size(); i++) {
                            if (var.equals(CDtemp.VarDecArray.get(i)[1])) {

                                    return CDtemp.VarDecArray.get(i)[0];//Epistrefoume to type ths metavlhths
                            }
                    }
            }
            return null;
    }

    String[] MethExistsInClass(String meth) {
            ClassDeclarations CDtemp = this;
            while (CDtemp != null) {
                    for (int i = 0; i < CDtemp.MethDecArray.size(); i++) {
                            if (meth.equals(CDtemp.MethDecArray.get(i)[1])) {

                                    String str = CDtemp.MethDecArray.get(i)[0];
                                    for (int j = 2; j < CDtemp.MethDecArray.get(i).length; j++) {
                                            str = str + " " + CDtemp.MethDecArray.get(i)[j];
                                    }

                                    String[] result = str.split(" ");
                                    return result;//Epistrefoume to type ths method kai twn arguments
                            }
                    }
                    CDtemp = CDtemp.ExtClass;
            }

            return null;
    }

    public String[][] ReturnMethodVtable() {
      String[][] VtableTemp = null;
      if (this.ExtClass != null) {
        VtableTemp = this.ExtClass.ReturnMethodVtable();
      }

      String[][] MyVtable = new String[this.MethDecSize / 8][];
      int counter = 0;
      if (VtableTemp != null) {
        for (int i = 0; i < VtableTemp.length; i++) {
          MyVtable[i] = new String[VtableTemp[i].length];

          for (int j = 0; j < VtableTemp[i].length; j++) {
            MyVtable[i][j] = VtableTemp[i][j];
          }
        }
        counter = VtableTemp.length;
      }

      for (int i = 0; i < this.MethDecArray.size(); i++) {
        //Elegxos an uparxei hdh h sunarthsh
        if (VtableTemp != null) {

          boolean flag = false;
          for (int j = 0; j < counter; j++) {
            if (MyVtable[j][2].equals(this.MethDecArray.get(i)[1])) {
              MyVtable[j][0] = this.ClassName;
              flag = true;
            }
          }
          if (flag) continue;
        }
        //---------------------------------

        MyVtable[counter] = new String[this.MethDecArray.get(i).length + 1];
        MyVtable[counter][0] = this.ClassName;

        for (int j = 0; j < this.MethDecArray.get(i).length; j++) {
          MyVtable[counter][j + 1] = this.MethDecArray.get(i)[j];
        }
        counter++;
      }

      return MyVtable;
    }

    public String[][] ReturnVariablesVtable() {
      String[][] VtableTemp = null;
      if (this.ExtClass != null) {
        VtableTemp = this.ExtClass.ReturnVariablesVtable();
      }

      String[][] MyVtable;
      if (VtableTemp != null)
        MyVtable = new String[VtableTemp.length + this.VarDecArray.size()][];
      else MyVtable = new String[this.VarDecArray.size()][];

      int counter = 0;
      if (VtableTemp != null) {
        for (int i = 0; i < VtableTemp.length; i++) {
          MyVtable[i] = new String[VtableTemp[i].length];

          for (int j = 0; j < VtableTemp[i].length; j++) {
            MyVtable[i][j] = VtableTemp[i][j];
          }
        }
        counter = VtableTemp.length;
      }

      for (int i = 0; i < this.VarDecArray.size(); i++) {
        MyVtable[counter] = new String[this.VarDecArray.get(i).length + 1];
        MyVtable[counter][0] = this.ClassName;

        for (int j = 0; j < this.VarDecArray.get(i).length; j++) {
          MyVtable[counter][j + 1] = this.VarDecArray.get(i)[j];
        }
        counter++;
      }

      return MyVtable;
    }

    boolean checkifclassParent(String ClassNameParent) {
      ClassDeclarations CDtemp = this;
      while (CDtemp != null) {
        if (CDtemp.ClassName.equals(ClassNameParent))
          return true;

        CDtemp = CDtemp.ExtClass;
      }
      return false;
    }
    //-------------------------------------------------------------------------------
    public void printClassInfo () {
            if (ExtClass == null)
                    System.out.println(this.ClassName);
            else
                    System.out.println(this.ClassName+" extends "+this.ExtClass.ClassName);
            System.out.println(this.VarDecSize+" - "+this.MethDecSize);
            for (int i = 0; i < this.VarDecArray.size(); i++) {
                    System.out.println(this.VarDecArray.get(i)[0]+"  "+this.VarDecArray.get(i)[1]);
            }
            for (int i = 0; i < this.MethDecArray.size(); i++) {
                    for (int j = 0; j < this.MethDecArray.get(i).length - 1; j++) {
                            System.out.print(this.MethDecArray.get(i)[j]+"  ");
                    }
                    System.out.println(this.MethDecArray.get(i)[this.MethDecArray.get(i).length - 1]);
            }
    }

    public void printProjectClassInfo () {
            System.out.println("-----------Class "+this.ClassName+"-----------");

            int pointer;

            System.out.println("--Variables---");
            if (this.ExtClass == null) pointer = 0;
            else pointer = this.ExtClass.VarDecSize;
            for (int i = 0; i < this.VarDecArray.size(); i++) {
                    System.out.println(this.ClassName+"."+this.VarDecArray.get(i)[1]+" : "+pointer);
                    if (this.VarDecArray.get(i)[0].equals("int")) pointer += 4;
                    else if (this.VarDecArray.get(i)[0].equals("boolean")) pointer += 1;
                    else if (this.VarDecArray.get(i)[0].equals("int[]")) pointer += 8;
                    else pointer += 8;
            }

            System.out.println("---Methods---");
            if (this.ExtClass == null) pointer = 0;
            else pointer = this.ExtClass.MethDecSize;
            for (int i = 0; i < this.MethDecArray.size(); i++) {

                    //Elegxoume an h methodos uparxei se klhrwnomoumenes Classeis
                    ClassDeclarations CDtemp = this.ExtClass;
                    boolean flag = false;
                    while (CDtemp != null) {
                      if (CDtemp.MethExistsInClass(this.MethDecArray.get(i)[1]) != null) {
                        flag = true;
                        break;
                      }
                      CDtemp = CDtemp.ExtClass;
                    }
                    if (flag) continue;


                    System.out.println(this.ClassName+"."+this.MethDecArray.get(i)[1]+" : "+pointer);
                    pointer += 8;
            }
    }
}
