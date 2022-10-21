/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Alexander Belov
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 *  A grammar for Ruby-like language written in ANTLR v4.
 *  You can find compiler into Parrot VM intermediate representation language
 *  (PIR) here: https://github.com/AlexBelov/corundum
 */

grammar CorundumTemplate;

prog : (fm_expression_list | expression_list) EOF;

expression_list : (fm_expression | expression) (fm_terminator | terminator)
                | fm_expression_list ( fm_expression | expression ) ( fm_terminator | terminator ) 
  | expression_list (fm_expression | expression) (fm_terminator | terminator)
                | terminator
                ;
fm_expression_list: FM_PLACEHOLDER | FM_IF (fm_expression_list | expression_list) (FM_ELSE_IF (fm_expression_list | expression_list))* FM_ELSE (fm_expression_list | expression_list) FM_IF_CLOSE;

expression : function_definition
           | function_inline_call
           | require_block
           | if_statement
           | unless_statement
           | rvalue
           | return_statement
           | while_statement
           | for_statement
           | pir_inline
           ;
fm_expression: FM_PLACEHOLDER | FM_IF (fm_expression | expression) (FM_ELSE_IF (fm_expression | expression))* FM_ELSE (fm_expression | expression) FM_IF_CLOSE;

global_get : (fm_lvalue | lvalue) ASSIGN (fm_id_global | id_global);

global_set : (fm_id_global | id_global) ASSIGN (fm_all_result | all_result);

global_result : id_global;

function_inline_call : function_call;

require_block : REQUIRE (fm_literal_t | literal_t);

pir_inline : PIR (fm_crlf | crlf) (fm_pir_expression_list | pir_expression_list) END;

pir_expression_list : expression_list;
fm_pir_expression_list: FM_PLACEHOLDER | FM_IF (fm_pir_expression_list | pir_expression_list) (FM_ELSE_IF (fm_pir_expression_list | pir_expression_list))* FM_ELSE (fm_pir_expression_list | pir_expression_list) FM_IF_CLOSE;

function_definition : (fm_function_definition_header | function_definition_header) (fm_function_definition_body | function_definition_body) END;

function_definition_body : expression_list;
fm_function_definition_body: FM_PLACEHOLDER | FM_IF (fm_function_definition_body | function_definition_body) (FM_ELSE_IF (fm_function_definition_body | function_definition_body))* FM_ELSE (fm_function_definition_body | function_definition_body) FM_IF_CLOSE;

function_definition_header : DEF (fm_function_name | function_name) (fm_crlf | crlf)
                           | DEF (fm_function_name | function_name) (fm_function_definition_params | function_definition_params) (fm_crlf | crlf)
                           ;
fm_function_definition_header: FM_PLACEHOLDER | FM_IF (fm_function_definition_header | function_definition_header) (FM_ELSE_IF (fm_function_definition_header | function_definition_header))* FM_ELSE (fm_function_definition_header | function_definition_header) FM_IF_CLOSE;

function_name : id_function
              | id_
              ;
fm_function_name: FM_PLACEHOLDER | FM_IF (fm_function_name | function_name) (FM_ELSE_IF (fm_function_name | function_name))* FM_ELSE (fm_function_name | function_name) FM_IF_CLOSE;

function_definition_params : LEFT_RBRACKET RIGHT_RBRACKET
                           | LEFT_RBRACKET (fm_function_definition_params_list | function_definition_params_list) RIGHT_RBRACKET
                           | function_definition_params_list
                           ;
fm_function_definition_params: FM_PLACEHOLDER | FM_IF (fm_function_definition_params | function_definition_params) (FM_ELSE_IF (fm_function_definition_params | function_definition_params))* FM_ELSE (fm_function_definition_params | function_definition_params) FM_IF_CLOSE;

function_definition_params_list : function_definition_param_id
                                | fm_function_definition_params_list COMMA ( fm_function_definition_param_id | function_definition_param_id ) 
  | function_definition_params_list COMMA (fm_function_definition_param_id | function_definition_param_id)
                                ;
fm_function_definition_params_list: FM_PLACEHOLDER | FM_IF (fm_function_definition_params_list | function_definition_params_list) (FM_ELSE_IF (fm_function_definition_params_list | function_definition_params_list))* FM_ELSE (fm_function_definition_params_list | function_definition_params_list) FM_IF_CLOSE;

