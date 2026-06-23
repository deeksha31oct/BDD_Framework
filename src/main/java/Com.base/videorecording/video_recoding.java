package Com.base.videorecording;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;

import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class video_recoding {

    private static final Logger log = LogManager.getLogger(video_recoding.class);

    private static ScreenRecorder screenRecorder;

    // start recording the desktop into the given folder
    public static void startRecording(String folderPath) {
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            GraphicsConfiguration gc = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();

            Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

            screenRecorder = new ScreenRecorder(
                    gc,
                    screen,
                    new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                            QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
                            FrameRateKey, Rational.valueOf(30)),
                    null,
                    folder);

            screenRecorder.start();
            log.info("Recording started -> " + folderPath);
        } catch (Exception e) {
            log.error("startRecording failed: " + e.getMessage());
        }
    }

    // stop recording and save the file
    public static void stopRecording() {
        try {
            if (screenRecorder != null) {
                screenRecorder.stop();
                log.info("Recording stopped and saved");
            }
        } catch (Exception e) {
            log.error("stopRecording failed: " + e.getMessage());
        }
    }
}
