package plc.project;

import java.io.PrintWriter;

public final class Generator implements Ast.Visitor<Void> {

    private final PrintWriter writer;
    private int indent = 0;

    public Generator(PrintWriter writer) {
        this.writer = writer;
    }

    private void print(Object... objects) {
        for (Object object : objects) {
            if (object instanceof Ast) {
                visit((Ast) object);
            } else {
                writer.write(object.toString());
            }
        }
    }

    private void newline(int indent) {
        writer.println();
        for (int i = 0; i < indent; i++) {
            writer.write("    ");
        }
    }

    @Override
    public Void visit(Ast.Source ast) { //TODO
        print("public class Main {");
        newline(indent);
        newline(++indent);

        //Fields
        for(int i = 0; i < ast.getFields().size(); i++){
            visit(ast.getFields().get(i));
            if(i != ast.getFields().size()-1){
                newline(indent);
            }else{
                newline(--indent);
                newline(++indent);
            }
        }

        print("public static void main(String[] args) {");
        newline(++indent);

        print("System.exit(new Main().main());");
        newline(--indent);
        print("}");
        newline(--indent);
        newline(++indent);

        //Methods
        for(int i = 0; i < ast.getMethods().size(); i++){
            visit(ast.getMethods().get(i));
            if(i != ast.getMethods().size()-1) {
                newline(--indent);
                newline(++indent);
            }else{
                newline(--indent);
                newline(indent);
            }
        }

        print("}");

        return null;
    }

    @Override
    public Void visit(Ast.Field ast) { //TODO
        print(Environment.getType(ast.getTypeName()).getJvmName(), " ", ast.getName());

        if(ast.getValue().isPresent()){
            print(" = ");
            visit(ast.getValue().get());
        }

        print(";");

        return null;
    }

    @Override
    public Void visit(Ast.Method ast) { //TODO
        print(ast.getFunction().getReturnType().getJvmName(), " ", ast.getName(), "(");

        if(!ast.getParameters().isEmpty()){
            for(int i = 0; i < ast.getParameters().size() - 1; i++){
                print(Environment.getType(ast.getParameterTypeNames().get(i)).getJvmName(), " ", ast.getParameters().get(i), ", ");
            }
            print(ast.getParameterTypeNames().get(ast.getParameterTypeNames().size() - 1), " ", ast.getParameters().get(ast.getParameters().size() - 1));
        }

        print(") {");

        if(!ast.getStatements().isEmpty()) {
            newline(++indent);

            for(int i = 0; i < ast.getStatements().size(); i++){
                if(i != 0){
                    newline(indent);
                }
                print(ast.getStatements().get(i));
            }

        }

        if(!ast.getStatements().isEmpty()) {
            newline(--indent);
        }else{
            newline(indent);
        }
        print("}");

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Expression ast) { //TODO
        visit(ast.getExpression());

        print(";");

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Declaration ast) { //TODO
        print(ast.getVariable().getType().getJvmName(), " ", ast.getVariable().getJvmName());

        if(ast.getValue().isPresent()){
            print(" = ", ast.getValue().get());
        }

        print(";");

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Assignment ast) { //TODO
        print(ast.getReceiver(), " = ");

        visit(ast.getValue());

        print(";");

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.If ast) { //TODO                     PASSED: If
        print("if (", ast.getCondition(), ") {");

        newline(++indent);

        for(int i = 0; i < ast.getThenStatements().size(); i++){
            if(i != 0){
                newline(indent);
            }
            print(ast.getThenStatements().get(i));
        }

        newline(--indent);

        print("}");

        if(!ast.getElseStatements().isEmpty()){
            print(" else {");

            newline(++indent);

            for(int i = 0; i < ast.getElseStatements().size(); i++){
                if(i != 0){
                    newline(indent);
                }
                print(ast.getElseStatements().get(i));
            }

            newline(--indent);

            print("}");
        }

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.For ast) { //TODO
        print("for (int ", ast.getName(), " : ");
        visit(ast.getValue());
        print(") {");
        if(!ast.getStatements().isEmpty()) {
            newline(++indent);
            for(int i = 0; i < ast.getStatements().size(); i++){
                if(i != 0){
                    newline(indent);
                }
                print(ast.getStatements().get(i));
            }
            newline(--indent);
        }

        print("}");

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.While ast) { //TODO
        print("while (", ast.getCondition(), ") {");

        if(!ast.getStatements().isEmpty()){
            newline(++indent);
            for(int i = 0; i < ast.getStatements().size(); i++){
                if(i != 0){
                    newline(indent);
                }
                print(ast.getStatements().get(i));
            }
            newline(--indent);
        }

        print("}");

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Return ast) { //TODO
        print("return ");
        visit(ast.getValue());
        print(";");
        return null;
    }

    @Override
    public Void visit(Ast.Expr.Literal ast) { //TODO                PASSED: Literal

        if(ast.getType().getName().equals("String")){
            print("\"");
            print(ast.getLiteral());
            print("\"");
        }
        else if(ast.getType().getName().equals("Character")){
            print("\'");
            print(ast.getLiteral());
            print("\'");
        }
        //Boolean, Int, Decimal
        else {
            print(ast.getLiteral());
        }

        return null;
    }

    @Override
    public Void visit(Ast.Expr.Group ast) { //TODO          PASSED: Group
        print("(");
        visit(ast.getExpression());
        print(")");
        return null;
    }

    @Override
    public Void visit(Ast.Expr.Binary ast) { //TODO
        visit(ast.getLeft());

        switch(ast.getOperator()){
            case "AND":
                print(" && ");
                break;
            case "OR":
                print(" || ");
                break;
            case "<":
                print(" < ");
                break;
            case "<=":
                print(" <= ");
                break;
            case ">":
                print(" > ");
                break;
            case ">=":
                print(" >= ");
                break;
            case "==":
                print(" == ");
                break;
            case "!=":
                print(" != ");
                break;
            case "+":
                print(" + ");
                break;
            case "-":
                print(" - ");
                break;
            case "*":
                print(" * ");
                break;
            case "/":
                print(" / ");
                break;
        }

        visit(ast.getRight());

        return null;
    }

    @Override
    public Void visit(Ast.Expr.Access ast) { //TODO                 PASSED: Access
        if(ast.getReceiver().isPresent()){
            visit(ast.getReceiver().get());
            print(".");
            print(ast.getName());
        }

        else{
            print(ast.getName());
        }
        return null;
    }

    @Override
    public Void visit(Ast.Expr.Function ast) { //TODO                   PASSED: Function...
        if(ast.getReceiver().isPresent()){
            visit(ast.getReceiver().get());
            print(".");
            print(ast.getFunction().getJvmName());
            print("(");

            if(ast.getArguments().size() > 0) {
                for (int i = 0; i < ast.getArguments().size() - 1; i++) {
                    print(ast.getArguments().get(i));
                    print(", ");
                }
                print(ast.getArguments().get(ast.getArguments().size() - 1));
            }

            print(")");
        }

        else{
            print(ast.getFunction().getJvmName());
            print("(");

            if(ast.getArguments().size() > 0) {
                for (int i = 0; i < ast.getArguments().size() - 1; i++) {
                    print(ast.getArguments().get(i));
                    print(", ");
                }
                print(ast.getArguments().get(ast.getArguments().size() - 1));
            }

            print(")");
        }

        return null;
    }

}
