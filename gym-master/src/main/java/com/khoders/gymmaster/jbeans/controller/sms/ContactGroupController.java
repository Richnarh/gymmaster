/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.jbeans.controller.sms;

import com.khoders.gymmaster.entities.sms.GroupContact;
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
@Named(value = "contactGroupController")
@SessionScoped
public class ContactGroupController implements Serializable{
    @Inject private CrudApi crudApi;
    @Inject private AppSession appSession;
    @Inject private SmsService smsService;
    
    private String optionText;
    
    private GroupContact contactGroup = new GroupContact();
    private List<GroupContact> contactGroupList = new LinkedList<>();
    
    @PostConstruct
    private void init()
    {
        clearContactGroup();
        contactGroupList = smsService.getGroupContactList();
    }
    
   public void saveContactGroup()
    {
        try 
        {
          contactGroup.genCode();
          if(crudApi.save(contactGroup) != null)
          {
              contactGroupList = CollectionList.washList(contactGroupList, contactGroup);
              
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.SUCCESS_MESSAGE, null)); 
          }
          else
          {
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Oops! failed to create contactGroup"), null));
          }
           clearContactGroup();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
   
    public void editContactGroup(GroupContact contactGroup)
    {
       optionText = "Update";
       this.contactGroup=contactGroup;
    }
    
    public void deleteContactGroup(GroupContact contactGroup)
    {
        try
        {
          if(crudApi.delete(contactGroup))
          {
              contactGroupList.remove(contactGroup);
              
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
    
    public void clearContactGroup() {
        contactGroup = new GroupContact();
        contactGroup.setUserAccount(appSession.getCurrentUser());
        optionText = "Save Changes";
        SystemUtils.resetJsfUI();
    }

    public GroupContact getContactGroup() {
        return contactGroup;
    }

    public void setContactGroup(GroupContact contactGroup) {
        this.contactGroup = contactGroup;
    }

    public List<GroupContact> getContactGroupList() {
        return contactGroupList;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
    
}
