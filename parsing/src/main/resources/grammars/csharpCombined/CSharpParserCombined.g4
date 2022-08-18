// Eclipse Public License - v 1.0, http://www.eclipse.org/legal/epl-v10.html
// Copyright (c) 2013, Christian Wulf (chwchw@gmx.de)
// Copyright (c) 2016-2017, Ivan Kochurkin (kvanttt@gmail.com), Positive Technologies.

parser grammar CSharpParserCombined;

options { tokenVocab=CSharpLexerCombined; superClass = CSharpParserBase; }

// entry point
compilation_unit
	: BYTE_ORDER_MARK? (fm_extern_alias_directivesOpt | extern_alias_directives)? (fm_using_directivesOpt | using_directives)?
	  (fm_global_attribute_sectionStar | global_attribute_section)* (fm_namespace_member_declarationsOpt | namespace_member_declarations)? EOF
	;

//B.2 Syntactic grammar

//B.2.1 Basic concepts

namespace_or_type_name 
	: ((fm_identifier | identifier) (fm_type_argument_listOpt | type_argument_list)? | qualified_alias_member) (FM_ImplicitToken1  (fm_identifier | identifier) (fm_type_argument_listOpt | type_argument_list)?)*
	;
fm_namespace_or_type_name: FM_PLACEHOLDER | FM_IF (fm_namespace_or_type_name | namespace_or_type_name) (FM_ELSE_IF (fm_namespace_or_type_name | namespace_or_type_name))* FM_ELSE (fm_namespace_or_type_name | namespace_or_type_name) FM_IF_CLOSE;

//B.2.2 Types
type_
	: (fm_base_type | base_type) (FM_ImplicitToken2  | rank_specifier | FM_ImplicitToken3 )*
	;
fm_type_: FM_PLACEHOLDER | FM_IF (fm_type_ | type_) (FM_ELSE_IF (fm_type_ | type_))* FM_ELSE (fm_type_ | type_) FM_IF_CLOSE;
fm_type_Opt: FM_PLACEHOLDER | FM_IF (fm_type_Opt | type_)? (FM_ELSE_IF (fm_type_Opt | type_)?)* (FM_ELSE (fm_type_Opt | type_)?)? FM_IF_CLOSE;

base_type
	: simple_type
	| class_type  // represents types: enum, class, interface, delegate, type_parameter
	| VOID FM_ImplicitToken3 
	| tuple_type
	;
fm_base_type: FM_PLACEHOLDER | FM_IF (fm_base_type | base_type) (FM_ELSE_IF (fm_base_type | base_type))* FM_ELSE (fm_base_type | base_type) FM_IF_CLOSE;

tuple_type
    : FM_ImplicitToken4  (fm_tuple_element | tuple_element) (FM_ImplicitToken5  (fm_tuple_element | tuple_element))+ FM_ImplicitToken6 
    ;

tuple_element
    : (fm_type_ | type_) (fm_identifierOpt | identifier)?
    ;
fm_tuple_element: FM_PLACEHOLDER | FM_IF (fm_tuple_element | tuple_element) (FM_ELSE_IF (fm_tuple_element | tuple_element))* FM_ELSE (fm_tuple_element | tuple_element) FM_IF_CLOSE;

simple_type 
	: numeric_type
	| BOOL
	;

numeric_type 
	: integral_type
	| floating_point_type
	| DECIMAL
	;

integral_type 
	: SBYTE
	| BYTE
	| SHORT
	| USHORT
	| INT
	| UINT
	| LONG
	| ULONG
	| CHAR
	;

floating_point_type 
	: FLOAT
	| DOUBLE
	;

/** namespace_or_type_name, OBJECT, STRING */
class_type 
	: namespace_or_type_name
	| OBJECT
	| DYNAMIC
	| STRING
	;
fm_class_type: FM_PLACEHOLDER | FM_IF (fm_class_type | class_type) (FM_ELSE_IF (fm_class_type | class_type))* FM_ELSE (fm_class_type | class_type) FM_IF_CLOSE;

type_argument_list 
	: FM_ImplicitToken7  (fm_type_ | type_) ( FM_ImplicitToken5  (fm_type_ | type_))* FM_ImplicitToken8 
	;
fm_type_argument_listOpt: FM_PLACEHOLDER | FM_IF (fm_type_argument_listOpt | type_argument_list)? (FM_ELSE_IF (fm_type_argument_listOpt | type_argument_list)?)* (FM_ELSE (fm_type_argument_listOpt | type_argument_list)?)? FM_IF_CLOSE;

//B.2.4 Expressions
argument_list 
	: (fm_argument | argument) ( FM_ImplicitToken5  (fm_argument | argument))*
	;
fm_argument_listOpt: FM_PLACEHOLDER | FM_IF (fm_argument_listOpt | argument_list)? (FM_ELSE_IF (fm_argument_listOpt | argument_list)?)* (FM_ELSE (fm_argument_listOpt | argument_list)?)? FM_IF_CLOSE;

argument
	: ((fm_identifier | identifier) FM_ImplicitToken9 )? refout=(REF | OUT | IN)? (VAR | type_)? (fm_expression | expression)
	;
fm_argument: FM_PLACEHOLDER | FM_IF (fm_argument | argument) (FM_ELSE_IF (fm_argument | argument))* FM_ELSE (fm_argument | argument) FM_IF_CLOSE;

expression
	: assignment
	| non_assignment_expression
	| REF (fm_non_assignment_expression | non_assignment_expression)
	;
fm_expression: FM_PLACEHOLDER | FM_IF (fm_expression | expression) (FM_ELSE_IF (fm_expression | expression))* FM_ELSE (fm_expression | expression) FM_IF_CLOSE;
fm_expressionOpt: FM_PLACEHOLDER | FM_IF (fm_expressionOpt | expression)? (FM_ELSE_IF (fm_expressionOpt | expression)?)* (FM_ELSE (fm_expressionOpt | expression)?)? FM_IF_CLOSE;

non_assignment_expression
	: lambda_expression
	| query_expression
	| conditional_expression
	;
fm_non_assignment_expression: FM_PLACEHOLDER | FM_IF (fm_non_assignment_expression | non_assignment_expression) (FM_ELSE_IF (fm_non_assignment_expression | non_assignment_expression))* FM_ELSE (fm_non_assignment_expression | non_assignment_expression) FM_IF_CLOSE;

assignment
	: (fm_unary_expression | unary_expression) (fm_assignment_operator | assignment_operator) (fm_expression | expression)
	| (fm_unary_expression | unary_expression) FM_ImplicitToken10  (fm_throwable_expression | throwable_expression)
	;

assignment_operator
	: FM_ImplicitToken11  | FM_ImplicitToken12  | FM_ImplicitToken13  | FM_ImplicitToken14  | FM_ImplicitToken15  | FM_ImplicitToken16  | FM_ImplicitToken17  | FM_ImplicitToken18  | FM_ImplicitToken19  | FM_ImplicitToken20  | right_shift_assignment
	;
fm_assignment_operator: FM_PLACEHOLDER | FM_IF (fm_assignment_operator | assignment_operator) (FM_ELSE_IF (fm_assignment_operator | assignment_operator))* FM_ELSE (fm_assignment_operator | assignment_operator) FM_IF_CLOSE;

conditional_expression
	: (fm_null_coalescing_expression | null_coalescing_expression) (FM_ImplicitToken2  (fm_throwable_expression | throwable_expression) FM_ImplicitToken9  (fm_throwable_expression | throwable_expression))?
	;

null_coalescing_expression
	: (fm_conditional_or_expression | conditional_or_expression) (FM_ImplicitToken21  (null_coalescing_expression | throw_expression))?
	;
fm_null_coalescing_expression: FM_PLACEHOLDER | FM_IF (fm_null_coalescing_expression | null_coalescing_expression) (FM_ELSE_IF (fm_null_coalescing_expression | null_coalescing_expression))* FM_ELSE (fm_null_coalescing_expression | null_coalescing_expression) FM_IF_CLOSE;

conditional_or_expression
	: (fm_conditional_and_expression | conditional_and_expression) (OP_OR (fm_conditional_and_expression | conditional_and_expression))*
	;
fm_conditional_or_expression: FM_PLACEHOLDER | FM_IF (fm_conditional_or_expression | conditional_or_expression) (FM_ELSE_IF (fm_conditional_or_expression | conditional_or_expression))* FM_ELSE (fm_conditional_or_expression | conditional_or_expression) FM_IF_CLOSE;

conditional_and_expression
	: (fm_inclusive_or_expression | inclusive_or_expression) (OP_AND (fm_inclusive_or_expression | inclusive_or_expression))*
	;
fm_conditional_and_expression: FM_PLACEHOLDER | FM_IF (fm_conditional_and_expression | conditional_and_expression) (FM_ELSE_IF (fm_conditional_and_expression | conditional_and_expression))* FM_ELSE (fm_conditional_and_expression | conditional_and_expression) FM_IF_CLOSE;

inclusive_or_expression
	: (fm_exclusive_or_expression | exclusive_or_expression) (FM_ImplicitToken22  (fm_exclusive_or_expression | exclusive_or_expression))*
	;
fm_inclusive_or_expression: FM_PLACEHOLDER | FM_IF (fm_inclusive_or_expression | inclusive_or_expression) (FM_ELSE_IF (fm_inclusive_or_expression | inclusive_or_expression))* FM_ELSE (fm_inclusive_or_expression | inclusive_or_expression) FM_IF_CLOSE;

exclusive_or_expression
	: (fm_and_expression | and_expression) (FM_ImplicitToken23  (fm_and_expression | and_expression))*
	;
fm_exclusive_or_expression: FM_PLACEHOLDER | FM_IF (fm_exclusive_or_expression | exclusive_or_expression) (FM_ELSE_IF (fm_exclusive_or_expression | exclusive_or_expression))* FM_ELSE (fm_exclusive_or_expression | exclusive_or_expression) FM_IF_CLOSE;

and_expression
	: (fm_equality_expression | equality_expression) (FM_ImplicitToken24  (fm_equality_expression | equality_expression))*
	;
fm_and_expression: FM_PLACEHOLDER | FM_IF (fm_and_expression | and_expression) (FM_ELSE_IF (fm_and_expression | and_expression))* FM_ELSE (fm_and_expression | and_expression) FM_IF_CLOSE;

equality_expression
	: (fm_relational_expression | relational_expression) ((OP_EQ | OP_NE)  (fm_relational_expression | relational_expression))*
	;
fm_equality_expression: FM_PLACEHOLDER | FM_IF (fm_equality_expression | equality_expression) (FM_ELSE_IF (fm_equality_expression | equality_expression))* FM_ELSE (fm_equality_expression | equality_expression) FM_IF_CLOSE;

relational_expression
	: (fm_shift_expression | shift_expression) ((FM_ImplicitToken7  | FM_ImplicitToken8  | FM_ImplicitToken25  | FM_ImplicitToken26 ) (fm_shift_expression | shift_expression) | IS (fm_isType | isType) | AS (fm_type_ | type_))*
	;
fm_relational_expression: FM_PLACEHOLDER | FM_IF (fm_relational_expression | relational_expression) (FM_ELSE_IF (fm_relational_expression | relational_expression))* FM_ELSE (fm_relational_expression | relational_expression) FM_IF_CLOSE;

shift_expression
	: (fm_additive_expression | additive_expression) ((FM_ImplicitToken27  | right_shift)  (fm_additive_expression | additive_expression))*
	;
fm_shift_expression: FM_PLACEHOLDER | FM_IF (fm_shift_expression | shift_expression) (FM_ELSE_IF (fm_shift_expression | shift_expression))* FM_ELSE (fm_shift_expression | shift_expression) FM_IF_CLOSE;

additive_expression
	: (fm_multiplicative_expression | multiplicative_expression) ((FM_ImplicitToken28  | FM_ImplicitToken29 )  (fm_multiplicative_expression | multiplicative_expression))*
	;
fm_additive_expression: FM_PLACEHOLDER | FM_IF (fm_additive_expression | additive_expression) (FM_ELSE_IF (fm_additive_expression | additive_expression))* FM_ELSE (fm_additive_expression | additive_expression) FM_IF_CLOSE;

multiplicative_expression
	: (fm_switch_expression | switch_expression) ((FM_ImplicitToken3  | FM_ImplicitToken30  | FM_ImplicitToken31 )  (fm_switch_expression | switch_expression))*
	;
fm_multiplicative_expression: FM_PLACEHOLDER | FM_IF (fm_multiplicative_expression | multiplicative_expression) (FM_ELSE_IF (fm_multiplicative_expression | multiplicative_expression))* FM_ELSE (fm_multiplicative_expression | multiplicative_expression) FM_IF_CLOSE;

switch_expression
    : (fm_range_expression | range_expression) (FM_ImplicitToken32  FM_ImplicitToken33  ((fm_switch_expression_arms | switch_expression_arms) FM_ImplicitToken5 ?)? FM_ImplicitToken34 )?
    ;
fm_switch_expression: FM_PLACEHOLDER | FM_IF (fm_switch_expression | switch_expression) (FM_ELSE_IF (fm_switch_expression | switch_expression))* FM_ELSE (fm_switch_expression | switch_expression) FM_IF_CLOSE;

switch_expression_arms
    : (fm_switch_expression_arm | switch_expression_arm) (FM_ImplicitToken5  (fm_switch_expression_arm | switch_expression_arm))*
    ;
fm_switch_expression_arms: FM_PLACEHOLDER | FM_IF (fm_switch_expression_arms | switch_expression_arms) (FM_ELSE_IF (fm_switch_expression_arms | switch_expression_arms))* FM_ELSE (fm_switch_expression_arms | switch_expression_arms) FM_IF_CLOSE;

switch_expression_arm
    : (fm_expression | expression) (fm_case_guardOpt | case_guard)? (fm_right_arrow | right_arrow) (fm_throwable_expression | throwable_expression)
    ;
