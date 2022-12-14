/*
 [The "BSD licence"]
 Copyright (c) 2014 Leonardo Lucena
 Copyright (c) 2018 Andrey Stolyarov
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
/*
   Derived from https://github.com/scala/scala/blob/2.12.x/spec/13-syntax-summary.md
 */

grammar ScalaFreemarkerTemplate;

literal
   : FM_ImplicitToken1 ? (IntegerLiteral)
   | FM_ImplicitToken2 ? (FloatingPointLiteral)
   | (BooleanLiteral)
   | (CharacterLiteral)
   | (StringLiteral)
   | SymbolLiteral
   | FM_ImplicitToken3 
   ;

qualId
   : (Id) (FM_ImplicitToken4  (Id))*
   ;
fm_qualId: FM_PLACEHOLDER | FM_IF (fm_qualId | qualId) (FM_ELSE_IF (fm_qualId | qualId))* FM_ELSE (fm_qualId | qualId) FM_IF_CLOSE;

ids
   : (Id) (FM_ImplicitToken5  (Id))*
   ;
fm_ids: FM_PLACEHOLDER | FM_IF (fm_ids | ids) (FM_ELSE_IF (fm_ids | ids))* FM_ELSE (fm_ids | ids) FM_IF_CLOSE;

stableId
   : (Id)
   | fm_stableId FM_ImplicitToken6 ( Id ) 
  | stableId FM_ImplicitToken6  (Id)
   | ((Id) FM_ImplicitToken7 )? (FM_ImplicitToken8  | FM_ImplicitToken9  (fm_classQualifierOpt | classQualifier)? FM_ImplicitToken10  (Id))
   ;
fm_stableId: FM_PLACEHOLDER | FM_IF (fm_stableId | stableId) (FM_ELSE_IF (fm_stableId | stableId))* FM_ELSE (fm_stableId | stableId) FM_IF_CLOSE;

classQualifier
   : FM_ImplicitToken11  (Id) FM_ImplicitToken12 
   ;
fm_classQualifierOpt: FM_PLACEHOLDER | FM_IF (fm_classQualifierOpt | classQualifier)? (FM_ELSE_IF (fm_classQualifierOpt | classQualifier)?)* (FM_ELSE (fm_classQualifierOpt | classQualifier)?)? FM_IF_CLOSE;

type_
   : (fm_functionArgTypes | functionArgTypes) FM_ImplicitToken13  (fm_type_ | type_)
   | (fm_infixType | infixType) (fm_existentialClauseOpt | existentialClause)?
   ;
fm_type_: FM_PLACEHOLDER | FM_IF (fm_type_ | type_) (FM_ELSE_IF (fm_type_ | type_))* FM_ELSE (fm_type_ | type_) FM_IF_CLOSE;

functionArgTypes
   : infixType
   | FM_ImplicitToken14  ((fm_paramType | paramType) (FM_ImplicitToken15  (fm_paramType | paramType))*)? FM_ImplicitToken16 
   ;
fm_functionArgTypes: FM_PLACEHOLDER | FM_IF (fm_functionArgTypes | functionArgTypes) (FM_ELSE_IF (fm_functionArgTypes | functionArgTypes))* FM_ELSE (fm_functionArgTypes | functionArgTypes) FM_IF_CLOSE;

existentialClause
   : FM_ImplicitToken17  FM_ImplicitToken18  (fm_existentialDclPlus | existentialDcl)+ FM_ImplicitToken19 
   ;
fm_existentialClauseOpt: FM_PLACEHOLDER | FM_IF (fm_existentialClauseOpt | existentialClause)? (FM_ELSE_IF (fm_existentialClauseOpt | existentialClause)?)* (FM_ELSE (fm_existentialClauseOpt | existentialClause)?)? FM_IF_CLOSE;

existentialDcl
   : FM_ImplicitToken20  (fm_typeDcl | typeDcl)
   | FM_ImplicitToken21  (fm_valDcl | valDcl)
   ;
fm_existentialDclPlus: FM_PLACEHOLDER | (FM_IF (fm_existentialDclPlus | existentialDcl)* (FM_ELSE_IF (fm_existentialDclPlus | existentialDcl)*)* (FM_ELSE (fm_existentialDclPlus | existentialDcl)*)? FM_IF_CLOSE | FM_LIST (fm_existentialDclPlus | existentialDcl)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_existentialDclPlus | existentialDcl)* (FM_ELSE_IF (fm_existentialDclPlus | existentialDcl)*)* FM_ELSE (fm_existentialDclPlus | existentialDcl)* FM_IF_CLOSE | FM_LIST (fm_existentialDclPlus | existentialDcl)* FM_ELSE (fm_existentialDclPlus | existentialDcl)* FM_LIST_CLOSE) (FM_IF (fm_existentialDclPlus | existentialDcl)* (FM_ELSE_IF (fm_existentialDclPlus | existentialDcl)*)* (FM_ELSE (fm_existentialDclPlus | existentialDcl)*)? FM_IF_CLOSE | FM_LIST (fm_existentialDclPlus | existentialDcl)* FM_LIST_CLOSE)*;

infixType
   : (fm_compoundType | compoundType) ((Id) (fm_compoundType | compoundType))*
   ;
fm_infixType: FM_PLACEHOLDER | FM_IF (fm_infixType | infixType) (FM_ELSE_IF (fm_infixType | infixType))* FM_ELSE (fm_infixType | infixType) FM_IF_CLOSE;

compoundType
   : (fm_annotType | annotType) (FM_ImplicitToken22  (fm_annotType | annotType))* (fm_refinementOpt | refinement)?
   | refinement
   ;
fm_compoundType: FM_PLACEHOLDER | FM_IF (fm_compoundType | compoundType) (FM_ELSE_IF (fm_compoundType | compoundType))* FM_ELSE (fm_compoundType | compoundType) FM_IF_CLOSE;

annotType
   : (fm_simpleType | simpleType) (fm_annotationStar | annotation)*
   ;
fm_annotType: FM_PLACEHOLDER | FM_IF (fm_annotType | annotType) (FM_ELSE_IF (fm_annotType | annotType))* FM_ELSE (fm_annotType | annotType) FM_IF_CLOSE;

simpleType
   : fm_simpleType ( fm_typeArgs | typeArgs ) 
  | simpleType (fm_typeArgs | typeArgs)
   | fm_simpleType FM_ImplicitToken23 ( Id ) 
  | simpleType FM_ImplicitToken23  (Id)
   | (fm_stableId | stableId) (FM_ImplicitToken24  FM_ImplicitToken25 )?
   | FM_ImplicitToken26  (fm_types | types) FM_ImplicitToken27 
   ;
fm_simpleType: FM_PLACEHOLDER | FM_IF (fm_simpleType | simpleType) (FM_ELSE_IF (fm_simpleType | simpleType))* FM_ELSE (fm_simpleType | simpleType) FM_IF_CLOSE;

typeArgs
   : FM_ImplicitToken28  (fm_types | types) FM_ImplicitToken29 
   ;
fm_typeArgs: FM_PLACEHOLDER | FM_IF (fm_typeArgs | typeArgs) (FM_ELSE_IF (fm_typeArgs | typeArgs))* FM_ELSE (fm_typeArgs | typeArgs) FM_IF_CLOSE;

types
   : (fm_type_ | type_) (FM_ImplicitToken30  (fm_type_ | type_))*
   ;
fm_types: FM_PLACEHOLDER | FM_IF (fm_types | types) (FM_ELSE_IF (fm_types | types))* FM_ELSE (fm_types | types) FM_IF_CLOSE;

refinement
   : (NL)? FM_ImplicitToken31  (fm_refineStatPlus | refineStat)+ FM_ImplicitToken32 
   ;
fm_refinementOpt: FM_PLACEHOLDER | FM_IF (fm_refinementOpt | refinement)? (FM_ELSE_IF (fm_refinementOpt | refinement)?)* (FM_ELSE (fm_refinementOpt | refinement)?)? FM_IF_CLOSE;

refineStat
   : dcl
   | FM_ImplicitToken33  (fm_typeDef | typeDef)
   ;
fm_refineStatPlus: FM_PLACEHOLDER | (FM_IF (fm_refineStatPlus | refineStat)* (FM_ELSE_IF (fm_refineStatPlus | refineStat)*)* (FM_ELSE (fm_refineStatPlus | refineStat)*)? FM_IF_CLOSE | FM_LIST (fm_refineStatPlus | refineStat)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_refineStatPlus | refineStat)* (FM_ELSE_IF (fm_refineStatPlus | refineStat)*)* FM_ELSE (fm_refineStatPlus | refineStat)* FM_IF_CLOSE | FM_LIST (fm_refineStatPlus | refineStat)* FM_ELSE (fm_refineStatPlus | refineStat)* FM_LIST_CLOSE) (FM_IF (fm_refineStatPlus | refineStat)* (FM_ELSE_IF (fm_refineStatPlus | refineStat)*)* (FM_ELSE (fm_refineStatPlus | refineStat)*)? FM_IF_CLOSE | FM_LIST (fm_refineStatPlus | refineStat)* FM_LIST_CLOSE)*;

typePat
   : type_
   ;
fm_typePat: FM_PLACEHOLDER | FM_IF (fm_typePat | typePat) (FM_ELSE_IF (fm_typePat | typePat))* FM_ELSE (fm_typePat | typePat) FM_IF_CLOSE;

ascription
   : FM_ImplicitToken34  (fm_infixType | infixType)
   | FM_ImplicitToken35  (fm_annotationPlus | annotation) +
   | FM_ImplicitToken36  FM_ImplicitToken37  FM_ImplicitToken38 
   ;
fm_ascriptionOpt: FM_PLACEHOLDER | FM_IF (fm_ascriptionOpt | ascription)? (FM_ELSE_IF (fm_ascriptionOpt | ascription)?)* (FM_ELSE (fm_ascriptionOpt | ascription)?)? FM_IF_CLOSE;

expr
   : (bindings | FM_ImplicitToken39 ? (Id) | FM_ImplicitToken40 ) FM_ImplicitToken41  (fm_expr | expr)
   | expr1
   ;
fm_expr: FM_PLACEHOLDER | FM_IF (fm_expr | expr) (FM_ELSE_IF (fm_expr | expr))* FM_ELSE (fm_expr | expr) FM_IF_CLOSE;
fm_exprOpt: FM_PLACEHOLDER | FM_IF (fm_exprOpt | expr)? (FM_ELSE_IF (fm_exprOpt | expr)?)* (FM_ELSE (fm_exprOpt | expr)?)? FM_IF_CLOSE;

expr1
   : FM_ImplicitToken42  FM_ImplicitToken43  (fm_expr | expr) FM_ImplicitToken44  (NL)* (fm_expr | expr) (FM_ImplicitToken45  (fm_expr | expr))?
   | FM_ImplicitToken46  FM_ImplicitToken47  (fm_expr | expr) FM_ImplicitToken48  (NL)* (fm_expr | expr)
   | FM_ImplicitToken49  (fm_expr | expr) (FM_ImplicitToken50  (fm_expr | expr))? (FM_ImplicitToken51  (fm_expr | expr))?
   | FM_ImplicitToken52  (fm_expr | expr) FM_ImplicitToken53  FM_ImplicitToken54  (fm_expr | expr) FM_ImplicitToken55 
   | FM_ImplicitToken56  (FM_ImplicitToken57  (fm_enumerators | enumerators) FM_ImplicitToken58  | FM_ImplicitToken59  (fm_enumerators | enumerators) FM_ImplicitToken60 ) FM_ImplicitToken61 ? (fm_expr | expr)
   | FM_ImplicitToken62  (fm_expr | expr)
   | FM_ImplicitToken63  (fm_exprOpt | expr)?
   | ((simpleExpr | (fm_simpleExpr1 | simpleExpr1) FM_ImplicitToken64 ?) FM_ImplicitToken65 )? (Id) FM_ImplicitToken66  (fm_expr | expr)
   | (fm_simpleExpr1 | simpleExpr1) (fm_argumentExprs | argumentExprs) FM_ImplicitToken67  (fm_expr | expr)
   | (fm_postfixExpr | postfixExpr) (fm_ascriptionOpt | ascription)?
   | (fm_postfixExpr | postfixExpr) FM_ImplicitToken68  FM_ImplicitToken69  (fm_caseClauses | caseClauses) FM_ImplicitToken70 
   ;

