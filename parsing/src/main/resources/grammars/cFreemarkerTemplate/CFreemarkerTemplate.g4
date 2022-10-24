/*
 [The "BSD licence"]
 Copyright (c) 2013 Sam Harwell
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

/** C 2011 grammar built from the C11 Spec */
grammar CFreemarkerTemplate;


primaryExpression
    :   Identifier
    |   (Constant)
    |   (StringLiteral)+
    |   LeftParen  (fm_expression | expression) RightParen 
    |   genericSelection
    |   FM_ImplicitToken1 ? LeftParen  (fm_compoundStatement | compoundStatement) RightParen  // Blocks (GCC extension)
    |   FM_ImplicitToken2  LeftParen  (fm_unaryExpression | unaryExpression) Comma  (fm_typeName | typeName) RightParen 
    |   FM_ImplicitToken3  LeftParen  (fm_typeName | typeName) Comma  (fm_unaryExpression | unaryExpression) RightParen 
    ;

genericSelection
    :   Generic  LeftParen  (fm_assignmentExpression | assignmentExpression) Comma  (fm_genericAssocList | genericAssocList) RightParen 
    ;

genericAssocList
    :   (fm_genericAssociation | genericAssociation) (Comma  (fm_genericAssociation | genericAssociation))*
    ;
fm_genericAssocList: FM_PLACEHOLDER | FM_IF (fm_genericAssocList | genericAssocList) (FM_ELSE_IF (fm_genericAssocList | genericAssocList))* FM_ELSE (fm_genericAssocList | genericAssocList) FM_IF_CLOSE;

genericAssociation
    :   (typeName | Default ) Colon  (fm_assignmentExpression | assignmentExpression)
    ;
fm_genericAssociation: FM_PLACEHOLDER | FM_IF (fm_genericAssociation | genericAssociation) (FM_ELSE_IF (fm_genericAssociation | genericAssociation))* FM_ELSE (fm_genericAssociation | genericAssociation) FM_IF_CLOSE;

postfixExpression
    :
    (   primaryExpression
    |   FM_ImplicitToken1 ? LeftParen  (fm_typeName | typeName) RightParen  LeftBrace  (fm_initializerList | initializerList) Comma ? RightBrace 
    )
    (LeftBracket  (fm_expression | expression) RightBracket 
    | LeftParen  (fm_argumentExpressionListOpt | argumentExpressionList)? RightParen 
    | (Dot  | Arrow ) (fm_Identifier | Identifier)
    | (PlusPlus  | MinusMinus )
    )*
    ;

argumentExpressionList
    :   (fm_assignmentExpression | assignmentExpression) (Comma  (fm_assignmentExpression | assignmentExpression))*
    ;
fm_argumentExpressionListOpt: FM_PLACEHOLDER | FM_IF (fm_argumentExpressionListOpt | argumentExpressionList)? (FM_ELSE_IF (fm_argumentExpressionListOpt | argumentExpressionList)?)* (FM_ELSE (fm_argumentExpressionListOpt | argumentExpressionList)?)? FM_IF_CLOSE;

unaryExpression
    :
    (PlusPlus  |  MinusMinus  |  Sizeof )*
    (postfixExpression
    |   (fm_unaryOperator | unaryOperator) (fm_castExpression | castExpression)
    |   (Sizeof  | Alignof ) LeftParen  (fm_typeName | typeName) RightParen 
    |   AndAnd  (fm_Identifier | Identifier) // GCC extension address of label
    )
    ;
fm_unaryExpression: FM_PLACEHOLDER | FM_IF (fm_unaryExpression | unaryExpression) (FM_ELSE_IF (fm_unaryExpression | unaryExpression))* FM_ELSE (fm_unaryExpression | unaryExpression) FM_IF_CLOSE;

unaryOperator
    :   And  | Star  | Plus  | Minus  | Tilde  | Not 
    ;
fm_unaryOperator: FM_PLACEHOLDER | FM_IF (fm_unaryOperator | unaryOperator) (FM_ELSE_IF (fm_unaryOperator | unaryOperator))* FM_ELSE (fm_unaryOperator | unaryOperator) FM_IF_CLOSE;

castExpression
    :   FM_ImplicitToken1 ? LeftParen  (fm_typeName | typeName) RightParen  (fm_castExpression | castExpression)
    |   unaryExpression
    |   (DigitSequence) // for
    ;
fm_castExpression: FM_PLACEHOLDER | FM_IF (fm_castExpression | castExpression) (FM_ELSE_IF (fm_castExpression | castExpression))* FM_ELSE (fm_castExpression | castExpression) FM_IF_CLOSE;

multiplicativeExpression
    :   (fm_castExpression | castExpression) ((Star |Div |Mod ) (fm_castExpression | castExpression))*
    ;
fm_multiplicativeExpression: FM_PLACEHOLDER | FM_IF (fm_multiplicativeExpression | multiplicativeExpression) (FM_ELSE_IF (fm_multiplicativeExpression | multiplicativeExpression))* FM_ELSE (fm_multiplicativeExpression | multiplicativeExpression) FM_IF_CLOSE;

additiveExpression
    :   (fm_multiplicativeExpression | multiplicativeExpression) ((Plus |Minus ) (fm_multiplicativeExpression | multiplicativeExpression))*
    ;
fm_additiveExpression: FM_PLACEHOLDER | FM_IF (fm_additiveExpression | additiveExpression) (FM_ELSE_IF (fm_additiveExpression | additiveExpression))* FM_ELSE (fm_additiveExpression | additiveExpression) FM_IF_CLOSE;

shiftExpression
    :   (fm_additiveExpression | additiveExpression) ((LeftShift |RightShift ) (fm_additiveExpression | additiveExpression))*
    ;
fm_shiftExpression: FM_PLACEHOLDER | FM_IF (fm_shiftExpression | shiftExpression) (FM_ELSE_IF (fm_shiftExpression | shiftExpression))* FM_ELSE (fm_shiftExpression | shiftExpression) FM_IF_CLOSE;

relationalExpression
    :   (fm_shiftExpression | shiftExpression) ((Less |Greater |LessEqual |GreaterEqual ) (fm_shiftExpression | shiftExpression))*
    ;
fm_relationalExpression: FM_PLACEHOLDER | FM_IF (fm_relationalExpression | relationalExpression) (FM_ELSE_IF (fm_relationalExpression | relationalExpression))* FM_ELSE (fm_relationalExpression | relationalExpression) FM_IF_CLOSE;

equalityExpression
    :   (fm_relationalExpression | relationalExpression) ((Equal | NotEqual ) (fm_relationalExpression | relationalExpression))*
    ;
fm_equalityExpression: FM_PLACEHOLDER | FM_IF (fm_equalityExpression | equalityExpression) (FM_ELSE_IF (fm_equalityExpression | equalityExpression))* FM_ELSE (fm_equalityExpression | equalityExpression) FM_IF_CLOSE;

andExpression
    :   (fm_equalityExpression | equalityExpression) ( And  (fm_equalityExpression | equalityExpression))*
    ;
fm_andExpression: FM_PLACEHOLDER | FM_IF (fm_andExpression | andExpression) (FM_ELSE_IF (fm_andExpression | andExpression))* FM_ELSE (fm_andExpression | andExpression) FM_IF_CLOSE;

exclusiveOrExpression
    :   (fm_andExpression | andExpression) (Caret  (fm_andExpression | andExpression))*
    ;
