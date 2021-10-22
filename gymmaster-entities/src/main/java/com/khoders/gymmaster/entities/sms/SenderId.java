/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.entities.sms;

import com.khoders.gymmaster.entities.UserAccountRecord;
import com.khoders.resource.utilities.SystemUtils;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author pascal
 */
@Entity
@Table(name = "sender_id")
public class SenderId extends UserAccountRecord implements Serializable
{
    @Column(name = "sender_id")
    private String senderId;

    public String getSenderId()
    {
        return senderId;
    }

    public void setSenderId(String senderId)
    {
        this.senderId = senderId;
    }
    
    @Override
    public String toString()
    {
        return senderId;
    }
    
    public boolean checkSenderIdSize()
    {
        return senderId.length() > 11;
    }

    public void genCode()
    {
        if (getSenderId() != null)
        {
            setSenderId(getSenderId());
        } else
        {
            setSenderId(SystemUtils.generateCode());
        }
    }
    
}
