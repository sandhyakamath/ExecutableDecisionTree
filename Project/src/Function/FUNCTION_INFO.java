package Function;

import Lexer.TYPE_INFO;

import java.util.ArrayList;

public class FUNCTION_INFO {
    public TYPE_INFO retValue;
    public String name;
    public ArrayList typeInfo;

    public FUNCTION_INFO(String name, TYPE_INFO retValue,
                         ArrayList formals)
    {

        this.retValue = retValue;
        this.typeInfo = formals;
        this.name = name;
    }
}
