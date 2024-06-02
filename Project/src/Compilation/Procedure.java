package Compilation;

import AST.Expression;
import ASTStatements.Statement;
import Context.COMPILATION_CONTEXT;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Scope.SymbolTable;
import Visitor.IExpressionVisitor;
import Visitor.ISemanticCheckVisitor;

import java.util.ArrayList;

public class Procedure extends PROC {
    public String mName;
    public ArrayList mFormals = null;
    public ArrayList mStatements = null;
    public SymbolTable mLocals = null;
    public TYPE_INFO type = TYPE_INFO.TYPE_ILLEGAL;
    private int index;

    public Procedure(String name,
                     ArrayList formals,
                     ArrayList stats,
                     SymbolTable locals,
                     TYPE_INFO type, int index)
    {
        mName = name;
        mFormals = formals;
        mStatements = stats;
        mLocals = locals;
        this.type = type;
        this.index = index;
    }

    public ArrayList<SymbolInfo> getmFormals() {
        return mFormals;
    }

    public void setmFormals(ArrayList mFormals) {
        this.mFormals = mFormals;
    }

    public TYPE_INFO getType() {
        return type;
    }

    public void setType(TYPE_INFO type) {
        this.type = type;
    }

    public TYPE_INFO accept(ISemanticCheckVisitor visitor, COMPILATION_CONTEXT context)
    {
        return visitor.visit(this, context);
    }


    public boolean Compile()
    {

        for (Object e1  :  mStatements)
        {
            Statement stmt = (Statement) e1;
            //stmt.execute();
        }

        return true;

    }

    @Override
    public SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT context,
                             ArrayList<Expression> actualParameters) throws Exception {

        /*for (Object stmt : mStatements) {
            Statement s = (Statement) stmt;
            s.accept(visitor, context);
        }*/

        return visitor.visit(this, context, actualParameters);

    }
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
