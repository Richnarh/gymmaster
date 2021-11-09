/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.khoders.gymmaster.jbeans.controller;

import com.khoders.resource.utilities.DateRangeUtil;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author richa
 */
public class ScheduleTimer extends TimerTask
{
    LocalDate fromDate = LocalDate.now();
    LocalDate todayDate = LocalDate.now();
//    DateRangeUtil dateRange = new DateRangeUtil();
    
    Timer timer = new Timer();
    
    @Override
    public void run()
    {
        Calendar calendar = Calendar.getInstance();
        timer.schedule(new TimerTask() {
        @Override
        public void run() {
            System.out.println("Running......"+calendar.getTime());
        }
    }, 0, TimeUnit.SECONDS.toMillis(2));
//    }, calendar.getTime(), TimeUnit.HOURS.toMillis(8));
    }
    
    public LocalDate nextWeekDate()
    {
        int todayDateValue = LocalDate.now().getDayOfWeek().getValue();
        
        fromDate = LocalDate.now().plusDays(todayDateValue);
        
        System.out.println("today DateValue => "+todayDateValue);
        System.out.println("From Date => "+fromDate);
        
        return fromDate;
    }
    public LocalDate previousWeekDate()
    {
        int todayDateValue = LocalDate.now().getDayOfWeek().getValue();
        
        fromDate = LocalDate.now().minusDays(todayDateValue);
        
        System.out.println("today DateValue => "+todayDateValue);
        System.out.println("From Date => "+fromDate);
        
        return fromDate;
    }
 
    public static void main(String[] args)
    {
        ScheduleTimer scheduleTimer = new ScheduleTimer();
//        scheduleTimer.run();
        
        scheduleTimer.nextWeekDate();
    }
}
