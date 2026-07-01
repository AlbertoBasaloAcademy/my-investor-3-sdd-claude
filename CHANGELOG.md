# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added

### Changed

### Fixed

### Removed

## 0.1.0 - 2026-07-01

### Features

- Rocket management: register rockets and classify them by range.
- Launch management: schedule and list rocket launches.
- Passenger booking: reserve a seat on a launch with name, email and phone number.
- Booking cancellation with a one-way `CREATED` to `CANCELLED` lifecycle.
- Claude Code skills for the team workflow: explore, specification, planify, verification, review and release.

### Fixes

- Enforce one-way status transition so a cancelled booking cannot be reopened.
- Correct typos across skill documentation.

### Chores

- Initial project boilerplate, `CLAUDE.md` and agent rules documentation.
- Revise README with project title and course details.
