package gralog.modalmucalculus.parser;

import java_cup.runtime.SymbolFactory;
import java.lang.StringBuffer;

%%
%unicode
%cup
%class ModalMuCalculusScanner
%{
    public ModalMuCalculusScanner(java.io.InputStream r, SymbolFactory sf){
        this(r);
        this.sf=sf;
    }
    private SymbolFactory sf;
    private StringBuffer string = new StringBuffer();
%}




%eofval{
    return sf.newSymbol("EOF",ModalMuCalculusScannerToken.EOF);
%eofval}




%state STRING




%%
<YYINITIAL> 
{
[ \t\r\n\f]   { /* ignore white space. */ }

"<"           { return sf.newSymbol("<",ModalMuCalculusScannerToken.LT); }
">"           { return sf.newSymbol(">",ModalMuCalculusScannerToken.GT); }
"["           { return sf.newSymbol("[",ModalMuCalculusScannerToken.BRACKETLEFT); }
"]"           { return sf.newSymbol("]",ModalMuCalculusScannerToken.BRACKETRIGHT); }
"("           { return sf.newSymbol("(",ModalMuCalculusScannerToken.PARENTHESISLEFT); }
")"           { return sf.newSymbol(")",ModalMuCalculusScannerToken.PARENTHESISRIGHT); }
\.            { return sf.newSymbol(".",ModalMuCalculusScannerToken.DOT); }

"\\neg"|¬     { return sf.newSymbol("¬",ModalMuCalculusScannerToken.NEG); }
"\\wedge"|∧   { return sf.newSymbol("∧",ModalMuCalculusScannerToken.WEDGE); }
"\\vee"|∨     { return sf.newSymbol("∨",ModalMuCalculusScannerToken.VEE); }
"\\bot"|⊥     { return sf.newSymbol("⊥",ModalMuCalculusScannerToken.BOT); }
"\\top"|⊤     { return sf.newSymbol("⊤",ModalMuCalculusScannerToken.TOP); }

"\\mu"|μ      { return sf.newSymbol("μ",ModalMuCalculusScannerToken.MU); }
"\\nu"|ν      { return sf.newSymbol("ν",ModalMuCalculusScannerToken.NU); }


\"            { string.setLength(0); yybegin(STRING); }
[A-Za-z0-9]+  { string.setLength(0); string.append(yytext()); return sf.newSymbol("simple", ModalMuCalculusScannerToken.STRING, string.toString()); }
.             { System.err.println("Illegal character: "+yytext()); }
}

<STRING>
{
\"            { yybegin(YYINITIAL); return sf.newSymbol("complex", ModalMuCalculusScannerToken.STRING, string.toString()); }
.             { string.append(yytext()); }
}
