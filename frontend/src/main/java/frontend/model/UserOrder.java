package frontend.model;

import java.util.Date;
import java.util.List;

public class UserOrder {

	protected long id;
	private String name;
	private String surname;
	private String email;
	private String phone;
	private String address;
	private List<OrderedItem> orderedProducts;
	private Date orderDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<OrderedItem> getOrderedProducts() {
		return orderedProducts;
	}

	public void setOrderedProducts(List<OrderedItem> orderedProducts) {
		this.orderedProducts = orderedProducts;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

}
