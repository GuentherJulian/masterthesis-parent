grammar SimpleFreeMarker;

ifThen: IF ANY (ELSE_IF ANY)* (ELSE ANY)? IF_CLOSE;

ifElse: IF ANY (ELSE_IF ANY)* ELSE ANY IF_CLOSE;

loop: LIST ANY LIST_CLOSE;

loopElse: LIST ANY ELSE ANY LIST_CLOSE;


PLACEHOLDER: '${' ~'}'+? '}';

IF: '<#if' ~'>'+? '>';
IF_CLOSE: '</#if>';
ELSE_IF: '<#elseif' ~'>'+? '>';
ELSE: '<#else>';
LIST: '<#list' .+? 'as' ~'>'+? '>';
LIST_CLOSE: '</#list>';

ANY: .;