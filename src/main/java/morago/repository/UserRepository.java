package morago.repository;

import morago.model.User;
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
    public Optional<User> getByPhoneNumber(String phoneNumber);
    public boolean existsByPhoneNumber(String phoneNumber);
}
