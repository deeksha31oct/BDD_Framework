package Com.base.utilites;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FilesUtility {

    private static final Logger log = LogManager.getLogger(FilesUtility.class);

    // ===================================================================
    //  LISTING
    // ===================================================================

    // all file NAMES (not folders) directly inside a folder
    public static List<String> getAllFileNameFolder(String folderPath) {
        List<String> names = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) names.add(f.getName());
            }
        }
        log.info("Files in " + folderPath + ": " + names);
        return names;
    }

    // all sub-directories inside a folder
    public static List<String> getSubDir(String folderPath) {
        List<String> dirs = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) dirs.add(f.getName());
            }
        }
        return dirs;
    }

    // the most recently modified file in a folder (e.g. newest download / report)
    public static File getLatestFileFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles(File::isFile);
        if (files == null || files.length == 0) {
            log.warn("No files found in: " + folderPath);
            return null;
        }
        return Arrays.stream(files)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
    }

    // ===================================================================
    //  DELETE
    // ===================================================================

    // delete a single file
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        boolean deleted = file.delete();
        log.info("deleteFile " + filePath + " -> " + deleted);
        return deleted;
    }

    // delete a folder and EVERYTHING inside it (recursive)
    public static boolean deleteFolder(String folderPath) {
        File folder = new File(folderPath);
        return deleteRecursively(folder);
    }

    private static boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) deleteRecursively(child);
            }
        }
        return file.delete();
    }

    // ===================================================================
    //  CREATE
    // ===================================================================

    // create a new file only if it does not already exist
    public static boolean createNewFileIfNotExist(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                boolean created = file.createNewFile();
                log.info("createNewFile " + filePath + " -> " + created);
                return created;
            }
            log.info("File already exists: " + filePath);
            return false;
        } catch (IOException e) {
            log.error("createNewFile failed: " + e.getMessage());
            return false;
        }
    }

    // create a directory (and any missing parent directories)
    public static boolean createDirectory(String dirPath) {
        File dir = new File(dirPath);
        boolean created = dir.mkdirs();
        log.info("createDirectory " + dirPath + " -> " + created);
        return created;
    }

    // return a FileWriter for the given path (caller must close it)
    public static FileWriter createFileWriter(String filePath, boolean append) throws IOException {
        return new FileWriter(filePath, append);
    }

    // ===================================================================
    //  READ / WRITE CONTENT
    // ===================================================================

    // write content to a file (overwrites). creates the file if missing.
    public static void writeContentToFile(String filePath, String content) {
        try (FileWriter fw = new FileWriter(filePath, false)) {  // false = overwrite
            fw.write(content);
            log.info("Wrote content to: " + filePath);
        } catch (IOException e) {
            log.error("writeContentToFile failed: " + e.getMessage());
        }
    }

    // read the whole file as a single String
    public static String readFileAsString(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            log.info("Read file: " + filePath);
            return content;
        } catch (IOException e) {
            log.error("readFileAsString failed: " + e.getMessage());
            return null;
        }
    }

    // ===================================================================
    //  TEMP DIRECTORY
    // ===================================================================

    // OS temp directory path (e.g. C:\Users\INDIAN\AppData\Local\Temp\)
    public static String getSystemTempDirectoryPath() {
        return System.getProperty("java.io.tmpdir");
    }

    // delete folders in the system temp dir whose name starts with 'prefix'
    // (e.g. Selenium "scoped_dir..." chrome profile leftovers)
    public static void deleteScopedDirectoryFilesFromTemp(String prefix) {
        File tempDir = new File(getSystemTempDirectoryPath());
        File[] files = tempDir.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.getName().startsWith(prefix)) {
                boolean deleted = deleteRecursively(f);
                log.info("Deleted temp item " + f.getName() + " -> " + deleted);
            }
        }
    }
}

//FilesUtility.getAllFileNameFolder("C:/Downloads");
//FilesUtility.getSubDir("C:/Downloads");
//File latest = FilesUtility.getLatestFileFolder("C:/Downloads");   // newest file
//FilesUtility.deleteFile("C:/Downloads/old.pdf");
//FilesUtility.deleteFolder("target/screenshots");
//FilesUtility.createNewFileIfNotExist("target/run.log");
//FilesUtility.createDirectory("target/screenshots");
//FilesUtility.writeContentToFile("target/run.log", "started");
//String text = FilesUtility.readFileAsString("target/run.log");
//FilesUtility.getSystemTempDirectoryPath();
//FilesUtility.deleteScopedDirectoryFilesFromTemp("scoped_dir");   // clean Chrome temp profiles
//