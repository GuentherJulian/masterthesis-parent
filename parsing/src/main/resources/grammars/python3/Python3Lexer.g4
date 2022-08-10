/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 by Bart Kiers
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * Project      : python3-parser; an ANTLR4 grammar for Python 3
 *                https://github.com/bkiers/python3-parser
 * Developed by : Bart Kiers, bart@big-o.nl
 */
lexer grammar Python3Lexer;

// All comments that start with "///" are copy-pasted from
// The Python Language Reference

tokens { INDENT, DEDENT }

options {
    superClass=Python3LexerBase;
}

/*
 * lexer rules
 */

STRING
 : STRING_LITERAL
 | BYTES_LITERAL
 ;

NUMBER
 : INTEGER
 | FLOAT_NUMBER
 | IMAG_NUMBER
 ;

INTEGER
 : DECIMAL_INTEGER
 | OCT_INTEGER
 | HEX_INTEGER
 | BIN_INTEGER
 ;

DEF : 'def';
RETURN : 'return';
RAISE : 'raise';
FROM : 'from';
IMPORT : 'import';
AS : 'as';
GLOBAL : 'global';
NONLOCAL : 'nonlocal';
ASSERT : 'assert';
IF : 'if';
ELIF : 'elif';
ELSE : 'else';
WHILE : 'while';
FOR : 'for';
IN : 'in';
TRY : 'try';
FINALLY : 'finally';
WITH : 'with';
EXCEPT : 'except';
LAMBDA : 'lambda';
OR : 'or';
AND : 'and';
NOT : 'not';
IS : 'is';
NONE : 'None';
TRUE : 'True';
FALSE : 'False';
CLASS : 'class';
YIELD : 'yield';
DEL : 'del';
PASS : 'pass';
CONTINUE : 'continue';
BREAK : 'break';
ASYNC : 'async';
AWAIT : 'await';

NEWLINE
 : ( {this.atStartOfInput()}?   SPACES
   | ( '\r'? '\n' | '\r' | '\f' ) SPACES?
   )
   {this.onNewLine();}
 ;

/// identifier   ::=  id_start id_continue*
NAME
 : ID_START ID_CONTINUE*
 ;

/// stringliteral   ::=  [stringprefix](shortstring | longstring)
/// stringprefix    ::=  "r" | "u" | "R" | "U" | "f" | "F"
///                      | "fr" | "Fr" | "fR" | "FR" | "rf" | "rF" | "Rf" | "RF"
STRING_LITERAL
 : ( [rR] | [uU] | [fF] | ( [fF] [rR] ) | ( [rR] [fF] ) )? ( SHORT_STRING | LONG_STRING )
 ;

/// bytesliteral   ::=  bytesprefix(shortbytes | longbytes)
/// bytesprefix    ::=  "b" | "B" | "br" | "Br" | "bR" | "BR" | "rb" | "rB" | "Rb" | "RB"
BYTES_LITERAL
 : ( [bB] | ( [bB] [rR] ) | ( [rR] [bB] ) ) ( SHORT_BYTES | LONG_BYTES )
 ;

/// decimalinteger ::=  nonzerodigit digit* | "0"+
DECIMAL_INTEGER
 : NON_ZERO_DIGIT DIGIT*
 | '0'+
 ;

/// octinteger     ::=  "0" ("o" | "O") octdigit+
OCT_INTEGER
 : '0' [oO] OCT_DIGIT+
 ;

/// hexinteger     ::=  "0" ("x" | "X") hexdigit+
HEX_INTEGER
 : '0' [xX] HEX_DIGIT+
 ;

/// bininteger     ::=  "0" ("b" | "B") bindigit+
BIN_INTEGER
 : '0' [bB] BIN_DIGIT+
 ;

/// floatnumber   ::=  pointfloat | exponentfloat
FLOAT_NUMBER
 : POINT_FLOAT
 | EXPONENT_FLOAT
 ;

/// imagnumber ::=  (floatnumber | intpart) ("j" | "J")
IMAG_NUMBER
 : ( FLOAT_NUMBER | INT_PART ) [jJ]
 ;

