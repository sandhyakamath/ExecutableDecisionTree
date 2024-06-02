package AST;

////////////////////////////////////////////////////////
// Expression is what evaluates for its value
//


import Context.COMPILATION_CONTEXT;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Visitor.IExpressionVisitor;
import Visitor.ISemanticCheckVisitor;

public abstract class Expression {
    public abstract SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT context) throws Exception;
    public abstract TYPE_INFO accept(ISemanticCheckVisitor visitor, COMPILATION_CONTEXT context) throws Exception;

    // public abstract TYPE_INFO typeCheck(COMPILATION_CONTEXT context) throws Exception;

    protected TYPE_INFO type;

    public TYPE_INFO getType() {
        return type;
    }

    public void setType(TYPE_INFO type) {
        this.type = type;
    }

}