function_definition_param_id : id_;
fm_function_definition_param_id: FM_PLACEHOLDER | FM_IF (fm_function_definition_param_id | function_definition_param_id) (FM_ELSE_IF (fm_function_definition_param_id | function_definition_param_id))* FM_ELSE (fm_function_definition_param_id | function_definition_param_id) FM_IF_CLOSE;

return_statement : RETURN (fm_all_result | all_result);

function_call : (fm_function_name | function_name) LEFT_RBRACKET (fm_function_call_param_list | function_call_param_list) RIGHT_RBRACKET
              | (fm_function_name | function_name) (fm_function_call_param_list | function_call_param_list)
              | (fm_function_name | function_name) LEFT_RBRACKET RIGHT_RBRACKET
              ;

function_call_param_list : function_call_params;
fm_function_call_param_list: FM_PLACEHOLDER | FM_IF (fm_function_call_param_list | function_call_param_list) (FM_ELSE_IF (fm_function_call_param_list | function_call_param_list))* FM_ELSE (fm_function_call_param_list | function_call_param_list) FM_IF_CLOSE;

function_call_params : function_param
                     | fm_function_call_params COMMA ( fm_function_param | function_param ) 
  | function_call_params COMMA (fm_function_param | function_param)
                     ;
fm_function_call_params: FM_PLACEHOLDER | FM_IF (fm_function_call_params | function_call_params) (FM_ELSE_IF (fm_function_call_params | function_call_params))* FM_ELSE (fm_function_call_params | function_call_params) FM_IF_CLOSE;

function_param : ( function_unnamed_param | function_named_param );
fm_function_param: FM_PLACEHOLDER | FM_IF (fm_function_param | function_param) (FM_ELSE_IF (fm_function_param | function_param))* FM_ELSE (fm_function_param | function_param) FM_IF_CLOSE;

function_unnamed_param : ( int_result | float_result | string_result | dynamic_result );

function_named_param : (fm_id_ | id_) ASSIGN ( int_result | float_result | string_result | dynamic_result );

function_call_assignment : function_call;

all_result : ( int_result | float_result | string_result | dynamic_result | global_result );
fm_all_result: FM_PLACEHOLDER | FM_IF (fm_all_result | all_result) (FM_ELSE_IF (fm_all_result | all_result))* FM_ELSE (fm_all_result | all_result) FM_IF_CLOSE;

elsif_statement : if_elsif_statement;
fm_elsif_statement: FM_PLACEHOLDER | FM_IF (fm_elsif_statement | elsif_statement) (FM_ELSE_IF (fm_elsif_statement | elsif_statement))* FM_ELSE (fm_elsif_statement | elsif_statement) FM_IF_CLOSE;

if_elsif_statement : ELSIF (fm_cond_expression | cond_expression) (fm_crlf | crlf) (fm_statement_body | statement_body)
                   | ELSIF (fm_cond_expression | cond_expression) (fm_crlf | crlf) (fm_statement_body | statement_body) (fm_else_token | else_token) (fm_crlf | crlf) (fm_statement_body | statement_body)
                   | ELSIF (fm_cond_expression | cond_expression) (fm_crlf | crlf) (fm_statement_body | statement_body) (fm_if_elsif_statement | if_elsif_statement)
                   ;
fm_if_elsif_statement: FM_PLACEHOLDER | FM_IF (fm_if_elsif_statement | if_elsif_statement) (FM_ELSE_IF (fm_if_elsif_statement | if_elsif_statement))* FM_ELSE (fm_if_elsif_statement | if_elsif_statement) FM_IF_CLOSE;

if_statement : IF (fm_cond_expression | cond_expression) (fm_crlf | crlf) (fm_statement_body | statement_body) END
             | IF (fm_cond_expression | cond_expression) (fm_crlf | crlf) (fm_statement_body | statement_body) (fm_else_token | else_token) (fm_crlf | crlf) (fm_statement_body | statement_body) END
             | IF (fm_cond_expression | cond_expression) (fm_crlf | crlf) (fm_statement_body | statement_body) (fm_elsif_statement | elsif_statement) END
             ;