fm_exclusiveOrExpression: FM_PLACEHOLDER | FM_IF (fm_exclusiveOrExpression | exclusiveOrExpression) (FM_ELSE_IF (fm_exclusiveOrExpression | exclusiveOrExpression))* FM_ELSE (fm_exclusiveOrExpression | exclusiveOrExpression) FM_IF_CLOSE;

inclusiveOrExpression
    :   (fm_exclusiveOrExpression | exclusiveOrExpression) (Or  (fm_exclusiveOrExpression | exclusiveOrExpression))*
    ;
fm_inclusiveOrExpression: FM_PLACEHOLDER | FM_IF (fm_inclusiveOrExpression | inclusiveOrExpression) (FM_ELSE_IF (fm_inclusiveOrExpression | inclusiveOrExpression))* FM_ELSE (fm_inclusiveOrExpression | inclusiveOrExpression) FM_IF_CLOSE;

logicalAndExpression
    :   (fm_inclusiveOrExpression | inclusiveOrExpression) (AndAnd  (fm_inclusiveOrExpression | inclusiveOrExpression))*
    ;
fm_logicalAndExpression: FM_PLACEHOLDER | FM_IF (fm_logicalAndExpression | logicalAndExpression) (FM_ELSE_IF (fm_logicalAndExpression | logicalAndExpression))* FM_ELSE (fm_logicalAndExpression | logicalAndExpression) FM_IF_CLOSE;

logicalOrExpression
    :   (fm_logicalAndExpression | logicalAndExpression) ( OrOr  (fm_logicalAndExpression | logicalAndExpression))*
    ;
fm_logicalOrExpression: FM_PLACEHOLDER | FM_IF (fm_logicalOrExpression | logicalOrExpression) (FM_ELSE_IF (fm_logicalOrExpression | logicalOrExpression))* FM_ELSE (fm_logicalOrExpression | logicalOrExpression) FM_IF_CLOSE;

conditionalExpression
    :   (fm_logicalOrExpression | logicalOrExpression) (Question  (fm_expression | expression) Colon  (fm_conditionalExpression | conditionalExpression))?
    ;
fm_conditionalExpression: FM_PLACEHOLDER | FM_IF (fm_conditionalExpression | conditionalExpression) (FM_ELSE_IF (fm_conditionalExpression | conditionalExpression))* FM_ELSE (fm_conditionalExpression | conditionalExpression) FM_IF_CLOSE;

assignmentExpression
    :   conditionalExpression
    |   (fm_unaryExpression | unaryExpression) (fm_assignmentOperator | assignmentOperator) (fm_assignmentExpression | assignmentExpression)
    |   (DigitSequence) // for
    ;
fm_assignmentExpression: FM_PLACEHOLDER | FM_IF (fm_assignmentExpression | assignmentExpression) (FM_ELSE_IF (fm_assignmentExpression | assignmentExpression))* FM_ELSE (fm_assignmentExpression | assignmentExpression) FM_IF_CLOSE;
fm_assignmentExpressionOpt: FM_PLACEHOLDER | FM_IF (fm_assignmentExpressionOpt | assignmentExpression)? (FM_ELSE_IF (fm_assignmentExpressionOpt | assignmentExpression)?)* (FM_ELSE (fm_assignmentExpressionOpt | assignmentExpression)?)? FM_IF_CLOSE;

assignmentOperator
    :   Assign  | StarAssign  | DivAssign  | ModAssign  | PlusAssign  | MinusAssign  | LeftShiftAssign  | RightShiftAssign  | AndAssign  | XorAssign  | OrAssign 
    ;
fm_assignmentOperator: FM_PLACEHOLDER | FM_IF (fm_assignmentOperator | assignmentOperator) (FM_ELSE_IF (fm_assignmentOperator | assignmentOperator))* FM_ELSE (fm_assignmentOperator | assignmentOperator) FM_IF_CLOSE;

expression
    :   (fm_assignmentExpression | assignmentExpression) (Comma  (fm_assignmentExpression | assignmentExpression))*
    ;
fm_expression: FM_PLACEHOLDER | FM_IF (fm_expression | expression) (FM_ELSE_IF (fm_expression | expression))* FM_ELSE (fm_expression | expression) FM_IF_CLOSE;
fm_expressionOpt: FM_PLACEHOLDER | FM_IF (fm_expressionOpt | expression)? (FM_ELSE_IF (fm_expressionOpt | expression)?)* (FM_ELSE (fm_expressionOpt | expression)?)? FM_IF_CLOSE;

constantExpression
    :   conditionalExpression
    ;
fm_constantExpression: FM_PLACEHOLDER | FM_IF (fm_constantExpression | constantExpression) (FM_ELSE_IF (fm_constantExpression | constantExpression))* FM_ELSE (fm_constantExpression | constantExpression) FM_IF_CLOSE;

declaration
    :   (fm_declarationSpecifiers | declarationSpecifiers) (fm_initDeclaratorListOpt | initDeclaratorList)? Semi 
    |   staticAssertDeclaration
    ;
fm_declarationPlus: FM_PLACEHOLDER | (FM_IF (fm_declarationPlus | declaration)* (FM_ELSE_IF (fm_declarationPlus | declaration)*)* (FM_ELSE (fm_declarationPlus | declaration)*)? FM_IF_CLOSE | FM_LIST (fm_declarationPlus | declaration)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_declarationPlus | declaration)* (FM_ELSE_IF (fm_declarationPlus | declaration)*)* FM_ELSE (fm_declarationPlus | declaration)* FM_IF_CLOSE | FM_LIST (fm_declarationPlus | declaration)* FM_LIST_CLOSE) (FM_IF (fm_declarationPlus | declaration)* (FM_ELSE_IF (fm_declarationPlus | declaration)*)* (FM_ELSE (fm_declarationPlus | declaration)*)? FM_IF_CLOSE | FM_LIST (fm_declarationPlus | declaration)* FM_LIST_CLOSE)*;

declarationSpecifiers
    :   (fm_declarationSpecifierPlus | declarationSpecifier)+
    ;
fm_declarationSpecifiers: FM_PLACEHOLDER | FM_IF (fm_declarationSpecifiers | declarationSpecifiers) (FM_ELSE_IF (fm_declarationSpecifiers | declarationSpecifiers))* FM_ELSE (fm_declarationSpecifiers | declarationSpecifiers) FM_IF_CLOSE;
fm_declarationSpecifiersOpt: FM_PLACEHOLDER | FM_IF (fm_declarationSpecifiersOpt | declarationSpecifiers)? (FM_ELSE_IF (fm_declarationSpecifiersOpt | declarationSpecifiers)?)* (FM_ELSE (fm_declarationSpecifiersOpt | declarationSpecifiers)?)? FM_IF_CLOSE;

declarationSpecifiers2
    :   (fm_declarationSpecifierPlus | declarationSpecifier)+
    ;
fm_declarationSpecifiers2: FM_PLACEHOLDER | FM_IF (fm_declarationSpecifiers2 | declarationSpecifiers2) (FM_ELSE_IF (fm_declarationSpecifiers2 | declarationSpecifiers2))* FM_ELSE (fm_declarationSpecifiers2 | declarationSpecifiers2) FM_IF_CLOSE;

declarationSpecifier
    :   storageClassSpecifier
    |   typeSpecifier
    |   typeQualifier
    |   functionSpecifier
    |   alignmentSpecifier
    ;
