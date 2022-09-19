/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
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

/**
 * A Java 8 grammar for ANTLR 4 derived from the Java Language Specification
 * chapter 19.
 *
 * NOTE: This grammar results in a generated parser that is much slower
 *       than the Java 7 grammar in the grammars-v4/java directory. This
 *     one is, however, extremely close to the spec.
 *
 * You can test with
 *
 *  $ antlr4 Java8.g4
 *  $ javac *.java
 *  $ grun Java8 compilationUnit *.java
 *
 * Or,
~/antlr/code/grammars-v4/java8 $ java Test .
/Users/parrt/antlr/code/grammars-v4/java8/./Java8BaseListener.java
/Users/parrt/antlr/code/grammars-v4/java8/./Java8Lexer.java
/Users/parrt/antlr/code/grammars-v4/java8/./Java8Listener.java
/Users/parrt/antlr/code/grammars-v4/java8/./Java8Parser.java
/Users/parrt/antlr/code/grammars-v4/java8/./Test.java
Total lexer+parser time 30844ms.
 */
grammar Java8FreemarkerTemplate;

/*
 * Productions from §3 (Lexical Structure)
 */

literal
  : (IntegerLiteral)
  | (FloatingPointLiteral)
  | (BooleanLiteral)
  | (CharacterLiteral)
  | (StringLiteral)
  | NullLiteral
  ;

/*
 * Productions from §4 (Types, Values, and Variables)
 */

type
  : primitiveType
  | referenceType
  ;

primitiveType
  : (fm_annotationStar | annotation)* (fm_numericType | numericType)
  | (fm_annotationStar | annotation)* BOOLEAN 
  ;
fm_primitiveType: FM_PLACEHOLDER | FM_IF (fm_primitiveType | primitiveType) (FM_ELSE_IF (fm_primitiveType | primitiveType))* FM_ELSE (fm_primitiveType | primitiveType) FM_IF_CLOSE;

numericType
  : integralType
  | floatingPointType
  ;
fm_numericType: FM_PLACEHOLDER | FM_IF (fm_numericType | numericType) (FM_ELSE_IF (fm_numericType | numericType))* FM_ELSE (fm_numericType | numericType) FM_IF_CLOSE;

integralType
  : BYTE 
  | SHORT 
  | INT 
  | LONG 
  | CHAR 
  ;

floatingPointType
  : FLOAT 
  | DOUBLE 
  ;

referenceType
  : classOrInterfaceType
  | typeVariable
  | arrayType
  ;
fm_referenceType: FM_PLACEHOLDER | FM_IF (fm_referenceType | referenceType) (FM_ELSE_IF (fm_referenceType | referenceType))* FM_ELSE (fm_referenceType | referenceType) FM_IF_CLOSE;

classOrInterfaceType
  : ( classType_lfno_classOrInterfaceType
    | interfaceType_lfno_classOrInterfaceType
    )
    ( classType_lf_classOrInterfaceType
    | interfaceType_lf_classOrInterfaceType
    )*
  ;
fm_classOrInterfaceType: FM_PLACEHOLDER | FM_IF (fm_classOrInterfaceType | classOrInterfaceType) (FM_ELSE_IF (fm_classOrInterfaceType | classOrInterfaceType))* FM_ELSE (fm_classOrInterfaceType | classOrInterfaceType) FM_IF_CLOSE;

classType
  : (fm_annotationStar | annotation)* (fm_Identifier | Identifier) (fm_typeArgumentsOpt | typeArguments)?
  | (fm_classOrInterfaceType | classOrInterfaceType) DOT  (fm_annotationStar | annotation)* (fm_Identifier | Identifier) (fm_typeArgumentsOpt | typeArguments)?
  ;
fm_classType: FM_PLACEHOLDER | FM_IF (fm_classType | classType) (FM_ELSE_IF (fm_classType | classType))* FM_ELSE (fm_classType | classType) FM_IF_CLOSE;

classType_lf_classOrInterfaceType
  : DOT  (fm_annotationStar | annotation)* (fm_Identifier | Identifier) (fm_typeArgumentsOpt | typeArguments)?
  ;

classType_lfno_classOrInterfaceType
  : (fm_annotationStar | annotation)* (fm_Identifier | Identifier) (fm_typeArgumentsOpt | typeArguments)?
  ;

interfaceType
  : classType
  ;
fm_interfaceType: FM_PLACEHOLDER | FM_IF (fm_interfaceType | interfaceType) (FM_ELSE_IF (fm_interfaceType | interfaceType))* FM_ELSE (fm_interfaceType | interfaceType) FM_IF_CLOSE;

interfaceType_lf_classOrInterfaceType
  : classType_lf_classOrInterfaceType
  ;

interfaceType_lfno_classOrInterfaceType
  : classType_lfno_classOrInterfaceType
  ;

typeVariable
  : (fm_annotationStar | annotation)* (fm_Identifier | Identifier)
  ;
fm_typeVariable: FM_PLACEHOLDER | FM_IF (fm_typeVariable | typeVariable) (FM_ELSE_IF (fm_typeVariable | typeVariable))* FM_ELSE (fm_typeVariable | typeVariable) FM_IF_CLOSE;

arrayType
  : (fm_primitiveType | primitiveType) (fm_dims | dims)
  | (fm_classOrInterfaceType | classOrInterfaceType) (fm_dims | dims)
  | (fm_typeVariable | typeVariable) (fm_dims | dims)
  ;
fm_arrayType: FM_PLACEHOLDER | FM_IF (fm_arrayType | arrayType) (FM_ELSE_IF (fm_arrayType | arrayType))* FM_ELSE (fm_arrayType | arrayType) FM_IF_CLOSE;

dims
  : (fm_annotationStar | annotation)* LBRACK  RBRACK  ((fm_annotationStar | annotation)* LBRACK  RBRACK )*
  ;
fm_dims: FM_PLACEHOLDER | FM_IF (fm_dims | dims) (FM_ELSE_IF (fm_dims | dims))* FM_ELSE (fm_dims | dims) FM_IF_CLOSE;
fm_dimsOpt: FM_PLACEHOLDER | FM_IF (fm_dimsOpt | dims)? (FM_ELSE_IF (fm_dimsOpt | dims)?)* (FM_ELSE (fm_dimsOpt | dims)?)? FM_IF_CLOSE;

typeParameter
  : (fm_typeParameterModifierStar | typeParameterModifier)* (fm_Identifier | Identifier) (fm_typeBoundOpt | typeBound)?
  ;
fm_typeParameter: FM_PLACEHOLDER | FM_IF (fm_typeParameter | typeParameter) (FM_ELSE_IF (fm_typeParameter | typeParameter))* FM_ELSE (fm_typeParameter | typeParameter) FM_IF_CLOSE;

typeParameterModifier
  : annotation
  ;
fm_typeParameterModifierStar: FM_PLACEHOLDER | FM_IF (fm_typeParameterModifierStar | typeParameterModifier)* (FM_ELSE_IF (fm_typeParameterModifierStar | typeParameterModifier)*)* (FM_ELSE (fm_typeParameterModifierStar | typeParameterModifier)*)? FM_IF_CLOSE | FM_LIST (fm_typeParameterModifierStar | typeParameterModifier)* FM_LIST_CLOSE;

typeBound
  : EXTENDS  (fm_typeVariable | typeVariable)
  | EXTENDS  (fm_classOrInterfaceType | classOrInterfaceType) (fm_additionalBoundStar | additionalBound)*
  ;
fm_typeBoundOpt: FM_PLACEHOLDER | FM_IF (fm_typeBoundOpt | typeBound)? (FM_ELSE_IF (fm_typeBoundOpt | typeBound)?)* (FM_ELSE (fm_typeBoundOpt | typeBound)?)? FM_IF_CLOSE;

additionalBound
  : BITAND  (fm_interfaceType | interfaceType)
  ;
fm_additionalBoundStar: FM_PLACEHOLDER | FM_IF (fm_additionalBoundStar | additionalBound)* (FM_ELSE_IF (fm_additionalBoundStar | additionalBound)*)* (FM_ELSE (fm_additionalBoundStar | additionalBound)*)? FM_IF_CLOSE | FM_LIST (fm_additionalBoundStar | additionalBound)* FM_LIST_CLOSE;

typeArguments
  : LT  (fm_typeArgumentList | typeArgumentList) GT 
  ;
fm_typeArgumentsOpt: FM_PLACEHOLDER | FM_IF (fm_typeArgumentsOpt | typeArguments)? (FM_ELSE_IF (fm_typeArgumentsOpt | typeArguments)?)* (FM_ELSE (fm_typeArgumentsOpt | typeArguments)?)? FM_IF_CLOSE;

typeArgumentList
  : (fm_typeArgument | typeArgument) (COMMA  (fm_typeArgument | typeArgument))*
  ;
fm_typeArgumentList: FM_PLACEHOLDER | FM_IF (fm_typeArgumentList | typeArgumentList) (FM_ELSE_IF (fm_typeArgumentList | typeArgumentList))* FM_ELSE (fm_typeArgumentList | typeArgumentList) FM_IF_CLOSE;

typeArgument
  : referenceType
  | wildcard
  ;
fm_typeArgument: FM_PLACEHOLDER | FM_IF (fm_typeArgument | typeArgument) (FM_ELSE_IF (fm_typeArgument | typeArgument))* FM_ELSE (fm_typeArgument | typeArgument) FM_IF_CLOSE;

wildcard
  : (fm_annotationStar | annotation)* QUESTION  (fm_wildcardBoundsOpt | wildcardBounds)?
  ;

wildcardBounds
  : EXTENDS  (fm_referenceType | referenceType)
  | SUPER  (fm_referenceType | referenceType)
  ;
fm_wildcardBoundsOpt: FM_PLACEHOLDER | FM_IF (fm_wildcardBoundsOpt | wildcardBounds)? (FM_ELSE_IF (fm_wildcardBoundsOpt | wildcardBounds)?)* (FM_ELSE (fm_wildcardBoundsOpt | wildcardBounds)?)? FM_IF_CLOSE;

/*
 * Productions from §6 (Names)
 */

packageName
  : Identifier
  | fm_packageName DOT ( fm_Identifier | Identifier ) 
  | packageName DOT  (fm_Identifier | Identifier)
  ;
fm_packageName: FM_PLACEHOLDER | FM_IF (fm_packageName | packageName) (FM_ELSE_IF (fm_packageName | packageName))* FM_ELSE (fm_packageName | packageName) FM_IF_CLOSE;

typeName
  : Identifier
  | (fm_packageOrTypeName | packageOrTypeName) DOT  (fm_Identifier | Identifier)
  ;
fm_typeName: FM_PLACEHOLDER | FM_IF (fm_typeName | typeName) (FM_ELSE_IF (fm_typeName | typeName))* FM_ELSE (fm_typeName | typeName) FM_IF_CLOSE;

packageOrTypeName
  : Identifier
  | fm_packageOrTypeName DOT ( fm_Identifier | Identifier ) 
  | packageOrTypeName DOT  (fm_Identifier | Identifier)
  ;
fm_packageOrTypeName: FM_PLACEHOLDER | FM_IF (fm_packageOrTypeName | packageOrTypeName) (FM_ELSE_IF (fm_packageOrTypeName | packageOrTypeName))* FM_ELSE (fm_packageOrTypeName | packageOrTypeName) FM_IF_CLOSE;

expressionName
  : Identifier
  | (fm_ambiguousName | ambiguousName) DOT  (fm_Identifier | Identifier)
  ;
fm_expressionName: FM_PLACEHOLDER | FM_IF (fm_expressionName | expressionName) (FM_ELSE_IF (fm_expressionName | expressionName))* FM_ELSE (fm_expressionName | expressionName) FM_IF_CLOSE;

methodName
  : Identifier
  ;
fm_methodName: FM_PLACEHOLDER | FM_IF (fm_methodName | methodName) (FM_ELSE_IF (fm_methodName | methodName))* FM_ELSE (fm_methodName | methodName) FM_IF_CLOSE;

ambiguousName
  : Identifier
  | fm_ambiguousName DOT ( fm_Identifier | Identifier ) 
  | ambiguousName DOT  (fm_Identifier | Identifier)
  ;
fm_ambiguousName: FM_PLACEHOLDER | FM_IF (fm_ambiguousName | ambiguousName) (FM_ELSE_IF (fm_ambiguousName | ambiguousName))* FM_ELSE (fm_ambiguousName | ambiguousName) FM_IF_CLOSE;

/*
 * Productions from §7 (Packages)
 */

compilationUnit
  : (fm_packageDeclarationOpt | packageDeclaration)? (fm_importDeclarationStar | importDeclaration)* (fm_typeDeclarationStar | typeDeclaration)* EOF
  ;

packageDeclaration
  : (fm_packageModifierStar | packageModifier)* PACKAGE  (fm_packageName | packageName) SEMI 
  ;
fm_packageDeclarationOpt: FM_PLACEHOLDER | FM_IF (fm_packageDeclarationOpt | packageDeclaration)? (FM_ELSE_IF (fm_packageDeclarationOpt | packageDeclaration)?)* (FM_ELSE (fm_packageDeclarationOpt | packageDeclaration)?)? FM_IF_CLOSE;
  
