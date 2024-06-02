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
import AST.Unary.UnaryMinus;
import AST.Unary.UnaryPlus;
import AST.Variable;
import Compilation.Procedure;
import Context.COMPILATION_CONTEXT;
import Lexer.TYPE_INFO;

public interface ISemanticCheckVisitor {
    TYPE_INFO visit(NumericConstant num, COMPILATION_CONTEXT context);
    TYPE_INFO visit(BooleanConstant bool, COMPILATION_CONTEXT context);
    TYPE_INFO visit(StringLiteral str, COMPILATION_CONTEXT context);
    TYPE_INFO visit(BinaryPlus plus, COMPILATION_CONTEXT context) throws Exception;
    TYPE_INFO visit(Variable var, COMPILATION_CONTEXT context);
    TYPE_INFO visit(BinaryMinus minus, COMPILATION_CONTEXT context) throws Exception;
    TYPE_INFO visit(BinaryMul mul, COMPILATION_CONTEXT context) throws Exception;
    TYPE_INFO visit(BinaryDiv div, COMPILATION_CONTEXT context) throws Exception;
    TYPE_INFO visit(UnaryPlus plus, COMPILATION_CONTEXT context) throws Exception;
    TYPE_INFO visit(UnaryMinus minus, COMPILATION_CONTEXT context) throws Exception;
    TYPE_INFO visit(RelationalExp relationalExp, COMPILATION_CONTEXT context) throws Exception;
    TYPE_INFO visit(LogicalExp logicalExp, COMPILATION_CONTEXT context) throws Exception;
    TYPE_INFO visit(LogicalNot logicalNot, COMPILATION_CONTEXT context) throws Exception;
    TYPE_INFO visit(IntegerConstant num, COMPILATION_CONTEXT context);
    TYPE_INFO visit(CallExpression exp, COMPILATION_CONTEXT context);

    TYPE_INFO visit(Procedure procedure, COMPILATION_CONTEXT context);

}
