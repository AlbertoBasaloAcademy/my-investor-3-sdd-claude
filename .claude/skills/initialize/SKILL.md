---
name: initialize
description: Genertaes the CLAUDE.Md rules file
user-invocable: true
disable-model-invocation: true
---
# Initizlize skill

## Role

Act as a software architect

## Task

Generate the main agent rules

## Context

- Template: [AGENTS.template.md](./assets/CLAUDE.template.md)

## Steps

### 1 Research

- Explore the current code base folder tree
- Read well-know files like `pom` `packaje.json`
- Look for product documentation
- Ask unser calrification questions with closed answers


### 2 Plan

- Organize the collected infromation
- Fill the template {placeholders}

### 3 Implment

- Write a `CLAUDE.md` file at the root folder

## Verification

- [ ] The  `CLAUDE.md` exists and its well formed