
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.regex.*;

@SuppressWarnings("unused")
public class Lexer {
	
	private ErrorLexer erro;
	private File arquivo;
	private Pointer indicador;
	private FileReader reader;	
	private String frase;	
	private char caracter = ' ';
	private char lastChar;
	private int ascii;
	private int tipo;
	boolean start = true;
	private Pattern charEspeciais = Pattern.compile("[@#$¨?~&]");
	private Matcher matcher;	
	
	public Pointer getPosicaoArquivo() {
		return this.indicador;
	}
	
	public Lexer(String args){ // construtor
		
		this.frase = new String();				 
		this.indicador = new Pointer();
		
		this.erro = new ErrorLexer();
		
		// abri o arquivo para leitura
		this.arquivo = new File(args);
		if (!arquivo.exists()) {
		    try {
				arquivo.createNewFile();
			} catch (IOException error) {				
				error.getMessage();
			}
		} 

		
		try {
			this.reader = new FileReader(this.arquivo);
		} catch (FileNotFoundException error) {
			error.getMessage();
		}
		

			 
	}
	
	private Token isPalavraReservada() {
		
		if(this.frase.equals("int")) {
			return new Token(TokensClasse.PR_INT.getClasse(), TokensClasse.PR_INT.getNome());
		}else
		if(this.frase.equals("float")) {
			return new Token(TokensClasse.PR_FLOAT.getClasse(), TokensClasse.PR_FLOAT.getNome());
		}else
		if(this.frase.equals("char")) {
			return new Token(TokensClasse.PR_CHAR.getClasse(), TokensClasse.PR_CHAR.getNome());
		}else
		if(this.frase.equals("main")) {
			return new Token(TokensClasse.PR_MAIN.getClasse(), TokensClasse.PR_MAIN.getNome());
		}else
		if(this.frase.equals("if")) {
			return new Token(TokensClasse.PR_IF.getClasse(), TokensClasse.PR_IF.getNome());
		}else
		if(this.frase.equals("else")) {
			return new Token(TokensClasse.PR_ELSE.getClasse(), TokensClasse.PR_ELSE.getNome());
		}else
		if(this.frase.equals("for")) {
			return new Token(TokensClasse.PR_FOR.getClasse(), TokensClasse.PR_FOR.getNome());
		}else
		if(this.frase.equals("do")) {
			return new Token(TokensClasse.PR_DO.getClasse(), TokensClasse.PR_DO.getNome());
		}else
		if(this.frase.equals("while")) {
			return new Token(TokensClasse.PR_WHILE.getClasse(), TokensClasse.PR_WHILE.getNome());
		}else {		
			return null;
		}
	}
	
	private boolean isEndFile() {
		return (this.caracter == -1);
	}
	
	private boolean isChar() {		
		return Character.isLetter(this.caracter);
		
	}
	
	private boolean isNum() {
		return Character.isDigit(this.caracter);
	}
	
	private boolean isCharOrNum() {
		return Character.isLetterOrDigit(this.caracter);
	}
	
	private void getNextChar() {		
		this.indicador.move(this.caracter);
		try {
			this.ascii = this.reader.read();
		} catch (IOException error) {
			error.getMessage();
		}
		if(this.caracter != ' ') {
			this.lastChar = this.caracter;
		}
		this.caracter = (char)this.ascii;
	
		
		this.matcher = charEspeciais.matcher(this.caracter+"");		
		if( this.matcher.find() ) {
			this.erro.logError(7, this.indicador.toString(), this.caracter);			
		}
	}
	
	private void incrementLexema() {
		this.frase += this.caracter;
	}

	private void getBlank() {	
		while( Character.isWhitespace(this.caracter) || Character.isSpaceChar(this.caracter)){
			this.getNextChar();
		}
	}

	private boolean isComment() {
		this.getNextChar();
		
		if(this.caracter == '/') { // comentario de linha
			this.getNextChar();
			while(this.caracter != '\n') {
				this.getNextChar();
			}
			return true;
		}
		
		if(this.caracter == '*') { // comentario multi-linha
			
			while(true) {
				
				this.getNextChar();
				if( this.ascii == -1 ) {
					this.erro.logError(4, this.indicador.toString(), this.caracter);				
					
					return false;
				}
				if(this.caracter == '*'){					
					this.getNextChar();
					if(this.caracter == '/') {
						this.getNextChar();
						return  true;
					}	
				}
			}			
		}		
		return false;
	}
	
