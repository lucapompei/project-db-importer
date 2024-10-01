package lp.web.entities;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

/**
 * The entity representing the project method
 *
 * @param id,   the project method identifier
 * @param name, the project method name
 */
@Node
public record ProjectMethodEntity(
        @Id @GeneratedValue UUID id,
        String name
) {

    /**
     * Base constructor defining just the project method {@code name}
     *
     * @param name the project method name
     */
    public ProjectMethodEntity(String name) {
        this(UUID.randomUUID(), name);
    }

}
