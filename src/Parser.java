
import java.io.IOException;


public class Parser{

	static Token token;
	@SuppressWarnings("unused")
	private int bloco;
	private ErrorParser error;
	private Lexer lexer;
	private TabelaSimbolos variaveisEscopo;
	
	// Constructor	
	public Parser (String arquivo){
		this.error = new ErrorParser();
		this.lexer = new Lexer(arquivo);		
		this.variaveisEscopo = new TabelaSimbolos();
		this.bloco = 0;
	}

	// token lido do lexer
	public void getNextToken() {
		try {

			do{
				token = this.lexer.getNextToken();
			}while( token.getClasse() == TokensClasse.COMENTARIO.getClasse() );
			//System.out.println("== Token lido: "+token.getLexema());
			if( token.getClasse() == TokensClasse.ENDFILE.getClasse() ) {
				this.error.showAllErrors();
				System.exit(0);
			}

		} catch (IOException error) {
			
			error.getMessage();
		} 
	}
	
	
	//<start ::= int main "(" ")" <bloco>
	public void execute() {
		
	
		this.getNextToken();
						
		if( token.getClasse() != TokensClasse.PR_INT.getClasse() ) { 
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "int", token.getLexema());			
		}

		this.getNextToken();			
		if( token.getClasse() != TokensClasse.PR_MAIN.getClasse() ) {
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "main", token.getLexema());
		}
		
		this.getNextToken();
		if( token.getClasse() != TokensClasse.ABRE_PARENTESES.getClasse() ) {
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "(", token.getLexema());
		}
		
		this.getNextToken();
		if( token.getClasse() != TokensClasse.FECHA_PARENTESES.getClasse() ) {
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), ")", token.getLexema());
		}
		
		this.getNextToken();
		this.bloco();
		
		if( token.getClasse() != TokensClasse.ENDFILE.getClasse() ){
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "EOF", token.getLexema());
		}			
		
	} 

	
	//<tipo> ::= int | float | char
	public boolean isFirstTipoValido() {
		
		if( token.getClasse() == TokensClasse.PR_INT.getClasse() ) {
			return true;
		}
		if( token.getClasse() == TokensClasse.PR_FLOAT.getClasse() ) {
			return true;
		}
		if( token.getClasse() == TokensClasse.PR_CHAR.getClasse() ) {
			return true;
		}
		return false;
	}
		
	
	//<bloco> ::= "{" {<decl_var>}* {<comando>}* "}"	
	public boolean isFirstBloco() {
		if( token.getClasse() == TokensClasse.ABRE_BLOCO.getClasse() ) {
			return true;
		}
		return false;
	}	
	public void bloco() {		
		// deve ser inicio de bloco
		if( !this.isFirstBloco() ) {			
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "{", token.getLexema());	
		}		
		
		this.variaveisEscopo.criarNovoBloco();
		
		this.getNextToken();
		
		while( this.isFirstDeclaracaoDeVariaveis() ) {
			this.declaracaoDeVariaveis();	
		}
					
		while( this.isFirstComando() ) {
			this.comando();
		}		
				
		if( token.getClasse() != TokensClasse.FECHA_BLOCO.getClasse() ) {
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "}", token.getLexema());
		}
		this.getNextToken();
					
		this.variaveisEscopo.deletarBloco();
	}
	
	
	//<comando_básico> ::= <atribuição> | <bloco>
	public boolean isFirstComandoBasico() {
		if( this.isFirstAtribuicao() ) {
			return true;		
		}
		if( this.isFirstBloco() ) {
			return true;
		}
		return false;
	}
	public void comandoBasico() {		
		if( this.isFirstAtribuicao() ) {			
			this.atribuicao();
			
		
		}else if( this.isFirstBloco() ) {			
			this.bloco();			
		}
	}
	
	
	//<atribuição> ::= <id> "=" <expr_arit> ";"
	public boolean isFirstAtribuicao() {
		if(token.getClasse() == TokensClasse.IDENTIFICADOR.getClasse()) {
			return true;
		}
		return false;
	}
	public void atribuicao() {
		Token var1 = token;
		
		if( isFirstAtribuicao() ) {		
			this.getNextToken();
			if( token.getClasse() == TokensClasse.ATRIBUICAO.getClasse() ) { // '='
				
				this.getNextToken();				
				
				if( this.isFirstExpAritmetica() ) {				
					this.expAritmetica();
					
				}else {
					this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "Expressão aritmetica", token.getLexema());
				}
				
				if(token.getClasse() != TokensClasse.PONTO_VIRGULA.getClasse()) {
					//mensagem de erro, deveria terminar com ponto e virgula
					this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), ";", token.getLexema());
				}
				this.getNextToken();
			}else {
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "=", token.getLexema());
			}
			
		}
	}
		
	
	//<decl_var> ::= <tipo> <id> {,<id>}* ";"
	public boolean isFirstDeclaracaoDeVariaveis() {
		if( this.isFirstTipoValido() ) {
			return true;
		}		
		return false;
	}
	public void declaracaoDeVariaveis() {
		Token tipoPego = token;	
		Variavel var;
		
		this.getNextToken();
		if( token.getClasse() != TokensClasse.IDENTIFICADOR.getClasse() ) {//erro, deve ter um identificador			
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "1 identificador(es)", token.getLexema());
		}		
		
		var = new Variavel( tipoPego.getClasse(), token.getLexema());
		this.variaveisEscopo.add(var);	
		
		
			
		this.getNextToken();				
		while( token.getClasse() == TokensClasse.VIRGULA.getClasse() ){
			
			this.getNextToken();
			if( token.getClasse() != TokensClasse.IDENTIFICADOR.getClasse() ) {//erro, deve ter um identificador				
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "identificador(es)", token.getLexema());
			}
			
			var = new Variavel( tipoPego.getClasse(), token.getLexema());			
			this.variaveisEscopo.add(var);	
			
			
			this.getNextToken();
		}
		
		if( token.getClasse() != TokensClasse.PONTO_VIRGULA.getClasse() ) {//erro, deve ter um identificador				
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), ";", token.getLexema());
		}		
		this.getNextToken();
	}
	
	
	//<comando> ::= <comando_básico> | <iteração> | if "("<expr_relacional>")" <comando> {else <comando>}?	
	public boolean isFirstComando() {
		if( this.isFirstComandoBasico() ) {
			return true;
			
		}
		if ( this.isFirstIteracao() ) {
			return true;
		}
		if( token.getClasse() == TokensClasse.PR_IF.getClasse() ) {
			return true;
		}
		return false;		
	}
	public void comando() {
				
		if( this.isFirstComandoBasico() ) {		
			this.comandoBasico();
			
		}else
		if ( this.isFirstIteracao() ) {			
			this.iteracao();
		
		}else {				
			if( token.getClasse() == TokensClasse.PR_IF.getClasse() ) {
				
				this.getNextToken();
				if( token.getClasse() != TokensClasse.ABRE_PARENTESES.getClasse() ) {
					this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "(", token.getLexema());
				}
				
				this.getNextToken();
				this.expRelacional();
								
				if( token.getClasse() != TokensClasse.FECHA_PARENTESES.getClasse() ) {
					this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), ")", token.getLexema());
				}			
			
				this.getNextToken();
				this.comando();
								
				if( token.getClasse() == TokensClasse.PR_ELSE.getClasse() ) {
					this.getNextToken();
					this.comando();
				}
				
				//this.getNextToken();
			}else {
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "if",token.getLexema());
			}

		}
		
		
	}
	
	
	//<expr_relacional> ::= <expr_arit> <op_relacional> <expr_arit>
	public boolean isFirstExpRelacional() {
		if( this.isFirstExpAritmetica() ) {
			return true;
		}
		return false;
	}
	public void expRelacional() {
		
		this.expAritmetica();
		
		
		if( this.isOpRelacional() ) {
			this.getNextToken();			
			this.expAritmetica();	
		}
		
		
	}

	
	
	//<op_relacional> ::= ">" | "<" | ">=" | "<=" | "==" | "!="
	public boolean isOpRelacional() {

		if(token.getClasse() == TokensClasse.IGUALDADE.getClasse()) {
			return true;
		}else
		if(token.getClasse() == TokensClasse.DIFERENCA.getClasse()) {
			return true;
		}else
		if(token.getClasse() == TokensClasse.MAIOR_IGUAL.getClasse()) {
			return true;
		}else
		if(token.getClasse() == TokensClasse.MENOR_IGUAL.getClasse()) {
			return true;
		}else
		if(token.getClasse() == TokensClasse.MENOR_QUE.getClasse()) {
			return true;
		}else
		if(token.getClasse() == TokensClasse.MAIOR_QUE.getClasse()) {
			return true;
		}else {
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "\">\" | \"<\" | \">=\" | \"<=\" | \"==\" | \"!=\"", token.getLexema());
		}
		return false;
	}
	
	
	
	//<iteração> ::= while "("<expr_relacional>")" <comando> | do <comando> while "("<expr_relacional>")"";"
	public boolean isFirstIteracao() {
		if( token.getClasse() == TokensClasse.PR_WHILE.getClasse() ) {
			return true;
		}
		if( token.getClasse() == TokensClasse.PR_DO.getClasse() ) {
			return true;
		}
		return false;
	}
	public void iteracao() {
		
		if( token.getClasse() == TokensClasse.PR_WHILE.getClasse() ) {
			
			this.getNextToken();
			if( token.getClasse() != TokensClasse.ABRE_PARENTESES.getClasse() ) {
				//error, deveria ter um abre parenteses '('
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "(", token.getLexema());
			}
			
			this.getNextToken();
			this.expRelacional();
						
			if( token.getClasse() != TokensClasse.FECHA_PARENTESES.getClasse() ) {
				//error, deveria ter um fecha parenteses ')'
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), ")", token.getLexema());
			}
			
			this.getNextToken();
			this.comando();		
			
			//this.getNextToken();
						
		}else			
		if( token.getClasse() == TokensClasse.PR_DO.getClasse() ) { //do <comando> while "("<expr_relacional>")"";"
			
			this.getNextToken();
			if( this.isFirstComando() ) {
				this.comando();
			}else {
				//error, deveria ter um comando aqui
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "Comando(s)", token.getLexema());
			}
	
			if( token.getClasse() != TokensClasse.PR_WHILE.getClasse() ) {
				//error, deveria ter um while aqui
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "while", token.getLexema());
			}
			
			this.getNextToken();
			if(token.getClasse() != TokensClasse.ABRE_PARENTESES.getClasse() ) {
				//error, deveria ter um abre parenteses '('
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "(", token.getLexema());
			}
			
			this.getNextToken();
			if( this.isFirstExpRelacional() ) {
				this.expRelacional();
			}else {
				//error, deveria ter uma expressão relacional aqui
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "Expressão Relacional", token.getLexema());
			}
						
			if( token.getClasse() != TokensClasse.FECHA_PARENTESES.getClasse() ) {
				//error, aqui deveria ter uma fecha parenteses aqui
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), ")", token.getLexema());
			}
			
			this.getNextToken();
			if( token.getClasse() != TokensClasse.PONTO_VIRGULA.getClasse() ) {
				//error, aqui deveria ter uma ponto e virgula aqui
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), ";", token.getLexema());
			}
			this.getNextToken();//proximo
		}
	}

	

	
	
	
	//<expr_arit> ::= <termo> |  <expr_arit> "+" <termo>   | <expr_arit> "-" <termo>  
	public boolean isFirstExpAritmetica() {
		
		if( this.isFirstTermo() ) {
			return true;
		}
		return false;
	}
	public void expAritmetica() {
		this.termo();
				
		exp();
	}
	private void exp() {
		
		if( token.getClasse() == TokensClasse.SOMA.getClasse() || token.getClasse() == TokensClasse.SUBTRACAO.getClasse() ) {			
			this.getNextToken();
			this.termo();
			this.exp();
			//this.expAritmetica();
		}
	}
	
		
	//<termo> ::= <fator> |  <termo> "*" <fator> | <termo> “/” <fator>
	public boolean isFirstTermo() {
		
		if( this.isFirstFator() ) {
			return true;
		}
		return false;
	}	
	public void termo() {
		this.fator();
				
		while( token.getClasse() == TokensClasse.MULTIPLICAO.getClasse() || token.getClasse() == TokensClasse.DIVISAO.getClasse() ) {	
			this.getNextToken();
			this.fator();
			
		}		
	}
	
	
	//<fator> ::= “(“ <expr_arit> “)” | <id> | <real> | <inteiro> | <char>
	public boolean isFirstFator() {
		
		if( token.getClasse() == TokensClasse.ABRE_PARENTESES.getClasse() ) {
			return true;
		}
		if( token.getClasse() == TokensClasse.IDENTIFICADOR.getClasse() ) {			
			return true;
		}
		if( token.getClasse() == TokensClasse.INTEIRO.getClasse() ||  token.getClasse() == TokensClasse.DECIMAL.getClasse() || token.getClasse() == TokensClasse.CARACTER.getClasse() ) {			
			return true;
		}
		return false;
	}
	public void fator() {
		
		if( token.getClasse() == TokensClasse.IDENTIFICADOR.getClasse() ) {
			this.getNextToken();
		
		}else
		if( token.getClasse() == TokensClasse.INTEIRO.getClasse() ||  token.getClasse() == TokensClasse.DECIMAL.getClasse() || token.getClasse() == TokensClasse.CARACTER.getClasse() ) {
			this.getNextToken();
		
		}else		
		if( token.getClasse() == TokensClasse.ABRE_PARENTESES.getClasse() ) {

			this.getNextToken();
			this.expAritmetica();			
			
			if( token.getClasse() != TokensClasse.FECHA_PARENTESES.getClasse() ) {
				//error, deveria ter uma fechamento de parenteses	
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), ")", token.getLexema());
			}
			this.getNextToken();
		}else {
			// error, deveria ter uma abertura de parenteses, identificador ou uma variavel de tipo valido
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "variavel, '(' ou [interiro, float, char] ", token.getLexema());
		}
		
		
	}

	
	
}
