package gesresa;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Interface de réservation de places de spectacle.
 * <p>
 * Note : il s'agit d'une interface applicative, les dates sont donc au format
 * java.util.Date.
 * 
 * @author Busca
 * 
 */
public interface GestionReservation {

    /**
     * Liste les représentations d'un spectacle situées entre deux dates.
     * 
     * @param spectacle
     *            nom du spectacle cherché
     * @param de
     *            début de période cherchée (incluse)
     * @param a
     *            fin de période cherchée (incluse)
     * 
     * @return la liste des représentations correspondantes
     * @throws SQLException
     *             si une erreur survient lors de la manipulation des données
     */
    List<Representation> listerRepresentations(String spectacle, Date de, Date a) throws SQLException;

    /**
     * Liste les places d'une représentation. Deux stratégies sont proposées :
     * <ul>
     * <li>stable : l'application garantit que le statut libre/réservé des
     * places n'est pas modifié jusqu'à l'appel d'une des méthodes
     * <code>reserverPlaces</code> pour la même représentation,
     * <li>instable : le statut des places peut changer après l'appel de cette
     * méthode ; en particulier, une place initialement déclarée libre peut ne
     * plus l'être au moment d'appeler <code>reserverPlaces</code>
     * </ul>
     * 
     * @param representation
     *            représentation à considérer
     * @param stable
     *            vrai si le statut des places doit rester stable et faux sinon
     * 
     * @return la liste des places de la représentation
     * @throws SQLException
     *             si une erreur survient lors de la manipulation des données
     */
    List<Place> listerPlaces(Representation representation, boolean stable) throws SQLException;

    /**
     * Réserve une ou plusieurs places à un tarif donné pour une représentation.
     * S'il reste moins de places libres que demandé au tarif spécifié, aucune
     * place n'est réservée.
     * 
     * @param representation
     *            représentation à considérer
     * @param nombre
     *            nombre de place à réserver
     * @param tarif
     *            tarif des places à réserver, <code>null</code> si indifférent
     * @return la liste des places réservées, vide si toutes les places
     *         demandées n'ont pu être réservées
     * @throws SQLException
     *             si une erreur survient lors de la manipulation des données
     */
    List<Place> reserverPlaces(Representation representation, int nombre, Tarif tarif) throws SQLException;

    /**
     * Réserve une liste de places donnée. Si une des places demandées n'est pas
     * libre, aucune place n'est réservée.
     * 
     * @param representation
     *            représentation à considérer
     * @param places
     *            liste des places à réserver
     * @return la liste des places réservées, vide si toutes les places
     *         demandées n'ont pu être réservées
     * @throws SQLException
     *             si une erreur survient lors de la manipulation des données
     */
    List<Place> reserverPlaces(Representation representation, List<Place> places) throws SQLException;
}
