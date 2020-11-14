/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connexion;


import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;


/**
 *
 * @author Black Bou
 */
public class BDD {
    
    //declaarations
    Connection connection ;
    Statement statement;
    String SQL;
    
    String url;
    
    String username;
    String pass;
    Socket client;
    
    int Port;
    String Host;
  
    
    
    public BDD(String url,String username,String pass,String Host,int Port)
    {
           this.url=url;
           this.username=username;
           this.pass=pass;
           this.Host=Host;
           this.Port=Port;   
    }
    
   //la fonction pour se connecter aux base de données !! 
     public Connection connexionDatabase()
     {
          try
          {
              Class.forName("com.mysql.cj.jdbc.Driver");
            //  Class.forName("com.mysql.jdbc.Driver");
              connection =DriverManager.getConnection(url, username, pass);
              
          }catch (Exception e)
                 { 
                     System.err.println(e.getMessage()); 
                     JOptionPane.showMessageDialog(null,e);
                 } //e.getMessage() affiche ou se trouve notre probleme
          return connection ;
     }
     
   //mettre fin a la connection  ...fermer la base de donné koi :!:
     public Connection closeconnexion()
     {
         try
         {
             connection.close();
             
         }catch (Exception e) {System.err.println(e.getMessage());}
         return connection ;
     } 
     
     
   // pour exception de requette 
     public ResultSet executionQuery(String sql)
     {
         connexionDatabase();
         ResultSet resultset =null ;
         try 
         {
             statement = connection.createStatement();
             resultset= statement.executeQuery(sql);
             
         }catch (SQLException ex) {System.err.println(ex);}
         return resultset ;
     }
    
   //pour executer des requette update :!:
     public String executionUpdate(String sql)
     {
         connexionDatabase();
         String result="";
         try
         {
             statement = connection.createStatement();
             statement.executeUpdate(sql);
             result=sql;
             
             
         }catch(SQLException ex)
         {
             result=ex.toString();
         }
         return result ;
     }
     
   
   

//Stock
     public ResultSet querySelect_Invetaire(String[] nomColonne , String nomTable)
     {
          connexionDatabase();
          int i;
          SQL="SELECT ";
          for(i=0;i<= nomColonne.length-1;i++)
          {
              SQL += nomColonne[i];
              if(i<nomColonne.length-1)
              {
                  SQL+= ",";
              }
          }
         SQL+=" FROM " + nomTable;
         SQL+=" WHERE Stock_dispo > 0";
         return this.executionQuery(SQL);
         
     }
     
      public String queryUpdateStock(String code ,String nom,String prix,String Stock)
       {
        connexionDatabase();
          SQL="UPDATE stock SET Stock_dispo='"+Stock+"',prix_unitaire='"+prix+"'  WHERE Code='"+code+"' AND libelle='"+nom+"'";
          return this.executionUpdate(SQL);
       }
      
      
      
      
     
      
      //GESTION DE LA DECREMENTATION DU STOCK APRES ENCAISSEMENT
        
      public void queryUpdateStockEnc(JTable Table) throws SQLException
      {
         for(int i=0;i<Table.getRowCount(); i++)
         {
            String ID= String.valueOf(Table.getValueAt(i, 1));
            String qtt= String.valueOf(Table.getValueAt(i, 3));
            System.out.println("ID = "+ID+" QTT= "+qtt+" Colum ="+i );
            int StockDisp = this.StockAct(Integer.parseInt(ID));
            System.out.print(this.UpdateStokModif(Integer.parseInt(ID), qtt, StockDisp));
            this.closeconnexion();
         }
      }
      
      public String UpdateStokModif(int id ,String qtt, int qttAct)
      {
          connexionDatabase();
          int stock = qttAct-Integer.parseInt(qtt);
          String SQL="UPDATE stock SET Stock_dispo ='"+stock+"' WHERE id ="+id ;
          return this.executionUpdate(SQL);
      }
      public int StockAct(int id) throws SQLException
      {
           connexionDatabase();
           SQL="SELECT * FROM stock WHERE id="+id;
           ResultSet rs =this.executionQuery(SQL);
           String solde="0";
          while(rs.next()){
              solde = rs.getString("Stock_dispo");
              
          }
           return Integer.parseInt(solde);
      }
      