fm_declarationSpecifierPlus: FM_PLACEHOLDER | (FM_IF (fm_declarationSpecifierPlus | declarationSpecifier)* (FM_ELSE_IF (fm_declarationSpecifierPlus | declarationSpecifier)*)* (FM_ELSE (fm_declarationSpecifierPlus | declarationSpecifier)*)? FM_IF_CLOSE | FM_LIST (fm_declarationSpecifierPlus | declarationSpecifier)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_declarationSpecifierPlus | declarationSpecifier)* (FM_ELSE_IF (fm_declarationSpecifierPlus | declarationSpecifier)*)* FM_ELSE (fm_declarationSpecifierPlus | declarationSpecifier)* FM_IF_CLOSE | FM_LIST (fm_declarationSpecifierPlus | declarationSpecifier)* FM_LIST_CLOSE) (FM_IF (fm_declarationSpecifierPlus | declarationSpecifier)* (FM_ELSE_IF (fm_declarationSpecifierPlus | declarationSpecifier)*)* (FM_ELSE (fm_declarationSpecifierPlus | declarationSpecifier)*)? FM_IF_CLOSE | FM_LIST (fm_declarationSpecifierPlus | declarationSpecifier)* FM_LIST_CLOSE)*;

initDeclaratorList
    :   (fm_initDeclarator | initDeclarator) (Comma  (fm_initDeclarator | initDeclarator))*
    ;
fm_initDeclaratorListOpt: FM_PLACEHOLDER | FM_IF (fm_initDeclaratorListOpt | initDeclaratorList)? (FM_ELSE_IF (fm_initDeclaratorListOpt | initDeclaratorList)?)* (FM_ELSE (fm_initDeclaratorListOpt | initDeclaratorList)?)? FM_IF_CLOSE;

initDeclarator
    :   (fm_declarator | declarator) (Assign  (fm_initializer | initializer))?
    ;
fm_initDeclarator: FM_PLACEHOLDER | FM_IF (fm_initDeclarator | initDeclarator) (FM_ELSE_IF (fm_initDeclarator | initDeclarator))* FM_ELSE (fm_initDeclarator | initDeclarator) FM_IF_CLOSE;

storageClassSpecifier
    :   Typedef 
    |   Extern 
    |   Static 
    |   ThreadLocal 
    |   Auto 
    |   Register 
    ;

typeSpecifier
    :   (Void 
    |   Char 
    |   Short 
    |   Int 
    |   Long 
    |   Float 
    |   Double 
    |   Signed 
    |   Unsigned 
    |   Bool 
    |   Complex 
    |   FM_ImplicitToken4 
    |   FM_ImplicitToken5 
    |   FM_ImplicitToken6 )
    |   FM_ImplicitToken1  LeftParen  (FM_ImplicitToken4  | FM_ImplicitToken5  | FM_ImplicitToken6 ) RightParen 
    |   atomicTypeSpecifier
    |   structOrUnionSpecifier
    |   enumSpecifier
    |   typedefName
    |   FM_ImplicitToken7  LeftParen  (fm_constantExpression | constantExpression) RightParen  // GCC extension
    ;

structOrUnionSpecifier
    :   (fm_structOrUnion | structOrUnion) (fm_IdentifierOpt | Identifier)? LeftBrace  (fm_structDeclarationList | structDeclarationList) RightBrace 
    |   (fm_structOrUnion | structOrUnion) (fm_Identifier | Identifier)
    ;

structOrUnion
    :   Struct 
    |   Union 
    ;
fm_structOrUnion: FM_PLACEHOLDER | FM_IF (fm_structOrUnion | structOrUnion) (FM_ELSE_IF (fm_structOrUnion | structOrUnion))* FM_ELSE (fm_structOrUnion | structOrUnion) FM_IF_CLOSE;

structDeclarationList
    :   (fm_structDeclarationPlus | structDeclaration)+
    ;
fm_structDeclarationList: FM_PLACEHOLDER | FM_IF (fm_structDeclarationList | structDeclarationList) (FM_ELSE_IF (fm_structDeclarationList | structDeclarationList))* FM_ELSE (fm_structDeclarationList | structDeclarationList) FM_IF_CLOSE;

structDeclaration // The first two rules have priority order and cannot be simplified to one expression.
    :   (fm_specifierQualifierList | specifierQualifierList) (fm_structDeclaratorList | structDeclaratorList) Semi 
    |   (fm_specifierQualifierList | specifierQualifierList) Semi 
    |   staticAssertDeclaration
    ;
fm_structDeclarationPlus: FM_PLACEHOLDER | (FM_IF (fm_structDeclarationPlus | structDeclaration)* (FM_ELSE_IF (fm_structDeclarationPlus | structDeclaration)*)* (FM_ELSE (fm_structDeclarationPlus | structDeclaration)*)? FM_IF_CLOSE | FM_LIST (fm_structDeclarationPlus | structDeclaration)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_structDeclarationPlus | structDeclaration)* (FM_ELSE_IF (fm_structDeclarationPlus | structDeclaration)*)* FM_ELSE (fm_structDeclarationPlus | structDeclaration)* FM_IF_CLOSE | FM_LIST (fm_structDeclarationPlus | structDeclaration)* FM_LIST_CLOSE) (FM_IF (fm_structDeclarationPlus | structDeclaration)* (FM_ELSE_IF (fm_structDeclarationPlus | structDeclaration)*)* (FM_ELSE (fm_structDeclarationPlus | structDeclaration)*)? FM_IF_CLOSE | FM_LIST (fm_structDeclarationPlus | structDeclaration)* FM_LIST_CLOSE)*;

specifierQualifierList
    :   (typeSpecifier| typeQualifier) (fm_specifierQualifierListOpt | specifierQualifierList)?
    ;
fm_specifierQualifierList: FM_PLACEHOLDER | FM_IF (fm_specifierQualifierList | specifierQualifierList) (FM_ELSE_IF (fm_specifierQualifierList | specifierQualifierList))* FM_ELSE (fm_specifierQualifierList | specifierQualifierList) FM_IF_CLOSE;
fm_specifierQualifierListOpt: FM_PLACEHOLDER | FM_IF (fm_specifierQualifierListOpt | specifierQualifierList)? (FM_ELSE_IF (fm_specifierQualifierListOpt | specifierQualifierList)?)* (FM_ELSE (fm_specifierQualifierListOpt | specifierQualifierList)?)? FM_IF_CLOSE;

structDeclaratorList
    :   (fm_structDeclarator | structDeclarator) (Comma  (fm_structDeclarator | structDeclarator))*
    ;
fm_structDeclaratorList: FM_PLACEHOLDER | FM_IF (fm_structDeclaratorList | structDeclaratorList) (FM_ELSE_IF (fm_structDeclaratorList | structDeclaratorList))* FM_ELSE (fm_structDeclaratorList | structDeclaratorList) FM_IF_CLOSE;

structDeclarator
    :   declarator
    |   (fm_declaratorOpt | declarator)? Colon  (fm_constantExpression | constantExpression)
    ;
fm_structDeclarator: FM_PLACEHOLDER | FM_IF (fm_structDeclarator | structDeclarator) (FM_ELSE_IF (fm_structDeclarator | structDeclarator))* FM_ELSE (fm_structDeclarator | structDeclarator) FM_IF_CLOSE;

enumSpecifier
    :   Enum  (fm_IdentifierOpt | Identifier)? LeftBrace  (fm_enumeratorList | enumeratorList) Comma ? RightBrace 
    |   Enum  (fm_Identifier | Identifier)
    ;

