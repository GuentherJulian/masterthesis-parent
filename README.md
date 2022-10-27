
# Master's thesis

This repository contains the code used and developed during the creation of the master thesis.

## Modules
###  AIM Pattern Detection
The core implementation for the pattern detection approach.

###  Grammar Generation
Generation of combined template grammars based on a given object language and a given metalanguage. Taken from https://github.com/maybeec/phd-tooling/tree/master/template-grammar-generator.

###  Parsing
Module with tests for parsing different template languages. Makes use of  [ANTLR v4](https://github.com/antlr/antlr4) to generate the corresponding parsers.

### Pattern Detector
Simple JavaFX GUI that can be used to test the pattern detection approach.

## Setup
The pattern detection engine implemented in the module *aim-pattern-detection*  uses an adapted version of ANTLR4 that is capable of parsing ambiguities. This version was implemented by [*Malte Brunnlieb*](https://github.com/maybeec) and is available in his [public fork of the antlr4 repository](https://github.com/maybeec/antlr4). In order to use the pattern detection engine, first clone this repository and build the project using Maven.