DOT : '.';
ELLIPSIS : '...';
STAR : '*';
OPEN_PAREN : '(' {this.openBrace();};
CLOSE_PAREN : ')' {this.closeBrace();};
COMMA : ',';
COLON : ':';
SEMI_COLON : ';';
POWER : '**';
ASSIGN : '=';
OPEN_BRACK : '[' {this.openBrace();};
CLOSE_BRACK : ']' {this.closeBrace();};
OR_OP : '|';
XOR : '^';
AND_OP : '&';
LEFT_SHIFT : '<<';
RIGHT_SHIFT : '>>';
ADD : '+';
MINUS : '-';
DIV : '/';
MOD : '%';
IDIV : '//';
NOT_OP : '~';
OPEN_BRACE : '{' {this.openBrace();};
CLOSE_BRACE : '}' {this.closeBrace();};
LESS_THAN : '<';
GREATER_THAN : '>';
EQUALS : '==';
GT_EQ : '>=';
LT_EQ : '<=';
NOT_EQ_1 : '<>';
NOT_EQ_2 : '!=';
AT : '@';
ARROW : '->';
ADD_ASSIGN : '+=';
SUB_ASSIGN : '-=';
MULT_ASSIGN : '*=';
AT_ASSIGN : '@=';
DIV_ASSIGN : '/=';
MOD_ASSIGN : '%=';
AND_ASSIGN : '&=';
OR_ASSIGN : '|=';
XOR_ASSIGN : '^=';
LEFT_SHIFT_ASSIGN : '<<=';
RIGHT_SHIFT_ASSIGN : '>>=';
POWER_ASSIGN : '**=';
IDIV_ASSIGN : '//=';

SKIP_
 : ( SPACES | COMMENT | LINE_JOINING ) -> skip
 ;

UNKNOWN_CHAR
 : .
 ;

/*
 * fragments
 */

