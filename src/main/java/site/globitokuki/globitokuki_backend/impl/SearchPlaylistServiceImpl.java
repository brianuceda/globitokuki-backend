package site.globitokuki.globitokuki_backend.impl;

import java.util.*;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import site.globitokuki.globitokuki_backend.dtos.ChapterDTO;
import site.globitokuki.globitokuki_backend.dtos.PlaylistDTO;
import site.globitokuki.globitokuki_backend.dtos.selenium.CookiesDTO;
import site.globitokuki.globitokuki_backend.entities.CookieEntity;
import site.globitokuki.globitokuki_backend.entities.SessionEntity;
import site.globitokuki.globitokuki_backend.entities.enums.StatesEnum;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.*;
import site.globitokuki.globitokuki_backend.repositories.PlaylistRepository;
import site.globitokuki.globitokuki_backend.repositories.SessionRepository;
import site.globitokuki.globitokuki_backend.services.SearchPlaylistService;
import site.globitokuki.globitokuki_backend.utils.SeleniumUtils;
import lombok.extern.java.Log;
import java.util.stream.Collectors;

@Service
@Log
public class SearchPlaylistServiceImpl implements SearchPlaylistService {
  // ? Selenium
  // private SeleniumUtils seleniumUtils;
  private ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();

  // ? Inyección de dependencias
  private SessionRepository sessionRepository;
  private PlaylistRepository playlistRepository;
  private SeleniumUtils seleniumUtils;

  // ? Variables
  private String sessionName = "globitokuki2";
  // Playlist Data

  // Image
  // private String search = "NOMBRE_DE_LA_BUSQUEDA";
  // private String size = "l";
  // @SuppressWarnings("unused")
  // private String urlSearch = "https://www.google.com/search?q=" + this.search +
  // "&sca_esv=f5d76052eac9eedd&udm=2&sxsrf=ADLYWIJy-JekOX9k_aUVpxKnAm5C54tO4A:1722096377153&source=lnt&tbs=isz:"
  // + this.size +
  // "&sa=X&ved=2ahUKEwiT59D2zMeHAxU4RjABHYhzAhwQpwV6BAgCEAc&biw=1280&bih=720&dpr=1";

  public SearchPlaylistServiceImpl(SessionRepository sessionRepository, PlaylistRepository playlistRepository,
      SeleniumUtils seleniumUtils) {
    this.sessionRepository = sessionRepository;
    this.playlistRepository = playlistRepository;
    this.seleniumUtils = seleniumUtils;
  }

  // ? Methods
  @Transactional
  @Override
  public void saveCookiesInDb(List<CookiesDTO> cookies) {
    String domain = cookies.get(0).getDomain();

    Optional<SessionEntity> existingSession = sessionRepository.findByDomain(domain);

    existingSession.ifPresent(sessionRepository::delete);

    SessionEntity session = new SessionEntity();

    session.setName(this.sessionName);
    session.setDomain(domain);

    List<CookieEntity> cookieEntities = cookies.stream().map(cookieDTO -> {
      CookieEntity cookieEntity = new CookieEntity();
      cookieEntity.setDomain(cookieDTO.getDomain());
      cookieEntity.setExpirationDate(cookieDTO.getExpirationDate());
      cookieEntity.setHostOnly(cookieDTO.getHostOnly());
      cookieEntity.setHttpOnly(cookieDTO.getHttpOnly());
      cookieEntity.setName(cookieDTO.getName());
      cookieEntity.setPath(cookieDTO.getPath());
      cookieEntity.setSameSite(cookieDTO.getSameSite());
      cookieEntity.setSecure(cookieDTO.getSecure());
      cookieEntity.setSession(cookieDTO.getSession());
      cookieEntity.setStoreId(cookieDTO.getStoreId());
      cookieEntity.setValue(cookieDTO.getValue());
      return cookieEntity;
    }).collect(Collectors.toList());

    session.setCookies(cookieEntities);
    sessionRepository.save(session);
  }

  // @Override
  // public void searchGoogle(String query) {
  // WebDriver webDriver = initializeWebDriverWithCookies(this.sessionName,
  // ".google.com");
  // try {
  // // String originalWindow = webDriver.getWindowHandle();
  // // webDriver.switchTo().newWindow(WindowType.TAB);
  // webDriver.get("https://www.google.com");

  // // Seleccionar el campo de búsqueda
  // WebElement searchBox = new WebDriverWait(webDriver, Duration.ofSeconds(10))
  // .until(ExpectedConditions.elementToBeClickable(By.name("q")));
  // searchBox.sendKeys(query);
  // searchBox.submit();

  // // Wait for results to load
  // new WebDriverWait(webDriver, Duration.ofSeconds(10))
  // .until(ExpectedConditions.presenceOfElementLocated(By.id("search")));

  // // Procesar los resultados si es necesario (por ejemplo, obtener URLs de los
  // // resultados)
  // List<WebElement> searchResults =
  // webDriver.findElements(By.cssSelector(".g"));