packageModifier
  : annotation
  ;
fm_packageModifierStar: FM_PLACEHOLDER | FM_IF (fm_packageModifierStar | packageModifier)* (FM_ELSE_IF (fm_packageModifierStar | packageModifier)*)* (FM_ELSE (fm_packageModifierStar | packageModifier)*)? FM_IF_CLOSE | FM_LIST (fm_packageModifierStar | packageModifier)* FM_LIST_CLOSE;

importDeclaration
  : singleTypeImportDeclaration
  | typeImportOnDemandDeclaration
  | singleStaticImportDeclaration
  | staticImportOnDemandDeclaration
  ;
fm_importDeclarationStar: FM_PLACEHOLDER | FM_IF (fm_importDeclarationStar | importDeclaration)* (FM_ELSE_IF (fm_importDeclarationStar | importDeclaration)*)* (FM_ELSE (fm_importDeclarationStar | importDeclaration)*)? FM_IF_CLOSE | FM_LIST (fm_importDeclarationStar | importDeclaration)* FM_LIST_CLOSE;

singleTypeImportDeclaration
  : IMPORT  (fm_typeName | typeName) SEMI 
  ;

typeImportOnDemandDeclaration
  : IMPORT  (fm_packageOrTypeName | packageOrTypeName) DOT  MUL  SEMI 
  ;

singleStaticImportDeclaration
  : IMPORT  STATIC  (fm_typeName | typeName) DOT  (fm_Identifier | Identifier) SEMI 
  ;

staticImportOnDemandDeclaration
  : IMPORT  STATIC  (fm_typeName | typeName) DOT  MUL  SEMI 
  ;

typeDeclaration
  : classDeclaration
  | interfaceDeclaration
  | SEMI 
  ;
fm_typeDeclarationStar: FM_PLACEHOLDER | FM_IF (fm_typeDeclarationStar | typeDeclaration)* (FM_ELSE_IF (fm_typeDeclarationStar | typeDeclaration)*)* (FM_ELSE (fm_typeDeclarationStar | typeDeclaration)*)? FM_IF_CLOSE | FM_LIST (fm_typeDeclarationStar | typeDeclaration)* FM_LIST_CLOSE;

/*
 * Productions from §8 (Classes)
 */

classDeclaration
  : normalClassDeclaration
  | enumDeclaration
  ;

normalClassDeclaration
  : (fm_classModifierStar | classModifier)* CLASS  (fm_Identifier | Identifier) (fm_typeParametersOpt | typeParameters)? (fm_superclassOpt | superclass)? (fm_superinterfacesOpt | superinterfaces)? (fm_classBody | classBody)
  ;

classModifier
  : annotation
  | PUBLIC 
  | PROTECTED 
  | PRIVATE 
  | ABSTRACT 
  | STATIC 
  | FINAL 
  | STRICTFP 
  ;
fm_classModifierStar: FM_PLACEHOLDER | FM_IF (fm_classModifierStar | classModifier)* (FM_ELSE_IF (fm_classModifierStar | classModifier)*)* (FM_ELSE (fm_classModifierStar | classModifier)*)? FM_IF_CLOSE | FM_LIST (fm_classModifierStar | classModifier)* FM_LIST_CLOSE;

typeParameters
  : LT  (fm_typeParameterList | typeParameterList) GT 
  ;
fm_typeParameters: FM_PLACEHOLDER | FM_IF (fm_typeParameters | typeParameters) (FM_ELSE_IF (fm_typeParameters | typeParameters))* FM_ELSE (fm_typeParameters | typeParameters) FM_IF_CLOSE;
fm_typeParametersOpt: FM_PLACEHOLDER | FM_IF (fm_typeParametersOpt | typeParameters)? (FM_ELSE_IF (fm_typeParametersOpt | typeParameters)?)* (FM_ELSE (fm_typeParametersOpt | typeParameters)?)? FM_IF_CLOSE;

typeParameterList
  : (fm_typeParameter | typeParameter) (COMMA  (fm_typeParameter | typeParameter))*
  ;
fm_typeParameterList: FM_PLACEHOLDER | FM_IF (fm_typeParameterList | typeParameterList) (FM_ELSE_IF (fm_typeParameterList | typeParameterList))* FM_ELSE (fm_typeParameterList | typeParameterList) FM_IF_CLOSE;

superclass
  : EXTENDS  (fm_classType | classType)
  ;
fm_superclassOpt: FM_PLACEHOLDER | FM_IF (fm_superclassOpt | superclass)? (FM_ELSE_IF (fm_superclassOpt | superclass)?)* (FM_ELSE (fm_superclassOpt | superclass)?)? FM_IF_CLOSE;

superinterfaces
  : IMPLEMENTS  (fm_interfaceTypeList | interfaceTypeList)
  ;
fm_superinterfacesOpt: FM_PLACEHOLDER | FM_IF (fm_superinterfacesOpt | superinterfaces)? (FM_ELSE_IF (fm_superinterfacesOpt | superinterfaces)?)* (FM_ELSE (fm_superinterfacesOpt | superinterfaces)?)? FM_IF_CLOSE;

interfaceTypeList
  : (fm_interfaceType | interfaceType) (COMMA  (fm_interfaceType | interfaceType))*
  ;
fm_interfaceTypeList: FM_PLACEHOLDER | FM_IF (fm_interfaceTypeList | interfaceTypeList) (FM_ELSE_IF (fm_interfaceTypeList | interfaceTypeList))* FM_ELSE (fm_interfaceTypeList | interfaceTypeList) FM_IF_CLOSE;

classBody
  : LBRACE  (fm_classBodyDeclarationStar | classBodyDeclaration)* RBRACE 
  ;
fm_classBody: FM_PLACEHOLDER | FM_IF (fm_classBody | classBody) (FM_ELSE_IF (fm_classBody | classBody))* FM_ELSE (fm_classBody | classBody) FM_IF_CLOSE;
fm_classBodyOpt: FM_PLACEHOLDER | FM_IF (fm_classBodyOpt | classBody)? (FM_ELSE_IF (fm_classBodyOpt | classBody)?)* (FM_ELSE (fm_classBodyOpt | classBody)?)? FM_IF_CLOSE;

classBodyDeclaration
  : classMemberDeclaration
  | instanceInitializer
  | staticInitializer
  | constructorDeclaration
  ;
fm_classBodyDeclarationStar: FM_PLACEHOLDER | FM_IF (fm_classBodyDeclarationStar | classBodyDeclaration)* (FM_ELSE_IF (fm_classBodyDeclarationStar | classBodyDeclaration)*)* (FM_ELSE (fm_classBodyDeclarationStar | classBodyDeclaration)*)? FM_IF_CLOSE | FM_LIST (fm_classBodyDeclarationStar | classBodyDeclaration)* FM_LIST_CLOSE;

classMemberDeclaration
  : fieldDeclaration
  | methodDeclaration
  | classDeclaration
  | interfaceDeclaration
  | SEMI 
  ;

fieldDeclaration
  : (fm_fieldModifierStar | fieldModifier)* (fm_unannType | unannType) (fm_variableDeclaratorList | variableDeclaratorList) SEMI 
  ;

fieldModifier
  : annotation
  | PUBLIC 
  | PROTECTED 
  | PRIVATE 
  | STATIC 
  | FINAL 
  | TRANSIENT 
  | VOLATILE 
  ;
fm_fieldModifierStar: FM_PLACEHOLDER | FM_IF (fm_fieldModifierStar | fieldModifier)* (FM_ELSE_IF (fm_fieldModifierStar | fieldModifier)*)* (FM_ELSE (fm_fieldModifierStar | fieldModifier)*)? FM_IF_CLOSE | FM_LIST (fm_fieldModifierStar | fieldModifier)* FM_LIST_CLOSE;

variableDeclaratorList
  : variableDeclarator
  | fm_variableDeclaratorList COMMA ( fm_variableDeclarator | variableDeclarator ) 
  | variableDeclaratorList COMMA  (fm_variableDeclarator | variableDeclarator)
  ;
fm_variableDeclaratorList: FM_PLACEHOLDER | FM_IF (fm_variableDeclaratorList | variableDeclaratorList) (FM_ELSE_IF (fm_variableDeclaratorList | variableDeclaratorList))* FM_ELSE (fm_variableDeclaratorList | variableDeclaratorList) FM_IF_CLOSE;

variableDeclarator
  : (fm_variableDeclaratorId | variableDeclaratorId) (ASSIGN  (fm_variableInitializer | variableInitializer))?
  ;
fm_variableDeclarator: FM_PLACEHOLDER | FM_IF (fm_variableDeclarator | variableDeclarator) (FM_ELSE_IF (fm_variableDeclarator | variableDeclarator))* FM_ELSE (fm_variableDeclarator | variableDeclarator) FM_IF_CLOSE;

variableDeclaratorId
  : (fm_Identifier | Identifier) (fm_dimsOpt | dims)?
  ;
fm_variableDeclaratorId: FM_PLACEHOLDER | FM_IF (fm_variableDeclaratorId | variableDeclaratorId) (FM_ELSE_IF (fm_variableDeclaratorId | variableDeclaratorId))* FM_ELSE (fm_variableDeclaratorId | variableDeclaratorId) FM_IF_CLOSE;

variableInitializer
  : expression
  | arrayInitializer
  ;
fm_variableInitializer: FM_PLACEHOLDER | FM_IF (fm_variableInitializer | variableInitializer) (FM_ELSE_IF (fm_variableInitializer | variableInitializer))* FM_ELSE (fm_variableInitializer | variableInitializer) FM_IF_CLOSE;

unannType
  : unannPrimitiveType
  | unannReferenceType
  ;
fm_unannType: FM_PLACEHOLDER | FM_IF (fm_unannType | unannType) (FM_ELSE_IF (fm_unannType | unannType))* FM_ELSE (fm_unannType | unannType) FM_IF_CLOSE;

unannPrimitiveType
  : numericType
  | BOOLEAN 
  ;
fm_unannPrimitiveType: FM_PLACEHOLDER | FM_IF (fm_unannPrimitiveType | unannPrimitiveType) (FM_ELSE_IF (fm_unannPrimitiveType | unannPrimitiveType))* FM_ELSE (fm_unannPrimitiveType | unannPrimitiveType) FM_IF_CLOSE;

unannReferenceType
  : unannClassOrInterfaceType
  | unannTypeVariable
  | unannArrayType
  ;

unannClassOrInterfaceType
  : ( unannClassType_lfno_unannClassOrInterfaceType
    | unannInterfaceType_lfno_unannClassOrInterfaceType
    )
    ( unannClassType_lf_unannClassOrInterfaceType
    | unannInterfaceType_lf_unannClassOrInterfaceType
    )*
  ;
fm_unannClassOrInterfaceType: FM_PLACEHOLDER | FM_IF (fm_unannClassOrInterfaceType | unannClassOrInterfaceType) (FM_ELSE_IF (fm_unannClassOrInterfaceType | unannClassOrInterfaceType))* FM_ELSE (fm_unannClassOrInterfaceType | unannClassOrInterfaceType) FM_IF_CLOSE;

unannClassType
  : (fm_Identifier | Identifier) (fm_typeArgumentsOpt | typeArguments)?
  | (fm_unannClassOrInterfaceType | unannClassOrInterfaceType) DOT  (fm_annotationStar | annotation)* (fm_Identifier | Identifier) (fm_typeArgumentsOpt | typeArguments)?
  ;
fm_unannClassType: FM_PLACEHOLDER | FM_IF (fm_unannClassType | unannClassType) (FM_ELSE_IF (fm_unannClassType | unannClassType))* FM_ELSE (fm_unannClassType | unannClassType) FM_IF_CLOSE;

unannClassType_lf_unannClassOrInterfaceType
  : DOT  (fm_annotationStar | annotation)* (fm_Identifier | Identifier) (fm_typeArgumentsOpt | typeArguments)?
  ;

unannClassType_lfno_unannClassOrInterfaceType
  : (fm_Identifier | Identifier) (fm_typeArgumentsOpt | typeArguments)?
  ;

unannInterfaceType
  : unannClassType
  ;

unannInterfaceType_lf_unannClassOrInterfaceType
  : unannClassType_lf_unannClassOrInterfaceType
  ;

unannInterfaceType_lfno_unannClassOrInterfaceType
  : unannClassType_lfno_unannClassOrInterfaceType
  ;

unannTypeVariable
  : Identifier
  ;
fm_unannTypeVariable: FM_PLACEHOLDER | FM_IF (fm_unannTypeVariable | unannTypeVariable) (FM_ELSE_IF (fm_unannTypeVariable | unannTypeVariable))* FM_ELSE (fm_unannTypeVariable | unannTypeVariable) FM_IF_CLOSE;

unannArrayType
  : (fm_unannPrimitiveType | unannPrimitiveType) (fm_dims | dims)
  | (fm_unannClassOrInterfaceType | unannClassOrInterfaceType) (fm_dims | dims)
  | (fm_unannTypeVariable | unannTypeVariable) (fm_dims | dims)
  ;

