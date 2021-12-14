package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import DAO.AuctionDAO;
import DAO.BidDAO;
import beans.Auction;
import beans.AuctionBid;
import beans.User;
import beans.HighestBidder;

/**
 * Servlet implementation class MakeBid
 */
@WebServlet("/MakeBid")
public class MakeBid extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MakeBid() {
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

		String codArt = request.getParameter("codArt");
		String value = request.getParameter("value");
		HttpSession s = request.getSession(false);
		User u = (User) s.getAttribute("user");
		
		try {
			AuctionDAO aDAO = new AuctionDAO(connection);
			Auction auction = aDAO .getAstaInfo(Integer.parseInt(codArt));
			if(auction == null || u.getId() == auction.getIdUser() || value ==null ) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters, cannot access to this page");
				return;
			}
			if(auction.isOpen() && !auction.isClosable()) {
				
				BidDAO bDAO = new BidDAO(connection);
				HighestBidder hB = bDAO.getAuctionHighestBidder( Integer.parseInt(codArt));
				float min = auction.getMinimumRaise();
				float minRaise = min;
				if(hB != null) {
					min += hB.getFinalBid();
				}else {
					min = auction.getStartingPrice();
				}
				if(Float.parseFloat(value) >= min) {
					int code = bDAO.makeBid(u.getId(), Integer.parseInt(codArt), Float.parseFloat(value));
					if(code != 1) {
						response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in database making bid");
						return;
					}
				} else {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters, value too low");
					return;
				}
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters, auction is closed");
				return;
			}
			
		} catch(SQLException e ) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database @Makebid");
			return;
		} catch(NumberFormatException e ) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incompatible parameters");
			return;
		}
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToBid?codArt="+codArt;
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
