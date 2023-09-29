package main.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import main.model.CurrentRate;
import main.model.RateDynamic;
import main.pojo.BankRate;
import main.repository.CurrentRateRepository;
import main.repository.RateDynamicRepository;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ConverterService {
	@Autowired
	private CurrentRateRepository currentRateRepository;
	@Autowired
	private RateDynamicRepository rateDynamicRepository;

	public void loadCurrentRates() throws IOException, ParserConfigurationException, SAXException {
		CurrentRate cr1 = new CurrentRate();
		cr1.setName("Российский рубль");
		cr1.setCharCode("RUB");
		cr1.setNominal(1);
		cr1.setValue(Double.valueOf(1));
		cr1.setUniqueCode("");
		currentRateRepository.save(cr1);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse("https://www.cbr.ru/scripts/XML_daily.asp");
		doc.getDocumentElement().normalize();
		NodeList list = doc.getElementsByTagName("Valute");
		for (int temp = 0; temp < list.getLength(); temp++) {
			Node node = list.item(temp);
			Element element = (Element) node;
			String charCode = element.getElementsByTagName("CharCode").item(0).getTextContent();
			Integer nominal = Integer.valueOf(element.getElementsByTagName("Nominal").item(0).getTextContent());
			String name = element.getElementsByTagName("Name").item(0).getTextContent();
			Double value = Double.valueOf( element.getElementsByTagName("Value").item(0).getTextContent().replace(",","."));
			String uniqueCode = element.getAttribute("ID");
	
			CurrentRate cr = new CurrentRate();
			cr.setName(name);
			cr.setCharCode(charCode);
			cr.setNominal(nominal);
			cr.setValue(value);
			cr.setUniqueCode(uniqueCode);
			currentRateRepository.save(cr);
		}
		
	}
	public void loadRateDynamic() throws ParserConfigurationException, SAXException, IOException, ParseException {
		ArrayList<CurrentRate> currencies = new ArrayList<>(currentRateRepository.findAll());
		LocalDate fromDate=LocalDate.now().minusYears(1);
		LocalDate monthAgo=LocalDate.now()
			.minusDays(31);
		LocalDate toDate=LocalDate.now();
		for (CurrentRate cr: currencies) {
			String d1 = fromDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			String d2 = toDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			String xml = "https://www.cbr.ru/scripts/XML_dynamic.asp"
					+ "?date_req1=" + d1+"&date_req2=" + d2
					+ "&VAL_NM_RQ=" + cr.getUniqueCode();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xml);
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName("Record");
			int per = 31;
			for (int temp = 0; temp < list.getLength(); temp+=per) {
				Node node = list.item(temp);
				Element element = (Element) node;
				String dateString = element.getAttribute("Date");
				Double value = Double.valueOf( element.getElementsByTagName("Value").item(0).getTextContent().replace(",","."));
			
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
				LocalDate date = LocalDate.parse(dateString, formatter);
				
				if (date.plusDays(31).compareTo(monthAgo)>0 && per==31) {
					per=1;
					temp=list.getLength()-31;
				}
				RateDynamic rd = new RateDynamic();
				rd.setCurrExchRate(cr);
				rd.setDate(date);
				rd.setValue(value);
				rateDynamicRepository.save(rd);
			}
		}
	}

	private ResponseEntity<String> getJsonAlpha() throws UnsupportedEncodingException {
		RestTemplate restTemplate = new RestTemplate();
		ZonedDateTime ztd = ZonedDateTime.now().withNano(0);
		String url="https://alfabank.ru/api/v1/scrooge/"
				+ "currencies/alfa-rates?"
				+ "currencyCode.in=USD,EUR"
				+ "&rateType.eq=makeCash&lastActualForDate.eq=true"
				+ "&clientType.eq=standardCC";
		
		URI uri = UriComponentsBuilder.fromHttpUrl(url)
		        .queryParam("date.lte", ztd)
		        .encode(StandardCharsets.UTF_8)
		        .build()
		        .toUri();
		
		ResponseEntity<String> response = restTemplate.getForEntity(uri, 
				String.class);
		return response;
	}
	private List<ResponseEntity<String>> getJsonTinkoff() {
		List<ResponseEntity<String>>rt=new ArrayList<>();
		RestTemplate restTemplate = new RestTemplate();
		String url="https://api.tinkoff.ru/v1/currency_rates?from=USD&to=RUB";
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class );
		rt.add(response);
		url="https://api.tinkoff.ru/v1/currency_rates?from=EUR&to=RUB";
		response = restTemplate.getForEntity(url, String.class );
		rt.add(response);
		return rt;
	}
	public void parseJsonAlpha(List<BankRate>br) throws JsonProcessingException, IOException{
		ResponseEntity<String>response=
				getJsonAlpha();
		String json, s1, s2;
		JsonNode jsonObject;
		json = response.getBody();
		jsonObject = new ObjectMapper()
			.readTree(json).at("/data/1/rateByClientType/0/ratesByType/0/lastActualRate");
		s1 = new ObjectMapper()
			.writeValueAsString(jsonObject.get("buy").get("originalValue"));
		s2 = new ObjectMapper()
			.writeValueAsString(jsonObject.get("sell").get("originalValue"));
		br.add(new BankRate("Альфабанк",s1,s2));
		jsonObject = new ObjectMapper()
			.readTree(json).at("/data/0/rateByClientType/0/ratesByType/0/lastActualRate");
		s1 = new ObjectMapper()
			.writeValueAsString(jsonObject.get("buy").get("originalValue"));
		s2 = new ObjectMapper()
			.writeValueAsString(jsonObject.get("sell").get("originalValue"));
		br.add(new BankRate("Альфабанк",s1,s2));
	}
	public void parseJsonTinkoff(List<BankRate>br) throws JsonProcessingException, IOException{
		List<ResponseEntity<String>>response=
				getJsonTinkoff();
		String json, s1, s2;
		JsonNode jsonObject;
		for (ResponseEntity<String>r:response) {
			json = r.getBody();
			jsonObject = new ObjectMapper()
				.readTree(json).at("/payload/rates/5");
			s1 = new ObjectMapper()
				.writeValueAsString(jsonObject.get("buy"));
			s2 = new ObjectMapper()
				.writeValueAsString(jsonObject.get("sell"));
			br.add(new BankRate("Тинькофф",s1,s2));
		}
	}
}
