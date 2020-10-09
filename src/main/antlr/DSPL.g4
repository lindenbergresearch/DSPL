/*                                                                     *\
**       __   ___  ______                     __         __            **
**      / /  / _ \/_  __/                 ___/ /__ ___  / /            **
**     / /__/ , _/ / /    Lindenberg     / _  (_-</ _ \/ /__           **
**    /____/_/|_| /_/  Research Tec.     \_,_/___/ .__/____/           **
**                                              /_/                    **
**                                                                     **
**                                                                     **
**	  https://github.com/lindenbergresearch/LRTRack	                   **
**    heapdump@icloud.com                                              **
**		                                                               **
**    Digital Signal Programming Language                              **
**    Copyright 2017-2020 by Patrick Lindenberg / LRT                  **
**                                                                     **
**    For Redistribution and use in source and binary forms,           **
**    with or without modification please see LICENSE.                 **
**                                                                     **
\*                                                                     */


grammar DSPL;


program
   : statement +
   ;

statement
   : 'if' paren_expr statement
   | 'if' paren_expr statement 'else' statement
   | 'while' paren_expr statement
   | 'do' statement 'while' paren_expr ';'
   | '{' statement* '}'
   | expr ';'
   | ';'
   ;

paren_expr
   : '(' expr ')'
   ;

expr
   : test
   | id '=' expr
   ;

test
   : sum
   | sum '<' sum
   ;

sum
   : term
   | sum '+' term
   | sum '-' term
   ;

term
   : id
   | integer
   | paren_expr
   ;

id
   : STRING
   ;

integer
   : INT
   ;


STRING
   : [a-z]+
   ;


INT
   : [0-9] +
   ;

WS
   : [ \r\n\t] -> skip
   ;
