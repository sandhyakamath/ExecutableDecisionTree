package Visitor;

import AST.Binary.BinaryDiv;
import AST.Binary.BinaryMinus;
import AST.Binary.BinaryMul;
import AST.Binary.BinaryPlus;
import AST.Boolean.LogicalExp;
import AST.Boolean.LogicalNot;
import AST.Boolean.RelationalExp;
import AST.CallExpression;
import AST.Constant.BooleanConstant;
import AST.Constant.IntegerConstant;
import AST.Constant.NumericConstant;
import AST.Constant.StringLiteral;
import AST.Expression;
import AST.Unary.UnaryMinus;
import AST.Unary.UnaryPlus;
import AST.Variable;
import ASTStatements.*;
import Compilation.Procedure;
import Compilation.TModule;
import Context.BYTECODE_CONTEXT;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Parser.RDParser;
import org.apache.bcel.Const;
import org.apache.bcel.generic.*;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class BCGeneratorVisitor implements IExpressionVisitor {
    private RDParser parser;

    public BCGeneratorVisitor(RDParser parser) {
        this.parser = parser;
    }

    @Override
    public SymbolInfo visit(TModule tmodule, RUNTIME_CONTEXT ctx) throws Exception {
        String className = tmodule.getName().toUpperCase() + ".class";
        FileOutputStream outputStream = new FileOutputStream(className);

        // Creates bytecode generation context for module
        BYTECODE_CONTEXT context = new BYTECODE_CONTEXT(
                tmodule, outputStream);
        context.SetModule(tmodule);

        // Iterates through the function and generates equivalent byte code
        if (tmodule.getProcedures() != null) {
            for (Object p : tmodule.getProcedures())
            {
                Procedure procedure =(Procedure)p;
                // Visits the procedure node
                procedure.accept(this, context,  null);
            }
        }


        // Generates bytecode and writes it file
        context.generate(outputStream);
        return null;
    }

    @Override
    public SymbolInfo visit(Procedure procedure, RUNTIME_CONTEXT ctx,
                            ArrayList<Expression> actualParameterExpressions) throws Exception {
        ArrayList<SymbolInfo> formalParameters = procedure.mFormals;
        if ( !procedure.mName.equalsIgnoreCase("predict")) {
            return null;
        }
        // Sets the return type, argument types and argument names for BCEL API
        Type returnType = getBCELType(procedure.type);
        Type[] argTypes;
        String[] argNames;
        if (formalParameters.size() == 0) {
            argTypes = Type.NO_ARGS;
            argNames = new String[] {};
        } else {
            argTypes = new Type[formalParameters.size()];
            argNames = new String[formalParameters.size()];
            for (int i = 0; i < argTypes.length; i++) {
                argTypes[i] = getBCELType(formalParameters.get(i).type);
                argNames[i] = "arg" + i;
            }
        }
// Creates bytecode generation context for current function
        BYTECODE_CONTEXT thisFnContext = new BYTECODE_CONTEXT(
                (BYTECODE_CONTEXT) ctx, procedure.mName
                .toLowerCase(), returnType, argTypes, argNames);
        thisFnContext.SetModule( ctx.getProgram());
        // Adds formal parameters to symbol table of current context
        for (SymbolInfo symbol : formalParameters) {
            storeSymbol(thisFnContext, symbol, false);
        }

        // Visits all statements and generates byte code
        for (Object statement : procedure.mStatements) {
            Statement stmt = (Statement) statement;
            stmt.accept(this, thisFnContext);
        }

        // Appends return statement for main function as Slang do not have
        // support for void type
        if (procedure.mName.equalsIgnoreCase("MAIN")) {
            thisFnContext.getInstructionList().append(
                    InstructionFactory.createReturn(Type.VOID));
        }

        // Sets the max stack size & the max number of local variables
        thisFnContext.getMethodGen().setMaxStack();
        thisFnContext.getMethodGen().setMaxLocals();

        // Generates method and adds it to class generator
        thisFnContext.getClassGen().addMethod(
                thisFnContext.getMethodGen().getMethod());

        // Disposes instruction lists (For enabling reuse of instruction
        // handles)
        thisFnContext.getInstructionList().dispose();
        return null;

    }

    private Type getBCELType(TYPE_INFO type) {
        switch (type) {
            case TYPE_NUMERIC:
                return Type.DOUBLE;
            case TYPE_STRING:
                return Type.STRING;
            case TYPE_BOOL:
                return Type.BOOLEAN;
            case TYPE_INTEGER:
                return Type.INT;
            default:
                return Type.VOID;
        }
    }

    private void storeSymbol(BYTECODE_CONTEXT context,
                             SymbolInfo varSymbol, boolean isLocalVariable) {
        // Sets byte code index in symbol
        varSymbol.index = context.getVariableIndex();

        // creates variable according to the type and updates index
        switch (varSymbol.type) {
            case TYPE_NUMERIC:
                // Function parameters automatically added by BCEL
                if (isLocalVariable) {
                    context.getInstructionList().append(
                            InstructionFactory.createNull(Type.DOUBLE));
                    context.getInstructionList().append(
                            InstructionFactory.createStore(Type.DOUBLE,
                                    varSymbol.index));
                }
                context.setVariableIndex(varSymbol.index + 2);
                break;
            case TYPE_STRING:
                if (isLocalVariable) {
                    context.getInstructionList().append(
                            InstructionFactory.createNull(Type.OBJECT));
                    context.getInstructionList().append(
                            InstructionFactory.createStore(Type.OBJECT,
                                    varSymbol.index));
                }
                context.setVariableIndex(varSymbol.index + 1);
                break;
            case TYPE_BOOL:
                if (isLocalVariable) {
                    context.getInstructionList().append(
                            InstructionFactory.createNull(Type.BOOLEAN));
                    context.getInstructionList().append(
                            InstructionFactory.createStore(Type.INT,
                                    varSymbol.index));
                }
                context.setVariableIndex(varSymbol.index + 1);
                break;
            case TYPE_INTEGER:
                context.setVariableIndex(varSymbol.index + 2);
                break;
            default:
                break;
        }
        // Adds symbol to symbol table
        context.getTable().add(varSymbol);
    }

    @Override
    public SymbolInfo visit(NumericConstant num, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        // Pushes constant into the stack
        context.getInstructionList()
                .append(new PUSH(context.getConstantPoolGen(),
                        num.getInfo().dblValue));
        context.stmtType = num.getType();
        return null;
    }

    @Override
    public SymbolInfo visit(BooleanConstant bool, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        // Pushes constant into the stack
        context.getInstructionList()
                .append(new PUSH(context.getConstantPoolGen(),
                        bool.getInfo().bolValue));
        context.stmtType = bool.getType();
        return null;
    }

    @Override
    public SymbolInfo visit(StringLiteral str, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;
        context.stmtType = TYPE_INFO.TYPE_STRING;
        // Pushes constant into the stack
        context.getInstructionList().append(
                new PUSH(context.getConstantPoolGen(), str.getInfo().strValue));
        return str.getInfo();
    }

    @Override
    public SymbolInfo visit(BinaryPlus plus, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;
        TYPE_INFO type = plus.getLeft().getType();
        if (type == null) {
            type = context.stmtType;
        }
        // Decides operation based on the type
        if (type == TYPE_INFO.TYPE_NUMERIC) {
            // Visits expression nodes
            plus.getLeft().accept(this, context);
            plus.getRight().accept(this, context);
            context.getInstructionList().append(InstructionConst.DADD);
        } else if (type == TYPE_INFO.TYPE_INTEGER) {
            // Visits expression nodes
            plus.getLeft().accept(this, context);
            plus.getRight().accept(this, context);
            context.getInstructionList().append(InstructionConst.IADD);
        }else {
            // If expressions are of String type, string concatenating is performed
            context.getInstructionList().append(
                    context.getInstructionFactory().createNew(
                            "java.lang.StringBuilder"));
            context.getInstructionList().append(InstructionConst.DUP);

            plus.getLeft().accept(this, context);
            context.getInstructionList().append(
                    context.getInstructionFactory()
                            .createInvoke("java.lang.StringBuilder", "<init>",
                                    Type.VOID, new Type[] { Type.STRING },
                                    Const.INVOKESPECIAL));

            plus.getRight().accept(this, context);
            context.getInstructionList().append(
                    context.getInstructionFactory()
                            .createInvoke("java.lang.StringBuilder", "append",
                                    new ObjectType("java.lang.StringBuilder"),
                                    new Type[] { Type.STRING },
                                    Const.INVOKEVIRTUAL));

            context.getInstructionList().append(
                    context.getInstructionFactory().createInvoke(
                            "java.lang.StringBuilder", "toString", Type.STRING,
                            Type.NO_ARGS, Const.INVOKEVIRTUAL));
        }
        return null;
    }

    @Override
    public SymbolInfo visit(Variable var, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        SymbolInfo varSymbol = context.getTable().get(var.getName());
        context.stmtType = varSymbol.type;
        // Loads the memory location with the byte code index
        context.getInstructionList().append(
                InstructionFactory.createLoad(getBCELType(varSymbol.type),
                        varSymbol.index));
        return null;
    }

    @Override
    public SymbolInfo visit(BinaryMinus minus, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        // Gets the type of the expression

        if (minus.getType() == TYPE_INFO.TYPE_NUMERIC || minus.getType() == TYPE_INFO.TYPE_INTEGER) {
            // Visits expression nodes
            minus.getLeft().accept(this, context);
            minus.getRight().accept(this, context);
            context.getInstructionList().append(InstructionConst.DSUB);
        }
        return null;
    }

    @Override
    public SymbolInfo visit(BinaryMul mul, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;
        TYPE_INFO type = mul.getLeft().getType();
        // Gets the type of the expression
        if ( type == TYPE_INFO.TYPE_NUMERIC) {
            // Visits expression nodes
            mul.getLeft().accept(this, context);
            mul.getRight().accept(this, context);
            context.getInstructionList().append(InstructionConst.DMUL);
        } else if ( type == TYPE_INFO.TYPE_INTEGER) {
                // Visits expression nodes
                mul.getLeft().accept(this, context);
                mul.getRight().accept(this, context);
                context.getInstructionList().append(InstructionConst.IMUL);
        }

        return null;
    }

    @Override
    public SymbolInfo visit(BinaryDiv div, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;
        TYPE_INFO type = div.getLeft().getType();
        // Gets the type of the expression

        if (type == TYPE_INFO.TYPE_NUMERIC) {
            // Visits expression nodes
            div.getLeft().accept(this, context);
            div.getRight().accept(this, context);
            context.getInstructionList().append(InstructionConst.DDIV);
        } else if (type == TYPE_INFO.TYPE_INTEGER) {
            // Visits expression nodes
            div.getLeft().accept(this, context);
            div.getRight().accept(this, context);
            context.getInstructionList().append(InstructionConst.IDIV);
        }
        return null;
    }

    @Override
    public SymbolInfo visit(UnaryPlus plus, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        // Gets the type of the expression
        TYPE_INFO lExprType = plus.getRight().getType();
        if (lExprType == TYPE_INFO.TYPE_NUMERIC || lExprType == TYPE_INFO.TYPE_INTEGER) {
            // Visits expression nodes
            plus.getRight().accept(this, context);
        }
        return null;
    }

    @Override
    public SymbolInfo visit(UnaryMinus minus, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        // Gets the type of the expression
       TYPE_INFO lExprType = minus.getRight().getType();
        if (lExprType == TYPE_INFO.TYPE_NUMERIC) {
            // Visits expression nodes
            minus.getRight().accept(this, context);
            context.getInstructionList().append(InstructionConst.DNEG);
        } else if (lExprType == TYPE_INFO.TYPE_INTEGER) {
            // Visits expression nodes
            minus.getRight().accept(this, context);
            context.getInstructionList().append(InstructionConst.INEG);
        }
        return null;
    }

    @Override
    public SymbolInfo visit(RelationalExp relationalExp, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        TYPE_INFO type = relationalExp.getLeft().getType();

        relationalExp.getLeft().accept(this, context);
        relationalExp.getRight().accept(this, context);

        if (type == TYPE_INFO.TYPE_NUMERIC ) {
            switch (relationalExp.getOp()) {
                case TOK_LT:
                    context.getInstructionList().append(InstructionConst.DCMPG);
                    context.getInstructionList().append(InstructionConst.I2D);
                    context.getInstructionList().append(
                            new PUSH(context.getConstantPoolGen(), -1.0));
                    context.getInstructionList().append(InstructionConst.DCMPG);
                    context.getInstructionList().append(
                            new PUSH(context.getConstantPoolGen(), 1));
                    context.getInstructionList().append(InstructionConst.IXOR);
                    return null;
                case TOK_LTE:
                    context.getInstructionList().append(InstructionConst.DCMPG);
                    context.getInstructionList().append(InstructionConst.I2D);
                    context.getInstructionList().append(
                            new PUSH(context.getConstantPoolGen(), 1.0));
                    context.getInstructionList().append(InstructionConst.DCMPG);
                    context.getInstructionList().append(InstructionConst.I2D);
                    context.getInstructionList().append(
                            new PUSH(context.getConstantPoolGen(), -1.0));
                    context.getInstructionList().append(InstructionConst.DCMPG);
                    context.getInstructionList().append(
                            new PUSH(context.getConstantPoolGen(), 1));
                    context.getInstructionList().append(InstructionConst.IXOR);
                    return null;
                case TOK_GT:
                    context.getInstructionList().append(InstructionConst.DCMPG);
                    context.getInstructionList().append(InstructionConst.I2D);
                    context.getInstructionList().append(
                            new PUSH(context.getConstantPoolGen(), 1.0));
                    context.getInstructionList().append(InstructionConst.DCMPG);
                    context.getInstructionList().append(InstructionConst.I2D);
                    context.getInstructionList().append(
                            new PUSH(context.getConstantPoolGen(), -1.0));
                    context.getInstructionList().append(InstructionConst.DCMPG);
                    return null;
                case TOK_GTE:
                    context.getInstructionList().append(InstructionConst.DCMPG);
                    context.getInstructionList().append(InstructionConst.I2D);
                    context.getInstructionList().append(
                            new PUSH(context.getConstantPoolGen(), -1.0));
                    context.getInstructionList().append(InstructionConst.DCMPG);
                    return null;
                case TOK_EQ:
                    context.getInstructionList().append(InstructionConst.DCMPG);
                    BranchInstruction ifneBranch_1 = InstructionFactory
                            .createBranchInstruction(Const.IFNE, null);
                    context.getInstructionList().append(ifneBranch_1);
                    context.getInstructionList().append(
                            new PUSH(context.getConstantPoolGen(), 1));
                    BranchInstruction gotoBranch_1 = InstructionFactory
                            .createBranchInstruction(Const.GOTO, null);
                    context.getInstructionList().append(gotoBranch_1);
                    InstructionHandle ifneTarget_1 = context.getInstructionList()
                            .append(new PUSH(context.getConstantPoolGen(), 0));
                    InstructionHandle gotoTarget_1 = context.getInstructionList()
                            .append(new PUSH(context.getConstantPoolGen(), 1));
                    context.getInstructionList().append(InstructionConst.IAND);
                    ifneBranch_1.setTarget(ifneTarget_1);
                    gotoBranch_1.setTarget(gotoTarget_1);
                    return null;
                case TOK_NEQ:
                    context.getInstructionList().append(InstructionConst.DCMPG);
                    BranchInstruction ifneBranch_2 = InstructionFactory
                            .createBranchInstruction(Const.IFNE, null);
                    context.getInstructionList().append(ifneBranch_2);
                    context.getInstructionList().append(
                            new PUSH(context.getConstantPoolGen(), 0));
                    BranchInstruction gotoBranch_2 = InstructionFactory
                            .createBranchInstruction(Const.GOTO, null);
                    context.getInstructionList().append(gotoBranch_2);
                    InstructionHandle ifneTarget_2 = context.getInstructionList()
                            .append(new PUSH(context.getConstantPoolGen(), 1));
                    InstructionHandle gotoTarget_2 = context.getInstructionList()
                            .append(new PUSH(context.getConstantPoolGen(), 0));
                    context.getInstructionList().append(InstructionConst.IOR);
                    ifneBranch_2.setTarget(ifneTarget_2);
                    gotoBranch_2.setTarget(gotoTarget_2);
                    return null;
                default:
                    return null;
            }
        } else if (type == TYPE_INFO.TYPE_INTEGER ) {
            switch (relationalExp.getOp()) {
                case TOK_LT:
                    context.getInstructionList()
                            .append(new PUSH(context.getConstantPoolGen(), "LT"));
                    context.getInstructionList().append(
                            context.getInstructionFactory().createInvoke(
                                    "Helper.Utils", "compareInt", Type.BOOLEAN,
                                    new Type[] { Type.INT, Type.INT, Type.STRING },
                                    Const.INVOKESTATIC));
                    return null;
                case TOK_LTE:
                    context.getInstructionList()
                            .append(new PUSH(context.getConstantPoolGen(), "LTE"));
                    context.getInstructionList().append(
                            context.getInstructionFactory().createInvoke(
                                    "Helper.Utils", "compareInt", Type.BOOLEAN,
                                    new Type[] { Type.INT, Type.INT, Type.STRING },
                                    Const.INVOKESTATIC));
                    return null;
                case TOK_GT:
                    context.getInstructionList()
                            .append(new PUSH(context.getConstantPoolGen(), "GT"));
                    context.getInstructionList().append(
                            context.getInstructionFactory().createInvoke(
                                    "Helper.Utils", "compareInt", Type.BOOLEAN,
                                    new Type[] { Type.INT, Type.INT, Type.STRING },
                                    Const.INVOKESTATIC));
                    return null;
                case TOK_GTE:
                    context.getInstructionList()
                            .append(new PUSH(context.getConstantPoolGen(), "GTE"));
                    context.getInstructionList().append(
                            context.getInstructionFactory().createInvoke(
                                    "Helper.Utils", "compareInt", Type.BOOLEAN,
                                    new Type[] { Type.INT, Type.INT, Type.STRING },
                                    Const.INVOKESTATIC));
                    return null;
                case TOK_EQ:
                    context.getInstructionList()
                            .append(new PUSH(context.getConstantPoolGen(), "EQ"));
                    context.getInstructionList().append(
                            context.getInstructionFactory().createInvoke(
                                    "Helper.Utils", "compareInt", Type.BOOLEAN,
                                    new Type[] { Type.INT, Type.INT, Type.STRING },
                                    Const.INVOKESTATIC));
                    return null;
                case TOK_NEQ:
                    context.getInstructionList()
                            .append(new PUSH(context.getConstantPoolGen(), "NEQ"));
                    context.getInstructionList().append(
                            context.getInstructionFactory().createInvoke(
                                    "Helper.Utils", "compareInt", Type.BOOLEAN,
                                    new Type[] { Type.INT, Type.INT, Type.STRING },
                                    Const.INVOKESTATIC));
                    return null;
                default:
                    return null;
            }
        } else if (type == TYPE_INFO.TYPE_BOOL) {
            switch (relationalExp.getOp()) {
                case TOK_EQ:
                    context.getInstructionList().append(InstructionConst.IXOR);
                    context.getInstructionList().append(
                            new PUSH(context.getConstantPoolGen(), 1));
                    context.getInstructionList().append(InstructionConst.IXOR);
                    return null;
                case TOK_NEQ:
                    context.getInstructionList().append(InstructionConst.IXOR);
                    return null;
                default:
                    return null;
            }
        } else {
            switch (relationalExp.getOp()) {
                case TOK_EQ:
                    context.getInstructionList().append(
                            context.getInstructionFactory().createInvoke(
                                    "java.lang.String", "equals", Type.BOOLEAN,
                                    new Type[] { Type.OBJECT },
                                    Const.INVOKEVIRTUAL));
                    return null;

                case TOK_NEQ:
                    context.getInstructionList().append(
                            context.getInstructionFactory().createInvoke(
                                    "java.lang.String", "equals", Type.BOOLEAN,
                                    new Type[] { Type.OBJECT },
                                    Const.INVOKEVIRTUAL));
                    context.getInstructionList().append(
                            new PUSH(context.getConstantPoolGen(), 1));
                    context.getInstructionList().append(InstructionConst.IXOR);
                    return null;

                default:
                    return null;
            }

        }
    }

    @Override
    public SymbolInfo visit(LogicalExp logicalExp, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        logicalExp.getLeft().accept(this, context);
        logicalExp.getRight().accept(this, context);

        switch (logicalExp.getOp()) {
            case TOK_AND:
                context.getInstructionList().append(InstructionConst.IAND);
                return null;
            case TOK_OR:
                context.getInstructionList().append(InstructionConst.IOR);
                return null;
            default:
                return null;
        }
    }

    @Override
    public SymbolInfo visit(LogicalNot logicalNot, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        logicalNot.getExp().accept(this, context);
        context.getInstructionList().append(
                new PUSH(context.getConstantPoolGen(), 1));
        context.getInstructionList().append(InstructionConst.IXOR);

        return null;
    }

    @Override
    public SymbolInfo visit(AssignmentStatement stmt, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        // Visits the expression and loads the value into the stack
        stmt.getExp1().accept(this, context);

        // Stores the value in the stack to the variable
        SymbolInfo varSymbol = context.getTable().get(
                stmt.getVariable().getName());
        context.getInstructionList()
                .append(InstructionFactory.createStore(
                        getBCELType(varSymbol.type), varSymbol.index));
        return null;
    }

    @Override
    public SymbolInfo visit(IfStatement stmt, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        stmt.getCond().accept(this, context);

        // Creates branching instruction, if expression is false branch
        // instruction to set target
        BranchInstruction ifCond_branch = InstructionFactory
                .createBranchInstruction(Const.IFEQ, null);
        context.getInstructionList().append(ifCond_branch);

        for (Object statement : stmt.getStmnts()) {
            Statement s = (Statement) statement;
            s.accept(this, context);
        }

        // Creates go to branch, avoids the false if condition is true
        BranchInstruction ifCondGoTo_branch = InstructionFactory
                .createBranchInstruction(Const.GOTO, null);
        context.getInstructionList().append(ifCondGoTo_branch);

        // Handle to the false part statements
        InstructionHandle falsePartBegin_handle = context.getInstructionList()
                .append(new PUSH(context.getConstantPoolGen(), 1));
        context.getInstructionList().append(InstructionConst.POP);
        if (stmt.getElsePart() != null) {
            for (Object statement : stmt.getElsePart()) {
                Statement s = (Statement) statement;
                s.accept(this, context);
            }
        }

        // Handle to the end of false part
        InstructionHandle falsePartEnd_handle = context.getInstructionList()
                .append(new PUSH(context.getConstantPoolGen(), 1));
        context.getInstructionList().append(InstructionConst.POP);

        ifCond_branch.setTarget(falsePartBegin_handle);
        ifCondGoTo_branch.setTarget(falsePartEnd_handle);

        return null;
    }

    @Override
    public SymbolInfo visit(PrintStatement stmt, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        // Access 'out' static object of class 'System'
        context.getInstructionList().append(
                context.getInstructionFactory().createFieldAccess(
                        "java.lang.System", "out",
                        new ObjectType("java.io.PrintStream"),
                        Const.GETSTATIC));

        // Visits the expression node and generates equivalent byte code
        stmt.getExp().accept(this, context);
        TYPE_INFO exprType = stmt.getExp().getType();
        if ( exprType == null)
            exprType = context.stmtType;
        // Invokes 'print' function of 'out' static object of class 'System'
        context.getInstructionList().append(
                context.getInstructionFactory().createInvoke(
                        "java.io.PrintStream", "print", Type.VOID,
                        new Type[] { getBCELType(exprType) },
                        Const.INVOKEVIRTUAL));
        return null;
    }

    @Override
    public SymbolInfo visit(PrintLineStatement stmt, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        // Access 'out' static object of class 'System'
        context.getInstructionList().append(
                context.getInstructionFactory().createFieldAccess(
                        "java.lang.System", "out",
                        new ObjectType("java.io.PrintStream"),
                        Const.GETSTATIC));

        // Visits the expression node and generates equivalent byte code
        stmt.getExp().accept(this, context);
        TYPE_INFO exprType = context.stmtType;

        // Invokes 'println' function of 'out' static object of class 'System'
        context.getInstructionList().append(
                context.getInstructionFactory().createInvoke(
                        "java.io.PrintStream", "println", Type.VOID,
                        new Type[] { getBCELType(exprType) },
                        Const.INVOKEVIRTUAL));
        return null;
    }

    @Override
    public SymbolInfo visit(VariableDeclStatement stmt, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        // Creates symbol in symbol table and byte code for corresponding
        // variable
        SymbolInfo varSymbol = stmt.getInfo();
        storeSymbol(context, varSymbol, true);
        return null;
    }

    @Override
    public SymbolInfo visit(WhileStatement stmt, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        // Go to branch to condition evaluation
        BranchInstruction gotoCond_branch = InstructionFactory
                .createBranchInstruction(Const.GOTO, null);
        context.getInstructionList().append(gotoCond_branch);

        // Handle for beginning of statements
        InstructionHandle beginStatements_handle = context.getInstructionList()
                .append(new PUSH(context.getConstantPoolGen(), 1));
        context.getInstructionList().append(InstructionConst.POP);
        for (Object statement : stmt.getStmnts()) {
            Statement s = (Statement) statement;
            s.accept(this, context);
        }

        // Condition evaluation
        InstructionHandle cond_handle = context.getInstructionList().append(
                new PUSH(context.getConstantPoolGen(), 1));
        context.getInstructionList().append(InstructionConst.POP);
        stmt.getCond().accept(this, context);
        context.getInstructionList().append(
                new PUSH(context.getConstantPoolGen(), 1));
        context.getInstructionList().append(InstructionConst.IXOR);
        // Branches to statements if condition is true
        BranchInstruction checkCond_branch = InstructionFactory
                .createBranchInstruction(Const.IFEQ, null);
        context.getInstructionList().append(checkCond_branch);

        gotoCond_branch.setTarget(cond_handle);
        checkCond_branch.setTarget(beginStatements_handle);
        return null;
    }

    @Override
    public SymbolInfo visit(IntegerConstant num, RUNTIME_CONTEXT ctx) throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        // Pushes constant into the stack
        context.getInstructionList()
                .append(new PUSH(context.getConstantPoolGen(),
                        num.getInfo().intValue));
        context.stmtType = num.getType();
        return num.getInfo();
    }

    @Override
    public SymbolInfo visit(CallExpression procedureCallExpression, RUNTIME_CONTEXT ctx) throws Exception {
        Procedure procedure = procedureCallExpression.getProcedure();
        ArrayList<SymbolInfo> formalParameters = procedureCallExpression
                .getProcedure().getmFormals();
        ArrayList<Expression> actualParameters = procedureCallExpression
                .getAcutalParameterExpressions();

        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        // Sets the return type
        Type returnType = getBCELType(procedureCallExpression.getType());

        // Sets the parameter type array
        Type[] typeParameterArray = new Type[formalParameters.size()];
        for (int i = 0; i < typeParameterArray.length; i++) {
            typeParameterArray[i] = getBCELType(formalParameters.get(i).type);
            actualParameters.get(i).accept(this, context);
        }
        String proc_name = procedure.mName.toLowerCase();
        if ( proc_name.equals("cmd_get_s")){
            context.getInstructionList().append(
                    context.getInstructionFactory().createInvoke(
                            "Helper.Utils", "cmd_get_s", Type.STRING,
                            new Type[] { Type.DOUBLE, Type.STRING},
                            Const.INVOKESTATIC));
            // #context.getInstructionList().append()
        }
        else  if ( proc_name.equals("cmd_get_i")){
            context.getInstructionList().append(
                    context.getInstructionFactory().createInvoke(
                            "Helper.Utils", "cmd_get_i", Type.INT,
                            new Type[] { Type.DOUBLE, Type.STRING},
                            Const.INVOKESTATIC));
        }
        else  if ( proc_name.equals("cmd_get_d")){
            context.getInstructionList().append(
                    context.getInstructionFactory().createInvoke(
                            "Helper.Utils", "cmd_get_d", Type.DOUBLE,
                            new Type[] { Type.DOUBLE, Type.STRING},
                            Const.INVOKESTATIC));
        }
        else if ( proc_name.equals("cmd_get_b")){
            context.getInstructionList().append(
                    context.getInstructionFactory().createInvoke(
                            "Helper.Utils", "cmd_get_b", Type.BOOLEAN,
                            new Type[] { Type.DOUBLE, Type.STRING},
                            Const.INVOKESTATIC));
        }
        else {
            // Invokes the function
            context.getInstructionList().append(
                    context.getInstructionFactory().createInvoke(
                            context.getModule().getName().toUpperCase(),
                            procedure.mName.toLowerCase(), returnType,
                            typeParameterArray, Const.INVOKESTATIC));
        }
        return null;
    }

    private Exception runTimeError(int index) {
        String errorMessage = "SLANG RUNTIME ERROR AT LINE: "
                + parser.getCurrentLine(index);
        return new Exception(errorMessage);
    }

    private SymbolInfo getLocalVariable(SymbolInfo aParameterSymbol,
                                        SymbolInfo fParameterSymbol) {
        SymbolInfo returnSymbol = new SymbolInfo(fParameterSymbol.symbolName,
                fParameterSymbol.type);

        switch (returnSymbol.type) {
            case TYPE_BOOL:
                returnSymbol.bolValue = aParameterSymbol.bolValue;
                break;
            case TYPE_NUMERIC:
                returnSymbol.dblValue = aParameterSymbol.dblValue;
                break;
            case TYPE_INTEGER:
                returnSymbol.intValue= aParameterSymbol.intValue;
                break;
            case TYPE_STRING:
                returnSymbol.strValue= aParameterSymbol.strValue;
                break;
            default:
                break;
        }

        return returnSymbol;
    }

    @Override
    public SymbolInfo visit(ReturnStatement returnStatement, RUNTIME_CONTEXT ctx)
            throws Exception {
        BYTECODE_CONTEXT context = (BYTECODE_CONTEXT) ctx;

        returnStatement.getExpression().accept(this, context);

        // Loads the memory location corresponding to the symbol
        context.getInstructionList().append(
                InstructionFactory.createReturn(getBCELType(returnStatement
                        .getExpression().getType())));
       return null;
    }
}
