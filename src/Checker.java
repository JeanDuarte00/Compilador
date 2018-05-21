
public class Checker {

	
	/**
	 * Confirma e converte o tipo para o mais adequado
	 * */
	public int typeCheck(int ladoEsq, int ladoDir) {
		int tipo = -1;
		
		if(ladoEsq == TokensClasse.CARACTER.getClasse()) {
			if(ladoDir != TokensClasse.CARACTER.getClasse()) {
				tipo = TokensClasse.CARACTER.getClasse();
			}else {
				System.out.println("CHAR SÓ OPERA COM CHAR");
				System.exit(1);
			}
			
		}else if(ladoEsq == ladoDir) {
			tipo = ladoEsq; // qualquer um tem o mesmo efeito
		
		}else if(ladoEsq == TokensClasse.DECIMAL.getClasse() || ladoDir == TokensClasse.DECIMAL.getClasse()) {
			tipo = TokensClasse.DECIMAL.getClasse();
		
		}		
		
		return tipo;
	}
	
	
	
	/**
	 * Checa se a atribuição é possivel
	 * e caso seja, retorna true, a converção 
	 * só ocorre quando um DECIMAL receber um INT
	 * para qualquer outra combinação deve dar erro
	 **/
	public int atribCheck(int tipoIdentificador, int tipoValor) {		
		//System.out.println("IDENT: "+tipoIdentificador+"  TIPO: "+tipoValor);
		// caso sejam diferentes tentar converter		
		if( tipoIdentificador != tipoValor ) {
			
			if( tipoIdentificador == TokensClasse.DECIMAL.getClasse() ) {
				if( tipoValor == TokensClasse.INTEIRO.getClasse() ) {
					//System.out.println("ident = (float)" + tipoValor);
					tipoValor = TokensClasse.DECIMAL.getClasse(); // convertendo INT em DECIMAL para salvar na variavel
					return tipoValor;
				}else {
					System.out.println("ATRIBUIÇÂO COM TIPOS NÂO COMPATIVEIS; DECIMAL RECEBE INTEIRO E DECIMAL");
					return -1;
				}
			}
			System.out.println("ATRIBUIÇÂO COM TIPOS NÂO COMPATIVEIS; CONVERSÂO DE TIPOS INVALIDA");			
			return -1; // apenas float pode receber outro tipo convertido, portanto erro para qualquer outra combinação
		}
		
		
		return tipoValor; // caso os tipos sejam identicos, logo atribuição é válida		
	}
	
	
}
