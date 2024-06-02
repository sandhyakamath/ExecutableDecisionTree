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
import Lexer.RELATION_OPERATOR;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;

public class TypeCheckVisitor implements ISemanticCheckVisitor {
    @Override
    public TYPE_INFO visit(NumericConstant num, COMPILATION_CONTEXT context) {
        return num.getInfo().type;
    }

    @Override
    public TYPE_INFO visit(BooleanConstant bool, COMPILATION_CONTEXT context) {
        return bool.getInfo().type;
    }
    @Override
    public TYPE_INFO visit(StringLiteral str, COMPILATION_CONTEXT context) {
        return str.getInfo().type;
    }
    @Override
    public TYPE_INFO visit(BinaryPlus plus, COMPILATION_CONTEXT context) throws Exception {
        TYPE_INFO evalLeft = plus.getLeft().accept(this, context);
        TYPE_INFO evalRight = plus.getRight().accept(this, context);

        if (evalLeft == evalRight && evalLeft != TYPE_INFO.TYPE_BOOL)
        {
            return evalLeft;
        }
        else
        {
            throw new Exception("Type mismatch failure");

        }
    }

    @Override
    public TYPE_INFO visit(BinaryMinus minus, COMPILATION_CONTEXT context) throws Exception {
        TYPE_INFO evalLeft = minus.getLeft().accept(this, context);
        TYPE_INFO evalRight = minus.getRight().accept(this, context);

        if (evalLeft == evalRight && evalLeft != TYPE_INFO.TYPE_BOOL)
        {
            return evalLeft;
        }
        else
        {
            throw new Exception("Type mismatch failure");

        }
    }
    @Override
    public TYPE_INFO visit(BinaryMul mul, COMPILATION_CONTEXT context) throws Exception {
        TYPE_INFO evalLeft = mul.getLeft().accept(this, context);
        TYPE_INFO evalRight = mul.getRight().accept(this, context);

        if (evalLeft == evalRight && evalLeft == TYPE_INFO.TYPE_NUMERIC)
        {
            return evalLeft;
        } else if (evalLeft == evalRight && evalLeft == TYPE_INFO.TYPE_INTEGER) {
            return evalLeft;

        }
        else
        {
            throw new Exception("Type mismatch failure");

        }
    }
    @Override
    public TYPE_INFO visit(BinaryDiv div, COMPILATION_CONTEXT context) throws Exception {
        TYPE_INFO evalLeft = div.getLeft().accept(this, context);
        TYPE_INFO evalRight = div.getRight().accept(this, context);

        if (evalLeft == evalRight && evalLeft == TYPE_INFO.TYPE_NUMERIC)
        {
            return evalLeft;
        } else if (evalLeft == evalRight && evalLeft == TYPE_INFO.TYPE_INTEGER) {
            return evalLeft;

        }
        else
        {
            throw new Exception("Type mismatch failure");

        }
    }
    @Override
    public TYPE_INFO visit(UnaryPlus plus, COMPILATION_CONTEXT context) throws Exception {
        TYPE_INFO evalRight = plus.getRight().accept(this, context);

        if (evalRight == TYPE_INFO.TYPE_NUMERIC || evalRight == TYPE_INFO.TYPE_INTEGER)
        {
            return evalRight;
        }
        else
        {
            throw new Exception("Type mismatch failure");

        }
    }

    @Override
    public TYPE_INFO visit(UnaryMinus minus, COMPILATION_CONTEXT context) throws Exception {
        TYPE_INFO evalRight = minus.getRight().accept(this, context);

        if (evalRight == TYPE_INFO.TYPE_NUMERIC || evalRight == TYPE_INFO.TYPE_INTEGER)
        {
            return evalRight;
        }
        else
        {
            throw new Exception("Type mismatch failure");

        }
    }

    @Override
    public TYPE_INFO visit(RelationalExp relationalExp, COMPILATION_CONTEXT context) throws Exception {
        TYPE_INFO eval_left = relationalExp.getLeft().accept(this, context);
        TYPE_INFO eval_right = relationalExp.getRight().accept(this, context);
        RELATION_OPERATOR op = relationalExp.getOp();
        if (eval_left != eval_right)
        {

            throw new Exception("Wrong Type in expression");
        }

        if (eval_left == TYPE_INFO.TYPE_STRING &&
                (!(op == RELATION_OPERATOR.TOK_EQ ||
                        op == RELATION_OPERATOR.TOK_NEQ)))
        {
            throw new Exception("Only == amd != supported for string type ");
        }

        if (eval_left == TYPE_INFO.TYPE_BOOL &&
                (!(op == RELATION_OPERATOR.TOK_EQ ||
                        op == RELATION_OPERATOR.TOK_NEQ)))
        {
            throw new Exception("Only == amd != supported for boolean type ");
        }
        // store the operand type as well
       // optype = eval_left;
        return TYPE_INFO.TYPE_BOOL;
    }


    @Override
    public TYPE_INFO visit(LogicalExp logicalExp, COMPILATION_CONTEXT context) throws Exception {
        TYPE_INFO eval_left = logicalExp.getLeft().accept(this, context);
        TYPE_INFO eval_right = logicalExp.getRight().accept(this, context);

        if (eval_left == eval_right &&
                eval_left == TYPE_INFO.TYPE_BOOL  )
        {
            return TYPE_INFO.TYPE_BOOL;
        }
        else
        {
            throw new Exception("Wrong Type in expression");

        }
    }

    @Override
    public TYPE_INFO visit(LogicalNot logicalNot, COMPILATION_CONTEXT context) throws Exception {
        TYPE_INFO eval_left = logicalNot.getExp().accept(this, context);


        if (eval_left == TYPE_INFO.TYPE_BOOL) {
            return TYPE_INFO.TYPE_BOOL;
        }
        else
        {
            throw new Exception("Wrong Type in expression");

        }
    }

    @Override
    public TYPE_INFO visit(IntegerConstant num, COMPILATION_CONTEXT context) {
        return num.getInfo().type;
    }

    @Override
    public TYPE_INFO visit(Variable variable, COMPILATION_CONTEXT context) {
        if (context.getTable() != null) {
            SymbolInfo a = context.getTable().get(variable.getName());
            if (a != null) {
                return a.type;
            }

        }
        return TYPE_INFO.TYPE_ILLEGAL;
    }

    @Override
    public TYPE_INFO visit(CallExpression exp, COMPILATION_CONTEXT context) {
        Procedure mProc =  exp.getProcedure();
        TYPE_INFO type = exp.getType();
        if (mProc != null)
        {
            type = mProc.accept(this, context);

        }

        return type;
    }

    @Override
    public TYPE_INFO visit(Procedure procedure, COMPILATION_CONTEXT context) {
      //  return TYPE_INFO.TYPE_NUMERIC;
       return procedure.getType();
    }

}
