package backend.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import backend.model.OrderedProduct;

@Stateless
public class OrderedProductDao {
	
	@PersistenceContext(unitName = "shop")
	EntityManager entityManager;

	public List<OrderedProduct> getUserOrders() {
		return entityManager.createQuery("SELECT op FROM OrderedProduct op", OrderedProduct.class).getResultList();
	}

	public OrderedProduct getOrderedProduct(long orderedProductId) {
		return entityManager.createQuery("SELECT op FROM OrderedProduct op WHERE op.id = :ids", OrderedProduct.class)
				.setParameter("ids", orderedProductId).getSingleResult();
	}

	public void addOrderedProduct(OrderedProduct orderedProduct) {				
		entityManager.persist(orderedProduct);
	}

	public void editOrderedProduct(OrderedProduct orderedProduct) {
		entityManager.merge(orderedProduct);
	}

	public void deleteUserOrder(OrderedProduct orderedProduct) {
		entityManager.remove(entityManager.contains(orderedProduct) ? orderedProduct : entityManager.merge(orderedProduct));
	}
}
