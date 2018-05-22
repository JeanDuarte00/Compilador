
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
	
	public void variavelNaoExiste(String posicao, String lex) {
		System.out.println("\033[91m" + "ERRO: " + posicao + "\n\033[1;32m" + "VARIAVEL '" + lex + "' NÃO EXISTE\n");
		System.exit(0);
	}
	
	public void charOnlyDivChar(String posicao, String lex) {
		System.out.println("\033[91m"+"ERRO: "+posicao+ "\n\033[1;32m"+ "CHAR SÓ PODE SER DIVIDIDO POR CHAR\n");
		System.exit(0);
	}
	
	public void canNotDiv(String posicao) {
		System.out.println("\033[91m"+"ERRO: "+posicao+"\n\033[1;32m" + "DIVISÃO DE VALORES INVALIDOS\n");
		System.exit(0);
	}
	
	public void imcompatibleTypes(String posicao) {
		System.out.println("\033[91m"+"ERRO: "+posicao+"\n\033[1;32m" + "ATRIBUIÇÂO COM TIPOS NÃO COMPATIVEIS\n");
		System.exit(0);
	}
	
	public void canNotAtrib(String posicao) {
		System.out.println("\033[91m"+"ERRO: "+posicao+"\n\033[1;32m" +"VARIAVEL NÂO DECLARADA, NÃO PODE FAZER ATRIBUIÇÂO\n");
		System.exit(0);
	}
	
}