enumeratorList
    :   (fm_enumerator | enumerator) (Comma  (fm_enumerator | enumerator))*
    ;
fm_enumeratorList: FM_PLACEHOLDER | FM_IF (fm_enumeratorList | enumeratorList) (FM_ELSE_IF (fm_enumeratorList | enumeratorList))* FM_ELSE (fm_enumeratorList | enumeratorList) FM_IF_CLOSE;

enumerator
    :   (fm_enumerationConstant | enumerationConstant) (Assign  (fm_constantExpression | constantExpression))?
    ;
fm_enumerator: FM_PLACEHOLDER | FM_IF (fm_enumerator | enumerator) (FM_ELSE_IF (fm_enumerator | enumerator))* FM_ELSE (fm_enumerator | enumerator) FM_IF_CLOSE;

enumerationConstant
    :   Identifier
    ;
fm_enumerationConstant: FM_PLACEHOLDER | FM_IF (fm_enumerationConstant | enumerationConstant) (FM_ELSE_IF (fm_enumerationConstant | enumerationConstant))* FM_ELSE (fm_enumerationConstant | enumerationConstant) FM_IF_CLOSE;

atomicTypeSpecifier
    :   Atomic  LeftParen  (fm_typeName | typeName) RightParen 
    ;

typeQualifier
    :   Const 
    |   Restrict 
    |   Volatile 
    |   Atomic 
    ;
fm_typeQualifierPlus: FM_PLACEHOLDER | (FM_IF (fm_typeQualifierPlus | typeQualifier)* (FM_ELSE_IF (fm_typeQualifierPlus | typeQualifier)*)* (FM_ELSE (fm_typeQualifierPlus | typeQualifier)*)? FM_IF_CLOSE | FM_LIST (fm_typeQualifierPlus | typeQualifier)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_typeQualifierPlus | typeQualifier)* (FM_ELSE_IF (fm_typeQualifierPlus | typeQualifier)*)* FM_ELSE (fm_typeQualifierPlus | typeQualifier)* FM_IF_CLOSE | FM_LIST (fm_typeQualifierPlus | typeQualifier)* FM_LIST_CLOSE) (FM_IF (fm_typeQualifierPlus | typeQualifier)* (FM_ELSE_IF (fm_typeQualifierPlus | typeQualifier)*)* (FM_ELSE (fm_typeQualifierPlus | typeQualifier)*)? FM_IF_CLOSE | FM_LIST (fm_typeQualifierPlus | typeQualifier)* FM_LIST_CLOSE)*;

functionSpecifier
    :   (Inline 
    |   Noreturn 
    |   FM_ImplicitToken8  // GCC extension
    |   FM_ImplicitToken9 )
    |   gccAttributeSpecifier
    |   FM_ImplicitToken10  LeftParen  (fm_Identifier | Identifier) RightParen 
    ;

alignmentSpecifier
    :   Alignas  LeftParen  (typeName | constantExpression) RightParen 
    ;

declarator
    :   (fm_pointerOpt | pointer)? (fm_directDeclarator | directDeclarator) (fm_gccDeclaratorExtensionStar | gccDeclaratorExtension)*
    ;
fm_declarator: FM_PLACEHOLDER | FM_IF (fm_declarator | declarator) (FM_ELSE_IF (fm_declarator | declarator))* FM_ELSE (fm_declarator | declarator) FM_IF_CLOSE;
fm_declaratorOpt: FM_PLACEHOLDER | FM_IF (fm_declaratorOpt | declarator)? (FM_ELSE_IF (fm_declaratorOpt | declarator)?)* (FM_ELSE (fm_declaratorOpt | declarator)?)? FM_IF_CLOSE;

directDeclarator
    :   Identifier
    |   LeftParen  (fm_declarator | declarator) RightParen 
    |   fm_directDeclarator LeftBracket ( fm_typeQualifierListOpt | typeQualifierList ) ? ( fm_assignmentExpressionOpt | assignmentExpression ) ? RightBracket 
  | directDeclarator LeftBracket  (fm_typeQualifierListOpt | typeQualifierList)? (fm_assignmentExpressionOpt | assignmentExpression)? RightBracket 
    |   fm_directDeclarator LeftBracket Static ( fm_typeQualifierListOpt | typeQualifierList ) ? ( fm_assignmentExpression | assignmentExpression ) RightBracket 
  | directDeclarator LeftBracket  Static  (fm_typeQualifierListOpt | typeQualifierList)? (fm_assignmentExpression | assignmentExpression) RightBracket 
    |   fm_directDeclarator LeftBracket ( fm_typeQualifierList | typeQualifierList ) Static ( fm_assignmentExpression | assignmentExpression ) RightBracket 
  | directDeclarator LeftBracket  (fm_typeQualifierList | typeQualifierList) Static  (fm_assignmentExpression | assignmentExpression) RightBracket 
    |   fm_directDeclarator LeftBracket ( fm_typeQualifierListOpt | typeQualifierList ) ? Star RightBracket 
  | directDeclarator LeftBracket  (fm_typeQualifierListOpt | typeQualifierList)? Star  RightBracket 
    |   fm_directDeclarator LeftParen ( fm_parameterTypeList | parameterTypeList ) RightParen 
  | directDeclarator LeftParen  (fm_parameterTypeList | parameterTypeList) RightParen 
    |   fm_directDeclarator LeftParen ( fm_identifierListOpt | identifierList ) ? RightParen 
  | directDeclarator LeftParen  (fm_identifierListOpt | identifierList)? RightParen 
    |   (fm_Identifier | Identifier) Colon  (DigitSequence)  // bit field
    |   (fm_vcSpecificModifer | vcSpecificModifer) (fm_Identifier | Identifier) // Visual C Extension
    |   LeftParen  (fm_vcSpecificModifer | vcSpecificModifer) (fm_declarator | declarator) RightParen  // Visual C Extension
    ;
fm_directDeclarator: FM_PLACEHOLDER | FM_IF (fm_directDeclarator | directDeclarator) (FM_ELSE_IF (fm_directDeclarator | directDeclarator))* FM_ELSE (fm_directDeclarator | directDeclarator) FM_IF_CLOSE;

vcSpecificModifer
    :   (FM_ImplicitToken11  
    |   FM_ImplicitToken12  
    |   FM_ImplicitToken9  
    |   FM_ImplicitToken13  
    |   FM_ImplicitToken14  
    |   FM_ImplicitToken15 ) 
    ;
fm_vcSpecificModifer: FM_PLACEHOLDER | FM_IF (fm_vcSpecificModifer | vcSpecificModifer) (FM_ELSE_IF (fm_vcSpecificModifer | vcSpecificModifer))* FM_ELSE (fm_vcSpecificModifer | vcSpecificModifer) FM_IF_CLOSE;


gccDeclaratorExtension
    :   FM_ImplicitToken16  LeftParen  (StringLiteral)+ RightParen 
    |   gccAttributeSpecifier
    ;
fm_gccDeclaratorExtensionStar: FM_PLACEHOLDER | FM_IF (fm_gccDeclaratorExtensionStar | gccDeclaratorExtension)* (FM_ELSE_IF (fm_gccDeclaratorExtensionStar | gccDeclaratorExtension)*)* (FM_ELSE (fm_gccDeclaratorExtensionStar | gccDeclaratorExtension)*)? FM_IF_CLOSE | FM_LIST (fm_gccDeclaratorExtensionStar | gccDeclaratorExtension)* FM_LIST_CLOSE;