methodDeclaration
  : (fm_methodModifierStar | methodModifier)* (fm_methodHeader | methodHeader) (fm_methodBody | methodBody)
  ;

methodModifier
  : annotation
  | PUBLIC 
  | PROTECTED 
  | PRIVATE 
  | ABSTRACT 
  | STATIC 
  | FINAL 
  | SYNCHRONIZED 
  | NATIVE 
  | STRICTFP 
  ;
fm_methodModifierStar: FM_PLACEHOLDER | FM_IF (fm_methodModifierStar | methodModifier)* (FM_ELSE_IF (fm_methodModifierStar | methodModifier)*)* (FM_ELSE (fm_methodModifierStar | methodModifier)*)? FM_IF_CLOSE | FM_LIST (fm_methodModifierStar | methodModifier)* FM_LIST_CLOSE;

methodHeader
  : (fm_result | result) (fm_methodDeclarator | methodDeclarator) (fm_throws_Opt | throws_)?
  | (fm_typeParameters | typeParameters) (fm_annotationStar | annotation)* (fm_result | result) (fm_methodDeclarator | methodDeclarator) (fm_throws_Opt | throws_)?
  ;
fm_methodHeader: FM_PLACEHOLDER | FM_IF (fm_methodHeader | methodHeader) (FM_ELSE_IF (fm_methodHeader | methodHeader))* FM_ELSE (fm_methodHeader | methodHeader) FM_IF_CLOSE;

result
  : unannType
  | VOID 
  ;
fm_result: FM_PLACEHOLDER | FM_IF (fm_result | result) (FM_ELSE_IF (fm_result | result))* FM_ELSE (fm_result | result) FM_IF_CLOSE;

methodDeclarator
  : (fm_Identifier | Identifier) LPAREN  (fm_formalParameterListOpt | formalParameterList)? RPAREN  (fm_dimsOpt | dims)?
  ;
fm_methodDeclarator: FM_PLACEHOLDER | FM_IF (fm_methodDeclarator | methodDeclarator) (FM_ELSE_IF (fm_methodDeclarator | methodDeclarator))* FM_ELSE (fm_methodDeclarator | methodDeclarator) FM_IF_CLOSE;

formalParameterList
  : (fm_formalParameters | formalParameters) COMMA  (fm_lastFormalParameter | lastFormalParameter)
  | lastFormalParameter
  ;
fm_formalParameterListOpt: FM_PLACEHOLDER | FM_IF (fm_formalParameterListOpt | formalParameterList)? (FM_ELSE_IF (fm_formalParameterListOpt | formalParameterList)?)* (FM_ELSE (fm_formalParameterListOpt | formalParameterList)?)? FM_IF_CLOSE;

formalParameters
  : (fm_formalParameter | formalParameter) (COMMA  (fm_formalParameter | formalParameter))*
  | (fm_receiverParameter | receiverParameter) (COMMA  (fm_formalParameter | formalParameter))*
  ;
fm_formalParameters: FM_PLACEHOLDER | FM_IF (fm_formalParameters | formalParameters) (FM_ELSE_IF (fm_formalParameters | formalParameters))* FM_ELSE (fm_formalParameters | formalParameters) FM_IF_CLOSE;

formalParameter
  : (fm_variableModifierStar | variableModifier)* (fm_unannType | unannType) (fm_variableDeclaratorId | variableDeclaratorId)
  ;
fm_formalParameter: FM_PLACEHOLDER | FM_IF (fm_formalParameter | formalParameter) (FM_ELSE_IF (fm_formalParameter | formalParameter))* FM_ELSE (fm_formalParameter | formalParameter) FM_IF_CLOSE;

variableModifier
  : annotation
  | FINAL 
  ;
fm_variableModifierStar: FM_PLACEHOLDER | FM_IF (fm_variableModifierStar | variableModifier)* (FM_ELSE_IF (fm_variableModifierStar | variableModifier)*)* (FM_ELSE (fm_variableModifierStar | variableModifier)*)? FM_IF_CLOSE | FM_LIST (fm_variableModifierStar | variableModifier)* FM_LIST_CLOSE;

lastFormalParameter
  : (fm_variableModifierStar | variableModifier)* (fm_unannType | unannType) (fm_annotationStar | annotation)* ELLIPSIS  (fm_variableDeclaratorId | variableDeclaratorId)
  | formalParameter
  ;
fm_lastFormalParameter: FM_PLACEHOLDER | FM_IF (fm_lastFormalParameter | lastFormalParameter) (FM_ELSE_IF (fm_lastFormalParameter | lastFormalParameter))* FM_ELSE (fm_lastFormalParameter | lastFormalParameter) FM_IF_CLOSE;

receiverParameter
  : (fm_annotationStar | annotation)* (fm_unannType | unannType) ((fm_Identifier | Identifier) DOT )? THIS 
  ;
fm_receiverParameter: FM_PLACEHOLDER | FM_IF (fm_receiverParameter | receiverParameter) (FM_ELSE_IF (fm_receiverParameter | receiverParameter))* FM_ELSE (fm_receiverParameter | receiverParameter) FM_IF_CLOSE;

throws_
  : THROWS  (fm_exceptionTypeList | exceptionTypeList)
  ;
fm_throws_Opt: FM_PLACEHOLDER | FM_IF (fm_throws_Opt | throws_)? (FM_ELSE_IF (fm_throws_Opt | throws_)?)* (FM_ELSE (fm_throws_Opt | throws_)?)? FM_IF_CLOSE;

exceptionTypeList
  : (fm_exceptionType | exceptionType) (COMMA  (fm_exceptionType | exceptionType))*
  ;
fm_exceptionTypeList: FM_PLACEHOLDER | FM_IF (fm_exceptionTypeList | exceptionTypeList) (FM_ELSE_IF (fm_exceptionTypeList | exceptionTypeList))* FM_ELSE (fm_exceptionTypeList | exceptionTypeList) FM_IF_CLOSE;

exceptionType
  : classType
  | typeVariable
  ;
fm_exceptionType: FM_PLACEHOLDER | FM_IF (fm_exceptionType | exceptionType) (FM_ELSE_IF (fm_exceptionType | exceptionType))* FM_ELSE (fm_exceptionType | exceptionType) FM_IF_CLOSE;

methodBody
  : block
  | SEMI 
  ;
fm_methodBody: FM_PLACEHOLDER | FM_IF (fm_methodBody | methodBody) (FM_ELSE_IF (fm_methodBody | methodBody))* FM_ELSE (fm_methodBody | methodBody) FM_IF_CLOSE;

instanceInitializer
  : block
  ;

staticInitializer
  : STATIC  (fm_block | block)
  ;

constructorDeclaration
  : (fm_constructorModifierStar | constructorModifier)* (fm_constructorDeclarator | constructorDeclarator) (fm_throws_Opt | throws_)? (fm_constructorBody | constructorBody)
  ;

constructorModifier
  : annotation
  | PUBLIC 
  | PROTECTED 
  | PRIVATE 
  ;
fm_constructorModifierStar: FM_PLACEHOLDER | FM_IF (fm_constructorModifierStar | constructorModifier)* (FM_ELSE_IF (fm_constructorModifierStar | constructorModifier)*)* (FM_ELSE (fm_constructorModifierStar | constructorModifier)*)? FM_IF_CLOSE | FM_LIST (fm_constructorModifierStar | constructorModifier)* FM_LIST_CLOSE;

constructorDeclarator
  : (fm_typeParametersOpt | typeParameters)? (fm_simpleTypeName | simpleTypeName) LPAREN  (fm_formalParameterListOpt | formalParameterList)? RPAREN 
  ;
fm_constructorDeclarator: FM_PLACEHOLDER | FM_IF (fm_constructorDeclarator | constructorDeclarator) (FM_ELSE_IF (fm_constructorDeclarator | constructorDeclarator))* FM_ELSE (fm_constructorDeclarator | constructorDeclarator) FM_IF_CLOSE;

simpleTypeName
  : Identifier
  ;
fm_simpleTypeName: FM_PLACEHOLDER | FM_IF (fm_simpleTypeName | simpleTypeName) (FM_ELSE_IF (fm_simpleTypeName | simpleTypeName))* FM_ELSE (fm_simpleTypeName | simpleTypeName) FM_IF_CLOSE;

constructorBody
  : LBRACE  (fm_explicitConstructorInvocationOpt | explicitConstructorInvocation)? (fm_blockStatementStar | blockStatement)* RBRACE 
  ;
fm_constructorBody: FM_PLACEHOLDER | FM_IF (fm_constructorBody | constructorBody) (FM_ELSE_IF (fm_constructorBody | constructorBody))* FM_ELSE (fm_constructorBody | constructorBody) FM_IF_CLOSE;

explicitConstructorInvocation
  : (fm_typeArgumentsOpt | typeArguments)? THIS  LPAREN  (fm_argumentListOpt | argumentList)? RPAREN  SEMI 
  | (fm_typeArgumentsOpt | typeArguments)? SUPER  LPAREN  (fm_argumentListOpt | argumentList)? RPAREN  SEMI 
  | (fm_expressionName | expressionName) DOT  (fm_typeArgumentsOpt | typeArguments)? SUPER  LPAREN  (fm_argumentListOpt | argumentList)? RPAREN  SEMI 
  | (fm_primary | primary) DOT  (fm_typeArgumentsOpt | typeArguments)? SUPER  LPAREN  (fm_argumentListOpt | argumentList)? RPAREN  SEMI 
  ;
fm_explicitConstructorInvocationOpt: FM_PLACEHOLDER | FM_IF (fm_explicitConstructorInvocationOpt | explicitConstructorInvocation)? (FM_ELSE_IF (fm_explicitConstructorInvocationOpt | explicitConstructorInvocation)?)* (FM_ELSE (fm_explicitConstructorInvocationOpt | explicitConstructorInvocation)?)? FM_IF_CLOSE;

enumDeclaration
  : (fm_classModifierStar | classModifier)* ENUM  (fm_Identifier | Identifier) (fm_superinterfacesOpt | superinterfaces)? (fm_enumBody | enumBody)
  ;

enumBody
  : LBRACE  (fm_enumConstantListOpt | enumConstantList)? COMMA ? (fm_enumBodyDeclarationsOpt | enumBodyDeclarations)? RBRACE 
  ;
fm_enumBody: FM_PLACEHOLDER | FM_IF (fm_enumBody | enumBody) (FM_ELSE_IF (fm_enumBody | enumBody))* FM_ELSE (fm_enumBody | enumBody) FM_IF_CLOSE;

enumConstantList
  : (fm_enumConstant | enumConstant) (COMMA  (fm_enumConstant | enumConstant))*
  ;
fm_enumConstantListOpt: FM_PLACEHOLDER | FM_IF (fm_enumConstantListOpt | enumConstantList)? (FM_ELSE_IF (fm_enumConstantListOpt | enumConstantList)?)* (FM_ELSE (fm_enumConstantListOpt | enumConstantList)?)? FM_IF_CLOSE;

enumConstant
  : (fm_enumConstantModifierStar | enumConstantModifier)* (fm_Identifier | Identifier) (LPAREN  (fm_argumentListOpt | argumentList)? RPAREN )? (fm_classBodyOpt | classBody)?
  ;
fm_enumConstant: FM_PLACEHOLDER | FM_IF (fm_enumConstant | enumConstant) (FM_ELSE_IF (fm_enumConstant | enumConstant))* FM_ELSE (fm_enumConstant | enumConstant) FM_IF_CLOSE;

enumConstantModifier
  : annotation
  ;
fm_enumConstantModifierStar: FM_PLACEHOLDER | FM_IF (fm_enumConstantModifierStar | enumConstantModifier)* (FM_ELSE_IF (fm_enumConstantModifierStar | enumConstantModifier)*)* (FM_ELSE (fm_enumConstantModifierStar | enumConstantModifier)*)? FM_IF_CLOSE | FM_LIST (fm_enumConstantModifierStar | enumConstantModifier)* FM_LIST_CLOSE;

enumBodyDeclarations
  : SEMI  (fm_classBodyDeclarationStar | classBodyDeclaration)*
  ;
fm_enumBodyDeclarationsOpt: FM_PLACEHOLDER | FM_IF (fm_enumBodyDeclarationsOpt | enumBodyDeclarations)? (FM_ELSE_IF (fm_enumBodyDeclarationsOpt | enumBodyDeclarations)?)* (FM_ELSE (fm_enumBodyDeclarationsOpt | enumBodyDeclarations)?)? FM_IF_CLOSE;

/*
 * Productions from §9 (Interfaces)
 */

interfaceDeclaration
  : normalInterfaceDeclaration
  | annotationTypeDeclaration
  ;

normalInterfaceDeclaration
  : (fm_interfaceModifierStar | interfaceModifier)* INTERFACE  (fm_Identifier | Identifier) (fm_typeParametersOpt | typeParameters)? (fm_extendsInterfacesOpt | extendsInterfaces)? (fm_interfaceBody | interfaceBody)
  ;

