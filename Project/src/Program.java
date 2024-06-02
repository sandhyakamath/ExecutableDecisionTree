import Compilation.TModule;
import Context.BYTECODE_CONTEXT;
import Context.RUNTIME_CONTEXT;
import Parser.RDParser;
import Visitor.BCGeneratorVisitor;
import Visitor.CGeneratorVisitor;
import Visitor.IExpressionVisitor;
import Visitor.TreeEvaluatorVisitor;

import java.io.*;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import java.util.Iterator;
import java.util.Map;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class ScriptingInterpreter {
    private String _filename;
    public ScriptingInterpreter(String filename) {
        _filename = filename;
    }
    private PythonInterpreter createInterpreter(Map<String, Object>  vars) {
        PythonInterpreter pi = new PythonInterpreter();
        Iterator<Map.Entry<String,Object>> it =vars.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String,Object> ent = it.next();
            pi.set(ent.getKey(), ent.getValue());
        }
        return pi;
    }

    public boolean Execute(Map<String, Object>  vars) throws Exception{
        PythonInterpreter ps = createInterpreter(vars);

       // ps.execfile(Thread.currentThread().getContextClassLoader().getResourceAsStream(_filename));4
        InputStream is = null;
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(_filename);
        if ( is == null) {
            System.out.println("Warning....Could Not Load ResourceAs Strieam");

        }
        is = new FileInputStream(_filename);
        if ( is == null) {
            System.out.println("Could not load File");
            return false;
        }
        try {
            ps.execfile(is);
        }
        catch(Exception e) {
            System.out.println("Warning....................Could not interpret");
            e.printStackTrace();
            return false;
        }
        PyString ret = (PyString) ps.get("RET_VAL");
        String m = ret.asString();
        vars.put("RET_CODE",ps.get("RET_CODE").asString());
        return (m == "FALSE") ? false : true;

    }

    public boolean Execute() {
        PythonInterpreter ps = new PythonInterpreter();
        ps.execfile(Thread.currentThread().getContextClassLoader().getResourceAsStream(_filename));
        return true;
    }
}

//------------------ Main Program
public class Program {
    public static String GetCodeTemplate(String file) throws Exception {
       InputStream fs =  Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
       if (fs == null ) {
           fs = new FileInputStream(file);
           if ( fs == null) {
               System.out.println("Could not load Code Template File");
               return null;
           }
       }
       String ret_value = new String( fs.readAllBytes());
       return ret_value;



    }
    public static String LoadList(String csv ) throws Exception{
        String retval = "["+"\n";
        try {
            List<String> allLines = Files.readAllLines(Paths.get(csv));
            int count = allLines.size();
            int i=0;
            for (String line : allLines) {
                if ( i != count -1 )
                    retval = retval + "[" + line + "],\n";
                else
                    retval = retval + "[" + line + "]";
                i++;
            }
            retval += "]"+"\n";
            return retval;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    static void TestFileScript(String type,String content,String modulename) throws Exception {
        RDParser pars = null;
        pars = new RDParser(content,modulename);
        TModule p = null;
        p = pars.doParse();

        if (p == null)
        {
            System.out.println("Parse Process Failed");
            return;
        }
        if (type.equals("C")) {
            generateCCode(p, pars);
        } else if (type.equals("Java")){
            generateByteCode(p, pars);
        } else {
            generateInterpretedCode(p, pars);
        }
    }

   static void generateCCode( TModule p, RDParser parser) throws Exception{
        IExpressionVisitor visitor = new CGeneratorVisitor(parser);
        RUNTIME_CONTEXT context = new RUNTIME_CONTEXT(p);
        p.Execute(visitor, context);
    }

    static void generateInterpretedCode(TModule p, RDParser parser) throws Exception{
        IExpressionVisitor visitor = new TreeEvaluatorVisitor(parser);
        RUNTIME_CONTEXT context = new RUNTIME_CONTEXT(p);
        p.Execute(visitor, context);
    }

    static void generateByteCode(TModule p, RDParser parser) throws Exception{
        IExpressionVisitor visitor = new BCGeneratorVisitor(parser);
        BYTECODE_CONTEXT context = new BYTECODE_CONTEXT(p,null);
        p.Execute(visitor, context);
    }

    public static void main(String args[]) throws Exception {

        String CurDir = System.getProperty("user.dir");
        System.out.println("Current Directory of a Process"+ CurDir);
        String codetemplate = CurDir + "/Project/" + "CODE_TEMPLATE.TXT";
        String ret = GetCodeTemplate(codetemplate);

        System.out.println(ret);

        if ( args.length != 4 ) {
            System.out.println("Invalid Command Line Arguments");
            return;
        }
        String filename = args[0];
        if ( filename == null ) {
            System.out.println("Scripting fiel not foind");
            return ;
        }

        String csvFile =  args[1];
        String test_data = LoadList(csvFile);
        if ( test_data == null ) {
            System.out.println("Failed to Load From File");
            return;
        }
        String modulename = args[2];

        final Map<String, Object> map = new HashMap<String,Object>();
        map.put("RET_VAL","TRUE");
        map.put("RET_CODE","");
        map.put("INPUT_DATA",test_data);
        ScriptingInterpreter sp = new ScriptingInterpreter(filename);
        boolean bRet = sp.Execute(map);
        if ( bRet == false) {  System.out.println("Erorr File"); return; }
        String code= (String) map.get("RET_CODE");
        if ( code == null) {
            return ;
        }
        System.out.println(code);
        String type = args[3];

        TestFileScript(type,code,modulename);

        //------------- .So
        // g++ -shared -fPIC DATASET.cpp -o DATASET.so
        // g++ Caller.cpp ./DATASET.so
        // ./a.out 700 87666 'M' 42 5.4 53 5.8 5.9 3.7 1.3 3.1 1.7 23

        //----------- JAR
        // javac Caller.java
        // jar cfe Model.jar Caller Caller.class DATASET.class Helper/Utils.class
        // java -jar Model.jar 634 34224 'F' 45 2.3 24 4 2.9 1 1 1.5 0.4 21

    }
}