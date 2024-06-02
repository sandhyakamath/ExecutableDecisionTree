package Builder;

import AST.Expression;
import Parser.RDParser;

public class ExpressionBuilder extends AbstractBuilder {
    public String exprString;
    public ExpressionBuilder(String expr)
    {
        exprString = expr;


    }
    /// <summary>
    ///
    /// </summary>
    /// <returns></returns>
    public Expression getExpression()
    {
        /*try
        {
            RDParser parser = new RDParser(exprString);
            return parser.callExpr();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        return null;
    }
}
