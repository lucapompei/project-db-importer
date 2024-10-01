package lp.web.services.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import lp.web.entities.ProjectClassEntity;
import lp.web.entities.ProjectMethodEntity;
import lp.web.repositories.ProjectClassRepository;
import lp.web.services.ProjectService;
import lp.web.utils.ParsingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class ProjectServiceImpl implements ProjectService {

    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);

    /**
     * The project class repository
     */
    @Autowired
    private ProjectClassRepository projectClassRepository;

    @Transactional
    @Override
    public void importProjectInDB(String projectFolder) throws IOException {
        // Obtaining project template files
        List<File> files = getProjectTemplateFiles(projectFolder);

        // Extracting the project information
        Collection<ProjectClassEntity> projectClasses = extractProjectData(projectFolder, files);

        // Storing data on DB
        updateProjectStatusOnDb(projectClasses);
    }

    /**
     * Identifies and extracts the files belonging the project template
     *
     * @param projectFolder, the project folderø
     * @return the project template files
     * @throws IOException, if any issue occurs during the file extraction phase
     */
    private List<File> getProjectTemplateFiles(String projectFolder) throws IOException {
        // Identifying files inside the project template folder
        Path folderPath = Paths.get(projectFolder);

        // Extracting identified files
        try (Stream<Path> filePathStream = Files.walk(folderPath)) {
            return filePathStream
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(e -> e.getName().endsWith(".java") && !e.getName().contains("package-info"))
                    .toList();
        }
    }

    /**
     * Extract the desired project data from the given {@code files}
     *
     * @param projectFolder, the project folder
     * @param files,         the files from which extract the information
     * @return the project data
     * @throws IOException, if the one of processed files is not valid or reachable
     */
    private Collection<ProjectClassEntity> extractProjectData(String projectFolder, List<File> files) throws IOException {
        // Configuring the parser
        ParsingUtils.configureParser(projectFolder);

        // Preparing maps to cache results and to identifies internal own classes
        Map<String, CompilationUnit> unitsClassesMap = new HashMap<>();
        Map<String, ProjectClassEntity> projectClassesMap = new HashMap<>();

        // Identifying own classes
        for (File file : files) {
            CompilationUnit compilationUnit = StaticJavaParser.parse(file);
            if (!compilationUnit.getTypes().isEmpty()) {

                // Composing class wrapper based on the current file
                String packageName = compilationUnit.getPackageDeclaration().map(PackageDeclaration::getNameAsString).orElse(null);
                String className = compilationUnit.getType(0).getNameAsString();
                String code = Files.readString(file.toPath());
                boolean isTest = file.getName().startsWith("Test");
                ProjectClassEntity projectClass = new ProjectClassEntity(packageName, className, code, isTest);

                // Updating maps
                unitsClassesMap.put(className, compilationUnit);
                projectClassesMap.put(className, projectClass);
            }
        }

        // Searching dependencies
        projectClassesMap.values().forEach(projectClass -> {

            // Extracting project information
            String className = projectClass.name();
            CompilationUnit compilationUnit = unitsClassesMap.get(className);

            // Composing methods based on the current file
            ParsingUtils.getNameExprFromMethodDeclarations(compilationUnit)
                    .map(ProjectMethodEntity::new)
                    .forEach(projectClass.methods()::add);

            // Composing dependencies based on the current file
            Stream.of(
                            ParsingUtils.getNameExprFromClassOrInterfaceTypes(compilationUnit),
                            ParsingUtils.getNameExprFromMethodExpressions(compilationUnit),
                            ParsingUtils.getNameExprFromFieldExpressions(compilationUnit)
                    )
                    .flatMap(e -> e)
                    .filter(projectClassesMap::containsKey)
                    .filter(dependency -> !dependency.equalsIgnoreCase(className))
                    .map(projectClassesMap::get)
                    .forEach(projectClass.dependencies()::add);

        });

        // Returned project information
        LOGGER.info("Obtained desired information from the project template files");
        return projectClassesMap.values();
    }

    /**
     * Based on the given {@code projectClasses}, refresh the project status on the DB
     *
     * @param projectClasses, the project information used to refresh the DB
     */
    private void updateProjectStatusOnDb(Collection<ProjectClassEntity> projectClasses) {
        if (projectClasses.isEmpty()) {
            LOGGER.warn("No project data obtained, unable to refresh the project status on DB");
        } else {
            projectClassRepository.deleteAll();
            projectClassRepository.saveAll(projectClasses);
            LOGGER.info("Project status updated on DB");
        }
    }

}
