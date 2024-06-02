package AST.Constant;

import AST.Expression;
import Context.COMPILATION_CONTEXT;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Visitor.IExpressionVisitor;
import Visitor.ISemanticCheckVisitor;

public class BooleanConstant extends Expression {
    private SymbolInfo info;

    public BooleanConstant(boolean pvalue)
    {
        info = new SymbolInfo();
        info.symbolName = null;
        info.bolValue = pvalue;
        info.type = TYPE_INFO.TYPE_BOOL;
        this.type = TYPE_INFO.TYPE_BOOL;
    }
    public SymbolInfo getInfo() {
        return info;
    }

    public void setInfo(SymbolInfo info) {
        this.info = info;
    }


    @Override
    public SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT context) throws Exception {
        return visitor.visit(this, context);
    }

    @Override
    public TYPE_INFO accept(ISemanticCheckVisitor visitor, COMPILATION_CONTEXT context) throws Exception {
        return visitor.visit(this, context);
    }

    /*@Override
    public TYPE_INFO typeCheck(COMPILATION_CONTEXT context) throws Exception {
        return info.type;
    }*/
}
