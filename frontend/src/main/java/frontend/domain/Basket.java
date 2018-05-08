package frontend.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import frontend.model.Item;
import frontend.model.OrderedItem;

@SessionScoped
@ManagedBean
public class Basket {

	private HashMap<Long, OrderedItem> items;

	private OrderedItem selectedItem;

	@PostConstruct
	public void init() {
		items = new HashMap<Long, OrderedItem>();
	}

	public void add(Item item, double quantity) {
		long itemId = item.getId();
		if (items.containsKey(itemId)) {
			OrderedItem orderedItem = items.get(itemId);
			//orderedItem.setQuantity(orderedItem.getQuantity() + quantity);
			orderedItem.setQuantity(quantity);
		} else {
			OrderedItem orderedItem = new OrderedItem();
			orderedItem.setItem(item);
			orderedItem.setQuantity(quantity);
			items.put(item.getId(), orderedItem);
		}
	}

	public List<OrderedItem> getItems() {
		return new ArrayList<OrderedItem>(items.values());
	}

	public void setItems(HashMap<Long, OrderedItem> items) {
		this.items = items;
	}

	private void remove(long itemId) {
		items.remove(itemId);
	}

	public void clear() {
		items.clear();
	}

	public void setSelectedItem(OrderedItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	public OrderedItem getSelectedItem() {
		return selectedItem;
	}

	public void removeSelected() {
		remove(selectedItem.getItem().getId());
	}
}
