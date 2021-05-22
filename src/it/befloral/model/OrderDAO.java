package it.befloral.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import it.befloral.beans.Order;
import it.befloral.beans.OrderItem;
import it.befloral.beans.User;

public class OrderDAO implements GenericDAO<Order> {
	private static DataSource ds;
	private static final String TABLE_NAME = "orders";

	static {
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("jdbc/befloral");
		} catch (NamingException e) {
			System.out.println("Error:" + e.getMessage());
		}
	}

	@Override
	public Collection<Order> doRetrieveAll(String order) throws SQLException {
		Collection<Order> orders = new LinkedList<>();
		String selectSQL = "SELECT * FROM " + TABLE_NAME;
		try (var conn = ds.getConnection()) {
			try (var stmt = conn.prepareStatement(selectSQL)) {
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Order bean = new Order();
					bean.setId(rs.getInt("id"));
					bean.setDestination(rs.getString("destination"));
					bean.setTotalProducts(rs.getInt("totalProducts"));
					bean.setTotalPaid(rs.getDouble("totalPaid"));
					bean.setStatus(rs.getString("status"));
					bean.setGift(rs.getBoolean("gift"));
					bean.setGiftMessage(rs.getString("giftMessage"));
					bean.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
					orders.add(bean);
				}
			}
		}
		return orders;
	}

	@Override
	public Order doRetriveByKey(int code) throws SQLException {
		String selectSQL = "SELECT o.id AS orderId, o.uid, o.destination, o.totalProducts, o.totalPaid, o.trackNumber, o.gift, o.giftMessage, o.createdAtTime,s.* FROM "+ TABLE_NAME +" o LEFT JOIN order_items  s ON  s.oid = o.id WHERE o.id = ?";
		Order order = new Order();
		try (var conn = ds.getConnection()) {
			try (var stmt = conn.prepareStatement(selectSQL)) {
				stmt.setInt(1, code);
				System.out.println(stmt);
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
					order.setId(rs.getInt("orderId"));
					order.setDestination(rs.getString("destination"));
					order.setTotalProducts(rs.getInt("totalProducts"));
					order.setTotalPaid(rs.getDouble("totalPaid"));
					order.setGift(rs.getBoolean("gift"));
					order.setGiftMessage(rs.getString("giftMessage"));
				}
				do {
					OrderItem item =new OrderItem();
					item.setId(rs.getInt("id"));
					item.setOid(rs.getInt("oid"));
					item.setName(rs.getString("name"));
					item.setDescription(rs.getString("description"));
					item.setShortDescription(rs.getString("shortDescription"));
					item.setPrice(rs.getDouble("price"));
					item.setWeight(rs.getDouble("weight"));
					item.setDiscount(rs.getDouble("discount"));
					item.setQuantity(rs.getInt("quantity"));
					order.addItem(item);
				}while(rs.next());
			}
		}
		return order;
	}

	@Override
	public void doSave(Order dao) throws SQLException {
		String insertOrder = "INSERT INTO orders (`uid`, `destination`, `totalProducts`, `totalPaid`, `trackNumber`, `gift`, `giftMessage`) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		String insertItem = "INSERT INTO order_items "
				+ "(`oid`, `name`, `description`, `shortDescription`, `price`, `weight`, `discount`, `quantity`) "
				+ "VALUES (? , ?, ?, ?, ?, ?, ?, ?)";
		var conn = ds.getConnection();
		try {
			var stmt = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, dao.getUser().getId());
			stmt.setString(2, dao.getDestination());
			stmt.setInt(3, dao.getTotalProducts());
			stmt.setDouble(4, dao.getTotalPaid());
			stmt.setString(5, dao.getTrackNumber());
			stmt.setBoolean(6, dao.isGift());
			stmt.setString(7, dao.getGiftMessage());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int lastInsertedId = rs.getInt(1);
				for (OrderItem item : dao.getItems()) {
					var stmt2 = conn.prepareStatement(insertItem);
					stmt2.setInt(1, lastInsertedId);
					stmt2.setString(2, item.getName());
					stmt2.setString(3, item.getDescription());
					stmt2.setString(4, item.getShortDescription());
					stmt2.setDouble(5, item.getPrice());
					stmt2.setDouble(6, item.getWeight());
					stmt2.setDouble(7, item.getDiscount());
					stmt2.setInt(8, item.getQuantity());
					stmt2.execute();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
		}

	}

	@Override
	public int doUpdate(Order dao) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean doDelete(int code) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public Collection<Order> doRetriveByUser(User userBean) throws SQLException {
		Collection<Order> orders = new LinkedList<Order>();
		String selectSQL = "SELECT * FROM " + TABLE_NAME;
		try (var conn = ds.getConnection()) {
			try (var stmt = conn.prepareStatement(selectSQL)) {
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Order bean = new Order();
					bean.setId(rs.getInt("id"));
					bean.setDestination(rs.getString("destination"));
					bean.setTotalProducts(rs.getInt("totalProducts"));
					bean.setTotalPaid(rs.getDouble("totalPaid"));
					bean.setStatus(rs.getString("status"));
					bean.setGift(rs.getBoolean("gift"));
					bean.setGiftMessage(rs.getString("giftMessage"));
					bean.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
					orders.add(bean);
				}
			}
		}
		return orders;
	}

}