   // Fin de GESTION DE LA DECREMENTATION DU STOCK APRES ENCAISSEMENT   
      
   //  ########################## S P I N E R########################"" 
      public int Nbr_Element_Table(String table) throws SQLException 
      {
           connexionDatabase();
          SQL= "SELECT COUNT(*) AS total FROM "+table ;
          ResultSet rs;
          rs = this.executionQuery(SQL);
          String solde="0";
          while(rs.next()){
              solde = rs.getString("total");
          }
          
          
          return Integer.parseInt(solde);       
      }
     public String[] Produit(String Table) throws SQLException
      {
           connexionDatabase();
          String Produit[] = new String[Nbr_Element_Table(Table)];
          SQL= "SELECT * FROM "+Table;
          ResultSet rs =this.executionQuery(SQL);
          int i=0;
          while(rs.next())
          {
              if(i<10){
                  Produit[i]="0"+rs.getString("id")+" "+rs.getString("libelle");  
              }else Produit[i]=rs.getString("id")+" "+rs.getString("libelle");  
              
              i++;
          }
          return Produit;
      }
      
      
    //  ########################## S P I N E R########################""    
      
      
      
      
      
     
    public String queryInsertStock(String code, String nom,String prix_unitaire,String prix_en_gros,String stock,String Service,String Categorie,String Stock_minimum)
     {
         connexionDatabase();
         SQL="INSERT INTO stock (Code,libelle,Categorie,Service,prix_unitaire,prix_en_gros,Stock_dispo,Stock_minimum) "
                 + "VALUES('"+code+"','"+nom+"','"+Categorie+"','"+Service+"','"+prix_unitaire+"','"+prix_en_gros+"','"+stock+"','"+Stock_minimum+"') ";
     return this.executionUpdate(SQL);
     } 
    
      public String queryInsertajoutStock(String Code,String nom,String Prix,String qtt,String Qtt_Avant_Ajout, String date, String heure, String fournisseur ,String Montant_Entree)
    {
         connexionDatabase();
         
       String SQL="INSERT INTO entree_stock (Code , Libelle , Prix_achat , Qtt_entrée,Qtt_Avant_Ajout ,Date_ajout , heure, fournisseur, Montant_Entree) VALUES ('"+Code+"','"+nom+"','"+Prix+"','"+qtt+"','"+Qtt_Avant_Ajout+"','"+date+"','"+heure+"','"+fournisseur+"','"+Montant_Entree+"')";
     return this.executionUpdate(SQL);
     }
      public String queryInsertsortiStock(String Code,String nom,String Prix,String qtt, String date, String heure, String Commentaire, String TotalSortie)
    {
         connexionDatabase();
         
       String SQL="INSERT INTO sortie_stock (Code , Libelle , Prix_unitaire , Qtt_Sortie,Date_sorte , heure , Commentaire,Montant_Sortie) VALUES ('"+Code+"','"+nom+"','"+Prix+"','"+qtt+"','"+date+"','"+heure+"','"+Commentaire+"','"+TotalSortie+"')";
     return this.executionUpdate(SQL);
     }
    
   
     
//Fin Stock     







//Facturation
      
      
       public ResultSet querySelect_Facture(String[] nomColonne , String nomTable, String NumFacture )
     {
          connexionDatabase();
          int i;
          SQL="SELECT ";
          for(i=0;i<= nomColonne.length-1;i++)
          {
              SQL += nomColonne[i];
              if(i<nomColonne.length-1)
              {
                  SQL+= ",";
              }
          }
         SQL+=" FROM " + nomTable;
         SQL+=" WHERE Num_Facture= '"+NumFacture+"' AND Statut= 'En Cour'";
         return this.executionQuery(SQL);
         
     }
     public ResultSet querySelect_Facture_Caisse(String[] nomColonne , String nomTable, String NumFacture , String statut )
     {
          connexionDatabase();
          int i;
          SQL="SELECT ";
          for(i=0;i<= nomColonne.length-1;i++)
          {
              SQL += nomColonne[i];
              if(i<nomColonne.length-1)
              {
                  SQL+= ",";
              }
          }
         SQL+=" FROM " + nomTable;
         SQL+=" WHERE Num_Facture= '"+NumFacture+"' AND Statut= '"+statut+"'";
         return this.executionQuery(SQL);
         
     }  
     
