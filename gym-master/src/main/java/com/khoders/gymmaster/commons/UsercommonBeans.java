/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.commons;

import com.khoders.gymmaster.entities.CustomerRegistration;
import com.khoders.gymmaster.entities.sms.MessageTemplate;
import com.khoders.gymmaster.entities.sms.SMSGrup;
import com.khoders.gymmaster.entities.sms.SenderId;
import com.khoders.gymmaster.services.SmsService;
import com.khoders.resource.jpa.CrudApi;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author richard
 */
@Named(value = "usercommonBeans")
@SessionScoped
public class UsercommonBeans implements Serializable
{
    @Inject private CrudApi crudApi;
    @Inject private SmsService smsService;
    
    private List<CustomerRegistration> customerRegistrationList = new LinkedList<>();
    private List<SenderId> senderIdList = new LinkedList<>();
    private List<MessageTemplate> messageTemplateList = new LinkedList<>();
    private List<SMSGrup> smsGroupList = new LinkedList<>();
    
    @PostConstruct
    public void init()
    {
        smsGroupList = smsService.getGroupList();
       customerRegistrationList = smsService.getContactList();
       senderIdList = smsService.getSenderIdList();
       messageTemplateList = smsService.getMessageTemplateList();
    }

    public List<CustomerRegistration> getCustomerRegistrationList()
    {
        return customerRegistrationList;
    }

    public List<SenderId> getSenderIdList()
    {
        return senderIdList;
    }

    public List<MessageTemplate> getMessageTemplateList()
    {
        return messageTemplateList;
    }

    public List<SMSGrup> getSmsGroupList() {
        return smsGroupList;
    }
    
}
