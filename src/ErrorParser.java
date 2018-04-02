
import java.util.LinkedList;


public class ErrorParser{

	
	private LinkedList<String> ListaErro;
	
	
	public ErrorParser() {	
		this.ListaErro = new LinkedList<>();		
	}
	
	public int getQtdErros() {
		return this.ListaErro.size();
	} 
	
	public void showAllErrors() {		
		for(String log: this.ListaErro) {
			System.out.println(log);	
		}
	}
	
	public void arquivoVazio() {
		System.out.println("\\033[91m"+"Arquivo vazio");
	}
	
	public void tokenErrado(String posicao, String tokenEsperado, String tokenLido) {
		//this.ListaErro.add("ERRO: "+posicao+"\nTOKEN LIDO: "+"("+tokenLido+")"+"\nTOKEN ESPERADO "+"("+tokenEsperado+")\n");
		System.out.println("\033[91m"+"ERRO: "+posicao+"\n\033[1;32m"+"TOKEN LIDO: "+"("+tokenLido+")"+"\nTOKEN ESPERADO "+"("+tokenEsperado+")\n");
		System.exit(0);
	}
	
}
