package plc.project;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Interpreter implements Ast.Visitor<Environment.PlcObject> {

    /**
     * https://www.craftinginterpreters.com/evaluating-expressions.html
     * https://www.craftinginterpreters.com/statements-and-state.html
     * https://www.craftinginterpreters.com/control-flow.html
     * https://www.craftinginterpreters.com/functions.html
     **/

    private Scope scope = new Scope(null);

    public Interpreter(Scope parent) {
        scope = new Scope(parent);
        scope.defineFunction("print", 1, args -> {
            System.out.println(args.get(0).getValue());
            return Environment.NIL;
        });
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public Environment.PlcObject visit(Ast.Source ast) { //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Environment.PlcObject visit(Ast.Field ast) { //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Environment.PlcObject visit(Ast.Method ast) { //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.Expression ast) { //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.Declaration ast) { //TODO (in lecture)
        if(ast.getValue().isPresent()){
            scope.defineVariable(ast.getName(),visit(ast.getValue().get()));
        }
        else{
            scope.defineVariable(ast.getName(), Environment.NIL);
        }
        return Environment.NIL;
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.Assignment ast) { //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.If ast) { //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.For ast) { //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.While ast) { //TODO (in lecture)
        while(requireType(Boolean.class,visit(ast.getCondition()))){
            try{
                scope = new Scope(scope);
                for(Ast.Stmt statement : ast.getStatements()){
                    visit(statement);
                }
            }
            finally {
                scope = scope.getParent();
            }
        }
        return Environment.NIL;
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.Return ast) { //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Literal ast) { //TODO       PASSED: LiteralExpression
        if(ast.getLiteral() == null) {
            return Environment.NIL;                                     //FIGURE OUT HOW TO RETURN ENVIRONMENT.NIL
        }

        /**Documentation hint: use "Environment.create" as needed (BUT we must return an Environment.PLCObject)*/

        /**All 3 pass*/
        //return Environment.create(ast.getLiteral());
        //return new Environment.PlcObject(null, ast.getLiteral());
        return new Environment.PlcObject(scope, ast.getLiteral());          //Do we make a new scope each time?
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Group ast) { //TODO
        String expression = ast.getExpression().toString();
        System.out.println(ast.getExpression());
        return new Environment.PlcObject(scope, ast.getExpression());
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Binary ast) { //TODO          PASSED: And, Or,
        //throw new UnsupportedOperationException();

        Environment.PlcObject left = Environment.create(ast.getLeft());
        System.out.println(left.getValue());
        Environment.PlcObject right = Environment.create(ast.getRight());

        switch (ast.getOperator()){
            case "AND":
                if(left == right){
                    return Environment.create(true);
                }
                return Environment.create(false);
            case "OR":
                if(ast.getLeft().toString().equals("false") && ast.getRight().toString().equals("false")){
                    return Environment.create(false);
                }
                return Environment.create(true);
            case ">":
            case ">=":
            case "<":
            case "<=":
            case "==":
            case "!=":
            case "+":
                //String Concatenation
                //Number Addition
            case "-":
            case "*":
            case "/":
        }
        return Environment.NIL;
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Access ast) { //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Function ast) { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Helper function to ensure an object is of the appropriate type.
     */
    private static <T> T requireType(Class<T> type, Environment.PlcObject object) {
        if (type.isInstance(object.getValue())) {
            return type.cast(object.getValue());
        } else {
            throw new RuntimeException("Expected type " + type.getName() + ", received " + object.getValue().getClass().getName() + ".");
        }
    }

    /**
     * Exception class for returning values.
     */
    private static class Return extends RuntimeException {

        private final Environment.PlcObject value;

        private Return(Environment.PlcObject value) {
            this.value = value;
        }

    }

}
