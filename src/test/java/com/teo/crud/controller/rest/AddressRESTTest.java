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
import com.teo.crud.data.service.AddressService;
import com.teo.crud.util.JsonMappingUtil;

/**
 * AddressREST controller unit tests
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
public class AddressRESTTest{

    protected MockMvc mvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
	private AddressService addressService;

	@Before
	public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		addressService.evictCache();
	}

	@Test
	public void testGetAddresss() throws Exception {

		String uri = "/address";

		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON)).andReturn();

		String content = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();

		Assert.assertEquals("Expected HTTP status", 200, status);
		Assert.assertTrue("Expected HTTP response body to have a value", content.trim().length() > 0);

	}

	@Test
	public void testGetAddress() throws Exception {

		String uri = "/address/{id}";
		Long id = new Long(1);

		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri, id).accept(MediaType.APPLICATION_JSON))
				.andReturn();

		String content = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();

		Assert.assertEquals("Expected HTTP status 200", 200, status);
		Assert.assertTrue("Expected HTTP response body to have a value", content.trim().length() > 0);

	}

	@Test
	public void testGetAddressNotFound() throws Exception {

		String uri = "/address/{id}";
		Long id = Long.MAX_VALUE;

		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri, id).accept(MediaType.APPLICATION_JSON))
				.andReturn();

		String content = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();

		Assert.assertEquals("Expected HTTP status 404", 404, status);
		Assert.assertTrue("Expected HTTP response body to be empty", content.trim().length() == 0);

	}

	@Test
	public void testCreateAddress() throws Exception {

		String uri = "/address";
		Address address = new Address();
		address.setStreetName("Gordon");
		address.setStreetNumber("212");
		address.setStreetType("avenue");
		address.setSuburb("Ellecort");
		address.setCity("Auckladn");
		address.setCountry("Angola");
		String inputJson = JsonMappingUtil.mapToJson(address);

		MvcResult result = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(inputJson)).andReturn();

		String content = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();

		Assert.assertEquals("Expected HTTP status 201", 201, status);
		Assert.assertTrue("Expected HTTP response body to have a value", content.trim().length() > 0);

		Address createdAddress = JsonMappingUtil.mapFromJson(content, Address.class);

		Assert.assertNotNull("Expected address not null", createdAddress);
		Assert.assertNotNull("Expected address.id not null", createdAddress.getAddressId());
		Assert.assertEquals("Expected address.text match", "Gordon", createdAddress.getStreetName());

	}

	@Test
	public void testUpdateAddress() throws Exception {

		String uri = "/address";
		Long id = new Long(1);
		Address address = addressService.read(id);
		String updatedText = address.getStreetName() + " test";
		address.setStreetName(updatedText);
		String inputJson = JsonMappingUtil.mapToJson(address);

		MvcResult result = mvc.perform(MockMvcRequestBuilders.put(uri, id).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(inputJson)).andReturn();

		String content = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();

		Assert.assertEquals("Expected HTTP status 200", 200, status);
		Assert.assertTrue("Expected HTTP response body to have a value", content.trim().length() > 0);

		Address updatedAddress = JsonMappingUtil.mapFromJson(content, Address.class);

		Assert.assertNotNull("Expected address not null", updatedAddress);
		Assert.assertEquals("Expected address.id unchanged", address.getAddressId(), updatedAddress.getAddressId());
		Assert.assertEquals("Expected updated address text match", updatedText, updatedAddress.getStreetName());

	}

	@Test
	public void testDeleteAddress() throws Exception {

		String uri = "/address/{id}";
		Long id = new Long(1);

		MvcResult result = mvc.perform(MockMvcRequestBuilders.delete(uri, id).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		String content = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();

		Assert.assertEquals("Expected HTTP status 204", 204, status);
		Assert.assertTrue("Expected HTTP response body to be empty", content.trim().length() == 0);

		Address deletedAddress = addressService.read(id);

		Assert.assertNull("Expected address to be null", deletedAddress);

	}

}
