package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.User;

public class UserDAO {
	
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
		
	}
	
	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT id, username, password FROM user WHERE username = ? AND password = ?";
		try(PreparedStatement pstatement = con.prepareStatement(query);){
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try(ResultSet result = pstatement.executeQuery();){
				if(!result.isBeforeFirst())
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("id"));
					user.setUsername(result.getString("username"));
					return user;
				}
			}
				
		}
	}

	public User createUser(String usrn, String pwd, String addr) throws SQLException {
		// TODO Auto-generated method stub
		String query = "INSERT into user (username, password, datiSpedizione ) VALUES (?, ?, ?)";
		PreparedStatement pstatement = null;
		int code = 0;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			pstatement.setString(3, addr);
			code = pstatement.executeUpdate();
			if (code == 1) {
				return checkCredentials(usrn, pwd);
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

		return null;
	}

}

