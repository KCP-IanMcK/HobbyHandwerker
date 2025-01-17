import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/resources")
public class RestConfig {

  public Set<Class<?>> getClasses() {
    return new HashSet<Class<?>>(
      Arrays.asList(UserService.class));
  }
}

