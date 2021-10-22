/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.jbeans.controller.sms;

import com.khoders.gymmaster.entities.sms.SMSGrup;
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
@Named(value = "smsGrupController")
@SessionScoped
public class SMSGrupController implements Serializable{
    @Inject private CrudApi crudApi;
    @Inject private AppSession appSession;
    @Inject private SmsService smsService;
    
    private String optionText;
    
    private SMSGrup smsGrup = new SMSGrup();
    private List<SMSGrup> smsGroupList = new LinkedList<>();
    
    @PostConstruct
    private void init()
    {
        clearSMSGrup();
        
        smsGroupList = smsService.getGroupList();
    }
    
   public void saveSMSGrup()
    {
        try 
        {
          smsGrup.genCode();
          if(crudApi.save(smsGrup) != null)
          {
              smsGroupList = CollectionList.washList(smsGroupList, smsGrup);
              
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.SUCCESS_MESSAGE, null)); 
          }
          else
          {
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Oops! failed to create smsGrup"), null));
          }
           clearSMSGrup();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
   
    public void editSMSGrup(SMSGrup smsGrup)
    {
       optionText = "Update";
       this.smsGrup=smsGrup;
    }
    
    public void deleteSMSGrup(SMSGrup smsGrup)
    {
        try
        {
          if(crudApi.delete(smsGrup))
          {
              smsGroupList.remove(smsGrup);
              
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.setMsg("Contact Group deleted successfully!"), null)); 
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
    
    public void clearSMSGrup() {
        smsGrup = new SMSGrup();
        smsGrup.setUserAccount(appSession.getCurrentUser());
        optionText = "Save Changes";
        SystemUtils.resetJsfUI();
    }

    public SMSGrup getSmsGrup()
    {
        return smsGrup;
    }

    public void setSmsGrup(SMSGrup smsGrup)
    {
        this.smsGrup = smsGrup;
    }
    
    public List<SMSGrup> getSMSGrupList() {
        return smsGroupList;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
    
}
