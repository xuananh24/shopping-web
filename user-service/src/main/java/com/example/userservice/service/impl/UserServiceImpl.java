package com.example.userservice.service.impl;

import com.example.userservice.common.constant.RoleConstant;
import com.example.userservice.common.constant.StatusConstant;
import com.example.userservice.model.entity.User;
import com.example.userservice.model.entity.VerifyCode;
import com.example.userservice.model.log.UserLog;
import com.example.userservice.model.mapper.UserMapper;
import com.example.userservice.model.request.UserInfoRequest;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.repository.VerifyCodeRepository;
import com.example.userservice.service.UserService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final VerifyCodeRepository verifyCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final KafkaTemplate<String, User> kafkaTemplateForRegisterTopic;
    private final KafkaTemplate<String, UserLog> kafkaTemplateForLogTopic;

    public UserServiceImpl(UserRepository userRepository, VerifyCodeRepository verifyCodeRepository, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender, KafkaTemplate<String, User> kafkaTemplateForRegisterTopic, KafkaTemplate<String, UserLog> kafkaTemplateForLogTopic) {
        this.userRepository = userRepository;
        this.verifyCodeRepository = verifyCodeRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.kafkaTemplateForRegisterTopic = kafkaTemplateForRegisterTopic;
        this.kafkaTemplateForLogTopic = kafkaTemplateForLogTopic;
    }

    @Override
    public void register(UserInfoRequest userInfoRequest) {
        if (!userRepository.findUserByUsername(userInfoRequest.getUsername()).isPresent()) {
            User user = UserMapper.toEntity(userInfoRequest);
            user.setRole(RoleConstant.ROLE_USER);
            user.setStatus(StatusConstant.STATUS_UNVERIFIED);
            user.setPassword(passwordEncoder.encode(userInfoRequest.getPassword()));
            userRepository.save(user);

            User data = userRepository.findUserByUsername(userInfoRequest.getUsername()).get();
            Message<User> messageForRegister = MessageBuilder
                    .withPayload(data)
                    .setHeader(KafkaHeaders.TOPIC, "user-service-register")
                    .build();
            kafkaTemplateForRegisterTopic.send(messageForRegister);

            UserLog userLog = UserLog.builder()
                    .userId(user.getId())
                    .action("REGISTER")
                    .actionData(user.toString())
                    .timestamp(LocalDateTime.now())
                    .build();
            Message<UserLog> messageForLog = MessageBuilder
                    .withPayload(userLog)
                    .setHeader(KafkaHeaders.TOPIC, "user-service-log")
                    .build();
            kafkaTemplateForLogTopic.send(messageForLog);
        }
    }

    @Override
    public void login(String username, String ipAddress) {
        User user = userRepository.findUserByUsername(username).get();
        UserLog userLog = UserLog.builder()
                .userId(user.getId())
                .action("LOGIN")
                .actionData(ipAddress)
                .timestamp(LocalDateTime.now())
                .build();
        Message<UserLog> messageForLog = MessageBuilder
                .withPayload(userLog)
                .setHeader(KafkaHeaders.TOPIC, "user-service-log")
                .build();
        kafkaTemplateForLogTopic.send(messageForLog);
    }

    @Override
    public void sendVerifyCode(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            VerifyCode verifyCode = VerifyCode.builder()
                    .userId(userId)
                    .code(getRandomString())
                    .build();
            verifyCodeRepository.save(verifyCode);
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom("xuananh24121999@gmail.com");
            simpleMailMessage.setTo(user.getEmail());
            simpleMailMessage.setSubject("E-commerce verify account");
            simpleMailMessage.setText("This is verify code: " + verifyCode.getCode());
            javaMailSender.send(simpleMailMessage);

            UserLog userLog = UserLog.builder()
                    .userId(user.getId())
                    .action("GET-VERIFY-CODE")
                    .actionData("email: " + user.getEmail() + ", code: " + verifyCode)
                    .timestamp(LocalDateTime.now())
                    .build();
            Message<UserLog> messageForLog = MessageBuilder
                    .withPayload(userLog)
                    .setHeader(KafkaHeaders.TOPIC, "user-service-log")
                    .build();
            kafkaTemplateForLogTopic.send(messageForLog);
        }

    }

    @Override
    public boolean verifyByCode(Long userId, String code) {
        UserLog userLog = UserLog.builder()
                .userId(userId)
                .action("VERIFY-EMAIL")
                .timestamp(LocalDateTime.now())
                .build();
        Message<UserLog> messageForLog = MessageBuilder
                .withPayload(userLog)
                .setHeader(KafkaHeaders.TOPIC, "user-service-log")
                .build();

        Optional<VerifyCode> verifyCodeOptional = verifyCodeRepository.findVerifyCodeByUserIdAndCode(userId, code);
        if (verifyCodeOptional.isPresent()) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setStatus(StatusConstant.STATUS_VERIFIED);
                userRepository.save(user);

                userLog.setActionData("Success");
                kafkaTemplateForLogTopic.send(messageForLog);

                return true;
            }
        }
        userLog.setActionData("Fail, code invalid: " + code);
        kafkaTemplateForLogTopic.send(messageForLog);
        return false;
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }


    public String getRandomString() {
        String uuid = UUID.randomUUID().toString();
        String randomString = uuid.replaceAll("-", "");

        int maxLength = 10;
        if (randomString.length() > maxLength) {
            randomString = randomString.substring(0, maxLength);
        }

        return randomString;
    }
}
