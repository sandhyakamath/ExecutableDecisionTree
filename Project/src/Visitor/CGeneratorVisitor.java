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
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Parser.RDParser;
import org.apache.bcel.generic.Type;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;

public class CGeneratorVisitor implements IExpressionVisitor{


    private RDParser parser;
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
    public CGeneratorVisitor(RDParser parser) {
        this.parser = parser;
    }
    @Override
    public SymbolInfo visit(TModule tmodule, RUNTIME_CONTEXT context) throws Exception {
        String className = tmodule.getName().toUpperCase() + ".cpp";
        BufferedWriter writer = new BufferedWriter(new FileWriter(className));

        context.appendString("#include <iostream> \r\n\r\n");
        context.appendString("#include <string> \r\n");
        context.appendString("using namespace std;\r\n");
       // context.appendString("int main() { \r\n");

        // Iterates through the function and generates equivalent byte code
        if (tmodule.getProcedures() != null) {
            for (Object p : tmodule.getProcedures())
            {
                Procedure procedure =(Procedure)p;
                // Visits the procedure node
              //  System.out.println("Generating Code for "+procedure.mName);
                procedure.accept(this, context,  null);
            }
        }


        // Generates bytecode and writes it file
       // System.out.println(context.getStr().toString());
        String CurDir = System.getProperty("user.dir");
        System.out.println("Current Directory of a Process"+ CurDir);
        String codetemplate = CurDir + "/" + "CODE_TEMPLATE.TXT";
        String ret = GetCodeTemplate(codetemplate);
        writer.write(ret+"\r\n");
        writer.write(context.getStr().toString());

        writer.close();
        return null;
    }

    @Override
    public SymbolInfo visit( Procedure procedure, RUNTIME_CONTEXT context,
                             ArrayList<Expression> actualParameterExpressions) throws Exception {

        //--------------- For the Project
        if ( !procedure.mName.equalsIgnoreCase("PREDICT")) {
            return null;
        }
        if (procedure.mName.equalsIgnoreCase("MAIN")) {
            context.appendString("int main() { \r\n");
        }
        else  {
            ArrayList<SymbolInfo> formalParameters = procedure.mFormals;
            // Sets the return type, argument types and argument names
            getType(procedure.type, context);
            context.appendString(procedure.mName.toLowerCase());
            Type[] argTypes;
            String[] argNames;
            if (formalParameters.size() == 0) {
                context.appendString("( )");
            } else {
                context.appendString("( ");
                argTypes = new Type[formalParameters.size()];
                // argNames = new String[formalParameters.size()];
                for (int i = 0; i < argTypes.length; i++) {
                    getType(formalParameters.get(i).type, context);
                    context.appendString(formalParameters.get(i).symbolName);
                    if ( i != argTypes.length-1)
                        context.appendString(", " );
                }
                context.appendString(" ) { \n");
            }
        }

        // Visits all statements and generates byte code
        for (Object statement : procedure.mStatements) {
            context.appendString("\t");
            Statement stmt = (Statement) statement;
            stmt.accept(this, context);
        }
        if (procedure.mName.equalsIgnoreCase("MAIN")) {
            context.appendString("return " + 0 +"; \r\n } \r\n");
        } else {
            context.appendString("\n\r");
            context.appendString(" }\r\n");
            context.appendString("\n\r");
        }

        return null;

    }

    private void getType(TYPE_INFO type, RUNTIME_CONTEXT context) {
        switch (type) {
            case TYPE_NUMERIC:
                context.appendString("double ");
                break;
            case TYPE_STRING:
                context.appendString("string ");
                break;
            case TYPE_BOOL:
                context.appendString("bool ");
                break;
            case TYPE_INTEGER:
                context.appendString("int ");
                break;
            default:
                break;
        }
    }

    @Override
    public SymbolInfo visit(NumericConstant num, RUNTIME_CONTEXT context) throws Exception {
         context.appendString(String.valueOf(num.getInfo().dblValue));
         return null;
    }

