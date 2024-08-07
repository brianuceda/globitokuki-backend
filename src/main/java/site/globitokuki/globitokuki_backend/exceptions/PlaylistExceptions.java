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

  public static class RepeatedChapters extends RuntimeException {
    public RepeatedChapters(String message) {
      super(message);
    }
  }

  public static class InvalidThumbnail extends RuntimeException {
    public InvalidThumbnail(String message) {
      super(message);
    }
  }

  public static class InvalidName extends RuntimeException {
    public InvalidName(String message) {
      super(message);
    }
  }
}
