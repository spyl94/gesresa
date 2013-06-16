package gesresa;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Main {
    

    // available clients
    public static String[] CLIENTS = { "Lise", "Dupont", "Luc", "Pierre", "Aurel", "Adrien", "Hugo", "Yann" };

    // available shows
    public static String[] SHOWS = { "Concert de Metal bien lourd", "Concert des RHCP", "Concert Rasta",
            "Concert de Funk", "Concert de Pop" };

    public static String URL = "jdbc:oracle:thin:@oracle.efrei.fr:1521:sgbd";
    public static String USER = "davida";
    public static String PASS = "davida";
    
    public static List<Integer> duree = Collections.synchronizedList(new ArrayList<Integer>());
    public static List<Integer> fail = Collections.synchronizedList(new ArrayList<Integer>());
    public static List<Integer> noplaces = Collections.synchronizedList(new ArrayList<Integer>());

    /**
     * @param args
     */
    public static void main(String[] args) {

        GestionReservationImpl.init();
        
       
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < CLIENTS.length; i++) {
            Thread t = new Thread(CLIENTS[i]) {
                public void run() {
                    try {
                        long start= System.currentTimeMillis(); 
                        int failCount = -1;
                        int noplace = 0;
                        // choix aléatoire d'un spectacle existant
                        String spectacle = SHOWS[(int) (Math.random() * (SHOWS.length))];

                        GestionReservation g = new GestionReservationImpl(URL, USER, PASS, Thread.currentThread()
                                .getName()); // nom du thread = nom du client

                        Representation r;
                        List<Place> choices = new LinkedList<Place>();

                        // consultation des représentations de ce spectacle
                        do {
                            failCount++;
                            int libre = 0;
                            
                            List<Place> places;
                            do {
                                List<Representation> representations = g.listerRepresentations(spectacle,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("31/02/2013"), new SimpleDateFormat(
                                                "dd/MM/yyyy").parse("31/02/2014"));
    
                                // réflexion pendant un délai aléatoire de 10 à 15
                                // secondes, et choix d’une représentation
                                Thread.sleep((int) (Math.random() * (5000)) + 10000); // +10000
                                r = representations.get((int) (Math.random() * (representations.size())));
                                System.out.println(Thread.currentThread().getName() + " choisi " + r);
    
                                // consultation des places de cette représentation
                                places = g.listerPlaces(r, false);
                                for (Place p : places) {
                                    if (p.isEstLibre()) libre++;
                                }
                                System.out.println(Thread.currentThread().getName() + libre + " places libres !");
                                if(libre <= 1) {
                                    System.out.println(Thread.currentThread().getName() + " pas assez de place !");
                                    noplace++;
                                    if (noplace >= 5) {
                                        System.out.println(Thread.currentThread().getName() + " abandonne !");
                                        return;
                                    }
                                }
                            } while(libre <= 1);
                            
                            // réflexion pendant un délai aléatoire de 20 à 30
                            // secondes, et choix aléatoire de deux places parmi
                            // celles données comme libres
                            Thread.sleep((int) (Math.random() * (10000)) + 20000); // +20000
                            Place p1 = null;
                            Place p2 = null;
                            do {
                                p1 = places.get((int) (Math.random() * (places.size())));
                                Thread.sleep(1);
                                p2 = places.get((int) (Math.random() * (places.size())));
                            } while (p1.getNumero() == p2.getNumero() || !p1.isEstLibre() || !p2.isEstLibre());
                            
                            System.out.println(Thread.currentThread().getName() + " choisi " + p1);
                            System.out.println(Thread.currentThread().getName() + " choisi " + p2);
                            
                            choices = new LinkedList<Place>();
                            choices.add(p1);
                            choices.add(p2);

                            // réservation de ces deux places en cas d’échec,
                            // reprise à l’étape 2
                        } while (g.reserverPlaces(r, choices) == null);
                        System.out.println("Réservation de " + Thread.currentThread().getName() + " bien effectuée !");
                        long time = System.currentTimeMillis(); 
                        long dure = time - start;
                        System.out.println("Durée : " + dure + " Nombre echec: " + failCount);
                        duree.add((int)dure);
                        fail.add((int)failCount);
                        noplaces.add(noplace);
                        
                          
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            threads.add(t);
            t.start();
        }
        
        // attend la fin de tous les Threads
        boolean done = false;
        do
        {
            done = true;
            for(Thread t : threads) {
                if (t.isAlive()) done = false;
            }
        } while(done==false);
            
        
        // calcul des moyennes
        double avg = 0;
        for (Integer i : duree) {
            avg += i;
        }
        avg /= duree.size();
        System.out.println("Moyenne de la durée : " + avg);
        avg = 0;
        for (Integer i : fail) {
            avg += i;
        }
        avg /= duree.size();
        System.out.println("Moyenne du nombre d'echecs : " + avg);
        avg = 0;
        for (Integer i : noplaces) {
            avg += i;
        }
        avg /= noplaces.size();
        System.out.println("Moyenne du nombre de places insuficantes : " + avg);
        
        
    }
}
