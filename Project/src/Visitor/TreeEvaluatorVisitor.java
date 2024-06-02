package Visitor;

import AST.Binary.*;
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
import Compilation.PROC;
import Compilation.TModule;
import Context.COMPILATION_CONTEXT;
import Context.RUNTIME_CONTEXT;
import Lexer.RELATION_OPERATOR;
import Lexer.SymbolInfo;
import Lexer.TOKEN;
import Lexer.TYPE_INFO;
import Compilation.Procedure;
import Parser.RDParser;

import java.util.ArrayList;

public class TreeEvaluatorVisitor implements IExpressionVisitor {

    private RDParser parser;

    public TreeEvaluatorVisitor(RDParser parser) {
        this.parser = parser;
    }

    @Override
    public SymbolInfo visit(TModule tmodule, RUNTIME_CONTEXT context) throws Exception {
        Procedure p = tmodule.find("Main");
        if (p != null)
        {

            return p.accept(this, context, null);
        }
        return null;
    }

    @Override
    public SymbolInfo visit( Procedure procedure, RUNTIME_CONTEXT context,
                        ArrayList<Expression> actualParameterExpressions) throws Exception {
       RUNTIME_CONTEXT caleeFnContext = new RUNTIME_CONTEXT(context.getProgram());
        ArrayList<SymbolInfo> fParameters = procedure.getmFormals();
        // Gets value for actual parameter and adds it into symbol table
        if (fParameters != null) {
            for (int i = 0; i < fParameters.size(); i++) {
                SymbolInfo aParameterSymbol = actualParameterExpressions.get(i).accept(
                        this, context);
                SymbolInfo fParameterSymbol = fParameters.get(i);
                caleeFnContext.getTable().add(
                        getLocalVariable(aParameterSymbol, fParameterSymbol));
            }
        }
        for (Object stmt : procedure.mStatements) {
            Statement statement = (Statement) stmt;
            SymbolInfo returnValueSymbol = statement.accept(this, caleeFnContext);
            // Checking for return statement
            if (returnValueSymbol != null) {
                return returnValueSymbol;
            }
        }

        // If no return statement find throws error except for 'MAIN' function
        if (procedure.mName.equalsIgnoreCase("MAIN")) {
            return null;
        } else {
            throw runTimeError(procedure.getIndex());
        }

    }

    // numeric
    @Override
    public SymbolInfo visit(NumericConstant num, RUNTIME_CONTEXT context) {
        return num.getInfo();
    }

    // boolean

    @Override
    public SymbolInfo visit(BooleanConstant bool, RUNTIME_CONTEXT context) throws Exception {
        return bool.getInfo();
    }

    // string literal
    @Override
    public SymbolInfo visit(StringLiteral str, RUNTIME_CONTEXT context) throws Exception {
        return str.getInfo();
    }
    // Binary PLUS

    @Override
    public SymbolInfo visit(BinaryPlus plus, RUNTIME_CONTEXT context) throws Exception {
        SymbolInfo evalLeft = plus.getLeft().accept(this, context);
        SymbolInfo evalRight = plus.getRight().accept(this, context);

        if (evalLeft.type == TYPE_INFO.TYPE_STRING &&
                evalRight.type == TYPE_INFO.TYPE_STRING)
        {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.strValue = evalLeft.strValue + evalRight.strValue;
            ret_val.type = TYPE_INFO.TYPE_STRING;
            ret_val.symbolName = "";
            return ret_val;
        }
        else if (evalLeft.type == TYPE_INFO.TYPE_NUMERIC &&
                evalRight.type == TYPE_INFO.TYPE_NUMERIC)
        {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.dblValue = evalLeft.dblValue + evalRight.dblValue;
            ret_val.type = TYPE_INFO.TYPE_NUMERIC;
            ret_val.symbolName = "";
            return ret_val;

        } else if (evalLeft.type == TYPE_INFO.TYPE_INTEGER &&
                evalRight.type == TYPE_INFO.TYPE_INTEGER) {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.intValue = evalLeft.intValue + evalRight.intValue;
            ret_val.type = TYPE_INFO.TYPE_INTEGER;
            ret_val.symbolName = "";
            return ret_val;
        }
        else
        {
            throw new Exception("Type mismatch");
        }

    }

    // Variable

    @Override
    public SymbolInfo visit(Variable var, RUNTIME_CONTEXT context) {
        if (context.getTable() == null) {
            return null;
        } else
        {
            SymbolInfo a = context.getTable().get(var.getName());
            return a;
        }
    }
    // Binary Minus

