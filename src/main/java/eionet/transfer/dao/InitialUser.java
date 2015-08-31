/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Transfer 1.0
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.
 *
 * Contributor(s):
 */
package eionet.transfer.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

/**
 * Create the initial user.
 */
public class InitialUser {

    /**
     * Service for user management.
     */
    private UserManagementService userManagementService;

    public void setUserManagementService(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    /** Inital username. Injected from configuration. */
    private String initialUser;

    public void setInitialUser(String initialUser) {
        this.initialUser = initialUser;
    }

    private Log logger = LogFactory.getLog(InitialUser.class);

    /**
     * Adds new user to database when bean is constructed.
     */
    public void createUser() {
        if (initialUser == null || initialUser.trim().equals("")) {
            logger.warn("No initial user to create");
            return; // Nothing to do
        }
        if (!userManagementService.userExists(initialUser)) {
            ArrayList<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<SimpleGrantedAuthority>(1);
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            User userDetails = new User(initialUser, "", grantedAuthorities);
            userManagementService.createUser(userDetails);
        }
        logger.warn("Initial user " + initialUser + " created");
    }

}
