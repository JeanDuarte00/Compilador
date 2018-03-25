package parser;

import java.io.IOException;
import lexer.Lexer;
import lexer.Token;
import lexer.TokensId;

public class Parser{

	static Token token;
	Lexer lexer;
	
	// Constructor
	public Parser (String arquivo){
		this.lexer = new Lexer(arquivo);		
				
	}
	
	public void execute() {
		
		if( this.isProgram() ) {
			
			System.out.println("Programa valido");
			
			
		}else {
			System.out.println("Programa invalido, função MAIN deve ser: 'int main ( ) {' ");
		}
		
	} 
	
	public void getNextToken() {
		try {
			token = this.lexer.getNextToken();
		} catch (IOException error) {
			
			error.getMessage();
		} 
	}
	
	public boolean isBloco() {
		
		this.getNextToken();		
		if( token.getId() == TokensId.ABRE_BLOCO.getId() ) {

			this.getNextToken();
			while( this.isDeclaracaoDeVariaveis() ) {
				this.getNextToken();
			}
			
			this.getNextToken();
			while( this.isComando() ) {
				this.getNextToken();
			}
			
			if( token.getId() != TokensId.FECHA_BLOCO.getId() ) {
				//erro, deveria fechar o bloc com }
			}
			
			return true;
		}
		
		return false;
	}
	
	public boolean isExpAritmetica() {
		return false;
	}
	
	public boolean isAtribuicao() {
		System.out.println("IS_ATRIBUICAO");
		System.out.println(token.getLexema());
		this.getNextToken();
		if( token.getId() == TokensId.IDENTIFICADOR.getId() ) {
			
			this.getNextToken();
			if( token.getId() == TokensId.ATRIBUICAO.getId() ) {
				
				this.getNextToken();
				if( this.isExpAritmetica() ) {
					
				}
				
			}	
		}
		
		return false;
	}
	
	public boolean isComandoBasico() {
		if( this.isAtribuicao() ) {
			return true;
		
		}else if( this.isBloco() ) {
			return true;
		}
		
		return false;
	}
	
	public boolean isCondicional() {
		return false;
	}
	
	public boolean isIteracao() {
		return false;
	}
	
	public boolean isComando() {
		
		if( this.isComandoBasico() ) {
			return true;
			
		}else if ( this.isCondicional() ) {
			return true;
			
		}else if ( this.isIteracao() ) {
			return true;
		}
		
		return false;
	}
	
	public boolean isProgram() {
		
		this.getNextToken();		
		if( token.getId() == TokensId.PR_INT.getId() ) {
			
			this.getNextToken();
			if( token.getId() == TokensId.PR_MAIN.getId() ) {
				
				this.getNextToken();
				if( token.getId() == TokensId.ABRE_PARENTESES.getId() ) {
				
					this.getNextToken();
					if( token.getId() == TokensId.FECHA_PARENTESES.getId() ) {
												
						if( this.isBloco() ) {
							
							return true;
							
						}					
						
					}					
					
				}
				
			}
			
		}
		
		return false;
	}
	
	public boolean isTipoValido() {
		if( token.getId() == TokensId.PR_INT.getId() ) {
			return true;
		}
		if( token.getId() == TokensId.PR_FLOAT.getId() ) {
			return true;
		}
		if( token.getId() == TokensId.PR_CHAR.getId() ) {
			return true;
		}
		return false;
	}
	
	public boolean isDeclaracaoDeVariaveis() {
		
		if( this.isTipoValido() ) {
			this.getNextToken();
			
			if( token.getId() == TokensId.IDENTIFICADOR.getId() ) {
				this.getNextToken();
				
				while( token.getId() != TokensId.PONTO_VIRGULA.getId() ){
					
					if( token.getId() != TokensId.VIRGULA.getId() ) {
						return false;
					}else {
						// erro, deve ter dvirgula para separar os identificadores
					}
					
					this.getNextToken();
					if( token.getId() != TokensId.IDENTIFICADOR.getId() ) {
						return false;
					}
					this.getNextToken();
				}
			}else {
				//erro, deve ter um identificador apos o tipo de valor
			}
		}
		return false;
	}
	
	
}
