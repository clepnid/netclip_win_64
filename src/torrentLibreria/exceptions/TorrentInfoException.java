
package torrentLibreria.exceptions;

public class TorrentInfoException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TorrentInfoException(Throwable cause) {
        super("No torrent info could be found or read", cause);
    }

}
