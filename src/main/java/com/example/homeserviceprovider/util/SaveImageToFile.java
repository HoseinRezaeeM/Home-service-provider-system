package com.example.homeserviceprovider.util;

import java.io.FileOutputStream;
import java.io.IOException;

public class SaveImageToFile {
      public static void saveImageToFile(byte[] imageBytes, String filePath) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                  fileOutputStream.write(imageBytes);
            } catch (IOException e) {
                  e.printStackTrace();
            }
      }
}
