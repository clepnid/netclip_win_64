
package torrentLibreria.exceptions;

public class NotInitializedException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotInitializedException() {
        super("TorrentStreamer is not initialized. Call init() first before getting an instance.");
    }

}
