package ru.kata.spring.boot_security.demo.service;


import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void saveAndUpdateUser(User user) {
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (Role role : user.getRoles()) {
                Role existingRole = roleRepository.findByRoleName(role.getRoleName());
                if (existingRole != null) {
                    roles.add(existingRole);
                } else {
                    roles.add(role);
                }
            }
            user.setRoles(roles);
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User getUserForId(long id) {
        User user = null;
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            user = byId.get();
        }
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("There is no user with this LOGIN: %s", username));
        }
        Hibernate.initialize(user.getRoles());

        return user;
    }
}
