package logic;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateFile {
	
	private List<ArrayList<String>> dataSet;
	private static Properties prop = ManageProperties.getInstance();
	private static final Logger log = Logger.getLogger(CreateFile.class.getName());

	public CreateFile(List<ArrayList<String>> dataSet) {
		setDataSet(dataSet);
	}
	
	
	public CreateFile() {
	}


	public void createFileCSV() {
					
		PrintWriter writer = null;
		try {
			//create file
			writer = new PrintWriter(new File(prop.getProperty("fileName")));
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE,"FileNotFoundException", e);
		}
		StringBuilder sb = new StringBuilder();

		int i = 0;
		//Append string to write in the file
		while (i < this.dataSet.size()) {
			sb.append(this.dataSet.get(i).get(0));
			sb.append(';');
			sb.append(this.dataSet.get(i).get(1));
			sb.append('\n');
			i++;
		}
		
		if (writer == null) {
			throw new IllegalStateException("Object is null.");
		}
		//Write CSV file
		writer.write(sb.toString());
	    writer.close();
	    log.info(prop.getProperty("done"));
		
	}
	

	public List<ArrayList<String>> getDataSet() {
		return dataSet;
	}

	public void setDataSet(List<ArrayList<String>> dataSetArrayList) {
		this.dataSet = dataSetArrayList;
	}

	public static void main(String[] args) {
		CreateFile createFile = new CreateFile();
		createFile.getDataSet();
	}

}
