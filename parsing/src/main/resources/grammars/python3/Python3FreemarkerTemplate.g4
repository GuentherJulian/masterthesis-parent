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
parser grammar Python3FreemarkerTemplate;

options {
    tokenVocab=Python3Lexer;
}

// All comments that start with "///" are copy-pasted from
// The Python Language Reference

single_input: NEWLINE | simple_stmt | (fm_compound_stmt | compound_stmt) NEWLINE;
file_input: (NEWLINE | stmt)* EOF;
eval_input: (fm_testlist | testlist) NEWLINE* EOF;

decorator: FM_ImplicitToken1  (fm_dotted_name | dotted_name) ( FM_ImplicitToken2  (arglist)? FM_ImplicitToken3  )? NEWLINE;
fm_decoratorPlus: FM_PLACEHOLDER | (FM_IF (fm_decoratorPlus | decorator)* (FM_ELSE_IF (fm_decoratorPlus | decorator)*)* (FM_ELSE (fm_decoratorPlus | decorator)*)? FM_IF_CLOSE | FM_LIST (fm_decoratorPlus | decorator)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_decoratorPlus | decorator)* (FM_ELSE_IF (fm_decoratorPlus | decorator)*)* FM_ELSE (fm_decoratorPlus | decorator)* FM_IF_CLOSE | FM_LIST (fm_decoratorPlus | decorator)* FM_ELSE (fm_decoratorPlus | decorator)* FM_LIST_CLOSE) (FM_IF (fm_decoratorPlus | decorator)* (FM_ELSE_IF (fm_decoratorPlus | decorator)*)* (FM_ELSE (fm_decoratorPlus | decorator)*)? FM_IF_CLOSE | FM_LIST (fm_decoratorPlus | decorator)* FM_LIST_CLOSE)*;
decorators: (fm_decoratorPlus | decorator)+;
fm_decorators: FM_PLACEHOLDER | FM_IF (fm_decorators | decorators) (FM_ELSE_IF (fm_decorators | decorators))* FM_ELSE (fm_decorators | decorators) FM_IF_CLOSE;
decorated: (fm_decorators | decorators) (classdef | funcdef | async_funcdef);

async_funcdef: ASYNC (fm_funcdef | funcdef);
funcdef: FM_ImplicitToken4  NAME (fm_parameters | parameters) (FM_ImplicitToken5  (fm_test | test))? FM_ImplicitToken6  (fm_suite | suite);
fm_funcdef: FM_PLACEHOLDER | FM_IF (fm_funcdef | funcdef) (FM_ELSE_IF (fm_funcdef | funcdef))* FM_ELSE (fm_funcdef | funcdef) FM_IF_CLOSE;

parameters: FM_ImplicitToken7  (typedargslist)? FM_ImplicitToken8 ;
fm_parameters: FM_PLACEHOLDER | FM_IF (fm_parameters | parameters) (FM_ELSE_IF (fm_parameters | parameters))* FM_ELSE (fm_parameters | parameters) FM_IF_CLOSE;
typedargslist: ((fm_tfpdef | tfpdef) (FM_ImplicitToken9  (fm_test | test))? (FM_ImplicitToken10  (fm_tfpdef | tfpdef) (FM_ImplicitToken11  (fm_test | test))?)* (FM_ImplicitToken12  (
        FM_ImplicitToken13  (tfpdef)? (FM_ImplicitToken14  (fm_tfpdef | tfpdef) (FM_ImplicitToken15  (fm_test | test))?)* (FM_ImplicitToken16  (FM_ImplicitToken17  (fm_tfpdef | tfpdef) (FM_ImplicitToken18 )?)?)?
      | FM_ImplicitToken19  (fm_tfpdef | tfpdef) (FM_ImplicitToken20 )?)?)?
  | FM_ImplicitToken21  (tfpdef)? (FM_ImplicitToken22  (fm_tfpdef | tfpdef) (FM_ImplicitToken23  (fm_test | test))?)* (FM_ImplicitToken24  (FM_ImplicitToken25  (fm_tfpdef | tfpdef) (FM_ImplicitToken26 )?)?)?
  | FM_ImplicitToken27  (fm_tfpdef | tfpdef) (FM_ImplicitToken28 )?);
