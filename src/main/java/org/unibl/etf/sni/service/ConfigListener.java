package org.unibl.etf.sni.service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigListener implements ServletContextListener {
    private static Properties properties;
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String cfgfile = servletContextEvent.getServletContext().getInitParameter("config_file");
        InputStream input = servletContextEvent.getServletContext().getResourceAsStream(cfgfile);
        try {
            if(input!=null){
                Properties prop=new Properties();

                prop.load(input);
                properties=prop;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Properties getProperties() {
        return properties;
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {

    }
}