fm_switch_expression_arm: FM_PLACEHOLDER | FM_IF (fm_switch_expression_arm | switch_expression_arm) (FM_ELSE_IF (fm_switch_expression_arm | switch_expression_arm))* FM_ELSE (fm_switch_expression_arm | switch_expression_arm) FM_IF_CLOSE;

range_expression
    : unary_expression
    | (fm_unary_expressionOpt | unary_expression)? OP_RANGE (fm_unary_expressionOpt | unary_expression)?
    ;
fm_range_expression: FM_PLACEHOLDER | FM_IF (fm_range_expression | range_expression) (FM_ELSE_IF (fm_range_expression | range_expression))* FM_ELSE (fm_range_expression | range_expression) FM_IF_CLOSE;

// https://msdn.microsoft.com/library/6a71f45d(v=vs.110).aspx
unary_expression
	: primary_expression
	| FM_ImplicitToken28  (fm_unary_expression | unary_expression)
	| FM_ImplicitToken29  (fm_unary_expression | unary_expression)
	| BANG (fm_unary_expression | unary_expression)
	| FM_ImplicitToken35  (fm_unary_expression | unary_expression)
	| FM_ImplicitToken36  (fm_unary_expression | unary_expression)
	| FM_ImplicitToken37  (fm_unary_expression | unary_expression)
	| OPEN_PARENS (fm_type_ | type_) CLOSE_PARENS (fm_unary_expression | unary_expression)
	| AWAIT (fm_unary_expression | unary_expression) // C# 5
	| FM_ImplicitToken24  (fm_unary_expression | unary_expression)
	| FM_ImplicitToken3  (fm_unary_expression | unary_expression)
	| FM_ImplicitToken23  (fm_unary_expression | unary_expression) // C# 8 ranges
	;
fm_unary_expression: FM_PLACEHOLDER | FM_IF (fm_unary_expression | unary_expression) (FM_ELSE_IF (fm_unary_expression | unary_expression))* FM_ELSE (fm_unary_expression | unary_expression) FM_IF_CLOSE;
fm_unary_expressionOpt: FM_PLACEHOLDER | FM_IF (fm_unary_expressionOpt | unary_expression)? (FM_ELSE_IF (fm_unary_expressionOpt | unary_expression)?)* (FM_ELSE (fm_unary_expressionOpt | unary_expression)?)? FM_IF_CLOSE;

primary_expression  // Null-conditional operators C# 6: https://msdn.microsoft.com/en-us/library/dn986595.aspx
	: (fm_primary_expression_start | primary_expression_start) FM_ImplicitToken38 ? (fm_bracket_expressionStar | bracket_expression)* FM_ImplicitToken38 ?
	  (((member_access | method_invocation | FM_ImplicitToken36  | FM_ImplicitToken37  | FM_ImplicitToken39  (fm_identifier | identifier)) FM_ImplicitToken38 ?) (fm_bracket_expressionStar | bracket_expression)* FM_ImplicitToken38 ?)*
	;

primary_expression_start
	: literal                                   #literalExpression
	| (fm_identifier | identifier) (fm_type_argument_listOpt | type_argument_list)?            #simpleNameExpression
	| OPEN_PARENS (fm_expression | expression) CLOSE_PARENS       #parenthesisExpressions
	| predefined_type                           #memberAccessExpression
	| qualified_alias_member                    #memberAccessExpression
	| LITERAL_ACCESS                            #literalAccessExpression
	| THIS                                      #thisReferenceExpression
	| BASE (FM_ImplicitToken1  (fm_identifier | identifier) (fm_type_argument_listOpt | type_argument_list)? | FM_ImplicitToken40  (fm_expression_list | expression_list) FM_ImplicitToken41 ) #baseAccessExpression
	| NEW ((fm_type_ | type_) (object_creation_expression
	             | object_or_collection_initializer
	             | FM_ImplicitToken40  (fm_expression_list | expression_list) FM_ImplicitToken41  (fm_rank_specifierStar | rank_specifier)* (fm_array_initializerOpt | array_initializer)?
	             | (fm_rank_specifierPlus | rank_specifier)+ (fm_array_initializer | array_initializer))
	      | anonymous_object_initializer
	      | (fm_rank_specifier | rank_specifier) (fm_array_initializer | array_initializer))                       #objectCreationExpression
	| OPEN_PARENS (fm_argument | argument) ( FM_ImplicitToken5  (fm_argument | argument) )+ CLOSE_PARENS           #tupleExpression
	| TYPEOF OPEN_PARENS (unbound_type_name | type_ | VOID) CLOSE_PARENS   #typeofExpression
	| CHECKED OPEN_PARENS (fm_expression | expression) CLOSE_PARENS                   #checkedExpression
	| UNCHECKED OPEN_PARENS (fm_expression | expression) CLOSE_PARENS                 #uncheckedExpression
	| DEFAULT (OPEN_PARENS (fm_type_ | type_) CLOSE_PARENS)?                     #defaultValueExpression
	| ASYNC? DELEGATE (OPEN_PARENS (fm_explicit_anonymous_function_parameter_listOpt | explicit_anonymous_function_parameter_list)? CLOSE_PARENS)? (fm_block | block) #anonymousMethodExpression
	| SIZEOF OPEN_PARENS (fm_type_ | type_) CLOSE_PARENS                          #sizeofExpression
	// C# 6: https://msdn.microsoft.com/en-us/library/dn986596.aspx
	| NAMEOF OPEN_PARENS ((fm_identifier | identifier) FM_ImplicitToken1 )* (fm_identifier | identifier) CLOSE_PARENS  #nameofExpression
	;
fm_primary_expression_start: FM_PLACEHOLDER | FM_IF (fm_primary_expression_start | primary_expression_start) (FM_ELSE_IF (fm_primary_expression_start | primary_expression_start))* FM_ELSE (fm_primary_expression_start | primary_expression_start) FM_IF_CLOSE;

throwable_expression
	: expression
	| throw_expression
	;
fm_throwable_expression: FM_PLACEHOLDER | FM_IF (fm_throwable_expression | throwable_expression) (FM_ELSE_IF (fm_throwable_expression | throwable_expression))* FM_ELSE (fm_throwable_expression | throwable_expression) FM_IF_CLOSE;

throw_expression
	: THROW (fm_expression | expression)
	;

member_access
	: FM_ImplicitToken2 ? FM_ImplicitToken1  (fm_identifier | identifier) (fm_type_argument_listOpt | type_argument_list)?
	;

bracket_expression
	: FM_ImplicitToken2 ? FM_ImplicitToken40  (fm_indexer_argument | indexer_argument) ( FM_ImplicitToken5  (fm_indexer_argument | indexer_argument))* FM_ImplicitToken41 
	;
fm_bracket_expressionStar: FM_PLACEHOLDER | FM_IF (fm_bracket_expressionStar | bracket_expression)* (FM_ELSE_IF (fm_bracket_expressionStar | bracket_expression)*)* (FM_ELSE (fm_bracket_expressionStar | bracket_expression)*)? FM_IF_CLOSE | FM_LIST (fm_bracket_expressionStar | bracket_expression)* FM_LIST_CLOSE;

indexer_argument
	: ((fm_identifier | identifier) FM_ImplicitToken9 )? (fm_expression | expression)
	;
fm_indexer_argument: FM_PLACEHOLDER | FM_IF (fm_indexer_argument | indexer_argument) (FM_ELSE_IF (fm_indexer_argument | indexer_argument))* FM_ELSE (fm_indexer_argument | indexer_argument) FM_IF_CLOSE;

predefined_type
	: BOOL | BYTE | CHAR | DECIMAL | DOUBLE | FLOAT | INT | LONG
	| OBJECT | SBYTE | SHORT | STRING | UINT | ULONG | USHORT
	;

expression_list
	: (fm_expression | expression) (FM_ImplicitToken5  (fm_expression | expression))*
	;
fm_expression_list: FM_PLACEHOLDER | FM_IF (fm_expression_list | expression_list) (FM_ELSE_IF (fm_expression_list | expression_list))* FM_ELSE (fm_expression_list | expression_list) FM_IF_CLOSE;

object_or_collection_initializer
	: object_initializer
	| collection_initializer
	;
fm_object_or_collection_initializerOpt: FM_PLACEHOLDER | FM_IF (fm_object_or_collection_initializerOpt | object_or_collection_initializer)? (FM_ELSE_IF (fm_object_or_collection_initializerOpt | object_or_collection_initializer)?)* (FM_ELSE (fm_object_or_collection_initializerOpt | object_or_collection_initializer)?)? FM_IF_CLOSE;

object_initializer
	: OPEN_BRACE ((fm_member_initializer_list | member_initializer_list) FM_ImplicitToken5 ?)? CLOSE_BRACE
	;

member_initializer_list
	: (fm_member_initializer | member_initializer) (FM_ImplicitToken5  (fm_member_initializer | member_initializer))*
	;
fm_member_initializer_list: FM_PLACEHOLDER | FM_IF (fm_member_initializer_list | member_initializer_list) (FM_ELSE_IF (fm_member_initializer_list | member_initializer_list))* FM_ELSE (fm_member_initializer_list | member_initializer_list) FM_IF_CLOSE;

member_initializer
	: (identifier | FM_ImplicitToken40  (fm_expression | expression) FM_ImplicitToken41 ) FM_ImplicitToken11  (fm_initializer_value | initializer_value) // C# 6
	;
fm_member_initializer: FM_PLACEHOLDER | FM_IF (fm_member_initializer | member_initializer) (FM_ELSE_IF (fm_member_initializer | member_initializer))* FM_ELSE (fm_member_initializer | member_initializer) FM_IF_CLOSE;

initializer_value
	: expression
	| object_or_collection_initializer
	;
fm_initializer_value: FM_PLACEHOLDER | FM_IF (fm_initializer_value | initializer_value) (FM_ELSE_IF (fm_initializer_value | initializer_value))* FM_ELSE (fm_initializer_value | initializer_value) FM_IF_CLOSE;

collection_initializer
	: OPEN_BRACE (fm_element_initializer | element_initializer) (FM_ImplicitToken5  (fm_element_initializer | element_initializer))* FM_ImplicitToken5 ? CLOSE_BRACE
	;

element_initializer
	: non_assignment_expression
	| OPEN_BRACE (fm_expression_list | expression_list) CLOSE_BRACE
	;
fm_element_initializer: FM_PLACEHOLDER | FM_IF (fm_element_initializer | element_initializer) (FM_ELSE_IF (fm_element_initializer | element_initializer))* FM_ELSE (fm_element_initializer | element_initializer) FM_IF_CLOSE;

anonymous_object_initializer
	: OPEN_BRACE ((fm_member_declarator_list | member_declarator_list) FM_ImplicitToken5 ?)? CLOSE_BRACE
	;

member_declarator_list
	: (fm_member_declarator | member_declarator) ( FM_ImplicitToken5  (fm_member_declarator | member_declarator))*
	;
fm_member_declarator_list: FM_PLACEHOLDER | FM_IF (fm_member_declarator_list | member_declarator_list) (FM_ELSE_IF (fm_member_declarator_list | member_declarator_list))* FM_ELSE (fm_member_declarator_list | member_declarator_list) FM_IF_CLOSE;

member_declarator
	: primary_expression
	| (fm_identifier | identifier) FM_ImplicitToken11  (fm_expression | expression)
	;
fm_member_declarator: FM_PLACEHOLDER | FM_IF (fm_member_declarator | member_declarator) (FM_ELSE_IF (fm_member_declarator | member_declarator))* FM_ELSE (fm_member_declarator | member_declarator) FM_IF_CLOSE;

unbound_type_name
	: (fm_identifier | identifier) ( (fm_generic_dimension_specifierOpt | generic_dimension_specifier)? | FM_ImplicitToken42  (fm_identifier | identifier) (fm_generic_dimension_specifierOpt | generic_dimension_specifier)?)
	  (FM_ImplicitToken1  (fm_identifier | identifier) (fm_generic_dimension_specifierOpt | generic_dimension_specifier)?)*
	;

generic_dimension_specifier
	: FM_ImplicitToken7  FM_ImplicitToken5 * FM_ImplicitToken8 
	;
fm_generic_dimension_specifierOpt: FM_PLACEHOLDER | FM_IF (fm_generic_dimension_specifierOpt | generic_dimension_specifier)? (FM_ELSE_IF (fm_generic_dimension_specifierOpt | generic_dimension_specifier)?)* (FM_ELSE (fm_generic_dimension_specifierOpt | generic_dimension_specifier)?)? FM_IF_CLOSE;

isType
	: (fm_base_type | base_type) (rank_specifier | FM_ImplicitToken3 )* FM_ImplicitToken2 ? (fm_isTypePatternArmsOpt | isTypePatternArms)? (fm_identifierOpt | identifier)?
	;
fm_isType: FM_PLACEHOLDER | FM_IF (fm_isType | isType) (FM_ELSE_IF (fm_isType | isType))* FM_ELSE (fm_isType | isType) FM_IF_CLOSE;

isTypePatternArms
	: FM_ImplicitToken33  (fm_isTypePatternArm | isTypePatternArm) (FM_ImplicitToken5  (fm_isTypePatternArm | isTypePatternArm))* FM_ImplicitToken34 
	;
fm_isTypePatternArmsOpt: FM_PLACEHOLDER | FM_IF (fm_isTypePatternArmsOpt | isTypePatternArms)? (FM_ELSE_IF (fm_isTypePatternArmsOpt | isTypePatternArms)?)* (FM_ELSE (fm_isTypePatternArmsOpt | isTypePatternArms)?)? FM_IF_CLOSE;

isTypePatternArm
	: (fm_identifier | identifier) FM_ImplicitToken9  (fm_expression | expression)
	;
