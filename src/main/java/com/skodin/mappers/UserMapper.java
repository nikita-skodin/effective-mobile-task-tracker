package com.skodin.mappers;

import com.skodin.DTOs.UserDTO;
import com.skodin.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserEntity getEntity(UserDTO userDTO, UserEntity userEntity) {
        modelMapper.map(userDTO, userEntity);
        return userEntity;
    }

    public UserDTO getDTO(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDTO.class);
    }
}
