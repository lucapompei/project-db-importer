package lp.web.entities;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The entity representing the project class
 *
 * @param id,           the project class identifier
 * @param packageName,  the project class package name
 * @param name,         the project class name
 * @param code,         the project class code
 * @param isTest,       the boolean indicating if it's a test class or not
 * @param methods,      the project class methods
 * @param dependencies, the project class dependencies
 */
@Node
public record ProjectClassEntity(
        @Id @GeneratedValue UUID id,
        String packageName,
        String name,
        String code,
        boolean isTest,
        @Relationship(type = "HAS_METHOD", direction = Relationship.Direction.OUTGOING)
        List<ProjectMethodEntity> methods,
        @Relationship(type = "DEPENDS_ON", direction = Relationship.Direction.OUTGOING)
        List<ProjectClassEntity> dependencies
) {

    /**
     * Base constructor defining just the project class {@code name}
     *
     * @param packageName, the project class package name
     * @param name,        the project class name
     * @param code,        the project class code
     * @param isTest,      the boolean indicating if it's a test class or not
     */
    public ProjectClassEntity(String packageName, String name, String code, boolean isTest) {
        this(UUID.randomUUID(), packageName, name, code, isTest, new ArrayList<>(), new ArrayList<>());
    }

}