interfaceModifier
  : annotation
  | PUBLIC 
  | PROTECTED 
  | PRIVATE 
  | ABSTRACT 
  | STATIC 
  | STRICTFP 
  ;
fm_interfaceModifierStar: FM_PLACEHOLDER | FM_IF (fm_interfaceModifierStar | interfaceModifier)* (FM_ELSE_IF (fm_interfaceModifierStar | interfaceModifier)*)* (FM_ELSE (fm_interfaceModifierStar | interfaceModifier)*)? FM_IF_CLOSE | FM_LIST (fm_interfaceModifierStar | interfaceModifier)* FM_LIST_CLOSE;

extendsInterfaces
  : EXTENDS  (fm_interfaceTypeList | interfaceTypeList)
  ;
fm_extendsInterfacesOpt: FM_PLACEHOLDER | FM_IF (fm_extendsInterfacesOpt | extendsInterfaces)? (FM_ELSE_IF (fm_extendsInterfacesOpt | extendsInterfaces)?)* (FM_ELSE (fm_extendsInterfacesOpt | extendsInterfaces)?)? FM_IF_CLOSE;

interfaceBody
  : LBRACE  (fm_interfaceMemberDeclarationStar | interfaceMemberDeclaration)* RBRACE 
  ;
fm_interfaceBody: FM_PLACEHOLDER | FM_IF (fm_interfaceBody | interfaceBody) (FM_ELSE_IF (fm_interfaceBody | interfaceBody))* FM_ELSE (fm_interfaceBody | interfaceBody) FM_IF_CLOSE;

interfaceMemberDeclaration
  : constantDeclaration
  | interfaceMethodDeclaration
  | classDeclaration
  | interfaceDeclaration
  | SEMI 
  ;
fm_interfaceMemberDeclarationStar: FM_PLACEHOLDER | FM_IF (fm_interfaceMemberDeclarationStar | interfaceMemberDeclaration)* (FM_ELSE_IF (fm_interfaceMemberDeclarationStar | interfaceMemberDeclaration)*)* (FM_ELSE (fm_interfaceMemberDeclarationStar | interfaceMemberDeclaration)*)? FM_IF_CLOSE | FM_LIST (fm_interfaceMemberDeclarationStar | interfaceMemberDeclaration)* FM_LIST_CLOSE;

constantDeclaration
  : (fm_constantModifierStar | constantModifier)* (fm_unannType | unannType) (fm_variableDeclaratorList | variableDeclaratorList) SEMI 
  ;

constantModifier
  : annotation
  | PUBLIC 
  | STATIC 
  | FINAL 
  ;
fm_constantModifierStar: FM_PLACEHOLDER | FM_IF (fm_constantModifierStar | constantModifier)* (FM_ELSE_IF (fm_constantModifierStar | constantModifier)*)* (FM_ELSE (fm_constantModifierStar | constantModifier)*)? FM_IF_CLOSE | FM_LIST (fm_constantModifierStar | constantModifier)* FM_LIST_CLOSE;

interfaceMethodDeclaration
  : (fm_interfaceMethodModifierStar | interfaceMethodModifier)* (fm_methodHeader | methodHeader) (fm_methodBody | methodBody)
  ;

interfaceMethodModifier
  : annotation
  | PUBLIC 
  | ABSTRACT 
  | DEFAULT 
  | STATIC 
  | STRICTFP 
  ;
fm_interfaceMethodModifierStar: FM_PLACEHOLDER | FM_IF (fm_interfaceMethodModifierStar | interfaceMethodModifier)* (FM_ELSE_IF (fm_interfaceMethodModifierStar | interfaceMethodModifier)*)* (FM_ELSE (fm_interfaceMethodModifierStar | interfaceMethodModifier)*)? FM_IF_CLOSE | FM_LIST (fm_interfaceMethodModifierStar | interfaceMethodModifier)* FM_LIST_CLOSE;

annotationTypeDeclaration
  : (fm_interfaceModifierStar | interfaceModifier)* AT  INTERFACE  (fm_Identifier | Identifier) (fm_annotationTypeBody | annotationTypeBody)
  ;

annotationTypeBody
  : LBRACE  (fm_annotationTypeMemberDeclarationStar | annotationTypeMemberDeclaration)* RBRACE 
  ;
fm_annotationTypeBody: FM_PLACEHOLDER | FM_IF (fm_annotationTypeBody | annotationTypeBody) (FM_ELSE_IF (fm_annotationTypeBody | annotationTypeBody))* FM_ELSE (fm_annotationTypeBody | annotationTypeBody) FM_IF_CLOSE;

annotationTypeMemberDeclaration
  : annotationTypeElementDeclaration
  | constantDeclaration
  | classDeclaration
  | interfaceDeclaration
  | SEMI 
  ;
fm_annotationTypeMemberDeclarationStar: FM_PLACEHOLDER | FM_IF (fm_annotationTypeMemberDeclarationStar | annotationTypeMemberDeclaration)* (FM_ELSE_IF (fm_annotationTypeMemberDeclarationStar | annotationTypeMemberDeclaration)*)* (FM_ELSE (fm_annotationTypeMemberDeclarationStar | annotationTypeMemberDeclaration)*)? FM_IF_CLOSE | FM_LIST (fm_annotationTypeMemberDeclarationStar | annotationTypeMemberDeclaration)* FM_LIST_CLOSE;

annotationTypeElementDeclaration
  : (fm_annotationTypeElementModifierStar | annotationTypeElementModifier)* (fm_unannType | unannType) (fm_Identifier | Identifier) LPAREN  RPAREN  (fm_dimsOpt | dims)? (fm_defaultValueOpt | defaultValue)? SEMI 
  ;

annotationTypeElementModifier
  : annotation
  | PUBLIC 
  | ABSTRACT 
  ;
fm_annotationTypeElementModifierStar: FM_PLACEHOLDER | FM_IF (fm_annotationTypeElementModifierStar | annotationTypeElementModifier)* (FM_ELSE_IF (fm_annotationTypeElementModifierStar | annotationTypeElementModifier)*)* (FM_ELSE (fm_annotationTypeElementModifierStar | annotationTypeElementModifier)*)? FM_IF_CLOSE | FM_LIST (fm_annotationTypeElementModifierStar | annotationTypeElementModifier)* FM_LIST_CLOSE;

defaultValue
  : DEFAULT  (fm_elementValue | elementValue)
  ;
fm_defaultValueOpt: FM_PLACEHOLDER | FM_IF (fm_defaultValueOpt | defaultValue)? (FM_ELSE_IF (fm_defaultValueOpt | defaultValue)?)* (FM_ELSE (fm_defaultValueOpt | defaultValue)?)? FM_IF_CLOSE;

annotation
  : normalAnnotation
  | markerAnnotation
  | singleElementAnnotation
  ;
fm_annotationStar: FM_PLACEHOLDER | FM_IF (fm_annotationStar | annotation)* (FM_ELSE_IF (fm_annotationStar | annotation)*)* (FM_ELSE (fm_annotationStar | annotation)*)? FM_IF_CLOSE | FM_LIST (fm_annotationStar | annotation)* FM_LIST_CLOSE;

normalAnnotation
  : AT  (fm_typeName | typeName) LPAREN  (fm_elementValuePairListOpt | elementValuePairList)? RPAREN 
  ;

elementValuePairList
  : (fm_elementValuePair | elementValuePair) (COMMA  (fm_elementValuePair | elementValuePair))*
  ;
fm_elementValuePairListOpt: FM_PLACEHOLDER | FM_IF (fm_elementValuePairListOpt | elementValuePairList)? (FM_ELSE_IF (fm_elementValuePairListOpt | elementValuePairList)?)* (FM_ELSE (fm_elementValuePairListOpt | elementValuePairList)?)? FM_IF_CLOSE;

elementValuePair
  : (fm_Identifier | Identifier) ASSIGN  (fm_elementValue | elementValue)
  ;
fm_elementValuePair: FM_PLACEHOLDER | FM_IF (fm_elementValuePair | elementValuePair) (FM_ELSE_IF (fm_elementValuePair | elementValuePair))* FM_ELSE (fm_elementValuePair | elementValuePair) FM_IF_CLOSE;

elementValue
  : conditionalExpression
  | elementValueArrayInitializer
  | annotation
  ;
fm_elementValue: FM_PLACEHOLDER | FM_IF (fm_elementValue | elementValue) (FM_ELSE_IF (fm_elementValue | elementValue))* FM_ELSE (fm_elementValue | elementValue) FM_IF_CLOSE;

elementValueArrayInitializer
  : LBRACE  (fm_elementValueListOpt | elementValueList)? COMMA ? RBRACE 
  ;

elementValueList
  : (fm_elementValue | elementValue) (COMMA  (fm_elementValue | elementValue))*
  ;
fm_elementValueListOpt: FM_PLACEHOLDER | FM_IF (fm_elementValueListOpt | elementValueList)? (FM_ELSE_IF (fm_elementValueListOpt | elementValueList)?)* (FM_ELSE (fm_elementValueListOpt | elementValueList)?)? FM_IF_CLOSE;

markerAnnotation
  : AT  (fm_typeName | typeName)
  ;

singleElementAnnotation
  : AT  (fm_typeName | typeName) LPAREN  (fm_elementValue | elementValue) RPAREN 
  ;

/*
 * Productions from §10 (Arrays)
 */

arrayInitializer
  : LBRACE  (fm_variableInitializerListOpt | variableInitializerList)? COMMA ? RBRACE 
  ;
fm_arrayInitializer: FM_PLACEHOLDER | FM_IF (fm_arrayInitializer | arrayInitializer) (FM_ELSE_IF (fm_arrayInitializer | arrayInitializer))* FM_ELSE (fm_arrayInitializer | arrayInitializer) FM_IF_CLOSE;

variableInitializerList
  : variableInitializer 
  | fm_variableInitializerList COMMA ( fm_variableInitializer | variableInitializer ) 
  | variableInitializerList COMMA  (fm_variableInitializer | variableInitializer)
  ;
fm_variableInitializerList: FM_PLACEHOLDER | FM_IF (fm_variableInitializerList | variableInitializerList) (FM_ELSE_IF (fm_variableInitializerList | variableInitializerList))* FM_ELSE (fm_variableInitializerList | variableInitializerList) FM_IF_CLOSE;
fm_variableInitializerListOpt: FM_PLACEHOLDER | FM_IF (fm_variableInitializerListOpt | variableInitializerList)? (FM_ELSE_IF (fm_variableInitializerListOpt | variableInitializerList)?)* (FM_ELSE (fm_variableInitializerListOpt | variableInitializerList)?)? FM_IF_CLOSE;

/*
 * Productions from §14 (Blocks and Statements)
 */

block
  : LBRACE  (fm_blockStatementStar | blockStatement)* RBRACE 
  ;
fm_block: FM_PLACEHOLDER | FM_IF (fm_block | block) (FM_ELSE_IF (fm_block | block))* FM_ELSE (fm_block | block) FM_IF_CLOSE;

blockStatement
  : localVariableDeclarationStatement
  | classDeclaration
  | statement
  ;
fm_blockStatementStar: FM_PLACEHOLDER | FM_IF (fm_blockStatementStar | blockStatement)* (FM_ELSE_IF (fm_blockStatementStar | blockStatement)*)* (FM_ELSE (fm_blockStatementStar | blockStatement)*)? FM_IF_CLOSE | FM_LIST (fm_blockStatementStar | blockStatement)* FM_LIST_CLOSE;
fm_blockStatementPlus: FM_PLACEHOLDER | (FM_IF (fm_blockStatementPlus | blockStatement)* (FM_ELSE_IF (fm_blockStatementPlus | blockStatement)*)* (FM_ELSE (fm_blockStatementPlus | blockStatement)*)? FM_IF_CLOSE | FM_LIST (fm_blockStatementPlus | blockStatement)* FM_LIST_CLOSE)* (FM_PLACEHOLDER | FM_IF (fm_blockStatementPlus | blockStatement)* (FM_ELSE_IF (fm_blockStatementPlus | blockStatement)*)* FM_ELSE (fm_blockStatementPlus | blockStatement)* FM_IF_CLOSE | FM_LIST (fm_blockStatementPlus | blockStatement)* FM_ELSE (fm_blockStatementPlus | blockStatement)* FM_LIST_CLOSE) (FM_IF (fm_blockStatementPlus | blockStatement)* (FM_ELSE_IF (fm_blockStatementPlus | blockStatement)*)* (FM_ELSE (fm_blockStatementPlus | blockStatement)*)? FM_IF_CLOSE | FM_LIST (fm_blockStatementPlus | blockStatement)* FM_LIST_CLOSE)*;

localVariableDeclarationStatement
  : (fm_localVariableDeclaration | localVariableDeclaration) SEMI 
  ;

localVariableDeclaration
  : (fm_variableModifierStar | variableModifier)* (fm_unannType | unannType) (fm_variableDeclaratorList | variableDeclaratorList)
  ;
