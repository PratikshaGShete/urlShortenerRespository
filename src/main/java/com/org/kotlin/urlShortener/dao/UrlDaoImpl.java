package com.org.kotlin.urlShortener.dao;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.org.kotlin.urlShortener.controller.UrlApiController;
import com.org.kotlin.urlShortener.model.Url;

@Transactional
@Repository
public class UrlDaoImpl  {
	
	private static final Logger logger = LoggerFactory.getLogger(UrlDaoImpl.class);
       
	@Autowired(required = true)
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.openSession();
	}
	
	public Boolean saveUrl(Url lUrl, int count) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Url.class);
		if (criteria != null) {
			try {
				session.saveOrUpdate(lUrl);
				tx.commit();
			} catch (ConstraintViolationException e) {
				logger.info(
						"ConstraintViolationException while persisting the Url Object {} and generatig new short Url",
						lUrl.getLongUrl());
				if (count <= 5) {
					lUrl.setShortUrl(null);
					saveUrl(lUrl, count++);
				}
			} catch (Exception ex) {
				logger.error("Exception while persisting the Url Object {} ", ex.getMessage());
				tx.rollback();
				return false;
			}
		}
		return true;
	}

	public List<Url> fetchAllUrl() {
		Session session = getSession();
		List<Url> result = null;
		Criteria criteria = session.createCriteria(Url.class);
		if (criteria != null) {
			result=criteria.list();
		}
		return result;	
	}
	
	public Url fetchUrlDetails(String columnName,String lUrl)
	{
		System.out.println(columnName+" "+lUrl);
		Session session = getSession();
		Criteria criteria = session.createCriteria(Url.class);
		if ( criteria == null )
        {
            return null;
        }
        return (Url) criteria
				.add(Restrictions.eq(columnName, lUrl)).uniqueResult();
	}

	public void deleteUrlFromDb(Set<String> lUrlSet) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Url.class);
		if (criteria != null) {
			try {
				session.delete(lUrlSet);
				tx.commit();
			} catch (Exception e) {
				tx.rollback();
			}
		}
	}
	
  
}