      public ResultSet querySelect_Facture_Caisse_Fait(String[] nomColonne , String nomTable, String NumFacture )
     {
          connexionDatabase();
          int i;
          SQL="SELECT ";
          for(i=0;i<= nomColonne.length-1;i++)
          {
              SQL += nomColonne[i];
              if(i<nomColonne.length-1)
              {
                  SQL+= ",";
              }
          }
         SQL+=" FROM " + nomTable;
         SQL+=" WHERE Num_Facture= '"+NumFacture+"' AND Statut= 'Encaissement_Fait'";
         return this.executionQuery(SQL);
         
     }  
     
     public ResultSet querySelect_Facture_Caisse_Fait_Admin(String[] nomColonne , String nomTable, String NumFacture )
     {
          connexionDatabase();
          int i;
          SQL="SELECT ";
          for(i=0;i<= nomColonne.length-1;i++)
          {
              SQL += nomColonne[i];
              if(i<nomColonne.length-1)
              {
                  SQL+= ",";
              }
          }
         SQL+=" FROM " + nomTable;
         SQL+=" WHERE Num_Facture= '"+NumFacture+"'";
         return this.executionQuery(SQL);
         
     }   
     
      public ResultSet querySelect_Caisse_Bilan(String[] nomColonne , String nomTable, String Date )
     {
          connexionDatabase();
          int i;
          SQL="SELECT ";
          for(i=0;i<= nomColonne.length-1;i++)
          {
              SQL += nomColonne[i];
              if(i<nomColonne.length-1)
              {
                  SQL+= ",";
              }
          }
         SQL+=" FROM " + nomTable;
         SQL+=" WHERE Date_Saved= '"+Date+"' AND Etat= 'Encaissement_Fait' OR Etat= 'Decaissement_Fait'";
         return this.executionQuery(SQL);
         
     }
      
