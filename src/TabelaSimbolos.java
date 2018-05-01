
import java.util.Stack;


public class TabelaSimbolos {
		
	private Stack<Grau> pilha;
			
	public TabelaSimbolos(){
		this.pilha = new Stack<>();		
	}
	
	public void criarNovoBloco() {
		this.pilha.push( new Grau() );
	}
	
	
	public void add(Variavel minhaVar){
		
		
		Grau helper = this.pilha.pop();	
		for(Variavel var : helper.getVarLista()) {
			if( var.getNomeVar().equals(minhaVar.getNomeVar()) ) {
				System.out.println("\033[91m"+"Erro: " + var.getNomeVar() + " já declarada no mesmo escopo");
				System.exit(0);
			}
		}
	
		helper.add(minhaVar);
		this.pilha.push(helper);		
	}
	
	public boolean existe(Variavel varCheck) {
		Grau helper = this.pilha.pop();	//variaveis do escopo atual	
		for(Variavel var : helper.getVarLista()) {
			if( var.getTipoVar() == varCheck.getTipoVar() && var.getNomeVar().equals(varCheck.getNomeVar()) ) {
				return true;
			}
		}
		return false;
	}
	
	public void deletarBloco() {
		Grau helper = this.pilha.pop();
		helper.getVarLista().clear();
	}
	
	
}

