package jeu;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import jeu.utils.Console;
import jeu.utils.LectureFichier;

public class Jeu {

	private ArrayList<Integer>[] cartesSuites; // Tableau contenant pour chaque suite une liste de cartes
	private Joueur[] joueurs; // Tableau contenant les joueurs

	private int nbJoueurs;
	private int cartesRestantes = 0; // Nombre de cartes dans la main de chaque joueur
	
	private Scanner scanner;

	public Jeu() {
		scanner = new Scanner(System.in);
		
		creerJoueurs();
		distribuerCartes();

		boucleJeu();
		
		scanner.close();
	}

	private void creerJoueurs() {
		ArrayList<String> nomJoueurs = LectureFichier.lireFichier("./config.txt");
		nbJoueurs = nomJoueurs.size();

		joueurs = new Joueur[nbJoueurs];
		for (int i = 0; i < nbJoueurs; i++) {
			String nom = nomJoueurs.remove(0);
			joueurs[i] = new Joueur(nom);
		}
	}

	private void distribuerCartes() {
		// Cr�ation et m�lange des cartes
		ArrayList<Integer> cartesPioche = new ArrayList<>();
		for (int i = 1; i <= 104; i++) {
			cartesPioche.add(i);
		}
		Collections.shuffle(cartesPioche);

		// Distribue 10 cartes � chaque joueur
		for (Joueur joueur : joueurs) {
			for (int i = 0; i < 10; i++) {
				int carte = cartesPioche.remove(0); // Prend une carte de la pioche
				joueur.ajouterCarte(carte);
			}
		}
		cartesRestantes = 10;

		cartesSuites = new ArrayList[4];
		for (int i = 0; i < 4; i++) {
			int carte = cartesPioche.remove(0); // Prend une carte de la pioche
			cartesSuites[i] = new ArrayList<>(5);
			cartesSuites[i].add(carte);
		}
	}

	// Boucle qui permet de faire une manche compl�te
	private void boucleJeu() {
		while (cartesRestantes > 0) {
			tourJeu();
		}
		
		affichageScoreFinal();
	}

	// Un tour de jeu
	private void tourJeu() {

		// Liste des joueurs tri� par ordre croissant de la carte choisie par chacun
		ArrayList<Joueur> listeJoueurs = new ArrayList<>();
		for (int i = 0; i < nbJoueurs; i++) {
			Joueur joueur = joueurs[i];
			listeJoueurs.add(joueur);
		}

		// S�lection d'une carte pour chaque joueur
		for (Joueur joueur : joueurs) {
			selectionCarte(joueur);
		}

		// Tri des joueurs par valeur de la carte choisie croissante
		Collections.sort(listeJoueurs, Joueur.triCarteCroissante);

		// Pr�paration du message cartes � placer (envoy� uniquement si un joueur doit choisir une suite � r�cup�rer)
		String messageCartesJoueurs = "Les cartes ";
		for (int i = 0; i < nbJoueurs; i++) {
			Joueur joueur = listeJoueurs.get(i);
			int carte = joueur.getCarteChoisie();

			messageCartesJoueurs += carte + "(" + joueur.getNom() + ")";
			if (i < nbJoueurs - 2) {
				messageCartesJoueurs += ", ";
			} else if (i == nbJoueurs - 2) {
				messageCartesJoueurs += " et ";
			}
		}
		boolean messageCartesEnCoursEnvoye = false;

		// Placement des cartes
		for (Joueur joueur : listeJoueurs) {
			int carte = joueur.getCarteChoisie();

			// Trouve la suite sur laquelle la carte peut �tre pos�e (croissant) avec la diff�rence la plus faible
			int indexSuiteChoisie = -1;
			int carteSuiteActuelle = 0;
			for (int i = 0; i < 4; i++) {
				int carteSuite = cartesSuites[i].get(cartesSuites[i].size() - 1);

				if (carteSuite < carte && carteSuite > carteSuiteActuelle) {
					indexSuiteChoisie = i;
					carteSuiteActuelle = carteSuite;
				}
			}

			// Si il est possible de poser la carte dans une suite, on la pause
			if (indexSuiteChoisie >= 0) {
				if (cartesSuites[indexSuiteChoisie].size() < 5) {
					cartesSuites[indexSuiteChoisie].add(carte);
					joueur.retirerCarteChoisie();
				} else { // D�j� 5 cartes dans la suite
					// Compter les t�tes de boeufs et les ajouter au joueur
					int tetesRecuperees = 0;
					for (int carteSuite : cartesSuites[indexSuiteChoisie]) {
						tetesRecuperees += Jeu.nbTetesBoeufsCarte(carteSuite);
					}
					joueur.ajouterTetesRecuperees(tetesRecuperees);

					// Vider la suite et ajouter la carte du joueur
					cartesSuites[indexSuiteChoisie].clear();
					cartesSuites[indexSuiteChoisie].add(carte);
					joueur.retirerCarteChoisie();
				}
			} else { // Aucune suite ne peut recevoir la carte
				if (!messageCartesEnCoursEnvoye) {
					messageCartesEnCoursEnvoye = true;
					System.out.println(messageCartesJoueurs + " vont �tre pos�es.");
				}

				System.out.println("Pour poser la carte " + carte + ", " + joueur.getNom() + " doit choisir la s�rie qu'il va ramasser.");
				afficherSuites();

				// Choix d'une suite
				System.out.print("Saisissez votre choix : ");
				String choix = scanner.nextLine();
				try {
					indexSuiteChoisie = Integer.parseInt(choix);
				} catch (NumberFormatException e) {
				}

				// Continue de demmander de choisir une s�rie jusqu'� ce qu'une s�rie soit choisie
				while (indexSuiteChoisie <= 0 || indexSuiteChoisie > 4) {
					System.out.print("Choix invalide, saisissez votre choix : ");
					choix = scanner.nextLine();
					try {
						indexSuiteChoisie = Integer.parseInt(choix);
					} catch (NumberFormatException e) {
					}
				}

				indexSuiteChoisie--; // D�cr�mente de 1 car les suites vont de 1 � 4 pour le joueur

				// Compter les t�tes de boeufs et les ajouter au joueur
				int tetesRecuperees = 0;
				for (int carteSuite : cartesSuites[indexSuiteChoisie]) {
					tetesRecuperees += Jeu.nbTetesBoeufsCarte(carteSuite);
				}
				joueur.ajouterTetesRecuperees(tetesRecuperees);

				// Vider la suite et ajouter la carte du joueur
				cartesSuites[indexSuiteChoisie].clear();
				cartesSuites[indexSuiteChoisie].add(carte);
				joueur.retirerCarteChoisie();
			}
		}

		// Message cartes plac�es et affichage des nouvelles suites
		System.out.println(messageCartesJoueurs + " ont �t� pos�es.");
		afficherSuites();

		// Tri croissant des joueurs par nombre de t�tes de boeufs r�cup�res
		Collections.sort(listeJoueurs, Joueur.triTetesRecupereesCroissant);
		
		// Affichage des joueurs qui ont rammass� des t�tes de boeufs (par ordre croissant)
		for (Joueur joueur : listeJoueurs) {
			int tetesRecuperees = joueur.getTetesRecuperees();
			if (tetesRecuperees > 0) {
				String nom = joueur.getNom();
				System.out.println(nom + " a ramass� " + tetesRecuperees + " t�tes de boeufs");
			}
		}
		
		// Transfert les t�tes de boeufs r�cup�r�es par chaque joueurs dans son total de t�tes et remise � 0 des t�tes r�cup�r�es
		for (Joueur joueur : listeJoueurs) {
			joueur.transfertTetes();
		}
		
		// D�cr�mente de 1 le nombre de cartes restantes
		cartesRestantes--;
	}
	
