package org.scd.service;

import org.scd.config.exception.BusinessException;
import org.scd.model.User;
import org.scd.model.dto.UserLoginDTO;
import org.scd.model.dto.UserRegisterDTO;

import java.util.List;

public interface UserService {
    /**
     * Get existing list of users from database
     * @return List<User>
     */
    List<User> getUsers();

    /**
     * Login into application
     * @param userLoginDTO - user information
     * @return Long
     */
    User login(final UserLoginDTO userLoginDTO) throws BusinessException;
    Long createUser(final UserRegisterDTO userRegisterDTO) throws BusinessException;
}
