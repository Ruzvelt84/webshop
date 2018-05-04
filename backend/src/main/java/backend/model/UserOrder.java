package backend.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_order")
public class UserOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;

	@Column(name = "name", columnDefinition = "VARCHAR(50)", nullable = false)
	@NotNull(message = "{emptyName}")
	private String name;

	@Column(name = "surname", columnDefinition = "VARCHAR(50)", nullable = false)
	private String surname;

	@Column(name = "email", columnDefinition = "VARCHAR(100)", nullable = false)
	private String email;

	@Column(name = "phone", columnDefinition = "VARCHAR(50)", nullable = false)
	private String phone;

	@Column(name = "address", columnDefinition = "VARCHAR(100)", nullable = false)
	private String address;

	@Column(name = "order_date", length = 20, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
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

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

}
