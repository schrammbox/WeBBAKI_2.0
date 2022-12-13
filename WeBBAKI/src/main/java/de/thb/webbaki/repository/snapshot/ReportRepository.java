package de.thb.webbaki.repository.snapshot;

import de.thb.webbaki.entity.Branch;
import de.thb.webbaki.entity.Sector;
import de.thb.webbaki.entity.snapshot.Report;
import de.thb.webbaki.entity.snapshot.Snapshot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;

@RepositoryDefinition(domainClass = Report.class, idClass = Long.class)
public interface ReportRepository extends CrudRepository<Report, Long> {
    Report findBySnapshotAndUser_Username(Snapshot snapshot, String username);
    Report findBySnapshotAndBranch(Snapshot snapshot, Branch branch);
    Report findBySnapshotAndSector(Snapshot snapshot, Sector sector);
    Report findBySnapshotAndUserIsNullAndBranchIsNullAndSectorIsNull(Snapshot snapshot);
}
