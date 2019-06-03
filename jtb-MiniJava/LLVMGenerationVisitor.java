import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.*;
import java.util.ArrayList;//Gia dynamically alocated Arrays
import java.util.Arrays;
import java.io.*;

public class LLVMGenerationVisitor extends GJDepthFirst<String,String> {
    ArrayList<ClassDeclarations> CDs;      //Auto afora tis plhrofories twn CLASS
    String[][][] ClassesMethodVtables;

    String[][][] ClassesVariablesVtables;

    FileWriter fw;

    ArrayList<String[]> SymbolTable;    //Xrhsh se kathe execution methodos

    int counter;    //Gia xrhsh stis metavlhtes pou orizoume sto llvm

    public void WriteInFile(String str) {
            try {
                    this.fw.write(str);
            } catch(Exception e) {
                    e.printStackTrace();
            }
    }

    public String typetoi(String type) {
            if (type.equals("boolean")) return "i1";
            if (type.equals("int")) return "i32";
            if (type.equals("int[]")) return "i32*";
            return "i8*";
    }

    public int ReturnIndexOfClass(String ClassName) {
            for (int i = 1; i < this.CDs.size(); i++) {
                    if (this.CDs.get(i).ClassName.equals(ClassName)) {
                            return i - 1;
                    }
            }
            return -1;
    }

    public int ReturnIndexOfMethodofClass(int indexofClass,String methodName) {
            for (int i = 0; i < ClassesMethodVtables[indexofClass].length; i++) {
                    if (ClassesMethodVtables[indexofClass][i][2].equals(methodName))
                            return i;
            }
            return -1;
    }

    public int ReturnIndexOfVariableofClass(int indexofClass,String variableName) {
            for (int i = ClassesVariablesVtables[indexofClass].length - 1; i >= 0; i--) {
                    if (ClassesVariablesVtables[indexofClass][i][2].equals(variableName))
                            return i;
            }
            return -1;
    }

    public int ReturnOffsetOfVariableofClass(int indexofClass,String variableName) {
            int indexofVar = ReturnIndexOfVariableofClass(indexofClass,variableName);
            if (indexofVar == -1) return -1;

            int offset = 0;
            for (int i = 0; i < indexofVar; i++) {
                    String type = ClassesVariablesVtables[indexofClass][i][1];

                    if (type.equals("boolean")) offset += 1;
                    else if (type.equals("int")) offset += 4;
                    else offset += 8;
            }

            return offset;
    }

    public String[] ReturnInfoOfVarInClass(String ClassName, String varName) {
            if (ClassName == null) return null;

            int ClassIndex = ReturnIndexOfClass(ClassName);
            if (ClassIndex == -1) return null;
            int VarIndex = ReturnIndexOfVariableofClass(ClassIndex,varName);
            if (VarIndex == -1) return null;
            int VarOffset = ReturnOffsetOfVariableofClass(ClassIndex,varName);
            if (VarOffset == -1) return null;

            String VarType = ClassesVariablesVtables[ClassIndex][VarIndex][1];

            String[] ReturnString = new String[2];
            ReturnString[0] = VarType;
            ReturnString[1] = Integer.toString(VarOffset + 8);

            return ReturnString;
    }

    public String ReturnTypeOfId(String identifier) {

            for(int i = 0; i < this.SymbolTable.size(); i++) {
                    if (this.SymbolTable.get(i)[1].equals(identifier)) {
                            return this.SymbolTable.get(i)[0];
                    }
            }
            return null;
    }



