package torrentLibreria.torrentstreamserver;

import torrentLibreria.listeners.TorrentListener;

public interface TorrentServerListener extends TorrentListener {

    void onServerReady(String url);

}