fm_localVariableDeclaration: FM_PLACEHOLDER | FM_IF (fm_localVariableDeclaration | localVariableDeclaration) (FM_ELSE_IF (fm_localVariableDeclaration | localVariableDeclaration))* FM_ELSE (fm_localVariableDeclaration | localVariableDeclaration) FM_IF_CLOSE;

statement
  : statementWithoutTrailingSubstatement
  | labeledStatement
  | ifThenStatement
  | ifThenElseStatement
  | whileStatement
  | forStatement
  ;
fm_statement: FM_PLACEHOLDER | FM_IF (fm_statement | statement) (FM_ELSE_IF (fm_statement | statement))* FM_ELSE (fm_statement | statement) FM_IF_CLOSE;

statementNoShortIf
  : statementWithoutTrailingSubstatement
  | labeledStatementNoShortIf
  | ifThenElseStatementNoShortIf
  | whileStatementNoShortIf
  | forStatementNoShortIf
  ;
fm_statementNoShortIf: FM_PLACEHOLDER | FM_IF (fm_statementNoShortIf | statementNoShortIf) (FM_ELSE_IF (fm_statementNoShortIf | statementNoShortIf))* FM_ELSE (fm_statementNoShortIf | statementNoShortIf) FM_IF_CLOSE;

statementWithoutTrailingSubstatement
  : block
  | emptyStatement
  | expressionStatement
  | assertStatement
  | switchStatement
  | doStatement
  | breakStatement
  | continueStatement
  | returnStatement
  | synchronizedStatement
  | throwStatement
  | tryStatement
  ;

emptyStatement
  : SEMI 
  ;

labeledStatement
  : (fm_Identifier | Identifier) COLON  (fm_statement | statement)
  ;

labeledStatementNoShortIf
  : (fm_Identifier | Identifier) COLON  (fm_statementNoShortIf | statementNoShortIf)
  ;

expressionStatement
  : (fm_statementExpression | statementExpression) SEMI 
  ;

statementExpression
  : assignment
  | preIncrementExpression
  | preDecrementExpression
  | postIncrementExpression
  | postDecrementExpression
  | methodInvocation
  | classInstanceCreationExpression
  ;
fm_statementExpression: FM_PLACEHOLDER | FM_IF (fm_statementExpression | statementExpression) (FM_ELSE_IF (fm_statementExpression | statementExpression))* FM_ELSE (fm_statementExpression | statementExpression) FM_IF_CLOSE;

ifThenStatement
  : IF  LPAREN  (fm_expression | expression) RPAREN  (fm_statement | statement)
  ;

ifThenElseStatement
  : IF  LPAREN  (fm_expression | expression) RPAREN  (fm_statementNoShortIf | statementNoShortIf) ELSE  (fm_statement | statement)
  ;

ifThenElseStatementNoShortIf
  : IF  LPAREN  (fm_expression | expression) RPAREN  (fm_statementNoShortIf | statementNoShortIf) ELSE  (fm_statementNoShortIf | statementNoShortIf)
  ;

assertStatement
  : ASSERT  (fm_expression | expression) SEMI 
  | ASSERT  (fm_expression | expression) COLON  (fm_expression | expression) SEMI 
  ;

switchStatement
  : SWITCH  LPAREN  (fm_expression | expression) RPAREN  (fm_switchBlock | switchBlock)
  ;

switchBlock
  : LBRACE  (fm_switchBlockStatementGroupStar | switchBlockStatementGroup)* (fm_switchLabelStar | switchLabel)* RBRACE 
  ;
fm_switchBlock: FM_PLACEHOLDER | FM_IF (fm_switchBlock | switchBlock) (FM_ELSE_IF (fm_switchBlock | switchBlock))* FM_ELSE (fm_switchBlock | switchBlock) FM_IF_CLOSE;

switchBlockStatementGroup
  : (fm_switchLabels | switchLabels) (fm_blockStatementPlus | blockStatement)+
  ;
fm_switchBlockStatementGroupStar: FM_PLACEHOLDER | FM_IF (fm_switchBlockStatementGroupStar | switchBlockStatementGroup)* (FM_ELSE_IF (fm_switchBlockStatementGroupStar | switchBlockStatementGroup)*)* (FM_ELSE (fm_switchBlockStatementGroupStar | switchBlockStatementGroup)*)? FM_IF_CLOSE | FM_LIST (fm_switchBlockStatementGroupStar | switchBlockStatementGroup)* FM_LIST_CLOSE;

switchLabels
  : (fm_switchLabel | switchLabel) (fm_switchLabelStar | switchLabel)*
  ;
fm_switchLabels: FM_PLACEHOLDER | FM_IF (fm_switchLabels | switchLabels) (FM_ELSE_IF (fm_switchLabels | switchLabels))* FM_ELSE (fm_switchLabels | switchLabels) FM_IF_CLOSE;

switchLabel
  : CASE  (fm_constantExpression | constantExpression) COLON 
  | CASE  (fm_enumConstantName | enumConstantName) COLON 
  | DEFAULT  COLON 
  ;
fm_switchLabel: FM_PLACEHOLDER | FM_IF (fm_switchLabel | switchLabel) (FM_ELSE_IF (fm_switchLabel | switchLabel))* FM_ELSE (fm_switchLabel | switchLabel) FM_IF_CLOSE;
fm_switchLabelStar: FM_PLACEHOLDER | FM_IF (fm_switchLabelStar | switchLabel)* (FM_ELSE_IF (fm_switchLabelStar | switchLabel)*)* (FM_ELSE (fm_switchLabelStar | switchLabel)*)? FM_IF_CLOSE | FM_LIST (fm_switchLabelStar | switchLabel)* FM_LIST_CLOSE;

enumConstantName
  : Identifier
  ;
fm_enumConstantName: FM_PLACEHOLDER | FM_IF (fm_enumConstantName | enumConstantName) (FM_ELSE_IF (fm_enumConstantName | enumConstantName))* FM_ELSE (fm_enumConstantName | enumConstantName) FM_IF_CLOSE;

whileStatement
  : WHILE  LPAREN  (fm_expression | expression) RPAREN  (fm_statement | statement)
  ;

whileStatementNoShortIf
  : WHILE  LPAREN  (fm_expression | expression) RPAREN  (fm_statementNoShortIf | statementNoShortIf)
  ;

doStatement
  : DO  (fm_statement | statement) WHILE  LPAREN  (fm_expression | expression) RPAREN  SEMI 
  ;

forStatement
  : basicForStatement
  | enhancedForStatement
  ;

forStatementNoShortIf
  : basicForStatementNoShortIf
  | enhancedForStatementNoShortIf
  ;

basicForStatement
  : FOR  LPAREN  (fm_forInitOpt | forInit)? SEMI  (fm_expressionOpt | expression)? SEMI  (fm_forUpdateOpt | forUpdate)? RPAREN  (fm_statement | statement)
  ;

basicForStatementNoShortIf
  : FOR  LPAREN  (fm_forInitOpt | forInit)? SEMI  (fm_expressionOpt | expression)? SEMI  (fm_forUpdateOpt | forUpdate)? RPAREN  (fm_statementNoShortIf | statementNoShortIf)
  ;

forInit
  : statementExpressionList
  | localVariableDeclaration
  ;
fm_forInitOpt: FM_PLACEHOLDER | FM_IF (fm_forInitOpt | forInit)? (FM_ELSE_IF (fm_forInitOpt | forInit)?)* (FM_ELSE (fm_forInitOpt | forInit)?)? FM_IF_CLOSE;

forUpdate
  : statementExpressionList
  ;
fm_forUpdateOpt: FM_PLACEHOLDER | FM_IF (fm_forUpdateOpt | forUpdate)? (FM_ELSE_IF (fm_forUpdateOpt | forUpdate)?)* (FM_ELSE (fm_forUpdateOpt | forUpdate)?)? FM_IF_CLOSE;

statementExpressionList
  : (fm_statementExpression | statementExpression) (COMMA  (fm_statementExpression | statementExpression))*
  ;

enhancedForStatement
  : FOR  LPAREN  (fm_variableModifierStar | variableModifier)* (fm_unannType | unannType) (fm_variableDeclaratorId | variableDeclaratorId) COLON  (fm_expression | expression) RPAREN  (fm_statement | statement)
  ;

enhancedForStatementNoShortIf
  : FOR  LPAREN  (fm_variableModifierStar | variableModifier)* (fm_unannType | unannType) (fm_variableDeclaratorId | variableDeclaratorId) COLON  (fm_expression | expression) RPAREN  (fm_statementNoShortIf | statementNoShortIf)
  ;

breakStatement
  : BREAK  (fm_IdentifierOpt | Identifier)? SEMI 
  ;

continueStatement
  : CONTINUE  (fm_IdentifierOpt | Identifier)? SEMI 
  ;

returnStatement
  : RETURN  (fm_expressionOpt | expression)? SEMI 
  ;

throwStatement
  : THROW  (fm_expression | expression) SEMI 
  ;

synchronizedStatement
  : SYNCHRONIZED  LPAREN  (fm_expression | expression) RPAREN  (fm_block | block)
  ;

tryStatement
  : TRY  (fm_block | block) (fm_catches | catches)
  | TRY  (fm_block | block) (fm_catchesOpt | catches)? (fm_finally_ | finally_)
  | tryWithResourcesStatement
  ;

catches
  : (fm_catchClause | catchClause) (fm_catchClauseStar | catchClause)*
  ;
fm_catches: FM_PLACEHOLDER | FM_IF (fm_catches | catches) (FM_ELSE_IF (fm_catches | catches))* FM_ELSE (fm_catches | catches) FM_IF_CLOSE;
fm_catchesOpt: FM_PLACEHOLDER | FM_IF (fm_catchesOpt | catches)? (FM_ELSE_IF (fm_catchesOpt | catches)?)* (FM_ELSE (fm_catchesOpt | catches)?)? FM_IF_CLOSE;

catchClause
  : CATCH  LPAREN  (fm_catchFormalParameter | catchFormalParameter) RPAREN  (fm_block | block)
  ;
fm_catchClause: FM_PLACEHOLDER | FM_IF (fm_catchClause | catchClause) (FM_ELSE_IF (fm_catchClause | catchClause))* FM_ELSE (fm_catchClause | catchClause) FM_IF_CLOSE;
fm_catchClauseStar: FM_PLACEHOLDER | FM_IF (fm_catchClauseStar | catchClause)* (FM_ELSE_IF (fm_catchClauseStar | catchClause)*)* (FM_ELSE (fm_catchClauseStar | catchClause)*)? FM_IF_CLOSE | FM_LIST (fm_catchClauseStar | catchClause)* FM_LIST_CLOSE;

catchFormalParameter
  : (fm_variableModifierStar | variableModifier)* (fm_catchType | catchType) (fm_variableDeclaratorId | variableDeclaratorId)
  ;
fm_catchFormalParameter: FM_PLACEHOLDER | FM_IF (fm_catchFormalParameter | catchFormalParameter) (FM_ELSE_IF (fm_catchFormalParameter | catchFormalParameter))* FM_ELSE (fm_catchFormalParameter | catchFormalParameter) FM_IF_CLOSE;

catchType
  : (fm_unannClassType | unannClassType) (BITOR  (fm_classType | classType))*
  ;
fm_catchType: FM_PLACEHOLDER | FM_IF (fm_catchType | catchType) (FM_ELSE_IF (fm_catchType | catchType))* FM_ELSE (fm_catchType | catchType) FM_IF_CLOSE;

finally_
  : FINALLY  (fm_block | block)
  ;
fm_finally_: FM_PLACEHOLDER | FM_IF (fm_finally_ | finally_) (FM_ELSE_IF (fm_finally_ | finally_))* FM_ELSE (fm_finally_ | finally_) FM_IF_CLOSE;
fm_finally_Opt: FM_PLACEHOLDER | FM_IF (fm_finally_Opt | finally_)? (FM_ELSE_IF (fm_finally_Opt | finally_)?)* (FM_ELSE (fm_finally_Opt | finally_)?)? FM_IF_CLOSE;

tryWithResourcesStatement
  : TRY  (fm_resourceSpecification | resourceSpecification) (fm_block | block) (fm_catchesOpt | catches)? (fm_finally_Opt | finally_)?
  ;

resourceSpecification
  : LPAREN  (fm_resourceList | resourceList) SEMI ? RPAREN 
  ;
fm_resourceSpecification: FM_PLACEHOLDER | FM_IF (fm_resourceSpecification | resourceSpecification) (FM_ELSE_IF (fm_resourceSpecification | resourceSpecification))* FM_ELSE (fm_resourceSpecification | resourceSpecification) FM_IF_CLOSE;

resourceList
  : (fm_resource | resource) (SEMI  (fm_resource | resource))*
  ;
fm_resourceList: FM_PLACEHOLDER | FM_IF (fm_resourceList | resourceList) (FM_ELSE_IF (fm_resourceList | resourceList))* FM_ELSE (fm_resourceList | resourceList) FM_IF_CLOSE;

resource
  : (fm_variableModifierStar | variableModifier)* (fm_unannType | unannType) (fm_variableDeclaratorId | variableDeclaratorId) ASSIGN  (fm_expression | expression)
  ;
