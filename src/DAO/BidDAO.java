package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import beans.AuctionBid;
import beans.HighestBidder;

public class BidDAO {

	private Connection con;
	
	public BidDAO(Connection connection) {
		this.con = connection;	
	}

	public List<AuctionBid> getAuctionBids(int codArt) throws SQLException{
		// TODO Auto-generated method stub
		List<AuctionBid> aBids = new ArrayList<AuctionBid>();
		String query = "SELECT U.username as username, O.valore as value , O.data as date "
				+ "  FROM offerta as O JOIN asta as A on O.codArt = A.codArt JOIN user AS U on O.idUser = U.id "
				+ "	 WHERE O.codArt = ? "
				+ "  ORDER BY O.data DESC"
				+ ";";
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, codArt);
			
			try(ResultSet result= pstatement.executeQuery();){
				while(result.next()){
					AuctionBid aBid = new AuctionBid();
					aBid.setUsername(result.getString("username"));
					aBid.setValue(result.getFloat("value"));
					Timestamp ts = result.getTimestamp("date");
					aBid.setDate(new Date(ts.getTime()));
					aBids.add(aBid);
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
		return aBids;
	}

	public HighestBidder getAuctionHighestBidder(int codArt)  throws SQLException{
		HighestBidder wU = null;
		String query = "SELECT U.id as userId, U.username as username, U.datiSpedizione as datiSped, A.codArt as codArt, O.valore as offertaMax "
				+ "     FROM user AS U JOIN offerta as O on U.id = O.idUser JOIN asta as A on O.codArt = A.codArt "
				+ "     WHERE O.codArt = ? AND (O.valore >= ALL ("
				+ "						SELECT O2.valore "
				+ "						FROM offerta AS O2 "
				+ "						WHERE O2.codArt = ? ) "
				+ ");"; 
		
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, codArt);
			pstatement.setInt(2, codArt);
			
			try(ResultSet result= pstatement.executeQuery();){
				while(result.next()){
					wU = new HighestBidder();
					wU.setUserId(result.getInt("userId"));
					wU.setCodArt(result.getInt("codArt"));
					wU.setUsername(result.getString("username"));
					wU.setDatiSpedizione(result.getString("datiSped"));
					wU.setFinalBid(result.getFloat("offertaMax"));
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
		
		
		return wU;
	}

	public int makeBid(int userId, int codArt, float valore) throws SQLException {
		int code = 0;
		String query = "INSERT into offerta (idUser, codArt, data, valore ) VALUES (?, ?, NOW() , ?)";
		PreparedStatement pstatement = null;

		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userId);
			pstatement.setInt(2, codArt);
			pstatement.setFloat(3, valore);
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
	
	
}