tfpdef: NAME (FM_ImplicitToken29  (fm_test | test))?;
fm_tfpdef: FM_PLACEHOLDER | FM_IF (fm_tfpdef | tfpdef) (FM_ELSE_IF (fm_tfpdef | tfpdef))* FM_ELSE (fm_tfpdef | tfpdef) FM_IF_CLOSE;
varargslist: ((fm_vfpdef | vfpdef) (FM_ImplicitToken30  (fm_test | test))? (FM_ImplicitToken31  (fm_vfpdef | vfpdef) (FM_ImplicitToken32  (fm_test | test))?)* (FM_ImplicitToken33  (
        FM_ImplicitToken34  (vfpdef)? (FM_ImplicitToken35  (fm_vfpdef | vfpdef) (FM_ImplicitToken36  (fm_test | test))?)* (FM_ImplicitToken37  (FM_ImplicitToken38  (fm_vfpdef | vfpdef) (FM_ImplicitToken39 )?)?)?
      | FM_ImplicitToken40  (fm_vfpdef | vfpdef) (FM_ImplicitToken41 )?)?)?
  | FM_ImplicitToken42  (vfpdef)? (FM_ImplicitToken43  (fm_vfpdef | vfpdef) (FM_ImplicitToken44  (fm_test | test))?)* (FM_ImplicitToken45  (FM_ImplicitToken46  (fm_vfpdef | vfpdef) (FM_ImplicitToken47 )?)?)?
  | FM_ImplicitToken48  (fm_vfpdef | vfpdef) (FM_ImplicitToken49 )?
);
vfpdef: NAME;
fm_vfpdef: FM_PLACEHOLDER | FM_IF (fm_vfpdef | vfpdef) (FM_ELSE_IF (fm_vfpdef | vfpdef))* FM_ELSE (fm_vfpdef | vfpdef) FM_IF_CLOSE;

stmt: simple_stmt | compound_stmt;
fm_stmtPlus: FM_PLACEHOLDER | (FM_IF (fm_stmtPlus | stmt)* (FM_ELSE_IF (fm_stmtPlus | stmt)*)* (FM_ELSE (fm_stmtPlus | stmt)*)? FM_IF_CLOSE | FM_LIST (fm_stmtPlus | stmt)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_stmtPlus | stmt)* (FM_ELSE_IF (fm_stmtPlus | stmt)*)* FM_ELSE (fm_stmtPlus | stmt)* FM_IF_CLOSE | FM_LIST (fm_stmtPlus | stmt)* FM_ELSE (fm_stmtPlus | stmt)* FM_LIST_CLOSE) (FM_IF (fm_stmtPlus | stmt)* (FM_ELSE_IF (fm_stmtPlus | stmt)*)* (FM_ELSE (fm_stmtPlus | stmt)*)? FM_IF_CLOSE | FM_LIST (fm_stmtPlus | stmt)* FM_LIST_CLOSE)*;
simple_stmt: (fm_small_stmt | small_stmt) (FM_ImplicitToken50  (fm_small_stmt | small_stmt))* (FM_ImplicitToken51 )? NEWLINE;
small_stmt: (expr_stmt | del_stmt | pass_stmt | flow_stmt |
             import_stmt | global_stmt | nonlocal_stmt | assert_stmt);
fm_small_stmt: FM_PLACEHOLDER | FM_IF (fm_small_stmt | small_stmt) (FM_ELSE_IF (fm_small_stmt | small_stmt))* FM_ELSE (fm_small_stmt | small_stmt) FM_IF_CLOSE;
expr_stmt: (fm_testlist_star_expr | testlist_star_expr) (annassign | (fm_augassign | augassign) (yield_expr|testlist) |
                     (FM_ImplicitToken52  (yield_expr|testlist_star_expr))*);
