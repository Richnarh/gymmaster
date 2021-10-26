/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.khoders.gymmaster.services;

import com.khoders.gymmaster.entities.CustomerRegistration;
import com.khoders.gymmaster.listener.AppSession;
import com.khoders.resource.jpa.CrudApi;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

/**
 *
 * @author Khoders
 */
@Stateless
public class CustomerService
{
    @Inject private CrudApi crudApi;
    @Inject private AppSession appSession;

    public List<CustomerRegistration> getCustomerRegistrationList()
    {
       try
        {
            String qryString = "SELECT e FROM CustomerRegistration e WHERE e.userAccount=?1";
            TypedQuery<CustomerRegistration> typedQuery = crudApi.getEm().createQuery(qryString, CustomerRegistration.class);
                                typedQuery.setParameter(1, appSession.getCurrentUser());
                            return typedQuery.getResultList();
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
    
    public List<CustomerRegistration> getExpiredRegistrationList()
    {
       try
        {
            String qryString = "SELECT e FROM CustomerRegistration e WHERE e.userAccount=?1 AND e.expiryDate <= ?2";
            TypedQuery<CustomerRegistration> typedQuery = crudApi.getEm().createQuery(qryString, CustomerRegistration.class);
                                typedQuery.setParameter(1, appSession.getCurrentUser());
                                typedQuery.setParameter(1, LocalDate.now());
                            return typedQuery.getResultList();
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
    
}
