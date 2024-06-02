package Builder;

import AST.Expression;
import ASTStatements.Statement;
import Compilation.Procedure;
import Context.COMPILATION_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Scope.SymbolTable;
import Visitor.ISemanticCheckVisitor;

import java.util.ArrayList;

public class ProcedureBuilder extends AbstractBuilder{
    private String procName;
    COMPILATION_CONTEXT context;
    ISemanticCheckVisitor visitor;

    ArrayList mFormals = new ArrayList();
    ArrayList mStatements = new ArrayList();
    private TYPE_INFO returnType;
    private int index;

    public TYPE_INFO getInfo() {
        return info;
    }

    public void setInfo(TYPE_INFO info) {
        this.info = info;
    }

    TYPE_INFO info = TYPE_INFO.TYPE_ILLEGAL;

    public ProcedureBuilder(String name, COMPILATION_CONTEXT _ctx, ISemanticCheckVisitor visitor)
    {
        context = _ctx;
        procName = name;
        this.visitor = visitor;
    }

    public boolean addLocal(SymbolInfo info)
    {
        context.getTable().add(info);
        return true;
    }

    public boolean addFormal(SymbolInfo info)
    {
        mFormals.add(info);
        return true;
    }

    public TYPE_INFO accept(Expression e) throws Exception {
          return e.accept(visitor, context);

    }
    public void addStatement(Statement st)
    {
        mStatements.add(st);
    }

    public SymbolInfo getSymbol(String strname)
    {
        return context.getTable().get(strname);
    }

    public boolean checkProto(String name)
    {
        return true;
    }

    public String getProcName() {
        return procName;
    }

    public void setProcName(String procName) {
        this.procName = procName;
    }

    public Procedure getProcedure()
    {
        return new Procedure(procName, mFormals,
                mStatements, context.getTable(), returnType, index);
    }

    public SymbolTable getTable()
    {
        return context.getTable();
    }

    public COMPILATION_CONTEXT getContext()
    {
       return context;
    }
    public ISemanticCheckVisitor getVisitor() {
        return visitor;
    }

    public void setVisitor(ISemanticCheckVisitor visitor) {
        this.visitor = visitor;
    }

    public void setStatements(ArrayList<Statement> statements) {
        this.mStatements = statements;
    }

    public void setFormalParameters(ArrayList<SymbolInfo> fParameters) {
        for (SymbolInfo symbol : fParameters) {
            this.context.getTable().add(symbol);
        }
        this.mFormals = fParameters;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setReturnType(TYPE_INFO type) {
        this.returnType = type;
    }

    public TYPE_INFO getReturnType() {
        return this.returnType;
    }

}
