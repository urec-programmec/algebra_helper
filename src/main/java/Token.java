public class Token {
    private TokenType type;

    private String contains;

    private int position;

    public Token(TokenType type, String contains, int position) {
        this.type = type;
        this.contains = contains;
        this.position = position;
    }

    public TokenType getType() {
        return type;
    }

    public String getContains() {
        return contains;
    }

    public void setContains(String contains) {
        this.contains = contains;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
