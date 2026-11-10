package com.company.singleresp;

public class UserPersistenceService {

    private final Store store;

    public UserPersistenceService(Store store) {
        this.store = store;
    }

    public void saveUser(User user) {
        store.store(user);
    }

    public User getUser(String id) {
        return store.getUser(id);
    }

    public void deleteUser(String id) {
        store.deleteUser(id);
    }
}
