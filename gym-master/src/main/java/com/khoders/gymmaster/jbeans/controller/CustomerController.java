/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.jbeans.controller;

import com.khoders.gymmaster.entities.Customer;
import com.khoders.gymmaster.listener.AppSession;
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
@Named(value = "customerController")
@SessionScoped
public class CustomerController implements Serializable{
    @Inject CrudApi crudApi;
    @Inject AppSession appSession;
    
    private Customer customer = new Customer();
    private List<Customer> customerList =  new LinkedList<>();
    
    private FormView pageView = FormView.listForm();
    private String optionText;
    
    @PostConstruct
    private void init()
    {
//        customerList = inventoryService.getCustomerList();
        
        clearCustomer();
    }
    
    public void initCLient()
    {
        clearCustomer();
        pageView.restToCreateView();
    }
    
    public void saveCustomer()
    {
        try 
        {
           customer.genCode();
          if(crudApi.save(customer) != null)
          {
              customerList = CollectionList.washList(customerList, customer);
              
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
    
    public void deleteCustomer(Customer customer)
    {
        try 
        {
          if(crudApi.delete(customer))
          {
              customerList.remove(customer);
              
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
    
    public void editCustomer(Customer customer)
    {
        pageView.restToCreateView();
       this.customer=customer;
       optionText = "Update";
    }
    
    public void clearCustomer() 
    {
        customer = new Customer();
        customer.setUserAccount(appSession.getCurrentUser());
        optionText = "Save Changes";
        SystemUtils.resetJsfUI();
    }
    
    public void closePage()
    {
       customer = new Customer();
       optionText = "Save Changes";
       pageView.restToListView();
    }
    public List<Customer> getCustomerList() {
        return customerList;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer bird) {
        this.customer = bird;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public FormView getPageView()
    {
        return pageView;
    }

    public void setPageView(FormView pageView)
    {
        this.pageView = pageView;
    }

}
