package lexer;

import java.util.LinkedList;
import java.util.logging.Logger;

public class ErrorLexer{

	private Logger logger;
	private LinkedList<String> ListaErro;
	
	public ErrorLexer() {
		logger = Logger.getLogger(ErrorLexer.class.getName());
		this.ListaErro = new LinkedList<>();		
	}
	
	public int getQtdErros() {
		return this.ListaErro.size();
	} 
	
	public void showAllErrors() {		
		for(String log: this.ListaErro) {
			this.logger.warning(log);			
		}
	}
	
	public void logError(int opcao, String posicao, char caracter) {
		
		switch( opcao ) {
		
			case 1:				
				this.ListaErro.add("ERRO na " + posicao + ",ultimo caracter lido: "+ caracter +" |  CARA, AQUI DEVERIA TER UM NÚMERO, NÃO ACHA?\n\n");
				
				break;
			
			case 2:
				this.ListaErro.add("ERRO na " + posicao + ",ultimo caracter lido: "+ caracter +" |  PARA VERIFICAR UMA DIFERENÇA DEVE-SE USAR != \n\n");
				break;
				
			case 3:
				this.ListaErro.add("ERRO na " + posicao + ",ultimo caracter lido: "+ caracter +" |  Comparação do tipo igualdade é deve usar 2 '=', como exemplo: == \n\n");
				break;
		
			case 4:
				this.ListaErro.add("ERRO na " + posicao + ",ultimo caracter lido: "+ "EOF" +" |  COMENTARIO MULTILINHA DEVE SER FECHADO ANTES DE TERMINAR O ARQUIVO\n\n");
				break;
								
			case 5:
				this.ListaErro.add("ERRO na " + posicao + ",ultimo caracter lido: "+ caracter +" |  APENAS LETRAS E NUMEROS PARA SER UM CHAR, ESPECIAIS SÃO INVALIDOS\n\n");
				break;
				
			case 6:
				this.ListaErro.add("ERRO na " + posicao + ",ultimo caracter lido: "+ caracter +" |  PARA FORMAR UM CHAR PRECISA SER ENTRA ASPAS SIMPLES: 'caracter ou digito' \n\n");
				break;
		}
		
		
	}
	
}
