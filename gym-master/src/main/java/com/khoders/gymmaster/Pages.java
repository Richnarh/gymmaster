package com.khoders.gymmaster;

import org.omnifaces.util.Faces;

/**
 * Configures JAX-RS for the application.
 * @author Juneau
 */
public class Pages {
    public static String index = Faces.getRequestBaseURL() + "secured/index.xhtml?faces-redirect=true";
    public static String login = Faces.getRequestBaseURL();
}
