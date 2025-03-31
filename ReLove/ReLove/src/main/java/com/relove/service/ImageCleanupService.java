package com.relove.service;

import com.relove.model.entity.Product;
import com.relove.repo.ProductRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ImageCleanupService {

    private final ProductRepo productRepo;

    public ImageCleanupService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanUnusedImages() {
        System.out.println("🔍 Стартира проверка за неизползвани изображения...");

        Path uploadDir = Paths.get("uploads");

        try (Stream<Path> fileStream = Files.list(uploadDir)) {
            List<String> usedImages = productRepo.findAll()
                    .stream()
                    .map(Product::getImageUrl)
                    .filter(url -> url != null && !url.isBlank())
                    .map(url -> url.replace("/uploads/", ""))
                    .collect(Collectors.toList());

            fileStream
                    .filter(Files::isRegularFile)
                    .filter(path -> !usedImages.contains(path.getFileName().toString()))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            System.out.println("🧹 Изтрито неизползвано изображение: " + path.getFileName());
                        } catch (IOException e) {
                            System.out.println("⚠️ Грешка при изтриване на " + path.getFileName() + ": " + e.getMessage());
                        }
                    });

        } catch (IOException e) {
            System.out.println("❌ Грешка при сканиране на папката uploads: " + e.getMessage());
        }
    }

}

