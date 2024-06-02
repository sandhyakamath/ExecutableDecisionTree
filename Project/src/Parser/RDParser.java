package Parser;

import AST.*;
import AST.Binary.BinaryDiv;
import AST.Binary.BinaryMinus;
import AST.Binary.BinaryMul;
import AST.Binary.BinaryPlus;
import AST.Boolean.LogicalExp;
import AST.Boolean.LogicalNot;
import AST.Boolean.RelationalExp;
import AST.Constant.BooleanConstant;
import AST.Constant.IntegerConstant;
import AST.Constant.NumericConstant;
import AST.Constant.StringLiteral;
import AST.Unary.UnaryMinus;
import AST.Unary.UnaryPlus;
import ASTStatements.*;
import Builder.ProcedureBuilder;
import Builder.TModuleBuilder;
import Compilation.Procedure;
import Compilation.TModule;
import Context.COMPILATION_CONTEXT;
import Helper.CParserException;
import Helper.CSyntaxErrorLog;
import Lexer.Lexer;
import Lexer.TOKEN;
import Lexer.SymbolInfo;
import Lexer.TYPE_INFO;
import Lexer.RELATION_OPERATOR;
import Visitor.TypeCheckVisitor;

import java.util.ArrayList;

public class RDParser extends Lexer {

    TModuleBuilder prog = null;

    public RDParser(String str, String mname) {
        super(str);
        prog = new TModuleBuilder(mname);

    }

    protected TOKEN getNextToken() throws Exception {
        endToken = currentToken;
        currentToken = getToken();
        return currentToken;
    }

    /// <summary>
    ///
    /// </summary>
    /// <returns></returns>
    public Expression callExpr(ProcedureBuilder context) throws Exception {
        currentToken = getToken();
        return expression(context);
    }

    public ArrayList parse(ProcedureBuilder context) throws Exception, CParserException {
        getNextToken();  // Get the Next Token
        //
        // Parse all the statements
        //
        return statementList(context);
    }

    public TModule doParse() throws Exception {
        try {
            /*IExpressionVisitor visitor = new TreeEvaluatorVisitor();
            ProcedureBuilder p = new ProcedureBuilder("MAIN", new COMPILATION_CONTEXT());
            ArrayList stmts = parse(visitor, p);

            for (Object s : stmts) {
                Statement stmt = (Statement) s;
                p.addStatement(stmt);
            }

            Procedure pc = p.getProcedure();

            prog.add(pc);
            return prog.getProgram();*/
            getNext();   // Get The First Valid Token
            return parseFunctions();
        } catch (Exception | CParserException e) {
            System.out.println(e.toString());
            throw new Exception(e);
        }

    }

    public TModule parseFunctions() throws Exception, CParserException {

        while (currentToken == TOKEN.TOK_FUNCTION) {
            ProcedureBuilder b = parseFunction();
            Procedure s = b.getProcedure();

            if (s == null) {
                System.out.println("Error While Parsing Functions");
                return null;
            }

            prog.add(s);
            getNext();
        }

        //
        //  Convert the builder into a program
        //
        return prog.getProgram();
    }

    ProcedureBuilder parseFunction() throws Exception, CParserException {
        //
        // Create a Procedure builder Object
        //
        ProcedureBuilder p = new ProcedureBuilder("", new COMPILATION_CONTEXT(), new TypeCheckVisitor());
        p.setIndex(getIndex());
        if (currentToken != TOKEN.TOK_FUNCTION)
            throw parseError();


        getNext();
        // return type of the Procedure ought to be
        // Boolean , Numeric or String
        if (!(currentToken == TOKEN.TOK_VAR_BOOL ||
                currentToken == TOKEN.TOK_VAR_NUMBER ||
                currentToken == TOKEN.TOK_VAR_STRING ||
                currentToken == TOKEN.TOK_VAR_INTEGER)) {

            throw parseError();

        }

        TYPE_INFO returnType = getType(currentToken);
        if (returnType == null)
            throw parseError();

        p.setReturnType(returnType);

        // Parsing the name of the function
        getNext();
        if (currentToken != TOKEN.TOK_UNQUOTED_STRING) {
            throw parseError();
        }
        p.setProcName(getString());

        getNext();
        if (currentToken != TOKEN.TOK_OPAREN) {
            throw parseError();
        }

        //---- Parse the Formal Parameter list
        formalParameters(p);


        if (currentToken != TOKEN.TOK_CPAREN)
            return null;

        getNext();

        // --------- Parse the Function code
        ArrayList lst = statementList(p);

        if (currentToken != TOKEN.TOK_END) {
            throw new Exception("END expected");
        }

        // Accumulate all statements to
        // Procedure builder
        //
        for (Object s : lst) {
            Statement stmt = (Statement) s;
            p.addStatement(stmt);

        }
        return p;
    }