    public void WriteVtables() {
            //Declarations of VTables in .ll file
            String str = "@."+this.CDs.get(0).ClassName+"_vtable = global [0 x i8*] []\n";
            WriteInFile(str);

            ClassesMethodVtables = new String[this.CDs.size() - 1][][];
            ClassesVariablesVtables = new String[this.CDs.size() - 1][][];
            for (int i = 1; i < this.CDs.size(); i++) {

                    int num_of_methods = this.CDs.get(i).MethDecSize / 8;
                    str = "@."+this.CDs.get(i).ClassName+"_vtable = global ["+num_of_methods+" x i8*] [";
                    WriteInFile(str);

                    String[][] ClassVtable = this.CDs.get(i).ReturnMethodVtable();
                    ClassesMethodVtables[i - 1] = ClassVtable;

                    for (int j = 0; j < ClassVtable.length; j++) {
                            WriteInFile("i8* bitcast ("+typetoi(ClassVtable[j][1])+" (i8*");
                            if (ClassVtable[j].length > 3) WriteInFile(",");

                            for (int k = 3; k < ClassVtable[j].length; k++) {
                                    WriteInFile(typetoi(ClassVtable[j][k]));
                                    if (k < ClassVtable[j].length - 1) WriteInFile(",");
                            }

                            WriteInFile(")* @"+this.CDs.get(i).ClassName+"."+ClassVtable[j][2]+" to i8*)");

                            if (j < ClassVtable.length - 1) WriteInFile(", ");
                    }

                    //Vtable gia ta Variables
                    ClassVtable = this.CDs.get(i).ReturnVariablesVtable();
                    ClassesVariablesVtables[i - 1] = ClassVtable;

                    WriteInFile("]\n");
            }
            WriteInFile("\n");
    }

    //----------------------------------------------------
    //EKTELESH - EURESH expression KAI identifier
    public String ExecuteExpr(String ClassName,String expr) {
            String[] str;
            String flag = ReturnTypeOfId(expr);
            if (flag != null) {
                    if (expr.equals("this")) {
                      expr = "%"+expr;
                    }
                    else {
                      WriteInFile("%_"+counter+" = load "+typetoi(flag)+", "+typetoi(flag)+"* %"+expr+"\n");
                      expr = "%_"+Integer.toString(counter);
                      counter++;
                    }
            }
            else {
                    str = ReturnInfoOfVarInClass(ClassName,expr);

                    if (str != null) {
                            WriteInFile("%_"+counter+" = getelementptr i8, i8* %this, i32 "+str[1]+"\n");
                            counter++;
                            WriteInFile("%_"+counter+" = bitcast i8* %_"+(counter-1)+" to "+typetoi(str[0])+"*\n");
                            counter++;
                            WriteInFile("%_"+counter+" = load "+typetoi(str[0])+", "+typetoi(str[0])+"* %_"+(counter-1)+"\n");
                            expr = "%_"+Integer.toString(counter);
                            counter++;
                    }
            }

            return expr;
    }

    public String ExecuteExprForArray(String ClassName,String expr) {
            String[] str;
            String flag = ReturnTypeOfId(expr);
            if (flag != null) {
                    expr = "%"+expr;
            }
            else {
                    str = ReturnInfoOfVarInClass(ClassName,expr);

                    if (str != null) {
                            WriteInFile("%_"+counter+" = getelementptr i8, i8* %this, i32 "+str[1]+"\n");
                            counter++;
                            WriteInFile("%_"+counter+" = bitcast i8* %_"+(counter-1)+" to "+typetoi(str[0])+"*\n");
                            counter++;
                            expr = "%_"+(counter-1);
                    }
            }
            return expr;
    }



    public String ExecuteID(String ClassName,String id,String expr) {
            String[] str;
            str = ReturnInfoOfVarInClass(ClassName,id);

            if (str != null) {
                    WriteInFile("%_"+counter+" = getelementptr i8, i8* %this, i32 "+str[1]+"\n");
                    counter++;
                    WriteInFile("%_"+counter+" = bitcast i8* %_"+(counter-1)+" to "+typetoi(str[0])+"*\n");
                    counter++;
                    WriteInFile("store "+typetoi(str[0])+" "+expr+", "+typetoi(str[0])+"* %_"+(counter-1)+"\n");
                    return "%_"+(counter-1);
            }
            else {
                    String TypeStrini = typetoi(ReturnTypeOfId(id));
                    WriteInFile("store "+TypeStrini+" "+expr+", "+TypeStrini+"* %"+id+"\n");
                    return "%"+id;
            }
    }

