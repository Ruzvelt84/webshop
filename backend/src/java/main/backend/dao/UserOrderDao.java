package backend.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import backend.model.UserOrder;

@Stateless
public class UserOrderDao {
	
	@PersistenceContext(unitName = "shop")
	EntityManager entityManager;

	public List<UserOrder> getUserOrders() {
		return entityManager.createQuery("SELECT uo FROM UserOrder uo", UserOrder.class).getResultList();
	}

	public UserOrder getUserOrder(long userOrderId) {
		return entityManager.createQuery("SELECT uo FROM UserOrder uo WHERE uo.id = :ids", UserOrder.class)
				.setParameter("ids", userOrderId).getSingleResult();
	}

	public void addUserOrder(UserOrder userOrder) {	
		entityManager.persist(userOrder);
	}

	public void editUserOrder(UserOrder userOrder) {
		entityManager.merge(userOrder);
	}

	public void deleteUserOrder(UserOrder userOrder) {
		entityManager.remove(entityManager.contains(userOrder) ? userOrder : entityManager.merge(userOrder));
	}
}