      public ResultSet querySelect_Decaissement_Caisse(String[] nomColonne , String nomTable, String id )
     {
          connexionDatabase();
          int i;
          SQL="SELECT ";
          for(i=0;i<= nomColonne.length-1;i++)
          {
              SQL += nomColonne[i];
              if(i<nomColonne.length-1)
              {
                  SQL+= ",";
              }
          }
         SQL+=" FROM " + nomTable;
         SQL+=" WHERE id= '"+id+"'";
         return this.executionQuery(SQL);
         
     }  
     
     
     
     
     
     
       
      
     public int TotalFactur(String NumFacture) throws SQLException
     {
         connexionDatabase();
         SQL= "SELECT SUM(Montant_Total) FROM Facture_En_cour " ;
         SQL+=" WHERE Num_Facture= '"+NumFacture+"' AND Statut <> 'Encaissement_Fait'";
         ResultSet rs =this.executionQuery(SQL);
         ArrayList info = new ArrayList();
         info = resultSetToArrayList(rs);
         String result=info.get(0).toString().substring(20,info.get(0).toString().length()-3);
         System.out.print(result);
         if(result.equals("nu")) result="0";
         int i = Integer.parseInt(result);
         return i ;
         
     }
     public int TotalFacturAll(String NumFacture) throws SQLException
     {
         connexionDatabase();
         SQL= "SELECT SUM(Montant_Total) FROM Facture_En_cour " ;
         SQL+=" WHERE Num_Facture= '"+NumFacture+"'";
         ResultSet rs =this.executionQuery(SQL);
         ArrayList info = new ArrayList();
         info = resultSetToArrayList(rs);
         String result=info.get(0).toString().substring(20,info.get(0).toString().length()-3);
         System.out.print(result);
         if(result.equals("nu")) result="0";
         int i = Integer.parseInt(result);
         return i ;
         
     }
     public int TotalDecaissement(String NumFacture) throws SQLException
     {
         connexionDatabase();
         SQL= "SELECT SUM(Montant) FROM bon_de_sorti " ;
         SQL+=" WHERE id= '"+NumFacture+"' AND Statut IS NULL";
         ResultSet rs =this.executionQuery(SQL);
         ArrayList info = new ArrayList();
         info = resultSetToArrayList(rs);
         String result=info.get(0).toString().substring(14,info.get(0).toString().length()-3);
         System.out.print(result);
         if(result.equals("nu")) result="0";
         int i = Integer.parseInt(result);
         return i ;
         
     }
     public int Encaissement_Journalier(String Date) throws SQLException
     {
         connexionDatabase();
         SQL= "SELECT SUM(Montant_Total) FROM Facture_En_cour " ;
         SQL+=" WHERE Date_Encaissement= '"+Date+"' AND (Statut ='Encaissement_Fait' OR Statut ='Decaissement_Fait') "; 
         ResultSet rs =this.executionQuery(SQL);
         ArrayList info = new ArrayList();
         info = resultSetToArrayList(rs);
         System.out.println(info+"  "+SQL);
         String result=info.get(0).toString().substring(20,info.get(0).toString().length()-3);
         
         if(result.equals("nu")) result="0";
             
         
         System.out.print(result);
         int i = Integer.parseInt(result);
         return i ;
     }
      public int Decaissement_Journalier(String Date) throws SQLException
     {
         connexionDatabase();
         SQL= "SELECT SUM(Montant) FROM bon_de_sorti " ;
         SQL+=" WHERE Date= '"+Date+"'"; 
         ResultSet rs =this.executionQuery(SQL);
         ArrayList info = new ArrayList();
         info = resultSetToArrayList(rs);
         String result=info.get(0).toString().substring(14,info.get(0).toString().length()-3);
         
         if(result.equals("nu")) result="0";
             
         
         System.out.print(result);
         int i = Integer.parseInt(result);
         return i ;
     }
      public int Solde_Initial(String Date) throws SQLException
      {
          connexionDatabase();
          SQL= "SELECT * FROM solde_caisse WHERE Date = '" +Date+"'" ;
          ResultSet rs =this.executionQuery(SQL);
          String solde="0";
          while(rs.next()){
              solde = rs.getString("Solde_initial");
          }
          
          
          return Integer.parseInt(solde);
      }
      public int Solde_Initial_Recup_Debut() throws SQLException
      {
          connexionDatabase();
          SQL="SELECT * FROM solde_caisse ORDER BY id DESC LIMIT 1";
           ResultSet rs =this.executionQuery(SQL);
           String solde="0";
          while(rs.next()){
              solde = rs.getString("Solde_Final");
              
          }
           return Integer.parseInt(solde);
      }
      
      public int BonDeSortie() throws SQLException
      {
       connexionDatabase();
          SQL="SELECT * FROM bon_de_sorti ORDER BY id DESC LIMIT 1";
           ResultSet rs =this.executionQuery(SQL);
           String id="0";
          while(rs.next()){
              id = rs.getString("id");
              
          }
           return Integer.parseInt(id);   
      }
      
      
      public String queryInsertBon(String design,String titBon,String montant,String Date,String Heure)
      {
          String SQL="INSERT INTO bon_de_sorti (Designation , Titulaire_du_Bon , Montant ,Date , heure , Statut) "
                  + "VALUES ('"+design+"','"+titBon+"','"+montant+"','"+Date+"','"+Heure+"','Decaissement_EnAttente')";
             return this.executionUpdate(SQL);
      }
      
      
     public int Solde_Journalier(String Date) throws SQLException
     {
         int result=0;
         result =Solde_Initial(Date)+ Encaissement_Journalier(Date)-Decaissement_Journalier(Date);
         
         return result;
     }
     
     
     
