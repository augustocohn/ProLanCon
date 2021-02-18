package plc.project;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

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
    public Ast.Stmt parseStatement() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses a declaration statement from the {@code statement} rule. This
     * method should only be called if the next tokens start a declaration
     * statement, aka {@code LET}.
     */
    public Ast.Stmt.Declaration parseDeclarationStatement() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses an if statement from the {@code statement} rule. This method
     * should only be called if the next tokens start an if statement, aka
     * {@code IF}.
     */
    public Ast.Stmt.If parseIfStatement() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses a for statement from the {@code statement} rule. This method
     * should only be called if the next tokens start a for statement, aka
     * {@code FOR}.
     */
    public Ast.Stmt.For parseForStatement() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses a while statement from the {@code statement} rule. This method
     * should only be called if the next tokens start a while statement, aka
     * {@code WHILE}.
     */
    public Ast.Stmt.While parseWhileStatement() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses a return statement from the {@code statement} rule. This method
     * should only be called if the next tokens start a return statement, aka
     * {@code RETURN}.
     */
    public Ast.Stmt.Return parseReturnStatement() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses the {@code expression} rule.
     */
    public Ast.Expr parseExpression() throws ParseException { //TODO
        //throw new UnsupportedOperationException();
        //if(peek(Token.Type.INTEGER)||peek(Token.Type.DECIMAL) || peek(Token.Type.CHARACTER) || peek(Token.Type.STRING)){
            return parsePrimaryExpression();

    }

    /**
     * Parses the {@code logical-expression} rule.
     */
    public Ast.Expr parseLogicalExpression() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses the {@code equality-expression} rule.
     */
    public Ast.Expr parseEqualityExpression() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses the {@code additive-expression} rule.
     */
    public Ast.Expr parseAdditiveExpression() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses the {@code multiplicative-expression} rule.
     */
    public Ast.Expr parseMultiplicativeExpression() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses the {@code secondary-expression} rule.
     */
    public Ast.Expr parseSecondaryExpression() throws ParseException { //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Parses the {@code primary-expression} rule. This is the top-level rule
     * for expressions and includes literal values, grouping, variables, and
     * functions. It may be helpful to break these up into other methods but is
     * not strictly necessary.
     */
    public Ast.Expr parsePrimaryExpression() { //TODO
        if(peek(Token.Type.INTEGER)){
            return new Ast.Expr.Literal(new BigInteger(tokens.get(0).getLiteral()));
        }
        if(peek(Token.Type.DECIMAL)){
            return new Ast.Expr.Literal(new BigDecimal(tokens.get(0).getLiteral()));
        }
        if(peek(Token.Type.CHARACTER)){
            String temp = tokens.get(0).getLiteral();
            temp = temp.substring(1, temp.length() - 1);
            temp = temp.replaceAll("\\\\\\\\","\\\\");
            char c;
            switch (temp){
                case "\b":
                    c = '\b';
                    break;
                case "\n":
                    c = '\n';
                    break;
                case "\r":
                    c = '\r';
                    break;
                case "\t":
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
            System.out.println(c);
            return new Ast.Expr.Literal(new Character(c));
        }
        if(peek(Token.Type.STRING)){
            String temp = tokens.get(0).getLiteral();
            temp = temp.substring(1, temp.length() - 1);
            temp = temp.replace("\\\\\\\\","\\");
            return new Ast.Expr.Literal(new String(temp));
        }
        if(peek(Token.Type.IDENTIFIER)){
            if(tokens.get(0).getLiteral().equals("NIL")){
                return new Ast.Expr.Literal(null);
            }
            if(tokens.get(0).getLiteral().equals("TRUE")){
                return new Ast.Expr.Literal(new Boolean(true));
            }
            if(tokens.get(0).getLiteral().equals("FALSE")){
                return new Ast.Expr.Literal(new Boolean(false));
            }
        }

        /*if(peek(Token.Type.OPERATOR)){
            if (match(tokens.get(0).getLiteral().equals("("))){
                Ast.Expr temp = parseExpression();
                if (match(tokens.get(0).getLiteral().equals(")"))){

                }
            }
        }*/
        return new Ast.Expr.Literal("DONE");
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