    @Override
    public SymbolInfo visit(BooleanConstant bool, RUNTIME_CONTEXT context) throws Exception {
        context.appendString(String.valueOf(bool.getInfo().bolValue));
        return null;
    }

    @Override
    public SymbolInfo visit(StringLiteral str, RUNTIME_CONTEXT context) throws Exception {
        context.appendString("\"");
        context.appendString(String.valueOf(str.getInfo().strValue));
        context.appendString("\"");
        return null;
    }


    @Override
    public SymbolInfo visit(BinaryPlus plus, RUNTIME_CONTEXT context) throws Exception {

        context.appendString("(");
        plus.getLeft().accept(this, context);
        context.appendString("+");
        plus.getRight().accept(this, context);
        context.appendString(")");
        return null;
    }

    @Override
    public SymbolInfo visit(Variable var, RUNTIME_CONTEXT context) throws Exception {
        context.appendString(var.getName());
        return null;
    }

    @Override
    public SymbolInfo visit(BinaryMinus minus, RUNTIME_CONTEXT context) throws Exception {
        context.appendString("(");
        minus.getLeft().accept(this, context);
        context.appendString("-");
        minus.getRight().accept(this, context);
        context.appendString(")");
        return null;
    }

    @Override
    public SymbolInfo visit(BinaryMul mul, RUNTIME_CONTEXT context) throws Exception {
        context.appendString("(");
        mul.getLeft().accept(this, context);
        context.appendString("*");
        mul.getRight().accept(this, context);
        context.appendString(")");
        return null;
    }

    @Override
    public SymbolInfo visit(BinaryDiv div, RUNTIME_CONTEXT context) throws Exception {
        context.appendString("(");
        div.getLeft().accept(this, context);
        context.appendString("/");
        div.getRight().accept(this, context);
        context.appendString(")");
        return null;
    }

    @Override
    public SymbolInfo visit(UnaryPlus plus, RUNTIME_CONTEXT context) throws Exception {
        context.appendString("+");
        plus.getRight().accept(this, context);
        return null;
    }

    @Override
    public SymbolInfo visit(UnaryMinus minus, RUNTIME_CONTEXT context) throws Exception {
        context.appendString("-");
        minus.getRight().accept(this, context);
        return null;
    }

    @Override
    public SymbolInfo visit(RelationalExp relationalExp, RUNTIME_CONTEXT context) throws Exception {
        relationalExp.getLeft().accept(this, context);
         switch (relationalExp.getOp()) {
            case TOK_EQ -> context.appendString("==");
            case TOK_GT -> context.appendString(">");
            case TOK_GTE -> context.appendString(">=");
            case TOK_LT -> context.appendString("<");
            case TOK_LTE -> context.appendString("<=");
            case TOK_NEQ -> context.appendString("!=");
        }
        relationalExp.getRight().accept(this, context);
        return null;
    }

    @Override
    public SymbolInfo visit(LogicalExp logicalExp, RUNTIME_CONTEXT context) throws Exception {
        logicalExp.getLeft().accept(this, context);
        switch (logicalExp.getOp()) {
            case TOK_AND -> context.appendString("&&");
            case TOK_OR -> context.appendString("||");
        }
        logicalExp.getRight().accept(this, context);
        return null;
    }

    @Override
    public SymbolInfo visit(LogicalNot logicalNot, RUNTIME_CONTEXT context) throws Exception {
        context.appendString("!");
        logicalNot.getExp().accept(this, context);
        return null;
    }

