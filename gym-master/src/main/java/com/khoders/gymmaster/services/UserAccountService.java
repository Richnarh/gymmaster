/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.services;

import com.khoders.gymmaster.entities.UserAccount;
import com.khoders.gymmaster.jbeans.UserModel;
import com.khoders.resource.jpa.CrudApi;
import static com.khoders.resource.utilities.SecurityUtil.hashText;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

/**
 *
 * @author Khoders
 */
@Stateless
public class UserAccountService
{
    @Inject private CrudApi crudApi;
    
    public UserAccount login(UserModel userModel)
    {
        
        try
        {
            String qryString = "SELECT e FROM UserAccount e WHERE e.phoneNumber=?1 AND e.password=?2";
            TypedQuery<UserAccount> typedQuery = crudApi.getEm().createQuery(qryString, UserAccount.class)
                    .setParameter(1, userModel.getUserEmail())
                    .setParameter(2, hashText(userModel.getPassword()));
            
                 return typedQuery.getResultStream().findFirst().orElse(null);
                 
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
    
    public boolean isTaken(String email)
    {
        String qryString = "SELECT e FROM UserAccount e WHERE e.email=?1";
        try {
            UserAccount account = crudApi.getEm().createQuery(qryString, UserAccount.class)
                    .setParameter(1, email)
                    .getSingleResult();
            
            return account != null;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return false;
    }
}
