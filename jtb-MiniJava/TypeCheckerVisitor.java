import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.*;
import java.util.ArrayList;//Gia dynamically alocated Arrays
import java.util.Arrays;

public class TypeCheckerVisitor extends GJDepthFirst<String,String> {
    ArrayList<ClassDeclarations> CDs;  //Auto afora tis plhrofories twn CLASS

    //Afora ta VariableDeclarations otan ta xreiazomaste
    //Dhladh allazei gia kathe 'execution'
    ArrayList<String[]> SymbolTable;

    boolean FoundError;

    int num_of_vars_in_class;//Gia xrhsh sto AddVarToSymbolTable -- plhthos vars ths classhs
    boolean AddVarToSymbolTable(String type, String id) {
            boolean flag = false;
            if (returnTypeVar(id) != null) {
                flag = true;
                for (int i = 0; i < this.CDs.size(); i++) {
                    if (this.CDs.get(i).ClassName.equals(id)) {
                          flag = false;
                    }
                }
                for (int i = 1; i < this.num_of_vars_in_class + 1; i++) {//+1 gia to this
                    if (i > this.SymbolTable.size() - 1) break;

                    if (id.equals(this.SymbolTable.get(i)[1])) {
                          this.SymbolTable.get(i)[0] = type;
                          flag = false;
                    }
                }
            }
            if (flag) return false;

            String tempStr[] = new String[2];
            tempStr[0] = type;
            tempStr[1] = id;
            this.SymbolTable.add(tempStr);
            return true;
    }

    String returnTypeVar(String VarName) {
            if (VarName == null) return null;
            if (VarName.equals("boolean") || VarName.equals("int") || VarName.equals("int[]"))
                    return VarName;

            for (int i = 0; i < this.SymbolTable.size(); i++) {
                    if (VarName.equals(this.SymbolTable.get(i)[1])) {
                            return this.SymbolTable.get(i)[0];
                    }
            }
            for (int i = 0; i < this.CDs.size(); i++) {
                    if (this.CDs.get(i).ClassName.equals(VarName)) {
                            return VarName;
                    }
            }

            return null;
    }

    String[] returnTypeClassMeth(String ClassName,String meth) {
            for (int i = 0; i < this.CDs.size(); i++) {
                    if (this.CDs.get(i).ClassName.equals(ClassName)) {
                            return this.CDs.get(i).MethExistsInClass(meth);
                    }
            }
            return null;
    }

    public TypeCheckerVisitor(ArrayList<ClassDeclarations> CDs) {
            this.CDs = CDs;
    }

    public String visit(Goal n, String argu) {
            System.out.println("~~~ Checking Executions of Main and Classes Methods ~~~");
            n.f0.accept(this, argu);
            n.f1.accept(this, argu);
            return null;
    }

    public String visit(MainClass n, String argu) {
            this.SymbolTable = new ArrayList<String[]>();
            System.out.println("~~~ Checking Executions of MAIN ~~~");
            n.f14.accept(this,argu);
            n.f15.accept(this,argu);
            this.SymbolTable.clear();
            return null;
    }

    public String visit(ClassDeclaration n, String argu) {
            this.SymbolTable = new ArrayList<String[]>();
            this.num_of_vars_in_class = 0;
            String id = n.f1.accept(this, argu);
            System.out.println("~~~ Checking Executions of Class "+id+" ~~~");

            for (int i = 0; i < CDs.size(); i++) {
              if (CDs.get(i).ClassName.equals(id))
                this.num_of_vars_in_class = CDs.get(i).VarDecArray.size();
            }

            String[] str = new String[2];
            str[0] = id;
            str[1] = "this";
            this.SymbolTable.add(str);//Vazw to this me classType

            n.f3.accept(this, argu);
            n.f4.accept(this, argu);
            this.SymbolTable.clear();
            return null;
    }

    public String visit(ClassExtendsDeclaration n, String argu) {
            this.SymbolTable = new ArrayList<String[]>();
            this.num_of_vars_in_class = 0;
            String id = n.f1.accept(this, argu);
            System.out.println("~~~ Checking Executions of Class "+id+" ~~~");

            //Parakatw vazw ta declarations kai apo tis upoklaseis
            int flag = 0;
            for (int i = 0; i < CDs.size(); i++) {
              if (CDs.get(i).ClassName.equals(id)) flag = i;
            }
            ClassDeclarations CDtemp = CDs.get(flag);
            while(CDtemp != null) {
              SymbolTable.addAll(CDtemp.VarDecArray);
              this.num_of_vars_in_class += CDtemp.VarDecArray.size();
              CDtemp = CDtemp.ExtClass;
            }

            String[] str = new String[2];
            str[0] = id;
            str[1] = "this";
            this.SymbolTable.add(str);//Vazw to this me classType

            n.f6.accept(this, argu);
            this.SymbolTable.clear();
            return null;
    }