/// shortstring     ::=  "'" shortstringitem* "'" | '"' shortstringitem* '"'
/// shortstringitem ::=  shortstringchar | stringescapeseq
/// shortstringchar ::=  <any source character except "\" or newline or the quote>
fragment SHORT_STRING
 : '\'' ( STRING_ESCAPE_SEQ | ~[\\\r\n\f'] )* '\''
 | '"' ( STRING_ESCAPE_SEQ | ~[\\\r\n\f"] )* '"'
 ;
/// longstring      ::=  "'''" longstringitem* "'''" | '"""' longstringitem* '"""'
fragment LONG_STRING
 : '\'\'\'' LONG_STRING_ITEM*? '\'\'\''
 | '"""' LONG_STRING_ITEM*? '"""'
 ;

/// longstringitem  ::=  longstringchar | stringescapeseq
fragment LONG_STRING_ITEM
 : LONG_STRING_CHAR
 | STRING_ESCAPE_SEQ
 ;

/// longstringchar  ::=  <any source character except "\">
fragment LONG_STRING_CHAR
 : ~'\\'
 ;

/// stringescapeseq ::=  "\" <any source character>
fragment STRING_ESCAPE_SEQ
 : '\\' .
 | '\\' NEWLINE
 ;

/// nonzerodigit   ::=  "1"..."9"
fragment NON_ZERO_DIGIT
 : [1-9]
 ;

/// digit          ::=  "0"..."9"
fragment DIGIT
 : [0-9]
 ;

/// octdigit       ::=  "0"..."7"
fragment OCT_DIGIT
 : [0-7]
 ;

/// hexdigit       ::=  digit | "a"..."f" | "A"..."F"
fragment HEX_DIGIT
 : [0-9a-fA-F]
 ;

/// bindigit       ::=  "0" | "1"
fragment BIN_DIGIT
 : [01]
 ;

/// pointfloat    ::=  [intpart] fraction | intpart "."
fragment POINT_FLOAT
 : INT_PART? FRACTION
 | INT_PART '.'
 ;

/// exponentfloat ::=  (intpart | pointfloat) exponent
fragment EXPONENT_FLOAT
 : ( INT_PART | POINT_FLOAT ) EXPONENT
 ;

/// intpart       ::=  digit+
fragment INT_PART
 : DIGIT+
 ;

/// fraction      ::=  "." digit+
fragment FRACTION
 : '.' DIGIT+
 ;

/// exponent      ::=  ("e" | "E") ["+" | "-"] digit+
fragment EXPONENT
 : [eE] [+-]? DIGIT+
 ;

/// shortbytes     ::=  "'" shortbytesitem* "'" | '"' shortbytesitem* '"'
/// shortbytesitem ::=  shortbyteschar | bytesescapeseq
fragment SHORT_BYTES
 : '\'' ( SHORT_BYTES_CHAR_NO_SINGLE_QUOTE | BYTES_ESCAPE_SEQ )* '\''
 | '"' ( SHORT_BYTES_CHAR_NO_DOUBLE_QUOTE | BYTES_ESCAPE_SEQ )* '"'
 ;

/// longbytes      ::=  "'''" longbytesitem* "'''" | '"""' longbytesitem* '"""'
fragment LONG_BYTES
 : '\'\'\'' LONG_BYTES_ITEM*? '\'\'\''
 | '"""' LONG_BYTES_ITEM*? '"""'
 ;

/// longbytesitem  ::=  longbyteschar | bytesescapeseq
fragment LONG_BYTES_ITEM
 : LONG_BYTES_CHAR
 | BYTES_ESCAPE_SEQ
 ;

/// shortbyteschar ::=  <any ASCII character except "\" or newline or the quote>
fragment SHORT_BYTES_CHAR_NO_SINGLE_QUOTE
 : [\u0000-\u0009]
 | [\u000B-\u000C]
 | [\u000E-\u0026]
 | [\u0028-\u005B]
 | [\u005D-\u007F]
 ;

fragment SHORT_BYTES_CHAR_NO_DOUBLE_QUOTE
 : [\u0000-\u0009]
 | [\u000B-\u000C]
 | [\u000E-\u0021]
 | [\u0023-\u005B]
 | [\u005D-\u007F]
 ;

/// longbyteschar  ::=  <any ASCII character except "\">
fragment LONG_BYTES_CHAR
 : [\u0000-\u005B]
 | [\u005D-\u007F]
 ;

/// bytesescapeseq ::=  "\" <any ASCII character>
fragment BYTES_ESCAPE_SEQ
 : '\\' [\u0000-\u007F]
 ;

fragment SPACES
 : [ \t]+
 ;

fragment COMMENT
 : '#' ~[\r\n\f]*
 ;

fragment LINE_JOINING
 : '\\' SPACES? ( '\r'? '\n' | '\r' | '\f')
 ;


// TODO: ANTLR seems lack of some Unicode property support...
//$ curl https://www.unicode.org/Public/13.0.0/ucd/PropList.txt | grep Other_ID_
//1885..1886    ; Other_ID_Start # Mn   [2] MONGOLIAN LETTER ALI GALI BALUDA..MONGOLIAN LETTER ALI GALI THREE BALUDA
//2118          ; Other_ID_Start # Sm       SCRIPT CAPITAL P
//212E          ; Other_ID_Start # So       ESTIMATED SYMBOL
//309B..309C    ; Other_ID_Start # Sk   [2] KATAKANA-HIRAGANA VOICED SOUND MARK..KATAKANA-HIRAGANA SEMI-VOICED SOUND MARK
//00B7          ; Other_ID_Continue # Po       MIDDLE DOT
//0387          ; Other_ID_Continue # Po       GREEK ANO TELEIA
//1369..1371    ; Other_ID_Continue # No   [9] ETHIOPIC DIGIT ONE..ETHIOPIC DIGIT NINE
//19DA          ; Other_ID_Continue # No       NEW TAI LUE THAM DIGIT ONE

fragment UNICODE_OIDS
 : '\u1885'..'\u1886'
 | '\u2118'
 | '\u212e'
 | '\u309b'..'\u309c'
 ;

fragment UNICODE_OIDC
 : '\u00b7'
 | '\u0387'
 | '\u1369'..'\u1371'
 | '\u19da'
 ;

/// id_start     ::=  <all characters in general categories Lu, Ll, Lt, Lm, Lo, Nl, the underscore, and characters with the Other_ID_Start property>
fragment ID_START
 : '_'
 | [\p{L}]
 | [\p{Nl}]
 //| [\p{Other_ID_Start}]
 | UNICODE_OIDS
 ;

/// id_continue  ::=  <all characters in id_start, plus characters in the categories Mn, Mc, Nd, Pc and others with the Other_ID_Continue property>
fragment ID_CONTINUE
 : ID_START
 | [\p{Mn}]
 | [\p{Mc}]
 | [\p{Nd}]
 | [\p{Pc}]
 //| [\p{Other_ID_Continue}]
 | UNICODE_OIDC
 ;
FM_PLACEHOLDER: '${' ~'}'+? '}';
FM_IF: '<#if' ~'>'+? '>';
FM_IF_CLOSE: '</#if>';
FM_ELSE_IF: '<#elseif' ~'>'+? '>';
FM_ELSE: '<#else>';
FM_LIST: '<#list' .+? 'as' ~'>'+? '>';
FM_LIST_CLOSE: '</#list>';
FM_ImplicitToken1:'@';
FM_ImplicitToken2:'(';
FM_ImplicitToken3:')';
FM_ImplicitToken4:'def';
FM_ImplicitToken5:'->';
FM_ImplicitToken6:':';
FM_ImplicitToken7:'(';
FM_ImplicitToken8:')';
FM_ImplicitToken9:'=';
FM_ImplicitToken10:',';
FM_ImplicitToken11:'=';
FM_ImplicitToken12:',';
FM_ImplicitToken13:'*';
FM_ImplicitToken14:',';
FM_ImplicitToken15:'=';
FM_ImplicitToken16:',';
FM_ImplicitToken17:'**';
FM_ImplicitToken18:',';
FM_ImplicitToken19:'**';
FM_ImplicitToken20:',';
FM_ImplicitToken21:'*';
FM_ImplicitToken22:',';
FM_ImplicitToken23:'=';
FM_ImplicitToken24:',';
FM_ImplicitToken25:'**';
FM_ImplicitToken26:',';
FM_ImplicitToken27:'**';
FM_ImplicitToken28:',';
FM_ImplicitToken29:':';
FM_ImplicitToken30:'=';
FM_ImplicitToken31:',';
FM_ImplicitToken32:'=';
FM_ImplicitToken33:',';
FM_ImplicitToken34:'*';
FM_ImplicitToken35:',';
FM_ImplicitToken36:'=';
FM_ImplicitToken37:',';
FM_ImplicitToken38:'**';
FM_ImplicitToken39:',';
FM_ImplicitToken40:'**';
FM_ImplicitToken41:',';
FM_ImplicitToken42:'*';
FM_ImplicitToken43:',';
FM_ImplicitToken44:'=';
FM_ImplicitToken45:',';
FM_ImplicitToken46:'**';
FM_ImplicitToken47:',';
FM_ImplicitToken48:'**';
FM_ImplicitToken49:',';
FM_ImplicitToken50:';';
FM_ImplicitToken51:';';
FM_ImplicitToken52:'=';
FM_ImplicitToken53:':';
FM_ImplicitToken54:'=';
FM_ImplicitToken55:',';
FM_ImplicitToken56:',';
FM_ImplicitToken57:'+=';
FM_ImplicitToken58:'-=';
FM_ImplicitToken59:'*=';
FM_ImplicitToken60:'@=';
FM_ImplicitToken61:'/=';
FM_ImplicitToken62:'%=';
FM_ImplicitToken63:'&=';
FM_ImplicitToken64:'|=';
FM_ImplicitToken65:'^=';
FM_ImplicitToken66:'<<=';
FM_ImplicitToken67:'>>=';
FM_ImplicitToken68:'**=';
FM_ImplicitToken69:'//=';
FM_ImplicitToken70:'del';
FM_ImplicitToken71:'pass';
FM_ImplicitToken72:'break';
FM_ImplicitToken73:'continue';
FM_ImplicitToken74:'return';
FM_ImplicitToken75:'raise';
FM_ImplicitToken76:'from';
FM_ImplicitToken77:'import';
FM_ImplicitToken78:'from';
FM_ImplicitToken79:'.';
FM_ImplicitToken80:'...';
FM_ImplicitToken81:'.';
FM_ImplicitToken82:'...';
FM_ImplicitToken83:'import';
FM_ImplicitToken84:'*';
FM_ImplicitToken85:'(';
FM_ImplicitToken86:')';
FM_ImplicitToken87:'as';
FM_ImplicitToken88:'as';
FM_ImplicitToken89:',';
FM_ImplicitToken90:',';
FM_ImplicitToken91:',';
FM_ImplicitToken92:'.';
FM_ImplicitToken93:'global';
FM_ImplicitToken94:',';
FM_ImplicitToken95:'nonlocal';
FM_ImplicitToken96:',';
FM_ImplicitToken97:'assert';
FM_ImplicitToken98:',';
FM_ImplicitToken99:'if';
FM_ImplicitToken100:':';
FM_ImplicitToken101:'elif';
FM_ImplicitToken102:':';
FM_ImplicitToken103:'else';
FM_ImplicitToken104:':';
FM_ImplicitToken105:'while';
FM_ImplicitToken106:':';
FM_ImplicitToken107:'else';
FM_ImplicitToken108:':';
FM_ImplicitToken109:'for';
FM_ImplicitToken110:'in';
FM_ImplicitToken111:':';
FM_ImplicitToken112:'else';
FM_ImplicitToken113:':';
FM_ImplicitToken114:'try';
FM_ImplicitToken115:':';
FM_ImplicitToken116:':';
FM_ImplicitToken117:'else';
FM_ImplicitToken118:':';
FM_ImplicitToken119:'finally';
FM_ImplicitToken120:':';
FM_ImplicitToken121:'finally';
FM_ImplicitToken122:':';
FM_ImplicitToken123:'with';
FM_ImplicitToken124:',';
FM_ImplicitToken125:':';
FM_ImplicitToken126:'as';
FM_ImplicitToken127:'except';
FM_ImplicitToken128:'as';
FM_ImplicitToken129:'if';
FM_ImplicitToken130:'else';
FM_ImplicitToken131:'lambda';
FM_ImplicitToken132:':';
FM_ImplicitToken133:'lambda';
FM_ImplicitToken134:':';
FM_ImplicitToken135:'or';
FM_ImplicitToken136:'and';
FM_ImplicitToken137:'not';
FM_ImplicitToken138:'<';
FM_ImplicitToken139:'>';
FM_ImplicitToken140:'==';
FM_ImplicitToken141:'>=';
FM_ImplicitToken142:'<=';
FM_ImplicitToken143:'<>';
FM_ImplicitToken144:'!=';
FM_ImplicitToken145:'in';
FM_ImplicitToken146:'not';
FM_ImplicitToken147:'in';
FM_ImplicitToken148:'is';
FM_ImplicitToken149:'is';
FM_ImplicitToken150:'not';
FM_ImplicitToken151:'*';
FM_ImplicitToken152:'|';
FM_ImplicitToken153:'^';
FM_ImplicitToken154:'&';
FM_ImplicitToken155:'<<';
FM_ImplicitToken156:'>>';
FM_ImplicitToken157:'+';
FM_ImplicitToken158:'-';
FM_ImplicitToken159:'*';
FM_ImplicitToken160:'@';
FM_ImplicitToken161:'/';
FM_ImplicitToken162:'%';
FM_ImplicitToken163:'//';
FM_ImplicitToken164:'+';
FM_ImplicitToken165:'-';
FM_ImplicitToken166:'~';
FM_ImplicitToken167:'**';
FM_ImplicitToken168:'(';
FM_ImplicitToken169:')';
FM_ImplicitToken170:'[';
FM_ImplicitToken171:']';
FM_ImplicitToken172:'{';
FM_ImplicitToken173:'}';
FM_ImplicitToken174:'...';
FM_ImplicitToken175:'None';
FM_ImplicitToken176:'True';
FM_ImplicitToken177:'False';
FM_ImplicitToken178:',';
FM_ImplicitToken179:',';
FM_ImplicitToken180:'(';
FM_ImplicitToken181:')';
FM_ImplicitToken182:'[';
FM_ImplicitToken183:']';
FM_ImplicitToken184:'.';
FM_ImplicitToken185:',';
FM_ImplicitToken186:',';
FM_ImplicitToken187:':';
FM_ImplicitToken188:':';
FM_ImplicitToken189:',';
FM_ImplicitToken190:',';
FM_ImplicitToken191:',';
FM_ImplicitToken192:',';
FM_ImplicitToken193:':';
FM_ImplicitToken194:'**';
FM_ImplicitToken195:',';
FM_ImplicitToken196:':';
FM_ImplicitToken197:'**';
FM_ImplicitToken198:',';
FM_ImplicitToken199:',';
FM_ImplicitToken200:',';
FM_ImplicitToken201:'class';
FM_ImplicitToken202:'(';
FM_ImplicitToken203:')';
FM_ImplicitToken204:':';
FM_ImplicitToken205:',';
FM_ImplicitToken206:',';
FM_ImplicitToken207:'=';
FM_ImplicitToken208:'**';
FM_ImplicitToken209:'*';
FM_ImplicitToken210:'for';
FM_ImplicitToken211:'in';
FM_ImplicitToken212:'if';
FM_ImplicitToken213:'yield';
FM_ImplicitToken214:'from';