gccAttributeSpecifier
    :   FM_ImplicitToken17  LeftParen  LeftParen  (fm_gccAttributeList | gccAttributeList) RightParen  RightParen 
    ;

gccAttributeList
    :   (fm_gccAttributeOpt | gccAttribute)? (Comma  (fm_gccAttributeOpt | gccAttribute)?)*
    ;
fm_gccAttributeList: FM_PLACEHOLDER | FM_IF (fm_gccAttributeList | gccAttributeList) (FM_ELSE_IF (fm_gccAttributeList | gccAttributeList))* FM_ELSE (fm_gccAttributeList | gccAttributeList) FM_IF_CLOSE;

gccAttribute
    :   ~(',' | '(' | ')') // relaxed def for "identifier or reserved word"
        (LeftParen  (fm_argumentExpressionListOpt | argumentExpressionList)? RightParen )?
    ;
fm_gccAttributeOpt: FM_PLACEHOLDER | FM_IF (fm_gccAttributeOpt | gccAttribute)? (FM_ELSE_IF (fm_gccAttributeOpt | gccAttribute)?)* (FM_ELSE (fm_gccAttributeOpt | gccAttribute)?)? FM_IF_CLOSE;

nestedParenthesesBlock
    :   (   ~('(' | ')')
        |   LeftParen  (fm_nestedParenthesesBlock | nestedParenthesesBlock) RightParen 
        )*
    ;
fm_nestedParenthesesBlock: FM_PLACEHOLDER | FM_IF (fm_nestedParenthesesBlock | nestedParenthesesBlock) (FM_ELSE_IF (fm_nestedParenthesesBlock | nestedParenthesesBlock))* FM_ELSE (fm_nestedParenthesesBlock | nestedParenthesesBlock) FM_IF_CLOSE;

pointer
    :  ((Star |Caret ) (fm_typeQualifierListOpt | typeQualifierList)?)+ // ^ - Blocks language extension
    ;
fm_pointerOpt: FM_PLACEHOLDER | FM_IF (fm_pointerOpt | pointer)? (FM_ELSE_IF (fm_pointerOpt | pointer)?)* (FM_ELSE (fm_pointerOpt | pointer)?)? FM_IF_CLOSE;

typeQualifierList
    :   (fm_typeQualifierPlus | typeQualifier)+
    ;
fm_typeQualifierList: FM_PLACEHOLDER | FM_IF (fm_typeQualifierList | typeQualifierList) (FM_ELSE_IF (fm_typeQualifierList | typeQualifierList))* FM_ELSE (fm_typeQualifierList | typeQualifierList) FM_IF_CLOSE;
fm_typeQualifierListOpt: FM_PLACEHOLDER | FM_IF (fm_typeQualifierListOpt | typeQualifierList)? (FM_ELSE_IF (fm_typeQualifierListOpt | typeQualifierList)?)* (FM_ELSE (fm_typeQualifierListOpt | typeQualifierList)?)? FM_IF_CLOSE;

parameterTypeList
    :   (fm_parameterList | parameterList) (Comma  Ellipsis )?
    ;
fm_parameterTypeList: FM_PLACEHOLDER | FM_IF (fm_parameterTypeList | parameterTypeList) (FM_ELSE_IF (fm_parameterTypeList | parameterTypeList))* FM_ELSE (fm_parameterTypeList | parameterTypeList) FM_IF_CLOSE;
fm_parameterTypeListOpt: FM_PLACEHOLDER | FM_IF (fm_parameterTypeListOpt | parameterTypeList)? (FM_ELSE_IF (fm_parameterTypeListOpt | parameterTypeList)?)* (FM_ELSE (fm_parameterTypeListOpt | parameterTypeList)?)? FM_IF_CLOSE;

parameterList
    :   (fm_parameterDeclaration | parameterDeclaration) (Comma  (fm_parameterDeclaration | parameterDeclaration))*
    ;
fm_parameterList: FM_PLACEHOLDER | FM_IF (fm_parameterList | parameterList) (FM_ELSE_IF (fm_parameterList | parameterList))* FM_ELSE (fm_parameterList | parameterList) FM_IF_CLOSE;

parameterDeclaration
    :   (fm_declarationSpecifiers | declarationSpecifiers) (fm_declarator | declarator)
    |   (fm_declarationSpecifiers2 | declarationSpecifiers2) (fm_abstractDeclaratorOpt | abstractDeclarator)?
    ;
fm_parameterDeclaration: FM_PLACEHOLDER | FM_IF (fm_parameterDeclaration | parameterDeclaration) (FM_ELSE_IF (fm_parameterDeclaration | parameterDeclaration))* FM_ELSE (fm_parameterDeclaration | parameterDeclaration) FM_IF_CLOSE;

identifierList
    :   (fm_Identifier | Identifier) (Comma  (fm_Identifier | Identifier))*
    ;
fm_identifierListOpt: FM_PLACEHOLDER | FM_IF (fm_identifierListOpt | identifierList)? (FM_ELSE_IF (fm_identifierListOpt | identifierList)?)* (FM_ELSE (fm_identifierListOpt | identifierList)?)? FM_IF_CLOSE;

typeName
    :   (fm_specifierQualifierList | specifierQualifierList) (fm_abstractDeclaratorOpt | abstractDeclarator)?
    ;
fm_typeName: FM_PLACEHOLDER | FM_IF (fm_typeName | typeName) (FM_ELSE_IF (fm_typeName | typeName))* FM_ELSE (fm_typeName | typeName) FM_IF_CLOSE;

abstractDeclarator
    :   pointer
    |   (fm_pointerOpt | pointer)? (fm_directAbstractDeclarator | directAbstractDeclarator) (fm_gccDeclaratorExtensionStar | gccDeclaratorExtension)*
    ;
fm_abstractDeclarator: FM_PLACEHOLDER | FM_IF (fm_abstractDeclarator | abstractDeclarator) (FM_ELSE_IF (fm_abstractDeclarator | abstractDeclarator))* FM_ELSE (fm_abstractDeclarator | abstractDeclarator) FM_IF_CLOSE;
fm_abstractDeclaratorOpt: FM_PLACEHOLDER | FM_IF (fm_abstractDeclaratorOpt | abstractDeclarator)? (FM_ELSE_IF (fm_abstractDeclaratorOpt | abstractDeclarator)?)* (FM_ELSE (fm_abstractDeclaratorOpt | abstractDeclarator)?)? FM_IF_CLOSE;

