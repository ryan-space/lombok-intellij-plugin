package lombok;

public enum AccessLevel {
  PUBLIC,
  MODULE,
  PROTECTED,
  PACKAGE,
  PRIVATE,
  NONE;

  private AccessLevel() {
  }
}
