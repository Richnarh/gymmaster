/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.jbeans.controller;

import com.khoders.gymmaster.listener.AppSession;
import com.khoders.resource.jpa.CrudApi;
import com.khoders.resource.utilities.Msg;
import com.khoders.resource.utilities.SecurityUtil;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author khoders
 */
@Named(value = "profileUpdateController")
@SessionScoped
public class ProfileUpdateController implements Serializable{
    @Inject private CrudApi crudApi;
    @Inject private AppSession appSession;
    
    private String password;
    private String confirmPassword;
    
    public void updateAccount()
    {
        try 
        {
            if(appSession.getCurrentUser() != null)
            {
                crudApi.save(appSession.getCurrentUser());
                FacesContext.getCurrentInstance().addMessage(null, 
                      new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.SUCCESS_MESSAGE, null));
            }
            else
            {
              FacesContext.getCurrentInstance().addMessage(null, 
                      new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.DELETE_MESSAGE, null));  
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    
       
    public void updatePassword()
    {
        if(!password.equals(confirmPassword))
        {
            FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Password do not match"), null));
            return;
        }
        
        String hashedPassword = SecurityUtil.hashText(password);
        
        if(hashedPassword.equalsIgnoreCase(appSession.getCurrentUser().getPassword()))
        {
            String msg = "This password is same as the old one, please use a new password";
            FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_WARN, Msg.setMsg(msg), null));
            return;
        }
        
        
        appSession.getCurrentUser().setPassword(hashedPassword);
        
        if(crudApi.save(appSession.getCurrentUser()) != null)
        {
            FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.setMsg("Password Reset successful!"), null));
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    
}
