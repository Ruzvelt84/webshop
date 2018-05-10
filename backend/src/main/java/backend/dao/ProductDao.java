package backend.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import backend.model.Product;

@Stateless
public class ProductDao {

	@PersistenceContext(unitName = "shop")
	EntityManager entityManager;

	public List<Product> getProducts() {
		return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
	}

	public Product getProduct(long productId) {
		return entityManager.createQuery("SELECT p FROM Product p WHERE p.id = :ids", Product.class)
				.setParameter("ids", productId).getSingleResult();
	}

	public Product getProductByName(String productName) {
		Product product;
		try {
			product = entityManager
					.createQuery("SELECT p FROM Product p WHERE p.description = :description", Product.class)
					.setParameter("description", productName).getSingleResult();
		} catch (NoResultException e) {
			product = null;
		}
		return product;
	}

	public void addProduct(Product product) {
		entityManager.persist(product);
	}

	public void editProduct(Product product) {
		entityManager.merge(product);
	}

	public void deleteProduct(Product product) {
		entityManager.remove(entityManager.contains(product) ? product : entityManager.merge(product));
	}
}