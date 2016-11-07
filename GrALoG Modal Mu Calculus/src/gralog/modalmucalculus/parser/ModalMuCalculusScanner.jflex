package gralog.modalmucalculus.parser;

import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;

%%
%class ModalMuCalculusScanner
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
    return symbol("END-OF-STRING", ModalMuCalculusScannerToken.EOF);
%eofval}

%%
[ \t\r\n\f]   { /* ignore white space. */ }

"\\mu" | "μ" | "mu"
    { return symbol("MU", ModalMuCalculusScannerToken.MU); }
"\\nu" | "ν" | "nu"
    { return symbol("NU", ModalMuCalculusScannerToken.NU); }
"\\diamond" | "◊" | "<>"
    { return symbol("NEG", ModalMuCalculusScannerToken.DIAMOND); }
"\\box" | "□" | "[]"
    { return symbol("NEG", ModalMuCalculusScannerToken.BOX); }
"\\neg" | [¬~-]
    { return symbol("NEG", ModalMuCalculusScannerToken.NEG); }
"\\wedge" | [∧*]
    { return symbol("AND", ModalMuCalculusScannerToken.AND); }
"\\vee" | [∨+]
    { return symbol("OR", ModalMuCalculusScannerToken.OR); }
"\\bot" | "bottom" | "false" | "⊥"
    { return symbol("BOTTOM", ModalMuCalculusScannerToken.BOT); }
"\\top" | "top" | "true" | "⊤"
    { return symbol("TOP", ModalMuCalculusScannerToken.TOP); }

"."           { return symbol("DOT", ModalMuCalculusScannerToken.DOT); }
"<"           { return symbol("<", ModalMuCalculusScannerToken.LT); }
">"           { return symbol(">", ModalMuCalculusScannerToken.GT); }
"["           { return symbol("[", ModalMuCalculusScannerToken.BRACKETLEFT); }
"]"           { return symbol("]", ModalMuCalculusScannerToken.BRACKETRIGHT); }
"("           { return symbol("(", ModalMuCalculusScannerToken.PARENTHESISLEFT); }
")"           { return symbol(")", ModalMuCalculusScannerToken.PARENTHESISRIGHT); }

[A-Za-z][A-Za-z0-9]* { return symbol("STRING", ModalMuCalculusScannerToken.STRING, yytext()); }
.                    { return symbol("UNEXPECTED CHARACTER", ModalMuCalculusScannerToken.error, yytext()); }
