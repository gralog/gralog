package gralog.npcompleteness.propositionallogic.parser;

import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;

%%
%class PropositionalLogicScanner
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
    return symbol("END-OF-STRING", PropositionalLogicScannerToken.EOF);
%eofval}

%%

[ \t\r\n\f]           { /* ignore white space. */ }

"\\neg" | [¬~-]       { return symbol("NEGATION", PropositionalLogicScannerToken.NEG); }
"\\vee" | [∨+]        { return symbol("OR", PropositionalLogicScannerToken.OR); }
"\\wedge" | [∧*]      { return symbol("AND", PropositionalLogicScannerToken.AND); }

"("                   { return symbol("(",PropositionalLogicScannerToken.OPEN); }
")"                   { return symbol(")",PropositionalLogicScannerToken.CLOSE); }

[A-Za-z][A-Za-z0-9]*  { return symbol("string", PropositionalLogicScannerToken.STRING, yytext()); }
.                     { return symbol("UNEXPECTED CHARACTER", PropositionalLogicScannerToken.error, yytext()); }
