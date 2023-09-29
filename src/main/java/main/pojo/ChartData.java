package main.pojo;

public class ChartData {
	private String x;
	private String value;
	public ChartData(String x, String value) {
		this.x = x;
		this.value=value;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
