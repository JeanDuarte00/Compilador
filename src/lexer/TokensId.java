package lexer;

public enum TokensId {
	ENDFILE(0, "END OF FILE"),
	CARACTER(1, "LETRA"),
	DIGITO(2, "NUMERAL"),
	DESCONHECIDO(3, "INVALIDO"),
	INTEIRO(4, "NUMERO INTEIRO"),
	DECIMAL(5, "NUMERO PONTO FLUTUANTE"),
	IDENTIFICADOR(6, "VARIAVEL"),
	MULTIPLICAO(7, "OPERADOR MULT (*)"),
	DIVISAO(8, "OPERADOR DIV (/)"),
	SOMA(9, "OPERADOR SOMA (+)"),
	SUBTRACAO(10, "OPERADOR SUB (-)"),
	ABRE_PARENTESES(11, "ABERTURA DE PARENTESES |(|"),
	FECHA_PARENTESES(12, "FECHAMENTO DE PARENTESES |)|"),
	ATRIBUICAO(13, "ATRIBUICAO (=)"),
	IGUALDADE(14, "COMPARACAO (==)"),
	DIFERENCA(14, "DIFERENCA (!=)"),
	COMENTARIO(17, "COMENTARIO"),
	RESERVADO(18, "PALAVRA RESERVADA"),
	MENOR_QUE(19, "MENOR QUE (<)"),
	MENOR_IGUAL(20, "MENOR IGUAL (<=)"),
	MAIOR_QUE(21, "MAIOR QUE (>)"),
	MAIOR_IGUAL(22, "MAIOR IGUAL (>=)"),
	ABRE_BLOCO(23, "ABERTURA DE BLOCK ({)"),
	FECHA_BLOCO(24, "FECHAMENTO DE BLOCO (})"),
	VIRGULA(25, "VIRGULA (,)"),
	PONTO_VIRGULA(26, "PONTO VIRGULA (;)"),
	PR_INT(27, "P.R INT"),
	PR_FLOAT(28, "P.R FLOAT"),
	PR_CHAR(29, "P.R CHAR"),
	PR_FOR(30, "P.R FOR"),
	PR_DO(31, "P.R DO"),
	PR_WHILE(32, "P.R WHILE"),
	PR_ELSE(33, "P.R ELSE"),
	PR_IF(34, "P.R IF"),
	PR_MAIN(35, "P.R MAIN");	
	
	private int id;
	private String nome;
	
	TokensId(int id, String nome){
		this.id = id;
		this.nome = nome;
	}

	public int getId() {
		return id;
	}

	public String getNome() {
		return this.nome;
	}
	
}
