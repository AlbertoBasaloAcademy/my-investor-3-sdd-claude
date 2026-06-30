---
name: explore
description: Shallow explore the current codebase
user-invocable: true
disable-model-invocation: true
---

## Explore skill

## Role

Act as a software architect

## Task

Generate the main agent rules

## Context

- Template: [arch.template.md](./assets/arch.template.md)

## Steps

### 1 Research

- Explore the current code base folder tree
- Ask unser clarification questions with closed answers

### 2 Plan

- Organize the collected information
- Fill the template {placeholders}
- Draw the mermaid diagrams

### 3 Implment

- Write a `arch.md` file at the `.poduct` folder

## Verification

- [ ] The  `arch.md` exists and its well formed
- [ ] Mermaid diagrmas ar syntactically correct