    public LLVMGenerationVisitor(ArrayList<ClassDeclarations> CDs, FileWriter fw) {
            this.CDs = CDs;
            this.fw = fw;

            WriteVtables();

            //-----------------------------------------------------------------------
            String BasicString = "declare i8* @calloc(i32, i32)\n"+
                                 "declare i32 @printf(i8*, ...)\n"+
                                 "declare void @exit(i32)\n"+
                                 "\n"+
                                 "@_cint = constant [4 x i8] c"+"\"%d\\0a\\00\""+"\n"+
                                 "@_cOOB = constant [15 x i8] c"+"\"Out of bounds\\0a\\00\""+"\n"+
                                 "define void @print_int(i32 %i) {\n"+
                                 "    %_str = bitcast [4 x i8]* @_cint to i8*\n"+
                                 "    call i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n"+
                                 "    ret void\n"+
                                 "}\n"+
                                 "\n"+
                                 "define void @throw_oob() {\n"+
                                 "    %_str = bitcast [15 x i8]* @_cOOB to i8*\n"+
                                 "    call i32 (i8*, ...) @printf(i8* %_str)\n"+
                                 "    call void @exit(i32 1)\n"+
                                 "    ret void\n"+
                                 "}\n\n";
            WriteInFile(BasicString);
    }

    public String visit(MainClass n, String argu) {
            this.SymbolTable = new ArrayList<String[]>();
            this.counter = 0;
            WriteInFile("define i32 @main() {\n");
            n.f14.accept(this, argu);
            n.f15.accept(this, argu);
            WriteInFile("\nret i32 0\n}\n");
            this.SymbolTable.clear();
            return null;
    }

    public String visit(ClassDeclaration n, String argu) {
            argu = n.f1.accept(this, argu);

            n.f4.accept(this, argu);
            return null;
    }

    public String visit(ClassExtendsDeclaration n, String argu) {
            argu = n.f1.accept(this, argu);

            n.f6.accept(this, argu);
            return null;
    }

    public String visit(VarDeclaration n, String argu) {
            String type = n.f0.accept(this, argu);
            String id = n.f1.accept(this, argu);
            WriteInFile("%"+id+" = alloca "+typetoi(type)+"\n");

            String[] str = new String[2];
            str[0]=type;
            str[1]=id;
            this.SymbolTable.add(str);

            return null;
    }

    public String visit(MethodDeclaration n, String argu) {
            WriteInFile("\n");
            this.SymbolTable = new ArrayList<String[]>();
            this.counter = 0;
            String[] str = new String[2];
            str[0]=argu;
            str[1]="this";
            this.SymbolTable.add(str);

            int indexofClass = ReturnIndexOfClass(argu);
            String ReturnType = n.f1.accept(this, argu);

            String methodName = n.f2.accept(this, argu);

            String[] methtypes = this.CDs.get(indexofClass + 1).MethExistsInClass(methodName);
            WriteInFile("define "+typetoi(methtypes[0])+" @"+argu+"."+methodName+"(i8* %this");
            if (methtypes.length != 1) WriteInFile(",");
            n.f4.accept(this, argu);
            WriteInFile(") {\n");

            for (int i = 1; i < this.SymbolTable.size(); i++) {
                    String typeini = typetoi(this.SymbolTable.get(i)[0]);
                    WriteInFile("%"+this.SymbolTable.get(i)[1]+" = alloca "+typeini+"\n");
                    WriteInFile("store "+typeini+" %."+this.SymbolTable.get(i)[1]+", "+typeini+"* %"+this.SymbolTable.get(i)[1]+"\n");
            }

            n.f7.accept(this, argu);
            n.f8.accept(this, argu);

            String expr = n.f10.accept(this, argu);
            expr = ExecuteExpr(argu,expr);
            WriteInFile("\nret "+typetoi(ReturnType)+" "+expr+"\n");

            WriteInFile("}\n");

            this.SymbolTable.clear();
            return null;
    }

