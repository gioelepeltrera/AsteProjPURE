package DAO;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import beans.Auction;
import beans.AuctionStats;

public class AuctionDAO {

	private Connection con;

	public AuctionDAO(Connection connection) {
		this.con = connection;	
	}
	
	public int createAuction(int userId, String name, String descr, InputStream image, Float startPr, Float minRaise, String date) throws SQLException {
		// TODO Auto-generated method stub
		String query = "INSERT into asta (idUser, nomeArt, descrizione, immagine, prezzoIniziale, rialzoMinimo, dataScadenza ) VALUES (?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstatement = null;
		int code = 0;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userId);
			pstatement.setString(2, name);
			pstatement.setString(3, descr);
			pstatement.setBlob(4, image);
			pstatement.setFloat(5, startPr);
			pstatement.setFloat(6, minRaise);
			pstatement.setString(7, date);
			code = pstatement.executeUpdate();
			
		} catch (SQLException e ) {
			e.printStackTrace();
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e ) {
				e.printStackTrace();
			}
		}

		return code;
	}
	
	public List<AuctionStats> getUserAuctions(boolean closed, int userId) throws SQLException {
		
		List<AuctionStats> aStatsResult = new ArrayList<AuctionStats>();
		String query = "SELECT A.codArt as codArt , A.nomeArt as nomeArt , O.valore as offertaMax, A.dataScadenza as expDate "
				+ "FROM asta as A LEFT outer JOIN offerta as O on A.codArt = O.codArt "
				+ "WHERE A.idUser = ? and A.chiusa = ? AND (O.valore >= ALL (SELECT O2.valore "
				+ "		FROM offerta as O2 "
				+ "		WHERE O2.codArt = A.codArt "
				+ "        ) ) "
				+ "ORDER BY A.dataScadenza ASC "
				+ "; ";
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userId);
			pstatement.setBoolean(2, closed);
			
			try(ResultSet result= pstatement.executeQuery();){
				while(result.next()){
					AuctionStats aStat = new AuctionStats();
					aStat.setCodArt(result.getInt("codArt"));
					aStat.setArtName(result.getString("nomeArt"));
					//if(result.getObject("offertaMax")!= null)
					aStat.setMaxBid(result.getFloat("offertaMax"));
					Timestamp ts = result.getTimestamp("expDate");
					aStat.setExpDate(new Date(ts.getTime()));
					aStatsResult.add(aStat);
				}
			}
			
		} catch (SQLException e ) {
			e.printStackTrace();
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e ) {
				e.printStackTrace();
			}
		}
		
		return aStatsResult;
	}

	public List<AuctionStats> searchAuction(String keyword, int userId) throws SQLException {
		List<AuctionStats> auctionsStats = new ArrayList<AuctionStats>();
		String query = "SELECT A.codArt as codArt , A.nomeArt as nomeArt , O.valore as offertaMax, A.dataScadenza as expDate "
				+ "FROM asta as A LEFT outer JOIN offerta as O on A.codArt = O.codArt "
				+ " WHERE A.chiusa = FALSE AND (O.valore >= ALL (SELECT O2.valore "
				+ "		FROM offerta as O2 "
				+ "		WHERE O2.codArt = A.codArt "
				+ "        ) ) AND (A.nomeArt LIKE CONCAT('%', ?,'%')  OR A.descrizione LIKE  CONCAT('%', ?,'%') ) "
				+ " AND A.dataScadenza > NOW() AND A.idUser <> ? "
				+ " ORDER BY A.dataScadenza DESC "
				+ "; ";
				PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, keyword);
			pstatement.setString(2, keyword);
			pstatement.setInt(3, userId);
			
			
			try(ResultSet result= pstatement.executeQuery();){
				while(result.next()){
					AuctionStats aStat = new AuctionStats();
					aStat.setCodArt(result.getInt("codArt"));
					aStat.setArtName(result.getString("nomeArt"));
					aStat.setMaxBid(result.getFloat("offertaMax"));
					Timestamp ts = result.getTimestamp("expDate");
					aStat.setExpDate(new Date(ts.getTime()));
					auctionsStats.add(aStat);

				}
			}
			
		} catch (SQLException e ) {
			e.printStackTrace();
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e ) {
				e.printStackTrace();
			}
		}

		return auctionsStats;
	}

	public Auction getAstaInfo(int codArt) throws SQLException{
		Auction auction = null;
		String query = "SELECT A.codArt as codArt, A.idUser as idUser, A.nomeArt as nomeArt, A.descrizione as descrizione, A.immagine as immagine, A.prezzoIniziale as prezzoIniziale, A.rialzoMinimo as rialzoMinimo, A.dataScadenza as dataScadenza, A.chiusa as chiusa"
				+ "	 FROM asta as A "
				+ "  WHERE A.codArt = ?"
				+ "  ;";
		PreparedStatement pstatement = null;

		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, codArt);
			
			try(ResultSet result= pstatement.executeQuery();){
				while(result.next()){
					auction = new Auction();
					auction.setCodArt(result.getInt("codArt"));
					auction.setIdUser(result.getInt("idUser"));
					auction.setNomeArt(result.getString("nomeArt"));
					auction.setDescription(result.getString("descrizione"));
					byte[] imgData = result.getBytes("immagine");
					String encodedImg = Base64.getEncoder().encodeToString(imgData);
					auction.setImage(encodedImg);
					auction.setStartingPrice(result.getFloat("prezzoIniziale"));
					auction.setMinimumRaise(result.getFloat("rialzoMinimo"));
					Timestamp ts = result.getTimestamp("dataScadenza");
					auction.setExpDate(new Date(ts.getTime()));
					auction.setClosed(result.getBoolean("chiusa"));
				}
			}
			
		} catch (SQLException e ) {
			e.printStackTrace();
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e ) {
				e.printStackTrace();
			}
		}
		
		return auction;
	}

	public int closeAuction(int codArt) throws SQLException {
		// TODO Auto-generated method stub
		int code  = 0;
		String query = "UPDATE asta SET chiusa = 1 WHERE codArt = ? ";
		PreparedStatement pstatement = null;
		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, codArt);
			code = pstatement.executeUpdate();
			
		} catch (SQLException e ) {
			e.printStackTrace();
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e ) {
				e.printStackTrace();
			}
		}

	
		return code;
	}
	