prefixDef
   : FM_ImplicitToken71  | FM_ImplicitToken72  | FM_ImplicitToken73  | FM_ImplicitToken74 
   ;
fm_prefixDef: FM_PLACEHOLDER | FM_IF (fm_prefixDef | prefixDef) (FM_ELSE_IF (fm_prefixDef | prefixDef))* FM_ELSE (fm_prefixDef | prefixDef) FM_IF_CLOSE;
fm_prefixDefOpt: FM_PLACEHOLDER | FM_IF (fm_prefixDefOpt | prefixDef)? (FM_ELSE_IF (fm_prefixDefOpt | prefixDef)?)* (FM_ELSE (fm_prefixDefOpt | prefixDef)?)? FM_IF_CLOSE;

postfixExpr
   : (fm_infixExpr | infixExpr) (Id)? ((fm_prefixDef | prefixDef) (fm_simpleExpr1 | simpleExpr1))* (NL)?
   ;
fm_postfixExpr: FM_PLACEHOLDER | FM_IF (fm_postfixExpr | postfixExpr) (FM_ELSE_IF (fm_postfixExpr | postfixExpr))* FM_ELSE (fm_postfixExpr | postfixExpr) FM_IF_CLOSE;

infixExpr
   : prefixExpr
   | fm_infixExpr ( Id ) ( NL ) ? ( fm_infixExpr | infixExpr ) 
  | infixExpr (Id) (NL)? (fm_infixExpr | infixExpr)
   ;
fm_infixExpr: FM_PLACEHOLDER | FM_IF (fm_infixExpr | infixExpr) (FM_ELSE_IF (fm_infixExpr | infixExpr))* FM_ELSE (fm_infixExpr | infixExpr) FM_IF_CLOSE;

prefixExpr
   : (fm_prefixDefOpt | prefixDef)? (simpleExpr | (fm_simpleExpr1 | simpleExpr1) FM_ImplicitToken75 ?)
   ;

simpleExpr
   : FM_ImplicitToken76  (classTemplate | templateBody)
   | blockExpr
   ;
fm_simpleExpr: FM_PLACEHOLDER | FM_IF (fm_simpleExpr | simpleExpr) (FM_ELSE_IF (fm_simpleExpr | simpleExpr))* FM_ELSE (fm_simpleExpr | simpleExpr) FM_IF_CLOSE;

// Dublicate lines to prevent left-recursive code.
// can't use (simpleExpr|simpleExpr1) '.' Id
simpleExpr1
   : literal
   | stableId
   | FM_ImplicitToken77 
   | FM_ImplicitToken78  (fm_exprsOpt | exprs)? FM_ImplicitToken79 
   | (fm_simpleExpr | simpleExpr) FM_ImplicitToken80  (Id)
   | fm_simpleExpr1 FM_ImplicitToken81 ? FM_ImplicitToken82 ( Id ) 
  | simpleExpr1 FM_ImplicitToken81 ?  FM_ImplicitToken82  (Id)
   | (fm_simpleExpr | simpleExpr)  (fm_typeArgs | typeArgs)
   | fm_simpleExpr1 FM_ImplicitToken83 ? ( fm_typeArgs | typeArgs ) 
  | simpleExpr1 FM_ImplicitToken83 ? (fm_typeArgs | typeArgs)
   | fm_simpleExpr1 ( fm_argumentExprs | argumentExprs ) 
  | simpleExpr1 (fm_argumentExprs | argumentExprs)
   ;
fm_simpleExpr1: FM_PLACEHOLDER | FM_IF (fm_simpleExpr1 | simpleExpr1) (FM_ELSE_IF (fm_simpleExpr1 | simpleExpr1))* FM_ELSE (fm_simpleExpr1 | simpleExpr1) FM_IF_CLOSE;

exprs
   : (fm_expr | expr) (FM_ImplicitToken84  (fm_expr | expr))*
   ;
fm_exprs: FM_PLACEHOLDER | FM_IF (fm_exprs | exprs) (FM_ELSE_IF (fm_exprs | exprs))* FM_ELSE (fm_exprs | exprs) FM_IF_CLOSE;
fm_exprsOpt: FM_PLACEHOLDER | FM_IF (fm_exprsOpt | exprs)? (FM_ELSE_IF (fm_exprsOpt | exprs)?)* (FM_ELSE (fm_exprsOpt | exprs)?)? FM_IF_CLOSE;

argumentExprs
    : FM_ImplicitToken85  (fm_args | args) FM_ImplicitToken86 
    | FM_ImplicitToken87  (fm_args | args) FM_ImplicitToken88 
    | (NL)? (fm_blockExpr | blockExpr)
    ;
fm_argumentExprs: FM_PLACEHOLDER | FM_IF (fm_argumentExprs | argumentExprs) (FM_ELSE_IF (fm_argumentExprs | argumentExprs))* FM_ELSE (fm_argumentExprs | argumentExprs) FM_IF_CLOSE;
fm_argumentExprsStar: FM_PLACEHOLDER | FM_IF (fm_argumentExprsStar | argumentExprs)* (FM_ELSE_IF (fm_argumentExprsStar | argumentExprs)*)* (FM_ELSE (fm_argumentExprsStar | argumentExprs)*)? FM_IF_CLOSE | FM_LIST (fm_argumentExprsStar | argumentExprs)* FM_LIST_CLOSE;
fm_argumentExprsPlus: FM_PLACEHOLDER | (FM_IF (fm_argumentExprsPlus | argumentExprs)* (FM_ELSE_IF (fm_argumentExprsPlus | argumentExprs)*)* (FM_ELSE (fm_argumentExprsPlus | argumentExprs)*)? FM_IF_CLOSE | FM_LIST (fm_argumentExprsPlus | argumentExprs)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_argumentExprsPlus | argumentExprs)* (FM_ELSE_IF (fm_argumentExprsPlus | argumentExprs)*)* FM_ELSE (fm_argumentExprsPlus | argumentExprs)* FM_IF_CLOSE | FM_LIST (fm_argumentExprsPlus | argumentExprs)* FM_ELSE (fm_argumentExprsPlus | argumentExprs)* FM_LIST_CLOSE) (FM_IF (fm_argumentExprsPlus | argumentExprs)* (FM_ELSE_IF (fm_argumentExprsPlus | argumentExprs)*)* (FM_ELSE (fm_argumentExprsPlus | argumentExprs)*)? FM_IF_CLOSE | FM_LIST (fm_argumentExprsPlus | argumentExprs)* FM_LIST_CLOSE)*;

args
    : (fm_exprsOpt | exprs)?
    | ((fm_exprs | exprs) FM_ImplicitToken89 )? (fm_postfixExpr | postfixExpr) (FM_ImplicitToken90  | FM_ImplicitToken91  | FM_ImplicitToken92 ) ?
    ;
fm_args: FM_PLACEHOLDER | FM_IF (fm_args | args) (FM_ELSE_IF (fm_args | args))* FM_ELSE (fm_args | args) FM_IF_CLOSE;

blockExpr
   : FM_ImplicitToken93  (fm_caseClauses | caseClauses) FM_ImplicitToken94 
   | FM_ImplicitToken95  (fm_block | block) FM_ImplicitToken96 
   ;
fm_blockExpr: FM_PLACEHOLDER | FM_IF (fm_blockExpr | blockExpr) (FM_ELSE_IF (fm_blockExpr | blockExpr))* FM_ELSE (fm_blockExpr | blockExpr) FM_IF_CLOSE;

block
   : (fm_blockStatPlus | blockStat)+ (fm_resultExprOpt | resultExpr)?
   ;
fm_block: FM_PLACEHOLDER | FM_IF (fm_block | block) (FM_ELSE_IF (fm_block | block))* FM_ELSE (fm_block | block) FM_IF_CLOSE;

blockStat
   : import_
   | (fm_annotationStar | annotation)* (FM_ImplicitToken97  | FM_ImplicitToken98 )? (fm_def_ | def_)
   | (fm_annotationStar | annotation)* (fm_localModifierStar | localModifier)* (fm_tmplDef | tmplDef)
   | expr1
   ;
fm_blockStatPlus: FM_PLACEHOLDER | (FM_IF (fm_blockStatPlus | blockStat)* (FM_ELSE_IF (fm_blockStatPlus | blockStat)*)* (FM_ELSE (fm_blockStatPlus | blockStat)*)? FM_IF_CLOSE | FM_LIST (fm_blockStatPlus | blockStat)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_blockStatPlus | blockStat)* (FM_ELSE_IF (fm_blockStatPlus | blockStat)*)* FM_ELSE (fm_blockStatPlus | blockStat)* FM_IF_CLOSE | FM_LIST (fm_blockStatPlus | blockStat)* FM_ELSE (fm_blockStatPlus | blockStat)* FM_LIST_CLOSE) (FM_IF (fm_blockStatPlus | blockStat)* (FM_ELSE_IF (fm_blockStatPlus | blockStat)*)* (FM_ELSE (fm_blockStatPlus | blockStat)*)? FM_IF_CLOSE | FM_LIST (fm_blockStatPlus | blockStat)* FM_LIST_CLOSE)*;

resultExpr
   : expr1
   | (bindings | (FM_ImplicitToken99 ? (Id) | FM_ImplicitToken100 ) FM_ImplicitToken101  (fm_compoundType | compoundType)) FM_ImplicitToken102  (fm_block | block)
   ;
fm_resultExprOpt: FM_PLACEHOLDER | FM_IF (fm_resultExprOpt | resultExpr)? (FM_ELSE_IF (fm_resultExprOpt | resultExpr)?)* (FM_ELSE (fm_resultExprOpt | resultExpr)?)? FM_IF_CLOSE;

enumerators
   : (fm_generatorPlus | generator)+
   ;
fm_enumerators: FM_PLACEHOLDER | FM_IF (fm_enumerators | enumerators) (FM_ELSE_IF (fm_enumerators | enumerators))* FM_ELSE (fm_enumerators | enumerators) FM_IF_CLOSE;

generator
   : (fm_pattern1 | pattern1) FM_ImplicitToken103  (fm_expr | expr) (guard_ | (fm_pattern1 | pattern1) FM_ImplicitToken104  (fm_expr | expr))*
   ;
fm_generatorPlus: FM_PLACEHOLDER | (FM_IF (fm_generatorPlus | generator)* (FM_ELSE_IF (fm_generatorPlus | generator)*)* (FM_ELSE (fm_generatorPlus | generator)*)? FM_IF_CLOSE | FM_LIST (fm_generatorPlus | generator)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_generatorPlus | generator)* (FM_ELSE_IF (fm_generatorPlus | generator)*)* FM_ELSE (fm_generatorPlus | generator)* FM_IF_CLOSE | FM_LIST (fm_generatorPlus | generator)* FM_ELSE (fm_generatorPlus | generator)* FM_LIST_CLOSE) (FM_IF (fm_generatorPlus | generator)* (FM_ELSE_IF (fm_generatorPlus | generator)*)* (FM_ELSE (fm_generatorPlus | generator)*)? FM_IF_CLOSE | FM_LIST (fm_generatorPlus | generator)* FM_LIST_CLOSE)*;

caseClauses
   : (fm_caseClausePlus | caseClause) +
   ;
fm_caseClauses: FM_PLACEHOLDER | FM_IF (fm_caseClauses | caseClauses) (FM_ELSE_IF (fm_caseClauses | caseClauses))* FM_ELSE (fm_caseClauses | caseClauses) FM_IF_CLOSE;

caseClause
   : FM_ImplicitToken105  (fm_pattern | pattern) (fm_guard_Opt | guard_)? FM_ImplicitToken106  (fm_block | block)
   ;
fm_caseClausePlus: FM_PLACEHOLDER | (FM_IF (fm_caseClausePlus | caseClause)* (FM_ELSE_IF (fm_caseClausePlus | caseClause)*)* (FM_ELSE (fm_caseClausePlus | caseClause)*)? FM_IF_CLOSE | FM_LIST (fm_caseClausePlus | caseClause)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_caseClausePlus | caseClause)* (FM_ELSE_IF (fm_caseClausePlus | caseClause)*)* FM_ELSE (fm_caseClausePlus | caseClause)* FM_IF_CLOSE | FM_LIST (fm_caseClausePlus | caseClause)* FM_ELSE (fm_caseClausePlus | caseClause)* FM_LIST_CLOSE) (FM_IF (fm_caseClausePlus | caseClause)* (FM_ELSE_IF (fm_caseClausePlus | caseClause)*)* (FM_ELSE (fm_caseClausePlus | caseClause)*)? FM_IF_CLOSE | FM_LIST (fm_caseClausePlus | caseClause)* FM_LIST_CLOSE)*;

