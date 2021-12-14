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
 * Servlet implementation class GoToBid
 */
@WebServlet("/GoToBid")
public class GoToBid extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToBid() {
        super();
        // TODO Auto-generated constructor stub
    }
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		
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
		
		String codArt = request.getParameter("codArt");
		HttpSession s = request.getSession(false);
		User u = (User) s.getAttribute("user");
		
		AuctionDAO aDAO = new AuctionDAO(connection);
		BidDAO bidDAO = new BidDAO(connection);
		Auction auction = new Auction();
		List<AuctionBid> aucBids = null;
		HighestBidder wU = new HighestBidder();
		float min = 0F;
		float minRaise = 0.5F;
		
		try {
			auction = aDAO.getAstaInfo(Integer.parseInt(codArt));
			if(auction==null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incompatible parameters");
				return;
			}
			if(u.getId() == auction.getIdUser()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters, cannot access to this page");
				return;
			}
			if(auction.isOpen()) {
				aucBids = bidDAO.getAuctionBids(Integer.parseInt(codArt));
				BidDAO bDAO = new BidDAO(connection);
				HighestBidder hB = bDAO.getAuctionHighestBidder( Integer.parseInt(codArt));
				min = auction.getMinimumRaise();
				if(hB != null) {
					min += hB.getFinalBid();
				}else {
					min = auction.getStartingPrice();
				}
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters, cannot access to this page");
				return;
			}
			
		} catch(SQLException e ) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database in makebid");
			return;
		} catch(NumberFormatException e ) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incompatible parameters");

		}

		String path = "/WEB-INF/Offerta.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("asta", auction);
		ctx.setVariable("auctionBids", aucBids);
		ctx.setVariable("minBid", min);
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
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
