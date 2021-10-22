/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.commons;

import com.khoders.gymmaster.enums.SMSType;
import com.khoders.resource.enums.Currency;
import com.khoders.resource.enums.PaymentMethod;
import com.khoders.resource.enums.PaymentStatus;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author pascal
 */
@Named(value = "commonClass")
@SessionScoped
public class CommonClass implements Serializable
{
    public List<PaymentStatus> getPaymentStatusList()
    {
        return Arrays.asList(PaymentStatus.values());
    }
    
    public List<PaymentMethod> getPaymentMethodList()
    {
        return Arrays.asList(PaymentMethod.values());
    }
    
    public List<Currency> getCurrencyList()
    {
        return Arrays.asList(Currency.values());
    }
    public List<SMSType> getSmsTypeList()
    {
        return Arrays.asList(SMSType.values());
    }
}
