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
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public Void visit(Ast.Field ast) { //TODO
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public Void visit(Ast.Method ast) { //TODO
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public Void visit(Ast.Stmt.Expression ast) { //TODO
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public Void visit(Ast.Stmt.Declaration ast) { //TODO
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public Void visit(Ast.Stmt.Assignment ast) { //TODO
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public Void visit(Ast.Stmt.If ast) { //TODO
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public Void visit(Ast.Stmt.For ast) { //TODO
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public Void visit(Ast.Stmt.While ast) { //TODO
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public Void visit(Ast.Stmt.Return ast) { //TODO
        throw new UnsupportedOperationException();
        //return null;
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

            for(int i = 0; i < ast.getArguments().size() - 1; i++){
                print(ast.getArguments().get(i));
                print(", ");
            }
            print(ast.getArguments().get(ast.getArguments().size() - 1));

            print(")");
        }

        else{
            print(ast.getFunction().getJvmName());
            print("(");

            for(int i = 0; i < ast.getArguments().size() - 1; i++){
                print(ast.getArguments().get(i));
                print(", ");
            }
            print(ast.getArguments().get(ast.getArguments().size() - 1));

            print(")");
        }

        return null;
    }

}
