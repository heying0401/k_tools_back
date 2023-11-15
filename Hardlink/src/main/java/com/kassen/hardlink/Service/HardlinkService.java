package com.kassen.hardlink.Service;

import com.kassen.hardlink.Mapper.SyncMapper;
import com.kassen.hardlink.POJO.SyncOperation;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
public class HardlinkService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ConcurrentHashMap<Integer, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private static final Logger logger = Logger.getLogger(HardlinkService.class.getName());
    private static FileHandler fileHandler;

    static {
        try {
            // Configure the logger with handler and formatter
            fileHandler = new FileHandler("/home/h-yu/Documents/synclogs.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processSyncOp(SyncOperation syncOperation) throws Exception {
        long interval = syncOperation.getDurationSeconds();

        ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            try {
                performSync(syncOperation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 0, interval, TimeUnit.SECONDS);

        scheduledTasks.put(syncOperation.getId(), scheduledFuture); // Store the future for later reference
    }

    public void performSync(SyncOperation syncOperation) throws IOException {
        Path rootDir = Paths.get(syncOperation.getRoot());
        Path targetDir = Paths.get(syncOperation.getTarget());

            // Traverse the root directory
        Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // Construct the corresponding path in the target directory
                Path relativePath = rootDir.relativize(file);
                Path targetPath = targetDir.resolve(relativePath);

                // If the file doesn't exist in the target directory or is older than the source file, copy it
                if (!Files.exists(targetPath) || Files.getLastModifiedTime(file).compareTo(Files.getLastModifiedTime(targetPath)) > 0) {
                    // Ensure the parent directory exists
                    Files.createDirectories(targetPath.getParent());
                    // Copy the file
                    Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
//                        System.out.println("Copied: " + file + " to " + targetPath);
                    logger.log(Level.INFO, "Copied: {0} to {1}", new Object[]{file, targetPath});

                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // Ensure the corresponding directory exists in the target directory
                Path relativeDir = rootDir.relativize(dir);
                Path targetDirPath = targetDir.resolve(relativeDir);
                if (Files.notExists(targetDirPath)) {
                    Files.createDirectories(targetDirPath);
//                        System.out.println("Created directory: " + targetDirPath);
                    logger.log(Level.INFO, "Created directory: {0}", targetDirPath);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void completeSyncOperation(Integer operationId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(operationId);
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(true); // Cancel the task
        }
        scheduledTasks.remove(operationId); // Remove from the tracking map
    }

    @PreDestroy
    public void destroy() {
        fileHandler.close();
        logger.removeHandler(fileHandler);
//        updateAllStatus(SyncOperation.SyncStatus.PAUSED);
        scheduler.shutdownNow();
        try {
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
