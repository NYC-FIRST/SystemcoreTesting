# Wiki Pages Staging Folder

This folder contains GitHub-wiki-ready Markdown pages copied and adapted from the
FTC student transition guide in the test project:

```text
testprojects/First Test June 25 2026/FTC_STUDENT_ULTIMATE_SYSTEMCORE_GUIDE.md
```

GitHub wikis are separate Git repositories, so these files are staged here until the
actual wiki repo is cloned.

The original test-project Markdown files should remain next to the sample code.
They are code-adjacent documentation and should continue to evolve with pull
requests that change the sample project.

## How to Use

1. Create or open the GitHub wiki for the repository.
2. Clone the wiki repo. For this repository, the URL should be:

   ```text
   https://github.com/NYC-FIRST/SystemcoreTesting.wiki.git
   ```

3. Copy the wiki page `.md` files from this folder into the cloned wiki repo.
4. Commit and push from the wiki repo.

`Home.md` is the wiki landing page. The other files are linked from it using GitHub
wiki page names.

This `README.md` explains the staging folder and usually should not be copied into
the GitHub wiki unless you want a visible wiki page named `README`.

## Current Scope

The NetworkTables and Elastic page is currently a short wiki bridge. The detailed
setup guide remains in the test project and can be migrated or expanded into the
wiki later.
