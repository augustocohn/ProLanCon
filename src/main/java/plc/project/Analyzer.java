package plc.project;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
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
    public Void visit(Ast.Method ast) { // TODO
        List<Environment.Type> paramType = new ArrayList<Environment.Type>();

        for(String str : ast.getParameterTypeNames()){
            paramType.add(Environment.getType(str));
        }

        Environment.Type returnType = Environment.getType(ast.getReturnTypeName().get());   //Look-back on; office hours


        scope.defineFunction(ast.getName(), ast.getName(), paramType, returnType, args -> Environment.NIL);

        scope = new Scope(scope);

        for(Ast.Stmt stmt : ast.getStatements()){   //Need to check for return (LOOK at hint); Instanceof Return?
            visit(stmt);
        }

        scope = scope.getParent();

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Expression ast) { // TODO
        //
        if(!(ast.getExpression() instanceof Ast.Expr.Function)){
            throw new RuntimeException("Invalid Expression");
        }

        //
        else{
            visit(ast.getExpression());
        }

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Declaration ast) { // TODO


        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Assignment ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.If ast) { // TODO            DONE??? 3 SHOULDn't pass
        visit(ast.getCondition());

        //Condition is not of Type Boolean OR if there are no Then Statement
        if(!ast.getCondition().getType().getName().equals("Boolean") || ast.getThenStatements().isEmpty()){
            throw new RuntimeException("Not valid If statement");
        }

        //Condition is of Type Boolean
        else{
            scope = new Scope(scope);
            for(Ast.Stmt stmt : ast.getThenStatements()){
                visit(stmt);
            }
            scope = scope.getParent();

            scope = new Scope(scope);
            for(Ast.Stmt stmt : ast.getElseStatements()){
                visit(stmt);
            }
            scope = scope.getParent();
        }

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.For ast) { // TODO    Done???
        //
        if(!ast.getValue().getType().getName().equals("IntegerIterable") || ast.getStatements().isEmpty()){
            throw new RuntimeException("Invalid For statement");
        }

        //
        else{
            scope = new Scope(scope);

            scope.defineVariable(ast.getName(), ast.getName(), Environment.Type.INTEGER, Environment.NIL);
            for(Ast.Stmt stmt : ast.getStatements()){
                visit(stmt);
            }

            scope = scope.getParent();
        }

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.While ast) { // TODO    DONE?
        visit(ast.getCondition());
        //
        if(!ast.getCondition().getType().getName().equals("Boolean")){
            throw new RuntimeException("Invalid While statement");
        }

        //
        else{
            scope = new Scope(scope);

            for(Ast.Stmt stmt : ast.getStatements()){
                visit(stmt);
            }

            scope = scope.getParent();
        }

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Return ast) { // TODO


        return null;
    }

    @Override
    public Void visit(Ast.Expr.Literal ast) { // TODO                                                   PASSED: Literal

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
    public Void visit(Ast.Expr.Binary ast) { // TODO                                                    PASSED: Binary
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
    public Void visit(Ast.Expr.Access ast) { // TODO
        //Has a receiver
        if(ast.getReceiver().isPresent()){
            System.out.println(ast.getReceiver().get());
            visit(ast.getReceiver().get());
            System.out.println(ast.getReceiver().get());
            Environment.Variable var = scope.lookupVariable(ast.getName());
            ast.setVariable(var);
        }

        //Doesn't have a receiver
        else{
            Environment.Variable temp = scope.lookupVariable(ast.getName());
            ast.setVariable(temp);
        }

        return null;
    }

    @Override
    public Void visit(Ast.Expr.Function ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    public static void requireAssignable(Environment.Type target, Environment.Type type) { // TODO      PASSED: requireAssignable
        System.out.println("Target: " + target);
        System.out.println("Type: " + type);
        if (!target.getName().equals("Comparable") && !target.getName().equals("Any")) {
            if (!type.getName().equals(target.getName())) {
                throw new RuntimeException("Wrong type");
            }
        }
        else if (target.getName().equals("Comparable")) {
            if(!type.getName().equals("Integer") && !type.getName().equals("Decimal") && !type.getName().equals("Character") && !type.getName().equals("String")){
                throw new RuntimeException("Wrong type");
            }
        }
        //Original: if(!type.getName().equals(target.getName())){throw new RuntimeException("Wrong type");}
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
