package br.com.psicologia.marcia.model.enums;

public enum Perfil {
	PSICOLOGIA,
	NEUROPSICOLOGIA;
	
	/**
	 * Formatação em representatividade para o usuário
	 * @return
	 */
	public String getFormattedName() {
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
	
//	System.out.println(Perfil.PSICOLOGIA.getFormattedName()); // Saída: Psicologia
//    System.out.println(Perfil.NEUROPSICOLOGIA.getFormattedName()); // Saída: Psicoterapia
	
	
}
