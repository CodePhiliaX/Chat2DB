# CONTRIBUTING

Thanks for your interest in contributing to Chat2DB.

Chat2DB is an open-source database client and SQL workspace for developers, DBAs, analysts, and data teams. We welcome contributions of all kinds, including bug reports, feature requests, documentation improvements, testing feedback, and pull requests.

This guide explains how to report issues, suggest improvements, and submit pull requests in a way that helps maintainers review and respond more efficiently.

## Before You Jump In

Looking for something to work on? Start by browsing open issues and discussions.

If you are new to the project, smaller bug fixes, documentation improvements, issue reproduction, and testing feedback are great ways to get started.

Before starting a larger change, please open an issue or leave a comment on an existing issue first. This helps us confirm the direction and avoid duplicated work.

If your pull request is related to an issue, please link it in the PR description.

## Bug Reports

Please search existing issues before opening a new bug report. Someone may already have reported the same problem.

> [!IMPORTANT]
> Please include enough information for maintainers to reproduce and understand the problem.

- A clear and descriptive title
- Chat2DB version
- How are you using Chat2DB: desktop app, Docker, or local source build
- Operating system
- Database type and version
- Steps to reproduce the problem
- Expected behavior
- Actual behavior
- Logs, screenshots, or screen recordings if available

For database connection or SQL execution issues, it is also helpful to include:

- The database you are using
- The connection method, without passwords or private information
- A minimal SQL example, if it is safe to share

> [!CAUTION]
> Please remove passwords, tokens, private hostnames, customer data, and other sensitive information before posting logs or screenshots.

## Feature Requests

Please search existing issues and discussions before opening a new feature request.

> [!NOTE]
> Feature requests with clear use cases are easier for maintainers and the community to discuss.

- A clear and descriptive title
- The problem or workflow you want to improve
- The feature you would like to see
- Example use cases
- Screenshots, mockups, or references if useful

## Questions and Discussions

Please use GitHub Discussions for:

- Usage questions
- Setup help
- Ideas and open-ended feedback
- Community support
- General product discussions

Please use GitHub Issues for:

- Reproducible bugs
- Clear feature requests
- Documentation problems
- Technical tasks that can be worked on

This keeps Issues focused and easier to manage.

## Submitting Your Pull Request

We welcome focused pull requests.

> [!TIP]
> Smaller pull requests are easier to review and merge.

Before opening a pull request:

1. Fork the repository.
2. Create or comment on a related issue.
3. Create a new branch for your work.
4. Keep your pull request focused on one topic.
5. Update documentation if your change affects user behavior or setup.
6. Add or update tests when practical.
7. Verify your change locally.
8. Link the related issue in your pull request description, if there is one.

You can link issues with:

```text
Fixes #123
```

or:

```text
Related to #123
```

## Pull Request Description

A good pull request description should include:

- What changed
- Why the change is needed
- Related issue link
- How you tested it
- Screenshots or screen recordings for UI changes
- Any known limitations or follow-up work

Please avoid mixing unrelated changes in one pull request.

## Local Setup

Please refer to the README for the latest setup instructions.

If you run into setup issues, please include the command you ran, the error output, and your local environment details when asking for help.

## Getting Help

If you get stuck while contributing, you can ask for help in:

- the related GitHub issue
- GitHub Discussions
- the Chat2DB community channels listed in the README

## Contributor Recognition

We appreciate every helpful contribution to Chat2DB, including code, documentation, testing, bug reports, issue reproduction, pull request reviews, and community support.

Thank you for helping improve Chat2DB.

## License

By contributing to Chat2DB, you agree that your contributions will be licensed under the project's existing licenses.

Please review:

- [Apache License 2.0](./LICENSE)
- [Chat2DB License](./Chat2DB_LICENSE)
