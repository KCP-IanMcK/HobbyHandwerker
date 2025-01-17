import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/userService")
public class UserService {

  private IUserDao dao = new UserDao();

  public void setDao(IUserDao dao) {
    this.dao = dao;
  }

  @RolesAllowed({"ADMIN", "USER"})
  @GET
  @Path("/alleUser")
  @Produces({MediaType.APPLICATION_JSON})
  public Response alleUser() {
    List<User> user = new ArrayList<>();

    user.addAll(dao.select());
    if (user.size() > 0) {
      return Response.ok(user).build();
    } else {
      return Response.status(Status.NOT_FOUND).build();
    }
  }
}
