package Context;

import Compilation.TModule;
import Lexer.TYPE_INFO;
import Scope.SymbolTable;
import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BYTECODE_CONTEXT extends RUNTIME_CONTEXT {
    private TModule module;
    private ClassGen classGen;
    private ConstantPoolGen constantPoolGen;
    private InstructionFactory instructionFactory;
    private InstructionList instructionList;
    private MethodGen methodGen;
    // Used to track variable index
    private int variableIndexOffset;

    public TYPE_INFO stmtType;

    public BYTECODE_CONTEXT() {

    }
    public boolean SetModule(TModule mp) {
        if (module == null) {
            module = mp;
            return true;
        }
        return false;
    }

    public BYTECODE_CONTEXT(TModule module,
                                     FileOutputStream outputStream) {
        this.module = module;
        this.classGen = new ClassGen(module.getName(), "java.lang.Object",
                "<generated>", Constants.ACC_PUBLIC | Constants.ACC_SUPER,
                new String[] {});
        this.constantPoolGen = this.classGen.getConstantPool();
        this.instructionFactory = new InstructionFactory(this.classGen,
                this.constantPoolGen);
    }

    public BYTECODE_CONTEXT(BYTECODE_CONTEXT callerFnContext,
                                     String methodName, Type returnType, Type[] argTypes,
                                     String[] argNames) {
        this.module = callerFnContext.module;
        this.classGen = callerFnContext.classGen;
        this.constantPoolGen = callerFnContext.constantPoolGen;
        this.instructionFactory = callerFnContext.instructionFactory;
        this.instructionList = new InstructionList();

        // Changes the method signature of main method
        if (methodName.equals("main")) {
            String temp = "arg" + (this.module.getProcedures().size() - 1);
            this.variableIndexOffset = 1;
            this.methodGen = new MethodGen(Constants.ACC_PUBLIC
                    | Constants.ACC_STATIC, Type.VOID,
                    new Type[] { new ArrayType(Type.STRING, 1) },
                    new String[] { temp }, methodName, module.getName(),
                    this.instructionList, this.constantPoolGen);
        } else {
            this.variableIndexOffset = 0;
            this.methodGen = new MethodGen(Constants.ACC_PUBLIC
                    | Constants.ACC_STATIC, returnType, argTypes, argNames,
                    methodName, "main", this.instructionList,
                    this.constantPoolGen);
        }
    }

    // Function to generate class fine
    public void generate(OutputStream out) throws IOException {
        classGen.getJavaClass().dump(out);
    }

    public ClassGen getClassGen() {
        return classGen;
    }

    public ConstantPoolGen getConstantPoolGen() {
        return constantPoolGen;
    }

    public InstructionFactory getInstructionFactory() {
        return instructionFactory;
    }

    public InstructionList getInstructionList() {
        return instructionList;
    }

    public void setInstructionList(InstructionList instructionList) {
        this.instructionList = instructionList;
    }

    public MethodGen getMethodGen() {
        return methodGen;
    }

    public void setMethodGen(MethodGen methodGen) {
        this.methodGen = methodGen;
    }

    public int getVariableIndex() {
        return variableIndexOffset;
    }

    public void setVariableIndex(int offset) {
        variableIndexOffset = offset;
    }

    public TModule getModule() {
        return module;
    }

}
