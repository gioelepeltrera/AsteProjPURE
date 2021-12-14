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

import DAO.AuctionDAO;
import beans.Auction;

/**
 * Servlet implementation class SearchAuction
 */
@WebServlet("/SearchAuction")
public class SearchAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchAuction() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String keyword = request.getParameter("keyword");
		if( keyword == null || keyword.isEmpty()) {
			response.sendError(505, "Parameters incomplete");
			return;
		}
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToBuy?keyword="+keyword;
		response.sendRedirect(path);		

	}

}
