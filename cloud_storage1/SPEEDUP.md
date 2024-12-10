你的代码在上传文件时性能受限的原因可能包括网络传输速度、文件写入效率、缓冲区大小、以及异步处理的配置等因素。以下是一些优化建议，结合代码中的具体情况进行分析和改进：

---

### **优化建议**

#### **1. 增大缓冲区**
- 当前缓冲区大小为 `8KB`，对于较大的文件（如 587MB），缓冲区较小会导致频繁的 I/O 操作，从而降低性能。

**优化方案：**
- 增大缓冲区大小，例如 `64KB` 或 `128KB`。

修改代码：
```java
private void saveFile(MultipartFile file, Path filePath) throws IOException {
    try (InputStream inputStream = file.getInputStream();
         OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
        byte[] buffer = new byte[65536]; // 64KB 缓冲区
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }
}
```

#### **2. 使用 Direct I/O 或 NIO**
- Java NIO 提供了更高效的文件操作支持，可以通过 `FileChannel` 实现大文件的快速传输。

**优化方案：**
使用 `FileChannel` 替代传统的流式操作：
```java
private void saveFile(MultipartFile file, Path filePath) throws IOException {
    try (InputStream inputStream = file.getInputStream();
         FileChannel outputChannel = FileChannel.open(filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
        byte[] buffer = new byte[65536]; // 64KB 缓冲区
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputChannel.write(ByteBuffer.wrap(buffer, 0, bytesRead));
        }
    }
}
```

#### **3. 文件写入的异步优化**
- `@Async` 的使用已为异步处理提供支持，但要确保线程池配置合理。
- 确保 `@Async` 使用的线程池有足够的核心线程数和队列容量。

**优化方案：**
增加线程池的核心线程数和队列容量：
```java
@Bean(name = "asyncExecutor")
public Executor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(8); // 核心线程数
    executor.setMaxPoolSize(16); // 最大线程数
    executor.setQueueCapacity(50); // 队列容量
    executor.setThreadNamePrefix("FileUpload-");
    executor.initialize();
    return executor;
}
```

#### **4. 修改文件所有权逻辑**
- 当前使用 `ProcessBuilder` 调用系统命令 `sudo chown` 修改文件所有权，会产生系统调用开销。
- 你已经注释掉了一个基于 Java API 的 `changeFileOwnership` 方法，建议使用该方法。

**优化方案：**
启用 `Files.setOwner` 替代外部命令：
```java
private void changeFileOwnership(Path filePath, String user) throws IOException {
    Files.setOwner(filePath, filePath.getFileSystem()
            .getUserPrincipalLookupService()
            .lookupPrincipalByName(user));
}
```

#### **5. 优化上传的网络传输**
- 如果用户上传大文件时网络传输时间过长，可以考虑分块上传（Chunked Upload）。
- 分块上传允许客户端将文件切分成多个小块，逐块上传，服务器端再合并。

**优化方案：**
- 客户端上传时分块：
    - 例如，每块 `5MB`，并通过文件标识和分块索引管理。
- 服务端接收并合并：
```java
// 示例伪代码
@PostMapping("/uploadChunk")
public ResponseEntity<String> uploadChunk(@RequestParam("chunkIndex") int chunkIndex,
                                          @RequestParam("totalChunks") int totalChunks,
                                          @RequestParam("fileId") String fileId,
                                          @RequestParam("file") MultipartFile file) {
    Path tempDir = Paths.get("/temp/uploads/" + fileId);
    Files.createDirectories(tempDir);

    Path chunkFile = tempDir.resolve("chunk-" + chunkIndex);
    file.transferTo(chunkFile.toFile());

    // 如果所有分块上传完毕，合并文件
    if (isAllChunksUploaded(fileId, totalChunks)) {
        mergeChunks(tempDir, "/home/kncsz/cloudstorage/userfile/user1/" + fileId);
    }

    return ResponseEntity.ok("Chunk uploaded successfully");
}

private void mergeChunks(Path tempDir, String finalPath) throws IOException {
    try (FileChannel outputChannel = FileChannel.open(Paths.get(finalPath), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
        Files.list(tempDir).sorted().forEach(chunkPath -> {
            try (FileChannel inputChannel = FileChannel.open(chunkPath, StandardOpenOption.READ)) {
                inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
    // 删除临时目录
    Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
}
```

#### **6. 配置文件上传临时目录**
- Spring 默认会将上传的文件存储在临时目录中，可能会导致性能瓶颈。
- 指定一个更快的临时目录（如挂载在 SSD 的目录）提升性能。

**优化方案：**
修改 `application.properties`：
```properties
spring.servlet.multipart.location=/mnt/ssd/temp
```

#### **7. 调整操作系统参数**
- **磁盘 I/O 性能优化：**
    - 使用 SSD 提升磁盘读写速度。
- **TCP 参数优化：**
    - 增加 `net.core.wmem_max` 和 `net.core.rmem_max`，提升网络传输效率：
```bash
sudo sysctl -w net.core.wmem_max=8388608
sudo sysctl -w net.core.rmem_max=8388608
```

---

### **优化后性能预期**
通过以上优化，文件上传时间可以显著降低，尤其是以下部分：
1. 增大缓冲区或使用 `FileChannel` 提升文件写入性能。
2. 分块上传和合并可以平衡网络带宽的使用。
3. 使用 Java API 替代外部命令减少系统调用开销。

你可以逐步应用这些优化，并通过实际测试来验证性能改进。