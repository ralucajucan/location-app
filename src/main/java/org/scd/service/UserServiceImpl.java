package org.scd.service;

import org.scd.config.exception.BusinessException;
import org.scd.model.User;
import org.scd.model.dto.UserLoginDTO;
import org.scd.model.dto.UserRegisterDTO;
import org.scd.model.security.Role;
import org.scd.repository.RoleRepository;
import org.scd.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Objects;


public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User login(UserLoginDTO userLoginDTO) throws BusinessException {

        if (Objects.isNull(userLoginDTO)) {
            throw new BusinessException(401, "Body null !");
        }

        if (Objects.isNull(userLoginDTO.getEmail())) {
            throw new BusinessException(400, "Email cannot be null ! ");
        }

        if (Objects.isNull(userLoginDTO.getPassword())) {
            throw new BusinessException(400, "Password cannot be null !");
        }

        final User user = userRepository.findByEmail(userLoginDTO.getEmail());

        if (Objects.isNull(user)) {
            throw new BusinessException(401, "Bad credentials !");
        }

        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "Bad credentials !");
        }

        return user;
    }
    @Override
    public Long createUser(UserRegisterDTO userRegisterDTO) throws BusinessException {
        if (Objects.isNull(userRegisterDTO)) {
            throw new BusinessException(401, "Body null !");
        }
        validateInput(userRegisterDTO.getFirstName(),userRegisterDTO.getLastName(),
                userRegisterDTO.getEmail(),userRegisterDTO.getPassword());

        if (!Objects.isNull(userRepository.findByEmail(userRegisterDTO.getEmail()))) {
            throw new BusinessException(409, "This email address is already being used !");
        }

        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            throw new BusinessException(401, "Your password and confirmation password do not match !");
        }
        //New User:
        User user = new User();
        user.setFirstName(userRegisterDTO.getFirstName());
        user.setLastName(userRegisterDTO.getLastName());
        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        //Save
        Role role = roleRepository.findById(2L).orElseThrow(
                ()->new BusinessException(404, "Role not found!"));
        user.getRoles().add(role);
        User createUser = userRepository.save(user);
        return createUser.getId();
    }

    private void validateInput(String firstName, String lastName, String email, String password) throws BusinessException{
        if (Objects.isNull(firstName)) {
            throw new BusinessException(400, "First name cannot be null ! ");
        }
        if (Objects.isNull(lastName)) {
            throw new BusinessException(400, "Last name cannot be null !");
        }
        if (Objects.isNull(email)) {
            throw new BusinessException(400, "Email cannot be null ! ");
        }
        if (Objects.isNull(password)) {
            throw new BusinessException(400, "Password cannot be null !");
        }
    }
}
