package services;

import org.springframework.stereotype.Component;
import services.domain.User;
import services.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * Created by gregorylaflash on 7/31/16.
 */
@Component
@Path("/user")
public class UserService {

    /**
     * GET /create  --> Create a new user and save it in the database.
     */
    @GET
    @Path("/create")
    public String create(@QueryParam("email") @NotNull String email,
                         @QueryParam("name") @NotNull String name) {
        String userId = "";
        try {
            User user = new User(email, name);
            userDao.save(user);
            userId = String.valueOf(user.getId());
        }
        catch (Exception ex) {
            return "Error creating the user: " + ex.toString();
        }
        return "User succesfully created with id = " + userId;
    }

    /**
     * GET /delete  --> Delete the user having the passed id.
     */
    @GET
    @Path("/delete")
    public String delete(@QueryParam("id") @NotNull long id) {
        try {
            User user = new User(id);
            userDao.delete(user);
        }
        catch (Exception ex) {
            return "Error deleting the user:" + ex.toString();
        }
        return "User succesfully deleted!";
    }

    /**
     * GET /get-by-email  --> Return the id for the user having the passed
     * email.
     */
    @GET
    @Path("/get-by-email")
    public String getByEmail(@QueryParam("email") @NotNull String email) {
        String userId = "";
        try {
            User user = userDao.findByEmail(email);
            userId = String.valueOf(user.getId());
        }
        catch (Exception ex) {
            return "User not found";
        }
        return "The user id is: " + userId;
    }

    /**
     * GET /update  --> Update the email and the name for the user in the
     * database having the passed id.
     */
    @GET
    @Path("/update")
    public String updateUser(@QueryParam("id") @NotNull long id,
                             @QueryParam("email") @NotNull String email,
                             @QueryParam("name") @NotNull String name) {
        try {
            User user = userDao.findOne(id);
            user.setEmail(email);
            user.setName(name);
            userDao.save(user);
        }
        catch (Exception ex) {
            return "Error updating the user: " + ex.toString();
        }
        return "User succesfully updated!";
    }

    // Private fields

    @Autowired
    private UserDao userDao;

}
