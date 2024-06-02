package ASTStatements;

import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Visitor.IExpressionVisitor;

public abstract class Statement {
    public abstract SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT context) throws Exception;
}
