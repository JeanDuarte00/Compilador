
public class Checker {

	
	/**
	 * Confirma e converte o tipo para o mais adequado
	 * */
	public int typeCheck(int ladoEsq, int ladoDir) {
		int tipo = -1;
		
		
		if(ladoEsq == ladoDir) {
			tipo = ladoEsq; // qualquer um tem o mesmo efeito
		
		}else if( (ladoEsq == TokensClasse.DECIMAL.getClasse() && ladoDir == TokensClasse.INTEIRO.getClasse()) || (ladoEsq == TokensClasse.INTEIRO.getClasse() && ladoDir == TokensClasse.DECIMAL.getClasse()) ) {
			tipo = TokensClasse.DECIMAL.getClasse(); // casting de tipo float e int, qualquer relação floar com int retorna float
		
		}else if(ladoEsq == TokensClasse.CARACTER.getClasse() && ladoDir == TokensClasse.CARACTER.getClasse()) {
			tipo = TokensClasse.CARACTER.getClasse(); // se qualquer operação feita com char, deve retornar um char	
		
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
					return -1;
				}
			}						
			return -1; // apenas float pode receber outro tipo convertido, portanto erro para qualquer outra combinação
		}
		
		
		return tipoValor; // caso os tipos sejam identicos, logo atribuição é válida		
	}
	
	
}
