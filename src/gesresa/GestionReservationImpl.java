package gesresa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Implémentation simple de la gestion de réservation de places de spectacle.
 *
 * @author Busca, Aurel
 *
 */
public class GestionReservationImpl implements GestionReservation {

    private String client;
    private Connection connection;
    private PreparedStatement getRepresentation;
    private PreparedStatement getPlace;
    private PreparedStatement getAvailablePlace;
    private PreparedStatement getAvailablePlaceByTarif;
    private PreparedStatement addBooking;
    private PreparedStatement delBooking;
    private PreparedStatement getRID;
    List<Place> tmp;

    public static void init() {
        System.setProperty("jdbc.drivers", "oracle.jdbc.driver.OracleDriver");
    }

    /**
     * Construit un nouvel objet permettant la réservation de places. Cet objet
     * établit une connexion à la base de données sous le nom d'utilisateur SQL
     * spécifié. Il agit pour le compte du client spécifié, à qui seront
     * attribuées les réservations faites via cet objet.
     *
     * @param url
     *            url de la base de données à laquelle se connecter
     * @param user
     *            nom d'utilisateur SQL utilisé pour la connection
     * @param password
     *            mot de passe correspondant
     * @param client
     *            nom du client effectuant les réservations.
     * @throws SQLException
     *             si une erreur survient lors de la manipulation des données
     */
    public GestionReservationImpl(String url, String user, String password, String client) throws SQLException {
        System.out.println("connexion de: " + client);



        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        this.client = client;
        tmp = new LinkedList<Place>();

    }

    @Override
    public List<Representation> listerRepresentations(String spectacle, Date de, Date a) throws SQLException {

        if (getRepresentation == null) {
            getRepresentation = connection
                    .prepareStatement("select * from REPRESENTATIONS natural join "
                            + "BOOKINGCLASSES natural join COST "
                            + "where SNAME = ? and STARTDATE >= ? and STARTDATE <= ? order by RID");
        }
        getRepresentation.setString(1, spectacle);
        getRepresentation.setDate(2, new java.sql.Date(de.getTime()));
        getRepresentation.setDate(3, new java.sql.Date(a.getTime()));

        ResultSet rs = getRepresentation.executeQuery();
        List<Representation> l = new LinkedList<Representation>();
        Representation tmp = null;
        int rid = -1;

        while (rs.next()) {
            if (rid == -1) { // première itération uniquement
                List<Tarif> tarif = new LinkedList<Tarif>();
                tarif.add(new Tarif(rs.getString("BNAME"), rs.getFloat("PRICE")));
                tmp = new Representation(rs.getString("SNAME"), rs.getString("RNAME"), rs.getDate("STARTDATE"), tarif);
                rid = rs.getInt("RID");
            } else {
                if (rs.getInt("RID") == rid) {// si rid actuel égal au
                                              // précédent j'ajoute le
                                              // nouveau tarif à la
                                              // représentation
                    List<Tarif> tarif = tmp.getTarifs();
                    tarif.add(new Tarif(rs.getString("BNAME"), rs.getFloat("PRICE")));
                    tmp = new Representation(tmp.getSpectacle(), tmp.getSalle(), tmp.getDate(), tarif);
                } else { // sinon on a une nouvelle représentation

                    if (tmp != null) {
                        l.add(tmp);
                        tmp = null;
                    }

                    List<Tarif> tarif = new LinkedList<Tarif>();
                    tarif.add(new Tarif(rs.getString("BNAME"), rs.getFloat("PRICE")));
                    tmp = new Representation(rs.getString("SNAME"), rs.getString("RNAME"), rs.getDate("STARTDATE"),
                            tarif);
                    rid = rs.getInt("RID");
                }
            }
        }
        if (tmp != null) l.add(tmp);
        return l;
    }

    @Override
    public List<Place> listerPlaces(Representation representation, boolean stable) throws SQLException {

        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);


        if (getPlace == null) {
            getPlace = connection
                    .prepareStatement("select distinct SEATS.SID, BNAME, PRICE, CNAME from REPRESENTATIONS "
                            + "natural join BOOKINGCLASSES natural join COST natural join SEATS "
                            + "left outer join RESERVATIONS "
                            + "T on (SEATS.SID = T.SID "
                            + "and REPRESENTATIONS.RID = T.RID and T.RNAME = RNAME) "
                            + "where SNAME = ? and "
                            + "REPRESENTATIONS.STARTDATE = ? and RNAME = ? order by SEATS.SID");
        }
        getPlace.setString(1, representation.getSpectacle());
        getPlace.setDate(2, new java.sql.Date(representation.getDate().getTime()));
        getPlace.setString(3, representation.getSalle());

