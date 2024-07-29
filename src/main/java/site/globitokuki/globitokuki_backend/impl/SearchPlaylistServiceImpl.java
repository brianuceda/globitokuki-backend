package site.globitokuki.globitokuki_backend.impl;

import site.globitokuki.globitokuki_backend.services.SearchPlaylistService;

public class SearchPlaylistServiceImpl implements SearchPlaylistService {
  private String search = "NOMBRE_DE_LA_BUSQUEDA";
  private String size = "l";

  // Busqueda con filtro size:large (1280x720)
  @SuppressWarnings("unused")
  private String urlSearch = "https://www.google.com/search?q=" + this.search + "&sca_esv=f5d76052eac9eedd&udm=2&sxsrf=ADLYWIJy-JekOX9k_aUVpxKnAm5C54tO4A:1722096377153&source=lnt&tbs=isz:" + this.size + "&sa=X&ved=2ahUKEwiT59D2zMeHAxU4RjABHYhzAhwQpwV6BAgCEAc&biw=1280&bih=720&dpr=1";
  
  @Override
  public String searchPlaylist(String search) {
    return null;
  }
}
