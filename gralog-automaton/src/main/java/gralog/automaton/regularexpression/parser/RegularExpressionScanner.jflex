package gralog.automaton.regularexpression.parser;

import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;

%%
%class RegularExpressionScanner
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
    return symbol("END-OF-STRING", RegularExpressionScannerToken.EOF);
%eofval}

%%

[ \t\r\n\f]   { /* ignore white space. */ }

"("           { return symbol("(", RegularExpressionScannerToken.PARENTHESISLEFT); }
")"           { return symbol(")", RegularExpressionScannerToken.PARENTHESISRIGHT); }
"*"           { return symbol("[", RegularExpressionScannerToken.KLEENESTAR); }
"+"|"\|"      { return symbol("]", RegularExpressionScannerToken.ALTERNATION); }

[A-Za-z0-9]   { return symbol("letter", RegularExpressionScannerToken.LETTER, yytext().charAt(0)); }
.             { return symbol("UNEXPECTED CHARACTER", RegularExpressionScannerToken.error, yytext()); }