unless_statement : UNLESS (fm_cond_expression | cond_expression) (fm_crlf | crlf) (fm_statement_body | statement_body) END
                 | UNLESS (fm_cond_expression | cond_expression) (fm_crlf | crlf) (fm_statement_body | statement_body) (fm_else_token | else_token) (fm_crlf | crlf) (fm_statement_body | statement_body) END
                 | UNLESS (fm_cond_expression | cond_expression) (fm_crlf | crlf) (fm_statement_body | statement_body) (fm_elsif_statement | elsif_statement) END
                 ;

while_statement : WHILE (fm_cond_expression | cond_expression) (fm_crlf | crlf) (fm_statement_body | statement_body) END;

for_statement : FOR LEFT_RBRACKET (fm_init_expression | init_expression) SEMICOLON (fm_cond_expression | cond_expression) SEMICOLON (fm_loop_expression | loop_expression) RIGHT_RBRACKET (fm_crlf | crlf) (fm_statement_body | statement_body) END
              | FOR (fm_init_expression | init_expression) SEMICOLON (fm_cond_expression | cond_expression) SEMICOLON (fm_loop_expression | loop_expression) (fm_crlf | crlf) (fm_statement_body | statement_body) END
              ;

init_expression : for_init_list;
fm_init_expression: FM_PLACEHOLDER | FM_IF (fm_init_expression | init_expression) (FM_ELSE_IF (fm_init_expression | init_expression))* FM_ELSE (fm_init_expression | init_expression) FM_IF_CLOSE;

all_assignment : ( int_assignment | float_assignment | string_assignment | dynamic_assignment );
fm_all_assignment: FM_PLACEHOLDER | FM_IF (fm_all_assignment | all_assignment) (FM_ELSE_IF (fm_all_assignment | all_assignment))* FM_ELSE (fm_all_assignment | all_assignment) FM_IF_CLOSE;

for_init_list : fm_for_init_list COMMA ( fm_all_assignment | all_assignment ) 
  | for_init_list COMMA (fm_all_assignment | all_assignment)
              | all_assignment
              ;
fm_for_init_list: FM_PLACEHOLDER | FM_IF (fm_for_init_list | for_init_list) (FM_ELSE_IF (fm_for_init_list | for_init_list))* FM_ELSE (fm_for_init_list | for_init_list) FM_IF_CLOSE;

cond_expression : comparison_list;
fm_cond_expression: FM_PLACEHOLDER | FM_IF (fm_cond_expression | cond_expression) (FM_ELSE_IF (fm_cond_expression | cond_expression))* FM_ELSE (fm_cond_expression | cond_expression) FM_IF_CLOSE;

loop_expression : for_loop_list;
fm_loop_expression: FM_PLACEHOLDER | FM_IF (fm_loop_expression | loop_expression) (FM_ELSE_IF (fm_loop_expression | loop_expression))* FM_ELSE (fm_loop_expression | loop_expression) FM_IF_CLOSE;

for_loop_list : fm_for_loop_list COMMA ( fm_all_assignment | all_assignment ) 
  | for_loop_list COMMA (fm_all_assignment | all_assignment)
              | all_assignment
              ;
fm_for_loop_list: FM_PLACEHOLDER | FM_IF (fm_for_loop_list | for_loop_list) (FM_ELSE_IF (fm_for_loop_list | for_loop_list))* FM_ELSE (fm_for_loop_list | for_loop_list) FM_IF_CLOSE;

statement_body : statement_expression_list;
fm_statement_body: FM_PLACEHOLDER | FM_IF (fm_statement_body | statement_body) (FM_ELSE_IF (fm_statement_body | statement_body))* FM_ELSE (fm_statement_body | statement_body) FM_IF_CLOSE;

statement_expression_list : (fm_expression | expression) (fm_terminator | terminator)
                          | RETRY (fm_terminator | terminator)
                          | (fm_break_expression | break_expression) (fm_terminator | terminator)
                          | fm_statement_expression_list ( fm_expression | expression ) ( fm_terminator | terminator ) 
  | statement_expression_list (fm_expression | expression) (fm_terminator | terminator)
                          | fm_statement_expression_list RETRY ( fm_terminator | terminator ) 
  | statement_expression_list RETRY (fm_terminator | terminator)
                          | fm_statement_expression_list ( fm_break_expression | break_expression ) ( fm_terminator | terminator ) 
  | statement_expression_list (fm_break_expression | break_expression) (fm_terminator | terminator)
                          ;
