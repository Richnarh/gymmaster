/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.khoders.gymmaster.services;

import com.khoders.gymmaster.entities.CustomerRegistration;
import com.khoders.gymmaster.listener.AppSession;
import com.khoders.resource.jpa.CrudApi;
import com.khoders.resource.utilities.DateUtil;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import javax.ejb.Asynchronous;
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
            String qryString = "SELECT e FROM CustomerRegistration e WHERE e.userAccount=?1 ORDER BY e.createdDate DESC";
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
            String qryString = "SELECT e FROM CustomerRegistration e WHERE e.userAccount=?1 AND e.expiryDate <= ?2 AND e.sentSms=?3";
            TypedQuery<CustomerRegistration> typedQuery = crudApi.getEm().createQuery(qryString, CustomerRegistration.class);
                                typedQuery.setParameter(1, appSession.getCurrentUser());
                                typedQuery.setParameter(2, LocalDate.now());
                                typedQuery.setParameter(3, false);
                            return typedQuery.getResultList();
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
    
    public CustomerRegistration expiredSubscription()
    {
        try
        {
            String qryString = "SELECT e FROM CustomerRegistration e WHERE e.expiryDate <= ?1 AND e.sentSms=?2";
            TypedQuery<CustomerRegistration> typedQuery = crudApi.getEm().createQuery(qryString, CustomerRegistration.class);
                                typedQuery.setParameter(1, LocalDate.now());
                                typedQuery.setParameter(2, false);
                            return typedQuery.getResultStream().findFirst().orElse(null);
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
    
    public CustomerRegistration expiringSubscription()
    {
        try
        {
            System.out.println("Next week date => "+DateUtil.nextWeekDate());
            String qryString = "SELECT e FROM CustomerRegistration e WHERE e.expiryDate < ?1 AND e.sentSms=?2";
            TypedQuery<CustomerRegistration> typedQuery = crudApi.getEm().createQuery(qryString, CustomerRegistration.class);
                                typedQuery.setParameter(1, DateUtil.nextWeekDate());
                                typedQuery.setParameter(2, false);
                            return typedQuery.getResultStream().findFirst().orElse(null);
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
