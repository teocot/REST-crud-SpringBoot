package com.teo.crud.data.service;

import java.util.Collection;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.teo.crud.Application;
import com.teo.crud.data.model.Address;
import com.teo.crud.data.service.AddressService;

/**
 * Unit test methods for the AddressService and AddressServiceBean.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
public class AddressServiceTest{

    @Autowired
    private AddressService service;

    @Before
    public void setUp() {
        service.evictCache();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFindAll() {

        Collection<Address> list = service.readAll();

        Assert.assertNotNull("failure - expected not null", list);
        Assert.assertEquals("failure - expected list size", 5, list.size());

    }

    @Test
    public void testFindOne() {

        Long id = new Long(1);

        Address entity = service.read(id);

        Assert.assertNotNull("failure - expected not null", entity);
        Assert.assertEquals("failure - expected id attribute match", id,
                entity.getAddressId());

    }

    @Test
    public void testFindOneNotFound() {

        Long id = Long.MAX_VALUE;

        Address entity = service.read(id);

        Assert.assertNull("failure - expected null", entity);

    }

    @Test
    public void testCreate() {

        Address entity = new Address();
        entity.setStreetName("streetName");

        Address createdEntity = service.create(entity);

        Assert.assertNotNull("failure - expected not null", createdEntity);
        Assert.assertNotNull("failure - expected id attribute not null",
                createdEntity.getAddressId());
        Assert.assertEquals("failure - expected text attribute match", "streetName",
                createdEntity.getStreetName());

        Collection<Address> list = service.readAll();

        Assert.assertEquals("failure - expected size", 6, list.size());

    }

    @Test
    public void testCreateWithId() {

        Exception exception = null;

        Address entity = new Address();
        entity.setAddressId(Long.MAX_VALUE);
        entity.setStreetName("test");

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

        Address entity = service.read(id);

        Assert.assertNotNull("failure - expected not null", entity);

        String updatedStreetName = entity.getStreetName() + " test";
        entity.setStreetName(updatedStreetName);
        Address updatedEntity = service.update(entity);

        Assert.assertNotNull("failure - expected not null", updatedEntity);
        Assert.assertEquals("failure - expected id attribute match", id,
                updatedEntity.getAddressId());
        Assert.assertEquals("failure - expected text attribute match",
                updatedStreetName, updatedEntity.getStreetName());

    }

    @Test
    public void testUpdateNotFound() {

        Exception exception = null;

        Address entity = new Address();
        entity.setAddressId(Long.MAX_VALUE);
        entity.setStreetName("streetName");

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

        Long id = new Long(5);

        Address entity = service.read(id);

        Assert.assertNotNull("failure - expected not null", entity);

        service.delete(id);

        Collection<Address> list = service.readAll();

        Assert.assertEquals("failure - expected size", 4, list.size());

        Address deletedEntity = service.read(id);

        Assert.assertNull("failure - expected null", deletedEntity);

    }

}
