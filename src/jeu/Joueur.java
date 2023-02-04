package jeu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Joueur {
	// Comparateurs pour le tri de joueurs
	public static Comparator<Joueur> triCarteCroissante = new Comparator<Joueur>() {
		@Override
		public int compare(Joueur j1, Joueur j2) {
			if (j1.getCarteChoisie() > j2.getCarteChoisie()) {
				return 1;
			} else {
				return -1;
			}
		}
	};
	public static Comparator<Joueur> triTetesRecupereesCroissant = new Comparator<Joueur>() {
		@Override
		public int compare(Joueur j1, Joueur j2) {
			if (j1.getCarteChoisie() > j2.getCarteChoisie()) {
				return 1;
			} else if (j1.getCarteChoisie() < j2.getCarteChoisie()) {
				return -1;
			} else { // En cas d'�galit� tri par nom
				return j1.getNom().compareTo(j2.getNom());
			}
		}
	};
	public static Comparator<Joueur> triTetesTotalesCroissant = new Comparator<Joueur>() {
		@Override
		public int compare(Joueur j1, Joueur j2) {
			if (j1.getTetesBoeufs() > j2.getTetesBoeufs()) {
				return 1;
			} else if (j1.getCarteChoisie() < j2.getCarteChoisie()) {
				return -1;
			} else { // En cas d'�galit� tri par nom
				return j1.getNom().compareTo(j2.getNom());
			}
		}
	};
	
	private String nom;

	private int tetesBoeufs = 0; // T�tes totales
	private int tetesRecuperees = 0; // T�tes r�cup�rees pendant ce tour
	
	private ArrayList<Integer> cartes = new ArrayList<>(10);
	private int carteChoisie = 0;

	public Joueur(String nom) {
		this.nom = nom;
	}

	// Affiche les cartes du joueur
	public void afficherCartes() {
		System.out.print("- Vos cartes : ");
		for (int i = 0; i < cartes.size(); i++) {
			System.out.print(Jeu.texteCarte(cartes.get(i)));
			if (i < cartes.size() - 1) {
				System.out.print(", ");
			}
		}
		System.out.println(); // Retour � la ligne
	}

	public int getTetesBoeufs() {
		return tetesBoeufs;
	}
	
	public int getTetesRecuperees() {
		return tetesRecuperees;
	}

	public void ajouterTetesRecuperees(int tetesBoeufs) {
		tetesRecuperees += tetesBoeufs;
	}
	
	// Ajoute les t�tes r�cup�res pendat la manche au total et remise � 0 des t�tes r�cup�r�es pendat la manche
	public void transfertTetes() {
		tetesBoeufs += tetesRecuperees;
		tetesRecuperees = 0;
	}

	public ArrayList<Integer> getCartes() {
		return cartes;
	}

	// Ajoute une carte et trie
	public void ajouterCarte(int carte) {
		cartes.add(carte);
		Collections.sort(cartes);
	}

	public void setCartes(ArrayList<Integer> cartes) {
		this.cartes = cartes;
	}

	/*
	 * Choisit une carte dans sa main
	 * Renvoie true si la carte peut bien �tre choisie
	 */
	public boolean choisirCarte(int carte) {
		if (cartes.contains(carte)) {
			carteChoisie = carte;
			// Retire la carte de la main du joueur 
			// Integer.valueOf(carte) permet de supprimer la carte dans la liste au lieu de supprimer la carte � l'index donn�
			cartes.remove(Integer.valueOf(carte));
			return true;
		}

		return false;
	}
	
	public int getCarteChoisie() {
		return carteChoisie;
	}
	
	public void retirerCarteChoisie() {
		carteChoisie = 0;
	}

	public String getNom() {
		return nom;
	}
}
