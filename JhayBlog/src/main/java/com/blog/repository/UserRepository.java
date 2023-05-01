package com.blog.repository;

import com.blog.enums.UserStatus;
import com.blog.model.Users;
import com.blog.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {

    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Page<Users> findAllByUserStatus(UserStatus userStatus, Pageable pageable);
    Page<Users> findAllByRolesContaining(Role role, Pageable pageable);

}