guard_
   : FM_ImplicitToken107  (fm_postfixExpr | postfixExpr)
   ;
fm_guard_Opt: FM_PLACEHOLDER | FM_IF (fm_guard_Opt | guard_)? (FM_ELSE_IF (fm_guard_Opt | guard_)?)* (FM_ELSE (fm_guard_Opt | guard_)?)? FM_IF_CLOSE;

pattern
   : (fm_pattern1 | pattern1) (FM_ImplicitToken108  (fm_pattern1 | pattern1))*
   ;
fm_pattern: FM_PLACEHOLDER | FM_IF (fm_pattern | pattern) (FM_ELSE_IF (fm_pattern | pattern))* FM_ELSE (fm_pattern | pattern) FM_IF_CLOSE;

pattern1
   : ((BoundVarid)| FM_ImplicitToken109  | (Id)) FM_ImplicitToken110  (fm_typePat | typePat)
   | pattern2
   ;
fm_pattern1: FM_PLACEHOLDER | FM_IF (fm_pattern1 | pattern1) (FM_ELSE_IF (fm_pattern1 | pattern1))* FM_ELSE (fm_pattern1 | pattern1) FM_IF_CLOSE;

pattern2
   : (Id) (FM_ImplicitToken111  (fm_pattern3 | pattern3))?
   | pattern3
   ;
fm_pattern2: FM_PLACEHOLDER | FM_IF (fm_pattern2 | pattern2) (FM_ELSE_IF (fm_pattern2 | pattern2))* FM_ELSE (fm_pattern2 | pattern2) FM_IF_CLOSE;

pattern3
   : simplePattern
   | (fm_simplePattern | simplePattern) ((Id) (NL)? (fm_simplePattern | simplePattern))*
   ;
fm_pattern3: FM_PLACEHOLDER | FM_IF (fm_pattern3 | pattern3) (FM_ELSE_IF (fm_pattern3 | pattern3))* FM_ELSE (fm_pattern3 | pattern3) FM_IF_CLOSE;

simplePattern
   : FM_ImplicitToken112 
   | Varid
   | literal
   | (fm_stableId | stableId) (FM_ImplicitToken113  (fm_patternsOpt | patterns)? FM_ImplicitToken114 )?
   | (fm_stableId | stableId) FM_ImplicitToken115  ((fm_patterns | patterns) FM_ImplicitToken116 )? ((Id) FM_ImplicitToken117 )? FM_ImplicitToken118  FM_ImplicitToken119  FM_ImplicitToken120 
   | FM_ImplicitToken121  (fm_patternsOpt | patterns)? FM_ImplicitToken122 
   ;
fm_simplePattern: FM_PLACEHOLDER | FM_IF (fm_simplePattern | simplePattern) (FM_ELSE_IF (fm_simplePattern | simplePattern))* FM_ELSE (fm_simplePattern | simplePattern) FM_IF_CLOSE;

patterns
   : (fm_pattern | pattern) (FM_ImplicitToken123  (fm_patterns | patterns))?
   | FM_ImplicitToken124  FM_ImplicitToken125 
   ;
fm_patterns: FM_PLACEHOLDER | FM_IF (fm_patterns | patterns) (FM_ELSE_IF (fm_patterns | patterns))* FM_ELSE (fm_patterns | patterns) FM_IF_CLOSE;
fm_patternsOpt: FM_PLACEHOLDER | FM_IF (fm_patternsOpt | patterns)? (FM_ELSE_IF (fm_patternsOpt | patterns)?)* (FM_ELSE (fm_patternsOpt | patterns)?)? FM_IF_CLOSE;

typeParamClause
   : FM_ImplicitToken126  (fm_variantTypeParam | variantTypeParam) (FM_ImplicitToken127  (fm_variantTypeParam | variantTypeParam))* FM_ImplicitToken128 
   ;
fm_typeParamClauseOpt: FM_PLACEHOLDER | FM_IF (fm_typeParamClauseOpt | typeParamClause)? (FM_ELSE_IF (fm_typeParamClauseOpt | typeParamClause)?)* (FM_ELSE (fm_typeParamClauseOpt | typeParamClause)?)? FM_IF_CLOSE;

funTypeParamClause
   : FM_ImplicitToken129  (fm_typeParam | typeParam) (FM_ImplicitToken130  (fm_typeParam | typeParam))* FM_ImplicitToken131 
   ;
fm_funTypeParamClauseOpt: FM_PLACEHOLDER | FM_IF (fm_funTypeParamClauseOpt | funTypeParamClause)? (FM_ELSE_IF (fm_funTypeParamClauseOpt | funTypeParamClause)?)* (FM_ELSE (fm_funTypeParamClauseOpt | funTypeParamClause)?)? FM_IF_CLOSE;

variantTypeParam
   : (fm_annotationStar | annotation)* (FM_ImplicitToken132  | FM_ImplicitToken133 )? (fm_typeParam | typeParam)
   ;
fm_variantTypeParam: FM_PLACEHOLDER | FM_IF (fm_variantTypeParam | variantTypeParam) (FM_ELSE_IF (fm_variantTypeParam | variantTypeParam))* FM_ELSE (fm_variantTypeParam | variantTypeParam) FM_IF_CLOSE;

typeParam
   : ((Id) | FM_ImplicitToken134 ) (fm_typeParamClauseOpt | typeParamClause)? (FM_ImplicitToken135  (fm_type_ | type_))? (FM_ImplicitToken136  (fm_type_ | type_))? (FM_ImplicitToken137  (fm_type_ | type_))* (FM_ImplicitToken138  (fm_type_ | type_))*
   ;
fm_typeParam: FM_PLACEHOLDER | FM_IF (fm_typeParam | typeParam) (FM_ELSE_IF (fm_typeParam | typeParam))* FM_ELSE (fm_typeParam | typeParam) FM_IF_CLOSE;

paramClauses
   : (fm_paramClauseStar | paramClause)* ((NL)? FM_ImplicitToken139  FM_ImplicitToken140  (fm_params | params) FM_ImplicitToken141 )?
   ;
fm_paramClauses: FM_PLACEHOLDER | FM_IF (fm_paramClauses | paramClauses) (FM_ELSE_IF (fm_paramClauses | paramClauses))* FM_ELSE (fm_paramClauses | paramClauses) FM_IF_CLOSE;

paramClause
   : (NL)? FM_ImplicitToken142  (fm_paramsOpt | params)? FM_ImplicitToken143 
   ;
fm_paramClause: FM_PLACEHOLDER | FM_IF (fm_paramClause | paramClause) (FM_ELSE_IF (fm_paramClause | paramClause))* FM_ELSE (fm_paramClause | paramClause) FM_IF_CLOSE;
fm_paramClauseStar: FM_PLACEHOLDER | FM_IF (fm_paramClauseStar | paramClause)* (FM_ELSE_IF (fm_paramClauseStar | paramClause)*)* (FM_ELSE (fm_paramClauseStar | paramClause)*)? FM_IF_CLOSE | FM_LIST (fm_paramClauseStar | paramClause)* FM_LIST_CLOSE;

params
   : (fm_param | param) (FM_ImplicitToken144  (fm_param | param))*
   ;
fm_params: FM_PLACEHOLDER | FM_IF (fm_params | params) (FM_ELSE_IF (fm_params | params))* FM_ELSE (fm_params | params) FM_IF_CLOSE;
fm_paramsOpt: FM_PLACEHOLDER | FM_IF (fm_paramsOpt | params)? (FM_ELSE_IF (fm_paramsOpt | params)?)* (FM_ELSE (fm_paramsOpt | params)?)? FM_IF_CLOSE;

param
   : (fm_annotationStar | annotation)* (Id) (FM_ImplicitToken145  (fm_paramType | paramType))? (FM_ImplicitToken146  (fm_expr | expr))?
   ;
fm_param: FM_PLACEHOLDER | FM_IF (fm_param | param) (FM_ELSE_IF (fm_param | param))* FM_ELSE (fm_param | param) FM_IF_CLOSE;

paramType
   : type_
   | FM_ImplicitToken147  (fm_type_ | type_)
   | (fm_type_ | type_) FM_ImplicitToken148 
   ;
fm_paramType: FM_PLACEHOLDER | FM_IF (fm_paramType | paramType) (FM_ELSE_IF (fm_paramType | paramType))* FM_ELSE (fm_paramType | paramType) FM_IF_CLOSE;

classParamClauses
   : (fm_classParamClauseStar | classParamClause)* ((NL)? FM_ImplicitToken149  FM_ImplicitToken150  (fm_classParams | classParams) FM_ImplicitToken151 )?
   ;
fm_classParamClauses: FM_PLACEHOLDER | FM_IF (fm_classParamClauses | classParamClauses) (FM_ELSE_IF (fm_classParamClauses | classParamClauses))* FM_ELSE (fm_classParamClauses | classParamClauses) FM_IF_CLOSE;

classParamClause
   : (NL)? FM_ImplicitToken152  (fm_classParamsOpt | classParams)? FM_ImplicitToken153 
   ;
fm_classParamClauseStar: FM_PLACEHOLDER | FM_IF (fm_classParamClauseStar | classParamClause)* (FM_ELSE_IF (fm_classParamClauseStar | classParamClause)*)* (FM_ELSE (fm_classParamClauseStar | classParamClause)*)? FM_IF_CLOSE | FM_LIST (fm_classParamClauseStar | classParamClause)* FM_LIST_CLOSE;

classParams
   : (fm_classParam | classParam) (FM_ImplicitToken154  (fm_classParam | classParam))*
   ;
fm_classParams: FM_PLACEHOLDER | FM_IF (fm_classParams | classParams) (FM_ELSE_IF (fm_classParams | classParams))* FM_ELSE (fm_classParams | classParams) FM_IF_CLOSE;
fm_classParamsOpt: FM_PLACEHOLDER | FM_IF (fm_classParamsOpt | classParams)? (FM_ELSE_IF (fm_classParamsOpt | classParams)?)* (FM_ELSE (fm_classParamsOpt | classParams)?)? FM_IF_CLOSE;

classParam
   : (fm_annotationStar | annotation)* (fm_modifierStar | modifier)* (FM_ImplicitToken155  | FM_ImplicitToken156 )? (Id) FM_ImplicitToken157  (fm_paramType | paramType) (FM_ImplicitToken158  (fm_expr | expr))?
   ;
fm_classParam: FM_PLACEHOLDER | FM_IF (fm_classParam | classParam) (FM_ELSE_IF (fm_classParam | classParam))* FM_ELSE (fm_classParam | classParam) FM_IF_CLOSE;

bindings
   : FM_ImplicitToken159  (fm_binding | binding) (FM_ImplicitToken160  (fm_binding | binding))* FM_ImplicitToken161 
   ;

binding
   : ((Id) | FM_ImplicitToken162 ) (FM_ImplicitToken163  (fm_type_ | type_))?
   ;
fm_binding: FM_PLACEHOLDER | FM_IF (fm_binding | binding) (FM_ELSE_IF (fm_binding | binding))* FM_ELSE (fm_binding | binding) FM_IF_CLOSE;

modifier
   : localModifier
   | accessModifier
   | FM_ImplicitToken164 
   ;
fm_modifierStar: FM_PLACEHOLDER | FM_IF (fm_modifierStar | modifier)* (FM_ELSE_IF (fm_modifierStar | modifier)*)* (FM_ELSE (fm_modifierStar | modifier)*)? FM_IF_CLOSE | FM_LIST (fm_modifierStar | modifier)* FM_LIST_CLOSE;

localModifier
   : FM_ImplicitToken165 
   | FM_ImplicitToken166 
   | FM_ImplicitToken167 
   | FM_ImplicitToken168 
   | FM_ImplicitToken169 
   ;
fm_localModifierStar: FM_PLACEHOLDER | FM_IF (fm_localModifierStar | localModifier)* (FM_ELSE_IF (fm_localModifierStar | localModifier)*)* (FM_ELSE (fm_localModifierStar | localModifier)*)? FM_IF_CLOSE | FM_LIST (fm_localModifierStar | localModifier)* FM_LIST_CLOSE;

