package Function;

import Scope.SymbolTable;

public class FRAME {
    private SymbolTable table;
    public FRAME()
    {
        table = new SymbolTable();
    }

    public SymbolTable getTABLE() {
            return table;
    }
}
