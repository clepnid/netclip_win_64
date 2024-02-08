package torrentLibreria.torrentstream;

import org.libtorrent4j.TorrentInfo;

public class TorrentInfoClone{

	private TorrentInfo torrentInfo;
	private Boolean introducido, fallo;
	
	TorrentInfoClone(){
		introducido = false;
		fallo = false;
	}

	public TorrentInfo getTorrentInfo() {
		return torrentInfo;
	}

	public void setTorrentInfo(TorrentInfo torrentInfo) {
		this.torrentInfo = torrentInfo;
	}

	public Boolean getIntroducido() {
		return introducido;
	}

	public void setIntroducido(Boolean introducido) {
		this.introducido = introducido;
	}

	public Boolean getFallo() {
		return fallo;
	}

	public void setFallo(Boolean fallo) {
		this.fallo = fallo;
	}
	
	
}
