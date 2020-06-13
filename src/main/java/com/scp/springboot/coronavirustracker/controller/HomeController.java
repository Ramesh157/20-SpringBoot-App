package com.scp.springboot.coronavirustracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.scp.springboot.coronavirustracker.model.LocationStats;
import com.scp.springboot.coronavirustracker.services.CoronaVirusDataService;

@Controller
public class HomeController {
	@Autowired
	CoronaVirusDataService coronaVirusDataService;

	@GetMapping("/")
	public String home(Model model) {

		List<LocationStats> allStats = coronaVirusDataService.getAllStats();
		int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
		int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();

		model.addAttribute("locationStats", allStats);
		model.addAttribute("totalReportedCases", totalReportedCases);

		return "home";
	}

}
