package torrentLibreria.torrentstreamserver;

import java.io.File;

import torrentLibreria.torrentstream.Torrent;

public class TorrentSubs {
	private Torrent torrent;
    private File srtSubtitleFile;
    private File vttSubtitleFile;
    public TorrentSubs(Torrent torrent, File srtSubtitleFile, File vttSubtitleFile) {
		setTorrent(torrent);
		setSrtSubtitleFile(srtSubtitleFile);
		setVttSubtitleFile(vttSubtitleFile);
	}
	public Torrent getTorrent() {
		return torrent;
	}
	public void setTorrent(Torrent torrent) {
		this.torrent = torrent;
	}
	public File getSrtSubtitleFile() {
		return srtSubtitleFile;
	}
	public void setSrtSubtitleFile(File srtSubtitleFile) {
		if (srtSubtitleFile != null) {
			this.srtSubtitleFile = srtSubtitleFile;
		}
	}
	public File getVttSubtitleFile() {
		return vttSubtitleFile;
	}
	public void setVttSubtitleFile(File vttSubtitleFile) {
		if (vttSubtitleFile != null) {
			this.vttSubtitleFile = vttSubtitleFile;
		}
	}
}
