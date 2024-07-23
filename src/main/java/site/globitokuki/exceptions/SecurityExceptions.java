package site.globitokuki.exceptions;

public class SecurityExceptions {
  public static class ProtectedResource extends RuntimeException {
    public ProtectedResource(String message) {
      super(message);
    }
  }
  
  public static class SQLInjectionException extends RuntimeException {
    public SQLInjectionException(String message) {
      super(message);
    }
  }
}
