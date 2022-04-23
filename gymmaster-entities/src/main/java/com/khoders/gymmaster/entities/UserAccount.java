package com.khoders.gymmaster.entities;

import com.khoders.resource.jpa.BaseModel;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author 
 */
@Entity
@Table(name = "user_account")
public class UserAccount extends BaseModel implements Serializable{

  @Column(name = "fullname")
  private String fullname;
  
  @Column(name = "email_address")
  private String email;
  
  @Column(name = "phone_number")
  private String phoneNumber;
  
  @Column(name = "password")
  private String password;
  
  @Column(name = "address")
  private String address;
  
  @Transient
  private String password2;

    public String getFullname()
    {
        return fullname;
    }

    public void setFullname(String fullname)
    {
        this.fullname = fullname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    @Override
    public String toString()
    {
        return phoneNumber + " - "+ fullname;
    }
  
    
}