annassign: FM_ImplicitToken53  (fm_test | test) (FM_ImplicitToken54  (fm_test | test))?;
testlist_star_expr: (test|star_expr) (FM_ImplicitToken55  (test|star_expr))* (FM_ImplicitToken56 )?;
fm_testlist_star_expr: FM_PLACEHOLDER | FM_IF (fm_testlist_star_expr | testlist_star_expr) (FM_ELSE_IF (fm_testlist_star_expr | testlist_star_expr))* FM_ELSE (fm_testlist_star_expr | testlist_star_expr) FM_IF_CLOSE;
augassign: (FM_ImplicitToken57  | FM_ImplicitToken58  | FM_ImplicitToken59  | FM_ImplicitToken60  | FM_ImplicitToken61  | FM_ImplicitToken62  | FM_ImplicitToken63  | FM_ImplicitToken64  | FM_ImplicitToken65  |
            FM_ImplicitToken66  | FM_ImplicitToken67  | FM_ImplicitToken68  | FM_ImplicitToken69 );
fm_augassign: FM_PLACEHOLDER | FM_IF (fm_augassign | augassign) (FM_ELSE_IF (fm_augassign | augassign))* FM_ELSE (fm_augassign | augassign) FM_IF_CLOSE;
// For normal and annotated assignments, additional restrictions enforced by the interpreter
del_stmt: FM_ImplicitToken70  (fm_exprlist | exprlist);
pass_stmt: FM_ImplicitToken71 ;
flow_stmt: break_stmt | continue_stmt | return_stmt | raise_stmt | yield_stmt;
break_stmt: FM_ImplicitToken72 ;
continue_stmt: FM_ImplicitToken73 ;
return_stmt: FM_ImplicitToken74  (testlist)?;
yield_stmt: yield_expr;
raise_stmt: FM_ImplicitToken75  ((fm_test | test) (FM_ImplicitToken76  (fm_test | test))?)?;
import_stmt: import_name | import_from;
import_name: FM_ImplicitToken77  (fm_dotted_as_names | dotted_as_names);
// note below: the ('.' | '...') is necessary because '...' is tokenized as ELLIPSIS
import_from: (FM_ImplicitToken78  ((FM_ImplicitToken79  | FM_ImplicitToken80 )* (fm_dotted_name | dotted_name) | (FM_ImplicitToken81  | FM_ImplicitToken82 )+)
              FM_ImplicitToken83  (FM_ImplicitToken84  | FM_ImplicitToken85  (fm_import_as_names | import_as_names) FM_ImplicitToken86  | import_as_names));
import_as_name: NAME (FM_ImplicitToken87  NAME)?;
fm_import_as_name: FM_PLACEHOLDER | FM_IF (fm_import_as_name | import_as_name) (FM_ELSE_IF (fm_import_as_name | import_as_name))* FM_ELSE (fm_import_as_name | import_as_name) FM_IF_CLOSE;
dotted_as_name: (fm_dotted_name | dotted_name) (FM_ImplicitToken88  NAME)?;
fm_dotted_as_name: FM_PLACEHOLDER | FM_IF (fm_dotted_as_name | dotted_as_name) (FM_ELSE_IF (fm_dotted_as_name | dotted_as_name))* FM_ELSE (fm_dotted_as_name | dotted_as_name) FM_IF_CLOSE;
import_as_names: (fm_import_as_name | import_as_name) (FM_ImplicitToken89  (fm_import_as_name | import_as_name))* (FM_ImplicitToken90 )?;
fm_import_as_names: FM_PLACEHOLDER | FM_IF (fm_import_as_names | import_as_names) (FM_ELSE_IF (fm_import_as_names | import_as_names))* FM_ELSE (fm_import_as_names | import_as_names) FM_IF_CLOSE;
dotted_as_names: (fm_dotted_as_name | dotted_as_name) (FM_ImplicitToken91  (fm_dotted_as_name | dotted_as_name))*;
fm_dotted_as_names: FM_PLACEHOLDER | FM_IF (fm_dotted_as_names | dotted_as_names) (FM_ELSE_IF (fm_dotted_as_names | dotted_as_names))* FM_ELSE (fm_dotted_as_names | dotted_as_names) FM_IF_CLOSE;
dotted_name: NAME (FM_ImplicitToken92  NAME)*;
fm_dotted_name: FM_PLACEHOLDER | FM_IF (fm_dotted_name | dotted_name) (FM_ELSE_IF (fm_dotted_name | dotted_name))* FM_ELSE (fm_dotted_name | dotted_name) FM_IF_CLOSE;
global_stmt: FM_ImplicitToken93  NAME (FM_ImplicitToken94  NAME)*;
nonlocal_stmt: FM_ImplicitToken95  NAME (FM_ImplicitToken96  NAME)*;
assert_stmt: FM_ImplicitToken97  (fm_test | test) (FM_ImplicitToken98  (fm_test | test))?;

