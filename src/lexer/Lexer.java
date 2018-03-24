package lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.regex.*;

@SuppressWarnings("unused")
public class Lexer {
	
	private Error erro;
	private File arquivo;
	private Pointer indicador;
	private FileReader reader;	
	private String frase;
	private Matcher matcher;
	private Pattern pattern;
	private char caracter = '¨';
	private char lastChar;
	private int ascii;
	private int tipo;
	boolean start = true;
	
	
	public Lexer(String args){ // construtor
		
		this.frase = new String();				 
		this.indicador = new Pointer();
		
		this.erro = new Error();
		
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
			return new Token(TokensId.PR_INT.getId(), TokensId.PR_INT.getNome());
		}else
		if(this.frase.equals("float")) {
			return new Token(TokensId.PR_FLOAT.getId(), TokensId.PR_FLOAT.getNome());
		}else
		if(this.frase.equals("char")) {
			return new Token(TokensId.PR_CHAR.getId(), TokensId.PR_CHAR.getNome());
		}else
		if(this.frase.equals("main")) {
			return new Token(TokensId.PR_MAIN.getId(), TokensId.PR_MAIN.getNome());
		}else
		if(this.frase.equals("if")) {
			return new Token(TokensId.PR_IF.getId(), TokensId.PR_IF.getNome());
		}else
		if(this.frase.equals("else")) {
			return new Token(TokensId.PR_ELSE.getId(), TokensId.PR_ELSE.getNome());
		}else
		if(this.frase.equals("for")) {
			return new Token(TokensId.PR_FOR.getId(), TokensId.PR_FOR.getNome());
		}else
		if(this.frase.equals("do")) {
			return new Token(TokensId.PR_DO.getId(), TokensId.PR_DO.getNome());
		}else
		if(this.frase.equals("while")) {
			return new Token(TokensId.PR_WHILE.getId(), TokensId.PR_WHILE.getNome());
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
		try {
			this.ascii = this.reader.read();
		} catch (IOException error) {
			error.getMessage();
		}
		if(this.caracter != '¨') {
			this.lastChar = this.caracter;
		}
		this.caracter = (char)this.ascii;
		this.indicador.move(this.caracter);
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
					this.erro.logError(4, this.indicador.showPosition(), this.caracter);				
					
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
				return new Token(TokensId.COMENTARIO.getId(), TokensId.COMENTARIO.getNome());
			}else {				
				return new Token(TokensId.DIVISAO.getId(), TokensId.DIVISAO.getNome());
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
			return new Token(TokensId.IDENTIFICADOR.getId(),this.frase);			
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
					this.erro.logError(1, this.indicador.showPosition(), this.caracter);					
					
				}	
				
				while( this.isNum() ) {
					this.incrementLexema();
					this.getNextChar();
				}				
				return new Token(TokensId.DECIMAL.getId(),this.frase);
			}
			return new Token(TokensId.INTEIRO.getId(),this.frase);
		}else
		
			
		if(this.caracter == '!') {
			this.incrementLexema();
			this.getNextChar();
			
			if(this.caracter == '=') {
				this.incrementLexema();
				this.getNextChar();				
				return new Token(TokensId.DIFERENCA.getId(), TokensId.DIFERENCA.getNome());
			}else {
				this.erro.logError(2, this.indicador.showPosition(), this.caracter);
				
			}
		}else
			
			
		if(this.caracter == '=') { //igualdade ou atribuição
			this.incrementLexema();
			this.getNextChar();
			
			if(this.caracter == '=') {
				this.incrementLexema();
				this.getNextChar();
				
				return new Token(TokensId.IGUALDADE.getId(), TokensId.IGUALDADE.getNome());
			}else {
				return new Token(TokensId.ATRIBUICAO.getId(), TokensId.ATRIBUICAO.getNome());
			}
			
		}else
		
			
		if(this.caracter == '+') {
			this.incrementLexema();
			this.getNextChar();
			return new Token(TokensId.SOMA.getId(), TokensId.SOMA.getNome());
			
		}else
			
			
		if(this.caracter == '-') {
			this.incrementLexema();
			this.getNextChar();
			return new Token(TokensId.SUBTRACAO.getId(), TokensId.SUBTRACAO.getNome());
			
		}else
				
			
		if(this.caracter == '*') {
			this.incrementLexema();
			this.getNextChar();
			return new Token(TokensId.MULTIPLICAO.getId(), TokensId.MULTIPLICAO.getNome());
		
		}else
			
		
		if(this.caracter == '<') {
			this.incrementLexema();
			this.getNextChar();
			
			if(this.caracter == '=') {
				this.incrementLexema();
				this.getNextChar();
				return new Token(TokensId.MENOR_IGUAL.getId(), TokensId.MENOR_IGUAL.getNome());
			}else {
				return new Token(TokensId.MENOR_QUE.getId(), TokensId.MENOR_QUE.getNome());
			}			
		}else
			
			
		if(this.caracter == '>') {
			this.incrementLexema();
			this.getNextChar();
			
			if(this.caracter == '=') {
				this.incrementLexema();
				this.getNextChar();
				return new Token(TokensId.MAIOR_IGUAL.getId(), TokensId.MAIOR_IGUAL.getNome());
			}else {
				return new Token(TokensId.MAIOR_QUE.getId(), TokensId.MAIOR_QUE.getNome());
			}			
		}else
			
			
		if(this.caracter == ',') {			
			this.getNextChar();
			return new Token(TokensId.VIRGULA.getId(), TokensId.VIRGULA.getNome());
		}else
			
		
		if(this.caracter == ';') {
			this.getNextChar();
			return new Token(TokensId.PONTO_VIRGULA.getId(), TokensId.PONTO_VIRGULA.getNome());
		}else
			
			
		if(this.caracter == '(') {
			this.getNextChar();
			return new Token(TokensId.ABRE_PARENTESES.getId(), TokensId.ABRE_PARENTESES.getNome());
		}else

			
		if(this.caracter == ')') {
			this.getNextChar();
			return new Token(TokensId.FECHA_PARENTESES.getId(), TokensId.FECHA_PARENTESES.getNome());
		}else
			
			
		if(this.caracter == '{') {
			this.getNextChar();
			return new Token(TokensId.ABRE_BLOCO.getId(), TokensId.ABRE_BLOCO.getNome());
		}else
			
			
		if(this.caracter == '}') {
			this.getNextChar();
			return new Token(TokensId.FECHA_BLOCO.getId(), TokensId.FECHA_BLOCO.getNome());
		}else
			
			
		if(this.caracter == '\'') {//aspa simples			
			
			this.getNextChar();		
			
			if( this.isCharOrNum() ) {
				
				this.incrementLexema();
				this.getNextChar();
				
				if(this.caracter == '\'') {
					
					this.getNextChar();
					return new Token(TokensId.PR_CHAR.getId(), this.frase);
				}else {
					this.erro.logError(6, this.indicador.showPosition(), this.caracter);
				
				}
				
			}else {
				this.erro.logError(5, this.indicador.showPosition(), this.caracter);
				
			}
		}
		
		
		this.erro.showAllErrors();
		
		
		//se nenhuma das opções, logo é fim de arquivo
		return new Token(TokensId.ENDFILE.getId(), TokensId.ENDFILE.getNome());
		
	}
	
	
	
	public Token getNextToken() throws IOException {
		if(this.start == true) {
			this.getNextChar();
			this.start = false;
		}	
		return this.getToken();
	}	
}



