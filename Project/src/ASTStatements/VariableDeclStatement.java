package ASTStatements;

import AST.Variable;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Visitor.IExpressionVisitor;

public class VariableDeclStatement extends Statement{
    SymbolInfo info = null;
    Variable var = null;
    public VariableDeclStatement(SymbolInfo info)
    {
        this.info = info;
    }

    public Variable getVar() {
        return var;
    }

    public void setVar(Variable var) {
        this.var = var;
    }
    public SymbolInfo getInfo() {
        return info;
    }

    public void setInfo(SymbolInfo info) {
        this.info = info;
    }


    @Override
    public SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT context) throws Exception {
        /*context.getTable().add(info);
        var = new Variable(info);
        return null;*/
        var = new Variable(info);
        visitor.visit(this, context);
        return null;
    }
}