fm_isTypePatternArm: FM_PLACEHOLDER | FM_IF (fm_isTypePatternArm | isTypePatternArm) (FM_ELSE_IF (fm_isTypePatternArm | isTypePatternArm))* FM_ELSE (fm_isTypePatternArm | isTypePatternArm) FM_IF_CLOSE;

lambda_expression
	: ASYNC? (fm_anonymous_function_signature | anonymous_function_signature) (fm_right_arrow | right_arrow) (fm_anonymous_function_body | anonymous_function_body)
	;

anonymous_function_signature
	: OPEN_PARENS CLOSE_PARENS
	| OPEN_PARENS (fm_explicit_anonymous_function_parameter_list | explicit_anonymous_function_parameter_list) CLOSE_PARENS
	| OPEN_PARENS (fm_implicit_anonymous_function_parameter_list | implicit_anonymous_function_parameter_list) CLOSE_PARENS
	| identifier
	;
fm_anonymous_function_signature: FM_PLACEHOLDER | FM_IF (fm_anonymous_function_signature | anonymous_function_signature) (FM_ELSE_IF (fm_anonymous_function_signature | anonymous_function_signature))* FM_ELSE (fm_anonymous_function_signature | anonymous_function_signature) FM_IF_CLOSE;

explicit_anonymous_function_parameter_list
	: (fm_explicit_anonymous_function_parameter | explicit_anonymous_function_parameter) ( FM_ImplicitToken5  (fm_explicit_anonymous_function_parameter | explicit_anonymous_function_parameter))*
	;
fm_explicit_anonymous_function_parameter_list: FM_PLACEHOLDER | FM_IF (fm_explicit_anonymous_function_parameter_list | explicit_anonymous_function_parameter_list) (FM_ELSE_IF (fm_explicit_anonymous_function_parameter_list | explicit_anonymous_function_parameter_list))* FM_ELSE (fm_explicit_anonymous_function_parameter_list | explicit_anonymous_function_parameter_list) FM_IF_CLOSE;
fm_explicit_anonymous_function_parameter_listOpt: FM_PLACEHOLDER | FM_IF (fm_explicit_anonymous_function_parameter_listOpt | explicit_anonymous_function_parameter_list)? (FM_ELSE_IF (fm_explicit_anonymous_function_parameter_listOpt | explicit_anonymous_function_parameter_list)?)* (FM_ELSE (fm_explicit_anonymous_function_parameter_listOpt | explicit_anonymous_function_parameter_list)?)? FM_IF_CLOSE;

explicit_anonymous_function_parameter
	: refout=(REF | OUT | IN)? (fm_type_ | type_) (fm_identifier | identifier)
	;
fm_explicit_anonymous_function_parameter: FM_PLACEHOLDER | FM_IF (fm_explicit_anonymous_function_parameter | explicit_anonymous_function_parameter) (FM_ELSE_IF (fm_explicit_anonymous_function_parameter | explicit_anonymous_function_parameter))* FM_ELSE (fm_explicit_anonymous_function_parameter | explicit_anonymous_function_parameter) FM_IF_CLOSE;

implicit_anonymous_function_parameter_list
	: (fm_identifier | identifier) (FM_ImplicitToken5  (fm_identifier | identifier))*
	;
fm_implicit_anonymous_function_parameter_list: FM_PLACEHOLDER | FM_IF (fm_implicit_anonymous_function_parameter_list | implicit_anonymous_function_parameter_list) (FM_ELSE_IF (fm_implicit_anonymous_function_parameter_list | implicit_anonymous_function_parameter_list))* FM_ELSE (fm_implicit_anonymous_function_parameter_list | implicit_anonymous_function_parameter_list) FM_IF_CLOSE;

anonymous_function_body
	: throwable_expression
	| block
	;
fm_anonymous_function_body: FM_PLACEHOLDER | FM_IF (fm_anonymous_function_body | anonymous_function_body) (FM_ELSE_IF (fm_anonymous_function_body | anonymous_function_body))* FM_ELSE (fm_anonymous_function_body | anonymous_function_body) FM_IF_CLOSE;

query_expression
	: (fm_from_clause | from_clause) (fm_query_body | query_body)
	;

from_clause
	: FROM (fm_type_Opt | type_)? (fm_identifier | identifier) IN (fm_expression | expression)
	;
fm_from_clause: FM_PLACEHOLDER | FM_IF (fm_from_clause | from_clause) (FM_ELSE_IF (fm_from_clause | from_clause))* FM_ELSE (fm_from_clause | from_clause) FM_IF_CLOSE;

query_body
	: (fm_query_body_clauseStar | query_body_clause)* (fm_select_or_group_clause | select_or_group_clause) (fm_query_continuationOpt | query_continuation)?
	;
fm_query_body: FM_PLACEHOLDER | FM_IF (fm_query_body | query_body) (FM_ELSE_IF (fm_query_body | query_body))* FM_ELSE (fm_query_body | query_body) FM_IF_CLOSE;

query_body_clause
	: from_clause
	| let_clause
	| where_clause
	| combined_join_clause
	| orderby_clause
	;
fm_query_body_clauseStar: FM_PLACEHOLDER | FM_IF (fm_query_body_clauseStar | query_body_clause)* (FM_ELSE_IF (fm_query_body_clauseStar | query_body_clause)*)* (FM_ELSE (fm_query_body_clauseStar | query_body_clause)*)? FM_IF_CLOSE | FM_LIST (fm_query_body_clauseStar | query_body_clause)* FM_LIST_CLOSE;

let_clause
	: LET (fm_identifier | identifier) FM_ImplicitToken11  (fm_expression | expression)
	;

where_clause
	: WHERE (fm_expression | expression)
	;

combined_join_clause
	: JOIN (fm_type_Opt | type_)? (fm_identifier | identifier) IN (fm_expression | expression) ON (fm_expression | expression) EQUALS (fm_expression | expression) (INTO (fm_identifier | identifier))?
	;

orderby_clause
	: ORDERBY (fm_ordering | ordering) (FM_ImplicitToken5   (fm_ordering | ordering))*
	;

ordering
	: (fm_expression | expression) dir=(ASCENDING | DESCENDING)?
	;
fm_ordering: FM_PLACEHOLDER | FM_IF (fm_ordering | ordering) (FM_ELSE_IF (fm_ordering | ordering))* FM_ELSE (fm_ordering | ordering) FM_IF_CLOSE;

select_or_group_clause
	: SELECT (fm_expression | expression)
	| GROUP (fm_expression | expression) BY (fm_expression | expression)
	;
fm_select_or_group_clause: FM_PLACEHOLDER | FM_IF (fm_select_or_group_clause | select_or_group_clause) (FM_ELSE_IF (fm_select_or_group_clause | select_or_group_clause))* FM_ELSE (fm_select_or_group_clause | select_or_group_clause) FM_IF_CLOSE;

query_continuation
	: INTO (fm_identifier | identifier) (fm_query_body | query_body)
	;
fm_query_continuationOpt: FM_PLACEHOLDER | FM_IF (fm_query_continuationOpt | query_continuation)? (FM_ELSE_IF (fm_query_continuationOpt | query_continuation)?)* (FM_ELSE (fm_query_continuationOpt | query_continuation)?)? FM_IF_CLOSE;

//B.2.5 Statements
statement
	: labeled_Statement
	| declarationStatement
	| embedded_statement
	;
fm_statement: FM_PLACEHOLDER | FM_IF (fm_statement | statement) (FM_ELSE_IF (fm_statement | statement))* FM_ELSE (fm_statement | statement) FM_IF_CLOSE;
fm_statementPlus: FM_PLACEHOLDER | (FM_IF (fm_statementPlus | statement)* (FM_ELSE_IF (fm_statementPlus | statement)*)* (FM_ELSE (fm_statementPlus | statement)*)? FM_IF_CLOSE | FM_LIST (fm_statementPlus | statement)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_statementPlus | statement)* (FM_ELSE_IF (fm_statementPlus | statement)*)* FM_ELSE (fm_statementPlus | statement)* FM_IF_CLOSE | FM_LIST (fm_statementPlus | statement)* FM_ELSE (fm_statementPlus | statement)* FM_LIST_CLOSE) (FM_IF (fm_statementPlus | statement)* (FM_ELSE_IF (fm_statementPlus | statement)*)* (FM_ELSE (fm_statementPlus | statement)*)? FM_IF_CLOSE | FM_LIST (fm_statementPlus | statement)* FM_LIST_CLOSE)*;

declarationStatement
	: (fm_local_variable_declaration | local_variable_declaration) FM_ImplicitToken43 
	| (fm_local_constant_declaration | local_constant_declaration) FM_ImplicitToken43 
	| local_function_declaration
	;

local_function_declaration
    : (fm_local_function_header | local_function_header) (fm_local_function_body | local_function_body)
    ;

local_function_header
    : (fm_local_function_modifiersOpt | local_function_modifiers)? (fm_return_type | return_type) (fm_identifier | identifier) (fm_type_parameter_listOpt | type_parameter_list)?
        OPEN_PARENS (fm_formal_parameter_listOpt | formal_parameter_list)? CLOSE_PARENS (fm_type_parameter_constraints_clausesOpt | type_parameter_constraints_clauses)?
    ;
fm_local_function_header: FM_PLACEHOLDER | FM_IF (fm_local_function_header | local_function_header) (FM_ELSE_IF (fm_local_function_header | local_function_header))* FM_ELSE (fm_local_function_header | local_function_header) FM_IF_CLOSE;

local_function_modifiers
    : (ASYNC | UNSAFE) STATIC?
    | STATIC (ASYNC | UNSAFE)
    ;
fm_local_function_modifiersOpt: FM_PLACEHOLDER | FM_IF (fm_local_function_modifiersOpt | local_function_modifiers)? (FM_ELSE_IF (fm_local_function_modifiersOpt | local_function_modifiers)?)* (FM_ELSE (fm_local_function_modifiersOpt | local_function_modifiers)?)? FM_IF_CLOSE;

local_function_body
    : block
    | (fm_right_arrow | right_arrow) (fm_throwable_expression | throwable_expression) FM_ImplicitToken43 
    ;
fm_local_function_body: FM_PLACEHOLDER | FM_IF (fm_local_function_body | local_function_body) (FM_ELSE_IF (fm_local_function_body | local_function_body))* FM_ELSE (fm_local_function_body | local_function_body) FM_IF_CLOSE;

labeled_Statement
	: (fm_identifier | identifier) FM_ImplicitToken9  (fm_statement | statement)  
	;

embedded_statement
	: block
	| simple_embedded_statement
	;
fm_embedded_statement: FM_PLACEHOLDER | FM_IF (fm_embedded_statement | embedded_statement) (FM_ELSE_IF (fm_embedded_statement | embedded_statement))* FM_ELSE (fm_embedded_statement | embedded_statement) FM_IF_CLOSE;

simple_embedded_statement
	: FM_ImplicitToken43                                                          #theEmptyStatement
	| (fm_expression | expression) FM_ImplicitToken43                                               #expressionStatement

	// selection statements
	| IF OPEN_PARENS (fm_expression | expression) CLOSE_PARENS (fm_if_body | if_body) (ELSE (fm_if_body | if_body))?               #ifStatement
    | SWITCH OPEN_PARENS (fm_expression | expression) CLOSE_PARENS OPEN_BRACE (fm_switch_sectionStar | switch_section)* CLOSE_BRACE           #switchStatement

    // iteration statements
	| WHILE OPEN_PARENS (fm_expression | expression) CLOSE_PARENS (fm_embedded_statement | embedded_statement)                                        #whileStatement
	| DO (fm_embedded_statement | embedded_statement) WHILE OPEN_PARENS (fm_expression | expression) CLOSE_PARENS FM_ImplicitToken43                                  #doStatement
	| FOR OPEN_PARENS (fm_for_initializerOpt | for_initializer)? FM_ImplicitToken43  (fm_expressionOpt | expression)? FM_ImplicitToken43  (fm_for_iteratorOpt | for_iterator)? CLOSE_PARENS (fm_embedded_statement | embedded_statement)  #forStatement
	| AWAIT? FOREACH OPEN_PARENS (fm_local_variable_type | local_variable_type) (fm_identifier | identifier) IN (fm_expression | expression) CLOSE_PARENS (fm_embedded_statement | embedded_statement)    #foreachStatement

    // jump statements
	| BREAK FM_ImplicitToken43                                                    #breakStatement
	| CONTINUE FM_ImplicitToken43                                                 #continueStatement
	| GOTO (identifier | CASE (fm_expression | expression) | DEFAULT) FM_ImplicitToken43            #gotoStatement
	| RETURN (fm_expressionOpt | expression)? FM_ImplicitToken43                                       #returnStatement
	| THROW (fm_expressionOpt | expression)? FM_ImplicitToken43                                        #throwStatement

	| TRY (fm_block | block) ((fm_catch_clauses | catch_clauses) (fm_finally_clauseOpt | finally_clause)? | finally_clause)  #tryStatement
	| CHECKED (fm_block | block)                                               #checkedStatement
	| UNCHECKED (fm_block | block)                                             #uncheckedStatement
	| LOCK OPEN_PARENS (fm_expression | expression) CLOSE_PARENS (fm_embedded_statement | embedded_statement)                  #lockStatement
	| USING OPEN_PARENS (fm_resource_acquisition | resource_acquisition) CLOSE_PARENS (fm_embedded_statement | embedded_statement)       #usingStatement
	| YIELD (RETURN (fm_expression | expression) | BREAK) FM_ImplicitToken43                        #yieldStatement

	// unsafe statements
	| UNSAFE (fm_block | block)                                                                       #unsafeStatement
	| FIXED OPEN_PARENS (fm_pointer_type | pointer_type) (fm_fixed_pointer_declarators | fixed_pointer_declarators) CLOSE_PARENS (fm_embedded_statement | embedded_statement)            #fixedStatement
	;

block
	: OPEN_BRACE (fm_statement_listOpt | statement_list)? CLOSE_BRACE
	;