    public String visit(FormalParameter n, String argu) {
            String type = n.f0.accept(this, argu);
            WriteInFile(" "+typetoi(type));
            String id = n.f1.accept(this, argu);
            WriteInFile(" %."+id);

            String[] str = new String[2];
            str[0]=type;
            str[1]=id;
            this.SymbolTable.add(str);

            return null;
    }

    public String visit(FormalParameterTerm n, String argu) {
            WriteInFile(",");
            n.f1.accept(this,argu);
            return null;
    }

    public String visit(AssignmentStatement n, String argu) {
            WriteInFile("\n");
            String expr = n.f2.accept(this, argu);
            expr = ExecuteExpr(argu,expr);

            String id = n.f0.accept(this, argu);
            ExecuteID(argu,id,expr);

            return null;
    }

    public String visit(ArrayAssignmentStatement n, String argu) {
            WriteInFile("\n");

            String id = n.f0.accept(this, argu);
            id = ExecuteExprForArray(argu,id);

            WriteInFile("%_"+counter+" = load i32*, i32** "+id+"\n");
            String array_pointer = "%_"+counter;
            counter++;

            String Indexexpr = n.f2.accept(this, argu);
            Indexexpr = ExecuteExpr(argu,Indexexpr);

            WriteInFile("%_"+counter+" = load i32, i32* "+array_pointer+"\n");
            counter++;
            WriteInFile("%_"+counter+" = icmp ult i32 "+Indexexpr+", %_"+(counter-1)+"\n");

            int oob1 = counter+1, oob2 = counter+2, oob3 = counter+3;
            WriteInFile("br i1 %_"+counter+", label %oob"+oob1+", label %oob"+oob2+"\n");
            counter = counter + 4;

            WriteInFile("oob"+oob1+":\n");
            WriteInFile("%_"+counter+" = add i32 "+Indexexpr+", 1\n");
            counter++;

            WriteInFile("%_"+counter+" = getelementptr i32, i32* "+array_pointer+", i32 %_"+(counter-1)+"\n");
            int StoreToPtr = counter;
            counter++;

            String expr = n.f5.accept(this, argu);

            expr = ExecuteExpr(argu,expr);

            WriteInFile("store i32 "+expr+", i32* %_"+StoreToPtr+"\n");
            WriteInFile("br label %oob"+oob3+"\n");


            WriteInFile("oob"+oob2+":\n");
            WriteInFile("call void @throw_oob()\nbr label %oob"+oob3+"\n");
            WriteInFile("oob"+oob3+":\n");

            return null;
    }

    public String visit(AndExpression n, String argu) {
            String clause1 = n.f0.accept(this, argu);
            clause1 = ExecuteExpr(argu,clause1);

            int andclause1 = counter, andclause2 = counter + 1, andclause3 = counter + 2, andclause4 = counter + 3;
            counter = counter + 4;
            WriteInFile("br label %andclause"+andclause1+"\n");
            WriteInFile("andclause"+andclause1+":\n");

            WriteInFile("br i1 "+clause1+", label %andclause"+andclause2+", label %andclause"+andclause4+"\n");

            WriteInFile("andclause"+andclause2+":\n");

            String clause2 = n.f2.accept(this, argu);
            clause2 = ExecuteExpr(argu,clause2);

            WriteInFile("br label %andclause"+andclause3+"\n");

            WriteInFile("andclause"+andclause3+":\n");
            WriteInFile("br label %andclause"+andclause4+"\n");

            WriteInFile("andclause"+andclause4+":\n");
            WriteInFile("%_"+counter+" = phi i1 [ 0 , %andclause"+andclause1+" ], [ %_"+(counter-1)+" , %andclause"+andclause3+" ]\n");
            counter++;

            return "%_"+(counter-1);
    }

