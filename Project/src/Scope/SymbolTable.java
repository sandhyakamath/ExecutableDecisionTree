package Scope;

import AST.Variable;
import Lexer.Lexer;
import Lexer.SymbolInfo;

import java.util.Hashtable;

public class SymbolTable {

    public Hashtable<String, SymbolInfo> ht = new Hashtable<>();

    public boolean add(SymbolInfo s)
    {
        ht.put(s.symbolName, s);
        return true;
    }

    public SymbolInfo get(String name)
    {
        return ht.get(name);
    }

    public void assign(Variable var, SymbolInfo value)
    {
        value.symbolName = var.getName();
        ht.put(var.getName(), value);
    }

    public void assign(String var, SymbolInfo value)
    {
        ht.put(var, value);
    }
}
