package Helper;

public class CParserException extends Throwable {
    private int errorCode;
    private String errorString;
    private int lexicalOffset;
    /// <summary>
    ///   Ctor
    /// </summary>
    /// <param name="pErrorCode"></param>
    /// <param name="pErrorString"></param>
    /// <param name="pLexical_Offset"></param>

    public CParserException(int pErrorCode,
                            String pErrorString,
                            int pLexicalOffset)
    {
        errorCode = pErrorCode;
        errorString = pErrorString;
        lexicalOffset = pLexicalOffset;
    }
    /// <summary>
    ///
    /// </summary>
    /// <returns></returns>
    public int getErrorCode()
    {
        return errorCode;
    }
    /// <summary>
    ///
    /// </summary>
    /// <returns></returns>
    public String getErrorString()
    {
        return errorString;
    }
    /// <summary>
    ///
    /// </summary>
    /// <returns></returns>

    public int getLexicalOffset()
    {
        return lexicalOffset;
    }
    /// <summary>
    ///
    /// </summary>
    /// <param name="lex"></param>

    public void setLexicalOffset(int lex)
    {
        lexicalOffset = lex;
    }
    /// <summary>
    ///
    /// </summary>
    /// <param name="pStr"></param>

    public void setErrorString(String pStr)
    {
        errorString = pStr;
    }

}
