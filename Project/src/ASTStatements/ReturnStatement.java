package ASTStatements;

import AST.Expression;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Visitor.IExpressionVisitor;

public class ReturnStatement extends Statement {
    private Expression exp;
    private SymbolInfo inf = null;

    /// <summary>
    ///
    /// </summary>
    /// <param name="e1"></param>
    public ReturnStatement(Expression e1)
    {
        exp = e1;
    }


    @Override
    public SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT context) throws Exception {
        inf = (exp == null) ? null : visitor.visit(this, context);
        return inf;
    }

    public Expression getExpression() {
        return this.exp;
    }
}
