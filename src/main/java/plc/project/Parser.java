package plc.project;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The parser takes the sequence of tokens emitted by the lexer and turns that
 * into a structured representation of the program, called the Abstract Syntax
 * Tree (AST).
 *
 * The parser has a similar architecture to the lexer, just with {@link Token}s
 * instead of characters. As before, {@link #peek(Object...)} and {@link
 * #match(Object...)} are helpers to make the implementation easier.
 *
 * This type of parser is called <em>recursive descent</em>. Each rule in our
 * grammar will have it's own function, and reference to other rules correspond
 * to calling that functions.
 */
public final class Parser {

    private final TokenStream tokens;

    //this is Darian
    //this is Gus

    public Parser(List<Token> tokens) {
        this.tokens = new TokenStream(tokens);
    }

    /**
     * Parses the {@code source} rule.
     */
    public Ast.Source parseSource() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses the {@code field} rule. This method should only be called if the
     * next tokens start a field, aka {@code LET}.
     */
    public Ast.Field parseField() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses the {@code method} rule. This method should only be called if the
     * next tokens start a method, aka {@code DEF}.
     */
    public Ast.Method parseMethod() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses the {@code statement} rule and delegates to the necessary method.
     * If the next tokens do not start a declaration, if, while, or return
     * statement, then it is an expression/assignment statement.
     */
    public Ast.Stmt parseStatement() throws ParseException { //TODO             DONE(PASSED: Expression, Assignment)
        //throw new UnsupportedOperationException();
        if(peek("LET")){
            return parseDeclarationStatement();
        }
        else if(peek("IF")){
            return parseIfStatement();
        }
        else if(peek("FOR")){
            return parseForStatement();
        }
        else if(peek("WHILE")){
            return parseWhileStatement();
        }
        else if(peek("RETURN")){
            return parseReturnStatement();
        }
        else{
            Ast.Expr left = parseExpression();
            if(peek("=")){
                String operator = tokens.get(0).getLiteral();
                match("=");
                Ast.Expr right = parseExpression();
                if(match(";")){
                    return new Ast.Stmt.Assignment(left, right);                    //Left: Receiver;  Right: Value
                }
            }
            else if(match(";")){
                return new Ast.Stmt.Expression(left);
            }

            throw new ParseException("Invalid statement", tokens.index);
        }
    }

    /**
     * Parses a declaration statement from the {@code statement} rule. This
     * method should only be called if the next tokens start a declaration
     * statement, aka {@code LET}.
     */
    public Ast.Stmt.Declaration parseDeclarationStatement() throws ParseException { //TODO      DONE(PASSED)
        //throw new UnsupportedOperationException();
        if(match("LET")) {
            String name = tokens.get(0).getLiteral();
            match(Token.Type.IDENTIFIER);
            if (match("=")) {
                Ast.Expr right = parseExpression();                                         //Is there a difference b/w Ast.Expr and Ast.Stmt.Expr***
                if (match(";")) {
                    return new Ast.Stmt.Declaration(name, Optional.of(right));
                }
            } else if (match(";")) {
                return new Ast.Stmt.Declaration(name, Optional.empty());
            }
        }
        throw new ParseException("Invalid statement declaration", tokens.index);
    }

    /**
     * Parses an if statement from the {@code statement} rule. This method
     * should only be called if the next tokens start an if statement, aka
     * {@code IF}.
     */
    public Ast.Stmt.If parseIfStatement() throws ParseException { //TODO        DONE(PASSED)
        //throw new UnsupportedOperationException();
        if(match("IF")) {
            Ast.Expr expression = parseExpression();
            List<Ast.Stmt> thenStatements = new ArrayList<Ast.Stmt>();
            List<Ast.Stmt> elseStatements = new ArrayList<Ast.Stmt>();

            if (match("DO")) {
                Ast.Stmt statement = parseStatement();                                        //WHAT HAPPENS W/ 0 STATEMENTS???
                thenStatements.add(statement);
                if (match("ELSE")) {
                    statement = parseStatement();                                             //WHAT HAPPENS W/ 0 STATEMENTS???
                    elseStatements.add(statement);
                    if (match("END")) {
                        return new Ast.Stmt.If(expression, thenStatements, elseStatements);
                    }
                }
                else if (match("END")) {
                    return new Ast.Stmt.If(expression, thenStatements, elseStatements);       //No Else clause
                }
            }
        }
        throw new ParseException("Invalid if statement", tokens.index);
    }

    /**
     * Parses a for statement from the {@code statement} rule. This method
     * should only be called if the next tokens start a for statement, aka
     * {@code FOR}.
     */
    public Ast.Stmt.For parseForStatement() throws ParseException { //TODO      DONE(PASSED)
        //throw new UnsupportedOperationException();
        if(match("FOR")){
            String name = tokens.get(0).getLiteral();
            match(Token.Type.IDENTIFIER);
            List<Ast.Stmt> stmtList = new ArrayList<Ast.Stmt>();

            if(match("IN")){
                Ast.Expr expression = parseExpression();
                if(match("DO")){
                    if(match("END")){                                           //Handles 0 Do statements
                        return new Ast.Stmt.For(name, expression, stmtList);
                    }
                    else {                                                               //Handles 1+ Do statements
                        System.out.println("HERE");
                        Ast.Stmt statement = parseStatement();
                        stmtList.add(statement);
                        if (match("END")) {
                            return new Ast.Stmt.For(name, expression, stmtList);
                        }
                    }
                }
            }
        }

        throw new ParseException("Invalid for statement", tokens.index);
    }

    /**
     * Parses a while statement from the {@code statement} rule. This method
     * should only be called if the next tokens start a while statement, aka
     * {@code WHILE}.
     */
    public Ast.Stmt.While parseWhileStatement() throws ParseException { //TODO      DONE(PASSED)
        //throw new UnsupportedOperationException();
        if(match("WHILE")){
            Ast.Expr expression = parseExpression();
            List<Ast.Stmt> stmtList = new ArrayList<Ast.Stmt>();

            if(match("DO")){
                if(match("END")){                                               //Handles 0 Do statements
                    return new Ast.Stmt.While(expression, stmtList);
                }
                else{                                                                   //Handles 1+ Do statements
                    Ast.Stmt statement = parseStatement();
                    stmtList.add(statement);
                    if (match("END")) {
                        return new Ast.Stmt.While(expression, stmtList);
                    }
                }
            }
        }

        throw new ParseException("Invalid while statement", tokens.index);
    }

    /**
     * Parses a return statement from the {@code statement} rule. This method
     * should only be called if the next tokens start a return statement, aka
     * {@code RETURN}.
     */
    public Ast.Stmt.Return parseReturnStatement() throws ParseException { //TODO        DONE(PASSED)
        //throw new UnsupportedOperationException();
        if(match("RETURN")){
            Ast.Expr expression = parseExpression();
            match(";");
            return new Ast.Stmt.Return(expression);
        }

        throw new ParseException("Invalid return statement", tokens.index);
    }

    /**
     * Parses the {@code expression} rule.
     */
    public Ast.Expr parseExpression() throws ParseException { //TODO
        //throw new UnsupportedOperationException();

        //return parseLogicalExpression();

        return parsePrimaryExpression();

    }

    /**
     * Parses the {@code logical-expression} rule.
     */
    public Ast.Expr parseLogicalExpression() throws ParseException { //TODO
        //throw new UnsupportedOperationException();
        Ast.Expr left = parseEqualityExpression();

        while (peek("AND", "OR")){
            String operator = tokens.get(0).getLiteral();
            match("AND","OR");
            Ast.Expr right = parseEqualityExpression();
            left = new Ast.Expr.Binary(operator, left, right);
        }

        return left;
    }

    /**
     * Parses the {@code equality-expression} rule.
     */
    public Ast.Expr parseEqualityExpression() throws ParseException { //TODO
        //throw new UnsupportedOperationException();
        Ast.Expr left = parseAdditiveExpression();

        while (peek(">", ">=", "<", "<=")){
            String operator = tokens.get(0).getLiteral();
            match(">", ">=", "<", "<=");
            Ast.Expr right = parseAdditiveExpression();
            left = new Ast.Expr.Binary(operator, left, right);
        }

        return left;
    }

    /**
     * Parses the {@code additive-expression} rule.
     */
    public Ast.Expr parseAdditiveExpression() throws ParseException { //TODO
        //throw new UnsupportedOperationException();
        Ast.Expr left = parseMultiplicativeExpression();

        while (peek("+", "-")){
            String operator = tokens.get(0).getLiteral();
            match("+", "-");
            Ast.Expr right = parseMultiplicativeExpression();
            left = new Ast.Expr.Binary(operator, left, right);
        }

        return left;
    }

    /**
     * Parses the {@code multiplicative-expression} rule.
     */
    public Ast.Expr parseMultiplicativeExpression() throws ParseException { //TODO
        //throw new UnsupportedOperationException();
        Ast.Expr left = parseSecondaryExpression();

        while (peek("*", "/")){
            String operator = tokens.get(0).getLiteral();
            match("*", "/");
            Ast.Expr right = parseSecondaryExpression();
            left = new Ast.Expr.Binary(operator, left, right);
        }

        return left;
    }

    /**
     * Parses the {@code secondary-expression} rule.
     */
    public Ast.Expr parseSecondaryExpression() throws ParseException { //TODO
        //throw new UnsupportedOperationException();
        List<Ast.Expr> arguments = new ArrayList<Ast.Expr>();
        Ast.Expr left = parsePrimaryExpression();
        /**
        * ('.' identifier
         *   ( '('
         *          (expression
         *              (',' expression)*
         *          )?
         *   ')' )?
         * )*
        *
        * */
        while (match(".")){
            String name = tokens.get(0).getLiteral();
            match(Token.Type.IDENTIFIER);
            if(match("(")){
                Ast.Expr expression = parseExpression();
                arguments.add(expression);
                while(match(",")){
                    expression = parseExpression();
                    arguments.add(expression);
                }
                match(")");
                return new Ast.Expr.Function(Optional.of(left),name,arguments);
            }

        }

        return left;
    }

    /**
     * Parses the {@code primary-expression} rule. This is the top-level rule
     * for expressions and includes literal values, grouping, variables, and
     * functions. It may be helpful to break these up into other methods but is
     * not strictly necessary.
     */
    public Ast.Expr parsePrimaryExpression() { //TODO                                       (PASSED: Literal)   (Failed: ....)
        if(peek(Token.Type.INTEGER)){
            return new Ast.Expr.Literal(new BigInteger(tokens.get(0).getLiteral()));
        }
        if(peek(Token.Type.DECIMAL)){
            return new Ast.Expr.Literal(new BigDecimal(tokens.get(0).getLiteral()));
        }
        if(peek(Token.Type.CHARACTER)){
            String temp = tokens.get(0).getLiteral();
            temp = temp.substring(1, temp.length() - 1);
            //temp = temp.replace("\\\\\\\\","\\\\");
            char c;
            switch (temp){
                case "\\b":
                    c = '\b';
                    break;
                case "\\n":
                    c = '\n';
                    break;
                case "\\r":
                    c = '\r';
                    break;
                case "\\t":
                    c = '\t';
                    break;
                case "\'":
                    c = '\'';
                    break;
                case "\"":
                    c = '\"';
                    break;
                case "\\":
                    c = '\\';
                    break;
                default:
                    c = temp.charAt(0);
            }
            return new Ast.Expr.Literal(new Character(c));
        }
        if(peek(Token.Type.STRING)){
            String temp = tokens.get(0).getLiteral();
            temp = temp.substring(1, temp.length() - 1);
            temp = temp.replaceAll("\\\\b","\b");
            temp = temp.replaceAll("\\\\n","\n");
            temp = temp.replaceAll("\\\\r","\r");
            temp = temp.replaceAll("\\\\t","\t");
            temp = temp.replaceAll("\\\\'","\'");
            temp = temp.replaceAll("\\\\\"","\"");
            temp = temp.replaceAll("\\\\","\\");

            return new Ast.Expr.Literal(new String(temp));
        }
        /*if(peek(Token.Type.IDENTIFIER)){
            if(tokens.get(0).getLiteral().equals("NIL")){
                return new Ast.Expr.Literal(null);
            }
            if(tokens.get(0).getLiteral().equals("TRUE")){
                return new Ast.Expr.Literal(new Boolean(true));
            }
            if(tokens.get(0).getLiteral().equals("FALSE")){
                return new Ast.Expr.Literal(new Boolean(false));
            }
        }*/
        //NOTE: Writing the literals also works
        if(match("NIL")){return new Ast.Expr.Literal(null);}
        if(match("TRUE")){return new Ast.Expr.Literal(true);}
        if(match("FALSE")){return new Ast.Expr.Literal(false);}

        if(match("(")){
            Ast.Expr expression = parseExpression();
            match(")");
            return new Ast.Expr.Group(expression);
        }

        if(peek(Token.Type.IDENTIFIER)){
            List<Ast.Expr> arguments = new ArrayList<Ast.Expr>();
            String name = tokens.get(0).getLiteral();
            match(Token.Type.IDENTIFIER);
            if(match("(")){
                if(match(")")){      //Handles zero arguments
                    return new Ast.Expr.Function(Optional.empty(),name,arguments);
                }

                Ast.Expr expression = parseExpression();
                arguments.add(expression);
                while(match(",")){
                    expression = parseExpression();
                    arguments.add(expression);
                }
                match(")");
                return new Ast.Expr.Function(Optional.empty(),name,arguments);
            }
            else{
                return new Ast.Expr.Access(Optional.empty(),name); //Fixed test case #2 of FunctionExpression  //Doesn't work for #3
            }
        }

        return new Ast.Expr.Literal(tokens.get(0));
    }

    /**
     * As in the lexer, returns {@code true} if the current sequence of tokens
     * matches the given patterns. Unlike the lexer, the pattern is not a regex;
     * instead it is either a {@link Token.Type}, which matches if the token's
     * type is the same, or a {@link String}, which matches if the token's
     * literal is the same.
     *
     * In other words, {@code Token(IDENTIFIER, "literal")} is matched by both
     * {@code peek(Token.Type.IDENTIFIER)} and {@code peek("literal")}.
     */
    private boolean peek(Object... patterns){
        for(int i = 0; i < patterns.length; i++){
            if(!tokens.has(i)){
                return false;
            } else if(patterns[i] instanceof Token.Type){
                if(patterns[i] != tokens.get(i).getType()){
                    return false;
                }
            } else if(patterns[i] instanceof String){
                if(!patterns[i].equals(tokens.get(i).getLiteral())){
                    return false;
                }
            } else {
                throw new AssertionError("Invalid pattern object: " + patterns[i].getClass());
            }
        }
        return true;
    }

    /**
     * As in the lexer, returns {@code true} if {@link #peek(Object...)} is true
     * and advances the token stream.
     */
    private boolean match(Object... patterns) {
        boolean peek = peek(patterns);
        if(peek){
            for(int i = 0; i < patterns.length; i++){
                tokens.advance();
            }
        }
        return peek;
    }

    private static final class TokenStream {

        private final List<Token> tokens;
        private int index = 0;

        private TokenStream(List<Token> tokens) {
            this.tokens = tokens;
        }

        /**
         * Returns true if there is a token at index + offset.
         */
        public boolean has(int offset) {
            return index + offset < tokens.size();
        }

        /**
         * Gets the token at index + offset.
         */
        public Token get(int offset) {
            return tokens.get(index + offset);
        }

        /**
         * Advances to the next token, incrementing the index.
         */
        public void advance() {
            index++;
        }

    }

}