directAbstractDeclarator
    :   LeftParen  (fm_abstractDeclarator | abstractDeclarator) RightParen  (fm_gccDeclaratorExtensionStar | gccDeclaratorExtension)*
    |   LeftBracket  (fm_typeQualifierListOpt | typeQualifierList)? (fm_assignmentExpressionOpt | assignmentExpression)? RightBracket 
    |   LeftBracket  Static  (fm_typeQualifierListOpt | typeQualifierList)? (fm_assignmentExpression | assignmentExpression) RightBracket 
    |   LeftBracket  (fm_typeQualifierList | typeQualifierList) Static  (fm_assignmentExpression | assignmentExpression) RightBracket 
    |   LeftBracket  Star  RightBracket 
    |   LeftParen  (fm_parameterTypeListOpt | parameterTypeList)? RightParen  (fm_gccDeclaratorExtensionStar | gccDeclaratorExtension)*
    |   fm_directAbstractDeclarator LeftBracket ( fm_typeQualifierListOpt | typeQualifierList ) ? ( fm_assignmentExpressionOpt | assignmentExpression ) ? RightBracket 
  | directAbstractDeclarator LeftBracket  (fm_typeQualifierListOpt | typeQualifierList)? (fm_assignmentExpressionOpt | assignmentExpression)? RightBracket 
    |   fm_directAbstractDeclarator LeftBracket Static ( fm_typeQualifierListOpt | typeQualifierList ) ? ( fm_assignmentExpression | assignmentExpression ) RightBracket 
  | directAbstractDeclarator LeftBracket  Static  (fm_typeQualifierListOpt | typeQualifierList)? (fm_assignmentExpression | assignmentExpression) RightBracket 
    |   fm_directAbstractDeclarator LeftBracket ( fm_typeQualifierList | typeQualifierList ) Static ( fm_assignmentExpression | assignmentExpression ) RightBracket 
  | directAbstractDeclarator LeftBracket  (fm_typeQualifierList | typeQualifierList) Static  (fm_assignmentExpression | assignmentExpression) RightBracket 
    |   fm_directAbstractDeclarator LeftBracket Star RightBracket 
  | directAbstractDeclarator LeftBracket  Star  RightBracket 
    |   fm_directAbstractDeclarator LeftParen ( fm_parameterTypeListOpt | parameterTypeList ) ? RightParen ( fm_gccDeclaratorExtensionStar | gccDeclaratorExtension ) * 
  | directAbstractDeclarator LeftParen  (fm_parameterTypeListOpt | parameterTypeList)? RightParen  (fm_gccDeclaratorExtensionStar | gccDeclaratorExtension)*
    ;
fm_directAbstractDeclarator: FM_PLACEHOLDER | FM_IF (fm_directAbstractDeclarator | directAbstractDeclarator) (FM_ELSE_IF (fm_directAbstractDeclarator | directAbstractDeclarator))* FM_ELSE (fm_directAbstractDeclarator | directAbstractDeclarator) FM_IF_CLOSE;

typedefName
    :   Identifier
    ;

initializer
    :   assignmentExpression
    |   LeftBrace  (fm_initializerList | initializerList) Comma ? RightBrace 
    ;
fm_initializer: FM_PLACEHOLDER | FM_IF (fm_initializer | initializer) (FM_ELSE_IF (fm_initializer | initializer))* FM_ELSE (fm_initializer | initializer) FM_IF_CLOSE;

initializerList
    :   (fm_designationOpt | designation)? (fm_initializer | initializer) (Comma  (fm_designationOpt | designation)? (fm_initializer | initializer))*
    ;
fm_initializerList: FM_PLACEHOLDER | FM_IF (fm_initializerList | initializerList) (FM_ELSE_IF (fm_initializerList | initializerList))* FM_ELSE (fm_initializerList | initializerList) FM_IF_CLOSE;

designation
    :   (fm_designatorList | designatorList) Assign 
    ;
fm_designationOpt: FM_PLACEHOLDER | FM_IF (fm_designationOpt | designation)? (FM_ELSE_IF (fm_designationOpt | designation)?)* (FM_ELSE (fm_designationOpt | designation)?)? FM_IF_CLOSE;

designatorList
    :   (fm_designatorPlus | designator)+
    ;
fm_designatorList: FM_PLACEHOLDER | FM_IF (fm_designatorList | designatorList) (FM_ELSE_IF (fm_designatorList | designatorList))* FM_ELSE (fm_designatorList | designatorList) FM_IF_CLOSE;

designator
    :   LeftBracket  (fm_constantExpression | constantExpression) RightBracket 
    |   Dot  (fm_Identifier | Identifier)
    ;
fm_designatorPlus: FM_PLACEHOLDER | (FM_IF (fm_designatorPlus | designator)* (FM_ELSE_IF (fm_designatorPlus | designator)*)* (FM_ELSE (fm_designatorPlus | designator)*)? FM_IF_CLOSE | FM_LIST (fm_designatorPlus | designator)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_designatorPlus | designator)* (FM_ELSE_IF (fm_designatorPlus | designator)*)* FM_ELSE (fm_designatorPlus | designator)* FM_IF_CLOSE | FM_LIST (fm_designatorPlus | designator)* FM_LIST_CLOSE) (FM_IF (fm_designatorPlus | designator)* (FM_ELSE_IF (fm_designatorPlus | designator)*)* (FM_ELSE (fm_designatorPlus | designator)*)? FM_IF_CLOSE | FM_LIST (fm_designatorPlus | designator)* FM_LIST_CLOSE)*;

staticAssertDeclaration
    :   StaticAssert  LeftParen  (fm_constantExpression | constantExpression) Comma  (StringLiteral)+ RightParen  Semi 
    ;

statement
    :   labeledStatement
    |   compoundStatement
    |   expressionStatement
    |   selectionStatement
    |   iterationStatement
    |   jumpStatement
    |   (FM_ImplicitToken16  | FM_ImplicitToken18 ) (Volatile  | FM_ImplicitToken19 ) LeftParen  ((fm_logicalOrExpression | logicalOrExpression) (Comma  (fm_logicalOrExpression | logicalOrExpression))*)? (Colon  ((fm_logicalOrExpression | logicalOrExpression) (Comma  (fm_logicalOrExpression | logicalOrExpression))*)?)* RightParen  Semi 
    ;
fm_statement: FM_PLACEHOLDER | FM_IF (fm_statement | statement) (FM_ELSE_IF (fm_statement | statement))* FM_ELSE (fm_statement | statement) FM_IF_CLOSE;

labeledStatement
    :   (fm_Identifier | Identifier) Colon  (fm_statement | statement)
    |   Case  (fm_constantExpression | constantExpression) Colon  (fm_statement | statement)
    |   Default  Colon  (fm_statement | statement)
    ;

compoundStatement
    :   LeftBrace  (fm_blockItemListOpt | blockItemList)? RightBrace 
    ;
fm_compoundStatement: FM_PLACEHOLDER | FM_IF (fm_compoundStatement | compoundStatement) (FM_ELSE_IF (fm_compoundStatement | compoundStatement))* FM_ELSE (fm_compoundStatement | compoundStatement) FM_IF_CLOSE;

blockItemList
    :   (fm_blockItemPlus | blockItem)+
    ;
fm_blockItemListOpt: FM_PLACEHOLDER | FM_IF (fm_blockItemListOpt | blockItemList)? (FM_ELSE_IF (fm_blockItemListOpt | blockItemList)?)* (FM_ELSE (fm_blockItemListOpt | blockItemList)?)? FM_IF_CLOSE;

blockItem
    :   statement
    |   declaration
    ;
fm_blockItemPlus: FM_PLACEHOLDER | (FM_IF (fm_blockItemPlus | blockItem)* (FM_ELSE_IF (fm_blockItemPlus | blockItem)*)* (FM_ELSE (fm_blockItemPlus | blockItem)*)? FM_IF_CLOSE | FM_LIST (fm_blockItemPlus | blockItem)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_blockItemPlus | blockItem)* (FM_ELSE_IF (fm_blockItemPlus | blockItem)*)* FM_ELSE (fm_blockItemPlus | blockItem)* FM_IF_CLOSE | FM_LIST (fm_blockItemPlus | blockItem)* FM_LIST_CLOSE) (FM_IF (fm_blockItemPlus | blockItem)* (FM_ELSE_IF (fm_blockItemPlus | blockItem)*)* (FM_ELSE (fm_blockItemPlus | blockItem)*)? FM_IF_CLOSE | FM_LIST (fm_blockItemPlus | blockItem)* FM_LIST_CLOSE)*;