    @Override
    public SymbolInfo visit(BinaryMinus minus, RUNTIME_CONTEXT context) throws Exception {
        SymbolInfo eval_left = minus.getLeft().accept(this, context);
        SymbolInfo eval_right = minus.getRight().accept(this, context);

       if (eval_left.type == TYPE_INFO.TYPE_NUMERIC &&
                eval_right.type == TYPE_INFO.TYPE_NUMERIC) {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.dblValue = eval_left.dblValue - eval_right.dblValue;
            ret_val.type = TYPE_INFO.TYPE_NUMERIC;
            ret_val.symbolName = "";
            return ret_val;

        } else if (eval_left.type == TYPE_INFO.TYPE_INTEGER &&
                eval_right.type == TYPE_INFO.TYPE_INTEGER) {
           SymbolInfo ret_val = new SymbolInfo();
           ret_val.intValue = eval_left.intValue - eval_right.intValue;
           ret_val.type = TYPE_INFO.TYPE_INTEGER;
           ret_val.symbolName = "";
           return ret_val;
       }
        else
        {
            throw new Exception("Type mismatch");
        }
    }

    // Binary Mul

    @Override
    public SymbolInfo visit(BinaryMul mul, RUNTIME_CONTEXT context) throws Exception {
        SymbolInfo eval_left = mul.getLeft().accept(this, context);
        SymbolInfo eval_right = mul.getRight().accept(this, context);

        if (eval_left.type == TYPE_INFO.TYPE_NUMERIC &&
                eval_right.type == TYPE_INFO.TYPE_NUMERIC) {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.dblValue = eval_left.dblValue * eval_right.dblValue;
            ret_val.type = TYPE_INFO.TYPE_NUMERIC;
            ret_val.symbolName = "";
            return ret_val;

        } else if (eval_left.type == TYPE_INFO.TYPE_INTEGER &&
                eval_right.type == TYPE_INFO.TYPE_INTEGER) {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.intValue = eval_left.intValue * eval_right.intValue;
            ret_val.type = TYPE_INFO.TYPE_INTEGER;
            ret_val.symbolName = "";
            return ret_val;
        }
        else
        {
            throw new Exception("Type mismatch");
        }
    }



    // Binary Div

    @Override
    public SymbolInfo visit(BinaryDiv div, RUNTIME_CONTEXT context) throws Exception {
        SymbolInfo eval_left = div.getLeft().accept(this, context);
        SymbolInfo eval_right = div.getRight().accept(this, context);

        if (eval_left.type == TYPE_INFO.TYPE_NUMERIC &&
                eval_right.type == TYPE_INFO.TYPE_NUMERIC) {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.dblValue = eval_left.dblValue / eval_right.dblValue;
            ret_val.type = TYPE_INFO.TYPE_NUMERIC;
            ret_val.symbolName = "";
            return ret_val;

        } else if (eval_left.type == TYPE_INFO.TYPE_INTEGER &&
                eval_right.type == TYPE_INFO.TYPE_INTEGER) {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.intValue = eval_left.intValue / eval_right.intValue;
            ret_val.type = TYPE_INFO.TYPE_INTEGER;
            ret_val.symbolName = "";
            return ret_val;
        }
        else
        {
            throw new Exception("Type mismatch");
        }
    }



    // Unary Plus

    @Override
    public SymbolInfo visit(UnaryPlus plus, RUNTIME_CONTEXT context) throws Exception {
        SymbolInfo evalRight = plus.getRight().accept(this, context);
        if (evalRight.type == TYPE_INFO.TYPE_NUMERIC)
        {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.dblValue = evalRight.dblValue;
            ret_val.type = TYPE_INFO.TYPE_NUMERIC;
            ret_val.symbolName = "";
            return ret_val;

        } else if (evalRight.type == TYPE_INFO.TYPE_INTEGER)
        {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.dblValue = evalRight.intValue;
            ret_val.type = TYPE_INFO.TYPE_INTEGER;
            ret_val.symbolName = "";
            return ret_val;

        }
        else
        {
            throw new Exception("Type mismatch");
        }
    }



    // Unary Minus

    @Override
    public SymbolInfo visit(UnaryMinus minus, RUNTIME_CONTEXT context) throws Exception {
        SymbolInfo evalLeft =minus.getRight().accept(this, context);
        if (evalLeft.type == TYPE_INFO.TYPE_NUMERIC)
        {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.dblValue = -evalLeft.dblValue;
            ret_val.type = TYPE_INFO.TYPE_NUMERIC;
            ret_val.symbolName = "";
            return ret_val;

        } else if (evalLeft.type == TYPE_INFO.TYPE_INTEGER) {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.intValue = -evalLeft.intValue ;
            ret_val.type = TYPE_INFO.TYPE_INTEGER;
            ret_val.symbolName = "";
            return ret_val;
        }
        else
        {
            throw new Exception("Type mismatch");
        }
    }



