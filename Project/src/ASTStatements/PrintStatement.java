package ASTStatements;

import AST.Expression;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Visitor.IExpressionVisitor;

public class PrintStatement extends Statement{

    private Expression exp;

    public PrintStatement(Expression exp) {
        this.exp = exp;
    }

    public Expression getExp() {
        return exp;
    }

    @Override
    public SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT context) throws Exception {
        /*SymbolInfo val = exp.accept(visitor, context);
        System.out.print((val.type == TYPE_INFO.TYPE_NUMERIC  ) ? String.valueOf(val.dblValue) :
                ( val.type == TYPE_INFO.TYPE_STRING ) ? val.strValue : val.bolValue ? "TRUE" : "FALSE" );
        return null;*/
        visitor.visit(this, context);
        return null;
    }
}
