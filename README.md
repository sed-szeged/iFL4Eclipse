# Interactive Fault Localization for Eclipse

<!--<img src="https://git.sed.hu/geryxyz/iFL-eclipse-plugin/raw/dev/org.eclipse.sed.ifl/icons/logo.png" alt="logo" width="200px"/>-->

Fault localization is a debugging activity which is, by definition, part of a programmer's work in which she has to interact with the source code of the software being debugged.
It follows that this can be performed most effectively through the IDE itself; hence the most logical form of supporting tools is when they are integrated into the IDE. 

With this in mind, we present iFL for Eclipse, which is an Eclipse plug-in for supporting iFL for Java projects developed in this environment. It supporting Java 10 and later, and Eclipse 2018-12 and later, so it is part of the well-known workspace of developers.
The plug-in reads the tree of project elements (classes and functions) and lists them in a table showing detailed information about those elements.
This information includes, among others, the suspiciousness scores calculated using a traditional SBFL formula, such as Tarantula.
This table also enables direct navigation towards the project tree and the contained source code elements.

Interactivity between the tool and the programmer is achieved by providing the capability to send feedback to the FL engine about the next element in the table based on user knowledge.
It involves the context of the investigated element, in our case, Java classes and methods.
This gives an opportunity to change the order of elements in the table and hopefully arrive more quickly at the faulty element.