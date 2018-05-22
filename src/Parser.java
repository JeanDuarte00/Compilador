
import java.io.IOException;

@SuppressWarnings("unused")
public class Parser{

	static Token token;		
	private ErrorParser error;
	private Lexer lexer;
	private TabelaSimbolos tabelaSimbolos;
	private Checker checker;
	private int ladoEsqAtrib;
	private int ladoDirAtrib;
	
	
	// Constructor	
	public Parser (String arquivo){
		this.checker = new Checker();
		this.error = new ErrorParser();
		this.lexer = new Lexer(arquivo);		
		this.tabelaSimbolos = new TabelaSimbolos();	
		
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
		
		this.tabelaSimbolos.criarNovoBloco();
		
		
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
		
		this.tabelaSimbolos.deletarBloco();
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
		
		//procura saber se existe a variavel no programa, não apenas no escopo
		if( this.tabelaSimbolos.existe(new Variavel(token.getClasse(), token.getLexema())) == null) {
			this.error.canNotAtrib(this.lexer.getPosicaoArquivo().toString());
		}
			
		this.ladoEsqAtrib = this.tabelaSimbolos.getTipoPR(token); // tipo do identificador
		//System.out.println("LADO ESQ ATRIB: "+this.ladoEsqAtrib);
		
		
		if( isFirstAtribuicao() ) {		
			this.getNextToken();
			if( token.getClasse() == TokensClasse.ATRIBUICAO.getClasse() ) { // '='
				
				this.getNextToken();				
				
				if( this.isFirstExpAritmetica() ) {				
					this.ladoDirAtrib = this.expAritmetica(); // lado direito da atribuição
					//System.out.println("LADO DIR ATRIB: "+this.ladoDirAtrib);
					
					// verifica se é possivel fazer atribuição
					this.ladoDirAtrib = checker.atribCheck(this.ladoEsqAtrib, this.ladoDirAtrib);
					if( this.ladoDirAtrib == -1 ) {
						this.error.imcompatibleTypes(this.lexer.getPosicaoArquivo().toString());
					}
					
					
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
		Token tipoPego = token;	// aqui vai pegar a classe da palavra reservada, portanto deve ser transformada para o tipo de dado
				
		this.getNextToken();
		if( token.getClasse() != TokensClasse.IDENTIFICADOR.getClasse() ) {//erro, deve ter um identificador			
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "1 identificador(es)", token.getLexema());
		}		
		
		
		this.tabelaSimbolos.add( new Variavel( this.tabelaSimbolos.getTipoPR(tipoPego), token.getLexema()) );	
		
					
		this.getNextToken();				
		while( token.getClasse() == TokensClasse.VIRGULA.getClasse() ){
			
			this.getNextToken();
			if( token.getClasse() != TokensClasse.IDENTIFICADOR.getClasse() ) {//erro, deve ter um identificador				
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "identificador(es)", token.getLexema());
			}
			
			
			this.tabelaSimbolos.add( new Variavel( this.tabelaSimbolos.getTipoPR(tipoPego), token.getLexema()) );
			
			
			this.getNextToken();
		}
		
		if( token.getClasse() != TokensClasse.PONTO_VIRGULA.getClasse() ) {//erro, deve ter um identificador				
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), ";", token.getLexema());
		}		
		
		
		/*Grau topo = this.tabelaSimbolos.pilha.pop();
		for(Variavel var : topo.getVarLista()) {
			System.out.println("TIPO VAR: "+var.getTipoVar());
			System.out.println("LEXE VAR: "+var.getNomeVar());
		}
		this.tabelaSimbolos.pilha.push(topo);
		System.out.println(" ");
		*/
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
		int esqRel, dirRel = -1;
		
		esqRel = this.expAritmetica();
		
		
		if( this.isOpRelacional() ) {
			this.getNextToken();			
			dirRel = this.expAritmetica();	
		}
		