fm_block: FM_PLACEHOLDER | FM_IF (fm_block | block) (FM_ELSE_IF (fm_block | block))* FM_ELSE (fm_block | block) FM_IF_CLOSE;

local_variable_declaration
	: (USING | REF | REF READONLY)? (fm_local_variable_type | local_variable_type) (fm_local_variable_declarator | local_variable_declarator) ( FM_ImplicitToken5   (fm_local_variable_declarator | local_variable_declarator) { this.IsLocalVariableDeclaration() }? )*
	| FIXED (fm_pointer_type | pointer_type) (fm_fixed_pointer_declarators | fixed_pointer_declarators)
	;
fm_local_variable_declaration: FM_PLACEHOLDER | FM_IF (fm_local_variable_declaration | local_variable_declaration) (FM_ELSE_IF (fm_local_variable_declaration | local_variable_declaration))* FM_ELSE (fm_local_variable_declaration | local_variable_declaration) FM_IF_CLOSE;

local_variable_type 
	: VAR
	| type_
	;
fm_local_variable_type: FM_PLACEHOLDER | FM_IF (fm_local_variable_type | local_variable_type) (FM_ELSE_IF (fm_local_variable_type | local_variable_type))* FM_ELSE (fm_local_variable_type | local_variable_type) FM_IF_CLOSE;

local_variable_declarator
	: (fm_identifier | identifier) (FM_ImplicitToken11  REF? (fm_local_variable_initializer | local_variable_initializer) )?
	;
fm_local_variable_declarator: FM_PLACEHOLDER | FM_IF (fm_local_variable_declarator | local_variable_declarator) (FM_ELSE_IF (fm_local_variable_declarator | local_variable_declarator))* FM_ELSE (fm_local_variable_declarator | local_variable_declarator) FM_IF_CLOSE;

local_variable_initializer
	: expression
	| array_initializer
	| stackalloc_initializer
	;
fm_local_variable_initializer: FM_PLACEHOLDER | FM_IF (fm_local_variable_initializer | local_variable_initializer) (FM_ELSE_IF (fm_local_variable_initializer | local_variable_initializer))* FM_ELSE (fm_local_variable_initializer | local_variable_initializer) FM_IF_CLOSE;

local_constant_declaration
	: CONST (fm_type_ | type_) (fm_constant_declarators | constant_declarators)
	;
fm_local_constant_declaration: FM_PLACEHOLDER | FM_IF (fm_local_constant_declaration | local_constant_declaration) (FM_ELSE_IF (fm_local_constant_declaration | local_constant_declaration))* FM_ELSE (fm_local_constant_declaration | local_constant_declaration) FM_IF_CLOSE;

if_body
	: block
	| simple_embedded_statement
	;
fm_if_body: FM_PLACEHOLDER | FM_IF (fm_if_body | if_body) (FM_ELSE_IF (fm_if_body | if_body))* FM_ELSE (fm_if_body | if_body) FM_IF_CLOSE;

switch_section
	: (fm_switch_labelPlus | switch_label)+ (fm_statement_list | statement_list)
	;
fm_switch_sectionStar: FM_PLACEHOLDER | FM_IF (fm_switch_sectionStar | switch_section)* (FM_ELSE_IF (fm_switch_sectionStar | switch_section)*)* (FM_ELSE (fm_switch_sectionStar | switch_section)*)? FM_IF_CLOSE | FM_LIST (fm_switch_sectionStar | switch_section)* FM_LIST_CLOSE;

switch_label
	: CASE (fm_expression | expression) (fm_case_guardOpt | case_guard)? FM_ImplicitToken9 
	| DEFAULT FM_ImplicitToken9 
	;
fm_switch_labelPlus: FM_PLACEHOLDER | (FM_IF (fm_switch_labelPlus | switch_label)* (FM_ELSE_IF (fm_switch_labelPlus | switch_label)*)* (FM_ELSE (fm_switch_labelPlus | switch_label)*)? FM_IF_CLOSE | FM_LIST (fm_switch_labelPlus | switch_label)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_switch_labelPlus | switch_label)* (FM_ELSE_IF (fm_switch_labelPlus | switch_label)*)* FM_ELSE (fm_switch_labelPlus | switch_label)* FM_IF_CLOSE | FM_LIST (fm_switch_labelPlus | switch_label)* FM_ELSE (fm_switch_labelPlus | switch_label)* FM_LIST_CLOSE) (FM_IF (fm_switch_labelPlus | switch_label)* (FM_ELSE_IF (fm_switch_labelPlus | switch_label)*)* (FM_ELSE (fm_switch_labelPlus | switch_label)*)? FM_IF_CLOSE | FM_LIST (fm_switch_labelPlus | switch_label)* FM_LIST_CLOSE)*;

case_guard
	: WHEN (fm_expression | expression)
	;
fm_case_guardOpt: FM_PLACEHOLDER | FM_IF (fm_case_guardOpt | case_guard)? (FM_ELSE_IF (fm_case_guardOpt | case_guard)?)* (FM_ELSE (fm_case_guardOpt | case_guard)?)? FM_IF_CLOSE;

statement_list
	: (fm_statementPlus | statement)+
	;
fm_statement_list: FM_PLACEHOLDER | FM_IF (fm_statement_list | statement_list) (FM_ELSE_IF (fm_statement_list | statement_list))* FM_ELSE (fm_statement_list | statement_list) FM_IF_CLOSE;
fm_statement_listOpt: FM_PLACEHOLDER | FM_IF (fm_statement_listOpt | statement_list)? (FM_ELSE_IF (fm_statement_listOpt | statement_list)?)* (FM_ELSE (fm_statement_listOpt | statement_list)?)? FM_IF_CLOSE;

for_initializer
	: local_variable_declaration
	| (fm_expression | expression) (FM_ImplicitToken5   (fm_expression | expression))*
	;
fm_for_initializerOpt: FM_PLACEHOLDER | FM_IF (fm_for_initializerOpt | for_initializer)? (FM_ELSE_IF (fm_for_initializerOpt | for_initializer)?)* (FM_ELSE (fm_for_initializerOpt | for_initializer)?)? FM_IF_CLOSE;

for_iterator
	: (fm_expression | expression) (FM_ImplicitToken5   (fm_expression | expression))*
	;
fm_for_iteratorOpt: FM_PLACEHOLDER | FM_IF (fm_for_iteratorOpt | for_iterator)? (FM_ELSE_IF (fm_for_iteratorOpt | for_iterator)?)* (FM_ELSE (fm_for_iteratorOpt | for_iterator)?)? FM_IF_CLOSE;

catch_clauses
	: (fm_specific_catch_clause | specific_catch_clause) (specific_catch_clause)* (fm_general_catch_clauseOpt | general_catch_clause)?
	| general_catch_clause
	;
fm_catch_clauses: FM_PLACEHOLDER | FM_IF (fm_catch_clauses | catch_clauses) (FM_ELSE_IF (fm_catch_clauses | catch_clauses))* FM_ELSE (fm_catch_clauses | catch_clauses) FM_IF_CLOSE;

specific_catch_clause
	: CATCH OPEN_PARENS (fm_class_type | class_type) (fm_identifierOpt | identifier)? CLOSE_PARENS (fm_exception_filterOpt | exception_filter)? (fm_block | block)
	;
fm_specific_catch_clause: FM_PLACEHOLDER | FM_IF (fm_specific_catch_clause | specific_catch_clause) (FM_ELSE_IF (fm_specific_catch_clause | specific_catch_clause))* FM_ELSE (fm_specific_catch_clause | specific_catch_clause) FM_IF_CLOSE;

general_catch_clause
	: CATCH (fm_exception_filterOpt | exception_filter)? (fm_block | block)
	;
fm_general_catch_clauseOpt: FM_PLACEHOLDER | FM_IF (fm_general_catch_clauseOpt | general_catch_clause)? (FM_ELSE_IF (fm_general_catch_clauseOpt | general_catch_clause)?)* (FM_ELSE (fm_general_catch_clauseOpt | general_catch_clause)?)? FM_IF_CLOSE;

exception_filter // C# 6
	: WHEN OPEN_PARENS (fm_expression | expression) CLOSE_PARENS
	;
fm_exception_filterOpt: FM_PLACEHOLDER | FM_IF (fm_exception_filterOpt | exception_filter)? (FM_ELSE_IF (fm_exception_filterOpt | exception_filter)?)* (FM_ELSE (fm_exception_filterOpt | exception_filter)?)? FM_IF_CLOSE;

finally_clause
	: FINALLY (fm_block | block)
	;
fm_finally_clauseOpt: FM_PLACEHOLDER | FM_IF (fm_finally_clauseOpt | finally_clause)? (FM_ELSE_IF (fm_finally_clauseOpt | finally_clause)?)* (FM_ELSE (fm_finally_clauseOpt | finally_clause)?)? FM_IF_CLOSE;

resource_acquisition
	: local_variable_declaration
	| expression
	;
fm_resource_acquisition: FM_PLACEHOLDER | FM_IF (fm_resource_acquisition | resource_acquisition) (FM_ELSE_IF (fm_resource_acquisition | resource_acquisition))* FM_ELSE (fm_resource_acquisition | resource_acquisition) FM_IF_CLOSE;

//B.2.6 Namespaces;
namespace_declaration
	: NAMESPACE (fm_qualified_identifier | qualified_identifier) (fm_namespace_body | namespace_body) FM_ImplicitToken43 ?
	;

qualified_identifier
	: (fm_identifier | identifier) ( FM_ImplicitToken1   (fm_identifier | identifier) )*
	;
fm_qualified_identifier: FM_PLACEHOLDER | FM_IF (fm_qualified_identifier | qualified_identifier) (FM_ELSE_IF (fm_qualified_identifier | qualified_identifier))* FM_ELSE (fm_qualified_identifier | qualified_identifier) FM_IF_CLOSE;

namespace_body
	: OPEN_BRACE (fm_extern_alias_directivesOpt | extern_alias_directives)? (fm_using_directivesOpt | using_directives)? (fm_namespace_member_declarationsOpt | namespace_member_declarations)? CLOSE_BRACE
	;
fm_namespace_body: FM_PLACEHOLDER | FM_IF (fm_namespace_body | namespace_body) (FM_ELSE_IF (fm_namespace_body | namespace_body))* FM_ELSE (fm_namespace_body | namespace_body) FM_IF_CLOSE;

extern_alias_directives
	: (fm_extern_alias_directivePlus | extern_alias_directive)+
	;
fm_extern_alias_directivesOpt: FM_PLACEHOLDER | FM_IF (fm_extern_alias_directivesOpt | extern_alias_directives)? (FM_ELSE_IF (fm_extern_alias_directivesOpt | extern_alias_directives)?)* (FM_ELSE (fm_extern_alias_directivesOpt | extern_alias_directives)?)? FM_IF_CLOSE;

extern_alias_directive
	: EXTERN ALIAS (fm_identifier | identifier) FM_ImplicitToken43 
	;
fm_extern_alias_directivePlus: FM_PLACEHOLDER | (FM_IF (fm_extern_alias_directivePlus | extern_alias_directive)* (FM_ELSE_IF (fm_extern_alias_directivePlus | extern_alias_directive)*)* (FM_ELSE (fm_extern_alias_directivePlus | extern_alias_directive)*)? FM_IF_CLOSE | FM_LIST (fm_extern_alias_directivePlus | extern_alias_directive)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_extern_alias_directivePlus | extern_alias_directive)* (FM_ELSE_IF (fm_extern_alias_directivePlus | extern_alias_directive)*)* FM_ELSE (fm_extern_alias_directivePlus | extern_alias_directive)* FM_IF_CLOSE | FM_LIST (fm_extern_alias_directivePlus | extern_alias_directive)* FM_ELSE (fm_extern_alias_directivePlus | extern_alias_directive)* FM_LIST_CLOSE) (FM_IF (fm_extern_alias_directivePlus | extern_alias_directive)* (FM_ELSE_IF (fm_extern_alias_directivePlus | extern_alias_directive)*)* (FM_ELSE (fm_extern_alias_directivePlus | extern_alias_directive)*)? FM_IF_CLOSE | FM_LIST (fm_extern_alias_directivePlus | extern_alias_directive)* FM_LIST_CLOSE)*;

using_directives
	: (fm_using_directivePlus | using_directive)+
	;
fm_using_directivesOpt: FM_PLACEHOLDER | FM_IF (fm_using_directivesOpt | using_directives)? (FM_ELSE_IF (fm_using_directivesOpt | using_directives)?)* (FM_ELSE (fm_using_directivesOpt | using_directives)?)? FM_IF_CLOSE;

using_directive
	: USING (fm_identifier | identifier) FM_ImplicitToken11  (fm_namespace_or_type_name | namespace_or_type_name) FM_ImplicitToken43             #usingAliasDirective
	| USING (fm_namespace_or_type_name | namespace_or_type_name) FM_ImplicitToken43                            #usingNamespaceDirective
	// C# 6: https://msdn.microsoft.com/en-us/library/ms228593.aspx
	| USING STATIC (fm_namespace_or_type_name | namespace_or_type_name) FM_ImplicitToken43                     #usingStaticDirective
	;
fm_using_directivePlus: FM_PLACEHOLDER | (FM_IF (fm_using_directivePlus | using_directive)* (FM_ELSE_IF (fm_using_directivePlus | using_directive)*)* (FM_ELSE (fm_using_directivePlus | using_directive)*)? FM_IF_CLOSE | FM_LIST (fm_using_directivePlus | using_directive)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_using_directivePlus | using_directive)* (FM_ELSE_IF (fm_using_directivePlus | using_directive)*)* FM_ELSE (fm_using_directivePlus | using_directive)* FM_IF_CLOSE | FM_LIST (fm_using_directivePlus | using_directive)* FM_ELSE (fm_using_directivePlus | using_directive)* FM_LIST_CLOSE) (FM_IF (fm_using_directivePlus | using_directive)* (FM_ELSE_IF (fm_using_directivePlus | using_directive)*)* (FM_ELSE (fm_using_directivePlus | using_directive)*)? FM_IF_CLOSE | FM_LIST (fm_using_directivePlus | using_directive)* FM_LIST_CLOSE)*;