	private void selectionCarte(Joueur joueur) {
		System.out.println("A " + joueur.getNom() + " de jouer.");

		// Pause
		Console.pause();

		// Affichage du jeu
		afficherSuites();
		joueur.afficherCartes();

		// Choix d'une carte
		int carteChoisie = -1;
//		Scanner scanner = new Scanner(System.in);
		System.out.print("Saisissez votre choix : ");
		String choix = scanner.nextLine(); // Entr�e du joueur
		try {
			carteChoisie = Integer.parseInt(choix);
		} catch (NumberFormatException e) {
		}

		// Continue de demmander de choisir une carte jusqu'� ce qu'une carte soit choisie
		while (!joueur.choisirCarte(carteChoisie)) {
			System.out.print("Choix invalide, saisissez votre choix : ");
			choix = scanner.nextLine(); // Entr�e du joueur
			try {
				carteChoisie = Integer.parseInt(choix);
			} catch (NumberFormatException e) {
			}
		}
		
		Console.clearScreen();
	}

	// Affiche la liste des cartes de chaque suite
	private void afficherSuites() {
		for (int i = 0; i < 4; i++) {
			System.out.print("- S�rie n� " + (i + 1) + " : ");
			for (int c = 0; c < cartesSuites[i].size(); c++) {
				int carte = cartesSuites[i].get(c);
				System.out.print(texteCarte(carte));
				if (c < cartesSuites[i].size() - 1) {
					System.out.print(", ");
				}
			}
			System.out.println(); // Retour � la ligne
		}
	}
	
	private void affichageScoreFinal() {
		// Cr�ation d'une liste qui contient tous les joueurs
		ArrayList<Joueur> listeJoueurs = new ArrayList<>();
		for (int i = 0; i < nbJoueurs; i++) {
			Joueur joueur = joueurs[i];
			listeJoueurs.add(joueur);
		}
		
		// Tri de la liste par nombre de t�tes croissant
		Collections.sort(listeJoueurs, Joueur.triTetesTotalesCroissant);
		
		// Affichage du score
		System.out.println("** Score final");
		for (Joueur joueur : listeJoueurs) {
			String nom = joueur.getNom();
			int tetesBoeufs = joueur.getTetesBoeufs();
			System.out.println(nom + " a ramass� " + tetesBoeufs + " t�tes de boeufs");
		}
	}

	// Donne un String qui permet de montrer la carte ex: 42, 15(5) ou 55(7)
	public static String texteCarte(int carte) {
		String texte = Integer.toString(carte);
		int tetesBoeufs = nbTetesBoeufsCarte(carte);

		if (tetesBoeufs > 1) {
			texte += "(" + tetesBoeufs + ")";
		}

		return texte;
	}

	// Donne le nombre de t�te de boeufs d'une carte
	public static int nbTetesBoeufsCarte(int carte) {
		int tetesBoeufs = 1;

		if (carte == 55) {// 55 vaut 7 t�tes de boeufs
			tetesBoeufs = 7;
		} else if (carte == 101 || carte % 100 / 10 == carte % 100 % 10) {// La carte a 2 nombres identiques (dizaines et unit�s) ou (la carte est 101)
			tetesBoeufs = 5;
		} else if (carte % 10 == 5) {// La carte finit par un 5
			tetesBoeufs = 2;
		} else if (carte % 10 == 0) {// La carte finit par un 0
			tetesBoeufs = 3;
		}

		return tetesBoeufs;
	}

	public static void main(String[] args) {
		new Jeu();
	}
}
