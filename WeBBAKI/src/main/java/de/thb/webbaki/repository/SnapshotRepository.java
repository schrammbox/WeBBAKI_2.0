package de.thb.webbaki.repository;

import de.thb.webbaki.entity.Snapshot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;
import java.util.Optional;

@RepositoryDefinition(domainClass = Snapshot.class, idClass = Long.class)
public interface SnapshotRepository extends CrudRepository<Snapshot, Long> {

    List<Snapshot> findAll();
    List<Snapshot> findAllByOrderByIdDesc();
    Snapshot findTopByOrderByIdDesc();
    Optional<Snapshot> findById(Long id);


}
