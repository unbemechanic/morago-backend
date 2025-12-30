package morago.repository;

import morago.model.interpreter.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language,Long> {
    boolean existsByNameIgnoreCase(String name);
}
