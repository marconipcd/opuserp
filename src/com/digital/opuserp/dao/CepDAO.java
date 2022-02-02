package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.util.ConnUtil;

public class CepDAO {


    public static Ceps getCep(String cep){
    	EntityManager em = ConnUtil.getEntity();
    	
    	try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Ceps> query = cb.createQuery(Ceps.class);
			Root<Ceps> root = query.from(Ceps.class);
			query.where(
					cb.equal(root.get("cep"), cb.parameter(String.class, "cep"))
					);
			
			TypedQuery<Ceps> q = em.createQuery(query);
			q.setParameter("cep", cep);
			if(q.getResultList().size() > 0){
				return (Ceps) q.getResultList().get(0);
			}
				return null;
		} catch (Exception e) {
			System.out.println("Erro ao buscar CEP: "+e.getMessage()+".\n Causado por: "+e.getCause());
			return null;
		}
    	
    }
  
    
}
