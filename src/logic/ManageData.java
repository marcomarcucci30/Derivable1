package logic;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ManageData {
	
	private List<ArrayList<String>> dataSet;
	private static final Logger log = Logger.getLogger(ManageData.class.getName());

	public ManageData(List<ArrayList<String>> dataSet) {
		setDataSet(dataSet);
	}
	
	
	public ManageData() {
	}

	public void completeList(String beginDate, String endDate) {
		LocalDate begin = LocalDate.parse(beginDate);
		LocalDate end = LocalDate.parse(endDate);
		List<Object> list = begin.datesUntil(end.plusMonths(1),Period.ofMonths(1)).collect(Collectors.toList());
		for (Object object : list) {
			String dateUtil = object.toString().substring(0,7);
			update(dateUtil, true);
		}	
				
	}
	
	/*Insert the date in the list respecting date order*/
	public void update(String date, boolean emptyMonth ) {
		
		int position = 0;
		Boolean find = false;
		
		if (date == null) {
			return;
		}
		
		//if dataSet is empty, add first element
		if (this.dataSet.isEmpty()) {
			List<String> ticketInfo  = new ArrayList<>();
			ticketInfo.add(date);
			ticketInfo.add("1");
			this.dataSet.add((ArrayList<String>) ticketInfo);
			return;
		}
		
		for (int i=0; i < this.dataSet.size(); i++) {
			
					
			//if the date is in dataSet, update the value of bug fixed for the date
			if (date.equals(this.dataSet.get(i).get(0))) {
				find = true;
				
				if (!emptyMonth) {
					int count = Integer.parseInt(this.dataSet.get(i).get(1)) + 1;
					this.dataSet.get(i).set(1, String.valueOf(count));
				}
				break;
			}
			
			//find eventual position to insert date if it's not in list
			if (this.dataSet.get(i).get(0).compareTo(date) < 0) {
				position = i + 1;
			}
	
		}
		//date is not in dataSet
		if (!Boolean.TRUE.equals(find)) {
			List<String> ticketInfo  = new ArrayList<>();
			ticketInfo.add(date);
			if (emptyMonth) {
				ticketInfo.add("0");
			}else {
				ticketInfo.add("1");
			}
			this.dataSet.add(position, (ArrayList<String>) ticketInfo);
		}
		
	}

	public List<ArrayList<String>> getDataSet() {
		return dataSet;
	}

	public void setDataSet(List<ArrayList<String>> dataSet) {
		this.dataSet = dataSet;
	}
	
	public static void main(String[] args) {
		ManageData manageData = new ManageData();
		manageData.completeList("2000-10-25", "2001-01-05");
		log.info("test");
	}

	

}
