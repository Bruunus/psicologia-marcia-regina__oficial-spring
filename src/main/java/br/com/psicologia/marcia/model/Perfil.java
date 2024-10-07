package br.com.psicologia.marcia.model;

public enum Perfil {
	PSICOLOGIA,
	PSICOTERAPIA;
	
	/**
	 * Formatação em representatividade para o usuário
	 * @return
	 */
	public String getFormattedName() {
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
	
//	System.out.println(Perfil.PSICOLOGIA.getFormattedName()); // Saída: Psicologia
//    System.out.println(Perfil.PSICOTERAPIA.getFormattedName()); // Saída: Psicoterapia
	
	
}
