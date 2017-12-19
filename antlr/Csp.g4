
grammar Csp;


file
   : EOL* (paragraph EOL+)* EOF; // EOF is neccesary!

paragraph
   : assertion
   | declaration
   | definition
   ;

assertion
   : ASSERT expression MOREREF expression
   ;

declaration
   : CHANNEL ID (COMMA ID)*
   ;

definition
   : ID ASSIGN expression
   ;


expression
   : ID                                                    # eid
   | SKIPP                                                 # eskip
   | STOPP                                                 # estop
   | LPAREN expression RPAREN                              # enop
   | ID ARROW expression                                   # earrow
   | expression SEQ expression                             # eseq
   | expression CHOICE expression                          # echoice
   | expression HIDE set                                   # ehide
   | expression INTER expression                           # einter
   | LPAREN expression LSYNC set RSYNC expression RPAREN   # esync
   ;

set
   : LCURLYBRACKET (ID (COMMA ID)*)? RCURLYBRACKET
   ;

/* Lexer
======================================================================== */

   CHOICE            : ('|~|' | '[]');
   ARROW             : '->';
   SEQ               : ';';
   HIDE              : '\\';
   LSYNC             : '[|';
   RSYNC             : '|]';
   INTER             : '|||';
   ASSIGN            : '=';
   LPAREN            : '(';
   RPAREN            : ')';
   LCURLYBRACKET     : '{';
   RCURLYBRACKET     : '}';
   COMMA             : ',';
   CHANNEL           : 'channel';
   ASSERT            : 'assert';
   MOREREF           : '[T=';
   STOPP             : 'STOP';
   SKIPP             : 'SKIP';

   fragment LETTER   : ([a-z]|[A-Z]);
   fragment DIGIT    : [0123456789];

   ID                : (LETTER) (LETTER | DIGIT | '_')*;

   EOL               : [\r\n]+[ \t\r\n]*;

   LINE_COMMENT      : '--  ' ~[\r\n]* -> skip;   // skip Comments
   WS                : [ \t]+ -> skip;            // skip spaces, tabs
