package lp.web.repositories;

import lp.web.entities.ProjectClassEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * The repository handling the project class entities
 */
@Repository
public interface ProjectClassRepository extends Neo4jRepository<ProjectClassEntity, UUID> {
}
