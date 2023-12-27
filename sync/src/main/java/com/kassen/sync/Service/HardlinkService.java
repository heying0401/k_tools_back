package com.kassen.sync.Service;

import com.kassen.sync.POJO.SyncOperation;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
public class HardlinkService implements ApplicationListener<ContextClosedEvent> {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ConcurrentHashMap<Integer, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private static final Logger logger = Logger.getLogger(HardlinkService.class.getName());
    private static FileHandler fileHandler;
    private final SyncService syncService;

    public HardlinkService(@Lazy SyncService syncService) {
        this.syncService = syncService;
    }

    static {
        try {
            // Configure the logger with handler and formatter
            fileHandler = new FileHandler("/home/h-yu/logs/synclogs.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processSyncOp(SyncOperation syncOperation){
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
        logger.log(Level.INFO, "同期操作開始 Job ID: {0}", syncOperation.getId());

        Path rootDir = Paths.get(syncOperation.getRoot());
        Path targetDir = Paths.get(syncOperation.getTarget());

        AtomicBoolean newFilesCopied = new AtomicBoolean(false);

        // Traverse the root directory
        Files.walkFileTree(rootDir, new SimpleFileVisitor<>() {
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
                    logger.log(Level.INFO, "コピー完了: {0}", relativePath);
                    newFilesCopied.set(true);
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
                    logger.log(Level.INFO, "ディレクトリ作成: {0}", relativeDir);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        if (newFilesCopied.get()) {
            logger.log(Level.INFO, "新しいファイルのコピーが完了しました。Job ID: {0}", syncOperation.getId());
        } else {
            logger.log(Level.INFO, "新しいファイルは見つかりませんでした。Job ID: {0}", syncOperation.getId());
        }
    }

    public void completeSyncOperation(Integer operationId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(operationId);
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(true); // Cancel the task
        }
        scheduledTasks.remove(operationId); // Remove from the tracking map
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        fileHandler.close();
        logger.removeHandler(fileHandler);
        int i = syncService.updateAllStatus(SyncOperation.SyncStatus.STOPPED);
        System.out.println("Stopped " + i + " Job(s)");
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
