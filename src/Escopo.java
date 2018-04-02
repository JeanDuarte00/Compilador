import java.util.LinkedList;

public class Escopo {
	
	LinkedList<Variavel> listaVariaveis = new LinkedList<Variavel>();
	
		
	public void add(Variavel var){
		this.listaVariaveis.add(var);
	}

}
