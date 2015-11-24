package gralog.automaton.regularexpression.parser;

import java_cup.runtime.SymbolFactory;
import java.lang.StringBuffer;

%%
%unicode
%cup
%class RegularExpressionScanner
%{
    public RegularExpressionScanner(java.io.InputStream r, SymbolFactory sf){
        this(r);
        this.sf=sf;
    }
    private SymbolFactory sf;
    private StringBuffer string = new StringBuffer();
%}



%eofval{
    return sf.newSymbol("EOF",RegularExpressionScannerToken.EOF);
%eofval}



%%

[ \t\r\n\f]   { /* ignore white space. */ }

"("           { return sf.newSymbol("(",RegularExpressionScannerToken.PARENTHESISLEFT); }
")"           { return sf.newSymbol(")",RegularExpressionScannerToken.PARENTHESISRIGHT); }
"*"           { return sf.newSymbol("[",RegularExpressionScannerToken.KLEENESTAR); }
"+"|"\|"      { return sf.newSymbol("]",RegularExpressionScannerToken.ALTERNATION); }

[A-Za-z0-9]+  { string.setLength(0); string.append(yytext()); return sf.newSymbol("simple", RegularExpressionScannerToken.STRING, string.toString()); }
.             { System.err.println("Illegal character: "+yytext()); }
