package org.example;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.List;

public class M {
    public static void main(String[] args) {
        // Configurar la ruta del driver de Chrome
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\raulb\\OneDrive\\Escritorio\\GRADO SUPERIOR RAUL\\ENTORNOS DE DESARROLLO\\Selenium\\chromedriver.exe");

        // Configurar opciones de Chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // Ejecutar sin interfaz gráfica
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // Inicializar WebDriver
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        String urlBase = "https://nachoiborraies.github.io/java/";

        try {
            // Abrir la página web
            driver.get(urlBase);

            // Obtener todos los enlaces <a>
            List<WebElement> links = driver.findElements(By.tagName("a"));

            for (WebElement link : links) {
                String subpageUrl = link.getAttribute("href");

                // Verificar que el enlace no sea nulo y que pertenezca al dominio
                if (subpageUrl != null && subpageUrl.contains("nachoiborraies.github.io")) {
                    driver.get(subpageUrl);

                    // Buscar enlaces a PDFs en la subpágina
                    List<WebElement> pdfLinks = driver.findElements(By.xpath("//a[contains(@href, '.pdf')]"));

                    for (WebElement pdfLink : pdfLinks) {
                        String pdfUrl = pdfLink.getAttribute("href");
                        if (pdfUrl != null && isValidURL(pdfUrl)) {
                            String fileName = pdfUrl.substring(pdfUrl.lastIndexOf("/") + 1);
                            System.out.println("Descargando: " + fileName);
                            downloadFile(pdfUrl, "C:\\Users\\raulb\\Downloads\\" + fileName);
                        }
                    }

                    // Volver a la página principal
                    driver.navigate().back();
                }
            }

            System.out.println("Descarga completada.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar el navegador
            driver.quit();
        }
    }

    // Método para descargar un archivo
    public static void downloadFile(String fileURL, String filePath) {
        try {
            FileUtils.copyURLToFile(new URL(fileURL), new File(filePath));
            System.out.println("Archivo guardado: " + filePath);
        } catch (IOException e) {
            System.err.println("Error al descargar el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para verificar si una URL es accesible (evita enlaces rotos)
    public static boolean isValidURL(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");
            huc.setConnectTimeout(5000);
            huc.connect();
            int responseCode = huc.getResponseCode();
            return (responseCode >= 200 && responseCode < 400);
        } catch (IOException e) {
            System.err.println("URL no válida o inaccesible: " + urlString);
            return false;
        }
    }
}