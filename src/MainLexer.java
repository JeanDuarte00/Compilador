

public class MainLexer {

	static Token token;
	
	public static void main(String args[]) throws Exception {
		
		Lexer lexer = new Lexer(args[0]); // cria o analisador lexico
		
		while(true) {			
					
			token = lexer.getNextToken();
			
			System.out.println("tokenID: "+token.getClasse() +" | "+ "lexema: " + token.getLexema() +"| "+ lexer.getPosicaoArquivo().toString());				
			
			
			if(token.getClasse() == TokensClasse.ENDFILE.getClasse()) {	
				break;
			}
		}
		
	}
}
