package im.chit.iroh;
  
public class Util{
  
  public static Object boxArg(Class paramType, Object arg){
    if(!paramType.isPrimitive())
      return paramType.cast(arg);
    else if(paramType == boolean.class)
      return Boolean.class.cast(arg);
    else if(paramType == char.class)
      return Character.class.cast(arg);
    else if(arg instanceof Number){
      Number n = (Number) arg;
      if(paramType == int.class)
        return n.intValue();
      else if(paramType == float.class)
        return n.floatValue();
      else if(paramType == double.class)
        return n.doubleValue();
      else if(paramType == long.class)
        return n.longValue();
      else if(paramType == short.class)
        return n.shortValue();
      else if(paramType == byte.class)
        return n.byteValue();
    }
    throw new IllegalArgumentException("Unexpected param type, expected: " + 
      paramType + ", given: " + arg.getClass().getName());
  }

  public static Object[] boxArgs(Class[] params, Object[] args){
    if(params.length == 0) return null;
    Object[] ret = new Object[params.length];
    for(int i = 0; i < params.length; i++){
      Object arg = args[i];
      Class paramType = params[i];
      ret[i] = boxArg(paramType, arg);
    }
    return ret;
  }

  public static boolean paramArgTypeMatch(Class paramType, Class argType){
    if(argType == null)
            return !paramType.isPrimitive();
    if(paramType == argType || paramType.isAssignableFrom(argType))
            return true;
    if(paramType == int.class)
            return argType == Integer.class
                || argType == long.class
                || argType == Long.class
                || argType == short.class
                || argType == byte.class;
    else if(paramType == float.class)
            return argType == Float.class
                || argType == double.class;
    else if(paramType == double.class)
            return argType == Double.class
                || argType == float.class;
    else if(paramType == long.class)
            return argType == Long.class
                || argType == int.class
                || argType == short.class
                || argType == byte.class;
    else if(paramType == char.class)
            return argType == Character.class;
    else if(paramType == short.class)
            return argType == Short.class;
    else if(paramType == byte.class)
            return argType == Byte.class;
    else if(paramType == boolean.class)
            return argType == Boolean.class;
      return false;
  }

  public static boolean isCongruent(Class[] params, Object[] args){
    boolean ret = false;
    if(args == null) return params.length == 0;
    if(params.length == args.length){
      ret = true;
      for(int i = 0; ret && i < params.length; i++){
        Object arg = args[i];
        Class argType = (arg == null) ? null : arg.getClass();
        Class paramType = params[i];
        ret = paramArgTypeMatch(paramType, argType);
      }
    }
    return ret;
  }
}