    public String visit(VarDeclaration n, String argu) {
            String type = n.f0.accept(this, argu);
            String id = n.f1.accept(this, argu);

            if (!AddVarToSymbolTable(type,id)) {
                    System.out.println("Error - "+id+" has already been defined!");
                    FoundError = true;
            }
            return null;
    }

    public String visit(MethodDeclaration n, String argu) {

            ArrayList<String[]> STTemp = new ArrayList<String[]>();//Krataw tempArrayList
            for (int i = 0; i < this.SymbolTable.size(); i++) {
              String[] str = new String[2];
              str[0] = this.SymbolTable.get(i)[0];
              str[1] = this.SymbolTable.get(i)[1];
              STTemp.add(str);
            }

            String MethodType = n.f1.accept(this, argu);
            String MethodName = n.f2.accept(this, argu);

            n.f4.accept(this, argu);

            n.f7.accept(this, argu);
            n.f8.accept(this, argu);

            String ReturnType = returnTypeVar(n.f10.accept(this, argu));

            if (!MethodType.equals(ReturnType)) {
                    System.out.println("Error - Method "+MethodName+" has different define and return Types -> "+MethodType+" - "+ReturnType+"!");
                    FoundError = true;
            }

            this.SymbolTable = STTemp;

            return null;
    }

    public String visit(FormalParameter n, String argu) {
            String type = n.f0.accept(this, argu);
            String id = n.f1.accept(this, argu);

            boolean flag = false;//flag = true an uparxei class onoma type
            for (int i = 0; i < CDs.size(); i++) {
                    if (type.equals(this.CDs.get(i).ClassName))
                            flag = true;
            }

            if (flag || type.equals("boolean") || type.equals("int") || type.equals("int[]")) {
                    String tempStr[] = new String[2];
                    tempStr[0] = type;
                    tempStr[1] = id;
                    this.SymbolTable.add(tempStr);
            }
            else {
                    System.out.println("Error - There is not type "+type+" for parameter "+id+"!");
            }
            return null;
    }

    //-----------------------------------------------------------------------------

    public String visit(Statement n, String argu) {
            return n.f0.accept(this, argu);
    }

    public String visit(Block n, String argu) {
            return n.f1.accept(this, argu);
    }

    public String visit(AssignmentStatement n, String argu) {
            String id = n.f0.accept(this, argu);
            String idtype = returnTypeVar(id);
            String id2 = n.f2.accept(this, argu);
            String id2type = returnTypeVar(id2);

            if (idtype == null) {
                    System.out.println("Error - "+id+" has not been defined!");
                    FoundError = true;
            }
            else if (id2type == null) {
                    System.out.println("Error - in ("+id+" = "+id2+") "+id2+" has not been defined!");
                    FoundError = true;
            }
            else if (!idtype.equals(id2type)) {
                    if (idtype.equals("int") || idtype.equals("boolean") || idtype.equals("int[]") ||
                        id2type.equals("int") || id2type.equals("boolean") || id2type.equals("int[]")) {
                          System.out.println("Error - "+id+" is "+idtype+" but you assign "+id2type+"!");
                          FoundError = true;
                    }
                    else {//checkifclassParent
                      for (int j = 0; j < CDs.size(); j++) {
                        if (CDs.get(j).ClassName.equals(id2type)) {
                          if (!CDs.get(j).checkifclassParent(idtype)) {
                              System.out.println("Error - "+id+" is "+idtype+" but you assign "+id2type+"!");
                              FoundError = true;
                          }
                        }
                      }
                    }
            }
            return null;
    }

    public String visit(ArrayAssignmentStatement n, String argu) {
            String id = n.f0.accept(this, argu);
            String idtype = returnTypeVar(id);

            if (idtype == null) {
                    System.out.println("Error - "+id+" has not been defined!");
                    FoundError = true;
            }
            else if (!idtype.equals("int[]")) {
                    System.out.println("Error - "+id+" should have been INTEGER ARRAY and not "+idtype+"!");
                    FoundError = true;
            }

            String type = returnTypeVar(n.f2.accept(this, argu));
            if (type == null || !type.equals("int")) {
                    System.out.println("Error - "+id+"[?], should have been INTEGER and not "+type+"!");
                    FoundError = true;
            }

            type = returnTypeVar(n.f5.accept(this, argu));
            if (type == null || !type.equals("int")) {
                    System.out.println("Error - "+id+"[..] = ?, should have been INTEGER and not "+type+"!");
                    FoundError = true;
            }
            return null;
    }

