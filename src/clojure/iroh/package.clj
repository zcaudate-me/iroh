(ns iroh.package
  (:require [dynapath.util :as dp]
            [iroh.common :refer :all]
            [clojure.java.io :refer [as-file] :as io])
  (:import [java.net URL URLClassLoader]
           [java.util.jar JarFile JarEntry]))

(def ^:dynamic *cache** (atom {}))

(def default-local-repo
  (io/file (System/getProperty "user.home") ".m2" "repository"))

(defn base-classloader []
  (or (.getClassLoader clojure.lang.RT)
      (.getContextClassLoader (Thread/currentThread))))

(defn classloader-hierachy []
  (take-while (comp not nil?)
              (iterate (fn [cl]
                         (.getParent ^ClassLoader cl))
                       (base-classloader))))

(defn jar-resource-path->class-name [path]
  (let [len (count path)]
    (-> path
        (subs 0 (- len 6))
        (.replaceAll "/" "."))))

(defn class-name->jar-resource-path [clsn]
  (str (.replaceAll clsn "\\." (System/getProperty "file.separator")) ".class"))

(defn find-jar
  ([cls] (find-jar cls (.getContextClassLoader (Thread/currentThread))))
  ([cls loader]
     (-> (.getName cls)
         (class-name->jar-resource-path)
         (io/resource loader)
         (.getPath)
         (->> (re-find #"file:(.*)"))
         second
         (clojure.string/split #"!/"))))

(defn find-maven
  ([cls] (find-maven cls (.getContextClassLoader (Thread/currentThread))))
  ([cls loader]
     (let [jarpath (first (find-jar cls loader))
           mvnpath (.getAbsolutePath default-local-repo)]
       (when (.startsWith jarpath mvnpath)
         (let [[_ version artifact & group]
               (-> (subs jarpath (count mvnpath))
                   (clojure.string/split (re-pattern (System/getProperty "file.separator")))
                   (->> (filter (comp not empty?)))
                   (reverse))]
           (-> (clojure.string/join  "." (reverse group))
               (str "/" artifact)
               symbol
               (vector version)))))))

(defn jar->class-names [path]
  (-> (as-file path)
      (JarFile.)
      (.entries)
      enumeration-seq
      (->> (map #(.getName %))
           (filter #(.endsWith % ".class"))
           (map jar-resource-path->class-name)
           set)))

(def memorize-jar->class-names
  (memoize jar->class-names))

(defn list-packages [cls-names]
  (reduce (fn [i s]
            (conj i (subs s 0 (.lastIndexOf s "."))))
          #{} cls-names))

(defn jar->package-names [path]
  (list-packages (memorize-jar->class-names path)))

(defn match-class-name? [cls pred]
  (cond (regex? pred)
        (re-find pred cls)

        (symbol? pred)
        (re-find (re-pattern (str "^" (.replaceAll (str pred) "\\." "\\.") "\\.[^\\.]+$"))
                 cls)

        (string? pred)
        (.startsWith cls (str pred "."))))

(defn all-class-names
  ([] (all-class-names (.getContextClassLoader (Thread/currentThread))))
  ([loader]
     (->> (dp/all-classpath-urls loader)
          (filter #(-> % (.toString) (.endsWith ".jar")))
          (mapcat memorize-jar->class-names))))

(defn all-package-names
  ([] (all-package-names (.getContextClassLoader (Thread/currentThread))))
  ([loader]
     (list-packages (all-class-names))))

(defn find-classes
  ([] (->> (all-class-names)
           (map #(suppress (Class/forName %)))
           (filter (comp not nil?))))
  ([pred] (find-classes pred (.getContextClassLoader (Thread/currentThread))))
  ([pred loader]
     (->> (all-class-names loader)
          (filter #(match-class-name? % pred))
          (map #(suppress (Class/forName %)))
          (filter (comp not nil?)))))


(comment
  (jar->package-names
   (-> (find-jar  org.sonatype.aether.connector.wagon.WagonRepositoryConnector$ExceptionWrapper)
       first))

  (jar->class-names
   (-> (find-jar  org.sonatype.aether.connector.wagon.WagonRepositoryConnector$ExceptionWrapper)
       first))

  (all-package-names)

  (find-classes "sun.security.ec")

  (first (find-classes "org.sonatype.guice.bean.locators"))

  (count (find-classes 'org.sonatype.guice.bean.locators))

  (re-find (re-pattern "^org\\.sonatype") (.getName org.sonatype.aether.connector.wagon.WagonRepositoryConnector$ExceptionWrapper))

  (re-find (re-pattern "^org\\.sonatype\\.[^\\.]+$") (.getName org.sonatype.aether.connector.wagon.WagonRepositoryConnector$ExceptionWrapper))


  (keys (System/getenv))


  (-> (System/getProperties) keys sort)
  '("awt.toolkit" "clojure.compile.path" "clojure.debug" "file.encoding" "file.encoding.pkg" "file.separator" "ftp.nonProxyHosts" "gopherProxySet" "http.nonProxyHosts" "iroh.version" "java.awt.graphicsenv" "java.awt.printerjob" "java.class.path" "java.class.version" "java.endorsed.dirs" "java.ext.dirs" "java.home" "java.io.tmpdir" "java.library.path" "java.runtime.name" "java.runtime.version" "java.specification.name" "java.specification.vendor" "java.specification.version" "java.vendor" "java.vendor.url" "java.vendor.url.bug" "java.version" "java.vm.info" "java.vm.name" "java.vm.specification.name" "java.vm.specification.vendor" "java.vm.specification.version" "java.vm.vendor" "java.vm.version" "line.separator" "os.arch" "os.name" "os.version" "path.separator" "socksNonProxyHosts" "sun.arch.data.model" "sun.boot.class.path" "sun.boot.library.path" "sun.cpu.endian" "sun.cpu.isalist" "sun.io.unicode.encoding" "sun.java.command" "sun.java.launcher" "sun.jnu.encoding" "sun.management.compiler" "sun.os.patch.level" "user.country" "user.country.format" "user.dir" "user.home" "user.language" "user.name" "user.timezone")

  (select-keys (System/getProperties) ["clojure.compile.path" "user.dir" "file.separator"])

  (select-keys (System/getProperties) ["sun.boot.class.path" "java.library.path"])

  (select-keys (System/getProperties) ["java.home" "java.vm.version"])

  (select-keys (System/getProperties) ["sun.management.compiler" "sun.java.command"])
  {"sun.java.command" "clojure.main -i /private/var/folders/dd/qfdy6sbn3mlgk20vcxc3j0ljnpxsqr/T/form-init5238405630186186457.clj", "sun.management.compiler" "HotSpot 64-Bit Tiered Compilers"}

  (comment
    (count (classes "org"))

    (count (all-class-names))
    (filter #(re-find #"^org." %) (all-class-names))

    (str (first (dp/classpath-urls (first (classloader-hierachy)))))




    '(.* (Class/forName "sun.net.spi.nameservice.dns.DNSNameService$1"))

    '("attr" "certs" "clone" "comment" "crc" "csize" "equals" "extra" "finalize" "flag" "getAttributes" "getCertificates" "getClass" "getCodeSigners" "getComment" "getCompressedSize" "getCrc" "getExtra" "getMethod" "getName" "getSize" "getTime" "hashCode" "isDirectory" "method" "name" "notify" "notifyAll" "setComment" "setCompressedSize" "setCrc" "setExtra" "setMethod" "setSize" "setTime" "signers" "size" "this$0" "time" "toString" "wait")
    )
)
