package Context;

///////////////////////////////////////////
// Class that stores the stack frame
//

import Compilation.TModule;
import Scope.SymbolTable;

public class RUNTIME_CONTEXT {
    StringBuilder str = new StringBuilder();
    private SymbolTable table;
    private TModule prog = null;

    public RUNTIME_CONTEXT() {
        table = new SymbolTable();
    }
    public boolean SetModule(TModule mp) {
        if (prog == null) {
            prog = mp;
            return true;
        }
        return false;
    }
    public RUNTIME_CONTEXT(TModule pgrm)
    {
        table = new SymbolTable();
        prog = pgrm;
    }

    public TModule getProgram()
    {
        return prog;
    }

    public SymbolTable getTable() {
        return table;
    }

    public void setTable(SymbolTable table) {
        this.table = table;
    }
    public void appendString(String value) {
        str.append(value);
    }

    public StringBuilder getStr() {
        return str;
    }
}