fm_statement_expression_list: FM_PLACEHOLDER | FM_IF (fm_statement_expression_list | statement_expression_list) (FM_ELSE_IF (fm_statement_expression_list | statement_expression_list))* FM_ELSE (fm_statement_expression_list | statement_expression_list) FM_IF_CLOSE;

assignment : (fm_lvalue | lvalue) ASSIGN (fm_rvalue | rvalue)
           | (fm_lvalue | lvalue) ( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) (fm_rvalue | rvalue)
           ;

dynamic_assignment : (fm_lvalue | lvalue) ASSIGN (fm_dynamic_result | dynamic_result)
                   | (fm_lvalue | lvalue) ( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) (fm_dynamic_result | dynamic_result)
                   ;

int_assignment : (fm_lvalue | lvalue) ASSIGN (fm_int_result | int_result)
               | (fm_lvalue | lvalue) ( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) (fm_int_result | int_result)
               ;

float_assignment : (fm_lvalue | lvalue) ASSIGN (fm_float_result | float_result)
                 | (fm_lvalue | lvalue) ( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) (fm_float_result | float_result)
                 ;

string_assignment : (fm_lvalue | lvalue) ASSIGN (fm_string_result | string_result)
                  | (fm_lvalue | lvalue) PLUS_ASSIGN (fm_string_result | string_result)
                  ;

initial_array_assignment : (fm_lvalue | lvalue) ASSIGN LEFT_SBRACKET RIGHT_SBRACKET;

array_assignment : (fm_array_selector | array_selector) ASSIGN (fm_all_result | all_result);

array_definition : LEFT_SBRACKET (fm_array_definition_elements | array_definition_elements) RIGHT_SBRACKET;

array_definition_elements : ( int_result | dynamic_result )
                          | fm_array_definition_elements COMMA ( int_result | dynamic_result ) 
  | array_definition_elements COMMA ( int_result | dynamic_result )
                          ;
fm_array_definition_elements: FM_PLACEHOLDER | FM_IF (fm_array_definition_elements | array_definition_elements) (FM_ELSE_IF (fm_array_definition_elements | array_definition_elements))* FM_ELSE (fm_array_definition_elements | array_definition_elements) FM_IF_CLOSE;

array_selector : (fm_id_ | id_) LEFT_SBRACKET ( int_result | dynamic_result ) RIGHT_SBRACKET
               | (fm_id_global | id_global) LEFT_SBRACKET ( int_result | dynamic_result ) RIGHT_SBRACKET
               ;
fm_array_selector: FM_PLACEHOLDER | FM_IF (fm_array_selector | array_selector) (FM_ELSE_IF (fm_array_selector | array_selector))* FM_ELSE (fm_array_selector | array_selector) FM_IF_CLOSE;

dynamic_result : fm_dynamic_result op = ( MUL | DIV | MOD ) ( fm_int_result | int_result ) 
  | dynamic_result ( MUL | DIV | MOD ) (fm_int_result | int_result)
               | (fm_int_result | int_result) ( MUL | DIV | MOD ) (fm_dynamic_result | dynamic_result)
               | fm_dynamic_result op = ( MUL | DIV | MOD ) ( fm_float_result | float_result ) 
  | dynamic_result ( MUL | DIV | MOD ) (fm_float_result | float_result)
               | (fm_float_result | float_result) ( MUL | DIV | MOD ) (fm_dynamic_result | dynamic_result)
               | fm_dynamic_result op = ( MUL | DIV | MOD ) ( fm_dynamic_result | dynamic_result ) 
  | dynamic_result ( MUL | DIV | MOD ) (fm_dynamic_result | dynamic_result)
               | fm_dynamic_result op = MUL ( fm_string_result | string_result ) 
  | dynamic_result MUL (fm_string_result | string_result)
               | (fm_string_result | string_result) MUL (fm_dynamic_result | dynamic_result)
               | fm_dynamic_result op = ( PLUS | MINUS ) ( fm_int_result | int_result ) 
  | dynamic_result ( PLUS | MINUS ) (fm_int_result | int_result)
               | (fm_int_result | int_result) ( PLUS | MINUS ) (fm_dynamic_result | dynamic_result)
               | fm_dynamic_result op = ( PLUS | MINUS ) ( fm_float_result | float_result ) 
  | dynamic_result ( PLUS | MINUS )  (fm_float_result | float_result)
               | (fm_float_result | float_result) ( PLUS | MINUS )  (fm_dynamic_result | dynamic_result)
               | fm_dynamic_result op = ( PLUS | MINUS ) ( fm_dynamic_result | dynamic_result ) 
  | dynamic_result ( PLUS | MINUS ) (fm_dynamic_result | dynamic_result)
               | LEFT_RBRACKET (fm_dynamic_result | dynamic_result) RIGHT_RBRACKET
               | dynamic_
               ;
