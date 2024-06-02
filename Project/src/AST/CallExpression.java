package AST;

import Compilation.Procedure;
import Context.BYTECODE_CONTEXT;
import Context.COMPILATION_CONTEXT;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Visitor.IExpressionVisitor;
import Visitor.ISemanticCheckVisitor;

import java.util.ArrayList;

public class CallExpression extends Expression{
    Procedure mProc;
    ArrayList mActuals;
    String procName;
    boolean isrecurse;

    @Override
    public TYPE_INFO getType() {
        return type;
    }

    @Override
    public void setType(TYPE_INFO type) {
        this.type = type;
    }

    TYPE_INFO type;

    public CallExpression(Procedure proc, ArrayList actuals)
    {
        mProc = proc;
        mActuals = actuals;
    }

    public CallExpression(String name, boolean recurse, ArrayList actuals)
    {
        procName = name;
        if (recurse)
            isrecurse = true;

        mActuals = actuals;
        //
        // For a recursive call Procedure Address will be null
        // During the interpretation time we will resolve the
        // call by look up...
        //    mProc = cont.GetProgram().Find(procname);
        // This is a hack for implementing one pass compiler
        mProc = null;
    }

    @Override
    public SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT context) throws Exception {
        if (mProc == null) {
            if ( context instanceof BYTECODE_CONTEXT) {
                mProc = ((BYTECODE_CONTEXT) context).getModule().find(procName);
                if (mProc == null) { throw  new Exception("Hello World..");}

            } else {
                mProc = context.getProgram().find(procName);
                if (mProc == null) {
                    throw new Exception("Hello World..");
                }
            }
        }

        return visitor.visit(this, context);
    }
    @Override
    public TYPE_INFO accept(ISemanticCheckVisitor visitor, COMPILATION_CONTEXT context) throws Exception {
        return visitor.visit(this, context);
    }

    /*@Override
    public TYPE_INFO typeCheck(COMPILATION_CONTEXT context) throws Exception {
        if (mProc != null)
        {
            type = mProc.TypeCheck(context);

        }

        return type;
    }*/

    public Procedure getProcedure() {
        return mProc;
    }

    public ArrayList<Expression> getAcutalParameterExpressions() {
        return mActuals;
    }

    public String getProcedureName() {
        return procName;
    }

    public void setProcedure(Procedure procedure) {
        this.mProc = procedure;

    }
}
