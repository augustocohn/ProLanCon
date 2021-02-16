package plc.project;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The lexer works through three main functions:
 *
 *  - {@link #lex()}, which repeatedly calls lexToken() and skips whitespace
 *  - {@link #lexToken()}, which lexes the next token
 *  - {@link CharStream}, which manages the state of the lexer and literals
 *
 * If the lexer fails to parse something (such as an unterminated string) you
 * should throw a {@link ParseException} with an index at the character which is
 * invalid or missing.
 *
 * The {@link #peek(String...)} and {@link #match(String...)} functions are
 * helpers you need to use, they will make the implementation a lot easier.
 */
public final class Lexer {

    private final CharStream chars;

    public Lexer(String input) {
        chars = new CharStream(input);
    }

    /**
     * Repeatedly lexes the input using {@link #lexToken()}, also skipping over
     * whitespace where appropriate.
     */
    public List<Token> lex() { //TODO
        List<Token> tokens = new ArrayList<Token>();
        while(chars.has(0)){
            if(peek("[ \b\n\r\t]")){    //Checks to see if the input is a whitespace character
                chars.advance();
                chars.skip();
            }
            else{       //If input isn't a whitespace character, it will add a token of it's determined type
                tokens.add(lexToken());
            }
        }
        return tokens;
    }

    /**
     * This method determines the type of the next token, delegating to the
     * appropriate lex method. As such, it is best for this method to not change
     * the state of the char stream (thus, use peek not match).
     *
     * The next character should start a valid token since whitespace is handled
     * by {@link #lex()}
     */
    public Token lexToken() { //TODO
        // After checking the first token and jumping to the respective method, keep checking that token type

        if(peek("[A-Za-z_]")){
            return lexIdentifier();
        }
        else if(peek("[+-]","[0-9]") | peek("[0-9]")){
            return lexNumber();
        }
        else if(peek("\'")){
            return lexCharacter();
        }
        else if(peek("\"")){
            return lexString();
        }
        else{
            return lexOperator(); //Not a letter, digit, ', " , or \
        }
    }

    public Token lexIdentifier() { //TODO
        if(match("[A-Za-z_]")){
            while(match("[A-Za-z0-9_-]"));
            return chars.emit(Token.Type.IDENTIFIER);
        }
        throw new ParseException("Not a valid identifier", chars.index);
    }

    public Token lexNumber() { //TODO
        //Need to consider different paths for Integers and Decimals

        if(match("[+-]","[0-9]") | match("[0-9]")){
            while(match("[0-9]"));

            //Decimal
            if(peek("\\.","[0-9]")){
                match("\\.");
                while(match("[0-9]"));
                return chars.emit(Token.Type.DECIMAL);
            }

            //Integer
            return chars.emit(Token.Type.INTEGER);
        }
        throw new ParseException("Not a valid number", chars.index);
    }

    public Token lexCharacter() { //TODO
        if(match("\'")){
            if(peek("\'")){     //Handles empty characters; False
                throw new ParseException("Not a valid character", chars.index);
            }
            else if(peek("\\\\")){
                lexEscape();
            }
            else if(match("[^\'\\n\\r\\\\]")) {}
        }
        if(match("\'")){
            return chars.emit(Token.Type.CHARACTER);
        }
        throw new ParseException("Not a valid character", chars.index);
    }

    public Token lexString() { //TODO
        if(match("\"")){
            while(peek("[^\"\n\r]")){
                if(peek("\\\\")){
                    lexEscape();
                }
                else if(peek("[^\\n\\r\\\\]")){
                    match("[^\"\\n\\r\\\\]");
                }
            }
            if(match("\"")){
                return chars.emit(Token.Type.STRING);
            }
        }
        throw new ParseException("Not a valid string", chars.index);
    }

    public void lexEscape() { //TODO
        if(match("\\\\","[bnrt\'\"\\\\]")){}
        else {
            throw new ParseException("Not a valid escape sequence", chars.index + 1);
        }
    }

    public Token lexOperator() { //TODO
        if(match("[<>!=]","=")){
            return chars.emit(Token.Type.OPERATOR);
        }
        else if(match("[<>!=]")){
            return chars.emit(Token.Type.OPERATOR);
        }
        else if(match("[^\\w\'\"\\\\]")){
            return chars.emit(Token.Type.OPERATOR);
        }
        throw new ParseException("Not a valid operator", chars.index);
    }

    /**
     * Returns true if the next sequence of characters match the given patterns,
     * which should be a regex. For example, {@code peek("a", "b", "c")} would
     * return true if the next characters are {@code 'a', 'b', 'c'}.
     */
    public boolean peek(String... patterns) { //TODO (in lecture)
        for(int i = 0; i < patterns.length; i++){
            if (!chars.has(i) || !String.valueOf(chars.get(i)).matches(patterns[i])){
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true in the same way as {@link #peek(String...)}, but also
     * advances the character stream past all matched characters if peek returns
     * true. Hint - it's easiest to have this method simply call peek.
     */
    public boolean match(String... patterns) { //TODO (in lecture)
        boolean peek = peek(patterns);

        if(peek){
            for(int i = 0; i < patterns.length; i++){
                chars.advance();
            }
        }
        return peek;
    }

    /**
     * A helper class maintaining the input string, current index of the char
     * stream, and the current length of the token being matched.
     *
     * You should rely on peek/match for state management in nearly all cases.
     * The only field you need to access is {@link #index} for any {@link
     * ParseException} which is thrown.
     */
    public static final class CharStream {

        private final String input;
        private int index = 0;
        private int length = 0;

        public CharStream(String input) {
            this.input = input;
        }

        public boolean has(int offset) {
            return index + offset < input.length();
        }

        public char get(int offset) {
            return input.charAt(index + offset);
        }

        public void advance() {
            index++;
            length++;
        }

        public void skip() {
            length = 0;
        }

        public Token emit(Token.Type type) {
            int start = index - length;
            skip();
            return new Token(type, input.substring(start, index), start);
        }

    }

}
