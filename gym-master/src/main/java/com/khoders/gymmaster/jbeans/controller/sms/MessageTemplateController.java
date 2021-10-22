/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.jbeans.controller.sms;

import com.khoders.gymmaster.entities.sms.MessageTemplate;
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
@Named(value = "messageTemplateController")
@SessionScoped
public class MessageTemplateController implements Serializable{
    @Inject CrudApi crudApi;
    @Inject AppSession appSession;
    @Inject SmsService smsService;
    
    private String optionText;
    
    private MessageTemplate messageTemplate = new MessageTemplate();
    private List<MessageTemplate> messageTemplateList = new LinkedList<>();
    
    @PostConstruct
    private void init()
    {
        clearMessageTemplate();
        
        messageTemplateList = smsService.getMessageTemplateList();
    }
    
   public void saveMessageTemplate()
    {
        try 
        {
          messageTemplate.genCode();
          if(crudApi.save(messageTemplate) != null)
          {
              messageTemplateList = CollectionList.washList(messageTemplateList, messageTemplate);
              
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.SUCCESS_MESSAGE, null)); 
          }
          else
          {
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Oops! failed to create messageTemplate"), null));
          }
           clearMessageTemplate();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
   
    public void editMessageTemplate(MessageTemplate messageTemplate)
    {
       optionText = "Update";
       this.messageTemplate=messageTemplate;
    }
    
    public void deleteMessageTemplate(MessageTemplate messageTemplate)
    {
        try
        {
          if(crudApi.delete(messageTemplate))
          {
              messageTemplateList.remove(messageTemplate);
              
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.setMsg("Message template successfully!"), null)); 
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
    
    public void clearMessageTemplate() {
        messageTemplate = new MessageTemplate();
        messageTemplate.setUserAccount(appSession.getCurrentUser());
        optionText = "Save Changes";
        SystemUtils.resetJsfUI();
    }

    public MessageTemplate getMessageTemplate()
    {
        return messageTemplate;
    }

    public void setMessageTemplate(MessageTemplate messageTemplate)
    {
        this.messageTemplate = messageTemplate;
    }

    public List<MessageTemplate> getMessageTemplateList() {
        return messageTemplateList;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
    
}