     public String AjoutFacturation(int id , int qtt , String NumFacture) throws SQLException 
     {
         connexionDatabase();
         
         String name,prixU,stock;
         int total;
            //Designation nom   
                   SQL= "SELECT libelle FROM stock WHERE ID= " +id ;
        
                   ResultSet rs = this.executionQuery(SQL);
                   ArrayList info = new ArrayList();
                   info = resultSetToArrayList(rs);
                                     if(info.toString().equals("[]")){JOptionPane.showMessageDialog(null,"Produit inexistant");}
                   name=info.get(0).toString().substring(9,info.get(0).toString().length()-1);
                   
            //Designation prixU 
                 String  SQL2= "SELECT prix_unitaire FROM stock WHERE ID= " +id ;        
                   rs = this.executionQuery(SQL2);
                   ArrayList info2 = new ArrayList();
                   info2 = resultSetToArrayList(rs);
                   prixU=info2.get(0).toString().substring(15,info2.get(0).toString().length()-1);
            //Designation Stock dispo 
                  String SQL3= "SELECT Stock_dispo FROM stock WHERE ID= " +id ;        
                   rs = this.executionQuery(SQL3);
                   ArrayList info3 = new ArrayList();
                   info3 = resultSetToArrayList(rs);
                   stock=info3.get(0).toString().substring(13,info3.get(0).toString().length()-1);   
                   
             
             
            //DEsignation montant total
            total = Integer.parseInt(prixU) * qtt;
           
               
               
            //Control Stock
            int stockk = Integer.parseInt(stock)/10;
            if(stockk < qtt-1)
            {
                JOptionPane.showMessageDialog(null,"Attention Stock insuffisant");
            }
        
        
         
         String SQL5="INSERT INTO Facture_En_cour (id , Libelle , qtt , prix_unitaire , Montant_Total , Statut,Num_Facture) VALUES ("+id+",'"+name+"',"+qtt+",'"+prixU+"','"+total+"','En Cour','"+NumFacture+"')";
         this.executionUpdate(SQL5);
         name= ""+total;
         return name;
         
         
     }
     public String Approvisionement_Decaissement(String Libelle, String Montant, String Statut ,String Date, String Heure)
     {
         String SQL5="INSERT INTO Facture_En_cour (id , Libelle , qtt , prix_unitaire , Montant_Total , Statut,Num_Facture, Date_Encaissement,Heure_Encaissement)"
                 + "  VALUES (-1,'"+Libelle+"',0,'0','"+Montant+"','"+Statut+"','Input_OutPut','"+Date+"','"+Heure+"')";
         this.executionUpdate(SQL5);
         String rest="fait ";
         return rest;
     }
     
     public ArrayList resultSetToArrayList(ResultSet rs) throws SQLException{
           ResultSetMetaData md = rs.getMetaData();
           int columns = md.getColumnCount();
           ArrayList results = new ArrayList();

           while (rs.next()) {
                  HashMap row = new HashMap();
                  results.add(row);

                  for(int i=1; i<=columns; i++){
                        row.put(md.getColumnName(i),rs.getObject(i));
                    }
                }
    return results;
}   
     
     public String queryDeleteFacturation(String nomTable, String pos)
      {
          connexionDatabase();
          SQL="DELETE FROM " + nomTable + " WHERE " + pos;
          return this.executionUpdate(SQL);
      }
      
        
     
      public String queryUpdateDecaissement(String numFacture ,String Date, String Heure )
    {
         connexionDatabase();
         
           String SQL="UPDATE bon_de_sorti SET Statut='Decaisser' , Date ='"+Date+"' , heure ='"+Heure+"'  WHERE id ='"+numFacture +"'";
     return this.executionUpdate(SQL);
     }
      public String queryUpdateFactur(String numFacture , String Client)
    {
         connexionDatabase();
         
           String SQL="UPDATE facture_en_cour SET Statut ='Caisse' , Client='"+Client+"'  WHERE Num_Facture ='"+numFacture +"'";
     return this.executionUpdate(SQL);
     }
       public String queryUpdateSoldeCaisseDay(String Date , String Solde)
    {
         connexionDatabase();
         
           String SQL="UPDATE solde_caisse SET Solde_Final ='"+Solde+"' WHERE Date ='"+Date +"'";
     return this.executionUpdate(SQL);
     }
      
