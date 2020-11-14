
package connexion;

/**
 *
 * @author Black Bou
 */
public class Parametre {
       
        public static String IPHOST="127.0.0.1"; // POUR LES TESTE SUR ORDI 
        // public static String IPHOST="192.168.1.18";  //POUR INSTALLATION REEL
        public static String nom_bdd="gestion_complete";
        public static String HOST_DB="jdbc:mysql://" + IPHOST + ":3306/Couvoir_Amar?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"  ;
      //public static String HOST_DB="jdbc:mysql://" + IPHOST + ":3306/"+nom_bdd;  //POUR RELL INSTALLATION
        public static String USERNAME_DB="root";
        public static String PASS_DB=""; //POUR LES TESTE
        //public static String PASS_DB="pass"; //POUR RELL INSTALLATION
        
        public static int PORT= 11111;
        public static String USER ;
        
}
