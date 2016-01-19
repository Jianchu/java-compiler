package scanner;


public class Token {

    private final String lexeme;
    private TokenType tokenType;

    public Token(String lexeme, TokenType tokenType) {
        this.lexeme = lexeme;
        this.tokenType = tokenType;
    }

    // May be used for replacing id with keyword
    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public String getLexeme() {
        return this.lexeme;
    }

    public TokenType getTokenType() {
        return this.tokenType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<");
        sb.append(this.lexeme);
        sb.append(", ");
        sb.append(tokenType.toString());
        sb.append(">");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        final Token token = (Token) obj;
        return this.lexeme.equals(token.lexeme)
                && this.tokenType.equals(token.tokenType);
    }
}
