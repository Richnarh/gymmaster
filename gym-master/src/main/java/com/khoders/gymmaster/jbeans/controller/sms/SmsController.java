/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.jbeans.controller.sms;

import Zenoph.SMSLib.Enums.REQSTATUS;
import static Zenoph.SMSLib.Enums.REQSTATUS.ERR_INSUFF_CREDIT;
import Zenoph.SMSLib.ZenophSMS;
import com.khoders.gymmaster.entities.CustomerRegistration;
import com.khoders.gymmaster.entities.sms.GroupContact;
import com.khoders.gymmaster.entities.sms.MessageTemplate;
import com.khoders.gymmaster.entities.sms.SMSGrup;
import com.khoders.gymmaster.entities.sms.SenderId;
import com.khoders.gymmaster.entities.sms.Sms;
import com.khoders.gymmaster.enums.MessagingType;
import com.khoders.gymmaster.enums.SMSType;
import com.khoders.gymmaster.listener.AppSession;
import com.khoders.gymmaster.services.CustomerService;
import com.khoders.gymmaster.services.SmsService;
import com.khoders.resource.jpa.CrudApi;
import com.khoders.resource.utilities.Msg;
import com.khoders.resource.utilities.SystemUtils;
import java.io.Serializable;
import java.time.LocalDateTime;
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
 * @author pascal
 */
@Named(value = "smsController")
@SessionScoped
public class SmsController implements Serializable
{
    @Inject private CrudApi crudApi;
    @Inject private AppSession appSession;
    @Inject private SmsService smsService;
    @Inject private CustomerService customerService;
    
    private CustomerRegistration selectedCustomer;

    private Sms sms = new Sms();
    private SMSGrup smsGrup = new SMSGrup();

    private List<CustomerRegistration> customerRegistrationList = new LinkedList<>();
    private List<Sms> smsList = new LinkedList<>();
    private List<Sms> smsSizeList = new LinkedList<>();
    
     private List<GroupContact> groupContactList = new LinkedList<>();

    private SenderId senderId = new SenderId();
    private MessageTemplate selectedMessageTemplate;
    
    private MessagingType selectedMessagingType = MessagingType.TEXT_MESSAGING;
    private SMSType smsType = SMSType.SINGLE_SMS;
    private String connectionStatus;
    private String textMessage;
    
    private boolean flag = false;

    @PostConstruct
    private void init()
    {
        customerRegistrationList = smsService.getContactList();
        loadSmslog();
        
        smsSizeList = smsService.smsList();
    }
    
    public void loadSmslog()
    {
        smsList =smsService.loadSmslogList(smsType);
    }
    
    public void loadCustomers()
    {
        customerRegistrationList = customerService.getCustomerRegistrationList();
    }
    
    private void getConnection()
    {
        if(smsService.isInternetAccessVailable() == true)
        {
            connectionStatus = "Internet Access";
        }
        else
        {
            connectionStatus = "No Internet Access";
        }
    }
    
    public void selectMessagingType()
    {
        flag = selectedMessagingType == MessagingType.TEMPLATE_MESSAGING;
    }
    
    public void activateSenderId()
    {
        sms.setSenderId(senderId);
    }

