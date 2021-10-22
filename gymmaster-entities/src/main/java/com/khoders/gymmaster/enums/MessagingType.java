/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.enums;

/**
 *
 * @author richa
 */
public enum MessagingType
{
    TEMPLATE_MESSAGING("Template Messaging"),
    TEXT_MESSAGING("Text Messaging");
    
    private final String label;
    
    private MessagingType(String label)
    {
        this.label=label;
    }

    @Override
    public String toString()
    {
        return label;
    }
}
