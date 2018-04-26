package frontend.manager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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
import frontend.model.Item;
import frontend.model.OrderedItem;
import frontend.model.UserOrder;

@ViewScoped
@ManagedBean
public class UserOrderManager {

	private CloseableHttpClient CLIENT = HttpClients.createDefault();

	private UserOrder userOrder;

	private List<UserOrder> userOrders;

	@ManagedProperty("#{basket}")
	private Basket basket;

	@PostConstruct
	public void init() {
		userOrder = new UserOrder();
	}

	public void save() {

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
				userOrder = mapper.readValue((EntityUtils.toString(entity)), UserOrder.class);
			} catch (JsonMappingException e) {
				// TODO: handle exception
			}

		} catch (IOException ex) {
		}

		for (OrderedItem orderedItem : basket.getItems()) {
			orderedItem.setUserOrder(userOrder);
			try {
				CLIENT = HttpClients.createDefault();
				HttpPost request = new HttpPost("http://localhost:8080/backend-0.0.1-SNAPSHOT/rest/addOrderedProduct");
				JSONObject json = new JSONObject();
				json.put("quantity", orderedItem.getQuantity());
				json.put("product", orderedItem.getItem());
				json.put("userOrder", orderedItem.getUserOrder());
				StringEntity params = new StringEntity(json.toString(), "UTF-8");
				request.addHeader("content-type", "application/json;charset=UTF-8");
				request.setEntity(params);
				HttpResponse response = (HttpResponse) CLIENT.execute(request);
				HttpEntity entity = response.getEntity();
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
				try {
					orderedItem = mapper.readValue((EntityUtils.toString(entity)), OrderedItem.class);
				} catch (JsonMappingException e) {
					// TODO: handle exception
				}

			} catch (IOException ex) {
			}
		}

		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Order saved", null));
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
