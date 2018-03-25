package parser;

import java.util.LinkedList;
import java.util.logging.Logger;

public class ErrorParser{

	private Logger logger;
	private LinkedList<String> ListaErro;
	
	public ErrorParser() {
		logger = Logger.getLogger(ErrorParser.class.getName());
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
	
	public void logError(int opcao, String posicao, char token) {
		
		switch( opcao ) {
		
			case 1:				
				this.ListaErro.add("ERRO na " + posicao + ",ultimo token lido: "+ token +" |  CARA, AQUI DEVERIA TER UM NÚMERO, NÃO ACHA?\n\n");
				
				break;
			
			case 2:
				
				break;
				
			case 3:
				
				break;
		
			case 4:
				
				break;
								
			case 5:
				
				break;
				
			case 6:
				
				break;
		}
		
		
	}
	
}
