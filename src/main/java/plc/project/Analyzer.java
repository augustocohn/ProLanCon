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

/**
 *
 * Check visit(ast.getReceiver()) for Assignment - NOT SURE IF NEEDED
 *
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
    public Void visit(Ast.Source ast) { // TODO
        //Check for main
        boolean checkMain = false;

        //Visit fields
        for(Ast.Field field : ast.getFields()){
            visit(field);
        }

        //Visit methods
        for(Ast.Method method : ast.getMethods()){
            visit(method);
            //If method name = main
            if(method.getName().equals("main")){
                //check for arity = 0 and returnType = Integer
                if(method.getParameters().isEmpty() && (method.getReturnTypeName().isPresent() && method.getReturnTypeName().get().equals("Integer"))){
                    checkMain = true;
                }
            }
        }

        //Check for name = main, arity = 0, returnType = Integer
        if(!checkMain) {
            throw new RuntimeException("No main/0/Integer");
        }

        return null;
    }

    @Override
    public Void visit(Ast.Field ast) { // TODO
        //Convert string type to Environment.Type
        Environment.Type type = Environment.getType(ast.getTypeName());

        //Ensure that type == expected type and visit if true
        if(ast.getValue().isPresent()) {
            //visit to assign type to it
            visit(ast.getValue().get());
            //check to ensure type matches expected type
            requireAssignable(type, ast.getValue().get().getType());
        }

        //Define variable in the current scope
        scope.defineVariable(ast.getName(), ast.getName(), type, Environment.NIL);
        //Sets it in the ast
        ast.setVariable(scope.lookupVariable(ast.getName()));

        return null;
    }

    @Override
    public Void visit(Ast.Method ast) { // TODO
        //Get list of return type strings and convert to corresponding Environment.Type
        List<Environment.Type> paramType = new ArrayList<Environment.Type>();
        for(String str : ast.getParameterTypeNames()){
            paramType.add(Environment.getType(str));
        }

        //Save expected return type to be checked with visit(Ast.Stmt.Return)
        Environment.Type returnType;
        //If return type is present, save
        if(ast.getReturnTypeName().isPresent()) {
            returnType = Environment.getType(ast.getReturnTypeName().get());
            //If not, return type is NIL (jvmName: Void)
        } else {
            returnType = Environment.Type.NIL;
        }

        //defines a function in the current scope
        scope.defineFunction(ast.getName(), ast.getName(), paramType, returnType, args -> Environment.NIL);

        scope = new Scope(scope);

        for(Ast.Stmt stmt : ast.getStatements()){
            visit(stmt);
            //Checks to see if visited statement was a return
            if(stmt instanceof Ast.Stmt.Return){
                //If the visited statement doesn't match expected, throw error
                if(!((Ast.Stmt.Return) stmt).getValue().getType().equals(returnType)){
                    throw new RuntimeException("Return types doesn't match");
                }
            }
        }

        scope = scope.getParent();

        //Sets function in the ast after statements have been visited and return type established
        ast.setFunction(scope.lookupFunction(ast.getName(), ast.getParameters().size()));

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Expression ast) { // TODO
        //Expression cannot be instance of Expr.Function
        if(!(ast.getExpression() instanceof Ast.Expr.Function)){
            throw new RuntimeException("Invalid Expression");
        }

        //Expression can be visited
        visit(ast.getExpression());

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Declaration ast) { // TODO
        Environment.Type type;
        //If type exists
        if(ast.getTypeName().isPresent()){
            //Store type
            type = Environment.getType(ast.getTypeName().get());
            //Check if value type is equal to expected type
            if(ast.getValue().isPresent()){
                visit(ast.getValue().get());
                //if(!type.equals(ast.getValue().get().getType())){
                //  throw new RuntimeException("Types don't match");
                //}
                requireAssignable(type, ast.getValue().get().getType());
            }
            //Type isn't present but value is
        } else if(ast.getValue().isPresent()){
            //Visit and set type equal to value type
            visit(ast.getValue().get());
            type = ast.getValue().get().getType();
            //If both aren't present throw error
        } else {
            throw new RuntimeException("Invalid declaration");
        }

        //Define variable
        scope.defineVariable(ast.getName(), ast.getName(), type, Environment.NIL);
        //Apply variable to ast
        ast.setVariable(scope.lookupVariable(ast.getName()));

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Assignment ast) { // TODO
        //If receiver isn't an access expression
        if(!(ast.getReceiver() instanceof Ast.Expr.Access)){
            throw new RuntimeException("Not Ast.Expr.Access");
        }

        //Type Analysis on value
        visit(ast.getValue());

        //NOT SURE VISIT RECEIVER - MUST FINISH ACCESS
        visit(ast.getReceiver());

        //If value is assignable to receiver
        Environment.Type typeExpected = ast.getReceiver().getType();
        Environment.Type typeReceived = ast.getValue().getType();
        requireAssignable(typeExpected, typeReceived);


        return null;
    }

    @Override
    public Void visit(Ast.Stmt.If ast) { // TODO            DONE??? 3 SHOULDN'T pass
        visit(ast.getCondition());

        //Condition is not of Type Boolean OR if there are no Then Statement
        if(!ast.getCondition().getType().getName().equals("Boolean") || ast.getThenStatements().isEmpty()){
            throw new RuntimeException("Not valid If statement");
        }
        //MIGHT WANT TO USE requireAssignable
        //requireAssignable(Environment.Type.BOOLEAN, ast.getCondition().getType());

        //Condition is of Type Boolean
        else{
            //Visit then statements
            scope = new Scope(scope);
            for(Ast.Stmt stmt : ast.getThenStatements()){
                visit(stmt);
            }
            scope = scope.getParent();

            //Visit else statements
            scope = new Scope(scope);
            for(Ast.Stmt stmt : ast.getElseStatements()){
                visit(stmt);
            }
            scope = scope.getParent();
        }

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.For ast) { // TODO
        //Checks for valid type
        requireAssignable(Environment.Type.INTEGER_ITERABLE, ast.getValue().getType());
        //checks for empty body
        if(ast.getStatements().isEmpty()){
            throw new RuntimeException("Invalid For statement");
        }

        //Preforms for loop
        else{
            scope = new Scope(scope);

            //Defines the for each variable
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
        //Condition isn't boolean or doesn't contain statements
        if(!ast.getCondition().getType().getName().equals("Boolean")){
            throw new RuntimeException("Invalid While statement");
        }

        //While is properly defined
        else{
            scope = new Scope(scope);

            for(Ast.Stmt stmt : ast.getStatements()){
                visit(stmt);
            }

            scope = scope.getParent();
        }

        return null;

        //LECTURE IMPLEMENTATION
        /*
        visit(ast.getCondition());
        requireAssignable(Environment.Type.BOOLEAN, ast.getCondition().getType());
        Try{
            scope = new Scope(scope);
            For(Ast.Stmt stmt : ast.getStatements()){
                visit(stmt);
            }
        }
        Finally{
            scope = scope.getParent();
        }
        return null;
    }
        */
    }

    @Override
    public Void visit(Ast.Stmt.Return ast) { // TODO
        visit(ast.getValue());
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
        //If expression isn't binary
        if(!(ast.getExpression() instanceof Ast.Expr.Binary)){
            throw new RuntimeException("Not a Binary expression");
        }

        //Proper grouping
        //Set the type of the internal expression
        visit(ast.getExpression());
        //Must set same type for the entire grouped expression
        ast.setType(ast.getExpression().getType());

        return null;
    }

    @Override
    public Void visit(Ast.Expr.Binary ast) { // TODO                                                    PASSED: Binary
        //Recursively visit left and right statements
        visit(ast.getLeft());
        visit(ast.getRight());

        switch(ast.getOperator()){
            //L & R must be boolean for AND/OR
            case "AND":
            case "OR":
                if(!(compareType(ast.getLeft().getType(), ast.getRight().getType()) == 0)){
                    throw new RuntimeException("Not Boolean");
                }
                else{
                    ast.setType(Environment.Type.BOOLEAN);
                }
                break;

            //L & R cannot be boolean or different types
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

            //Numerical arithmetic
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
    public Void visit(Ast.Expr.Access ast) { // TODO          OFFICE HOURS SOLUTION
        //Has a receiver
        if(ast.getReceiver().isPresent()){
            visit(ast.getReceiver().get());
            ast.setVariable(ast.getReceiver().get().getType().getField(ast.getName()));
        }
        //Doesn't have receiver
        else{
            Environment.Variable temp = scope.lookupVariable(ast.getName());
            ast.setVariable(temp);
        }

        return null;
    }

    @Override
    public Void visit(Ast.Expr.Function ast) { // TODO    TWO DIFFERENT SOLUTIONS - NOT SURE WHICH IS CORRECT
        //Has a receiver
        if(ast.getReceiver().isPresent()){
            visit(ast.getReceiver().get());
            ast.setFunction(ast.getReceiver().get().getType().getMethod(ast.getName(), ast.getArguments().size()));
        }
        //Doesn't have a receiver
        else{
            Environment.Function temp = scope.lookupFunction(ast.getName(), ast.getArguments().size());
            ast.setFunction(temp);
        }

        //Checks the args are assignable to the parameter types
        //MIGHT NEED TWEAKING
        for(int i = 0; i < ast.getArguments().size(); i++){
            //visit arguments to apply type
            visit(ast.getArguments().get(i));
            //make sure arguments are right type as specified by definition
            requireAssignable(ast.getFunction().getParameterTypes().get(i), ast.getArguments().get(i).getType());
        }

        return null;
    }

    public static void requireAssignable(Environment.Type target, Environment.Type type) { // TODO      PASSED: requireAssignable
        //System.out.println("Target: " + target);
        //System.out.println("Type: " + type);
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