    void formalParameters(ProcedureBuilder pb) throws Exception {

        if (currentToken != TOKEN.TOK_OPAREN)
            throw new Exception("Opening Parenthesis expected");
        getNext();

        ArrayList lst_types = new ArrayList();

        while (currentToken == TOKEN.TOK_VAR_BOOL ||
                currentToken == TOKEN.TOK_VAR_NUMBER ||
                currentToken == TOKEN.TOK_VAR_STRING ||
                currentToken == TOKEN.TOK_VAR_INTEGER) {
            SymbolInfo inf = new SymbolInfo();

            inf.type = (currentToken == TOKEN.TOK_VAR_BOOL) ?
                    TYPE_INFO.TYPE_BOOL : (currentToken == TOKEN.TOK_VAR_NUMBER) ?
                    TYPE_INFO.TYPE_NUMERIC : (currentToken == TOKEN.TOK_VAR_INTEGER) ?
                    TYPE_INFO.TYPE_INTEGER : TYPE_INFO.TYPE_STRING;

            getNext();
            if (currentToken != TOKEN.TOK_UNQUOTED_STRING) {
                throw new Exception("Variable Name expected");
            }

            inf.symbolName = this.lastStr;
            lst_types.add(inf.type);
            pb.addFormal(inf);
            pb.addLocal(inf);


            getNext();

            if (currentToken != TOKEN.TOK_COMMA) {
                break;
            }
            getNext();
        }

        prog.addFunctionProtoType(pb.getProcName(), pb.getReturnType(), lst_types);


    }

    private ArrayList statementList(ProcedureBuilder context) throws Exception, CParserException {
        ArrayList arr = new ArrayList();
        while ((currentToken != TOKEN.TOK_ELSE) &&
                (currentToken != TOKEN.TOK_ENDIF) &&
                (currentToken != TOKEN.TOK_WEND) &&
                (currentToken != TOKEN.TOK_NULL) &&
                (currentToken != TOKEN.TOK_END)) {
            Statement temp = statement(context);
            if (temp != null) {
                arr.add(temp);
            }
        }
        return arr;
    }

    private Statement statement(ProcedureBuilder context) throws Exception, CParserException {
        Statement retval = null;
        int tempVarIndex = getIndex();
        switch (currentToken) {
            case TOK_VAR_STRING:
            case TOK_VAR_NUMBER:
            case TOK_VAR_INTEGER:
            case TOK_VAR_BOOL:
                retval = parseVariableDeclStatement(context);
                getNext();
                return retval;
            case TOK_PRINT:
                retval = parsePrintStatement(context);
                getNextToken();
                break;
            case TOK_PRINTLN:
                retval = parsePrintLNStatement(context);
                getNextToken();
                break;
            case TOK_UNQUOTED_STRING:
                retval = parseAssignmentStatement(context);
                getNext();
                return retval;
            case TOK_IF:
                retval = parseIfStatement(context);
                getNext();
                return retval;

            case TOK_WHILE:
                retval = parseWhileStatement(context);
                getNext();
                return retval;
            case TOK_RETURN:
                retval = parseReturnStatement(context);
                getNext();
                break;
            default:
                throw new Exception("Invalid statement");
        }
        context.setIndex(tempVarIndex);
        return retval;
    }

    private Statement parsePrintStatement(ProcedureBuilder context) throws Exception {
        getNextToken();
        Expression a = BExpr(context);
        a.accept(context.getVisitor(), context.getContext());
        if (currentToken != TOKEN.TOK_SEMI) {
            throw new Exception("; is expected");
        }
        return new PrintStatement(a);
    }

    private Statement parsePrintLNStatement(ProcedureBuilder context) throws Exception {
        getNextToken();
        Expression exp = expression(context);

        if (currentToken != TOKEN.TOK_SEMI) {
            throw new Exception("; is expected");
        }
        return new PrintLineStatement(exp);
    }

