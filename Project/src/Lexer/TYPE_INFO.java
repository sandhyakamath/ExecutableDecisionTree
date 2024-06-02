package Lexer;

public enum TYPE_INFO {

        TYPE_ILLEGAL(-1), // NOT A TYPE
        TYPE_NUMERIC(0),      // IEEE Double precision floating point
        TYPE_BOOL(1),         // Boolean Data type
        TYPE_STRING(2),      // String data type
        TYPE_INTEGER(3),  // Integer data type
      TYPE_VOID(4);
    private int info;

    private TYPE_INFO(int info) {
        this.info = info;
    }

    public int getTypeInfo() {
        return this.info;
    }


}