public List<AuctionStats> getWonUserAuctions(int userId) throws SQLException {
		
		List<AuctionStats> aStatsResult = new ArrayList<AuctionStats>();
		String query = "SELECT A.codArt as codArt , A.nomeArt as nomeArt , O.valore as offertaMax, A.dataScadenza as expDate "
				+ "FROM asta as A JOIN offerta as O on A.codArt = O.codArt "
				+ "WHERE O.idUser = ? and A.chiusa = 1 AND (O.valore >= ALL (SELECT O2.valore "
				+ "		FROM offerta as O2 "
				+ "		WHERE O2.codArt = A.codArt "
				+ "        ) ) "
				+ "ORDER BY A.dataScadenza ASC "
				+ "; ";
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userId);
			
			try(ResultSet result= pstatement.executeQuery();){
				while(result.next()){
					AuctionStats aStat = new AuctionStats();
					aStat.setCodArt(result.getInt("codArt"));
					aStat.setArtName(result.getString("nomeArt"));
					//if(result.getObject("offertaMax")!= null)
					aStat.setMaxBid(result.getFloat("offertaMax"));
					Timestamp ts = result.getTimestamp("expDate");
					aStat.setExpDate(new Date(ts.getTime()));
					aStatsResult.add(aStat);
				}
			}
			
		} catch (SQLException e ) {
			e.printStackTrace();
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e ) {
				e.printStackTrace();
			}
		}
		
		return aStatsResult;
	}

}
