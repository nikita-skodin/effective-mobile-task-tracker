package com.skodin.services.auth;

import com.skodin.entities.UserEntity;
import com.skodin.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service("CheckPermissionsService")
public class CheckPermissionsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckPermissionsService.class);

    public boolean permitAdmin(UserDetails userDetails) {
        boolean contains = ((UserEntity) userDetails).getRole().equals(Role.ADMIN);
        LOGGER.info("attempting to check admin permissions for user: {}, result: {}", userDetails.getUsername(), contains);
        return contains;
    }
}
