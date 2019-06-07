package jdi;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ReportGenerator implements Runnable {
	
	private String data;
	private String reportDefinition;
	private Path report;
	private ArrayList<String> file = new ArrayList<String>();
	
	public ReportGenerator(String data, String reportDefinition) {
		this.data = data;
		this.reportDefinition = reportDefinition;
	}

	@Override
	public void run() {
		try {
			
			Object dataFileObj = new JSONParser().parse(new FileReader(this.data));
			Object reportDefinitionFileObj = new JSONParser().parse(new FileReader(this.reportDefinition));
			JSONArray data = (JSONArray) dataFileObj;
			JSONObject reportDefinition = (JSONObject) reportDefinitionFileObj;
			HashMap<String, Long> employees = new HashMap<String, Long>();
			boolean useExprienceMultiplier = (boolean) reportDefinition.get("useExprienceMultiplier");
			long score = 0;
			
			report = Paths.get("D:/report.csv");
			file.add("Name,\tScore");
			
			for (Object obj: data) {
				JSONObject jobj = (JSONObject) obj;
				if (useExprienceMultiplier) {
					score = (long) jobj.get("totalSales") / (long) jobj.get("salesPeriod") 
							*  (long) jobj.get("experienceMultiplier");
					employees.put((String)jobj.get("name"), score);
				} else {
					score = (long) jobj.get("totalSales") / (long) jobj.get("salesPeriod");
					employees.put((String)jobj.get("name"), score);
				}
			}
			
			List<Long> employeesValues = new ArrayList<>(employees.values());
			long topPercents = employeesValues.size() * ((Long)reportDefinition.get("topPerformersThreshold")/100);
			Comparator c = Collections.reverseOrder();
			Collections.sort(employeesValues, c);
			long topValue = Math.round(employeesValues.get((int) topPercents));
			
			for (Object obj: data) {
				JSONObject jobj = (JSONObject) obj;
				long salesPeriod = (Long) jobj.get("salesPeriod");
				long periodLimit = (Long) reportDefinition.get("periodLimit");
				
				if (salesPeriod <= periodLimit && employees.get(jobj.get("name")) <= topValue) {
					file.add(jobj.get("name")+", "+employees.get(jobj.get("name")));
				}
			}
			Files.write(report, file, Charset.forName("UTF-8"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
