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
    public Environment.PlcObject visit(Ast.Expr.Literal ast) { //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Group ast) { //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Binary ast) { //TODO
        throw new UnsupportedOperationException();
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
            throw new RuntimeException("Expected type " + type.getName() + ", received " + object.getValue().getClass().getName() + "."); //TODO
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
