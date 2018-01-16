package branch.checks;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


class MorningCheckThread implements Runnable{
	private String ipAdress;
	
	public MorningCheckThread(String serverIP) {
		this.ipAdress = serverIP;
	}
	
	public void run() {
		process();
	} // end run
	
	public synchronized void process() {

        CommandEngine serverCheck = new CommandEngine(ipAdress);
		serverCheck.checkSOD();
		//System.out.println(CommandEngine.getAlertSOD() + "\n" + CommandEngine.getReportSOD());

            }//end process
	
}// end MOrniingCheckThread

public class MorningCheck implements Job{
	
	final private Logger myLogger = Logger.getLogger(MorningCheck.class.getName());
	
	private static Logger logger = Logger.getLogger(MorningCheck.class.getName()); 

	public void execute(JobExecutionContext jec) throws JobExecutionException {
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		
		// iterate through the arrayList from getServerIP
		for(String iterator: getServerIP()) {
			executor.submit(new MorningCheckThread(iterator));
		}
		
		executor.shutdown();
		
		 try {

	            executor.awaitTermination(1, TimeUnit.DAYS);

	        } catch (InterruptedException ex) {

	            logger.log(Level.ERROR, null, ex);

	        }
		
		try {
			Email mailChecks = new Email();
			
			String sodAlertMail = CommandEngine.getAlertSOD();
			
			if(sodAlertMail == null) {
				sodAlertMail = "\n";
			}// end IF
			
			String sodReportMail = CommandEngine.getReportSOD();
			
			if(sodReportMail == null) {
				sodReportMail = "\n";
			}// end IF
			
			String mailReport = sodAlertMail + "\n" + sodReportMail;
			System.out.println(mailReport);
			mailChecks.setSubject("VAF Morning Checks - SIT");
			mailChecks.setMessage(mailReport);
			mailChecks.sendMail();
			
			
		} // end try
		catch (Exception ex) {
			myLogger.log(Level.ERROR, "Error sending email... " +ex);
			
		}//end catch
		
		
	}// end execute
	
	// Retrieve Server IP addresses to be checked from a file
	
	private List<String> getServerIP(){
		List<String> serverList = new ArrayList<String>();
		
		//Get scanner instance
		Scanner input = null;
		try {
			input = new Scanner(new File("/home/muzi/vafservers.txt"));
			
			while(input.hasNextLine()) {
				String line =input.nextLine();
				serverList.add(line);
			}
		} catch (FileNotFoundException ex) {
			myLogger.log(Level.ERROR, "Error fecthing server list file... " +ex);
		}
		
		// close scanner
		input.close();
		return serverList;
		
	}

}
