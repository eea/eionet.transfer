package eionet.transfer.model;

/**
 * Object to hold user and roles.
 */
public class Authorisation {
	
    private String userId;
    
    /** Comma separated list of roles. */
    private String[] authorisations;
    

    public Authorisation(String userId, String[] authorisations) {
        this.userId = userId;
        this.authorisations = authorisations;
    }

    public String getUserId() {
        return userId;
    }

    public String[] getAuthorisations() {
        return authorisations;
    }
    
}
