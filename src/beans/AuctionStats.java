package beans;

import java.time.Duration;
import java.util.Date;

public class AuctionStats {
	
	private int codArt;
	private String artName;
	private float maxBid;
	private Date expDate;
	private long daysLeft;
	private long hoursLeft;
	private long minutesLeft;
	
	public AuctionStats() {
		super();
	}
	
	public int getCodArt() {
		return codArt;
	}
	public void setCodArt(int codArt) {
		this.codArt = codArt;
	}
	public String getArtName() {
		return artName;
	}
	public void setArtName(String artName) {
		this.artName = artName;
	}
	public float getMaxBid() {
		return maxBid;
	}
	public void setMaxBid(float maxBid) {
		this.maxBid = maxBid;
	}
	public Date getExpDate() {
		return expDate;
	}
	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}

	public long getDaysLeft() {
		return daysLeft;
	}

	public void calculateDaysLeft(Date date) {
		this.daysLeft = Duration.between(date.toInstant(), expDate.toInstant()).toDays();
	}

	public void calculateDaysPassed(Date date) {
		this.daysLeft = Duration.between(expDate.toInstant(), date.toInstant()).toDays();
	}

	public long getHoursLeft() {
		return hoursLeft;
	}

	public void calculateHoursLeft(Date date) {
		this.hoursLeft = Duration.between(date.toInstant(), expDate.toInstant()).toHours() %24;
	}
	public void calculateHoursPassed(Date date) {
		this.hoursLeft = Duration.between(expDate.toInstant(), date.toInstant()).toHours() %24;
	}
	
	public long getMinutesLeft() {
		return minutesLeft;
	}

	public void calculateMinutesLeft(Date date) {
		this.minutesLeft =  Duration.between(date.toInstant(),expDate.toInstant()).toMinutes()  %60;
	}
	public void calculateMinutesPassed(Date date) {
		this.minutesLeft =  Duration.between(expDate.toInstant(), date.toInstant()).toMinutes()  %60;
	}
}
