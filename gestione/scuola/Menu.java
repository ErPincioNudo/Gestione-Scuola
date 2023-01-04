/**
 * Classe Menu, fornisce un menu di base.
 * 
 * @version 1.3 (4-1-2023)
 * @author Adnaan Juma
 * @author Matteo Del Checcolo
 * @author Lorenzo Freccero
 */

package gestione.scuola;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
	/**
	 * Pulisce la console
	 */
	public static void cls()
	{
		System.out.print("\033[H\033[2J"); // Pulisce lo schermo
		System.out.flush();
	}
	
	/*
	 * Funzione Main
	 */
	public static void main(String[] args)
	{
		GestoreStudenti gestore = new GestoreStudenti();
		String pathSalvataggio = "lista.bin";
		
		boolean chiudiProgramma = false;
		
		try {
			gestore.caricaListaStudenti(pathSalvataggio);
		} catch (FileNotFoundException exception) {
			System.err.println("[AVVERTIMENTO]: Il file di salvataggio non e' presente e non verra' quindi caricata la lista.");
		} catch (IOException exception) {
			System.err.println("[ERRORE]: Errore occorso nell'apertura del file!");
		} catch (ClassNotFoundException exception) {
			System.err.println("[ERRORE]: File di salvataggio corrotto!");
		}

		/*
		 * 
		 */
		while (!chiudiProgramma) {

			Scanner sc = new Scanner(System.in);
			
			System.out.print("Scegli una delle seguenti opzioni:"
			+ "\n[1]: Salva studente"
			+ "\n[2]: Cerca studente"
			+ "\n[3]: Modifica informazioni studente"
			+ "\n[4]: Elimina studente"
			+ "\n[5]: Visualizza lista studenti"
			+ "\n[6]: Chiudi il programma\n\n");

			int scelta = -1;

			while (scelta < 1 || scelta > 6) {
				try {
					scelta = sc.nextInt();

					if (scelta < 1 || scelta > 6) {
						throw new InputMismatchException();
					}

					sc.nextLine();

				} catch (InputMismatchException exception) {
					System.out.println("Input inserito invalido, si prega di reinserirlo");
					sc.nextLine();
				}
			}
			
			cls();
			

			switch(scelta) {
				case 1: { // Salva studente
					Studente studente = new Studente();
					
					System.out.print(">Salva Studente\n\n");

					System.out.print("\tInserisci il Nome: ");
					studente.setNome(sc.nextLine());
					System.out.print("\tInserisci il Cognome: ");
					studente.setCognome(sc.nextLine());
					System.out.print("\tInserisci la Data di Nascita (aaaa-mm-gg): ");
					studente.setDataDiNascita(LocalDate.parse(sc.nextLine()));
					System.out.print("\tInserisci il Luogo di Nascita: ");
					studente.setLuogoDiNascita(sc.nextLine());
					System.out.print("\tInserisci la Classe Frequentata (max 127): ");
					studente.setClasseFrequentata(sc.nextByte());
					sc.nextLine();
					System.out.print("\tInserisci la Sezione Frequentata (max 127): ");
					studente.setSezioneFrequentata(sc.nextLine());
					sc.nextLine();
					System.out.print("\tLo Studente e' Ripetente? (si/no): ");

					switch (sc.nextLine()) {
						case "si":
							System.out.print("\tQuanti Anni ha Dovuto Ripetere? (max 127): ");
							studente.setAnniDiRipetizione(sc.nextByte());
							sc.nextLine();
							break;
						default:
					}

					try {
						gestore.salvaStudente(studente);
						System.out.print("\nStudente inserito nella lista con successo!");
						
						gestore.salvaListaStudenti(pathSalvataggio);
						System.out.print("\n[AVVERTIMENTO]: La lista e' stata serializzata sul file. ");
						sc.nextLine();
						cls();
					} catch (StudenteGiaEsistenteException exception) {
						System.err.print("\n[ERRORE]: Lo studente e' gia' stato inserito nella lista precedentemente, e non verra inserito! ");
						sc.nextLine();
						cls();
					} catch (Exception exception) { // TODO: Espandere il catch con le eccezioni specifiche
						sc.nextLine();
						cls();
					}

					break;
				}
				case 2: { // Cerca studente
					System.out.print(">Cerca studente\n\n");

					System.out.print("\tInserire il Nome e il Cognome dello/degli studente/studenti che desidera cercare (ex. \"Marco Polo\"): ");
					String[] input = sc.nextLine().split(" ");

					try {
						for (Studente st : gestore.cercaStudente(input[0], input[1])) {
							System.out.print("\n" + st.toString() + "\n");
						}

						sc.nextLine();
						cls();
					} catch (StudenteNonTrovatoException exception) {
						System.out.print("\nNessuno Studente con tali Credenziali Trovato nella Lista. ");
						sc.nextLine();
						cls();
					}

					break;
				}
				case 3: { // Modifica informazioni studente
					System.out.print(">Modifica informazioni studente\n\n");

					System.out.print("\tInserire il nome e il cognome dello studente di cui vuoi modificare i dati (ex. \"Richard Wattson\"): ");
					String[] credenziali = sc.nextLine().split(" ");

					try {
						Studente[] risultato = gestore.cercaStudente(credenziali[0], credenziali[1]);
						
						if (risultato.length == 1) {
							String input = "";
							Studente nuovoStudente = new Studente(risultato[0]);

							System.out.print("\n\tStudente trovato! Se non si desidera cambiare un certo dato, lasicarlo vuoto.\n\n");
							
							System.out.print("\tInserisci il Nuovo Nome (Vecchio Nome: \"" + risultato[0].getNome() + "\"): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setNome(input);
							}

							System.out.print("\tInserisci il Nuovo Cognome (Vecchio Cognome: \"" + risultato[0].getCognome() + "\"): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setCognome(input);
							}

							System.out.print("\tInserisci la Nuova Data di Nascita (Vecchia Data di Nascita: \"" + risultato[0].getDataDiNascita().toString() + "\") (aaaa-mm-gg): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setDataDiNascita(LocalDate.parse(input));
							}
							
							System.out.print("\tInserisci il Nuovo Luogo di Nascita (Vecchio Luogo di Nascita: \"" + risultato[0].getLuogoDiNascita() + "\"): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setLuogoDiNascita(input);
							}

							System.out.print("\tInserisci la Nuova Classe Frequenta (Vecchia Classe Frequentata: \"" + risultato[0].getClasseFrequentata() + "\") (max 127): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setClasseFrequentata(Byte.parseByte(input));
							}
							
							System.out.print("\tInserisci la Nuova Sezione Frequentata (Vecchia Sezione Frequentata: \"" + risultato[0].getSezioneFrequentata() + "\"): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setSezioneFrequentata(input);
							}
							
							System.out.print("\tInserisci i Nuovi Anni di Ripetizione (Vecchi Anni di Ripetizione: \"" + risultato[0].getAnniDiRipetizione() + "\") (max 127) (se lo studente non e' mai stato bocciato inserire 0): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setAnniDiRipetizione(Byte.parseByte(input));
							}

							gestore.modificaStudente(risultato[0], nuovoStudente); // Puo tirare StudenteNonTrovatoException ma non accradra' mai in questo punto del codice
							System.out.print("\nInformazioni studente modificate con successo!");
						
							gestore.salvaListaStudenti(pathSalvataggio);
							System.out.print("\n[AVVERTIMENTO]: La lista e' stata serializzata sul file. ");
							sc.nextLine();
							cls();

						} else {
							int scelta2 = -1;

							System.out.print("\n\tPiu' di uno studente trovato!\n");

							for (int i = 0; i < risultato.length; i++) {
								System.out.print("\n\t[" + i + "] " + risultato[i].toString() + "\n");
							}

							System.out.print("\nQuale dei " + risultato.length + " Studenti vuole Modificare? ");
							
							while (scelta2 < 0 || scelta2 > risultato.length - 1) {
								try {
									scelta2 = sc.nextInt();
				
									if (scelta2 < 0 || scelta2 > risultato.length - 1) {
										throw new InputMismatchException();
									}
				
									sc.nextLine();
				
								} catch (InputMismatchException exception) {
									System.out.println("Input inserito invalido, si prega di reinserirlo");
									sc.nextLine();
								}
							}

							String input = "";
							Studente nuovoStudente = new Studente(risultato[scelta2]);

							System.out.print("\n\tSe non si desidera cambiare un certo dato, lasicarlo vuoto.\n\n");
							
							System.out.print("\tInserisci il Nuovo Nome (Vecchio Nome: \"" + risultato[0].getNome() + "\"): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setNome(input);
							}

							System.out.print("\tInserisci il Nuovo Cognome (Vecchio Cognome: \"" + risultato[0].getCognome() + "\"): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setCognome(input);
							}

							System.out.print("\tInserisci la Nuova Data di Nascita (Vecchia Data di Nascita: \"" + risultato[0].getDataDiNascita().toString() + "\") (aaaa-mm-gg): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setDataDiNascita(LocalDate.parse(input));
							}
							
							System.out.print("\tInserisci il Nuovo Luogo di Nascita (Vecchio Luogo di Nascita: \"" + risultato[0].getLuogoDiNascita() + "\"): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setLuogoDiNascita(input);
							}

							System.out.print("\tInserisci la Nuova Classe Frequenta (Vecchia Classe Frequentata: \"" + risultato[0].getClasseFrequentata() + "\") (max 127): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setClasseFrequentata(Byte.parseByte(input));
							}
							
							System.out.print("\tInserisci la Nuova Sezione Frequentata (Vecchia Sezione Frequentata: \"" + risultato[0].getSezioneFrequentata() + "\"): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setSezioneFrequentata(input);
							}
							
							System.out.print("\tInserisci i Nuovi Anni di Ripetizione (Vecchi Anni di Ripetizione: \"" + risultato[0].getAnniDiRipetizione() + "\") (max 127) (se lo studente non e' mai stato bocciato inserire 0): ");
							input = sc.nextLine();
							if (!input.isEmpty()) {
								nuovoStudente.setAnniDiRipetizione(Byte.parseByte(input));
							}

							gestore.modificaStudente(risultato[0], nuovoStudente); // Puo tirare StudenteNonTrovatoException ma non accradra' mai in questo punto del codice
							System.out.print("\nInformazioni studente modificate con successo!");
						
							gestore.salvaListaStudenti(pathSalvataggio);
							System.out.print("\n[AVVERTIMENTO]: La lista e' stata serializzata sul file. ");
							sc.nextLine();
							cls();
						}
						
					} catch (StudenteNonTrovatoException exception) {
						System.out.print("\nNessuno studente con tali credenziali trovato nella lista. ");
						sc.nextLine();
						cls();
					} catch (Exception exception){
						sc.nextLine();
						cls();
					}
					
					break;
				}
				case 4: { // Elimina studente
					System.out.print(">Elimina studente\n\n");

					System.out.print("Inserisci il nome e il cognome dello studente che vuoi eliminare dalla lista (ex. \"Richard Wattson\"): ");
					String[] credenziali = sc.nextLine().split(" ");

					try {
						Studente[] risultato = gestore.cercaStudente(credenziali[0], credenziali[1]);
						
						if (risultato.length == 1) {
							System.out.print("\n\tStudente trovato: \n\t" + risultato[0].toString() + "\n");

							System.out.print("\n\tVuole davvero eliminarlo dalla lista? (si/no): ");

							switch (sc.nextLine()) {
								case "si":
									gestore.eliminaStudente(risultato[0]);
									System.out.print("\nLo studente e' stato rimosso dalla lista con successo. ");
									gestore.salvaListaStudenti(pathSalvataggio);
									System.out.print("\n[AVVERTIMENTO]: La lista e' stata serializzata sul file. ");
									sc.nextLine();
									cls();
									break;
								default:
									System.out.print("\nOperazione annullata. Ritornerai al menu principale. ");
									sc.nextLine();
									cls();
							}
						} else {
							int scelta2 = -1;

							System.out.print("\n\tPiu' di uno studente trovato!\n");

							for (int i = 0; i < risultato.length; i++) {
								System.out.print("\n\t[" + i + "] " + risultato[i].toString() + "\n");
							}

							System.out.print("\nQuale dei " + risultato.length + " vuoi eliminare? ");
							
							while (scelta2 < 0 || scelta2 > risultato.length - 1) {
								try {
									scelta2 = sc.nextInt();
				
									if (scelta2 < 0 || scelta2 > risultato.length - 1) {
										throw new InputMismatchException();
									}
				
									sc.nextLine();
				
								} catch (InputMismatchException exception) {
									System.out.println("Input inserito invalido, si prega di reinserirlo");
									sc.nextLine();
								}
							}

							System.out.print("\n\tVuole davvero eliminare lo studente dalla lista? (si/no): ");

							switch (sc.nextLine()) {
								case "si":
									gestore.eliminaStudente(risultato[scelta2]);
									System.out.print("\nLo studente e' stato rimosso dalla lista con successo. ");
									gestore.salvaListaStudenti(pathSalvataggio);
									System.out.print("\n[AVVERTIMENTO]: La lista e' stata serializzata sul file. ");
									sc.nextLine();
									cls();
									break;
								default:
									System.out.print("\nOperazione annullata. Ritornerai al menu principale. ");
									sc.nextLine();
									cls();
							}
						}
						
					} catch (StudenteNonTrovatoException exception) {
						System.out.print("\nNessuno studente con tali credenziali trovato nella lista. ");
						sc.nextLine();
						cls();
					} catch (Exception exception) { // TODO: Espandere il catch con le eccezioni specifiche
						sc.nextLine();
						cls();
					}
					
					break;

				}
				case 5: { // Visualizza lista studenti
					System.out.print(">Visualizza lista studenti\n\n");

					for (Studente studente : gestore.getListaStudenti()) {
						System.out.print("\n" + studente.toString() + "\n");
					}

					sc.nextLine();
					cls();

					break;
				}
				case 6:
					chiudiProgramma = true;
					sc.close();
			}
		}
	}
}