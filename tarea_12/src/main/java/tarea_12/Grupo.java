package tarea_12;

import java.io.Serializable;

public class Grupo implements Serializable {

	private static final long serialVersionUID = 7464692470382357789L;

	// Atributos privados de la clase Grupo:

	private int numeroGrupo;
	private String nombreGrupo;

	// Constructores de la clase Grupo:

	public Grupo(int numeroGrupo, String nombreGrupo) {

		this.numeroGrupo = numeroGrupo;
		this.nombreGrupo = nombreGrupo;
	}

	// Getters & Setters:

	public int getNumeroGrupo() {
		return numeroGrupo;
	}

	public void setNumeroGrupo(int numeroGrupo) {
		this.numeroGrupo = numeroGrupo;
	}

	public String getNombreGrupo() {
		return nombreGrupo;
	}

	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}
}