package app.util;



import app.controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.InputStream;

public class SpringFXMLLoader {
    private static Logger LOG = Logger.getLogger(SpringFXMLLoader.class);
    private static final ApplicationContext APPLICATION_CONTEXT = new ClassPathXmlApplicationContext("/spring-config.xml");

    public static IController load(String url){

        InputStream fxmlStream = null;
        try {
            fxmlStream = SpringFXMLLoader.class.getResourceAsStream(url);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Class.class.getResource(url));
            loader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> aClass) {
                    return APPLICATION_CONTEXT.getBean(aClass);
                }
            });


            Node view = (Node) loader.load(fxmlStream);
            IController controller = loader.getController();
            controller.setView(view);

            return controller;
        }

        catch (IOException e) {
            LOG.error("Can't load resource", e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        finally {
            if (fxmlStream != null) {
                try {
                    fxmlStream.close();
                }
                catch (IOException e) {
                    LOG.error("Can't close stream", e);
                }
            }
        }
    }
}