expressionStatement
    :   (fm_expressionOpt | expression)? Semi 
    ;

selectionStatement
    :   If  LeftParen  (fm_expression | expression) RightParen  (fm_statement | statement) (Else  (fm_statement | statement))?
    |   Switch  LeftParen  (fm_expression | expression) RightParen  (fm_statement | statement)
    ;

iterationStatement
    :   While LeftParen  (fm_expression | expression) RightParen  (fm_statement | statement)
    |   Do (fm_statement | statement) While LeftParen  (fm_expression | expression) RightParen  Semi 
    |   For LeftParen  (fm_forCondition | forCondition) RightParen  (fm_statement | statement)
    ;

//    |   'for' '(' expression? ';' expression?  ';' forUpdate? ')' statement
//    |   For '(' declaration  expression? ';' expression? ')' statement

forCondition
	:   (forDeclaration | (fm_expressionOpt | expression)?) Semi  (fm_forExpressionOpt | forExpression)? Semi  (fm_forExpressionOpt | forExpression)?
	;
fm_forCondition: FM_PLACEHOLDER | FM_IF (fm_forCondition | forCondition) (FM_ELSE_IF (fm_forCondition | forCondition))* FM_ELSE (fm_forCondition | forCondition) FM_IF_CLOSE;

forDeclaration
    :   (fm_declarationSpecifiers | declarationSpecifiers) (fm_initDeclaratorListOpt | initDeclaratorList)?
    ;

forExpression
    :   (fm_assignmentExpression | assignmentExpression) (Comma  (fm_assignmentExpression | assignmentExpression))*
    ;
fm_forExpressionOpt: FM_PLACEHOLDER | FM_IF (fm_forExpressionOpt | forExpression)? (FM_ELSE_IF (fm_forExpressionOpt | forExpression)?)* (FM_ELSE (fm_forExpressionOpt | forExpression)?)? FM_IF_CLOSE;

jumpStatement
    :   (Goto  (fm_Identifier | Identifier)
    |   (Continue | Break )
    |   Return  (fm_expressionOpt | expression)?
    |   Goto  (fm_unaryExpression | unaryExpression) // GCC extension
    )
    Semi 
    ;

compilationUnit
    :   (fm_translationUnitOpt | translationUnit)? EOF
    ;

translationUnit
    :   (fm_externalDeclarationPlus | externalDeclaration)+
    ;
fm_translationUnitOpt: FM_PLACEHOLDER | FM_IF (fm_translationUnitOpt | translationUnit)? (FM_ELSE_IF (fm_translationUnitOpt | translationUnit)?)* (FM_ELSE (fm_translationUnitOpt | translationUnit)?)? FM_IF_CLOSE;

externalDeclaration
    :   functionDefinition
    |   declaration
    |   Semi  // stray ;
    ;
fm_externalDeclarationPlus: FM_PLACEHOLDER | (FM_IF (fm_externalDeclarationPlus | externalDeclaration)* (FM_ELSE_IF (fm_externalDeclarationPlus | externalDeclaration)*)* (FM_ELSE (fm_externalDeclarationPlus | externalDeclaration)*)? FM_IF_CLOSE | FM_LIST (fm_externalDeclarationPlus | externalDeclaration)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_externalDeclarationPlus | externalDeclaration)* (FM_ELSE_IF (fm_externalDeclarationPlus | externalDeclaration)*)* FM_ELSE (fm_externalDeclarationPlus | externalDeclaration)* FM_IF_CLOSE | FM_LIST (fm_externalDeclarationPlus | externalDeclaration)* FM_LIST_CLOSE) (FM_IF (fm_externalDeclarationPlus | externalDeclaration)* (FM_ELSE_IF (fm_externalDeclarationPlus | externalDeclaration)*)* (FM_ELSE (fm_externalDeclarationPlus | externalDeclaration)*)? FM_IF_CLOSE | FM_LIST (fm_externalDeclarationPlus | externalDeclaration)* FM_LIST_CLOSE)*;

functionDefinition
    :   (fm_declarationSpecifiersOpt | declarationSpecifiers)? (fm_declarator | declarator) (fm_declarationListOpt | declarationList)? (fm_compoundStatement | compoundStatement)
    ;

declarationList
    :   (fm_declarationPlus | declaration)+
    ;
fm_declarationListOpt: FM_PLACEHOLDER | FM_IF (fm_declarationListOpt | declarationList)? (FM_ELSE_IF (fm_declarationListOpt | declarationList)?)* (FM_ELSE (fm_declarationListOpt | declarationList)?)? FM_IF_CLOSE;

Auto : 'auto';
Break : 'break';
Case : 'case';
Char : 'char';
Const : 'const';
Continue : 'continue';
Default : 'default';
Do : 'do';
Double : 'double';
Else : 'else';
Enum : 'enum';
Extern : 'extern';
Float : 'float';
For : 'for';
Goto : 'goto';
If : 'if';
Inline : 'inline';
Int : 'int';
Long : 'long';
Register : 'register';
Restrict : 'restrict';
Return : 'return';
Short : 'short';
Signed : 'signed';
Sizeof : 'sizeof';
Static : 'static';
Struct : 'struct';
Switch : 'switch';
Typedef : 'typedef';
Union : 'union';
Unsigned : 'unsigned';
Void : 'void';
Volatile : 'volatile';
While : 'while';

Alignas : '_Alignas';
Alignof : '_Alignof';
Atomic : '_Atomic';
Bool : '_Bool';
Complex : '_Complex';
Generic : '_Generic';
Imaginary : '_Imaginary';
Noreturn : '_Noreturn';
StaticAssert : '_Static_assert';
ThreadLocal : '_Thread_local';

LeftParen : '(';
RightParen : ')';
LeftBracket : '[';
RightBracket : ']';
LeftBrace : '{';
RightBrace : '}';

Less : '<';
LessEqual : '<=';
Greater : '>';
GreaterEqual : '>=';
LeftShift : '<<';
RightShift : '>>';

Plus : '+';
PlusPlus : '++';
Minus : '-';
MinusMinus : '--';
Star : '*';
Div : '/';
Mod : '%';

And : '&';
Or : '|';
AndAnd : '&&';
OrOr : '||';
Caret : '^';
Not : '!';
Tilde : '~';

Question : '?';
Colon : ':';
Semi : ';';
Comma : ',';

Assign : '=';
// '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|='
StarAssign : '*=';
DivAssign : '/=';
ModAssign : '%=';
PlusAssign : '+=';
MinusAssign : '-=';
LeftShiftAssign : '<<=';
RightShiftAssign : '>>=';
AndAssign : '&=';
XorAssign : '^=';
OrAssign : '|=';

Equal : '==';
NotEqual : '!=';

Arrow : '->';
Dot : '.';
Ellipsis : '...';

Identifier
    :   IdentifierNondigit
        (   IdentifierNondigit
        |   Digit
        )*
    ;
