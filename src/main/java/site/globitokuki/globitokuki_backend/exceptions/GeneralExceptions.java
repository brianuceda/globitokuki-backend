package site.globitokuki.globitokuki_backend.exceptions;

public class GeneralExceptions {
  public static class RequiredFieldMissing extends RuntimeException {
    public RequiredFieldMissing(String message) {
      super(message);
    }
  }
}