fm_dynamic_result: FM_PLACEHOLDER | FM_IF (fm_dynamic_result | dynamic_result) (FM_ELSE_IF (fm_dynamic_result | dynamic_result))* FM_ELSE (fm_dynamic_result | dynamic_result) FM_IF_CLOSE;

dynamic_ : id_
        | function_call_assignment
        | array_selector
        ;

int_result : fm_int_result op = ( MUL | DIV | MOD ) ( fm_int_result | int_result ) 
  | int_result ( MUL | DIV | MOD ) (fm_int_result | int_result)
           | fm_int_result op = ( PLUS | MINUS ) ( fm_int_result | int_result ) 
  | int_result ( PLUS | MINUS ) (fm_int_result | int_result)
           | LEFT_RBRACKET (fm_int_result | int_result) RIGHT_RBRACKET
           | int_t
           ;
fm_int_result: FM_PLACEHOLDER | FM_IF (fm_int_result | int_result) (FM_ELSE_IF (fm_int_result | int_result))* FM_ELSE (fm_int_result | int_result) FM_IF_CLOSE;

float_result : fm_float_result op = ( MUL | DIV | MOD ) ( fm_float_result | float_result ) 
  | float_result ( MUL | DIV | MOD ) (fm_float_result | float_result)
             | (fm_int_result | int_result) ( MUL | DIV | MOD ) (fm_float_result | float_result)
             | fm_float_result op = ( MUL | DIV | MOD ) ( fm_int_result | int_result ) 
  | float_result ( MUL | DIV | MOD ) (fm_int_result | int_result)
             | fm_float_result op = ( PLUS | MINUS ) ( fm_float_result | float_result ) 
  | float_result ( PLUS | MINUS ) (fm_float_result | float_result)
             | (fm_int_result | int_result) ( PLUS | MINUS )  (fm_float_result | float_result)
             | fm_float_result op = ( PLUS | MINUS ) ( fm_int_result | int_result ) 
  | float_result ( PLUS | MINUS )  (fm_int_result | int_result)
             | LEFT_RBRACKET (fm_float_result | float_result) RIGHT_RBRACKET
             | float_t
             ;
fm_float_result: FM_PLACEHOLDER | FM_IF (fm_float_result | float_result) (FM_ELSE_IF (fm_float_result | float_result))* FM_ELSE (fm_float_result | float_result) FM_IF_CLOSE;

string_result : fm_string_result op = MUL ( fm_int_result | int_result ) 
  | string_result MUL (fm_int_result | int_result)
              | (fm_int_result | int_result) MUL (fm_string_result | string_result)
              | fm_string_result op = PLUS ( fm_string_result | string_result ) 
  | string_result PLUS (fm_string_result | string_result)
              | literal_t
              ;
fm_string_result: FM_PLACEHOLDER | FM_IF (fm_string_result | string_result) (FM_ELSE_IF (fm_string_result | string_result))* FM_ELSE (fm_string_result | string_result) FM_IF_CLOSE;

comparison_list : (fm_comparison | comparison) BIT_AND (fm_comparison_list | comparison_list)
                | (fm_comparison | comparison) (AND) (fm_comparison_list | comparison_list)
                | (fm_comparison | comparison) BIT_OR (fm_comparison_list | comparison_list)
                | (fm_comparison | comparison) (OR) (fm_comparison_list | comparison_list)
                | LEFT_RBRACKET (fm_comparison_list | comparison_list) RIGHT_RBRACKET
                | comparison
                ;
fm_comparison_list: FM_PLACEHOLDER | FM_IF (fm_comparison_list | comparison_list) (FM_ELSE_IF (fm_comparison_list | comparison_list))* FM_ELSE (fm_comparison_list | comparison_list) FM_IF_CLOSE;

