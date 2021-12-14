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
 * Servlet implementation class GoToSell
 */
@WebServlet("/GoToSell")
public class GoToSell extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToSell() {
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
		
		HttpSession s = request.getSession(false);
		User u = (User) s.getAttribute("user");
		AuctionDAO aDAO = new AuctionDAO(connection);
		List<AuctionStats> openAuctions = null;
		List<AuctionStats> closedAuctions = null;
		try {
			openAuctions = aDAO.getUserAuctions(false, u.getId()); // false -> not closed
			closedAuctions = aDAO.getUserAuctions(true, u.getId());
			
			for(AuctionStats as : openAuctions) {
				as.calculateDaysLeft(u.getLoginTime());
				as.calculateHoursLeft(u.getLoginTime());
				as.calculateMinutesLeft(u.getLoginTime());
			}
			for(AuctionStats as : closedAuctions) {
				as.calculateDaysPassed(u.getLoginTime());
				as.calculateHoursPassed(u.getLoginTime());
				as.calculateMinutesPassed(u.getLoginTime());
			}
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
		String path = "/WEB-INF/Vendo.html";
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("openAuctions", openAuctions);
		ctx.setVariable("closedAuctions", closedAuctions);
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
