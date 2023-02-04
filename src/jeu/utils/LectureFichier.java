package jeu.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class LectureFichier {
	public static ArrayList<String> lireFichier(String nomFichier) {
		ArrayList<String> lignes = new ArrayList<>();

		try {
			// Ouverture du fichier
			File fichier = new File(nomFichier);
			Scanner scanner = new Scanner(fichier);
			
			// Lecture des lignes
			while (scanner.hasNextLine()) {
				String ligne = scanner.nextLine();
				lignes.add(ligne);
			}
			
			// Fermeture du scanner
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Le fichier des noms de joueurs n'a pas pu �tre trouv�");
			e.printStackTrace();
		}
		
		return lignes;
	}
}
