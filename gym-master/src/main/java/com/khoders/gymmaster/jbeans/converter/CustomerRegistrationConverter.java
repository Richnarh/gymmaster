/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.khoders.gymmaster.jbeans.converter;

import com.khoders.gymmaster.entities.CustomerRegistration;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import org.omnifaces.converter.SelectItemsConverter;

/**
 *
 * @author richard
 */
@FacesConverter(forClass = CustomerRegistration.class)
public class CustomerRegistrationConverter extends SelectItemsConverter {
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value)
    {
        if (value == null)
        {
            return null;
        }
        return ((CustomerRegistration) value).getId();
    }
}
