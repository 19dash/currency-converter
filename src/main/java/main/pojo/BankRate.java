package main.pojo;


public class BankRate {
	String bank;
	String sell;
	String buy;
	public BankRate(String bank, String sell, String buy) {
		this.bank=bank;
		this.buy=buy;
		this.sell=sell;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getBuy() {
		return buy;
	}
	public void setBuy(String buy) {
		this.buy = buy;
	}
	public String getSell() {
		return sell;
	}
	public void setSell(String sell) {
		this.sell = sell;
	}
	
}
