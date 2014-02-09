(ns midje-doc.iroh-guide
  (:require [iroh.core :refer :all]
            [midje.sweet :refer :all]))

[[:chapter {:title "Overview"}]]

"
    'Even in the material world, you will find that if you look for the light, 
     You can often find it. But if you look for the dark, that is all you will
     ever see. Many things that seem threatening in the dark become welcoming 
     when we shine light on them.'
     
                                        - Uncle Iroh, The Legend of Korra
"
[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies: 

  `[im.chit/iroh `\"`{{PROJECT.version}}`\"`]`
"


"We often get so caught up with *how* something is done, rather than *why* something is done. 

 - the java type system and the java security model.

Some of the `features` include eliminating type errors with inheritence, default encapsulation of object functionality and the object orientated paradigm.
"

[[:file {:src "test/midje_doc/iroh_walkthrough.clj"}]]