package branch.checks;

public class CheckSODApp {

	public static void main(String[] args) {
		
		CommandEngine serverCheck = new CommandEngine("10.187.2.119");
		serverCheck.checkSOD();
		System.out.println(CommandEngine.getAlertSOD() + "\n" + CommandEngine.getReportSOD());
		
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
			mailChecks.setSubject("VAF Morning Checks - SIT");
			mailChecks.setMessage(mailReport);
			mailChecks.sendMail();
			
			
		} // end try
		catch (Exception ex) {
			
		}//end catch
		
		
		

	}

}
