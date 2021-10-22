/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khoders.gymmaster.listener;

import com.khoders.gymmaster.Pages;
import java.util.Date;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.inject.Inject;
import org.omnifaces.util.Faces;

/**
 *
 * @author pascal
 */
public class SessionListener implements PhaseListener
{

    @Inject
    private AppSession appSession;

    public SessionListener()
    {
    }

    @Override
    public void beforePhase(PhaseEvent event)
    {
        if (event.getPhaseId() == PhaseId.RENDER_RESPONSE)
        {
            String viewId = event.getFacesContext().getViewRoot().getViewId();
            System.out.println(viewId);
            if (!viewId.contains("secured"))
            {
                return;
            }
            
            if (appSession != null)
            {
                if (appSession.getCurrentUser() == null)
                {
                    try
                    {
                        System.out.println("redirecting.... " + new Date());
                        Faces.redirect(Pages.login);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    public PhaseId getPhaseId()
    {
        return PhaseId.ANY_PHASE;
    }

    @Override
    public void afterPhase(PhaseEvent pe)
    {

    }
}