compound_stmt: if_stmt | while_stmt | for_stmt | try_stmt | with_stmt | funcdef | classdef | decorated | async_stmt;
fm_compound_stmt: FM_PLACEHOLDER | FM_IF (fm_compound_stmt | compound_stmt) (FM_ELSE_IF (fm_compound_stmt | compound_stmt))* FM_ELSE (fm_compound_stmt | compound_stmt) FM_IF_CLOSE;
async_stmt: ASYNC (funcdef | with_stmt | for_stmt);
if_stmt: FM_ImplicitToken99  (fm_test | test) FM_ImplicitToken100  (fm_suite | suite) (FM_ImplicitToken101  (fm_test | test) FM_ImplicitToken102  (fm_suite | suite))* (FM_ImplicitToken103  FM_ImplicitToken104  (fm_suite | suite))?;
while_stmt: FM_ImplicitToken105  (fm_test | test) FM_ImplicitToken106  (fm_suite | suite) (FM_ImplicitToken107  FM_ImplicitToken108  (fm_suite | suite))?;
for_stmt: FM_ImplicitToken109  (fm_exprlist | exprlist) FM_ImplicitToken110  (fm_testlist | testlist) FM_ImplicitToken111  (fm_suite | suite) (FM_ImplicitToken112  FM_ImplicitToken113  (fm_suite | suite))?;
try_stmt: (FM_ImplicitToken114  FM_ImplicitToken115  (fm_suite | suite)
           (((fm_except_clause | except_clause) FM_ImplicitToken116  (fm_suite | suite))+
            (FM_ImplicitToken117  FM_ImplicitToken118  (fm_suite | suite))?
            (FM_ImplicitToken119  FM_ImplicitToken120  (fm_suite | suite))? |
           FM_ImplicitToken121  FM_ImplicitToken122  (fm_suite | suite)));
with_stmt: FM_ImplicitToken123  (fm_with_item | with_item) (FM_ImplicitToken124  (fm_with_item | with_item))*  FM_ImplicitToken125  (fm_suite | suite);
with_item: (fm_test | test) (FM_ImplicitToken126  (fm_expr | expr))?;
fm_with_item: FM_PLACEHOLDER | FM_IF (fm_with_item | with_item) (FM_ELSE_IF (fm_with_item | with_item))* FM_ELSE (fm_with_item | with_item) FM_IF_CLOSE;
// NB compile.c makes sure that the default except clause is last
except_clause: FM_ImplicitToken127  ((fm_test | test) (FM_ImplicitToken128  NAME)?)?;
fm_except_clause: FM_PLACEHOLDER | FM_IF (fm_except_clause | except_clause) (FM_ELSE_IF (fm_except_clause | except_clause))* FM_ELSE (fm_except_clause | except_clause) FM_IF_CLOSE;
suite: simple_stmt | NEWLINE INDENT (fm_stmtPlus | stmt)+ DEDENT;
fm_suite: FM_PLACEHOLDER | FM_IF (fm_suite | suite) (FM_ELSE_IF (fm_suite | suite))* FM_ELSE (fm_suite | suite) FM_IF_CLOSE;

