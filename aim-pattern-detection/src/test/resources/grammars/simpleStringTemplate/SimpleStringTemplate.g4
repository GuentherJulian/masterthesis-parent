grammar SimpleStringTemplate;

ifThen: IF ANY (ELSE_IF ANY)* (ELSE ANY)? IF_CLOSE;

ifElse: IF ANY (ELSE_IF ANY)* ELSE ANY IF_CLOSE;

loop: LIST ANY LIST_CLOSE;

loopElse: LIST ANY ELSE ANY LIST_CLOSE;


PLACEHOLDER: '<' ~'>' +? '>';

IF: '<if' [ \t]? '(' .+? ')' [ \t]? '>';
IF_CLOSE: '<endif>';
ELSE_IF: '<elseif' [ \t]? '(' .+? ')' [ \t]? '>';
ELSE: '<else>';
LIST: '<' ~':'+? [ \t]? ':' [ \t]? '{'  ~'|'+? [ \t]? '|';
LIST_CLOSE: '}' [ \t]? '>';

ANY: .;