fm_resource: FM_PLACEHOLDER | FM_IF (fm_resource | resource) (FM_ELSE_IF (fm_resource | resource))* FM_ELSE (fm_resource | resource) FM_IF_CLOSE;

/*
 * Productions from §15 (Expressions)
 */

primary
  : ( primaryNoNewArray_lfno_primary
    | arrayCreationExpression
    )
    ( primaryNoNewArray_lf_primary
    )*
  ;
fm_primary: FM_PLACEHOLDER | FM_IF (fm_primary | primary) (FM_ELSE_IF (fm_primary | primary))* FM_ELSE (fm_primary | primary) FM_IF_CLOSE;

primaryNoNewArray
  : literal
  | (fm_typeName | typeName) (LBRACK  RBRACK )* DOT  CLASS 
  | VOID  DOT  CLASS 
  | THIS 
  | (fm_typeName | typeName) DOT  THIS 
  | LPAREN  (fm_expression | expression) RPAREN 
  | classInstanceCreationExpression
  | fieldAccess
  | arrayAccess
  | methodInvocation
  | methodReference
  ;

primaryNoNewArray_lf_arrayAccess
  :
  ;
fm_primaryNoNewArray_lf_arrayAccess: FM_PLACEHOLDER | FM_IF (fm_primaryNoNewArray_lf_arrayAccess | primaryNoNewArray_lf_arrayAccess) (FM_ELSE_IF (fm_primaryNoNewArray_lf_arrayAccess | primaryNoNewArray_lf_arrayAccess))* FM_ELSE (fm_primaryNoNewArray_lf_arrayAccess | primaryNoNewArray_lf_arrayAccess) FM_IF_CLOSE;

primaryNoNewArray_lfno_arrayAccess
  : literal
  | (fm_typeName | typeName) (LBRACK  RBRACK )* DOT  CLASS 
  | VOID  DOT  CLASS 
  | THIS 
  | (fm_typeName | typeName) DOT  THIS 
  | LPAREN  (fm_expression | expression) RPAREN 
  | classInstanceCreationExpression
  | fieldAccess
  | methodInvocation
  | methodReference
  ;
fm_primaryNoNewArray_lfno_arrayAccess: FM_PLACEHOLDER | FM_IF (fm_primaryNoNewArray_lfno_arrayAccess | primaryNoNewArray_lfno_arrayAccess) (FM_ELSE_IF (fm_primaryNoNewArray_lfno_arrayAccess | primaryNoNewArray_lfno_arrayAccess))* FM_ELSE (fm_primaryNoNewArray_lfno_arrayAccess | primaryNoNewArray_lfno_arrayAccess) FM_IF_CLOSE;

primaryNoNewArray_lf_primary
  : classInstanceCreationExpression_lf_primary
  | fieldAccess_lf_primary
  | arrayAccess_lf_primary
  | methodInvocation_lf_primary
  | methodReference_lf_primary
  ;

primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary
  :
  ;
fm_primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary: FM_PLACEHOLDER | FM_IF (fm_primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary | primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary) (FM_ELSE_IF (fm_primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary | primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary))* FM_ELSE (fm_primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary | primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary) FM_IF_CLOSE;

primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary
  : classInstanceCreationExpression_lf_primary
  | fieldAccess_lf_primary
  | methodInvocation_lf_primary
  | methodReference_lf_primary
  ;
fm_primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary: FM_PLACEHOLDER | FM_IF (fm_primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary | primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary) (FM_ELSE_IF (fm_primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary | primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary))* FM_ELSE (fm_primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary | primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary) FM_IF_CLOSE;

primaryNoNewArray_lfno_primary
  : literal
  | (fm_typeName | typeName) (LBRACK  RBRACK )* DOT  CLASS 
  | (fm_unannPrimitiveType | unannPrimitiveType) (LBRACK  RBRACK )* DOT  CLASS 
  | VOID  DOT  CLASS 
  | THIS 
  | (fm_typeName | typeName) DOT  THIS 
  | LPAREN  (fm_expression | expression) RPAREN 
  | classInstanceCreationExpression_lfno_primary
  | fieldAccess_lfno_primary
  | arrayAccess_lfno_primary
  | methodInvocation_lfno_primary
  | methodReference_lfno_primary
  ;

primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary
  :
  ;
fm_primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary: FM_PLACEHOLDER | FM_IF (fm_primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary | primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary) (FM_ELSE_IF (fm_primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary | primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary))* FM_ELSE (fm_primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary | primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary) FM_IF_CLOSE;

primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary
  : literal
  | (fm_typeName | typeName) (LBRACK  RBRACK )* DOT  CLASS 
  | (fm_unannPrimitiveType | unannPrimitiveType) (LBRACK  RBRACK )* DOT  CLASS 
  | VOID  DOT  CLASS 
  | THIS 
  | (fm_typeName | typeName) DOT  THIS 
  | LPAREN  (fm_expression | expression) RPAREN 
  | classInstanceCreationExpression_lfno_primary
  | fieldAccess_lfno_primary
  | methodInvocation_lfno_primary
  | methodReference_lfno_primary
  ;
fm_primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary: FM_PLACEHOLDER | FM_IF (fm_primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary | primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary) (FM_ELSE_IF (fm_primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary | primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary))* FM_ELSE (fm_primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary | primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary) FM_IF_CLOSE;

classInstanceCreationExpression
  : NEW  (fm_typeArgumentsOpt | typeArguments)? (fm_annotationStar | annotation)* (fm_Identifier | Identifier) (DOT  (fm_annotationStar | annotation)* (fm_Identifier | Identifier))* (fm_typeArgumentsOrDiamondOpt | typeArgumentsOrDiamond)? LPAREN  (fm_argumentListOpt | argumentList)? RPAREN  (fm_classBodyOpt | classBody)?
  | (fm_expressionName | expressionName) DOT  NEW  (fm_typeArgumentsOpt | typeArguments)? (fm_annotationStar | annotation)* (fm_Identifier | Identifier) (fm_typeArgumentsOrDiamondOpt | typeArgumentsOrDiamond)? LPAREN  (fm_argumentListOpt | argumentList)? RPAREN  (fm_classBodyOpt | classBody)?
  | (fm_primary | primary) DOT  NEW  (fm_typeArgumentsOpt | typeArguments)? (fm_annotationStar | annotation)* (fm_Identifier | Identifier) (fm_typeArgumentsOrDiamondOpt | typeArgumentsOrDiamond)? LPAREN  (fm_argumentListOpt | argumentList)? RPAREN  (fm_classBodyOpt | classBody)?
  ;

classInstanceCreationExpression_lf_primary
  : DOT  NEW  (fm_typeArgumentsOpt | typeArguments)? (fm_annotationStar | annotation)* (fm_Identifier | Identifier) (fm_typeArgumentsOrDiamondOpt | typeArgumentsOrDiamond)? LPAREN  (fm_argumentListOpt | argumentList)? RPAREN  (fm_classBodyOpt | classBody)?
  ;

classInstanceCreationExpression_lfno_primary
  : NEW  (fm_typeArgumentsOpt | typeArguments)? (fm_annotationStar | annotation)* (fm_Identifier | Identifier) (DOT  (fm_annotationStar | annotation)* (fm_Identifier | Identifier))* (fm_typeArgumentsOrDiamondOpt | typeArgumentsOrDiamond)? LPAREN  (fm_argumentListOpt | argumentList)? RPAREN  (fm_classBodyOpt | classBody)?
  | (fm_expressionName | expressionName) DOT  NEW  (fm_typeArgumentsOpt | typeArguments)? (fm_annotationStar | annotation)* (fm_Identifier | Identifier) (fm_typeArgumentsOrDiamondOpt | typeArgumentsOrDiamond)? LPAREN  (fm_argumentListOpt | argumentList)? RPAREN  (fm_classBodyOpt | classBody)?
  ;

typeArgumentsOrDiamond
  : typeArguments
  | LT  GT 
  ;
fm_typeArgumentsOrDiamondOpt: FM_PLACEHOLDER | FM_IF (fm_typeArgumentsOrDiamondOpt | typeArgumentsOrDiamond)? (FM_ELSE_IF (fm_typeArgumentsOrDiamondOpt | typeArgumentsOrDiamond)?)* (FM_ELSE (fm_typeArgumentsOrDiamondOpt | typeArgumentsOrDiamond)?)? FM_IF_CLOSE;

fieldAccess
  : (fm_primary | primary) DOT  (fm_Identifier | Identifier)
  | SUPER  DOT  (fm_Identifier | Identifier)
  | (fm_typeName | typeName) DOT  SUPER  DOT  (fm_Identifier | Identifier)
  ;

fieldAccess_lf_primary
  : DOT  (fm_Identifier | Identifier)
  ;

fieldAccess_lfno_primary
  : SUPER  DOT  (fm_Identifier | Identifier)
  | (fm_typeName | typeName) DOT  SUPER  DOT  (fm_Identifier | Identifier)
  ;

arrayAccess
  : ( (fm_expressionName | expressionName) LBRACK  (fm_expression | expression) RBRACK 
    | (fm_primaryNoNewArray_lfno_arrayAccess | primaryNoNewArray_lfno_arrayAccess) LBRACK  (fm_expression | expression) RBRACK 
    )
    ( (fm_primaryNoNewArray_lf_arrayAccess | primaryNoNewArray_lf_arrayAccess) LBRACK  (fm_expression | expression) RBRACK 
    )*
  ;

arrayAccess_lf_primary
  : ( (fm_primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary | primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary) LBRACK  (fm_expression | expression) RBRACK 
    )
    ( (fm_primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary | primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary) LBRACK  (fm_expression | expression) RBRACK 
    )*
  ;

arrayAccess_lfno_primary
  : ( (fm_expressionName | expressionName) LBRACK  (fm_expression | expression) RBRACK 
    | (fm_primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary | primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary) LBRACK  (fm_expression | expression) RBRACK 
    )
    ( (fm_primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary | primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary) LBRACK  (fm_expression | expression) RBRACK 
    )*
  ;

methodInvocation
  : (fm_methodName | methodName) LPAREN  (fm_argumentListOpt | argumentList)? RPAREN 
  | (fm_typeName | typeName) DOT  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier) LPAREN  (fm_argumentListOpt | argumentList)? RPAREN 
  | (fm_expressionName | expressionName) DOT  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier) LPAREN  (fm_argumentListOpt | argumentList)? RPAREN 
  | (fm_primary | primary) DOT  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier) LPAREN  (fm_argumentListOpt | argumentList)? RPAREN 
  | SUPER  DOT  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier) LPAREN  (fm_argumentListOpt | argumentList)? RPAREN 
  | (fm_typeName | typeName) DOT  SUPER  DOT  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier) LPAREN  (fm_argumentListOpt | argumentList)? RPAREN 
  ;

methodInvocation_lf_primary
  : DOT  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier) LPAREN  (fm_argumentListOpt | argumentList)? RPAREN 
  ;

methodInvocation_lfno_primary
  : (fm_methodName | methodName) LPAREN  (fm_argumentListOpt | argumentList)? RPAREN 
  | (fm_typeName | typeName) DOT  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier) LPAREN  (fm_argumentListOpt | argumentList)? RPAREN 
  | (fm_expressionName | expressionName) DOT  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier) LPAREN  (fm_argumentListOpt | argumentList)? RPAREN 
  | SUPER  DOT  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier) LPAREN  (fm_argumentListOpt | argumentList)? RPAREN 
  | (fm_typeName | typeName) DOT  SUPER  DOT  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier) LPAREN  (fm_argumentListOpt | argumentList)? RPAREN 
  ;

argumentList
  : (fm_expression | expression) (COMMA  (fm_expression | expression))*
  ;
fm_argumentListOpt: FM_PLACEHOLDER | FM_IF (fm_argumentListOpt | argumentList)? (FM_ELSE_IF (fm_argumentListOpt | argumentList)?)* (FM_ELSE (fm_argumentListOpt | argumentList)?)? FM_IF_CLOSE;

methodReference
  : (fm_expressionName | expressionName) COLONCOLON  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier)
  | (fm_referenceType | referenceType) COLONCOLON  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier)
  | (fm_primary | primary) COLONCOLON  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier)
  | SUPER  COLONCOLON  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier)
  | (fm_typeName | typeName) DOT  SUPER  COLONCOLON  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier)
  | (fm_classType | classType) COLONCOLON  (fm_typeArgumentsOpt | typeArguments)? NEW 
  | (fm_arrayType | arrayType) COLONCOLON  NEW 
  ;

methodReference_lf_primary
  : COLONCOLON  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier)
  ;

