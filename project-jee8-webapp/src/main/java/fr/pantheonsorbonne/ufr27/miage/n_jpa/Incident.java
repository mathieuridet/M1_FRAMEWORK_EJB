package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
@NamedQueries({
	@NamedQuery(name="Incident.getAllIncidents", query="SELECT i FROM Incident i"),
	@NamedQuery(name="Incident.getNbIncidents", query="SELECT COUNT(i) FROM Incident i"),
	@NamedQuery(name="Incident.getIncidentById", query="SELECT i FROM Incident i WHERE i.id = :id")
})
public class Incident {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	int typeIncident;
	LocalDateTime heureDebut;
	int duree;
	int etat;
	
	// TODO
	public String getStrEtat() {
		String strEtat = "";
		if(etat == 0) strEtat = "en cours";
		else if(etat == 1) strEtat = "terminé";
		else strEtat = "???";
		return strEtat;
	}

	public int getId() {
		return id;
	}

	public int getTypeIncident() {
		return typeIncident;
	}

	public void setTypeIncident(int typeIncident) {
		this.typeIncident = typeIncident;
	}

	public LocalDateTime getHeureDebut() {
		return heureDebut;
	}

	public void setHeureDebut(LocalDateTime heureDebut) {
		this.heureDebut = heureDebut;
	}

	public int getDuree() {
		return duree;
	}

	public void setDuree(int duree) {
		this.duree = duree;
	}

	public int getEtat() {
		return etat;
	}

	public void setEtat(int etat) {
		this.etat = etat;
	}
	
	public enum CodeEtatIncident {

		EN_COURS(1), RESOLU(0);

		private int code;

		private CodeEtatIncident(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}
	
	public enum CodeTypeIncident {

		ANIMAL_SUR_VOIE(1), ARBRE_SUR_VOIE(2), MALAISE_PASSAGER(3), FEUILLE_MOUILLIE_SUR_VOIE(4), PANNE_ELECTRIQUE(5), PERSONNE_SUR_VOIE(6);

		private int code;

		private CodeTypeIncident(int code) {
			this.code = code;
		}

		public static LocalTime getTempEstimation(int code) {
			switch (code) {
			case 1:
				return LocalTime.of(0, 5);

			case 2:
				return LocalTime.of(3, 0);
			
			case 3:
				return LocalTime.of(1, 0);
			
			case 4:
				return LocalTime.of(1, 30);
			
			case 5:
				return LocalTime.of(2, 0);
				
			case 6:
				return LocalTime.of(0, 30);
			
			default:
				return LocalTime.of(0, 0);
			}

		}
		
		public int getCode() {
			return code;
		}
	}
}