    public String visit(IfStatement n, String argu) {
            String type = returnTypeVar(n.f2.accept(this, argu));
            if (type == null || !type.equals("boolean")) {
                    System.out.println("Error - if( ? ) should have been boolean and not "+type+"!");
                    FoundError = true;
            }
            n.f4.accept(this, argu);
            n.f6.accept(this, argu);
            return null;
    }

    public String visit(WhileStatement n, String argu) {
            String type = returnTypeVar(n.f2.accept(this, argu));
            if (type == null || !type.equals("boolean")) {
                    System.out.println("Error - while( ? ) should have been boolean and not "+type+"!");
                    FoundError = true;
            }
            n.f4.accept(this, argu);
            return null;
    }

    public String visit(PrintStatement n, String argu) {
            String type = returnTypeVar(n.f2.accept(this, argu));
            if (!type.equals("boolean") && !type.equals("int")) {
              System.out.println("Error - System.out.println(?) cant print type "+type+"!");
              FoundError = true;
            }
            return null;
    }

    //------------------------------------------------------------
    //------------------------------------------------------------

    public String visit(Expression n, String argu) {
            return n.f0.accept(this, argu);
    }

    public String visit(AndExpression n, String argu) {
            String temp1 = n.f0.accept(this, argu);
            n.f1.accept(this, argu);
            String temp2 = n.f2.accept(this, argu);
            String type1 = returnTypeVar(temp1);
            String type2 = returnTypeVar(temp2);

            if (type1 == null || !type1.equals("boolean") || type2 == null || !type2.equals("boolean")) {
                    System.out.println("Error - "+type1+" && "+type2+" cant be calculated!");
                    FoundError = true;
            }
            return "boolean";
    }

    public String visit(CompareExpression n, String argu) {
            String temp1 = n.f0.accept(this, argu);
            n.f1.accept(this, argu);
            String temp2 = n.f2.accept(this, argu);
            String type1 = returnTypeVar(temp1);
            String type2 = returnTypeVar(temp2);

            if (type1 == null || !type1.equals("int") || type2 == null || !type2.equals("int")) {
                    System.out.println("Error - "+type1+" < "+type2+" cant be calculated!");
                    FoundError = true;
            }
            return "boolean";
    }

    public String visit(PlusExpression n, String argu) {
            String temp1 = n.f0.accept(this, argu);
            n.f1.accept(this, argu);
            String temp2 = n.f2.accept(this, argu);
            String type1 = returnTypeVar(temp1);
            String type2 = returnTypeVar(temp2);

            if (type1 == null || !type1.equals("int") || type2 == null || !type2.equals("int")) {
                    System.out.println("Error - "+type1+" + "+type2+" cant be calculated!");
                    FoundError = true;
            }
            return "int";
    }

    public String visit(MinusExpression n, String argu) {
            String temp1 = n.f0.accept(this, argu);
            n.f1.accept(this, argu);
            String temp2 = n.f2.accept(this, argu);
            String type1 = returnTypeVar(temp1);
            String type2 = returnTypeVar(temp2);

            if (type1 == null || !type1.equals("int") || type2 == null || !type2.equals("int")) {
                    System.out.println("Error - "+type1+" - "+type2+" cant be calculated!");
                    FoundError = true;
            }
            return "int";
    }

    public String visit(TimesExpression n, String argu) {
            String temp1 = n.f0.accept(this, argu);
            n.f1.accept(this, argu);
            String temp2 = n.f2.accept(this, argu);
            String type1 = returnTypeVar(temp1);
            String type2 = returnTypeVar(temp2);

            if (type1 == null || !type1.equals("int") || type2 == null || !type2.equals("int")) {
                    System.out.println("Error - "+type1+" * "+type2+" cant be calculated!");
                    FoundError = true;
            }
            return "int";
    }

    public String visit(ArrayLookup n, String argu) {
            String temp = n.f0.accept(this, argu);
            String type = returnTypeVar(temp);
            if (type == null || !type.equals("int[]")) {
                    System.out.println("Error - "+temp+" is not int[] and cant use it as Array!");
                    FoundError = true;
            }

            temp = n.f2.accept(this, argu);
            type = returnTypeVar(temp);
            if (type == null || !type.equals("int")) {
                    System.out.println("Error - "+temp+"[?], should have been INTEGER and not "+type+"!");
                    FoundError = true;
            }
            return "int";
    }

