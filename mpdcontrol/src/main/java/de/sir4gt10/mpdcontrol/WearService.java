package de.sir4gt10.mpdcontrol;

/**
 * Created by michael.schaefer on 19.04.17.
 */

import android.util.Log;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import de.sir4gt10.mpdcontrol.mpd.MPD;
import de.sir4gt10.mpdcontrol.mpd.MPDStatus;
import de.sir4gt10.mpdcontrol.mpd.exception.MPDServerException;

public class WearService extends WearableListenerService {

    private int lastVolume = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(WearService.class.getSimpleName(), "WEAR create");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(WearService.class.getSimpleName(), "WEAR destroy");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        Log.i(WearService.class.getSimpleName(), "WEAR Data changed " );
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.i(WearService.class.getSimpleName(), "WEAR Message " + messageEvent.getPath());
        String message = messageEvent.getPath();

        final MainApplication app = (MainApplication) this.getApplication();
        final MPD mpd = app.oMPDAsyncHelper.oMPD;

        switch (message) {
            case "/mpd/stop":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mpd.stop();
                        } catch (MPDServerException e) {
                            Log.w(MainApplication.TAG, e.getMessage());
                        }
                    }
                }).start();
                break;

            case "/mpd/next":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mpd.next();
                        } catch (MPDServerException e) {
                            Log.w(MainApplication.TAG, e.getMessage());
                        }
                    }
                }).start();
                break;

            case "/mpd/prev":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mpd.previous();
                        } catch (MPDServerException e) {
                            Log.w(MainApplication.TAG, e.getMessage());
                        }
                    }
                }).start();
                break;

            case "/mpd/play":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String state;
                        try {
                            state = mpd.getStatus().getState();
                            if (state.equals(MPDStatus.MPD_STATE_PLAYING) || state.equals(MPDStatus.MPD_STATE_PAUSED)) {
                                mpd.pause();
                            }
                            else {
                                mpd.play();
                            }
                        } catch (MPDServerException e) {
                            Log.w(MainApplication.TAG, e.getMessage());
                        }
                    }
                }).start();
                break;

            case "/mpd/shuffle":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mpd.setRandom(!mpd.getStatus().isRandom());
                        } catch (MPDServerException e) {
                            Log.w(MainApplication.TAG, e.getMessage());
                        }
                    }
                }).start();
                break;

            case "/mpd/repeat":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mpd.setRepeat(!mpd.getStatus().isRepeat());
                        } catch (MPDServerException e) {
                            Log.w(MainApplication.TAG, e.getMessage());
                        }
                    }
                }).start();
                break;

            case "/mpd/volup":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            lastVolume = mpd.getVolume();
                            if (lastVolume < 100) {
                                mpd.setVolume(lastVolume + 2);
                            }
                        } catch (MPDServerException e) {
                            Log.w(MainApplication.TAG, e.getMessage());
                        }
                    }
                }).start();
                break;

            case "/mpd/voldown":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            lastVolume = mpd.getVolume();
                            if (lastVolume > 0) {
                                mpd.setVolume(lastVolume - 2);
                            }
                        } catch (MPDServerException e) {
                            Log.w(MainApplication.TAG, e.getMessage());
                        }
                    }
                }).start();
                break;

            case "/mpd/mute":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mpd.getVolume() == 0) {
                                mpd.setVolume(lastVolume);
                            } else {
                                lastVolume = mpd.getVolume();
                                mpd.setVolume(0);
                            }
                        } catch (MPDServerException e) {
                            Log.w(MainApplication.TAG, e.getMessage());
                        }
                    }
                }).start();
                break;

            default:
                break;
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        Log.i(WearService.class.getSimpleName(), "WEAR Connected ");
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.i(WearService.class.getSimpleName(), "WEAR Disconnected");
    }

}