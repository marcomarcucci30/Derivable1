package logic;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Command {
	
	private String ticket;
	private static final Logger log = Logger.getLogger(Command.class.getName());
	private static Properties prop = ManageProperties.getInstance();
	private static String excpetion = "Object is null";
	private String directory = "directory";
	private String gitC = "git -C";
	private String ioException = "IOException in command";
	private String interruptedException = "InterruptedException in Command.";

	public Command(String keyValue) {
		setTicket(keyValue);
	}
	
	public Command() {
		
	}
	
	private static void exception (Object o) {
		if (o == null) {
			throw new IllegalStateException(excpetion);
		}
	}
	
	//Update repository
	public void gitPull() {

		String command = gitC+" "+prop.getProperty(directory)+" pull "+ prop.getProperty("repo")+"";
		Process p = null;
		try {
			//execute Command
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
		} catch (IOException e) {
			log.info(ioException );
		} catch (InterruptedException e) {
			log.log(Level.SEVERE,interruptedException, e);
			Thread.currentThread().interrupt();
		}
		
		exception(p);
		
		if (p.exitValue() == 0) {
			log.info(prop.getProperty("UpdateComplete"));
		}else {
			log.info("Update repository not succesfully.");
		}
	}
	
	//Clone the repository in the specified directory
	public void gitClone() {
		
		String command = "git clone "+prop.getProperty("repo")+" "+prop.getProperty(directory)+"";
		Process p = null;
		try {
			//execute command
			p = Runtime.getRuntime().exec(command);
			log.info(prop.getProperty("infoGitCLone"));
			p.waitFor();
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		} catch (InterruptedException e) {
			log.log(Level.SEVERE,interruptedException, e);
			Thread.currentThread().interrupt();
		}
		exception(p);
		if (p.exitValue() != 0) {
			log.info(prop.getProperty("dirAlredyExist"));
			gitPull();
		}else {
			log.info(prop.getProperty("CloneComplete!"));
		}
 	}
	
	//return the date of the first and last commit of the the project
	public List<String> lifeProject() throws IOException {	
		//Command log commit
		String commandEndDate = gitC+" "+ prop.getProperty(directory) +" log --pretty=format:\"%cd\" --date=iso-strict --max-count=1";
		String commandBeginDate = gitC+" "+ prop.getProperty(directory) +" rev-list --reverse --max-parents=0 HEAD --pretty=format:\"%cd\" --date=iso-strict";
		
		Process pBegin = null;
		Process pEnd = null;
		List<String> date = new ArrayList<>();
		String dateBegin = null;
		String dateEnd = null;
		String line;
		try {
			//execute command
			pBegin = Runtime.getRuntime().exec(commandBeginDate);
			pEnd = Runtime.getRuntime().exec(commandEndDate);
			pBegin.waitFor();
			pEnd.waitFor();
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		} catch (InterruptedException e) {
			log.info(interruptedException);
			Thread.currentThread().interrupt();
		}
		exception(pEnd);
		exception(pBegin);
		BufferedReader inputBegin = new BufferedReader(new InputStreamReader(pBegin.getInputStream()));
		BufferedReader inputEnd = new BufferedReader(new InputStreamReader(pEnd.getInputStream()));
		try {
			int count = 0;
			while ((line = inputBegin.readLine()) != null) {
				//extract date in form 'yyyy-mm'
				if (count == 1) {
					dateBegin = line.substring(0,10);
				}
				count++;				
			}
			inputBegin.close();
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		try {
			while ((line = inputEnd.readLine()) != null) {
				//extract date in form 'yyyy-mm'
				dateEnd = line.substring(0,10);				
			}
			inputEnd.close();
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		date.add(dateBegin);
		date.add(dateEnd);
		return date;
	}
	
	public String log() {	
		//Command log commit
		String command = gitC+" "+ prop.getProperty(directory) +" log --pretty=format:\"%cd\" "
				+ "--grep=" + ticket +" --date=iso-strict  --max-count=1";
		
		
		Process p = null;
		String date = null;
		String line;
		try {
			//execute command
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		} catch (InterruptedException e) {
			log.log(Level.SEVERE,ioException, e);
			Thread.currentThread().interrupt();
		}
		exception(p);
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		try {
			while ((line = input.readLine()) != null) {
				//extract date in form 'yyyy-mm'
				date = line.substring(0,7);
			}
			input.close();	
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		return date;
	}
	
	public void setTicket(String ticket){
		this.ticket = ticket;
	}
	public String getTicket() {
		return this.ticket;
	}

	public static void main(String[] args) {
		Command cmdCommand = new Command();
		cmdCommand.getTicket();
	}

}
