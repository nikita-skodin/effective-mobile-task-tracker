package com.skodin.services.auth;

import com.skodin.entities.UserEntity;
import com.skodin.enums.Role;
import com.skodin.services.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Log4j2
@Service("CheckPermissionsService")
@RequiredArgsConstructor
public class CheckPermissionsService {
    private final TaskService taskService;

    public boolean permitAdmin(UserDetails userDetails) {
        boolean contains = ((UserEntity) userDetails).getRole().equals(Role.ADMIN);
        log.info("attempting to check admin permissions for user: {}, result: {}", userDetails.getUsername(), contains);
        return contains;
    }

    public boolean permitUserToTask(UserDetails userDetails, Long taskId) {
        log.info("attempting to check permissions for user: {}", userDetails.getUsername());
        UserEntity user = (UserEntity) userDetails;
        Role role = user.getRole();

        if (role.equals(Role.ADMIN)) {
            log.info("result : true");
            return true;
        }

        Boolean exists = taskService.existsByIdAndAssigneeId(taskId, user.getId());
        log.info("result : {}", exists);
        return exists;
    }


}
