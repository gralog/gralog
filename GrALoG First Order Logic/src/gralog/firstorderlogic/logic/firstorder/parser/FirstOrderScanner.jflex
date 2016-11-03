package gralog.firstorderlogic.logic.firstorder.parser;

import java_cup.runtime.SymbolFactory;
import java.lang.StringBuffer;

%%
%unicode
%cup
%class FirstOrderScanner
%{
    public FirstOrderScanner(java.io.InputStream r, SymbolFactory sf){
        this(r);
        this.sf=sf;
    }
    private SymbolFactory sf;
%}




%eofval{
    return sf.newSymbol("EOF",FirstOrderScannerToken.EOF);
%eofval}





%%

[ \t\r\n\f]           { /* ignore white space. */ }

"\\exists" | [∃?]     { return sf.newSymbol("\\exists",FirstOrderScannerToken.EXISTS); }
"\\forall" | [∀!]     { return sf.newSymbol("\\forall",FirstOrderScannerToken.FORALL); }
"\\neg" | [¬~-]       { return sf.newSymbol("\\neg",FirstOrderScannerToken.NEG); }
"\\vee" | [∨+]        { return sf.newSymbol("\\vee",FirstOrderScannerToken.OR); }
"\\wedge" | [∧*]      { return sf.newSymbol("\\wedge",FirstOrderScannerToken.AND); }

"("                   { return sf.newSymbol("(",FirstOrderScannerToken.OPEN); }
")"                   { return sf.newSymbol(")",FirstOrderScannerToken.CLOSE); }
","                   { return sf.newSymbol(",",FirstOrderScannerToken.COMMA); }
"."                   { return sf.newSymbol(".",FirstOrderScannerToken.DOT); }

[A-Za-z][A-Za-z0-9]*  { return sf.newSymbol("string", FirstOrderScannerToken.STRING, yytext()); }
.                     { System.err.println("Illegal character: "+yytext()); }
