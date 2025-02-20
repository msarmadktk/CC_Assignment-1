public class Token {
    public enum TokenType {
        KEYWORD,
        IDENTIFIER,
        NUMBER,
        OPERATOR,
        SEPARATOR,
        COMMENT,
        STRING,
        CHARACTER,
        BOOLEAN_LITERAL,
        UNKNOWN
    }

    private TokenType type;
    private String lexeme;
    private int line;
    private int column;

    public Token(TokenType type, String lexeme, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.format("Token[%s, '%s', line=%d, col=%d]",
                type, lexeme, line, column);
    }
}