namespace_member_declarations
	: (fm_namespace_member_declarationPlus | namespace_member_declaration)+
	;
fm_namespace_member_declarationsOpt: FM_PLACEHOLDER | FM_IF (fm_namespace_member_declarationsOpt | namespace_member_declarations)? (FM_ELSE_IF (fm_namespace_member_declarationsOpt | namespace_member_declarations)?)* (FM_ELSE (fm_namespace_member_declarationsOpt | namespace_member_declarations)?)? FM_IF_CLOSE;

namespace_member_declaration
	: namespace_declaration
	| type_declaration
	;
fm_namespace_member_declarationPlus: FM_PLACEHOLDER | (FM_IF (fm_namespace_member_declarationPlus | namespace_member_declaration)* (FM_ELSE_IF (fm_namespace_member_declarationPlus | namespace_member_declaration)*)* (FM_ELSE (fm_namespace_member_declarationPlus | namespace_member_declaration)*)? FM_IF_CLOSE | FM_LIST (fm_namespace_member_declarationPlus | namespace_member_declaration)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_namespace_member_declarationPlus | namespace_member_declaration)* (FM_ELSE_IF (fm_namespace_member_declarationPlus | namespace_member_declaration)*)* FM_ELSE (fm_namespace_member_declarationPlus | namespace_member_declaration)* FM_IF_CLOSE | FM_LIST (fm_namespace_member_declarationPlus | namespace_member_declaration)* FM_ELSE (fm_namespace_member_declarationPlus | namespace_member_declaration)* FM_LIST_CLOSE) (FM_IF (fm_namespace_member_declarationPlus | namespace_member_declaration)* (FM_ELSE_IF (fm_namespace_member_declarationPlus | namespace_member_declaration)*)* (FM_ELSE (fm_namespace_member_declarationPlus | namespace_member_declaration)*)? FM_IF_CLOSE | FM_LIST (fm_namespace_member_declarationPlus | namespace_member_declaration)* FM_LIST_CLOSE)*;

type_declaration
	: (fm_attributesOpt | attributes)? (fm_all_member_modifiersOpt | all_member_modifiers)?
      (class_definition | struct_definition | interface_definition | enum_definition | delegate_definition)
  ;

qualified_alias_member
	: (fm_identifier | identifier) FM_ImplicitToken42  (fm_identifier | identifier) (fm_type_argument_listOpt | type_argument_list)?
	;

//B.2.7 Classes;
type_parameter_list
	: FM_ImplicitToken7  (fm_type_parameter | type_parameter) (FM_ImplicitToken5   (fm_type_parameter | type_parameter))* FM_ImplicitToken8 
	;
fm_type_parameter_listOpt: FM_PLACEHOLDER | FM_IF (fm_type_parameter_listOpt | type_parameter_list)? (FM_ELSE_IF (fm_type_parameter_listOpt | type_parameter_list)?)* (FM_ELSE (fm_type_parameter_listOpt | type_parameter_list)?)? FM_IF_CLOSE;

type_parameter
	: (fm_attributesOpt | attributes)? (fm_identifier | identifier)
	;
fm_type_parameter: FM_PLACEHOLDER | FM_IF (fm_type_parameter | type_parameter) (FM_ELSE_IF (fm_type_parameter | type_parameter))* FM_ELSE (fm_type_parameter | type_parameter) FM_IF_CLOSE;

class_base
	: FM_ImplicitToken9  (fm_class_type | class_type) (FM_ImplicitToken5   (fm_namespace_or_type_name | namespace_or_type_name))*
	;
fm_class_baseOpt: FM_PLACEHOLDER | FM_IF (fm_class_baseOpt | class_base)? (FM_ELSE_IF (fm_class_baseOpt | class_base)?)* (FM_ELSE (fm_class_baseOpt | class_base)?)? FM_IF_CLOSE;

interface_type_list
	: (fm_namespace_or_type_name | namespace_or_type_name) (FM_ImplicitToken5   (fm_namespace_or_type_name | namespace_or_type_name))*
	;
fm_interface_type_list: FM_PLACEHOLDER | FM_IF (fm_interface_type_list | interface_type_list) (FM_ELSE_IF (fm_interface_type_list | interface_type_list))* FM_ELSE (fm_interface_type_list | interface_type_list) FM_IF_CLOSE;

type_parameter_constraints_clauses
	: (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)+
	;
fm_type_parameter_constraints_clausesOpt: FM_PLACEHOLDER | FM_IF (fm_type_parameter_constraints_clausesOpt | type_parameter_constraints_clauses)? (FM_ELSE_IF (fm_type_parameter_constraints_clausesOpt | type_parameter_constraints_clauses)?)* (FM_ELSE (fm_type_parameter_constraints_clausesOpt | type_parameter_constraints_clauses)?)? FM_IF_CLOSE;

type_parameter_constraints_clause
	: WHERE (fm_identifier | identifier) FM_ImplicitToken9  (fm_type_parameter_constraints | type_parameter_constraints)
	;
fm_type_parameter_constraints_clausePlus: FM_PLACEHOLDER | (FM_IF (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)* (FM_ELSE_IF (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)*)* (FM_ELSE (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)*)? FM_IF_CLOSE | FM_LIST (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)* (FM_ELSE_IF (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)*)* FM_ELSE (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)* FM_IF_CLOSE | FM_LIST (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)* FM_ELSE (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)* FM_LIST_CLOSE) (FM_IF (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)* (FM_ELSE_IF (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)*)* (FM_ELSE (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)*)? FM_IF_CLOSE | FM_LIST (fm_type_parameter_constraints_clausePlus | type_parameter_constraints_clause)* FM_LIST_CLOSE)*;

type_parameter_constraints
	: constructor_constraint
	| (fm_primary_constraint | primary_constraint) (FM_ImplicitToken5  (fm_secondary_constraints | secondary_constraints))? (FM_ImplicitToken5  (fm_constructor_constraint | constructor_constraint))?
	;
fm_type_parameter_constraints: FM_PLACEHOLDER | FM_IF (fm_type_parameter_constraints | type_parameter_constraints) (FM_ELSE_IF (fm_type_parameter_constraints | type_parameter_constraints))* FM_ELSE (fm_type_parameter_constraints | type_parameter_constraints) FM_IF_CLOSE;

primary_constraint
	: class_type
	| CLASS FM_ImplicitToken2 ?
	| STRUCT
	| UNMANAGED
	;
fm_primary_constraint: FM_PLACEHOLDER | FM_IF (fm_primary_constraint | primary_constraint) (FM_ELSE_IF (fm_primary_constraint | primary_constraint))* FM_ELSE (fm_primary_constraint | primary_constraint) FM_IF_CLOSE;

// namespace_or_type_name includes identifier
secondary_constraints
	: (fm_namespace_or_type_name | namespace_or_type_name) (FM_ImplicitToken5  (fm_namespace_or_type_name | namespace_or_type_name))*
	;
fm_secondary_constraints: FM_PLACEHOLDER | FM_IF (fm_secondary_constraints | secondary_constraints) (FM_ELSE_IF (fm_secondary_constraints | secondary_constraints))* FM_ELSE (fm_secondary_constraints | secondary_constraints) FM_IF_CLOSE;

constructor_constraint
	: NEW OPEN_PARENS CLOSE_PARENS
	;
fm_constructor_constraint: FM_PLACEHOLDER | FM_IF (fm_constructor_constraint | constructor_constraint) (FM_ELSE_IF (fm_constructor_constraint | constructor_constraint))* FM_ELSE (fm_constructor_constraint | constructor_constraint) FM_IF_CLOSE;

class_body
	: OPEN_BRACE (fm_class_member_declarationsOpt | class_member_declarations)? CLOSE_BRACE
	;
fm_class_body: FM_PLACEHOLDER | FM_IF (fm_class_body | class_body) (FM_ELSE_IF (fm_class_body | class_body))* FM_ELSE (fm_class_body | class_body) FM_IF_CLOSE;

class_member_declarations
	: (fm_class_member_declarationPlus | class_member_declaration)+
	;
fm_class_member_declarationsOpt: FM_PLACEHOLDER | FM_IF (fm_class_member_declarationsOpt | class_member_declarations)? (FM_ELSE_IF (fm_class_member_declarationsOpt | class_member_declarations)?)* (FM_ELSE (fm_class_member_declarationsOpt | class_member_declarations)?)? FM_IF_CLOSE;

class_member_declaration
	: (fm_attributesOpt | attributes)? (fm_all_member_modifiersOpt | all_member_modifiers)? (common_member_declaration | destructor_definition)
	;
fm_class_member_declarationPlus: FM_PLACEHOLDER | (FM_IF (fm_class_member_declarationPlus | class_member_declaration)* (FM_ELSE_IF (fm_class_member_declarationPlus | class_member_declaration)*)* (FM_ELSE (fm_class_member_declarationPlus | class_member_declaration)*)? FM_IF_CLOSE | FM_LIST (fm_class_member_declarationPlus | class_member_declaration)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_class_member_declarationPlus | class_member_declaration)* (FM_ELSE_IF (fm_class_member_declarationPlus | class_member_declaration)*)* FM_ELSE (fm_class_member_declarationPlus | class_member_declaration)* FM_IF_CLOSE | FM_LIST (fm_class_member_declarationPlus | class_member_declaration)* FM_ELSE (fm_class_member_declarationPlus | class_member_declaration)* FM_LIST_CLOSE) (FM_IF (fm_class_member_declarationPlus | class_member_declaration)* (FM_ELSE_IF (fm_class_member_declarationPlus | class_member_declaration)*)* (FM_ELSE (fm_class_member_declarationPlus | class_member_declaration)*)? FM_IF_CLOSE | FM_LIST (fm_class_member_declarationPlus | class_member_declaration)* FM_LIST_CLOSE)*;

all_member_modifiers
	: (fm_all_member_modifierPlus | all_member_modifier)+
	;
fm_all_member_modifiersOpt: FM_PLACEHOLDER | FM_IF (fm_all_member_modifiersOpt | all_member_modifiers)? (FM_ELSE_IF (fm_all_member_modifiersOpt | all_member_modifiers)?)* (FM_ELSE (fm_all_member_modifiersOpt | all_member_modifiers)?)? FM_IF_CLOSE;

all_member_modifier
	: NEW
	| PUBLIC
	| PROTECTED
	| INTERNAL
	| PRIVATE
	| READONLY
	| VOLATILE
	| VIRTUAL
	| SEALED
	| OVERRIDE
	| ABSTRACT
	| STATIC
	| UNSAFE
	| EXTERN
	| PARTIAL
	| ASYNC  // C# 5
	;
fm_all_member_modifierPlus: FM_PLACEHOLDER | (FM_IF (fm_all_member_modifierPlus | all_member_modifier)* (FM_ELSE_IF (fm_all_member_modifierPlus | all_member_modifier)*)* (FM_ELSE (fm_all_member_modifierPlus | all_member_modifier)*)? FM_IF_CLOSE | FM_LIST (fm_all_member_modifierPlus | all_member_modifier)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_all_member_modifierPlus | all_member_modifier)* (FM_ELSE_IF (fm_all_member_modifierPlus | all_member_modifier)*)* FM_ELSE (fm_all_member_modifierPlus | all_member_modifier)* FM_IF_CLOSE | FM_LIST (fm_all_member_modifierPlus | all_member_modifier)* FM_ELSE (fm_all_member_modifierPlus | all_member_modifier)* FM_LIST_CLOSE) (FM_IF (fm_all_member_modifierPlus | all_member_modifier)* (FM_ELSE_IF (fm_all_member_modifierPlus | all_member_modifier)*)* (FM_ELSE (fm_all_member_modifierPlus | all_member_modifier)*)? FM_IF_CLOSE | FM_LIST (fm_all_member_modifierPlus | all_member_modifier)* FM_LIST_CLOSE)*;

// represents the intersection of struct_member_declaration and class_member_declaration
common_member_declaration
	: constant_declaration
	| typed_member_declaration
	| event_declaration
	| (fm_conversion_operator_declarator | conversion_operator_declarator) (body | (fm_right_arrow | right_arrow) (fm_throwable_expression | throwable_expression) FM_ImplicitToken43 ) // C# 6
	| constructor_declaration
	| VOID (fm_method_declaration | method_declaration)
	| class_definition
	| struct_definition
	| interface_definition
	| enum_definition
	| delegate_definition
	;

typed_member_declaration
	: (REF | READONLY REF | REF READONLY)? (fm_type_ | type_)
	  ( (fm_namespace_or_type_name | namespace_or_type_name) FM_ImplicitToken1  (fm_indexer_declaration | indexer_declaration)
	  | method_declaration
	  | property_declaration
	  | indexer_declaration
	  | operator_declaration
	  | field_declaration
	  )
	;

constant_declarators
	: (fm_constant_declarator | constant_declarator) (FM_ImplicitToken5   (fm_constant_declarator | constant_declarator))*
	;
fm_constant_declarators: FM_PLACEHOLDER | FM_IF (fm_constant_declarators | constant_declarators) (FM_ELSE_IF (fm_constant_declarators | constant_declarators))* FM_ELSE (fm_constant_declarators | constant_declarators) FM_IF_CLOSE;

constant_declarator
	: (fm_identifier | identifier) FM_ImplicitToken11  (fm_expression | expression)
	;
fm_constant_declarator: FM_PLACEHOLDER | FM_IF (fm_constant_declarator | constant_declarator) (FM_ELSE_IF (fm_constant_declarator | constant_declarator))* FM_ELSE (fm_constant_declarator | constant_declarator) FM_IF_CLOSE;

variable_declarators
	: (fm_variable_declarator | variable_declarator) (FM_ImplicitToken5   (fm_variable_declarator | variable_declarator))*
	;
