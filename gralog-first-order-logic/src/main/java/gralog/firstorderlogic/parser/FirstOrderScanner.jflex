package gralog.firstorderlogic.parser;

import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;

%%
%class FirstOrderScanner
%unicode
%cup
%column

%{
    private ComplexSymbolFactory sf = new ComplexSymbolFactory();

    private Symbol symbol(String name, int type) {
        return symbol(name, type, null);
    }

    private Symbol symbol(String name, int type, Object value) {
        return sf.newSymbol(name, type,
            new Location(0, yycolumn, yychar),
            new Location(0, yycolumn + yylength(), yychar + yylength()),
            value);
    }
%}

%eofval{
    return symbol("END-OF-STRING", FirstOrderScannerToken.EOF);
%eofval}

%%

[ \t\r\n\f]           { /* ignore white space. */ }

"\\exists" | [∃?]     { return symbol("EXISTS", FirstOrderScannerToken.EXISTS); }
"\\forall" | [∀!]     { return symbol("FORALL", FirstOrderScannerToken.FORALL); }
"\\neg" | [¬~-]       { return symbol("NEGATION", FirstOrderScannerToken.NEG); }
"\\vee" | [∨+]        { return symbol("OR", FirstOrderScannerToken.OR); }
"\\wedge" | [∧*]      { return symbol("AND", FirstOrderScannerToken.AND); }

"("                   { return symbol("(", FirstOrderScannerToken.OPEN); }
")"                   { return symbol(")", FirstOrderScannerToken.CLOSE); }
","                   { return symbol(",", FirstOrderScannerToken.COMMA); }
"."                   { return symbol(".", FirstOrderScannerToken.DOT); }
"="                   { return symbol(".", FirstOrderScannerToken.EQUALS); }

[A-Za-z][A-Za-z0-9]*  { return symbol("string", FirstOrderScannerToken.STRING, yytext()); }
.                     { return symbol("UNEXPECTED CHARACTER", FirstOrderScannerToken.error, yytext()); }