methodReference_lfno_primary
  : (fm_expressionName | expressionName) COLONCOLON  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier)
  | (fm_referenceType | referenceType) COLONCOLON  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier)
  | SUPER  COLONCOLON  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier)
  | (fm_typeName | typeName) DOT  SUPER  COLONCOLON  (fm_typeArgumentsOpt | typeArguments)? (fm_Identifier | Identifier)
  | (fm_classType | classType) COLONCOLON  (fm_typeArgumentsOpt | typeArguments)? NEW 
  | (fm_arrayType | arrayType) COLONCOLON  NEW 
  ;

arrayCreationExpression
  : NEW  (fm_primitiveType | primitiveType) (fm_dimExprs | dimExprs) (fm_dimsOpt | dims)?
  | NEW  (fm_classOrInterfaceType | classOrInterfaceType) (fm_dimExprs | dimExprs) (fm_dimsOpt | dims)?
  | NEW  (fm_primitiveType | primitiveType) (fm_dims | dims) (fm_arrayInitializer | arrayInitializer)
  | NEW  (fm_classOrInterfaceType | classOrInterfaceType) (fm_dims | dims) (fm_arrayInitializer | arrayInitializer)
  ;

dimExprs
  : (fm_dimExpr | dimExpr) (fm_dimExprStar | dimExpr)*
  ;
fm_dimExprs: FM_PLACEHOLDER | FM_IF (fm_dimExprs | dimExprs) (FM_ELSE_IF (fm_dimExprs | dimExprs))* FM_ELSE (fm_dimExprs | dimExprs) FM_IF_CLOSE;

dimExpr
  : (fm_annotationStar | annotation)* LBRACK  (fm_expression | expression) RBRACK 
  ;
fm_dimExpr: FM_PLACEHOLDER | FM_IF (fm_dimExpr | dimExpr) (FM_ELSE_IF (fm_dimExpr | dimExpr))* FM_ELSE (fm_dimExpr | dimExpr) FM_IF_CLOSE;
fm_dimExprStar: FM_PLACEHOLDER | FM_IF (fm_dimExprStar | dimExpr)* (FM_ELSE_IF (fm_dimExprStar | dimExpr)*)* (FM_ELSE (fm_dimExprStar | dimExpr)*)? FM_IF_CLOSE | FM_LIST (fm_dimExprStar | dimExpr)* FM_LIST_CLOSE;

constantExpression
  : expression
  ;
fm_constantExpression: FM_PLACEHOLDER | FM_IF (fm_constantExpression | constantExpression) (FM_ELSE_IF (fm_constantExpression | constantExpression))* FM_ELSE (fm_constantExpression | constantExpression) FM_IF_CLOSE;

expression
  : lambdaExpression
  | assignmentExpression
  ;
fm_expression: FM_PLACEHOLDER | FM_IF (fm_expression | expression) (FM_ELSE_IF (fm_expression | expression))* FM_ELSE (fm_expression | expression) FM_IF_CLOSE;
fm_expressionOpt: FM_PLACEHOLDER | FM_IF (fm_expressionOpt | expression)? (FM_ELSE_IF (fm_expressionOpt | expression)?)* (FM_ELSE (fm_expressionOpt | expression)?)? FM_IF_CLOSE;

lambdaExpression
  : (fm_lambdaParameters | lambdaParameters) ARROW  (fm_lambdaBody | lambdaBody)
  ;
fm_lambdaExpression: FM_PLACEHOLDER | FM_IF (fm_lambdaExpression | lambdaExpression) (FM_ELSE_IF (fm_lambdaExpression | lambdaExpression))* FM_ELSE (fm_lambdaExpression | lambdaExpression) FM_IF_CLOSE;

lambdaParameters
  : Identifier
  | LPAREN  (fm_formalParameterListOpt | formalParameterList)? RPAREN 
  | LPAREN  (fm_inferredFormalParameterList | inferredFormalParameterList) RPAREN 
  ;
fm_lambdaParameters: FM_PLACEHOLDER | FM_IF (fm_lambdaParameters | lambdaParameters) (FM_ELSE_IF (fm_lambdaParameters | lambdaParameters))* FM_ELSE (fm_lambdaParameters | lambdaParameters) FM_IF_CLOSE;

inferredFormalParameterList
  : (fm_Identifier | Identifier) (COMMA  (fm_Identifier | Identifier))*
  ;
fm_inferredFormalParameterList: FM_PLACEHOLDER | FM_IF (fm_inferredFormalParameterList | inferredFormalParameterList) (FM_ELSE_IF (fm_inferredFormalParameterList | inferredFormalParameterList))* FM_ELSE (fm_inferredFormalParameterList | inferredFormalParameterList) FM_IF_CLOSE;

lambdaBody
  : expression
  | block
  ;
fm_lambdaBody: FM_PLACEHOLDER | FM_IF (fm_lambdaBody | lambdaBody) (FM_ELSE_IF (fm_lambdaBody | lambdaBody))* FM_ELSE (fm_lambdaBody | lambdaBody) FM_IF_CLOSE;

assignmentExpression
  : conditionalExpression
  | assignment
  ;

assignment
  : (fm_leftHandSide | leftHandSide) (fm_assignmentOperator | assignmentOperator) (fm_expression | expression)
  ;

leftHandSide
  : expressionName
  | fieldAccess
  | arrayAccess
  ;
fm_leftHandSide: FM_PLACEHOLDER | FM_IF (fm_leftHandSide | leftHandSide) (FM_ELSE_IF (fm_leftHandSide | leftHandSide))* FM_ELSE (fm_leftHandSide | leftHandSide) FM_IF_CLOSE;

assignmentOperator
  : ASSIGN 
  | MUL_ASSIGN 
  | DIV_ASSIGN 
  | MOD_ASSIGN 
  | ADD_ASSIGN 
  | SUB_ASSIGN 
  | LSHIFT_ASSIGN 
  | RSHIFT_ASSIGN 
  | URSHIFT_ASSIGN 
  | AND_ASSIGN 
  | XOR_ASSIGN 
  | OR_ASSIGN 
  ;
fm_assignmentOperator: FM_PLACEHOLDER | FM_IF (fm_assignmentOperator | assignmentOperator) (FM_ELSE_IF (fm_assignmentOperator | assignmentOperator))* FM_ELSE (fm_assignmentOperator | assignmentOperator) FM_IF_CLOSE;

conditionalExpression
  : conditionalOrExpression
  | (fm_conditionalOrExpression | conditionalOrExpression) QUESTION  (fm_expression | expression) COLON  (fm_conditionalExpression | conditionalExpression)
  ;
fm_conditionalExpression: FM_PLACEHOLDER | FM_IF (fm_conditionalExpression | conditionalExpression) (FM_ELSE_IF (fm_conditionalExpression | conditionalExpression))* FM_ELSE (fm_conditionalExpression | conditionalExpression) FM_IF_CLOSE;

conditionalOrExpression
  : conditionalAndExpression
  | fm_conditionalOrExpression OR ( fm_conditionalAndExpression | conditionalAndExpression ) 
  | conditionalOrExpression OR  (fm_conditionalAndExpression | conditionalAndExpression)
  ;
fm_conditionalOrExpression: FM_PLACEHOLDER | FM_IF (fm_conditionalOrExpression | conditionalOrExpression) (FM_ELSE_IF (fm_conditionalOrExpression | conditionalOrExpression))* FM_ELSE (fm_conditionalOrExpression | conditionalOrExpression) FM_IF_CLOSE;

conditionalAndExpression
  : inclusiveOrExpression
  | fm_conditionalAndExpression AND ( fm_inclusiveOrExpression | inclusiveOrExpression ) 
  | conditionalAndExpression AND  (fm_inclusiveOrExpression | inclusiveOrExpression)
  ;
fm_conditionalAndExpression: FM_PLACEHOLDER | FM_IF (fm_conditionalAndExpression | conditionalAndExpression) (FM_ELSE_IF (fm_conditionalAndExpression | conditionalAndExpression))* FM_ELSE (fm_conditionalAndExpression | conditionalAndExpression) FM_IF_CLOSE;

inclusiveOrExpression
  : exclusiveOrExpression
  | fm_inclusiveOrExpression BITOR ( fm_exclusiveOrExpression | exclusiveOrExpression ) 
  | inclusiveOrExpression BITOR  (fm_exclusiveOrExpression | exclusiveOrExpression)
  ;
fm_inclusiveOrExpression: FM_PLACEHOLDER | FM_IF (fm_inclusiveOrExpression | inclusiveOrExpression) (FM_ELSE_IF (fm_inclusiveOrExpression | inclusiveOrExpression))* FM_ELSE (fm_inclusiveOrExpression | inclusiveOrExpression) FM_IF_CLOSE;

exclusiveOrExpression
  : andExpression
  | fm_exclusiveOrExpression CARET ( fm_andExpression | andExpression ) 
  | exclusiveOrExpression CARET  (fm_andExpression | andExpression)
  ;
fm_exclusiveOrExpression: FM_PLACEHOLDER | FM_IF (fm_exclusiveOrExpression | exclusiveOrExpression) (FM_ELSE_IF (fm_exclusiveOrExpression | exclusiveOrExpression))* FM_ELSE (fm_exclusiveOrExpression | exclusiveOrExpression) FM_IF_CLOSE;

andExpression
  : equalityExpression
  | fm_andExpression BITAND ( fm_equalityExpression | equalityExpression ) 
  | andExpression BITAND  (fm_equalityExpression | equalityExpression)
  ;
fm_andExpression: FM_PLACEHOLDER | FM_IF (fm_andExpression | andExpression) (FM_ELSE_IF (fm_andExpression | andExpression))* FM_ELSE (fm_andExpression | andExpression) FM_IF_CLOSE;

equalityExpression
  : relationalExpression
  | fm_equalityExpression EQUAL ( fm_relationalExpression | relationalExpression ) 
  | equalityExpression EQUAL  (fm_relationalExpression | relationalExpression)
  | fm_equalityExpression NOTEQUAL ( fm_relationalExpression | relationalExpression ) 
  | equalityExpression NOTEQUAL  (fm_relationalExpression | relationalExpression)
  ;
fm_equalityExpression: FM_PLACEHOLDER | FM_IF (fm_equalityExpression | equalityExpression) (FM_ELSE_IF (fm_equalityExpression | equalityExpression))* FM_ELSE (fm_equalityExpression | equalityExpression) FM_IF_CLOSE;

relationalExpression
  : shiftExpression
  | fm_relationalExpression LT ( fm_shiftExpression | shiftExpression ) 
  | relationalExpression LT  (fm_shiftExpression | shiftExpression)
  | fm_relationalExpression GT ( fm_shiftExpression | shiftExpression ) 
  | relationalExpression GT  (fm_shiftExpression | shiftExpression)
  | fm_relationalExpression LE ( fm_shiftExpression | shiftExpression ) 
  | relationalExpression LE  (fm_shiftExpression | shiftExpression)
  | fm_relationalExpression GE ( fm_shiftExpression | shiftExpression ) 
  | relationalExpression GE  (fm_shiftExpression | shiftExpression)
  | fm_relationalExpression INSTANCEOF ( fm_referenceType | referenceType ) 
  | relationalExpression INSTANCEOF  (fm_referenceType | referenceType)
  ;
fm_relationalExpression: FM_PLACEHOLDER | FM_IF (fm_relationalExpression | relationalExpression) (FM_ELSE_IF (fm_relationalExpression | relationalExpression))* FM_ELSE (fm_relationalExpression | relationalExpression) FM_IF_CLOSE;

shiftExpression
  : additiveExpression
  | fm_shiftExpression LT LT ( fm_additiveExpression | additiveExpression ) 
  | shiftExpression LT  LT  (fm_additiveExpression | additiveExpression)
  | fm_shiftExpression GT GT ( fm_additiveExpression | additiveExpression ) 
  | shiftExpression GT  GT  (fm_additiveExpression | additiveExpression)
  | fm_shiftExpression GT GT GT ( fm_additiveExpression | additiveExpression ) 
  | shiftExpression GT  GT  GT  (fm_additiveExpression | additiveExpression)
  ;
fm_shiftExpression: FM_PLACEHOLDER | FM_IF (fm_shiftExpression | shiftExpression) (FM_ELSE_IF (fm_shiftExpression | shiftExpression))* FM_ELSE (fm_shiftExpression | shiftExpression) FM_IF_CLOSE;

additiveExpression
  : multiplicativeExpression
  | fm_additiveExpression ADD ( fm_multiplicativeExpression | multiplicativeExpression ) 
  | additiveExpression ADD  (fm_multiplicativeExpression | multiplicativeExpression)
  | fm_additiveExpression SUB ( fm_multiplicativeExpression | multiplicativeExpression ) 
  | additiveExpression SUB  (fm_multiplicativeExpression | multiplicativeExpression)
  ;
fm_additiveExpression: FM_PLACEHOLDER | FM_IF (fm_additiveExpression | additiveExpression) (FM_ELSE_IF (fm_additiveExpression | additiveExpression))* FM_ELSE (fm_additiveExpression | additiveExpression) FM_IF_CLOSE;

