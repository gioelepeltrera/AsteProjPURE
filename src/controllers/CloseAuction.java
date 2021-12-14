package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import DAO.AuctionDAO;
import beans.Auction;
import beans.User;

/**
 * Servlet implementation class CloseAuction
 */
@WebServlet("/CloseAuction")
public class CloseAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CloseAuction() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
    	try {
    		ServletContext context = getServletContext();
    		String driver = context.getInitParameter("dbDriver");
    		String url = context.getInitParameter("dbUrl");
    		String user = context.getInitParameter("dbUser");
    		String password = context.getInitParameter("dbPassword");
    		Class.forName(driver);
    		connection = DriverManager.getConnection(url, user, password);
    		
    		
    		
    	} catch (ClassNotFoundException e) {
    		throw new UnavailableException("Couldn't load db driver");
    	} catch (SQLException e) {
    		throw new UnavailableException("Couldn't get db connection");
    	}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String codArt = request.getParameter("codArt");
		HttpSession s = request.getSession(false);
		User u = (User) s.getAttribute("user");
		
		try {
			AuctionDAO aDAO = new AuctionDAO(connection);
			Auction auction = aDAO .getAstaInfo(Integer.parseInt(codArt));
			if(auction == null || u.getId() != auction.getIdUser()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters, cannot access to this page");
				return;
			}
			if(auction.isOpen() && auction.isClosable()) {
				int code = aDAO.closeAuction(Integer.parseInt(codArt));
				if(code != 1) {
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in database closing auction");
					return;
				}
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters, auction already closed");
			}
			
		} catch(SQLException e ) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database CloseAuction");
			return;
		} catch(NumberFormatException e ) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incompatible parameters");

		}
		

		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GetAuctionInfo?codArt="+codArt;
		response.sendRedirect(path);
	}

	public void destroy() {
		try {
			if (connection!=null)
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
