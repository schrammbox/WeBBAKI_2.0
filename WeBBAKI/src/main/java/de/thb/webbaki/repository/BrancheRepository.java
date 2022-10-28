package de.thb.webbaki.repository;

import de.thb.webbaki.entity.Branche;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;

@RepositoryDefinition(domainClass = Branche.class, idClass = Long.class)
public interface BrancheRepository extends CrudRepository<Branche, Long> {
    @Override
    List<Branche> findAll();

    Branche getBrancheByName(String name);
}