  // for (WebElement result : searchResults) {
  // String resultText = result.getText();
  // log.info(resultText);
  // }

  // // Esperar 5 segundos sin hacer nada
  // try {
  // Thread.sleep(5000);
  // } catch (InterruptedException e) {
  // log.warning("Sleep interrupted: " + e.getMessage());
  // }

  // // Close the current tab and switch back to the original tab
  // // webDriver.close();
  // // webDriver.switchTo().window(originalWindow);
  // } finally {
  // webDriver.quit();
  // driver.remove();
  // }
  // }

  @Override
  public List<String> searchGoogleImages(String query) {
    WebDriver webDriver = initializeWebDriverWithCookies(this.sessionName, ".google.com");
    List<String> imageSrcList = new ArrayList<>();
    try {
      int maxSearches = 5;
      query = query.replace(" ", "+").toLowerCase() + "+serie+coreana";
      String urlSearch = "https://www.google.com/search?q=" + query + "&tbm=isch&biw=1280&bih=720";

      // Navegar a la URL de búsqueda de imágenes
      webDriver.get(urlSearch);

      // Esperar a que se carguen los resultados
      WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20));
      wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[jsname='dTDiAc']")));

      // Obtener los src de las primeras 5 imágenes
      List<WebElement> imageResults = webDriver.findElements(By.cssSelector("div[jsname='dTDiAc']"));
      int count = 0;
      for (WebElement result : imageResults) {
        if (count >= maxSearches) {
          break;
        }
        try {
          WebElement imgElement = result.findElement(By.cssSelector("img"));
          String imageUrl = imgElement.getAttribute("src");
          if (imageUrl != null && !imageUrl.isEmpty()) {
            imageSrcList.add(imageUrl);
            count++;
          }
        } catch (Exception e) {
          log.warning("Error al procesar la imagen: " + e.getMessage());
        }
      }
    } finally {
      webDriver.quit();
      driver.remove();
    }

    return imageSrcList;
  }

  @Override
  public PlaylistDTO searchYtPlaylistData(String playlistYtUrl) {
    String domain = "youtube.com";

    if (!playlistYtUrl.contains(domain)) {
      throw new PlaylistNotFound("Playlist inválida");
    }

    WebDriver webDriver = initializeWebDriverWithCookies(this.sessionName, "." + domain);
    PlaylistDTO playlistDTO = new PlaylistDTO();
    List<ChapterDTO> chapterList = new ArrayList<>();

    try {
      webDriver.get(playlistYtUrl);

      // Validar que la lista de reproducción sea válida
      validatePlaylist(webDriver, domain);

      // Esperar a que se cargue el botón de menú
      try {
        WebElement btn = new WebDriverWait(webDriver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#button-shape .yt-spec-button-shape-next--icon-button")));

        if (btn != null) {
          btn.click();

          WebElement showUnavailableVideoBtn = new WebDriverWait(webDriver, Duration.ofSeconds(10))
              .until(ExpectedConditions.presenceOfElementLocated(By.tagName("ytd-menu-navigation-item-renderer")));

          if (showUnavailableVideoBtn != null) {
            WebElement linkElement = showUnavailableVideoBtn.findElement(By.tagName("a"));
            linkElement.click();
          }
        }
      } catch (TimeoutException e) {
        throw new TimeoutException("Un elemento tardó en cargar");
      }

      // Scroll hasta el final de la página
      scrollToBottom(webDriver);

      // Esperar a que se carguen los resultados
      new WebDriverWait(webDriver, Duration.ofSeconds(10))
          .until(ExpectedConditions.presenceOfElementLocated(By.tagName("ytd-playlist-video-renderer")));

      // Título de la lista de reproducción
      List<WebElement> dynamicTextContainers = webDriver.findElements(By.cssSelector("div.dynamic-text-container"));

      playlistDTO.setPlaylistLink(playlistYtUrl);
      playlistDTO.setState(StatesEnum.WATCHED);
      getAndGenerateNames(playlistDTO, dynamicTextContainers);

      // Generar el orden de visualización
      playlistRepository.findFirstByOrderByOrderViewDesc().ifPresentOrElse(playlistEntity -> {
        playlistDTO.setOrderView(playlistEntity.getOrderView() + 1);
      }, () -> {
        playlistDTO.setOrderView(1);
      });

      // Obtener los elementos de la lista de reproducción
      List<WebElement> videoElements = webDriver.findElements(By.tagName("ytd-playlist-video-renderer"));

      if (videoElements.size() > 0) {
        int iteration = 0;
        for (WebElement videoElement : videoElements) {
          iteration++;

          try {
            // Extraer información relevante del video
            String videoTitle = videoElement.findElement(By.id("video-title")).getText();
            String videoUrl = videoElement.findElement(By.id("video-title")).getAttribute("href");
            String videoId = extractVideoIdFromUrl(videoUrl);

            // Extraer el número del capítulo
            String chapterText = videoTitle.split("Chapter ")[1].split(" ")[0];
            int chapterNumber = Integer.parseInt(chapterText);

            // Intentar obtener la duración del video
            WebElement durationElement = new WebDriverWait(webDriver, Duration.ofSeconds(5))
                .until(ExpectedConditions
                    .visibilityOf(videoElement.findElement(By.cssSelector("div.badge-shape-wiz__text"))));
            String durationText = durationElement.getText();
            String duration = formatDuration(durationText);

            // Asignar minuto visto de cada cap.

            // Crear y agregar ChapterDTO a la lista
            ChapterDTO chapterDTO = new ChapterDTO(chapterNumber, videoId, "00:00:00", duration,
                iteration == 1 ? false : true, iteration == videoElements.size() ? false : true);

            chapterList.add(chapterDTO);

          } catch (Exception e) {
            log.warning("Error al procesar el capítulo " + iteration + " - Razón: " + e.getMessage());
          }
        }
      }

      playlistDTO.setChapterList(chapterList);

      playlistDTO.setChapters(chapterList.size());
    } finally {
      webDriver.quit();
      driver.remove();
    }

    return playlistDTO;
  }

  private WebDriver initializeWebDriverWithCookies(String sessionName, String domain) {
    Optional<SessionEntity> sessionOpt = sessionRepository.findByNameAndDomain(sessionName, domain);

    if (!sessionOpt.isPresent()) {
      throw new IllegalStateException("No cookies found for the session: " + sessionName);
    }

    SessionEntity session = sessionOpt.get();
    List<CookieEntity> cookies = session.getCookies();

    try {
      driver = this.seleniumUtils.setUp(driver);
    } catch (Exception e) {
      throw new IllegalStateException("Error initializing WebDriver: " + e.getMessage());
    }

    WebDriver webDriver = driver.get();
    webDriver.get("https://www" + domain);

    // Add cookies to WebDriver
    for (CookieEntity cookieEntity : cookies) {
      Cookie cookie = new Cookie.Builder(cookieEntity.getName(), cookieEntity.getValue())
          .domain(cookieEntity.getDomain())
          .path(cookieEntity.getPath())
          .expiresOn(new java.util.Date(cookieEntity.getExpirationDate() * 1000))
          .isSecure(cookieEntity.getSecure())
          .isHttpOnly(cookieEntity.getHttpOnly())
          .build();
      webDriver.manage().addCookie(cookie);
    }

    webDriver.navigate().refresh(); // Refrescar para aplicar las cookies

    return webDriver;
  }

  private void scrollToBottom(WebDriver webDriver) {
    JavascriptExecutor js = (JavascriptExecutor) webDriver;
    long lastHeight = (long) js.executeScript("return document.documentElement.scrollHeight");

    while (true) {
      for (int i = 0; i < 5; i++) { // Dividir el scroll en 10 incrementos
        js.executeScript("window.scrollBy(0, " + (lastHeight / 5) + ");");
        try {
          Thread.sleep(500); // Esperar 0.5 segundos entre incrementos
        } catch (InterruptedException e) {
          log.warning("Sleep interrupted: " + e.getMessage());
        }
      }

      long newHeight = (long) js.executeScript("return document.documentElement.scrollHeight");

      if (newHeight == lastHeight) {
        break;
      }

      lastHeight = newHeight;
    }
  }

  private void validatePlaylist(WebDriver webDriver, String domain) {
    String currentUrl = webDriver.getCurrentUrl();
    if (currentUrl.equals("https://www." + domain)) {
      throw new PlaylistNotFound("Playlist inválida");
    }

    try {
      webDriver.findElement(By.cssSelector(".ERROR"));
      throw new PlaylistNotFound("Playlist inválida");
    } catch (NoSuchElementException e) {
      // Playlist válida
    }
  }

  private void getAndGenerateNames(PlaylistDTO playlistDTO, List<WebElement> dynamicTextContainers) {
    String realName = "";
    if (dynamicTextContainers.size() >= 2) {
      WebElement secondContainer = dynamicTextContainers.get(1);
      WebElement formattedString = secondContainer.findElement(By.cssSelector("yt-formatted-string"));
      realName = formattedString.getText();
      playlistDTO.setRealName(realName);
      playlistDTO.setFullName(formatFullName(realName));
      playlistDTO.setShortName(formatShortName(realName));
    }
  }

  private String formatFullName(String value) {
    return value.toLowerCase().replaceAll("\\s+", "-");
  }

  private String formatShortName(String value) {
    return Arrays.stream(value.split(" "))
        .map(word -> word.substring(0, 1).toLowerCase())
        .collect(Collectors.joining());
  }

  private String formatDuration(String duration) {
    String[] parts = duration.split(":");

    String formattedDuration = String.format("%02d:%s:%s", Integer.parseInt(parts[0]), parts[1], parts[2]);
    return formattedDuration;
  }

  private String extractVideoIdFromUrl(String url) {
    String[] parts = url.split("v=");
    if (parts.length > 1) {
      return parts[1].split("&")[0];
    }
    return null;
  }
}
