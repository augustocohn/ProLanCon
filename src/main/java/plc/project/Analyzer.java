package plc.project;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * See the specification for information about what the different visit
 * methods should do.
 */
public final class Analyzer implements Ast.Visitor<Void> {

    public Scope scope;
    private Ast.Method method;

    public Analyzer(Scope parent) {
        scope = new Scope(parent);
        scope.defineFunction("print", "System.out.println", Arrays.asList(Environment.Type.ANY), Environment.Type.NIL, args -> Environment.NIL);
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public Void visit(Ast.Source ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Field ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Method ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.Expression ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.Declaration ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.Assignment ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.If ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.For ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.While ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.Return ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Expr.Literal ast) { // TODO                   PASSED: LITERAL

        //Nil, Bool, Char, String
        if(ast.getLiteral() == null){
            ast.setType(Environment.Type.NIL);
        }
        else if(ast.getLiteral() instanceof Boolean){
            ast.setType(Environment.Type.BOOLEAN);
        }
        else if(ast.getLiteral() instanceof Character){
            ast.setType(Environment.Type.CHARACTER);
        }
        else if(ast.getLiteral() instanceof String){
            ast.setType(Environment.Type.STRING);
        }

        //Integer
        else if(ast.getLiteral() instanceof BigInteger){
            if(((BigInteger) ast.getLiteral()).longValue() >= Integer.MIN_VALUE && ((BigInteger) ast.getLiteral()).longValue() <= Integer.MAX_VALUE) {
                ast.setType(Environment.Type.INTEGER);
            }
            else{
                throw new RuntimeException("Not within Integer range");
            }
        }

        //Decimal
        else if(ast.getLiteral() instanceof BigDecimal){
            if(!(((BigDecimal) ast.getLiteral()).doubleValue() == Double.NEGATIVE_INFINITY || ((BigDecimal) ast.getLiteral()).doubleValue() == Double.POSITIVE_INFINITY)){
                ast.setType(Environment.Type.DECIMAL);
            }
            else{
                throw new RuntimeException("Not within Double range");
            }
        }

        return null;
    }

    @Override
    public Void visit(Ast.Expr.Group ast) { // TODO
        if(!(ast.getExpression() instanceof Ast.Expr.Binary)){
            throw new RuntimeException("Not a Binary expression");
        }

        visit(ast.getExpression());

        return null;
    }

    @Override
    public Void visit(Ast.Expr.Binary ast) { // TODO                PASSED: Binary
        visit(ast.getLeft());
        visit(ast.getRight());

        switch(ast.getOperator()){
            case "AND":
            case "OR":
                if(!(compareType(ast.getLeft().getType(), ast.getRight().getType()) == 0)){
                    throw new RuntimeException("Not Boolean");
                }
                else{
                    ast.setType(Environment.Type.BOOLEAN);
                }
                break;

            case "<":
            case "<=":
            case ">":
            case ">=":
            case "==":
            case "!=":
                if(!(compareType(ast.getLeft().getType(), ast.getRight().getType()) == -1) && !(compareType(ast.getLeft().getType(), ast.getRight().getType()) == 0)){
                    ast.setType(Environment.Type.BOOLEAN);
                }
                else{
                    throw new RuntimeException("Not Comparable");
                }
                break;

            case "+":
                //String Concatenation
                if(ast.getLeft().getType().getName().equals("String") || ast.getRight().getType().getName().equals("String")){
                    ast.setType(Environment.Type.STRING);
                }

                //Number Addition
                else if(compareType(ast.getLeft().getType(), ast.getRight().getType()) == 1){
                    ast.setType(Environment.Type.INTEGER);
                }
                else if(compareType(ast.getLeft().getType(), ast.getRight().getType()) == 2){
                    ast.setType(Environment.Type.DECIMAL);
                }
                else{
                    throw new RuntimeException("Not Addable");
                }
                break;

            case "-":
            case "*":
            case "/":
                if(compareType(ast.getLeft().getType(), ast.getRight().getType()) == 1){
                    ast.setType(Environment.Type.INTEGER);
                }
                else if(compareType(ast.getLeft().getType(), ast.getRight().getType()) == 2){
                    ast.setType(Environment.Type.DECIMAL);
                }
                else{
                    throw new RuntimeException("Not Addable");
                }
                break;
        }

        return null;
    }

    @Override
    public Void visit(Ast.Expr.Access ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Expr.Function ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    public static void requireAssignable(Environment.Type target, Environment.Type type) { // TODO
        if(!type.getName().equals(target.getName())){throw new RuntimeException("Wrong type");}
    }

    //enum Type {Boolean, Integer, Decimal, Character, String}

    public static int compareType (Environment.Type left, Environment.Type right){
        if(left.getName().equals("Boolean") && right.getName().equals("Boolean")){
            return 0;
        }
        if(left.getName().equals("Integer") && right.getName().equals("Integer")){
            return 1;
        }
        if(left.getName().equals("Decimal") && right.getName().equals("Decimal")){
            return 2;
        }
        if(left.getName().equals("Character") && right.getName().equals("Character")){
            return 3;
        }
        if(left.getName().equals("String") && right.getName().equals("String")){
            return 4;
        }
        return -1;
    }

}
