package gralog.computationtreelogic.parser;

import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;

%%
%class ComputationTreeLogicScanner
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
    return symbol("EOF", ComputationTreeLogicScannerToken.EOF);
%eofval}

%%
[ \t\r\n\f]   { /* ignore white space. */ }

"("           { return symbol("(",ComputationTreeLogicScannerToken.PARENTHESISLEFT); }
")"           { return symbol(")",ComputationTreeLogicScannerToken.PARENTHESISRIGHT); }

"A"           { return symbol("A",ComputationTreeLogicScannerToken.ALWAYS); }
"E"           { return symbol("E",ComputationTreeLogicScannerToken.EXISTS); }

"X"           { return symbol("X",ComputationTreeLogicScannerToken.NEXT); }
"G"           { return symbol("G",ComputationTreeLogicScannerToken.GLOBALLY); }
"F"           { return symbol("F",ComputationTreeLogicScannerToken.FINALLY); }
"U"           { return symbol("U",ComputationTreeLogicScannerToken.UNTIL); }

"\\neg"       { return symbol("\\neg",ComputationTreeLogicScannerToken.NEG); }
"\\wedge"     { return symbol("\\wedge",ComputationTreeLogicScannerToken.WEDGE); }
"\\vee"       { return symbol("\\vee",ComputationTreeLogicScannerToken.VEE); }
"\\bot"       { return symbol("\\bot",ComputationTreeLogicScannerToken.BOT); }
"\\top"       { return symbol("\\top",ComputationTreeLogicScannerToken.TOP); }

[A-Za-z][A-Za-z0-9]* { return symbol("STRING", ComputationTreeLogicScannerToken.STRING, yytext()); }
.                    { return symbol("UNEXPECTED CHARACTER", ComputationTreeLogicScannerToken.error, yytext()); }