    public String visit(CompareExpression n, String argu) {
            String expr1 = n.f0.accept(this, argu);
            expr1 = ExecuteExpr(argu,expr1);

            String expr2 = n.f2.accept(this, argu);
            expr2 = ExecuteExpr(argu,expr2);

            WriteInFile("%_"+counter+" = icmp slt i32 "+expr1+", "+expr2+"\n");
            counter++;

            return "%_"+(counter-1);
    }

    public String visit(PlusExpression n, String argu) {
            String pe1 = n.f0.accept(this, argu);
            String pe2 = n.f2.accept(this, argu);

            pe1 = ExecuteExpr(argu,pe1);

            pe2 = ExecuteExpr(argu,pe2);

            WriteInFile("%_"+counter+" = add i32 "+pe1+", "+pe2+"\n");
            counter++;

            return "%_"+Integer.toString(counter - 1);
    }

    public String visit(MinusExpression n, String argu) {
            String pe1 = n.f0.accept(this, argu);
            String pe2 = n.f2.accept(this, argu);

            pe1 = ExecuteExpr(argu,pe1);

            pe2 = ExecuteExpr(argu,pe2);

            WriteInFile("%_"+counter+" = sub i32 "+pe1+", "+pe2+"\n");
            counter++;

            return "%_"+Integer.toString(counter - 1);
    }

    public String visit(TimesExpression n, String argu) {
            String pe1 = n.f0.accept(this, argu);
            String pe2 = n.f2.accept(this, argu);

            pe1 = ExecuteExpr(argu,pe1);

            pe2 = ExecuteExpr(argu,pe2);

            WriteInFile("%_"+counter+" = mul i32 "+pe1+", "+pe2+"\n");
            counter++;

            return "%_"+Integer.toString(counter - 1);
    }

    public String visit(IfStatement n, String argu) {
            String expr = n.f2.accept(this, argu);
            expr = ExecuteExpr(argu,expr);

            int if1 = counter, if2 = counter + 1, if3 = counter + 2;
            WriteInFile("br i1 "+expr+", label %if"+if1+", label %if"+if2+"\n");
            counter = counter + 3;

            WriteInFile("if"+if1+":\n");
            n.f4.accept(this, argu);
            WriteInFile("br label %if"+if3+"\n");

            WriteInFile("if"+if2+":\n");
            n.f6.accept(this, argu);
            WriteInFile("br label %if"+if3+"\n");

            WriteInFile("if"+if3+":\n");

            return null;
    }

    public String visit(WhileStatement n, String argu) {
            int loop1 = counter;
            counter++;
            WriteInFile("br label %loop"+loop1+"\n");
            WriteInFile("loop"+loop1+":\n");

            String expr = n.f2.accept(this, argu);
            expr = ExecuteExpr(argu,expr);

            int loop2 = counter, loop3 = counter + 1;
            WriteInFile("br i1 "+expr+", label %loop"+loop2+", label %loop"+loop3+"\n");
            counter = counter + 2;

            WriteInFile("loop"+loop2+":\n");
            n.f4.accept(this, argu);

            WriteInFile("br label %loop"+loop1+"\n");

            WriteInFile("loop"+loop3+":\n");
            return null;
    }

    public String visit(PrintStatement n, String argu) {
            String expr = n.f2.accept(this, argu);

            expr = ExecuteExpr(argu,expr);

            WriteInFile("call void (i32) @print_int(i32 "+expr+")\n");

            return null;
    }

