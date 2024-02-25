package com.example.foodthought.service;

import com.example.foodthought.common.dto.ResponseDto;
import com.example.foodthought.dto.user.CreateUserDto;
import com.example.foodthought.entity.User;
import com.example.foodthought.repository.UserRepository;
import com.example.foodthought.util.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;

    @Transactional
    public ResponseDto createUser(CreateUserDto createUserDto, MultipartFile file) throws IOException {
        Optional<User> findUser = userRepository.findByUserId(createUserDto.getUserId());

        if(findUser.isPresent()){
            throw new IllegalArgumentException("이미 존재하는 회원입니다");
        }

        String passwordEncryption = passwordEncoder.encode(createUserDto.getPassword());
        String fileUrl = storageService.uploadFile(file);

        User user = new User(createUserDto,passwordEncryption,fileUrl);
        userRepository.save(user);

        return new ResponseDto(HttpStatus.CREATED.value(),"회원 가입 성공");
    }
}