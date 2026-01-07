package morago.repository;

import morago.model.interpreter.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language,Long> {
    boolean existsByNameIgnoreCase(String name);
}
