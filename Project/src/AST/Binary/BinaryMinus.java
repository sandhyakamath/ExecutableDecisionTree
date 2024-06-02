package AST.Binary;

import AST.Expression;
import Context.COMPILATION_CONTEXT;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Visitor.IExpressionVisitor;
import Visitor.ISemanticCheckVisitor;

public class BinaryMinus extends Expression {
    private Expression left;
    private Expression right;
    TYPE_INFO type;

    public BinaryMinus(Expression exp1, Expression exp2) {
        this.left = exp1;
        this.right = exp2;
    }

    public TYPE_INFO getType() {
        return type;
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

    @Override
    public SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT context) throws Exception {
        return visitor.visit(this, context);
    }

    @Override
    public TYPE_INFO accept(ISemanticCheckVisitor visitor, COMPILATION_CONTEXT context) throws Exception {
        return visitor.visit(this, context);
    }
   /* @Override
    public TYPE_INFO typeCheck(COMPILATION_CONTEXT context) throws Exception {
        TYPE_INFO eval_left = left.typeCheck(context);
        TYPE_INFO eval_right = right.typeCheck(context);

        if (eval_left == eval_right && eval_left == TYPE_INFO.TYPE_NUMERIC)
        {
            type = eval_left;
            return type;
        } else if (eval_left == eval_right && eval_left == TYPE_INFO.TYPE_INTEGER)
        {
            type = eval_left;
            return type;
        }
        else
        {
            throw new Exception("Type mismatch failure");

        }
    }*/

}
