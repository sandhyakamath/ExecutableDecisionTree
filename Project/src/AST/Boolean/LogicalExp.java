package AST.Boolean;

import AST.Expression;
import Context.COMPILATION_CONTEXT;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Lexer.TOKEN;
import Visitor.IExpressionVisitor;
import Visitor.ISemanticCheckVisitor;

public class LogicalExp extends Expression {

    private TOKEN op; // &&, ||
    private Expression left, right;
    TYPE_INFO type;

    public TOKEN getOp() {
        return op;
    }

    public void setOp(TOKEN op) {
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

    public LogicalExp(TOKEN op, Expression e1, Expression e2)
    {
        op = op;
        left = e1;
        right = e2;

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

        // The Types should be Boolean...
        // Logical Operators only make sense
        // with Boolean Types

        if (eval_left == eval_right &&
                eval_left == TYPE_INFO.TYPE_BOOL  )
        {
            type = TYPE_INFO.TYPE_BOOL;
            return type;
        }
        else
        {
            throw new Exception("Wrong Type in expression");

        }
    }*/
}
