package com.teo.crud.data.service;

import java.util.Collection;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.teo.crud.Application;
import com.teo.crud.data.model.Customer;
import com.teo.crud.data.service.CustomerService;

/**
 * Unit test methods for the CustomerService and CustomerServiceBean.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
public class CustomerServiceTest {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
    private CustomerService service;

    @Before
    public void setUp() {
        service.evictCache();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFindAll() {

        Collection<Customer> list = service.readAll();

        Assert.assertNotNull("failure - expected not null", list);
        Assert.assertEquals("failure - expected list size", 4, list.size());

    }

    @Test
    public void testFindOne() {

        Long id = new Long(1);

        Customer entity = service.read(id);

        Assert.assertNotNull("failure - expected not null", entity);
        Assert.assertEquals("failure - expected id attribute match", id,
                entity.getCustomerId());

    }

    @Test
    public void testFindOneNotFound() {

        Long id = Long.MAX_VALUE;

        Customer entity = service.read(id);

        Assert.assertNull("failure - expected null", entity);

    }

    @Test
    public void testCreate() {

        Customer entity = new Customer();
        entity.setName("CustomerName");

        Customer createdEntity = service.create(entity);

        Assert.assertNotNull("failure - expected not null", createdEntity);
        Assert.assertNotNull("failure - expected id attribute not null",
                createdEntity.getCustomerId());
        Assert.assertEquals("failure - expected text attribute match", "CustomerName",
                createdEntity.getName());

        Collection<Customer> list = service.readAll();

        Assert.assertEquals("failure - expected size", 5, list.size());

    }

    @Test
    public void testCreateWithId() {

        Exception exception = null;

        Customer entity = new Customer();
        entity.setCustomerId(Long.MAX_VALUE);
        entity.setName("test");

        try {
            service.create(entity);
        } catch (EntityExistsException e) {
            exception = e;
        }

        Assert.assertNotNull("failure - expected exception", exception);
        Assert.assertTrue("failure - expected EntityExistsException",
                exception instanceof EntityExistsException);

    }

    @Test
    public void testUpdate() {

        Long id = new Long(1);

        Customer entity = service.read(id);

        Assert.assertNotNull("failure - expected not null", entity);

        String updatedStreetName = entity.getName() + " test";
        entity.setName(updatedStreetName);
        Customer updatedEntity = service.update(entity);

        Assert.assertNotNull("failure - expected not null", updatedEntity);
        Assert.assertEquals("failure - expected id attribute match", id,
                updatedEntity.getCustomerId());
        Assert.assertEquals("failure - expected text attribute match",
                updatedStreetName, updatedEntity.getName());

    }

    @Test
    public void testUpdateNotFound() {

        Exception exception = null;

        Customer entity = new Customer();
        entity.setCustomerId(Long.MAX_VALUE);
        entity.setName("CustomerName");

        try {
            service.update(entity);
        } catch (NoResultException e) {
            exception = e;
        }

        Assert.assertNotNull("failure - expected exception", exception);
        Assert.assertTrue("failure - expected NoResultException",
                exception instanceof NoResultException);

    }

    /**
     * this test will ensure the address that is deleted 
     * is not referenced from a customer
     */
    @Test
    public void testDelete() {

        Long id = new Long(4);

        Customer entity = service.read(id);

        Assert.assertNotNull("failure - expected not null", entity);

        service.delete(id);

        Collection<Customer> list = service.readAll();

        Assert.assertEquals("failure - expected size", 3, list.size());

        Customer deletedEntity = service.read(id);

        Assert.assertNull("failure - expected null", deletedEntity);

    }

}
