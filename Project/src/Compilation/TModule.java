package Compilation;

import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Visitor.IExpressionVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TModule extends CompilationUnit{
    private String name;
    private ArrayList procedures;
    private ArrayList compiledProcs = null;

    public TModule(String moduleName,ArrayList procs) {
        this.name = moduleName;
        procedures = procs;
    }
    // private ExeGenerator _exe = null;

    public boolean createExecutable(String name)
    {

        // exe = new ExeGenerator(this,name);
        // Compile The module...
        // compile(null);
        // Save the Executable...
        // exe.Save();
        return true;
    }

    public boolean Compile( )
    {
        return true;

    }


    @Override
    public SymbolInfo Execute(IExpressionVisitor visitor, RUNTIME_CONTEXT cont) throws Exception {
        visitor.visit(this, cont);
        return null;

    }

    public String getName() {
        return name;
    }

    public ArrayList  getProcedures() {
        return procedures;
    }

    public Procedure find(String  procedureName)
    {
        for (Object p : procedures)
        {
            Procedure procedure =(Procedure)p;
            String pname = procedure.mName;

            if (pname.toUpperCase().compareTo(procedureName.toUpperCase()) == 0)
                return procedure;

        }

        return null;

    }
}
