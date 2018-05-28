
import java.io.IOException;

@SuppressWarnings("unused")
public class Parser{
	String lex;
	int tipo;
	static Token token;		
	private ErrorParser error;
	private Lexer lexer;
	private TabelaSimbolos tabelaSimbolos;
	private Checker checker;
	private Code ladoEsqAtrib, ladoDirAtrib;	
	private int counter;
	private int whileCounter;
	private int ifCounter;
	private int doWhileCounter;
	
	private String masOP;
	
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
			
		int n = this.tabelaSimbolos.getTipoPR(token); // tipo do identificador
		this.ladoEsqAtrib = new Code(n,token.getLexema());
		//System.out.println("LADO ESQ ATRIB: "+this.ladoEsqAtrib);
		//System.out.println("VAR atr= "+token.getLexema());
		
		if( isFirstAtribuicao() ) {		
			this.getNextToken();
			if( token.getClasse() == TokensClasse.ATRIBUICAO.getClasse() ) { // '='
				
				this.getNextToken();				
				
				if( this.isFirstExpAritmetica() ) {				
					this.ladoDirAtrib = this.expAritmetica(); // lado direito da atribuição
					//System.out.println("LADO ESQ ATRIB: "+this.ladoEsqAtrib);
					//System.out.println("LADO DIR ATRIB: "+this.ladoDirAtrib);
					if(this.ladoDirAtrib.getClasse() == TokensClasse.INTEIRO.getClasse() && this.ladoEsqAtrib.getClasse() == TokensClasse.DECIMAL.getClasse()) {
						System.out.println(this.newTemp()+" = (float)"+this.ladoDirAtrib.getLexema());
						this.ladoDirAtrib.lexema = "T"+counter;
					}
					// verifica se é possivel fazer atribuição
					this.ladoEsqAtrib.classe = checker.atribCheck(this.ladoEsqAtrib.getClasse(), this.ladoDirAtrib.getClasse());
					if( this.ladoEsqAtrib.classe == -1) {
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
		
		System.out.println(this.ladoEsqAtrib.getLexema()+" = "+this.ladoDirAtrib.getLexema());
	
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
	
	
	public void condicional() {
		String expRel = null;
		int cont  = ifCounter;
		
		if( token.getClasse() == TokensClasse.PR_IF.getClasse() ) {
			
			this.getNextToken();
			if( token.getClasse() != TokensClasse.ABRE_PARENTESES.getClasse() ) {
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "(", token.getLexema());
			}
			
			this.getNextToken();
			expRel = this.expRelacional();
							
			if( token.getClasse() != TokensClasse.FECHA_PARENTESES.getClasse() ) {
				this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), ")", token.getLexema());
			}			
			System.out.println("if "+ expRel +" == 0 goto"+" label_else_"+cont);
			this.ifCounter++;
			this.getNextToken();
			this.comando();
			
			System.out.println("goto label_fim_if_"+cont);
			
			System.out.println("label_else_"+cont+":");
			
			if( token.getClasse() == TokensClasse.PR_ELSE.getClasse() ) {
				this.getNextToken();
				this.comando();
			}
			
			System.out.println("label_fim_if_"+cont+":");
			
			
		}else {
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "if",token.getLexema());
		}
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
			this.condicional();

		}		
	}
	
	
	//<expr_relacional> ::= <expr_arit> <op_relacional> <expr_arit>
	public boolean isFirstExpRelacional() {
		if( this.isFirstExpAritmetica() ) {
			return true;
		}
		return false;
	}
	public String expRelacional() {
		Code esqRel, dirRel = null;
		Token op = null;
		
		esqRel = this.expAritmetica();
		
		
		if( this.isOpRelacional() ) {
			op = token;
			this.getNextToken();			
			dirRel = this.expAritmetica();	
		}
		
		if(dirRel != null) {
			if(esqRel.getClasse() != dirRel.getClasse()) {
				if((esqRel.getClasse() == TokensClasse.CARACTER.getClasse() && dirRel.getClasse() != TokensClasse.CARACTER.getClasse()) || (esqRel.getClasse() != TokensClasse.CARACTER.getClasse() && dirRel.getClasse() == TokensClasse.CARACTER.getClasse())) {
					this.error.checkChar(this.lexer.getPosicaoArquivo().toString());
				}
				
			}
		}
		
		System.out.println(this.newTemp()+"\t= "+esqRel.getLexema()+this.getOperator(op.getClasse())+dirRel.getLexema());
		esqRel.lexema = "T"+counter;
		
		return esqRel.lexema;		
		
		
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
	
	public void comandoWhile() {
		String expRel = null;
		int cont = whileCounter;
		
		System.out.println("label_while_inicio_"+cont+":");
		
		this.getNextToken();
		if( token.getClasse() != TokensClasse.ABRE_PARENTESES.getClasse() ) {
			//error, deveria ter um abre parenteses '('
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), "(", token.getLexema());
		}
		
		this.getNextToken();
		expRel = this.expRelacional();
					
		if( token.getClasse() != TokensClasse.FECHA_PARENTESES.getClasse() ) {
			//error, deveria ter um fecha parenteses ')'
			this.error.tokenErrado(lexer.getPosicaoArquivo().toString(), ")", token.getLexema());
		}
		System.out.println("if "+  expRel + " == 0 goto"+" label_while_fim_"+cont);
		this.whileCounter++;
		
		this.getNextToken();
		this.comando();		
		System.out.println("goto label_while_inicio_"+cont);
		
		System.out.println("label_while_fim_"+cont+":");
		
	} 
	
	public void comandoDoWhile() {
		String expRel = null;
		int cont = doWhileCounter;
		
		System.out.println("label_doWhile_inicio_"+cont);
		this.doWhileCounter++;
		
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
			expRel = this.expRelacional();
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
		
		System.out.println("if "+expRel+" != 0 goto label_doWhile_inicio_"+cont);
		
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
	public void iteracao() { // while ou doWhile
		
		if( token.getClasse() == TokensClasse.PR_WHILE.getClasse() ) {
			
			this.comandoWhile();
						
		}else			
		if( token.getClasse() == TokensClasse.PR_DO.getClasse() ) { //do <comando> while "("<expr_relacional>")"";"
			
			this.comandoDoWhile();
			
		}
	}

	

	
	
	
	//<expr_arit> ::= <termo> |  <expr_arit> "+" <termo>   | <expr_arit> "-" <termo>  
	public boolean isFirstExpAritmetica() {
		
		if( this.isFirstTermo() ) {
			return true;
		}
		return false;
	}
	public Code expAritmetica() {
		Code ladoEsq = this.termo();
		Token op  = token;
		masOP = op.getLexema();
		Code ladoDir = this.exp();
		
		if(ladoDir != null) {
			
			if(ladoEsq.getClasse() == TokensClasse.INTEIRO.getClasse() && ladoDir.getClasse() == TokensClasse.DECIMAL.getClasse() ) {
				System.out.println(this.newTemp()+" = (float)"+ladoEsq.getLexema());
				ladoEsq.lexema = "T"+counter;
			}else if (ladoEsq.getClasse() == TokensClasse.DECIMAL.getClasse() && ladoDir.getClasse() == TokensClasse.INTEIRO.getClasse() ) {
				System.out.println(this.newTemp()+" = (float)"+ladoDir.getLexema());
				ladoDir.lexema = "T"+counter;
			}
			
if((ladoEsq.getClasse() == TokensClasse.CARACTER.getClasse() && ladoDir.getClasse() != TokensClasse.CARACTER.getClasse())||(ladoEsq.getClasse() != TokensClasse.CARACTER.getClasse() && ladoDir.getClasse() == TokensClasse.CARACTER.getClasse()) ){
				System.out.println("\033[91m" + "ERRO: "+this.lexer.getPosicaoArquivo().toString()+"\n\033[1;32m"+"CHAR APENAS OPERA COM CHAR");System.exit(0);		
			}
			int n = this.checker.typeCheck(ladoEsq.getClasse(), ladoDir.getClasse());
			
			ladoEsq.classe = n;			
			System.out.println(this.newTemp()+"= "+ladoEsq.getLexema()+this.getOperator(op.getClasse())+ladoDir.getLexema());
			ladoEsq.lexema = "T"+counter;
			
		}
	
		return ladoEsq;
					
		
	}
	private Code exp() {
		Code ladoEsq = null;
		Code ladoDir;
		
		//Token op = token;
		
		
		if( token.getClasse() == TokensClasse.SOMA.getClasse() || token.getClasse() == TokensClasse.SUBTRACAO.getClasse() ) {			
			//System.out.println("OP: "+token.getLexema());
			this.getNextToken();
			ladoEsq = this.termo();
			Token op = token;
			ladoDir = this.exp();

			
						
			if(ladoDir != null) {
if((ladoEsq.getClasse() == TokensClasse.CARACTER.getClasse() && ladoDir.getClasse() != TokensClasse.CARACTER.getClasse())||(ladoEsq.getClasse() != TokensClasse.CARACTER.getClasse() && ladoDir.getClasse() == TokensClasse.CARACTER.getClasse()) ){
				System.out.println("\033[91m" + "ERRO: "+this.lexer.getPosicaoArquivo().toString()+"\n\033[1;32m"+"CHAR APENAS OPERA COM CHAR");System.exit(0);		
			}
				ladoEsq.classe =  checker.typeCheck(ladoEsq.getClasse(), ladoDir.getClasse());
			}else {
				return ladoEsq;			
			}
			//System.out.println("OPCLASSE: "+op.getClasse());
			System.out.println(this.newTemp()+" = "+ladoEsq.getLexema()+  this.getOperator(op.getClasse())  +ladoDir.getLexema());
			ladoEsq.lexema = "T"+counter;
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
	public Code termo() {
		Code ladoEsq = this.fator();		
		Code ladoDir = null;
		Token op= token;
		masOP = token.lexema;
		
		while( token.getClasse() == TokensClasse.MULTIPLICAO.getClasse() || token.getClasse() == TokensClasse.DIVISAO.getClasse() ) {	
			op = token;
			this.getNextToken();
			ladoDir = this.fator();			
			
			if(ladoEsq.getClasse() == TokensClasse.INTEIRO.getClasse() && ladoDir.getClasse() == TokensClasse.DECIMAL.getClasse() ) {
				System.out.println(this.newTemp()+" = (float)"+ladoEsq.getLexema());
				ladoEsq.lexema = "T"+counter;
			}else if (ladoEsq.getClasse() == TokensClasse.DECIMAL.getClasse() && ladoDir.getClasse() == TokensClasse.INTEIRO.getClasse() ) {
				System.out.println(this.newTemp()+" = (float)"+ladoDir.getLexema());
				ladoDir.lexema = "T"+counter;
			}
			
			if((ladoEsq.getClasse() == TokensClasse.CARACTER.getClasse() && ladoDir.getClasse() != TokensClasse.CARACTER.getClasse())||(ladoEsq.getClasse() != TokensClasse.CARACTER.getClasse() && ladoDir.getClasse() == TokensClasse.CARACTER.getClasse()) ){
				System.out.println("\033[91m" + "ERRO: "+this.lexer.getPosicaoArquivo().toString()+"\n\033[1;32m"+"CHAR APENAS OPERA COM CHAR");System.exit(0);		
			}
						
			if( op.getClasse() == TokensClasse.DIVISAO.getClasse() ) {				
				
				if(ladoEsq.getClasse() == TokensClasse.CARACTER.getClasse()) {
					if(ladoDir.getClasse() != TokensClasse.CARACTER.getClasse()) {
						this.error.charOnlyDivChar(this.lexer.getPosicaoArquivo().toString(), token.getLexema());
					}
					ladoDir.classe = TokensClasse.CARACTER.getClasse();
				}else
				if( (ladoEsq.getClasse() == TokensClasse.INTEIRO.getClasse() && ladoDir.getClasse() == TokensClasse.DECIMAL.getClasse()) || (ladoEsq.getClasse() == TokensClasse.DECIMAL.getClasse() || ladoDir.getClasse() == TokensClasse.INTEIRO.getClasse()) ) {
					
					
					ladoDir.classe = TokensClasse.DECIMAL.getClasse(); //converte em DECIMAL	
				}else {
					this.error.canNotDiv(this.lexer.getPosicaoArquivo().toString());
				}	
			}			
			
			ladoEsq.classe = checker.typeCheck(ladoEsq.getClasse(), ladoDir.getClasse());				
									
			System.out.println(this.newTemp()+" = "+ladoEsq.getLexema()+  this.getOperator(op.getClasse())  +ladoDir.getLexema());
			ladoEsq.lexema  = "T"+counter;
				
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
	public Code fator() {
		Code code = null; int flag=0;
		tipo = token.getClasse();
		lex = token.getLexema();
		
		//System.out.println("lexer: "+lex);
			
		if( token.getClasse() == TokensClasse.IDENTIFICADOR.getClasse() ) {
			tipo = this.tabelaSimbolos.getTipoPR(token); // tipo do identificador
			if(tipo == -1){
				this.error.variavelNaoExiste(lexer.getPosicaoArquivo().toString(), token.getLexema());
			}
			this.getNextToken();
			flag = 1;
					
		}else
		if( token.getClasse() == TokensClasse.INTEIRO.getClasse() ||  token.getClasse() == TokensClasse.DECIMAL.getClasse() || token.getClasse() == TokensClasse.CARACTER.getClasse() ) {
			this.getNextToken();			
		
		}else		
		if( token.getClasse() == TokensClasse.ABRE_PARENTESES.getClasse() ) {
			
			this.getNextToken();//retornar null se forem iguais pois não tem que ter esse lado
			code = this.expAritmetica();// if(code.getClasse() == tipo) {lex = ""; tipo = code.getClasse();}			
			lex = code.getLexema();
			tipo = code.getClasse();
			                       
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
		
		//System.out.println("TIPO: "+tipo+" ::: LEX: "+lex);
		
		code = new Code(tipo, lex);
		//System.out.println("CODE::: tipo: "+code.getClasse()+"    ,lexema: "+code.getLexema());
		return code;
	}


	private String getOperator(int classe) {
		if(classe == TokensClasse.DIVISAO.getClasse()) {
			return "/";
		}
		if(classe == TokensClasse.MULTIPLICAO.getClasse()) {
			return "*";
		}
		if(classe == TokensClasse.SOMA.getClasse()) {
			return "+";
		}
		if(classe == TokensClasse.SUBTRACAO.getClasse()) {
			return "-";
		}
		if(classe == TokensClasse.MENOR_QUE.getClasse()) {
			return "<";
		}
		if(classe == TokensClasse.MAIOR_QUE.getClasse()) {
			return ">";
		}
		if(classe == TokensClasse.MENOR_IGUAL.getClasse()) {
			return "<=";
		}
		if(classe == TokensClasse.MAIOR_IGUAL.getClasse()) {
			return ">=";
		}
		if(classe == TokensClasse.DIFERENCA.getClasse()) {
			return "!=";
		}
		if(classe == TokensClasse.IGUALDADE.getClasse()) {
			return "==";
		}
		return "";
	}

	private String newTemp(){			
		this.counter++;
		return "T"+this.counter;
	}

	
	
}