         public String queryUpdateFacturCaisse(String numFacture , String Date , String heure, String statut)
    {
         connexionDatabase();
         
           String SQL="UPDATE facture_en_cour SET Statut ='"+statut+"' , Date_Encaissement='"+Date+"' , "
                   + "Heure_Encaissement='"+heure+"'  WHERE Num_Facture ='"+numFacture +"'";
     return this.executionUpdate(SQL);
     }   
         public String querySavedFacturation(String numFacture, String montantTotal, String Date, String Heure, String statut )
         {
             String SQL="INSERT INTO facture_saved (Num_Facture , Montant_Total , Etat ,nature , Date_Saved , Heure_Saved) "
                     + "VALUES ('"+numFacture+"','"+montantTotal+"','"+statut+"','Encaissement','"+Date+"','"+Heure+"')";
             return this.executionUpdate(SQL);
         }
         public String queryUpdateFacturation(String numFacture, String montantTotal, String Date, String Heure)
         {
             String SQL="UPDATE facture_saved SET Etat ='Encaissement_Fait' , Date_Saved='"+Date+"' , "
                   + "Heure_Saved='"+Heure+"'  WHERE Num_Facture ='"+numFacture +"'";
             return this.executionUpdate(SQL);
         }
                  public String querySavedDecaissement(String numFacture, String montantTotal, String Date, String Heure )
         {
             String SQL="INSERT INTO facture_saved (Num_Facture , Montant_Total , Etat ,nature , Date_Saved , Heure_Saved) VALUES ("+numFacture+",'"+montantTotal+"','Decaissement_Fait','Decaissement','"+Date+"','"+Heure+"')";
             return this.executionUpdate(SQL);
         }
      
         public String queryInsertCaisseNewDay(String Date,String Solde_initial,String Caissier)
         {
             String SQL="INSERT INTO solde_caisse (Date , Solde_initial , Solde_Final ,Caissier) VALUES ('"+Date+"','"+Solde_initial+"','"+Solde_initial+"','"+Caissier+"')";
             return this.executionUpdate(SQL);
         }
      
        public String queryUpdateElementExecption(String num , String prixU, String tot)
    {
         connexionDatabase();
         
           String SQL="UPDATE facture_en_cour SET prix_unitaire ='"+prixU+"' , Montant_Total='"+tot+"'  WHERE Num ='"+num +"'";
     return this.executionUpdate(SQL);
     }
     
   //Fin facturation  
  
                  
   //Administatration LOGG
                  
   //FIN LOGG
   
         
         
         
    //ADMINE
         public String queryUpdatePersonnel(String poste, String nom, String ident)
         {
             String SQL="UPDATE gestionnaire SET Nom_Usager ='"+nom+"', "
                   + "Identifiant='"+ident+"'  WHERE Poste ='"+poste +"'";
             return this.executionUpdate(SQL);
         }
         
         public String queryUpdateStock(int id,String code, String nom, String categorie, String service, String prix_unit , String prix_gros, String stock_min)
         {
             String SQL="UPDATE stock SET Code ='"+code+"', "
                   + "libelle='"+nom+"' , Categorie='"+categorie+"',Service='"+service+"',prix_unitaire='"+prix_unit+"' ,"
                     + " prix_en_gros='"+prix_gros+"', Stock_minimum='"+stock_min+"'  WHERE id ="+id ;
             return this.executionUpdate(SQL);
         }
         
         
   //ADMINE      
     
     



//  fonction pour tous afficher 
     public ResultSet querySelectAll(String nomTable)
     {
         connexionDatabase();
         SQL = "SELECT * FROM " + nomTable;
         System.out.println(SQL);
         return this.executionQuery(SQL);
     }
     
   // fonction pour fficher tous suivant un paramettre : 
     public ResultSet  querySelectAll(String nomTable, String post)
     {
         connexionDatabase();
         SQL= "SELECT * FROM " + nomTable + " WHERE " + post ;
         return this.executionQuery(SQL);
         
     }
     
