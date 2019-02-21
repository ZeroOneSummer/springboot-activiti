package com.act.activiti.dao;

import java.util.List;

/**
 * @author pen
 */
public interface BaseDao<T, K> {

	void insert(T prama);
	
	void delete(T prama);
	
	void deleteByList(T prama);
	
	void update(T pramarama);
	
	K findByParam(T k);
	
	List<K> findAll();
	
	List<T> findListByParam(T t);
}
