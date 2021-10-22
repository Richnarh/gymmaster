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
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 *
 * @author pascal
 */
@Entity
@Table(name = "message_template")
public class MessageTemplate extends UserAccountRecord implements Serializable
{

    @Column(name = "tempalate_id")
    private String templateId = SystemUtils.generateCode();

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "template_text")
    @Lob
    private String templateText;

    public String getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public String getTemplateText()
    {
        return templateText;
    }

    public void setTemplateText(String templateText)
    {
        this.templateText = templateText;
    }

    @Override
    public String toString()
    {
        return templateName;
    }

    public void genCode()
    {
        if (getTemplateId() != null)
        {
            setTemplateId(getTemplateId());
        } else
        {
            setTemplateId(SystemUtils.generateCode());
        }
    }
    
}
