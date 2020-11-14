/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connexion;

/**
 *
 * @author Black bOu
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;


public class ResultSetTableModel extends AbstractTableModel 
{
   //delclaration
 private ResultSet rs;
 
 
 
 
 public ResultSetTableModel (ResultSet rs)
 {
  this.rs=rs;
  fireTableDataChanged();
  
 }
    
 //
 public int getColumnCount()
 {
     try
     {
         if(rs == null)
         {
             return 0 ;
         }else {
                return rs.getMetaData().getColumnCount();
               }
     }catch (SQLException e)
          {
            System.out.println("getColumnCount a eu une erreur !");
            System.out.println(e.getMessage());
            return 0 ;
          }
     
 }
 
 //
 
 public int getRowCount()
 {
     try 
     {
         if(rs == null)
         {
             return 0; 
         }else {
                rs.last();
                return rs.getRow();
               }
         
     }catch (SQLException e)
         {
          System.out.println("getRowCount a rencontré une erreur");
          System.out.println(e.getMessage());
          return 0;
         }
 
 }

 //
 public Object getValueAt(int rowIndex,int colomnIndex)
 {
     if(rowIndex <0 || rowIndex > getRowCount() ||colomnIndex <0 || colomnIndex> getColumnCount())
       {
         return null ;
       }
     
     try
       {
         if(rs == null)
         {
             return null ;
         }else {
                rs.absolute(rowIndex + 1);
                return rs.getObject(colomnIndex + 1);
                
               }
       }catch(SQLException e)
            {
                System.out.println("getValueAt a rencontrer une erreur :");
                System.out.println(e.getMessage());
                return null ;
            }
 }
 
 
//
 @Override
 public String getColumnName(int columnIndex)
 {
     try
     {
         return rs.getMetaData().getColumnName(columnIndex + 1 );
     
     }catch(SQLException e)
            {
             System.out.println("getColumnName a rencontrer une erreur");
             System.out.println(e.getMessage());
              
            }
     return super.getColumnName(columnIndex);
     
 }
 
}