comparison : (fm_comp_var | comp_var) ( LESS | GREATER | LESS_EQUAL | GREATER_EQUAL ) (fm_comp_var | comp_var)
           | (fm_comp_var | comp_var) ( EQUAL | NOT_EQUAL ) (fm_comp_var | comp_var)
           ;
fm_comparison: FM_PLACEHOLDER | FM_IF (fm_comparison | comparison) (FM_ELSE_IF (fm_comparison | comparison))* FM_ELSE (fm_comparison | comparison) FM_IF_CLOSE;

comp_var : all_result
         | array_selector
         | id_
         ;
fm_comp_var: FM_PLACEHOLDER | FM_IF (fm_comp_var | comp_var) (FM_ELSE_IF (fm_comp_var | comp_var))* FM_ELSE (fm_comp_var | comp_var) FM_IF_CLOSE;

lvalue : id_
       //| id_global
       ;
fm_lvalue: FM_PLACEHOLDER | FM_IF (fm_lvalue | lvalue) (FM_ELSE_IF (fm_lvalue | lvalue))* FM_ELSE (fm_lvalue | lvalue) FM_IF_CLOSE;

rvalue : lvalue

       | initial_array_assignment
       | array_assignment

       | int_result
       | float_result
       | string_result

       | global_set
       | global_get
       | dynamic_assignment
       | string_assignment
       | float_assignment
       | int_assignment
       | assignment

       | function_call
       | literal_t
       | bool_t
       | float_t
       | int_t
       | nil_t

       | fm_rvalue EXP ( fm_rvalue | rvalue ) 
  | rvalue EXP (fm_rvalue | rvalue)

       | ( (NOT) | BIT_NOT )(fm_rvalue | rvalue)

       | fm_rvalue ( MUL | DIV | MOD ) ( fm_rvalue | rvalue ) 
  | rvalue ( MUL | DIV | MOD ) (fm_rvalue | rvalue)
       | fm_rvalue ( PLUS | MINUS ) ( fm_rvalue | rvalue ) 
  | rvalue ( PLUS | MINUS ) (fm_rvalue | rvalue)

       | fm_rvalue ( BIT_SHL | BIT_SHR ) ( fm_rvalue | rvalue ) 
  | rvalue ( BIT_SHL | BIT_SHR ) (fm_rvalue | rvalue)

       | fm_rvalue BIT_AND ( fm_rvalue | rvalue ) 
  | rvalue BIT_AND (fm_rvalue | rvalue)

       | fm_rvalue ( BIT_OR | BIT_XOR ) ( fm_rvalue | rvalue ) 
  | rvalue ( BIT_OR | BIT_XOR )(fm_rvalue | rvalue)

       | fm_rvalue ( LESS | GREATER | LESS_EQUAL | GREATER_EQUAL ) ( fm_rvalue | rvalue ) 
  | rvalue ( LESS | GREATER | LESS_EQUAL | GREATER_EQUAL ) (fm_rvalue | rvalue)

       | fm_rvalue ( EQUAL | NOT_EQUAL ) ( fm_rvalue | rvalue ) 
  | rvalue ( EQUAL | NOT_EQUAL ) (fm_rvalue | rvalue)

       | fm_rvalue ( ( OR ) | ( AND ) ) ( fm_rvalue | rvalue ) 
  | rvalue ( (OR) | (AND) ) (fm_rvalue | rvalue)

       | LEFT_RBRACKET (fm_rvalue | rvalue) RIGHT_RBRACKET
       ;
fm_rvalue: FM_PLACEHOLDER | FM_IF (fm_rvalue | rvalue) (FM_ELSE_IF (fm_rvalue | rvalue))* FM_ELSE (fm_rvalue | rvalue) FM_IF_CLOSE;

break_expression : BREAK;
fm_break_expression: FM_PLACEHOLDER | FM_IF (fm_break_expression | break_expression) (FM_ELSE_IF (fm_break_expression | break_expression))* FM_ELSE (fm_break_expression | break_expression) FM_IF_CLOSE;

literal_t : (LITERAL);
fm_literal_t: FM_PLACEHOLDER | FM_IF (fm_literal_t | literal_t) (FM_ELSE_IF (fm_literal_t | literal_t))* FM_ELSE (fm_literal_t | literal_t) FM_IF_CLOSE;

