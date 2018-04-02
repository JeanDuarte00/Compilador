
public class Variavel {
	
	
	private int tipoVar; 
	private String nomeVar;
	
	public int getTipo(){
		return this.tipoVar;
	}
	
	public String getNome(){
		return this.nomeVar;
	}
	
	public Variavel(int tipo, String nome){
		this.tipoVar = tipo;
		this.nomeVar = nome;
	}

}

