package vn.tuannpa.coursefpt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.tuannpa.coursefpt.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    List<User> findAll();

    User save(User user);
}
