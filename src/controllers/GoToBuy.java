package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
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
import beans.Auction;
import beans.AuctionStats;
import beans.User;

/**
 * Servlet implementation class GoToBuy
 */
@WebServlet("/GoToBuy")
public class GoToBuy extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToBuy() {
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

		HttpSession s = request.getSession(false);
		User u = (User) s.getAttribute("user");
		String keyword = request.getParameter("keyword");

		List<AuctionStats> searchResult = null;
		List<AuctionStats> wonAuctions = null;
		AuctionDAO aDAO = new  AuctionDAO(connection);

		if( keyword != null && !keyword.isEmpty()) {
			
			try {
				searchResult = aDAO.searchAuction(keyword, u.getId());
				for(AuctionStats as : searchResult) {
					Date now = new Date();
					as.calculateDaysLeft(now);
					as.calculateHoursLeft(now);
					as.calculateMinutesLeft(now);
				}
			}  catch (SQLException e) {
				// TODO Auto-generated catch block
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database Auctions search");
				return;
			}
		}
		try {
			wonAuctions = aDAO.getWonUserAuctions(u.getId());
		}  catch (SQLException e) {
			// TODO Auto-generated catch block
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database Auctions Won by user");
			return;
		}
		
		
		String path = "/WEB-INF/Compro.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("searchResult", searchResult );
		ctx.setVariable("wonAuctions", wonAuctions );
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
