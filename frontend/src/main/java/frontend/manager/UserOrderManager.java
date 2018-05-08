package frontend.manager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import frontend.model.UserOrder;
import frontend.model.UserOrderResponse;

@ViewScoped
@ManagedBean
public class UserOrderManager {

	private CloseableHttpClient CLIENT = HttpClients.createDefault();

	private UserOrder userOrder;
	private UserOrderResponse userOrderResponse;

	private List<UserOrder> userOrders;

	@ManagedProperty("#{basket}")
	private Basket basket;

	@PostConstruct
	public void init() {
		userOrder = new UserOrder();
	}

	public void save() {

		if (randompayment()) {
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

		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Payment is fault, try again", null));
		}
	}

	public boolean randompayment() {
		Random random = new Random();
		return random.nextBoolean();
	}

	public void loadAll() {
		// userOrders = userOrderService.findAll();
	}

	public void loadAllWithItems() {
		// userOrders = userOrderService.findAllWithItems();
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

}
