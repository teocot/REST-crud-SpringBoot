package com.teo.crud.controller.rest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.teo.crud.Application;
import com.teo.crud.data.model.Address;
import com.teo.crud.data.model.Customer;
import com.teo.crud.data.service.AddressService;
import com.teo.crud.data.service.CustomerService;
import com.teo.crud.util.JsonMappingUtil;

/**
 * CustomerREST controller unit tests
 * We are using Spring MVC Mocks to simulate HTTP requests 
 * 
 * This test RequestMappings and the 
 * request and response bodies are serialized correctly
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Transactional
public class CustomerRESTTest {

	protected MockMvc mvc;
	@Autowired
	protected WebApplicationContext webApplicationContext;

	@Autowired
	private CustomerService customerService;
	@Autowired
	private AddressService addressService;

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		customerService.evictCache();
		addressService.evictCache();
	}

	@Test
	public void testGetCustomers() throws Exception {

		String uri = "/customer";

		MvcResult result = mvc.perform(
				MockMvcRequestBuilders
				.get(uri)
				.accept(MediaType.APPLICATION_JSON))
				.andReturn();

		String content = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();

		Assert.assertEquals("Expected HTTP status", 200, status);
		Assert.assertTrue("Expected HTTP response body to have a value", content.trim().length() > 0);

	}

	@Test
	public void testGetCustomer() throws Exception {

		String uri = "/customer/{id}";
		Long id = new Long(1);

		MvcResult result = mvc.perform(
				MockMvcRequestBuilders
				.get(uri, id)
				.accept(MediaType.APPLICATION_JSON))
				.andReturn();

		String content = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();

		Assert.assertEquals("Expected HTTP status 200", 200, status);
		Assert.assertTrue("Expected HTTP response body to have a value", content.trim().length() > 0);

	}

	@Test
	public void testGetCustomerNotFound() throws Exception {

		String uri = "/customer/{id}";
		Long id = Long.MAX_VALUE;

		MvcResult result = mvc.perform(
				MockMvcRequestBuilders
				.get(uri, id)
				.accept(MediaType.APPLICATION_JSON))
				.andReturn();

		String content = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();

		Assert.assertEquals("Expected HTTP status 404", 404, status);
		Assert.assertTrue("Expected HTTP response body to be empty", content.trim().length() == 0);

	}

	@Test
	public void testCreateCustomer() throws Exception {

		String uri = "/customer";
		Customer customer = new Customer();
		customer.setName("Billai");
		customer.setTelephoneNumber("0800 CODE ONE");
		Address address = addressService.read(1L);
		customer.setAddress(address);

		String inputJson = JsonMappingUtil.mapToJson(customer);

		MvcResult result = mvc.perform(
				MockMvcRequestBuilders
				.post(uri).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(inputJson))
				.andReturn();

		String content = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();

		Assert.assertEquals("Expected HTTP status 201", 201, status);
		Assert.assertTrue("Expected HTTP response body to have a value", content.trim().length() > 0);

		Customer createdCustomer = JsonMappingUtil.mapFromJson(content, Customer.class);

		Assert.assertNotNull("Expected customer not null", createdCustomer);
		Assert.assertNotNull("Expected customer.id not null", createdCustomer.getCustomerId());
		Assert.assertEquals("Expected customer.name match", "Billai", createdCustomer.getName());
		Assert.assertEquals("Expected customer.telephoneNumber match", "0800 CODE ONE",
				createdCustomer.getTelephoneNumber());

	}

	@Test
	public void testUpdateCustomer() throws Exception {

		String uri = "/customer";
		Long id = new Long(1);
		Customer customer = customerService.read(id);
		String updatedText = customer.getName() + " Seinfeld";
		customer.setName(updatedText);
		String inputJson = JsonMappingUtil.mapToJson(customer);

		MvcResult result = mvc.perform(
				MockMvcRequestBuilders
				.put(uri, id).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(inputJson))
				.andReturn();

		String content = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();

		Assert.assertEquals("Expected HTTP status 200", 200, status);
		Assert.assertTrue("Expected HTTP response body to have a value", content.trim().length() > 0);

		Customer updatedCustomer = JsonMappingUtil.mapFromJson(content, Customer.class);

		Assert.assertNotNull("expected customer not null", updatedCustomer);
		Assert.assertEquals("expected customer.id unchanged", customer.getCustomerId(),
				updatedCustomer.getCustomerId());
		Assert.assertEquals("expected updated customer text match", updatedText, updatedCustomer.getName());

	}

	@Test
	public void testUpdateCustomerAddress() throws Exception {

		String uri = "/customer/{customerId}/address/{addressId}";
		Long customerId = new Long(1);
		Long updatedAddressId = new Long(2);
		Customer customer = customerService.read(customerId);

		String inputJson = JsonMappingUtil.mapToJson(customer);

		MvcResult result = mvc
				.perform(
						MockMvcRequestBuilders
						.put(uri, customerId, updatedAddressId)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(inputJson))
						.andReturn();

		String content = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();

		Assert.assertEquals("Expected HTTP status 200", 200, status);
		Assert.assertTrue("Expected HTTP response body to have a value", content.trim().length() > 0);

		Customer updatedCustomer = JsonMappingUtil.mapFromJson(content, Customer.class);

		Assert.assertNotNull("Expected customer not null", updatedCustomer);
		Assert.assertEquals("Expected customer.id unchanged", customer.getCustomerId(),
				updatedCustomer.getCustomerId());
		Assert.assertEquals("Expected updated customer text match", updatedAddressId,
				updatedCustomer.getAddress().getAddressId());

	}

	@Test
	public void testDeleteCustomer() throws Exception {

		String uri = "/customer/{id}";
		Long id = new Long(1);

		MvcResult result = mvc.perform(
				MockMvcRequestBuilders
				.delete(uri, id)
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		String content = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();
		Assert.assertEquals("Expected HTTP status 204", 204, status);
		Assert.assertTrue("Expected HTTP response body to be empty", content.trim().length() == 0);

		Customer deletedCustomer = customerService.read(id);
		Assert.assertNull("Expected customer to be null", deletedCustomer);

	}

}
