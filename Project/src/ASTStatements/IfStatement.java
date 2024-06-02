package ASTStatements;

import AST.Expression;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Visitor.IExpressionVisitor;

import java.util.ArrayList;

public class IfStatement extends Statement {
    private Expression cond;
    private ArrayList stmnts;
    private ArrayList elsePart;

    public IfStatement(Expression c, ArrayList s, ArrayList e)
    {
        cond = c;
        stmnts = s;
        elsePart = e;
    }

    public Expression getCond() {
        return cond;
    }

    public void setCond(Expression cond) {
        this.cond = cond;
    }

    public ArrayList getStmnts() {
        return stmnts;
    }

    public void setStmnts(ArrayList stmnts) {
        this.stmnts = stmnts;
    }

    public ArrayList getElsePart() {
        return elsePart;
    }

    public void setElsePart(ArrayList elsePart) {
        this.elsePart = elsePart;
    }

    @Override
    public SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT context) throws Exception {

        /*SymbolInfo mCond = cond.accept(visitor, context);

        if (mCond == null || mCond.type != TYPE_INFO.TYPE_BOOL)
            return null;

        if (mCond.bolValue == true) {
            for (Object s : stmnts) {
                Statement stmt = (Statement) s;
                stmt.accept(visitor, context);

            }
        } else if (elsePart != null) {
            for (Object s : stmnts) {
                Statement stmt = (Statement) s;
                stmt.accept(visitor, context);

            }

        }*/

        return visitor.visit(this, context);


    }




}

