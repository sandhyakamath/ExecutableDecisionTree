package Lexer;

public class Lexer {

    String exp; // Expression string
    int index; // index into a character
    int length; // Length of the string
    double number; // Last grabbed number from the stream
    protected ValueTable[] keyword = null;
    public String lastStr;
    protected TOKEN currentToken;  // Current Token
    protected TOKEN endToken;     // Penultimate token
    int integer;

    // ctor
    public Lexer(String expr) {
        exp = expr;
        length = exp.length();
        index = 0;

        keyword = new ValueTable[17];
        keyword[0] = new ValueTable(TOKEN.TOK_BOOL_FALSE, "FALSE");
        keyword[1] = new ValueTable(TOKEN.TOK_BOOL_TRUE, "TRUE");
        keyword[2] = new ValueTable(TOKEN.TOK_VAR_STRING, "STRING");
        keyword[3] = new ValueTable(TOKEN.TOK_VAR_BOOL, "BOOLEAN");
        keyword[4] = new ValueTable(TOKEN.TOK_VAR_NUMBER, "NUMERIC");
        keyword[5] = new ValueTable(TOKEN.TOK_VAR_INTEGER, "INTEGER");
        keyword[6] = new ValueTable(TOKEN.TOK_PRINT, "PRINT");
        keyword[7] = new ValueTable(TOKEN.TOK_PRINTLN, "PRINTLINE");
        keyword[8] = new ValueTable(TOKEN.TOK_IF, "IF");
        keyword[9] = new ValueTable(TOKEN.TOK_WHILE, "WHILE");
        keyword[10] = new ValueTable(TOKEN.TOK_WEND, "WEND");
        keyword[11] = new ValueTable(TOKEN.TOK_ELSE, "ELSE");
        keyword[12] = new ValueTable(TOKEN.TOK_ENDIF, "ENDIF");
        keyword[13] = new ValueTable(TOKEN.TOK_THEN, "THEN");
        keyword[14] = new ValueTable(TOKEN.TOK_END, "END");
        keyword[15] = new ValueTable(TOKEN.TOK_FUNCTION, "FUNCTION");
        keyword[16] = new ValueTable(TOKEN.TOK_RETURN, "RETURN");
    }

    protected TOKEN getNext() {
        endToken = currentToken;
        currentToken = getToken();
        return currentToken;
    }

    public int saveIndex()
    {
        return index;
    }
    protected int getIndex() {
        return index;
    }

    protected String getString() {
        return this.lastStr;
    }
    public String getCurrentLine(int pindex)
    {
        int tindex = pindex;
        if (pindex >= length)
        {
            tindex = length - 1;
        }
        while (tindex > 0 && exp.charAt(tindex) != '\n')
            tindex--;

        if (exp.charAt(tindex) == '\n')
            tindex++;

        String currentLine = "";

        while (tindex < length && (exp.charAt(tindex) != '\n'))
        {
            currentLine = currentLine + exp.charAt(tindex);
            tindex++;
        }

        return currentLine + "\n";

    }

    public String getPreviousLine(int pindex)
    {

        int tindex = pindex;
        while (tindex > 0 && exp.charAt(tindex) != '\n')
            tindex--;

        if (exp.charAt(tindex) == '\n')
            tindex--;
        else
            return "";

        while (tindex > 0 && exp.charAt(tindex) != '\n')
            tindex--;


        if (exp.charAt(tindex) == '\n')
            tindex--;


        String CurrentLine = "";

        while (tindex < length && (exp.charAt(tindex) != '\n'))
        {
            CurrentLine = CurrentLine + exp.charAt(tindex);
            tindex++;
        }

        return CurrentLine + "\n";



    }

    public void restoreIndex(int index)
    {
        this.index = index;

    }

    private String extractString()
    {
        String retValue = "";
        while (index < exp.length() &&
                (Character.isLetterOrDigit(exp.charAt(index)) || exp.charAt(index) == '_'))
        {
            retValue = retValue + exp.charAt(index);
            index++;
        }
        return retValue;
    }

    /**
     *  Skip to END OF LINE
     */
    public void SkipToEoln()
    {
        while (index < length && (exp.charAt(index) != '\r'))
            index++;

        if (index == length)
            return;

        if (exp.charAt(index + 1) == '\n')
        {
            index += 2;
            return;
        }
        index++;
    }