    public Statement parseVariableDeclStatement(ProcedureBuilder ctx) throws CParserException {

        //--- Save the Data type
        TOKEN tok = currentToken;
        getNext();

        if (currentToken == TOKEN.TOK_UNQUOTED_STRING) {
            SymbolInfo symb = new SymbolInfo();
            symb.symbolName = super.lastStr;
            symb.type = (tok == TOKEN.TOK_VAR_BOOL) ?
                    TYPE_INFO.TYPE_BOOL : (tok == TOKEN.TOK_VAR_NUMBER) ?
                    TYPE_INFO.TYPE_NUMERIC : (tok == TOKEN.TOK_VAR_STRING) ? TYPE_INFO.TYPE_STRING : TYPE_INFO.TYPE_INTEGER;
            //---------- Skip to Expect the SemiColon

            getNext();


            if (currentToken == TOKEN.TOK_SEMI) {
                // ----------- Add to the Symbol Table
                // for type analysis
                ctx.getTable().add(symb);

                // --------- return the Object of type
                // --------- VariableDeclStatement
                // This will just store the Variable name
                // to be looked up in the above table
                return new VariableDeclStatement(symb);
            } else {
                CSyntaxErrorLog.addLine("; expected");
                CSyntaxErrorLog.addLine(getCurrentLine(saveIndex()));
                throw new CParserException(-100, ", or ; expected", saveIndex());
            }
        } else {

            CSyntaxErrorLog.addLine("invalid variable declaration");
            CSyntaxErrorLog.addLine(getCurrentLine(saveIndex()));
            throw new CParserException(-100, ", or ; expected", saveIndex());
        }


    }

    public Statement parseAssignmentStatement(ProcedureBuilder ctx) throws Exception, CParserException {

        //
        // Retrieve the variable and look it up in
        // the symbol table ..if not found throw exception
        //
        String variable = super.lastStr;
        SymbolInfo s = ctx.getTable().get(variable);
        /*if (s == null) {
            s = ctx.getContext().getTable().get(variable);
        }*/
        if (s == null) {
            CSyntaxErrorLog.addLine("Variable not found  " + lastStr);
            CSyntaxErrorLog.addLine(getCurrentLine(saveIndex()));
            throw new CParserException(-100, "Variable not found", saveIndex());

        }

        //------------ The next token ought to be an assignment
        // expression....

        getNext();

        if (currentToken != TOKEN.TOK_ASSIGN) {

            CSyntaxErrorLog.addLine("= expected");
            CSyntaxErrorLog.addLine(getCurrentLine(saveIndex()));
            throw new CParserException(-100, "= expected", saveIndex());

        }

        //-------- Skip the token to start the expression
        // parsing on the RHS
        getNext();
        Expression exp = BExpr(ctx);

        //------------ Do the type analysis ...

        if (exp.accept(ctx.getVisitor(), ctx.getContext()) != s.type) {
            throw new Exception("Type mismatch in assignment");

        }

        // -------------- End of statement ( ; ) is expected
        if (currentToken != TOKEN.TOK_SEMI) {
            CSyntaxErrorLog.addLine("; expected");
            CSyntaxErrorLog.addLine(getCurrentLine(saveIndex()));
            throw new CParserException(-100, " ; expected", -1);

        }
        // return an instance of AssignmentStatement node..
        //   s => Symbol info associated with variable
        //   exp => to evaluated and assigned to symbol_info
        return new AssignmentStatement(s, exp);

    }

    public Statement parseIfStatement(ProcedureBuilder pb) throws Exception, CParserException {
        getNext();
        ArrayList true_part = null;
        ArrayList false_part = null;
        Expression exp = BExpr(pb);  // Evaluate Expression


        if (pb.accept(exp) != TYPE_INFO.TYPE_BOOL) {
            throw new Exception("Expects a boolean expression");

        }


        if (currentToken != TOKEN.TOK_THEN) {
            CSyntaxErrorLog.addLine(" Then Expected");
            CSyntaxErrorLog.addLine(getCurrentLine(saveIndex()));
            throw new CParserException(-100, "Then Expected", saveIndex());

        }

        getNext();

        true_part = statementList(pb);

        if (currentToken == TOKEN.TOK_ENDIF) {
            return new IfStatement(exp, true_part, false_part);
        }


        if (currentToken != TOKEN.TOK_ELSE) {

            throw new Exception("ELSE expected");
        }

        getNext();
        false_part = statementList(pb);

        if (currentToken != TOKEN.TOK_ENDIF) {
            throw new Exception("END IF EXPECTED");

        }

        return new IfStatement(exp, true_part, false_part);

    }

