grammar SimpleVelocity;

ifThen: IF ANY (ELSE_IF ANY)* (ELSE ANY)? IF_LIST_CLOSE;

ifElse: IF ANY (ELSE_IF ANY)* ELSE ANY IF_LIST_CLOSE;

loop: LIST ANY IF_LIST_CLOSE;

loopElse: LIST ANY ELSE ANY IF_LIST_CLOSE;

PLACEHOLDER: '${' ~'}'+? '}';

IF: '#if' [ \t]? '(' .+? ')';
IF_LIST_CLOSE: '#end';
ELSE_IF: '#elseif' [ \t]? '(' .+? ')';
ELSE: '#else';
LIST: '#foreach' [ \t]? '(' .+? 'in' .+? ')';

ANY: .;