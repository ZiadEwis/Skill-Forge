package service;


import test.database.JsonDatabaseManager;
import test.model.User;

public class UserService {
    private final JsonDatabaseManager dbManager;
    
    public UserService(JsonDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
  
    public User getUserById(String userId) {
        return dbManager.getUserById(userId);
    }
    

    public User getUserByEmail(String email) {
        return dbManager.getUserByEmail(email);
    }
    

    public void updateUser(User user) {
        dbManager.updateUser(user);
    }
}