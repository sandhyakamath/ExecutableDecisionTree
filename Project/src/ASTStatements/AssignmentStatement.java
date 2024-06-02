package ASTStatements;

import AST.Expression;
import AST.Variable;
import Context.RUNTIME_CONTEXT;
import Lexer.SymbolInfo;
import Visitor.IExpressionVisitor;

public class AssignmentStatement extends Statement{
    private Variable variable;
    private Expression exp1;

    public AssignmentStatement(Variable var, Expression e)
    {
        variable = var;
        exp1 = e;

    }

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public Expression getExp1() {
        return exp1;
    }

    public void setExp1(Expression exp1) {
        this.exp1 = exp1;
    }

    public AssignmentStatement(SymbolInfo var, Expression e)
    {
        variable = new Variable(var);
        exp1 = e;

    }
    @Override
    public SymbolInfo accept(IExpressionVisitor visitor, RUNTIME_CONTEXT context) throws Exception {
        /*SymbolInfo val = exp1.accept(visitor, context);
        context.getTable().assign(variable, val);
        return null;*/

        return visitor.visit(this, context);
    }
}