   //
     public ResultSet querySelect(String[] nomColonne , String nomTable)
     {
          connexionDatabase();
          int i;
          SQL="SELECT ";
          for(i=0;i<= nomColonne.length-1;i++)
          {
              SQL += nomColonne[i];
              if(i<nomColonne.length-1)
              {
                  SQL+= ",";
              }
          }
         SQL+=" FROM " + nomTable;
         return this.executionQuery(SQL);
         
     }
      public ResultSet querySelect(String[] nomColonne , String nomTable,String date)
     {
          connexionDatabase();
          int i;
          SQL="SELECT ";
          for(i=0;i<= nomColonne.length-1;i++)
          {
              SQL += nomColonne[i];
              if(i<nomColonne.length-1)
              {
                  SQL+= ",";
              }
          }
         SQL+=" FROM " + nomTable +" WHERE date ='"+date+"'";
         return this.executionQuery(SQL);
         
     }
         public String queryInsertRapport (String numMachine,String time,String temp,String hum,String vent,String pos,String date,String heure,String addBy)
    {
         connexionDatabase();
         
       String SQL="INSERT INTO rapport (numMachine,time,temp,hum,vent,pos,date,heure,addBy) VALUES ('"+numMachine+"','"+time+"','"+temp+"','"+hum+"','"+vent+"','"+pos+"','"+date+"','"+heure+"','"+addBy+"')";
         return this.executionUpdate(SQL);
     }
             public String queryInsertUser  (String nom,String pseudo,String pass,String id)
    {
         connexionDatabase();
         
       String SQL="INSERT INTO user (nom,pseudo,pass,authority) VALUES ('"+nom+"','"+pseudo+"','"+pass+"','"+id+"')";
         return this.executionUpdate(SQL);
     }    
    //
     public ResultSet fcSelectCommand(String[] nomColonne , String nomTable,String pos)
     {
          connexionDatabase();
          int i;
          SQL="SELECT ";
          for(i=0;i<= nomColonne.length-1;i++)
          {
              SQL += nomColonne[i];
              if(i<nomColonne.length-1)
              {
                  SQL+= ",";
              }
          }
         SQL+=" FROM " + nomTable + " WHERE " + pos;
         return this.executionQuery(SQL);
         
     }
     
    // fonction pour inserer des données
     public String queryInsert(String nomTable,String[] contenu)
     {
         connexionDatabase();
         int i;
         SQL="INSERT INTO " + nomTable + " VALUES(";
         for(i=0; i<= contenu.length-1;i++)
         {
             SQL+="'" + contenu[i] + "'" ;
             if(i<contenu.length-1)
             {
                 SQL+=",";
             }
         }
         SQL+=")";
         return this.executionUpdate(SQL);
         
     }
     
     //* 
     //* 
    
     
     
     ////////////
      public int querySomme(String Table , String colone ){
          connexionDatabase();
          BDD db ;
          int Somme=1;
          String SQL="SELECT SUM("+colone+") AS "+Somme+" FROM "+Table+"";
         
           this.executionUpdate(SQL);
              
           //Somme=Integer.parseInt(S);
          return Somme;
          
      }
      
      
       public String queryInsertmodifStock(int id,int valeur)
    {
         connexionDatabase();
         
           String SQL="UPDATE stock SET Stock_dispo ="+valeur+" WHERE ID ="+id ;
     return this.executionUpdate(SQL);
     }
   /////////////////
       
     
     
     
     public String queryInsert(String nomTable,String[] nomColonne,String[] contenu)
     {
         connexionDatabase();
         int i;
         SQL="INSERT INTO " + nomTable + " (";
         for(i=0; i<= nomColonne.length-1;i++)
         {
             SQL+= nomColonne[i];
             if(i<nomColonne.length-1)
             {
                 SQL+="','";
             }
         }
         
         SQL=")VALUES(";
         for(i=0; i<= contenu.length-1;i++)
         {
             SQL+="'" + contenu[i] + "'" ;
             if(i<contenu.length-1)
             {
                 SQL+=",";
             }
         }
         SQL+=")";
         return this.executionUpdate(SQL);
         
     }
    
   
     
     
      public String queryUpdate(String nomTable,String[] nomColonne,String[] contenu,String pos)
      {
          connexionDatabase();
          int i;
          SQL="UPDATE " + nomTable + " SET ";
          for(i=0 ; i<=nomColonne.length-1 ; i++)
          {
              SQL+= nomColonne[i] + "='" + contenu[i] + "'";
              if(i<nomColonne.length-1)
              {
                  SQL+=",";
              }
          }
          SQL+=" WHERE " + pos ;
          return this.executionUpdate(SQL);
          
      }
      
    //fonction pour supprimer la table
      public String queryDelete(String nomTable)
      {
          connexionDatabase();
          SQL="DELETE FROM " + nomTable ;
          return this.executionUpdate(SQL);
      }
     
    //fonction pour supprimer avec des parametre
      public String queryDelete(String nomTable, String pos)
      {
          connexionDatabase();
          SQL="DELETE FROM " + nomTable + " WHERE " + pos;
          return this.executionUpdate(SQL);
      }
}
