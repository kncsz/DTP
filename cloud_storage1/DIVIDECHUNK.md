如果用户是在浏览器上传文件，那么客户端的代码需要通过 **HTML + JavaScript** 来实现分块上传。可以使用浏览器提供的 `File` 和 `Blob` API，将大文件分块，然后通过 AJAX 或 `fetch` 向服务器上传。

---

### **1. 浏览器端实现分块上传**

#### **核心步骤：**
1. 用户在 HTML 页面中选择文件。
2. 使用 JavaScript 将文件分块（通过 `Blob.slice`）。
3. 每个分块通过 AJAX 或 `fetch` 单独上传到服务器。
4. 服务器记录分块并在完成后合并。

---

#### **示例代码**

##### **HTML 页面**
提供一个简单的文件选择和上传界面：
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>File Chunk Upload</title>
</head>
<body>
    <h1>Chunked File Upload</h1>
    <input type="file" id="fileInput" />
    <button id="uploadBtn">Upload</button>
    <div id="progress"></div>

    <script src="upload.js"></script>
</body>
</html>
```

##### **JavaScript（`upload.js`）**
实现文件分块上传的逻辑：
```javascript
document.getElementById("uploadBtn").addEventListener("click", () => {
    const fileInput = document.getElementById("fileInput");
    if (!fileInput.files.length) {
        alert("Please select a file!");
        return;
    }

    const file = fileInput.files[0];
    const chunkSize = 5 * 1024 * 1024; // 每块 5MB
    const totalChunks = Math.ceil(file.size / chunkSize);

    let chunkIndex = 0;

    // 递归上传每个块
    const uploadChunk = () => {
        const start = chunkIndex * chunkSize;
        const end = Math.min(start + chunkSize, file.size);

        const chunk = file.slice(start, end); // 分块
        const formData = new FormData();
        formData.append("file", chunk);
        formData.append("chunkIndex", chunkIndex);
        formData.append("totalChunks", totalChunks);
        formData.append("fileName", file.name);

        // 使用 fetch 上传块
        fetch("http://localhost:8080/api/uploadChunk", {
            method: "POST",
            body: formData,
        })
            .then((response) => response.text())
            .then((message) => {
                console.log(`Chunk ${chunkIndex + 1}/${totalChunks} uploaded: ${message}`);
                document.getElementById("progress").innerText = `Uploaded ${chunkIndex + 1} of ${totalChunks} chunks.`;

                chunkIndex++;
                if (chunkIndex < totalChunks) {
                    uploadChunk(); // 上传下一个块
                } else {
                    alert("File upload complete!");
                }
            })
            .catch((error) => {
                console.error("Error uploading chunk:", error);
                alert("Failed to upload chunk. Please try again.");
            });
    };

    // 开始上传第一个块
    uploadChunk();
});
```

---

### **2. 服务器端接收分块并合并**

服务器端代码逻辑已经在之前的回答中提到，可参考以下核心流程：

#### **分块上传的主要逻辑**
1. **接收块数据：**
    - 每次上传的块保存到临时目录，以 `chunkIndex` 命名。

2. **检查所有块是否完成：**
    - 根据 `totalChunks` 和当前已接收的块数量判断是否完成。

3. **合并分块：**
    - 按照块顺序将所有分块合并为完整文件。

#### **完整的 Controller 示例**
```java
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    private static final String TEMP_DIR = "/tmp/uploads/";

    @PostMapping("/uploadChunk")
    public String uploadChunk(
            @RequestParam("file") MultipartFile chunk,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("fileName") String fileName) throws IOException {

        Path tempDir = Paths.get(TEMP_DIR + fileName);
        Files.createDirectories(tempDir);

        // 保存块到临时目录
        File chunkFile = tempDir.resolve("chunk-" + chunkIndex).toFile();
        try (FileOutputStream fos = new FileOutputStream(chunkFile)) {
            fos.write(chunk.getBytes());
        }

        // 检查是否所有块都已上传
        if (isAllChunksUploaded(tempDir, totalChunks)) {
            mergeChunks(tempDir, Paths.get("/final/uploads/" + fileName));
            return "File uploaded and merged successfully!";
        }

        return "Chunk " + chunkIndex + " uploaded successfully.";
    }

    private boolean isAllChunksUploaded(Path tempDir, int totalChunks) {
        File[] files = tempDir.toFile().listFiles();
        return files != null && files.length == totalChunks;
    }

    private void mergeChunks(Path tempDir, Path finalFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(finalFilePath.toFile())) {
            Files.list(tempDir).sorted(Comparator.comparing(Path::getFileName)).forEach(chunkPath -> {
                try {
                    Files.copy(chunkPath, fos);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to merge chunk: " + chunkPath.getFileName(), e);
                }
            });
        }

        // 删除临时目录
        Files.walk(tempDir)
             .sorted(Comparator.reverseOrder())
             .map(Path::toFile)
             .forEach(File::delete);
    }
}
```

---

### **3. 优化建议**

#### **3.1 浏览器端优化**
1. **并发上传：**
    - 使用 `Promise.all` 实现多块并发上传，提高上传速度。
    - 设置同时上传的最大块数（如 3 个块并行）。

#### **3.2 服务器端优化**
1. **多线程合并：**
    - 使用多线程加速分块合并，尤其是文件较大时。

2. **断点续传：**
    - 在临时目录记录已上传的块，客户端只需上传未完成的部分。

3. **校验块完整性：**
    - 上传时附加 `MD5` 校验值，确保分块未被篡改。

---

### **4. 总结**

通过上述代码，用户可以在浏览器上传大文件，服务器端按块接收并合并：
1. 客户端将文件分块并逐块上传。
2. 服务器逐块存储并最终合并为完整文件。

改进方向包括客户端并发上传、断点续传以及校验完整性，以满足更高效和更可靠的上传需求。