accessModifier
   : (FM_ImplicitToken170  | FM_ImplicitToken171 ) (fm_accessQualifierOpt | accessQualifier)?
   ;
fm_accessModifierOpt: FM_PLACEHOLDER | FM_IF (fm_accessModifierOpt | accessModifier)? (FM_ELSE_IF (fm_accessModifierOpt | accessModifier)?)* (FM_ELSE (fm_accessModifierOpt | accessModifier)?)? FM_IF_CLOSE;

accessQualifier
   : FM_ImplicitToken172  ((Id) | FM_ImplicitToken173 ) FM_ImplicitToken174 
   ;
fm_accessQualifierOpt: FM_PLACEHOLDER | FM_IF (fm_accessQualifierOpt | accessQualifier)? (FM_ELSE_IF (fm_accessQualifierOpt | accessQualifier)?)* (FM_ELSE (fm_accessQualifierOpt | accessQualifier)?)? FM_IF_CLOSE;

annotation
   : FM_ImplicitToken175  (fm_simpleType | simpleType) (fm_argumentExprsStar | argumentExprs)*
   ;
fm_annotation: FM_PLACEHOLDER | FM_IF (fm_annotation | annotation) (FM_ELSE_IF (fm_annotation | annotation))* FM_ELSE (fm_annotation | annotation) FM_IF_CLOSE;
fm_annotationStar: FM_PLACEHOLDER | FM_IF (fm_annotationStar | annotation)* (FM_ELSE_IF (fm_annotationStar | annotation)*)* (FM_ELSE (fm_annotationStar | annotation)*)? FM_IF_CLOSE | FM_LIST (fm_annotationStar | annotation)* FM_LIST_CLOSE;
fm_annotationPlus: FM_PLACEHOLDER | (FM_IF (fm_annotationPlus | annotation)* (FM_ELSE_IF (fm_annotationPlus | annotation)*)* (FM_ELSE (fm_annotationPlus | annotation)*)? FM_IF_CLOSE | FM_LIST (fm_annotationPlus | annotation)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_annotationPlus | annotation)* (FM_ELSE_IF (fm_annotationPlus | annotation)*)* FM_ELSE (fm_annotationPlus | annotation)* FM_IF_CLOSE | FM_LIST (fm_annotationPlus | annotation)* FM_ELSE (fm_annotationPlus | annotation)* FM_LIST_CLOSE) (FM_IF (fm_annotationPlus | annotation)* (FM_ELSE_IF (fm_annotationPlus | annotation)*)* (FM_ELSE (fm_annotationPlus | annotation)*)? FM_IF_CLOSE | FM_LIST (fm_annotationPlus | annotation)* FM_LIST_CLOSE)*;

constrAnnotation
   : FM_ImplicitToken176  (fm_simpleType | simpleType) (fm_argumentExprs | argumentExprs)
   ;
fm_constrAnnotationStar: FM_PLACEHOLDER | FM_IF (fm_constrAnnotationStar | constrAnnotation)* (FM_ELSE_IF (fm_constrAnnotationStar | constrAnnotation)*)* (FM_ELSE (fm_constrAnnotationStar | constrAnnotation)*)? FM_IF_CLOSE | FM_LIST (fm_constrAnnotationStar | constrAnnotation)* FM_LIST_CLOSE;

templateBody
   : (NL)? FM_ImplicitToken177  (fm_selfTypeOpt | selfType)? (fm_templateStatPlus | templateStat)+ FM_ImplicitToken178 
   ;
fm_templateBody: FM_PLACEHOLDER | FM_IF (fm_templateBody | templateBody) (FM_ELSE_IF (fm_templateBody | templateBody))* FM_ELSE (fm_templateBody | templateBody) FM_IF_CLOSE;
fm_templateBodyOpt: FM_PLACEHOLDER | FM_IF (fm_templateBodyOpt | templateBody)? (FM_ELSE_IF (fm_templateBodyOpt | templateBody)?)* (FM_ELSE (fm_templateBodyOpt | templateBody)?)? FM_IF_CLOSE;

templateStat
   : import_
   | ((fm_annotation | annotation) (NL)?)* (fm_modifierStar | modifier)* (fm_def_ | def_)
   | ((fm_annotation | annotation) (NL)?)* (fm_modifierStar | modifier)* (fm_dcl | dcl)
   | expr
   ;
fm_templateStatPlus: FM_PLACEHOLDER | (FM_IF (fm_templateStatPlus | templateStat)* (FM_ELSE_IF (fm_templateStatPlus | templateStat)*)* (FM_ELSE (fm_templateStatPlus | templateStat)*)? FM_IF_CLOSE | FM_LIST (fm_templateStatPlus | templateStat)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_templateStatPlus | templateStat)* (FM_ELSE_IF (fm_templateStatPlus | templateStat)*)* FM_ELSE (fm_templateStatPlus | templateStat)* FM_IF_CLOSE | FM_LIST (fm_templateStatPlus | templateStat)* FM_ELSE (fm_templateStatPlus | templateStat)* FM_LIST_CLOSE) (FM_IF (fm_templateStatPlus | templateStat)* (FM_ELSE_IF (fm_templateStatPlus | templateStat)*)* (FM_ELSE (fm_templateStatPlus | templateStat)*)? FM_IF_CLOSE | FM_LIST (fm_templateStatPlus | templateStat)* FM_LIST_CLOSE)*;

selfType
   : (Id) (FM_ImplicitToken179  (fm_type_ | type_))? FM_ImplicitToken180 
   | FM_ImplicitToken181  FM_ImplicitToken182  (fm_type_ | type_) FM_ImplicitToken183 
   ;
fm_selfTypeOpt: FM_PLACEHOLDER | FM_IF (fm_selfTypeOpt | selfType)? (FM_ELSE_IF (fm_selfTypeOpt | selfType)?)* (FM_ELSE (fm_selfTypeOpt | selfType)?)? FM_IF_CLOSE;

import_
   : FM_ImplicitToken184  (fm_importExpr | importExpr) (FM_ImplicitToken185  (fm_importExpr | importExpr))*
   ;

importExpr
   : (fm_stableId | stableId) (FM_ImplicitToken186  ((Id) | FM_ImplicitToken187  | importSelectors))?
   ;
fm_importExpr: FM_PLACEHOLDER | FM_IF (fm_importExpr | importExpr) (FM_ELSE_IF (fm_importExpr | importExpr))* FM_ELSE (fm_importExpr | importExpr) FM_IF_CLOSE;

importSelectors
   : FM_ImplicitToken188  ((fm_importSelector | importSelector) FM_ImplicitToken189 )* (importSelector | FM_ImplicitToken190 ) FM_ImplicitToken191 
   ;

importSelector
   : (Id) (FM_ImplicitToken192  ((Id) | FM_ImplicitToken193 ))?
   ;
fm_importSelector: FM_PLACEHOLDER | FM_IF (fm_importSelector | importSelector) (FM_ELSE_IF (fm_importSelector | importSelector))* FM_ELSE (fm_importSelector | importSelector) FM_IF_CLOSE;

dcl
   : FM_ImplicitToken194  (fm_valDcl | valDcl)
   | FM_ImplicitToken195  (fm_varDcl | varDcl)
   | FM_ImplicitToken196  (fm_funDcl | funDcl)
   | FM_ImplicitToken197  (NL)* (fm_typeDcl | typeDcl)
   ;
fm_dcl: FM_PLACEHOLDER | FM_IF (fm_dcl | dcl) (FM_ELSE_IF (fm_dcl | dcl))* FM_ELSE (fm_dcl | dcl) FM_IF_CLOSE;

valDcl
   : (fm_ids | ids) FM_ImplicitToken198  (fm_type_ | type_)
   ;
fm_valDcl: FM_PLACEHOLDER | FM_IF (fm_valDcl | valDcl) (FM_ELSE_IF (fm_valDcl | valDcl))* FM_ELSE (fm_valDcl | valDcl) FM_IF_CLOSE;

varDcl
   : (fm_ids | ids) FM_ImplicitToken199  (fm_type_ | type_)
   ;
fm_varDcl: FM_PLACEHOLDER | FM_IF (fm_varDcl | varDcl) (FM_ELSE_IF (fm_varDcl | varDcl))* FM_ELSE (fm_varDcl | varDcl) FM_IF_CLOSE;

funDcl
   : (fm_funSig | funSig) (FM_ImplicitToken200  (fm_type_ | type_))?
   ;
fm_funDcl: FM_PLACEHOLDER | FM_IF (fm_funDcl | funDcl) (FM_ELSE_IF (fm_funDcl | funDcl))* FM_ELSE (fm_funDcl | funDcl) FM_IF_CLOSE;

funSig
   : (Id) (fm_funTypeParamClauseOpt | funTypeParamClause)? (fm_paramClauses | paramClauses)
   ;
fm_funSig: FM_PLACEHOLDER | FM_IF (fm_funSig | funSig) (FM_ELSE_IF (fm_funSig | funSig))* FM_ELSE (fm_funSig | funSig) FM_IF_CLOSE;

typeDcl
   : (Id) (fm_typeParamClauseOpt | typeParamClause)? (FM_ImplicitToken201  (fm_type_ | type_))? (FM_ImplicitToken202  (fm_type_ | type_))?
   ;
fm_typeDcl: FM_PLACEHOLDER | FM_IF (fm_typeDcl | typeDcl) (FM_ELSE_IF (fm_typeDcl | typeDcl))* FM_ELSE (fm_typeDcl | typeDcl) FM_IF_CLOSE;

patVarDef
   : FM_ImplicitToken203  (fm_patDef | patDef)
   | FM_ImplicitToken204  (fm_varDef | varDef)
   ;
fm_patVarDef: FM_PLACEHOLDER | FM_IF (fm_patVarDef | patVarDef) (FM_ELSE_IF (fm_patVarDef | patVarDef))* FM_ELSE (fm_patVarDef | patVarDef) FM_IF_CLOSE;

def_
   : patVarDef
   | FM_ImplicitToken205  (fm_funDef | funDef)
   | FM_ImplicitToken206  (NL)* (fm_typeDef | typeDef)
   | tmplDef
   ;
fm_def_: FM_PLACEHOLDER | FM_IF (fm_def_ | def_) (FM_ELSE_IF (fm_def_ | def_))* FM_ELSE (fm_def_ | def_) FM_IF_CLOSE;

patDef
   : (fm_pattern2 | pattern2) (FM_ImplicitToken207  (fm_pattern2 | pattern2))* (FM_ImplicitToken208  (fm_type_ | type_))? FM_ImplicitToken209  (fm_expr | expr)
   ;
fm_patDef: FM_PLACEHOLDER | FM_IF (fm_patDef | patDef) (FM_ELSE_IF (fm_patDef | patDef))* FM_ELSE (fm_patDef | patDef) FM_IF_CLOSE;

varDef
   : patDef
   | (fm_ids | ids) FM_ImplicitToken210  (fm_type_ | type_) FM_ImplicitToken211  FM_ImplicitToken212 
   ;
fm_varDef: FM_PLACEHOLDER | FM_IF (fm_varDef | varDef) (FM_ELSE_IF (fm_varDef | varDef))* FM_ELSE (fm_varDef | varDef) FM_IF_CLOSE;

funDef
   : (fm_funSig | funSig) (FM_ImplicitToken213  (fm_type_ | type_))? FM_ImplicitToken214  (fm_expr | expr)
   | (fm_funSig | funSig) (NL)? FM_ImplicitToken215  (fm_block | block) FM_ImplicitToken216 
   | FM_ImplicitToken217  (fm_paramClause | paramClause) (fm_paramClauses | paramClauses) (FM_ImplicitToken218  (fm_constrExpr | constrExpr) | (NL)? (fm_constrBlock | constrBlock))
   ;
fm_funDef: FM_PLACEHOLDER | FM_IF (fm_funDef | funDef) (FM_ELSE_IF (fm_funDef | funDef))* FM_ELSE (fm_funDef | funDef) FM_IF_CLOSE;

typeDef
   : (Id) (fm_typeParamClauseOpt | typeParamClause)? FM_ImplicitToken219  (fm_type_ | type_)
   ;
fm_typeDef: FM_PLACEHOLDER | FM_IF (fm_typeDef | typeDef) (FM_ELSE_IF (fm_typeDef | typeDef))* FM_ELSE (fm_typeDef | typeDef) FM_IF_CLOSE;

