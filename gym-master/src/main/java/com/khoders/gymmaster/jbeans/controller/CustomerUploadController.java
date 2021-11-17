/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.jbeans.controller;

import com.khoders.gymmaster.entities.CustomerRegistration;
import com.khoders.gymmaster.listener.AppSession;
import com.khoders.resource.enums.PaymentStatus;
import com.khoders.resource.jpa.CrudApi;
import com.khoders.resource.utilities.BeansUtil;
import com.khoders.resource.utilities.DateUtil;
import com.khoders.resource.utilities.Msg;
import com.khoders.resource.utilities.StringUtil;
import com.khoders.resource.utilities.SystemUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author khoders
 */
@Named(value = "customerUploadController")
@SessionScoped
public class CustomerUploadController implements Serializable
{
    @Inject private CrudApi crudApi;
    @Inject private AppSession appSession;
    
    private CustomerRegistration customerRegistration;
    private List<CustomerRegistration> successRegistrationList = new LinkedList<>();
    private List<CustomerRegistration> failedRegistrationList = new LinkedList<>();
    private UploadedFile file = null;
    
    public String getFileExtension(String filename) {
        if(filename == null)
        {
            return null;
        }
        return filename.substring(filename.lastIndexOf(".") + 1, filename.length());
    }
    
    public void uploadCustomers()
    {
        try
        {
               String extension = getFileExtension(file.getFileName());
               
               
//               if(!extension.equals(FileExtension.xls.name()) || !extension.equals(FileExtension.xlsx.name()))
//               {
//                 FacesContext.getCurrentInstance().addMessage(null, 
//                new FacesMessage(FacesMessage.SEVERITY_ERROR, Msg.setMsg("File not supported!"), null));  
//                 return;
//               }
                
                InputStream inputStream = file.getInputStream();
            try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
                XSSFSheet  sheet =  workbook.getSheetAt(0);
                
                sheet.removeRow(sheet.getRow(0));
                
                Iterator<Row> iterator = sheet.iterator();
                while(iterator.hasNext())
                {
                    customerRegistration = new CustomerRegistration();
                    Row currentRow = iterator.next();
                    
                    customerRegistration.setCustomerName(BeansUtil.objToString(currentRow.getCell(0)));
                    
                    String phoneNumber = BeansUtil.objToString(currentRow.getCell(1));
                    
                    phoneNumber = StringUtil.removeTrailingZero(phoneNumber);
                   
                    try
                    {
                        phoneNumber = new BigDecimal(phoneNumber).toEngineeringString();
                    } catch (Exception e)
                    {
                    }
                    
                    if(phoneNumber.length() >= 9)
                    {
                        if(phoneNumber.contains("+233") || phoneNumber.contains("233"))
                        {
                          customerRegistration.setPhoneNumber(phoneNumber);  
                        }
                        else
                        {
                            customerRegistration.setPhoneNumber("0"+phoneNumber);
                        }
                        
                        successRegistrationList.add(customerRegistration);
                    }
                    else
                    {
                        customerRegistration.setPhoneNumber(phoneNumber);   
                        failedRegistrationList.add(customerRegistration);
                    }           
                }
                
                System.out.println("failedRegistrationList size => "+failedRegistrationList.size());
                System.out.println("successRegistrationList size => "+successRegistrationList.size());
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void saveUpload()
    {
        try
        {
            if(crudApi.save(customerRegistration) != null)
            {
               successRegistrationList.forEach(customer ->{
                customer.genCode();
                customer.setUserAccount(appSession.getCurrentUser());
                crudApi.save(customer);
            }); 
               FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, Msg.setMsg("Customer uploads saved successful!"), null));   
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void clear()
    {
        successRegistrationList = new LinkedList<>();
        failedRegistrationList = new LinkedList<>();
        file = null;
        customerRegistration = new CustomerRegistration();
        SystemUtils.resetJsfUI();
    }
        
    public CustomerRegistration getCustomerRegistration()
    {
        return customerRegistration;
    }

    public void setCustomerRegistration(CustomerRegistration customerRegistration)
    {
        this.customerRegistration = customerRegistration;
    }
    
    public UploadedFile getFile()
    {
        return file;
    }

    public void setFile(UploadedFile file)
    {
        this.file = file;
    }

    public List<CustomerRegistration> getSuccessRegistrationList()
    {
        return successRegistrationList;
    }

    public List<CustomerRegistration> getFailedRegistrationList()
    {
        return failedRegistrationList;
    }
    
}
