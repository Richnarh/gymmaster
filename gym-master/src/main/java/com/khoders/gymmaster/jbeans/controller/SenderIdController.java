/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.jbeans.controller;

import com.khoders.gymmaster.entities.sms.SenderId;
import com.khoders.gymmaster.listener.AppSession;
import com.khoders.gymmaster.services.SmsService;
import com.khoders.resource.jpa.CrudApi;
import com.khoders.resource.utilities.CollectionList;
import com.khoders.resource.utilities.Msg;
import com.khoders.resource.utilities.SystemUtils;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author khoders
 */
@Named(value = "senderIdController")
@SessionScoped
public class SenderIdController implements Serializable
{
    @Inject CrudApi crudApi;
    @Inject AppSession appSession;
    @Inject SmsService smsService;
    
    private String optionText;
    
    private SenderId senderId = new SenderId();
    private List<SenderId> senderIdList = new LinkedList<>();
    
    @PostConstruct
    private void init()
    {
        clearSenderId();
        
       senderIdList = smsService.getSenderIdList();
    }
    
   public void saveSenderId()
   {
        try 
        {
          senderId.genCode();
          
          if(senderId.checkSenderIdSize())
          {
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Sender Id must be 11 characters"), null));
              return;
          }
          
          if(crudApi.save(senderId) != null)
          {
              senderIdList = CollectionList.washList(senderIdList, senderId);
              
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.SUCCESS_MESSAGE, null)); 
          }
          else
          {
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Oops! failed to create sender Id"), null));
          }
           clearSenderId();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
   
    public void editSenderId(SenderId senderId)
    {
       optionText = "Update";
       this.senderId=senderId;
    }
    
    public void deleteSenderId(SenderId senderId)
    {
        try
        {
          if(crudApi.delete(senderId))
          {
              senderIdList.remove(senderId);
              
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.DELETE_MESSAGE, null)); 
          }
          else
          {
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.FAILED_MESSAGE, null));
          }
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    public void clearSenderId() {
        senderId = new SenderId();
        senderId.setUserAccount(appSession.getCurrentUser());
        optionText = "Save Changes";
        SystemUtils.resetJsfUI();
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public SenderId getSenderId()
    {
        return senderId;
    }

    public void setSenderId(SenderId senderId)
    {
        this.senderId = senderId;
    }

    public List<SenderId> getSenderIdList()
    {
        return senderIdList;
    }
    
}