    public Statement parseWhileStatement(ProcedureBuilder pb) throws Exception, CParserException {

        getNext();

        Expression exp = BExpr(pb);
        if (pb.accept(exp) != TYPE_INFO.TYPE_BOOL) {
            throw new Exception("Expects a boolean expression");

        }

        ArrayList body = statementList(pb);
        if ((currentToken != TOKEN.TOK_WEND)) {
            CSyntaxErrorLog.addLine("Wend Expected");
            CSyntaxErrorLog.addLine(getCurrentLine(saveIndex()));
            throw new CParserException(-100, "Wend Expected", saveIndex());

        }


        return new WhileStatement(exp, body);

    }

    public Statement parseReturnStatement(ProcedureBuilder pb) throws Exception, CParserException {

        getNext();
        Expression exp = BExpr(pb);
        if (currentToken != TOKEN.TOK_SEMI) {
            CSyntaxErrorLog.addLine("; expected");
            CSyntaxErrorLog.addLine(getCurrentLine(saveIndex()));
            throw new CParserException(-100, " ; expected", -1);

        }
        pb.accept(exp);
        return new ReturnStatement(exp);

    }

    /// <summary>
    ///    Convert a token to Relational Operator
    /// </summary>
    /// <param name="tok"></param>
    /// <returns></returns>
    private RELATION_OPERATOR getRelOp(TOKEN tok) {
        if (tok == TOKEN.TOK_EQ)
            return RELATION_OPERATOR.TOK_EQ;
        else if (tok == TOKEN.TOK_NEQ)
            return RELATION_OPERATOR.TOK_NEQ;
        else if (tok == TOKEN.TOK_GT)
            return RELATION_OPERATOR.TOK_GT;
        else if (tok == TOKEN.TOK_GTE)
            return RELATION_OPERATOR.TOK_GTE;
        else if (tok == TOKEN.TOK_LT)
            return RELATION_OPERATOR.TOK_LT;
        else
            return RELATION_OPERATOR.TOK_LTE;


    }

    public Expression BExpr(ProcedureBuilder pb) throws Exception {
        TOKEN lToken;
        Expression RetValue = LExpr(pb);
        while (currentToken == TOKEN.TOK_AND || currentToken == TOKEN.TOK_OR) {
            lToken = currentToken;
            currentToken = getNext();
            Expression e2 = LExpr(pb);
            RetValue = new LogicalExp(lToken, RetValue, e2);

        }
        return RetValue;

    }

    public Expression LExpr(ProcedureBuilder pb) throws Exception {
        TOKEN lToken;
        Expression retValue = expression(pb);
        while (currentToken == TOKEN.TOK_GT ||
                currentToken == TOKEN.TOK_LT ||
                currentToken == TOKEN.TOK_GTE ||
                currentToken == TOKEN.TOK_LTE ||
                currentToken == TOKEN.TOK_NEQ ||
                currentToken == TOKEN.TOK_EQ) {
            lToken = currentToken;
            currentToken = getNext();
            Expression e2 = expression(pb);
            RELATION_OPERATOR relop = getRelOp(lToken);
            retValue = new RelationalExp(relop, retValue, e2);


        }
        return retValue;

    }

    /// <summary>
    ///
    /// </summary>
    /// <returns></returns>
    public Expression expression(ProcedureBuilder context) throws Exception {
        TOKEN l_token;
        Expression retValue = term(context);
        while (currentToken == TOKEN.TOK_PLUS || currentToken == TOKEN.TOK_SUB) {
            l_token = currentToken;
            currentToken = getToken();
            Expression e1 = expression(context);
            if (l_token == TOKEN.TOK_PLUS)
                retValue = new BinaryPlus(retValue, e1);
            else
                retValue = new BinaryMinus(retValue, e1);
        }

        return retValue;

    }

    /// <summary>
    ///
    /// </summary>
    public Expression term(ProcedureBuilder context) throws Exception {
        TOKEN l_token;
        Expression retValue = factor(context);

        while (currentToken == TOKEN.TOK_MUL || currentToken == TOKEN.TOK_DIV) {
            l_token = currentToken;
            currentToken = getToken();


            Expression e1 = term(context);
            if (l_token == TOKEN.TOK_MUL)
                retValue = new BinaryMul(retValue, e1);
            else
                retValue = new BinaryDiv(retValue, e1);

        }

        return retValue;
    }

