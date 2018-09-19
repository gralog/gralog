package gralog.modallogic.parser;

import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;

%%
%class ModalLogicScanner
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
    return symbol("END-OF-STRING", ModalLogicScannerToken.EOF);
%eofval}

%%
[ \t\r\n\f]   { /* ignore white space. */ }

"\\diamond" | "◊" | "<>"
    { return symbol("NEG", ModalLogicScannerToken.DIAMOND); }
"\\box" | "□" | "[]"
    { return symbol("NEG", ModalLogicScannerToken.BOX); }
"\\neg" | [¬~-]
    { return symbol("NEG", ModalLogicScannerToken.NEG); }
"\\wedge" | [∧*]
    { return symbol("AND", ModalLogicScannerToken.WEDGE); }
"\\vee" | [∨+]
    { return symbol("OR", ModalLogicScannerToken.VEE); }
"\\bot" | "bottom" | "false" | "⊥"
    { return symbol("BOTTOM", ModalLogicScannerToken.BOT); }
"\\top" | "top" | "true" | "⊤"
    { return symbol("TOP", ModalLogicScannerToken.TOP); }

"<"           { return symbol("<", ModalLogicScannerToken.LT); }
">"           { return symbol(">", ModalLogicScannerToken.GT); }
"["           { return symbol("[", ModalLogicScannerToken.BRACKETLEFT); }
"]"           { return symbol("]", ModalLogicScannerToken.BRACKETRIGHT); }
"("           { return symbol("(", ModalLogicScannerToken.PARENTHESISLEFT); }
")"           { return symbol(")", ModalLogicScannerToken.PARENTHESISRIGHT); }

[A-Za-z][A-Za-z0-9]* { return symbol("STRING", ModalLogicScannerToken.STRING, yytext()); }
.                    { return symbol("UNEXPECTED CHARACTER", ModalLogicScannerToken.error, yytext()); }
