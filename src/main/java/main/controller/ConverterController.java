package main.controller;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;

import main.model.CurrentRate;

import main.model.RateDynamic;
import main.pojo.BankRate;
import main.pojo.ChartData;
import main.repository.CurrentRateRepository;

import main.repository.RateDynamicRepository;
import main.service.ConverterService;


@Controller
public class ConverterController {
	@Autowired
	private CurrentRateRepository currentRateRepository;
	@Autowired
	private RateDynamicRepository rateDynamicRepository;
	@Autowired
	private ConverterService converterService;
	@Autowired
	private LocalDate updateDate;
	
	@GetMapping("/converter")
	public String initialPage(Model model) throws IOException, ParserConfigurationException, SAXException, ParseException {
		long days = ChronoUnit.DAYS.between
				(updateDate, LocalDate.now());
		if (days!=0) {
			currentRateRepository.deleteAll();
			rateDynamicRepository.deleteAll();
			converterService.loadCurrentRates();
			converterService.loadRateDynamic();
			updateDate=LocalDate.now();
		}
		ArrayList<CurrentRate> currencies = new ArrayList<>(currentRateRepository.findAll());
		ArrayList<RateDynamic> dynamic = new ArrayList<>(rateDynamicRepository.findAll());
		model.addAttribute("currencies", currencies);
		model.addAttribute("dynamic", dynamic);
		return "converter";
	}
	
	@ResponseBody
	@GetMapping("/dynamic")
	public List<ChartData> sendDynamic(
			@RequestParam(value="period") String period,
			@RequestParam(value = "curId") Long rateId,
			Model model) {
		int days;
		if (period.equals("Неделя"))
			days=7;
		else if (period.equals("Месяц"))
			days=31;
		else days=365;
		List<RateDynamic>list= rateDynamicRepository.
				findPeriod(LocalDate.now().minusDays(days),
						LocalDate.now(), rateId);
		List<ChartData>list2=new ArrayList<>();
		for (RateDynamic rd: list) {
			list2.add(new ChartData(rd.getDate()+"", rd.getValue()+""));
		}
		return list2;
	}
	
	@ResponseBody
	@GetMapping("/banks")
	public List<BankRate> sendBanks(Model model) throws JsonProcessingException, IOException {
		List<BankRate> br = new ArrayList<>();
		converterService.parseJsonAlpha(br);
		converterService.parseJsonTinkoff(br);
		return br;
	}
	
	@ResponseBody
	@GetMapping("/result")
	public Double convert(
			@RequestParam("curId1") Long c1, 
			@RequestParam("curId2") Long c2,
			@RequestParam("sum") Double sum, 
			Model model) {
		CurrentRate cr1 = currentRateRepository.findCurrentRateById(Long.valueOf(c1));
		CurrentRate cr2 = currentRateRepository.findCurrentRateById(Long.valueOf(c2));
		double result = (cr1.getValue() / cr1.getNominal() * sum) 
				/ (cr2.getValue() / cr2.getNominal());
		result=(int)(result*100)/100.0;
		return result;
	}
}
