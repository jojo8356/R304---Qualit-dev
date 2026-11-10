package com.company.singleresp;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

//Handles incoming JSON requests that work on User resource/entity
public class UserController {
  // Store used by controller
  private Store store = new Store();

  // Create a new user
  public String createUser(String userJson) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    UserValidator userValid = new UserValidator();

    User user = mapper.readValue(userJson, User.class);

    try {
      java.lang.reflect.Method validateMethod = UserValidator.class.getDeclaredMethod("isValidUser", User.class);
      validateMethod.setAccessible(true);
      Object isValid = validateMethod.invoke(userValid, user);

      if (!(Boolean) isValid) {
        return "ERROR";
      }
    } catch (Exception e) {
      return "ERROR";
    }

    store.store(user);

    return "SUCCESS";
  }

}
