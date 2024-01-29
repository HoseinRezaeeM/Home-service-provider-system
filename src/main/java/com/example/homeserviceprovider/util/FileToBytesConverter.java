package com.example.homeserviceprovider.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileToBytesConverter {
      public static byte[] convertFileToBytes(File file) throws Exception {
            Path filePath = Paths.get(file.getAbsolutePath());
            return Files.readAllBytes(filePath);
      }
}