fm_variable_declarators: FM_PLACEHOLDER | FM_IF (fm_variable_declarators | variable_declarators) (FM_ELSE_IF (fm_variable_declarators | variable_declarators))* FM_ELSE (fm_variable_declarators | variable_declarators) FM_IF_CLOSE;

variable_declarator
	: (fm_identifier | identifier) (FM_ImplicitToken11  (fm_variable_initializer | variable_initializer))?
	;
fm_variable_declarator: FM_PLACEHOLDER | FM_IF (fm_variable_declarator | variable_declarator) (FM_ELSE_IF (fm_variable_declarator | variable_declarator))* FM_ELSE (fm_variable_declarator | variable_declarator) FM_IF_CLOSE;

variable_initializer
	: expression
	| array_initializer
	;
fm_variable_initializer: FM_PLACEHOLDER | FM_IF (fm_variable_initializer | variable_initializer) (FM_ELSE_IF (fm_variable_initializer | variable_initializer))* FM_ELSE (fm_variable_initializer | variable_initializer) FM_IF_CLOSE;

return_type
	: type_
	| VOID
	;
fm_return_type: FM_PLACEHOLDER | FM_IF (fm_return_type | return_type) (FM_ELSE_IF (fm_return_type | return_type))* FM_ELSE (fm_return_type | return_type) FM_IF_CLOSE;

member_name
	: namespace_or_type_name
	;
fm_member_name: FM_PLACEHOLDER | FM_IF (fm_member_name | member_name) (FM_ELSE_IF (fm_member_name | member_name))* FM_ELSE (fm_member_name | member_name) FM_IF_CLOSE;

method_body
	: block
	| FM_ImplicitToken43 
	;

formal_parameter_list
	: parameter_array
	| (fm_fixed_parameters | fixed_parameters) (FM_ImplicitToken5  (fm_parameter_array | parameter_array))?
	;
fm_formal_parameter_list: FM_PLACEHOLDER | FM_IF (fm_formal_parameter_list | formal_parameter_list) (FM_ELSE_IF (fm_formal_parameter_list | formal_parameter_list))* FM_ELSE (fm_formal_parameter_list | formal_parameter_list) FM_IF_CLOSE;
fm_formal_parameter_listOpt: FM_PLACEHOLDER | FM_IF (fm_formal_parameter_listOpt | formal_parameter_list)? (FM_ELSE_IF (fm_formal_parameter_listOpt | formal_parameter_list)?)* (FM_ELSE (fm_formal_parameter_listOpt | formal_parameter_list)?)? FM_IF_CLOSE;

fixed_parameters
	: (fm_fixed_parameter | fixed_parameter) ( FM_ImplicitToken5  (fm_fixed_parameter | fixed_parameter) )*
	;
fm_fixed_parameters: FM_PLACEHOLDER | FM_IF (fm_fixed_parameters | fixed_parameters) (FM_ELSE_IF (fm_fixed_parameters | fixed_parameters))* FM_ELSE (fm_fixed_parameters | fixed_parameters) FM_IF_CLOSE;

fixed_parameter
	: (fm_attributesOpt | attributes)? (fm_parameter_modifierOpt | parameter_modifier)? (fm_arg_declaration | arg_declaration)
	| ARGLIST
	;
fm_fixed_parameter: FM_PLACEHOLDER | FM_IF (fm_fixed_parameter | fixed_parameter) (FM_ELSE_IF (fm_fixed_parameter | fixed_parameter))* FM_ELSE (fm_fixed_parameter | fixed_parameter) FM_IF_CLOSE;

parameter_modifier
	: REF
	| OUT
	| IN
	| REF THIS
	| IN THIS
	| THIS
	;
fm_parameter_modifierOpt: FM_PLACEHOLDER | FM_IF (fm_parameter_modifierOpt | parameter_modifier)? (FM_ELSE_IF (fm_parameter_modifierOpt | parameter_modifier)?)* (FM_ELSE (fm_parameter_modifierOpt | parameter_modifier)?)? FM_IF_CLOSE;

parameter_array
	: (fm_attributesOpt | attributes)? PARAMS (fm_array_type | array_type) (fm_identifier | identifier)
	;
fm_parameter_array: FM_PLACEHOLDER | FM_IF (fm_parameter_array | parameter_array) (FM_ELSE_IF (fm_parameter_array | parameter_array))* FM_ELSE (fm_parameter_array | parameter_array) FM_IF_CLOSE;

accessor_declarations
	: (fm_attributesOpt | attributes)? (fm_accessor_modifierOpt | accessor_modifier)?
	  (GET (fm_accessor_body | accessor_body) (fm_set_accessor_declarationOpt | set_accessor_declaration)? | SET (fm_accessor_body | accessor_body) (fm_get_accessor_declarationOpt | get_accessor_declaration)?)
	;
fm_accessor_declarations: FM_PLACEHOLDER | FM_IF (fm_accessor_declarations | accessor_declarations) (FM_ELSE_IF (fm_accessor_declarations | accessor_declarations))* FM_ELSE (fm_accessor_declarations | accessor_declarations) FM_IF_CLOSE;

get_accessor_declaration
	: (fm_attributesOpt | attributes)? (fm_accessor_modifierOpt | accessor_modifier)? GET (fm_accessor_body | accessor_body)
	;
fm_get_accessor_declarationOpt: FM_PLACEHOLDER | FM_IF (fm_get_accessor_declarationOpt | get_accessor_declaration)? (FM_ELSE_IF (fm_get_accessor_declarationOpt | get_accessor_declaration)?)* (FM_ELSE (fm_get_accessor_declarationOpt | get_accessor_declaration)?)? FM_IF_CLOSE;

set_accessor_declaration
	: (fm_attributesOpt | attributes)? (fm_accessor_modifierOpt | accessor_modifier)? SET (fm_accessor_body | accessor_body)
	;
fm_set_accessor_declarationOpt: FM_PLACEHOLDER | FM_IF (fm_set_accessor_declarationOpt | set_accessor_declaration)? (FM_ELSE_IF (fm_set_accessor_declarationOpt | set_accessor_declaration)?)* (FM_ELSE (fm_set_accessor_declarationOpt | set_accessor_declaration)?)? FM_IF_CLOSE;

accessor_modifier
	: PROTECTED
	| INTERNAL
	| PRIVATE
	| PROTECTED INTERNAL
	| INTERNAL PROTECTED
	;
fm_accessor_modifierOpt: FM_PLACEHOLDER | FM_IF (fm_accessor_modifierOpt | accessor_modifier)? (FM_ELSE_IF (fm_accessor_modifierOpt | accessor_modifier)?)* (FM_ELSE (fm_accessor_modifierOpt | accessor_modifier)?)? FM_IF_CLOSE;

accessor_body
	: block
	| FM_ImplicitToken43 
	;
fm_accessor_body: FM_PLACEHOLDER | FM_IF (fm_accessor_body | accessor_body) (FM_ELSE_IF (fm_accessor_body | accessor_body))* FM_ELSE (fm_accessor_body | accessor_body) FM_IF_CLOSE;

event_accessor_declarations
	: (fm_attributesOpt | attributes)? (ADD (fm_block | block) (fm_remove_accessor_declaration | remove_accessor_declaration) | REMOVE (fm_block | block) (fm_add_accessor_declaration | add_accessor_declaration))
	;
fm_event_accessor_declarations: FM_PLACEHOLDER | FM_IF (fm_event_accessor_declarations | event_accessor_declarations) (FM_ELSE_IF (fm_event_accessor_declarations | event_accessor_declarations))* FM_ELSE (fm_event_accessor_declarations | event_accessor_declarations) FM_IF_CLOSE;

add_accessor_declaration
	: (fm_attributesOpt | attributes)? ADD (fm_block | block)
	;
fm_add_accessor_declaration: FM_PLACEHOLDER | FM_IF (fm_add_accessor_declaration | add_accessor_declaration) (FM_ELSE_IF (fm_add_accessor_declaration | add_accessor_declaration))* FM_ELSE (fm_add_accessor_declaration | add_accessor_declaration) FM_IF_CLOSE;

remove_accessor_declaration
	: (fm_attributesOpt | attributes)? REMOVE (fm_block | block)
	;
fm_remove_accessor_declaration: FM_PLACEHOLDER | FM_IF (fm_remove_accessor_declaration | remove_accessor_declaration) (FM_ELSE_IF (fm_remove_accessor_declaration | remove_accessor_declaration))* FM_ELSE (fm_remove_accessor_declaration | remove_accessor_declaration) FM_IF_CLOSE;

overloadable_operator
	: FM_ImplicitToken28 
	| FM_ImplicitToken29 
	| BANG
	| FM_ImplicitToken35 
	| FM_ImplicitToken36 
	| FM_ImplicitToken37 
	| TRUE
	| FALSE
	| FM_ImplicitToken3 
	| FM_ImplicitToken30 
	| FM_ImplicitToken31 
	| FM_ImplicitToken24 
	| FM_ImplicitToken22 
	| FM_ImplicitToken23 
	| FM_ImplicitToken27 
	| right_shift
	| OP_EQ
	| OP_NE
	| FM_ImplicitToken8 
	| FM_ImplicitToken7 
	| FM_ImplicitToken26 
	| FM_ImplicitToken25 
	;
fm_overloadable_operator: FM_PLACEHOLDER | FM_IF (fm_overloadable_operator | overloadable_operator) (FM_ELSE_IF (fm_overloadable_operator | overloadable_operator))* FM_ELSE (fm_overloadable_operator | overloadable_operator) FM_IF_CLOSE;

conversion_operator_declarator
	: (IMPLICIT | EXPLICIT) OPERATOR (fm_type_ | type_) OPEN_PARENS (fm_arg_declaration | arg_declaration) CLOSE_PARENS
	;
fm_conversion_operator_declarator: FM_PLACEHOLDER | FM_IF (fm_conversion_operator_declarator | conversion_operator_declarator) (FM_ELSE_IF (fm_conversion_operator_declarator | conversion_operator_declarator))* FM_ELSE (fm_conversion_operator_declarator | conversion_operator_declarator) FM_IF_CLOSE;

constructor_initializer
	: FM_ImplicitToken9  (BASE | THIS) OPEN_PARENS (fm_argument_listOpt | argument_list)? CLOSE_PARENS
	;
fm_constructor_initializerOpt: FM_PLACEHOLDER | FM_IF (fm_constructor_initializerOpt | constructor_initializer)? (FM_ELSE_IF (fm_constructor_initializerOpt | constructor_initializer)?)* (FM_ELSE (fm_constructor_initializerOpt | constructor_initializer)?)? FM_IF_CLOSE;

body
	: block
	| FM_ImplicitToken43 
	;
fm_body: FM_PLACEHOLDER | FM_IF (fm_body | body) (FM_ELSE_IF (fm_body | body))* FM_ELSE (fm_body | body) FM_IF_CLOSE;

//B.2.8 Structs
struct_interfaces
	: FM_ImplicitToken9  (fm_interface_type_list | interface_type_list)
	;
fm_struct_interfacesOpt: FM_PLACEHOLDER | FM_IF (fm_struct_interfacesOpt | struct_interfaces)? (FM_ELSE_IF (fm_struct_interfacesOpt | struct_interfaces)?)* (FM_ELSE (fm_struct_interfacesOpt | struct_interfaces)?)? FM_IF_CLOSE;

struct_body
	: OPEN_BRACE (fm_struct_member_declarationStar | struct_member_declaration)* CLOSE_BRACE
	;
fm_struct_body: FM_PLACEHOLDER | FM_IF (fm_struct_body | struct_body) (FM_ELSE_IF (fm_struct_body | struct_body))* FM_ELSE (fm_struct_body | struct_body) FM_IF_CLOSE;

struct_member_declaration
	: (fm_attributesOpt | attributes)? (fm_all_member_modifiersOpt | all_member_modifiers)?
	  (common_member_declaration | FIXED (fm_type_ | type_) (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)+ FM_ImplicitToken43 )
	;
fm_struct_member_declarationStar: FM_PLACEHOLDER | FM_IF (fm_struct_member_declarationStar | struct_member_declaration)* (FM_ELSE_IF (fm_struct_member_declarationStar | struct_member_declaration)*)* (FM_ELSE (fm_struct_member_declarationStar | struct_member_declaration)*)? FM_IF_CLOSE | FM_LIST (fm_struct_member_declarationStar | struct_member_declaration)* FM_LIST_CLOSE;

//B.2.9 Arrays
array_type
	: (fm_base_type | base_type) ((FM_ImplicitToken3  | FM_ImplicitToken2 )* (fm_rank_specifier | rank_specifier))+
	;
fm_array_type: FM_PLACEHOLDER | FM_IF (fm_array_type | array_type) (FM_ELSE_IF (fm_array_type | array_type))* FM_ELSE (fm_array_type | array_type) FM_IF_CLOSE;

rank_specifier
	: FM_ImplicitToken40  FM_ImplicitToken5 * FM_ImplicitToken41 
	;
