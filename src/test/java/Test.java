import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private int count = 1;

    private CopyOnWriteArrayList<String> content = new CopyOnWriteArrayList<>();

    @org.junit.Test
    public void test() throws InterruptedException, IOException {
        final StreamPlayer player = new StreamPlayer();
        final String path = "http://soundbible.com/grab.php?id=1751&type=mp3";

        player.addStreamPlayerListener(new StreamPlayerListener() {
            @Override
            public void opened(Object dataSource, Map<String, Object> properties) {

            }

            @Override
            public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {
                content.add(Arrays.toString(pcmData));
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

        Thread.sleep(1 * 5 * 1_000);


        Files.newBufferedWriter(Paths.get("out.txt"), StandardOpenOption.CREATE).write(String.join("\n", content));
        System.out.println("out.txt created");
    }
}
