package beans;

public class HighestBidder {
	
	private int userId;
	private int codArt;
	private String username;
	private String DatiSpedizione;
	private float finalBid;
	
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getCodArt() {
		return codArt;
	}
	public void setCodArt(int codArt) {
		this.codArt = codArt;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getDatiSpedizione() {
		return DatiSpedizione;
	}
	public void setDatiSpedizione(String datiSpedizione) {
		DatiSpedizione = datiSpedizione;
	}
	public float getFinalBid() {
		return finalBid;
	}
	public void setFinalBid(float finalBid) {
		this.finalBid = finalBid;
	}
	
	
	
}