float_t : (FLOAT);

int_t : (INT);

bool_t : TRUE
       | FALSE
       ;

nil_t : NIL;

id_ : (ID);
fm_id_: FM_PLACEHOLDER | FM_IF (fm_id_ | id_) (FM_ELSE_IF (fm_id_ | id_))* FM_ELSE (fm_id_ | id_) FM_IF_CLOSE;

id_global : ID_GLOBAL;
fm_id_global: FM_PLACEHOLDER | FM_IF (fm_id_global | id_global) (FM_ELSE_IF (fm_id_global | id_global))* FM_ELSE (fm_id_global | id_global) FM_IF_CLOSE;

id_function : (ID_FUNCTION);

terminator : fm_terminator SEMICOLON 
  | terminator SEMICOLON
           | fm_terminator ( fm_crlf | crlf ) 
  | terminator (fm_crlf | crlf)
           | SEMICOLON
           | crlf
           ;
fm_terminator: FM_PLACEHOLDER | FM_IF (fm_terminator | terminator) (FM_ELSE_IF (fm_terminator | terminator))* FM_ELSE (fm_terminator | terminator) FM_IF_CLOSE;

else_token : ELSE;
fm_else_token: FM_PLACEHOLDER | FM_IF (fm_else_token | else_token) (FM_ELSE_IF (fm_else_token | else_token))* FM_ELSE (fm_else_token | else_token) FM_IF_CLOSE;

crlf : (CRLF);
fm_crlf: FM_PLACEHOLDER | FM_IF (fm_crlf | crlf) (FM_ELSE_IF (fm_crlf | crlf))* FM_ELSE (fm_crlf | crlf) FM_IF_CLOSE;

fragment ESCAPED_QUOTE : '\\"';
LITERAL : '"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"'
        | '\'' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '\'';

COMMA : ',';
SEMICOLON : ';';
CRLF : '\r'? '\n';

REQUIRE : 'require';
END : 'end';
DEF : 'def';
RETURN : 'return';
PIR : 'pir';

IF: 'if';
ELSE : 'else';
ELSIF : 'elsif';
UNLESS : 'unless';
WHILE : 'while';
RETRY : 'retry';
BREAK : 'break';
FOR : 'for';

TRUE : 'true';
FALSE : 'false';

PLUS : '+';
MINUS : '-';
MUL : '*';
DIV : '/';
MOD : '%';
EXP : '**';

EQUAL : '==';
NOT_EQUAL : '!=';
GREATER : '>';
LESS : '<';
LESS_EQUAL : '<=';
GREATER_EQUAL : '>=';

ASSIGN : '=';
PLUS_ASSIGN : '+=';
MINUS_ASSIGN : '-=';
MUL_ASSIGN : '*=';
DIV_ASSIGN : '/=';
MOD_ASSIGN : '%=';
EXP_ASSIGN : '**=';

BIT_AND : '&';
BIT_OR : '|';
BIT_XOR : '^';
BIT_NOT : '~';
BIT_SHL : '<<';
BIT_SHR : '>>';

AND : 'and' | '&&';
OR : 'or' | '||';
NOT : 'not' | '!';

LEFT_RBRACKET : '(';
RIGHT_RBRACKET : ')';
LEFT_SBRACKET : '[';
RIGHT_SBRACKET : ']';

NIL : 'nil';

SL_COMMENT : ('#' ~('\r' | '\n')* '\r'? '\n') -> skip;
ML_COMMENT : ('=begin' .*? '=end' '\r'? '\n') -> skip;
WS : (' '|'\t')+ -> skip;

INT : [0-9]+;
FLOAT : [0-9]*'.'[0-9]+;
ID : [a-zA-Z_][a-zA-Z0-9_]*;
ID_GLOBAL : '$'ID;
ID_FUNCTION : ID[?];
FM_PLACEHOLDER: '${' ~'}'+? '}';
FM_IF: '<#if' ~'>'+? '>';
FM_IF_CLOSE: '</#if>';
FM_ELSE_IF: '<#elseif' ~'>'+? '>';
FM_ELSE: '<#else>';
FM_LIST: '<#list' .+? 'as' ~'>'+? '>';
FM_LIST_CLOSE: '</#list>';