import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.ArrayList;//Gia dynamically alocated Arrays
import java.util.Arrays;

public class ClassesInfoVisitor extends GJDepthFirst<String,String> {
    ArrayList<ClassDeclarations> CDs;

    boolean FoundError;

    public ClassesInfoVisitor() {
            this.CDs =  new ArrayList<ClassDeclarations>();
            FoundError = false;
    }

    public void printClassesInfo() {
            for (int i = 0; i < this.CDs.size(); i++)
                    this.CDs.get(i).printClassInfo();
    }

    public void printProjectClassInfo () {
            if (FoundError == true) {
                    System.out.println("Classes Declarations have ERRORS so we dont print offsets!");
                    return;
            }
            for (int i = 0; i < this.CDs.size(); i++) {
                    if (this.CDs.get(i).FoundError == true) {
                            System.out.println("Classes Declarations have ERRORS so we dont print offsets!");
                            return;
                    }
            }

            //An den exei kapoio Error sta DECLARATIONS! twn Classewn tote ektupwnei ta offsets
            System.out.println("~~~~~~~~~~~ Print Offset for Classes Declarations ~~~~~~~~~~~~");
            for (int i = 1; i < this.CDs.size(); i++)
                    this.CDs.get(i).printProjectClassInfo();
    }

    public String visit(Goal n, String argu) {
            System.out.println("~~~ Checking Classes Declarations and Reading OFFSETS ~~~");
            n.f0.accept(this, argu);
            n.f1.accept(this, argu);
            return null;
    }

    public String visit(MainClass n, String argu) {
       String id = n.f1.accept(this, argu);
       ClassDeclarations CD = new ClassDeclarations(id);

       this.CDs.add(CD);//Prosthetw thn nea Class sto ArrayList
       return null;
    }

    public String visit(ClassDeclaration n, String argu) {
            String id = n.f1.accept(this, argu);

            for (int i = 0; i < this.CDs.size(); i++) {
                    if (id.equals(this.CDs.get(i).ClassName)) {
                            System.out.println("Class : "+id+" - Error : Class "+id+" has already been defined previously!");
                            FoundError = true;
                            return null;
                    }
            }

            ClassDeclarations CD = new ClassDeclarations(id);

            this.CDs.add(CD);//Prosthetw thn nea Class sto ArrayList

            n.f3.accept(this, id);
            n.f4.accept(this, id);

            return null;
    }

    public String visit(ClassExtendsDeclaration n, String argu) {
            String id = n.f1.accept(this, argu);

            for (int i = 0; i < this.CDs.size(); i++) {
                    if (id.equals(this.CDs.get(i).ClassName)) {
                            System.out.println("Class : "+id+" - Error : Class "+id+" has already been defined previously!");
                            FoundError = true;
                            return null;
                    }
            }

            String exid = n.f3.accept(this, argu);

            int flag = -1;
            for (int i = 0; i < this.CDs.size(); i++) {
                    if (exid.equals(this.CDs.get(i).ClassName)) flag = i;
            }

            ClassDeclarations CD;
            if (flag == -1) {
                    System.out.println("Class : "+id+" - Error : Class "+exid+" has not been defined previously!");
                    FoundError = true;
                    CD = new ClassDeclarations(id);
            }
            else {
                    ClassDeclarations extCD = this.CDs.get(flag);

                    CD = new ClassDeclarations(id,extCD);
            }

            this.CDs.add(CD); //Prosthetw thn nea Class sto ArrayList

            String ClassInfo = id + " " + exid;

            n.f5.accept(this, ClassInfo);
            n.f6.accept(this, ClassInfo);
            return null;
    }

    public String visit(MethodDeclaration n, String argu) {
            n.f0.accept(this, argu);
            String type = n.f1.accept(this, argu);
            String id = n.f2.accept(this, argu);
            n.f3.accept(this, argu);
            String arguments = n.f4.accept(this, argu);

            if (argu != null) {
                    if (arguments != null) {
                            String[] splited = arguments.split(" ");

                            this.CDs.get(this.CDs.size() - 1).addMethDec(type,id,splited);
                    }
                    else {
                            this.CDs.get(this.CDs.size() - 1).addMethDec(type,id,null);
                    }
            }
            return type + " " + id;
    }

    public String visit(VarDeclaration n, String argu) {
            String type = n.f0.accept(this, argu);
            String id = n.f1.accept(this, argu);

            if (argu != null)
                    this.CDs.get(this.CDs.size() - 1).addVarDec(type,id);

            return null;
    }

    String temp;//Xrhsh gia na mazepsoume ta paramaters tis sunarthseis
    public String visit(FormalParameterList n, String argu) {
            temp = "";
            if (n.f0.accept(this, argu) == null) return null;
            this.temp = n.f0.accept(this, argu);

            n.f1.accept(this, argu);

            return this.temp;
    }

    public String visit(FormalParameter n, String argu) {
            return n.f0.accept(this, argu);
    }

    public String visit(FormalParameterTail n, String argu) {
            return n.f0.accept(this, argu);
    }

    public String visit(FormalParameterTerm n, String argu) {
            temp = temp + " " + n.f1.accept(this, argu);
            return n.f1.accept(this, argu);
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

}
