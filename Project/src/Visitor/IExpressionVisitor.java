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
import Compilation.Procedure;
import Compilation.TModule;
import Context.COMPILATION_CONTEXT;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;

import java.util.ArrayList;

public interface IExpressionVisitor {
    SymbolInfo visit(ReturnStatement returnStatement, RUNTIME_CONTEXT context)
            throws Exception;
    SymbolInfo visit(TModule tmodule, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(Procedure proc, RUNTIME_CONTEXT context,
                     ArrayList<Expression> actualParameters) throws Exception;
    SymbolInfo visit(NumericConstant num, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(BooleanConstant bool, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(StringLiteral str, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(BinaryPlus plus, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(Variable var, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(BinaryMinus minus, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(BinaryMul mul, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(BinaryDiv div, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(UnaryPlus plus, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(UnaryMinus minus, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(RelationalExp relationalExp, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(LogicalExp logicalExp, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(LogicalNot logicalNot, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(AssignmentStatement stmt, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(IfStatement stmt, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(PrintStatement stmt, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(PrintLineStatement stmt, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(VariableDeclStatement stmt, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(WhileStatement stmt, RUNTIME_CONTEXT context) throws Exception;
    SymbolInfo visit(IntegerConstant num, RUNTIME_CONTEXT context) throws Exception;
    /*SymbolInfo visit(Procedure proc, RUNTIME_CONTEXT  context) throws Exception;*/
    SymbolInfo visit(CallExpression procedureCallExpression,
                     RUNTIME_CONTEXT context) throws Exception;
}