    public void processMessage()
    {
        if (selectedCustomer == null)
        {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Please select contact"), null));
            return;
        }
        if (sms.getSenderId() == null)
        {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Please set sender ID"), null));
            return;
        }

        try
        {
            if (smsService.isInternetAccessVailable() == true)
            {
                clearSMS();
                
                ZenophSMS zsms = smsService.extractParams();

                // set message parameters.
                if (selectedMessagingType == MessagingType.TEMPLATE_MESSAGING)
                {
                    zsms.setMessage(selectedMessageTemplate.getTemplateText());

                    System.out.println("TEMPLATE_MESSAGING -- " + selectedMessageTemplate.getTemplateText());
                } else
                {
                    if(textMessage.isEmpty())
                    {
                         FacesContext.getCurrentInstance().addMessage(null,
                                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Please type a message"), null));
                        
                        return;
                    }
                    zsms.setMessage(textMessage);
                }
                
                String phoneNumber = selectedCustomer.getPhoneNumber();
                List<String> numbers = zsms.extractPhoneNumbers(phoneNumber);

                for (String number : numbers)
                {
                    zsms.addRecipient(number);
                }
                
                zsms.setSenderId(sms.getSenderId().getSenderId());
                

                List<String[]> response = zsms.submit();
                for (String[] destination : response)
                {
                    REQSTATUS reqstatus = REQSTATUS.fromInt(Integer.parseInt(destination[0]));
                    if (reqstatus == null)
                    {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("failed to send message"), null));
                        break;
                    } else
                    {
                        switch (reqstatus)
                        {
                            case SUCCESS:
                                saveMessage(zsms.getMessage());
                                break;
                            case ERR_INSUFF_CREDIT:
                                FacesContext.getCurrentInstance().addMessage(null,
                                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Insufficeint Credit"), null));
                            default:
                                FacesContext.getCurrentInstance().addMessage(null,
                                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Failed to send message"), null));
                                return;
                        }
                    }
                }

            } else
            {
                System.out.println("--------- INTERNET CONNECTION NOT AVAILABLE ----");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void processBulkMessage()
    {
        if (groupContactList.isEmpty())
        {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Please load contacts"), null));
            return;
        }
        
        if (sms.getSenderId() == null)
        {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Please set sender ID"), null));
            return;
        }

        try
        {
            if (smsService.isInternetAccessVailable() == true)
            {
                clearSMS();
                
                ZenophSMS zsms = smsService.extractParams();
                zsms.authenticate();

               // set message parameters.
                if (selectedMessagingType == MessagingType.TEMPLATE_MESSAGING)
                {
                    zsms.setMessage(selectedMessageTemplate.getTemplateText());
                    
                    System.out.println("TEMPLATE_MESSAGING -- " + selectedMessageTemplate.getTemplateText());
                } else
                {
                    if(textMessage.isEmpty())
                    {
                         FacesContext.getCurrentInstance().addMessage(null,
                                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Please type a message"), null));
                        
                        return;
                    }
                    zsms.setMessage(textMessage);
                }
                
                String phoneNumber = null;
                GroupContact gc=null;
                
                for (GroupContact groupContact : groupContactList)
                {
                    gc = groupContact;
                    phoneNumber = groupContact.getCustomerRegistration().getPhoneNumber();
                    
                    List<String> numbers = zsms.extractPhoneNumbers(phoneNumber);

                    for (String number : numbers)
                    {
                        zsms.addRecipient(number);
                    }
                }
                
                zsms.setSenderId(sms.getSenderId().getSenderId());

                List<String[]> response = zsms.submit();
                for (String[] destination : response)
                {
                    REQSTATUS reqstatus = REQSTATUS.fromInt(Integer.parseInt(destination[0]));
                    if (reqstatus == null)
                    {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("failed to send message"), null));
                        break;
                    }
                    else
                    {
                        switch (reqstatus)
                        {
                            case SUCCESS:
                                saveBulkMessage(zsms.getMessage(), gc);
                                
                                 FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.setMsg("Sending Bulk Message successful!"), null));
                                break;
                            case ERR_INSUFF_CREDIT:
                                FacesContext.getCurrentInstance().addMessage(null,
                                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Insufficeint Credit"), null));
                            default:
                                FacesContext.getCurrentInstance().addMessage(null,
                                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Failed to send message"), null));
                                return;
                        }
                    }
                }

            } else
            {
                System.out.println("---------Connection not Available ----");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } 
    }

    public void saveMessage(String smsMessage)
    {
        try
        {
            sms.setSmsTime(LocalDateTime.now());
            sms.setMessage(smsMessage);
            sms.setCustomerRegistration(selectedCustomer);
            sms.setsMSType(SMSType.SINGLE_SMS);
            sms.setUserAccount(appSession.getCurrentUser());
           if(crudApi.save(sms) != null)
           {
               FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.setMsg("SMS sent to "+selectedCustomer.getCustomerName()), null));
               
               System.out.println("SMS sent and saved -- ");
           }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
   
   public void saveBulkMessage(String smsMessage, GroupContact groupContact)
   {
      try
        {
                sms.genCode();
                sms.setSmsTime(LocalDateTime.now());
                sms.setMessage(smsMessage);
                sms.setCustomerRegistration(groupContact.getCustomerRegistration());
                sms.setsMSType(SMSType.BULK_SMS);
                sms.setUserAccount(appSession.getCurrentUser());
                
                if (crudApi.save(sms) != null)
                {
                    System.out.println("SMS sent and saved -- ");
                }  
           
        } catch (Exception e)
        {
            e.printStackTrace();
        }  
   }

    public void loadContactGroup()
    {
        groupContactList = smsService.getContactGroupList(smsGrup);
    }
    
    public void deleteSms(Sms sms)
    {
        try
        {
            if(crudApi.delete(sms))
            {
                smsList.remove(sms);
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.SUCCESS_MESSAGE, null)); 
            }
            else
            {
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.FAILED_MESSAGE, null)); 
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void clearSMS()
    {
        sms = new Sms();
        sms.setSenderId(senderId);
        sms.setUserAccount(appSession.getCurrentUser());
        SystemUtils.resetJsfUI();
    }

    public void manage(CustomerRegistration customer)
    {
        this.selectedCustomer = customer;
    }
   
    public Sms getSms()
    {
        return sms;
    }

    public void setSms(Sms sms)
    {
        this.sms = sms;
    }

    public List<Sms> getSmsList()
    {
        return smsList;
    }

    public String getConnectionStatus()
    {
        return connectionStatus;
    }
    
    public SenderId getSenderId()
    {
        return senderId;
    }

    public void setSenderId(SenderId senderId)
    {
        this.senderId = senderId;
    }

    public List<CustomerRegistration> getCustomerRegistrationList()
    {
        return customerRegistrationList;
    }
    
    public MessageTemplate getSelectedMessageTemplate()
    {
        return selectedMessageTemplate;
    }

    public void setSelectedMessageTemplate(MessageTemplate selectedMessageTemplate)
    {
        this.selectedMessageTemplate = selectedMessageTemplate;
    }

    public MessagingType getSelectedMessagingType()
    {
        return selectedMessagingType;
    }

    public void setSelectedMessagingType(MessagingType selectedMessagingType)
    {
        this.selectedMessagingType = selectedMessagingType;
    }

    public boolean isFlag()
    {
        return flag;
    }

    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }

    public String getTextMessage()
    {
        return textMessage;
    }

    public void setTextMessage(String textMessage)
    {
        this.textMessage = textMessage;
    }

    public SMSGrup getSmsGrup()
    {
        return smsGrup;
    }

    public void setSmsGrup(SMSGrup smsGrup)
    {
        this.smsGrup = smsGrup;
    }

    public List<GroupContact> getGroupContactList()
    {
        return groupContactList;
    }

    public SMSType getSmsType()
    {
        return smsType;
    }

    public void setSmsType(SMSType smsType)
    {
        this.smsType = smsType;
    }

    public CustomerRegistration getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(CustomerRegistration selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public List<Sms> getSmsSizeList()
    {
        return smsSizeList;
    }

 
}
