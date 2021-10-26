/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.jbeans.controller;

import Zenoph.SMSLib.Enums.REQSTATUS;
import Zenoph.SMSLib.ZenophSMS;
import com.khoders.gymmaster.Pages;
import com.khoders.gymmaster.entities.CustomerRegistration;
import com.khoders.gymmaster.entities.UserAccount;
import com.khoders.gymmaster.entities.sms.Sms;
import com.khoders.gymmaster.enums.SMSType;
import com.khoders.gymmaster.jbeans.UserModel;
import com.khoders.gymmaster.listener.AppSession;
import com.khoders.gymmaster.services.CustomerService;
import com.khoders.gymmaster.services.SmsService;
import com.khoders.gymmaster.services.UserAccountService;
import com.khoders.resource.jpa.CrudApi;
import com.khoders.resource.utilities.DateRangeUtil;
import com.khoders.resource.utilities.Msg;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.util.Faces;

/**
 *
 * @author khoders
 */
@Named(value="loginController")
@RequestScoped
public class LoginController implements Serializable
{
    @Inject private AppSession appSession;
    @Inject private UserAccountService userAccountService;
    @Inject private CustomerService customerService;
    
    @Inject private CrudApi crudApi;
    @Inject private SmsService smsService;
    
    private String userEmail;
    private String password;
    
    private UserModel userModel = new UserModel();
    
    private final DateRangeUtil dateRange = new DateRangeUtil();
    
   
    
    public String doLogin()
    {
        try
        {
            userModel.setPhoneNumber(userEmail);
            userModel.setPassword(password);

            UserAccount account = userAccountService.login(userModel);

            if (account == null)
            {
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Wrong username or Password"), null));
                return null;
            }

            initLogin(account);
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
        
    public String initLogin(UserAccount userAccount)
    {
        try
        {
            if (userAccount == null)
            {
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("Wrong username or Password"), null));
                return null;
            }
            appSession.login(userAccount);
            Faces.redirect(Pages.index);

            // Processing expired registrations
             expiredRegistrants();
             
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String doLogout()
    {
        try
        {
            Faces.invalidateSession();
            Faces.logout();

            Faces.redirect(Pages.login);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public void expiredRegistrants()
    {
        List<CustomerRegistration> registrationList = customerService.getExpiredRegistrationList();
        
        for (CustomerRegistration  registration : registrationList) {
            if(!registration.isSentSms())
            {
                // send sms
                processExipredClient(registration.getPhoneNumber());
            }
        }
    }
    
    public void processExipredClient(String clientPhone)
    {  
        String clientMessage = "Please be reminded that your subscription has expired on "+LocalDate.now()+". Thank you.";
        try
        {
            ZenophSMS zsms = smsService.extractParams();

            List<String> numbers = zsms.extractPhoneNumbers(clientPhone);
            zsms.setMessage(password);

            for (String number : numbers)
            {
                zsms.addRecipient(number);
            }

            zsms.setSenderId("SWEATOUTGYM");

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
                            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.setMsg("Message sent"), null));
                                saveMessage(clientMessage);
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
        } catch (Exception e)
        {
            e.printStackTrace();
        }
            
    }
    
    public void saveMessage(String clientMessage)
    {
        Sms sms = new Sms();
        
        try
        {
            sms.setSmsTime(LocalDateTime.now());
            sms.setMessage(clientMessage);
            sms.setsMSType(SMSType.BULK_SMS);
            sms.setUserAccount(appSession.getCurrentUser());
            if(crudApi.save(sms) != null)
            {
               FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.setMsg("SMS sent to"), null));
               
               System.out.println("SMS sent and saved -- ");
           }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AppSession getAppSession()
    {
        return appSession;
    }

    public void setAppSession(AppSession appSession)
    {
        this.appSession = appSession;
    }

    public UserModel getUserModel()
    {
        return userModel;
    }

    public void setUserModel(UserModel userModel)
    {
        this.userModel = userModel;
    }
    
}