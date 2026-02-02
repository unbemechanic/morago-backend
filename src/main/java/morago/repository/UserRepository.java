package morago.repository;

import morago.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
        select u from User u
        left join fetch u.roles
        where u.phoneNumber = :phoneNumber
    """)
    Optional<User> getByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findUserById(Long id);

    Optional<User> findFirstByRoles_Name(String roleEnum);

    @Query("select u from User u")
    Page<User> findAllUsers(Pageable pageable);
}
