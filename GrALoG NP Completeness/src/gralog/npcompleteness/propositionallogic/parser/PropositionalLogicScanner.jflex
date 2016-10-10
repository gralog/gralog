package gralog.npcompleteness.propositionallogic.parser;

import java_cup.runtime.SymbolFactory;
import java.lang.StringBuffer;

%%
%unicode
%cup
%class PropositionalLogicScanner
%{
    public PropositionalLogicScanner(java.io.InputStream r, SymbolFactory sf){
        this(r);
        this.sf=sf;
    }
    private SymbolFactory sf;
%}




%eofval{
    return sf.newSymbol("EOF",PropositionalLogicScannerToken.EOF);
%eofval}





%%

[ \t\r\n\f]           { /* ignore white space. */ }

"\\neg"               { return sf.newSymbol("\\neg",PropositionalLogicScannerToken.NEG); }
"\\vee"               { return sf.newSymbol("\\vee",PropositionalLogicScannerToken.OR); }
"\\wedge"             { return sf.newSymbol("\\wedge",PropositionalLogicScannerToken.AND); }

"("                   { return sf.newSymbol("(",PropositionalLogicScannerToken.OPEN); }
")"                   { return sf.newSymbol(")",PropositionalLogicScannerToken.CLOSE); }

[A-Za-z][A-Za-z0-9]*  { return sf.newSymbol("string", PropositionalLogicScannerToken.STRING, yytext()); }
.                     { System.err.println("Illegal character: "+yytext()); }
