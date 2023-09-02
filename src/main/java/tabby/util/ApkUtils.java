package tabby.util;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.util.FileUtil;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * des:
 * author: zyh 01202309
 * email: 2455023452qq.com
 **/
public class ApkUtils {

    public static void unzipFile(String filePath, String outputPath) {

        if (StringUtils.isEmpty(filePath)) return;
        try {
            File outputFile = new File(outputPath);
            if (!outputFile.exists()) {
                outputFile.mkdir();
            }

            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(filePath))) {
                ZipEntry zipEntry = zipInputStream.getNextEntry();

                while (zipEntry != null) {

                    String entryName = zipEntry.getName();
                    String entryPath = outputPath + File.separator + entryName;
                    if (StringUtils.isEmpty(entryName)) {
                        zipEntry = zipInputStream.getNextEntry();
                        continue;
                    } else if (!entryName.endsWith(".dex")) {
                        File f = new File(entryPath);
                        if (f.exists()) {
                            f.delete();
                        }
                        zipEntry = zipInputStream.getNextEntry();
                        continue;
                    }
                    if (zipEntry.isDirectory()) {
                        File fileDir = new File(entryPath);
                        fileDir.mkdirs();
                    } else {
                        try (FileOutputStream fos = new FileOutputStream(entryPath)) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zipInputStream.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                    }
                    zipEntry = zipInputStream.getNextEntry();
                }
            }


        } catch (Exception e) {
            System.out.println("unzipError: " + e.getMessage());
        }
    }

    public static void shellScriptExecutor(String path, String inputDir, String outputDir) {
        try {
            path = getRealPath(path);
            inputDir = getRealPath(inputDir);
            outputDir = getRealPath(outputDir);
            ProcessBuilder processBuilder = new ProcessBuilder(path, inputDir, outputDir);
            Process process = processBuilder.start();
            // 读取脚本输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // 等待脚本执行完成
            int exitCode = process.waitFor();
            System.out.println("脚本执行完成，退出码：" + exitCode);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getRealPath(String filepath) throws IllegalArgumentException {
        try {
            Path path = Paths.get(filepath);
            return path.toRealPath().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filepath;
    }

}