tmplDef
   : FM_ImplicitToken220 ? FM_ImplicitToken221  (fm_classDef | classDef)
   | FM_ImplicitToken222 ? FM_ImplicitToken223  (fm_objectDef | objectDef)
   | FM_ImplicitToken224  (fm_traitDef | traitDef)
   ;
fm_tmplDef: FM_PLACEHOLDER | FM_IF (fm_tmplDef | tmplDef) (FM_ELSE_IF (fm_tmplDef | tmplDef))* FM_ELSE (fm_tmplDef | tmplDef) FM_IF_CLOSE;

classDef
   : (Id) (fm_typeParamClauseOpt | typeParamClause)? (fm_constrAnnotationStar | constrAnnotation)* (fm_accessModifierOpt | accessModifier)? (fm_classParamClauses | classParamClauses) (fm_classTemplateOptional | classTemplateOptional)
   ;
fm_classDef: FM_PLACEHOLDER | FM_IF (fm_classDef | classDef) (FM_ELSE_IF (fm_classDef | classDef))* FM_ELSE (fm_classDef | classDef) FM_IF_CLOSE;

traitDef
   : (Id) (fm_typeParamClauseOpt | typeParamClause)? (fm_traitTemplateOptional | traitTemplateOptional)
   ;
fm_traitDef: FM_PLACEHOLDER | FM_IF (fm_traitDef | traitDef) (FM_ELSE_IF (fm_traitDef | traitDef))* FM_ELSE (fm_traitDef | traitDef) FM_IF_CLOSE;

objectDef
   : (Id) (fm_classTemplateOptional | classTemplateOptional)
   ;
fm_objectDef: FM_PLACEHOLDER | FM_IF (fm_objectDef | objectDef) (FM_ELSE_IF (fm_objectDef | objectDef))* FM_ELSE (fm_objectDef | objectDef) FM_IF_CLOSE;

classTemplateOptional
   : FM_ImplicitToken225  (fm_classTemplate | classTemplate)
   | (FM_ImplicitToken226 ? (fm_templateBody | templateBody))?
   ;
fm_classTemplateOptional: FM_PLACEHOLDER | FM_IF (fm_classTemplateOptional | classTemplateOptional) (FM_ELSE_IF (fm_classTemplateOptional | classTemplateOptional))* FM_ELSE (fm_classTemplateOptional | classTemplateOptional) FM_IF_CLOSE;

traitTemplateOptional
   : FM_ImplicitToken227  (fm_traitTemplate | traitTemplate)
   | (FM_ImplicitToken228 ? (fm_templateBody | templateBody))?
   ;
fm_traitTemplateOptional: FM_PLACEHOLDER | FM_IF (fm_traitTemplateOptional | traitTemplateOptional) (FM_ELSE_IF (fm_traitTemplateOptional | traitTemplateOptional))* FM_ELSE (fm_traitTemplateOptional | traitTemplateOptional) FM_IF_CLOSE;

classTemplate
   : (fm_earlyDefsOpt | earlyDefs)? (fm_classParents | classParents) (fm_templateBodyOpt | templateBody)?
   ;
fm_classTemplate: FM_PLACEHOLDER | FM_IF (fm_classTemplate | classTemplate) (FM_ELSE_IF (fm_classTemplate | classTemplate))* FM_ELSE (fm_classTemplate | classTemplate) FM_IF_CLOSE;

traitTemplate
   : (fm_earlyDefsOpt | earlyDefs)? (fm_traitParents | traitParents) (fm_templateBodyOpt | templateBody)?
   ;
fm_traitTemplate: FM_PLACEHOLDER | FM_IF (fm_traitTemplate | traitTemplate) (FM_ELSE_IF (fm_traitTemplate | traitTemplate))* FM_ELSE (fm_traitTemplate | traitTemplate) FM_IF_CLOSE;

classParents
   : (fm_constr | constr) (FM_ImplicitToken229  (fm_annotType | annotType))*
   ;
fm_classParents: FM_PLACEHOLDER | FM_IF (fm_classParents | classParents) (FM_ELSE_IF (fm_classParents | classParents))* FM_ELSE (fm_classParents | classParents) FM_IF_CLOSE;

traitParents
   : (fm_annotType | annotType) (FM_ImplicitToken230  (fm_annotType | annotType))*
   ;
fm_traitParents: FM_PLACEHOLDER | FM_IF (fm_traitParents | traitParents) (FM_ELSE_IF (fm_traitParents | traitParents))* FM_ELSE (fm_traitParents | traitParents) FM_IF_CLOSE;

constr
   : (fm_annotType | annotType) (fm_argumentExprsStar | argumentExprs)*
   ;
fm_constr: FM_PLACEHOLDER | FM_IF (fm_constr | constr) (FM_ELSE_IF (fm_constr | constr))* FM_ELSE (fm_constr | constr) FM_IF_CLOSE;

earlyDefs
   : FM_ImplicitToken231  (fm_earlyDefPlus | earlyDef)+ FM_ImplicitToken232  FM_ImplicitToken233 
   ;
fm_earlyDefsOpt: FM_PLACEHOLDER | FM_IF (fm_earlyDefsOpt | earlyDefs)? (FM_ELSE_IF (fm_earlyDefsOpt | earlyDefs)?)* (FM_ELSE (fm_earlyDefsOpt | earlyDefs)?)? FM_IF_CLOSE;

earlyDef
   : ((fm_annotation | annotation) (NL)?)* (fm_modifierStar | modifier)* (fm_patVarDef | patVarDef)
   ;
fm_earlyDefPlus: FM_PLACEHOLDER | (FM_IF (fm_earlyDefPlus | earlyDef)* (FM_ELSE_IF (fm_earlyDefPlus | earlyDef)*)* (FM_ELSE (fm_earlyDefPlus | earlyDef)*)? FM_IF_CLOSE | FM_LIST (fm_earlyDefPlus | earlyDef)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_earlyDefPlus | earlyDef)* (FM_ELSE_IF (fm_earlyDefPlus | earlyDef)*)* FM_ELSE (fm_earlyDefPlus | earlyDef)* FM_IF_CLOSE | FM_LIST (fm_earlyDefPlus | earlyDef)* FM_ELSE (fm_earlyDefPlus | earlyDef)* FM_LIST_CLOSE) (FM_IF (fm_earlyDefPlus | earlyDef)* (FM_ELSE_IF (fm_earlyDefPlus | earlyDef)*)* (FM_ELSE (fm_earlyDefPlus | earlyDef)*)? FM_IF_CLOSE | FM_LIST (fm_earlyDefPlus | earlyDef)* FM_LIST_CLOSE)*;

constrExpr
   : selfInvocation
   | constrBlock
   ;
fm_constrExpr: FM_PLACEHOLDER | FM_IF (fm_constrExpr | constrExpr) (FM_ELSE_IF (fm_constrExpr | constrExpr))* FM_ELSE (fm_constrExpr | constrExpr) FM_IF_CLOSE;

constrBlock
   : FM_ImplicitToken234  (fm_selfInvocation | selfInvocation) (blockStat)* FM_ImplicitToken235 
   ;
fm_constrBlock: FM_PLACEHOLDER | FM_IF (fm_constrBlock | constrBlock) (FM_ELSE_IF (fm_constrBlock | constrBlock))* FM_ELSE (fm_constrBlock | constrBlock) FM_IF_CLOSE;

selfInvocation
   : FM_ImplicitToken236  (fm_argumentExprsPlus | argumentExprs) +
   ;
fm_selfInvocation: FM_PLACEHOLDER | FM_IF (fm_selfInvocation | selfInvocation) (FM_ELSE_IF (fm_selfInvocation | selfInvocation))* FM_ELSE (fm_selfInvocation | selfInvocation) FM_IF_CLOSE;

topStatSeq
   : (fm_topStatPlus | topStat)+
   ;
fm_topStatSeq: FM_PLACEHOLDER | FM_IF (fm_topStatSeq | topStatSeq) (FM_ELSE_IF (fm_topStatSeq | topStatSeq))* FM_ELSE (fm_topStatSeq | topStatSeq) FM_IF_CLOSE;

topStat
   : ((fm_annotation | annotation) (NL)?)* (fm_modifierStar | modifier)* (fm_tmplDef | tmplDef)
   | import_
   | packaging
   | packageObject
   ;
fm_topStatPlus: FM_PLACEHOLDER | (FM_IF (fm_topStatPlus | topStat)* (FM_ELSE_IF (fm_topStatPlus | topStat)*)* (FM_ELSE (fm_topStatPlus | topStat)*)? FM_IF_CLOSE | FM_LIST (fm_topStatPlus | topStat)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_topStatPlus | topStat)* (FM_ELSE_IF (fm_topStatPlus | topStat)*)* FM_ELSE (fm_topStatPlus | topStat)* FM_IF_CLOSE | FM_LIST (fm_topStatPlus | topStat)* FM_ELSE (fm_topStatPlus | topStat)* FM_LIST_CLOSE) (FM_IF (fm_topStatPlus | topStat)* (FM_ELSE_IF (fm_topStatPlus | topStat)*)* (FM_ELSE (fm_topStatPlus | topStat)*)? FM_IF_CLOSE | FM_LIST (fm_topStatPlus | topStat)* FM_LIST_CLOSE)*;

packaging
   : FM_ImplicitToken237  (fm_qualId | qualId)  (NL)? FM_ImplicitToken238  (fm_topStatSeq | topStatSeq) FM_ImplicitToken239 
   ;

packageObject
   : FM_ImplicitToken240  FM_ImplicitToken241  (fm_objectDef | objectDef)
   ;


compilationUnit
   : (FM_ImplicitToken242  (fm_qualId | qualId))* (fm_topStatSeq | topStatSeq)
   ;

// Lexer


Id
   : Plainid
   | '`' (CharNoBackQuoteOrNewline | UnicodeEscape | CharEscapeSeq )+ '`'
   ;


BooleanLiteral
   : 'true' | 'false'
   ;


CharacterLiteral
   : '\'' (PrintableChar | CharEscapeSeq) '\''
   ;


SymbolLiteral
   : '\'' Plainid
   ;


IntegerLiteral
   : (DecimalNumeral | HexNumeral) ('L' | 'l')?
   ;


StringLiteral
   : '"' StringElement* '"' | '"""' MultiLineChars '"""'
   ;

FloatingPointLiteral
   : Digit + '.' Digit + ExponentPart? FloatType? | '.' Digit + ExponentPart? FloatType? | Digit ExponentPart FloatType? | Digit + ExponentPart? FloatType
   ;


Varid
   : Lower Idrest
   ;

BoundVarid
   : Varid
   | '`' Varid '`'
   ;
Paren
   : '(' | ')' | '[' | ']' | '{' | '}'
   ;


Delim
   : '`' | '\'' | '"' | '.' | ';' | ','
   ;

Semi
   : (';' | (NL)+) -> skip
   ;

NL
   : '\n'
   | '\r' '\n'?
   ;


// \u0020-\u0026 """ !"#$%"""
// \u0028-\u007E """()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~"""
fragment CharNoBackQuoteOrNewline
   : [\u0020-\u0026\u0028-\u007E]
   ;

// fragments

fragment UnicodeEscape
   : '\\' 'u' 'u'? HexDigit HexDigit HexDigit HexDigit
   ;


fragment WhiteSpace
   : '\u0020' | '\u0009' | '\u000D' | '\u000A'
   ;


fragment Opchar
   : '!' | '#' | '%' | '&' | '*' | '+' | '-' | ':' | '<' | '=' | '>' | '?' | '@' | '\\' | '^' | '|' | '~'
   ;


fragment Op
   : '/'? Opchar +
   ;


fragment Idrest
   : (Letter | Digit)* ('_' Op)?
   ;


fragment StringElement
   : '\u0020' | '\u0021' | '\u0023' .. '\u007F' | CharEscapeSeq
   ;


fragment MultiLineChars
   : (StringElement | NL) *
   ;


fragment HexDigit
   : '0' .. '9' | 'A' .. 'F' | 'a' .. 'f'
   ;


fragment FloatType
   : 'F' | 'f' | 'D' | 'd'
   ;


fragment Upper
   : 'A' .. 'Z' | '$' | '_' | UnicodeClass_LU
   ;


fragment Lower
   : 'a' .. 'z' | UnicodeClass_LL
   ;


