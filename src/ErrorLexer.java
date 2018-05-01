

import java.util.LinkedList;
//import java.util.logging.Logger;

public class ErrorLexer{

	//private Logger logger;
	private LinkedList<String> ListaErro;
	
	public ErrorLexer() {
		//logger = Logger.getLogger(ErrorLexer.class.getName());
		this.ListaErro = new LinkedList<>();		
	}
	
	public int getQtdErros() {
		return this.ListaErro.size();
	} 
	
	public void showAllErrors() {		
		for(String log: this.ListaErro) {
			//this.logger.warning(log);
			System.out.println(log);
		}
	}
	
	public void logError(int opcao, String posicao, char caracter) {
		
		String msg = "\033[91m"+"ERRO: "+ posicao + "\033[1;32m"+"\nULTIMO CARACTER LIDO: ";
		switch( opcao ) {
		
			case 1:				
				System.out.println(msg+caracter+"\nCARA, AQUI DEVERIA TER UM NÚMERO, NÃO ACHA?\n\n"+"\033[0m");
				System.exit(0);
				break;
			
			case 2:
				System.out.println(msg+caracter+"\nPARA VERIFICAR UMA DIFERENÇA DEVE-SE USAR != \n\n"+"\033[0m");
				System.exit(0);
				break;
				
			case 3:
				System.out.println(msg+caracter+"\nCOMPARAÇÃO DE VALOR DEVE USAR 2 '=', COMO EXEMPLO: == \n\n"+"\033[0m");
				System.exit(0);
				break;
		
			case 4:
				System.out.println(msg+"EOF"+"\nCOMENTARIO MULTILINHA DEVE SER FECHADO ANTES DE TERMINAR O ARQUIVO\n\n"+"\033[0m");
				System.exit(0);
				break;
								
			case 5:
				System.out.println(msg+caracter+"\nAPENAS LETRAS E NUMEROS PARA SER UM CHAR, ESPECIAIS SÃO INVALIDOS\n\n"+"\033[0m");
				System.exit(0);
				break;
				
			case 6:
				System.out.println(msg+caracter+"\nPARA FORMAR UM CHAR PRECISA SER ENTRA ASPAS SIMPLES: 'caracter ou digito' \n\n");
				System.exit(0);
				break;
				
			case 7:
				System.out.println(msg+caracter+"\nCARACTERES ESPECIAIS NÃO SÃO VALIDOS \n\n");
				System.exit(0);
				break;
		}
		
		
	}
	
}
