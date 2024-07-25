package site.globitokuki.globitokuki_backend.exceptions;

public class PlaylistExceptions {
  public static class PlaylistNotFound extends RuntimeException {
    public PlaylistNotFound(String message) {
      super(message);
    }
  }

  public static class PlaylistAlreadyExists extends RuntimeException {
    public PlaylistAlreadyExists(String message) {
      super(message);
    }
  }
}
