package test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.List;

public class TestRun{
  public static void run() throws Throwable {
    MethodHandle h1 = MethodHandles.lookup().findSpecial(Base.class, "toString",
        MethodType.methodType(String.class),
        Test.class);
    MethodHandle h2 = MethodHandles.lookup().findSpecial(Object.class, "toString",
        MethodType.methodType(String.class),
        Test.class);
    System.out.println(h1.invoke(new Test()));   // outputs Base
    System.out.println(h2.invoke(new Test()));   // outputs Test@860d49
  }
}