# Compilador - Analisador Léxico

### Símbolos

letra ::= [a-z]

dígito ::= [0-9]

id ::= (letra | "_") (letra | "_" | dígito)*

oprelacional ::= <  |  >  |  <=  |  >=  |  ==  |  !=

oparitmético ::= "+"  |  "-"  |  "*"  |  "/"  |  "="

especial ::= ")"  |  "("  |  "{"  |  "}"  |  ","  |  ";"

palreservada ::= main  |  if  |  else  |  while  |  do  |  for  |  int  |  float  |  char

inteiro ::= dígito+

float ::= dígito*.dígito+

char ::= 'letra'  |  'dígito'         // Uma constante do tipo char (entre aspas simples)



### Aspectos Gerais da Linguagem

    Linguagem de formato livre.

    Linguagem é case sensitive, ou seja “WHILE” != “while”

    As palavras reservadas são delimitadas, no programa fonte, por brancos, operadores aritméticos ou símbolos especiais;

    Os comentários são delimitados por

        “//” - indicando comentário até o final da linha

        "/*" e "*/"



### Atribuições do Scanner

    Retornar: classificação e lexema

    Formato de mensagem de erro: "ERRO na linha n, coluna m, ultimo token lido t: mensagem"

     Considere o TAB como equivalente a 4 colunas