		if(dirRel != -1) {
			if(esqRel != dirRel) {
				if(esqRel == TokensClasse.CARACTER.getClasse() || dirRel == TokensClasse.CARACTER.getClasse()) {
					System.out.println("CHAR SÓ PODE SER COMPARADO COM O MESMO TIPO, "+this.lexer.getPosicaoArquivo());	
				}
				
			}
		}else {
			
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
	public int expAritmetica() {
		int ladoEsq = this.termo();
				
		int ladoDir = this.exp();
		
		//System.out.print("ESQ :"+ladoEsq);
		//System.out.println(" ==== DIR :"+ladoDir);
		
		if(ladoDir != -1) {
			return this.checker.typeCheck(ladoEsq, ladoDir);
		}
		return ladoEsq;
					
		
	}
	private int exp() {
		int ladoEsq = -1;
		int ladoDir;
		
		if( token.getClasse() == TokensClasse.SOMA.getClasse() || token.getClasse() == TokensClasse.SUBTRACAO.getClasse() ) {			
			this.getNextToken();
			ladoEsq = this.termo();
			ladoDir = this.exp();
						
			if(ladoDir != -1) {
				ladoEsq =  checker.typeCheck(ladoEsq, ladoDir);
			}else {
				return ladoEsq;
			}
		}
		return ladoEsq;
	}
	
		
	//<termo> ::= <fator> |  <termo> "*" <fator> | <termo> “/” <fator>
	public boolean isFirstTermo() {
		
		if( this.isFirstFator() ) {
			return true;
		}
		return false;
	}	
	public int termo() {
		int ladoEsq = this.fator();		
		int ladoDir;
		Token div= token;
		
		while( token.getClasse() == TokensClasse.MULTIPLICAO.getClasse() || token.getClasse() == TokensClasse.DIVISAO.getClasse() ) {	
			this.getNextToken();
			ladoDir = this.fator();			

						
			if( div.getClasse() == TokensClasse.DIVISAO.getClasse() ) {				
				
				if(ladoEsq == TokensClasse.CARACTER.getClasse()) {
					if(ladoDir != TokensClasse.CARACTER.getClasse()) {
						this.error.charOnlyDivChar(this.lexer.getPosicaoArquivo().toString(), token.getLexema());
					}
					ladoEsq = TokensClasse.CARACTER.getClasse();
				}else
				if(ladoEsq == TokensClasse.INTEIRO.getClasse() || ladoDir == TokensClasse.INTEIRO.getClasse() || ladoEsq == TokensClasse.DECIMAL.getClasse() || ladoDir == TokensClasse.DECIMAL.getClasse()) {
					ladoEsq = TokensClasse.DECIMAL.getClasse(); //converte em DECIMAL	
				}else {
					this.error.canNotDiv(this.lexer.getPosicaoArquivo().toString());
				}	
			}else {			
				ladoEsq = checker.typeCheck(ladoEsq, ladoDir);
			}
					

		}
		return ladoEsq;
		
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
	public int fator() {
		
		int tipo = token.getClasse();
		String lex = token.getLexema();
		
		
		if( token.getClasse() == TokensClasse.IDENTIFICADOR.getClasse() ) {
			tipo = this.tabelaSimbolos.getTipoPR(token); // tipo do identificador
			this.getNextToken();
					
		}else
		if( token.getClasse() == TokensClasse.INTEIRO.getClasse() ||  token.getClasse() == TokensClasse.DECIMAL.getClasse() || token.getClasse() == TokensClasse.CARACTER.getClasse() ) {
			this.getNextToken();			
		
		}else		
		if( token.getClasse() == TokensClasse.ABRE_PARENTESES.getClasse() ) {

			this.getNextToken();
			tipo = this.expAritmetica();			
			
			if( token.getClasse() != TokensClasse.FECHA_PARENTESES.getClasse() ) {
				//error, deveria ter uma fechamento de parenteses	
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), ")", token.getLexema());
			}
			this.getNextToken();
		}else {
			// error, deveria ter uma abertura de parenteses, identificador ou uma variavel de tipo valido
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "variavel, '(' ou [interiro, float, char] ", token.getLexema());
		}		
		
		// caso tenhamos ((( var || 1 || 'c' ))) entramos recursivamente até o identificador ou valor
		
		if(tipo == -1){
			this.error.variavelNaoExiste(lexer.getPosicaoArquivo().toString(), lex);
		}
		return tipo;
	}
	private int getTipoDirAtribuicao() {
		
		if( token.getClasse() == TokensClasse.IDENTIFICADOR.getClasse() ) {
			return TokensClasse.IDENTIFICADOR.getClasse();
		}else
		if( token.getClasse() == TokensClasse.INTEIRO.getClasse() ) {
			return TokensClasse.INTEIRO.getClasse();
		}else
		if( token.getClasse() == TokensClasse.CARACTER.getClasse() ) {
			return TokensClasse.CARACTER.getClasse();
		}else
		if( token.getClasse() == TokensClasse.DECIMAL.getClasse() ) {
			return TokensClasse.DECIMAL.getClasse();
		}else {
			return -1; // trocar para código de erro
		}
		
	}

	
	
}
