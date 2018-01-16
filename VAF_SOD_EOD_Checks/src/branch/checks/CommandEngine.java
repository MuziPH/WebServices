package branch.checks;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class CommandEngine {
	Statement stmnt;
	private PreparedStatement pstmnt;
	Session session;
	private CallableStatement cstmnt;
	private static String alertSOD;
	private static String alertEOD;
	private static String reportSOD;
	private static String reportEOD;
	private static String serverIP;
	
	final private Logger myLogger = Logger.getLogger(CommandEngine.class.getName());

	
	
	public CommandEngine(String serverIP) {
		CommandEngine.serverIP = serverIP;
	}

	public static String getReportSOD() {
		return reportSOD;
	}

	public void setReportSOD(String localReport) {
		reportSOD = localReport;
	}
	
	

	public static String getReportEOD() {
		return reportEOD;
	}

	public void setReportEOD(String reportEOD1) {
		CommandEngine.reportEOD = reportEOD1;
		
	}

	public static String getAlertSOD() {
		return alertSOD;
	}

	public void setAlertSOD(String alertSOD1) {
		CommandEngine.alertSOD = alertSOD1;
	}

	public static String getAlertEOD() {
		return alertEOD;
	}

	public void setAlertEOD(String alertEOD) {
		CommandEngine.alertEOD = alertEOD;
	}

	private Connection createDBConnection(String serverIP) {

		//String conUrl = "jdbc:jtds:sqlserver://"+serverIP+":1433;databaseName=NTStannic2000;domain=sbicza01;useNTLMv2=true";
		//JDBC MS SQl SERVER 2012
		//Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String conUrl = "jdbc:microsoft:sqlserver://"+serverIP+":1433;databaseName=NTStannic2000;domain=sbicza01;useNTLMv2=true";
		// String conUrl =
		// "jdbc:sqlserver://10.187.2.119:1433;databasename=NTStannic2000;user=SBICZA01\\SA20999029;password=@dev2build;integratedSecurity=true";
		String userid = "SA20999029";
		String password = "@dev2build";
		Connection dbConnection = null;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			dbConnection = DriverManager.getConnection(conUrl, userid, password);
			System.out.println("Connection established...!!");
		} // end try
		catch (SQLException e) {
			System.out.println(e);

		} // end catch
 catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dbConnection;
	}

	public boolean shellConnect() {
		// Server connection variables

		String user = "sbsaAPPL";

		String pass = "ostutuxp";

		int port = 22;

		String keypath = "/home/muzi/temp/id_rsa.id";

		// String serverIP = "10.187.2.119";
		String serverIP = "10.144.173.133";

		try {

			JSch jsch = new JSch();

			jsch.addIdentity(keypath, pass);

			System.out.println("Key added....!!");

			session = jsch.getSession(user, serverIP, port);

			System.out.println("Session created");

			Properties config = new Properties();

			config.put("StrictHostKeyChecking", "no");

			session.setConfig(config);

			session.setPassword(pass);

			session.connect();

			System.out.println("Session now connected");

			return true;

		} // end try
		catch (JSchException e) {

			System.err.println("\nError occured while connecting to server..." + ":\n" + e);

			return false;

		} // end catch

	}

	public void checkSOD() {
		//Statement stmnt;

		try {
			String sqlQuery = "SELECT  CurrentDate, PreviousDate,RunIndicator FROM STC0000";
			stmnt = createDBConnection(serverIP).createStatement();
			ResultSet rs = stmnt.executeQuery(sqlQuery);
			ResultSetMetaData rsmd = rs.getMetaData();
			String colName1 = rsmd.getColumnName(1);
			String colName2 = rsmd.getColumnName(2);
			String colName3 = rsmd.getColumnName(3);
			
			if (rs.next()) {
				String localReport = "Morning Checks Muzi_Alert \n"
									+ "--------------------------------- \n";
						
				localReport += colName1 +" : " + rs.getString("CurrentDate") + "\n" 
							+ colName2 + " : " + rs.getString("PreviousDate") + "\n"
							+ colName3 + " : " + rs.getInt("RunIndicator") + "\n";
				
				if (rs.getInt("RunIndicator") == 41){
					localReport += "\n" + "SOD ran successfully \n";
					setAlertSOD(localReport);
				} else {
					localReport += "\n" + "SOD failed... Starting Job to correct!!";
					runSODJob();
					setAlertSOD(localReport);
				}
				myLogger.log(Level.INFO, "Database operations and reports successful in checkSOD job... " + serverIP);

			} // end while
			rs.close();
			stmnt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} // end catch

	}// end checkSOD
	
	public void checkEOD() {
		//Statement stmnt;

		try {
			String sqlQuery = "SELECT  CurrentDate, PreviousDate,RunIndicator FROM STC0000";
			stmnt = createDBConnection(serverIP).createStatement();
			ResultSet rs = stmnt.executeQuery(sqlQuery);
			ResultSetMetaData rsmd = rs.getMetaData();
			String colName1 = rsmd.getColumnName(1);
			String colName2 = rsmd.getColumnName(2);
			String colName3 = rsmd.getColumnName(3);
			
			if (rs.next()) {
				String localReport = "End-of-Day Checks Muzi_Alert \n"
									+ "--------------------------------- \n";
						
				localReport += colName1 +" : " + rs.getString("CurrentDate") + "\n" 
							+ colName2 + " : " + rs.getString("PreviousDate") + "\n"
							+ colName3 + " : " + rs.getInt("RunIndicator") + "\n";
				
				if (rs.getInt("RunIndicator") == 99) {
					localReport += "\n" + "EOD ran successful \n";
					setAlertEOD(localReport);
				} else {
					localReport += "\n" + "EOD failed... Starting Job to correct!!";
					runEODJob();
					setAlertEOD(localReport);
				}

			} // end while
			rs.close();
			stmnt.close();
			myLogger.log(Level.INFO, "Database operations and reports successful in checkEOD job... " + serverIP);
		} catch (SQLException e) {
			e.printStackTrace();
		} // end catch

	}// end checkSOD

	

	public void runSODJob() {
		
		// Setup Execution Hour and Minute
		LocalTime timeNow = LocalTime.now();
		int exHour = timeNow.getHour();
		int exMin = timeNow.getMinute();
		
		try {
			//Update SODTIME for Job start time
			System.out.println("Execution Hour: " + exHour + "  Execution Minute: " + exMin);
			pstmnt = createDBConnection(serverIP).prepareStatement("UPDATE SODTIME SET EXECHOUR = ?,EXECMIN = ?");
			pstmnt.setInt(1, exHour);
			pstmnt.setInt(2, exMin);
			pstmnt.executeUpdate();
			
			createDBConnection(serverIP).prepareCall("EXEC msdb.dbo.sp_start_job N'S2K_Auto_SOD'");
			//boolean checkvar = cstmnt.execute();
			// stmnt.execute("EXEC msdb.dbo.sp_start_job @job_name = N'S2K_Auto_EOD'");
			// allow the job to finish running
			TimeUnit.MINUTES.sleep(3);
			checkSODJob();
			//System.out.println("Inside runJob after sleep of 3 minutes....Job Status: " + checkvar);
			pstmnt.close();
			myLogger.log(Level.INFO, "Database operations successful in SOD job... " + serverIP);

		} catch (SQLException e) {
			myLogger.log(Level.ERROR, "Database error in SOD job... " +e);
		} /*catch (InterruptedException e) {
			e.printStackTrace();
		}*/ catch (InterruptedException e) {
			myLogger.log(Level.ERROR, "SOD job interrupted while running... " +e);
		}

	} // end runSODJob

	public void checkSODJob() {

		try {
			//create connection
			//stmnt = createDBConnection(serverIP).createStatement();
			
			// Check job status and write to report
			ResultSet rs = stmnt.executeQuery("EXEC msdb.dbo.sp_help_jobhistory \n" + "@job_name = N'S2K_Auto_SOD'");
			ResultSetMetaData rsmd = rs.getMetaData();
			String localReport = "Report after automated kickstart... \n \n";
			
			// Result Set to for Job History
			if (rs.next()) {
				localReport += "Job Details" + "\n"
							   + "========" + "\n"
							   + rsmd.getColumnName(2) + " : " + rs.getString(2) + "\n"
							   + rsmd.getColumnName(3) + " : " + rs.getInt(3) + "\n"
							   + rsmd.getColumnName(4) + " : " + rs.getInt(4) + "\n"
							   + rsmd.getColumnName(5) + " : " + rs.getInt(5) + "\n"
							   + rsmd.getColumnName(6) + " : " + rs.getInt(6) + "\n \n";
				setReportSOD(localReport);

			} // end IF
			
			//Create Result Set for SOD table
			String sqlQuery = "SELECT  CurrentDate, PreviousDate,RunIndicator FROM STC0000";
			ResultSet rs2 = stmnt.executeQuery(sqlQuery);
			ResultSetMetaData rsmd2 = rs2.getMetaData();

			if (rs2.next()) {
				localReport +=  "SOD Data" + "\n"
								+ "=======" + "\n"
								+ rsmd2.getColumnName(1) +" : " + rs2.getString("CurrentDate") + "\n" 
								+ rsmd2.getColumnName(2) + " : " + rs2.getString("PreviousDate") + "\n"
								+ rsmd2.getColumnName(3) + " : " + rs2.getInt("RunIndicator");
				setReportSOD(localReport);
			}
			myLogger.log(Level.INFO, "Database operations successful in checkEOD job... " + serverIP);

		} catch (

		SQLException e) {
			e.printStackTrace();
		} // end CATCH

	}// end checkJobStatus
	
	public void runEODJob() {
		
		// Setup Execution Hour and Minute
		LocalTime timeNow = LocalTime.now();
		int exHour = timeNow.getHour();
		int exMin = timeNow.getMinute();
		
		try {
			//Update SODTIME for Job start time
			System.out.println("Execution Hour: " + exHour + "  Execution Minute: " + exMin);
			pstmnt = createDBConnection(serverIP).prepareStatement("UPDATE EODTIME SET EXECHOUR = ?,EXECMIN = ?");
			pstmnt.setInt(1, exHour);
			pstmnt.setInt(2, exMin);
			pstmnt.executeUpdate();
			
			// run actual Job
			cstmnt = createDBConnection(serverIP).prepareCall("EXEC msdb.dbo.sp_start_job N'S2K_Auto_EOD'");
			boolean checkvar = cstmnt.execute();
			
			// allow the job to finish running
			TimeUnit.SECONDS.sleep(10);
			checkEODJob();
			System.out.println("Inside runJob after sleep of 3 minutes....Job Status: " + checkvar);
			pstmnt.close();
			myLogger.log(Level.INFO, "Database operations successful in runEODJob job... " + serverIP);

		} catch (SQLException e) {
			myLogger.log(Level.ERROR, "Database error in EOD job... " +e);
		} catch (InterruptedException e) {
			myLogger.log(Level.ERROR, "EOD job interrupted while running... " +e);
		}

	} // end runSODJob
	
	public void checkEODJob() {

		try {
			//create connection
			//stmnt = createDBConnection(serverIP).createStatement();
			
			// Check job status and write to report
			ResultSet rs = stmnt.executeQuery("EXEC msdb.dbo.sp_help_jobhistory \n" + "@job_name = N'S2K_Auto_EOD'");
			ResultSetMetaData rsmd = rs.getMetaData();
			String localReport = "Report after automated kickstart... \n \n";
			
			// Result Set to for Job History
			if (rs.next()) {
				localReport += "Job Details" + "\n"
							   + "========" + "\n"
							   + rsmd.getColumnName(2) + " : " + rs.getString(2) + "\n"
							   + rsmd.getColumnName(3) + " : " + rs.getInt(3) + "\n"
							   + rsmd.getColumnName(4) + " : " + rs.getInt(4) + "\n"
							   + rsmd.getColumnName(5) + " : " + rs.getInt(5) + "\n"
							   + rsmd.getColumnName(6) + " : " + rs.getInt(6) + "\n \n";
				setReportEOD(localReport);

			} // end IF
			
			//Create Result Set for EOD table
			String sqlQuery = "SELECT  CurrentDate, PreviousDate,RunIndicator FROM STC0000";
			ResultSet rs2 = stmnt.executeQuery(sqlQuery);
			ResultSetMetaData rsmd2 = rs2.getMetaData();

			if (rs2.next()) {
				localReport +=  "EOD Data" + "\n"
								+ "=======" + "\n"
								+ rsmd2.getColumnName(1) +" : " + rs2.getString("CurrentDate") + "\n" 
								+ rsmd2.getColumnName(2) + " : " + rs2.getString("PreviousDate") + "\n"
								+ rsmd2.getColumnName(3) + " : " + rs2.getInt("RunIndicator");
				setReportEOD(localReport);
			}
			myLogger.log(Level.INFO, "Database operations successful in EOD job... " + serverIP);
		} catch (

		SQLException e) {
			myLogger.log(Level.ERROR, "Database error in EOD job... " +e);
			
		} // end CATCH
		
		

	}// end checkEODJob
}// end class
