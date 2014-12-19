JGEX_SagaciousMatterFork
========================

Refactor of JGEX Java Geometry Expert

I (Douglas Kutach) have taken the source code for JGEX (http://sourceforge.net/projects/jgex/)
and attempted to some basic fixes. Mainly, I switched much of the code to use properly typed Java collection
classes instead of untyped arrays, and I altered the jgex file format so that it uses xml rather than the previous
proprietary format that changed from version to version.

Unfortunately, the original source code I worked from had very poor encapsulation, no unit testing, and too few comments
to let other coders know what was going on. I have tried to avoid introducing any new errors, but much of the
technical code in the automated theorem proving part of the program is hard to decypher. It uses ints
for many different kinds of variables, and a good task to take up now is to refactor that code so that it encapsulates
the monomials, polynomials, triangular polynomials and similar classes.
