package frontend.manager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.primefaces.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import frontend.domain.Basket;
import frontend.model.OrderedItem;
import frontend.model.PaymentMethod;
import frontend.model.UserOrder;

@ViewScoped
@ManagedBean
public class UserOrderManager {

	private CloseableHttpClient CLIENT = HttpClients.createDefault();

	private UserOrder userOrder;

	private PaymentMethod paymentMethod;

	private List<UserOrder> userOrders;
	List<PaymentMethod> paymentMethods;

	@ManagedProperty("#{basket}")
	private Basket basket;

	@PostConstruct
	public void init() {
		userOrder = new UserOrder();
		paymentMethod = new PaymentMethod();
		paymentMethods = new ArrayList<>();
	}

	public void save() {
		if (randomPayment()) {
			UserOrder uOrder = null;
			try {
				CLIENT = HttpClients.createDefault();
				HttpPost request = new HttpPost("http://localhost:8080/backend-0.0.1-SNAPSHOT/rest/addUserOrder");
				JSONObject json = new JSONObject();
				json.put("address", userOrder.getAddress());
				json.put("email", userOrder.getEmail());
				json.put("phone", userOrder.getPhone());
				json.put("name", userOrder.getName());
				json.put("surname", userOrder.getSurname());
				json.put("orderDate", new SimpleDateFormat("yyyy-MM-dd").format(userOrder.getOrderDate()));

				StringEntity params = new StringEntity(json.toString(), "UTF-8");
				request.addHeader("content-type", "application/json;charset=UTF-8");
				request.setEntity(params);
				HttpResponse response = (HttpResponse) CLIENT.execute(request);
				HttpEntity entity = response.getEntity();
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);

				try {
					uOrder = mapper.readValue((EntityUtils.toString(entity)), UserOrder.class);
				} catch (JsonMappingException e) {
					e.printStackTrace();
				}

			} catch (IOException ex) {
			}

			for (OrderedItem orderedItem : basket.getItems()) {
				orderedItem.setUserOrder(uOrder);
				try {
					CLIENT = HttpClients.createDefault();
					HttpPost request = new HttpPost(
							"http://localhost:8080/backend-0.0.1-SNAPSHOT/rest/addOrderedProduct");
					ObjectMapper mapper = new ObjectMapper();

					JSONObject json = new JSONObject();

					JSONObject item = new JSONObject();
					item.put("id", orderedItem.getItem().getId());
					item.put("description", orderedItem.getItem().getDescription());
					item.put("price", orderedItem.getItem().getPrice());
					item.put("stock", orderedItem.getItem().getStock());
					item.put("image", orderedItem.getItem().getImage());

					JSONObject uo = new JSONObject();
					uo.put("id", orderedItem.getUserOrder().getId());
					uo.put("address", orderedItem.getUserOrder().getAddress());
					uo.put("email", orderedItem.getUserOrder().getEmail());
					uo.put("phone", orderedItem.getUserOrder().getPhone());
					uo.put("name", orderedItem.getUserOrder().getName());
					uo.put("surname", orderedItem.getUserOrder().getSurname());
					uo.put("orderDate",
							new SimpleDateFormat("yyyy-MM-dd").format(orderedItem.getUserOrder().getOrderDate()));

					json.put("quantity", orderedItem.getQuantity());
					json.put("product", item);
					json.put("userOrder", uo);

					StringEntity params = new StringEntity(json.toString(), "UTF-8");
					request.addHeader("content-type", "application/json;charset=UTF-8");
					request.setEntity(params);
					HttpResponse response = (HttpResponse) CLIENT.execute(request);
					HttpEntity entity = response.getEntity();
					mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
					try {
						orderedItem = mapper.readValue((EntityUtils.toString(entity)), OrderedItem.class);
					} catch (JsonMappingException e) {
						// TODO: handle exception
					}

				} catch (IOException ex) {
				}

				try {
					CLIENT = HttpClients.createDefault();
					HttpPost request = new HttpPost("http://localhost:8080/backend-0.0.1-SNAPSHOT/rest/editProduct");
					ObjectMapper mapper = new ObjectMapper();

					JSONObject item = new JSONObject();
					item.put("id", orderedItem.getItem().getId());
					item.put("description", orderedItem.getItem().getDescription());
					item.put("price", orderedItem.getItem().getPrice());
					item.put("stock", orderedItem.getItem().getStock() - orderedItem.getQuantity());
					item.put("image", orderedItem.getItem().getImage());

					StringEntity params = new StringEntity(item.toString(), "UTF-8");
					request.addHeader("content-type", "application/json;charset=UTF-8");
					request.setEntity(params);
					HttpResponse response = (HttpResponse) CLIENT.execute(request);
					HttpEntity entity = response.getEntity();
					mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
					try {
						mapper.readValue((EntityUtils.toString(entity)), OrderedItem.class);
					} catch (JsonMappingException e) {
						// TODO: handle exception
					}

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Payment is OK", null));

			try {
				ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
				ec.redirect("http://localhost:8080/frontend-0.0.1-SNAPSHOT/item/list.xhtml");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			basket.clear();
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Payment is fault, try again", null));
		}

	}

	public List<PaymentMethod> getPaymentMethods() {

		CLIENT = HttpClients.createDefault();
		HttpGet request = new HttpGet("http://localhost:8080/backend-0.0.1-SNAPSHOT/rest/getPaymentMethods");
		request.addHeader("content-type", "application/json;charset=UTF-8");
		request.addHeader("charset", "UTF-8");
		HttpResponse response;

		try {

			response = (HttpResponse) CLIENT.execute(request);
			HttpEntity entity = response.getEntity();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			PaymentMethod[] model = mapper.readValue((EntityUtils.toString(entity)), PaymentMethod[].class);

			for (PaymentMethod paymentMethod : model) {
				paymentMethods.add(paymentMethod);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return paymentMethods;
	}

	public boolean randomPayment() {
		boolean status = false;
		CLIENT = HttpClients.createDefault();
		HttpGet request = new HttpGet("http://localhost:8080/backend-0.0.1-SNAPSHOT/rest/getPaymentStatus");
		request.addHeader("content-type", "application/json;charset=UTF-8");
		request.addHeader("charset", "UTF-8");
		HttpResponse response;

		try {

			response = (HttpResponse) CLIENT.execute(request);
			HttpEntity entity = response.getEntity();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			status = mapper.readValue((EntityUtils.toString(entity)), Boolean.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return status;
	}

	public UserOrder getUserOrder() {
		return userOrder;
	}

	public List<UserOrder> getUserOrders() {
		return userOrders;
	}

	public Basket getBasket() {
		return basket;
	}

	public void setBasket(Basket basket) {
		this.basket = basket;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

}
