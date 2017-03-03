# Yet Another Software Solution

If you need to quickly build a minimum viable product to support ad-hoc processes with fast changing requirements, yass can help!

Yass provides you with the tool support, templates and sound configuration defaults you need at the beginning of the development lifecycle. This empowers you to quickly iterate through prototypes.

## How do we do this?

Let's take a look at simple example to gain an understanding of the yass philosophy. If you're pressed for time, just skip to the [tl;dr](#tl;dr).

### Our Evernote Clone

Instead of using the prototypical todo app, we will build an evernote clone that will store your important notes.

The data model for our app is quite simple:

| Name | Foo | Bar |
| ---- | --- | --- |
|foo| bar| baz|

Data modelling in yass occurs in a resource oriented fashion inspired by RDF. A Resource (also refered to as a *Concept*) can have many different representations, which can be thought of as schemas. These Representations are defined implicitely by adding instantiated resources. The following example illustrates the data model for our evernote clone in a standard `.yaml` file.

```yaml
# nuggeting - an app to track the cool things I find on the web

nuggeting:
  meta:
    "@context":
      "vocab": "http://schema.org"
    version: 0.1.0
  traits:
    - use-prelude
    - data-inherits
  data:
    - nugget:
        name:
          EditorConfig
        description:
          EditorConfig helps developers define and maintain consistent coding styles between different editors and IDEs.
        date:
          Mi, 25 Jan 2017 00:15:10 +0100
        url:
          http://editorconfig.org
    - nugget:
        name:
          Monad papers
        description:
          a collection of scientific articles about and introducing the concept of 'monads' in functional programming languages
        date:
          Fr, 20 Jan 2017 22:56:26 +0100
        url:
          http://homepages.inf.ed.ac.uk/wadler/topics/monads.html
```

#### Meta

If the Meta section we model Concept (Resource) we are modelling has already been defined in a vocabulary or ontology, we can link to it in the `meta` section.

#### Traits

In the Traits section we define the *affordances* provided by a particular Resource. It is similar to what many programming languages define as an Interface.

#### Data

In the data section we define our instantiated data.

by default a view is associated with the model. The user can perform CRUD on this model although its better to do this through a view.

TODO
