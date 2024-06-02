package AST.Boolean;

import AST.Expression;
import Context.COMPILATION_CONTEXT;
import Context.RUNTIME_CONTEXT;
import Lexer.RELATION_OPERATOR;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Visitor.IExpressionVisitor;
import Visitor.ISemanticCheckVisitor;

public class RelationalExp extends Expression {
    private RELATION_OPERATOR op;
    private Expression left, right;
    TYPE_INFO type;

    public RelationalExp(RELATION_OPERATOR op, Expression e1, Expression e2)
    {
        this.op = op;
        left = e1;
        right = e2;

    }

    public RELATION_OPERATOR getOp() {
        return op;
    }

    public void setOp(RELATION_OPERATOR op) {
        this.op = op;
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public TYPE_INFO getType() {
        return type;
    }

    public void setType(TYPE_INFO type) {
        this.type = type;
    }

    @Override
    public SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT context) throws Exception {
        return visitor.visit(this, context);
    }

    @Override
    public TYPE_INFO accept(ISemanticCheckVisitor visitor, COMPILATION_CONTEXT context) throws Exception {
        return visitor.visit(this, context);
    }

    /*@Override
    public TYPE_INFO typeCheck(COMPILATION_CONTEXT context) throws Exception {
        TYPE_INFO eval_left = left.typeCheck(context);
        TYPE_INFO eval_right = right.typeCheck(context);

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
        optype = eval_left;
        type = TYPE_INFO.TYPE_BOOL;
        return type;
    }*/
}
