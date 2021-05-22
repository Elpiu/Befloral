package it.befloral.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.befloral.beans.Address;
import it.befloral.beans.User;
import it.befloral.model.AddressDAO;
import it.befloral.model.OrderDAO;
import it.befloral.model.UserDAO;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/User")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println(this.getClass().getSimpleName() + " get:" + request.getParameter("action"));
		User user = (User) request.getSession().getAttribute("user");
		request.setAttribute("active", "User");
		var action = request.getParameter("action");
		if (user != null) {
			OrderDAO orderDao = new OrderDAO();
			try {
				request.getSession().setAttribute("orders", orderDao.doRetriveByUser(user));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(action == null) {
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/users/view.jsp");
				dispatcher.forward(request, response);
			} else if (action.equals("viewAddresses")) {
				System.out.println("addresses");
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/users/addresses.jsp");
				dispatcher.forward(request, response);
			} else if (action.equals("createAddress")) {
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/users/createAddress.jsp");
				dispatcher.forward(request, response);
			}
			return;
		} else {
			response.sendRedirect("Login");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println(this.getClass().getSimpleName() + " post:" + request.getParameter("action"));
		User user = (User) request.getSession().getAttribute("user");
		request.setAttribute("active", "User");
		var action = request.getParameter("action");
		if (user != null) {
			if(action == null) {
				response.sendError(500);
				return;
			}
			
			if(action.equals("createAddress")) {
				Address address = new Address();
				address.setFirstName(request.getParameter("firstName"));
				address.setLastName(request.getParameter("lastName"));
				address.setAddress(request.getParameter("address"));
				address.setPostalCode(request.getParameter("postalCode"));
				address.setCity(request.getParameter("city"));
				address.setProvince(request.getParameter("province"));
				address.setPhone(request.getParameter("phone"));
				address.setInfo(request.getParameter("info"));
				address.setAlias(request.getParameter("alias"));
				UserDAO userDAO = new UserDAO();
				try {
					userDAO.doSaveAddress(user, address);
					user.addAddress(address);
					request.getSession().setAttribute("user", user);
				} catch (SQLException e) {
					e.printStackTrace();
					response.sendError(500);
				}
				response.sendRedirect("User");
			} else if (action.equals("setPreferredAddress")) {
				UserDAO userDAO = new UserDAO();
				int id = Integer.parseInt(request.getParameter("id"));
				if(user.setPreferredAddress(id)) {
					try {
						userDAO.doSetPreferredAddress(user, id);
					} catch (SQLException e) {
						e.printStackTrace();
						response.sendError(500);
						return;
					}
				}
				response.sendRedirect("User");
				return;
			}
		}
	}

}
