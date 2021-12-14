package beans;

import java.util.Date;

public class Auction {
	
	private int codArt;
	private int idUser;
	private String nomeArt;
	private String description;
	private String image;
	private float startingPrice;
	private float minimumRaise;
	private Date expDate;
	private boolean closed;
	
	public int getCodArt() {
		return codArt;
	}
	public void setCodArt(int codArt) {
		this.codArt = codArt;
	}
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public String getNomeArt() {
		return nomeArt;
	}
	public void setNomeArt(String nomeArt) {
		this.nomeArt = nomeArt;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public float getStartingPrice() {
		return startingPrice;
	}
	public void setStartingPrice(float startingPrice) {
		this.startingPrice = startingPrice;
	}
	public float getMinimumRaise() {
		return minimumRaise;
	}
	public void setMinimumRaise(float minimumRaise) {
		this.minimumRaise = minimumRaise;
	}
	public Date getExpDate() {
		return expDate;
	}
	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}
	public boolean isClosed() {
		return closed;
	}
	public boolean isOpen() {
		return !closed;
	}
	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	public boolean isClosable() {
		Date now = new Date();
		if( expDate!=null && !closed && expDate.before(now)) {
			return true;
		}
		return false;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	

}
