import lexer.Lexer;
import lexer.Token;
import lexer.TokensId;


public class Main {

	static Token token;
	
	public static void main(String args[]) throws Exception {
		
		Lexer lexer = new Lexer(args[0]); // cria o analisador lexico
		
		while(true) {			
					
			token = lexer.getNextToken();
			
			//System.out.println("tokenID: "+token.getId() +" | "+ "lexema: " + token.getLexema());				
			
			
			if(token.getId() == TokensId.ENDFILE.getId()) {	
				break;
			}
		}
		
	}
}
