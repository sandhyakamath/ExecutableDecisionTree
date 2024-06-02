package AST;

public enum OPERATOR {
    ILLEGAL(-1),
    PLUS(0),
    MINUS(1),
    DIV(2),
    MUL(3);

    private int op;
    private OPERATOR(int op) {
        this.op = op;
    }
    public int getOperator() {
        return this.op;
    }
}