    /**
     *     grab the token from stream
     */
    public TOKEN getToken() {
        restart:
        while (true) {
            // label
            TOKEN tok = TOKEN.ILLEGAL_TOKEN;

            // Skip the white space
            while (index < length &&
                    (exp.charAt(index) == ' ' || exp.charAt(index) == '\t'))
                index++;

            // End of string ? return NULL
            if (index == length)
                return TOKEN.TOK_NULL;

            switch (exp.charAt(index)) {
                case '\r':
                case '\n':
                    index++;
                    continue restart;
                case '+':
                    tok = TOKEN.TOK_PLUS;
                    index++;
                    break;
                case '-':
                    tok = TOKEN.TOK_SUB;
                    index++;
                    break;
                case '*':
                    tok = TOKEN.TOK_MUL;
                    index++;
                    break;
                case ',':
                    tok = TOKEN.TOK_COMMA;
                    index++;
                    break;
                case '(':
                    tok = TOKEN.TOK_OPAREN;
                    index++;
                    break;
                case ')':
                    tok = TOKEN.TOK_CPAREN;
                    index++;
                    break;
                case ';':
                    tok = TOKEN.TOK_SEMI;
                    index++;
                    break;
                case '!':
                    if (exp.charAt(index+1) == '=')
                    {
                        tok = TOKEN.TOK_NEQ;
                        index += 2;
                    } else {
                        tok = TOKEN.TOK_NOT;
                        index++;
                    }
                    break;
                case '>':
                    if (exp.charAt(index+1) == '=')
                    {
                        tok = TOKEN.TOK_GTE;
                        index += 2;
                    }
                    else
                    {
                        tok = TOKEN.TOK_GT;
                        index++;
                    }
                    break;
                case '<':
                    if (exp.charAt(index+1) == '=')
                    {
                        tok = TOKEN.TOK_LTE;
                        index += 2;
                    }
                    else if (exp.charAt(index+1) == '>')
                    {
                        tok = TOKEN.TOK_NEQ;
                        index += 2;
                    }
                    else
                    {
                        tok = TOKEN.TOK_LT;
                        index++;
                    }
                    break;
                case '=':
                    if (exp.charAt(index+1) == '=')
                    {
                        tok = TOKEN.TOK_EQ;
                        index += 2;
                    }
                    else
                    {
                        tok = TOKEN.TOK_ASSIGN;
                        index++;
                    }
                    break;
                case '&':
                    if (exp.charAt(index+1) == '&')
                    {
                        tok = TOKEN.TOK_AND;
                        index += 2;
                    }
                    else
                    {
                        tok = TOKEN.ILLEGAL_TOKEN;
                        index++;
                    }
                    break;
                case '|':
                    if (exp.charAt(index+1) == '|')
                    {
                        tok = TOKEN.TOK_OR;
                        index += 2;
                    }
                    else
                    {
                        tok = TOKEN.ILLEGAL_TOKEN;
                        index++;
                    }
                    break;
                case '/':

                    if (exp.charAt(index+1) == '/')
                    {
                        SkipToEoln();
                        continue restart;
                    }
                    else
                    {
                        tok = TOKEN.TOK_DIV;
                        index++;
                    }
                    break;
                case '"':
                    String x = "";
                    index++;
                    while (index < length && exp.charAt(index)  != '"')
                    {
                        x = x + exp.charAt(index) ;
                        index++;
                    }

                    if (index == length)
                    {
                        tok = TOKEN.ILLEGAL_TOKEN;
                        return tok;
                    }
                    else
                    {
                        index++;
                        lastStr = x;
                        tok = TOKEN.TOK_STRING;
                        return tok;
                    }
                default:
                    if (Character.isDigit(exp.charAt(index))) {
                        String str = "";
                        while (index < length &&
                                (exp.charAt(index) == '0' ||
                                        exp.charAt(index) == '1' ||
                                        exp.charAt(index) == '2' ||
                                        exp.charAt(index) == '3' ||
                                        exp.charAt(index) == '4' ||
                                        exp.charAt(index) == '5' ||
                                        exp.charAt(index) == '6' ||
                                        exp.charAt(index) == '7' ||
                                        exp.charAt(index) == '8' ||
                                        exp.charAt(index) == '9')) {
                            str += String.valueOf(exp.charAt(index));
                            index++;
                        }
                        if (exp.charAt(index) == '.') {
                            str = str + ".";
                            index++;
                            while (index < length && (exp.charAt(index) == '0' ||
                                    exp.charAt(index) == '1' ||
                                    exp.charAt(index) == '2' ||
                                    exp.charAt(index) == '3' ||
                                    exp.charAt(index) == '4' ||
                                    exp.charAt(index) == '5' ||
                                    exp.charAt(index) == '6' ||
                                    exp.charAt(index) == '7' ||
                                    exp.charAt(index) == '8' ||
                                    exp.charAt(index) == '9')) {
                                str += String.valueOf(exp.charAt(index));
                                index++;
                            }
                            number = Double.parseDouble(str);
                            return TOKEN.TOK_NUMERIC;
                        }
                        number = Double.parseDouble(str);
                        tok = TOKEN.TOK_NUMERIC;

                    } else if (Character.isLetter(exp.charAt(index))) {

                        String temp = String.valueOf((exp.charAt(index)));
                        index++;
                        while (index < length && ((Character.isLetterOrDigit(exp.charAt(index))) ||
                                exp.charAt(index) == '_')) {
                            temp += exp.charAt(index);
                            index++;
                        }

                        temp = temp.toUpperCase();

                        for (int i = 0; i < this.keyword.length; ++i) {
                            if (keyword[i].Value.compareTo(temp) == 0)
                                return keyword[i].tok;

                        }


                        this.lastStr = temp;


                        return TOKEN.TOK_UNQUOTED_STRING;


                    } else {
                        return TOKEN.ILLEGAL_TOKEN;
                    }

                    break;
            }
            return tok;

        }
    }
    public double getNumber () {
        return this.number;
    }

    public int getInteger () {
        return this.integer;
    }

}
