package plc.project;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        if(ast.getLiteral() == null)
            return Environment.NIL;
        return Environment.create(ast.getLiteral());
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Group ast) { //TODO
        return visit(ast.getExpression());
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Binary ast) { //TODO          PASSED: And, Or,
        Environment.PlcObject left = visit(ast.getLeft());
        Environment.PlcObject right = visit(ast.getRight());
        switch (ast.getOperator()){
            case "AND":
                if(requireType(Boolean.class, left) && requireType(Boolean.class, right)){
                    return Environment.create(true);
                }
                return Environment.create(false);

            case "OR":
                if(requireType(Boolean.class, left)){
                    return Environment.create(true);
                }
                else if(requireType(Boolean.class, right)){
                    return Environment.create(true);
                }
                return Environment.create(false);

            case ">":
                if(compareType(left, right) == 0){
                    int temp = requireType(BigInteger.class, left).compareTo(requireType(BigInteger.class, right));
                    if(temp > 0){return Environment.create(true);}

                    return Environment.create(false);
                }
                if(compareType(left, right) == 1){
                    int temp = requireType(BigDecimal.class, left).compareTo(requireType(BigDecimal.class, right));
                    if(temp > 0){return Environment.create(true);}

                    return Environment.create(false);
                }

            case ">=":
                if(compareType(left, right) == 0){
                    int temp = requireType(BigInteger.class, left).compareTo(requireType(BigInteger.class, right));
                    if(temp >= 0){return Environment.create(true);}

                    return Environment.create(false);
                }
                if(compareType(left, right) == 1){
                    int temp = requireType(BigDecimal.class, left).compareTo(requireType(BigDecimal.class, right));
                    if(temp >= 0){return Environment.create(true);}

                    return Environment.create(false);
                }

            case "<":
                if(compareType(left, right) == 0){
                    int temp = requireType(BigInteger.class, left).compareTo(requireType(BigInteger.class, right));
                    if(temp < 0){return Environment.create(true);}

                    return Environment.create(false);
                }
                if(compareType(left, right) == 1){
                    int temp = requireType(BigDecimal.class, left).compareTo(requireType(BigDecimal.class, right));
                    if(temp < 0){return Environment.create(true);}

                    return Environment.create(false);
                }

            case "<=":
                if(compareType(left, right) == 0){
                    int temp = requireType(BigInteger.class, left).compareTo(requireType(BigInteger.class, right));
                    if(temp <= 0){return Environment.create(true);}

                    return Environment.create(false);
                }
                if(compareType(left, right) == 1){
                    int temp = requireType(BigDecimal.class, left).compareTo(requireType(BigDecimal.class, right));
                    if(temp <= 0){return Environment.create(true);}

                    return Environment.create(false);
                }

            case "==":
                if(compareType(left, right) != -1){
                    if(Objects.equals(left, right)){
                        return Environment.create(true);
                    }
                }
                return Environment.create(false);

            case "!=":
                if(compareType(left, right) != -1){
                    if(Objects.equals(left, right)){
                        return Environment.create(false);
                    }
                }
                return Environment.create(true);

            case "+":
                //String Concatenation
                if((left.getValue() instanceof String) || (right.getValue() instanceof String)){
                    String temp = left.getValue().toString() + right.getValue().toString();
                    return Environment.create(temp);
                }

                //Number Addition
                if(compareType(left, right) == 0){
                    BigInteger temp = ((BigInteger) left.getValue()).add((BigInteger) right.getValue());
                    return Environment.create(temp);
                }
                if(compareType(left, right) == 1){
                    BigDecimal temp = ((BigDecimal) left.getValue()).add((BigDecimal) right.getValue());
                    return Environment.create(temp);
                }

            case "-":
                if(compareType(left, right) == 0){
                    BigInteger temp = ((BigInteger) left.getValue()).subtract((BigInteger) right.getValue());
                    return Environment.create(temp);
                }
                if(compareType(left, right) == 1){
                    BigDecimal temp = ((BigDecimal) left.getValue()).subtract((BigDecimal) right.getValue());
                    return Environment.create(temp);
                }

            case "*":
                if(compareType(left, right) == 0){
                    BigInteger temp = ((BigInteger) left.getValue()).multiply((BigInteger) right.getValue());
                    return Environment.create(temp);
                }
                if(compareType(left, right) == 1){
                    BigDecimal temp = ((BigDecimal) left.getValue()).multiply((BigDecimal) right.getValue());
                    return Environment.create(temp);
                }

            case "/":
                if (right.getValue().toString().equals("0")) {
                    throw new RuntimeException();
                }
                if(compareType(left, right) == 0){
                    BigInteger temp = ((BigInteger) left.getValue()).divide((BigInteger) right.getValue());
                    return Environment.create(temp);
                }
                if(compareType(left, right) == 1){
                    BigDecimal temp = ((BigDecimal) left.getValue()).divide((BigDecimal) right.getValue(), RoundingMode.HALF_EVEN);
                    return Environment.create(temp);
                }
        }
        return Environment.NIL;
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Access ast) { //TODO
        if(ast.getReceiver().isPresent()){
            Ast.Expr temp = ast.getReceiver().get();
            scope.defineVariable(ast.getReceiver().get().toString(),Environment.create(ast.getName()));
            System.out.println(ast.getReceiver().get());
            return new Environment.PlcObject(scope,ast.getReceiver());
        }
        //When the Receiver is empty (Not present)
        return Environment.create(ast.getName());
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

    private static int compareType (Environment.PlcObject left, Environment.PlcObject right){
        if((left.getValue() instanceof BigInteger) && (right.getValue() instanceof BigInteger)){
            return 0;
        }
        if((left.getValue() instanceof BigDecimal) && (right.getValue() instanceof BigDecimal)){
            return 1;
        }
        if((left.getValue() instanceof Boolean) && (right.getValue() instanceof Boolean)){
            return 2;
        }
        if((left.getValue() instanceof String) && (right.getValue() instanceof String)){
            return 3;
        }
        return -1;
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
