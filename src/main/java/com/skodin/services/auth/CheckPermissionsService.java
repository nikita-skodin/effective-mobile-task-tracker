package com.skodin.services.auth;

import com.skodin.entities.UserEntity;
import com.skodin.enums.Role;
import com.skodin.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service("CheckPermissionsService")
@RequiredArgsConstructor
public class CheckPermissionsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckPermissionsService.class);

    private final TaskRepository taskRepository;

    public boolean permitAdmin(UserDetails userDetails) {
        boolean contains = ((UserEntity) userDetails).getRole().equals(Role.ADMIN);
        LOGGER.info("attempting to check admin permissions for user: {}, result: {}", userDetails.getUsername(), contains);
        return contains;
    }

    public boolean permitUserToTask(UserDetails userDetails, Long taskId) {
        LOGGER.info("attempting to check permissions for user: {}", userDetails.getUsername());
        UserEntity user = (UserEntity) userDetails;
        Role role = user.getRole();

        if (role.equals(Role.ADMIN)) {
            LOGGER.info("result : true");
            return true;
        }

        Boolean exists = taskRepository.existsByIdAndAssigneeId(taskId, user.getId());
        LOGGER.info("result : {}", exists);
        return exists;
    }


}
