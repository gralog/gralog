package gralog.modallogic.parser;

import java_cup.runtime.SymbolFactory;
import java.lang.StringBuffer;

%%
%unicode
%cup
%class ModalLogicScanner
%{
    public ModalLogicScanner(java.io.InputStream r, SymbolFactory sf){
        this(r);
        this.sf=sf;
    }
    private SymbolFactory sf;
    private StringBuffer string = new StringBuffer();
%}




%eofval{
    return sf.newSymbol("EOF",ModalLogicScannerToken.EOF);
%eofval}




%state STRING




%%
<YYINITIAL> 
{
[ \t\r\n\f]   { /* ignore white space. */ }

"<"           { return sf.newSymbol("<",ModalLogicScannerToken.LT); }
">"           { return sf.newSymbol(">",ModalLogicScannerToken.GT); }
"["           { return sf.newSymbol("[",ModalLogicScannerToken.BRACKETLEFT); }
"]"           { return sf.newSymbol("]",ModalLogicScannerToken.BRACKETRIGHT); }
"("           { return sf.newSymbol("(",ModalLogicScannerToken.PARENTHESISLEFT); }
")"           { return sf.newSymbol(")",ModalLogicScannerToken.PARENTHESISRIGHT); }

"\\neg"       { return sf.newSymbol("\\neg",ModalLogicScannerToken.NEG); }
"\\wedge"     { return sf.newSymbol("\\wedge",ModalLogicScannerToken.WEDGE); }
"\\vee"       { return sf.newSymbol("\\vee",ModalLogicScannerToken.VEE); }
"\\bot"       { return sf.newSymbol("\\bot",ModalLogicScannerToken.BOT); }
"\\top"       { return sf.newSymbol("\\top",ModalLogicScannerToken.TOP); }

\"            { string.setLength(0); yybegin(STRING); }
[A-Za-z0-9]+  { string.setLength(0); string.append(yytext()); return sf.newSymbol("simple", ModalLogicScannerToken.STRING, string.toString()); }
.             { System.err.println("Illegal character: "+yytext()); }
}

<STRING>
{
\"            { yybegin(YYINITIAL); return sf.newSymbol("complex", ModalLogicScannerToken.STRING, string.toString()); }
.             { string.append(yytext()); }
}
