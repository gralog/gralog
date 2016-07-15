package gralog.computationtreelogic.parser;

import java_cup.runtime.SymbolFactory;
import java.lang.StringBuffer;

%%
%unicode
%cup
%class ComputationTreeLogicScanner
%{
    public ComputationTreeLogicScanner(java.io.InputStream r, SymbolFactory sf){
        this(r);
        this.sf=sf;
    }
    private SymbolFactory sf;
    private StringBuffer string = new StringBuffer();
%}




%eofval{
    return sf.newSymbol("EOF",ComputationTreeLogicScannerToken.EOF);
%eofval}




%state STRING




%%
<YYINITIAL> 
{
[ \t\r\n\f]   { /* ignore white space. */ }

"("           { return sf.newSymbol("(",ComputationTreeLogicScannerToken.PARENTHESISLEFT); }
")"           { return sf.newSymbol(")",ComputationTreeLogicScannerToken.PARENTHESISRIGHT); }

"A"           { return sf.newSymbol("A",ComputationTreeLogicScannerToken.ALWAYS); }
"E"           { return sf.newSymbol("E",ComputationTreeLogicScannerToken.EXISTS); }

"X"           { return sf.newSymbol("X",ComputationTreeLogicScannerToken.NEXT); }
"G"           { return sf.newSymbol("G",ComputationTreeLogicScannerToken.GLOBALLY); }
"F"           { return sf.newSymbol("F",ComputationTreeLogicScannerToken.FINALLY); }
"U"           { return sf.newSymbol("U",ComputationTreeLogicScannerToken.UNTIL); }

"\\neg"       { return sf.newSymbol("\\neg",ComputationTreeLogicScannerToken.NEG); }
"\\wedge"     { return sf.newSymbol("\\wedge",ComputationTreeLogicScannerToken.WEDGE); }
"\\vee"       { return sf.newSymbol("\\vee",ComputationTreeLogicScannerToken.VEE); }
"\\bot"       { return sf.newSymbol("\\bot",ComputationTreeLogicScannerToken.BOT); }
"\\top"       { return sf.newSymbol("\\top",ComputationTreeLogicScannerToken.TOP); }

\"            { string.setLength(0); yybegin(STRING); }
[A-Za-z0-9]+  { string.setLength(0); string.append(yytext()); return sf.newSymbol("simple", ComputationTreeLogicScannerToken.STRING, string.toString()); }
.             { System.err.println("Illegal character: "+yytext()); }
}

<STRING>
{
\"            { yybegin(YYINITIAL); return sf.newSymbol("complex", ComputationTreeLogicScannerToken.STRING, string.toString()); }
.             { string.append(yytext()); }
}
