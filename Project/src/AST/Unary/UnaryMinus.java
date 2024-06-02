package AST.Unary;////////////////////////////////////////////////////////
// This class supports Unary Operators like +, -, *,/
//

import AST.Expression;
import Context.COMPILATION_CONTEXT;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Visitor.IExpressionVisitor;
import Visitor.ISemanticCheckVisitor;

public class UnaryMinus extends Expression {

    private Expression right;
    TYPE_INFO type;

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public UnaryMinus(Expression exp) {
        this.right = exp;
    }

    public TYPE_INFO getType() {
        return type;
    }

    @Override
    public SymbolInfo accept (IExpressionVisitor visitor, RUNTIME_CONTEXT context) throws Exception {
        return visitor.visit(this, context);
    }

    @Override
    public TYPE_INFO accept(ISemanticCheckVisitor visitor, COMPILATION_CONTEXT context) throws Exception {
        return visitor.visit(this, context);
    }

    /*@Override
    public TYPE_INFO typeCheck(COMPILATION_CONTEXT context) throws Exception {
        TYPE_INFO eval_right = right.typeCheck(context);

        if (eval_right == TYPE_INFO.TYPE_NUMERIC)
        {
            type = eval_right;
            return type;
        } else if (eval_right == TYPE_INFO.TYPE_INTEGER)
        {
            type = eval_right;
            return type;
        }
        else
        {
            throw new Exception("Type mismatch failure");

        }
    }*/

}
