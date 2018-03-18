package lexer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Lex {
	
	File arquivo;
	Pointer indicador;
	FileReader reader;
	int ascii;
	char caracter;
	String frase;
	
public Token getNextToken() throws Exception {
		
		this.ascii = this.reader.read();//ler o caracter em ascii (primeiro do arquivo)
		
		
		while( this.ascii !=  -1) { // enquanto não é fim de arquivo
			this.caracter = (char)ascii;
			
			if( this.isNumber(caracter) ) {
				System.out.println("numero: "+this.caracter);
			}
			
			
			//depois de cada letra lida movemos o ponteiro de posição 
			indicador.move(this.caracter);
			
			//ler o proximo caracter
			this.ascii = this.reader.read();
			this.caracter = (char)ascii;
		}		
		
		
		
		return null;//retornar o token lido		
	}
	
	@SuppressWarnings("unused")
	private boolean isIdentificador(String frase) throws IOException {
		
		if( this.isChar(frase.charAt(0)) ) {
		
			for(int c = 1; c < frase.length(); c++) {
				if( !(this.isChar(frase.charAt(c)) || this.isNumber(frase.charAt(c))) ) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	private boolean isNumber(char caracter) throws IOException {
		if(caracter >= '0' && caracter <= '9') {
			while(caracter >= '0' && caracter <= '9') {
				this.ascii = this.reader.read();
				this.caracter = (char)this.ascii;
				this.frase = this.frase + this.caracter;
			}
			for(int c = 0; c < this.frase.length(); c++) {
				if( !(this.frase.charAt(c) >= '0' && this.frase.charAt(c) <= '9') ) {
					return false;
				}
			}
			return true;
		}else {
			return false;
		}
	}
	
	private boolean isChar(char caracter) {
		if(caracter >= 'A' && caracter <= 'z') {
			return true;
		}else {
			return false;
		}
				
	}

	boolean isCharToken(char caracter) {
		if(caracter == ';') {
			return true;
		}else {
		}if(caracter == '(') {
			return true;
		}else {
		}if(caracter == ')') {
			return true;
		}else {
		return false;
		}
	}
	
	
	boolean isPalavraReservada(String lexema) {
		
		if( lexema.equals("float") ) {
			lexema = "";
			return true;
		
		}else if( lexema.equals("double") ) {
			lexema = "";
			return true;
		
		}else {
			lexema = "";
			return false;
		
		}
	}
	
}
