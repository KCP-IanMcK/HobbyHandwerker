  import java.util.List;

public interface IUserDao {
    public List<User> select();
    public User select(int ID);
}