test: (fm_or_test | or_test) (FM_ImplicitToken129  (fm_or_test | or_test) FM_ImplicitToken130  (fm_test | test))? | lambdef;
fm_test: FM_PLACEHOLDER | FM_IF (fm_test | test) (FM_ELSE_IF (fm_test | test))* FM_ELSE (fm_test | test) FM_IF_CLOSE;
test_nocond: or_test | lambdef_nocond;
fm_test_nocond: FM_PLACEHOLDER | FM_IF (fm_test_nocond | test_nocond) (FM_ELSE_IF (fm_test_nocond | test_nocond))* FM_ELSE (fm_test_nocond | test_nocond) FM_IF_CLOSE;
lambdef: FM_ImplicitToken131  (varargslist)? FM_ImplicitToken132  (fm_test | test);
lambdef_nocond: FM_ImplicitToken133  (varargslist)? FM_ImplicitToken134  (fm_test_nocond | test_nocond);
or_test: (fm_and_test | and_test) (FM_ImplicitToken135  (fm_and_test | and_test))*;
fm_or_test: FM_PLACEHOLDER | FM_IF (fm_or_test | or_test) (FM_ELSE_IF (fm_or_test | or_test))* FM_ELSE (fm_or_test | or_test) FM_IF_CLOSE;
and_test: (fm_not_test | not_test) (FM_ImplicitToken136  (fm_not_test | not_test))*;
fm_and_test: FM_PLACEHOLDER | FM_IF (fm_and_test | and_test) (FM_ELSE_IF (fm_and_test | and_test))* FM_ELSE (fm_and_test | and_test) FM_IF_CLOSE;
not_test: FM_ImplicitToken137  (fm_not_test | not_test) | comparison;
fm_not_test: FM_PLACEHOLDER | FM_IF (fm_not_test | not_test) (FM_ELSE_IF (fm_not_test | not_test))* FM_ELSE (fm_not_test | not_test) FM_IF_CLOSE;
comparison: (fm_expr | expr) ((fm_comp_op | comp_op) (fm_expr | expr))*;
// <> isn't actually a valid comparison operator in Python. It's here for the
// sake of a __future__ import described in PEP 401 (which really works :-)
comp_op: FM_ImplicitToken138 |FM_ImplicitToken139 |FM_ImplicitToken140 |FM_ImplicitToken141 |FM_ImplicitToken142 |FM_ImplicitToken143 |FM_ImplicitToken144 |FM_ImplicitToken145 |FM_ImplicitToken146  FM_ImplicitToken147 |FM_ImplicitToken148 |FM_ImplicitToken149  FM_ImplicitToken150 ;
fm_comp_op: FM_PLACEHOLDER | FM_IF (fm_comp_op | comp_op) (FM_ELSE_IF (fm_comp_op | comp_op))* FM_ELSE (fm_comp_op | comp_op) FM_IF_CLOSE;
star_expr: FM_ImplicitToken151  (fm_expr | expr);
expr: (fm_xor_expr | xor_expr) (FM_ImplicitToken152  (fm_xor_expr | xor_expr))*;
fm_expr: FM_PLACEHOLDER | FM_IF (fm_expr | expr) (FM_ELSE_IF (fm_expr | expr))* FM_ELSE (fm_expr | expr) FM_IF_CLOSE;
xor_expr: (fm_and_expr | and_expr) (FM_ImplicitToken153  (fm_and_expr | and_expr))*;
fm_xor_expr: FM_PLACEHOLDER | FM_IF (fm_xor_expr | xor_expr) (FM_ELSE_IF (fm_xor_expr | xor_expr))* FM_ELSE (fm_xor_expr | xor_expr) FM_IF_CLOSE;
and_expr: (fm_shift_expr | shift_expr) (FM_ImplicitToken154  (fm_shift_expr | shift_expr))*;
fm_and_expr: FM_PLACEHOLDER | FM_IF (fm_and_expr | and_expr) (FM_ELSE_IF (fm_and_expr | and_expr))* FM_ELSE (fm_and_expr | and_expr) FM_IF_CLOSE;
shift_expr: (fm_arith_expr | arith_expr) ((FM_ImplicitToken155 |FM_ImplicitToken156 ) (fm_arith_expr | arith_expr))*;
fm_shift_expr: FM_PLACEHOLDER | FM_IF (fm_shift_expr | shift_expr) (FM_ELSE_IF (fm_shift_expr | shift_expr))* FM_ELSE (fm_shift_expr | shift_expr) FM_IF_CLOSE;
arith_expr: (fm_term | term) ((FM_ImplicitToken157 |FM_ImplicitToken158 ) (fm_term | term))*;
fm_arith_expr: FM_PLACEHOLDER | FM_IF (fm_arith_expr | arith_expr) (FM_ELSE_IF (fm_arith_expr | arith_expr))* FM_ELSE (fm_arith_expr | arith_expr) FM_IF_CLOSE;
term: (fm_factor | factor) ((FM_ImplicitToken159 |FM_ImplicitToken160 |FM_ImplicitToken161 |FM_ImplicitToken162 |FM_ImplicitToken163 ) (fm_factor | factor))*;
fm_term: FM_PLACEHOLDER | FM_IF (fm_term | term) (FM_ELSE_IF (fm_term | term))* FM_ELSE (fm_term | term) FM_IF_CLOSE;
factor: (FM_ImplicitToken164 |FM_ImplicitToken165 |FM_ImplicitToken166 ) (fm_factor | factor) | power;
fm_factor: FM_PLACEHOLDER | FM_IF (fm_factor | factor) (FM_ELSE_IF (fm_factor | factor))* FM_ELSE (fm_factor | factor) FM_IF_CLOSE;
power: (fm_atom_expr | atom_expr) (FM_ImplicitToken167  (fm_factor | factor))?;
atom_expr: (AWAIT)? (fm_atom | atom) (fm_trailerStar | trailer)*;
fm_atom_expr: FM_PLACEHOLDER | FM_IF (fm_atom_expr | atom_expr) (FM_ELSE_IF (fm_atom_expr | atom_expr))* FM_ELSE (fm_atom_expr | atom_expr) FM_IF_CLOSE;
atom: (FM_ImplicitToken168  (yield_expr|testlist_comp)? FM_ImplicitToken169  |
       FM_ImplicitToken170  (testlist_comp)? FM_ImplicitToken171  |
       FM_ImplicitToken172  (dictorsetmaker)? FM_ImplicitToken173  |
       NAME | NUMBER | STRING+ | FM_ImplicitToken174  | FM_ImplicitToken175  | FM_ImplicitToken176  | FM_ImplicitToken177 );
