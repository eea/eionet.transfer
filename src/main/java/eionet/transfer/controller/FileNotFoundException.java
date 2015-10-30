package eionet.transfer.controller;


/**
 * An application-specific file-not-found exception. 
 */
public class FileNotFoundException extends RuntimeException {

        public FileNotFoundException(String uuid) {
                super(uuid + " not found");
        }
}
