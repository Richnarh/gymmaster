/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.jbeans.controller;

import com.khoders.gymmaster.entities.UserAccount;
import com.khoders.gymmaster.services.UserAccountService;
import com.khoders.resource.jpa.CrudApi;
import com.khoders.resource.utilities.Msg;
import com.khoders.resource.utilities.SecurityUtil;
import static com.khoders.resource.utilities.SecurityUtil.hashText;
import com.khoders.resource.utilities.SystemUtils;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author khoders
 */
@Named(value="signupController")
@RequestScoped
public class SignupController implements Serializable{
    @Inject CrudApi crudApi;
    @Inject UserAccountService accountService;
    
    private UserAccount userAccount = new UserAccount();
    
    private List<UserAccount> accountList = new LinkedList<>();
    
    public void saveAccount()
    {
        try 
        {
            if(!SecurityUtil.checkPassword(userAccount.getPassword(), userAccount.getPassword2()))
            {
                FacesContext
                        .getCurrentInstance()
                        .addMessage(null, 
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR, Msg.setMsg("Password do not match"), null));
                return;
            }
            
            if(!accountService.isTaken(userAccount.getPhoneNumber()))
            {
              userAccount.setPassword(hashText(userAccount.getPassword()));

            if(crudApi.save(userAccount) != null)
            {
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.setMsg("Account created, Login now!"), null));
            }  
            else
            {
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Oops! something went wrong, could not create account!"), null));
            }
          }
          else
           {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Email is already taken!"), null));
           }
        reset();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void reset()
    {
        userAccount = new UserAccount();
        SystemUtils.resetJsfUI();
    }
       
    public void editAccount(UserAccount account)
    {
        this.userAccount = account;
    }
    
    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount farmAccount) {
        this.userAccount = farmAccount;
    }

    public List<UserAccount> getAccountList()
    {
        return accountList;
    }

    public void setAccountList(List<UserAccount> accountList)
    {
        this.accountList = accountList;
    }
    
}
