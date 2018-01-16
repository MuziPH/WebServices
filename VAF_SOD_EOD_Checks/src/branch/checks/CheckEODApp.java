package branch.checks;

public class CheckEODApp {

	public static void main(String[] args) {
			
			CommandEngine serverCheck = new CommandEngine("10.187.2.119");
			serverCheck.checkEOD();
			System.out.println(CommandEngine.getAlertEOD() + "\n" + CommandEngine.getReportEOD());
			
			try {
				Email mailChecks = new Email();
				String eodAlertMail = CommandEngine.getAlertEOD();
				if(eodAlertMail == null) {
					eodAlertMail = "\n";
				}// end IF
				
				String eodReportMail = CommandEngine.getReportEOD();
				if(eodReportMail == null) {
					eodReportMail = "\n";
				}// end IF
				String mailReport = eodAlertMail + "\n" + eodReportMail;
				mailChecks.setSubject("VAF EOD Checks - SIT");
				mailChecks.setMessage(mailReport);
				mailChecks.sendMail();
				
				
			} // end try
			catch (Exception ex) {
				
			}//end catch
			
			
			

		}

	}


