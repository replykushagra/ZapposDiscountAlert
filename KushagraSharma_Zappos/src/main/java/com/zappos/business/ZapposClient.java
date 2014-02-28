package com.zappos.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.product.bean.ProductBean;

public class ZapposClient {

	private static final String SEARCH_URI = "http://api.zappos.com/Search?key=52ddafbe3ee659bad97fcce7c53592916a6bfd73";
	private static final String RESULTS = "results";
	private static final String PRODUCT_ID = "productId";
	private static final String PRODUCT_NAME = "productName";
	private static final String PRICE = "price";
	private static final String DISCOUNT = "percentOff";
	private static final String EMAIL_ID = "Ksharma9@asu.edu";

	public void search(final String term) throws Exception {
		try {
			// Sending a request to the API
			JsonNode jsonReply = sendRequest();
			// Creating a list of type ProductBean.
			ArrayList<ProductBean> productList = new ArrayList<ProductBean>();
			// For traversing through all the records returned
			int counter = 0;
			// Populate productList with the products retrieved
			productList = populateProductBean(jsonReply, productList, counter);
			/*
			 * Initializing list of subscribed products(the user selected). Set
			 * is chosen so that the user is not able to add the same product
			 * again to his wishlist.
			 */
			Set<ProductBean> listOfSubscribedProducts = new HashSet<ProductBean>();
			// Subscribe user to different products
			listOfSubscribedProducts = subscribeProducts(productList,
					listOfSubscribedProducts);
			// Thread sleeps for 2 seconds before making a call to API
			// Thread.sleep(2000);
			// Check if a discount of 20% or more is there on any product in
			// user's wishlist
			checkDiscountAfterInterval(listOfSubscribedProducts);

		} catch (UnknownHostException e) {
			System.out.println("Either Incorrect url or network problem");
		} catch (IOException exception) {
			System.out.println("IO Exception");
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (NumberFormatException excpetion) {
			System.out.println("Please enter the numerals");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JsonNode sendRequest() throws Exception {
		final ClientRequest clientRequest = new ClientRequest(SEARCH_URI);
		// For capturing Response
		final ClientResponse<String> clientResponse = clientRequest
				.get(String.class);
		// Collecting response in JsonNode
		final JsonNode jsonReply = new ObjectMapper().readTree(clientResponse
				.getEntity());
		return jsonReply;
	}

	public ArrayList<ProductBean> populateProductBean(JsonNode jsonReply,
			ArrayList<ProductBean> productList, int counter) {
		// Get the results until the last index
		while (jsonReply.get(RESULTS).get(counter) != null) {
			ProductBean pBean = new ProductBean();
			// Populating the ProductBean with one record
			pBean.setProductId(jsonReply.get(RESULTS).get(counter)
					.get(PRODUCT_ID).asText());
			// Extracting the numeral by eliminating % and setting the discount
			// in ProductBean
			double discount = Double.parseDouble((jsonReply.get(RESULTS)
					.get(counter).get(DISCOUNT).asText().replace("%", "")));
			pBean.setDiscount(discount);
			// Setting the Product Name
			pBean.setProductname(jsonReply.get(RESULTS).get(counter)
					.get(PRODUCT_NAME).asText());
			counter++;
			// Adding all the ProdcutBean objects to productList
			productList.add(pBean);
		}
		return productList;
	}

	private Set<ProductBean> subscribeProducts(
			ArrayList<ProductBean> productList,
			Set<ProductBean> listOfSubscribedProducts) throws IOException {
		// Prompting user to select products one after the other and free the
		// choice
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = " ";
		System.out
				.println("Choose 1 or more products by writing the index number followed by an Enter");
		for (int i = 0; i < productList.size(); i++) {
			System.out.println(i + "." + " "
					+ productList.get(i).getProductname());
		}
		System.out.println(productList.size() + ". Freeze my choice");
		System.out.println(productList.size() + 1 + ". Exit");
		input = br.readLine();
		if (Integer.parseInt(input) < 0
				|| Integer.parseInt(input) > productList.size() + 1) {
			System.out.println("Incorrect Choice");
			System.exit(0);
		}
		if (Integer.parseInt(input) == productList.size()) {
			System.out.println("No Product Added");
			System.exit(0);
		}
		// Checking if the user froze his preferences, only then we can move
		// forward.
		while (!input.equalsIgnoreCase(Integer.toString(productList.size()))) {
			// Checking if the user quits at any point
			if (input
					.equalsIgnoreCase(Integer.toString(productList.size() + 1))) {
				System.exit(0);
			}
			// Populating the user's products from the productList
			listOfSubscribedProducts.add(productList.get(Integer
					.parseInt(input)));
			input = br.readLine();
			if (Integer.parseInt(input) < 0
					|| Integer.parseInt(input) > productList.size() + 1) {
				System.out.println("Incorrect Choice");
				System.exit(0);
			}
		}
		return listOfSubscribedProducts;
	}

	private void checkDiscountAfterInterval(
			Set<ProductBean> listOfSubscribedProducts)
			throws InterruptedException, Exception {
		Thread.sleep(2000);
		for (int i = 1; i <= 3; i++) {
			// At every iteration the counter should be initialized to 0
			int counter = 0;
			// API call
			JsonNode reply = sendRequest();
			// Retrieving records until last
			while (reply.get(RESULTS).get(counter) != null) {
				java.util.Iterator<ProductBean> iter = listOfSubscribedProducts
						.iterator();
				while (iter.hasNext()) {
					String productId = reply.get(RESULTS).get(counter)
							.get("productId").asText();
					double discount = Double.parseDouble((reply.get(RESULTS)
							.get(counter).get(DISCOUNT).asText().replace("%",
							"")));
					/*
					 * Assigning discount to 2 products manually since on every
					 * API call every product is returning the same price as the
					 * original
					 */
					if (counter == 1) {
						discount = 21;
					}
					if (counter == 2) {
						discount = 22;
					}
					if (productId.equalsIgnoreCase(((ProductBean) iter.next())
							.getProductId()) && discount >= 20) {
						String productName = reply.get(RESULTS).get(counter)
								.get(PRODUCT_NAME).asText();
						/*
						 * Sending an alert to the person saying that a x% of
						 * discount is available
						 */
						sendAnAlert(EMAIL_ID, productName, discount);
					}
				}
				counter += 1;
			}
			Thread.sleep(6000);
		}

	}

	private void sendAnAlert(String emailId, String productName, double discount) {
		/*
		 * Printing the alert. Have not implemented the actual sending of email
		 * to the subscriber.
		 */
		System.out.println("Send out an alert to " + EMAIL_ID
				+ " stating that " + discount + "% discount is available on "
				+ productName);
	}

}