    public String visit(ArrayLookup n, String argu) {
            WriteInFile("\n");
            String Arrayexpr = n.f0.accept(this, argu);

            Arrayexpr = ExecuteExprForArray(argu,Arrayexpr);

            WriteInFile("%_"+counter+" = load i32*, i32** "+Arrayexpr+"\n");
            String array_pointer = "%_"+counter;
            counter++;

            String index = n.f2.accept(this, argu);

            index = ExecuteExpr(argu,index);

            WriteInFile("%_"+counter+" = load i32, i32* "+array_pointer+"\n");
            counter++;
            WriteInFile("%_"+counter+" = icmp ult i32 "+index+", %_"+(counter-1)+"\n");

            int oob1 = counter+1, oob2 = counter+2, oob3 = counter+3;
            WriteInFile("br i1 %_"+counter+", label %oob"+oob1+", label %oob"+oob2+"\n");
            counter = counter + 4;

            WriteInFile("oob"+oob1+":\n");
            WriteInFile("%_"+counter+" = add i32 "+index+", 1\n");
            counter++;

            WriteInFile("%_"+counter+" = getelementptr i32, i32* "+array_pointer+", i32 %_"+(counter-1)+"\n");
            counter++;

            WriteInFile("%_"+counter+" = load i32, i32* %_"+(counter-1)+"\n");
            counter++;


            WriteInFile("br label %oob"+oob3+"\n");

            WriteInFile("oob"+oob2+":\n");
            WriteInFile("call void @throw_oob()\nbr label %oob"+oob3+"\n");
            WriteInFile("oob"+oob3+":\n");

            return "%_"+Integer.toString(counter-1);
    }

    public String visit(ArrayLength n, String argu) {
            String expr = n.f0.accept(this, argu);

            expr = ExecuteExprForArray(argu,expr);

            WriteInFile("%_"+counter+" = load i32*, i32** "+expr+"\n");
            counter++;

            WriteInFile("%_"+counter+" = load i32, i32* %_"+(counter-1)+"\n");
            counter++;

            return "%_"+Integer.toString(counter-1);
    }

    public String visit(MessageSend n, String argu) {
            String expr = n.f0.accept(this, argu);
            String typeofexpr = ReturnTypeOfId(expr);

            if (typeofexpr == null) {
                    String[] str = ReturnInfoOfVarInClass(argu,expr);
                    if (str != null) typeofexpr = str[0];
                    else typeofexpr = TypeWeHaveNow;
            }

            if (expr.equals("this")) {
                    expr = "%this";
            }
            else
                    expr = ExecuteExpr(argu,expr);

            WriteInFile("%_"+counter+" = bitcast i8* "+expr+" to i8***\n");
            counter++;
            WriteInFile("%_"+counter+" = load i8**, i8*** %_"+(counter-1)+"\n");
            counter++;

            String id = n.f2.accept(this, argu);

            int ClassIndex = ReturnIndexOfClass(typeofexpr);
            int MethodIndex = ReturnIndexOfMethodofClass(ClassIndex,id);
            String[] MethodInfo = ClassesMethodVtables[ClassIndex][MethodIndex];

            WriteInFile("%_"+counter+" = getelementptr i8*, i8** %_"+(counter-1)+", i32 "+MethodIndex+"\n");
            counter++;
            WriteInFile("%_"+counter+" = load i8*, i8** %_"+(counter-1)+"\n");
            counter++;

            String Returntype = typetoi(MethodInfo[1]);
            int PtrToMethod = counter;

            String TempString;
            TempString = "%_"+counter+" = bitcast i8* %_"+(counter-1)+" to "+Returntype+" (i8*";
            if (MethodInfo.length > 3) {
                    for (int i = 3; i < MethodInfo.length; i++) {
                            TempString = TempString+", "+typetoi(MethodInfo[i]);
                    }
            }
            WriteInFile(TempString+")*\n");
            counter++;

            String exprList = n.f4.accept(this, argu);

            if (exprList == null) {
                    WriteInFile("%_"+counter+" = call "+Returntype+" %_"+(counter-1)+"(i8* "+expr+")\n");
            }
            else {
                    String[] methodexprs = exprList.split(" ");
                    TempString = "%_"+counter+" = call "+Returntype+" %_"+PtrToMethod+"(i8* "+expr;
                    for (int i = 0; i < methodexprs.length; i++) {
                            TempString = TempString+", "+typetoi(MethodInfo[i + 3])+" "+methodexprs[i];
                    }
                    WriteInFile(TempString+")\n");
            }
            counter++;

            return "%_"+(counter-1);
    }

