package branch.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
	
	public static void main(String[] args) {
		 Connection con = null;
		 String serverIP = "10.187.12.83";
		 //MS SQL SERVER 2012
		 String conUrl = "jdbc:sqlserver://"+serverIP+":1433;databaseName=NTStannic2000;domain=sbicza01;useNTLMv2=true";
		 //MS SQL SERVER 2003
		 //String conUrl = "jdbc:jtds:sqlserver://10.187.2.119:1433;databaseName=NTStannic2000;domain=sbicza01;useNTLMv2=true";
         //String conUrl = "jdbc:sqlserver://10.187.2.119:1433;databasename=NTStannic2000;user=SBICZA01\\SA20999029;password=@dev2build;integratedSecurity=true";
         String userid = "SA20999029";
         String password = "@dev2build";
         //String authType = "Integrated Security=true";

	   try {
          // ...
		   Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	    con = DriverManager.getConnection(conUrl,userid,password);
	    System.out.println("Connection established...!!");
          // ... 
 	  } catch (Exception e) { e.printStackTrace(); }
           finally {
             if (con != null) try { con.close(); } catch(Exception e) {}
           }
	}

}
