package Com.base.videorecording;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class DesktopScreenrecorder {

    private static final Logger log = LogManager.getLogger(DesktopScreenrecorder.class);

    private static ScreenRecorder screenRecorder;

    // create / start recording the desktop into a movie file (.avi)
    public static void createMovieFile(String movieFolderPath) {
        try {
            File folder = new File(movieFolderPath);
            if (!folder.exists()) folder.mkdirs();

            GraphicsConfiguration gc = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();

            Rectangle screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

            screenRecorder = new ScreenRecorder(
                    gc,
                    screenSize,
                    new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                            QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
                    null,
                    folder);

            screenRecorder.start();
            log.info("Screen recording started -> " + movieFolderPath);
        } catch (IOException | AWTException e) {
            log.error("createMovieFile failed: " + e.getMessage());
        }
    }

    // stop recording and save the movie file
    public static void stopRecording() {
        try {
            if (screenRecorder != null) {
                screenRecorder.stop();
                log.info("Screen recording stopped and saved");
            }
        } catch (IOException e) {
            log.error("stopRecording failed: " + e.getMessage());
        }
    }
}