    @Override
    public SymbolInfo visit(RelationalExp relationalExp, RUNTIME_CONTEXT context) throws Exception {
        SymbolInfo eval_left = relationalExp.getLeft().accept(this, context);
        SymbolInfo eval_right = relationalExp.getRight().accept(this, context);

        SymbolInfo ret_val = new SymbolInfo();
        if (eval_left.type == TYPE_INFO.TYPE_NUMERIC &&
                eval_right.type == TYPE_INFO.TYPE_NUMERIC) {

            ret_val.type = TYPE_INFO.TYPE_BOOL;
            ret_val.symbolName = "";

            if (relationalExp.getOp() == RELATION_OPERATOR.TOK_EQ)
                ret_val.bolValue = eval_left.dblValue == eval_right.dblValue;
            else if (relationalExp.getOp() == RELATION_OPERATOR.TOK_NEQ)
                ret_val.bolValue = eval_left.dblValue != eval_right.dblValue;
            else if (relationalExp.getOp() == RELATION_OPERATOR.TOK_GT)
                ret_val.bolValue = eval_left.dblValue > eval_right.dblValue;
            else if (relationalExp.getOp() == RELATION_OPERATOR.TOK_GTE)
                ret_val.bolValue = eval_left.dblValue >= eval_right.dblValue;
            else if (relationalExp.getOp() == RELATION_OPERATOR.TOK_LTE)
                ret_val.bolValue = eval_left.dblValue <= eval_right.dblValue;
            else if (relationalExp.getOp() == RELATION_OPERATOR.TOK_LT)
                ret_val.bolValue = eval_left.dblValue < eval_right.dblValue;

            return ret_val;

        } else if (eval_left.type == TYPE_INFO.TYPE_INTEGER &&
                eval_right.type == TYPE_INFO.TYPE_INTEGER) {

            ret_val.type = TYPE_INFO.TYPE_BOOL;
            ret_val.symbolName = "";

            if (relationalExp.getOp() == RELATION_OPERATOR.TOK_EQ)
                ret_val.bolValue = eval_left.intValue == eval_right.intValue;
            else if (relationalExp.getOp() == RELATION_OPERATOR.TOK_NEQ)
                ret_val.bolValue = eval_left.intValue != eval_right.intValue;
            else if (relationalExp.getOp() == RELATION_OPERATOR.TOK_GT)
                ret_val.bolValue = eval_left.intValue > eval_right.intValue;
            else if (relationalExp.getOp() == RELATION_OPERATOR.TOK_GTE)
                ret_val.bolValue = eval_left.intValue >= eval_right.intValue;
            else if (relationalExp.getOp() == RELATION_OPERATOR.TOK_LTE)
                ret_val.bolValue = eval_left.intValue <= eval_right.intValue;
            else if (relationalExp.getOp() == RELATION_OPERATOR.TOK_LT)
                ret_val.bolValue = eval_left.intValue < eval_right.intValue;



            return ret_val;

        }
        else if (eval_left.type == TYPE_INFO.TYPE_STRING &&
                eval_right.type == TYPE_INFO.TYPE_STRING)
        {

            ret_val.type = TYPE_INFO.TYPE_BOOL;
            ret_val.symbolName = "";

            if (relationalExp.getOp() == RELATION_OPERATOR.TOK_EQ)
            {
                ret_val.bolValue =
                        eval_left.strValue.equals(eval_right.strValue);

            }
            else if (relationalExp.getOp() == RELATION_OPERATOR.TOK_NEQ)
            {
                ret_val.bolValue = !(eval_left.strValue.equals(eval_right.strValue));

            }
            else
            {
                ret_val.bolValue = false;

            }


            return ret_val;

        }
        if (eval_left.type == TYPE_INFO.TYPE_BOOL &&
                eval_right.type == TYPE_INFO.TYPE_BOOL)
        {

            ret_val.type = TYPE_INFO.TYPE_BOOL;
            ret_val.symbolName = "";

            if (relationalExp.getOp()  == RELATION_OPERATOR.TOK_EQ)
                ret_val.bolValue = eval_left.bolValue == eval_right.bolValue;
            else if (relationalExp.getOp()  == RELATION_OPERATOR.TOK_NEQ)
                ret_val.bolValue = eval_left.bolValue != eval_right.bolValue;
            else
            {
                ret_val.bolValue = false;

            }
            return ret_val;

        }
        return null;
    }



    @Override
    public SymbolInfo visit(LogicalExp logicalExp, RUNTIME_CONTEXT context) throws Exception {
        SymbolInfo eval_left = logicalExp.getLeft().accept(this, context);
        SymbolInfo eval_right = logicalExp.getRight().accept(this, context);

        if (eval_left.type == TYPE_INFO.TYPE_BOOL &&
                eval_right.type == TYPE_INFO.TYPE_BOOL)
        {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.type = TYPE_INFO.TYPE_BOOL;
            ret_val.symbolName = "";

            if (logicalExp.getOp() == TOKEN.TOK_AND)
                ret_val.bolValue = ( eval_left.bolValue && eval_right.bolValue);
            else if (logicalExp.getOp() == TOKEN.TOK_OR)
                ret_val.bolValue = (eval_left.bolValue || eval_right.bolValue);
            else
            {
                return null;

            }
            return ret_val;

        }

        return null;
    }

