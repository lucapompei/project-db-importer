package lp.web.services;

import java.io.IOException;

/**
 * This service handles project requests
 */
public interface ProjectService {

    /**
     * Imports the project data into the DB
     *
     * @param projectFolder, the project folder
     * @throws IOException, if any issue occurs during the refreshing phase
     */
    void importProjectInDB(String projectFolder) throws IOException;

}