fragment Letter
   : Upper | Lower | UnicodeClass_LO | UnicodeClass_LT // TODO Add category Nl
   ;

// and Unicode categories Lo, Lt, Nl

fragment ExponentPart
   : ('E' | 'e') ('+' | '-')? Digit +
   ;


fragment PrintableChar
   : '\u0020' .. '\u007F'
   ;

fragment PrintableCharExceptWhitespace
   : '\u0021' .. '\u007F'
   ;


fragment CharEscapeSeq
   : '\\' ('b' | 't' | 'n' | 'f' | 'r' | '"' | '\'' | '\\')
   ;


fragment DecimalNumeral
   : '0' | NonZeroDigit Digit*
   ;


fragment HexNumeral
   : '0' 'x' HexDigit HexDigit +
   ;


fragment Digit
   : '0' | NonZeroDigit
   ;


fragment NonZeroDigit
   : '1' .. '9'
   ;


fragment VaridFragment
   : Varid
   ;


fragment Plainid
   : Upper Idrest | Lower Idrest | Op
   ;


//
// Unicode categories
// https://github.com/antlr/grammars-v4/blob/master/stringtemplate/LexUnicode.g4
//

fragment UnicodeLetter
   : UnicodeClass_LU
   | UnicodeClass_LL
   | UnicodeClass_LT
   | UnicodeClass_LM
   | UnicodeClass_LO
   ;

fragment UnicodeClass_LU
   : '\u0041'..'\u005a'
   | '\u00c0'..'\u00d6'
   | '\u00d8'..'\u00de'
   | '\u0100'..'\u0136'
   | '\u0139'..'\u0147'
   | '\u014a'..'\u0178'
   | '\u0179'..'\u017d'
   | '\u0181'..'\u0182'
   | '\u0184'..'\u0186'
   | '\u0187'..'\u0189'
   | '\u018a'..'\u018b'
   | '\u018e'..'\u0191'
   | '\u0193'..'\u0194'
   | '\u0196'..'\u0198'
   | '\u019c'..'\u019d'
   | '\u019f'..'\u01a0'
   | '\u01a2'..'\u01a6'
   | '\u01a7'..'\u01a9'
   | '\u01ac'..'\u01ae'
   | '\u01af'..'\u01b1'
   | '\u01b2'..'\u01b3'
   | '\u01b5'..'\u01b7'
   | '\u01b8'..'\u01bc'
   | '\u01c4'..'\u01cd'
   | '\u01cf'..'\u01db'
   | '\u01de'..'\u01ee'
   | '\u01f1'..'\u01f4'
   | '\u01f6'..'\u01f8'
   | '\u01fa'..'\u0232'
   | '\u023a'..'\u023b'
   | '\u023d'..'\u023e'
   | '\u0241'..'\u0243'
   | '\u0244'..'\u0246'
   | '\u0248'..'\u024e'
   | '\u0370'..'\u0372'
   | '\u0376'..'\u037f'
   | '\u0386'..'\u0388'
   | '\u0389'..'\u038a'
   | '\u038c'..'\u038e'
   | '\u038f'..'\u0391'
   | '\u0392'..'\u03a1'
   | '\u03a3'..'\u03ab'
   | '\u03cf'..'\u03d2'
   | '\u03d3'..'\u03d4'
   | '\u03d8'..'\u03ee'
   | '\u03f4'..'\u03f7'
   | '\u03f9'..'\u03fa'
   | '\u03fd'..'\u042f'
   | '\u0460'..'\u0480'
   | '\u048a'..'\u04c0'
   | '\u04c1'..'\u04cd'
   | '\u04d0'..'\u052e'
   | '\u0531'..'\u0556'
   | '\u10a0'..'\u10c5'
   | '\u10c7'..'\u10cd'
   | '\u1e00'..'\u1e94'
   | '\u1e9e'..'\u1efe'
   | '\u1f08'..'\u1f0f'
   | '\u1f18'..'\u1f1d'
   | '\u1f28'..'\u1f2f'
   | '\u1f38'..'\u1f3f'
   | '\u1f48'..'\u1f4d'
   | '\u1f59'..'\u1f5f'
   | '\u1f68'..'\u1f6f'
   | '\u1fb8'..'\u1fbb'
   | '\u1fc8'..'\u1fcb'
   | '\u1fd8'..'\u1fdb'
   | '\u1fe8'..'\u1fec'
   | '\u1ff8'..'\u1ffb'
   | '\u2102'..'\u2107'
   | '\u210b'..'\u210d'
   | '\u2110'..'\u2112'
   | '\u2115'..'\u2119'
   | '\u211a'..'\u211d'
   | '\u2124'..'\u212a'
   | '\u212b'..'\u212d'
   | '\u2130'..'\u2133'
   | '\u213e'..'\u213f'
   | '\u2145'..'\u2183'
   | '\u2c00'..'\u2c2e'
   | '\u2c60'..'\u2c62'
   | '\u2c63'..'\u2c64'
   | '\u2c67'..'\u2c6d'
   | '\u2c6e'..'\u2c70'
   | '\u2c72'..'\u2c75'
   | '\u2c7e'..'\u2c80'
   | '\u2c82'..'\u2ce2'
   | '\u2ceb'..'\u2ced'
   | '\u2cf2'..'\ua640'
   | '\ua642'..'\ua66c'
   | '\ua680'..'\ua69a'
   | '\ua722'..'\ua72e'
   | '\ua732'..'\ua76e'
   | '\ua779'..'\ua77d'
   | '\ua77e'..'\ua786'
   | '\ua78b'..'\ua78d'
   | '\ua790'..'\ua792'
   | '\ua796'..'\ua7aa'
   | '\ua7ab'..'\ua7ad'
   | '\ua7b0'..'\ua7b1'
   | '\uff21'..'\uff3a'
   ;

fragment UnicodeClass_LL
   : '\u0061'..'\u007A'
   | '\u00b5'..'\u00df'
   | '\u00e0'..'\u00f6'
   | '\u00f8'..'\u00ff'
   | '\u0101'..'\u0137'
   | '\u0138'..'\u0148'
   | '\u0149'..'\u0177'
   | '\u017a'..'\u017e'
   | '\u017f'..'\u0180'
   | '\u0183'..'\u0185'
   | '\u0188'..'\u018c'
   | '\u018d'..'\u0192'
   | '\u0195'..'\u0199'
   | '\u019a'..'\u019b'
   | '\u019e'..'\u01a1'
   | '\u01a3'..'\u01a5'
   | '\u01a8'..'\u01aa'
   | '\u01ab'..'\u01ad'
   | '\u01b0'..'\u01b4'
   | '\u01b6'..'\u01b9'
   | '\u01ba'..'\u01bd'
   | '\u01be'..'\u01bf'
   | '\u01c6'..'\u01cc'
   | '\u01ce'..'\u01dc'
   | '\u01dd'..'\u01ef'
   | '\u01f0'..'\u01f3'
   | '\u01f5'..'\u01f9'
   | '\u01fb'..'\u0233'
   | '\u0234'..'\u0239'
   | '\u023c'..'\u023f'
   | '\u0240'..'\u0242'
   | '\u0247'..'\u024f'
   | '\u0250'..'\u0293'
   | '\u0295'..'\u02af'
   | '\u0371'..'\u0373'
   | '\u0377'..'\u037b'
   | '\u037c'..'\u037d'
   | '\u0390'..'\u03ac'
   | '\u03ad'..'\u03ce'
   | '\u03d0'..'\u03d1'
   | '\u03d5'..'\u03d7'
   | '\u03d9'..'\u03ef'
   | '\u03f0'..'\u03f3'
   | '\u03f5'..'\u03fb'
   | '\u03fc'..'\u0430'
   | '\u0431'..'\u045f'
   | '\u0461'..'\u0481'
   | '\u048b'..'\u04bf'
   | '\u04c2'..'\u04ce'
   | '\u04cf'..'\u052f'
   | '\u0561'..'\u0587'
   | '\u1d00'..'\u1d2b'
   | '\u1d6b'..'\u1d77'
   | '\u1d79'..'\u1d9a'
   | '\u1e01'..'\u1e95'
   | '\u1e96'..'\u1e9d'
   | '\u1e9f'..'\u1eff'
   | '\u1f00'..'\u1f07'
   | '\u1f10'..'\u1f15'
   | '\u1f20'..'\u1f27'
   | '\u1f30'..'\u1f37'
   | '\u1f40'..'\u1f45'
   | '\u1f50'..'\u1f57'
   | '\u1f60'..'\u1f67'
   | '\u1f70'..'\u1f7d'
   | '\u1f80'..'\u1f87'
   | '\u1f90'..'\u1f97'
   | '\u1fa0'..'\u1fa7'
   | '\u1fb0'..'\u1fb4'
   | '\u1fb6'..'\u1fb7'
   | '\u1fbe'..'\u1fc2'
   | '\u1fc3'..'\u1fc4'
   | '\u1fc6'..'\u1fc7'
   | '\u1fd0'..'\u1fd3'
   | '\u1fd6'..'\u1fd7'
   | '\u1fe0'..'\u1fe7'
   | '\u1ff2'..'\u1ff4'
   | '\u1ff6'..'\u1ff7'
   | '\u210a'..'\u210e'
   | '\u210f'..'\u2113'
   | '\u212f'..'\u2139'
   | '\u213c'..'\u213d'
   | '\u2146'..'\u2149'
   | '\u214e'..'\u2184'
   | '\u2c30'..'\u2c5e'
   | '\u2c61'..'\u2c65'
   | '\u2c66'..'\u2c6c'
   | '\u2c71'..'\u2c73'
   | '\u2c74'..'\u2c76'
   | '\u2c77'..'\u2c7b'
   | '\u2c81'..'\u2ce3'
   | '\u2ce4'..'\u2cec'
   | '\u2cee'..'\u2cf3'
   | '\u2d00'..'\u2d25'
   | '\u2d27'..'\u2d2d'
   | '\ua641'..'\ua66d'
   | '\ua681'..'\ua69b'
   | '\ua723'..'\ua72f'
   | '\ua730'..'\ua731'
   | '\ua733'..'\ua771'
   | '\ua772'..'\ua778'
   | '\ua77a'..'\ua77c'
   | '\ua77f'..'\ua787'
   | '\ua78c'..'\ua78e'
   | '\ua791'..'\ua793'
   | '\ua794'..'\ua795'
   | '\ua797'..'\ua7a9'
   | '\ua7fa'..'\uab30'
   | '\uab31'..'\uab5a'
   | '\uab64'..'\uab65'
   | '\ufb00'..'\ufb06'
   | '\ufb13'..'\ufb17'
   | '\uff41'..'\uff5a'
   ;

fragment UnicodeClass_LT
   : '\u01c5'..'\u01cb'
   | '\u01f2'..'\u1f88'
   | '\u1f89'..'\u1f8f'
   | '\u1f98'..'\u1f9f'
   | '\u1fa8'..'\u1faf'
   | '\u1fbc'..'\u1fcc'
   | '\u1ffc'..'\u1ffc'
   ;

fragment UnicodeClass_LM
   : '\u02b0'..'\u02c1'
   | '\u02c6'..'\u02d1'
   | '\u02e0'..'\u02e4'
   | '\u02ec'..'\u02ee'
   | '\u0374'..'\u037a'
   | '\u0559'..'\u0640'
   | '\u06e5'..'\u06e6'
   | '\u07f4'..'\u07f5'
   | '\u07fa'..'\u081a'
   | '\u0824'..'\u0828'
   | '\u0971'..'\u0e46'
   | '\u0ec6'..'\u10fc'
   | '\u17d7'..'\u1843'
   | '\u1aa7'..'\u1c78'
   | '\u1c79'..'\u1c7d'
   | '\u1d2c'..'\u1d6a'
   | '\u1d78'..'\u1d9b'
   | '\u1d9c'..'\u1dbf'
   | '\u2071'..'\u207f'
   | '\u2090'..'\u209c'
   | '\u2c7c'..'\u2c7d'
   | '\u2d6f'..'\u2e2f'
   | '\u3005'..'\u3031'
   | '\u3032'..'\u3035'
   | '\u303b'..'\u309d'
   | '\u309e'..'\u30fc'
   | '\u30fd'..'\u30fe'
   | '\ua015'..'\ua4f8'
   | '\ua4f9'..'\ua4fd'
   | '\ua60c'..'\ua67f'
   | '\ua69c'..'\ua69d'
   | '\ua717'..'\ua71f'
   | '\ua770'..'\ua788'
   | '\ua7f8'..'\ua7f9'
   | '\ua9cf'..'\ua9e6'
   | '\uaa70'..'\uaadd'
   | '\uaaf3'..'\uaaf4'
   | '\uab5c'..'\uab5f'
   | '\uff70'..'\uff9e'
   | '\uff9f'..'\uff9f'
   ;

