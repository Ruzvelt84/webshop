package frontend.manager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
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

@ViewScoped
@ManagedBean
public class ItemManager {

	private Item item;
	private List<Item> items;
	private double quantity;

	private CloseableHttpClient CLIENT = HttpClients.createDefault();

	@ManagedProperty("#{basket}")
	private Basket basket;

	@PostConstruct
	public void init() {
		items = new ArrayList<>();
		item = new Item();
		quantity = 1;
	}

	public void removeSelected() {

	}

	public void addItemToBasket() {
		basket.add(item, quantity);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Result:", "Item added to basket"));
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public List<Item> getItems() {

		CLIENT = HttpClients.createDefault();
		HttpGet request = new HttpGet("http://localhost:8080/backend-0.0.1-SNAPSHOT/rest/getProducts");
		request.addHeader("content-type", "application/json;charset=UTF-8");
		request.addHeader("charset", "UTF-8");
		HttpResponse response;

		try {

			response = (HttpResponse) CLIENT.execute(request);
			HttpEntity entity = response.getEntity();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			Item[] model = mapper.readValue((EntityUtils.toString(entity)), Item[].class);

			for (Item item : model) {
				items.add(item);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return items;
	}

	public void loadOne() {
		CLIENT = HttpClients.createDefault();

		try {

			URIBuilder builder = new URIBuilder("http://localhost:8080/backend-0.0.1-SNAPSHOT/rest/getProduct");
			builder.setParameter("id", String.valueOf(item.getId()));
			HttpPost request = new HttpPost(builder.build());
			request.addHeader("content-type", "application/json;charset=UTF-8");
			request.addHeader("charset", "UTF-8");
			HttpResponse response;
			response = (HttpResponse) CLIENT.execute(request);
			HttpEntity entity = response.getEntity();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			item = mapper.readValue((EntityUtils.toString(entity)), Item.class);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void loadAll() {
		// items = itemService.findAll();
	}

	public void save() {

		try {
			CLIENT = HttpClients.createDefault();
			HttpPost request = new HttpPost("http://localhost:8080/backend-0.0.1-SNAPSHOT/rest/addProduct");
			JSONObject json = new JSONObject();
			json.put("description", item.getDescription());
			json.put("price", item.getPrice());
			json.put("stock", item.getStock());
			json.put("image", item.getImage());
			StringEntity params = new StringEntity(json.toString(), "UTF-8");
			request.addHeader("content-type", "application/json;charset=UTF-8");
			request.setEntity(params);
			HttpResponse response = (HttpResponse) CLIENT.execute(request);
			HttpEntity entity = response.getEntity();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			try {
				item = mapper.readValue((EntityUtils.toString(entity)), Item.class);
			} catch (JsonMappingException e) {
				// TODO: handle exception
			}
			
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Data succesfully saved"));

		} catch (IOException ex) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Data unsuccesfully saved"));
			ex.printStackTrace();
		}
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Basket getBasket() {
		return basket;
	}

	public void setBasket(Basket basket) {
		this.basket = basket;
	}
}