fm_atom: FM_PLACEHOLDER | FM_IF (fm_atom | atom) (FM_ELSE_IF (fm_atom | atom))* FM_ELSE (fm_atom | atom) FM_IF_CLOSE;
testlist_comp: (test|star_expr) ( comp_for | (FM_ImplicitToken178  (test|star_expr))* (FM_ImplicitToken179 )? );
trailer: FM_ImplicitToken180  (arglist)? FM_ImplicitToken181  | FM_ImplicitToken182  (fm_subscriptlist | subscriptlist) FM_ImplicitToken183  | FM_ImplicitToken184  NAME;
fm_trailerStar: FM_PLACEHOLDER | FM_IF (fm_trailerStar | trailer)* (FM_ELSE_IF (fm_trailerStar | trailer)*)* (FM_ELSE (fm_trailerStar | trailer)*)? FM_IF_CLOSE | FM_LIST (fm_trailerStar | trailer)* FM_LIST_CLOSE;
subscriptlist: (fm_subscript_ | subscript_) (FM_ImplicitToken185  (fm_subscript_ | subscript_))* (FM_ImplicitToken186 )?;
fm_subscriptlist: FM_PLACEHOLDER | FM_IF (fm_subscriptlist | subscriptlist) (FM_ELSE_IF (fm_subscriptlist | subscriptlist))* FM_ELSE (fm_subscriptlist | subscriptlist) FM_IF_CLOSE;
subscript_: test | (test)? FM_ImplicitToken187  (test)? (sliceop)?;
fm_subscript_: FM_PLACEHOLDER | FM_IF (fm_subscript_ | subscript_) (FM_ELSE_IF (fm_subscript_ | subscript_))* FM_ELSE (fm_subscript_ | subscript_) FM_IF_CLOSE;
sliceop: FM_ImplicitToken188  (test)?;
exprlist: (expr|star_expr) (FM_ImplicitToken189  (expr|star_expr))* (FM_ImplicitToken190 )?;
fm_exprlist: FM_PLACEHOLDER | FM_IF (fm_exprlist | exprlist) (FM_ELSE_IF (fm_exprlist | exprlist))* FM_ELSE (fm_exprlist | exprlist) FM_IF_CLOSE;
testlist: (fm_test | test) (FM_ImplicitToken191  (fm_test | test))* (FM_ImplicitToken192 )?;
fm_testlist: FM_PLACEHOLDER | FM_IF (fm_testlist | testlist) (FM_ELSE_IF (fm_testlist | testlist))* FM_ELSE (fm_testlist | testlist) FM_IF_CLOSE;
dictorsetmaker: ( (((fm_test | test) FM_ImplicitToken193  (fm_test | test) | FM_ImplicitToken194  (fm_expr | expr))
                   (comp_for | (FM_ImplicitToken195  ((fm_test | test) FM_ImplicitToken196  (fm_test | test) | FM_ImplicitToken197  (fm_expr | expr)))* (FM_ImplicitToken198 )?)) |
                  ((test | star_expr)
                   (comp_for | (FM_ImplicitToken199  (test | star_expr))* (FM_ImplicitToken200 )?)) );

