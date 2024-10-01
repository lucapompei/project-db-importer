package lp.web.mappers;

import lp.web.constants.CommonConstants;
import lp.web.entities.ProjectClassEntity;
import lp.web.entities.ProjectMethodEntity;

import java.util.List;

/**
 * This mapper class expose project mapping utilities
 */
public class ProjectMapper {

    /**
     * Private constructor for the utility class
     */
    private ProjectMapper() {
        throw new IllegalAccessError(CommonConstants.STANDARD_MESSAGE_UTILITY_CLASS);
    }

    /**
     * Map the given {@code methods} into a list of {@link String} names
     *
     * @param methods, the methods to transform
     * @return the mapped request
     */
    private static List<String> toAIProjectMethodsRequests(List<ProjectMethodEntity> methods) {
        return methods
                .stream()
                .map(ProjectMethodEntity::name)
                .toList();
    }

    /**
     * Map the given {@code methods} into a list of {@link String}
     *
     * @param dependencies, the dependencies to transform
     * @return the mapped request
     */
    private static List<String> toAIProjectDependenciesRequests(List<ProjectClassEntity> dependencies) {
        return dependencies
                .stream()
                .map(dependency -> dependency.packageName() + ":" + dependency.name())
                .toList();
    }

}