    public String visit(ArrayLength n, String argu) {
            String temp = n.f0.accept(this, argu);
            String type = returnTypeVar(temp);
            if (type == null || !type.equals("int[]")) {
                    System.out.println("Error - "+temp+" is not int[] and cant use it as Array!");
                    FoundError = true;
            }
            return "int";
    }

    public String visit(MessageSend n, String argu) {
            String temp = n.f0.accept(this, argu);
            String type = returnTypeVar(temp);

            String meth = n.f2.accept(this, argu);

            String[] methtypes = returnTypeClassMeth(type,meth);

            String giventypes = n.f4.accept(this, argu);
            if (methtypes == null) {
              System.out.println("Error - Couldnt find Method "+meth+" in ClassType "+type+" for Variable "+temp+"!");
              FoundError = true;
              return null;
            }
            else {
              if (giventypes == null && methtypes.length == 1) return methtypes[0];

              String[] giventypesArray;
              giventypesArray = giventypes.split(" ");

              if (giventypesArray.length != methtypes.length - 1) {
                System.out.println("Error - Method "+meth+" in ClassType "+type+" is defined with different number of arguments!");
                FoundError = true;
                return null;
              }

              for (int i = 0; i < giventypesArray.length; i++) {
                if (!giventypesArray[i].equals(methtypes[i + 1])) {

                  int flag = -1;
                  for (int j = 0; j < CDs.size(); j++) {
                    if (CDs.get(j).ClassName.equals(giventypesArray[i])) flag = j;
                  }

                  if (flag == -1) {
                    System.out.println("Error - Method "+meth+" in ClassType "+type+" does not have argument type "+giventypesArray[i]+",which is undefined as type!");
                    FoundError = true;
                    return null;
                  }

                  boolean res = CDs.get(flag).checkifclassParent(methtypes[i + 1]);
                  if (res == false) {
                    System.out.println("Error - type "+giventypesArray[i]+" does not support definition of Method "+meth+",which need type "+methtypes[i+1]+"!");
                    FoundError = true;
                    return null;
                  }
                }
              }
            }

            return methtypes[0];
    }

    String temp;//Xrhsh gia na mazepsoume ta paramaters tis sunarthseis
    public String visit(ExpressionList n, String argu) {
            temp = "";
            String temp1 = n.f0.accept(this, argu);
            if (temp1 == null) return null;
            this.temp = returnTypeVar(n.f0.accept(this, argu));

            n.f1.accept(this, argu);
            return this.temp;
    }


    public String visit(ExpressionTail n, String argu) {
            return n.f0.accept(this, argu);
    }

    public String visit(ExpressionTerm n, String argu) {
            temp = temp + " " + returnTypeVar(n.f1.accept(this, argu));
            return n.f1.accept(this, argu);
    }

    public String visit(Clause n, String argu) {
            return n.f0.accept(this, argu);
    }

    //---------------------------------------------------------------------
    //---------------------------------------------------------------------

    public String visit(Type n, String argu) {
            return n.f0.accept(this, argu);
    }

    public String visit(ArrayType n, String argu) {
            return n.f0.toString() + n.f1.toString() + n.f2.toString();
    }

    public String visit(IntegerType n, String argu) {
            return n.f0.toString();
    }

    public String visit(BooleanType n, String argu) {
            return n.f0.toString();
    }

    public String visit(IntegerLiteral n, String argu) {
            return "int";
    }

    public String visit(TrueLiteral n, String argu) {
            return "boolean";
    }

    public String visit(FalseLiteral n, String argu) {
            return "boolean";
    }

    public String visit(Identifier n, String argu) {
            return n.f0.toString();
    }

    public String visit(ThisExpression n, String argu) {
            return n.f0.toString();
    }

    public String visit(ArrayAllocationExpression n, String argu) {
            String type = returnTypeVar(n.f3.accept(this, argu));

            if (type == null || !type.equals("int")) {
                    System.out.println("Error - .. new int[?], should have been INTEGER and not "+type+"!");
                    FoundError = true;
            }
            return "int[]";
    }

    public String visit(AllocationExpression n, String argu) {
            return n.f1.accept(this,argu);
    }

    public String visit(NotExpression n, String argu) {
            return n.f1.accept(this, argu);
    }

    public String visit(BracketExpression n, String argu) {
            return n.f1.accept(this, argu);
    }
}