classdef: FM_ImplicitToken201  NAME (FM_ImplicitToken202  (arglist)? FM_ImplicitToken203 )? FM_ImplicitToken204  (fm_suite | suite);

arglist: (fm_argument | argument) (FM_ImplicitToken205  (fm_argument | argument))*  (FM_ImplicitToken206 )?;

// The reason that keywords are test nodes instead of NAME is that using NAME
// results in an ambiguity. ast.c makes sure it's a NAME.
// "test '=' test" is really "keyword '=' test", but we have no such token.
// These need to be in a single rule to avoid grammar that is ambiguous
// to our LL(1) parser. Even though 'test' includes '*expr' in star_expr,
// we explicitly match '*' here, too, to give it proper precedence.
// Illegal combinations and orderings are blocked in ast.c:
// multiple (test comp_for) arguments are blocked; keyword unpackings
// that precede iterable unpackings are blocked; etc.
argument: ( (fm_test | test) (comp_for)? |
            (fm_test | test) FM_ImplicitToken207  (fm_test | test) |
            FM_ImplicitToken208  (fm_test | test) |
            FM_ImplicitToken209  (fm_test | test) );
fm_argument: FM_PLACEHOLDER | FM_IF (fm_argument | argument) (FM_ELSE_IF (fm_argument | argument))* FM_ELSE (fm_argument | argument) FM_IF_CLOSE;

comp_iter: comp_for | comp_if;
comp_for: (ASYNC)? FM_ImplicitToken210  (fm_exprlist | exprlist) FM_ImplicitToken211  (fm_or_test | or_test) (comp_iter)?;
comp_if: FM_ImplicitToken212  (fm_test_nocond | test_nocond) (comp_iter)?;

// not used in grammar, but may appear in "node" passed from Parser to Compiler
encoding_decl: NAME;

yield_expr: FM_ImplicitToken213  (yield_arg)?;
yield_arg: FM_ImplicitToken214  (fm_test | test) | testlist;

