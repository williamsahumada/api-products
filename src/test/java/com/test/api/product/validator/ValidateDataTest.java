package com.test.api.product.validator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class ValidateDataTest {

    @InjectMocks
    ValidatorData validateData;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test(expected = ResponseStatusException.class)
    public void givenInvalidId_whenValidateId_thenThrowsResponseStatusException(){

        validateData.validateId("A");

    }

    @Test
    public void givenEmptyId_whenValidateId_thenThrowsResponseStatusException(){

       thrown.expect(ResponseStatusException.class);
       thrown.expect(hasProperty("status", is(HttpStatus.BAD_REQUEST)));
       validateData.validateId("");

    }

    @Test(expected = ResponseStatusException.class)
    public void givenEmptyId_whenValidateId_thenThrowsResponseStatusException2(){

        validateData.validateId("");

    }
}
