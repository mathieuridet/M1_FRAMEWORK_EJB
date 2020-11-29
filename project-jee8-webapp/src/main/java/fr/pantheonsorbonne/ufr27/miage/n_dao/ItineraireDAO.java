package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;

@ManagedBean
public class ItineraireDAO {

	@Inject
	EntityManager em;

	public Itineraire getItineraireById(int idItineraire) {
		return em.createNamedQuery("Itineraire.getItineraireById", Itineraire.class).setParameter("id", idItineraire)
				.getSingleResult();
	}

	public Itineraire getItineraireByTrainEtEtat(int idTrain, CodeEtatItinieraire etat)
			throws MulitpleResultsNotExpectedException {
		
		System.out.println("== getItineraireByTrainEtEtat ==");
		
		List<Itineraire> itineraires = getAllItinerairesByTrainEtEtat(idTrain, etat);
		if (itineraires.isEmpty()) {
			return null;
		} else if (itineraires.size() > 1) {
			throw new MulitpleResultsNotExpectedException("Expected only one 'Itineraire'");
		}
		return itineraires.get(0);
	}

	public List<Itineraire> getAllItinerairesByTrainEtEtat(int idTrain, CodeEtatItinieraire etat) {
		return (List<Itineraire>) em.createNamedQuery("Itineraire.getItineraireByTrainEtEtat", Itineraire.class)
				.setParameter("idTrain", idTrain).setParameter("etat", etat.getCode()).getResultList();
	}

	public void associerIncidentItineraire(Itineraire itineraire, Incident incident) {
		em.getTransaction().begin();
		itineraire.setIncident(incident);
		em.getTransaction().commit();
	}

	public void majEtatItineraire(Itineraire itineraire, int newEtat) {
		em.getTransaction().begin();
		itineraire.setEtat(newEtat);
		em.getTransaction().commit();
	}

	public void majArretActuel(Itineraire itineraire, Arret arret) {
		em.getTransaction().begin();
		itineraire.setArretActuel(arret);
		em.getTransaction().commit();
	}

	public void supprimerArretDansUnItineraire(Itineraire itineraire, Arret arret) {
		// On supprime l'arrêt de l'itinéraire
		em.getTransaction().begin();
		itineraire.getGaresDesservies().remove(arret);
		em.remove(arret);
		em.getTransaction().commit();
	}

	public void ajouterUnArretDansUnItineraire(Itineraire itineraire, Arret arret, Gare gare, List<Trajet> trajets) {
		em.getTransaction().begin();
		// On ajoute l'arrêt à l'itinéraire
		for (int i = 0; i < trajets.size(); i++) {
			if (gare.equals(trajets.get(i).getGareArrivee())) {
				if (i == trajets.size() - 1) {
					// arrêt qu'on s'ajoute à la fin
					itineraire.addArret(arret);
				} else {
					// arrêt qu'on ajoute en cours d'itinéraire
					List<Arret> arretsDeTransition = new LinkedList<Arret>();
					int length = itineraire.getGaresDesservies().size();
					for (int j = i + 1; j < length; j++) {
						arretsDeTransition.add(itineraire.getGaresDesservies().remove(j));
					}
					itineraire.addArret(arret);
					itineraire.getGaresDesservies().addAll(arretsDeTransition);
				}
			}
		}
		em.getTransaction().commit();
	}

	public void retarderTrain(LocalTime tempsRetard, Arret arretActuel, Itineraire itineraire) {
		em.getTransaction().begin();

		if (LocalDateTime.now().isBefore(arretActuel.getHeureDepartDeGare())) {
			arretActuel.setHeureDepartDeGare(arretActuel.getHeureDepartDeGare().plus(tempsRetard.toSecondOfDay(), ChronoUnit.SECONDS));
		}

		for (Arret a : itineraire.getGaresDesservies()) {
			if (a.getHeureArriveeEnGare().isAfter(arretActuel.getHeureArriveeEnGare())) {
				a.setHeureArriveeEnGare(a.getHeureArriveeEnGare().plus(tempsRetard.toSecondOfDay(), ChronoUnit.SECONDS));
				a.setHeureDepartDeGare(a.getHeureDepartDeGare().plus(tempsRetard.toSecondOfDay(), ChronoUnit.SECONDS));
			}
		}

		em.getTransaction().commit();
	}

	public class MulitpleResultsNotExpectedException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6747179329715195790L;

		public MulitpleResultsNotExpectedException(String message) {
			super(message);
		}

	}

}