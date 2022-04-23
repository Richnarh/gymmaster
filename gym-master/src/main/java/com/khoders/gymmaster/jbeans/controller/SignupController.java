/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.jbeans.controller;

import com.khoders.gymmaster.entities.UserAccount;
import com.khoders.gymmaster.services.UserAccountService;
import com.khoders.resource.jpa.CrudApi;
import com.khoders.resource.utilities.CollectionList;
import com.khoders.resource.utilities.Msg;
import com.khoders.resource.utilities.SecurityUtil;
import static com.khoders.resource.utilities.SecurityUtil.hashText;
import com.khoders.resource.utilities.SystemUtils;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author khoders
 */
@Named(value="signupController")
@SessionScoped
public class SignupController implements Serializable{
    @Inject CrudApi crudApi;
    @Inject UserAccountService accountService;
    
    private UserAccount userAccount = new UserAccount();
    
    private List<UserAccount> accountList = new LinkedList<>();
    private String optionText;
    private String updatePassword;
    
    @PostConstruct
    private void init()
    {
        clear();
        accountList = accountService.accountList();
    }
    
    public void saveAccount()
    {
        System.out.println("heeeeeeeeeeeeeee");
        try 
        {
            if(!SecurityUtil.checkPassword(userAccount.getPassword(), userAccount.getPassword2()))
            {
               Msg.error("Password do not match");
                return;
            }
            
//            if(!accountService.isTaken(userAccount.getPhoneNumber()))
//            {
              if(optionText.equals("Update")){
                  userAccount.setPassword(updatePassword);
              }
              else
              {
                  userAccount.setPassword(hashText(userAccount.getPassword()));
              }
              
            if(crudApi.save(userAccount) != null)
            {
                accountList = CollectionList.washList(accountList, userAccount);
                Msg.info("Account created, Login now!");
            }  
            else
            {
              Msg.info("Oops! something went wrong, could not create account!");
            }
//          }
//          else
//           {
//            Msg.error("Email is already taken!");
//           }
        clear();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void clear()
    {
        userAccount = new UserAccount();
        optionText = "Save Changes";
        SystemUtils.resetJsfUI();
    }
       
    public void editAccount(UserAccount account)
    {
        updatePassword = account.getPassword();
        this.userAccount = account;
        optionText = "Update";
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

    public String getOptionText()
    {
        return optionText;
    }

    public void setOptionText(String optionText)
    {
        this.optionText = optionText;
    }
    
}