	private Token getToken() {
		this.frase = "";
						
		this.getBlank();//pega os brancos e para no primeiro caracter valido achado		
		
		
		if(this.caracter == '/') { //comentario ou divisao			
			
			if( this.isComment() ) {
				return new Token(TokensClasse.COMENTARIO.getClasse(), TokensClasse.COMENTARIO.getNome());
			}else {				
				return new Token(TokensClasse.DIVISAO.getClasse(), TokensClasse.DIVISAO.getNome());
			}
		
		}else
		
			
		if( this.isChar() || this.caracter == '_' ) { //identificador ou palvra reservada
						
			while( this.isCharOrNum() || this.caracter == '_') {
				this.incrementLexema();
				this.getNextChar();
			}
			
			Token tokeN = this.isPalavraReservada();			
			if(tokeN != null) {
				return tokeN;
			} 			
			return new Token(TokensClasse.IDENTIFICADOR.getClasse(),this.frase);			
		}else
		
			
		if(this.isNum() || this.caracter == '.'){ //inteiro ou float
						
			while( this.isNum() ) {
				this.incrementLexema();
				this.getNextChar();
			}
						
			if( this.caracter == '.') {
				this.incrementLexema();
				this.getNextChar();
				if( !this.isNum() ) {
					//mensagem de erro					
					this.erro.logError(1, this.indicador.toString(), this.caracter);					
					
				}	
				
				while( this.isNum() ) {
					this.incrementLexema();
					this.getNextChar();
				}				
				return new Token(TokensClasse.DECIMAL.getClasse(),this.frase);
			}
			return new Token(TokensClasse.INTEIRO.getClasse(),this.frase);
		}else
		
			
		if(this.caracter == '!') {
			this.incrementLexema();
			this.getNextChar();
			
			if(this.caracter == '=') {
				this.incrementLexema();
				this.getNextChar();				
				return new Token(TokensClasse.DIFERENCA.getClasse(), TokensClasse.DIFERENCA.getNome());
			}else {
				this.erro.logError(2, this.indicador.toString(), this.caracter);
				
			}
		}else
			
			
		if(this.caracter == '=') { //igualdade ou atribuição
			this.incrementLexema();
			this.getNextChar();
			
			if(this.caracter == '=') {
				this.incrementLexema();
				this.getNextChar();
				
				return new Token(TokensClasse.IGUALDADE.getClasse(), TokensClasse.IGUALDADE.getNome());
			}else {
				return new Token(TokensClasse.ATRIBUICAO.getClasse(), TokensClasse.ATRIBUICAO.getNome());
			}
			
		}else
		
			
		if(this.caracter == '+') {
			this.incrementLexema();
			this.getNextChar();
			return new Token(TokensClasse.SOMA.getClasse(), TokensClasse.SOMA.getNome());
			
		}else
			
			
		if(this.caracter == '-') {
			this.incrementLexema();
			this.getNextChar();
			return new Token(TokensClasse.SUBTRACAO.getClasse(), TokensClasse.SUBTRACAO.getNome());
			
		}else
				
			
		if(this.caracter == '*') {
			this.incrementLexema();
			this.getNextChar();
			return new Token(TokensClasse.MULTIPLICAO.getClasse(), TokensClasse.MULTIPLICAO.getNome());
		
		}else
			
		
		if(this.caracter == '<') {
			this.incrementLexema();
			this.getNextChar();
			
			if(this.caracter == '=') {
				this.incrementLexema();
				this.getNextChar();
				return new Token(TokensClasse.MENOR_IGUAL.getClasse(), TokensClasse.MENOR_IGUAL.getNome());
			}else {
				return new Token(TokensClasse.MENOR_QUE.getClasse(), TokensClasse.MENOR_QUE.getNome());
			}			
		}else
			
			
		if(this.caracter == '>') {
			this.incrementLexema();
			this.getNextChar();
			
			if(this.caracter == '=') {
				this.incrementLexema();
				this.getNextChar();
				return new Token(TokensClasse.MAIOR_IGUAL.getClasse(), TokensClasse.MAIOR_IGUAL.getNome());
			}else {
				return new Token(TokensClasse.MAIOR_QUE.getClasse(), TokensClasse.MAIOR_QUE.getNome());
			}			
		}else
			
			
		if(this.caracter == ',') {			
			this.getNextChar();
			return new Token(TokensClasse.VIRGULA.getClasse(), TokensClasse.VIRGULA.getNome());
		}else
			
		
		if(this.caracter == ';') {
			this.getNextChar();
			return new Token(TokensClasse.PONTO_VIRGULA.getClasse(), TokensClasse.PONTO_VIRGULA.getNome());
		}else
			
			
		if(this.caracter == '(') {
			this.getNextChar();
			return new Token(TokensClasse.ABRE_PARENTESES.getClasse(), TokensClasse.ABRE_PARENTESES.getNome());
		}else

			
		if(this.caracter == ')') {
			this.getNextChar();
			return new Token(TokensClasse.FECHA_PARENTESES.getClasse(), TokensClasse.FECHA_PARENTESES.getNome());
		}else
			
			
		if(this.caracter == '{') {
			this.getNextChar();
			return new Token(TokensClasse.ABRE_BLOCO.getClasse(), TokensClasse.ABRE_BLOCO.getNome());
		}else
			
			
		if(this.caracter == '}') {
			this.getNextChar();
			return new Token(TokensClasse.FECHA_BLOCO.getClasse(), TokensClasse.FECHA_BLOCO.getNome());
		}else
			
			
		if(this.caracter == '\'') {//aspa simples			
			
			this.getNextChar();		
			
			if( this.isCharOrNum() ) {
				
				this.incrementLexema();
				this.getNextChar();
				
				if(this.caracter == '\'') {
					
					this.getNextChar();
					return new Token(TokensClasse.CARACTER.getClasse(), this.frase);
				}else {
					this.erro.logError(6, this.indicador.toString(), this.caracter);
				
				}
				
			}else {
				this.erro.logError(5, this.indicador.toString(), this.caracter);
				
			}
		}
		
		
		this.erro.showAllErrors();
		
		
		//se nenhuma das opções, logo é fim de arquivo
		return new Token(TokensClasse.ENDFILE.getClasse(), TokensClasse.ENDFILE.getNome());
		
	}
	
	
	
	public Token getNextToken() throws IOException {
		if(this.start == true) {
			this.getNextChar();
			this.start = false;
		}	
		return this.getToken();
	}	
}



