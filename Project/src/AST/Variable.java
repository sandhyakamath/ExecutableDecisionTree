package AST;

import Context.COMPILATION_CONTEXT;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Visitor.IExpressionVisitor;
import Visitor.ISemanticCheckVisitor;

public class Variable extends Expression{
    private String varName;  // Var name

    TYPE_INFO type; // variable type

    public Variable(SymbolInfo info)
    {
        varName = info.symbolName;
        type = info.type;

    }
    public Variable(COMPILATION_CONTEXT st, String name, double value)
    {
        SymbolInfo s = new SymbolInfo();
        s.symbolName = name;
        s.type = TYPE_INFO.TYPE_NUMERIC;
        s.dblValue = value;
        st.getTable().add(s);
        varName = name;
    }

    public Variable(COMPILATION_CONTEXT st, String name, boolean value)
    {
        SymbolInfo s = new SymbolInfo();
        s.symbolName = name;
        s.type = TYPE_INFO.TYPE_BOOL;
        s.bolValue = value;
        st.getTable().add(s);
        varName = name;
    }

    public Variable(COMPILATION_CONTEXT st, String name, String value)
    {
        SymbolInfo s = new SymbolInfo();
        s.symbolName = name;
        s.type = TYPE_INFO.TYPE_STRING;
        s.strValue = value;
        st.getTable().add(s);
        varName = name;
    }

    @Override
    public SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT context) throws Exception {
        /*if (context.getTable() == null) {
            return null;
        } else
        {
            SymbolInfo a = context.getTable().get(varName);
            return a;
        }*/
        return visitor.visit(this, context);
    }

    @Override
    public TYPE_INFO accept(ISemanticCheckVisitor visitor, COMPILATION_CONTEXT context) throws Exception {
        return visitor.visit(this, context);
    }

    /*@Override
    public TYPE_INFO typeCheck(ITypeCheckVisitor visitor, COMPILATION_CONTEXT context) {
        if (context.getTable() != null) {
            SymbolInfo a = context.getTable().get(varName);
            if (a != null) {
                type = a.type;
                return type;

            }

        }
        return TYPE_INFO.TYPE_ILLEGAL;
    }*/


    public String getName() {
        return varName;
    }

    public void setName(String varName) {
        this.varName = varName;
    }

    public TYPE_INFO getType() {
        return type;
    }

}