multiplicativeExpression
  : unaryExpression
  | fm_multiplicativeExpression MUL ( fm_unaryExpression | unaryExpression ) 
  | multiplicativeExpression MUL  (fm_unaryExpression | unaryExpression)
  | fm_multiplicativeExpression DIV ( fm_unaryExpression | unaryExpression ) 
  | multiplicativeExpression DIV  (fm_unaryExpression | unaryExpression)
  | fm_multiplicativeExpression MOD ( fm_unaryExpression | unaryExpression ) 
  | multiplicativeExpression MOD  (fm_unaryExpression | unaryExpression)
  ;
fm_multiplicativeExpression: FM_PLACEHOLDER | FM_IF (fm_multiplicativeExpression | multiplicativeExpression) (FM_ELSE_IF (fm_multiplicativeExpression | multiplicativeExpression))* FM_ELSE (fm_multiplicativeExpression | multiplicativeExpression) FM_IF_CLOSE;

unaryExpression
  : preIncrementExpression
  | preDecrementExpression
  | ADD  (fm_unaryExpression | unaryExpression)
  | SUB  (fm_unaryExpression | unaryExpression)
  | unaryExpressionNotPlusMinus
  ;
fm_unaryExpression: FM_PLACEHOLDER | FM_IF (fm_unaryExpression | unaryExpression) (FM_ELSE_IF (fm_unaryExpression | unaryExpression))* FM_ELSE (fm_unaryExpression | unaryExpression) FM_IF_CLOSE;

preIncrementExpression
  : INC  (fm_unaryExpression | unaryExpression)
  ;

preDecrementExpression
  : DEC  (fm_unaryExpression | unaryExpression)
  ;

unaryExpressionNotPlusMinus
  : postfixExpression
  | TILDE  (fm_unaryExpression | unaryExpression)
  | BANG  (fm_unaryExpression | unaryExpression)
  | castExpression
  ;
fm_unaryExpressionNotPlusMinus: FM_PLACEHOLDER | FM_IF (fm_unaryExpressionNotPlusMinus | unaryExpressionNotPlusMinus) (FM_ELSE_IF (fm_unaryExpressionNotPlusMinus | unaryExpressionNotPlusMinus))* FM_ELSE (fm_unaryExpressionNotPlusMinus | unaryExpressionNotPlusMinus) FM_IF_CLOSE;

postfixExpression
  : ( primary
    | expressionName
    )
    ( postIncrementExpression_lf_postfixExpression
    | postDecrementExpression_lf_postfixExpression
    )*
  ;
fm_postfixExpression: FM_PLACEHOLDER | FM_IF (fm_postfixExpression | postfixExpression) (FM_ELSE_IF (fm_postfixExpression | postfixExpression))* FM_ELSE (fm_postfixExpression | postfixExpression) FM_IF_CLOSE;

postIncrementExpression
  : (fm_postfixExpression | postfixExpression) INC 
  ;

postIncrementExpression_lf_postfixExpression
  : INC 
  ;

postDecrementExpression
  : (fm_postfixExpression | postfixExpression) DEC 
  ;

postDecrementExpression_lf_postfixExpression
  : DEC 
  ;

castExpression
  : LPAREN  (fm_primitiveType | primitiveType) RPAREN  (fm_unaryExpression | unaryExpression)
  | LPAREN  (fm_referenceType | referenceType) (fm_additionalBoundStar | additionalBound)* RPAREN  (fm_unaryExpressionNotPlusMinus | unaryExpressionNotPlusMinus)
  | LPAREN  (fm_referenceType | referenceType) (fm_additionalBoundStar | additionalBound)* RPAREN  (fm_lambdaExpression | lambdaExpression)
  ;

// LEXER

// §3.9 Keywords

ABSTRACT : 'abstract';
ASSERT : 'assert';
BOOLEAN : 'boolean';
BREAK : 'break';
BYTE : 'byte';
CASE : 'case';
CATCH : 'catch';
CHAR : 'char';
CLASS : 'class';
CONST : 'const';
CONTINUE : 'continue';
DEFAULT : 'default';
DO : 'do';
DOUBLE : 'double';
ELSE : 'else';
ENUM : 'enum';
EXTENDS : 'extends';
FINAL : 'final';
FINALLY : 'finally';
FLOAT : 'float';
FOR : 'for';
IF : 'if';
GOTO : 'goto';
IMPLEMENTS : 'implements';
IMPORT : 'import';
INSTANCEOF : 'instanceof';
INT : 'int';
INTERFACE : 'interface';
LONG : 'long';
NATIVE : 'native';
NEW : 'new';
PACKAGE : 'package';
PRIVATE : 'private';
PROTECTED : 'protected';
PUBLIC : 'public';
RETURN : 'return';
SHORT : 'short';
STATIC : 'static';
STRICTFP : 'strictfp';
SUPER : 'super';
SWITCH : 'switch';
SYNCHRONIZED : 'synchronized';
THIS : 'this';
THROW : 'throw';
THROWS : 'throws';
TRANSIENT : 'transient';
TRY : 'try';
VOID : 'void';
VOLATILE : 'volatile';
WHILE : 'while';

// §3.10.1 Integer Literals

IntegerLiteral
  : DecimalIntegerLiteral
  | HexIntegerLiteral
  | OctalIntegerLiteral
  | BinaryIntegerLiteral
  ;

fragment
DecimalIntegerLiteral
  : DecimalNumeral IntegerTypeSuffix?
  ;

fragment
HexIntegerLiteral
  : HexNumeral IntegerTypeSuffix?
  ;

fragment
OctalIntegerLiteral
  : OctalNumeral IntegerTypeSuffix?
  ;

fragment
BinaryIntegerLiteral
  : BinaryNumeral IntegerTypeSuffix?
  ;

fragment
IntegerTypeSuffix
  : [lL]
  ;

fragment
DecimalNumeral
  : '0'
  | NonZeroDigit (Digits? | Underscores Digits)
  ;

fragment
Digits
  : Digit (DigitsAndUnderscores? Digit)?
  ;

fragment
Digit
  : '0'
  | NonZeroDigit
  ;

fragment
NonZeroDigit
  : [1-9]
  ;

fragment
DigitsAndUnderscores
  : DigitOrUnderscore+
  ;

fragment
DigitOrUnderscore
  : Digit
  | '_'
  ;

fragment
Underscores
  : '_'+
  ;

fragment
HexNumeral
  : '0' [xX] HexDigits
  ;

fragment
HexDigits
  : HexDigit (HexDigitsAndUnderscores? HexDigit)?
  ;

fragment
HexDigit
  : [0-9a-fA-F]
  ;

fragment
HexDigitsAndUnderscores
  : HexDigitOrUnderscore+
  ;

fragment
HexDigitOrUnderscore
  : HexDigit
  | '_'
  ;

fragment
OctalNumeral
  : '0' Underscores? OctalDigits
  ;

fragment
OctalDigits
  : OctalDigit (OctalDigitsAndUnderscores? OctalDigit)?
  ;

fragment
OctalDigit
  : [0-7]
  ;

fragment
OctalDigitsAndUnderscores
  : OctalDigitOrUnderscore+
  ;

fragment
OctalDigitOrUnderscore
  : OctalDigit
  | '_'
  ;

fragment
BinaryNumeral
  : '0' [bB] BinaryDigits
  ;

fragment
BinaryDigits
  : BinaryDigit (BinaryDigitsAndUnderscores? BinaryDigit)?
  ;

fragment
BinaryDigit
  : [01]
  ;

fragment
BinaryDigitsAndUnderscores
  : BinaryDigitOrUnderscore+
  ;

fragment
BinaryDigitOrUnderscore
  : BinaryDigit
  | '_'
  ;

// §3.10.2 Floating-Point Literals

FloatingPointLiteral
  : DecimalFloatingPointLiteral
  | HexadecimalFloatingPointLiteral
  ;

fragment
DecimalFloatingPointLiteral
  : Digits '.' Digits? ExponentPart? FloatTypeSuffix?
  | '.' Digits ExponentPart? FloatTypeSuffix?
  | Digits ExponentPart FloatTypeSuffix?
  | Digits FloatTypeSuffix
  ;

fragment
ExponentPart
  : ExponentIndicator SignedInteger
  ;

fragment
ExponentIndicator
  : [eE]
  ;

fragment
SignedInteger
  : Sign? Digits
  ;

fragment
Sign
  : [+-]
  ;

fragment
FloatTypeSuffix
  : [fFdD]
  ;

fragment
HexadecimalFloatingPointLiteral
  : HexSignificand BinaryExponent FloatTypeSuffix?
  ;

fragment
HexSignificand
  : HexNumeral '.'?
  | '0' [xX] HexDigits? '.' HexDigits
  ;

fragment
BinaryExponent
  : BinaryExponentIndicator SignedInteger
  ;

fragment
BinaryExponentIndicator
  : [pP]
  ;

// §3.10.3 Boolean Literals

BooleanLiteral
  : 'true'
  | 'false'
  ;

// §3.10.4 Character Literals

CharacterLiteral
  : '\'' SingleCharacter '\''
  | '\'' EscapeSequence '\''
  ;

fragment
SingleCharacter
  : ~['\\]
  ;

// §3.10.5 String Literals

StringLiteral
  : '"' StringCharacters? '"'
  ;

fragment
StringCharacters
  : StringCharacter+
  ;

fragment
StringCharacter
  : ~["\\]
  | EscapeSequence
  ;

// §3.10.6 Escape Sequences for Character and String Literals

fragment
EscapeSequence
  : '\\' [btnfr"'\\]
  | OctalEscape
    |   UnicodeEscape // This is not in the spec but prevents having to preprocess the input
  ;

fragment
OctalEscape
  : '\\' OctalDigit
  | '\\' OctalDigit OctalDigit
  | '\\' ZeroToThree OctalDigit OctalDigit
  ;

fragment
ZeroToThree
  : [0-3]
  ;

// This is not in the spec but prevents having to preprocess the input
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// §3.10.7 The Null Literal

NullLiteral
  : 'null'
  ;

// §3.11 Separators

LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';
LBRACK : '[';
RBRACK : ']';
SEMI : ';';
COMMA : ',';
DOT : '.';

// §3.12 Operators

ASSIGN : '=';
GT : '>';
LT : '<';
BANG : '!';
TILDE : '~';
QUESTION : '?';
COLON : ':';
EQUAL : '==';
LE : '<=';
GE : '>=';
NOTEQUAL : '!=';
AND : '&&';
OR : '||';
INC : '++';
DEC : '--';
ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';
BITAND : '&';
BITOR : '|';
CARET : '^';
MOD : '%';
ARROW : '->';
COLONCOLON : '::';

ADD_ASSIGN : '+=';
SUB_ASSIGN : '-=';
MUL_ASSIGN : '*=';
DIV_ASSIGN : '/=';
AND_ASSIGN : '&=';
OR_ASSIGN : '|=';
XOR_ASSIGN : '^=';
MOD_ASSIGN : '%=';
LSHIFT_ASSIGN : '<<=';
RSHIFT_ASSIGN : '>>=';
URSHIFT_ASSIGN : '>>>=';

// §3.8 Identifiers (must appear after all keywords in the grammar)

Identifier
  : JavaLetter JavaLetterOrDigit*
  ;
fm_Identifier: FM_PLACEHOLDER | FM_IF (fm_Identifier | Identifier) (FM_ELSE_IF (fm_Identifier | Identifier))* FM_ELSE (fm_Identifier | Identifier) FM_IF_CLOSE;
fm_IdentifierOpt: FM_PLACEHOLDER | FM_IF (fm_IdentifierOpt | Identifier)? (FM_ELSE_IF (fm_IdentifierOpt | Identifier)?)* (FM_ELSE (fm_IdentifierOpt | Identifier)?)? FM_IF_CLOSE;

fragment
JavaLetter
  : [a-zA-Z$_] // these are the "java letters" below 0xFF
  | // covers all characters above 0xFF which are not a surrogate
    ~[\u0000-\u00FF\uD800-\uDBFF]
    {Character.isJavaIdentifierStart(_input.LA(-1))}?
  | // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    [\uD800-\uDBFF] [\uDC00-\uDFFF]
    {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
  ;

fragment
JavaLetterOrDigit
  : [a-zA-Z0-9$_] // these are the "java letters or digits" below 0xFF
  | // covers all characters above 0xFF which are not a surrogate
    ~[\u0000-\u00FF\uD800-\uDBFF]
    {Character.isJavaIdentifierPart(_input.LA(-1))}?
  | // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    [\uD800-\uDBFF] [\uDC00-\uDFFF]
    {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
  ;

//
// Additional symbols not defined in the lexical specification
//

AT : '@';
ELLIPSIS : '...';

//
// Whitespace and comments
//

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;

FM_PLACEHOLDER: '${#' ~'#'+? '#}';
FM_IF: '<#if' ~'>'+? '>';
FM_IF_CLOSE: '</#if>';
FM_ELSE_IF: '<#elseif' ~'>'+? '>';
FM_ELSE: '<#else>';
FM_LIST: '<#list' .+? 'as' ~'>'+? '>';
FM_LIST_CLOSE: '</#list>';