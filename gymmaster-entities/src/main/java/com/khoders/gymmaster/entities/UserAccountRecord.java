/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.entities;

import com.khoders.resource.jpa.BaseModel;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author richa
 */
@MappedSuperclass
public class UserAccountRecord extends BaseModel
{
    @ManyToOne
    @JoinColumn(name = "user_account", referencedColumnName = "id")
    private UserAccount userAccount;
    
    public UserAccount getUserAccount()
    {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount)
    {
        this.userAccount = userAccount;
    }
}
