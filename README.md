# iroh

> 'Even in the material world, you will find that if you look for the light, 
>  You can often find it. But if you look for the dark, that is all you will
>  ever see. Many things that seem threatening in the dark become welcoming 
>  when we shine light on them.'
 
>  - Uncle Iroh, The Legend of Korra 

Iroh is a library for jvm reflection. It is designed to be used for testing, repl based development, and blantant hacks bypassing the jvm security mechanism. When working and understanding badly written, poorly encapsulated code, I have found that the best way is to expose everything first, then to test each piece of functionality in a controlled way. Finally only when all the pieces are known, then work out a strategy for code refactoring/rewriting. 

Although private and protected keywords have their uses in java, I'm beginning to think of them as functionality obsfucators. They are complete hinderences when I am trying to do something to the code base that the previous author had not intended for me to do - one of them being to understand what is going on underneath. If the previous author had taken shortcuts in design, those private keywords turn one of those over-protective parents that get in the way of the growth of their children. Taking inspiration from clj-wallhack, here are some primary use cases for the library:   

- To explore the members of classes as well as all instances within the repl
- To be able to test methods and functions that are usually not testable, or very hard to test:
  - Make hidden class members visible by providing access to private methods and fields 
  - Make immutable class members flexible by providing ability to change final members (So that initial states can be set up easily)
- Extract out class members into documented and executable functions
- Better understand jvm security and how to dodge it if needed
- Better understand the java type system as well as clojure's own interface definitions
- To make working with java fun again

## Installation

Add to project.clj dependencies:

```clojure
[im.chit/iroh "0.1.5-SNAPSHOT"]
```

## Usage

Main functionality is accessed through:

```clojure
(use 'iroh.core)
```

The api consists of the following macros:

```clojure
  >ns - for importing object elements into a namespace
  >var - for importing elements into current namespace
  .> - for showing type hierarchy
  .? - for showing class elements
  .* - for showing instance elements
  .$ - for reflective invocation of objects
```

### `>ns` - Import as Namespace

### `>var` - Import as Var

### Elements

#### Application
In the following example, We can assign functions to var `char-at`:

```clojure
(def char-at (first (.? String "charAt" :#)))

    or

(def char-at (.? String "charAt" :#))
```

`char-at` is an method element. It can be turned into a string:

```clojure
(str char-at)
;;=> "#[charAt :: (java.lang.String, int) -> char]"
```

From the string, it hints that `char-at` is invokable. The element takes in a `String` and an `int`, returning a `char`. It can be used like any other clojure function.

```clojure
(char-at "hello" 0) => \h

(mapv #(char-at "hello" %) (range 5))  => [\h \e \l \l \o]
```

Data for char-at is accessed using keyword lookups:

```clojure
(:params char-at) => [java.lang.String Integer/TYPE]

(:modifiers char-at) => #{:instance :method :public}

(:type char-at)  => Character/TYPE

(:all char-at)
=> (just {:tag :method
          :name "charAt"
          :modifiers #{:instance :method :public},
          :origins [CharSequence String]
          :hash number?
          :container String
          :static false
          :params [String Integer/TYPE]
          :type Character/TYPE
          :delegate fn?})
```


Private class members and fields can be exposed as easily as public ones. First, a list of private methods defined in Integers are listed:

```clojure
(.? Integer :private :method :name)
=> ["toUnsignedString"]
```

Since there is only toUnsignedString, it will be extracted:


```clojure
(def unsigned-str (.? Integer "toUnsignedString" :#))
```

As can be seen by its modifiers, unsigned-str is private static method:


```clojure
(:modifiers unsigned-str)
;;=> #{:method :private :static}
```

The string representation shows that it takes two ints and returns a String:


```clojure
(str unsigned-str)
;;=> "#[toUnsignedString :: (int, int) -> java.lang.String]"
```

The element can now be used, just like a normal function:

```clojure
(unsigned-str 10 1)
;;=> "1010"

(mapv #(unsigned-str 32 (inc %)) (range 6))
;;=> ["100000" "200" "40" "20" "10" "w"]
```


### `.>` - Type Hierarchy

```clojure
(.> 1)
;;=> [java.lang.Long
;;    [java.lang.Number #{java.lang.Comparable}]
;;    [java.lang.Object #{java.io.Serializable}]]

(.> "hello")
;;=> [java.lang.String
;;    [java.lang.Object #{java.lang.CharSequence
;;                        java.io.Serializable
;;                        java.lang.Comparable}]]

(.> {})
;;=> [clojure.lang.PersistentArrayMap
;;    [clojure.lang.APersistentMap #{clojure.lang.IObj
;;                                   clojure.lang.IEditableCollection}]
;;    [clojure.lang.AFn #{clojure.lang.MapEquivalence
;;                        clojure.lang.IHashEq
;;                        java.io.Serializable
;;                        clojure.lang.IPersistentMap
;;                        java.util.Map
;;                        java.lang.Iterable}]
;;    [java.lang.Object #{clojure.lang.IFn}]]
```

### `.?` and `.*` - Exploration

`.?` and `.*` have the same listing and filtering mechanisms but they do things a little differently. `.?` holds the java view of the Class declaration, staying true to the class and its members. `.*` holds the runtime view of Objects and what methods could be applied to that instance. `.*` will also look up the inheritance tree to fill in additional functionality. 

Below shows three examples. All the method asks for members of String beginning with `c`.:

```clojure
(.? String  #"^c" :name)
;;=> ["charAt" "checkBounds" "codePointAt" "codePointBefore"
;;    "codePointCount" "compareTo" "compareToIgnoreCase"
;;    "concat" "contains" "contentEquals" "copyValueOf"]

(.* String #"^c" :name)
;;=> ["cachedConstructor" "cannotCastMsg" "cast" "checkBounds" 
;;    "checkMemberAccess" "classRedefinedCount" "classValueMap" 
;;    "clearCachesOnClassRedefinition" "clone" "copyValueOf"]

(.* (String.) #"^c" :name)
;;=> ["charAt" "clone" "codePointAt" "codePointBefore" 
;;    "codePointCount" "compareTo" "compareToIgnoreCase" 
;;    "concat" "contains" "contentEquals"]

`.?` lists is what we expect. `.*` lists all static methods and fields as well as Class methods of `String`, whilst for instances of `String`, it will list all the instance methods from the entire class hierachy.
```

#### Filtering

There are many filters that can be used with .?:

  - regexes and strings for filtering of element names
  - symbols and classes for filtering of return type
  - vectors for filtering of input types
  - longs for filtering of input argment count
  - keywords for filtering of element modifiers
  - keywords for customization of return types

Below are examples of results All the private element names in String beginning with `c`:

```clojure
(.? String  #"^c" :name :private)
;;=> ["checkBounds"]
```

All the private field names in String:

```clojure
(.? String :name :private :field)
;;=> ["HASHING_SEED" "hash" "hash32" "serialPersistentFields"
;;    "serialVersionUID" "value"]
```
    
All the private static field names in String:

```clojure
(.? String :name :private :field :static)
;;=> ["HASHING_SEED" "serialPersistentFields" "serialVersionUID"]
```

All the private non-static field names in String:

```clojure
(.? String :name :private :field :instance)
;;=> ["hash" "hash32" "value"]
```



## License

Copyright © 2014 Chris Zheng