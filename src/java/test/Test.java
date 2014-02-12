package test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.List;

public class Test extends Base {
  @Override
  public String toString() {
    return "Test";
  }

}

class Base {
  @Override
  public String toString() {
    return "Base";
  }
}