fm_rank_specifier: FM_PLACEHOLDER | FM_IF (fm_rank_specifier | rank_specifier) (FM_ELSE_IF (fm_rank_specifier | rank_specifier))* FM_ELSE (fm_rank_specifier | rank_specifier) FM_IF_CLOSE;
fm_rank_specifierStar: FM_PLACEHOLDER | FM_IF (fm_rank_specifierStar | rank_specifier)* (FM_ELSE_IF (fm_rank_specifierStar | rank_specifier)*)* (FM_ELSE (fm_rank_specifierStar | rank_specifier)*)? FM_IF_CLOSE | FM_LIST (fm_rank_specifierStar | rank_specifier)* FM_LIST_CLOSE;
fm_rank_specifierPlus: FM_PLACEHOLDER | (FM_IF (fm_rank_specifierPlus | rank_specifier)* (FM_ELSE_IF (fm_rank_specifierPlus | rank_specifier)*)* (FM_ELSE (fm_rank_specifierPlus | rank_specifier)*)? FM_IF_CLOSE | FM_LIST (fm_rank_specifierPlus | rank_specifier)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_rank_specifierPlus | rank_specifier)* (FM_ELSE_IF (fm_rank_specifierPlus | rank_specifier)*)* FM_ELSE (fm_rank_specifierPlus | rank_specifier)* FM_IF_CLOSE | FM_LIST (fm_rank_specifierPlus | rank_specifier)* FM_ELSE (fm_rank_specifierPlus | rank_specifier)* FM_LIST_CLOSE) (FM_IF (fm_rank_specifierPlus | rank_specifier)* (FM_ELSE_IF (fm_rank_specifierPlus | rank_specifier)*)* (FM_ELSE (fm_rank_specifierPlus | rank_specifier)*)? FM_IF_CLOSE | FM_LIST (fm_rank_specifierPlus | rank_specifier)* FM_LIST_CLOSE)*;

array_initializer
	: OPEN_BRACE ((fm_variable_initializer | variable_initializer) (FM_ImplicitToken5   (fm_variable_initializer | variable_initializer))* FM_ImplicitToken5 ?)? CLOSE_BRACE
	;
fm_array_initializer: FM_PLACEHOLDER | FM_IF (fm_array_initializer | array_initializer) (FM_ELSE_IF (fm_array_initializer | array_initializer))* FM_ELSE (fm_array_initializer | array_initializer) FM_IF_CLOSE;
fm_array_initializerOpt: FM_PLACEHOLDER | FM_IF (fm_array_initializerOpt | array_initializer)? (FM_ELSE_IF (fm_array_initializerOpt | array_initializer)?)* (FM_ELSE (fm_array_initializerOpt | array_initializer)?)? FM_IF_CLOSE;

//B.2.10 Interfaces
variant_type_parameter_list
	: FM_ImplicitToken7  (fm_variant_type_parameter | variant_type_parameter) (FM_ImplicitToken5  (fm_variant_type_parameter | variant_type_parameter))* FM_ImplicitToken8 
	;
fm_variant_type_parameter_listOpt: FM_PLACEHOLDER | FM_IF (fm_variant_type_parameter_listOpt | variant_type_parameter_list)? (FM_ELSE_IF (fm_variant_type_parameter_listOpt | variant_type_parameter_list)?)* (FM_ELSE (fm_variant_type_parameter_listOpt | variant_type_parameter_list)?)? FM_IF_CLOSE;

variant_type_parameter
	: (fm_attributesOpt | attributes)? (fm_variance_annotationOpt | variance_annotation)? (fm_identifier | identifier)
	;
fm_variant_type_parameter: FM_PLACEHOLDER | FM_IF (fm_variant_type_parameter | variant_type_parameter) (FM_ELSE_IF (fm_variant_type_parameter | variant_type_parameter))* FM_ELSE (fm_variant_type_parameter | variant_type_parameter) FM_IF_CLOSE;

variance_annotation
	: IN | OUT
	;
fm_variance_annotationOpt: FM_PLACEHOLDER | FM_IF (fm_variance_annotationOpt | variance_annotation)? (FM_ELSE_IF (fm_variance_annotationOpt | variance_annotation)?)* (FM_ELSE (fm_variance_annotationOpt | variance_annotation)?)? FM_IF_CLOSE;

interface_base
	: FM_ImplicitToken9  (fm_interface_type_list | interface_type_list)
	;
fm_interface_baseOpt: FM_PLACEHOLDER | FM_IF (fm_interface_baseOpt | interface_base)? (FM_ELSE_IF (fm_interface_baseOpt | interface_base)?)* (FM_ELSE (fm_interface_baseOpt | interface_base)?)? FM_IF_CLOSE;

interface_body // ignored in csharp 8
	: OPEN_BRACE (fm_interface_member_declarationStar | interface_member_declaration)* CLOSE_BRACE
	;

interface_member_declaration
	: (fm_attributesOpt | attributes)? NEW?
	  (UNSAFE? (REF | REF READONLY | READONLY REF)? (fm_type_ | type_)
	    ( (fm_identifier | identifier) (fm_type_parameter_listOpt | type_parameter_list)? OPEN_PARENS (fm_formal_parameter_listOpt | formal_parameter_list)? CLOSE_PARENS (fm_type_parameter_constraints_clausesOpt | type_parameter_constraints_clauses)? FM_ImplicitToken43 
	    | (fm_identifier | identifier) OPEN_BRACE (fm_interface_accessors | interface_accessors) CLOSE_BRACE
	    | THIS FM_ImplicitToken40  (fm_formal_parameter_list | formal_parameter_list) FM_ImplicitToken41  OPEN_BRACE (fm_interface_accessors | interface_accessors) CLOSE_BRACE)
	  | UNSAFE? VOID (fm_identifier | identifier) (fm_type_parameter_listOpt | type_parameter_list)? OPEN_PARENS (fm_formal_parameter_listOpt | formal_parameter_list)? CLOSE_PARENS (fm_type_parameter_constraints_clausesOpt | type_parameter_constraints_clauses)? FM_ImplicitToken43 
	  | EVENT (fm_type_ | type_) (fm_identifier | identifier) FM_ImplicitToken43 )
	;
fm_interface_member_declarationStar: FM_PLACEHOLDER | FM_IF (fm_interface_member_declarationStar | interface_member_declaration)* (FM_ELSE_IF (fm_interface_member_declarationStar | interface_member_declaration)*)* (FM_ELSE (fm_interface_member_declarationStar | interface_member_declaration)*)? FM_IF_CLOSE | FM_LIST (fm_interface_member_declarationStar | interface_member_declaration)* FM_LIST_CLOSE;

interface_accessors
	: (fm_attributesOpt | attributes)? (GET FM_ImplicitToken43  ((fm_attributesOpt | attributes)? SET FM_ImplicitToken43 )? | SET FM_ImplicitToken43  ((fm_attributesOpt | attributes)? GET FM_ImplicitToken43 )?)
	;
fm_interface_accessors: FM_PLACEHOLDER | FM_IF (fm_interface_accessors | interface_accessors) (FM_ELSE_IF (fm_interface_accessors | interface_accessors))* FM_ELSE (fm_interface_accessors | interface_accessors) FM_IF_CLOSE;

//B.2.11 Enums
enum_base
	: FM_ImplicitToken9  (fm_type_ | type_)
	;
fm_enum_baseOpt: FM_PLACEHOLDER | FM_IF (fm_enum_baseOpt | enum_base)? (FM_ELSE_IF (fm_enum_baseOpt | enum_base)?)* (FM_ELSE (fm_enum_baseOpt | enum_base)?)? FM_IF_CLOSE;

enum_body
	: OPEN_BRACE ((fm_enum_member_declaration | enum_member_declaration) (FM_ImplicitToken5   (fm_enum_member_declaration | enum_member_declaration))* FM_ImplicitToken5 ?)? CLOSE_BRACE
	;
fm_enum_body: FM_PLACEHOLDER | FM_IF (fm_enum_body | enum_body) (FM_ELSE_IF (fm_enum_body | enum_body))* FM_ELSE (fm_enum_body | enum_body) FM_IF_CLOSE;

enum_member_declaration
	: (fm_attributesOpt | attributes)? (fm_identifier | identifier) (FM_ImplicitToken11  (fm_expression | expression))?
	;
fm_enum_member_declaration: FM_PLACEHOLDER | FM_IF (fm_enum_member_declaration | enum_member_declaration) (FM_ELSE_IF (fm_enum_member_declaration | enum_member_declaration))* FM_ELSE (fm_enum_member_declaration | enum_member_declaration) FM_IF_CLOSE;

//B.2.12 Delegates

//B.2.13 Attributes
global_attribute_section
	: FM_ImplicitToken40  (fm_global_attribute_target | global_attribute_target) FM_ImplicitToken9  (fm_attribute_list | attribute_list) FM_ImplicitToken5 ? FM_ImplicitToken41 
	;
fm_global_attribute_sectionStar: FM_PLACEHOLDER | FM_IF (fm_global_attribute_sectionStar | global_attribute_section)* (FM_ELSE_IF (fm_global_attribute_sectionStar | global_attribute_section)*)* (FM_ELSE (fm_global_attribute_sectionStar | global_attribute_section)*)? FM_IF_CLOSE | FM_LIST (fm_global_attribute_sectionStar | global_attribute_section)* FM_LIST_CLOSE;

global_attribute_target
	: keyword
	| identifier
	;
fm_global_attribute_target: FM_PLACEHOLDER | FM_IF (fm_global_attribute_target | global_attribute_target) (FM_ELSE_IF (fm_global_attribute_target | global_attribute_target))* FM_ELSE (fm_global_attribute_target | global_attribute_target) FM_IF_CLOSE;

attributes
	: (fm_attribute_sectionPlus | attribute_section)+
	;
fm_attributesOpt: FM_PLACEHOLDER | FM_IF (fm_attributesOpt | attributes)? (FM_ELSE_IF (fm_attributesOpt | attributes)?)* (FM_ELSE (fm_attributesOpt | attributes)?)? FM_IF_CLOSE;

attribute_section
	: FM_ImplicitToken40  ((fm_attribute_target | attribute_target) FM_ImplicitToken9 )? (fm_attribute_list | attribute_list) FM_ImplicitToken5 ? FM_ImplicitToken41 
	;
fm_attribute_sectionPlus: FM_PLACEHOLDER | (FM_IF (fm_attribute_sectionPlus | attribute_section)* (FM_ELSE_IF (fm_attribute_sectionPlus | attribute_section)*)* (FM_ELSE (fm_attribute_sectionPlus | attribute_section)*)? FM_IF_CLOSE | FM_LIST (fm_attribute_sectionPlus | attribute_section)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_attribute_sectionPlus | attribute_section)* (FM_ELSE_IF (fm_attribute_sectionPlus | attribute_section)*)* FM_ELSE (fm_attribute_sectionPlus | attribute_section)* FM_IF_CLOSE | FM_LIST (fm_attribute_sectionPlus | attribute_section)* FM_ELSE (fm_attribute_sectionPlus | attribute_section)* FM_LIST_CLOSE) (FM_IF (fm_attribute_sectionPlus | attribute_section)* (FM_ELSE_IF (fm_attribute_sectionPlus | attribute_section)*)* (FM_ELSE (fm_attribute_sectionPlus | attribute_section)*)? FM_IF_CLOSE | FM_LIST (fm_attribute_sectionPlus | attribute_section)* FM_LIST_CLOSE)*;

attribute_target
	: keyword
	| identifier
	;
fm_attribute_target: FM_PLACEHOLDER | FM_IF (fm_attribute_target | attribute_target) (FM_ELSE_IF (fm_attribute_target | attribute_target))* FM_ELSE (fm_attribute_target | attribute_target) FM_IF_CLOSE;

attribute_list
	: (fm_attribute | attribute) (FM_ImplicitToken5   (fm_attribute | attribute))*
	;
fm_attribute_list: FM_PLACEHOLDER | FM_IF (fm_attribute_list | attribute_list) (FM_ELSE_IF (fm_attribute_list | attribute_list))* FM_ELSE (fm_attribute_list | attribute_list) FM_IF_CLOSE;

attribute
	: (fm_namespace_or_type_name | namespace_or_type_name) (OPEN_PARENS ((fm_attribute_argument | attribute_argument) (FM_ImplicitToken5   (fm_attribute_argument | attribute_argument))*)? CLOSE_PARENS)?
	;
fm_attribute: FM_PLACEHOLDER | FM_IF (fm_attribute | attribute) (FM_ELSE_IF (fm_attribute | attribute))* FM_ELSE (fm_attribute | attribute) FM_IF_CLOSE;

attribute_argument
	: ((fm_identifier | identifier) FM_ImplicitToken9 )? (fm_expression | expression)
	;
fm_attribute_argument: FM_PLACEHOLDER | FM_IF (fm_attribute_argument | attribute_argument) (FM_ELSE_IF (fm_attribute_argument | attribute_argument))* FM_ELSE (fm_attribute_argument | attribute_argument) FM_IF_CLOSE;

//B.3 Grammar extensions for unsafe code
pointer_type
	: (simple_type | class_type) (rank_specifier | FM_ImplicitToken2 )* FM_ImplicitToken3 
	| VOID FM_ImplicitToken3 
	;
fm_pointer_type: FM_PLACEHOLDER | FM_IF (fm_pointer_type | pointer_type) (FM_ELSE_IF (fm_pointer_type | pointer_type))* FM_ELSE (fm_pointer_type | pointer_type) FM_IF_CLOSE;

fixed_pointer_declarators
	: (fm_fixed_pointer_declarator | fixed_pointer_declarator) (FM_ImplicitToken5   (fm_fixed_pointer_declarator | fixed_pointer_declarator))*
	;
fm_fixed_pointer_declarators: FM_PLACEHOLDER | FM_IF (fm_fixed_pointer_declarators | fixed_pointer_declarators) (FM_ELSE_IF (fm_fixed_pointer_declarators | fixed_pointer_declarators))* FM_ELSE (fm_fixed_pointer_declarators | fixed_pointer_declarators) FM_IF_CLOSE;

fixed_pointer_declarator
	: (fm_identifier | identifier) FM_ImplicitToken11  (fm_fixed_pointer_initializer | fixed_pointer_initializer)
	;
fm_fixed_pointer_declarator: FM_PLACEHOLDER | FM_IF (fm_fixed_pointer_declarator | fixed_pointer_declarator) (FM_ELSE_IF (fm_fixed_pointer_declarator | fixed_pointer_declarator))* FM_ELSE (fm_fixed_pointer_declarator | fixed_pointer_declarator) FM_IF_CLOSE;

fixed_pointer_initializer
	: FM_ImplicitToken24 ? (fm_expression | expression)
	| stackalloc_initializer
	;
