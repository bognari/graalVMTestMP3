import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static int count = 1;

    public static void main(String[] args) throws InterruptedException {
        final StreamPlayer player = new StreamPlayer();
        final String path = "http://soundbible.com/grab.php?id=1751&type=mp3";

        player.addStreamPlayerListener(new StreamPlayerListener() {
            @Override
            public void opened(Object dataSource, Map<String, Object> properties) {

            }

            @Override
            public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {
                System.out.println(Arrays.toString(pcmData));
            }

            @Override
            public void statusUpdated(StreamPlayerEvent event) {
                if (event.getPlayerStatus() == Status.STOPPED) {
                    executorService.submit(() -> {
                        try {
                            System.out.println(String.format("########### NEW AUDIO (%d) ##############", count++));
                            player.open(new BufferedInputStream(new URL(path).openStream()));
                            player.play();
                        } catch (StreamPlayerException | IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });

        try {
            player.open(new BufferedInputStream(new URL(path).openStream()));
            player.play();
        } catch (StreamPlayerException | IOException e) {
            e.printStackTrace();
        }

        Thread.sleep(3 * 60 * 1_000);
    }
}