        ResultSet rs = getPlace.executeQuery();

        List<Place> l = new LinkedList<Place>();

        while (rs.next()) {
            rs.getString("CNAME"); // jointure externe avec réservation
                                   // si le client existe : déjà réservé
                                   // si client = NULL : place libre
            if (rs.wasNull())
                l.add(new Place(representation, rs.getInt("SID"), new Tarif(rs.getString("BNAME"), rs.getFloat("PRICE")),
                        true));
            else
                l.add(new Place(representation, rs.getInt("SID"), new Tarif(rs.getString("BNAME"), rs.getFloat("PRICE")),
                        false));
        }
        if(stable) return listerPlacesStable(representation, l);
        return l;
    }

    public List<Place> listerPlacesStable(Representation representation, List<Place> l) throws SQLException {

        LinkedList<Place> available = new LinkedList<Place>();
        for (Place p : l) {
            if (p.isEstLibre()) available.add(p); // élimine les places non libres
        }
        tmp = new LinkedList<Place>(); // reset de tmp
        tmp = reserverPlaces(representation, available); // réserve temporairement les places libres
        if (tmp == null || tmp.isEmpty())  tmp = new LinkedList<Place>();
        return l;
    }

    public List<Place> cancelReservationStableExcept(List<Place> reservation) throws SQLException {
            if (delBooking == null) {
                delBooking = connection.prepareStatement("delete from RESERVATIONS where SID = ? and RNAME = ? and RID = ? and CNAME =?");
            }
            HashSet<Integer> numeros = new HashSet<Integer>();
            for (Place r : reservation) {
                numeros.add(r.getNumero());
            }
            for (Place p : tmp) {
                    if(!numeros.contains(p.getNumero())) { // on supprime la réservation temporaire si elle n'est pas officialisée
                        delBooking.setInt(1, p.getNumero());
                        delBooking.setString(2, p.getRepresentation().getSalle());

                        /**
                         * la requête suivante est liée au fait que nous utilisons un RID
                         * pour représenter l'identifiant d'une preprésentation. Celui-ci
                         * n'est pas présent dans le modèle proposé (la classe
                         * Representation). Cependant nous considèrons qu'un spectacle peut
                         * avoir plusieurs représentations à la même date. En changeant le
                         * modèle java proposé on éliminerait facilement cette requête peu
                         * intéressante. Améliorant ainsi le performances.
                         */
                         if (getRID == null) {
                             getRID = connection.prepareStatement("select RID from REPRESENTATIONS where "
                                     +"SNAME = ? and STARTDATE = ? and RNAME = ?");
                         }

                         getRID.setString(1, p.getRepresentation().getSpectacle());
                         getRID.setDate(2, new java.sql.Date(p.getRepresentation().getDate().getTime()));
                         getRID.setString(3, p.getRepresentation().getSalle());

                        ResultSet rs = getRID.executeQuery();
                        rs.next();
                        delBooking.setInt(3, rs.getInt("RID"));

                        /** fin de l'utilisation de la requête **/

                        delBooking.setString(4, client);
                        delBooking.executeQuery();
                    }
            }
            connection.commit();
            List<Place> l = new LinkedList<Place>();
            for (Place p : reservation) {
               l.add(new Place(p.getRepresentation(),p.getNumero(),p.getTarif(),false));
            }
            return l;
    }


    @Override
    public List<Place> reserverPlaces(Representation representation, int nombre, Tarif tarif) throws SQLException {

        if (representation == null || nombre <= 0) return null;

        try {
            ResultSet rs;
            if (tarif == null) // indiférent
            {
                if (getAvailablePlace == null) {
                    getAvailablePlace = connection.prepareStatement("select SID, RID, RNAME "
                            + "from REPRESENTATIONS "
                            + "natural join BOOKINGCLASSES natural join COST natural join SEATS "
                            + "where SNAME = ? and STARTDATE = ? and RNAME =  ? "
                            + "and SID not in ( select SID from RESERVATIONS "
                            + "where RESERVATIONS.RNAME = RNAME and RESERVATIONS.RID = RID) order by SID");
                }
                getAvailablePlace.setString(1, representation.getSpectacle());
                getAvailablePlace.setDate(2, new java.sql.Date(representation.getDate().getTime()));
                getAvailablePlace.setString(3, representation.getSalle());
                rs = getAvailablePlace.executeQuery();

            } else { // tarif précisé

                if (getAvailablePlaceByTarif == null) {
                    getAvailablePlaceByTarif = connection.prepareStatement("select SID, RID, RNAME "
                            + "from REPRESENTATIONS "
                            + "natural join BOOKINGCLASSES natural join COST natural join SEATS "
                            + "where SNAME = ? and STARTDATE = ? and RNAME =  ? "
                            + "and BNAME = ? and PRICE = ? and SID not in ( select SID "
                            + "from RESERVATIONS where RESERVATIONS.RNAME = RNAME "
                            + "and RESERVATIONS.RID = RID) order by SID");
                }
                getAvailablePlaceByTarif.setString(1, representation.getSpectacle());
                getAvailablePlaceByTarif.setDate(2, new java.sql.Date(representation.getDate().getTime()));
                getAvailablePlaceByTarif.setString(3, representation.getSalle());
                getAvailablePlaceByTarif.setString(4, tarif.getLibelle());
                getAvailablePlaceByTarif.setFloat(5, tarif.getPrix());
                rs = getAvailablePlaceByTarif.executeQuery();
            }

            if (addBooking == null) {
                addBooking = connection.prepareStatement("insert into RESERVATIONS values(?,?,?,?)");
            }

            List<Place> l = new LinkedList<Place>();

            while (nombre > 0 && rs.next()) {
                addBooking.setInt(1, rs.getInt("SID"));
                addBooking.setString(2, rs.getString("RNAME"));
                addBooking.setInt(3, rs.getInt("RID"));
                addBooking.setString(4, client);
                addBooking.executeQuery();
                l.add(new Place(representation, rs.getInt("SID"), tarif, false));
                nombre--;
            }
            if (nombre > 0) { // pas assez de places disponibles pour la demande
                connection.rollback(); // on annule les réservations
                return null;
            }
            // tout s'est bien passé : on valide la transaction
            connection.commit();
            return l;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    @Override
    public List<Place> reserverPlaces(Representation representation, List<Place> places) throws SQLException {
        if(!tmp.isEmpty()) return cancelReservationStableExcept(places);
        if (addBooking == null) {
            addBooking = connection.prepareStatement("insert into RESERVATIONS values(?,?,?,?)");
        }
        List<Place> l = new LinkedList<Place>();
        for (Place p : places) {
            addBooking.setInt(1, p.getNumero());
            addBooking.setString(2, representation.getSalle());

            /**
             * la requête suivante est liée au fait que nous utilisons un RID
             * pour représenter l'identifiant d'une preprésentation. Celui-ci
             * n'est pas présent dans le modèle proposé (la classe
             * Representation). Cependant nous considèrons qu'un spectacle peut
             * avoir plusieurs représentations à la même date. En changeant le
             * modèle java proposé on éliminerait facilement cette requête peu
             * intéressante. Améliorant ainsi le performances.
             */
             if (getRID == null) {
                 getRID = connection.prepareStatement("select RID from REPRESENTATIONS where "
                         +"SNAME = ? and STARTDATE = ? and RNAME = ?");
             }

             getRID.setString(1, representation.getSpectacle());
             getRID.setDate(2, new java.sql.Date(representation.getDate().getTime()));
             getRID.setString(3, representation.getSalle());

            ResultSet rs = getRID.executeQuery();
            rs.next();
            addBooking.setInt(3, rs.getInt("RID"));

            /** fin de l'utilisation de la requête **/

            addBooking.setString(4, client);

            try {
                addBooking.executeQuery();
            } catch (SQLException e) { // une place n'a pas pu etre réservée
                System.out.println("reservation de " + client + " n a pas pu etre reserve.");
                connection.rollback();
                return null;
            }
            l.add(new Place(representation, p.getNumero(), p.getTarif(), false));
        }
        // tout s'est bien passé : on valide la transaction
        connection.commit();
        return l; /* les places ne sont plus libres */
    }

}