    String temp;    //Xrhsh gia na mazepsoume ta paramaters tis sunarthseis
    public String visit(ExpressionList n, String argu) {
            temp = "";

            String expr = n.f0.accept(this, argu);
            expr = ExecuteExpr(argu,expr);

            if (expr == null) return null;
            this.temp = expr;

            n.f1.accept(this, argu);
            return this.temp;
    }


    public String visit(ExpressionTail n, String argu) {
            return n.f0.accept(this, argu);
    }

    public String visit(ExpressionTerm n, String argu) {
            String expr = n.f1.accept(this, argu);
            expr = ExecuteExpr(argu,expr);

            this.temp = this.temp + " " + expr;
            return expr;
    }


    public String visit(Clause n, String argu) {
            return n.f0.accept(this, argu);
    }

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

    public String visit(Identifier n, String argu) {
            return n.f0.toString();
    }

    public String visit(IntegerLiteral n, String argu) {
            return n.f0.toString();
    }

    public String visit(TrueLiteral n, String argu) {
            return "1";
    }

    public String visit(FalseLiteral n, String argu) {
            return "0";
    }

    public String visit(ThisExpression n, String argu) {
            return n.f0.toString();
    }

    public String visit(ArrayAllocationExpression n, String argu) {
            String expr = n.f3.accept(this, argu);

            expr = ExecuteExpr(argu,expr);

            WriteInFile("%_"+counter+" = icmp slt i32 "+expr+", 0\n");
            WriteInFile("br i1 %_"+counter+", label %arr_alloc"+(counter+1)+", label %arr_alloc"+(counter+2)+"\n");
            counter = counter + 3;

            WriteInFile("arr_alloc"+(counter-2)+":\ncall void @throw_oob()\nbr label %arr_alloc"+(counter-1)+"\n");

            WriteInFile("arr_alloc"+(counter-1)+":\n");
            WriteInFile("%_"+counter+" = add i32 "+expr+", 1\n");
            counter++;
            WriteInFile("%_"+counter+" = call i8* @calloc(i32 4, i32 "+"%_"+(counter-1)+")\n");
            counter++;
            WriteInFile("%_"+counter+" = bitcast i8* %_"+(counter-1)+" to i32*\n");
            WriteInFile("store i32 "+expr+", i32* %_"+counter+"\n");
            counter++;

            return "%_"+Integer.toString(counter-1);
    }


    String TypeWeHaveNow;    //Xrhsh gia new xwris assignment

    public String visit(AllocationExpression n, String argu) {
            String id = n.f1.accept(this, argu);
            TypeWeHaveNow = id;

            int ClassIndex = ReturnIndexOfClass(id);
            int MemToAlloc = CDs.get(ClassIndex + 1).VarDecSize;
            int num_of_methods = this.CDs.get(ClassIndex + 1).MethDecSize / 8;

            WriteInFile("%_"+counter+" = call i8* @calloc(i32 1, i32 "+(MemToAlloc+8)+")\n");
            counter++;

            WriteInFile("%_"+counter+" = bitcast i8* %_"+(counter-1)+" to i8***\n");
            counter++;
            WriteInFile("%_"+counter+" = getelementptr ["+num_of_methods+" x i8*], ["+num_of_methods+" x i8*]* @."+id+"_vtable, i32 0, i32 0\n");
            counter++;
            WriteInFile("store i8** %_"+(counter-1)+", i8*** %_"+(counter-2)+"\n");

            return "%_"+(counter-3);
    }

    public String visit(NotExpression n, String argu) {
            String clause = n.f1.accept(this, argu);

            clause = ExecuteExpr(argu,clause);

            WriteInFile("%_"+counter+" = xor i1 1, "+clause+"\n");
            counter++;

            return "%_"+Integer.toString(counter-1);
    }

    public String visit(BracketExpression n, String argu) {
            return n.f1.accept(this, argu);
    }
}
