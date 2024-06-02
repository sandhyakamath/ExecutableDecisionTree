package Lexer;

public class ValueTable {
    public TOKEN tok;          // Token id
    public String Value;       // Token string
    public ValueTable(TOKEN tok, String Value)
    {
        this.tok = tok;
        this.Value = Value;

    }
}
