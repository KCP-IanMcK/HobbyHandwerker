import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.*;

public class UserDao implements IUserDao {
    private Logger logger = LogManager.getLogger(UserService.class);

    @Override
    public List<User> select() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
        }
        List<User> user = new ArrayList<>();
        try (Connection con = getConnection("jdbc:mysql://localhost:3306/hobbyhandwerker", "u_adm", "the_password")) {

            try (Statement stmt = con.createStatement()) {
                String tableSql = "SELECT * from user";
                try (ResultSet resultSet = stmt.executeQuery(tableSql)) {

                    while (resultSet.next()) {

                        User u = new User();
                        u.setId(resultSet.getInt("userID"));
                        u.setName(resultSet.getString("name"));
                        u.setEmail(resultSet.getString("email"));
                        u.setPassword(resultSet.getString("password"));

                        stmt.close();
                        con.close();
                    }
                } catch (Exception e) {
                    logger.log(Level.ERROR, e.getMessage());
                    con.close();
                }
            } catch (Exception e) {
                logger.log(Level.ERROR, e.getMessage());
                return null;
            }
            return user;
        } catch (Exception e) {
            logger.log(Level.ERROR, e.getMessage());
            return null;
        }
    }

    @Override
    public User select(int ID) {
        return null;
    }
}
