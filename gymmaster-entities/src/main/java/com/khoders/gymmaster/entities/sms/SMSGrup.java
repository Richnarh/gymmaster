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
@Table(name = "sms_group")
public class SMSGrup extends UserAccountRecord implements Serializable
{

    @Column(name = "group_id")
    private String groupId;

    @Column(name = "group_name")
    private String groupName;

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    @Override
    public String toString()
    {
        return groupName;
    }

    public void genCode()
    {
        if (getGroupId() != null)
        {
            setGroupId(getGroupId());
        } else
        {
            setGroupId(SystemUtils.generateCode());
        }
    }
    
}
