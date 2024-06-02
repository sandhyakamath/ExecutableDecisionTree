package Builder;

import Compilation.Procedure;
import Compilation.TModule;
import Function.FUNCTION_INFO;
import Lexer.TYPE_INFO;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TModuleBuilder {
    private String moduleName;
    private ArrayList procs; // holds procedures
    private ArrayList protos=null; // holds prototypes

    /**
     *  Ctor
     */
    public TModuleBuilder() {
        procs = new ArrayList();
        protos = new ArrayList();
    }

    public TModuleBuilder(String moduleName) {
        this.moduleName = moduleName.toUpperCase();
        procs = new ArrayList();
        protos = new ArrayList();
    }

    /**
     *
     * @param p
     * @return
     */
    public boolean add(Procedure p) {
        procs.add(p);
        return true;
    }

    public TModule getProgram() {
        return new TModule(moduleName, procs);
    }

    public Procedure getProc(String name) {
        for (Object p : procs) {
            Procedure procedure = (Procedure) p;
            if (procedure.mName.equals(name)) {
                return procedure;
            }

        }

        return null;

    }

    public boolean IsFunction(String name)
    {
        for (Object fp : protos)
        {
            FUNCTION_INFO fpinfo = (FUNCTION_INFO) fp;
            if (fpinfo.name.equals(name))
            {
                return true;
            }

        }

        return false;

    }

    public void addFunctionProtoType(String name, TYPE_INFO ret_type,
                                     ArrayList type_infos)
    {
        FUNCTION_INFO info = new FUNCTION_INFO(name, ret_type, type_infos);
        protos.add(info);
    }

    public boolean checkFunctionProtoType(String name, TYPE_INFO retType,
                                       ArrayList infos)
    {
        for (Object fp : protos) {
            FUNCTION_INFO fpinfo = (FUNCTION_INFO) fp;
            if (fpinfo.name.equals(name))
            {
                if (fpinfo.retValue == retType)
                {
                    if (infos.size() == fpinfo.typeInfo.size())
                    {
                        for (int i = 0; i < infos.size(); ++i)
                        {
                            TYPE_INFO a = (TYPE_INFO)infos.get(i);
                            TYPE_INFO b = (TYPE_INFO)infos.get(i);

                            if (a != b)
                                return false;

                        }

                        return true;

                    }


                }

            }

        }

        return false;

    }

}
