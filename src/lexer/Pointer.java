package lexer;

public class Pointer {

	private int coluna;
	private int linha;
	
	private int getColuna() {
		return this.coluna;
	}

	private int getLinha() {
		return this.linha;
	}

	private void setColuna(int posicao) {
		this.coluna = posicao;
	}

	public void setLinha(int posicao) {
		this.linha = posicao;
	}
	
	private void moveLinha(){
		this.linha = this.linha + 1;
	}
	
	private void moveColuna(){
		this.coluna = this.coluna + 1;
	}
	
	public  void move(char caracter) {
		this.moveColuna();
		if(caracter == '\t') {
			this.setColuna(this.getColuna()+4);
						
		}
		if(caracter == '\n') {
			this.setColuna(1);
			this.moveLinha();
		}
		
	}
	
	public String showPosition() {
		
		return "LINHA: "+(this.getLinha())+" , COLUNA: "+(this.getColuna()-1);
		
	}
	
	public Pointer() {
		this.coluna = 1;
		this.linha = 1;
	}
	
	
	
}
