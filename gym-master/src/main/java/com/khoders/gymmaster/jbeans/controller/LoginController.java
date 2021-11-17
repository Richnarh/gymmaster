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
import com.khoders.resource.utilities.DateUtil;
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
//             expiredRegistrants();
             
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
        CustomerRegistration expiredCustomer = customerService.expiredSubscription();
     
        System.out.println("Customer Name => "+expiredCustomer.getCustomerName());
        System.out.println("Customer Phone Number => "+expiredCustomer.getPhoneNumber());
        
        try
        {
            ZenophSMS zsms = smsService.extractParams();
            
            LocalDate formatedDate = DateUtil.parseLocalDateWithPattern(DateUtil.parseLocalDateString(expiredCustomer.getExpiryDate(), "dd/MM/yyyy"), "dd/MM/yyyy") ;
            
            String clientMessage = "Hello " + expiredCustomer.getCustomerName().trim() + ", \n"
                    + "kindly be reminded that your gym subscription expire on " + formatedDate + "." + " \n"
                    + "Kindly renew to have continuous access to the gym. \n" + "Thank you.";

            zsms.setMessage(clientMessage);
            
            List<String> numbers = zsms.extractPhoneNumbers(expiredCustomer.getPhoneNumber().trim());
            for (String number : numbers)
            {
                zsms.addRecipient(number);
            }

            zsms.setSenderId("SWEATOUTGYM");

//            List<String[]> response = null;
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
                            saveMessage(zsms.getMessage(),expiredCustomer);
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
    
    public void saveMessage(String clientMessage, CustomerRegistration customerRegistration)
    {
        try
        {
                Sms newSms = new Sms();
                newSms.genCode();
                newSms.setSmsTime(LocalDateTime.now());
                newSms.setMessage(clientMessage);
//                newSms.setsMSType(SMSType.SUBSCRIPTION_SMS);
                newSms.setUserAccount(appSession.getCurrentUser());
                newSms.setCustomerRegistration(customerRegistration);
                if(crudApi.save(newSms) != null)
                {
                    CustomerRegistration cr = crudApi.find(CustomerRegistration.class, customerRegistration.getId());
                    cr.setSentSms(true);
                    crudApi.save(cr);
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