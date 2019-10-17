# play9592

Hosts Public domain scripts with various versions aid in memorization

Script Variations:
- For each character:
  - First letter of each word
  - First word of each sentence
  - Reveal full word when you click a character
  -  Completely blank lines with Spoken cue lines
  
 In Progress:
  - Focus on certain sections
  
### To build:
The Scripts:

    // in directory: /playScripts 
    rm -rf ../content/generated/* && mill all scriptManipulation.{reformat,run} && git status

The Front End:

    // in directory: /jsClient/scala-js-example-app
    scalafmt
    ~myProject/fastOptJS::webpack

    // Copy js bundle to correct hugo location
    cp -u target/scala-2.12/scalajs-bundler/main/example-fastopt-bundle.js ../../assets/js/example-fastopt-bundle.js
    
Serve it all up via hugo:

    // in directory /
    hugo server --disableFastRender