fm_Identifier: FM_PLACEHOLDER | FM_IF (fm_Identifier | Identifier) (FM_ELSE_IF (fm_Identifier | Identifier))* FM_ELSE (fm_Identifier | Identifier) FM_IF_CLOSE;
fm_IdentifierOpt: FM_PLACEHOLDER | FM_IF (fm_IdentifierOpt | Identifier)? (FM_ELSE_IF (fm_IdentifierOpt | Identifier)?)* (FM_ELSE (fm_IdentifierOpt | Identifier)?)? FM_IF_CLOSE;

fragment
IdentifierNondigit
    :   Nondigit
    |   UniversalCharacterName
    //|   // other implementation-defined characters...
    ;

fragment
Nondigit
    :   [a-zA-Z_]
    ;

fragment
Digit
    :   [0-9]
    ;

fragment
UniversalCharacterName
    :   '\\u' HexQuad
    |   '\\U' HexQuad HexQuad
    ;

fragment
HexQuad
    :   HexadecimalDigit HexadecimalDigit HexadecimalDigit HexadecimalDigit
    ;

Constant
    :   IntegerConstant
    |   FloatingConstant
    //|   EnumerationConstant
    |   CharacterConstant
    ;

fragment
IntegerConstant
    :   DecimalConstant IntegerSuffix?
    |   OctalConstant IntegerSuffix?
    |   HexadecimalConstant IntegerSuffix?
    |	BinaryConstant
    ;

fragment
BinaryConstant
	:	'0' [bB] [0-1]+
	;

fragment
DecimalConstant
    :   NonzeroDigit Digit*
    ;

fragment
OctalConstant
    :   '0' OctalDigit*
    ;

fragment
HexadecimalConstant
    :   HexadecimalPrefix HexadecimalDigit+
    ;

fragment
HexadecimalPrefix
    :   '0' [xX]
    ;

fragment
NonzeroDigit
    :   [1-9]
    ;

fragment
OctalDigit
    :   [0-7]
    ;

fragment
HexadecimalDigit
    :   [0-9a-fA-F]
    ;

fragment
IntegerSuffix
    :   UnsignedSuffix LongSuffix?
    |   UnsignedSuffix LongLongSuffix
    |   LongSuffix UnsignedSuffix?
    |   LongLongSuffix UnsignedSuffix?
    ;

fragment
UnsignedSuffix
    :   [uU]
    ;

fragment
LongSuffix
    :   [lL]
    ;

fragment
LongLongSuffix
    :   'll' | 'LL'
    ;

fragment
FloatingConstant
    :   DecimalFloatingConstant
    |   HexadecimalFloatingConstant
    ;

fragment
DecimalFloatingConstant
    :   FractionalConstant ExponentPart? FloatingSuffix?
    |   DigitSequence ExponentPart FloatingSuffix?
    ;

fragment
HexadecimalFloatingConstant
    :   HexadecimalPrefix (HexadecimalFractionalConstant | HexadecimalDigitSequence) BinaryExponentPart FloatingSuffix?
    ;

fragment
FractionalConstant
    :   DigitSequence? '.' DigitSequence
    |   DigitSequence '.'
    ;

fragment
ExponentPart
    :   [eE] Sign? DigitSequence
    ;

fragment
Sign
    :   [+-]
    ;

DigitSequence
    :   Digit+
    ;

fragment
HexadecimalFractionalConstant
    :   HexadecimalDigitSequence? '.' HexadecimalDigitSequence
    |   HexadecimalDigitSequence '.'
    ;

fragment
BinaryExponentPart
    :   [pP] Sign? DigitSequence
    ;

fragment
HexadecimalDigitSequence
    :   HexadecimalDigit+
    ;

fragment
FloatingSuffix
    :   [flFL]
    ;

fragment
CharacterConstant
    :   '\'' CCharSequence '\''
    |   'L\'' CCharSequence '\''
    |   'u\'' CCharSequence '\''
    |   'U\'' CCharSequence '\''
    ;

fragment
CCharSequence
    :   CChar+
    ;

fragment
CChar
    :   ~['\\\r\n]
    |   EscapeSequence
    ;

fragment
EscapeSequence
    :   SimpleEscapeSequence
    |   OctalEscapeSequence
    |   HexadecimalEscapeSequence
    |   UniversalCharacterName
    ;

fragment
SimpleEscapeSequence
    :   '\\' ['"?abfnrtv\\]
    ;

fragment
OctalEscapeSequence
    :   '\\' OctalDigit OctalDigit? OctalDigit?
    ;

fragment
HexadecimalEscapeSequence
    :   '\\x' HexadecimalDigit+
    ;

StringLiteral
    :   EncodingPrefix? '"' SCharSequence? '"'
    ;

fragment
EncodingPrefix
    :   'u8'
    |   'u'
    |   'U'
    |   'L'
    ;

fragment
SCharSequence
    :   SChar+
    ;

fragment
SChar
    :   ~["\\\r\n]
    |   EscapeSequence
    |   '\\\n'   // Added line
    |   '\\\r\n' // Added line
    ;

ComplexDefine
    :   '#' Whitespace? 'define'  ~[#\r\n]*
        -> skip
    ;

IncludeDirective
    :   '#' Whitespace? 'include' Whitespace? (('"' ~[\r\n]* '"') | ('<' ~[\r\n]* '>' )) Whitespace? Newline
        -> skip
    ;

// ignore the following asm blocks:
/*
    asm
    {
        mfspr x, 286;
    }
 */
AsmBlock
    :   'asm' ~'{'* '{' ~'}'* '}'
	-> skip
    ;

// ignore the lines generated by c preprocessor
// sample line : '#line 1 "/home/dm/files/dk1.h" 1'
LineAfterPreprocessing
    :   '#line' Whitespace* ~[\r\n]*
        -> skip
    ;

LineDirective
    :   '#' Whitespace? DecimalConstant Whitespace? StringLiteral ~[\r\n]*
        -> skip
    ;

PragmaDirective
    :   '#' Whitespace? 'pragma' Whitespace ~[\r\n]*
        -> skip
    ;

Whitespace
    :   [ \t]+
        -> skip
    ;

Newline
    :   (   '\r' '\n'?
        |   '\n'
        )
        -> skip
    ;

BlockComment
    :   '/*' .*? '*/'
        -> skip
    ;

LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;
FM_PLACEHOLDER: '${' ~'}'+? '}';
FM_IF: '<#if' ~'>'+? '>';
FM_IF_CLOSE: '</#if>';
FM_ELSE_IF: '<#elseif' ~'>'+? '>';
FM_ELSE: '<#else>';
FM_LIST: '<#list' .+? 'as' ~'>'+? '>';
FM_LIST_CLOSE: '</#list>';
FM_ImplicitToken1:'__extension__';
FM_ImplicitToken2:'__builtin_va_arg';
FM_ImplicitToken3:'__builtin_offsetof';
FM_ImplicitToken4:'__m128';
FM_ImplicitToken5:'__m128d';
FM_ImplicitToken6:'__m128i';
FM_ImplicitToken7:'__typeof__';
FM_ImplicitToken8:'__inline__';
FM_ImplicitToken9:'__stdcall';
FM_ImplicitToken10:'__declspec';
FM_ImplicitToken11:'__cdecl';
FM_ImplicitToken12:'__clrcall';
FM_ImplicitToken13:'__fastcall';
FM_ImplicitToken14:'__thiscall';
FM_ImplicitToken15:'__vectorcall';
FM_ImplicitToken16:'__asm';
FM_ImplicitToken17:'__attribute__';
FM_ImplicitToken18:'__asm__';
FM_ImplicitToken19:'__volatile__';