fragment UnicodeClass_LO
   : '\u00aa'..'\u00ba'
   | '\u01bb'..'\u01c0'
   | '\u01c1'..'\u01c3'
   | '\u0294'..'\u05d0'
   | '\u05d1'..'\u05ea'
   | '\u05f0'..'\u05f2'
   | '\u0620'..'\u063f'
   | '\u0641'..'\u064a'
   | '\u066e'..'\u066f'
   | '\u0671'..'\u06d3'
   | '\u06d5'..'\u06ee'
   | '\u06ef'..'\u06fa'
   | '\u06fb'..'\u06fc'
   | '\u06ff'..'\u0710'
   | '\u0712'..'\u072f'
   | '\u074d'..'\u07a5'
   | '\u07b1'..'\u07ca'
   | '\u07cb'..'\u07ea'
   | '\u0800'..'\u0815'
   | '\u0840'..'\u0858'
   | '\u08a0'..'\u08b2'
   | '\u0904'..'\u0939'
   | '\u093d'..'\u0950'
   | '\u0958'..'\u0961'
   | '\u0972'..'\u0980'
   | '\u0985'..'\u098c'
   | '\u098f'..'\u0990'
   | '\u0993'..'\u09a8'
   | '\u09aa'..'\u09b0'
   | '\u09b2'..'\u09b6'
   | '\u09b7'..'\u09b9'
   | '\u09bd'..'\u09ce'
   | '\u09dc'..'\u09dd'
   | '\u09df'..'\u09e1'
   | '\u09f0'..'\u09f1'
   | '\u0a05'..'\u0a0a'
   | '\u0a0f'..'\u0a10'
   | '\u0a13'..'\u0a28'
   | '\u0a2a'..'\u0a30'
   | '\u0a32'..'\u0a33'
   | '\u0a35'..'\u0a36'
   | '\u0a38'..'\u0a39'
   | '\u0a59'..'\u0a5c'
   | '\u0a5e'..'\u0a72'
   | '\u0a73'..'\u0a74'
   | '\u0a85'..'\u0a8d'
   | '\u0a8f'..'\u0a91'
   | '\u0a93'..'\u0aa8'
   | '\u0aaa'..'\u0ab0'
   | '\u0ab2'..'\u0ab3'
   | '\u0ab5'..'\u0ab9'
   | '\u0abd'..'\u0ad0'
   | '\u0ae0'..'\u0ae1'
   | '\u0b05'..'\u0b0c'
   | '\u0b0f'..'\u0b10'
   | '\u0b13'..'\u0b28'
   | '\u0b2a'..'\u0b30'
   | '\u0b32'..'\u0b33'
   | '\u0b35'..'\u0b39'
   | '\u0b3d'..'\u0b5c'
   | '\u0b5d'..'\u0b5f'
   | '\u0b60'..'\u0b61'
   | '\u0b71'..'\u0b83'
   | '\u0b85'..'\u0b8a'
   | '\u0b8e'..'\u0b90'
   | '\u0b92'..'\u0b95'
   | '\u0b99'..'\u0b9a'
   | '\u0b9c'..'\u0b9e'
   | '\u0b9f'..'\u0ba3'
   | '\u0ba4'..'\u0ba8'
   | '\u0ba9'..'\u0baa'
   | '\u0bae'..'\u0bb9'
   | '\u0bd0'..'\u0c05'
   | '\u0c06'..'\u0c0c'
   | '\u0c0e'..'\u0c10'
   | '\u0c12'..'\u0c28'
   | '\u0c2a'..'\u0c39'
   | '\u0c3d'..'\u0c58'
   | '\u0c59'..'\u0c60'
   | '\u0c61'..'\u0c85'
   | '\u0c86'..'\u0c8c'
   | '\u0c8e'..'\u0c90'
   | '\u0c92'..'\u0ca8'
   | '\u0caa'..'\u0cb3'
   | '\u0cb5'..'\u0cb9'
   | '\u0cbd'..'\u0cde'
   | '\u0ce0'..'\u0ce1'
   | '\u0cf1'..'\u0cf2'
   | '\u0d05'..'\u0d0c'
   | '\u0d0e'..'\u0d10'
   | '\u0d12'..'\u0d3a'
   | '\u0d3d'..'\u0d4e'
   | '\u0d60'..'\u0d61'
   | '\u0d7a'..'\u0d7f'
   | '\u0d85'..'\u0d96'
   | '\u0d9a'..'\u0db1'
   | '\u0db3'..'\u0dbb'
   | '\u0dbd'..'\u0dc0'
   | '\u0dc1'..'\u0dc6'
   | '\u0e01'..'\u0e30'
   | '\u0e32'..'\u0e33'
   | '\u0e40'..'\u0e45'
   | '\u0e81'..'\u0e82'
   | '\u0e84'..'\u0e87'
   | '\u0e88'..'\u0e8a'
   | '\u0e8d'..'\u0e94'
   | '\u0e95'..'\u0e97'
   | '\u0e99'..'\u0e9f'
   | '\u0ea1'..'\u0ea3'
   | '\u0ea5'..'\u0ea7'
   | '\u0eaa'..'\u0eab'
   | '\u0ead'..'\u0eb0'
   | '\u0eb2'..'\u0eb3'
   | '\u0ebd'..'\u0ec0'
   | '\u0ec1'..'\u0ec4'
   | '\u0edc'..'\u0edf'
   | '\u0f00'..'\u0f40'
   | '\u0f41'..'\u0f47'
   | '\u0f49'..'\u0f6c'
   | '\u0f88'..'\u0f8c'
   | '\u1000'..'\u102a'
   | '\u103f'..'\u1050'
   | '\u1051'..'\u1055'
   | '\u105a'..'\u105d'
   | '\u1061'..'\u1065'
   | '\u1066'..'\u106e'
   | '\u106f'..'\u1070'
   | '\u1075'..'\u1081'
   | '\u108e'..'\u10d0'
   | '\u10d1'..'\u10fa'
   | '\u10fd'..'\u1248'
   | '\u124a'..'\u124d'
   | '\u1250'..'\u1256'
   | '\u1258'..'\u125a'
   | '\u125b'..'\u125d'
   | '\u1260'..'\u1288'
   | '\u128a'..'\u128d'
   | '\u1290'..'\u12b0'
   | '\u12b2'..'\u12b5'
   | '\u12b8'..'\u12be'
   | '\u12c0'..'\u12c2'
   | '\u12c3'..'\u12c5'
   | '\u12c8'..'\u12d6'
   | '\u12d8'..'\u1310'
   | '\u1312'..'\u1315'
   | '\u1318'..'\u135a'
   | '\u1380'..'\u138f'
   | '\u13a0'..'\u13f4'
   | '\u1401'..'\u166c'
   | '\u166f'..'\u167f'
   | '\u1681'..'\u169a'
   | '\u16a0'..'\u16ea'
   | '\u16f1'..'\u16f8'
   | '\u1700'..'\u170c'
   | '\u170e'..'\u1711'
   | '\u1720'..'\u1731'
   | '\u1740'..'\u1751'
   | '\u1760'..'\u176c'
   | '\u176e'..'\u1770'
   | '\u1780'..'\u17b3'
   | '\u17dc'..'\u1820'
   | '\u1821'..'\u1842'
   | '\u1844'..'\u1877'
   | '\u1880'..'\u18a8'
   | '\u18aa'..'\u18b0'
   | '\u18b1'..'\u18f5'
   | '\u1900'..'\u191e'
   | '\u1950'..'\u196d'
   | '\u1970'..'\u1974'
   | '\u1980'..'\u19ab'
   | '\u19c1'..'\u19c7'
   | '\u1a00'..'\u1a16'
   | '\u1a20'..'\u1a54'
   | '\u1b05'..'\u1b33'
   | '\u1b45'..'\u1b4b'
   | '\u1b83'..'\u1ba0'
   | '\u1bae'..'\u1baf'
   | '\u1bba'..'\u1be5'
   | '\u1c00'..'\u1c23'
   | '\u1c4d'..'\u1c4f'
   | '\u1c5a'..'\u1c77'
   | '\u1ce9'..'\u1cec'
   | '\u1cee'..'\u1cf1'
   | '\u1cf5'..'\u1cf6'
   | '\u2135'..'\u2138'
   | '\u2d30'..'\u2d67'
   | '\u2d80'..'\u2d96'
   | '\u2da0'..'\u2da6'
   | '\u2da8'..'\u2dae'
   | '\u2db0'..'\u2db6'
   | '\u2db8'..'\u2dbe'
   | '\u2dc0'..'\u2dc6'
   | '\u2dc8'..'\u2dce'
   | '\u2dd0'..'\u2dd6'
   | '\u2dd8'..'\u2dde'
   | '\u3006'..'\u303c'
   | '\u3041'..'\u3096'
   | '\u309f'..'\u30a1'
   | '\u30a2'..'\u30fa'
   | '\u30ff'..'\u3105'
   | '\u3106'..'\u312d'
   | '\u3131'..'\u318e'
   | '\u31a0'..'\u31ba'
   | '\u31f0'..'\u31ff'
   | '\u3400'..'\u4db5'
   | '\u4e00'..'\u9fcc'
   | '\ua000'..'\ua014'
   | '\ua016'..'\ua48c'
   | '\ua4d0'..'\ua4f7'
   | '\ua500'..'\ua60b'
   | '\ua610'..'\ua61f'
   | '\ua62a'..'\ua62b'
   | '\ua66e'..'\ua6a0'
   | '\ua6a1'..'\ua6e5'
   | '\ua7f7'..'\ua7fb'
   | '\ua7fc'..'\ua801'
   | '\ua803'..'\ua805'
   | '\ua807'..'\ua80a'
   | '\ua80c'..'\ua822'
   | '\ua840'..'\ua873'
   | '\ua882'..'\ua8b3'
   | '\ua8f2'..'\ua8f7'
   | '\ua8fb'..'\ua90a'
   | '\ua90b'..'\ua925'
   | '\ua930'..'\ua946'
   | '\ua960'..'\ua97c'
   | '\ua984'..'\ua9b2'
   | '\ua9e0'..'\ua9e4'
   | '\ua9e7'..'\ua9ef'
   | '\ua9fa'..'\ua9fe'
   | '\uaa00'..'\uaa28'
   | '\uaa40'..'\uaa42'
   | '\uaa44'..'\uaa4b'
   | '\uaa60'..'\uaa6f'
   | '\uaa71'..'\uaa76'
   | '\uaa7a'..'\uaa7e'
   | '\uaa7f'..'\uaaaf'
   | '\uaab1'..'\uaab5'
   | '\uaab6'..'\uaab9'
   | '\uaaba'..'\uaabd'
   | '\uaac0'..'\uaac2'
   | '\uaadb'..'\uaadc'
   | '\uaae0'..'\uaaea'
   | '\uaaf2'..'\uab01'
   | '\uab02'..'\uab06'
   | '\uab09'..'\uab0e'
   | '\uab11'..'\uab16'
   | '\uab20'..'\uab26'
   | '\uab28'..'\uab2e'
   | '\uabc0'..'\uabe2'
   | '\uac00'..'\ud7a3'
   | '\ud7b0'..'\ud7c6'
   | '\ud7cb'..'\ud7fb'
   | '\uf900'..'\ufa6d'
   | '\ufa70'..'\ufad9'
   | '\ufb1d'..'\ufb1f'
   | '\ufb20'..'\ufb28'
   | '\ufb2a'..'\ufb36'
   | '\ufb38'..'\ufb3c'
   | '\ufb3e'..'\ufb40'
   | '\ufb41'..'\ufb43'
   | '\ufb44'..'\ufb46'
   | '\ufb47'..'\ufbb1'
   | '\ufbd3'..'\ufd3d'
   | '\ufd50'..'\ufd8f'
   | '\ufd92'..'\ufdc7'
   | '\ufdf0'..'\ufdfb'
   | '\ufe70'..'\ufe74'
   | '\ufe76'..'\ufefc'
   | '\uff66'..'\uff6f'
   | '\uff71'..'\uff9d'
   | '\uffa0'..'\uffbe'
   | '\uffc2'..'\uffc7'
   | '\uffca'..'\uffcf'
   | '\uffd2'..'\uffd7'
   | '\uffda'..'\uffdc'
   ;

