package de.sir4gt10.mpdcontrol;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.*;

import java.util.List;

public class MainActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient client;

    private ButtonEventHandler buttonEventHandler = null;

    private ImageButton buttonShuffle = null;
    private ImageButton buttonPrev = null;
    private ImageButton buttonPlayPause = null;
    //private ImageButton buttonStop = null;
    private ImageButton buttonNext = null;
    private ImageButton buttonRepeat = null;
    private ImageButton buttonVolUp = null;
    private ImageButton buttonVolDown = null;
    private ImageButton buttonMute = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.i(MainActivity.class.getSimpleName(), "API Connection failed");
                    }
                })
                .addApi(Wearable.API)
                .build();

        client.connect();

        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                buttonShuffle = (ImageButton) stub.findViewById(R.id.shuffle);
                buttonPrev = (ImageButton) stub.findViewById(R.id.prev);
                buttonPlayPause = (ImageButton) stub.findViewById(R.id.playpause);
                //buttonStop = (ImageButton) stub.findViewById(R.id.stop);
                buttonNext = (ImageButton) stub.findViewById(R.id.next);
                buttonRepeat = (ImageButton) stub.findViewById(R.id.repeat);
                buttonVolUp = (ImageButton) stub.findViewById(R.id.volume_up);
                buttonVolDown = (ImageButton) stub.findViewById(R.id.volume_down);
                buttonMute = (ImageButton) stub.findViewById(R.id.volume_off);

                buttonEventHandler = new ButtonEventHandler();

                buttonShuffle.setOnClickListener(buttonEventHandler);
                buttonPrev.setOnClickListener(buttonEventHandler);
                buttonPlayPause.setOnClickListener(buttonEventHandler);
                //buttonStop.setOnClickListener(buttonEventHandler);
                buttonNext.setOnClickListener(buttonEventHandler);
                buttonRepeat.setOnClickListener(buttonEventHandler);
                buttonVolUp.setOnClickListener(buttonEventHandler);
                buttonVolDown.setOnClickListener(buttonEventHandler);
                buttonMute.setOnClickListener(buttonEventHandler);
            }
        });
    }

    private void sendMessage(final String message, final byte[] payload) {
        Log.i(MainActivity.class.getSimpleName(), "WEAR try sending message: " + message);
        Wearable.NodeApi.getConnectedNodes(client).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                List<Node> nodes = getConnectedNodesResult.getNodes();
                for (Node node : nodes) {
                    Log.i(MainActivity.class.getSimpleName(), "WEAR sending " + message + " to " + node);
                    Wearable.MessageApi.sendMessage(client, node.getId(), message, payload).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Log.i(MainActivity.class.getSimpleName(), "WEAR Result " + sendMessageResult.getStatus());
                        }
                    });
                }

            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(client, this);
        sendMessage("/start", null);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(MainActivity.class.getSimpleName(), "Connection failed");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Wearable.MessageApi.removeListener(client, this);
        client.disconnect();
    }

    private class ButtonEventHandler implements Button.OnClickListener
    {
        @Override
        public void onClick(View v) {

            switch (v.getId())
            {
                case R.id.stop:
                    // currently disabled
                    sendMessage("/mpd/stop", null);
                    break;

                case R.id.next:
                    sendMessage("/mpd/next", null);
                    break;

                case R.id.prev:
                    sendMessage("/mpd/prev", null);
                    break;

                case R.id.playpause:
                    sendMessage("/mpd/play", null);
                    break;

                case R.id.shuffle:
                    sendMessage("/mpd/shuffle", null);
                    break;

                case R.id.repeat:
                    sendMessage("/mpd/repeat", null);
                    break;

                case R.id.volume_up:
                    sendMessage("/mpd/volup", null);
                    break;

                case R.id.volume_down:
                    sendMessage("/mpd/voldown", null);
                    break;

                case R.id.volume_off:
                    sendMessage("/mpd/mute", null);
                    break;
            }
        }
    }
}