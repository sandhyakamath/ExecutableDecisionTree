package Context;

import Scope.SymbolTable;

public class COMPILATION_CONTEXT {
    private SymbolTable table;

    public COMPILATION_CONTEXT() {
        table = new SymbolTable();
    }

    public SymbolTable getTable() {
        return table;
    }

    public void setTable(SymbolTable table) {
        this.table = table;
    }

}
