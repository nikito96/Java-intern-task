package jdi;

import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Program {
	
	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public static void main(String[] args) {
		ReportGenerator report = new ReportGenerator(args[0], args[1]);
		scheduler.scheduleAtFixedRate(report, 0, 31, TimeUnit.DAYS);
	}

}
