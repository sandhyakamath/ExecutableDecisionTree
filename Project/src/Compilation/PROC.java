package Compilation;

import AST.Expression;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Visitor.IExpressionVisitor;

import java.util.ArrayList;

public abstract class PROC {
    public abstract SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT cont,
                                      ArrayList<Expression> actualParameters) throws Exception;
    // public abstract boolean Compile(DNET_EXECUTABLE_GENERATION_CONTEXT cont);
}
