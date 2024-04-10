package com.kassen.sync.Service;

import com.kassen.sync.POJO.SyncOperation;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import net.jpountz.xxhash.XXHashFactory;

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
            logger.log(Level.SEVERE, "Failed to set up file handler for logging", e);
        }
    }

    public void processSyncOp(SyncOperation syncOperation){
        long interval = syncOperation.getDurationSeconds();

        ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            try {
                performSync(syncOperation);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to perform sync operation", e);
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
                    logger.log(Level.INFO, "コピー完了: {0}", relativePath);
                    newFilesCopied.set(true);

                    // After copying, verify the file
                    if (verifyCopy(file, targetPath)) {
                        logger.log(Level.INFO, "ファイルベリファイに成功しました: {0}", new Object[]{relativePath});
                    } else {
                        logger.log(Level.SEVERE, "ファイルベリファイに失敗しました: {0}", new Object[]{relativePath});
                        // Handle verification failure (e.g., throw an exception, retry, etc.)
                    }
                }
                return FileVisitResult.CONTINUE;
            }


            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relativeDir = rootDir.relativize(dir);
                Path targetDirPath = targetDir.resolve(relativeDir);

                if (Files.notExists(targetDirPath)) {
                    Files.createDirectories(targetDirPath);
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

    private boolean verifyCopy(Path source, Path target) {
        try {
            long sourceHash = computeXXHash3(source);
            long targetHash = computeXXHash3(target);
            boolean verificationPassed = sourceHash == targetHash;

            if (!verificationPassed) {
                logger.log(Level.SEVERE, "ファイルベリファイに失敗しました。ソース: {0}, ターゲット: {1}", new Object[]{source, target});
            }

            return verificationPassed;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ファイルベリファイ中にエラーが発生しました。ソース: {0}, ターゲット: {1}, エラー: {2}", new Object[]{source, target, e.getMessage()});
            return false; // Consider verification failed if an error occurs during hashing
        }
    }

    private long computeXXHash3(Path path) throws IOException {
        XXHashFactory factory = XXHashFactory.fastestInstance();
        try (InputStream inputStream = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192]; // Buffer size
            int read;
            long hash = 0;
            while ((read = inputStream.read(buffer)) != -1) {
                hash = factory.hash64().hash(buffer, 0, read, hash);
            }
            return hash;
        } catch (IOException e) {
            // Log error without stack trace
            logger.log(Level.SEVERE, "ハッシュ計算中にエラーが発生しました。パス: {0}, エラー: {1}", new Object[]{path, e.getMessage()});
            throw e; // Re-throw to handle upstream
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            logger.log(Level.INFO, "Shutting down...");
            scheduler.shutdownNow();
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
                if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    logger.log(Level.SEVERE, "Scheduler did not terminate");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            fileHandler.close();
            logger.removeHandler(fileHandler);
        }
        int i = syncService.updateAllStatus(SyncOperation.SyncStatus.STOPPED);
        logger.log(Level.INFO, "Stopped {0} Job(s)", i);
    }
}
