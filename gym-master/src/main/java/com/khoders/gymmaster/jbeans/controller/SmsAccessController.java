/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.jbeans.controller;

import com.khoders.gymmaster.entities.sms.SmsAccess;
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
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author richa
 */
@Named(value = "smsAccessController")
@SessionScoped
public class SmsAccessController implements Serializable
{
    @Inject private CrudApi crudApi;
    @Inject private SmsService smsService;
    private SmsAccess smsAccess = new SmsAccess();
    private List<SmsAccess> smsAccessList = new LinkedList<>();
    
    private String optionText;
    
    @PostConstruct
    private void init()
    {
        clear();
        smsAccessList = smsService.smsAccessList();
    }
    
    public void saveSmsAccess(){
        try
        {
            if(crudApi.save(smsAccess) != null)
            {
                smsAccessList = CollectionList.washList(smsAccessList, smsAccess);
                Msg.setMsg(Msg.SUCCESS_MESSAGE);
            }
            clear();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void deleteSmsAccess(SmsAccess smsAccess){
        try
        {
            if(crudApi.delete(smsAccess))
            {
                smsAccessList.remove(smsAccess);
                Msg.setMsg(Msg.DELETE_MESSAGE);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void editSmsAccess(SmsAccess smsAccess)
    {
        this.smsAccess = smsAccess;
        optionText = "Update";
    }
    
    public void clear(){
        smsAccess = new SmsAccess();
        optionText = "Save Changes";
        SystemUtils.resetJsfUI();
    }

    public SmsAccess getSmsAccess()
    {
        return smsAccess;
    }

    public void setSmsAccess(SmsAccess smsAccess)
    {
        this.smsAccess = smsAccess;
    }

    public String getOptionText()
    {
        return optionText;
    }

    public void setOptionText(String optionText)
    {
        this.optionText = optionText;
    }

    public List<SmsAccess> getSmsAccessList()
    {
        return smsAccessList;
    }
    
}
