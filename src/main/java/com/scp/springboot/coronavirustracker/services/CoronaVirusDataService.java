 package com.scp.springboot.coronavirustracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.scp.springboot.coronavirustracker.model.LocationStats;

@Service
public class CoronaVirusDataService {
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";

	private List<LocationStats> allStats = new ArrayList<>();

	public List<LocationStats> getAllStats() {
		return allStats;
	}

	public void setAllStats(List<LocationStats> allStats) {
		this.allStats = allStats;
	}

	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException {
		List<LocationStats> newStats = new ArrayList<>();

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();

		HttpResponse<String> httpResponce = client.send(request, HttpResponse.BodyHandlers.ofString());

		System.out.println(httpResponce.body());

		StringReader csvbodyreader = new StringReader(httpResponce.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvbodyreader);
		for (CSVRecord record : records) {

			LocationStats locationstats = new LocationStats();
			locationstats.setCountry(record.get("Country/Region"));
			locationstats.setState(record.get("Province/State"));
			int latestCases=Integer.parseInt(record.get(record.size()-1));
			int previousDayCases=Integer.parseInt(record.get(record.size()-2));
			locationstats.setLatestTotalCases(latestCases);
			locationstats.setDiffFromPrevDay(latestCases-previousDayCases);
			

			newStats.add(locationstats);
		}
		this.allStats = newStats;
	}

}
