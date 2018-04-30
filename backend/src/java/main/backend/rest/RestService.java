package backend.rest;

import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import backend.dao.OrderedProductDao;
import backend.dao.ProductDao;
import backend.dao.UserOrderDao;
import backend.model.OrderedProduct;
import backend.model.Product;
import backend.model.UserOrder;

@Path("/")
@Stateless
public class RestService {

	@EJB
	ProductDao productDao;
	@EJB
	UserOrderDao userOrderDao;
	@EJB
	OrderedProductDao orderedProductDao;

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("getProducts")
	public Response getProducts() {
		HashMap<String, Object> json = null;

		try {
			return Response.ok().entity(productDao.getProducts()).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok().entity(json).build();
		}

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("getProduct")
	public Response getProduct(@QueryParam(value = "id") long productId) {
		HashMap<String, Object> json = null;

		try {
			return Response.ok().entity(productDao.getProduct(productId)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok().entity(json).build();
		}

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("addProduct")
	public Response addProduct(Product product) {
		try {
			System.out.println("item : " + product);
			productDao.addProduct(product);
			return Response.ok().build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}

	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("editProduct")
	public Response updateProduct(Product product) {
		try {
			System.out.println("item : " + product);
			productDao.editProduct(product);
			return Response.ok().build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("addUserOrder")
	public Response addUserOrder(UserOrder userOrder) {
		try {
			userOrderDao.addUserOrder(userOrder);
			System.out.println("user order persisted id: " + userOrder.getId());
			return Response.ok().entity(userOrder).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("addOrderedProduct")
	public Response addOrderedProduct(OrderedProduct orderedProduct) {
		try {
			orderedProductDao.addOrderedProduct(orderedProduct);
			return Response.ok().build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}

	}
}
