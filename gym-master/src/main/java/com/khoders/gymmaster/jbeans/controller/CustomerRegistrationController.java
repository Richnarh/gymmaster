/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.jbeans.controller;

import com.khoders.gymmaster.entities.CustomerRegistration;
import com.khoders.gymmaster.listener.AppSession;
import com.khoders.gymmaster.services.CustomerService;
import com.khoders.resource.jpa.CrudApi;
import com.khoders.resource.utilities.CollectionList;
import com.khoders.resource.utilities.FormView;
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
@Named(value = "customerRegistrationController")
@SessionScoped
public class CustomerRegistrationController implements Serializable{
    @Inject CrudApi crudApi;
    @Inject AppSession appSession;
    @Inject CustomerService customerService;
    
    private CustomerRegistration customerRegistration = new CustomerRegistration();
    private List<CustomerRegistration> customerRegistrationList =  new LinkedList<>();
    
    private FormView pageView = FormView.listForm();
    private String optionText;
    
    @PostConstruct
    private void init()
    {
        customerRegistrationList = customerService.getCustomerRegistrationList();
        
        clearCustomerRegistration();
    }
    
    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
     public void delete() {
        addMessage("Confirmed", "Record deleted");
    }
     
    public void initRegistration()
    {
        clearCustomerRegistration();
        pageView.restToCreateView();
    }
    
    public void saveCustomerRegistration()
    {
        try 
        {
           customerRegistration.genCode();
          if(crudApi.save(customerRegistration) != null)
          {
              customerRegistrationList = CollectionList.washList(customerRegistrationList, customerRegistration);
              
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.SUCCESS_MESSAGE, null)); 
          }
          else
          {
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.FAILED_MESSAGE, null));
          }
          closePage();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    public void deleteCustomerRegistration(CustomerRegistration customerRegistration)
    {
        try 
        {
          if(crudApi.delete(customerRegistration))
          {
              customerRegistrationList.remove(customerRegistration);
              
              FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.SUCCESS_MESSAGE, null)); 
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
    
    public void editCustomerRegistration(CustomerRegistration customerRegistration)
    {
        pageView.restToCreateView();
       this.customerRegistration=customerRegistration;
       optionText = "Update";
    }
    
    public void clearCustomerRegistration() 
    {
        customerRegistration = new CustomerRegistration();
        customerRegistration.setUserAccount(appSession.getCurrentUser());
        optionText = "Save Changes";
        SystemUtils.resetJsfUI();
    }
    
    public void closePage()
    {
       customerRegistration = new CustomerRegistration();
       optionText = "Save Changes";
       pageView.restToListView();
    }

    public CustomerRegistration getCustomerRegistration()
    {
        return customerRegistration;
    }

    public void setCustomerRegistration(CustomerRegistration customerRegistration)
    {
        this.customerRegistration = customerRegistration;
    }

    public FormView getPageView()
    {
        return pageView;
    }

    public void setPageView(FormView pageView)
    {
        this.pageView = pageView;
    }

    public String getOptionText()
    {
        return optionText;
    }

    public void setOptionText(String optionText)
    {
        this.optionText = optionText;
    }

    public List<CustomerRegistration> getCustomerRegistrationList()
    {
        return customerRegistrationList;
    }
    
}
