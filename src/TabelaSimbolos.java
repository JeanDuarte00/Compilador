
import java.util.Stack;


public class TabelaSimbolos {
		
	private Stack<Grau> pilha;
			
	public TabelaSimbolos(){
		this.pilha = new Stack<>();		
	}
	
	public void criarNovoBloco() {
		this.pilha.push( new Grau() );
	}
	
	public void inserirEmBloco(Variavel minhaVar){
		Grau helper = this.pilha.pop();
		helper.getVarLista().add(minhaVar);
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
		this.pilha.pop();
	}
	
	
}