fm_fixed_pointer_initializer: FM_PLACEHOLDER | FM_IF (fm_fixed_pointer_initializer | fixed_pointer_initializer) (FM_ELSE_IF (fm_fixed_pointer_initializer | fixed_pointer_initializer))* FM_ELSE (fm_fixed_pointer_initializer | fixed_pointer_initializer) FM_IF_CLOSE;

fixed_size_buffer_declarator
	: (fm_identifier | identifier) FM_ImplicitToken40  (fm_expression | expression) FM_ImplicitToken41 
	;
fm_fixed_size_buffer_declaratorPlus: FM_PLACEHOLDER | (FM_IF (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)* (FM_ELSE_IF (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)*)* (FM_ELSE (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)*)? FM_IF_CLOSE | FM_LIST (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)* (FM_ELSE_IF (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)*)* FM_ELSE (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)* FM_IF_CLOSE | FM_LIST (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)* FM_ELSE (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)* FM_LIST_CLOSE) (FM_IF (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)* (FM_ELSE_IF (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)*)* (FM_ELSE (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)*)? FM_IF_CLOSE | FM_LIST (fm_fixed_size_buffer_declaratorPlus | fixed_size_buffer_declarator)* FM_LIST_CLOSE)*;

stackalloc_initializer
	: STACKALLOC (fm_type_ | type_) FM_ImplicitToken40  (fm_expression | expression) FM_ImplicitToken41 
	| STACKALLOC (fm_type_Opt | type_)? FM_ImplicitToken40  (fm_expressionOpt | expression)? FM_ImplicitToken41  OPEN_BRACE (fm_expression | expression) (FM_ImplicitToken5  (fm_expression | expression))* FM_ImplicitToken5 ? CLOSE_BRACE
	;

right_arrow
	: first=FM_ImplicitToken11  second=FM_ImplicitToken8  {$first.index + 1 == $second.index}? // Nothing between the tokens?
	;
fm_right_arrow: FM_PLACEHOLDER | FM_IF (fm_right_arrow | right_arrow) (FM_ELSE_IF (fm_right_arrow | right_arrow))* FM_ELSE (fm_right_arrow | right_arrow) FM_IF_CLOSE;

right_shift
	: first=FM_ImplicitToken8  second=FM_ImplicitToken8  {$first.index + 1 == $second.index}? // Nothing between the tokens?
	;

right_shift_assignment
	: first=FM_ImplicitToken8  second=FM_ImplicitToken26  {$first.index + 1 == $second.index}? // Nothing between the tokens?
	;

literal
	: boolean_literal
	| string_literal
	| INTEGER_LITERAL
	| HEX_INTEGER_LITERAL
	| BIN_INTEGER_LITERAL
	| REAL_LITERAL
	| CHARACTER_LITERAL
	| NULL_
	;

boolean_literal
	: TRUE
	| FALSE
	;

string_literal
	: interpolated_regular_string
	| interpolated_verbatium_string
	| REGULAR_STRING
	| VERBATIUM_STRING
	;

interpolated_regular_string
	: INTERPOLATED_REGULAR_STRING_START (fm_interpolated_regular_string_partStar | interpolated_regular_string_part)* DOUBLE_QUOTE_INSIDE
	;


interpolated_verbatium_string
	: INTERPOLATED_VERBATIUM_STRING_START (fm_interpolated_verbatium_string_partStar | interpolated_verbatium_string_part)* DOUBLE_QUOTE_INSIDE
	;

interpolated_regular_string_part
	: interpolated_string_expression
	| DOUBLE_CURLY_INSIDE
	| REGULAR_CHAR_INSIDE
	| REGULAR_STRING_INSIDE
	;
fm_interpolated_regular_string_partStar: FM_PLACEHOLDER | FM_IF (fm_interpolated_regular_string_partStar | interpolated_regular_string_part)* (FM_ELSE_IF (fm_interpolated_regular_string_partStar | interpolated_regular_string_part)*)* (FM_ELSE (fm_interpolated_regular_string_partStar | interpolated_regular_string_part)*)? FM_IF_CLOSE | FM_LIST (fm_interpolated_regular_string_partStar | interpolated_regular_string_part)* FM_LIST_CLOSE;

interpolated_verbatium_string_part
	: interpolated_string_expression
	| DOUBLE_CURLY_INSIDE
	| VERBATIUM_DOUBLE_QUOTE_INSIDE
	| VERBATIUM_INSIDE_STRING
	;
fm_interpolated_verbatium_string_partStar: FM_PLACEHOLDER | FM_IF (fm_interpolated_verbatium_string_partStar | interpolated_verbatium_string_part)* (FM_ELSE_IF (fm_interpolated_verbatium_string_partStar | interpolated_verbatium_string_part)*)* (FM_ELSE (fm_interpolated_verbatium_string_partStar | interpolated_verbatium_string_part)*)? FM_IF_CLOSE | FM_LIST (fm_interpolated_verbatium_string_partStar | interpolated_verbatium_string_part)* FM_LIST_CLOSE;

interpolated_string_expression
	: (fm_expression | expression) (FM_ImplicitToken5  (fm_expression | expression))* (FM_ImplicitToken9  FORMAT_STRING+)?
	;

//B.1.7 Keywords
keyword
	: ABSTRACT
	| AS
	| BASE
	| BOOL
	| BREAK
	| BYTE
	| CASE
	| CATCH
	| CHAR
	| CHECKED
	| CLASS
	| CONST
	| CONTINUE
	| DECIMAL
	| DEFAULT
	| DELEGATE
	| DO
	| DOUBLE
	| ELSE
	| ENUM
	| EVENT
	| EXPLICIT
	| EXTERN
	| FALSE
	| FINALLY
	| FIXED
	| FLOAT
	| FOR
	| FOREACH
	| GOTO
	| IF
	| IMPLICIT
	| IN
	| INT
	| INTERFACE
	| INTERNAL
	| IS
	| LOCK
	| LONG
	| NAMESPACE
	| NEW
	| NULL_
	| OBJECT
	| OPERATOR
	| OUT
	| OVERRIDE
	| PARAMS
	| PRIVATE
	| PROTECTED
	| PUBLIC
	| READONLY
	| REF
	| RETURN
	| SBYTE
	| SEALED
	| SHORT
	| SIZEOF
	| STACKALLOC
	| STATIC
	| STRING
	| STRUCT
	| SWITCH
	| THIS
	| THROW
	| TRUE
	| TRY
	| TYPEOF
	| UINT
	| ULONG
	| UNCHECKED
	| UNMANAGED
	| UNSAFE
	| USHORT
	| USING
	| VIRTUAL
	| VOID
	| VOLATILE
	| WHILE
	;

// -------------------- extra rules for modularization --------------------------------

class_definition
	: CLASS (fm_identifier | identifier) (fm_type_parameter_listOpt | type_parameter_list)? (fm_class_baseOpt | class_base)? (fm_type_parameter_constraints_clausesOpt | type_parameter_constraints_clauses)?
	    (fm_class_body | class_body) FM_ImplicitToken43 ?
	;

struct_definition
	: (READONLY | REF)? STRUCT (fm_identifier | identifier) (fm_type_parameter_listOpt | type_parameter_list)? (fm_struct_interfacesOpt | struct_interfaces)? (fm_type_parameter_constraints_clausesOpt | type_parameter_constraints_clauses)?
	    (fm_struct_body | struct_body) FM_ImplicitToken43 ?
	;

interface_definition
	: INTERFACE (fm_identifier | identifier) (fm_variant_type_parameter_listOpt | variant_type_parameter_list)? (fm_interface_baseOpt | interface_base)?
	    (fm_type_parameter_constraints_clausesOpt | type_parameter_constraints_clauses)? (fm_class_body | class_body) FM_ImplicitToken43 ?
	;

enum_definition
	: ENUM (fm_identifier | identifier) (fm_enum_baseOpt | enum_base)? (fm_enum_body | enum_body) FM_ImplicitToken43 ?
	;

delegate_definition
	: DELEGATE (fm_return_type | return_type) (fm_identifier | identifier) (fm_variant_type_parameter_listOpt | variant_type_parameter_list)?
	  OPEN_PARENS (fm_formal_parameter_listOpt | formal_parameter_list)? CLOSE_PARENS (fm_type_parameter_constraints_clausesOpt | type_parameter_constraints_clauses)? FM_ImplicitToken43 
	;

event_declaration
	: EVENT (fm_type_ | type_) ((fm_variable_declarators | variable_declarators) FM_ImplicitToken43  | (fm_member_name | member_name) OPEN_BRACE (fm_event_accessor_declarations | event_accessor_declarations) CLOSE_BRACE)
	;

field_declaration
	: (fm_variable_declarators | variable_declarators) FM_ImplicitToken43 
	;

property_declaration // Property initializer & lambda in properties C# 6
	: (fm_member_name | member_name) (OPEN_BRACE (fm_accessor_declarations | accessor_declarations) CLOSE_BRACE (FM_ImplicitToken11  (fm_variable_initializer | variable_initializer) FM_ImplicitToken43 )? | (fm_right_arrow | right_arrow) (fm_throwable_expression | throwable_expression) FM_ImplicitToken43 )
	;

constant_declaration
	: CONST (fm_type_ | type_) (fm_constant_declarators | constant_declarators) FM_ImplicitToken43 
	;

indexer_declaration // lamdas from C# 6
	: THIS FM_ImplicitToken40  (fm_formal_parameter_list | formal_parameter_list) FM_ImplicitToken41  (OPEN_BRACE (fm_accessor_declarations | accessor_declarations) CLOSE_BRACE | (fm_right_arrow | right_arrow) (fm_throwable_expression | throwable_expression) FM_ImplicitToken43 )
	;
fm_indexer_declaration: FM_PLACEHOLDER | FM_IF (fm_indexer_declaration | indexer_declaration) (FM_ELSE_IF (fm_indexer_declaration | indexer_declaration))* FM_ELSE (fm_indexer_declaration | indexer_declaration) FM_IF_CLOSE;

destructor_definition
	: FM_ImplicitToken35  (fm_identifier | identifier) OPEN_PARENS CLOSE_PARENS (fm_body | body)
	;

constructor_declaration
	: (fm_identifier | identifier) OPEN_PARENS (fm_formal_parameter_listOpt | formal_parameter_list)? CLOSE_PARENS (fm_constructor_initializerOpt | constructor_initializer)? (fm_body | body)
	;

method_declaration // lamdas from C# 6
	: (fm_method_member_name | method_member_name) (fm_type_parameter_listOpt | type_parameter_list)? OPEN_PARENS (fm_formal_parameter_listOpt | formal_parameter_list)? CLOSE_PARENS
	    (fm_type_parameter_constraints_clausesOpt | type_parameter_constraints_clauses)? (method_body | (fm_right_arrow | right_arrow) (fm_throwable_expression | throwable_expression) FM_ImplicitToken43 )
	;
fm_method_declaration: FM_PLACEHOLDER | FM_IF (fm_method_declaration | method_declaration) (FM_ELSE_IF (fm_method_declaration | method_declaration))* FM_ELSE (fm_method_declaration | method_declaration) FM_IF_CLOSE;

method_member_name
	: (identifier | (fm_identifier | identifier) FM_ImplicitToken42  (fm_identifier | identifier)) ((fm_type_argument_listOpt | type_argument_list)? FM_ImplicitToken1  (fm_identifier | identifier))*
	;
fm_method_member_name: FM_PLACEHOLDER | FM_IF (fm_method_member_name | method_member_name) (FM_ELSE_IF (fm_method_member_name | method_member_name))* FM_ELSE (fm_method_member_name | method_member_name) FM_IF_CLOSE;

operator_declaration // lamdas form C# 6
	: OPERATOR (fm_overloadable_operator | overloadable_operator) OPEN_PARENS IN? (fm_arg_declaration | arg_declaration)
	       (FM_ImplicitToken5  IN? (fm_arg_declaration | arg_declaration))? CLOSE_PARENS (body | (fm_right_arrow | right_arrow) (fm_throwable_expression | throwable_expression) FM_ImplicitToken43 )
	;

arg_declaration
	: (fm_type_ | type_) (fm_identifier | identifier) (FM_ImplicitToken11  (fm_expression | expression))?
	;
fm_arg_declaration: FM_PLACEHOLDER | FM_IF (fm_arg_declaration | arg_declaration) (FM_ELSE_IF (fm_arg_declaration | arg_declaration))* FM_ELSE (fm_arg_declaration | arg_declaration) FM_IF_CLOSE;

method_invocation
	: OPEN_PARENS (fm_argument_listOpt | argument_list)? CLOSE_PARENS
	;

object_creation_expression
	: OPEN_PARENS (fm_argument_listOpt | argument_list)? CLOSE_PARENS (fm_object_or_collection_initializerOpt | object_or_collection_initializer)?
	;

identifier
	: IDENTIFIER
	| ADD
	| ALIAS
	| ARGLIST
	| ASCENDING
	| ASYNC
	| AWAIT
	| BY
	| DESCENDING
	| DYNAMIC
	| EQUALS
	| FROM
	| GET
	| GROUP
	| INTO
	| JOIN
	| LET
	| NAMEOF
	| ON
	| ORDERBY
	| PARTIAL
	| REMOVE
	| SELECT
	| SET
	| UNMANAGED
	| VAR
	| WHEN
	| WHERE
	| YIELD
	;
fm_identifier: FM_PLACEHOLDER | FM_IF (fm_identifier | identifier) (FM_ELSE_IF (fm_identifier | identifier))* FM_ELSE (fm_identifier | identifier) FM_IF_CLOSE;
fm_identifierOpt: FM_PLACEHOLDER | FM_IF (fm_identifierOpt | identifier)? (FM_ELSE_IF (fm_identifierOpt | identifier)?)* (FM_ELSE (fm_identifierOpt | identifier)?)? FM_IF_CLOSE;