fragment UnicodeDigit // UnicodeClass_ND
   : '\u0030'..'\u0039'
   | '\u0660'..'\u0669'
   | '\u06f0'..'\u06f9'
   | '\u07c0'..'\u07c9'
   | '\u0966'..'\u096f'
   | '\u09e6'..'\u09ef'
   | '\u0a66'..'\u0a6f'
   | '\u0ae6'..'\u0aef'
   | '\u0b66'..'\u0b6f'
   | '\u0be6'..'\u0bef'
   | '\u0c66'..'\u0c6f'
   | '\u0ce6'..'\u0cef'
   | '\u0d66'..'\u0d6f'
   | '\u0de6'..'\u0def'
   | '\u0e50'..'\u0e59'
   | '\u0ed0'..'\u0ed9'
   | '\u0f20'..'\u0f29'
   | '\u1040'..'\u1049'
   | '\u1090'..'\u1099'
   | '\u17e0'..'\u17e9'
   | '\u1810'..'\u1819'
   | '\u1946'..'\u194f'
   | '\u19d0'..'\u19d9'
   | '\u1a80'..'\u1a89'
   | '\u1a90'..'\u1a99'
   | '\u1b50'..'\u1b59'
   | '\u1bb0'..'\u1bb9'
   | '\u1c40'..'\u1c49'
   | '\u1c50'..'\u1c59'
   | '\ua620'..'\ua629'
   | '\ua8d0'..'\ua8d9'
   | '\ua900'..'\ua909'
   | '\ua9d0'..'\ua9d9'
   | '\ua9f0'..'\ua9f9'
   | '\uaa50'..'\uaa59'
   | '\uabf0'..'\uabf9'
   | '\uff10'..'\uff19'
   ;


//
// Whitespace and comments
//
NEWLINE
   : NL+ -> skip
   ;

WS
   :  WhiteSpace+ -> skip
   ;

COMMENT
   :   '/*' (COMMENT | .)* '*/' -> skip
   ;


LINE_COMMENT
   :   '//' (~[\r\n])* -> skip
   ;

FM_PLACEHOLDER: '${' ~'}'+? '}';
FM_IF: '<#if' ~'>'+? '>';
FM_IF_CLOSE: '</#if>';
FM_ELSE_IF: '<#elseif' ~'>'+? '>';
FM_ELSE: '<#else>';
FM_LIST: '<#list' .+? 'as' ~'>'+? '>';
FM_LIST_CLOSE: '</#list>';
FM_ImplicitToken1:'-';
FM_ImplicitToken2:'-';
FM_ImplicitToken3:'null';
FM_ImplicitToken4:'.';
FM_ImplicitToken5:',';
FM_ImplicitToken6:'.';
FM_ImplicitToken7:'.';
FM_ImplicitToken8:'this';
FM_ImplicitToken9:'super';
FM_ImplicitToken10:'.';
FM_ImplicitToken11:'[';
FM_ImplicitToken12:']';
FM_ImplicitToken13:'=>';
FM_ImplicitToken14:'(';
FM_ImplicitToken15:',';
FM_ImplicitToken16:')';
FM_ImplicitToken17:'forSome';
FM_ImplicitToken18:'{';
FM_ImplicitToken19:'}';
FM_ImplicitToken20:'type';
FM_ImplicitToken21:'val';
FM_ImplicitToken22:'with';
FM_ImplicitToken23:'#';
FM_ImplicitToken24:'.';
FM_ImplicitToken25:'type';
FM_ImplicitToken26:'(';
FM_ImplicitToken27:')';
FM_ImplicitToken28:'[';
FM_ImplicitToken29:']';
FM_ImplicitToken30:',';
FM_ImplicitToken31:'{';
FM_ImplicitToken32:'}';
FM_ImplicitToken33:'type';
FM_ImplicitToken34:':';
FM_ImplicitToken35:':';
FM_ImplicitToken36:':';
FM_ImplicitToken37:'_';
FM_ImplicitToken38:'*';
FM_ImplicitToken39:'implicit';
FM_ImplicitToken40:'_';
FM_ImplicitToken41:'=>';
FM_ImplicitToken42:'if';
FM_ImplicitToken43:'(';
FM_ImplicitToken44:')';
FM_ImplicitToken45:'else';
FM_ImplicitToken46:'while';
FM_ImplicitToken47:'(';
FM_ImplicitToken48:')';
FM_ImplicitToken49:'try';
FM_ImplicitToken50:'catch';
FM_ImplicitToken51:'finally';
FM_ImplicitToken52:'do';
FM_ImplicitToken53:'while';
FM_ImplicitToken54:'(';
FM_ImplicitToken55:')';
FM_ImplicitToken56:'for';
FM_ImplicitToken57:'(';
FM_ImplicitToken58:')';
FM_ImplicitToken59:'{';
FM_ImplicitToken60:'}';
FM_ImplicitToken61:'yield';
FM_ImplicitToken62:'throw';
FM_ImplicitToken63:'return';
FM_ImplicitToken64:'_';
FM_ImplicitToken65:'.';
FM_ImplicitToken66:'=';
FM_ImplicitToken67:'=';
FM_ImplicitToken68:'match';
FM_ImplicitToken69:'{';
FM_ImplicitToken70:'}';
FM_ImplicitToken71:'-';
FM_ImplicitToken72:'+';
FM_ImplicitToken73:'~';
FM_ImplicitToken74:'!';
FM_ImplicitToken75:'_';
FM_ImplicitToken76:'new';
FM_ImplicitToken77:'_';
FM_ImplicitToken78:'(';
FM_ImplicitToken79:')';
FM_ImplicitToken80:'.';
FM_ImplicitToken81:'_';
FM_ImplicitToken82:'.';
FM_ImplicitToken83:'_';
FM_ImplicitToken84:',';
FM_ImplicitToken85:'(';
FM_ImplicitToken86:')';
FM_ImplicitToken87:'{';
FM_ImplicitToken88:'}';
FM_ImplicitToken89:',';
FM_ImplicitToken90:':';
FM_ImplicitToken91:'_';
FM_ImplicitToken92:'*';
FM_ImplicitToken93:'{';
FM_ImplicitToken94:'}';
FM_ImplicitToken95:'{';
FM_ImplicitToken96:'}';
FM_ImplicitToken97:'implicit';
FM_ImplicitToken98:'lazy';
FM_ImplicitToken99:'implicit';
FM_ImplicitToken100:'_';
FM_ImplicitToken101:':';
FM_ImplicitToken102:'=>';
FM_ImplicitToken103:'<-';
FM_ImplicitToken104:'=';
FM_ImplicitToken105:'case';
FM_ImplicitToken106:'=>';
FM_ImplicitToken107:'if';
FM_ImplicitToken108:'|';
FM_ImplicitToken109:'_';
FM_ImplicitToken110:':';
FM_ImplicitToken111:'@';
FM_ImplicitToken112:'_';
FM_ImplicitToken113:'(';
FM_ImplicitToken114:')';
FM_ImplicitToken115:'(';
FM_ImplicitToken116:',';
FM_ImplicitToken117:'@';
FM_ImplicitToken118:'_';
FM_ImplicitToken119:'*';
FM_ImplicitToken120:')';
FM_ImplicitToken121:'(';
FM_ImplicitToken122:')';
FM_ImplicitToken123:',';
FM_ImplicitToken124:'_';
FM_ImplicitToken125:'*';
FM_ImplicitToken126:'[';
FM_ImplicitToken127:',';
FM_ImplicitToken128:']';
FM_ImplicitToken129:'[';
FM_ImplicitToken130:',';
FM_ImplicitToken131:']';
FM_ImplicitToken132:'+';
FM_ImplicitToken133:'-';
FM_ImplicitToken134:'_';
FM_ImplicitToken135:'>:';
FM_ImplicitToken136:'<:';
FM_ImplicitToken137:'<%';
FM_ImplicitToken138:':';
FM_ImplicitToken139:'(';
FM_ImplicitToken140:'implicit';
FM_ImplicitToken141:')';
FM_ImplicitToken142:'(';
FM_ImplicitToken143:')';
FM_ImplicitToken144:',';
FM_ImplicitToken145:':';
FM_ImplicitToken146:'=';
FM_ImplicitToken147:'=>';
FM_ImplicitToken148:'*';
FM_ImplicitToken149:'(';
FM_ImplicitToken150:'implicit';
FM_ImplicitToken151:')';
FM_ImplicitToken152:'(';
FM_ImplicitToken153:')';
FM_ImplicitToken154:',';
FM_ImplicitToken155:'val';
FM_ImplicitToken156:'var';
FM_ImplicitToken157:':';
FM_ImplicitToken158:'=';
FM_ImplicitToken159:'(';
FM_ImplicitToken160:',';
FM_ImplicitToken161:')';
FM_ImplicitToken162:'_';
FM_ImplicitToken163:':';
FM_ImplicitToken164:'override';
FM_ImplicitToken165:'abstract';
FM_ImplicitToken166:'final';
FM_ImplicitToken167:'sealed';
FM_ImplicitToken168:'implicit';
FM_ImplicitToken169:'lazy';
FM_ImplicitToken170:'private';
FM_ImplicitToken171:'protected';
FM_ImplicitToken172:'[';
FM_ImplicitToken173:'this';
FM_ImplicitToken174:']';
FM_ImplicitToken175:'@';
FM_ImplicitToken176:'@';
FM_ImplicitToken177:'{';
FM_ImplicitToken178:'}';
FM_ImplicitToken179:':';
FM_ImplicitToken180:'=>';
FM_ImplicitToken181:'this';
FM_ImplicitToken182:':';
FM_ImplicitToken183:'=>';
FM_ImplicitToken184:'import';
FM_ImplicitToken185:',';
FM_ImplicitToken186:'.';
FM_ImplicitToken187:'_';
FM_ImplicitToken188:'{';
FM_ImplicitToken189:',';
FM_ImplicitToken190:'_';
FM_ImplicitToken191:'}';
FM_ImplicitToken192:'=>';
FM_ImplicitToken193:'_';
FM_ImplicitToken194:'val';
FM_ImplicitToken195:'var';
FM_ImplicitToken196:'def';
FM_ImplicitToken197:'type';
FM_ImplicitToken198:':';
FM_ImplicitToken199:':';
FM_ImplicitToken200:':';
FM_ImplicitToken201:'>:';
FM_ImplicitToken202:'<:';
FM_ImplicitToken203:'val';
FM_ImplicitToken204:'var';
FM_ImplicitToken205:'def';
FM_ImplicitToken206:'type';
FM_ImplicitToken207:',';
FM_ImplicitToken208:':';
FM_ImplicitToken209:'=';
FM_ImplicitToken210:':';
FM_ImplicitToken211:'=';
FM_ImplicitToken212:'_';
FM_ImplicitToken213:':';
FM_ImplicitToken214:'=';
FM_ImplicitToken215:'{';
FM_ImplicitToken216:'}';
FM_ImplicitToken217:'this';
FM_ImplicitToken218:'=';
FM_ImplicitToken219:'=';
FM_ImplicitToken220:'case';
FM_ImplicitToken221:'class';
FM_ImplicitToken222:'case';
FM_ImplicitToken223:'object';
FM_ImplicitToken224:'trait';
FM_ImplicitToken225:'extends';
FM_ImplicitToken226:'extends';
FM_ImplicitToken227:'extends';
FM_ImplicitToken228:'extends';
FM_ImplicitToken229:'with';
FM_ImplicitToken230:'with';
FM_ImplicitToken231:'{';
FM_ImplicitToken232:'}';
FM_ImplicitToken233:'with';
FM_ImplicitToken234:'{';
FM_ImplicitToken235:'}';
FM_ImplicitToken236:'this';
FM_ImplicitToken237:'package';
FM_ImplicitToken238:'{';
FM_ImplicitToken239:'}';
FM_ImplicitToken240:'package';
FM_ImplicitToken241:'object';
FM_ImplicitToken242:'package';