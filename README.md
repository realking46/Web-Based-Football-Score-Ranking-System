# Football-Tournament
A simple web-based platform to manage and track local football matches. Designed for tournaments without built-in score tracking, it allows users to add scores, update player stats, maintain rankings, and oversee a series of matches. Built to support local games with ease and clarity.

![UI tests](https://github.com/realking46/Web-Based-Football-Score-Ranking-System/actions/workflows/ui-tests.yml/badge.svg)

## Automated UI tests

The `selenium-tests/` folder holds an end-to-end UI suite written in **Java** with **Selenium WebDriver** and **TestNG**, using the Page Object Model. The site itself is unchanged: the tests start a local static server and drive the pages the way a user would.

| Area | What is checked |
|---|---|
| Home page | Standings table columns, a fresh tournament starting at zero, the team lists |
| Team selection | Picking a team updates the table, every round block and the logo (data-driven over several teams) |
| Player selection | Add and remove players, options disabled once picked, independent squads, **11-player cap with alert** (boundary test) |
| Persistence | Ctrl+S save, state restored after reload, Ctrl+R reset |
| Navigation | Every round's Overview button and the standings page |

**21 test runs** (8 smoke + 13 regression), Chrome and Firefox, run by GitHub Actions and by the included `Jenkinsfile`.

### Run locally
Requires JDK 21, Maven and Chrome (or Firefox).
```bash
cd selenium-tests
mvn test                                  # headless Chrome
mvn test -Dheadless=false                 # watch the browser
mvn test -Dbrowser=firefox
mvn test -DsuiteXmlFile=testng-known-bugs.xml   # reproduce open bugs (expected to fail)
```
Reports: `selenium-tests/target/surefire-reports/`.

### Bugs found by testing
| # | Bug | Test |
|---|---|---|
| 1 | After a reload, players already in a squad can be picked again. `script.js` compares image URLs against a hard-coded local path (`file:///C:/Users/LENOVO/Desktop/tournament/`), so the check only works on one machine. | `PersistenceTest.pickedPlayersStayDisabledAfterReload` |
| 2 | After Ctrl+R the team logo becomes `<img src="null">`, a broken image, because the placeholder option has no `data-image`. | `PersistenceTest.resetLeavesNoBrokenLogo` |

These tests live in the `known-bugs` group and are excluded from the default run until the bugs are fixed.
