package Lexer;


public class SymbolInfo {
        public String symbolName;   // Symbol Name
        public TYPE_INFO type;      // Data type
        public String strValue;      // memory to hold string
        public double dblValue;      // memory to hold double
        public boolean bolValue;      // memory to hold boolean
        public int intValue;      // memory to hold boolean
        public int index;
        // to check for runtime error
        public boolean isValueNull = true;

        public SymbolInfo() {

        }

        public SymbolInfo (String name, TYPE_INFO type) {
                symbolName = name;
                this.type = type;

        }

}
