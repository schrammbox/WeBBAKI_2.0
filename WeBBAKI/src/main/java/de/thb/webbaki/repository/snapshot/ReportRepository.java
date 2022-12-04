package de.thb.webbaki.repository.snapshot;

import de.thb.webbaki.entity.snapshot.Report;
import de.thb.webbaki.entity.snapshot.Snapshot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = Report.class, idClass = Long.class)
public interface ReportRepository extends CrudRepository<Report, Long> {
    Report findBySnapshot_IdAndUser_Username(Long snapshotId, String username);
    Report findBySnapshot_IdAndBranch_Name(Long snapshotId, String branchName);
}
