
package torrentLibreria.exceptions;

public class DirectoryModifyException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DirectoryModifyException() {
        super("Could not create or delete save directory");
    }

}
