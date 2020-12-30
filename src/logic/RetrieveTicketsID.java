package logic;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;


import org.json.JSONArray;

public class RetrieveTicketsID {
	
	private static final Logger log = Logger.getLogger(RetrieveTicketsID.class.getName());


   private static String readAll(Reader rd) throws IOException {
	      StringBuilder sb = new StringBuilder();
	      int cp;
	      while ((cp = rd.read()) != -1) {
	         sb.append((char) cp);
	      }
	      return sb.toString();
	   }

   public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try (BufferedReader rd = new BufferedReader(new InputStreamReader(is,  StandardCharsets.UTF_8))) {
         String jsonText = readAll(rd);
         return new JSONArray(jsonText);
       } finally {
         is.close();
       }
   }

   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try(BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
         String jsonText = readAll(rd);
         return new JSONObject(jsonText);
       } finally {
         is.close();
       }
   }
   
   
  	   public static void main(String[] args) throws IOException, JSONException {
  		   
  		   Properties prop = ManageProperties.getInstance();
  		   long startTime = System.currentTimeMillis();
  		   
  		   List<ArrayList<String>> dataSet = new ArrayList<>();
  		   ManageData manageData = new ManageData(dataSet);
  		   CreateFile createFile = new CreateFile(dataSet);
  		   Command cmd = new Command();
  		   cmd.gitClone();
  		   log.info(prop.getProperty("WaitingMain"));
  		   
  		   String projName =prop.getProperty("MAHOUT");
	   Integer j = 0;
	   Integer i = 0;
	   Integer total = 1;
      //Get JSON API for closed bugs w/ AV in the project
	   do {
         //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
         j = i + 1000;
         String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                + i.toString() + "&maxResults=" + j.toString();
         JSONObject json = readJsonFromUrl(url);
         JSONArray issues = json.getJSONArray("issues");
         total = json.getInt("total");
         
         for (; i < total && i < j; i++) {
            //Iterate through each bug
        	String key = issues.getJSONObject(i%1000).get("key").toString();
        	cmd.setTicket(key);
        	String date = cmd.log();
        	manageData.update(date,false);
         }  
      } while (i < total);
	   //retrieve date of first and last commit
	   String beginDate = cmd.lifeProject().get(0);
	   String endDate = cmd.lifeProject().get(1);
	   
	   //add the date which there is not bug fixed
	   manageData.completeList(beginDate,endDate);
	   
	   //create CSV file
	   createFile.createFileCSV();
	   
	  long estimatedTime = System.currentTimeMillis() - startTime;
	  String finalMsg = prop.getProperty("programFinished")+" "+ estimatedTime+ "second";
	  log.log(Level.INFO, finalMsg);
   }

 
}
