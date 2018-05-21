
import java.util.Stack;


public class TabelaSimbolos {
		
	public Stack<Grau> pilha;
			
	public TabelaSimbolos(){
		this.pilha = new Stack<>();		
	}
	
	public void criarNovoBloco() {
		this.pilha.push( new Grau() );
	}
	
	
	public int getTipoPR(Token varCheck) {
		
		/*Grau helper = this.pilha.pop();	//variaveis do escopo atual	
		
		for(Variavel var : helper.getVarLista()) {
			if(var.getNomeVar().equals(varCheck.lexema) ) {
				this.pilha.push(helper);
				
				if(var.getTipoVar() == TokensClasse.PR_FLOAT.getClasse()) {
					return TokensClasse.DECIMAL.getClasse();
				}else
				if(var.getTipoVar() == TokensClasse.PR_CHAR.getClasse()) {
					return TokensClasse.CARACTER.getClasse();
				}else
				if(var.getTipoVar() == TokensClasse.PR_INT.getClasse()) {
					return TokensClasse.INTEIRO.getClasse();
				}
			}
		}*/
		
		
		if(varCheck.getClasse() == TokensClasse.PR_FLOAT.getClasse()) {
			return TokensClasse.DECIMAL.getClasse();
			
		}else
		if(varCheck.getClasse() == TokensClasse.PR_CHAR.getClasse()) {
			return TokensClasse.CARACTER.getClasse();
			
		}else
		if(varCheck.getClasse() == TokensClasse.PR_INT.getClasse()) {			
			return TokensClasse.INTEIRO.getClasse();
			
		}else { // buscar na tabela de simbolos
			Variavel var = this.existe( new Variavel(varCheck.getClasse(), varCheck.getLexema()) );
			if( var == null ) {
				return -1;	
			}else {
				return var.getTipoVar();
			}
		}
		
		
		
	}
	
	/**
	 * Antes de colocar na tabela, deve-se verificar
	 * se já tem variavel com mesmo nome em mesmo escopo
	 * */
	public void add(Variavel minhaVar){
		
		if(this.pilha.isEmpty()) {
			this.pilha.push( new Grau() );
			return;
		}
		
		Grau topo = this.pilha.pop();
		
		for(Variavel var : topo.getVarLista()) {
			if( var.getNomeVar().equals(minhaVar.getNomeVar()) ) {
				System.out.println("\033[91m"+"Erro: " + minhaVar.getNomeVar() + " já declarada no mesmo escopo");
				System.exit(0);	
			}
		}			
		topo.getVarLista().add(minhaVar);
		this.pilha.push(topo);	
					
	}
	
	public Variavel existe(Variavel varCheck) {
		Stack<Grau> pilhaEx = new Stack<>();
		Grau helper;
		
		while( !this.pilha.isEmpty() ){			
			helper = this.pilha.pop();		
			pilhaEx.push( helper );
			
			for(Variavel var : helper.getVarLista()) {
				if(var.getNomeVar().equals(varCheck.getNomeVar()) ) {
					
					while( !pilhaEx.isEmpty() ) {	// coloca novamente na pilha principal				
						helper = pilhaEx.pop();	
						this.pilha.push( helper );	
					}
					
					return var; // já existe
				}
			}
			
		}
		
		while(!pilhaEx.isEmpty()) {
			this.pilha.push(pilhaEx.pop());
		}
		return null; // não existe
	}
	
	public void deletarBloco() {
		Grau topoPilha = this.pilha.pop();
		topoPilha.getVarLista().clear();
	}
	
	
}