    /// <summary>
    ///
    /// </summary>
    public Expression factor(ProcedureBuilder context) throws Exception {
        TOKEN l_token;
        Expression retValue = null;
        if (currentToken == TOKEN.TOK_NUMERIC) {

            retValue = new NumericConstant(getNumber());
            currentToken = getToken();

        } else if (currentToken == TOKEN.TOK_INTEGER) {
            retValue = new IntegerConstant(getInteger());
            currentToken = getToken();
        } else if (currentToken == TOKEN.TOK_STRING) {
            retValue = new StringLiteral(lastStr);
            currentToken = getToken();
        } else if (currentToken == TOKEN.TOK_BOOL_FALSE ||
                currentToken == TOKEN.TOK_BOOL_TRUE) {
            retValue = new BooleanConstant(
                    currentToken == TOKEN.TOK_BOOL_TRUE ? true : false);
            currentToken = getToken();
        } else if (currentToken == TOKEN.TOK_OPAREN) {

            currentToken = getToken();

            retValue = BExpr(context);  // Recurse

            if (currentToken != TOKEN.TOK_CPAREN) {
                System.out.println("Missing Closing Parenthesis\n");
                throw new Exception();

            }
            currentToken = getToken();
        } else if (currentToken == TOKEN.TOK_PLUS || currentToken == TOKEN.TOK_SUB) {
            l_token = currentToken;
            currentToken = getToken();
            retValue = factor(context);

            if (l_token == TOKEN.TOK_PLUS)
                retValue = new UnaryPlus(retValue);
            else
                retValue = new UnaryMinus(retValue);
        } else if (currentToken == TOKEN.TOK_NOT) {
            l_token = currentToken;
            currentToken = getToken();
            retValue = factor(context);

            retValue = new LogicalNot(retValue);
        } else if (currentToken == TOKEN.TOK_UNQUOTED_STRING) {
            ///
            ///  Variables
            ///
            String str = super.lastStr;
            if (!prog.IsFunction(str)) {
                SymbolInfo info = context.getTable().get(str);

                if (info == null)
                    throw new Exception("Undefined symbol");

                getNextToken();
                return new Variable(info);
            }
            //
            // P can be null , if we are parsing a
            // recursive function call
            //
            Procedure p = prog.getProc(str);
            // It is a Function Call
            // Parse the function invocation
            //
            Expression ptr = parseCallProc(context, p);
            if(ptr.getType() == null) {
                ptr.setType(context.getReturnType());
            }
            getNext();
            return ptr;
        } else {

            System.out.println("Illegal Token");
            throw new Exception();
        }


        return retValue;

    }

    public Expression parseCallProc(ProcedureBuilder pb, Procedure p) throws Exception {
        getNext();

        if (currentToken != TOKEN.TOK_OPAREN) {
            throw new Exception("Opening Parenthesis expected");
        }

        getNext();

        ArrayList actualparams = new ArrayList();

        while (true) {
            // Evaluate Each Expression in the
            // parameter list and populate actualparams
            // list
            Expression exp = BExpr(pb);
            // do type analysis
            exp.accept(pb.getVisitor(), pb.getContext());
            // if , there are more parameters
            if (currentToken == TOKEN.TOK_COMMA) {
                actualparams.add(exp);
                getNext();
                continue;
            }


            if (currentToken != TOKEN.TOK_CPAREN) {
                throw new Exception("Expected paranthesis");
            } else {
                // Add the last parameters
                actualparams.add(exp);
                break;

            }
        }

        // if p is null , that means it is a
        // recursive call. Being a one pass
        // compiler , we need to wait till
        // the parse process to be over to
        // resolve the Procedure.
        //
        //
        if (p != null)
            return new CallExpression(p, actualparams);
        else {
            System.out.println("P is null ************");
            return new CallExpression(pb.getProcName(),
                    true,  // recurse !
                    actualparams);
        }


    }

    private TYPE_INFO getType(TOKEN tok) throws Exception {
        if (tok == TOKEN.TOK_VAR_NUMBER) {
            return TYPE_INFO.TYPE_NUMERIC;
        } else if (tok == TOKEN.TOK_VAR_STRING) {
            return TYPE_INFO.TYPE_STRING;
        } else if (tok == TOKEN.TOK_VAR_BOOL) {
            return TYPE_INFO.TYPE_BOOL;
        } else if (tok == TOKEN.TOK_VAR_INTEGER) {
            return TYPE_INFO.TYPE_INTEGER;
        } else {
            throw parseError();
        }
    }
    private Exception parseError() {
        int index = getIndex();
        if (endToken == TOKEN.TOK_STRING) {
            index -= getString().length();
        }
        String errorMessage = "Slang Compile Time Error - SYNTAX ERROR AT: "
                + getCurrentLine(index);
        return new Exception(errorMessage);
    }

}
