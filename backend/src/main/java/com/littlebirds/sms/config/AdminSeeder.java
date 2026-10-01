package com.littlebirds.sms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.littlebirds.sms.entity.Role;
import com.littlebirds.sms.entity.StaffUser;
import com.littlebirds.sms.repository.StaffUserRepository;

/**
 * Creates the first HEADMASTER from environment variables when the database has no staff accounts,
 * so no password is ever stored in the code or the repository. Later accounts are created in the app.
 */
@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final StaffUserRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final String staffId;
    private final String name;
    private final String username;
    private final String password;

    public AdminSeeder(StaffUserRepository staffRepository, PasswordEncoder passwordEncoder,
                       @Value("${sms.admin.id:ST001}") String staffId,
                       @Value("${sms.admin.name:Headmaster}") String name,
                       @Value("${sms.admin.username:}") String username,
                       @Value("${sms.admin.password:}") String password) {
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
        this.staffId = staffId;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (staffRepository.count() > 0) {
            return;
        }
        if (username.isBlank() || password.length() < 8) {
            log.warn("There are no staff accounts and nobody can log in. Set SMS_ADMIN_USERNAME and "
                    + "SMS_ADMIN_PASSWORD (at least 8 characters) and restart to create the first Headmaster.");
            return;
        }
        staffRepository.save(new StaffUser(staffId, name, username.trim(),
                passwordEncoder.encode(password), Role.HEADMASTER));
        log.info("Created the first HEADMASTER account '{}'.", username.trim());
    }
}
