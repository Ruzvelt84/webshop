package backend.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import backend.model.PaymentMethod;

@Stateless
public class PaymentMethodDao {
	@PersistenceContext(unitName = "shop")
	EntityManager entityManager;

	public List<PaymentMethod> getPaymentMethods() {
		return entityManager.createQuery("SELECT pm FROM PaymentMethod pm", PaymentMethod.class).getResultList();
	}

	public PaymentMethod getPaymentMethod(long paymentMethodId) {
		return entityManager.createQuery("SELECT pm FROM PaymentMethod pm WHERE pm.id = :ids", PaymentMethod.class)
				.setParameter("ids", paymentMethodId).getSingleResult();
	}

	public PaymentMethod getPaymentMethodByName(String paymentMethodName) {
		PaymentMethod paymentMethod;
		try {
			paymentMethod = entityManager
					.createQuery("SELECT pm FROM PaymentMethod pm WHERE pm.name = :name", PaymentMethod.class)
					.setParameter("name", paymentMethodName).getSingleResult();
		} catch (NoResultException e) {
			paymentMethod = null;
		}
		return paymentMethod;
	}

	public void addPaymentMethod(PaymentMethod paymentMethod) {
		entityManager.persist(paymentMethod);
	}

	public void editPaymentMethod(PaymentMethod paymentMethod) {
		entityManager.merge(paymentMethod);
	}

	public void deletePaymentMethod(PaymentMethod paymentMethod) {
		entityManager
				.remove(entityManager.contains(paymentMethod) ? paymentMethod : entityManager.merge(paymentMethod));
	}
}
