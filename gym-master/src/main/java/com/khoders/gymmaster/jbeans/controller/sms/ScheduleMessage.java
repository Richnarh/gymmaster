/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.khoders.gymmaster.jbeans.controller.sms;

import Zenoph.SMSLib.Enums.REQSTATUS;
import Zenoph.SMSLib.ZenophSMS;
import com.khoders.gymmaster.entities.CustomerRegistration;
import com.khoders.gymmaster.entities.sms.Sms;
import com.khoders.gymmaster.enums.SMSType;
import com.khoders.gymmaster.jbeans.controller.ScheduleTimer;
import com.khoders.gymmaster.listener.AppSession;
import com.khoders.gymmaster.services.CustomerService;
import com.khoders.gymmaster.services.SmsService;
import com.khoders.resource.jpa.CrudApi;
import com.khoders.resource.utilities.Msg;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author richa
 */

public class ScheduleMessage extends TimerTask
{
    @Inject private CustomerService customerService;
    @Inject private AppSession appSession;
    @Inject private CrudApi crudApi;
    @Inject private SmsService smsService;
    
    Timer timer = new Timer();
    
    @Override
    public void run()
    {
        Calendar calendar = Calendar.getInstance();
        timer.schedule(new TimerTask() {
        @Override
        public void run() {
            System.out.println("Running......100");
            expiredRegistrants();
            System.out.println("Running......"+calendar.getTime());
        }
//    }, calendar.getTime(), TimeUnit.HOURS.toMillis(8));
     }, calendar.getTime(), TimeUnit.MINUTES.toMillis(2));
    }
    
//    public static void main(String[] args)
//    {
//        ScheduleTimer scheduleTimer = new ScheduleTimer();
//        scheduleTimer.run();
//    }
    
    public void expiredRegistrants()
    {  
         System.out.println("User => "+ appSession.getCurrentUser());
        CustomerRegistration expiredCustomer = customerService.expiredSubscription();
     
        try
        {
            ZenophSMS zsms = smsService.extractParams();

            String clientMessage = "Hello " + expiredCustomer.getCustomerName() + ", \n"
                    + "kindly be reminded that your gym subscription has expired on " + expiredCustomer.getExpiryDate() + "." + " \n"
                    + "Kindly renew to have continuous access to the gym. \n" + "Thank you.";

            zsms.setMessage(clientMessage);
            
            List<String> numbers = zsms.extractPhoneNumbers(expiredCustomer.getPhoneNumber());
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
        System.out.println("User => "+ appSession.getCurrentUser());
        try
        {
                Sms newSms = new Sms();
                newSms.genCode();
                newSms.setSmsTime(LocalDateTime.now());
                newSms.setMessage(clientMessage);
                newSms.setsMSType(SMSType.SUBSCRIPTION_SMS);
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
    
}