    @Override
    public SymbolInfo visit(LogicalNot logicalNot, RUNTIME_CONTEXT context) throws Exception {
        SymbolInfo eval_left = logicalNot.getExp().accept(this, context);


        if (eval_left.type == TYPE_INFO.TYPE_BOOL)
        {
            SymbolInfo ret_val = new SymbolInfo();
            ret_val.type = TYPE_INFO.TYPE_BOOL;
            ret_val.symbolName = "";
            ret_val.bolValue = !eval_left.bolValue;
            return ret_val;
        }
        else
        {
            return null;

        }
    }



    @Override
    public SymbolInfo visit(AssignmentStatement stmt, RUNTIME_CONTEXT context) throws Exception {
        SymbolInfo val = stmt.getExp1().accept(this, context);
        context.getTable().assign(stmt.getVariable(), val);
        return null;
    }

    @Override
    public SymbolInfo visit(IfStatement stmt, RUNTIME_CONTEXT context) throws Exception{
        SymbolInfo mCond = stmt.getCond().accept(this, context);

        if (mCond == null || mCond.type != TYPE_INFO.TYPE_BOOL)
            return null;
        ArrayList stmnts = stmt.getStmnts();
        ArrayList elseStmnts = stmt.getElsePart();
        if (mCond.bolValue == true) {
            for (Object s : stmnts) {
                Statement st = (Statement) s;
                SymbolInfo returnValue = st.accept(this, context);
                if (returnValue != null) {
                    return returnValue;
                }

            }
        } else if (stmt.getElsePart() != null) {
            for (Object s : elseStmnts) {
                Statement st = (Statement) s;
                SymbolInfo returnValue = st.accept(this, context);
                if (returnValue != null) {
                    return returnValue;
                }

            }

        }
        return null;
    }

    @Override
    public SymbolInfo visit(ReturnStatement returnStatement, RUNTIME_CONTEXT context)
            throws Exception {
        SymbolInfo exprValue = returnStatement.getExpression()
                .accept( this, context);
        return exprValue;
    }

    @Override
    public SymbolInfo visit(PrintLineStatement stmt, RUNTIME_CONTEXT context) throws Exception {
        SymbolInfo val = stmt.getExp().accept(this, context);
        System.out.println((val.type == TYPE_INFO.TYPE_NUMERIC  ) ? String.valueOf(val.dblValue) :
                ( val.type == TYPE_INFO.TYPE_STRING ) ? val.strValue :
                        (val.type == TYPE_INFO.TYPE_INTEGER  ) ? String.valueOf(val.intValue):
                val.bolValue ? "TRUE" : "FALSE" );
        return null;
    }

    @Override
    public SymbolInfo visit(PrintStatement stmt, RUNTIME_CONTEXT context) throws Exception {
        SymbolInfo val = stmt.getExp().accept(this, context);
        System.out.print((val.type == TYPE_INFO.TYPE_NUMERIC  ) ? String.valueOf(val.dblValue) :
                ( val.type == TYPE_INFO.TYPE_STRING ) ? val.strValue :
                        (val.type == TYPE_INFO.TYPE_INTEGER  ) ? String.valueOf(val.intValue):
                                val.bolValue ? "TRUE" : "FALSE" );
        return null;
    }

    @Override
    public SymbolInfo visit(VariableDeclStatement stmt, RUNTIME_CONTEXT context) throws Exception {
        context.getTable().add(stmt.getInfo());
        return null;
    }

    @Override
    public SymbolInfo visit(WhileStatement stmt, RUNTIME_CONTEXT context) throws Exception {
        while (true) {
            SymbolInfo info = stmt.getCond().accept(this, context);

            if (info == null || info.type != TYPE_INFO.TYPE_BOOL)
                return null;

            if (info.bolValue != true)
                return null;

            SymbolInfo tsp = null;
            for (Object smt : stmt.getStmnts()) {
                Statement rst = (Statement) smt;
                tsp = rst.accept(this, context);
                if (tsp != null) {
                    return tsp;
                }
            }
        }

    }

    public SymbolInfo visit(IntegerConstant num, RUNTIME_CONTEXT context) {
        return num.getInfo();
    }


    @Override
    public SymbolInfo visit(CallExpression procedureCallExpression, RUNTIME_CONTEXT context
                        ) throws Exception {
        Procedure procedure = procedureCallExpression.getProcedure();

        // Calls the function by passing the arguements
        return procedure.accept(this, context,
                procedureCallExpression.getAcutalParameterExpressions());
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


}
