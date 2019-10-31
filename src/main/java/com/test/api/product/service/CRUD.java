package com.test.api.product.service;

import com.test.api.product.exception.DataNotFoundException;

import java.util.List;

public interface CRUD<T> {

    T entity(Long t) throws DataNotFoundException;

    List<T> list();

    T save(T t);

    T update(T t) throws DataNotFoundException;

    boolean delete(T t) throws DataNotFoundException;

}
