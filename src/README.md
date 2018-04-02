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

# Compilador - Analisador Sintático


### BNF para a linguagem proposta

<decl_var> 		::= 	<tipo> <id> {,<id>}* ";"
<tipo> 			::= 	int | float | char
<programa>		::=	 int main"("")" <bloco>
<bloco> 		::= 	“{“ {<decl_var>}* {<comando>}* “}”
<comando>		::= 	<comando_basico> | <iteracao> | if "("<expr_relacional>")" <comando> {else <comando>}?
<comando_basico>	::= 	<atribuicao> | <bloco>
<iteracao> 		::=	 while "("<expr_relacional>")" <comando> | do <comando> while "("<expr_relacional>")"";"
<atribuicao> 		::=	 <id> "=" <expr_arit> ";"
<expr_relacional>	::= 	<expr_arit> <op_relacional> <expr_arit>
<expr_arit> 		::=	 <expr_arit> "+" <termo>   | <expr_arit> "-" <termo> | <termo>
<termo> 		::= 	<termo> "*" <fator> | <termo> “/” <fator> | <fator>
<fator> 		::=	 “(“ <expr_arit> “)” | <id> | <real> | <inteiro> | <char>
<op_relacional> 	::= 	"==", "!=", "<", ">", "<=", ">="


