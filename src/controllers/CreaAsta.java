package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import DAO.AuctionDAO;
import beans.User;

/**
 * Servlet implementation class CreaAsta
 */
@WebServlet("/CreaAsta")
@MultipartConfig
public class CreaAsta extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreaAsta() {
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
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// recupero dati
		HttpSession session = request.getSession(false);
		
		if (session == null) {
			String path = getServletContext().getContextPath();
			response.sendRedirect(path);
		}
		
		User u = (User) session.getAttribute("user");
		
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		InputStream imageStream = null;
		String mimeType = null;
		Part imagePart = request.getPart("image");
		if(imagePart != null) {
			imageStream = imagePart.getInputStream();
			String filename = imagePart.getSubmittedFileName();
			mimeType = getServletContext().getMimeType(filename);
		}
		String startingPrice = request.getParameter("startingprice");
		String minimumRaise = request.getParameter("minimumraise");
		String expirationDate = request.getParameter("expirationdate");
		// controllo dati
		
		Float startingPriceF = 0F ;
		Float minimumRaiseF = 0F;
		
		try {
			startingPriceF = Float.parseFloat(startingPrice);
			minimumRaiseF = Float.parseFloat(minimumRaise);
		} catch(NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad parameter in auction creation");
			return;
		}
		if (name == null || name.isEmpty() || description == null || description.isEmpty() || 
				imageStream == null || (imageStream.available()==0) || !mimeType.startsWith("image/") || 
				startingPrice == null || startingPrice.isEmpty() || Float.parseFloat(startingPrice)<=0 ||
				minimumRaise == null || minimumRaise.isEmpty() || Float.parseFloat(minimumRaise)<=0 ||
				expirationDate == null || expirationDate.isEmpty()) {
			response.sendError(505, "Parameters incomplete");
			return;	
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"); 
		Date expDate;
		try {
			expDate = sdf.parse(expirationDate);
			Date now = new Date();
			if(expDate.before(now)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Past expiration date in auction creation");
				return;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad parameter for date in auction creation");
			return;
		}
		
		// aggiorno db
		AuctionDAO aDAO = new AuctionDAO(connection);
		try {
			int res = aDAO.createAuction(u.getId(), name, description, imageStream, startingPriceF, minimumRaiseF, expirationDate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database Auction creation");
			return;
		}
		
		
		
		String ctxpath = getServletContext().getContextPath();
		
		String path = ctxpath + "/GoToSell";  //TODO rivedi
		
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
