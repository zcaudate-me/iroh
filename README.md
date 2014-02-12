# iroh

> 'Even in the material world, you will find that if you look for the light, 
>  You can often find it. But if you look for the dark, that is all you will
>  ever see. Many things that seem threatening in the dark become welcoming 
>  when we shine light on them.'
 
>                     Uncle Iroh, The Legend of Korra 

## Installation

Add to project.clj dependencies:

    [im.chit/iroh "0.1.5"]

## Usage

Main functionality is accessed through:

    (use 'iroh.core)

The api consists of the following macros:

    .> - for showing type hierarchy
    .? - for showing class elements
    .* - for showing instance elements
    .$ - for reflective invocation of objects
    >var - for importing elements into current namespace
    >ns - for importing object elements into a namespace

## `.>` - Type Hierarchy

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

### `.?` and `.*`

`.?` and `.*` have the same listing and filtering mechanisms but they do things a little differently. `.?` holds the java view of the Class declaration, staying true to the class and its members. `.*` holds the runtime view of Objects and what methods could be applied to that instance. `.*` will also look up the inheritance tree to fill in additional functionality.

#### Filtering

There are many filters that can be used with .?:

  - regexes and strings for filtering of element names
  - symbols and classes for filtering of return type
  - vectors for filtering of input types
  - longs for filtering of input argment count
  - keywords for filtering of element modifiers
  - keywords for customization of return types

All the method names in String beginning with `c`:

```clojure
(.? String  #"^c" :name)
;;=> ["charAt" "checkBounds" "codePointAt" "codePointBefore"
;;    "codePointCount" "compareTo" "compareToIgnoreCase"
;;    "concat" "contains" "contentEquals" "copyValueOf"]
```

All the private element names in String beginning with `c`:

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


#### Application
The keyword `:#` is used as a convienience argument to create elements from the returned list. In the following example, It is assigned to char-at:

    (def char-at (.? String "charAt" :#))

`char-at` is an method element. It can be turned into a string:

    (str char-at)
    ;;=> "#[charAt :: (java.lang.String, int) -> char]"

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




## License

Copyright © 2014 Chris Zheng