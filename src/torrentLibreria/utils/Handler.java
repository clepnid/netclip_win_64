package torrentLibreria.utils;

import torrentLibreria.torrentstream.TorrentInfoClone;

public class Handler {

    private final Looper looper;

    public Handler(Looper looper) {
        this.looper = looper;
    }

    public boolean post(Runnable runnable) {
        return looper.post(runnable);
    }
    

	public TorrentInfoClone postInfo(TorrentInfoClone torrentInfo, Runnable runnable) {
		// TODO Auto-generated method stub
		looper.post(runnable);
        return torrentInfo;
	}

}