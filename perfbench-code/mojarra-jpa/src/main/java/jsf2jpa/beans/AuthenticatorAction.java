package jsf2jpa.beans;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.Query;
import javax.persistence.EntityManager;

import jsf2jpa.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean(name="authenticator")
@RequestScoped
public class AuthenticatorAction extends SimpleAction
{
    protected static final Logger logger = LoggerFactory.getLogger(AuthenticatorAction.class);
    
    private User user;
    
    public User getUser()
    {
        if (user == null)
        {
            user = new User();
        }
        return user;
    }

    public String authenticate() {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select u from User u"
                + " where u.username = :username and u.password = :password");
        query.setParameter("username", user.getUsername());
        query.setParameter("password", user.getPassword());
        List<User> users = query.getResultList();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (users.size() == 0) {
            if (BookingApplication.LOG_ENABLED)
            {
                logger.error("Login failed");
            }
            facesContext.addMessage(null, new FacesMessage("Login failed"));
            return null;
        }
        User user = users.get(0);
        BookingSession session = facesContext.getApplication().
                evaluateExpressionGet(facesContext, "#{"+BookingSession.NAME+"}", 
                    BookingSession.class);
        session.setUser(user);
        if (BookingApplication.LOG_ENABLED)
        {
            logger.info("Login succeeded");
        }
        facesContext.addMessage(null, new FacesMessage("Login succeeded"));
        session.info("Welcome, " + user.getUsername());
        return "main";
    }
}