    @Override
    public SymbolInfo visit(AssignmentStatement stmt, RUNTIME_CONTEXT context) throws Exception {
        //SymbolInfo val = stmt.getExp1().accept(this, context);
        //context.getTable().assign(stmt.getVariable(), val);
        //context.appendString("\t");
        stmt.getVariable().accept(this, context);
        context.appendString( " = ");
        stmt.getExp1().accept(this, context);
        context.appendString(";\r\n");
        return null;
    }
    @Override
    public SymbolInfo visit(IfStatement stmt, RUNTIME_CONTEXT context) throws Exception {
        context.appendString("if ( ");
        stmt.getCond().accept(this, context);
        context.appendString(" ) { \r\n");

        for (Object rst : stmt.getStmnts())
        {
            context.appendString("\t");
            Statement s = (Statement) rst;
            s.accept(this, context);

        }

        if (stmt.getElsePart() != null)
        {
           context.appendString("\n\r\t} \r\n\t else { \r\n ");

            for  (Object rst : stmt.getElsePart())
            {
                context.appendString("\t");
                Statement s = (Statement) rst;
                s.accept(this, context);

            }

        }
        context.appendString("\r\n\t} \r\n  \r\n ");
        return null;
    }

    @Override
    public SymbolInfo visit(PrintLineStatement stmt, RUNTIME_CONTEXT context) throws Exception {
        context.appendString("\tstd::cout << ");
        stmt.getExp().accept(this, context);
        context.appendString("<< std::endl;\r\n");
        return null;
    }

    @Override
    public SymbolInfo visit(PrintStatement stmt, RUNTIME_CONTEXT context) throws  Exception{
        context.appendString("\tstd::cout << ");
        stmt.getExp().accept(this, context);
        context.appendString(";\r\n");
        return null;
    }

    @Override
    public SymbolInfo visit(VariableDeclStatement stmt, RUNTIME_CONTEXT context) {
        if (stmt.getInfo().type.name().equals("TYPE_STRING"))
            context.appendString("string " + stmt.getInfo().symbolName+ ";\r\n");
        else if (stmt.getInfo().type.name().equals("TYPE_BOOL"))
            context.appendString("bool " + stmt.getInfo().symbolName+ ";\r\n");
        else if (stmt.getInfo().type.name().equals("TYPE_NUMERIC"))
            context.appendString("double " + stmt.getInfo().symbolName+ ";\r\n");
        else if (stmt.getInfo().type.name().equals("TYPE_INTEGER"))
            context.appendString("int " + stmt.getInfo().symbolName+ ";\r\n");
        return null;

    }


    @Override
    public SymbolInfo visit(WhileStatement stmt, RUNTIME_CONTEXT context) throws Exception {
        context.appendString("while (");
        stmt.getCond().accept(this, context);
        context.appendString(") {\r\n");
        for (Object rst : stmt.getStmnts())
        {
            context.appendString("\t");
            Statement s = (Statement) rst;
            s.accept(this, context);

        }
        context.appendString("\r\n\t}\r\n");
        return null;
    }

    public SymbolInfo visit(IntegerConstant num, RUNTIME_CONTEXT context) throws Exception {
        context.appendString(String.valueOf(num.getInfo().intValue));
        return null;
    }

    @Override
    public SymbolInfo visit(CallExpression procedureCallExpression, RUNTIME_CONTEXT context
    ) throws Exception {
       // Procedure procedure = procedureCallExpression.getProcedure();
        context.appendString(procedureCallExpression.getProcedure().mName.toLowerCase());
        context.appendString("(");
        ArrayList mActuals = procedureCallExpression.getAcutalParameterExpressions();

        for (int i = 0; i < mActuals.size(); i++) {
            Expression e = (Expression) mActuals.get(i);
            e.accept(this, context);
            if (i != mActuals.size() - 1)
                context.appendString(", ");

        }
        context.appendString(")");
        // Calls the function by passing the arguements
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
    public SymbolInfo visit(ReturnStatement returnStatement, RUNTIME_CONTEXT context)
            throws Exception {
        context.appendString("return ");

        SymbolInfo exprValue = returnStatement.getExpression()
                .accept( this, context);
        context.appendString(";");
        return null;
    }
}
